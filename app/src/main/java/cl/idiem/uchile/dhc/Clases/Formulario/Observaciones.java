package cl.idiem.uchile.dhc.Clases.Formulario;

/**
 * Created by jose.becerra on 07-03-2016.
 */
public class Observaciones {

    private String obsMuestreador;
    private String obsEncargado;
    private String nomEncargado;
    private String comentarios;
    private boolean anulaBoleta;
    private String motivoAnular;



    public Observaciones() {
    }

    //<editor-fold desc="Getters & Setters">

    public String getObsMuestreador() {
        return obsMuestreador;
    }

    public void setObsMuestreador(String obsMuestreador) {
        this.obsMuestreador = obsMuestreador;
    }

    public String getObsEncargado() {
        return obsEncargado;
    }

    public void setObsEncargado(String obsEncargado) {
        this.obsEncargado = obsEncargado;
    }

    public String getNomEncargado() {
        return nomEncargado;
    }

    public void setNomEncargado(String nomEncargado) {
        this.nomEncargado = nomEncargado;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public boolean isAnulaBoleta() {
        return anulaBoleta;
    }

    public void setAnulaBoleta(boolean anulaBoleta) {
        this.anulaBoleta = anulaBoleta;
    }

    public String getMotivoAnular() {
        return motivoAnular;
    }

    public void setMotivoAnular(String motivoAnular) {
        this.motivoAnular = motivoAnular;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "{'Observaciones':{" +
                "'obsMuestreador':'" + obsMuestreador + '\'' +
                ", 'obsEncargado':'" + obsEncargado + '\'' +
                ", 'nomEncargado':'" + nomEncargado + '\'' +
                ", 'comentarios':'" + comentarios + '\'' +
                ", 'anulaBoleta':" + anulaBoleta + '\'' +
                ", 'motivoAnular':" + motivoAnular +
                "}}";
    }
}
