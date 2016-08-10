package cl.idiem.uchile.dhc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cl.idiem.uchile.dhc.Assist.Utilitys;
import cl.idiem.uchile.dhc.utils.ImageUtils;

/**
 *
 * Clase que administra la vista "activity_camera"
 * <p/>
 * Es la encargada capturar y guardad la imagen referente la muestra activa en
 * {@link cl.idiem.uchile.dhc.FormularioActivity FormularioActivity}
 *
 */

public class CameraActivity extends AppCompatActivity {

    /**
     * Ubicación donde se almacenan las imagenes capturadas.
     */
    private String MEDIA_DIRECTORY = "DCIM/Camera";
    public static int WIDTH = 600;
    public static int HEIGHT = 800;
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", new Locale("es", "CL")).format(new Date());
    /**
     * Constante de la opción "Tomar Foto", del menú de opciones
     */
    private final int PHOTO_CODE = 100;

    /**
     * Constante de la opción "seleccionar Foto", del menú de opciones
     */
   // private final int SELECT_PICTURE = 200;

    /**
     * Atributo que almacena el contenedor de la vista para la imagen a tomar
     */
    //private ImageView imageView;

    /**
     *
     * Método que es llamado cuando se crea por primera vez la actividad.
     * <p/>
     * Aquí es donde se maneja el negocio de la actividad y genera las instancias necesarias
     * para poder tomar una foto.
     *
     * @param savedInstanceState Paquete que contiene el estado previamente congelado de la actividad, si la hubo.
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Globals globals = (Globals) getApplication().getApplicationContext();
        openCamera();
    }

    /**
     *
     * Método que es llamado cuando se pone en marcha salidas de una actividad,
     * entregando el requestCode con que empezó, el resultCode que retorna
     * y cualquier dato adicional de la misma.
     *
     * @param requestCode Dato con que empieza el request
     * @param resultCode Dato recibido después de la acción
     * @param data Dato adicional al request
     *
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Globals globals = (Globals) getApplicationContext();


        if (resultCode == RESULT_OK) {
                   final String dir = Environment.getExternalStorageDirectory() + File.separator
                          //  + MEDIA_DIRECTORY + File.separator + "DHC-" + globals.getMuestra().getNumMuestra() + ".jpg";
                           + MEDIA_DIRECTORY + File.separator + timeStamp + ".jpg";
                    globals.setDir(dir);
                    System.out.println(dir);
                    decodeBitmap(dir);
                    Toast.makeText(getApplicationContext(),"Imagen Guardada",Toast.LENGTH_SHORT).show();
                    //finish();
                }

        finish();

    }

    /**
     *
     * Método invocado de manera automática, una vez que se finaliza la actividad.
     *
     */

    @Override
    protected void onDestroy() {
        this.finish();
        super.onDestroy();
    }

    /**
     *
     * Método que identifica cuál es la opción del menú que fue seleccionada.
     *
     * @param item El parámetro item es el objeto del ítem que fue seleccionado
     * @return Si fue seleccionado un ítem válido o no.
     *
     */

    /**
     *
     * Método que llama al layout del menú a mostrar en esquina superior derecha de la vista
     * "activity_camera".
     *
     * @param menu El parámetro menu es el objeto que contiene el menú a mostrar
     * @return Si se muestra o no el menú
     *
     */


 /*    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }*/

    /**
     *
     * Método que llama a las funciones nativas de android necesarias para capturar una imagen
     * desde la cámara del dispositivo
     *
     */

    private void openCamera() {
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        System.out.println(file);
        file.mkdirs();

        String path = Environment.getExternalStorageDirectory() + File.separator
              //  + MEDIA_DIRECTORY + File.separator + "DHC-" + numMuestra + ".jpg";
                + MEDIA_DIRECTORY + File.separator + timeStamp +".jpg";
        System.out.println(path);
        File newFile = new File(path);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
        startActivityForResult(intent, PHOTO_CODE);
    }

    /**
     *
     * Método encargado de codificar la imagen capturada a través del método
     * {@link #onActivityResult(int, int, Intent)}.
     *
     * Utiliza el método {@link cl.idiem.uchile.dhc.Assist.Utilitys #encodeToBase64}
     * para una re-codificación
     *
     * @param dir String que contiene la ruta del archivo de imagen generado.
     */

    private void decodeBitmap(final String dir) {

       final Globals globals = (Globals) getApplicationContext();

       /* ExifInterface ei;
        int orientation = 0;
        try {
            ei = new ExifInterface(dir);
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch(orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                dir = rotateImage(90, dir);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                dir = rotateImage(180, dir);
                break;
        }*/
        Bitmap bitmap;
        //bitmap = BitmapFactory.decodeFile(dir);
        bitmap = decodeSampledBitmapFromFile(dir);
        globals.getMuestra().setImage(Utilitys.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 70));

    }

   /* private String rotateImage(int degree, String imagePath){

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
    }*/

    public  Bitmap decodeSampledBitmapFromFile(String path){
        return decodeSampledBitmapFromFile(path, WIDTH, HEIGHT);
    }

    public  Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){
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
}
