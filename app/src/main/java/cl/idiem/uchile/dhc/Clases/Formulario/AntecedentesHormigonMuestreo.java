package cl.idiem.uchile.dhc.Clases.Formulario;

/**
 * Created by jose.becerra on 01-03-2016.
 */
public class AntecedentesHormigonMuestreo extends Muestra{

    private String confeccionado;
    private String numCamion;
    private int numGuia;
    private double cantiM3;
    private String planta;
    private String codProducto;
    private String gradoTipo;
    private String plantaOut;
    private String aditivo;
    private String otroAditivo;
    private String aditivo2;
    private String otroAditivo2;
    private double cantidadAditivo;
    private String medidaAditivo;
    private String adicion;
    private int cantidadAgua;
    private String medidaAgua;
    private String otroAdicion;
    private String compactObra;
    private String otroCompactObra;

    public AntecedentesHormigonMuestreo() {
    }

    //<editor-fold desc="Getters & Setters">
    public String getConfeccionado() {
        return confeccionado;
    }

    public void setConfeccionado(String confeccionado) {
        this.confeccionado = confeccionado;
    }

    public String getNumCamion() {
        return numCamion;
    }

    public void setNumCamion(String numCamion) {
        this.numCamion = numCamion;
    }

    public int getNumGuia() {
        return numGuia;
    }

    public void setNumGuia(int numGuia) {
        this.numGuia = numGuia;
    }

    public double getCantiM3() {
        return cantiM3;
    }

    public void setCantiM3(double cantiM3) {
        this.cantiM3 = cantiM3;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getGradoTipo() {
        return gradoTipo;
    }

    public void setGradoTipo(String gradoTipo) {
        this.gradoTipo = gradoTipo;
    }

    public String getPlantaOut() {
        return plantaOut;
    }

    public void setPlantaOut(String plantaOut) {
        this.plantaOut = plantaOut;
    }

    public String getAditivo() {
        return aditivo;
    }

    public void setAditivo(String aditivo) {
        this.aditivo = aditivo;
    }

    public double getCantidadAditivo() {
        return cantidadAditivo;
    }

    public void setCantidadAditivo(double cantidadAditivo) {
        this.cantidadAditivo = cantidadAditivo;
    }

    public String getMedidaAditivo() {
        return medidaAditivo;
    }

    public void setMedidaAditivo(String medidaAditivo) {
        this.medidaAditivo = medidaAditivo;
    }

    public String getAdicion() {
        return adicion;
    }

    public void setAdicion(String adicion) {
        this.adicion = adicion;
    }

    public String getCompactObra() {
        return compactObra;
    }

    public void setCompactObra(String compactObra) {
        this.compactObra = compactObra;
    }

    public String getOtroAditivo() {
        return otroAditivo;
    }

    public void setOtroAditivo(String otroAditivo) {
        this.otroAditivo = otroAditivo;
    }

    public String getOtroAdicion() {
        return otroAdicion;
    }

    public void setOtroAdicion(String otroAdicion) {
        this.otroAdicion = otroAdicion;
    }

    public String getOtroCompactObra() {
        return otroCompactObra;
    }

    public void setOtroCompactObra(String otroCompactObra) {
        this.otroCompactObra = otroCompactObra;
    }

    public int getCantidadAgua() {
        return cantidadAgua;
    }

    public void setCantidadAgua(int cantidadAgua) {
        this.cantidadAgua = cantidadAgua;
    }

    public String getMedidaAgua() {
        return medidaAgua;
    }

    public void setMedidaAgua(String medidaAgua) {
        this.medidaAgua = medidaAgua;
    }

    public String getAditivo2() {
        return aditivo2;
    }

    public void setAditivo2(String aditivo2) {
        this.aditivo2 = aditivo2;
    }

    public String getOtroAditivo2() {
        return otroAditivo2;
    }

    public void setOtroAditivo2(String otroAditivo2) {
        this.otroAditivo2 = otroAditivo2;
    }

    //</editor-fold>

    @Override
    public String toString() {
        return "{'AntecedentesHormigonMuestreo':{" +
                "'confeccionado':'" + confeccionado + '\'' +
                ", 'numCamion':'" + numCamion + '\'' +
                ", 'numGuia':" + numGuia +
                ", 'cantiM3':" + cantiM3 +
                ", 'planta':'" + planta + '\'' +
                ", 'codProducto':'" + codProducto + '\'' +
                ", 'gradoTipo':'" + gradoTipo + '\'' +
                ", 'plantaOut':'" + plantaOut + '\'' +
                ", 'aditivo':'" + aditivo + '\'' +
                ", 'aditivo2':'" + aditivo2 + '\'' +
                ", 'otroAditivo':'" + otroAditivo + '\'' +
                ", 'otroAditivo2':'" + otroAditivo2 + '\'' +
                ", 'cantidadAditivo':" + cantidadAditivo +
                ", 'medidaAditivo':'" + medidaAditivo + '\'' +
                ", 'adicion':'" + adicion + '\'' +
                ", 'cantidadAgua':" + cantidadAgua +
                ", 'medidaAgua':'" + medidaAgua + '\'' +
                ", 'otroAdicion':'" + otroAdicion + '\'' +
                ", 'compactObra':'" + compactObra + '\'' +
                ", 'otroCompactObra':'" + otroCompactObra + '\'' +
                "}}";
    }
}
