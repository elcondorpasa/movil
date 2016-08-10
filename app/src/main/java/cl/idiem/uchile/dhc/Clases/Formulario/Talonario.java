package cl.idiem.uchile.dhc.Clases.Formulario;

import android.content.Context;
import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by jose.becerra on 25-04-2016.
 */
public class Talonario {

    private int index;
    private int id;
    private int start;
    private int current;
    private int tecnico;
    private int end;

    public Talonario() {
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isEmpty() {
        return this.current == this.end;
    }

    public int getTecnico() {
        return tecnico;
    }

    public void setTecnico(int tecnico) {
        this.tecnico = tecnico;
    }

    public double getCurrentPercentage() {
        return this.current / (this.end - this.start) * 100;
    }

    public int getNext() {
        return current + 1;
    }

    public void setNext() {
        this.current += 1;
        saveTalonario( this.index );
    }

    public boolean setTalonario( int index ) {
        if (this.isEmpty()) {

            File sdCard, directory;
            byte[] buffer = new byte[100];
            String str = new String();
            JSONObject json;

            sdCard = Environment.getExternalStorageDirectory();

            directory = new File(sdCard.getAbsolutePath()
                    + "/Archivos Formulario DHC/Talonarios");

            try {
                FileInputStream fis = new FileInputStream(new File(directory + "/Talonario_" + index + ".txt"));
                fis.read(buffer);
                fis.close();
                str = new String(buffer);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            try {
                json = new JSONObject(str);
                this.index = index;
                this.id = json.getInt("id");
                this.tecnico = json.getInt("tecnico");
                this.start = json.getInt("start");
                this.current = json.getInt("current");
                this.end = json.getInt("end");
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
            return true;
        } else
            return false;
    }

    public void saveTalonario( int index ) {

        File sdCard, directory;
        FileWriter file;
        String jsonTalonario = this.toString();

        try {
            if (Environment.getExternalStorageState().equals("mounted")) {

                sdCard = Environment.getExternalStorageDirectory();

                try {
                    directory = new File(sdCard.getAbsolutePath()
                            + "/Archivos Formulario DHC/Talonarios");
                    directory.mkdirs();

                    file = new FileWriter(directory + "/Talonario_" + index + ".txt", false);
                    file.write(jsonTalonario);
                    file.flush();
                    file.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public String toString() {
        return '{' +
                "'id':" + id +
                ", 'tecnico':" + tecnico +
                ", 'start':" + start +
                ", 'current':" + current +
                ", 'end':" + end +
                '}';
    }
}
