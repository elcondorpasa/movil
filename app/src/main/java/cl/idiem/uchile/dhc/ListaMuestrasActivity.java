package cl.idiem.uchile.dhc;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import cl.idiem.uchile.dhc.Assist.ListaArrayAdapter;
import cl.idiem.uchile.dhc.Clases.API.MuestraListado;

/**
 * Actividad del listado de muestras
 */

public class ListaMuestrasActivity extends AppCompatActivity
        implements Response.Listener<JSONObject>, Response.ErrorListener {

    private ListView list;
    private ArrayList<MuestraListado> muestras = new ArrayList<MuestraListado>();
    private ListaArrayAdapter adapter;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private int mInterval = 10000; // 5 seg by default, can be changed later
    private Handler mHandler;
    private View mProgressView;
    private int contadorLinea;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private CameraUpdate mCamera; // CameraUpdate nos permite el movimiento de la camara mapa.
    final static Calendar calendar = Calendar.getInstance();
    final static int mMonth = calendar.get(Calendar.MONTH);
    final static int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    final static String fecha = Integer.toString(mDay).concat(Integer.toString(mMonth + 1));
    final static String fechaAnterior = Integer.toString(mDay - 1).concat(Integer.toString(mMonth + 1));

    /**
     * Método que es utilizado cuando se inicia la actividad por primera vez, obtiene el listado
     * de muestras en caso de que existan o haya conectividad
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Globals globals = (Globals) getApplicationContext();
        final Context context = this;
        final int method = Request.Method.GET;
        final String latit = globals.getLatitud();
        final String longi = globals.getLongitud();
        setContentView(R.layout.activity_lista_muestras);
        mProgressView = findViewById(R.id.load_progress);


        list = (ListView) findViewById(R.id.ListView01);
        muestras.add(new MuestraListado("No hay muestras disponibles"));
        adapter = new ListaArrayAdapter(this, muestras);
        list.setAdapter(adapter);

        request = Volley.newRequestQueue(context);
        getRequest(method);
        globals.setListaMuestras(muestras);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (muestras.get(position).getEstado() != R.drawable.lost) {
                    globals.setMuestraActiva(muestras.get(position));
                    startActivity(new Intent(ListaMuestrasActivity.this, FormularioActivity.class));
                } else
                    Toast.makeText(getApplicationContext(), "El estado actual de la muestra no permite realizar esta acción", Toast.LENGTH_LONG).show();
            }

        });

        mHandler = new Handler();
        startRepeatingTask();
        setUpMapIfNeeded();

    }

    /**
     * añade los marcadores al mapa que se muestra en la actividad de listado de muestras
     */
    private void setMultipleMarkers() {

        Globals globals = (Globals) getApplicationContext();
        // Creamos Markers para añadirlos a nuestro Mapa.
        for (int i = 0; i < globals.getListaMuestras().size(); i++) {
            if (globals.getListaMuestras().get(i).getLongitud() != null
                    && globals.getListaMuestras().get(i).getLatitud() != null
                    && 0 != globals.getListaMuestras().get(i).getLatitud()
                    && 0 != globals.getListaMuestras().get(i).getLongitud())

                setMarker(
                        new LatLng(
                                globals.getListaMuestras().get(i).getLatitud(),
                                globals.getListaMuestras().get(i).getLongitud()
                        ),
                        "Muestra: " + globals.getListaMuestras().get(i).getNumInterno(),
                        "Obra: " + globals.getListaMuestras().get(i).getNombreObra(),
                        1,
                        0.6F,
                        0.7F,
                        R.drawable._pin
                );
        }
    }

    /**
     * Shows the progress UI
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                getRequest(Request.Method.GET);
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };


    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    /**
     * Método que obtiene el listado de muestras
     * @param method
     */
    public void getRequest(int method) {
        showProgress(true);
        Globals globals = (Globals) getApplication().getApplicationContext();
        jsonObjectRequest = new JsonObjectRequest(method, Globals.getURL() + "listaMuestras/" + globals.getInspector(), this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {

        ArrayList<MuestraListado> elementos = getJSONObject(response);
        muestras.clear();

        for (int i = 0; i < elementos.size(); i++) {
            muestras.add(elementos.get(i));
        }

        adapter = new ListaArrayAdapter(this, muestras);
        String sFicheroAnterior = "storage/emulated/0/Archivos Formulario DHC/ListadoMuestras_DHC_" + fechaAnterior + ".txt";
        File ficheroAnterior = new File(sFicheroAnterior);
        String sficheroActual = "storage/emulated/0/Archivos Formulario DHC/ListadoMuestras_DHC_" + fecha + ".txt";
        File ficheroActual = new File(sficheroActual);
        if (ficheroAnterior.exists() || ficheroActual.exists()) {
            ficheroAnterior.delete();
            ficheroActual.delete();
        }
        createFile(response.toString());

        list.setAdapter(adapter);
        showProgress(false);
        setMultipleMarkers();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        JSONObject json;

        try {
            json = new JSONObject(readLocal(this));
        } catch (JSONException e) {
            e.printStackTrace();
            json = new JSONObject();
        }

        ArrayList<MuestraListado> elementos = getJSONObject(json);
        muestras.clear();

        for (int i = 0; i < elementos.size(); i++) {
            muestras.add(elementos.get(i));
        }

        adapter = new ListaArrayAdapter(this, muestras);

        list.setAdapter(adapter);
        showProgress(false);
        setMultipleMarkers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onDestroy() {
        this.stopRepeatingTask();
        this.finish();
        super.onDestroy();
    }

    /**
     * Menú para seleccionar el tipo de mapa a utilizar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            case R.id.MenuOpcion1:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // Establecemos el mapa normal
                return true;

            case R.id.MenuOpcion2:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // Establecemos el mapa satelite
                return true;

            case R.id.MenuOpcion3:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN); // Establecemos el mapa terrestre
                return true;

            case R.id.MenuOpcion4:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Establecemos el mapa hibrido
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
   /* private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Inicializamos la app con el mapa hibrido
                mMap.setMyLocationEnabled(true);

                //mMap.getMyLocation().getLatitude();
                //mMap.getMyLocation().getLongitude();
                setUpMap();
            }
        }
    }*/
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map))
                    .getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;

                            // Check if we were successful in obtaining the map.
                            if (mMap != null) {
                                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Inicializamos la app con el mapa hibrido
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    // here to request the missing permissions, and then overriding
                                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                    //                                          int[] grantResults)
                                    // to handle the case where the user grants the permission. See the documentation
                                    // for ActivityCompat#requestPermissions for more details.
                                    return;
                                }
                                mMap.setMyLocationEnabled(true);
                                setUpMap();
                            }
                        }
                    });
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        // mMap.addMarker(new MarkerOptions().position(new LatLng(40.070823, -2.137360)).title("Cuenca")
        //         .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        //         .icon(BitmapDescriptorFactory.fromResource(R.drawable.cafeteria))
        //         .snippet("Cidudad Patrimonio de la Humanidad"));
      //  mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng( mMap.getMyLocation().getLatitude(),  mMap.getMyLocation().getLongitude()), 14);
        Globals globals = (Globals) getApplicationContext();
        mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.valueOf(globals.getLatitud()), Double.valueOf(globals.getLongitud())), 14);
        mMap.animateCamera(mCamera);
    }
    /**
     * Podemos añadir a nuestro mapa tantos Markers como queramos, solo tenemos que declararlos en el
     * método onCreate.
     *
     * @param position Es la latitud y longitud de donde va a estar situado nuestro Marker en el mapa.
     * @param title Es el título del Marker.
     * @param info Información adicional en la etiqueta del Marker.
     * @param opacity Opacidad del Marker.
     * @param dimension1 Alto del Marker.
     * @param dimension2 Ancho del Marker.
     * @param icon Es la imagen de nuestro Marker (Icono).
     */
    private void setMarker(LatLng position, String title, String info, float opacity, float dimension1, float dimension2, int icon){
        // Agregamos un marcador para indicar sitios de interes.
        mMap.addMarker(new MarkerOptions()
                .position(position)     // Posicion del marcador
                .title(title)           // Agrega titulo al marcador
                .snippet(info)          // Agrega información detalle relacionada con el marcador
                .alpha(opacity)         // Opacidad del icono
                .anchor(dimension1, dimension2)     // Tamaño del icono (alto y ancho)
                .icon(BitmapDescriptorFactory.fromResource(icon)));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    private ArrayList<MuestraListado> getJSONObject( JSONObject json ) {
        final ArrayList<MuestraListado> muestras = new ArrayList<MuestraListado>();
        final Globals globals = (Globals)getApplicationContext();
        int numInterno;

        MuestraListado elemento;

        String key;
        try {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()){
                key = keys.next();
                elemento = new MuestraListado();
                try {
                    elemento.setNumInterno(Integer.parseInt(key));
                } catch (NumberFormatException e) {
                    elemento.setNumInterno(0);
                }
                muestras.add(elemento);
            }

            for (int i = 0; i < muestras.size(); i++) {
                try {
                    if (json.has(String.valueOf(muestras.get(i).getNumInterno()))) {

                        JSONObject jsonAux = json.getJSONObject(String.valueOf(muestras.get(i).getNumInterno()));
                        numInterno = jsonAux.getInt("numInterno");
                        muestras.get(i).setId(jsonAux.getInt("id"));
                        muestras.get(i).setDataNumInterno("NUM. MUESTRA: " + (numInterno>9?numInterno:"0"+numInterno));
                        muestras.get(i).getDatos().add("OBRA: " + jsonAux.getString("OBRA"));
                        muestras.get(i).getDatos().add("DIRECCION: " + jsonAux.getString("DIRECCION"));
                        muestras.get(i).getDatos().add("COMUNA: " + jsonAux.getString("COMUNA"));
                        muestras.get(i).getDatos().add("CONSTRUCTORA: " + jsonAux.getString("contratista"));
                        muestras.get(i).setCodigoObra(jsonAux.getString("codigo_obra"));
                        muestras.get(i).setComuna(jsonAux.getString("comuna"));
                        muestras.get(i).setContratista(jsonAux.getString("contratista"));
                        muestras.get(i).setCorrelativoPetreos(jsonAux.getString("correlativo_petreos"));
                        muestras.get(i).setDestinatario(jsonAux.getString("destinatario"));
                        muestras.get(i).setDireccionObra(jsonAux.getString("direccion_obra"));
                        muestras.get(i).setEdadesEnsayo(jsonAux.getString("edades_ensayo"));
                        muestras.get(i).setEstado(globals.getEstados().get(jsonAux.getString("estado")));
                        muestras.get(i).setFrecuencia(jsonAux.getInt("frecuencia"));
                        muestras.get(i).setGradoTipo(jsonAux.getString("grado_tipo"));
                        muestras.get(i).setLatitud(Double.parseDouble(jsonAux.getString("latitud_obra").equals("") ? "0" : jsonAux.getString("latitud_obra")));
                        muestras.get(i).setLongitud(Double.parseDouble(jsonAux.getString("longuitud_obra").equals("") ? "0" : jsonAux.getString("longuitud_obra")));
                        muestras.get(i).setNombreObra(jsonAux.getString("nombre_obra"));
                        muestras.get(i).setNumInterno(jsonAux.getInt("numInterno"));
                        muestras.get(i).setNumPedidoPetreos(jsonAux.getInt("num_pedido_petreos"));
                        muestras.get(i).setObraId(jsonAux.getInt("obra_id"));
                        muestras.get(i).setObservacion(jsonAux.getString("observacion"));
                        muestras.get(i).setPrioridad(jsonAux.getString("prioridad"));
                        muestras.get(i).setSolicitante(jsonAux.getString("solicitante"));
                        muestras.get(i).setTipoAsentamiento(jsonAux.getString("tipo_asentamiento"));
                        muestras.get(i).setTipoProbeta(jsonAux.getString("tipo_probeta"));
                        muestras.get(i).setCantMuestras(jsonAux.getInt("cantidad_muestras"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort( muestras );
        return muestras;
    }

    private void createFile( String arr ) {

        File sdCard, directory;
        FileWriter file;

        try {
            if (Environment.getExternalStorageState().equals("mounted")) {

                sdCard = Environment.getExternalStorageDirectory();

                contadorLinea = arr.length();

                FileOutputStream fout;
                try {
                    directory = new File(sdCard.getAbsolutePath()
                            + "/Archivos Formulario DHC");
                    directory.mkdirs();
                    file = new FileWriter(directory + "/ListadoMuestras_DHC_"+ fecha + ".txt", false);
                    file.write(arr);
                    file.flush();
                    file.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private static String readLocal( Context context) {

        File sdCard, directory;
        sdCard = Environment.getExternalStorageDirectory();

        directory = new File(sdCard.getAbsolutePath()
                + "/Archivos Formulario DHC");
        byte[] buffer = new byte[100000];
        String str = new String();

        try {
            FileInputStream fis = new FileInputStream(new File(directory + "/ListadoMuestras_DHC_" + fecha +".txt"));
            fis.read(buffer);
            fis.close();
            str = new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return str;

    }
}
