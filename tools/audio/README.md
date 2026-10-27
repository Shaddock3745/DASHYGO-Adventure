# 背景音樂與音效產生程式

`src/Musics/` 與 `src/Sounds/` 裡的所有 `.wav` 都是由這裡的程式合成的原創 8-bit 音訊。

- `chiptune.py`：6 首背景音樂。樂譜直接寫在 `SONGS` 裡（每小節 16 個十六分音符），合成方波主旋律、方波琶音、三角波貝斯與雜訊鼓，並把尾音折回開頭，讓循環播放沒有接縫
- `sfx.py`：15 個音效，每個函式對應遊戲中的一個事件

所有檔案皆為 22050 Hz、16-bit、立體聲；背景音樂統一響度，音效統一短時響度並略大於背景音樂。

需要 Python 3 與 NumPy：

```bash
python chiptune.py ../../src/Musics
python sfx.py ../../src/Sounds
```
