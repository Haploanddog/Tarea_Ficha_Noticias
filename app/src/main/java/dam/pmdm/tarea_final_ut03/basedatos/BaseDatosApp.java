package dam.pmdm.tarea_final_ut03.basedatos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import dam.pmdm.tarea_final_ut03.daos.NoticiaDAO;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;

@Database(entities ={Noticia.class}, version = 1, exportSchema = true)
@TypeConverters({DateConverter.class})
public abstract class BaseDatosApp extends RoomDatabase{
    //Usando el patrón SINGLETON, nos aseguramos que solo haya una instancia de la
    // base de datos creada en nuestra aplicación.
    private static BaseDatosApp INSTANCIA;
    private boolean useExternalDB;

    public static BaseDatosApp getInstance(Context context) {
        INSTANCIA = null;
        if (INSTANCIA == null) {
                INSTANCIA = Room.databaseBuilder(
                                context.getApplicationContext(), //contexto
                                BaseDatosApp.class,              //clase de la BD
                                "dbNoticias")                      //nombre de BD
                        .build();
            }

        return INSTANCIA;
    }

    public static void destroyInstance() {
        INSTANCIA = null;
    }

    //Método para construir el objeto NoticiaDAO con el que accederemos
    //a la base de datos.
    public abstract NoticiaDAO noticiaDAO();
}
/** ESTO SERÍA PARA USAR UNA BASE DE DATOS EXTERNA AL INSTANCIAR BaseDatosa
 * @Database(entities ={Noticia.class}, version = 1, exportSchema = true)
 * @TypeConverters({DateConverter.class})
 * public abstract class BaseDatosApp extends RoomDatabase{
 *     //Usando el patrón SINGLETON, nos aseguramos que solo haya una instancia de la
 *     // base de datos creada en nuestra aplicación.
 *     private static BaseDatosApp INSTANCIA;
 *     private boolean useExternalDB;
 *
 *     public static BaseDatosApp getInstance(Context context, boolean useExternalDB) {
 *         INSTANCIA = null;
 *         if (INSTANCIA == null) {
 *             if(useExternalDB) {
 *                BaseDatosExterna dbExterna = new BaseDatosExterna(URL, USER, PASS);
 *                return new NoticiaDAO(dbExterna);
 *             } else {
 *                 INSTANCIA = Room.databaseBuilder(
 *                                 context.getApplicationContext(), //contexto
 *                                 BaseDatosApp.class,              //clase de la BD
 *                                 "dbNoticias")                      //nombre de BD
 *                         .build();
 *             }
 *         }
 *
 *         return INSTANCIA;
 *     }
 *
 *     public static void destroyInstance() {
 *         INSTANCIA = null;
 *     }
 *
 *     //Método para construir el objeto NoticiaDAO con el que accederemos
 *     //a la base de datos.
 *     public abstract NoticiaDAO noticiaDAO();
 * }
 */