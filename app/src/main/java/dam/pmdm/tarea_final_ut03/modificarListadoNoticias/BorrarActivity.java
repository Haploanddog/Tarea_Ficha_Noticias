package dam.pmdm.tarea_final_ut03.modificarListadoNoticias;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dam.pmdm.tarea_final_ut03.R;
import dam.pmdm.tarea_final_ut03.adaptadores.AdaptadorNoticias;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosApp;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosExterna;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.listaNoticias.ListadoNoticiasActivity;
import dam.pmdm.tarea_final_ut03.listaNoticias.ListadoViewModel;

public class BorrarActivity extends AppCompatActivity {
    private RecyclerView rv;

    private ListadoViewModel listaNoticiasViewModel;
    private Button btnBorrar;
    private Button btnCancelar;
    private BaseDatosApp bd;
    private AdaptadorNoticias adaptador;
    private boolean esParaBorrarNoticias;

    private ArrayList<Integer> filasSeleccionadas;
    private int numNoticias;

    private List<Noticia> listaNoticias = new ArrayList<>();

    private boolean esBdExterna = ListadoNoticiasActivity.esBdExterna;
    private BaseDatosExterna bdExterna;
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

        //Comprobamos si se utiliza la BD externa o interna y se instancia lo que corresponda
        if(esBdExterna){
            try {
                bdExterna = new BaseDatosExterna(ListadoNoticiasActivity.URL, ListadoNoticiasActivity.USER, ListadoNoticiasActivity.PASS);
            } catch (Exception ex) {
                Toast.makeText(this, "Error al conectar con bd", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            bd = BaseDatosApp.getInstance(this);
        }

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
                if (esBdExterna) {
                    bdExterna = new BaseDatosExterna(ListadoNoticiasActivity.URL, ListadoNoticiasActivity.USER, ListadoNoticiasActivity.PASS);
                    listaNoticias = bdExterna.obtenerTodasNoticias().getValue();
                } else {
                    listaNoticias = noticias;
                }
                adaptador.setListadoNoticias(listaNoticias);
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
                if(adaptador.getNoticiasSeleccionadas().size() > 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(BorrarActivity.this);
                builder.setTitle("Eliminar noticias");
                StringBuilder sb = new StringBuilder();
                for (Noticia noticia : adaptador.getNoticiasSeleccionadas()
                ) {
                    sb.append(adaptador.getListadoNoticias().indexOf(noticia)+1);
                    sb.append(", ");
                }
                if (sb.length() > 1) { // Eliminamos los últimos caracteres ", "
                    sb.deleteCharAt(sb.length() - 1);
                    sb.deleteCharAt(sb.length() - 1);
                } else {
                    sb.append("");
                }
                builder.setMessage("¿Desea realmente eliminar las noticias nº: " + sb.toString() + "?");
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

            } else {
                    Toast.makeText(BorrarActivity.this, "No ha seleccionado ninguna noticia",Toast.LENGTH_SHORT).show();
                }}
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
            if (esBdExterna) {
                try {
                    bdExterna.eliminarNoticia(noticia);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
            // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
            bd.noticiaDAO().delete(noticia);
        }
    }}
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("noticiasSeleccionadas", adaptador.getNoticiasSeleccionadas());
        outState.putInt("posicion", adaptador.getPosicion());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        adaptador.setNoticiasSeleccionadas(savedInstanceState.getParcelableArrayList("noticiasSeleccionadas",Noticia.class));
        adaptador.setPosicion(savedInstanceState.getInt("posicion"));
        adaptador.notifyDataSetChanged();
    }
}