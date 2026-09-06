package com.citycollege.musicexplorer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import com.citycollege.musicexplorer.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageLoader {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);
    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    public static void load(ImageView imageView, String imageUrl) {
        imageView.setImageResource(R.drawable.ic_music_note);
        imageView.setTag(imageUrl);
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return;
        }
        EXECUTOR.execute(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
                connection.setConnectTimeout(8000);
                connection.setReadTimeout(8000);
                InputStream stream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(stream);
                MAIN.post(() -> {
                    Object tag = imageView.getTag();
                    if (imageUrl.equals(tag) && bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } catch (Exception ignored) {
                MAIN.post(() -> imageView.setImageResource(R.drawable.ic_music_note));
            }
        });
    }
}
