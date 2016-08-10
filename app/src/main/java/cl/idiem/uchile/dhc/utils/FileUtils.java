package cl.idiem.uchile.dhc.utils;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cl.idiem.uchile.dhc.Globals;

/**
 * Created by Roberto Obregón Sepúlveda on 18-04-16.
 */
public class FileUtils {
    // helper to retrieve the path of an image URI
    public static String getPathFromImageUri(Uri uri) {
        if (uri == null) return null;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = Globals.getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();

        return picturePath;
    }

    // url = file path or whatever suitable URL you want.
    public static String getMimeType(String url)
    {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String readFile(String path) {
        try {
            return readFile(new FileInputStream(new File(path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFile(InputStream is) {
        byte[] buffer = new byte[1024];

        StringBuilder sb = new StringBuilder();
        try {
            BufferedInputStream buf = new BufferedInputStream(is);
            int n;
            while (-1 != (n = buf.read(buffer))) {
                sb.append(new String(buffer, 0, n));
            }
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    public static String saveImage(Bitmap image, String path) {
        Long tsLong = System.currentTimeMillis() / 1000;
        String nameFile = tsLong.toString();

        try {
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdirs();

            OutputStream fOut = null;
            File file = new File(path, nameFile + ".jpg");
            file.createNewFile();
            fOut = new FileOutputStream(file);

            image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();

            return nameFile + ".jpg";

        } catch (Exception e) {
            e.printStackTrace();
            return nameFile + ".jpg";
        }
    }
}
