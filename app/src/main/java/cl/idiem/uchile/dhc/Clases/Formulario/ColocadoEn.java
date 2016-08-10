package cl.idiem.uchile.dhc.Clases.Formulario;

/**
 * Created by jose.becerra on 04-03-2016.
 */
public class ColocadoEn {

    private String tipoEdificio;
    private String elemento;
    private String ubicacion;
    private String eje;

    public ColocadoEn() {
    }

    //<editor-fold desc="Getters & Setters">
    public String getTipoEdificio() {
        return tipoEdificio;
    }

    public void setTipoEdificio(String tipoEdificio) {
        this.tipoEdificio = tipoEdificio;
    }

    public String getElemento() {
        return elemento;
    }

    public void setElemento(String elemento) {
        this.elemento = elemento;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEje() {
        return eje;
    }

    public void setEje(String eje) {
        this.eje = eje;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "{'ColocadoEn':{" +
                "'tipoEdificio':'" + tipoEdificio + '\'' +
                ", 'elemento':'" + elemento + '\'' +
                ", 'ubicacion':'" + ubicacion + '\'' +
                ", 'eje':'" + eje + '\'' +
                "}}";
    }
}
