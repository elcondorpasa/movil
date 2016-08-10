package cl.idiem.uchile.dhc;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import cl.idiem.uchile.dhc.Assist.GPSTracker;

/**
 *
 * Clase encargada de administrarar la pantalla de logeo, el que se realiza con el rut y una contraseña.
 *
 * @author José Becerra
 * @version 08/04/2016/A
 *
 */

public class LoginActivity extends AppCompatActivity {

    /**
     * Administra si hay conexión o no con el servidor
     */
    boolean conexion = false;

    /**
     * Administra la cantidad de carácteres utilizar en la creación del archivo de respaldo
     */
    int contadorLinea;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private String android_id;
    private String imei;
    private String lat;
    private String lon;
    final Calendar calendar = Calendar.getInstance();
    final int mMonth = calendar.get(Calendar.MONTH);
    final int mDay = calendar.get(Calendar.DAY_OF_MONTH);
    final String fecha = Integer.toString(mDay).concat(Integer.toString(mMonth+1));

    /**
     *
     * Método que es llamado cuando se crea por primera vez la actividad.
     * <p/>
     * Aquí es donde se maneja el negocio de la actividad y genera las instancias necesarias
     * para mostrar la información correcta del login.
     *
     * @param savedInstanceState Paquete que contiene el estado previamente congelado de la actividad, si la hubo.
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Globals globals = (Globals)getApplicationContext();
        TelephonyManager message = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        globals.setImei(message.getDeviceId());
        globals.setLocation();
        lat = globals.getLatitud();
        lon = globals.getLongitud();
        imei = globals.getImei();
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = (AutoCompleteTextView) findViewById(R.id.user);

        String sFichero = "storage/emulated/0/Archivos Formulario DHC/Login/DHC_Login" + fecha + ".txt";
        File fichero = new File(sFichero);

        if(fichero.exists()){
            globals.setInspector( getTecnicoFromFile( fichero ) );
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mUserSignInButton = (Button) findViewById(R.id.user_sign_in_button);
        mUserSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
    private int getTecnicoFromFile( File file) {

        byte[] buffer = new byte[100];
        String str;
        JSONObject json;

        try {
            FileInputStream fis = new FileInputStream(file);
            fis.read(buffer);
            fis.close();
            str = new String(buffer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        try {
            json = new JSONObject(str);
            return json.getInt("usuario");
        } catch (JSONException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     *
     * Intenta identificarse con una cuenta especificada por el formulario de acceso.
     * Si hay errores en el formulario (usuario no válido, campos vacíos, etc.),
     * los errores se muestran y no se hace ningún intento de acceso real.
     *
     */

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = mUserView.getText().toString();
        String pass = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(pass) || !isPasswordValid(pass)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(String.valueOf(user)) || !isUserValid(user)) {
            mUserView.setError(getString(R.string.error_invalid_user));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            try {
                mAuthTask = new UserLoginTask(Integer.parseInt(user), pass);
            } catch ( NumberFormatException e ) {
                mAuthTask = new UserLoginTask(0, pass);
            }
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isUserValid(String user) {
        return user.length() > 6;
    }

    /**
     *
     * Shows the progress UI and hides the login form.
     *
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final int iUser;
        private final String sPass;

        UserLoginTask(int user, String pass) {
            iUser = user;
            sPass = pass;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                sendPost(iUser, sPass);
            } catch (Exception e) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                //se conecta
                if (conexion)
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                mAuthTask = null;
                showProgress(false);
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void sendPost( final int user, final String pass ){

        final Globals globals = (Globals) getApplication().getApplicationContext();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Globals.getURL() + "login",
                new Response.Listener<String>() {
                    View focusView = null;
                    JSONObject jsonResponse;
                    String sResponse;

                    @Override
                    public void onResponse(String response) {

                        try {
                            jsonResponse = new JSONObject( response );
                        } catch (JSONException e) {
                            jsonResponse = null;
                        }

                        try {
                            sResponse = jsonResponse.getString("response");
                        } catch (JSONException e) {
                            sResponse = "";
                        }

                        if ( sResponse.equals("conectado")) {
                            try {
                                jsonResponse = new JSONObject("{'usuario':'" +String.valueOf(user) +"'}");
                            } catch (JSONException e) {
                                jsonResponse = null;
                            }
                            globals.setInspector( user );
                            createFile(jsonResponse.toString());
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                            conexion = true;
                        }
                        else {
                            mAuthTask = null;
                            showProgress(false);
                            if (sResponse.equals("noPass")) {
                                mPasswordView.setError(getString(R.string.error_incorrect_password_post));
                                focusView = mPasswordView;
                            }

                            if (sResponse.equals("noUser")) {
                                mUserView.setError(getString(R.string.error_incorrect_user_post));
                                focusView = mUserView;
                            }
                            focusView.requestFocus();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                        Toast.makeText(getApplicationContext(), "No se pudo conectar con el servidor, intente nuevamente", Toast.LENGTH_LONG).show();
                        mAuthTask = null;
                        showProgress(false);
                    }
                }){

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();

                params.put("usuario", String.valueOf(user));
                params.put("pass", pass);
               /* params.put("imei", imei);
                params.put("latitud",lat);
                params.put("longitud",lon);*/

                return params;
            }
            public Map getHeaders() throws AuthFailureError {
                Map headers = new HashMap();
                headers.put("api_key", "$2y$10$jriZ2PDg8sPnPVJJ2xPD0ObFKYgHDHCTZ2wliVnx0ZF.1itLzFudi");
                return headers;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    private void createFile( String arr ) {

        File sdCard, directory;
        FileWriter file;

        try {
            if (Environment.getExternalStorageState().equals("mounted")) {

                sdCard = Environment.getExternalStorageDirectory();

                contadorLinea = arr.length();

                try {
                    directory = new File(sdCard.getAbsolutePath()
                            + "/Archivos Formulario DHC/Login");
                    directory.mkdirs();

                    file = new FileWriter(directory + "/DHC_Login" + fecha + ".txt");
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

