package cl.idiem.uchile.dhc.Clases.API;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by jose.becerra on 16-03-2016.
 */

public class MuestraListado implements Comparator<MuestraListado>, Comparable<MuestraListado>{

    private int numInterno;
    private int id;
    private String dataNumInterno;
    ArrayList<String> datos = new ArrayList<String>();
    private int obraId;
    private String codigoObra;
    private String correlativoPetreos;
    private int numPedidoPetreos;
    private String nombreObra;
    private String comuna;
    private String contratista;
    private String solicitante;
    private String destinatario;
    private String direccionObra;
    private String edadesEnsayo;
    private String tipoProbeta;
    private String tipoAsentamiento;
    private String gradoTipo;
    private Double latitud;
    private Double longitud;
    private int cantMuestras;
    private int estado;
    private int frecuencia;
    private String prioridad;
    private String observacion;
    private String imagen;

    public MuestraListado() {
    }

    public MuestraListado( String dataNumInterno) {
        this.dataNumInterno = dataNumInterno;
    }


    public MuestraListado( String dataNumInterno, ArrayList<String> datos, int estado ) {
        this.dataNumInterno = dataNumInterno;
        this.datos = datos;
        this.estado = estado;
    }

    //<editor-fold desc="Getters & Setters">


    public int getNumInterno() {
        return numInterno;

    }

    public void setNumInterno(int numInterno) {
        this.numInterno = numInterno;
    }

    public String getDataNumInterno() {
        return dataNumInterno;
    }

    public void setDataNumInterno(String dataNumInterno) {
        this.dataNumInterno = dataNumInterno;
    }

    public int getObraId() {
        return obraId;
    }

    public ArrayList<String> getDatos() {
        return datos;
    }

    public void setDatos(ArrayList<String> datos) {
        this.datos = datos;
    }

    public void setObraId(int obraId) {
        this.obraId = obraId;
    }

    public String getCodigoObra() {
        return codigoObra;
    }

    public void setCodigoObra(String codigoObra) {
        this.codigoObra = codigoObra;
    }

    public String getCorrelativoPetreos() {
        return correlativoPetreos;
    }

    public void setCorrelativoPetreos(String correlativoPetreos) {
        this.correlativoPetreos = correlativoPetreos;
    }

    public int getNumPedidoPetreos() {
        return numPedidoPetreos;
    }

    public void setNumPedidoPetreos(int numPedidoPetreos) {
        this.numPedidoPetreos = numPedidoPetreos;
    }

    public String getNombreObra() {
        return nombreObra;
    }

    public void setNombreObra(String nombreObra) {
        this.nombreObra = nombreObra;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getContratista() {
        return contratista;
    }

    public void setContratista(String contratista) {
        this.contratista = contratista;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getDireccionObra() {
        return direccionObra;
    }

    public void setDireccionObra(String direccionObra) {
        this.direccionObra = direccionObra;
    }

    public String getEdadesEnsayo() {
        return edadesEnsayo;
    }

    public void setEdadesEnsayo(String edadesEnsayo) {
        this.edadesEnsayo = edadesEnsayo;
    }

    public String getTipoProbeta() {
        return tipoProbeta;
    }

    public void setTipoProbeta(String tipoProbeta) {
        this.tipoProbeta = tipoProbeta;
    }

    public String getTipoAsentamiento() {
        return tipoAsentamiento;
    }

    public void setTipoAsentamiento(String tipoAsentamiento) {
        this.tipoAsentamiento = tipoAsentamiento;
    }

    public String getGradoTipo() {
        return gradoTipo;
    }

    public void setGradoTipo(String gradoTipo) {
        this.gradoTipo = gradoTipo;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public int getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(int frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getCantMuestras() {
        return cantMuestras;
    }

    public void setCantMuestras(int cantMuestras) {
        this.cantMuestras = cantMuestras;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    //</editor-fold>


    @Override
    public String toString() {
        return "MuestraListado{" +
                "numInterno=" + numInterno +
                ", id=" + id +
                ", dataNumInterno='" + dataNumInterno + '\'' +
                ", datos=" + datos +
                ", obraId=" + obraId +
                ", codigoObra='" + codigoObra + '\'' +
                ", correlativoPetreos='" + correlativoPetreos + '\'' +
                ", numPedidoPetreos=" + numPedidoPetreos +
                ", nombreObra='" + nombreObra + '\'' +
                ", comuna='" + comuna + '\'' +
                ", contratista='" + contratista + '\'' +
                ", solicitante='" + solicitante + '\'' +
                ", destinatario='" + destinatario + '\'' +
                ", direccionObra='" + direccionObra + '\'' +
                ", edadesEnsayo='" + edadesEnsayo + '\'' +
                ", tipoProbeta='" + tipoProbeta + '\'' +
                ", tipoAsentamiento='" + tipoAsentamiento + '\'' +
                ", gradoTipo='" + gradoTipo + '\'' +
                ", latitud=" + latitud +
                ", longitud=" + longitud +
                ", estado=" + estado +
                ", cantMuestras=" + cantMuestras +
                ", frecuencia=" + frecuencia +
                ", prioridad='" + prioridad + '\'' +
                ", observacion='" + observacion + '\'' +
                '}';
    }

    @Override
    public int compareTo(MuestraListado muestra) {

        return (this.dataNumInterno).compareTo( muestra.dataNumInterno );
    }

    @Override
    public int compare(MuestraListado m1, MuestraListado m2) {
        return m1.numInterno - m2.numInterno;
    }
}
