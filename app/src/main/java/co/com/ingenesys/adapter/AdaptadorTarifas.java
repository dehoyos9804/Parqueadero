package co.com.ingenesys.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.MenuModels;
import co.com.ingenesys.modelo.Tarifa;
import co.com.ingenesys.modelo.Tarifas;
import co.com.ingenesys.modelo.TipoVehiculo;
import co.com.ingenesys.utils.Utilidades;

public class AdaptadorTarifas extends RecyclerView.Adapter<AdaptadorTarifas.ExpenseViewHolder> implements ItemClickListener{

    //Lista de objetos {@link Consultas} que representan la fuente de datos de inflado
    private List<Tarifa> items;

    //contexto donde actuá el Recicle View
    private Context context;

    //constructor de la clase Recicle View
    public AdaptadorTarifas(List<Tarifa> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        if(items != null){
            return items.size();
        }

        return 0;
    }

    @Override
    public AdaptadorTarifas.ExpenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_section_tarifas, viewGroup ,false);
        return new AdaptadorTarifas.ExpenseViewHolder(cardView, this);
    }

    @Override
    public void onBindViewHolder(final AdaptadorTarifas.ExpenseViewHolder viewHolder, int i){

        viewHolder.txtTipoTiempo.setText(items.get(i).getTipoTiempo());
        viewHolder.txtPrecio.setText("$" +items.get(i).getPrecio());
        viewHolder.txtTipoVehiculo.setText(items.get(i).getNombre());

    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //campos respectivos del items
        public ImageView icono_tipo_vehiculo;
        public TextView txtTipoVehiculo;
        public TextView txtTipoTiempo;
        public TextView txtPrecio;
        public Button btnEditar;
        public ItemClickListener listener;

        public ExpenseViewHolder(View view, ItemClickListener listener){
            super(view);

            icono_tipo_vehiculo = (ImageView) view.findViewById(R.id.icono_tipo_vehiculo);
            txtTipoVehiculo = (TextView) view.findViewById(R.id.txtTipoVehiculo);
            txtTipoTiempo = (TextView) view.findViewById(R.id.txtTipoTiempo);
            txtPrecio = (TextView) view.findViewById(R.id.txtPrecio);
            btnEditar = (Button) view.findViewById(R.id.btnEditar);

            this.listener = listener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }

}