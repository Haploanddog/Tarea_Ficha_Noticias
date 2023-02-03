package dam.pmdm.tarea_final_ut03.entidades;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Noticia implements Parcelable {

    //Anotación que indica cual es la clave primaria de la tabla y que será autogenerado
    @PrimaryKey(autoGenerate = true)
    //Anotación que permite asignar el atributo al campo de la tabla, lo vincula con este
    @ColumnInfo(name = "_id")
    private int uid;

    @ColumnInfo(name = "noticia")
    private String titular;

    @ColumnInfo(name = "enlace")
    private String enlace;

    @ColumnInfo(name = "fecha")
    private long fecha;

    @ColumnInfo(name = "leida")
    private boolean leida;

    @ColumnInfo(name = "favorita")
    private boolean favorita;

    @ColumnInfo(name = "fiable")
    private boolean fiable;

    public Noticia(String titular, String enlace, String fecha, boolean leida, boolean favorita, boolean fiable) {
        this.titular = titular;
        this.enlace = enlace;
        this.fecha = transformarFechaStringToLong(fecha);
        this.leida = leida;
        this.favorita = favorita;
        this.fiable = fiable;
    }

    public Noticia(){

    }

    protected Noticia(Parcel in) {
        uid = in.readInt();
        titular = in.readString();
        enlace = in.readString();
        fecha = in.readLong();
        leida = in.readByte() != 0;
        favorita = in.readByte() != 0;
        fiable = in.readByte() != 0;
    }

    public static final Creator<Noticia> CREATOR = new Creator<Noticia>() {
        @Override
        public Noticia createFromParcel(Parcel in) {
            return new Noticia(in);
        }

        @Override
        public Noticia[] newArray(int size) {
            return new Noticia[size];
        }
    };

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public boolean isFavorita() {
        return favorita;
    }

    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }

    public boolean isFiable() {
        return fiable;
    }

    public void setFiable(boolean fiable) {
        this.fiable = fiable;
    }

    public void setFechaFromString(String fechaString) {
        this.fecha = transformarFechaStringToLong(fechaString);
    }


    private long transformarFechaStringToLong(String fecha) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            // Verifica si el día o mes es menor a 10 y agrega un cero a la izquierda.
            String[] fechaArray = fecha.split("/");
            String dia = fechaArray[0].length() == 1 ? "0" + fechaArray[0] : fechaArray[0];
            String mes = fechaArray[1].length() == 1 ? "0" + fechaArray[1] : fechaArray[1];
            fecha = dia + "/" + mes + "/" + fechaArray[2];
            Date fechaDate = sdf.parse(fecha);
            return fechaDate.getTime();
        } catch (ParseException e) {
            Date fechaActual = new Date();
            return fechaActual.getTime();
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(uid);
        dest.writeString(titular);
        dest.writeString(enlace);
        dest.writeLong(fecha);
        dest.writeByte((byte) (leida ? 1 : 0));
        dest.writeByte((byte) (favorita ? 1 : 0));
        dest.writeByte((byte) (fiable ? 1 : 0));
    }
}
