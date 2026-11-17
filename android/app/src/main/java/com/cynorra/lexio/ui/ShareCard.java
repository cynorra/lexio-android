package com.cynorra.lexio.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.drawable.DrawableCompat;

import com.cynorra.lexio.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/** Draws the shareable result picture (4:5, feed/story friendly) and exposes it through the FileProvider. */
public final class ShareCard {
    private static final int W = 1080;
    private static final int H = 1350;

    private ShareCard() {}

    /** @param answers one entry per question: true correct, false wrong, null unanswered. May be empty. */
    public static Uri render(Context ctx, String title, int correct, int total, int streak, List<Boolean> answers) {
        try {
            Bitmap bmp = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bmp);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

            p.setShader(new LinearGradient(0, 0, W, H, 0xFF1B3A73, 0xFF060D1C, Shader.TileMode.CLAMP));
            c.drawRect(0, 0, W, H, p);
            p.setShader(null);
            p.setColor(0x22F97316);
            c.drawCircle(W * 0.85f, H * 0.12f, 420, p);
            p.setColor(0x143B82F6);
            c.drawCircle(W * 0.1f, H * 0.9f, 380, p);

            // logo tile + wordmark
            p.setColor(0x22FFFFFF);
            c.drawRoundRect(new RectF(90, 90, 250, 250), 40, 40, p);
            Drawable logo = ContextCompat.getDrawable(ctx, R.drawable.ic_launcher_foreground);
            if (logo != null) {
                logo.setBounds(100, 100, 240, 240);
                logo.draw(c);
            }
            p.setColor(0xFFFFFFFF);
            p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            p.setTextSize(84);
            c.drawText("Lexio", 280, 195, p);
            float dot = p.measureText("Lexio");
            p.setColor(0xFFF97316);
            c.drawText(".", 280 + dot, 195, p);

            // title
            p.setColor(0xFFFDBA74);
            p.setTextSize(48);
            c.drawText(title, 90, 420, p);

            // score
            p.setColor(0xFFFFFFFF);
            p.setTextSize(330);
            String score = correct + "/" + total;
            c.drawText(score, 90, 740, p);

            // result squares (Wordle-style)
            int n = Math.max(total, 1);
            float size = Math.min(130f, (W - 180f - (n - 1) * 24f) / n);
            float x = 90;
            for (int i = 0; i < n; i++) {
                Boolean ok = answers != null && i < answers.size() ? answers.get(i) : null;
                p.setColor(ok == null ? 0x33FFFFFF : ok ? 0xFF10B981 : 0xFFEF4444);
                c.drawRoundRect(new RectF(x, 820, x + size, 820 + size), 28, 28, p);
                x += size + 24;
            }

            // streak
            if (streak > 0) {
                Drawable flame = ContextCompat.getDrawable(ctx, R.drawable.ic_flame);
                if (flame != null) {
                    flame = DrawableCompat.wrap(flame.mutate());
                    DrawableCompat.setTint(flame, 0xFFFDBA74);
                    flame.setBounds(90, 1020, 170, 1100);
                    flame.draw(c);
                }
                p.setColor(0xFFFFFFFF);
                p.setTextSize(56);
                c.drawText(ctx.getString(R.string.result_streak, streak), 195, 1078, p);
            }

            // call to action
            p.setColor(0xFFF97316);
            c.drawRoundRect(new RectF(90, 1160, W - 90, 1270), 55, 55, p);
            p.setColor(0xFFFFFFFF);
            p.setTextSize(48);
            p.setTextAlign(Paint.Align.CENTER);
            c.drawText(ctx.getString(R.string.share_card_cta) + "  -  " + ctx.getString(R.string.share_card_store),
                    W / 2f, 1232, p);

            File dir = new File(ctx.getCacheDir(), "share");
            if (!dir.exists() && !dir.mkdirs()) return null;
            File file = new File(dir, "lexio_result.png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            }
            bmp.recycle();
            return FileProvider.getUriForFile(ctx, ctx.getPackageName() + ".fileprovider", file);
        } catch (Exception | OutOfMemoryError e) {
            return null; // the caller falls back to sharing text only
        }
    }
}
