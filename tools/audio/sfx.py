"""Original 8-bit sound effects for DASHYGO Adventure.

Every effect is normalised to the same short-term loudness so none of them
drowns the others, and they sit a few dB above the background music.
Usage: py -I sfx.py <output dir>
"""
import os
import sys

import numpy as np

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
from chiptune import SR, envelope, freq, lowpass, midi, pulse, triangle, write_wav

RNG = np.random.default_rng(2022)
SFX_SHORT_RMS = 0.30        # loudest 50 ms window of every effect
JINGLE_SHORT_RMS = 0.24     # the two jingles are long and melodic, keep them a bit softer
PEAK_CAP = 0.95


def t_axis(sec):
    return np.arange(int(sec * SR)) / SR


def sweep(f0, f1, sec, shape="pulse", duty=0.5, curve="exp"):
    """Oscillator gliding from f0 to f1 over `sec`."""
    t = t_axis(sec)
    k = t / sec
    f = f0 * (f1 / f0) ** k if curve == "exp" else f0 + (f1 - f0) * k
    ph = np.cumsum(f) / SR % 1.0
    if shape == "pulse":
        return np.where(ph < duty, 1.0, -1.0)
    if shape == "tri":
        return 4 * np.abs(ph - 0.5) - 1
    return np.sin(2 * np.pi * ph)


def noise(sec, cutoff=None):
    n = RNG.uniform(-1, 1, int(sec * SR))
    return lowpass(n[:, None], cutoff)[:, 0] * 2 if cutoff else n


def decay(sec, tau):
    return np.exp(-t_axis(sec) / tau)


def place(total_sec, *parts):
    """Mix (start_sec, signal) pairs into one buffer."""
    out = np.zeros(int(total_sec * SR))
    for start, sig in parts:
        s = int(start * SR)
        e = min(s + len(sig), len(out))
        out[s:e] += sig[: e - s]
    return out


def tone(note, sec, duty=0.5, gate=0.85, shape="pulse"):
    n = int(sec * SR)
    gate_n = int(n * gate)
    osc = triangle(freq(midi(note)), n) if shape == "tri" else pulse(freq(midi(note)), n, duty)
    return osc * envelope(n, gate_n, decay=0.15, sustain=0.7, release=0.04)


def melody(notes, step_sec, duty=0.5, shape="pulse"):
    """'C5:2 E5:2 -:1' with durations in steps."""
    parts, pos = [], 0.0
    for tok in notes.split():
        n, d = tok.split(":")
        sec = int(d) * step_sec
        if n != "-":
            parts.append((pos, tone(n, sec, duty, shape=shape)))
        pos += sec
    return place(pos + 0.1, *parts)


# ---------------------------------------------------------------- effects

def counter():
    """Sword deflects a bullet: bright rising 'ting' with a metallic click."""
    ring = sweep(1400, 2600, 0.35, duty=0.25) * decay(0.35, 0.09)
    ring2 = sweep(2100, 3900, 0.35, duty=0.5) * decay(0.35, 0.05) * 0.4
    click = noise(0.03) * decay(0.03, 0.006)
    return place(0.35, (0, ring + ring2), (0, click))


def fireball():
    """Fireball sent back: roaring whoosh that falls in pitch."""
    sec = 0.6
    roar = noise(sec, 2500) * np.sin(np.pi * t_axis(sec) / sec) ** 0.5 * decay(sec, 0.35)
    body = sweep(320, 90, sec, duty=0.5) * decay(sec, 0.18) * 0.5
    return roar + body


def ice_mallet():
    """Wizard's ice mallet: crystalline descending chime with shimmer."""
    notes = ["E7", "B6", "G#6", "E6", "B5"]
    parts = [(i * 0.06, triangle(freq(midi(n)), int(0.3 * SR)) * decay(0.3, 0.08)) for i, n in enumerate(notes)]
    shimmer = noise(0.6) * decay(0.6, 0.15) * 0.15
    shimmer = shimmer - lowpass(shimmer[:, None], 3000)[:, 0]        # keep only the hiss
    return place(0.6, *parts, (0, shimmer))


def slime_puff():
    """Slime spits a blob: soft wobbly bloop."""
    sec = 0.25
    t = t_axis(sec)
    f = 180 + 420 * (t / sec) ** 0.6 + 25 * np.sin(2 * np.pi * 30 * t)
    blob = np.sin(2 * np.pi * np.cumsum(f) / SR) * envelope(len(t), int(0.18 * SR), decay=0.08, sustain=0.6)
    return blob + noise(sec, 900) * decay(sec, 0.03) * 0.4


def seed_puff():
    """Fly trap spits a seed: short 'pff' with a pop."""
    pff = noise(0.2, 1800) * decay(0.2, 0.04)
    pop = sweep(700, 250, 0.06, shape="sine") * decay(0.06, 0.02)
    return place(0.2, (0, pff), (0, pop))


def flytrap():
    """Fly trap bites: two jaw snaps over a leafy rustle."""
    snap = lambda: sweep(220, 70, 0.12, duty=0.25) * decay(0.12, 0.05) + noise(0.12, 4000) * decay(0.12, 0.02)
    rustle = noise(0.9, 1500) * np.sin(np.pi * t_axis(0.9) / 0.9) * 0.35
    return place(0.95, (0, rustle), (0.12, snap()), (0.42, snap()))


def rock_throw():
    """Golem hurls a rock: short low whoosh."""
    sec = 0.28
    whoosh = noise(sec, 1200) * np.sin(np.pi * t_axis(sec) / sec)
    grunt = sweep(160, 80, sec, duty=0.5) * decay(sec, 0.08) * 0.5
    return whoosh + grunt


def rock_fall():
    """Rocks crash down: thumps and rubble."""
    def thump(f0):
        return sweep(f0, 40, 0.3, shape="sine") * decay(0.3, 0.09) + noise(0.3, 1500) * decay(0.3, 0.06) * 0.7
    rubble = noise(0.8, 2500) * decay(0.8, 0.25) * 0.4
    return place(0.85, (0, rubble), (0, thump(130)), (0.16, thump(110) * 0.8), (0.33, thump(95) * 0.6))


def lightning():
    """Lightning strike: sharp crack, then rolling thunder."""
    crack = noise(0.15) * decay(0.15, 0.03)
    zap = sweep(1800, 300, 0.15, duty=0.5) * decay(0.15, 0.05) * 0.5
    t = t_axis(1.3)
    rumble = noise(1.3, 220) * 3 * (1 + 0.5 * np.sin(2 * np.pi * 7 * t)) * decay(1.3, 0.45)
    return place(1.45, (0, crack + zap), (0.08, rumble))


def warning():
    """Area attack incoming: two urgent beeps."""
    beep = lambda: tone("A5", 0.14, duty=0.5, gate=0.9) + tone("A6", 0.14, duty=0.125, gate=0.9) * 0.3
    return place(0.6, (0, beep()), (0.22, beep()))


def chain():
    """Chain rattles: a run of metallic clinks."""
    parts = []
    pos = 0.0
    for i in range(9):
        f = RNG.uniform(1800, 3200)
        clink = sweep(f, f * 0.92, 0.08, duty=0.25) * decay(0.08, 0.02) + noise(0.08) * decay(0.08, 0.008) * 0.6
        parts.append((pos, clink * (1 - i * 0.06)))
        pos += RNG.uniform(0.07, 0.12)
    return place(pos + 0.12, *parts)


def enemy_dead():
    """Enemy defeated: falling square burst into an explosion."""
    burst = sweep(900, 110, 0.45, duty=0.5) * decay(0.45, 0.2)
    boom = noise(0.85, 1800) * decay(0.85, 0.22)
    return place(0.85, (0, burst * 0.7), (0.05, boom))


def dead():
    """Player is hit: short low drop."""
    hit = sweep(500, 90, 0.3, duty=0.25) * decay(0.3, 0.12)
    return hit + noise(0.3, 1200) * decay(0.3, 0.04) * 0.6


def game_over():
    """Game over jingle: slow descending minor line over a falling bass."""
    lead = melody("E5:2 D5:2 C5:2 B4:2 A4:2 G#4:2 A4:6", 0.17, duty=0.25)
    bass = melody("A3:4 F3:4 E3:4 A2:6", 0.17, shape="tri")
    return place(max(len(lead), len(bass)) / SR, (0, lead), (0, bass * 0.9))


def victory():
    """Victory fanfare: quick rising arpeggio landing on a held chord."""
    lead = melody("C5:1 E5:1 G5:1 C6:1 -:1 G5:1 C6:5", 0.11, duty=0.25)
    harm = melody("-:4 -:1 E5:1 G5:5", 0.11, duty=0.125)
    bass = melody("C3:4 -:1 G2:1 C3:5", 0.11, shape="tri")
    return place(len(lead) / SR, (0, lead), (0, harm * 0.5), (0, bass * 0.9))


EFFECTS = {
    "Counter": counter, "Fireball": fireball, "IceMallet": ice_mallet,
    "Slime_Puff": slime_puff, "Seed_Puff": seed_puff, "Flytrap": flytrap,
    "Rock_Throw": rock_throw, "Rock_Fall": rock_fall, "Lightning": lightning,
    "Warning": warning, "Chain": chain, "Enemy_Dead": enemy_dead,
    "Dead": dead, "Game_Over": game_over, "Victory": victory,
}
JINGLES = {"Game_Over", "Victory"}


def short_term_peak_rms(x, window=0.05):
    n = int(window * SR)
    sq = np.convolve(x ** 2, np.ones(n) / n, mode="valid")
    return np.sqrt(sq.max())


def finish(name, x):
    x = lowpass(x[:, None], 9000)[:, 0]
    x -= x.mean()
    fade = min(len(x), int(0.01 * SR))
    x[-fade:] *= np.linspace(1, 0, fade)                 # no click at the end
    target = JINGLE_SHORT_RMS if name in JINGLES else SFX_SHORT_RMS
    x *= target / short_term_peak_rms(x)
    if np.abs(x).max() > PEAK_CAP:
        x = np.tanh(x / PEAK_CAP) * PEAK_CAP              # soft-limit the rare transient
    return np.stack([x, x], axis=1)


if __name__ == "__main__":
    out_dir = sys.argv[1]
    os.makedirs(out_dir, exist_ok=True)
    for name, fn in EFFECTS.items():
        data = finish(name, fn())
        write_wav(os.path.join(out_dir, f"{name}.wav"), data)
        print(f"{name:11s} {len(data) / SR:4.2f}s peak {np.abs(data).max():.2f} "
              f"short-term rms {short_term_peak_rms(data[:, 0]):.2f}")
