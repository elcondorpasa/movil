package cl.idiem.uchile.dhc.Assist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertView {

    public static void showError( String message, Context ctx ) {
        showAlert( "Error", message, ctx );
    }

    public static void showAlert( String message, Context ctx ) {
        showAlert( "Alerta", message, ctx );
    }

    public static void showAlert( String title, String message, Context ctx ) {

        //Crea un builder
        AlertDialog.Builder builder = new AlertDialog.Builder( ctx );
        builder.setTitle( title );
        builder.setMessage( message );
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        //agrega buttons y listener
        EmptyListener pl = new EmptyListener();
        builder.setPositiveButton( "OK", pl );

        //Create the dialog
        AlertDialog ad = builder.create();

        //mostrar
        ad.show();
    }

}

class EmptyListener implements DialogInterface.OnClickListener {

    @Override
    public void onClick( DialogInterface dialog, int which ) { }

}