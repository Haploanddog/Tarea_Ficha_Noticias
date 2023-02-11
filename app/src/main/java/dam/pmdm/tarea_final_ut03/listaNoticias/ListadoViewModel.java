package dam.pmdm.tarea_final_ut03.listaNoticias;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosApp;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosExterna;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.listaNoticias.ListadoNoticiasActivity;

public class ListadoViewModel extends AndroidViewModel {

    private LiveData<List<Noticia>> noticias = null;
    BaseDatosExterna bdExt;


    public ListadoViewModel(@NonNull Application application) {
        super(application);
        SharedPreferences sharedPreferences =
                application.getSharedPreferences("bd", 0);
        boolean isExternalDB = sharedPreferences.getBoolean("bd", false);
        if(!isExternalDB){ //Utilizamos NoticiaDAO
            noticias = BaseDatosApp
                    .getInstance(application)
                    .noticiaDAO().getAll();
        } else { //Instanciamos una BaseDatosExterna con los valores cargados en las preferencias
            bdExt = new BaseDatosExterna(ListadoNoticiasActivity.URL,ListadoNoticiasActivity.USER,ListadoNoticiasActivity.PASS);
            // Creamos una lista que guardará todas los registros de Noticia en la BD externa
            LiveData<List<Noticia>> listaNots = bdExt.obtenerTodasNoticias();
            noticias = listaNots;
        }

    }


    public LiveData<List<Noticia>> getNoticias() {
        return noticias;
    }
}
