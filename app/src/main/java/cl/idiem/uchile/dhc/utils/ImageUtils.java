package cl.idiem.uchile.dhc.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cl.idiem.uchile.dhc.Globals;

/**
 * Created by Roberto Obregón Sepúlveda on 18-04-16.
 */
public class ImageUtils {
    public static int WIDTH = 720;
    public static int HEIGHT = 1080;
    public static final int PICTURE_REQUEST_CODE_FILE = 1000;
    public static final int PICTURE_REQUEST_CODE_CAMERA = 2000;

    public static Bitmap decodeSampledBitmapFromFile(String path){
        return decodeSampledBitmapFromFile(path, WIDTH, HEIGHT);
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public static File createImageFile() throws IOException {
        //Globals globals = (Globals) Globals.getInstance().getApplicationContext();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("es", "CL")).format(new Date());
        // String imageFileName = "DHC-"+Integer.toString(123/*UserPreferences.getNumeroMuestra(Globals.getContext())*/);
        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = new File("storage/emulated/0/DCIM/Camera");
        storageDir.mkdir();
        return createTempFile(timeStamp, ".jpg", storageDir);
    }

    public static String dispatchTakePictureIntent(Activity activity) {
        return dispatchTakePictureIntent(activity, null);
    }

    public static String dispatchTakePictureIntent(Activity activity, Fragment fragment) {
        String path = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                path = photoFile.getAbsolutePath();

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                if (fragment != null)
                    fragment.startActivityForResult(takePictureIntent, PICTURE_REQUEST_CODE_CAMERA);
                else
                    activity.startActivityForResult(takePictureIntent, PICTURE_REQUEST_CODE_CAMERA);
            }
            else
                Toast.makeText(activity,"Error al crear imagen", Toast.LENGTH_LONG).show();
        }
        else
            Toast.makeText(activity, "Error en camara", Toast.LENGTH_LONG).show();

        return path;
    }
    public static File createTempFile(String prefix, String suffix, File directory)
            throws IOException {
        // Force a prefix null check first
        if (prefix.length() < 3) {
            throw new IllegalArgumentException("prefix must be at least 3 characters");
        }
        if (suffix == null) {
            suffix = ".tmp";
        }
        File tmpDirFile = directory;
        if (tmpDirFile == null) {
            String tmpDir = System.getProperty("java.io.tmpdir", ".");
            tmpDirFile = new File(tmpDir);
        }
        File result;
        do {
            result = new File(tmpDirFile, prefix + suffix);
        } while (!result.createNewFile());
        return result;
    }
}
