package cl.idiem.uchile.dhc;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;

import com.sewoo.port.android.BluetoothPort;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import cl.idiem.uchile.dhc.Assist.AlertView;
import cl.idiem.uchile.dhc.Assist.DemoSleeper;
import cl.idiem.uchile.dhc.Assist.PrinterZebraIMZ320;

public class PrinterActivity extends AppCompatActivity {

    private static final String TAG = "BluetoothConnectMenu";
    // Intent request codes
    // private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private ProgressDialog progressDialog;
    private String lastConnAddr;
    private static final String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "//temp";
    private static final String fileName = dir + "//BTPrinter";

    ArrayAdapter<String> adapter;
    private BluetoothAdapter mBluetoothAdapter;
    private Vector<BluetoothDevice> remoteDevices;
    private BroadcastReceiver discoveryResult;
    private Context context;
    // UI
    private EditText btAddrBox;
    private Button connectButton;
    private ListView list;
    private AlertDialog.Builder builder;
    // BT
    private BluetoothPort bluetoothPort;

    private Connection printerConnection;
    private ZebraPrinter printer;
    private TextView statusField;
    private PrinterZebraIMZ320 print;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_printer);
        print = new PrinterZebraIMZ320(getApplication().getApplicationContext());

        final Globals globals = (Globals)getApplicationContext();

        // getActionBar().setDisplayHomeAsUpEnabled(true);// Configuración
        statusField = (TextView) this.findViewById(R.id.statusText);
        btAddrBox = (EditText) findViewById( R.id.EditTextAddressBT );
        connectButton = (Button) findViewById( R.id.ButtonConnectBT );
        list = (ListView) findViewById( R.id.ListView01 );
        context = this;

        // Configuración
        loadSettingFile();
        bluetoothSetup();

        // Button -- Conectar
        connectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                globals.setAddrPrinter(btAddrBox.getText().toString());
                new Thread(new Runnable() {
                    public void run() {
                        enableConnectButton(false);
                        Looper.prepare();
                        doConnectionTest();
                        Looper.loop();
                        Looper.myLooper().quit();
                    }
                }).start();
            }
        });

        // Lista de dispositivos Bluetooth
        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1 );
        list.setAdapter( adapter );
        addPairedDevices();

        // Conectar - click en item de la lista.
        list.setOnItemClickListener( new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick( AdapterView<?> arg0, View arg1, int arg2, long arg3 ) {

                BluetoothDevice btDev = remoteDevices.elementAt( arg2 );

                try  {
                    if( mBluetoothAdapter.isDiscovering() ) {
                        mBluetoothAdapter.cancelDiscovery();
                    }

                    btAddrBox.setText(btDev.getAddress());
                    lastConnAddr = btDev.getAddress();
                    saveSettingFile();
                    globals.setAddrPrinter(btDev.getAddress());
                    new Thread(new Runnable() {
                        public void run() {
                            enableConnectButton(false);
                            Looper.prepare();
                            doConnectionTest();
                            Looper.loop();
                            Looper.myLooper().quit();
                        }
                    }).start();
                }
                catch ( Exception e ) {
                    AlertView.showAlert( e.getMessage(), context );
                    return;
                }

            }

        });

        // UI - Event Handler.
        // Buscar dispositivo, luego agrega a la lista.
        discoveryResult = new BroadcastReceiver() {

            @Override
            public void onReceive( Context context, Intent intent ) {

                String key;
                BluetoothDevice remoteDevice = intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );

                if( remoteDevice != null ) {

                    if( remoteDevice.getBondState() != BluetoothDevice.BOND_BONDED ) {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "]";
                    }
                    else  {
                        key = remoteDevice.getName() + "\n[" + remoteDevice.getAddress() + "] [Paired]";
                    }

                    if( bluetoothPort.isValidAddress( remoteDevice.getAddress() ) ) {
                        remoteDevices.add( remoteDevice );
                        adapter.add( key );
                    }

                }

            }

        };

    }

    private void printingTickets( final int quantity ) {
        progressDialog = ProgressDialog.show(PrinterActivity.this, "Impresión", "Imprimiendo ticket...", true, false);
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                if ( !print.setPrintTicket(btAddrBox.getText().toString(), 123412, quantity ) )
                    Toast.makeText(getApplication().getApplicationContext(), "Ocurrió un error, intente nuevamente", Toast.LENGTH_LONG ).show();
                progressDialog.dismiss();
                switch ( quantity ) {
                    case PrinterZebraIMZ320.PRINT_ONE:
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        printingTickets( PrinterZebraIMZ320.PRINT_ONE );
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };
                        builder = new AlertDialog.Builder(context);
                        builder.setMessage("Imprimir siguiente ticket?").setPositiveButton("Sí", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                        break;
                    case PrinterZebraIMZ320.PRINT_ALL:
                        break;
                }
                Looper.loop();
                Looper.myLooper().quit();
            }
        }).start();
    }

    @Override
    protected void onDestroy()  {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    // Seteo Bluetooth.
    private void bluetoothSetup() {
        // Initialize
        clearBtDevData();
        bluetoothPort = BluetoothPort.getInstance();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if ( mBluetoothAdapter == null ) {
            // Dispositivo no soporta Bluetooth
            return;
        }
        if ( !mBluetoothAdapter.isEnabled() ) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

    }

    // Borrar lista de los datos del dispositivo utilizado.
    private void clearBtDevData()
    {
        remoteDevices = new Vector<BluetoothDevice>();
    }

    // añadir a la lista de dispositivos emparejados.
    private void addPairedDevices() {

        BluetoothDevice pairedDevice;
        Iterator<BluetoothDevice> iter = ( mBluetoothAdapter.getBondedDevices() ).iterator();

        while( iter.hasNext() ) {
            pairedDevice = iter.next();

            //  if( bluetoothPort.isValidAddress( pairedDevice.getAddress() ) ) {
            remoteDevices.add( pairedDevice );
            adapter.add( pairedDevice.getName() + "\n[" + pairedDevice.getAddress() + "] [Paired]" );
            //  }

        }

    }

    private void doConnectionTest() {
        printer = connect();
        if (printer != null) {
            sendTestLabel();
        } else {
            disconnect();
        }
    }

    private String getMacAddressFieldText() {
        return btAddrBox.getText().toString();
    }

    public ZebraPrinter connect() {
        Globals globals = (Globals) getApplication().getApplicationContext();
        setStatus("Conectando...", Color.YELLOW);
        printerConnection = null;
        printerConnection = new BluetoothConnection(getMacAddressFieldText());
        globals.setAddrPrinter( getMacAddressFieldText() );

        try {
            printerConnection.open();
            setStatus("Conectado", Color.GREEN);
        } catch (ConnectionException e) {
            setStatus("Comm Error! Desconectando", Color.RED);
            DemoSleeper.sleep(1000);
            disconnect();
        }

        ZebraPrinter printer = null;

        if (printerConnection.isConnected()) {
            try {
                printer = ZebraPrinterFactory.getInstance(printerConnection);
                setStatus("Determinando Languaje de Impresión", Color.YELLOW);
                PrinterLanguage pl = printer.getPrinterControlLanguage();
                setStatus("Languaje de Impresión " + pl, Color.BLUE);
            } catch (ConnectionException e) {
                setStatus("Languaje de Impresión desconocido", Color.RED);
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                setStatus("Languaje de Impresión desconocido", Color.RED);
                printer = null;
                DemoSleeper.sleep(1000);
                disconnect();
            }
        }

        return printer;
    }

    public void disconnect() {
        try {
            setStatus("Desconectando", Color.RED);
            if (printerConnection != null) {
                printerConnection.close();
            }
            setStatus("Desconectado", Color.RED);
        } catch (ConnectionException e) {
            setStatus("COMM Error! Desconectado", Color.RED);
        } finally {
            enableConnectButton(true);
        }
    }

    private void enableConnectButton(final boolean enabled) {
        runOnUiThread(new Runnable() {
            public void run() {
                connectButton.setEnabled(enabled);
            }
        });
    }

    private void setStatus(final String statusMessage, final int color) {
        runOnUiThread(new Runnable() {
            public void run() {
                statusField.setBackgroundColor(color);
                statusField.setText(statusMessage);
            }
        });
        DemoSleeper.sleep(1000);
    }

    private void sendTestLabel() {
        try {
            byte[] configLabel = getConfigLabel();
            printerConnection.write(configLabel);
            setStatus("Enviando datos", Color.BLUE);
            DemoSleeper.sleep(1500);
            if (printerConnection instanceof BluetoothConnection) {
                String friendlyName = ((BluetoothConnection) printerConnection).getFriendlyName();
                setStatus(friendlyName, Color.MAGENTA);
                DemoSleeper.sleep(500);
            }
        } catch (ConnectionException e) {
            setStatus(e.getMessage(), Color.RED);
        } finally {
            disconnect();
        }
    }

    private byte[] getConfigLabel() {
        PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();

        byte[] configLabel = null;
        if (printerLanguage == PrinterLanguage.ZPL) {
            configLabel = ("^XA^LL100" +
                    "^FO100, 30^ADN, 11, 7^FD *****IMPRESORA CONECTADA*****^FS" +
                    "^XZ").getBytes();
        } else if (printerLanguage == PrinterLanguage.CPCL) {
            String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 CONECTADA\r\n" + "PRINT\r\n";
            configLabel = cpclConfigLabel.getBytes();
        }
        return configLabel;
    }

    private void loadSettingFile()  {
        int rin;
        char [] buf = new char[128];

        try {
            FileReader fReader = new FileReader( fileName );
            rin = fReader.read( buf );

            if( rin > 0 ) {
                lastConnAddr = new String( buf, 0, rin );
                btAddrBox.setText(lastConnAddr);
            }

            fReader.close();
        }
        catch ( FileNotFoundException e ) {
            Log.i(TAG, "No existe historial de conexión.");
        }
        catch ( IOException e ) {
            Log.e( TAG, e.getMessage(), e );
        }

    }

    private void saveSettingFile() {

        try {
            File tempDir = new File( dir );

            if( !tempDir.exists() ) {
                tempDir.mkdir();
            }

            FileWriter fWriter = new FileWriter( fileName );

            if( lastConnAddr != null )
                fWriter.write(lastConnAddr);

            fWriter.close();
        }
        catch ( FileNotFoundException e ) {
            Log.e(TAG, e.getMessage(), e);
        }
        catch ( IOException e ) {
            Log.e( TAG, e.getMessage(), e );
        }
    }
}
