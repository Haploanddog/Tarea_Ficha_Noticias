package dam.pmdm.tarea_final_ut03.listaNoticias.ui.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosApp;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;

public class ListadoViewModel extends AndroidViewModel {

    private final LiveData<List<Noticia>> noticias;


    public ListadoViewModel(@NonNull Application application) {
        super(application);
        noticias = BaseDatosApp
                .getInstance(application)
                .noticiaDAO().getAll();
    }


    public LiveData<List<Noticia>> getNoticias() {
        return noticias;
    }
}