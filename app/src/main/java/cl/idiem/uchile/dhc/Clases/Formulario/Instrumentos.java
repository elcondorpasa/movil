package cl.idiem.uchile.dhc.Clases.Formulario;

/**
 * Created by Usuario on 22-07-2016.
 */
public class Instrumentos {
    private int ins_id;
    private int ins_usu_id;
    private String ins_flexometro;
    private String ins_termometro;
    private String ins_equipo_cono;
    private String ins_motor_vibrador;
    private String ins_sonda;

    public int getIns_id() {
        return ins_id;
    }

    public void setIns_id(int ins_id) {
        this.ins_id = ins_id;
    }

    public int getIns_usu_id() {
        return ins_usu_id;
    }

    public void setIns_usu_id(int ins_usu_id) {
        this.ins_usu_id = ins_usu_id;
    }

    public String getIns_flexometro() {
        return ins_flexometro;
    }

    public void setIns_flexometro(String ins_flexometro) {
        this.ins_flexometro = ins_flexometro;
    }

    public String getIns_termometro() {
        return ins_termometro;
    }

    public void setIns_termometro(String ins_termometro) {
        this.ins_termometro = ins_termometro;
    }

    public String getIns_equipo_cono() {
        return ins_equipo_cono;
    }

    public void setIns_equipo_cono(String ins_equipo_cono) {
        this.ins_equipo_cono = ins_equipo_cono;
    }

    public String getIns_motor_vibrador() {
        return ins_motor_vibrador;
    }

    public void setIns_motor_vibrador(String ins_motor_vibrador) {
        this.ins_motor_vibrador = ins_motor_vibrador;
    }

    public String getIns_sonda() {
        return ins_sonda;
    }

    public void setIns_sonda(String ins_sonda) {
        this.ins_sonda = ins_sonda;
    }

    @Override
    public String toString() {
        return '{' +
                "'ins_id':" + ins_id +
                ", 'ins_usu_id':" + ins_usu_id +
                ", 'ins_flexometro':" + ins_flexometro +
                ", 'ins_termometro':" + ins_termometro +
                ", 'ins_equipo_cono':" + ins_equipo_cono +
                ", 'ins_motor_vibrador':" + ins_motor_vibrador +
                ", 'ins_sonda':" + ins_sonda +
                '}';
    }
}
