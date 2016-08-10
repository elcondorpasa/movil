package cl.idiem.uchile.dhc.Clases.Formulario;

/**
 * Created by jose.becerra on 07-03-2016.
 */
/**
 * Clase que crea el objeto OtrosEnsayosInSitu con sus respectivos datos
 */
public class OtrosEnsayosInSitu {

    private Double densidadHorFresco;
    private String balanza;
    private Double medidaVolumetrica;
    private Double peso;
    private Double contAire;
    private String aerimetro;
    private Double peso2;
    private String maquinaVolumetrica;

    public String getAerimetro() {
        return aerimetro;
    }

    public void setAerimetro(String aerimetro) {
        this.aerimetro = aerimetro;
    }

    public OtrosEnsayosInSitu() {
    }

    //<editor-fold desc="Getters & Setters">
    public Double getDensidadHorFresco() {
        return densidadHorFresco;
    }

    public void setDensidadHorFresco(Double densidadHorFresco) {
        this.densidadHorFresco = densidadHorFresco;
    }

    public String getMaquinaVolumetrica() {
        return maquinaVolumetrica;
    }

    public void setMaquinaVolumetrica(String maquinaVolumetrica) {
        this.maquinaVolumetrica = maquinaVolumetrica;
    }

    public String getBalanza() {
        return balanza;
    }

    public void setBalanza(String balanza) {
        this.balanza = balanza;
    }

    public Double getMedidaVolumetrica() {
        return medidaVolumetrica;
    }

    public void setMedidaVolumetrica(Double medidaVolumetrica) {
        this.medidaVolumetrica = medidaVolumetrica;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Double getPeso2() {
        return peso2;
    }

    public void setPeso2(Double peso2) {
        this.peso2 = peso2;
    }

    public Double getContAire() {
        return contAire;
    }

    public void setContAire(Double contAire) {
        this.contAire = contAire;
    }
    //</editor-fold>

    /**
     * Método para crear el json del objeto con sus respectivos datos
     * @return el json de {@Link #OtrosEnsayosInSitu}
     */
    @Override
    public String toString() {
        return "{'OtrosEnsayosInSitu':{" +
                "'densidadHorFresco':" + densidadHorFresco +
                ", 'balanza':" + balanza +
                ", 'medidaVolumetrica':" + medidaVolumetrica +
                ", 'peso':" + peso +
                ", 'contAire':" + contAire +
                ", 'aerimetro':" + aerimetro +
                "}}";
    }
}
