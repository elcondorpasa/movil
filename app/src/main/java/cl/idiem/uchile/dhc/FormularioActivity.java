package cl.idiem.uchile.dhc;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import cl.idiem.uchile.dhc.Assist.AlertView;
import cl.idiem.uchile.dhc.Assist.DecimalDigitsInputFilter;
import cl.idiem.uchile.dhc.Assist.PrinterZebraIMZ320;
import cl.idiem.uchile.dhc.Assist.Utilitys;
import cl.idiem.uchile.dhc.Clases.API.Elemento;
import cl.idiem.uchile.dhc.Clases.API.MuestraListado;
import cl.idiem.uchile.dhc.Clases.Formulario.Talonario;
import cl.idiem.uchile.dhc.Services.UploadService;
import cl.idiem.uchile.dhc.utils.PhotoHolder;
import cl.idiem.uchile.dhc.utils.UserPreferences;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sewoo.port.android.BluetoothPort;
import com.sewoo.request.android.RequestHandler;
import org.json.JSONException;
import org.json.JSONObject;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static cl.idiem.uchile.dhc.utils.ImageUtils.dispatchTakePictureIntent;


/**
 * Clase que administra la vista "activity_formulario"
 * <p/>
 * Es la encargada del negocio principal de la aplicación. Recibe, valida y envía la información
 * de las muestras tomadas al servidor.
 */

public class FormularioActivity extends AppCompatActivity {

    /**
     * El {@link android.support.v4.view.PagerAdapter} proporcionará fragmentos para cada una
     * de las secciones.
     * <p/>
     * Utilizamos un derivado de {@link FragmentPagerAdapter}, que mantendrá cada fragmento cargado
     * en la memoria. Si esto llega a ser demasiado intensivo para la memoria, puede que sea mejor
     * cambiar a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * El {@link ViewPager} es quien alojará el contenido de la sección.
     */
    private ViewPager mViewPager;

    private static ArrayAdapter<String> FillSpinner(Context context, ArrayList<String> data) {
        ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(
                context,
                R.layout.spinner_item,
                data
        );



        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    /**
     * Método que es llamado cuando se crea por primera vez la actividad.
     * <p/>
     * Aquí es donde es cargada cada fragmento que compone el formulario.
     *
     * @param savedInstanceState Paquete que contiene el estado previamente congelado de la
     *                           actividad, si la hubo.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        final Globals globals = (Globals) getApplicationContext();
        globals.getAntecedentesProbeta().setEdades(new HashMap<String, String>());
        // final String addrPrinter = globals.getAddrPrinter();
        final boolean bViajePerdido = globals.getViajePerdido();
        /*final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (addrPrinter != null) {
            try {
                btConn(mBluetoothAdapter.getRemoteDevice(addrPrinter));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    /**
     * Método invocado de manera automática, una vez que se finaliza la actividad.
     */

    @Override
    public void onDestroy() {
        final Globals globals = (Globals) getApplicationContext();
        globals.setMuestraActiva(null);
        this.finish();
        super.onDestroy();
    }

    /**
     * Método que llama al layout del menú a mostrar en esquina superior derecha de la vista
     * "activity_camera".
     *
     * @param menu El parámetro menu es el objeto que contiene el menú a mostrar
     * @return Si se muestra o no el menú
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_formulario, menu);
        return true;
    }

    /**
     * Método que identifica cuál es la opción del menú que fue seleccionada.
     *
     * @param item El parámetro item es el objeto del ítem que fue seleccionado
     * @return Si fue seleccionado un ítem válido o no.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.out) {
            final Globals globals = (Globals) getApplicationContext();
            globals.setMuestraActiva(null);
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void btConn(final BluetoothDevice btDev) throws IOException {
        new connTask().execute(btDev);
    }

    /**
     * Clase que contiene una vista simple (fragmento).
     */

    public static class PlaceholderFragment extends Fragment implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TextWatcher {

        public static final int PICTURE_REQUEST_CODE_FILE = 1000;
        public static final int PICTURE_REQUEST_CODE_CAMERA = 2000;

        /**
         * Representa el número de sección de este fragmento.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        /**
         * Cuenta de validación para envío de correo.
         */
        private static final String username = "ticidiem@gmail.com";
        /**
         * Contraseña de cuenta {@link #username}
         */
        private static final String password = "idiem883";
        /**
         * Dirección a la se envía correos de confirmación de muestreo.
         */
        private static final String sendTo = "recepcion.fdigital@idiem.cl";
        /**
         * Administra la obtención de la localización actual.
         */
        public LocationManager mLocationManager;
        /**
         * Maneja si el viaje está perdido o no
         */
        private boolean bViajePerdido;
        /**
         * Adaptador Bluetooth.
         */
        private BluetoothPort bluetoothPort = BluetoothPort.getInstance();
        private AlertDialog.Builder builder;
        private PrinterZebraIMZ320 printer;
        private String addrPrinter;


        private PhotoHolder mPhotoHolder;
        private String mPicturePath;

        /**
         * Última localización válida.
         */
        private Location mLastLocation;
        /**
         * Campo de latitud del formulario.
         */
        private EditText lat;
        /**
         * Campo de longitud del formulario.
         */
        private EditText lon;
        private final LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                mLastLocation = location;

                lat.setText("Lat:" + String.valueOf(mLastLocation.getLatitude()));
                lon.setText("Lon:" + String.valueOf(mLastLocation.getLongitude()));
                try {
                    Globals globals = (Globals) getActivity().getApplicationContext();

                    globals.getMuestra().setLat(mLastLocation.getLatitude());
                    globals.getMuestra().setLon(mLastLocation.getLongitude());

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    mLocationManager.removeUpdates(mLocationListener);
                    mLocationManager = null;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                System.out.println("onStatusChanged");
            }

            @Override
            public void onProviderEnabled(String provider) {
                System.out.println("onProviderEnabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                System.out.println("onProviderDisabled");
                //turns off gps services
            }
        };
        private int contadorLinea;

        /**
         * Datos de plantas para select del formulario
         */
        private ArrayList<String> plantas = new ArrayList<String>();
        /**
         * Datos de elementos para select del formulario.
         */
        private ArrayList<String> elementos = new ArrayList<String>();
        /**
         * Datos de ubicaciones para select del formulario.
         */
        private ArrayList<String> ubicaciones = new ArrayList<String>();
        /**
         * Bandera que guarda si se deja formulario vacío o no, cuando se selecciona una muestra
         * en {@link cl.idiem.uchile.dhc.ListaMuestrasActivity}
         */
        private boolean empty = true;
        /**
         * Diálogo de carga. Se muestra cuando se solicita enviar información del formulario al
         * servidor.
         */
        private ProgressDialog progressDialog;
        /**
         * Indica si la instancia de la actividad se inició desde
         * {@link cl.idiem.uchile.dhc.ListaMuestrasActivity} o desde
         * {@link cl.idiem.uchile.dhc.MainActivity}
         */
        private boolean muestraCargada = false;


        /**
         * Constructor de la clase
         * {@link cl.idiem.uchile.dhc.FormularioActivity.PlaceholderFragment}
         */

        public PlaceholderFragment() {
        }

        /**
         * Devuelve una nueva instancia de
         * {@link cl.idiem.uchile.dhc.FormularioActivity.PlaceholderFragment}
         * para el número de la sección entregada.
         */

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

     /*  final Globals globals = (Globals) getActivity().getApplicationContext();

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == PICTURE_REQUEST_CODE_FILE && resultCode == Activity.RESULT_OK) {
                mPhotoHolder = new PhotoHolder(data.getData());
                //mPhotoHolder.showImg(getActivity(), mImgPhoto, PhotoHolder.SHOW_ORIGINAL);
                mPicturePath = mPhotoHolder.getOriginalPath();
            } else if(requestCode == PICTURE_REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
                if (mPicturePath == null) return;
                mPhotoHolder = new PhotoHolder(mPicturePath);
                //mPhotoHolder.showImg(getActivity(), mImgPhoto, PhotoHolder.SHOW_ORIGINAL);
                mPicturePath = mPhotoHolder.getOriginalPath();
                globals.getMuestra().setImage(Utilitys.encodeToBase64(mPhotoHolder.getOriginal(), Bitmap.CompressFormat.JPEG, 70));
                Toast.makeText(getContext(),"Imagen Guardada",Toast.LENGTH_SHORT).show();
            } else if(requestCode == PICTURE_REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_CANCELED){
                if(mPhotoHolder != null){
                    //mPhotoHolder.showImg(getActivity(), mImgPhoto, PhotoHolder.SHOW_ORIGINAL);
                    mPicturePath = mPhotoHolder.getOriginalPath();
                }
            }
        }*/

        private static String readLocal(String Adress, int lenght, Context context) {

            byte[] buffer = new byte[lenght];
            String str = new String();
            try {
                FileInputStream fis = new FileInputStream(new File(Adress));
                fis.read(buffer);
                fis.close();
                str = new String(buffer);
                Toast.makeText(context, str, Toast.LENGTH_LONG).show();
                Log.i("Ficheros", "Texto: " + str);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "No se encontraron datos", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Error al procesar los datos", Toast.LENGTH_LONG).show();
            }

            return str;

        }

        /**
         * Método que llena los campos necesarios de acuerdo a la información entregada,
         * desde el servidor, en cada muestra en {@link cl.idiem.uchile.dhc.ListaMuestrasActivity}
         */

        private void llenadoDatosMuestraActiva(int section, View rootView, MuestraListado muestra) {
            Globals globals = (Globals) getActivity().getApplicationContext();

            globals.getMuestra().setIdProg( muestra.getId() );
            if (section == 1) {

                EditText numInterno = (EditText) rootView.findViewById(R.id.numInterno);
                EditText codObra = (EditText) rootView.findViewById(R.id.codObra);
                EditText detalleObra = (EditText) rootView.findViewById(R.id.detalleObra);
                EditText direccion = (EditText) rootView.findViewById(R.id.direccion);
                EditText comuna = (EditText) rootView.findViewById(R.id.comuna);
                EditText contratista = (EditText) rootView.findViewById(R.id.contratista);
                EditText latitud = (EditText) rootView.findViewById(R.id.lat);
                EditText longitud = (EditText) rootView.findViewById(R.id.lon);
                EditText solicitante = (EditText) rootView.findViewById(R.id.solicitante);
                numInterno.setText(String.valueOf(muestra.getNumInterno()));
                numInterno.setKeyListener(null);
                codObra.setText(String.valueOf(muestra.getCodigoObra()));
                codObra.setKeyListener(null);
                comuna.setText(muestra.getComuna());
                comuna.setKeyListener(null);
                detalleObra.setText(muestra.getNombreObra());
               // detalleObra.setKeyListener(null);
                direccion.setText(muestra.getDireccionObra());
                direccion.setKeyListener(null);
                contratista.setText(muestra.getContratista());
                contratista.setKeyListener(null);
                latitud.setText(String.valueOf(muestra.getLatitud()));
                latitud.setKeyListener(null);
                longitud.setText(String.valueOf(muestra.getLongitud()));
                longitud.setKeyListener(null);
                solicitante.setText(String.valueOf(muestra.getSolicitante()));
                solicitante.setKeyListener(null);

            } else if (section == 2) {

                EditText gradoTipo = (EditText) rootView.findViewById(R.id.gradoTipo);
                gradoTipo.setText(String.valueOf(muestra.getGradoTipo()));

            } else if (section == 4) {
                Spinner asentamiento = (Spinner) rootView.findViewById(R.id.asentamiento);
                asentamiento.setSelection(globals.getAsentamientos().indexOf(muestra.getTipoAsentamiento()));
                asentamiento.setSelected(false);

            } else if (section == 5) {
                Spinner tipoProbeta = (Spinner) rootView.findViewById(R.id.probEmpleada);
                tipoProbeta.setSelection(globals.getProbEmpleadas().indexOf(muestra.getTipoProbeta()));
                tipoProbeta.setSelected(false);
            }

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView;
            final int section = getArguments().getInt(ARG_SECTION_NUMBER);
            final Globals globals = (Globals) getActivity().getApplicationContext();
            final MuestraListado muestra = globals.getMuestraActiva();
            final ArrayList<Elemento> elements = globals.getElementos();
            final Calendar calendar = Calendar.getInstance();




            bViajePerdido = globals.getViajePerdido();

            /** VIAJE PERDIDO  **/

            if (section == 1) {
                final Talonario talonario = globals.getTalonarios().getCurrentTalonario();
                //<editor-fold desc="Rutina Viaje Perdido">
                rootView = inflater.inflate(R.layout.fragment_form1, container, false);
                int LOCATION_REFRESH_TIME = 1000;
                int LOCATION_REFRESH_DISTANCE = 5;

                mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }


                /** EDIT TEXT VISTA **/
                //<editor-fold desc="EDIT TEXT VISTA">
                Switch viajePerdido = (Switch) rootView.findViewById(R.id.viajePerdido);
                final EditText numInterno = (EditText) rootView.findViewById(R.id.numInterno);
                final EditText numMuestra = (EditText) rootView.findViewById(R.id.numMuestra);
                final EditText codObra = (EditText) rootView.findViewById(R.id.codObra);
                final EditText detalleObra = (EditText) rootView.findViewById(R.id.detalleObra);
                final EditText direccion = (EditText) rootView.findViewById(R.id.direccion);
                final EditText comuna = (EditText) rootView.findViewById(R.id.comuna);
                final EditText contratista = (EditText) rootView.findViewById(R.id.contratista);
                final EditText solicitante = (EditText) rootView.findViewById(R.id.solicitante);
                final EditText horaIn = (EditText) rootView.findViewById(R.id.horaIn);
                final LinearLayout boxEnviarPerdido = (LinearLayout) rootView.findViewById(R.id.boxEnviarPerdido);
                ImageView sendServer = (ImageView) rootView.findViewById(R.id.sendServer);
                //ImageView saveFile = (ImageView) rootView.findViewById(R.id.saveFile);
                ImageView firmar = (ImageView) rootView.findViewById(R.id.firmar);
                final LinearLayout ubicacion = (LinearLayout) rootView.findViewById(R.id.ubicacion);
                lat = (EditText) rootView.findViewById(R.id.lat);
                lon = (EditText) rootView.findViewById(R.id.lon);
                //</editor-fold>
                /** FIN **/

                if (muestra == null) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                            LOCATION_REFRESH_DISTANCE, mLocationListener);
                } else {
                    if (!muestraCargada) {
                        muestraCargada = true;
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Utilizar Datos de la Muestra")
                                .setMessage("Desea utilizar los datos que vienen con la muestra?")
                                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        llenadoDatosMuestraActiva(section, rootView, muestra);
                                        empty = false;
                                    }

                                })
                                .setNegativeButton("No", null)
                                .show();
                        ubicacion.setVisibility(View.GONE);
                    }
                }
                if (talonario != null) {
                    numMuestra.setText(String.valueOf(talonario.getNext()));
                    globals.getMuestra().setNumMuestra(talonario.getNext());
                    //UserPreferences.saveNumeroMuestra(talonario.getNext(), Globals.getContext());

                    numMuestra.setKeyListener(null);
                }
                /** SE GUARDA INFORMACIÓN DE INPUTS **/
                //<editor-fold desc="Guarda Info">
                numInterno.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                        try {
                            if (numInterno.getText().toString().equals("")) return;
                            globals.getMuestra().setNumInterno(Integer.parseInt(numInterno.getText().toString()));
                        } catch (NumberFormatException e) {
                            numInterno.setText("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }

                });

                numMuestra.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            if (numMuestra.getText().toString().equals("")) return;
                            globals.getMuestra().setNumMuestra(Integer.parseInt(numMuestra.getText().toString()));

                        } catch (NumberFormatException e) {
                            numMuestra.setText("");
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                codObra.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        try {
                            if (codObra.getText().toString().equals("")) return;
                            globals.getMuestra().setCodObra(Integer.parseInt(codObra.getText().toString()));

                        } catch (NumberFormatException e) {
                            codObra.setText("");
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                detalleObra.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        globals.getMuestra().setDetalleObra(detalleObra.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                direccion.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        globals.getMuestra().setDireccion(direccion.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                comuna.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        globals.getMuestra().setComuna(comuna.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                contratista.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        globals.getMuestra().setContratista(contratista.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                solicitante.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        globals.getMuestra().setSolicitante(solicitante.getText().toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                String time = (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute);
                horaIn.setText(time);
                globals.getMuestra().setHoraIn(time);

                viajePerdido.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {

                        if (isChecked) {
                            boxEnviarPerdido.setVisibility(View.VISIBLE);
                            globals.getMuestra().setViajePerdido(true);
                            globals.setViajePerdido(true);
                            bViajePerdido = true;

                        } else {

                            boxEnviarPerdido.setVisibility(View.GONE);
                            globals.getMuestra().setViajePerdido(false);
                            globals.setViajePerdido(false);
                            bViajePerdido = false;
                        }

                    }
                });
                //</editor-fold>
                /** FINALIZA GUARDADO **/

                sendServer.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (globals.getMuestra().getFirma() != null && !globals.getMuestra().getFirma().equals("")) {
                        // sendMail(sendTo, "Datos Muestreo - Viaje Perdido (correo de prueba)", globals.getMuestra().toString());
                        String muestra = String.valueOf(globals.getMuestra().getNumMuestra());
                        JSONObject json = setJSONCompleto(bViajePerdido);
                        try {
                            createFile(json.toString(4), muestra, false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            String horaTermino = (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute);
                            globals.getMuestra().setHoraTermino(horaTermino);
                        sendPost(bViajePerdido, globals.getMuestra().getNumMuestra());

                        } else
                            Toast.makeText(getContext(), "Debe realizar firma antes de realizar esta acción", Toast.LENGTH_LONG).show();

                    }

                });
                firmar.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        startActivity(new Intent(getActivity(), CaptureSignature.class));

                    }
                });

              /*  saveFile.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String numMuestra = String.valueOf(globals.getMuestra().getNumMuestra());
                        JSONObject json = setJSONCompleto(bViajePerdido);
                        try {
                            createFile(json.toString(4), numMuestra, true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //  readLocal("storage/emulated/0/Archivos Boleta DHC/DHC_"+numMuestra+".txt",contadorLinea,getActivity().getApplicationContext());


                    }
                });*/
                //</editor-fold>

            }

            /** ANTECEDENTES HORMIGON MUESTREO **/

            else if (section == 2) {


                //<editor-fold desc="Rutina Antecedentes Hormigón Muestreo">
                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);
                else {
                    rootView = inflater.inflate(R.layout.fragment_form2, container, false);
                    /** EDIT TEXT VISTA **/
                    //<editor-fold desc="EDIT TEXT VISTA">
                    final Spinner confeccionado = (Spinner) rootView.findViewById(R.id.confeccionado);
                    final EditText numCamion = (EditText) rootView.findViewById(R.id.numCamion);
                    final EditText numGuia = (EditText) rootView.findViewById(R.id.numGuia);
                    final EditText cantiM3 = (EditText) rootView.findViewById(R.id.cantiM3);
                    final Spinner planta = (Spinner) rootView.findViewById(R.id.planta);
                    final EditText codProducto = (EditText) rootView.findViewById(R.id.codProducto);
                    final EditText gradoTipo = (EditText) rootView.findViewById(R.id.gradoTipo);
                    final EditText plantaOut = (EditText) rootView.findViewById(R.id.plantaOut);
                    final Spinner aditivo = (Spinner) rootView.findViewById(R.id.aditivo);
                    final LinearLayout layoutOtroAditivo = (LinearLayout) rootView.findViewById(R.id.layoutOtroAditivo);
                    final EditText otroAditivo = (EditText) rootView.findViewById(R.id.otroAditivo);
                    final LinearLayout layoutAditivo2 = (LinearLayout) rootView.findViewById(R.id.layoutAditivo2);
                    final Spinner aditivo2 = (Spinner) rootView.findViewById(R.id.aditivo2);
                    final LinearLayout layoutOtroAditivo2 = (LinearLayout) rootView.findViewById(R.id.layoutOtroAditivo2);
                    final EditText otroAditivo2 = (EditText) rootView.findViewById(R.id.otroAditivo2);
                    final EditText cantidadAditivo = (EditText) rootView.findViewById(R.id.cantidadAditivo);
                    final Spinner medidaAditivo = (Spinner) rootView.findViewById(R.id.medidaAditivo);
                    final Spinner adicion = (Spinner) rootView.findViewById(R.id.adicion);
                    final LinearLayout layoutAdicionAgua = (LinearLayout) rootView.findViewById(R.id.layoutAdicionAgua);
                    final EditText cantidadAgua = (EditText) rootView.findViewById(R.id.cantidadAgua);
                    final LinearLayout layoutMedidaAgua = (LinearLayout) rootView.findViewById(R.id.layoutMedidaAgua);
                    final Spinner medidaAgua = (Spinner) rootView.findViewById(R.id.medidaAgua);
                    final LinearLayout layoutOtroAdicion = (LinearLayout) rootView.findViewById(R.id.layoutOtroAdicion);
                    final EditText otroAdicion = (EditText) rootView.findViewById(R.id.otroAdicion);
                    final TextView addAditivo = (TextView) rootView.findViewById(R.id.addAditivo);
                    final Spinner compactObra = (Spinner) rootView.findViewById(R.id.compactObra);
                    final LinearLayout layoutOtroCompactObra = (LinearLayout) rootView.findViewById(R.id.layoutOtroCompactObra);
                    final EditText otroCompactObra = (EditText) rootView.findViewById(R.id.otroCompactObra);
                    //</editor-fold>
                    /** FIN **/

                    /** LLENADO SELECTS **/
                    //<editor-fold desc="Rutina Llenado">

                    /** Confeccionado**/
                    confeccionado.setAdapter(FillSpinner(getActivity(), globals.getConfeccionados()));

                    /** Aditivo**/
                    aditivo.setAdapter(FillSpinner(getActivity(), globals.getAditivos()));
                    aditivo2.setAdapter(FillSpinner(getActivity(), globals.getAditivos()));

                    /** Medidas Aditivo**/
                    medidaAditivo.setAdapter(FillSpinner(getActivity(), globals.getMedidasAditivo()));
                    medidaAgua.setAdapter(FillSpinner(getActivity(), globals.getMedidasAditivo()));

                    /** Adicion**/
                    adicion.setAdapter(FillSpinner(getActivity(), globals.getAdiciones()));

                    /** Compact Obra**/
                    compactObra.setAdapter(FillSpinner(getActivity(), globals.getCompactsObra()));

                    //</editor-fold>
                    /** FIN LLENADO SELECTS **/
                    if (muestra != null)
                        llenadoDatosMuestraActiva(section, rootView, muestra);



                        /** SE GUARDA INFORMACIÓN DE INPUTS **/
                        //<editor-fold desc="Guarda Info">
                        confeccionado.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setConfeccionado(parentView.getItemAtPosition(position).toString());

                                        int j;
                                        ArrayList<String> datos;
                                        plantas.clear();

                                        for (int i = 0; i < elements.size(); i++) {

                                            datos = elements.get(i).getDatos();

                                            if (elements.get(i).getNombre().equalsIgnoreCase(parentView.getItemAtPosition(position).toString())) {

                                                for (j = 0; j < datos.size(); j++) {
                                                    plantas.add(datos.get(j));
                                                }

                                                planta.setAdapter(FillSpinner(getActivity(), plantas));
                                            }

                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );

                        numCamion.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (numCamion.getText().toString().length() < 7) {
                                    globals.getAntecedentesHormigonMuestreo().setNumCamion(numCamion.getText().toString());
                                } else
                                    numCamion.setText("");
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        numGuia.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (numGuia.getText().toString().equals("")) return;
                                    globals.getAntecedentesHormigonMuestreo().setNumGuia(Integer.parseInt(numGuia.getText().toString()));

                                } catch (NumberFormatException e) {
                                    numGuia.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        cantiM3.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
                        cantiM3.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (cantiM3.getText().toString().equals("")) return;
                                    if (Double.parseDouble(cantiM3.getText().toString()) > 10 || Double.parseDouble(cantiM3.getText().toString()) < 0) {
                                        Toast.makeText(getContext(), "Carga fuera de límites", Toast.LENGTH_SHORT).show();
                                        cantiM3.setText("");
                                    } else {
                                        String[] cant = cantiM3.getText().toString().split("\\.");

                                        if (cant.length > 1) {
                                            if (cant[1].equals("0") || cant[1].equals("5"))
                                                globals.getAntecedentesHormigonMuestreo().setCantiM3(Double.parseDouble(cantiM3.getText().toString()));
                                            else {
                                                globals.getAntecedentesHormigonMuestreo().setCantiM3(Double.parseDouble(cant[0]));
                                                cantiM3.setText(cant[0] + ".0");
                                            }
                                        } else
                                            globals.getAntecedentesHormigonMuestreo().setCantiM3(Double.parseDouble(cantiM3.getText().toString()));
                                    }
                                } catch (NumberFormatException e) {
                                    cantiM3.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        planta.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setPlanta(parentView.getItemAtPosition(position).toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );

                        codProducto.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (codProducto.getText().toString().length() < 11)
                                    globals.getAntecedentesHormigonMuestreo().setCodProducto(codProducto.getText().toString());
                                else
                                    codProducto.setText("");
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        gradoTipo.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesHormigonMuestreo().setGradoTipo(gradoTipo.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        plantaOut.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                int minute = calendar.get(Calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String time = (selectedHour > 9 ? selectedHour : "0" + selectedHour)
                                                + ":" +
                                                (selectedMinute > 9 ? selectedMinute : "0" + selectedMinute);
                                        //validar hora
                                        plantaOut.setText(time);
                                        globals.getAntecedentesHormigonMuestreo().setPlantaOut(time);
                                    }
                                }, hour, minute, true);
                                mTimePicker.setTitle("Seleccione Hora");
                                mTimePicker.show();

                            }
                        });

                        aditivo.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setAditivo(parentView.getItemAtPosition(position).toString());
                                        otroAditivo.setText("");

                                        if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otro"))
                                            layoutOtroAditivo.setVisibility(View.VISIBLE);
                                        else
                                            layoutOtroAditivo.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );

                        addAditivo.setOnClickListener(new AdapterView.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (layoutAditivo2.getVisibility() == View.GONE)
                                    layoutAditivo2.setVisibility(View.VISIBLE);
                                else
                                    layoutAditivo2.setVisibility(View.GONE);
                            }
                        });

                        otroAditivo.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesHormigonMuestreo().setOtroAditivo(otroAditivo.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        aditivo2.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setAditivo2(parentView.getItemAtPosition(position).toString());
                                        otroAditivo2.setText("");

                                        if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otro"))
                                            layoutOtroAditivo2.setVisibility(View.VISIBLE);
                                        else
                                            layoutOtroAditivo2.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );

                        otroAditivo2.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesHormigonMuestreo().setOtroAditivo2(otroAditivo2.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        cantidadAditivo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
                        cantidadAditivo.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (cantidadAditivo.getText().toString().equals("")) return;
                                    double cant = Double.parseDouble(cantidadAditivo.getText().toString());
                                    if (cant < 20.1)
                                        globals.getAntecedentesHormigonMuestreo().setCantidadAditivo(cant);
                                    else
                                        cantidadAditivo.setText("");
                                } catch (NumberFormatException e) {
                                    cantidadAditivo.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        medidaAditivo.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setMedidaAditivo(parentView.getItemAtPosition(position).toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        adicion.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setAdicion(parentView.getItemAtPosition(position).toString());
                                        otroAdicion.setText("");
                                        cantidadAgua.setText("");

                                        if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otro"))
                                            layoutOtroAdicion.setVisibility(View.VISIBLE);
                                        else if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Agua")) {
                                            layoutAdicionAgua.setVisibility(View.VISIBLE);
                                            layoutMedidaAgua.setVisibility(View.VISIBLE);
                                            layoutOtroAdicion.setVisibility(View.GONE);
                                        } else {
                                            layoutAdicionAgua.setVisibility(View.GONE);
                                            layoutMedidaAgua.setVisibility(View.GONE);
                                            layoutOtroAdicion.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );

                        cantidadAgua.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (cantidadAgua.getText().toString().equals("")) return;
                                    int cant = Integer.parseInt(cantidadAgua.getText().toString());
                                    if (cant < 21)
                                        globals.getAntecedentesHormigonMuestreo().setCantidadAgua(cant);
                                    else
                                        cantidadAgua.setText("");
                                } catch (NumberFormatException e) {
                                    cantidadAgua.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        medidaAgua.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setMedidaAgua(parentView.getItemAtPosition(position).toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );

                        otroAdicion.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesHormigonMuestreo().setOtroAdicion(otroAdicion.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        compactObra.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesHormigonMuestreo().setCompactObra(parentView.getItemAtPosition(position).toString());
                                        otroCompactObra.setText("");

                                        if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otra"))
                                            layoutOtroCompactObra.setVisibility(View.VISIBLE);
                                        else
                                            layoutOtroCompactObra.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        otroCompactObra.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesHormigonMuestreo().setOtroCompactObra(otroCompactObra.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        //</editor-fold>
                        /** FINALIZA GIARDADO **/
                    }
                    //</editor-fold>

                }

            /** COLOCADO EN  **/

            else if (section == 3) {

                //<editor-fold desc="Rutina Colocado En">

                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);

                else {
                    rootView = inflater.inflate(R.layout.fragment_form3, container, false);

                    /** EDIT TEXT VISTA **/
                    //<editor-fold desc="EDIT TEXT VISTA">
                    final Spinner tipoEdif = (Spinner) rootView.findViewById(R.id.tipoEdif);
                    final Spinner elemento = (Spinner) rootView.findViewById(R.id.elemento);
                    final Spinner ubicacion = (Spinner) rootView.findViewById(R.id.ubicacion);
                    final EditText eje = (EditText) rootView.findViewById(R.id.eje);
                    final EditText otroEdif = (EditText) rootView.findViewById(R.id.otroEdif);
                    final LinearLayout layoutOtroEdif = (LinearLayout) rootView.findViewById(R.id.layoutOtroEdif);
                    final LinearLayout layoutElemento = (LinearLayout) rootView.findViewById(R.id.layoutElemento);
                    final LinearLayout layoutUbicacion = (LinearLayout) rootView.findViewById(R.id.layoutUbicacion);
                    final LinearLayout layoutEje = (LinearLayout) rootView.findViewById(R.id.layoutEje);



                    //</editor-fold>
                    /** FIN **/

                    /** DATOS DE SELECTS **/
                    //<editor-fold desc="Selects">
                    //crear json
                    final String jsonEjes = "{" +
                            "'':''," +
                            "'Losa Cielo':'Eje A y B entre 1 y 2'," +
                            "'Muro':'Eje A entre ejes 1 y 2'," +
                            "'Dintel':'Eje A entre ejes 1 y 2'," +
                            "'Viga':'Eje A entre ejes 1 y 2'," +
                            "'Columna':'Eje 1-A'" +
                            "}";


                    //</editor-fold>
                    /** FIN DATOS SELECTS **/

                    /** LLENADO SELECTS **/
                    //<editor-fold desc="Rutina Llenado">
                    tipoEdif.setAdapter(FillSpinner(getActivity(), globals.getTiposEdif()));
                    //</editor-fold>
                    /** FIN LLENADO SELECTS **/

                    /** SE GUARDA INFORMACIÓN DE INPUTS **/
                    //<editor-fold desc="Guarda Info">
                    tipoEdif.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {

                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                           int position, long id) {

                                    globals.getColocadoEn().setTipoEdificio(parentView.getItemAtPosition(position).toString());
                                    elementos.clear();
                                    ubicaciones.clear();
                                    otroEdif.setText("");

                                    if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otro")) {

                                        layoutOtroEdif.setVisibility(View.VISIBLE);
                                        layoutElemento.setVisibility(View.GONE);
                                        layoutUbicacion.setVisibility(View.GONE);
                                        layoutEje.setVisibility(View.GONE);

                                    } else{
                                        layoutOtroEdif.setVisibility(View.GONE);
                                        layoutElemento.setVisibility(View.VISIBLE);
                                        layoutUbicacion.setVisibility(View.VISIBLE);
                                        layoutEje.setVisibility(View.VISIBLE);
                                }
                                    int j;
                                    ArrayList<String> datos;

                                    for (int i = 0; i < elements.size(); i++) {

                                        datos = elements.get(i).getDatos();

                                        if (elements.get(i).getNombre().equalsIgnoreCase("elemento") && elements.get(i).getPadre().equalsIgnoreCase(parentView.getItemAtPosition(position).toString())) {
                                            for (j = 0; j < datos.size(); j++) {
                                                elementos.add(datos.get(j));
                                            }

                                            elemento.setAdapter(FillSpinner(getActivity(), elementos));
                                        }
                                        if (elements.get(i).getNombre().equalsIgnoreCase("ubicacion") && elements.get(i).getPadre().equalsIgnoreCase(parentView.getItemAtPosition(position).toString())) {

                                            for (j = 0; j < datos.size(); j++) {
                                                ubicaciones.add(datos.get(j));
                                            }

                                            ubicacion.setAdapter(FillSpinner(getActivity(), ubicaciones));
                                        }

                                    }

                                    eje.setText("");
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            }
                    );
                    elemento.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {

                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                           int position, long id) {
                                    globals.getColocadoEn().setElemento(parentView.getItemAtPosition(position).toString());

                                    JSONObject jsonAux;

                                    //<editor-fold desc="Proceso Eje">
                                    try {
                                        jsonAux = new JSONObject(jsonEjes);
                                    } catch (JSONException e) {
                                        jsonAux = new JSONObject();
                                        e.printStackTrace();
                                    }

                                    try {
                                        eje.setText(jsonAux.getString(parentView.getItemAtPosition(position).toString()));
                                    } catch (JSONException e) {
                                        eje.setText("");
                                        e.printStackTrace();
                                    }
                                    //</editor-fold>
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            }
                    );
                    ubicacion.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {

                                public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                           int position, long id) {
                                    globals.getColocadoEn().setUbicacion(parentView.getItemAtPosition(position).toString());
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            }
                    );
                    eje.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getColocadoEn().setEje(eje.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    otroEdif.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getColocadoEn().setTipoEdificio(otroEdif.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    //</editor-fold>
                    /** FINALIZA GIARDADO **/
                }

                //</editor-fold>

            }

            /** ANTECEDENTES DEL MUESTREO  **/

            else if (section == 4) {

                //<editor-fold desc="Rutina Antecedentes del Muestreo">
                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);
                else {
                    rootView = inflater.inflate(R.layout.fragment_form4, container, false);
                    /** EDIT TEXT VISTA **/
                    //<editor-fold desc="EDIT TEXT VISTA">
                    final EditText dateMuestreo = (EditText) rootView.findViewById(R.id.dateMuestreo);
                    //dateMuestreo.setEnabled(true);
                    Switch cono = (Switch) rootView.findViewById(R.id.aptoCono);
                    final LinearLayout boxCono = (LinearLayout) rootView.findViewById(R.id.boxCono);
                    final EditText timeMuestreo = (EditText) rootView.findViewById(R.id.timeMuestreo);
                    final EditText timeCamion = (EditText) rootView.findViewById(R.id.timeCamion);
                    final EditText tempAmb = (EditText) rootView.findViewById(R.id.tempAmb);
                    final EditText tempMezcla = (EditText) rootView.findViewById(R.id.tempMezcla);
                    final Spinner asentamiento = (Spinner) rootView.findViewById(R.id.asentamiento);
                    final Spinner compactacion = (Spinner) rootView.findViewById(R.id.compactacion);
                    final Spinner condClima = (Spinner) rootView.findViewById(R.id.condClima);
                    final EditText extraeA = (EditText) rootView.findViewById(R.id.extraeA);
                    final EditText mixero = (EditText) rootView.findViewById(R.id.mixero);
                    final EditText valCono = (EditText) rootView.findViewById(R.id.valCono);
                    final EditText medCono1 = (EditText) rootView.findViewById(R.id.medCono1);
                    final EditText medCono2 = (EditText) rootView.findViewById(R.id.medCono2);
                    final EditText horaDescarga = (EditText) rootView.findViewById(R.id.horaDescarga);
                    final LinearLayout layoutMedCono2 = (LinearLayout) rootView.findViewById(R.id.layoutMedCono2);
                    final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                    //</editor-fold>
                    /** FIN **/

                    /** LLENADO SELECTS **/
                    //<editor-fold desc="Rutina Llenado">

                    /** Asentamiento**/
                    asentamiento.setAdapter(FillSpinner(getActivity(), globals.getAsentamientos()));

                    /** Compactación Muestra**/
                    compactacion.setAdapter(FillSpinner(getActivity(), globals.getCompactaciones()));

                    /** Condición CLima**/
                    condClima.setAdapter(FillSpinner(getActivity(), globals.getCondsClima()));


                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    String formattedDate = df.format(calendar.getTime());
                    dateMuestreo.setText(formattedDate);
                    globals.getAntecedentesMuestreo().setDateMuestreo(formattedDate);
                    //</editor-fold>
                    /** FIN LLENADO SELECTS **/

                    if (muestra != null)
                        llenadoDatosMuestraActiva(section, rootView, muestra);


                        /** SE GUARDA INFORMACIÓN DE INPUTS **/
                        //<editor-fold desc="Guarda Info">

                        timeMuestreo.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                int hour = calendar.get(calendar.HOUR_OF_DAY);
                                int minute = calendar.get(calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String time = (selectedHour > 9 ? selectedHour : "0" + selectedHour)
                                                + ":" +
                                                (selectedMinute > 9 ? selectedMinute : "0" + selectedMinute);

                                        Date horaInTime;
                                        Date horaMuestreoTime;
                                        try {
                                            if (globals.getMuestra().getHoraIn() == null) {
                                                Toast.makeText(getContext(), "No se ha definido hora de llegada", Toast.LENGTH_SHORT).show();
                                                timeMuestreo.setText("");
                                            } else {
                                                horaInTime = parser.parse(globals.getMuestra().getHoraIn());
                                                horaMuestreoTime = parser.parse(time);
                                                if (horaMuestreoTime.after(horaInTime)) {

                                                    timeMuestreo.setText(time);
                                                    globals.getAntecedentesMuestreo().setTimeMuestreo(time);
                                                } else {
                                                    Toast.makeText(getContext(), "Hora de Muestreo no puede ser inferior a la hora de llegada", Toast.LENGTH_SHORT).show();
                                                    timeMuestreo.setText("");
                                                }
                                            }
                                        } catch (ParseException e) {
                                            timeMuestreo.setText("");
                                        }
                                    }
                                }, hour, minute, true);
                                mTimePicker.setTitle("Seleccione Hora");
                                mTimePicker.show();

                            }
                        });
                        timeCamion.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                int hour = calendar.get(calendar.HOUR_OF_DAY);
                                int minute = calendar.get(calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String time = (selectedHour > 9 ? selectedHour : "0" + selectedHour)
                                                + ":" +
                                                (selectedMinute > 9 ? selectedMinute : "0" + selectedMinute);
                                        Date horaPlantaTime;
                                        Date horaMuestroTime;
                                        Date horaCamionTime;

                                        try {
                                            if (globals.getAntecedentesHormigonMuestreo().getPlantaOut() == null) {
                                                Toast.makeText(getContext(), "No se ha definido hora de salida planta", Toast.LENGTH_SHORT).show();
                                                timeCamion.setText("");
                                            } else {
                                                horaPlantaTime = parser.parse(globals.getAntecedentesHormigonMuestreo().getPlantaOut());

                                                horaCamionTime = parser.parse(time);
                                                if (horaCamionTime.after(horaPlantaTime)) {

                                                    timeCamion.setText(time);
                                                    globals.getAntecedentesMuestreo().setTimeCamion(time);
                                                } else {
                                                    Toast.makeText(getContext(), "Hora llegada camióm no puede ser inferior a la hora de salida planta", Toast.LENGTH_SHORT).show();
                                                    timeCamion.setText("");
                                                }
                                            }
                                        } catch (ParseException e) {
                                            timeCamion.setText("");
                                        }

                                        try {
                                            if (globals.getAntecedentesMuestreo().getTimeMuestreo() == null) {
                                                Toast.makeText(getContext(), "No se ha definido hora de muestreo", Toast.LENGTH_SHORT).show();
                                                timeCamion.setText("");
                                            } else {
                                                horaMuestroTime = parser.parse(globals.getAntecedentesMuestreo().getTimeMuestreo());

                                                horaCamionTime = parser.parse(time);
                                                if (horaCamionTime.before(horaMuestroTime)) {

                                                    timeCamion.setText(time);
                                                    globals.getAntecedentesMuestreo().setTimeCamion(time);
                                                } else {
                                                    Toast.makeText(getContext(), "Hora llegada camióm no puede ser mayor a la hora de toma muestra", Toast.LENGTH_SHORT).show();
                                                    timeCamion.setText("");
                                                }
                                            }
                                        } catch (ParseException e) {
                                            timeCamion.setText("");
                                        }

                                    }
                                }, hour, minute, true);
                                mTimePicker.setTitle("Seleccione Hora");
                                mTimePicker.show();

                            }
                        });
                        tempAmb.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
                        tempAmb.addTextChangedListener(new TextWatcher() {

                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (tempAmb.getText().toString().equals("") || tempAmb.getText().toString().equals("-"))
                                        return;
                                    if (Double.parseDouble(tempAmb.getText().toString()) > 50 || Double.parseDouble(tempAmb.getText().toString()) < -50) {
                                        Toast.makeText(getContext(), "Temperatura fuera de límites", Toast.LENGTH_SHORT).show();
                                        tempAmb.setText("");
                                    } else {
                                        globals.getAntecedentesMuestreo().setTempAmb(Double.parseDouble(tempAmb.getText().toString()));
                                    }
                                } catch (NumberFormatException e) {
                                    tempAmb.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });

                        tempMezcla.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (tempMezcla.getText().toString().equals("") || tempAmb.getText().toString().equals("-"))
                                        return;
                                    if (Double.parseDouble(tempMezcla.getText().toString()) > 50 || Double.parseDouble(tempMezcla.getText().toString()) < -50) {
                                        Toast.makeText(getContext(), "Temperatura fuera de límites", Toast.LENGTH_SHORT).show();
                                        tempMezcla.setText("");
                                    } else {
                                        globals.getAntecedentesMuestreo().setTempMezcla(Double.parseDouble(tempMezcla.getText().toString()));
                                    }
                                } catch (NumberFormatException e) {
                                    tempMezcla.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        asentamiento.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesMuestreo().setAsentamiento(parentView.getItemAtPosition(position).toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        valCono.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {

                                    if (valCono.getText().toString().equals("")) return;
                                    if (Integer.parseInt(valCono.getText().toString()) > 99 || Integer.parseInt(valCono.getText().toString()) < 1) {
                                        valCono.setText("");
                                        return;
                                    }

                                } catch (NumberFormatException e) {
                                    valCono.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        medCono1.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
                        medCono1.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    globals.getAntecedentesMuestreo().setControlCono(false);
                                    layoutMedCono2.setVisibility(View.GONE);
                                    medCono2.setText("");

                                    if (medCono1.getText().toString().equals("")) return;
                                    if (Double.parseDouble(medCono1.getText().toString()) > 99) {
                                        medCono1.setText("");
                                        return;
                                    }

                                    String[] cant = medCono1.getText().toString().split("\\.");
                                    String cantFinal;

                                    if (cant.length > 1) {

                                        if (cant[1].equals("0") || cant[1].equals("5"))
                                            cantFinal = medCono1.getText().toString();
                                        else {
                                            medCono1.setText(cant[0] + ".0");
                                            cantFinal = cant[0] + ".0";
                                        }
                                    } else
                                        cantFinal = medCono1.getText().toString();

                                    if (globals.getAntecedentesMuestreo().getAsentamiento().equalsIgnoreCase("Abrams")) {
                                        int cono;
                                        try {
                                            cono = Integer.parseInt(valCono.getText().toString());

                                        } catch (NumberFormatException e) {
                                            cono = 0;
                                        }

                                        if (cono == 0) {
                                            medCono1.setText("");
                                            return;
                                        }
                                        double medidaCono = Double.parseDouble(cantFinal);


                                        if (cono <= 2) {
                                            if (medidaCono < 1.5 || medidaCono > 3)
                                                layoutMedCono2.setVisibility(View.VISIBLE);
                                        } else if (cono >=3 || cono <= 9) {
                                            if (medidaCono < (cono - 2) || medidaCono > (cono + 2))
                                                layoutMedCono2.setVisibility(View.VISIBLE);
                                        } else if (cono >= 10 || cono < 21) {
                                            if (medidaCono < (cono - 3) || medidaCono > (cono + 3))
                                                layoutMedCono2.setVisibility(View.VISIBLE);
                                        } else
                                            layoutMedCono2.setVisibility(View.GONE);
                                    }

                                    globals.getAntecedentesMuestreo().setMedCono1(Double.parseDouble(cantFinal));

                                } catch (NumberFormatException e) {
                                    medCono1.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        medCono2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
                        medCono2.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    globals.getAntecedentesMuestreo().setControlCono(false);
                                    if (medCono2.getText().toString().equals("")) return;
                                    if (Double.parseDouble(medCono2.getText().toString()) > 99) {
                                        medCono2.setText("");
                                        return;
                                    }

                                    String[] cant = medCono2.getText().toString().split("\\.");
                                    String cantFinal;

                                    if (cant.length > 1) {

                                        if (cant[1].equals("0") || cant[1].equals("5"))
                                            cantFinal = medCono2.getText().toString();
                                        else {
                                            medCono2.setText(cant[0] + ".0");
                                            cantFinal = cant[0] + ".0";
                                        }
                                    } else
                                        cantFinal = medCono2.getText().toString();

                                    if (globals.getAntecedentesMuestreo().getAsentamiento().equalsIgnoreCase("Abrams")) {
                                        int cono;
                                        try {
                                            cono = Integer.parseInt(valCono.getText().toString());

                                        } catch (NumberFormatException e) {
                                            cono = 0;
                                        }

                                        if (cono == 0) {
                                            medCono2.setText("");
                                            return;
                                        }
                                        double medidaCono = Double.parseDouble(cantFinal);

                                        if (cono <= 2) {
                                            if (medidaCono < 1.5 || medidaCono > 3)
                                                globals.getAntecedentesMuestreo().setControlCono(true);
                                        } else if (cono >=3 || cono <= 9) {
                                            if (medidaCono < (cono - 2) || medidaCono > (cono + 2))
                                                globals.getAntecedentesMuestreo().setControlCono(true);
                                        } else if (cono >= 10 && cono < 21) {
                                            if (medidaCono < (cono - 3) || medidaCono > (cono + 3))
                                                globals.getAntecedentesMuestreo().setControlCono(true);
                                        } else
                                            globals.getAntecedentesMuestreo().setControlCono(false);
                                    }

                                    globals.getAntecedentesMuestreo().setMedCono2(Double.parseDouble(cantFinal));

                                } catch (NumberFormatException e) {
                                    medCono2.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });


                        cono.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(CompoundButton buttonView,
                                                         boolean isChecked) {
                                if (isChecked) {
                                    boxCono.setVisibility(View.VISIBLE);
                                    valCono.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            try {

                                                if (valCono.getText().toString().equals("")) return;
                                                if (Integer.parseInt(valCono.getText().toString()) > 99 || Integer.parseInt(valCono.getText().toString()) < 1) {
                                                    valCono.setText("");
                                                    return;
                                                }

                                            } catch (NumberFormatException e) {
                                                valCono.setText("");
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    medCono1.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
                                    medCono1.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            try {
                                                globals.getAntecedentesMuestreo().setControlCono(false);
                                                layoutMedCono2.setVisibility(View.GONE);
                                                medCono2.setText("");

                                                if (medCono1.getText().toString().equals(""))
                                                    return;
                                                if (Double.parseDouble(medCono1.getText().toString()) > 99) {
                                                    medCono1.setText("");
                                                    return;
                                                }

                                                String[] cant = medCono1.getText().toString().split("\\.");
                                                String cantFinal;

                                                if (cant.length > 1) {

                                                    if (cant[1].equals("0") || cant[1].equals("5"))
                                                        cantFinal = medCono1.getText().toString();
                                                    else {
                                                        medCono1.setText(cant[0] + ".0");
                                                        cantFinal = cant[0] + ".0";
                                                    }
                                                } else
                                                    cantFinal = medCono1.getText().toString();

                                                if (globals.getAntecedentesMuestreo().getAsentamiento().equalsIgnoreCase("Abrams")) {
                                                    int cono;
                                                    try {
                                                        cono = Integer.parseInt(valCono.getText().toString());

                                                    } catch (NumberFormatException e) {
                                                        cono = 0;
                                                    }

                                                    if (cono == 0) {
                                                        medCono1.setText("");
                                                        return;
                                                    }
                                                    double medidaCono = Double.parseDouble(cantFinal);

                                                    if (cono <= 2) {
                                                        if (medidaCono < 1.5 || medidaCono > 3)
                                                            layoutMedCono2.setVisibility(View.VISIBLE);
                                                    } else if (cono > 3 && cono < 9) {
                                                        if (medidaCono < 1.5 || medidaCono > 11)
                                                            layoutMedCono2.setVisibility(View.VISIBLE);
                                                    } else if (cono >= 10 && cono < 21) {
                                                        if (medidaCono < (cono - 3) || medidaCono > (cono + 3))
                                                            layoutMedCono2.setVisibility(View.VISIBLE);
                                                    } else
                                                        layoutMedCono2.setVisibility(View.GONE);
                                                }

                                                globals.getAntecedentesMuestreo().setMedCono1(Double.parseDouble(cantFinal));

                                            } catch (NumberFormatException e) {
                                                medCono1.setText("");
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                    medCono2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 1)});
                                    medCono2.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                        }

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            try {
                                                globals.getAntecedentesMuestreo().setControlCono(false);
                                                if (medCono2.getText().toString().equals(""))
                                                    return;
                                                if (Double.parseDouble(medCono2.getText().toString()) > 99) {
                                                    medCono2.setText("");
                                                    return;
                                                }

                                                String[] cant = medCono2.getText().toString().split("\\.");
                                                String cantFinal;

                                                if (cant.length > 1) {

                                                    if (cant[1].equals("0") || cant[1].equals("5"))
                                                        cantFinal = medCono2.getText().toString();
                                                    else {
                                                        medCono2.setText(cant[0] + ".0");
                                                        cantFinal = cant[0] + ".0";
                                                    }
                                                } else
                                                    cantFinal = medCono2.getText().toString();

                                                if (globals.getAntecedentesMuestreo().getAsentamiento().equalsIgnoreCase("Abrams")) {
                                                    int cono;
                                                    try {
                                                        cono = Integer.parseInt(valCono.getText().toString());

                                                    } catch (NumberFormatException e) {
                                                        cono = 0;
                                                    }

                                                    if (cono == 0) {
                                                        medCono2.setText("");
                                                        return;
                                                    }
                                                    double medidaCono = Double.parseDouble(cantFinal);

                                                    if (cono <= 2) {
                                                        if (medidaCono < 1.5 || medidaCono > 3)
                                                            globals.getAntecedentesMuestreo().setControlCono(true);
                                                    } else if (cono > 3 && cono < 9) {
                                                        if (medidaCono < 1.5 || medidaCono > 11)
                                                            globals.getAntecedentesMuestreo().setControlCono(true);
                                                    } else if (cono >= 10 && cono < 21) {
                                                        if (medidaCono < (cono - 3) || medidaCono > (cono + 3))
                                                            globals.getAntecedentesMuestreo().setControlCono(true);
                                                    } else
                                                        globals.getAntecedentesMuestreo().setControlCono(false);
                                                }

                                                globals.getAntecedentesMuestreo().setMedCono2(Double.parseDouble(cantFinal));

                                            } catch (NumberFormatException e) {
                                                medCono2.setText("");
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {

                                        }
                                    });
                                } else {
                                    boxCono.setVisibility(View.GONE);
                                }
                            }
                        });
                        compactacion.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesMuestreo().setCompactacion(parentView.getItemAtPosition(position).toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );

                        condClima.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesMuestreo().setCondClima(parentView.getItemAtPosition(position).toString());
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        extraeA.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});
                        extraeA.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (extraeA.getText().toString().equals("")) return;
                                    Double cantidadm3 = globals.getAntecedentesHormigonMuestreo().getCantiM3();
                                    Double extraido = Double.parseDouble(extraeA.getText().toString());

                                    if (cantidadm3 != 0) {
                                        if(extraido != 0.0){
                                            Double operacion = extraido * 100 / cantidadm3;
                                            if (operacion >= 10 && operacion <= 90) {
                                                globals.getAntecedentesMuestreo().setExtraeA(extraeA.getText().toString());
                                            } else {
                                                Toast.makeText(getContext(), "Fuera de límites porcentuales", Toast.LENGTH_SHORT).show();
                                                extraeA.setText("");
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Cantidad m3 debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                                        extraeA.setText("");
                                    }
                                } catch (NumberFormatException e) {
                                    extraeA.setText("");

                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        mixero.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesMuestreo().setMixero(mixero.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        horaDescarga.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                int hour = calendar.get(calendar.HOUR_OF_DAY);
                                int minute = calendar.get(calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String time = (selectedHour > 9 ? selectedHour : "0" + selectedHour)
                                                + ":" +
                                                (selectedMinute > 9 ? selectedMinute : "0" + selectedMinute);
                                        Date horaCamionTime;
                                        Date horaMuestreoTime;
                                        Date horaDescargaTime;
                                        try {
                                            if (globals.getAntecedentesMuestreo().getTimeMuestreo() == null) {
                                                Toast.makeText(getContext(), "No se ha definido hora de muestreo", Toast.LENGTH_SHORT).show();
                                                horaDescarga.setText("");
                                            } else {
                                                horaMuestreoTime = parser.parse(globals.getAntecedentesMuestreo().getTimeMuestreo());
                                                horaDescargaTime = parser.parse(time);
                                                if (horaMuestreoTime.before(horaDescargaTime)) {
                                                    Toast.makeText(getContext(), "Hora de descarga no puede ser mayor a la hora de muestreo", Toast.LENGTH_SHORT).show();
                                                    horaDescarga.setText("");
                                                } else {

                                                    if (globals.getAntecedentesMuestreo().getTimeCamion() == null) {
                                                        Toast.makeText(getContext(), "No se ha definido hora llegada camion", Toast.LENGTH_SHORT).show();
                                                        horaDescarga.setText("");
                                                    } else {
                                                        horaCamionTime = parser.parse(globals.getAntecedentesMuestreo().getTimeCamion());

                                                        horaDescargaTime = parser.parse(time);
                                                        if (horaDescargaTime.after(horaCamionTime)) {

                                                            horaDescarga.setText(time);
                                                            globals.getAntecedentesMuestreo().setHoraDescarga(time);
                                                        } else {
                                                            Toast.makeText(getContext(), "Hora de descarga no puede ser inferior a la hora de llegada camion", Toast.LENGTH_SHORT).show();
                                                            horaDescarga.setText("");
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (ParseException e) {
                                            horaDescarga.setText("");
                                        }
                                    }
                                }, hour, minute, true);
                                mTimePicker.setTitle("Seleccione Hora");
                                mTimePicker.show();

                            }
                        });
                        //</editor-fold>
                        /** FINALIZA GIARDADO **/

                    //</editor-fold>
                }
            }
            /** ANTECEDENTES DE LA PROBETA  **/

            else if (section == 5) {

                //<editor-fold desc="Rutina Antecedentes de la Probeta">

                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);
                else {
                    rootView = inflater.inflate(R.layout.fragment_form5, container, false);

                    /** EDIT TEXT VISTA **/
                    //<editor-fold desc="EDIT TEXT VISTA">
                    final Spinner probEmpleada = (Spinner) rootView.findViewById(R.id.probEmpleada);
                    final EditText otraProbeta = (EditText) rootView.findViewById(R.id.otraProbeta);
                    final Spinner cantProbeta = (Spinner) rootView.findViewById(R.id.cantProbeta);
                    final Spinner curadoProb = (Spinner) rootView.findViewById(R.id.curadoProb);
                    final Spinner sinProbeta = (Spinner) rootView.findViewById(R.id.sinProbeta);
                    final EditText otroCurado = (EditText) rootView.findViewById(R.id.otroCurado);
                    final EditText tempCiba = (EditText) rootView.findViewById(R.id.tempCiba);
                    final EditText numCiba = (EditText) rootView.findViewById(R.id.numCiba);
                    final LinearLayout layoutOtraProbeta = (LinearLayout) rootView.findViewById(R.id.layoutOtraProbeta);
                    final LinearLayout layoutOtroCurado = (LinearLayout) rootView.findViewById(R.id.layoutOtroCurado);
                    final LinearLayout layoutCantProbeta = (LinearLayout) rootView.findViewById(R.id.layoutCantProbeta);
                    final LinearLayout layoutCantProbetaIni = (LinearLayout) rootView.findViewById(R.id.layoutCantProbetaIni);
                    final LinearLayout layoutSinProbeta = (LinearLayout) rootView.findViewById(R.id.layoutSinProbeta);
                    final LinearLayout layoutTempCiba = (LinearLayout) rootView.findViewById(R.id.layoutTempCiba);
                    final LinearLayout layoutNumCiba = (LinearLayout) rootView.findViewById(R.id.layoutNumCiba);
                    //</editor-fold>
                    /** FIN **/

                    /** DATOS DE SELECTS **/
                    //<editor-fold desc="Datos">
                    ArrayList<String> cantProbetas = new ArrayList<String>();
                    cantProbetas.add("");
                    for (int i = 1; i <= globals.getCantProbetas(); i++)
                        cantProbetas.add(String.valueOf(i));
                    //</editor-fold>
                    /** FIN DATOS SELECTS **/

                    /** LLENADO SELECTS **/
                    //<editor-fold desc="Rutina Llenado">

                    /** Probeta Empleada**/
                    probEmpleada.setAdapter(FillSpinner(getActivity(), globals.getProbEmpleadas()));

                    /** Cantidad Probetas**/
                    cantProbeta.setAdapter(FillSpinner(getActivity(), cantProbetas));

                    /** Curado Probeta**/
                    curadoProb.setAdapter(FillSpinner(getActivity(), globals.getCuradosProb()));

                    /** Sin Probeta**/
                    sinProbeta.setAdapter(FillSpinner(getActivity(), globals.getSinProbetas()));

                    //</editor-fold>
                    /** FIN LLENADO SELECTS **/

                    if (muestra != null)
                        llenadoDatosMuestraActiva(section, rootView, muestra);

                        /** SE GUARDA INFORMACIÓN DE INPUTS **/
                        //<editor-fold desc="Guarda Info">
                        probEmpleada.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesProbeta().setProbEmpleada(parentView.getItemAtPosition(position).toString());
                                        otraProbeta.setText("");

                                        if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otra"))
                                            layoutOtraProbeta.setVisibility(View.VISIBLE);
                                        else
                                            layoutOtraProbeta.setVisibility(View.GONE);

                                        if (parentView.getItemAtPosition(position).toString().equals("-")) {
                                            globals.getAntecedentesProbeta().setCantProbeta(0);
                                            layoutCantProbeta.removeAllViews();
                                            layoutCantProbeta.setVisibility(View.GONE);
                                            layoutCantProbetaIni.setVisibility(View.GONE);
                                            //globals.getAntecedentesProbeta().getEdades().clear();
                                            layoutSinProbeta.setVisibility(View.VISIBLE);

                                        } else {
                                            layoutSinProbeta.setVisibility(View.GONE);
                                            layoutCantProbetaIni.setVisibility(View.VISIBLE);
                                        }

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        cantProbeta.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        try {
                                            globals.getAntecedentesProbeta().setCantProbeta(
                                                    Integer.parseInt(parentView.getItemAtPosition(position).toString())
                                            );
                                        } catch (NumberFormatException e) {
                                            globals.getAntecedentesProbeta().setCantProbeta(0);
                                        }

                                        int cantidad = globals.getAntecedentesProbeta().getCantProbeta();

                                        if (cantidad > 0) {

                                            layoutCantProbeta.removeAllViews();
                                            layoutCantProbeta.setVisibility(View.VISIBLE);
                                            //globals.getAntecedentesProbeta().setEdades(new HashMap<String, String>());

                                            final Context thisContext = getActivity();
                                            Display display = ((WindowManager) thisContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                                            LinearLayoutCompat.LayoutParams lp = new LinearLayoutCompat.LayoutParams((int) (display.getWidth() / 2.5), LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

                                            Map<String, String> edadesMap = globals.getAntecedentesProbeta().getEdades();
                                            Map<String, String> moldesMap = globals.getAntecedentesProbeta().getMoldes();

                                            for (int i = 0; i < 2; i++) {

                                                LinearLayout l = new LinearLayout(thisContext);
                                                l.setOrientation(LinearLayout.VERTICAL);

                                                for (int j = 1; j <= cantidad; j++) {

                                                    TextView tv1 = new AppCompatTextView(thisContext);
                                                    TextView tv2 = new AppCompatTextView(thisContext);
                                                    final String text1 = "Edad " + j;
                                                    final String text2 = "Num. Molde " + j;
                                                    final EditText et1 = new AppCompatEditText(thisContext);
                                                    final EditText et2 = new AppCompatEditText(thisContext);
                                                    tv1.setText(text1);
                                                    tv2.setText(text2);
                                                    tv1.setTextSize(13);
                                                    tv1.setTypeface(null, Typeface.BOLD);
                                                    tv1.setTextColor(getResources().getColor(R.color.ColorInput));
                                                    tv2.setTextSize(13);
                                                    tv2.setTypeface(null, Typeface.BOLD);
                                                    tv2.setTextColor(getResources().getColor(R.color.ColorInput));
                                                    et1.setHint("Ingrese valor");
                                                    et1.setTextSize(13);
                                                    et1.setInputType(1);
                                                    et2.setHint("Ingrese valor");
                                                    et2.setTextSize(13);
                                                    et2.setInputType(1);


                                                    if (edadesMap.get(text1) != null && moldesMap.get(text2) != null) {
                                                        Globals globalsAux = (Globals) getActivity().getApplicationContext();
                                                        et1.setText(globalsAux.getAntecedentesProbeta().getEdades().get(text1));
                                                        et2.setText(globalsAux.getAntecedentesProbeta().getMoldes().get(text2));
                                                    }

                                                    if (i == 0) l.addView(tv1, lp);
                                                    else l.addView(tv2, lp);

                                                    final int num = i;



                                                    et1.addTextChangedListener(new TextWatcher() {

                                                        @Override
                                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                        }

                                                        @Override
                                                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                                                            if (et1.getText().toString().length() < 4) {
                                                                if(et1.getText().toString().equals("")) return;
                                                                    try {
                                                                        int numero = Integer.parseInt(et1.getText().toString());
                                                                    } catch (NumberFormatException e) {
                                                                        int total = et1.getText().toString().length();
                                                                        char letra = et1.getText().toString().charAt(total - 1);
                                                                        if ((letra == 'd') || (letra == 'h')) {
                                                                            globals.getAntecedentesProbeta().getEdades().put((num == 0 ? text1 : text2), et1.getText().toString());
                                                                        } else {
                                                                            Toast.makeText(getContext(), "Letra incorrecta" ,Toast.LENGTH_SHORT).show();
                                                                            et1.setText("");

                                                                        }
                                                                      }

                                                            } else {
                                                                et1.setText("");
                                                            }
                                                        }

                                                        @Override
                                                        public void afterTextChanged(Editable s) {

                                                        }
                                                    });

                                                    et2.addTextChangedListener(new TextWatcher() {
                                                        @Override
                                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                                        }

                                                        @Override
                                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                            if (et2.getText().toString().length() < 5) {
                                                                globals.getAntecedentesProbeta().getMoldes().put((num == 0 ? text1 : text2), et2.getText().toString());

                                                            } else {
                                                                et2.setText("");
                                                            }
                                                        }

                                                        @Override
                                                        public void afterTextChanged(Editable s) {

                                                        }
                                                    });

                                                    if (i == 0) l.addView(et1, lp);
                                                    else l.addView(et2, lp);
                                                }

                                                layoutCantProbeta.addView(l);

                                            }
                                        } else layoutCantProbeta.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        curadoProb.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesProbeta().setCuradoProb(parentView.getItemAtPosition(position).toString());
                                        otroCurado.setText("");

                                        if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otro"))
                                            layoutOtroCurado.setVisibility(View.VISIBLE);
                                        else if (!parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Ciba"))
                                            layoutOtroCurado.setVisibility(View.GONE);
                                        layoutTempCiba.setVisibility(View.GONE);
                                        layoutNumCiba.setVisibility(View.GONE);

                                        if (parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Ciba")) {
                                            layoutTempCiba.setVisibility(View.VISIBLE);
                                            layoutNumCiba.setVisibility(View.VISIBLE);
                                            layoutOtroCurado.setVisibility(View.GONE);
                                        } else if (!parentView.getItemAtPosition(position).toString().equalsIgnoreCase("Otro")) {
                                            layoutTempCiba.setVisibility(View.GONE);
                                            layoutNumCiba.setVisibility(View.GONE);
                                            layoutOtroCurado.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        sinProbeta.setOnItemSelectedListener(
                                new AdapterView.OnItemSelectedListener() {

                                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
                                                               int position, long id) {
                                        globals.getAntecedentesProbeta().setSinProbeta(parentView.getItemAtPosition(position).toString());

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                }
                        );
                        otraProbeta.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesProbeta().setOtraProbeta(otraProbeta.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        tempCiba.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (tempCiba.getText().toString().equals("") || tempCiba.getText().toString().equals("-"))
                                        return;
                                    if (Double.parseDouble(tempCiba.getText().toString()) > 50 || Double.parseDouble(tempCiba.getText().toString()) < -50) {
                                        Toast.makeText(getContext(), "Temperatura fuera de límites", Toast.LENGTH_SHORT).show();
                                        tempCiba.setText("");
                                    } else {
                                        globals.getAntecedentesProbeta().setTempCiba(Double.parseDouble(tempCiba.getText().toString()));
                                    }
                                } catch (NumberFormatException e) {
                                    tempCiba.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        numCiba.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                try {
                                    if (numCiba.getText().toString().equals("")) return;
                                    globals.getAntecedentesProbeta().setNumCiba(Integer.parseInt(numCiba.getText().toString()));
                                } catch (NumberFormatException e) {
                                    numCiba.setText("");
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        otroCurado.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                globals.getAntecedentesProbeta().setOtroCurado(otroCurado.getText().toString());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        //</editor-fold>
                        /** FINALIZA GIARDADO **/
                    }
                //</editor-fold>
            }


            /** OTROS ENSAYOS IN SITU  **/

            else if (section == 6) {

                //<editor-fold desc="Rutina Otros Ensayos In Situ">

                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);
                else {
                    rootView = inflater.inflate(R.layout.fragment_form6, container, false);

                    /** EDIT TEXT VISTA **/
                    //<editor-fold desc="EDIT TEXT VISTA">
                    Switch contenidoAire = (Switch) rootView.findViewById(R.id.contenidoAire);
                    Switch hormigonFresco = (Switch) rootView.findViewById(R.id.hormigonFresco);
                    final LinearLayout boxHormigonFresco = (LinearLayout) rootView.findViewById(R.id.boxHormigonFresco);
                    final LinearLayout boxDensidadAire = (LinearLayout) rootView.findViewById(R.id.boxDensidadAire);
                    final EditText densHorFresco = (EditText) rootView.findViewById(R.id.densHorFresco);
                    final EditText balanza = (EditText) rootView.findViewById(R.id.balanza);
                    final EditText medidaVolumetrica = (EditText) rootView.findViewById(R.id.medidaVolumetrica);
                    final EditText peso = (EditText) rootView.findViewById(R.id.peso);
                    final EditText peso2 = (EditText) rootView.findViewById(R.id.peso2);
                    final EditText maquinaVolumetrica = (EditText) rootView.findViewById(R.id.maquinaVolumetrica);
                    final EditText contAire = (EditText) rootView.findViewById(R.id.contAire);
                    final EditText aerimetro = (EditText) rootView.findViewById(R.id.aerimetro);
                    //</editor-fold>
                    /** FIN **/

                    /** SE GUARDA INFORMACIÓN DE INPUTS **/

                    hormigonFresco.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {

                            if (isChecked) {

                                boxHormigonFresco.setVisibility(View.VISIBLE);

                                balanza.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            if (balanza.getText().toString().equals("")) return;
                                            globals.getOtrosEnsayosInSitu().setBalanza(balanza.getText().toString());
                                        } catch (NumberFormatException e) {
                                            balanza.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                maquinaVolumetrica.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            if (maquinaVolumetrica.getText().toString().equals(""))
                                                return;
                                            globals.getOtrosEnsayosInSitu().setMaquinaVolumetrica(maquinaVolumetrica.getText().toString());
                                        } catch (NumberFormatException e) {
                                            maquinaVolumetrica.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                medidaVolumetrica.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            if (medidaVolumetrica.getText().toString().equals("")) {
                                                globals.getOtrosEnsayosInSitu().setMedidaVolumetrica(0.0);
                                                return;
                                            }
                                            globals.getOtrosEnsayosInSitu().setMedidaVolumetrica(Double.parseDouble(medidaVolumetrica.getText().toString()));

                                            densHorFresco.setText(String.valueOf(setDenHorfgresco(
                                                    globals.getOtrosEnsayosInSitu().getPeso2(), globals.getOtrosEnsayosInSitu().getPeso(), globals.getOtrosEnsayosInSitu().getMedidaVolumetrica()
                                            )));
                                        } catch (NumberFormatException e) {
                                            medidaVolumetrica.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                peso.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 3)});
                                peso.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            if (peso.getText().toString().equals("")) {
                                                globals.getOtrosEnsayosInSitu().setPeso(0.0);
                                                return;
                                            }
                                            if (Double.parseDouble(peso.getText().toString()) > 9000) {
                                                Toast.makeText(getContext(), "Fuera de limite", Toast.LENGTH_SHORT).show();
                                                peso.setText("");
                                            } else {
                                                globals.getOtrosEnsayosInSitu().setPeso(Double.parseDouble(peso.getText().toString()));

                                                densHorFresco.setText(String.valueOf(setDenHorfgresco(
                                                        globals.getOtrosEnsayosInSitu().getPeso2(), globals.getOtrosEnsayosInSitu().getPeso(), globals.getOtrosEnsayosInSitu().getMedidaVolumetrica()
                                                )));
                                            }
                                        } catch (NumberFormatException e) {
                                            peso.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                peso2.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 3)});
                                peso2.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            if (peso2.getText().toString().equals("")) {
                                                globals.getOtrosEnsayosInSitu().setPeso2(0.0);
                                                return;
                                            }
                                            if (Double.parseDouble(peso2.getText().toString()) > 9000) {
                                                Toast.makeText(getContext(), "Fuera de limite", Toast.LENGTH_SHORT).show();
                                                peso2.setText("");
                                            } else {
                                                globals.getOtrosEnsayosInSitu().setPeso2(Double.parseDouble(peso2.getText().toString()));
                                                densHorFresco.setText(String.valueOf(setDenHorfgresco(
                                                        globals.getOtrosEnsayosInSitu().getPeso2(), globals.getOtrosEnsayosInSitu().getPeso(), globals.getOtrosEnsayosInSitu().getMedidaVolumetrica()
                                                )));
                                            }
                                        } catch (NumberFormatException e) {
                                            peso2.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                                densHorFresco.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {

                                            if (densHorFresco.getText().toString().equals(""))
                                                return;

                                            globals.getOtrosEnsayosInSitu().setDensidadHorFresco(Double.parseDouble(densHorFresco.getText().toString()));

                                        } catch (NumberFormatException e) {
                                            densHorFresco.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                            } else {
                                boxHormigonFresco.setVisibility(View.GONE);


                            }
                        }
                    });

                    contenidoAire.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {

                            if (isChecked) {
                                boxDensidadAire.setVisibility(View.VISIBLE);
                                contAire.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(2, 1)});
                                contAire.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            if (contAire.getText().toString().equals("")) return;
                                            if (Double.parseDouble(contAire.getText().toString()) >= 10) {
                                                Toast.makeText(getContext(), "Fuera de limite", Toast.LENGTH_SHORT).show();
                                                contAire.setText("");
                                            } else {
                                                if (contAire.getText().toString().equals(""))
                                                    return;
                                                globals.getOtrosEnsayosInSitu().setPeso(Double.parseDouble(contAire.getText().toString()));
                                            }
                                        } catch (NumberFormatException e) {
                                            contAire.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });
                                aerimetro.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        try {
                                            if (aerimetro.getText().toString().equals("")) return;
                                            globals.getOtrosEnsayosInSitu().setAerimetro(aerimetro.getText().toString());
                                        } catch (NumberFormatException e) {
                                            aerimetro.setText("");
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                            } else {
                                boxDensidadAire.setVisibility(View.GONE);

                            }
                        }
                    });

                    //</editor-fold>
                    /** FINALIZA GUARDADO **/
                }

                //</editor-fold>
            }

            /** EQUIPO UTILIZADO  **/

            else if (section == 7) {

                //<editor-fold desc="Rutina Equipo Utilizado">

                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);
                else {
                    rootView = inflater.inflate(R.layout.fragment_form7, container, false);

                    /** EDIT TEXT VISTA **/
                    //<editor-fold desc="EDIT TEXT VISTA">
                    final EditText cono = (EditText) rootView.findViewById(R.id.cono);
                    cono.setText(globals.getInstrumentos().getIns_equipo_cono());
                    final EditText termometro = (EditText) rootView.findViewById(R.id.termometro);
                    termometro.setText(globals.getInstrumentos().getIns_termometro());
                    final EditText flexometro = (EditText) rootView.findViewById(R.id.flexometro);
                    flexometro.setText(globals.getInstrumentos().getIns_flexometro());
                    final EditText motorVibra = (EditText) rootView.findViewById(R.id.motorVibra);
                    motorVibra.setText(globals.getInstrumentos().getIns_motor_vibrador());
                    final EditText sonda = (EditText) rootView.findViewById(R.id.sonda);
                    sonda.setText(globals.getInstrumentos().getIns_sonda());
                    //</editor-fold>
                    /** FIN **/

                    /** SE GUARDA INFORMACIÓN DE INPUTS **/
                    //<editor-fold desc="Guarda Info">
                    cono.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getEquipoUtilizado().setCono(cono.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    termometro.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getEquipoUtilizado().setTermometro(termometro.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    flexometro.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getEquipoUtilizado().setFlexometro(flexometro.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    motorVibra.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getEquipoUtilizado().setMotorVibra(motorVibra.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    sonda.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getEquipoUtilizado().setSonda(sonda.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    //</editor-fold>
                    /** FINALIZA GIARDADO **/
                }

                //</editor-fold>
            }

            /** OBSERVACIONES  **/

            else if (section == 8) {

                //<editor-fold desc="Rutina Observaciones">

                final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);
                else {
                    rootView = inflater.inflate(R.layout.fragment_form8, container, false);

                    /** EDIT TEXT VISTA **/
                    //<editor-fold desc="EDIT TEXT VISTA">
                    final EditText obsMuestreador = (EditText) rootView.findViewById(R.id.obsMuestreador);
                    final EditText obsEncargado = (EditText) rootView.findViewById(R.id.obsEncargado);
                    final EditText nomEncargado = (EditText) rootView.findViewById(R.id.nomEncargado);
                    final EditText comentarios = (EditText) rootView.findViewById(R.id.comentarios);
                    final EditText horaOut = (EditText) rootView.findViewById(R.id.horaOut);
                    Switch anulaBoleta = (Switch) rootView.findViewById(R.id.anulaBoleta);
                    final LinearLayout boxAnularBoleta = (LinearLayout) rootView.findViewById(R.id.boxAnularBoleta);
                    final EditText motivoAnular = (EditText) rootView.findViewById(R.id.motivoAnular);
                    //</editor-fold>
                    /** FIN **/

                    /** SE GUARDA INFORMACIÓN DE INPUTS **/
                    //<editor-fold desc="Guarda Info">
                    obsMuestreador.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getObservaciones().setObsMuestreador(obsMuestreador.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    obsEncargado.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getObservaciones().setObsEncargado(obsEncargado.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    nomEncargado.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                            globals.getObservaciones().setNomEncargado(nomEncargado.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    horaOut.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            int hour = calendar.get(calendar.HOUR_OF_DAY);
                            int minute = calendar.get(calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    String time = (selectedHour > 9 ? selectedHour : "0" + selectedHour)
                                            + ":" +
                                            (selectedMinute > 9 ? selectedMinute : "0" + selectedMinute);

                                    Date horaInTime;
                                    Date horaOutTime;
                                    try {
                                        if (globals.getMuestra().getHoraIn() == null) {
                                            Toast.makeText(getContext(), "No se ha definido hora de llegada", Toast.LENGTH_SHORT).show();
                                            horaOut.setText("");
                                        } else {
                                            horaInTime = parser.parse(globals.getMuestra().getHoraIn());
                                            horaOutTime = parser.parse(time);
                                            if (horaOutTime.after(horaInTime)) {

                                                horaOut.setText(time);
                                                globals.getMuestra().setHoraOut(time);
                                            } else {
                                                Toast.makeText(getContext(), "Hora de salida no puede ser inferior a la hora de llegada", Toast.LENGTH_SHORT).show();
                                                horaOut.setText("");
                                            }
                                        }
                                    } catch (ParseException e) {
                                        horaOut.setText("");
                                    }
                                }
                            }, hour, minute, true);
                            mTimePicker.setTitle("Seleccione Hora");
                            mTimePicker.show();

                        }
                    });
                    comentarios.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            globals.getObservaciones().setComentarios(comentarios.getText().toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                    anulaBoleta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            // globals.getObservaciones().setAnulaBoleta(isChecked);
                            if (isChecked) {
                                globals.getObservaciones().setAnulaBoleta(isChecked);
                                boxAnularBoleta.setVisibility(View.VISIBLE);
                                motivoAnular.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                                        globals.getObservaciones().setMotivoAnular(motivoAnular.getText().toString());
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                            } else {
                                boxAnularBoleta.setVisibility(View.GONE);


                            }
                        }
                    });
                    //</editor-fold>
                    /** FINALIZA GIARDADO **/
                }

                //</editor-fold>
            }

            /** OPCIONES DE GUARDADO  **/

            else if (section == 9) {

                //<editor-fold desc="Rutina Opciones Guardado">

                addrPrinter = globals.getAddrPrinter();
                printer = new PrinterZebraIMZ320(getContext().getApplicationContext());

                if (bViajePerdido)
                    rootView = inflater.inflate(R.layout.fragment_form_perdido, container, false);
                else {
                    rootView = inflater.inflate(R.layout.fragment_form9, container, false);

                    /** INPUTS VISTA **/
                    //<editor-fold desc="INPUTS VISTA">
                    final ImageView sendServer = (ImageView) rootView.findViewById(R.id.sendServer);
                   // final ImageView saveFile = (ImageView) rootView.findViewById(R.id.saveFile);
                    final ImageView firmar = (ImageView) rootView.findViewById(R.id.firmar);
                    final ImageView takePicture = (ImageView) rootView.findViewById(R.id.takePicture);
                    final ImageView print = (ImageView) rootView.findViewById(R.id.print);
                    final TextView textSendServer = (TextView) rootView.findViewById(R.id.textSendServer);
                    final TextView textControlCono = (TextView) rootView.findViewById(R.id.textControlCono);


                   /* final AlertDialog alerta = new AlertDialog.Builder(getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Falta llenar los siguientes campos")
                            .setMessage(message)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    empty = false;
                                }

                            })
                            .show();*/
                    //</editor-fold>
                    /** FIN **/

                    if (globals.getAntecedentesMuestreo().isControlCono()) {
                        textControlCono.setVisibility(View.VISIBLE);
                        textSendServer.setVisibility(View.GONE);
                    } else {
                        textControlCono.setVisibility(View.GONE);
                        textSendServer.setVisibility(View.VISIBLE);
                    }

                    sendServer.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                          //  if (!validation(globals)){


                                if (globals.getMuestra().getImage() != null && !globals.getMuestra().getImage().equals("")) {
                                    String datos = "Datos obtenidos desde el formulario:\n\n" +
                                            globals.getMuestra().toString() + "\n" +
                                            globals.getAntecedentesHormigonMuestreo().toString() + "\n" +
                                            globals.getColocadoEn().toString() + "\n" +
                                            globals.getAntecedentesMuestreo().toString() + "\n" +
                                            globals.getAntecedentesProbeta().toString() + "\n" +
                                            globals.getOtrosEnsayosInSitu().toString() + "\n" +
                                            globals.getEquipoUtilizado().toString() + "\n" +
                                            globals.getObservaciones().toString() + "\n";
                                    //    sendMail(sendTo, "Datos Muestreo (correo de prueba)", datos);
                                    JSONObject json = setJSONCompleto(bViajePerdido);
                                    try {
                                        createFile(json.toString(4), String.valueOf(globals.getMuestra().getNumMuestra()), false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (muestraCargada) {
                                        int index = globals.getListaMuestras().lastIndexOf(muestra);
                                        globals.getListaMuestras().get(index).setCantMuestras(globals.getListaMuestras().get(index).getCantMuestras() + 1);
                                    }
                                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                                    int minute = calendar.get(Calendar.MINUTE);
                                    String horaTermino = (hour > 9 ? hour : "0" + hour) + ":" + (minute > 9 ? minute : "0" + minute);
                                    globals.getMuestra().setHoraTermino(horaTermino);

                                    sendPost(bViajePerdido, globals.getMuestra().getNumMuestra());

                               } else
                                    Toast.makeText(getContext(), "Debe seleccionar una foto antes de realizar esta acción", Toast.LENGTH_LONG).show();

                         //  }

                        }
                    });

                  /*  saveFile.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            JSONObject json = setJSONCompleto(bViajePerdido);
                            try {
                                createFile(json.toString(4), String.valueOf(globals.getMuestra().getNumMuestra()), true );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });*/

                    takePicture.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                           // mPicturePath = dispatchTakePictureIntent(getActivity(), PlaceholderFragment.this);
                          startActivity(new Intent(getActivity(), CameraActivity.class));
                        }
                    });

                    firmar.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            startActivity(new Intent(getActivity(), CaptureSignature.class));

                        }
                    });

                    print.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (validation(globals)) {

                              //  if (globals.getMuestra().getImage() != null && !globals.getMuestra().getImage().equals("")) {

                                    progressDialog = ProgressDialog.show(getActivity(), "Impresión", "Imprimiendo voucher...", true, false);
                                    new Thread(new Runnable() {
                                        public void run() {
                                            Looper.prepare();
                                            if (printer.setPrintVaucher(
                                                    addrPrinter,
                                                    globals.getMuestra(),
                                                    globals.getAntecedentesHormigonMuestreo(),
                                                    globals.getColocadoEn(),
                                                    globals.getAntecedentesMuestreo()

                                            )) {
                                                progressDialog.dismiss();
                                                final CharSequence[] options = {"Imprimir todos los tickets", "Imprimir tickets de a uno", "Cancelar"};
                                                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());

                                                builder.setTitle("Imprimir tickets?");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int seleccion) {
                                                        if (options[seleccion] == "Imprimir todos los tickets") {
                                                            printingTickets(PrinterZebraIMZ320.PRINT_ALL);
                                                        } else if (options[seleccion] == "Imprimir tickets de a uno") {
                                                            printingTickets(PrinterZebraIMZ320.PRINT_ONE);
                                                        } else if (options[seleccion] == "Cancelar") {
                                                            dialog.dismiss();
                                                        }
                                                    }
                                                });
                                                builder.show();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(getContext(), "Ocurrió un error, intente nuevamente", Toast.LENGTH_LONG).show();
                                            }
                                            Looper.loop();
                                            Looper.myLooper().quit();
                                        }
                                    }).start();
                                //} else
                                   // Toast.makeText(getContext(), "Debe seleccionar una foto antes de realizar esta acción", Toast.LENGTH_LONG).show();
                            }
                        }

                    });

                }



                //</editor-fold>

            }

            /** OPCION DEFAULT  **/

            else
                rootView = inflater.inflate(R.layout.fragment_form1, container, false);

            // Filtro de acciones que serán alertadas
            IntentFilter filter = new IntentFilter(Globals.ACTION_RUN_ISERVICE);
            filter.addAction(Globals.ACTION_PROGRESS_EXIT);

            // Crear un nuevo ResponseReceiver
            ResponseReceiver receiver =
                    new ResponseReceiver();

            // Registrar el receiver y su filtro
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                    receiver,
                    filter);

            return rootView;
        }

        private void printingTickets(final int quantity) {
            progressDialog = ProgressDialog.show(getActivity(), "Impresión", "Imprimiendo ticket...", true, false);
            new Thread(new Runnable() {
                public void run() {
                    Looper.prepare();

                    if (!printer.setPrintTicket(addrPrinter, 123412, quantity))
                        Toast.makeText(getContext(), "Ocurrió un error, intente nuevamente", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    switch (quantity) {
                        case PrinterZebraIMZ320.PRINT_ONE:
                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            printingTickets(PrinterZebraIMZ320.PRINT_ONE);
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };
                            builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Imprimir siguiente ticket?").setPositiveButton("Sí", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                            break;
                        case PrinterZebraIMZ320.PRINT_ALL:
                            break;
                    }
                    Looper.loop();
                    Looper.myLooper().quit();
                }
            }).start();
        }

        public void turnIntentService(int muestra) {
            Intent intent = new Intent(getActivity(), UploadService.class);
            intent.putExtra("muestra", muestra);
            intent.setAction(Globals.ACTION_RUN_ISERVICE);
            getActivity().startService(intent);
        }

        private double setDenHorfgresco(Double m2, Double m1, Double v) {
            Double m = (m2 != null ? m2 : 0) - (m1 != null ? m1 : 0);
            return ((m != null && v != null && v != 0) ? m / v : 0)*1000 ;

        }
        public double Redondear(double numero,int digitos)     {
            int cifras=(int) Math.pow(10,digitos);
            return Math.rint(numero*cifras)/cifras;

        }

        private Boolean validation (Globals globals){
            String message = "";
            boolean bandera = true;

            /*if (globals.getAntecedentesHormigonMuestreo().getNumGuia() == 0) {
                message += "Falta número de guía";
                bandera = true;
            }
            if (Double.toString(globals.getAntecedentesHormigonMuestreo().getCantiM3()) == null) {
                message += "\nFalta cantidad m3";
                bandera = true;
            }
            if (globals.getAntecedentesHormigonMuestreo().getPlanta() == null) {
                message += "\nFalta planta";
                bandera = true;
            }
            if (globals.getAntecedentesHormigonMuestreo().getGradoTipo() == null) {
                message += "\nFalta grado tipo";
                bandera = true;
            }
            if (globals.getAntecedentesHormigonMuestreo().getPlantaOut() == null) {
                message += "\nFalta hora salida planta";
                bandera = true;
            }
            if (globals.getAntecedentesHormigonMuestreo().getCompactObra() == null) {
                message += "\nFalta compactación";
                bandera = true;
            }
            if (globals.getAntecedentesMuestreo().getTimeMuestreo() == null) {
                message += "\nFalta hora muestra";
                bandera = true;
            }
            if (globals.getAntecedentesMuestreo().getTempAmb() == null) {
                message += "\nFalta temperatura ambiente";
                bandera = true;
            }
            if (globals.getAntecedentesMuestreo().getTempMezcla() == null) {
                message += "\nFalta temperatura mezcla";
                bandera = true;
            }
            if (globals.getAntecedentesMuestreo().getAsentamiento() == null) {
                message += "\nFalta asentamiento";
                bandera = true;
            }
            if (globals.getAntecedentesMuestreo().getCompactacion() == null) {
                message += "\nFalta compactación";
                bandera = true;
            }
            if (globals.getAntecedentesMuestreo().getHoraDescarga() == null) {
                message += "\nFalta hora descarga";
                bandera = true;
            }
            if (globals.getAntecedentesProbeta().getEdades() == null) {
                message += "\nFalta edades";
                bandera = true;
            }
            if (globals.getAntecedentesProbeta().getProbEmpleada() == null) {
                message += "\nFalta probeta empleada";
                bandera = true;
            }
            if (globals.getAntecedentesProbeta().getCuradoProb() == null) {
                message += "\nFalta curado probeta";
                bandera = true;
            }
            if (globals.getAntecedentesProbeta().getCuradoProb().equals("Ciba")) {
                if (globals.getAntecedentesProbeta().getTempCiba() == null) {
                    message += "\nFalta temperatura ciba";
                    bandera = true;
                }
                if (globals.getAntecedentesProbeta().getNumCiba() == 0) {
                    message += "\nFalta numero ciba";
                    bandera = true;
                }

            }
            if (globals.getAntecedentesMuestreo().getMixero() == null) {
                message += "\nFalta mixero";
                bandera = true;
            }
            if (globals.getColocadoEn().getTipoEdificio() == null) {
                message += "\nFalta colocado en";
                bandera = true;
            }*/

           /* if (globals.getAntecedentesMuestreo().getDateMuestreo() == null) {
                message += "Fecha Muestreo";
                bandera = false;
            }*/

            if(!bandera){
                message(message);
            }

            return bandera;


        }

        private void message (String message){
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Falta llenar los siguientes campos")
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            empty = false;
                        }

                    })
                    .show();
        }



        private void sendPost(final boolean bViajePerdido, final int muestra) {
            Globals globals = (Globals) getActivity().getApplicationContext();
            globals.getTalonarios().getCurrentTalonario().setNext();

            progressDialog = ProgressDialog.show(getActivity(), "Envío de formulario", "Enviando datos", true, false);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Globals.getURL() + "form",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "No se pudo enviar muestra al servidor, la muestra quedará en espera", Toast.LENGTH_LONG).show();
                            turnIntentService(muestra);
                            getActivity().finish();
                        }
                    }) {
                public Map getHeaders() throws AuthFailureError {
                    Map headers = new HashMap();
                    headers.put("api_key", "$2y$10$jriZ2PDg8sPnPVJJ2xPD0ObFKYgHDHCTZ2wliVnx0ZF.1itLzFudi");
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Globals globals = (Globals) getActivity().getApplicationContext();
                    Map<String, String> params = new HashMap<String, String>();
                    JSONObject json;
                    String key;
                    Iterator<String> keys;

                    //<editor-fold desc="Envia Muestra">
                    try {
                        params.put("talonarioId",String.valueOf(globals.getTalonarios().getCurrentTalonario().getId()));

                        if (bViajePerdido)
                            json = new JSONObject(globals.getMuestra().toStringNoImg());
                        else
                            json = new JSONObject(globals.getMuestra().toString());
                        json = new JSONObject(json.getString("Muestra"));

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

                    if (!bViajePerdido) {

                        //<editor-fold desc="Envía Antecedentes Hormigón Muestreo">
                        try {
                            json = new JSONObject(globals.getAntecedentesHormigonMuestreo().toString());
                            json = new JSONObject(json.getString("AntecedentesHormigonMuestreo"));

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
                            json = new JSONObject(globals.getAntecedentesMuestreo().toString());
                            json = new JSONObject(json.getString("AntecedentesMuestreo"));

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
                            json = new JSONObject(globals.getAntecedentesProbeta().toString());
                            json = new JSONObject(json.getString("AntecedentesProbeta"));

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
                            json = new JSONObject(globals.getColocadoEn().toString());
                            json = new JSONObject(json.getString("ColocadoEn"));

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
                            json = new JSONObject(globals.getEquipoUtilizado().toString());
                            json = new JSONObject(json.getString("EquipoUtilizado"));

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
                            json = new JSONObject(globals.getObservaciones().toString());
                            json = new JSONObject(json.getString("Observaciones"));

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
                            json = new JSONObject(globals.getOtrosEnsayosInSitu().toString());
                            json = new JSONObject(json.getString("OtrosEnsayosInSitu"));

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

                    return params;

                }


            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);


        }

        private JSONObject setJSONCompleto(boolean viajePerido) {
            Globals globals = (Globals) getActivity().getApplicationContext();
            //Globals globals = (Globals)getContext().getApplicationContext();
            SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat hours = new SimpleDateFormat("HH:mm:ss");
            String currentDate = date.format(new Date());
            String currentTime = hours.format(new Date());
            String idTalonario = String.valueOf(globals.getTalonarios().getCurrentTalonario().getId());
            String getDir = globals.getDir();
            String datosAntecedentes = "{'datosAntecedentes':{" +
                    "'Inspector':" + globals.getInspector() + ", " +
                    "'Hora':'" + currentTime + '\'' +
                    ",'Fecha':'" + currentDate + '\'' +
                    ",'talonarioId':'" + idTalonario + '\'' +
                    ",'dir':'" + getDir + '\'' +
                    "}}";

            JSONObject combinado = null;
            try {
                JSONObject json_muestra = new JSONObject(globals.getMuestra().toStringNoImg());
                JSONObject json_hmuestreo = new JSONObject(globals.getAntecedentesHormigonMuestreo().toString());
                JSONObject json_amuestreo = new JSONObject(globals.getAntecedentesMuestreo().toString());
                JSONObject json_probeta = new JSONObject(globals.getAntecedentesProbeta().toString());
                JSONObject json_colocado = new JSONObject(globals.getColocadoEn().toString());
                JSONObject json_equipo = new JSONObject(globals.getEquipoUtilizado().toString());
                JSONObject json_observaciones = new JSONObject(globals.getObservaciones().toString());
                JSONObject json_otroe = new JSONObject(globals.getOtrosEnsayosInSitu().toString());
                JSONObject json_datosAntecedentes = new JSONObject(datosAntecedentes);

                JSONObject[] objs;
                combinado = new JSONObject();
                if (viajePerido) {
                    objs = new JSONObject[]{json_datosAntecedentes, json_muestra};
                } else {
                    objs = new JSONObject[]{json_datosAntecedentes, json_muestra, json_hmuestreo, json_amuestreo, json_probeta, json_colocado,
                            json_equipo, json_observaciones, json_otroe};
                }
                for (JSONObject obj : objs) {
                    Iterator it = obj.keys();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        if (key.equals("viajePerdido"))
                            combinado.put(key, bViajePerdido);
                        else
                            combinado.put(key, obj.get(key));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return combinado;
        }

        private void createFile(String arr, String muestra, boolean msg) {

            File sdCard, directory;
            FileWriter file;

            try {
                if (Environment.getExternalStorageState().equals("mounted")) {

                    sdCard = Environment.getExternalStorageDirectory();

                    contadorLinea = arr.length();

                    try {
                        directory = new File(sdCard.getAbsolutePath()
                                + "/Archivos Formulario DHC");
                        directory.mkdirs();

                        file = new FileWriter(directory + "/DHC_" + muestra + ".txt", false);
                        file.write(arr);
                        file.flush();
                        file.close();

                        if (msg) Toast.makeText(getContext(),
                                "Archivo almacenado",
                                Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                        if (msg) Toast.makeText(getContext(),
                                "Error al intentar generar el archivo",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (msg) Toast.makeText(getContext(),
                            "El almacenamineto externo no se encuentra disponible",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        //---- Below here is from what the class implements
        @Override
        public void onConnected(Bundle bundle) {

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {

        }

        private void sendMail(String email, String subject, String messageBody) {
            Session session = createSessionObject();

            try {
                Message message = createMessage(email, subject, messageBody, session);
                new SendMailTask().execute(message);
            } catch (AddressException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-responder@idiem.cl", "Aplicación DHC::IDIEM"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
            message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse("jose.becerra@idiem.cl"));
            message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse("raul.sanchez@idiem.cl"));
            message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse("juan.aguilar@idiem.cl"));
            message.setSubject(subject);
            message.setText(messageBody);
            return message;
        }

        private Session createSessionObject() {
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");

            return Session.getInstance(properties, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        private class ResponseReceiver extends BroadcastReceiver {

            private ResponseReceiver() {
            }

            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {

                    case Globals.ACTION_RUN_ISERVICE:
                        // System.out.println(intent.getIntExtra(Globals.EXTRA_PROGRESS, -1) + "");
                        break;

                    case Globals.ACTION_PROGRESS_EXIT:
                        // System.out.println("Progreso");
                        break;
                }
            }
        }

        private class SendMailTask extends AsyncTask<Message, Void, Void> {
            private ProgressDialog progressDialog;

            @Override
            protected void onPreExecute() {
                progressDialog = ProgressDialog.show(getActivity(), "Envío de formulario", "Enviando datos", true, false);
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                progressDialog.dismiss();
                super.onPostExecute(aVoid);
            }

            @Override
            protected Void doInBackground(Message... messages) {
                try {
                    Transport.send(messages[0]);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

    // Task Conexión Bluetooth.
    class connTask extends AsyncTask<BluetoothDevice, Void, Integer> {

        private static final String TAG = "BluetoothConnectMenu";
        // Intent request codes
        // private static final int REQUEST_CONNECT_DEVICE = 1;
        private static final int REQUEST_ENABLE_BT = 2;
        private final ProgressDialog dialog = new ProgressDialog(FormularioActivity.this);
        ArrayAdapter<String> adapter;
        private BluetoothAdapter mBluetoothAdapter;
        private BroadcastReceiver searchFinish;
        private Thread hThread;
        private Context context = FormularioActivity.this;
        // BT
        private BluetoothPort bluetoothPort;
        private String lastConnAddr;

        @Override
        protected void onPreExecute() {
            dialog.setTitle(getResources().getString(R.string.bt_tab));
            dialog.setMessage(getResources().getString(R.string.connecting_msg));
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(BluetoothDevice... params) {

            Integer retVal;
            bluetoothSetup();

            try {
                bluetoothPort.connect(params[0]);
                lastConnAddr = params[0].getAddress();
                retVal = new Integer(0);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                retVal = new Integer(-1);
            }

            return retVal;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result.intValue() == 0) {

                // Conexión exitosa.
                RequestHandler rh = new RequestHandler();
                hThread = new Thread(rh);
                hThread.start();

                if (dialog.isShowing())
                    dialog.dismiss();

                Toast toast = Toast.makeText(context, getResources().getString(R.string.bt_conn_msg), Toast.LENGTH_SHORT);
                toast.show();
            } else {

                // Conexión fallida.
                if (dialog.isShowing())
                    dialog.dismiss();

                AlertView.showAlert(getResources().getString(R.string.bt_conn_fail_msg),
                        getResources().getString(R.string.dev_check_msg), context);

            }

            super.onPostExecute(result);
        }

        private void bluetoothSetup() {
            // Initialize
            bluetoothPort = BluetoothPort.getInstance();
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                // Dispositivo no soporta Bluetooth
                return;
            }
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 9 total pages.
            return 9;
        }


    }

}