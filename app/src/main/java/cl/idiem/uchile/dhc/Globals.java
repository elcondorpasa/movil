package cl.idiem.uchile.dhc;

import android.app.Application;
import android.content.Context;
import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cl.idiem.uchile.dhc.Assist.GPSTracker;
import cl.idiem.uchile.dhc.Clases.API.Elemento;
import cl.idiem.uchile.dhc.Clases.API.MuestraListado;
import cl.idiem.uchile.dhc.Clases.Formulario.AntecedentesHormigonMuestreo;
import cl.idiem.uchile.dhc.Clases.Formulario.AntecedentesMuestreo;
import cl.idiem.uchile.dhc.Clases.Formulario.AntecedentesProbeta;
import cl.idiem.uchile.dhc.Clases.Formulario.ColocadoEn;
import cl.idiem.uchile.dhc.Clases.Formulario.EquipoUtilizado;
import cl.idiem.uchile.dhc.Clases.Formulario.Instrumentos;
import cl.idiem.uchile.dhc.Clases.Formulario.ListaTalonarios;
import cl.idiem.uchile.dhc.Clases.Formulario.Muestra;
import cl.idiem.uchile.dhc.Clases.Formulario.Observaciones;
import cl.idiem.uchile.dhc.Clases.Formulario.OtrosEnsayosInSitu;

/**
 * Clase encargada de administrarar todas las variables y constantes globales que estarán
 * disponibles durante toda la vida de la aplicación.
 *
 * @author José Becerra
 * @version 08/04/2016/A
 */

public class Globals extends Application {

    private static final String URL = "http://intranet.idiem.cl/Portal/apisdhc/public/dhc/";
    //private static final String URL = "http://172.17.56.61/Portal/apisdhc/public/dhc/";
    public static final String ACTION_RUN_ISERVICE = "cl.idiem.uchile.dhc.action.RUN_INTENT_SERVICE";
    public static final String ACTION_PROGRESS_EXIT = "cl.idiem.uchile.dhc.action.PROGRESS_EXIT";
    public static final String ACTION_RUN_ISERVICE2 = "cl.idiem.uchile.dhc.action.RUN_INTENT_SERVICE2";
    public static final String ACTION_PROGRESS_EXIT2 = "cl.idiem.uchile.dhc.action.PROGRESS_EXIT2";
    public static final String EXTRA_PROGRESS = "cl.idiem.uchile.dhc.extra.PROGRESS";
    public static final String DIR_STORAGE = "storage/emulated/0/Archivos Formulario DHC/";
    private static Globals mInstance;
    private String latitud;
    private String longitud;
    private Location mLastLocation;
    private ListaTalonarios talonarios = new ListaTalonarios();
    private ArrayList<Elemento> elementos = new ArrayList<>();
    private MuestraListado muestraActiva;
    private ArrayList<MuestraListado> listaMuestras = new ArrayList<>();
    private String addrPrinter;
    private boolean viajePerdido;


    private Muestra muestra = new Muestra();
    private AntecedentesHormigonMuestreo antecedentesHormigonMuestreo = new AntecedentesHormigonMuestreo();
    private ColocadoEn colocadoEn = new ColocadoEn();
    private AntecedentesMuestreo antecedentesMuestreo = new AntecedentesMuestreo();
    private AntecedentesProbeta antecedentesProbeta = new AntecedentesProbeta();
    private OtrosEnsayosInSitu otrosEnsayosInSitu = new OtrosEnsayosInSitu();
    private EquipoUtilizado equipoUtilizado = new EquipoUtilizado();
    private Observaciones observaciones = new Observaciones();
    private Map<String, Integer> estados = new HashMap<String, Integer>();
    //private Map<String, Instrumentos> instrumentos = new HashMap<String, Instrumentos>();
    private Instrumentos instrumentos = new Instrumentos();
    private int inspector;
    private int contadorLineaSelect;
    private String dir;
    private String imei;
    //<editor-fold desc="Antecedentes Hormigon Muestreoects">
    private ArrayList<String> confeccionados = new ArrayList<String>();
    private ArrayList<String> aditivos = new ArrayList<String>();
    private ArrayList<String> medidasAditivo = new ArrayList<String>();
    private ArrayList<String> adiciones = new ArrayList<String>();
    //</editor-fold>
    private ArrayList<String> compactsObra = new ArrayList<String>();
    //<editor-fold desc="Antecedentes Muestreo">
    private ArrayList<String> asentamientos = new ArrayList<String>();
    private ArrayList<String> compactaciones = new ArrayList<String>();
    //</editor-fold>
    private ArrayList<String> condsClima = new ArrayList<String>();
    //<editor-fold desc="AntecedentesProbeta">
    private ArrayList<String> curadosProb = new ArrayList<String>();
    private ArrayList<String> probEmpleadas = new ArrayList<String>();
    private ArrayList<String> sinProbetas = new ArrayList<String>();
    //</editor-fold>
    private int cantProbetas;
    //</editor-fold>
    //<editor-fold desc="ColocadoEn">
    private ArrayList<String> tiposEdif = new ArrayList<String>();

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public static String getURL() {
        return URL;
    }

    public static Globals getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance;
    }

    public int getInspector() {
        return inspector;
    }

    public void setInspector(int inspector) {
        this.inspector = inspector;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Instrumentos getInstrumentos() {
        return instrumentos;
    }

    public void setInstrumentos(Instrumentos instrumentos) {
        this.instrumentos = instrumentos;
    }

    public String getAddrPrinter() {
        return addrPrinter;
    }

    public void setAddrPrinter(String addrPrinter) {
        this.addrPrinter = addrPrinter;
    }

    public boolean getViajePerdido() {
        return viajePerdido;
    }

    public Muestra getMuestra() {
        return muestra;
    }

    public void setMuestra(Muestra muestra) {
        this.muestra = muestra;
    }

    public AntecedentesHormigonMuestreo getAntecedentesHormigonMuestreo() {
        return antecedentesHormigonMuestreo;
    }

    public void setAntecedentesHormigonMuestreo(AntecedentesHormigonMuestreo antecedentesHormigonMuestreo) {
        this.antecedentesHormigonMuestreo = antecedentesHormigonMuestreo;
    }

    public ColocadoEn getColocadoEn() {
        return colocadoEn;
    }

    public void setColocadoEn(ColocadoEn colocadoEn) {
        this.colocadoEn = colocadoEn;
    }

    public AntecedentesMuestreo getAntecedentesMuestreo() {
        return antecedentesMuestreo;
    }

    public void setAntecedentesMuestreo(AntecedentesMuestreo antecedentesMuestreo) {
        this.antecedentesMuestreo = antecedentesMuestreo;
    }

    public AntecedentesProbeta getAntecedentesProbeta() {
        return antecedentesProbeta;
    }

    public void setAntecedentesProbeta(AntecedentesProbeta antecedentesProbeta) {
        this.antecedentesProbeta = antecedentesProbeta;
    }

    public OtrosEnsayosInSitu getOtrosEnsayosInSitu() {
        return otrosEnsayosInSitu;
    }

    public void setOtrosEnsayosInSitu(OtrosEnsayosInSitu otrosEnsayosInSitu) {
        this.otrosEnsayosInSitu = otrosEnsayosInSitu;
    }

    public EquipoUtilizado getEquipoUtilizado() {
        return equipoUtilizado;
    }

    public void setEquipoUtilizado(EquipoUtilizado equipoUtilizado) {
        this.equipoUtilizado = equipoUtilizado;
    }

    public Observaciones getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(Observaciones observaciones) {
        this.observaciones = observaciones;
    }

    public ArrayList<Elemento> getElementos() {
        return elementos;
    }

    public void setElementos(ArrayList<Elemento> elementos) {
        this.elementos = elementos;
    }

    public MuestraListado getMuestraActiva() {
        return muestraActiva;
    }

    public void setMuestraActiva(MuestraListado muestraActiva) {
        this.muestraActiva = muestraActiva;
    }

    public boolean isViajePerdido() {
        return viajePerdido;
    }

    public void setViajePerdido(boolean viajePerdido) {
        this.viajePerdido = viajePerdido;
    }

    public ArrayList<MuestraListado> getListaMuestras() {
        return listaMuestras;
    }

    public void setListaMuestras(ArrayList<MuestraListado> listaMuestras) {
        this.listaMuestras = listaMuestras;
    }

    public Map<String, Integer> getEstados() {
        return estados;
    }

    public void setEstados(Map<String, Integer> estados) {
        this.estados = estados;
    }

    public int getContadorLineaSelect() {
        return contadorLineaSelect;
    }

    public void setContadorLineaSelect(int contadorLineaSelect) {
        this.contadorLineaSelect = contadorLineaSelect;
    }

    public ArrayList<String> getConfeccionados() {
        return confeccionados;
    }

    public void setConfeccionados(ArrayList<String> confeccionados) {
        this.confeccionados = confeccionados;
    }

    public ArrayList<String> getAditivos() {
        return aditivos;
    }

    public void setAditivos(ArrayList<String> aditivos) {
        this.aditivos = aditivos;
    }

    public ArrayList<String> getMedidasAditivo() {
        return medidasAditivo;
    }

    public void setMedidasAditivo(ArrayList<String> medidasAditivo) {
        this.medidasAditivo = medidasAditivo;
    }

    public ArrayList<String> getAdiciones() {
        return adiciones;
    }

    public void setAdiciones(ArrayList<String> adiciones) {
        this.adiciones = adiciones;
    }

    public ArrayList<String> getCompactsObra() {
        return compactsObra;
    }

    public void setCompactsObra(ArrayList<String> compactsObra) {
        this.compactsObra = compactsObra;
    }

    public ArrayList<String> getAsentamientos() {
        return asentamientos;
    }

    public void setAsentamientos(ArrayList<String> asentamientos) {
        this.asentamientos = asentamientos;
    }

    public ArrayList<String> getCompactaciones() {
        return compactaciones;
    }

    public void setCompactaciones(ArrayList<String> compactaciones) {
        this.compactaciones = compactaciones;
    }

    public ArrayList<String> getCondsClima() {
        return condsClima;
    }

    public void setCondsClima(ArrayList<String> condsClima) {
        this.condsClima = condsClima;
    }

    public ArrayList<String> getTiposEdif() {
        return tiposEdif;
    }

    public void setTiposEdif(ArrayList<String> tiposEdif) {
        this.tiposEdif = tiposEdif;
    }

    public ArrayList<String> getCuradosProb() {
        return curadosProb;
    }

    public void setCuradosProb(ArrayList<String> curadosProb) {
        this.curadosProb = curadosProb;
    }

    public ArrayList<String> getProbEmpleadas() {
        return probEmpleadas;
    }

    public void setProbEmpleadas(ArrayList<String> probEmpleadas) {
        this.probEmpleadas = probEmpleadas;
    }

    public int getCantProbetas() {
        return cantProbetas;
    }

    public void setCantProbetas(int cantProbetas) {
        this.cantProbetas = cantProbetas;
    }

    public ArrayList<String> getSinProbetas() {
        return sinProbetas;
    }

    public void setSinProbetas(ArrayList<String> sinProbetas) {
        this.sinProbetas = sinProbetas;
    }

    @Override
    public void onCreate() {
        mInstance = this;
        super.onCreate();
    }

    public ListaTalonarios getTalonarios() {
        return talonarios;
    }

    public void setTalonarios(ListaTalonarios talonarios) {
        this.talonarios = talonarios;
    }

   // Latitud y longitud
    public void setLocation() {
        GPSTracker gps = new GPSTracker(this);
        this.latitud = String.valueOf(gps.getLatitude());
        this.longitud = String.valueOf(gps.getLongitude());
    }

    public String getLatitud() {
        return latitud;
    }

    public String getLongitud() {
        return longitud;
    }

}



