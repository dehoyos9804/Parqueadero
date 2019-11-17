package co.com.ingenesys.modelo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**clase que permite serializar la imagen enviada desde el servidor*/
public class Imagen {
    private String Foto;
    private Bitmap imagen;

    //constructor
    public Imagen(String Foto) {
        this.Foto = Foto;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String Foto) {
        this.Foto = Foto;
    }

    public Bitmap getImagen() {
        try {
            byte[] bytes = Base64.decode(this.Foto, Base64.DEFAULT);
            this.imagen = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }
}
