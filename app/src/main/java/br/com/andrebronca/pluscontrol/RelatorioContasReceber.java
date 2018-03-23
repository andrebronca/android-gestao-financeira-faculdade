package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import br.com.andrebronca.pluscontrol.Constantes.GerarPDF;
import br.com.andrebronca.pluscontrol.dao.RelatorioDAO;
import br.com.andrebronca.pluscontrol.modelo.RelatorioContasPagarReceberMod;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RelatorioContasReceber extends AppCompatActivity {
    private TextView tvPeriodo;
    private Button btnExportar;
    private WebView webView;
    private Intent intent;
    private Bundle params = new Bundle();
    private String sql_contas;
    private List<RelatorioContasPagarReceberMod> objContas;
    private double totalPeriodo = 0.0;
    private final String TITULO_RELATORIO = "Contas a Receber";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_contas_receber);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                sql_contas = params.getString("sql_contas");
                tvPeriodo.setText("Período: " + params.getString("data"));
            }
        }

        getValoresToWebView();
        getTotalContas();
        setParamsToWebView();

        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GerarPDF gerarPDF = new GerarPDF(RelatorioContasReceber.this, "ContasReceber", getHtmlToWebView());
                gerarPDF.gerarArquivoPDF();
            }
        });
    }

    private void linkToXml() {
        tvPeriodo = (TextView) findViewById(R.id.tv_contas_receber_cab_periodo);
        btnExportar = (Button) findViewById(R.id.btn_contas_receber_exportar_pdf);
        webView = (WebView) findViewById(R.id.webview_contas_receber);
    }

    private void getValoresToWebView() {
        RelatorioDAO rel = RelatorioDAO.getInstance(this);
        objContas = rel.getTituloContasPagarReceberBySQL(sql_contas, null);
    }

    private void getTotalContas() {
        for (RelatorioContasPagarReceberMod obj : objContas) {
            totalPeriodo += obj.getVlSaldo();
        }
    }

    private void setParamsToWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
        webView.loadData(getHtmlToWebView(), "text/html; charset=utf-8", "utf-8");
    }

    private String getHtmlToWebView() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("<head>");
//        sb.append(" <meta charset=\"utf-8\" />");
        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        sb.append("<style>");
        sb.append("th, td { border-bottom: 1px solid #ddd; }");
        sb.append("th { background-color: #4CAF50; color: #000; padding: 2px; margin: 2px}");
        //retirado o zebrado
        if (objContas.size() > 10) {
            sb.append("tr:nth-child(even) {background-color: #C6C2C2}");  //cinza
        }
//        sb.append("tr:nth-child(even) {background-color: #B0E982}"); //verde
        sb.append("</style>");
        sb.append("</head>");
        sb.append("<body>");
        sb.append("<center><h3>" + TITULO_RELATORIO + "</h3></center>");
        sb.append("<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"2\">");
//        sb.append("<caption></caption>");
        sb.append("<tr>");
        sb.append(" <th style=\"font-size: x-small\">VENCIMENTO</th>");
        sb.append(" <th style=\"font-size: x-small\">TÍTULO</th>");
        sb.append(" <th style=\"font-size: x-small\">CATEGORIA</th>");
        sb.append(" <th style=\"font-size: x-small\">PESSOA</th>");
        sb.append(" <th style=\"font-size: x-small\">SALDO</th>");
        sb.append("</tr>");
        for (int i = 0; i < objContas.size(); i++) {
            sb.append("<tr>");
            sb.append("<td style=\"font-size: 5pt\">" + Util.dateToStringFormat(objContas.get(i).getDtVencimento()) + "</td>");
            sb.append("<td style=\"font-size: 5pt\">" + objContas.get(i).getDsTitulo() + "</td>");
            sb.append("<td style=\"font-size: 5pt\">" + objContas.get(i).getDsCategoria() + "</td>");
            sb.append("<td style=\"font-size: 5pt\">" + objContas.get(i).getNmPessoa() + "</td>");
            sb.append("<td align=\"right\" style=\"font-size: xx-small\">" + String.format("R$ %.2f", objContas.get(i).getVlSaldo()) + "</td>");
            sb.append("</tr>");
        }
        sb.append("<tr>");
        sb.append("<td colspan=\"4\" style=\"font-size: x-small\"><strong>Total</strong></td>");
        sb.append("<td align=\"right\" style=\"font-size: x-small\"><strong>" + String.format("R$ %.2f", totalPeriodo) + "</strong></td>");
        sb.append("</tr>");
        sb.append("</table>");
        sb.append("<br /><br />");
        sb.append("<hr />");
        sb.append("<strong><center>PlusControl</center></strong>");
        sb.append("<br />");
        sb.append("<p align=\"right\" style=\"font-size: x-small\">" + Util.getDataAtual() + "</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
}