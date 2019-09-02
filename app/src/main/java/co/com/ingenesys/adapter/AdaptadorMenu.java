package co.com.ingenesys.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.com.ingenesys.R;
import co.com.ingenesys.modelo.MenuModels;

public class AdaptadorMenu extends RecyclerView.Adapter<AdaptadorMenu.ExpenseViewHolder> implements ItemClickListener {

    //Lista de objetos {@link Consultas} que representan la fuente de datos de inflado
    private List<MenuModels> items;

    //contexto donde actuá el Recicle View
    private Context context;

    //constructor de la clase Recicle View
    public AdaptadorMenu(List<MenuModels> items, Context context) {
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
    public AdaptadorMenu.ExpenseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View cardView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_section_menu, viewGroup ,false);
        return new AdaptadorMenu.ExpenseViewHolder(cardView,this);
    }

    @Override
    public void onBindViewHolder(final AdaptadorMenu.ExpenseViewHolder viewHolder, int i){
        viewHolder.txtTextoMenu.setText(items.get(i).getTexto());
        //colocar los drawables
        Glide.with(viewHolder.imgIcon.getContext())
                .load(items.get(i).getIsDrawable())
                .centerCrop()
                .into(viewHolder.imgIcon);
    }

    @Override
    public void onItemClick(View view, int position) {
        //DetailConsultaActivity.launch((Activity) context, items.get(position).getConsecutivo());
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //campos respectivos del items
        public ImageView imgIcon;
        public TextView txtTextoMenu;
        public ItemClickListener listener;

        public ExpenseViewHolder(View view, ItemClickListener listener){
            super(view);

            imgIcon = (ImageView) view.findViewById(R.id.imgIcon);
            txtTextoMenu = (TextView) view.findViewById(R.id.txtTextoMenu);
            this.listener = listener;

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onItemClick(view, getAdapterPosition());
        }
    }
}