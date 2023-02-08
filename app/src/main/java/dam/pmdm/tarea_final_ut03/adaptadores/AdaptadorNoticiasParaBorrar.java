package dam.pmdm.tarea_final_ut03.adaptadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dam.pmdm.tarea_final_ut03.R;
import dam.pmdm.tarea_final_ut03.entidades.Noticia;
import dam.pmdm.tarea_final_ut03.listaNoticias.FichaActivity;

public class AdaptadorNoticiasParaBorrar  extends RecyclerView.Adapter {

        private List<Noticia> listadoNoticiasParaBorrar;

        private Context contexto;

        private int posicion;

        private List<Integer> listaPosicionesSeleccionadas;


        public AdaptadorNoticiasParaBorrar(Context contexto, List<Noticia> listadoNoticias){
            this.listadoNoticiasParaBorrar = listadoNoticias;
            this.contexto = contexto;
            this.listaPosicionesSeleccionadas = new ArrayList<>();
        }

        public List<Noticia> getListadoNoticiasParaBorrar() {
            return listadoNoticiasParaBorrar;
        }

        public void setListadoNoticiasParaBorrar(List<Noticia> listadoNoticiasParaBorrar) {
            this.listadoNoticiasParaBorrar = listadoNoticiasParaBorrar;
            // El adaptador actualiza la vista del RecyclerView con los datos nuevos, aunque no los recarga
            notifyDataSetChanged();
        }

        public int getPosicion() {
            return posicion;
        }

        public void setPosicion(int posicion) {
            this.posicion = posicion;
        }

        /**
         * Este método se utiliza para crear un nuevo ViewHolder y enlazarlo con una vista para cada
         * elemento de la lista
         * @param parent The ViewGroup into which the new View will be added after it is bound to
         *               an adapter position.
         * @param viewType The view type of the new View.
         *
         * @return
         */
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Inflamos el layout del contenido de cada fila de la lista que representamos.
            View layout = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.noticia_row, parent, false);
            //Creamos el objeto ViewHolder que servirá para mostrar los datos en la fila, y le pasamos
            //el layout que hemos inflado en la anterior línea de código.
            AdaptadorNoticiasParaBorrar.NoticiaParaBorrarViewHolder noticia = new AdaptadorNoticiasParaBorrar.NoticiaParaBorrarViewHolder(layout);
            return noticia;
        }

        /**
         * Actuliza los datos de un ViewHolder existente con los datos para la posición especificada
         * @param holder The ViewHolder which should be updated to represent the contents of the
         *        item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            //Asignamos el dato del array correspondiente a la posición actual al
            //objeto ViewHolder, de forma que se represente en el RecyclerView.
            ((AdaptadorNoticiasParaBorrar.NoticiaParaBorrarViewHolder) holder).bindNoticia(listadoNoticiasParaBorrar.get(position));
            //Si detectamos un click, hacemos que el atributo "posicion" del Adaptador
            //sea igual a la posición del elemento del RecyclerView donde se haga el click.
            //Así conseguimos guardar el elemento sobre el que tenemos que actuar.

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setPosicion(holder.getAbsoluteAdapterPosition());
                    listaPosicionesSeleccionadas.add(posicion);
//                    Intent intent = new Intent(contexto, FichaActivity.class);
//                    intent.putExtra("ID", posicion);
//                    intent.putExtra("esParaActualizar",true);
//                    contexto.startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {

            //Devolvemos el tamaño del listadoNoticias
            return listadoNoticiasParaBorrar.size();
        }

        public class NoticiaParaBorrarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

            private ImageView iconoTrueFalse;

            private TextView numeroNoticia;

            private TextView titularNoticia;


            public NoticiaParaBorrarViewHolder(@NonNull View itemView) {
                super(itemView);
                iconoTrueFalse = itemView.findViewById(R.id.iconoRecView);
                numeroNoticia = itemView.findViewById(R.id.tvNumeroNoticia);
                titularNoticia = itemView.findViewById(R.id.tvTitularNoticia);
                //Asignamos al Layout de cada fila que obtenga el evento onClick
                itemView.setOnClickListener(this);



            }
            public void bindNoticia(Noticia noticia) {
                if(noticia.isFiable()){
                    iconoTrueFalse.setImageResource(R.drawable.ok_icon);
                } else {
                    iconoTrueFalse.setImageResource(R.drawable.no_ok_icon);
                }
                if(noticia.isFavorita()){

                    numeroNoticia.setBackgroundColor(ContextCompat.getColor(contexto,R.color.amarillo));
                } else {
                    numeroNoticia.setBackgroundColor(Color.WHITE);
                }

                if(noticia.isLeida()){
                    titularNoticia.setTextAppearance(R.style.TitleNormal);
                } else {
                    titularNoticia.setTextAppearance(R.style.TitleBold);
                }
                numeroNoticia.setText(String.valueOf(noticia.getUid())+"º");
                titularNoticia.setText(noticia.getTitular().toString());
            }
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(contexto, FichaActivity.class);
                intent.putExtra("ID", posicion);
                contexto.startActivity(intent);
            }
        }
    }


