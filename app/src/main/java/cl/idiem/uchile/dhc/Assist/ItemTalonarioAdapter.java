package cl.idiem.uchile.dhc.Assist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cl.idiem.uchile.dhc.Clases.Formulario.Talonario;
import cl.idiem.uchile.dhc.R;

/**
 * Created by jose.becerra on 29-04-2016.
 */
public class ItemTalonarioAdapter extends ArrayAdapter<Talonario> {
    public ItemTalonarioAdapter(Context context, ArrayList<Talonario> talonarios) {
        super(context, 0, talonarios);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con custom_list_item.xml
            listItemView = inflater.inflate(
                    R.layout.item_talonario,
                    parent,
                    false);
        }

        //Obteniendo instancias de los elementos
        TextView id = (TextView)listItemView.findViewById(R.id.idTalonario);
        TextView data = (TextView)listItemView.findViewById(R.id.data);


        //Obteniendo instancia de la Tarea en la posición actual
        Talonario talonario = getItem(position);

        id.setText("Talonario: " +talonario.getId());
        data.setText("Uso actual: " + talonario.getCurrentPercentage() + "%");

        //Devolver al ListView la fila creada
        return listItemView;
    }
}
