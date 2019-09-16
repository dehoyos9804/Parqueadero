package co.com.ingenesys.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;

import co.com.ingenesys.R;

public class PruebaFragment extends Fragment {
    //Etiqueta de depuracion
    private static final String TAG = PruebaFragment.class.getSimpleName();

    private BottomSheetBehavior sheetBehavior;
    private Button btn_bottom_sheet;
    private LinearLayout bottom_sheet;

    //costructor del fragmento
    public PruebaFragment() {

    }

    /**
     * Crea una instancia prefabricada de {@link PruebaFragment}
     *
     * @return Instancia dle fragmento
     */
    public static PruebaFragment newInstance() {
        PruebaFragment fragment = new PruebaFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //View view = super.onCreateView(inflater, container, savedInstanceState);
         View view = inflater.inflate(R.layout.fragment_menu_prueba, container, false);

        btn_bottom_sheet = (Button) view.findViewById(R.id.btn_bottom_sheet);
        bottom_sheet = (LinearLayout) view.findViewById(R.id.bottom_shent);

        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);

        btn_bottom_sheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    btn_bottom_sheet.setText("Close sheet");
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    btn_bottom_sheet.setText("Expand sheet");
                }
            }
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        btn_bottom_sheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        btn_bottom_sheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        return view;
    }
}
