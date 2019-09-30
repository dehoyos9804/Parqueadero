package co.com.ingenesys.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import co.com.ingenesys.R;
import co.com.ingenesys.ui.InitialActivity;
import co.com.ingenesys.utils.Utilidades;

public class DialogoReservar {
    /**
     * Método que permite crear el dialogo de para finalizar la cita
     **/
    public static void showDialogReservar(final Activity activity, String nombreParqueadero){
        final Dialog d = new Dialog(activity);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setCancelable(false);
        d.setContentView(R.layout.dialog_reservar_cupo);

        //instancias
        TextView txtNombreParqueadero = (TextView) d.findViewById(R.id.txtNombreParqueadero);
        Button btnCancelar = (Button) d.findViewById(R.id.btnCancelar);
        Button btnReservacion = (Button) d.findViewById(R.id.btnReservacion);

        txtNombreParqueadero.setText(nombreParqueadero);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();//finalizo el dialogo
            }
        });

        btnReservacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo = new AlertDialog.Builder(activity);
                dialogo.setTitle("Reservar");
                dialogo.setMessage("¿Esta seguro que quieres reservar");
                dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Utilidades.showToast(activity, "Reservacion...");
                        d.dismiss();
                    }
                });
                dialogo.show();
            }
        });

        d.show();//abro el dialogo
    }
}
