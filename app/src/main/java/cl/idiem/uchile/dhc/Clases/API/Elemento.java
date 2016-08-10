package cl.idiem.uchile.dhc.Clases.API;

import java.util.ArrayList;

/**
 * Created by jose.becerra on 02-03-2016.
 */
public class Elemento {

    String nombre;
    String tipo;
    ArrayList<String> datos = new ArrayList<>();
    String padre;
    int estado;


    public Elemento() {
    }

    public Elemento( String nombre) {
        this.nombre = nombre;
    }

    public Elemento( String nombre, ArrayList<String> datos, int estado ) {
        this.nombre = nombre;
        this.datos = datos;
        this.estado = estado;
    }

    public ArrayList<String> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<String> datos) {
        this.datos = datos;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPadre() {
        return padre;
    }

    public void setPadre(String padre) {
        this.padre = padre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "{'Elemento':{" +
                "'nombre':'" + nombre + '\'' +
                ", 'tipo':'" + tipo + '\'' +
                ", 'datos':" + datos +
                ", 'padre':'" + padre + '\'' +
                ", 'estado':" + estado +
                "}}";
    }
}
