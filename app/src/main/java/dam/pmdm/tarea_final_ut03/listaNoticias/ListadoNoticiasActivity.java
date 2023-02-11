package dam.pmdm.tarea_final_ut03.listaNoticias;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import dam.pmdm.tarea_final_ut03.MainActivity;
import dam.pmdm.tarea_final_ut03.R;
import dam.pmdm.tarea_final_ut03.adaptadores.AdaptadorNoticias;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosApp;
import dam.pmdm.tarea_final_ut03.basedatos.BaseDatosExterna;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.modificarListadoNoticias.BorrarActivity;
import dam.pmdm.tarea_final_ut03.modificarListadoNoticias.FichaActivity;
import dam.pmdm.tarea_final_ut03.modificarListadoNoticias.FichaFragment;
import dam.pmdm.tarea_final_ut03.menus.AboutUs;

public class ListadoNoticiasActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FichaFragment ficha;
    private ListadoViewModel listaNoticiasViewModel;
    private Button btnVolver;
    private BaseDatosApp bd;
    private AdaptadorNoticias adaptador;
    private boolean esParaBorrarNoticias;

    private ArrayList<Integer> filasSeleccionadas;
    private int numNoticias;

    private List<Noticia> listaNoticias = new ArrayList<>();
    //Variables para acceder a una BaseDatosExterna
    public static boolean esBdExterna;
    public static String URL = "";
    public static String USER = "";
    public static String PASS = "";
    BaseDatosExterna bdExterna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_noticias);

        // Creamos el objeto de tipo ListaNoticiasViewModel
        listaNoticiasViewModel = new ViewModelProvider((ViewModelStoreOwner) this,
                (ViewModelProvider.Factory) new ViewModelProvider.AndroidViewModelFactory(getApplication()))
                .get(ListadoViewModel.class);
        //Bindeamos
        btnVolver = findViewById(R.id.btnVolverListado);
        rv = findViewById(R.id.recyclerView);
        rv.setHasFixedSize(true);
        //Inicializamos las filasSeleccionadas
        filasSeleccionadas = new ArrayList<>();
        //El AdaptadorNoticias no permitirá que se puedan seleccionar varias filas
        esParaBorrarNoticias = false;

        //Cargamos las preferencias para determinar si la base de datos es externa.
        //En ese caso, establecemos los valores que el usuario haya determinado
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        esBdExterna = preferences.getBoolean("bd", false);
        String nombreBD = preferences.getString("nombreBD", "");
        String ip = preferences.getString("ip", "");
        String puerto = preferences.getString("puerto", "");
        USER = preferences.getString("usuario", "");
        PASS = preferences.getString("password","");
//        String URL = "";
        if( puerto.length() == 0){
            URL = ip+"/"+nombreBD;
        } else{
            URL = ip +":"+ puerto +"/"+nombreBD;
        }


//    String URL = "http://{" + ip + "}:{" + puerto + "}/phpmyadmin";
        //URL = "jdbc:mysql://10.0.2.2:3306/";
        try {
            bdExterna = new BaseDatosExterna(URL, USER, PASS);
        } catch (Exception ex) {
            Toast.makeText(this, "Error al conectar con bd", Toast.LENGTH_SHORT).show();
        }
        if(!esBdExterna) {
            //Creamos/obtenemos la instancia de la base de datos Room
            bd = BaseDatosApp.getInstance(this);
        }else{
            try {
                bdExterna.conectar();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        //Creamos el adaptador
        adaptador = new AdaptadorNoticias(this, listaNoticias, false);

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
                    bdExterna = new BaseDatosExterna(URL, USER, PASS);
                    listaNoticias = bdExterna.obtenerTodasNoticias().getValue();
                } else {
                    listaNoticias = noticias;
                }
                adaptador.setListadoNoticias(listaNoticias);
            }
        });



        //Comprobamos si hay alguna noticia registrada en la base de datos
        Executor executor = Executors.newFixedThreadPool(5);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (esBdExterna) {
                    try {
                        final int numNoticiasExternas = bdExterna.getNumeroNoticias();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                numNoticias = numNoticiasExternas;
                            }
                        });
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }  else {
                numNoticias = bd.noticiaDAO().getNumNoticias();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (numNoticias == 0) { //Si no hay ninguna noticia registrada:
                            AlertDialog.Builder builder = new AlertDialog.Builder(ListadoNoticiasActivity.this);
                            builder.setMessage("El listado está vacío, ¿desea cargar alguna noticia de ejemplo?")
                                    .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            try {
                                                anadirNoticiasDeEjemplo();
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // No hace nada
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } // En caso de haber alguna noticia, no se muestra el AlertDialog
                    }
                });
            }
        });


        //Escuchador del botón Volver (lleva a la MainActivity)
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                volverAMainActivity();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("noticia_guardada", false)) {
//            listaNoticiasViewModel.getNoticias().removeObserver(observer);
            listaNoticiasViewModel.getNoticias().observe(this, adaptador::setListadoNoticias);

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Se infla el menú
        getMenuInflater().inflate(R.menu.menu, menu);
        // Se muestra el grupo de opciones
        menu.setGroupVisible(R.id.it_menu_ppal, true);
        menu.setGroupVisible(R.id.it_menu_listado, true);
        menu.setGroupVisible(R.id.it_menu_preferencias, false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.it_acerca_de:
                // Si se pulsa este botón, pasamos a la actividad AboutUs
                Intent intentAbout = new Intent(this, AboutUs.class);

                startActivity(intentAbout);
                break;

            case R.id.it_salir:
                // Si se pulsa este botón, salimos de la aplicación
                finishAffinity();
                break;
            case R.id.it_borrar_noticia:
                Intent intentBorrar = new Intent(this, BorrarActivity.class);
                startActivity(intentBorrar);
                break;
            case R.id.it_volver:
                volverAMainActivity();
                break;
            case R.id.it_anadir_noticia:
                Intent intentAnadir = new Intent(this, FichaActivity.class);
                intentAnadir.putExtra("esParaActualizar", false);
                startActivity(intentAnadir);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public List<Noticia> getListaNoticias() {
        return listaNoticias;
    }

    public AdaptadorNoticias getAdaptadorNoticias() {
        return adaptador;
    }

    private void volverAMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
    }

    public void anadirNoticiasDeEjemplo() throws SQLException {
        //titular,enlace,fecha,leida,favorita,fiable
        Noticia n1 = new Noticia("Justicia e Igualdad revisan cada punto de la ley del 'solo sí es sí' para garantizar que no se bajen las penas",
                "https://www.eldiario.es/politica/justicia-e-igualdad-revisan-punto-ley-si-si-garantizar-no-bajen-penas_1_9916822.html",
                "01/02/2023", true, false, true);
        Noticia n2 = new Noticia("El Parlamento Europeo toma como ejemplo a Yolanda Díaz y avanza en una 'ley rider' comunitaria",
                "https://www.publico.es/internacional/parlamento-europeo-toma-ejemplo-yolanda-diaz-avanza-ley-rider-comunitaria.html#md=modulo-portada-bloque:4col-t5;mm=mobile-big",
                "02/02/2023",
                false, true, true);

        Executor executor = Executors.newFixedThreadPool(10);
        // Inserta el Producto en un hilo secundario
        if (esBdExterna) { //Si estamos con la BD externa
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        bdExterna.agregarNoticia(n1);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        bdExterna.agregarNoticia(n2);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

        } else { // ConNoticiaDAO
            executor.execute(new InsertarNoticia(n1));
            executor.execute(new InsertarNoticia(n2));
        }
        listaNoticiasViewModel.getNoticias().observe(ListadoNoticiasActivity.this, new Observer<List<Noticia>>() {
            @Override
            public void onChanged(List<Noticia> noticias) {
                listaNoticias.add(n1);
                listaNoticias.add(n2);
                adaptador.setListadoNoticias(listaNoticias);
                adaptador.notifyDataSetChanged();
            }
        });
    }

    class InsertarNoticia implements Runnable {

        private Noticia noticia;

        public InsertarNoticia(Noticia noticia) {
            this.noticia = noticia;
        }

        @Override
        public void run() {
            if (esBdExterna) {
                try {
                    bdExterna.agregarNoticia(noticia);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                // Se usa el método insertAll de la interfaz productoDAO, implementada en BaseDatosApp
                bd.noticiaDAO().insertAll(noticia);
            }
        }
    }

}