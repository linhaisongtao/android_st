package com.netease.bobo.pathdemo.util;

import android.util.Log;

import com.netease.bobo.pathdemo.App;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by gzdaisongsong@corp.netease.com on 2017/7/28.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static File getRootDir() {
        return App.getApp().getExternalCacheDir();
    }

    public static File openFile(String fileName) {
        return new File(getRootDir(), fileName);
    }

    public static String read(String fileName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = null;
        try {
            in = new FileInputStream(openFile(fileName));
            byte[] buffer = new byte[1024];
            int size = in.read(buffer);
            while (size > 0) {
                baos.write(buffer, 0, size);
                size = in.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String string = baos.toString();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    public static void write(String fileName, String content) {
        File file = openFile(fileName);
        BufferedWriter writer = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void clearAll() {
        deleteFileRecursion(getRootDir());
    }

    private static void deleteFileRecursion(File file) {
        Log.i(TAG, "deleteFileRecursion: delete file " + file.getAbsolutePath());
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                deleteFileRecursion(file1);
            }
        }
    }
}
