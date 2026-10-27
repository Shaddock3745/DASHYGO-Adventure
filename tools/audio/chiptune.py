"""Original 8-bit background music for DASHYGO Adventure.

Renders six seamless loops (22050 Hz, 16-bit, stereo) with a tiny NES-style
synth: pulse lead, pulse arpeggio, triangle bass, noise drums.
Usage: py -I chiptune.py <output dir>
"""
import os
import sys
import wave

import numpy as np

SR = 22050
MUSIC_RMS = 0.12
NOTE_INDEX = {"C": 0, "D": 2, "E": 4, "F": 5, "G": 7, "A": 9, "B": 11}
CHORD_SHAPES = {"": (0, 4, 7), "m": (0, 3, 7), "dim": (0, 3, 6), "7": (0, 4, 7, 10)}


def midi(name):
    """'C#5' / 'Bb4' -> MIDI number."""
    letter, rest = name[0], name[1:]
    acc = 0
    while rest and rest[0] in "#b":
        acc += 1 if rest[0] == "#" else -1
        rest = rest[1:]
    return 12 * (int(rest) + 1) + NOTE_INDEX[letter] + acc


def freq(m):
    return 440.0 * 2 ** ((m - 69) / 12)


def parse_chord(sym):
    """'Bb' / 'C#m' / 'Bdim' / 'C7' -> (root pitch class, intervals)."""
    root = sym[0]
    i = 1
    acc = 0
    while i < len(sym) and sym[i] in "#b":
        acc += 1 if sym[i] == "#" else -1
        i += 1
    return (NOTE_INDEX[root] + acc) % 12, CHORD_SHAPES[sym[i:]]


def parse_bar(text):
    """'E5:4 -:4 G5:8' -> [(midi or None, steps)], must fill 16 sixteenths."""
    events = []
    for tok in text.split():
        n, d = tok.split(":")
        events.append((None if n == "-" else midi(n), int(d)))
    total = sum(d for _, d in events)
    if total != 16:
        raise ValueError(f"bar sums to {total}, not 16: {text}")
    return events


# ---------------------------------------------------------------- oscillators

def phase_of(f, n, vibrato=0.0, vib_delay=0.15):
    t = np.arange(n) / SR
    inst = np.full(n, f)
    if vibrato:
        ramp = np.clip((t - vib_delay) / 0.2, 0, 1)
        inst = f * (1 + vibrato * ramp * np.sin(2 * np.pi * 5.5 * t))
    return np.cumsum(inst) / SR


def pulse(f, n, duty, vibrato=0.0):
    return np.where(phase_of(f, n, vibrato) % 1.0 < duty, 1.0, -1.0)


def triangle(f, n):
    p = phase_of(f, n) % 1.0
    tri = 4 * np.abs(p - 0.5) - 1
    return np.round(tri * 7.5) / 7.5          # 4-bit steps like the NES triangle


def envelope(n, gate, attack=0.004, decay=0.08, sustain=0.7, release=0.03):
    """ADSR over n samples; note held for `gate` samples."""
    t = np.arange(n) / SR
    g = gate / SR
    env = np.where(t < attack, t / attack,
                   sustain + (1 - sustain) * np.exp(-(t - attack) / decay))
    rel = np.clip(1 - (t - g) / release, 0, 1)
    return env * np.where(t < g, 1.0, rel)


# ---------------------------------------------------------------- drums

RNG = np.random.default_rng(3745)


def kick(n):
    t = np.arange(n) / SR
    f = 45 + 110 * np.exp(-t / 0.03)
    return np.sin(2 * np.pi * np.cumsum(f) / SR) * np.exp(-t / 0.11)


def snare(n):
    t = np.arange(n) / SR
    noise = RNG.uniform(-1, 1, n)
    tone = np.sin(2 * np.pi * 185 * t)
    return (0.8 * noise + 0.4 * tone) * np.exp(-t / 0.07)


def hat(n):
    noise = RNG.uniform(-1, 1, n)
    noise = np.diff(noise, prepend=0.0) * 0.5      # crude high-pass
    return noise * np.exp(-np.arange(n) / SR / 0.018)


# ---------------------------------------------------------------- track render

class Mix:
    def __init__(self, length):
        self.buf = np.zeros((length, 2))

    def add(self, start, sig, gain, pan=0.0):
        end = min(start + len(sig), len(self.buf))
        sig = sig[: end - start] * gain
        self.buf[start:end, 0] += sig * np.sqrt((1 - pan) / 2)
        self.buf[start:end, 1] += sig * np.sqrt((1 + pan) / 2)


def render(song):
    bpm = song["bpm"]
    step = int(round(SR * 60 / bpm / 4))           # one sixteenth, in samples
    bars = len(song["chords"])
    loop_len = bars * 16 * step
    tail = SR * 2
    mix = Mix(loop_len + tail)

    # lead melody
    pos = 0
    lead = [ev for bar in song["melody"] for ev in parse_bar(bar)]
    for m, d in lead:
        if m is not None:
            length = d * step
            gate = int(length * song.get("lead_gate", 0.9))
            n = gate + int(0.05 * SR)
            vib = 0.004 if d >= 6 else 0.0
            sig = pulse(freq(m), n, song["lead_duty"], vib) * envelope(n, gate, sustain=0.75)
            mix.add(pos, sig, song["lead_gain"], 0.0)
        pos += d * step
    assert pos == loop_len, "melody length must match chord bars"

    for b, bar_chords in enumerate(song["chords"]):
        bar_start = b * 16 * step
        parts = bar_chords.split()
        span = 16 // len(parts)
        for p, sym in enumerate(parts):
            root, shape = parse_chord(sym)
            cstart = bar_start + p * span * step

            # bass (triangle)
            base = 12 * (song["bass_oct"] + 1) + root
            for s, interval in song["bass"]:
                if s >= span:
                    continue
                m = base + interval
                d = song["bass_len"]
                gate = int(d * step * 0.85)
                n = gate + int(0.03 * SR)
                sig = triangle(freq(m), n) * envelope(n, gate, decay=0.2, sustain=0.85)
                mix.add(cstart + s * step, sig, song["bass_gain"], 0.0)

            # harmony: arpeggio or pad
            tones = [12 * (song["harm_oct"] + 1) + root + i for i in shape]
            if song["harm"] == "arp":
                for s in range(span):
                    m = tones[s % len(tones)] + (12 if (s // len(tones)) % 2 else 0)
                    gate = int(step * 0.7)
                    n = gate + int(0.02 * SR)
                    sig = pulse(freq(m), n, 0.125) * envelope(n, gate, decay=0.04, sustain=0.4)
                    mix.add(cstart + s * step, sig, song["harm_gain"], 0.35)
            elif song["harm"] == "pad":
                length = span * step
                for k, m in enumerate(tones[:3]):
                    n = length + int(0.05 * SR)
                    sig = pulse(freq(m), n, 0.5) * envelope(n, length, attack=0.03, decay=0.4, sustain=0.5)
                    mix.add(cstart, sig, song["harm_gain"] / 3, (-0.5, 0.0, 0.5)[k])

        # drums
        for name, fn, gain, pan in (("k", kick, 0.55, 0.0), ("s", snare, 0.22, 0.1), ("h", hat, 0.07, -0.3)):
            pattern = song["drums"].get(name, "")
            for s, ch in enumerate(pattern):
                if ch == "x":
                    mix.add(bar_start + s * step, fn(int(0.25 * SR)), gain, pan)

    # fold the tail back to the start so the loop is seamless
    out = mix.buf[:loop_len].copy()
    out[:tail] += mix.buf[loop_len:loop_len + tail]

    # gentle one-pole low-pass to soften aliasing, then soft clip and normalise
    out = lowpass(out, 7000)
    out -= out.mean(axis=0)
    out = np.tanh(out / np.max(np.abs(out)) * 1.5)
    # every track at the same loudness, a few dB under the sound effects
    out *= MUSIC_RMS / np.sqrt(np.mean(out ** 2))
    return out


def lowpass(x, cutoff):
    """One-pole low-pass along axis 0, done in blocks of the closed-form IIR."""
    a = np.exp(-2 * np.pi * cutoff / SR)
    x2 = x.reshape(len(x), -1)
    y = np.empty_like(x2)
    prev = np.zeros(x2.shape[1])
    block = 12                       # small, so a**-block stays well inside float range
    j = np.arange(block)
    for s in range(0, len(x2), block):
        seg = x2[s:s + block] * (1 - a)
        k = len(seg)
        # y[n] = sum_{i<=n} a^(n-i) seg[i] + a^(n+1) prev
        acc = np.cumsum(seg * a ** -j[:k, None], axis=0) * a ** j[:k, None]
        acc += np.outer(a ** (j[:k] + 1), prev)
        y[s:s + k] = acc
        prev = acc[-1]
    return y.reshape(x.shape)


def write_wav(path, data):
    pcm = (np.clip(data, -1, 1) * 32767).astype("<i2")
    with wave.open(path, "wb") as w:
        w.setnchannels(2)
        w.setsampwidth(2)
        w.setframerate(SR)
        w.writeframes(pcm.tobytes())


# ---------------------------------------------------------------- the songs
# melody: one string per bar, 16 sixteenths each. chords: one string per bar,
# a bar may hold two chords ("F G"). bass: (sixteenth offset, semitones).

ROOT_OCT8 = [(0, 0), (4, 12), (8, 0), (12, 12)]
GALLOP = [(0, 0), (3, 0), (4, 0), (6, 0), (8, 0), (11, 0), (12, 0), (14, 0)]
DRIVE8 = [(s, 12 if s % 4 else 0) for s in range(0, 16, 2)]

SONGS = {
    "Title": dict(
        bpm=96, lead_duty=0.25, lead_gain=0.22, bass_oct=2, bass_len=4, bass_gain=0.30,
        bass=[(0, 0), (4, 7), (8, 12), (12, 7)], harm="arp", harm_oct=4, harm_gain=0.07,
        drums={"k": "x.......x.......", "h": "..x...x...x...x."},
        chords=["C", "Am", "F", "G", "C", "Am", "F G", "C"],
        melody=[
            "E5:4 G5:4 C6:6 B5:2",
            "A5:6 G5:2 E5:8",
            "F5:4 A5:4 C6:4 A5:4",
            "G5:8 -:4 D5:4",
            "E5:4 G5:4 C6:4 E6:4",
            "C6:4 B5:2 A5:2 E5:8",
            "F5:4 A5:4 B5:4 D6:4",
            "C6:12 -:4",
        ],
    ),
    "Level_1": dict(
        bpm=138, lead_duty=0.5, lead_gain=0.18, lead_gate=0.75, bass_oct=2, bass_len=2, bass_gain=0.30,
        bass=[(0, 0), (2, 12), (4, 7), (6, 12), (8, 0), (10, 12), (12, 7), (14, 12)],
        harm="pad", harm_oct=4, harm_gain=0.10,
        drums={"k": "x.......x.x.....", "s": "....x.......x...", "h": "x.x.x.x.x.x.x.x."},
        chords=["F", "F", "Bb", "C", "Dm", "Bb", "C", "F",
                "F", "F", "Bb", "C", "Dm", "Bb", "C7", "F"],
        melody=[
            "C5:2 -:2 F5:2 -:2 A5:2 -:2 F5:2 -:2",
            "C6:3 A5:1 G5:2 F5:2 A5:4 -:4",
            "D5:2 -:2 F5:2 -:2 Bb5:2 -:2 D6:4",
            "C6:2 Bb5:2 A5:2 G5:2 E5:4 C5:4",
            "D5:2 F5:2 A5:2 D6:2 C6:2 A5:2 F5:4",
            "Bb5:4 A5:2 G5:2 F5:4 D5:4",
            "E5:2 G5:2 C6:2 E6:2 D6:2 C6:2 Bb5:4",
            "A5:6 G5:2 F5:4 -:4",
            "C5:2 -:2 F5:2 -:2 A5:2 -:2 F5:2 -:2",
            "C6:3 A5:1 G5:2 F5:2 A5:4 -:4",
            "D5:2 -:2 F5:2 -:2 Bb5:2 -:2 D6:4",
            "C6:2 Bb5:2 A5:2 G5:2 E5:4 C5:4",
            "D5:2 F5:2 A5:2 D6:2 C6:2 A5:2 F5:4",
            "Bb5:4 A5:2 G5:2 F5:4 D5:4",
            "E5:2 G5:2 C6:2 E6:2 D6:2 C6:2 Bb5:4",
            "F5:4 A5:2 C6:2 F6:4 -:4",
        ],
    ),
    "Level_2": dict(
        bpm=152, lead_duty=0.25, lead_gain=0.20, bass_oct=2, bass_len=1, bass_gain=0.32,
        bass=GALLOP, harm="arp", harm_oct=4, harm_gain=0.06,
        drums={"k": "x.....x.x.......", "s": "....x.......x...", "h": "xxxxxxxxxxxxxxxx"},
        chords=["Dm", "Dm", "Bb", "C", "Dm", "Dm", "Gm", "A",
                "Dm", "Dm", "Bb", "C", "Gm", "A", "Dm", "A"],
        melody=[
            "D5:3 D5:3 F5:2 A5:4 G5:2 F5:2",
            "E5:4 D5:4 A4:8",
            "D5:3 D5:3 F5:2 Bb5:4 A5:2 G5:2",
            "A5:4 G5:4 E5:8",
            "D6:3 C6:3 A5:2 F5:4 A5:2 C6:2",
            "D6:8 -:2 D6:2 E6:2 F6:2",
            "G6:3 F6:3 D6:2 Bb5:4 D6:4",
            "C#6:8 E6:4 A5:4",
            "D5:3 D5:3 F5:2 A5:4 G5:2 F5:2",
            "E5:4 D5:4 A4:8",
            "D5:3 D5:3 F5:2 Bb5:4 A5:2 G5:2",
            "A5:4 G5:4 E5:8",
            "Bb5:3 A5:3 G5:2 D5:4 G5:4",
            "C#6:3 B5:3 A5:2 E5:4 A5:4",
            "D6:4 A5:4 F5:4 D5:4",
            "E5:4 A5:4 C#6:4 E6:4",
        ],
    ),
    "Level_3": dict(
        bpm=108, lead_duty=0.5, lead_gain=0.20, bass_oct=1, bass_len=8, bass_gain=0.40,
        bass=[(0, 0), (8, 0)], harm="pad", harm_oct=3, harm_gain=0.13,
        drums={"k": "x.........x.....", "s": "........x.......", "h": "x...x...x...x..."},
        chords=["Em", "Em", "C", "D", "Em", "Em", "C", "B",
                "Em", "G", "C", "D", "Am", "Em", "B", "B"],
        melody=[
            "E4:6 G4:2 B4:8",
            "A4:4 G4:4 F#4:4 E4:4",
            "C5:6 B4:2 G4:8",
            "A4:6 F#4:2 D4:8",
            "E5:6 D5:2 B4:8",
            "G4:4 A4:4 B4:8",
            "C5:4 E5:4 G5:4 E5:4",
            "D#5:8 F#5:4 B4:4",
            "B4:6 E5:2 G5:8",
            "F#5:4 G5:4 D5:8",
            "E5:6 C5:2 G4:8",
            "F#4:4 A4:4 D5:8",
            "C5:6 B4:2 A4:8",
            "G4:4 F#4:4 E4:8",
            "F#4:4 A4:4 B4:4 D#5:4",
            "F#5:12 -:4",
        ],
    ),
    "Level_4": dict(
        bpm=116, lead_duty=0.125, lead_gain=0.24, bass_oct=2, bass_len=2, bass_gain=0.30,
        bass=[(0, 0), (6, 0), (8, 7), (11, 0)], harm="arp", harm_oct=4, harm_gain=0.05,
        drums={"k": "x..........x....", "s": "........x.......", "h": "..x...x...x...x."},
        chords=["Am", "F", "Dm", "E", "Am", "F", "Bdim", "E",
                "Am", "Am", "F", "F", "Dm", "E", "Am", "E"],
        melody=[
            "A5:2 -:2 E5:2 -:2 C6:2 -:2 B5:2 G#5:2",
            "A5:6 F5:2 C5:8",
            "D5:2 -:2 F5:2 -:2 A5:2 -:2 G#5:2 A5:2",
            "B5:6 G#5:2 E5:8",
            "E6:2 -:2 C6:2 -:2 A5:2 -:2 G#5:2 A5:2",
            "F6:6 E6:2 C6:8",
            "D6:4 F6:4 B5:4 G#5:4",
            "E5:12 -:4",
            "C5:4 E5:4 A5:4 C6:4",
            "B5:4 A5:4 G#5:4 A5:4",
            "A5:4 C6:4 F6:8",
            "E6:4 D6:4 C6:8",
            "D6:4 A5:4 F5:4 D5:4",
            "E5:4 G#5:4 B5:4 D6:4",
            "C6:8 A5:8",
            "G#5:8 B5:4 E5:4",
        ],
    ),
    "Level_5": dict(
        bpm=168, lead_duty=0.25, lead_gain=0.20, bass_oct=2, bass_len=2, bass_gain=0.32,
        bass=DRIVE8, harm="arp", harm_oct=4, harm_gain=0.06,
        drums={"k": "x...x...x...x...", "s": "....x.......x...", "h": "xxxxxxxxxxxxxxxx"},
        chords=["Cm", "Ab", "Bb", "G", "Cm", "Ab", "Fm", "G",
                "Ab", "Bb", "Cm", "Cm", "Fm", "G", "Ab", "G"],
        melody=[
            "C6:2 G5:2 Eb6:2 G5:2 C6:2 G5:2 D6:2 Eb6:2",
            "C6:6 Ab5:2 Eb5:8",
            "D6:2 Bb5:2 F6:2 Bb5:2 D6:2 Bb5:2 Eb6:2 F6:2",
            "D6:6 B5:2 G5:8",
            "G6:4 F6:2 Eb6:2 D6:2 Eb6:2 C6:4",
            "Ab6:4 G6:2 F6:2 Eb6:4 C6:4",
            "F6:2 Eb6:2 D6:2 C6:2 Ab5:4 F5:4",
            "B5:4 D6:4 G6:4 F6:4",
            "Eb6:4 C6:4 Ab5:4 C6:4",
            "F6:4 D6:4 Bb5:4 D6:4",
            "G6:6 Eb6:2 C6:8",
            "D6:2 Eb6:2 F6:2 G6:2 Ab6:4 G6:4",
            "F6:6 Eb6:2 C6:8",
            "D6:6 F6:2 B5:8",
            "C6:2 Eb6:2 Ab6:2 Eb6:2 C6:2 Eb6:2 Ab6:2 C7:2",
            "B6:4 G6:4 D6:4 B5:4",
        ],
    ),
}


if __name__ == "__main__":
    out_dir = sys.argv[1]
    os.makedirs(out_dir, exist_ok=True)
    for name, song in SONGS.items():
        data = render(song)
        write_wav(os.path.join(out_dir, f"{name}.wav"), data)
        rms = np.sqrt(np.mean(data ** 2))
        print(f"{name}: {len(data) / SR:.1f}s, peak {np.max(np.abs(data)):.2f}, rms {rms:.3f}")
