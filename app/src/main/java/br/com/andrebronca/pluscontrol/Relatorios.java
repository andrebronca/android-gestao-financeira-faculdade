package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class Relatorios extends AppCompatActivity {
    private Intent intent;
    private Button btnContasPagar, btnContasReceber, btnFluxoCaixa, btnDespesasCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorios);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToXml();

        btnContasPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Relatorios.this, RelatorioContasPagarFiltro.class);
                startActivity(intent);
            }
        });

        btnContasReceber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Relatorios.this, RelatorioContasReceberFiltro.class);
                startActivity(intent);
            }
        });

        btnDespesasCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Relatorios.this, RelatorioDespesasCategoriaFiltro.class);
                startActivity(intent);
            }
        });

        btnFluxoCaixa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Relatorios.this, RelatorioFluxoCaixaFiltro.class);
                startActivity(intent);
            }
        });

    }

    private void linkToXml() {
        btnDespesasCategoria = (Button) findViewById(R.id.btn_relatorio_despesas_categoria);
        btnContasPagar = (Button) findViewById(R.id.btn_relatorio_contas_pagar);
        btnContasReceber = (Button) findViewById(R.id.btn_relatorio_contas_receber);
        btnFluxoCaixa = (Button) findViewById(R.id.btn_relatorio_fluxo_caixa);
    }

}