package br.com.andrebronca.pluscontrol;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class MainActivity extends AppCompatActivity {
    private Button btnCadastro, btnReceitaDespesa, btnMovimentos;
    private Intent intent;
    private TextView tvReceitaVencer, tvReceitaAVencer;
    private TextView tvDespesaVencer, tvDespesaAVencer, tvSaldoTotalContas;
    private TextView tvTotalReceitaPeriodo, tvTotalDespesaPeriodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("PlusControl");
        linkToXML();

        preencherReceitaVencida();
        preencherReceitaAVencer();
        preencherDespesaVencida();
        preencherDespesaAVencer();
        preencherSaldoTotalContas();
        preencherTotalReceitaPeriodo();
        preencherTotalDespesaPeriodo();

        //todo: valor total de receita e despesa do mes atual (alterar tab titulo, adicionar vl_parcela (fixo)

        btnCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, Cadastros.class);
                startActivity(intent);
            }
        });

        btnReceitaDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaSeTemCategoria() && verificaSeTemPessoa()) {
                    intent = new Intent(MainActivity.this, Titulos.class);
                    startActivity(intent);
                } else {
                    Util.exibeMensagem(MainActivity.this, "É preciso realizar os cadastros de:\nCategoria\nCliente/Fornecedor\nConta");
                }
            }
        });

        btnMovimentos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaSeTemConta() && verificaSeTemTitulo()) {
                    intent = new Intent(MainActivity.this, Movimentos.class);
                    startActivity(intent);
                } else {
                    Util.exibeMensagem(MainActivity.this, "É preciso ter Conta e Receita / Despesa cadastrados!");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sobre) {
            intent = new Intent(MainActivity.this, Sobre.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_relatorios) {
            intent = new Intent(MainActivity.this, Relatorios.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_backup) {
            intent = new Intent(MainActivity.this, Backup.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_usuario) {
            intent = new Intent(MainActivity.this, Usuario.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void linkToXML() {
        btnCadastro = (Button) findViewById(R.id.btn_main_cadastro);
        btnReceitaDespesa = (Button) findViewById(R.id.btn_main_despesa_receita);
        btnMovimentos = (Button) findViewById(R.id.btn_main_movimento);
        tvReceitaVencer = (TextView) findViewById(R.id.tv_main_receita_vencida);
        tvReceitaAVencer = (TextView) findViewById(R.id.tv_main_receita_avencer);
        tvDespesaVencer = (TextView) findViewById(R.id.tv_main_despesa_vencida);
        tvDespesaAVencer = (TextView) findViewById(R.id.tv_main_despesa_avencer);
        tvSaldoTotalContas = (TextView) findViewById(R.id.tv_main_saldo_total_contas);
        tvTotalReceitaPeriodo = (TextView) findViewById(R.id.main_total_receita_periodo);
        tvTotalDespesaPeriodo = (TextView) findViewById(R.id.main_total_despesa_periodo);
    }

    private boolean verificaSeTemCategoria() {
        CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(this);
        int total = categoriaDAO.getTotalCategoria();
        return total > 0;
    }

    private boolean verificaSeTemPessoa() {
        PessoaDAO pessoaDAO = PessoaDAO.getInstance(MainActivity.this, "MainActivity_verificaSeTemPessoa()");
        int total = pessoaDAO.getTotalPessoa();
        return total > 0;
    }

    /**
     * para lançar movimento é necessário ter conta cadastada
     * é necessário ter receita cadastrada
     *
     * @return
     */
    private boolean verificaSeTemConta() {
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        int total = contaDAO.getTotalConta();
        return total > 0;
    }

    private boolean verificaSeTemTitulo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(this);
        PessoaDAO pessoaDAO = PessoaDAO.getInstance(this, "MainActivity_verificaSeTemTitulo()");
        int total = tituloDAO.getTotalTitulo("MainActivity_verificaSeTemTitulo()"); //todo: cuidar aqui, pois lista até os já baixados
        return total > 0;
    }

    private void preencherSaldoTotalContas() {

        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        double vlTotal = tituloDAO.getSaldoTotalContas();
        if (vlTotal < 0.0) {
            tvSaldoTotalContas.setTextColor(Color.RED);
        } else {
            tvSaldoTotalContas.setTextColor(Color.BLUE);
        }

        tvSaldoTotalContas.setText(String.format("R$ %.2f", vlTotal));
    }

    private void preencherReceitaVencida() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        tvReceitaVencer.setText(String.format("R$ %.2f", tituloDAO.getValorReceitaDespesaVencidoVencer(true, "R")));
    }

    private void preencherReceitaAVencer() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        tvReceitaAVencer.setText(String.format("R$ %.2f", tituloDAO.getValorReceitaDespesaVencidoVencer(false, "R")));
    }

    private void preencherDespesaVencida() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        tvDespesaVencer.setText(String.format("R$ %.2f", tituloDAO.getValorReceitaDespesaVencidoVencer(true, "D")));
    }

    private void preencherDespesaAVencer() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        tvDespesaAVencer.setText(String.format("R$ %.2f", tituloDAO.getValorReceitaDespesaVencidoVencer(false, "D")));
    }

    private void preencherTotalReceitaPeriodo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        double vlTotal = tituloDAO.getTotalDoPeriodo("R");
        if (vlTotal < 0.0) {
            tvTotalReceitaPeriodo.setTextColor(Color.RED);
        } else {
            tvTotalReceitaPeriodo.setTextColor(Color.BLUE);
        }

        tvTotalReceitaPeriodo.setText(String.format("R$ %.2f", vlTotal));

    }

    private void preencherTotalDespesaPeriodo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        double vlTotal = tituloDAO.getTotalDoPeriodo("D");
        if (vlTotal < 0.0) {
            tvTotalDespesaPeriodo.setTextColor(Color.RED);
        } else {
            tvTotalDespesaPeriodo.setTextColor(Color.BLUE);
        }

        tvTotalDespesaPeriodo.setText(String.format("R$ %.2f", vlTotal));
    }

}