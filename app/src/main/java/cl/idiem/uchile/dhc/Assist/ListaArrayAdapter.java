package cl.idiem.uchile.dhc.Assist;


/**
 * Created by jose.becerra on 15-03-2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cl.idiem.uchile.dhc.Clases.API.Elemento;
import cl.idiem.uchile.dhc.Clases.API.MuestraListado;
import cl.idiem.uchile.dhc.R;

import java.util.List;

public class ListaArrayAdapter extends ArrayAdapter<MuestraListado> {

    public ListaArrayAdapter(Context context, List<MuestraListado> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater)getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View listItemView = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo con custom_list_item.xml
            listItemView = inflater.inflate(
                    R.layout.custom_list_item,
                    parent,
                    false);
        }

        //Obteniendo instancias de los elementos
        TextView titulo = (TextView)listItemView.findViewById(R.id.numMuestra);
        TextView subtitulo = (TextView)listItemView.findViewById(R.id.data);
        ImageView categoria = (ImageView)listItemView.findViewById(R.id.state);


        //Obteniendo instancia de la Tarea en la posición actual
        MuestraListado item = getItem(position);

        titulo.setText(item.getDataNumInterno());
        subtitulo.setText(item.getDatos().toString());
        categoria.setImageResource(item.getEstado());

        //Devolver al ListView la fila creada
        return listItemView;

    }
}