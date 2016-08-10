package cl.idiem.uchile.dhc;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sewoo.port.android.BluetoothPort;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import cl.idiem.uchile.dhc.Assist.AlertView;
import cl.idiem.uchile.dhc.Assist.Utilitys;
import cl.idiem.uchile.dhc.Clases.API.Elemento;
import cl.idiem.uchile.dhc.Clases.Formulario.Instrumentos;
import cl.idiem.uchile.dhc.Services.Conectivity;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,  Response.Listener<JSONObject>, Response.ErrorListener {

    private ListView list;
    private BluetoothPort bluetoothPort = BluetoothPort.getInstance();
    private static final String TAG = "BluetoothConnectMenu";
    private RequestQueue request;
    private LocationListener gps;
    JsonObjectRequest jsonObjectRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Globals globals = (Globals)getApplicationContext();
        globals.getTalonarios().getTalonariosFromFile(globals.getInspector(), getApplicationContext());
        final ArrayList<Elemento> elementos = globals.getElementos();
        if ( elementos == null ) {
            globals.setElementos(new ArrayList<Elemento>());
        }

        globals.setLocation();
        globals.getEstados().put("Pendiente", R.drawable.working);
        //globals.getEstados().put("Pendiente2", R.drawable.working2);
        globals.getEstados().put("Tomada", R.drawable.done);
        globals.getEstados().put("Anulada", R.drawable.lost);
        globals.getEstados().put("Perdida", R.drawable.lost);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById( R.id.ListView01 );

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        request = Volley.newRequestQueue(this);
        getRequest();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        turnIntentService();
    }

    public void getRequest() {
        final Globals globals = (Globals)getApplicationContext();
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Globals.getURL()+"selecciones/"+globals.getInspector() , this, this);
        request.add(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        getJSONArray(response, null);
        createFile(response.toString());
        llenaSelects();

    }

    @Override
    public void onErrorResponse(VolleyError error) {
        String sFichero = "storage/emulated/0/Archivos Formulario DHC/DHC_Selects.txt";
        File fichero = new File(sFichero);

        if(fichero.exists()){

            try {
                JSONObject json = new JSONObject( readLocal(sFichero, 3000, getApplication().getApplicationContext()) );
                getJSONArray(json, null);
                llenaSelects();
            } catch (JSONException e) {
                getRequest();
            }
        } else {
            getRequest();
        }

    }


    private void llenaSelects() {
        final Globals globals = (Globals)getApplicationContext();

        ArrayList<Elemento> elementos = globals.getElementos();
        int j;
        for(int i=0; i< elementos.size(); i++){

            ArrayList<String> datos = elementos.get(i).getDatos();

            if ( elementos.get(i).getTipo().equalsIgnoreCase("select") ) {

                //<editor-fold desc="AntecedentesHormigonMuestreo">
                if ( elementos.get(i).getPadre() != null && elementos.get(i).getPadre().equalsIgnoreCase("AntecedentesHormigonMuestreo") ) {

                    if (elementos.get(i).getNombre().equalsIgnoreCase("compactObra") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getCompactsObra().add(datos.get(j));
                        }
                    }

                    if (elementos.get(i).getNombre().equalsIgnoreCase("confeccionados") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getConfeccionados().add(datos.get(j));
                        }
                    }

                    if (elementos.get(i).getNombre().equalsIgnoreCase("aditivo") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getAditivos().add(datos.get(j));
                        }
                    }

                    if (elementos.get(i).getNombre().equalsIgnoreCase("adicion") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getAdiciones().add(datos.get(j));
                        }
                    }

                    if (elementos.get(i).getNombre().equalsIgnoreCase("medidaAditivo") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getMedidasAditivo().add(datos.get(j));
                        }
                    }

                }
                //</editor-fold>

                //<editor-fold desc="AntecedentesMuestreo">
                if ( elementos.get(i).getPadre() != null && elementos.get(i).getPadre().equalsIgnoreCase("AntecedentesMuestreo") ) {

                    if (elementos.get(i).getNombre().equalsIgnoreCase("asentamiento") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getAsentamientos().add(datos.get(j));
                        }
                    }

                    if (elementos.get(i).getNombre().equalsIgnoreCase("compactacion") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getCompactaciones().add(datos.get(j));
                        }
                    }

                    if (elementos.get(i).getNombre().equalsIgnoreCase("condClima") ){

                        for(j=0; j< datos.size(); j++) {
                            globals.getCondsClima().add(datos.get(j));
                        }
                    }

                }
                //</editor-fold>

                //<editor-fold desc="ColocadoEn">

                if (elementos.get(i).getNombre().equalsIgnoreCase("tipoEdif") ){

                    for(j=0; j< datos.size(); j++) {
                        globals.getTiposEdif().add(datos.get(j));
                    }
                }
                //</editor-fold>

                //<editor-fold desc="AntecedentesProbeta">

                if (elementos.get(i).getNombre().equalsIgnoreCase("cantProbeta") ){

                    globals.setCantProbetas(Integer.parseInt(datos.get(0)));
                }

                if (elementos.get(i).getNombre().equalsIgnoreCase("curadoProb") ){

                    for(j=0; j< datos.size(); j++) {
                        globals.getCuradosProb().add(datos.get(j));
                    }
                }

                if (elementos.get(i).getNombre().equalsIgnoreCase("probEmpleada") ){
                    for(j=0; j< datos.size(); j++) {
                        globals.getProbEmpleadas().add(datos.get(j));
                    }
                }

                if (elementos.get(i).getNombre().equalsIgnoreCase("sinProbeta") ){

                    for(j=0; j< datos.size(); j++) {
                        globals.getSinProbetas().add(datos.get(j));
                    }
                }
                //</editor-fold>
            }
        }

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
            e.printStackTrace();
            Toast.makeText(context, "No se encontraron datos", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context,"Error al procesar los datos", Toast.LENGTH_LONG).show();
        }

        return str;

    }

    private void getJSONArray( JSONObject json, String padre ) {
        final Globals globals = (Globals)getApplicationContext();
        final ArrayList<Elemento> elementos = globals.getElementos();
        Elemento elemento;
        JSONArray jsonAuxArr;
        String key;
        try {
            Iterator<String> keys = json.keys();
            while (keys.hasNext()){
                key = keys.next();
                elemento = new Elemento();
                elemento.setNombre(key);
                elemento.setTipo("select");
                elemento.setPadre(padre);
                elementos.add(elemento);

                if ( padre != null ) {
                    for (int i = 0 ; i< elementos.size(); i++ ){
                        if ( elementos.get( i ).getNombre().equals(padre) )
                            elementos.get( i ).getDatos().add(key);
                    }
                }

            }

            for (int i = 0; i < elementos.size(); i++) {
                try {
                    if (json.has(elementos.get(i).getNombre())) {
                        if (json.get(elementos.get(i).getNombre()).getClass().equals(JSONObject.class)) {
                            getJSONArray((JSONObject) json.get(elementos.get(i).getNombre()), elementos.get(i).getNombre());
                        } else {
                            jsonAuxArr = (JSONArray) json.get(elementos.get(i).getNombre());
                            for (int j = 0; j < jsonAuxArr.length(); j++) {

                                if (jsonAuxArr.get(j).getClass().equals(JSONObject.class)) {
                                    getJSONArray((JSONObject) jsonAuxArr.get(j), elementos.get(i).getNombre());
                                } else {
                                   /* if (elementos.get(i).getPadre()!= null
                                            && elementos.get(i).getPadre().equalsIgnoreCase("conjunto Habitacional")
                                          //  && !elementos.get(i).getNombre().equalsIgnoreCase( padre )
                                            )
                                        AlertView.showAlert(elementos.get(i).getNombre(), MainActivity.this);*/
                                    elementos.get(i).getDatos().add(jsonAuxArr.getString(j));
                                }

                            }
                        }
                    }
                    if (json.has("Instrumentos")) {
                        globals.setInstrumentos( new Instrumentos());
                        JSONObject jsonAux = (JSONObject) json.get("Instrumentos");
                        globals.getInstrumentos().setIns_flexometro(jsonAux.getString("ins_flexometro"));
                        globals.getInstrumentos().setIns_termometro(jsonAux.getString("ins_termometro"));
                        globals.getInstrumentos().setIns_equipo_cono(jsonAux.getString("ins_equipo_cono"));
                        globals.getInstrumentos().setIns_motor_vibrador(jsonAux.getString("ins_motor_vibrador"));
                        globals.getInstrumentos().setIns_sonda(jsonAux.getString("ins_sonda"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        globals.setElementos(elementos);
    }

    @Override
    protected void onDestroy()  {
        try {
            bluetoothPort.disconnect();
        }
        catch ( IOException e ) {
            Log.e(TAG, e.getMessage(), e);
        }
        catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.disconnect) {
            final Calendar calendar = Calendar.getInstance();
            final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            final String fecha = Integer.toString(mDay);

            if ( Utilitys.deleteFile( "DHC_Login" + fecha + ".txt" ) ) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
            return true;
        } else if (id == R.id.talonarios) {
            startActivity(new Intent(MainActivity.this, TalonariosActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_samples_list) {
            startActivity( new Intent( MainActivity.this, ListaMuestrasActivity.class ) );
        } else if (id == R.id.nav_empty_form) {
            startActivity( new Intent( MainActivity.this, FormularioActivity.class ) );
        } else if (id == R.id.nav_print) {
            startActivity( new Intent( MainActivity.this, PrinterActivity.class ) );
            //  } else if (id == R.id.nav_camera) {
            //      startActivity( new Intent( Main.this, CameraActivity.class ) );
        } else if (id == R.id.nav_location) {
            startActivity( new Intent( MainActivity.this, MapsActivity.class ) );
            //      } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {

            final Calendar calendar = Calendar.getInstance();
            final int mMonth = calendar.get(Calendar.MONTH);
            final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
            final String fecha =Integer.toString(mDay).concat(Integer.toString(mMonth+1));
            //Toast.makeText(getApplication().getApplicationContext(), fecha, Toast.LENGTH_LONG).show();
            if ( Utilitys.deleteFile( "/Login/DHC_Login" + fecha + ".txt" ) ) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void turnIntentService() {
        Intent intent = new Intent(getApplicationContext(), Conectivity.class);
        intent.setAction(Globals.ACTION_RUN_ISERVICE);
        getApplicationContext().startService(intent);
    }

    private void createFile( String arr ) {

        File sdCard, directory;
        FileWriter file;

        try {
            if (Environment.getExternalStorageState().equals("mounted")) {

                sdCard = Environment.getExternalStorageDirectory();
                final Globals globals = (Globals)getApplicationContext();
                globals.setContadorLineaSelect(arr.length());

                try {
                    directory = new File(sdCard.getAbsolutePath()
                            + "/Archivos Formulario DHC");
                    directory.mkdirs();

                    file = new FileWriter(directory + "/DHC_Selects.txt", false);
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
}
