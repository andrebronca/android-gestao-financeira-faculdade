package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.Constantes.PercentFormatter;
import br.com.andrebronca.pluscontrol.dao.RelatorioDAO;
import br.com.andrebronca.pluscontrol.modelo.RelatorioDespCategoria;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

/*
https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Data
 */
public class RelatorioDespesasCategoria extends AppCompatActivity {
    private PieChart pieChart;
    private TextView tvData;
    private Intent intent;
    private Bundle params = new Bundle();
    private String tipo_rel, sql_receita, sql_despesa, sql_grafico;
    private List<String> nome_categoria;    //, cor_categoria;
    private List<Float> valor_categoria;
    private ArrayList<Integer> cores = new ArrayList<>();
    private float soma_total = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_despesas_categoria);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pieChart = (PieChart) findViewById(R.id.pie_chart);
        tvData = (TextView) findViewById(R.id.relatorio_cabecalho_data);

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                tipo_rel = params.getString("rel_tipo");
                sql_grafico = params.getString("sql_grafico");
//                sql_receita = params.getString("sql_receita");
//                sql_despesa = params.getString("sql_despesa");
            }
        }
        linkToXml();

        if (params.getString("data") != null) {
            tvData.setText("Período de pesquisa: " + params.getString("data"));
        } else {
            tvData.setText("");
        }
        getValoresToGraph();
        preencherGraficoSetorial();
    }

    private void linkToXml() {
        tvData = (TextView) findViewById(R.id.relatorio_cabecalho_data);
    }

    private void preencherGraficoSetorial() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) pieChart.getLayoutParams();
        lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
        lp.height = LinearLayout.LayoutParams.MATCH_PARENT;
        pieChart.setLayoutParams(lp);

        List<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < nome_categoria.size(); i++) {
            PieEntry pieEntry = new PieEntry(getPercentual(valor_categoria.get(i)), nome_categoria.get(i));
            entries.add(pieEntry);
        }
        String titulo = (tipo_rel.equals("R")) ? "Categoria Receitas" : "Categoria Despesas";
        PieDataSet set = new PieDataSet(entries, titulo);
        set.setValueFormatter(new PercentFormatter());
        set.setColors(cores);

        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.invalidate();  //refresh
    }

    private void getValoresToGraph() {
        RelatorioDAO rel = RelatorioDAO.getInstance(this);
        List<RelatorioDespCategoria> obj_categorias = rel.getTituloRelatorioDespCatBySQL(sql_grafico, null);
//        List<RelatorioDespCategoria> obj_receita = rel.getTituloRelatorioDespCatBySQL(sql_receita, null);
//        List<RelatorioDespCategoria> obj_despesa = rel.getTituloRelatorioDespCatBySQL(sql_despesa, null);
//        setValoresToList(obj_receita, obj_despesa);
        setValoresToList(obj_categorias);
    }

    private void setValoresToList(List<RelatorioDespCategoria> objCategorias) {
        nome_categoria = new ArrayList<>();
        valor_categoria = new ArrayList<>();
        //get descricoes
        for (RelatorioDespCategoria obj : objCategorias) {
            nome_categoria.add(obj.getRelDsCategoria());
        }
        //get valores
        for (RelatorioDespCategoria obj : objCategorias) {
            valor_categoria.add((float) obj.getRelVlTotal());
        }
        //get cores
        for (RelatorioDespCategoria obj : objCategorias) {
            cores.add(Util.getCorIntRGB(obj.getRelCdCor()));
        }
        somaTodosValores();
    }

    private void somaTodosValores() {
        for (float vl : valor_categoria) {
            soma_total += vl;
        }
    }

    private float getPercentual(float valor) {
        return (valor * 100) / soma_total;
    }

}