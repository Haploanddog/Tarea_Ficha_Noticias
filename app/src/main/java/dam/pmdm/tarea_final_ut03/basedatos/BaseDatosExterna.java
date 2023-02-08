package dam.pmdm.tarea_final_ut03.basedatos;

import java.sql.Connection;
import java.sql.DriverManager;

public class BaseDatosExterna {

    private final String URL;
    private final String USER;
    private final String PASS;

    public BaseDatosExterna(String url, String user, String pass) {
        this.URL = url;
        this.USER = user;
        this.PASS = pass;
    }

    public Connection conectar() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void desconectar(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//    public void agregarNoticia(Noticia noticia) {
//        Connection con = conectar();
//        try {
//            PreparedStatement statement = con.prepareStatement("INSERT INTO noticias(titular, enlace, fecha, leida, favorita, fiable) VALUES (?, ?, ?, ?, ?, ?)");
//            statement.setString(1, noticia.getTitular());
//            statement.setString(2, noticia.getEnlace());
//            statement.setLong(3, noticia.getFecha());
//            statement.setBoolean(4, noticia.isLeida());
//            statement.setBoolean(5, noticia.isFavorita());
//            statement.setBoolean(6, noticia.isFiable());
//            statement.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            desconectar(con);
//        }
//    }
//
//    public List<Noticia> obtenerTodasNoticias() {
//        Connection con = conectar();
//        List<Noticia> noticias = new ArrayList<>();
//        try {
//            Statement statement = con.createStatement();
//            ResultSet result = statement.executeQuery("SELECT * FROM noticias");
//            while (result.next()) {
//                Noticia noticia = new Noticia();
//                noticia.setUid(result.getInt("_id"));
//                noticia.setTitular(result.getString("titular"));
//                noticia.setEnlace(result.getString("enlace"));
//                noticia.setFecha(result.getLong("fecha"));
//                noticia.setLeida(result.getBoolean("leida"));
//                noticia.setFavorita(result.getBoolean("favorita"));
//                noticia.setFiable(result.getBoolean("fiable"));
//                noticias.add(noticia);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            desconectar(con);
//        }
//        return noticias;
//    }
//
//    public void actualizarNoticia(Noticia noticia) {
//        Connection con = conectar();
//        try {
//            PreparedStatement statement = con.prepareStatement("UPDATE noticias SET titular=?, enlace=?, fecha=?, leida=?, favorita=?, fiable=? WHERE _id=?");
//            statement.setString(1, noticia.getTitular());
//            statement.setString(2, noticia.getEnlace());
//            statement.setLong(3, noticia.getFecha());
//            statement.setBoolean(4, noticia.isLeida());
//            statement.setBoolean(5, noticia.isFavorita());
//            statement.setBoolean(6, noticia.isFiable());
//            statement.setInt(7, noticia.getUid());
//            statement.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            desconectar(con);
//        }
//    }
//
//    public void eliminarNoticia(Noticia noticia) {
//        Connection con = conectar();
//        try {
//            PreparedStatement statement = con.prepareStatement("DELETE FROM noticias WHERE _id=?");
//            statement.setInt(1, noticia.getUid());
//            statement.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            desconectar(con);
//        }
//    }
}

