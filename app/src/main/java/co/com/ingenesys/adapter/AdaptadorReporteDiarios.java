package co.com.ingenesys.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.Convenios;
import co.com.ingenesys.modelo.Reportes;

public class AdaptadorReporteDiarios extends RecyclerView.Adapter<AdaptadorReporteDiarios.ExpenseViewHolder> implements ItemClickListener{

    //Lista de objetos {@link Consultas} que representan la fuente de datos de inflado
    private List<Reportes> items;

    //contexto donde actuá el Recicle View
    private Context context;

    //constructor de la clase Recicle View
    public AdaptadorReporteDiarios(List<Reportes> items, Context context) {
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
    public AdaptadorReporteDiarios.ExpenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_section_reporte, viewGroup ,false);
        return new AdaptadorReporteDiarios.ExpenseViewHolder(cardView, this);
    }

    @Override
    public void onBindViewHolder(final AdaptadorReporteDiarios.ExpenseViewHolder viewHolder, int i){

        viewHolder.txtFechaEntradaSalida.setText(("Fecha Inicial \n" + items.get(i).getFechaHoraIngreso() + "\n Fecha Salida \n" + items.get(i).getFechaHoraSalida()));
        viewHolder.txtNombreUsuario.setText((items.get(i).getNOMBRE() + " " + items.get(i).getAPELLIDO()));
        viewHolder.txtNumeroCedula.setText(("Cedula: " +items.get(i).getCEDULA()));
        viewHolder.txtNumeroVenta.setText(("Venta N°" +items.get(i).getNumeroVenta()));
        viewHolder.txtVehiculo.setText(("Tipo Vehiculo" +items.get(i).getTipovehiculo()));
        viewHolder.txtPrecioTarifa.setText(("Tarifa $" +items.get(i).getPrecioTarifa() + " - SubT. $" +items.get(i).getDescuentototal()));
        viewHolder.txtDescuento.setText(("Descuento: " +items.get(i).getDescuento() + "%"));

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //campos respectivos del items
        public TextView txtFechaEntradaSalida;
        public TextView txtNombreUsuario;
        public TextView txtNumeroCedula;
        public TextView txtNumeroVenta;
        public TextView txtVehiculo;
        public TextView txtPrecioTarifa;
        public TextView txtDescuento;
        public ItemClickListener listener;

        public ExpenseViewHolder(View view, ItemClickListener listener){
            super(view);
            txtFechaEntradaSalida = (TextView) view.findViewById(R.id.txtFechaEntradaSalida);
            txtNombreUsuario = (TextView) view.findViewById(R.id.txtNombreUsuario);
            txtNumeroCedula = (TextView) view.findViewById(R.id.txtNumeroCedula);
            txtNumeroVenta = (TextView) view.findViewById(R.id.txtNumeroVenta);
            txtVehiculo = (TextView) view.findViewById(R.id.txtVehiculo);
            txtPrecioTarifa = (TextView) view.findViewById(R.id.txtPrecioTarifa);
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
