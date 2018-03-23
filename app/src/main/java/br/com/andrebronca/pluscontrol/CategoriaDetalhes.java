package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class CategoriaDetalhes extends AppCompatActivity {
    private TextView tvDescricao, tvTipo, tvCor, tvAtivo, tvDtCad, tvDtAlt;
    private Intent intent;
    private Bundle params = new Bundle();
    private int id_categoria;
    private CategoriaDAO categoriaDAO;
    private Categoria categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria_detalhes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXML();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        categoriaDAO = CategoriaDAO.getInstance(this);

        params = getIntent().getExtras();

        preencheDetalhes(getDadosCategoria());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(CategoriaDetalhes.this, CategoriaEditar.class);
                intent.putExtra("id", params.getString("id")); //todo arrumar isso depois
                startActivity(intent);
            }
        });
    }

    private void linkToXML() {
        tvDescricao = (TextView) findViewById(R.id.tv_categoria_detalhe_descricao);
        tvTipo = (TextView) findViewById(R.id.tv_categoria_detalhe_tipo);
        tvCor = (TextView) findViewById(R.id.tv_categoria_detalhe_cor);
        tvAtivo = (TextView) findViewById(R.id.tv_categoria_detalhe_ativo);
        tvDtCad = (TextView) findViewById(R.id.tv_categoria_detalhe_dtcad);
        tvDtAlt = (TextView) findViewById(R.id.tv_categoria_detalhe_dtalt);
    }

    private Categoria getDadosCategoria() {
        id_categoria = Integer.parseInt(params.getString("id"));
        categoria = categoriaDAO.getCategoriaById(id_categoria);
        return categoria;
    }

    private void preencheDetalhes(Categoria categoria) {
        tvDescricao.setText(categoria.getDsCategoria());
        tvTipo.setText(categoria.getTpCategoriaLong());
        Util.setCorCategoria(categoria.getCdCor(), tvCor);
        tvAtivo.setText(categoria.getStAtivoLong());
        tvDtCad.setText(categoria.getDtCadastroFormat());
        tvDtAlt.setText(categoria.getDtAlteradoFormat());
    }

}