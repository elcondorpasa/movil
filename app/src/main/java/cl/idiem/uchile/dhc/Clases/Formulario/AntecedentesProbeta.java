package cl.idiem.uchile.dhc.Clases.Formulario;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jose.becerra on 07-03-2016.
 */
public class AntecedentesProbeta {

    private String probEmpleada;
    private String otraProbeta;
    private int cantProbeta;
    private Map<String,String> edades = new HashMap<String,String>();
    private Map<String,String> moldes = new HashMap<>();
    private String curadoProb;
    private Double tempCiba;
    private int numCiba;
    private String otroCurado;
    private String sinProbeta;

    //<editor-fold desc="Getters & Setters">
    public AntecedentesProbeta() {
    }

    public String getProbEmpleada() {
        return probEmpleada;
    }

    public void setProbEmpleada(String probEmpleada) {
        this.probEmpleada = probEmpleada;
    }

    public String getCuradoProb() {
        return curadoProb;
    }

    public void setCuradoProb(String curadoProb) {
        this.curadoProb = curadoProb;
    }

    public int getCantProbeta() {
        return cantProbeta;
    }

    public void setCantProbeta(int cantProbeta) {
        this.cantProbeta = cantProbeta;
    }

    public String getOtraProbeta() {
        return otraProbeta;
    }

    public void setOtraProbeta(String otraProbeta) {
        this.otraProbeta = otraProbeta;
    }

    public String getOtroCurado() {
        return otroCurado;
    }

    public void setOtroCurado(String otroCurado) {
        this.otroCurado = otroCurado;
    }

    public Map<String, String> getEdades() {
        return edades;
    }

    public void setEdades(Map<String, String> edades) {
        this.edades = edades;
    }

    public Map<String, String> getMoldes() {
        return moldes;
    }

    public void setMoldes(Map<String, String> moldes) {
        this.moldes = moldes;
    }

    public String getSinProbeta() {
        return sinProbeta;
    }

    public void setSinProbeta(String sinProbeta) {
        this.sinProbeta = sinProbeta;
    }

    public Double getTempCiba() {
        return tempCiba;
    }

    public void setTempCiba(Double tempCiba) {
        this.tempCiba = tempCiba;
    }

    public int getNumCiba() {
        return numCiba;
    }

    public void setNumCiba(int numCiba) {
        this.numCiba = numCiba;
    }

    //</editor-fold>

    @Override
    public String toString() {
        JSONObject edadesJson = new JSONObject( edades );
        JSONObject moldesJson = new JSONObject(moldes);
        return "{'AntecedentesProbeta':{" +
                "'probEmpleada':'" + probEmpleada + '\'' +
                ", 'otraProbeta':'" + otraProbeta + '\'' +
                ", 'sinProbeta':'" + sinProbeta + '\'' +
                ", 'cantProbeta':" + cantProbeta +
                ", 'edades':" + edadesJson +
                ", 'moldes':" + moldesJson +
                ", 'curadoProb':'" + curadoProb + '\'' +
                ", 'tempCiba':" + tempCiba +
                ", 'numCiba':" + numCiba +
                ", 'otroCurado':'" + otroCurado + '\'' +
                "}}";
    }

}
