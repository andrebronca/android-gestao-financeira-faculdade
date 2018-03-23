package br.com.andrebronca.pluscontrol.Constantes;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 23/10/16.
 * Acessado em: 23/10/2016 : https://www.youtube.com/watch?v=dAUPhsyhuZY
 * Conteúdo foi adaptado para ser utilizado em várias telas
 */

public class GerarPDF {
    private static final String PROJETO_APP = "pluscontrol";
    private static final String SUBDIRETORIO = "ArquivosPDF";
    private Context context;
    private String conteudo;
    private String nomeArquivo;
    private String autor;
    private String titulo;

    public GerarPDF(Context context, String nome_arquivo, String conteudo) {
        this.context = context;
        setNomeArquivoPDF(nome_arquivo);
        setConteudoPDF(conteudo);
    }

    private String getConteudoPDF() {
        return conteudo;
    }

    private void setConteudoPDF(String conteudo) {
        this.conteudo = conteudo;
    }

    private String getNomeArquivoPDF() {
        return nomeArquivo + ".pdf";
    }

    private void setNomeArquivoPDF(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getAutorPDF() {
        return autor;
    }

    public void setAutorPDF(String autor) {
        this.autor = autor;
    }

    private String getTituloPDF() {
        return titulo;
    }

    public void setTituloPDF(String titulo) {
        this.titulo = titulo;
    }

    public void gerarArquivoPDF() {
        Document document = new Document(PageSize.A4);
        //String NOMBRE_ARCHIVO = "MiArchivoPDF.pdf";
        String cartaoSD = Environment.getExternalStorageDirectory().toString();
        File pdfDir = new File(cartaoSD + File.separator + PROJETO_APP);
        if (!pdfDir.exists()) {
            pdfDir.mkdir();
        }

        File pdfSubDir = new File(pdfDir.getPath() + File.separator + SUBDIRETORIO);
        if (!pdfSubDir.exists()) {
            pdfSubDir.mkdir();
        }

        String nombre_completo = Environment.getExternalStorageDirectory() + File.separator +
                PROJETO_APP + File.separator + SUBDIRETORIO + File.separator + getNomeArquivoPDF();

        File outputfile = new File(nombre_completo);
        if (outputfile.exists()) {
            outputfile.delete();
        }

        try {
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(nombre_completo));

            document.open();
            if (autor != null) {
                document.addAuthor(getAutorPDF());
            }
            if (titulo != null) {
                document.addTitle(getTituloPDF());
            }
//            document.addCreator("PlusControl");
//            document.addSubject("Gestão Financeira Pessoal");
            document.addCreationDate();

            XMLWorkerHelper worker = XMLWorkerHelper.getInstance();

            try {
                worker.parseXHtml(pdfWriter, document, new StringReader(getConteudoPDF()));
                document.close();
                exibePDF(nombre_completo);
            } catch (IOException e) {
                e.getMessage();
                e.getCause();
                //e.printStackTrace();
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void exibePDF(String archivo) {
        File file = new File(archivo);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Util.exibeMensagem(context, "Falta leitor de PDF");
            e.printStackTrace();
        }
    }
}
