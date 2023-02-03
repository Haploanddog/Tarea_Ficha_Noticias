package dam.pmdm.tarea_final_ut03.listaNoticias;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dam.pmdm.tarea_final_ut03.R;
import dam.pmdm.tarea_final_ut03.adaptadores.AdaptadorNoticias;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosApp;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.listaNoticias.ui.main.FichaFragment;
import dam.pmdm.tarea_final_ut03.listaNoticias.ui.main.ListadoViewModel;

public class ListadoNoticiasActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FichaFragment ficha;
    private ListadoViewModel listaNoticiasViewModel;
    private Button btnVolver;
    private BaseDatosApp bd;
    private AdaptadorNoticias adaptador;

    private List<Noticia> listaNoticias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_noticias);

        // Creamos el objeto de tipo ListaNoticiasViewModel
        listaNoticiasViewModel = new ViewModelProvider((ViewModelStoreOwner) this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(ListadoViewModel.class);

        btnVolver = findViewById(R.id.btnVolverListado);
        rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        //Creamos/obtenemos la instancia de la base de datos Room
        bd = BaseDatosApp.getInstance(this);

        //Creamos el adaptador
        adaptador = new AdaptadorNoticias(this, listaNoticias);

        if (listaNoticias.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("El listado está vacío, ¿desea cargar alguna noticia de ejemplo?")
                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Añade noticias de ejemplo a la lista
                            anadirNoticiasDeEjemplo();

                            // Notifica al adaptador de los cambios
                            adaptador.notifyDataSetChanged();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // No hace nada
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

// Asignamos el adaptador al RecyclerView
        rv.setAdapter(adaptador);

        //Asignamos el adaptador al RecyclerView
        rv.setAdapter(adaptador);

        //Asignamos un LinearLayout vertical al RecyclerView de forma que los datos se vean en
        //formato lista.
        rv.setLayoutManager( new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));

        listaNoticiasViewModel.getNoticias().observe(this, adaptador::setListadoNoticias);
    }

    public void anadirNoticiasDeEjemplo(){
        //titular,enlace,fecha,leida,favorita,fiable
        Noticia n1 = new Noticia("Justicia e Igualdad revisan cada punto de la ley del 'solo sí es sí' para garantizar que no se bajen las penas",
                "https://www.eldiario.es/politica/justicia-e-igualdad-revisan-punto-ley-si-si-garantizar-no-bajen-penas_1_9916822.html",
                "01/02/2023",true,false,true);
        Noticia n2 = new Noticia("El Parlamento Europeo toma como ejemplo a Yolanda Díaz y avanza en una 'ley rider' comunitaria",
                "https://www.publico.es/internacional/parlamento-europeo-toma-ejemplo-yolanda-diaz-avanza-ley-rider-comunitaria.html#md=modulo-portada-bloque:4col-t5;mm=mobile-big",
                "02/02/2023",
                false,true,true);

        Executor executor = Executors.newFixedThreadPool(5);
        // Inserta el Producto en un hilo secundario
        executor.execute(new InsertarNoticia(n1));
        executor.execute(new InsertarNoticia(n2));

    }
    class InsertarNoticia implements Runnable {

        private Noticia noticia;

        public InsertarNoticia(Noticia noticia) {
            this.noticia = noticia;
        }

        @Override
        public void run() {
            // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
            bd.noticiaDAO().insertAll(noticia);
        }
    }

}