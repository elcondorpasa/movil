package cl.idiem.uchile.dhc.Clases.Formulario;

/**
 * Created by jose.becerra on 07-03-2016.
 */

/**
 * Clase que crea el objeto EquipoUtilizado con sus respectivos datos
 */
public class EquipoUtilizado {

    private String cono;
    private String termometro;
    private String flexometro;
    private String motorVibra;
    private String sonda;

    public EquipoUtilizado() {
    }

    //<editor-fold desc="Getters & Setters">
    public String getCono() {
        return cono;
    }

    public void setCono(String cono) {
        this.cono = cono;
    }

    public String getTermometro() {
        return termometro;
    }

    public void setTermometro(String termometro) {
        this.termometro = termometro;
    }

    public String getFlexometro() {
        return flexometro;
    }

    public void setFlexometro(String flexometro) {
        this.flexometro = flexometro;
    }

    public String getMotorVibra() {
        return motorVibra;
    }

    public void setMotorVibra(String motorVibra) {
        this.motorVibra = motorVibra;
    }

    public String getSonda() {
        return sonda;
    }

    public void setSonda(String sonda) {
        this.sonda = sonda;
    }
    //</editor-fold>
    /**
     * Método para crear el json del objeto con sus respectivos datos
     * @return el json de {@Link #EquipoUtilizado}
     */

    @Override
    public String toString() {
        return "{'EquipoUtilizado':{" +
                "'cono':'" + cono + '\'' +
                ", 'termometro':'" + termometro + '\'' +
                ", 'huincha':'" + flexometro + '\'' +
                ", 'motorVibra':'" + motorVibra + '\'' +
                ", 'sonda':'" + sonda + '\'' +
                "}}";
    }
}
