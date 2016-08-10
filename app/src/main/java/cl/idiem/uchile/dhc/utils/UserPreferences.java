package cl.idiem.uchile.dhc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import cl.idiem.uchile.dhc.Globals;

/**
 * Created by daniela on 25-07-16.
 */
public class UserPreferences {

    public static void saveNumeroMuestra(int numMuestra, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Datos_User", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putInt("NumMuestra", numMuestra);
        prefsEditor.apply();
    }

    public static int getNumeroMuestra(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Datos_User", Context.MODE_PRIVATE);
        return preferences.getInt("NumMuestra", 0);
    }
}
