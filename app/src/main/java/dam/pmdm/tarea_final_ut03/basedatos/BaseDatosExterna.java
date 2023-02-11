package dam.pmdm.tarea_final_ut03.basedatos;

import android.os.StrictMode;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import dam.pmdm.tarea_final_ut03.entidades.Noticia;

public class BaseDatosExterna {

    private final String URL;
    private final String USER;
    private final String PASS;
    private Connection conn;

    public BaseDatosExterna(String url, String user, String pass) {
        this.URL = url;
        this.USER = user;
        this.PASS = pass;
    }

    public Connection conectar() throws SQLException {
        final String class_jdbc = "com.mysql.jdbc.Driver";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        try {
            Class.forName(class_jdbc);
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException se) {
            Log.e("ERROR1", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERROR2", e.getMessage());
        } catch (Exception e) {
            Log.e("ERROR3", e.getMessage());
        }
        return conn;
    }

    public void desconectar() throws SQLException {
        if (conn!= null) {
            conn.close();
        }
    }

    public Connection getConnection() {
        return conn;
    }


    public void agregarNoticia(Noticia noticia) throws SQLException {
        Connection con = conectar();

        try {
            PreparedStatement statement = con.prepareStatement("INSERT INTO noticias VALUES (0,?, ?, ?, ?, ?, ?)");
            statement.setString(1, noticia.getTitular());
            statement.setString(2, noticia.getEnlace());
            statement.setLong(3, noticia.getFecha());
            statement.setBoolean(4, noticia.isLeida());
            statement.setBoolean(5, noticia.isFavorita());
            statement.setBoolean(6, noticia.isFiable());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
    }

    public int getNumeroNoticias() throws SQLException {
        Connection con = conectar();
        int numero = 0;

        try {
            Statement statement = con.createStatement();
            ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM noticias");
            while (result.next()) {
                numero = result.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
        return numero;
    }

    public Noticia obtenerNoticiaPorId(int id) throws SQLException, SQLException {
        Connection con = conectar();
        Noticia noticia = new Noticia();
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM noticias WHERE _id=?");
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                noticia.setUid(result.getInt("_id"));
                noticia.setTitular(result.getString("noticia"));
                noticia.setEnlace(result.getString("enlace"));
                noticia.setFecha(result.getLong("fecha"));
                noticia.setLeida(result.getBoolean("leida"));
                noticia.setFavorita(result.getBoolean("favorita"));
                noticia.setFiable(result.getBoolean("fiable"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
        return noticia;
    }




    public LiveData<List<Noticia>> obtenerTodasNoticias() {
        final MutableLiveData<List<Noticia>> noticiasLiveData = new MutableLiveData<>();
        Executor executor = Executors.newFixedThreadPool(10);
        FutureTask<List<Noticia>> futureTask = new FutureTask<>(() -> {
            List<Noticia> noticias = new ArrayList<>();
            try {
                Connection con = conectar();
                Statement statement = con.createStatement();
                ResultSet result = statement.executeQuery("SELECT * FROM noticias");
                while (result.next()) {
                    Noticia noticia = new Noticia();
                    noticia.setUid(result.getInt("_id"));
                    noticia.setTitular(result.getString("noticia"));
                    noticia.setEnlace(result.getString("enlace"));
                    noticia.setFecha(result.getLong("fecha"));
                    noticia.setLeida(result.getBoolean("leida"));
                    noticia.setFavorita(result.getBoolean("favorita"));
                    noticia.setFiable(result.getBoolean("fiable"));
                    noticias.add(noticia);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    desconectar();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
            return noticias;
        });
        executor.execute(futureTask);
        try {
            noticiasLiveData.setValue(futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return noticiasLiveData;
    }





    public void actualizarNoticia(Noticia noticia) throws SQLException {
        Connection con = conectar();
        try {
            PreparedStatement statement = con.prepareStatement("UPDATE noticias SET noticia = ?, enlace = ?, fecha = ?, leida = ?, favorita = ?, fiable = ? WHERE _id = ?");
            statement.setString(1, noticia.getTitular());
            statement.setString(2, noticia.getEnlace());
            statement.setLong(3, noticia.getFecha());
            statement.setBoolean(4, noticia.isLeida());
            statement.setBoolean(5, noticia.isFavorita());
            statement.setBoolean(6, noticia.isFiable());
            statement.setInt(7, noticia.getUid());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
    }


    public void eliminarNoticia(Noticia noticia) throws SQLException {
        Connection con = conectar();
        try {
            PreparedStatement statement = con.prepareStatement("DELETE FROM noticias WHERE _id=?");
            statement.setInt(1, noticia.getUid());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            desconectar();
        }
    }
}

