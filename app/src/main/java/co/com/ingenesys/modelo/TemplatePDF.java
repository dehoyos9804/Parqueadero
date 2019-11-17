package co.com.ingenesys.modelo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.slf4j.helpers.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;

import co.com.ingenesys.fragment.MisConveniosFragment;
import co.com.ingenesys.ui.InitialAdministradorActivity;
import co.com.ingenesys.utils.Utilidades;

public class TemplatePDF {
    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.BOLD);
    private Font fSubTitle = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
    private Font fText = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD, BaseColor.RED);

    //constructor
    public TemplatePDF(Context context) {
        this.context = context;
    }

    //abrir documento
    public void openDocument(){
        createFile();
        try {
            document = new Document(PageSize.A4);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();//abrimos el documento
        }catch (Exception e){
            Log.e("openDocument", e.getLocalizedMessage());
        }
    }

    //crear un folder
    private void createFile(){
        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");

        if(!folder.exists())
            folder.mkdirs();
        pdfFile = new File(folder, "TemplatePDF.pdf");
    }

    //cerrar documento
    public void closeDocument(){
        document.close();
    }

    //agregar metadatos
    public void addMetaData(String title, String subject, String author){
        document.addTitle(title);
        document.addSubject(subject);
        document.addAuthor(author);
    }

    public void addTitles(String title, String subTitle, String date){
        try {
            paragraph = new Paragraph();
            addChildP(new Paragraph(title, fTitle));
            addChildP(new Paragraph(subTitle, fSubTitle));
            addChildP(new Paragraph("Generado" + date, fHighText));
            paragraph.setSpacingAfter(30);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("addTitle", e.getLocalizedMessage());
        }
    }

    private void addChildP(Paragraph childpaParagraph){
        childpaParagraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.add(childpaParagraph);
    }

    public void addParagraph(String text){
        try {
            paragraph = new Paragraph(text,fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        }catch (Exception e){
            Log.e("addParagraph", e.getLocalizedMessage());
        }
    }

    public void createTable(String[] header, ArrayList<String[]> clients){
        try {
            paragraph = new Paragraph();
            paragraph.setFont(fText);
            PdfPTable pdfPTable = new PdfPTable(header.length);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.setSpacingBefore(20);
            PdfPCell pdfPCell;
            int indexC = 0;
            while (indexC< header.length){
                pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubTitle));
                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfPCell.setBackgroundColor(BaseColor.GREEN);
                pdfPTable.addCell(pdfPCell);
            }

            for (int indexR = 0; indexR < clients.size(); indexR++){
                String[] row = clients.get(indexR);
                for (indexC = 0; indexC < header.length; indexC++){
                    pdfPCell = new PdfPCell(new Phrase(row[indexC]));
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setFixedHeight(40);
                    pdfPTable.addCell(pdfPCell);
                }
            }

            paragraph.add(pdfPTable);
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("createTable", e.getLocalizedMessage());
        }
    }

    public void viewPDF(){
        Intent intent  = new Intent(context, InitialAdministradorActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void appViewPDF(){
        Activity activity = (Activity) context;
        if(pdfFile.exists()){
            Uri uri = Uri.fromFile(pdfFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "application/pdf");

            try {
                activity.startActivity(intent);
            }catch (ActivityNotFoundException e){
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.adobe.reader")));
                Utilidades.showToast(activity, "No cuentas con una aplicacion para pdf");
            }
        }else{
            Utilidades.showToast(activity, "El PDF NO SE ENCONTRO");
        }
    }
}
