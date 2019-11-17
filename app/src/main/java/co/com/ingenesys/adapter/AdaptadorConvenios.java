package co.com.ingenesys.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.Convenios;
import co.com.ingenesys.modelo.Tarifa;

public class AdaptadorConvenios extends RecyclerView.Adapter<AdaptadorConvenios.ExpenseViewHolder> implements ItemClickListener{

    //Lista de objetos {@link Consultas} que representan la fuente de datos de inflado
    private List<Convenios> items;

    //contexto donde actuá el Recicle View
    private Context context;

    //constructor de la clase Recicle View
    public AdaptadorConvenios(List<Convenios> items, Context context) {
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
    public AdaptadorConvenios.ExpenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_section_convenios, viewGroup ,false);
        return new AdaptadorConvenios.ExpenseViewHolder(cardView, this);
    }

    @Override
    public void onBindViewHolder(final AdaptadorConvenios.ExpenseViewHolder viewHolder, int i){

        viewHolder.txtRazonSocial.setText(items.get(i).getRazonSocial());
        viewHolder.txtNitDireccion.setText(("NIT: " +items.get(i).getNit() + " - Dir. " + items.get(i).getDireccion()));
        viewHolder.txtTelefonoConvenio.setText(items.get(i).getTelefono());
        viewHolder.txtDescuento.setText(("Descuento $" +items.get(i).getDescuento() + "%"));

    }

    @Override
    public void onItemClick(View view, int position) {

    }


    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //campos respectivos del items
        public TextView txtRazonSocial;
        public TextView txtNitDireccion;
        public TextView txtTelefonoConvenio;
        public TextView txtDescuento;
        public ItemClickListener listener;

        public ExpenseViewHolder(View view, ItemClickListener listener){
            super(view);
            txtRazonSocial = (TextView) view.findViewById(R.id.txtRazonSocial);
            txtNitDireccion = (TextView) view.findViewById(R.id.txtNitDireccion);
            txtTelefonoConvenio = (TextView) view.findViewById(R.id.txtTelefonoConvenio);
            txtDescuento = (TextView) view.findViewById(R.id.txtDescuento);

            this.listener = listener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }
}
