package co.com.ingenesys.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

import co.com.ingenesys.R;

public class ViewPDFActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        pdfView = (PDFView) findViewById(R.id.pdfView);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            file = new File(bundle.getString("path", ""));
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(true)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }
}
