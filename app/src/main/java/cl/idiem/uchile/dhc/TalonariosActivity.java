package cl.idiem.uchile.dhc;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import cl.idiem.uchile.dhc.Assist.ItemTalonarioAdapter;
import cl.idiem.uchile.dhc.Clases.Formulario.Talonario;

public class TalonariosActivity extends AppCompatActivity {

    private ItemTalonarioAdapter adapter;
    private ListView list;
    private Globals globals;
    private Talonario talonario;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talonarios);
        list = (ListView) findViewById(R.id.ListView01);
        mProgressView = findViewById(R.id.load_progress);
        globals = (Globals)getApplicationContext();
        adapter = new ItemTalonarioAdapter(this, globals.getTalonarios().getTalonarios());
        list.setAdapter( adapter );

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String data = "";
                talonario = globals.getTalonarios().getTalonarios().get(position);
                AlertDialog alertDialog = new AlertDialog.Builder(TalonariosActivity.this).create();
                alertDialog.setIcon(android.R.drawable.ic_search_category_default);
                alertDialog.setTitle("Detalle Talonario " + talonario.getId());
                data += "Inicio: " + talonario.getStart() + "\n";
                data += "Término: " + talonario.getEnd() + "\n";
                data += "Uso: " + talonario.getCurrentPercentage() + "%\n";
                data += "Próximo: " + talonario.getNext() + "\n";
                alertDialog.setMessage(data);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Aceptar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
                globals.getTalonarios().getTalonariosFromAPI(globals.getInspector(), getApplicationContext());
                adapter = new ItemTalonarioAdapter(getApplicationContext(), globals.getTalonarios().getTalonarios());
                list.setAdapter(adapter);
            }
        });
    }


}
