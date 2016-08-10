package cl.idiem.uchile.dhc.Assist;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 *
 * Clase encargada de almacenar los métodos estáticos a utilizar en cualquier instancia que se desee/necesite.
 *
 * @author José Becerra
 * @version 08/04/2016/A
 *
 */

public class Utilitys {

    /**
     * String que almacena la key a utilizar en el método de encriptación {@link #stringCrypt(String, int)}.
     */

    private static String key = "idiem883";

    /**
     *
     * Método que permite eliminar un archivo cualquiera, almacenado previamente en el dispositivo.
     *
     * @param file El parámetro file indica el nombre del archivo que se desea eliminar
     * @return Retorna si fue eliminado o no el archivo
     */

    public static boolean deleteFile( String file ) {

        String sFichero = "storage/emulated/0/Archivos Formulario DHC/" + file;
        File fichero = new File(sFichero);

        if( fichero.exists() )
            return fichero.delete();
        else
            return false;

    }

    /**
     *
     * Método que encripta o desencripta un string dado, en base a una key entregada por
     * {@link #key}.
     * <p/>
     * La labor a realizar por el método dependerá del valor entregado para el parámetro type
     *
     * @param string Es el String a trabajar, ya sea para encriptar o desencriptar
     * @param type Es el tipo de acción que se realizará (encriptar/desencriptar)
     * @return Devuelve un String trabajado, encriptado o desencriptado
     *
     */
    public static String stringCrypt( String string, int type ) {

        String sReturn;
        byte[] value;
        string += key;

        if ( 1 == type ) {
            value = Base64.encode(string.getBytes(), Base64.DEFAULT);
            sReturn = new String( value );
        } else if ( 0 == type ) {
            value = Base64.decode(string.getBytes(), Base64.DEFAULT);
            sReturn = new String( value );
            sReturn.replace( key, "" );
        } else
            sReturn = "";

        return sReturn;

    }

    /**
     *
     * Método que codifica una imagen en base64.
     * <p/>
     * Este método es utilizado para poder enviar la foto tomada en
     * {@link cl.idiem.uchile.dhc.FormularioActivity}, en formato JSON al servidor.
     *
     * @param image Este parámetro es el que contiene la imagen a codificar.
     * @param compressFormat Es el método de compresión a utilizar durante la codificación.
     * @param quality Calidad con la que se codificará la imagen.
     *
     * @return String de la imagen codificada.
     */

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {

        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();

        image.compress(compressFormat, quality, byteArrayOS);

        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);

    }


}
