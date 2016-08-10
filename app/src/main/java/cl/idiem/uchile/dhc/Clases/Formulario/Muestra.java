package cl.idiem.uchile.dhc.Clases.Formulario;


/**
 * Created by jose.becerra on 01-03-2016.
 */
public class Muestra {

    private boolean viajePerdido;
    private int id_prog;
    private int numInterno;
    private int numMuestra;
    private int codObra;
    private String detalleObra;
    private String direccion;
    private String comuna;
    private String contratista;
    private String solicitante;
    private String horaIn;
    private String horaOut;
    private double lat;
    private double lon;
    private String image;
    private String firma;
    private String horaTermino;


    public Muestra() {
    }

    //<editor-fold desc="Getters & Setters">
    public boolean isViajePerdido() {
        return viajePerdido;
    }

    public void setViajePerdido(boolean viajePerdido) {
        this.viajePerdido = viajePerdido;
    }

    public int getNumInterno() {
        return numInterno;
    }

    public void setNumInterno(int numInterno) {
        this.numInterno = numInterno;
    }

    public int getNumMuestra() {
        return numMuestra;
    }

    public void setNumMuestra(int numMuestra) {
        this.numMuestra = numMuestra;
    }

    public int getCodObra() {
        return codObra;
    }

    public void setCodObra(int codObra) {
        this.codObra = codObra;
    }

    public String getDetalleObra() {
        return detalleObra;
    }

    public void setDetalleObra(String detalleObra) {
        this.detalleObra = detalleObra;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getHoraIn() {
        return horaIn;
    }

    public void setHoraIn(String horaIn) {
        this.horaIn = horaIn;
    }

    public String getHoraOut() {
        return horaOut;
    }

    public void setHoraOut(String horaOut) {
        this.horaOut = horaOut;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirma() {
        return firma;
    }

    public void setFirma(String firma) {
        this.firma = firma;
    }

    public int getIdProg() {
        return id_prog;
    }

    public void setIdProg(int id_prog) {
        this.id_prog = id_prog;
    }

    public String getHoraTermino() {
        return horaTermino;
    }

    public void setHoraTermino(String horaTermino) {
        this.horaTermino = horaTermino;
    }

    //</editor-fold>

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Muestra muestra = (Muestra) o;

        return numInterno == muestra.numInterno;

    }

    @Override
    public int hashCode() {
        return numInterno;
    }

    @Override
    public String toString() {
        return "{'Muestra':{" +
                "'viajePerdido':" + viajePerdido +
                ", 'id_prog':" + id_prog +
                ", 'numInterno':" + numInterno +
                ", 'numMuestra':" + numMuestra +
                ", 'codObra':" + codObra +
                ", 'detalleObra':'" + detalleObra + '\'' +
                ", 'direccion':'" + direccion + '\'' +
                ", 'comuna':'" + comuna + '\'' +
                ", 'contratista':'" + contratista + '\'' +
                ", 'solicitante':'" + solicitante + '\'' +
                ", 'horaIn':'" + horaIn + '\'' +
                ", 'horaOut':'" + horaOut + '\'' +
                ", 'horaTermino':'" + horaTermino + '\'' +
                ", 'lat'=" + lat +
                ", 'lon'=" + lon +
                ", 'image':'" + image + '\'' +
                ", 'firma':'" + firma + '\'' +
                "}}";
    }

    public String toStringNoImg() {
        return "{'Muestra':{" +
                "'viajePerdido':" + viajePerdido +
                ", 'id_prog':" + id_prog +
                ", 'numInterno':" + numInterno +
                ", 'numMuestra':" + numMuestra +
                ", 'codObra':" + codObra +
                ", 'detalleObra':'" + detalleObra + '\'' +
                ", 'direccion':'" + direccion + '\'' +
                ", 'comuna':'" + comuna + '\'' +
                ", 'contratista':'" + contratista + '\'' +
                ", 'solicitante':'" + solicitante + '\'' +
                ", 'horaIn':'" + horaIn + '\'' +
                ", 'horaOut':'" + horaOut + '\'' +
                ", 'horaTermino':'" + horaTermino + '\'' +
                ", 'lat'=" + lat +
                ", 'lon'=" + lon +
                "}}";
    }
}
