package cl.idiem.uchile.dhc.Clases.Formulario;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cl.idiem.uchile.dhc.Globals;
import cl.idiem.uchile.dhc.LoginActivity;
import cl.idiem.uchile.dhc.MainActivity;

/**
 * Created by jose.becerra on 25-04-2016.
 */
public class ListaTalonarios {

    private int tecnico;
    private Context context;
    private ArrayList<Talonario> talonarios = new ArrayList<Talonario>();

    public ListaTalonarios() {
    }
    public ListaTalonarios( int tecnico, Context context ) {
        this.tecnico = tecnico;
        this.context = context;
    }

    public void getTalonariosFromFile( final int tecnico, final Context context ) {

        String sFichero;
        File fichero;
        Talonario talonario;
        int i = 0;
        boolean flag = false;
        this.talonarios.clear();
        sFichero = "storage/emulated/0/Archivos Formulario DHC/Talonarios//Talonario_" + i + ".txt";
        fichero = new File(sFichero);

        while (fichero.exists()){
            //fichero.delete();
            talonario =  new Talonario();
            if(talonario.setTalonario( i )) {
                if (!talonario.isEmpty()) {
                    this.talonarios.add(talonario);
                    flag = true;
                }
            }
            i++;

            sFichero = "storage/emulated/0/Archivos Formulario DHC/Talonarios//Talonario_" + i + ".txt";
            fichero = new File(sFichero);
        }

        if ( !flag)
            this.getTalonariosFromAPI(tecnico, context);
    }

    public void getTalonariosFromAPI( final int tecnico, final Context context ) {
        talonarios.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Globals.getURL() + "talonarios/" + tecnico,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Talonario talonario;
                        JSONObject json;
                        JSONArray jsonArray;
                        try {
                            jsonArray = new JSONArray( response );

                            for (int i = 0; i < jsonArray.length(); i++) {
                                talonario = new Talonario();
                                try {
                                    json = jsonArray.getJSONObject(i);
                                    try {
                                        talonario.setTecnico(json.getInt("tecnico"));
                                        talonario.setId(json.getInt("id"));
                                        talonario.setStart(json.getInt("start"));
                                        talonario.setCurrent(json.getInt("current"));
                                        talonario.setEnd(json.getInt("end"));
                                        talonario.saveTalonario(i);
                                        talonarios.add(talonario);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                        Toast.makeText(context, "No se pudieron obtener los talonarios", Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("tecnico", String.valueOf(tecnico));
                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public Talonario getCurrentTalonario() {
        for (int i = 0; i < this.talonarios.size(); i++) {
            if (!talonarios.get(i).isEmpty()) {
                return this.talonarios.get(i);
            }
        }
        return null;
    }

    public ArrayList<Talonario> getTalonarios() {
        return talonarios;
    }

}
