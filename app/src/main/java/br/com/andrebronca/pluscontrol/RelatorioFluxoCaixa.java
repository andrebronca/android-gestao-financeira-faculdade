package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.Constantes.GerarPDF;
import br.com.andrebronca.pluscontrol.dao.RelatorioDAO;
import br.com.andrebronca.pluscontrol.modelo.RelatorioFluxoCaixaMod;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RelatorioFluxoCaixa extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private String dataPesquisa;
    private String sql_movimentos;
    private TextView tvPeriodo;
    private Button btnExportar;
    private WebView webView;
    private List<RelatorioFluxoCaixaMod> objFluxoCaixa;
    private final String TITULO_RELATORIO = "Fluxo de Caixa";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_fluxo_caixa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                sql_movimentos = params.getString("sql_movimentos");
                tvPeriodo.setText("Período: " + params.getString("data"));
            }
        }

        getValoresToWebView();
        setParamsToWebView();

        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GerarPDF gerarPDF = new GerarPDF(RelatorioFluxoCaixa.this, "FluxoCaixa", getHtmlToWebView());
                gerarPDF.gerarArquivoPDF();
            }
        });
    }

    private void linkToXml() {
        tvPeriodo = (TextView) findViewById(R.id.tv_fluxo_caixa_cab_periodo);
        btnExportar = (Button) findViewById(R.id.btn_fluxo_caixa_exportar_pdf);
        webView = (WebView) findViewById(R.id.webview_fluxo_caixa);
    }

    private void getValoresToWebView() {
        RelatorioDAO rel = RelatorioDAO.getInstance(this);
        objFluxoCaixa = rel.getMovimentoFluxoBySQL(sql_movimentos, null);
    }

    private void setParamsToWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setSupportZoom(false);
//        webSettings.setDefaultTextEncodingName("utf-8");
        webView.loadData(getHtmlToWebView(), "text/html; charset=utf-8", "utf-8");
    }

    private String getHtmlToWebView() {
        StringBuilder sb = new StringBuilder();

        List<Double> vlEntrada = new ArrayList<>();
        List<Double> vlSaida = new ArrayList<>();
        double saldoTotal;
        double saldoMovimento = 0.0;
        String valorSaldo = null;

        sb.append("<html>");
        sb.append("<head>");
//        sb.append(" <meta charset=\"utf-8\" />");
//        sb.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">");
        sb.append("<style>");
        sb.append("th, td { border-bottom: 1px solid #ddd; }");
        sb.append("th { background-color: #4CAF50; color: #000; padding: 2px; margin: 2px}");
//        sb.append(".entrada { color: #0A227B }");
        //retirado o zebrado
        if (objFluxoCaixa.size() > 10) {
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
        sb.append(" <th style=\"font-size: x-small\">DATA</th>");
        sb.append(" <th style=\"font-size: x-small\">HISTÓRICO</th>");
        sb.append(" <th style=\"font-size: x-small\">TIPO</th>");
        sb.append(" <th style=\"font-size: x-small\">ENTRADA</th>");
        sb.append(" <th style=\"font-size: x-small\">SAÍDA</th>");
        sb.append(" <th style=\"font-size: x-small\">SALDO</th>");
        sb.append("</tr>");
        for (int i = 0; i < objFluxoCaixa.size(); i++) {
            sb.append("<tr>");
            sb.append("<td style=\"font-size: 5pt\">" + Util.dateToStringFormat(objFluxoCaixa.get(i).getDtMovimento()) + "</td>"); //ok
            sb.append("<td style=\"font-size: 5pt\">" + objFluxoCaixa.get(i).getDsHistorico() + "</td>");
            sb.append("<td style=\"font-size: 5pt\">" + objFluxoCaixa.get(i).getStTipoLongo() + "</td>");

            double valor = objFluxoCaixa.get(i).getVlMovimento();

            String valorEntrada = null;
            String valorSaida = null;
            String direcao = objFluxoCaixa.get(i).getStDirecao();
            if (direcao.equals("E")) {
                vlEntrada.add(valor);
                saldoMovimento += valor;
                valorEntrada = String.format("R$ %.2f", valor);
                valorSaida = "--";
            } else {
                vlEntrada.add(valor);
                saldoMovimento -= valor;
                valorSaida = String.format("R$ %.2f", valor);
                valorEntrada = "--";
            }
            sb.append("<td align=\"right\" style=\"font-size: 5pt; color: #0A227B;\">" + valorEntrada + "</td>");
            sb.append("<td align=\"right\" style=\"font-size: 5pt; color: #D20E0E;\">" + valorSaida + "</td>");
            sb.append("<td align=\"right\" style=\"font-size: 5pt\">" + String.format("R$ %.2f", saldoMovimento) + "</td>");
            sb.append("</tr>");
        }
        sb.append("<tr>");
        sb.append("<td colspan=\"5\" style=\"font-size: x-small\"><strong>Total</strong></td>");
        String corTotal = (saldoMovimento > 0.0) ? "0A227B" : "D20E0E";
        sb.append("<td align=\"right\" style=\"font-size: x-small; color: #" + corTotal + "\"><strong>" + String.format("R$ %.2f", saldoMovimento) + "</strong></td>");
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

//    private double getSaldoTotal(List<Double> entradas, List<Double> saidas){
//        double somaEntrada = 0.0;
//        double somaSaida = 0.0;
//
//        for(double valor : entradas){
//            somaEntrada += valor;
//        }
//
//        for(double valor : saidas){
//            somaSaida += valor;
//        }
//
//        return somaEntrada - somaSaida;
//    }
}