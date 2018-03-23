package br.com.andrebronca.pluscontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;

public class FornecedorDetalhes extends AppCompatActivity {
    private TextView tvNome, tvTipo, tvEmail, tvAtivo, tvDtCad, tvDtAlt;
    private Intent intent;
    private Bundle params;
    private int id_pessoa;
    private PessoaDAO pessoaDAO;
    private Pessoa pessoa;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fornecedor_detalhes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;
        params = getIntent().getExtras();
        linkToXml();
        pessoaDAO = PessoaDAO.getInstance(this, "FornecedorDetalhes_onCreate()");
        preencheDetalhes(getDadosPessoa());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(context, FornecedorEditar.class);
                intent.putExtra("id", params.getString("id"));
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void linkToXml() {
        tvNome = (TextView) findViewById(R.id.tv_fornecedor_detalhe_nome);
        tvTipo = (TextView) findViewById(R.id.tv_fornecedor_detalhe_tipo);
        tvEmail = (TextView) findViewById(R.id.tv_fornecedor_detalhe_email);
        tvAtivo = (TextView) findViewById(R.id.tv_fornecedor_detalhe_ativo);
        tvDtCad = (TextView) findViewById(R.id.tv_fornecedor_detalhe_dtcad);
        tvDtAlt = (TextView) findViewById(R.id.tv_fornecedor_detalhe_dtalt);
    }

    private Pessoa getDadosPessoa() {
        id_pessoa = Integer.parseInt(params.getString("id"));
        pessoa = pessoaDAO.getPessoaByID(id_pessoa);
        return pessoa;
    }

    private void preencheDetalhes(Pessoa pessoa) {
        tvNome.setText(pessoa.getNmPessoa());
        tvEmail.setText(pessoa.getDsEmail());
        tvTipo.setText(pessoa.getTpPessoaLongo());
        tvAtivo.setText(pessoa.getStAtivoLongo());
        tvDtCad.setText(pessoa.getDtCadastroFormat());
        tvDtAlt.setText(pessoa.getDtAlteradoFormat());
    }

}
