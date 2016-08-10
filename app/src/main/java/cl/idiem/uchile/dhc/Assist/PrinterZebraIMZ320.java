package cl.idiem.uchile.dhc.Assist;

import android.content.Context;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import cl.idiem.uchile.dhc.Clases.Formulario.AntecedentesHormigonMuestreo;
import cl.idiem.uchile.dhc.Clases.Formulario.AntecedentesMuestreo;
import cl.idiem.uchile.dhc.Clases.Formulario.ColocadoEn;
import cl.idiem.uchile.dhc.Clases.Formulario.Muestra;
import cl.idiem.uchile.dhc.Globals;

/**
 * Created by jose.becerra on 26-04-2016.
 */
public class PrinterZebraIMZ320 {

    public final static int PRINT_ALL = 0;
    public final static int PRINT_ONE = 1;
    private final static int MARGIN_LEFT = 15;
    private final static int POSITION_START = 10;
    private final static int POSITION_NEXT_TITLE = 40;
    private final static int MAX_LINE_LENGTH = 46;
    private final static int POSITION_NEXT_LINE = 25;
    private final static int PRINT_VOUCHER = 1;
    private final static int PRINT_TICKET = 2;
    private final static String COMMAND_INI = "^XA^POI";
    private final static String COMMAND_HEIGHT = "^LL";
    private final static String COMMAND_END = "^XZ";
    private final static String COMMAND_ZERO_CONFIG = "~JR";
    private final static String COMMAND_START_FIELD = "^FO";
    private final static String COMMAND_END_FIELD = "^FS";
    private final static String COMMAND_FONT_SIZE = "^ADN, 7, 3";
    private final static String COMMAND_TEXT = "^FD";
    private final static String COMMAND_BAR_CODE = "^B3,,100^FD";
    private final static String COMMAND_QR_CODE = "^BQN,2,10^FDMM,A";
    private final static String COMMAND_RESET_PROPERTIES = "^BY2,3";
    private final static String COMMAND_PRINT_QUANTITY = "^PQ";
    private int positionY = POSITION_START;
    private Connection printerConnection;
    private ZebraPrinter printer;
    private Context context;
    private int numMuestra;
    private int ticketQuantity;
    private Globals globals;
    private Muestra muestra;
    private AntecedentesHormigonMuestreo antecedentesHormigonMuestreo;
    private ColocadoEn colocadoEn;
    private AntecedentesMuestreo antecedentesMuestreo;

    public PrinterZebraIMZ320(Context context) {
        this.context = context;
        this.globals = (Globals) context;
    }

    public boolean setPrintVaucher(String macAddress, Muestra muestra, AntecedentesHormigonMuestreo antecedentesHormigonMuestreo, ColocadoEn colocadoEn, AntecedentesMuestreo antecedentesMuestreo ) {
        this.printer = connect( macAddress );
        this.numMuestra = muestra.getNumMuestra();
        if (this.printer != null) {
            sendLabel( PRINT_VOUCHER );
            return true;
        } else {
            disconnect();
            return false;
        }
    }

    public boolean setPrintTicket( String macAddress, int numMuestra, int quantity ) {
        this.printer = connect( macAddress );
        this.numMuestra = numMuestra;
        this.ticketQuantity = quantity;
        if (this.printer != null) {
            sendLabel( PRINT_TICKET );
            return true;
        } else {
            disconnect();
            return false;
        }
    }

    private void sendLabel( int which ) {
        try {
            byte[] configLabel;
            switch (which){
                case PRINT_VOUCHER:
                    configLabel = printVoucher();
                    break;
                case PRINT_TICKET:
                    configLabel = printTicket();
                    break;
                default:
                    configLabel = printVoucher();
                    break;
            }
            this.printerConnection.write(configLabel);
            if (this.printerConnection instanceof BluetoothConnection) {
                DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private byte[] printVoucher() {
        String url = "http://repositorio.idiem.cl/dir1/dir2/" + Utilitys.stringCrypt(String.valueOf(this.numMuestra), 1);
        byte[] templateZPL = (
            init( 800 ) +
            setText("IDIEM                       N. Muestra: " + this.numMuestra, 60) +
            setQRCode(url) +
            setText( url, 320, 0 ) +
            setText( "FECHA       : " + globals.getAntecedentesMuestreo().getDateMuestreo(), POSITION_NEXT_TITLE ) +
            setText( "TIPO/GRADO  : " + globals.getAntecedentesHormigonMuestreo().getGradoTipo()) +
            setText( "DOCILIDAD   : " + globals.getAntecedentesMuestreo().getMedCono1() ) +
            setText( "T. HORMIGON : " + globals.getAntecedentesMuestreo().getTempMezcla() + " C" ) +
            setText( "COLOCACION  : " + globals.getColocadoEn().getEje() ) +
            setText( "N. GUIA     : " + globals.getAntecedentesHormigonMuestreo().getNumGuia() ) +
            setText( "HORMIGONERA : " + globals.getAntecedentesHormigonMuestreo().getConfeccionado() ) +

            end()
        ).getBytes();
        return templateZPL;
    }

    private byte[] printTicket() {
        System.out.println(globals.getAntecedentesProbeta().getCantProbeta());
        byte[] templateZPL = (
                init( 250 ) +
                setText("IDIEM", 0, 270) +
                setBarCode( String.valueOf( globals.getMuestra().getNumMuestra() ) ) +
                //Indicar cantidad de muestras a imprimir****

                ( this.ticketQuantity == PRINT_ALL ? setPrintAll( globals.getAntecedentesProbeta().getCantProbeta() ) : "") +
                end()
        ).getBytes();
        return templateZPL;
    }

    private String setText( String text ) {
        this.positionY += POSITION_NEXT_LINE;
        if ( text.length() > MAX_LINE_LENGTH ){
            String aux, sReturn = "";
            aux = text.substring(0, 46);
            text = text.substring(46);
            sReturn += COMMAND_START_FIELD + MARGIN_LEFT + ", " + this.positionY + COMMAND_FONT_SIZE + COMMAND_TEXT + aux + COMMAND_END_FIELD;
            sReturn += setText( text, 0, 180 );
            return sReturn;
        } else
            return COMMAND_START_FIELD + MARGIN_LEFT + ", " + this.positionY + COMMAND_FONT_SIZE + COMMAND_TEXT + text + COMMAND_END_FIELD;
    }

    private String setText( String text, int positionY ) {
        positionY = (positionY == 0 ? POSITION_NEXT_LINE : positionY);
        this.positionY += positionY;
        if ( text.length() > MAX_LINE_LENGTH ){
            String aux, sReturn = "";
            aux = text.substring(0, 46);
            text = text.substring(46);
            sReturn += COMMAND_START_FIELD + MARGIN_LEFT + ", " + this.positionY + COMMAND_FONT_SIZE + COMMAND_TEXT + aux + COMMAND_END_FIELD;
            sReturn += setText( text, 0, 180 );
            return sReturn;
        } else
            return COMMAND_START_FIELD + MARGIN_LEFT + ", " + this.positionY + COMMAND_FONT_SIZE + COMMAND_TEXT + text + COMMAND_END_FIELD;
    }

    private String setText( String text, int positionY, int positionX ) {
        positionY = (positionY == 0 ? POSITION_NEXT_LINE : positionY);
        positionX = (positionX == 0 ? MARGIN_LEFT : positionX);
        this.positionY += positionY;
        if ( text.length() > MAX_LINE_LENGTH ){
            String aux, sReturn = "";
            aux = text.substring(0, 46);
            text = text.substring(46);
            sReturn += COMMAND_START_FIELD + positionX + ", " + this.positionY + COMMAND_FONT_SIZE + COMMAND_TEXT + aux + COMMAND_END_FIELD;
            sReturn += setText( text, 0, positionX );
            return sReturn;
        } else
            return COMMAND_START_FIELD + positionX + ", " + this.positionY + COMMAND_FONT_SIZE + COMMAND_TEXT + text + COMMAND_END_FIELD;
    }

    private String setBarCode( String text ) {
        this.positionY += POSITION_NEXT_LINE + POSITION_NEXT_LINE;
        return COMMAND_START_FIELD + 150 + ", " + this.positionY + COMMAND_BAR_CODE + text + COMMAND_RESET_PROPERTIES + COMMAND_END_FIELD;
    }
    private String setQRCode( String text ) {
        this.positionY += POSITION_NEXT_LINE;
        return COMMAND_START_FIELD + (MARGIN_LEFT*10) + ", " + this.positionY + COMMAND_QR_CODE + text + COMMAND_END_FIELD;
    }

    private static String zeroConfig() {
        return COMMAND_ZERO_CONFIG;
    }

    private static String init() {
        return COMMAND_INI;
    }

    private static String init( int heightTemplate ) {
        return COMMAND_INI + COMMAND_HEIGHT + heightTemplate;
    }
    private static String setPrintAll( int quantity) {
        return COMMAND_PRINT_QUANTITY + quantity;
       // return String.valueOf(quantity);
    }

    private String end() {
        this.positionY = POSITION_START;
        return COMMAND_END;
    }

    private ZebraPrinter connect( String macAddress ) {
        this.printerConnection = null;
        this.printerConnection = new BluetoothConnection(macAddress);

        try {
            this.printerConnection.open();
        } catch (ConnectionException e) {
            e.printStackTrace();
            disconnect();
        }

        ZebraPrinter printer = null;

        if (this.printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(this.printerConnection);
            } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
                e.printStackTrace();
                printer = null;
                disconnect();
            }
        }

        return printer;
    }

    public void disconnect() {
        try {
            if (this.printerConnection != null) {
                this.printerConnection.close();
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }


}
