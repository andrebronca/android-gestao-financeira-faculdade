package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class Titulos extends AppCompatActivity {
    private Button btnReceita, btnDespesa;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titulos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXml();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Titulos.this, ReceitaLista.class);
                startActivity(intent);
            }
        });

        btnDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(Titulos.this, DespesaLista.class);
                startActivity(intent);
            }
        });
    }

    private void linkToXml(){
        btnReceita = (Button) findViewById(R.id.btn_cadastro_receita);
        btnDespesa = (Button) findViewById(R.id.btn_cadastro_despesa);
    }

}