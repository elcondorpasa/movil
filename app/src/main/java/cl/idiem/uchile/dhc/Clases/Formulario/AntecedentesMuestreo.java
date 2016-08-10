package cl.idiem.uchile.dhc.Clases.Formulario;


/**
 * Created by Joe-Xidu on 06-03-2016.
 */
public class AntecedentesMuestreo {

    private String dateMuestreo;
    private String timeMuestreo;
    private String timeCamion;
    private Double tempAmb;
    private Double tempMezcla;
    private Double medCono1;
    private Double medCono2;
    private String asentamiento;
    private String compactacion;
    private String condClima;
    private String extraeA;
    private String mixero;
    private String horaDescarga;
    private boolean controlCono;

    public AntecedentesMuestreo() {
    }

    //<editor-fold desc="Getters & Setters">
    public String getDateMuestreo() {
        return dateMuestreo;
    }

    public void setDateMuestreo(String dateMuestreo) {
        this.dateMuestreo = dateMuestreo;
    }

    public String getTimeMuestreo() {
        return timeMuestreo;
    }

    public void setTimeMuestreo(String timeMuestreo) {
        this.timeMuestreo = timeMuestreo;
    }

    public Double getTempAmb() {
        return tempAmb;
    }

    public void setTempAmb(Double tempAmb) {
        this.tempAmb = tempAmb;
    }

    public Double getTempMezcla() {
        return tempMezcla;
    }

    public void setTempMezcla(Double tempMezcla) {
        this.tempMezcla = tempMezcla;
    }

    public String getAsentamiento() {
        return asentamiento;
    }

    public void setAsentamiento(String asentamiento) {
        this.asentamiento = asentamiento;
    }

    public String getCompactacion() {
        return compactacion;
    }

    public void setCompactacion(String compactacion) {
        this.compactacion = compactacion;
    }

    public String getCondClima() {
        return condClima;
    }

    public void setCondClima(String condClima) {
        this.condClima = condClima;
    }

    public String getExtraeA() {
        return extraeA;
    }

    public void setExtraeA(String extraeA) {
        this.extraeA = extraeA;
    }

    public String getMixero() {
        return mixero;
    }

    public void setMixero(String mixero) {
        this.mixero = mixero;
    }

    public String getHoraDescarga() {
        return horaDescarga;
    }

    public void setHoraDescarga(String horaDescarga) {
        this.horaDescarga = horaDescarga;
    }

    public String getTimeCamion() {
        return timeCamion;
    }

    public void setTimeCamion(String timeCamion) {
        this.timeCamion = timeCamion;
    }

    public Double getMedCono1() {
        return medCono1;
    }

    public void setMedCono1(Double medCono1) {
        this.medCono1 = medCono1;
    }

    public Double getMedCono2() {
        return medCono2;
    }

    public void setMedCono2(Double medCono2) {
        this.medCono2 = medCono2;
    }

    public boolean isControlCono() {
        return controlCono;
    }

    public void setControlCono(boolean controlCono) {
        this.controlCono = controlCono;
    }

    //</editor-fold>

    @Override
    public String toString() {
        return "{'AntecedentesMuestreo':{" +
                "'dateMuestreo':'" + dateMuestreo + '\'' +
                ", 'timeMuestreo':'" + timeMuestreo + '\'' +
                ", 'timeCamion':'" + timeCamion + '\'' +
                ", 'tempAmb':" + tempAmb +
                ", 'tempMezcla':" + tempMezcla +
                ", 'asentamiento':'" + asentamiento + '\'' +
                ", 'medCono1':" + medCono1 +
                ", 'medCono2':" + medCono2 +
                ", 'compactacion':'" + compactacion + '\'' +
                ", 'condClima':'" + condClima + '\'' +
                ", 'extraeA':'" + extraeA + '\'' +
                ", 'mixero':'" + mixero + '\'' +
                ", 'horaDescarga':'" + horaDescarga + '\'' +
                ", controlCono=" + controlCono +
                "}}";
    }

}
