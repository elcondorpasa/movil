package cl.idiem.uchile.dhc.Services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cl.idiem.uchile.dhc.Assist.Utilitys;
import cl.idiem.uchile.dhc.Globals;
import cl.idiem.uchile.dhc.R;

/**
 * Un {@link IntentService} que simula un proceso en primer plano
 *  Clase que envía los datos del formulario, si no hay conectividad, este los deja en espera hasta que exista conexión.
 */
public class UploadService extends IntentService {

    private int muestra;
    private boolean send = false;
    private RequestQueue requestQueue;
    private String sResponse;
    private Bitmap bitmap;
    private Uri uri;
    private Bitmap bitmap2;
    private Uri uri2;



    public UploadService() {
        super("UploadService");

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Globals.ACTION_RUN_ISERVICE.equals(action)) {
                muestra = intent.getIntExtra("muestra", 0);
                handleActionRun( muestra );
            }
        }
    }

    /**
     * Maneja la acción de ejecución del servicio
     */
    private void handleActionRun( int muestra ) {

        Intent localIntent = new Intent(Globals.ACTION_RUN_ISERVICE);
        String sFichero = Globals.DIR_STORAGE + "DHC_" + muestra + ".txt";
        try {
            // Se construye la notificación
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.stat_sys_upload_done)
                    .setContentTitle( "DHC::IDIEM - Envío de muestra " + muestra )
                    .setContentText("Procesando...");

            String data = readLocal(sFichero, 8000, getApplicationContext());


            // Bucle de simulación
            startForeground(1, builder.build());
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            if (requestQueue != null ) requestQueue.stop();


            if(send)
                send=false;


            while (  !send ) {
                if ( isOnline() ) {
                    sendPost(data, muestra, getApplicationContext());
                    send = true;
                }
            }
            // Quitar de primer plano
            if (send) {
                send =false;
                stopForeground(true);
            }
        }  catch (JSONException e) {
        }
    }

    /**
     * Método que crea la notificación cuando la muestra ya es enviada.
     */
    @Override
    public void onDestroy() {
        Intent localIntent = new Intent(Globals.ACTION_PROGRESS_EXIT);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    /**
     * Método que hace un ping constantemente consultando si existe conextividad con internet
     * @return estado de la conectividad
     */
    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          {  }
        catch (InterruptedException e) {  }

        return false;
    }

    /**
     * Método que busca y lee un archivo
     * @param Adress dirección donde se encuentra almacenado el archivo a leer
     * @param lenght cantidad de caracteres que se van a leer del archivo a leer
     * @param context contexto de la aplicación
     * @return el string del texto leido.
     */
    private String readLocal(String Adress, int lenght, Context context) {

        byte[] buffer = new byte[lenght];
        String str = new String();

        try {
            FileInputStream fis = new FileInputStream(new File(Adress));
            fis.read(buffer);
            fis.close();
            str = new String(buffer);
        } catch (FileNotFoundException e) {
            stopForeground(true);
            Toast.makeText(context, "(1) Error al intentar enviar muestra " + muestra, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            stopForeground(true);
            Toast.makeText(context,"(2) Error al intentar enviar muestra " + muestra, Toast.LENGTH_LONG).show();
        }

        return str;

    }

    /**
     * Método que envía el json con los datos a guardar en la base de datos.
     * @param json json con los datos de la boleta a enviar
     * @param muestra numero de la muestra
     * @param context contexto de la aplicación
     * @throws JSONException
     */
    private void sendPost( String json ,final int muestra, final Context context ) throws JSONException {
        final JSONObject auxJson = new JSONObject(json);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Globals.getURL() + "form",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        send = true;
                        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                                .setSmallIcon(R.drawable.cloud)
                                .setTicker("Muestra " + muestra + " enviada")
                                .setWhen(System.currentTimeMillis())
                                .setContentTitle("Muestra " + muestra + " enviada")
                                .setContentText("Respuesta del Servidor: " + response)
                                .setContentInfo(String.valueOf(muestra));

                        NotificationManager notifManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                        notifManager.notify(muestra, builder.build());
                        Toast.makeText(context, "Muestra " + muestra + ":" + response, Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        send = false;
                    }
                }) {
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("api_key", "$2y$10$jriZ2PDg8sPnPVJJ2xPD0ObFKYgHDHCTZ2wliVnx0ZF.1itLzFudi");
                return headers;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                JSONObject json;
                String key;
                Iterator<String> keys;
                boolean bViajePerdido = false;
                String datosAntecedentes = "{}";
                String mMuestra = "{}";
                String mAntecedentesHormigonMuestreo = "{}";
                String mAntecedentesMuestreo = "{}";
                String mAntecedentesProbeta = "{}";
                String mColocadoEn = "{}";
                String mEquipoUtilizado = "{}";
                String mObservaciones = "{}";
                String mOtrosEnsayosInSitu = "{}";
                String verificaViajePerdido ="";
                String dir = "";

                try {
                    datosAntecedentes = auxJson.getString( "datosAntecedentes" );
                    mMuestra = auxJson.getString( "Muestra" );
                    mAntecedentesHormigonMuestreo = auxJson.getString("AntecedentesHormigonMuestreo");
                    mAntecedentesMuestreo = auxJson.getString("AntecedentesMuestreo");
                    mAntecedentesProbeta = auxJson.getString("AntecedentesProbeta");
                    mColocadoEn = auxJson.getString("ColocadoEn");
                    mEquipoUtilizado = auxJson.getString("EquipoUtilizado");
                    mObservaciones = auxJson.getString("Observaciones");
                    mOtrosEnsayosInSitu = auxJson.getString("OtrosEnsayosInSitu");
                }  catch (JSONException e) {
                }
                //<editor-fold desc="Envia idTalonario">
                try {
                    json = new JSONObject(datosAntecedentes);

                    keys = json.keys();
                    while (keys.hasNext()) {
                        key = keys.next();
                        if(key.equalsIgnoreCase("dir")) {
                             dir = String.valueOf(json.get(key));
                        }
                        try {
                            params.put(key, String.valueOf(json.get(key)));
                        } catch (JSONException e) {
                            params.put(key, "");
                        }
                    }
                } catch (JSONException e) {
                    json = new JSONObject();
                }
                //</editor-fold>
                //<editor-fold desc="Envia Muestra">
                try {
                    json = new JSONObject(mMuestra);

                    keys = json.keys();
                    while (keys.hasNext()) {
                        key = keys.next();
                        try {

                            if ( key.equals("viajePerdido") ) {
                                verificaViajePerdido = String.valueOf(json.get(key));
                            }
                            if(verificaViajePerdido.equals("true"))
                               bViajePerdido = true;
                            params.put(key, String.valueOf(json.get(key)));
                        } catch (JSONException e) {
                            params.put(key, "");
                        }
                    }
                } catch (JSONException e) {
                    json = new JSONObject();
                }

                //</editor-fold>
                if (!bViajePerdido) {

                    //<editor-fold desc="Envía Antecedentes Hormigón Muestreo">
                    try {
                        json = new JSONObject(mAntecedentesHormigonMuestreo);

                        keys = json.keys();
                        while (keys.hasNext()) {
                            key = keys.next();
                            try {
                                params.put(key, String.valueOf(json.get(key)));
                            } catch (JSONException e) {
                                params.put(key, "");
                            }
                        }

                    } catch (JSONException e) {
                        json = new JSONObject();
                    }
                    //</editor-fold>

                    //<editor-fold desc="Envía Antecedentes Muestreo">
                    try {
                        json = new JSONObject(mAntecedentesMuestreo);

                        keys = json.keys();
                        while (keys.hasNext()) {
                            key = keys.next();
                            try {
                                params.put(key, String.valueOf(json.get(key)));
                            } catch (JSONException e) {
                                params.put(key, "");
                            }
                        }

                    } catch (JSONException e) {
                        json = new JSONObject();
                    }
                    //</editor-fold>

                    //<editor-fold desc="Envía Antecedentews Probeta">
                    try {
                        json = new JSONObject(mAntecedentesProbeta);

                        keys = json.keys();
                        while (keys.hasNext()) {
                            key = keys.next();
                            try {
                                params.put(key, String.valueOf(json.get(key)));
                            } catch (JSONException e) {
                                params.put(key, "");
                            }
                        }

                    } catch (JSONException e) {
                        json = new JSONObject();
                    }
                    //</editor-fold>

                    //<editor-fold desc="Envía Colocado En">
                    try {
                        json = new JSONObject(json.getString(mColocadoEn));

                        keys = json.keys();
                        while (keys.hasNext()) {
                            key = keys.next();
                            try {
                                params.put(key, String.valueOf(json.get(key)));
                            } catch (JSONException e) {
                                params.put(key, "");
                            }
                        }

                    } catch (JSONException e) {
                        json = new JSONObject();
                    }
                    //</editor-fold>

                    //<editor-fold desc="Envía Equipo Utilizado">
                    try {
                        json = new JSONObject(mEquipoUtilizado);

                        keys = json.keys();
                        while (keys.hasNext()) {
                            key = keys.next();
                            try {
                                params.put(key, String.valueOf(json.get(key)));
                            } catch (JSONException e) {
                                params.put(key, "");
                            }
                        }

                    } catch (JSONException e) {
                        json = new JSONObject();
                    }
                    //</editor-fold>

                    //<editor-fold desc="Envía Observaciones">
                    try {
                        json = new JSONObject(mObservaciones);

                        keys = json.keys();
                        while (keys.hasNext()) {
                            key = keys.next();
                            try {
                                params.put(key, String.valueOf(json.get(key)));
                            } catch (JSONException e) {
                                params.put(key, "");
                            }
                        }

                    } catch (JSONException e) {
                        json = new JSONObject();
                    }
                    //</editor-fold>

                    //<editor-fold desc="Envía Otros Ensayos">
                    try {
                        json = new JSONObject(mOtrosEnsayosInSitu);

                        keys = json.keys();
                        while (keys.hasNext()) {
                            key = keys.next();
                            try {
                                params.put(key, String.valueOf(json.get(key)));
                            } catch (JSONException e) {
                                params.put(key, "");
                            }
                        }

                    } catch (JSONException e) {
                        json = new JSONObject();
                    }
                    //</editor-fold>



                }
                if(verificaViajePerdido.equals("false")){


                    //uri = Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "DCIM/Camera/DHC-" + muestra + ".jpg");
                    uri = Uri.parse(dir);
                    System.out.println(uri);
                    bitmap = BitmapFactory.decodeFile(uri.getPath());
                    params.put("image", Utilitys.encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 50));
                }
               // uri = Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "DCIM/Camera/DHC-" + muestra + ".jpg");
                uri2 = Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "Archivos Formulario DHC/Firmas/Firma-" + muestra + ".JPEG");
               // bitmap = BitmapFactory.decodeFile(uri.getPath());
                bitmap2 = BitmapFactory.decodeFile(uri2.getPath());
                //bitmap = decodeFile(uri.getPath());
                //bitmap2 = decodeFile(uri2.getPath());


                params.put("firma",Utilitys.encodeToBase64(bitmap2, Bitmap.CompressFormat.JPEG, 20));

               // System.out.println(params);
                return params;
            }

        };
        requestQueue = Volley.newRequestQueue(getApplication().getApplicationContext());
        requestQueue.add(stringRequest);
    }

 /*private Bitmap decodeFile(String f) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=100;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *=2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }*/



}