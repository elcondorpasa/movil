package cl.idiem.uchile.dhc.Clases;

import com.sewoo.jpos.command.ESCPOS;
import com.sewoo.jpos.command.ESCPOSConst;
import com.sewoo.jpos.printer.ESCPOSPrinter;
import com.sewoo.jpos.printer.LKPrint;

import java.io.IOException;

import cl.idiem.uchile.dhc.Assist.Utilitys;
import cl.idiem.uchile.dhc.Clases.Formulario.AntecedentesHormigonMuestreo;
import cl.idiem.uchile.dhc.Clases.Formulario.AntecedentesMuestreo;
import cl.idiem.uchile.dhc.Clases.Formulario.ColocadoEn;
import cl.idiem.uchile.dhc.Clases.Formulario.Muestra;

public class Printer
{
    private ESCPOSPrinter posPtr;
    // 0x1B
    private final char ESC = ESCPOS.ESC;

    public Printer()
    {
        posPtr = new ESCPOSPrinter();
    }

    //	private final String TAG = "PrinterStsChecker";
    private int rtn;

    public int printTest() throws InterruptedException {

        String data = "http://www.idiem.cl/intranet/url-de-ejemplo/";

        try {
            rtn = posPtr.printerSts();
            // Deja de imprimir cerca del final del papel.
            //if( ( rtn != 0 ) && ( rtn != ESCPOSConst.STS_PAPERNEAREMPTY ) ) return rtn;
            // No deja de imprimir cerca del final del papel.
            if( rtn != 0 )  return rtn;
        }
        catch( IOException e ){
            e.printStackTrace();
            return rtn;
        }

        try {
            //imprimir imagen
            //posPtr.printBitmap("//sdcard//temp//test//car_s.jpg", LKPrint.LK_ALIGNMENT_CENTER);
            posPtr.printNormal("Prueba de impresora satisfactoria DHC::IDIEM\n\n");
            //posPtr.printString( "QRCode\r\n" );
            //posPtr.printQRCode( data, data.length(), 3, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER );
            posPtr.lineFeed( 4 );
            posPtr.cutPaper();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        return 0;
    }

    public int print( Muestra muestra, AntecedentesHormigonMuestreo antecedentesHormigonMuestreo, ColocadoEn colocadoEn, AntecedentesMuestreo antecedentesMuestreo ) throws InterruptedException {
        String url = "http://repositorio.idiem.cl/dir1/dir2/" + Utilitys.stringCrypt(String.valueOf(muestra.getNumMuestra()), 1);
        String barCodeData = String.valueOf(muestra.getNumInterno());
        try {
            rtn = posPtr.printerSts();
            // Deja de imprimir cerca del final del papel.
            //if( ( rtn != 0 ) && ( rtn != ESCPOSConst.STS_PAPERNEAREMPTY ) ) return rtn;
            // No deja de imprimir cerca del final del papel.
            if( rtn != 0 )  return rtn;
        }
        catch( IOException e ){
            e.printStackTrace();
            return rtn;
        }

        try {
            posPtr.lineFeed(3);
            posPtr.printNormal("IDIEM                   Muestra Num.: " + barCodeData + "\n");
            posPtr.lineFeed(1);
            posPtr.printQRCode(url, url.length(), 10, ESCPOSConst.LK_QRCODE_EC_LEVEL_L, ESCPOSConst.LK_ALIGNMENT_CENTER);
            posPtr.printNormal(url + "\n");
            posPtr.lineFeed(1);
            posPtr.printNormal("FECHA           : 21/07/2016  \n");
            posPtr.printNormal("TIPO/GRADO      : " + antecedentesHormigonMuestreo.getGradoTipo() + "\n");
            posPtr.printNormal("DOCILIDAD       : 10cm." + "\n" );
            posPtr.printNormal("TEMP. HORMIGON  : " + antecedentesMuestreo.getTempMezcla() + "\n" );
            posPtr.printNormal("COLOCACION      : " + colocadoEn.getEje() + "\n" );
            posPtr.printNormal("NUM. GUIA       : " + antecedentesHormigonMuestreo.getNumGuia() + "\n" );
            posPtr.printNormal("HORMIGONERA     : " + antecedentesHormigonMuestreo.getConfeccionado() + "\n");
            posPtr.lineFeed(2);
            posPtr.printText("IDIEM\r\n", ESCPOSConst.LK_ALIGNMENT_CENTER, 0, 10);
            posPtr.printBarCode( barCodeData, ESCPOSConst.LK_BCS_EAN13, 70, 3, ESCPOSConst.LK_ALIGNMENT_CENTER, ESCPOSConst.LK_HRI_TEXT_BELOW );
            posPtr.lineFeed(3);
            posPtr.cutPaper();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        return 0;
    }
}