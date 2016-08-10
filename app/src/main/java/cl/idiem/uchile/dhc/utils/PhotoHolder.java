package cl.idiem.uchile.dhc.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

//import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cl.idiem.uchile.dhc.Globals;

/**
 * Created by Roberto Obregón Sepúlveda on 18-04-16.
 */
public class PhotoHolder implements Parcelable {

    public static final boolean SHOW_ORIGINAL = true;
    public static final boolean SHOW_THUMB = false;

    private Bitmap mOriginal;
    private String mOriginalPath;

    public PhotoHolder(Uri uri) {
        this(FileUtils.getPathFromImageUri(uri));
    }

    public PhotoHolder(String path) {
        ExifInterface ei;
        int orientation = 0;
        try {
            ei = new ExifInterface(path);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                path = rotateImage(90, path);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                path = rotateImage(180, path);
                break;
        }

        this.mOriginal = ImageUtils.decodeSampledBitmapFromFile(path);
        this.mOriginalPath = FileUtils.saveImage(this.mOriginal, Globals.getContext().getFilesDir() + File.separator);
        this.mOriginalPath = Globals.getContext().getFilesDir() + File.separator.concat(this.mOriginalPath);
    }

    public void showImg(Context c, ImageView view) {
       // Picasso.with(c).load("file://" + this.mOriginalPath).into(view);
    }

    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= ImageUtils.decodeSampledBitmapFromFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagePath;
    }

    public Bitmap getOriginal() {
        return mOriginal;
    }

    public void setOriginal(Bitmap original) {
        this.mOriginal = original;
    }

    public String getOriginalPath() {
        return mOriginalPath;
    }

    public void setOriginalPath(String originalPath) {
        this.mOriginalPath = originalPath;
    }

    public boolean isCreateOriginalPath(){
        return mOriginalPath != null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mOriginal, 0);
        dest.writeString(this.mOriginalPath);
    }

    private PhotoHolder(Parcel in) {
        this.mOriginal = in.readParcelable(Bitmap.class.getClassLoader());
        this.mOriginalPath = in.readString();
    }

    public static final Creator<PhotoHolder> CREATOR = new Creator<PhotoHolder>() {
        public PhotoHolder createFromParcel(Parcel source) {
            return new PhotoHolder(source);
        }

        public PhotoHolder[] newArray(int size) {
            return new PhotoHolder[size];
        }
    };
}