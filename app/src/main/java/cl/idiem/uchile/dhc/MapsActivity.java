package cl.idiem.uchile.dhc;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 *
 * Clase encargada de generar y administrar lo que sucede en la vista "activity_maps".
 * <p/>
 * Se genera mapa de Google Maps, donde se muestra la posición de cada una de las muestras
 * que se obtienen desde la API, a través de la actividad {@link cl.idiem.uchile.dhc.ListaMuestrasActivity ListaMuestras}
 *
 * @author José Becerra
 * @version 08/04/2016/A
 *
 */

public class MapsActivity extends AppCompatActivity {

    // Atributos de la clase

    /**
     * Podría ser null si Google Play services no está disponible
     */
    private GoogleMap mMap;

    /**
     * CameraUpdate nos permite el movimiento de la camara
     */
    private CameraUpdate mCamera;

    /**
     *
     * Método que es llamado cuando se crea por primera vez la actividad.
     * <p/>
     * Aquí es donde se maneja el negocio de la actividad y genera las instancias necesarias
     * para mostrar la información correcta dentro del mapa.
     *
     * @param savedInstanceState Paquete que contiene el estado previamente congelado de la actividad, si la hubo.
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Globals globals = (Globals) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

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
     *
     * Método que llama al layout del menú a mostrar en esquina superior derecha de la vista
     * "activity_maps".
     *
     * @param menu El parámetro menu es el objeto que contiene el menú a mostrar
     * @return Si se muestra o no el menú
     *
     */

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    /**
     *
     * Método que identifica cuál es la opción del menú que fue seleccionada.
     *
     * @param item El parámetro item es el objeto del ítem que fue seleccionado
     * @return Si fue seleccionado un ítem válido o no.
     *
     */

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

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
     *
     * Método que recarga las configuraciones del mapa, una vez que se retoma la actividad.
     * Es invocado de manera automática.
     *
     */

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
     * Se configura el mapa si es posible hacerlo (es decir, los servicios de Google Play APK
     * están instalado correctamente) y el mapa ya no ha creado una instancia. Esto asegurará que
     * sólo llamemos una vez a {@link #setUpMap()}, una vez que {@link #mMap} deje de ser nulo.
     * <p/>
     * Si no se instala {@link SupportMapFragment} (y
     * {@link com.google.android.gms.maps.MapView MapView}) mostrará un mensaje para que el usuario
     * instale/actualice servicios de Google Play APK en su dispositivo.
     * <p/>
     * El usuario puede volver a esta Actividad después de seguir la pronta y correcta
     * instalación/actualización/permisividad de los servicios de Google Play. Ya que la Actividad
     * puede no haber sido completamente destruida durante este proceso (lo más probable es que
     * sólo se detenga o pause), {@link #onCreate(Bundle)} puede no ser llamado de nuevo,
     * por lo que deberíamos llamar a este método en {@link #onResume()} para garantizar que se
     * va a llamar.
     *
     */

  /*  private void setUpMapIfNeeded() {

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}

                mMap.setMyLocationEnabled(true);
                // Habilitamos nuestra localización en el mapa
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // Inicializamos la app con el mapa hibrido
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
     *
     * Aquí es donde podemos añadir marcadores o líneas, añadir detectores o mover la cámara.
     * En este caso, simplemente seteamos la cámara cerca de las instalaciones de Idiem, en Arturo Prat.
     * <P/>
     * Este métodos sólo se debe llamar una vez y cuando estamos seguros de que {@link #mMap} no es nulo.
     *
     */

    private void setUpMap() {
        mCamera = CameraUpdateFactory.newLatLngZoom(new LatLng(-33.4601764, -70.647816), 14);
        mMap.animateCamera(mCamera);
    }

    /**
     * Método que añade al mapa tantos Markers como se desee, sólo hay que declararlos en el
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

}