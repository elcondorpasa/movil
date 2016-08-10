package cl.idiem.uchile.dhc.Services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cl.idiem.uchile.dhc.Assist.GPSTracker;
import cl.idiem.uchile.dhc.Globals;


/**
 * Created by Usuario on 26-07-2016.
 */
public class Conectivity extends IntentService implements LocationListener {
    TelephonyManager message;
    GPSTracker gps;
    final Calendar calendar = Calendar.getInstance();
    private String imei;
    final int mMonth = calendar.get(Calendar.MONTH);
    final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    final String fecha = Integer.toString(mDay).concat(Integer.toString(mMonth + 1));
    String latitud;
    String longitud;
    String lat;
    String lon;
    Location mCurrentLocation;
    LocationManager locationManager;
    private boolean send = false;
    private RequestQueue requestQueue;

    public Conectivity() {
        super("Conectivity");
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        lat = String.valueOf(location.getLatitude());
        lon = String.valueOf (location.getLongitude());
        //updateUI();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void updateUI() {
        latitud =(String.valueOf(mCurrentLocation.getLatitude()));
        longitud =(String.valueOf(mCurrentLocation.getLongitude()));


    }


    @Override
    protected void onHandleIntent(Intent intent) {
        message = (TelephonyManager) getSystemService(this.TELEPHONY_SERVICE);
        imei = message.getDeviceId();
        //gps = new GPSTracker(this);
        Context context = this;
        handleActionRun(context);
    }

    private void handleActionRun(final Context context ) {

        Intent localIntent = new Intent(Globals.ACTION_RUN_ISERVICE2);
        String sFichero = "storage/emulated/0/Archivos Formulario DHC/Login/" + "DHC_Login" + fecha + ".txt";
            final String data = readLocal(sFichero, 100, getApplicationContext());

            // Bucle de simulación

            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

                final Handler handler = new Handler();
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                while (1!=0) {
                                 gps = new GPSTracker(context);
                                   // latitud = String.valueOf(gps.getLatitude());
                                   // longitud = String.valueOf(gps.getLongitude());
                                    if (isOnline()) {
                                        try {
                                            sendPost(data, getApplicationContext(),latitud,longitud);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        send = true;
                                        try {
                                            Thread.sleep(300000);

                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }, 2000);




    }
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
            Toast.makeText(context, "(1) Error al mandar estado conectividad ", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            stopForeground(true);
            Toast.makeText(context,"(2) Sin Conexión ", Toast.LENGTH_LONG).show();
        }

        return str;

    }

    private void sendPost(String json , final Context context, String latitud, String longitud ) throws JSONException {
        final JSONObject auxJson = new JSONObject(json);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Globals.getURL() + "connect",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        send = true;
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

                try {
                    params.put("usuario", auxJson.getString( "usuario" ));
                }  catch (JSONException e) {
                }

                params.put("imei",imei);
                params.put("latitud",String.valueOf(gps.getLatitude()));
                params.put("longitud",String.valueOf(gps.getLongitude()));

                System.out.println(params);
                return params;

            }


        };
        requestQueue = Volley.newRequestQueue(getApplication().getApplicationContext());
        requestQueue.add(stringRequest);

    }


    @Override
    public void onDestroy() {
        Intent localIntent = new Intent(Globals.ACTION_PROGRESS_EXIT2);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }


}
