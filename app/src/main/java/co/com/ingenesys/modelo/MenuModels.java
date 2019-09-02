package co.com.ingenesys.modelo;

import java.util.ArrayList;

import co.com.ingenesys.R;

/**
 * Envoltura para generar una lista para el menu
 */
public class MenuModels {
    private int isDrawable;
    private String texto;

    public MenuModels(int isDrawable, String texto) {
        this.isDrawable = isDrawable;
        this.texto = texto;
    }

    //getter y setter

    public int getIsDrawable() {
        return isDrawable;
    }

    public void setIsDrawable(int isDrawable) {
        this.isDrawable = isDrawable;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }


    public static final String[] newTexto = {"Inicio", "Perfil", "Acerca de","Cerrar Sesion"};
    public static final int[] newDrawable = {R.drawable.ic_home_light, R.drawable.ic_person_light, R.drawable.ic_acerca_de_light, R.drawable.ic_power_light};

    /**
     * Genera una lista de objetos
     * @return Lista
     */
    public static ArrayList<MenuModels> listaMenu(){
        ArrayList<MenuModels> items = new ArrayList<>();

        int tamaño  = newDrawable.length;
        int i = 0;

        while (items.size() < tamaño){
            items.add(new MenuModels(newDrawable[i], newTexto[i]));
            i++;
        }

        return new ArrayList<>(items);
    }
}
