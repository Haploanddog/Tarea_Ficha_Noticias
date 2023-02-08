package dam.pmdm.tarea_final_ut03.listaNoticias;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
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

public class BorrarActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FichaFragment ficha;
    private ListadoViewModel listaNoticiasViewModel;
    private Button btnBorrar;
    private Button btnCancelar;
    private BaseDatosApp bd;
    private AdaptadorNoticias adaptador;
    private boolean esParaBorrarNoticias;

    private ArrayList<Integer> filasSeleccionadas;
    private int numNoticias;

    private List<Noticia> listaNoticias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrar);

        // Creamos el objeto de tipo ListaNoticiasViewModel
        listaNoticiasViewModel = new ViewModelProvider((ViewModelStoreOwner) this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(ListadoViewModel.class);
        btnBorrar = findViewById(R.id.btnBorrar);
        btnCancelar = findViewById(R.id.btnCancelar);
        rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        filasSeleccionadas = new ArrayList<>();
        esParaBorrarNoticias = false;
        //Creamos/obtenemos la instancia de la base de datos Room
        bd = BaseDatosApp.getInstance(this);

        //Creamos el adaptador
        adaptador = new AdaptadorNoticias(this, listaNoticias, true);


        // Asignamos el adaptador al RecyclerView
        rv.setAdapter(adaptador);


        //Asignamos un LinearLayout vertical al RecyclerView de forma que los datos se vean en
        //formato lista.
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //Observamos los cambios en el listado de noticias y actualizamos el adaptador cuando sea necesario
        listaNoticiasViewModel.getNoticias().observe(this, new Observer<List<Noticia>>() {
            @Override
            public void onChanged(List<Noticia> noticias) {
                synchronized (this) {
                    listaNoticias = noticias;
                    adaptador.setListadoNoticias(listaNoticias);
                }
            }
        });
        //Escuchador del botón Cancelar (lleva al ListadoNoticiasActivity)
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                volverAListadoNoticiasActivity();
            }
        });
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(BorrarActivity.this);
                builder.setTitle("Eliminar noticias");
                StringBuilder sb = new StringBuilder();
                for (Noticia noticia : adaptador.getNoticiasSeleccionadas()
                ) {
                    sb.append(noticia.getUid());
                    sb.append(", ");
                }
                builder.setMessage("¿Desea realmente eliminar las noticias con id: " + sb.toString() + "?");
                builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Executor executor = Executors.newSingleThreadExecutor();
                        for (Noticia noticia : adaptador.getNoticiasSeleccionadas()
                        ) {
                            executor.execute(new BorrarNoticia(noticia));
                        }

                        volverAListadoNoticiasActivity();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //No se hace nada
                    }
                });
                builder.create().show();
            }
        });
    }


    public List<Noticia> getListaNoticias() {
        return listaNoticias;
    }

    public AdaptadorNoticias getAdaptadorNoticias() {
        return adaptador;
    }

    private void volverAListadoNoticiasActivity() {
        Intent intent = new Intent(this, ListadoNoticiasActivity.class);
        adaptador.notifyDataSetChanged();
        startActivity(intent);
    }


    class BorrarNoticia implements Runnable {

        private Noticia noticia;

        public BorrarNoticia(Noticia noticia) {
            this.noticia = noticia;
        }

        @Override
        public void run() {
            // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
            bd.noticiaDAO().delete(noticia);
        }
    }
    class EncontrarNoticia implements Runnable {


        private int id;

        public EncontrarNoticia(int id) {
            this.id = id + 1;
        }

        @Override
        public void run() {
            // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
            Noticia noticia = bd.noticiaDAO().findByNoticia(id);

        }
    }
}