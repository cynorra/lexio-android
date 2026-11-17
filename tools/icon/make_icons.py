"""Renders the Lexio launcher icon (scales of justice) into the legacy mipmap PNGs and the 512px Play icon.

The adaptive-icon vectors in res/drawable (ic_launcher_foreground/background/monochrome.xml) hold the same
geometry on a 108x108 viewport; keep both in sync. Run: python tools/icon/make_icons.py
"""
from pathlib import Path
from PIL import Image, ImageDraw, ImageChops
import numpy as np

ROOT = Path(__file__).resolve().parents[2]
RES = ROOT / "android/app/src/main/res"
S = 8  # supersampling
V = 108 * S


def gradient(size, c0, c1, p0, p1):
    """Linear gradient between points p0->p1 (viewport units) over a size x size canvas."""
    ys, xs = np.mgrid[0:size, 0:size].astype(np.float32)
    k = size / 108.0
    x0, y0, x1, y1 = p0[0] * k, p0[1] * k, p1[0] * k, p1[1] * k
    dx, dy = x1 - x0, y1 - y0
    t = ((xs - x0) * dx + (ys - y0) * dy) / (dx * dx + dy * dy)
    t = np.clip(t, 0, 1)[..., None]
    a, b = np.array(c0, np.float32), np.array(c1, np.float32)
    return Image.fromarray((a + (b - a) * t).astype(np.uint8), "RGB")


def pt(x, y):
    return (x * S, y * S)


def arc_pts(cx, cy, r, a0, a1, n=48):
    import math
    return [pt(cx + r * math.cos(math.radians(a0 + (a1 - a0) * i / n)),
               cy + r * math.sin(math.radians(a0 + (a1 - a0) * i / n))) for i in range(n + 1)]


def scales_mask():
    m = Image.new("L", (V, V), 0)
    d = ImageDraw.Draw(m)
    d.rectangle([*pt(52.5, 31), *pt(55.5, 74)], fill=255)                                   # pillar
    d.polygon([pt(41, 73.5), pt(67, 73.5), pt(69, 77), pt(39, 77)], fill=255)               # base
    d.ellipse([*pt(50.6, 26.6), *pt(57.4, 33.4)], fill=255)                                 # finial
    d.rounded_rectangle([*pt(30, 35.6), *pt(78, 38.4)], radius=1.4 * S, fill=255)           # beam
    for ax, bx0, bx1 in ((33, 24.5, 41.5), (75, 66.5, 83.5)):
        w = 1.3 * S
        d.line([pt(ax, 38.2), pt(bx0, 56)], fill=255, width=int(w))                         # strings
        d.line([pt(ax, 38.2), pt(bx1, 56)], fill=255, width=int(w))
        cx = (bx0 + bx1) / 2
        d.polygon(arc_pts(cx, 56, 9, 0, 180) + [pt(cx - 9, 56), pt(cx + 9, 56)], fill=255)  # pan bowl
    return m


def render(size):
    """Full 108-viewport artwork at size x size."""
    bg = gradient(size, (22, 52, 102), (6, 13, 28), (0, 0), (108, 108))
    glow = Image.new("L", (size, size), 0)
    ImageDraw.Draw(glow).ellipse([size * 0.2, size * 0.1, size * 0.8, size * 0.7], fill=70)
    glow = glow.resize((size, size)).filter(__import__("PIL.ImageFilter", fromlist=["x"]).GaussianBlur(size * 0.12))
    bg = Image.composite(Image.new("RGB", (size, size), (47, 92, 170)), bg, glow)
    gold = gradient(size, (255, 214, 122), (249, 115, 22), (54, 26), (54, 78))
    mask = scales_mask().resize((size, size), Image.LANCZOS)
    bg.paste(gold, (0, 0), mask)
    return bg


def main():
    big = render(1024)
    # legacy icons / Play icon show only the central 72 of 108 units
    crop = big.crop((int(1024 * 18 / 108), int(1024 * 18 / 108), int(1024 * 90 / 108), int(1024 * 90 / 108)))
    for dpi, px in (("mdpi", 48), ("hdpi", 72), ("xhdpi", 96), ("xxhdpi", 144), ("xxxhdpi", 192)):
        icon = crop.resize((px, px), Image.LANCZOS).convert("RGBA")
        sq = Image.new("L", (px * 4, px * 4), 0)
        ImageDraw.Draw(sq).rounded_rectangle([0, 0, px * 4 - 1, px * 4 - 1], radius=int(px * 4 * 0.22), fill=255)
        rd = Image.new("L", (px * 4, px * 4), 0)
        ImageDraw.Draw(rd).ellipse([0, 0, px * 4 - 1, px * 4 - 1], fill=255)
        for name, m in (("ic_launcher", sq), ("ic_launcher_round", rd)):
            out = icon.copy()
            out.putalpha(m.resize((px, px), Image.LANCZOS))
            out.save(RES / f"mipmap-{dpi}" / f"{name}.png")
    play = crop.resize((512, 512), Image.LANCZOS)
    play.save(ROOT / "play_store_icon.png")
    play.save(ROOT / "lexio_icon_final_512.png")


if __name__ == "__main__":
    main()
