package dam.pmdm.tarea_final_ut03.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import dam.pmdm.tarea_final_ut03.entidades.Noticia;

@Dao
public interface NoticiaDAO {
    //Anotación que permite realizar una consulta de todas las noticias de la lista
    @Query("SELECT * FROM noticia")
    //Método que realiza la consulta anterior
    //En este caso haremos que esta consulta se regenere cada vez que se produzcan cambios
    //en la base de datos mediante un objeto LiveData.
    LiveData<List<Noticia>> getAll();

    //Anotación que permite realizar una consulta para las noticias con uns ids determinados
    @Query("SELECT * FROM noticia WHERE _id IN (:noticiaIds)")
    //Método que realiza la consulta anterior
    List<Noticia> loadAllByIds(int[] noticiaIds);

    //Anotación que permite realizar una consulta para una noticia para un nombre determinado
    @Query("SELECT * FROM noticia WHERE _id LIKE :uid LIMIT 1")
    //Método que realiza la consulta anterior
    Noticia findByNoticia(int uid);

    //Anotación que permite realizar la inserción de una relación de noticias
    @Insert
    //Método que realiza la inserción anterior
    void insertAll(Noticia... noticias);

    //Anotación que permite realizar el borrado de una noticia
    @Delete
    //Método que realiza el borrado anterior
    void delete(Noticia noticia);

    //Anotación que permite realizar la actualización de una noticia
    @Update
    //Método que realiza la actualización anterior
    void update(Noticia noticia);

}
