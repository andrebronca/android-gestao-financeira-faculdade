package br.com.andrebronca.pluscontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.dao.MovimentoContaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class EstornoDetalhesConfirmar extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private int id_movconta, id_conta, id_titulo, id_parcela;
    private TextView tvConta, tvTitulo, tvQtdParcela, tvNrParcela;
    private TextView tvValor, tvData, tvHistorico, tvTipo;
    //    private TextView tvDetalhes;    //usado para "debug"
    private Button btnEstorno;
    private MovimentoConta movimentoConta;
    private String flagTipo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estorno_detalhes_confirmar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                id_movconta = params.getInt("id_movconta");
            }
        }

        linkToXml();
        getMovimentoConta();
        preencherDetalhes();

        btnEstorno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(EstornoDetalhesConfirmar.this);
                alerta
                        .setTitle("Atenção")
                        .setIcon(R.drawable.ic_estorno_24dp)
                        .setCancelable(false)
                        .setMessage("Deseja estornar o movimento?")
                        .setPositiveButton("Estornar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MovimentoContaDAO movContaDao = MovimentoContaDAO.getInstance(EstornoDetalhesConfirmar.this);
                                if (movContaDao.movimentoEstorno(getSqlExecutar())) {
                                    Util.exibeMensagem(EstornoDetalhesConfirmar.this, "Estorno realizado com sucesso!");
                                    fecharActivity();
                                } else {
                                    Util.exibeMensagem(EstornoDetalhesConfirmar.this, "Ocorreu alguma falha no estorno");
                                }
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alertDialog = alerta.create();
                alertDialog.show();
            }
        });

    }

    private void linkToXml() {
        tvTipo = (TextView) findViewById(R.id.tv_estorno_detalhe_tipo);
        tvConta = (TextView) findViewById(R.id.tv_estorno_detalhe_conta);
        tvTitulo = (TextView) findViewById(R.id.tv_estorno_detalhe_titulo);
        tvQtdParcela = (TextView) findViewById(R.id.tv_estorno_detalhe_qtdparcela_titulo);
        tvNrParcela = (TextView) findViewById(R.id.tv_estorno_detalhe_nr_parcela);
        tvValor = (TextView) findViewById(R.id.tv_estorno_detalhe_valor);
        tvData = (TextView) findViewById(R.id.tv_estorno_detalhe_data);
        tvHistorico = (TextView) findViewById(R.id.tv_estorno_detalhe_historico);
        btnEstorno = (Button) findViewById(R.id.btn_estorno_detalhe_confirmar);
    }

    private void getMovimentoConta() {
        MovimentoContaDAO movimentoContaDAO = MovimentoContaDAO.getInstance(this);
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        movimentoConta = movimentoContaDAO.getMovimentoByID(id_movconta, this);
        id_conta = movimentoConta.getIdConta();
        id_titulo = movimentoConta.getIdTitulo();
        id_parcela = movimentoConta.getIdParcela();
        flagTipo = movimentoConta.getStTipo();
    }

    private void preencherDetalhes() {
        tvTipo.setText(movimentoConta.getStTipoLongo());
        tvConta.setText(movimentoConta.getConta().getDsConta());
        if (flagTipo.equals("R") || flagTipo.equals("P")) {
            String descricao = movimentoConta.getTitulo().getDsTitulo();
            tvTitulo.setText( descricao != null ? descricao : "" );
            tvQtdParcela.setText(String.valueOf(movimentoConta.getTitulo().getQtParcela()));
            tvNrParcela.setText(String.valueOf(movimentoConta.getIdParcela()));
        } else {
            tvTitulo.setText("--");
            tvQtdParcela.setText("--");
            tvNrParcela.setText("--");
        }
        tvValor.setText(String.format("R$ %.2f", movimentoConta.getVlMovimento()));
        tvData.setText(Util.dateToStringFormat(movimentoConta.getDtMovimento()));
        tvHistorico.setText(movimentoConta.getDsHistorico());
//        tvDetalhes.setText("registro: "+ id_movconta +","+id_conta +","+id_titulo +","+id_parcela +","+ movimentoConta.getStDirecao() +
//            ","+ movimentoConta.getStEstorno() +","+ flagTipo);
    }

    private void fecharActivity() {
        intent = new Intent(this, EstornoLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private List<String> getSqlExecutar() {
        List<String> sqlExecutar = new ArrayList<>();
        if (flagTipo.equals("R") || flagTipo.equals("P")) {
            sqlExecutar.add(sqlRecebimentoPagamentoSaldoConta1());
            sqlExecutar.add(sqlRecebimentoPagamentoParcelaTitulo2());
            sqlExecutar.add(sqlRecebimentoPagamentoVerificaParcelas3());
            sqlExecutar.add(sqlRecebimentoPagamentoFlagEstorno4());
        } else if (flagTipo.equals("T") || flagTipo.equals("S")) {
            sqlExecutar.add(sqlTransferenciaSaqueSaldoContaOrigem1());
            sqlExecutar.add(sqlTransferenciaSaqueSaldoContaDestino2());
            sqlExecutar.add(sqlTransferenciaSaqueFlagOrigem3());
            sqlExecutar.add(sqlTransferenciaSaqueFlagDestino4());
        }
        return sqlExecutar;
    }

    /**
     * atualiza o valor do saldo da conta (decrementa)
     *
     * @return
     */
    private String sqlRecebimentoPagamentoSaldoConta1() {
        StringBuilder sql = new StringBuilder();
        sql.append(" update conta set ");
        if (flagTipo.equals("R")) {
            sql.append("  vl_saldo = vl_saldo - ( select vl_movimento from movimentoconta ");
        }
        if (flagTipo.equals("P")) {
            sql.append("  vl_saldo = vl_saldo + ( select vl_movimento from movimentoconta ");
        }
        sql.append("     where id_movconta = " + id_movconta + " and id_conta = " + id_conta + ")");
        sql.append(" where id_conta = " + id_conta);
        return sql.toString();
    }

    /**
     * atualiza o valor do saldo da conta (decrementa)
     *
     * @return
     */
    private String sqlRecebimentoPagamentoParcelaTitulo2() {
        StringBuilder sql = new StringBuilder();
        sql.append(" update titulo set ");
        sql.append("  vl_saldo = (select vl_movimento from movimentoconta ");
        sql.append("     where id_movconta =" + id_movconta + " and id_titulo =" + id_titulo + " and id_parcela = " + id_parcela + "),");
        sql.append("  dt_baixa = null ");
        sql.append(" where id_titulo = " + id_titulo + " and id_parcela = " + id_parcela);
        return sql.toString();
    }

    /**
     * altera o flag para edição do título se nao tem título com valor 0
     *
     * @return
     */
    private String sqlRecebimentoPagamentoVerificaParcelas3() {
        StringBuilder sql = new StringBuilder();
        sql.append(" update titulo set ");
        sql.append("  st_movimento = ( ");
        sql.append("        select case ");
        sql.append("          when (select count(*) from titulo where id_titulo = " + id_titulo + " and vl_saldo = 0 ) = 0 ");
        sql.append("              then 'N' ");
        sql.append("              else 'S' ");
        sql.append("           end )");
        sql.append(" where id_titulo = " + id_titulo);
        return sql.toString();
    }

    /**
     * atualiza o flag de estono na tabela de movimento (para não ser mais visualizado)
     *
     * @return
     */
    private String sqlRecebimentoPagamentoFlagEstorno4() {
        StringBuilder sql = new StringBuilder();
        sql.append(" update movimentoconta set ");
        sql.append(" st_estorno = 'S', ");
        sql.append(" dt_movimento = current_date ");
        sql.append(" where id_movconta = " + id_movconta);
        return sql.toString();
    }

    /**
     * atualiza o valor do saldo da conta (incrementa) (origem)
     *
     * @return
     */
    private String sqlTransferenciaSaqueSaldoContaOrigem1() {
        StringBuilder sql = new StringBuilder();
        sql.append(" update conta set ");
        sql.append(" vl_saldo = vl_saldo + ");
        sql.append(" (select vl_movimento from movimentoconta where id_movconta = " + id_movconta + ") ");
        sql.append(" where id_conta = " + id_conta);
        return sql.toString();
    }

    /**
     * atualiza o valor do saldo da conta (decrementa) (destino)
     *
     * @return
     */
    private String sqlTransferenciaSaqueSaldoContaDestino2() {
        int idseq = id_movconta + 1;
        StringBuilder sql = new StringBuilder();
        sql.append(" update conta set ");
        sql.append(" vl_saldo = vl_saldo - ");
        sql.append(" (select vl_movimento from movimentoconta where id_movconta = " + idseq + ") ");
        sql.append(" where id_conta = (select id_conta from movimentoconta where id_movconta =" + idseq + ")");
        return sql.toString();
    }

    /**
     * atualiza o flag de estono na tabela de movimento (origem)
     *
     * @return
     */
    private String sqlTransferenciaSaqueFlagOrigem3() {
        StringBuilder sql = new StringBuilder();
        sql.append(" update movimentoconta set ");
        sql.append(" st_estorno = 'S', ");
        sql.append(" dt_movimento = current_date ");
        sql.append(" where id_movconta = " + id_movconta);
        return sql.toString();
    }

    /**
     * atualiza o flag de estono na tabela de movimento (destino)
     *
     * @return
     */
    private String sqlTransferenciaSaqueFlagDestino4() {
        StringBuilder sql = new StringBuilder();
        sql.append(" update movimentoconta set ");
        sql.append(" st_estorno = 'S', ");
        sql.append(" dt_movimento = current_date ");
        sql.append(" where id_movconta = " + (id_movconta + 1));
        return sql.toString();
    }
}
/**
 * MOVIMENTO DO TIPO: RECEBIMENTO
 * TABELA: MOVIMENTOCONTA, ST_TIPO = 'R'
 * DEVE SERVIR PARA O TIPO: PAGAMENTO (ST_TIPO = 'P
 * -- atualiza o valor do saldo da conta (decrementa)
 * update conta
 * set vl_saldo = vl_saldo - (select vl_movimento from movimentoconta where id_movconta = 1 and id_conta = 2)
 * where id_conta = 2
 * <p>
 * -- atualiza o valor da parcela do título (incrementa)
 * update titulo
 * set vl_saldo = (
 * select vl_movimento from movimentoconta where id_movconta = 1 and id_titulo = 1 and id_parcela = 2
 * ),
 * dt_baixa = null
 * where id_titulo = 1
 * and id_parcela = 2
 * <p>
 * -- altera o flag para edição do título se nao tem título com valor 0
 * update titulo
 * set st_movimento = (
 * select case
 * when (select count(*) from titulo where id_titulo = 1 and vl_saldo = 0) = 0
 * then 'N'
 * else 'S'
 * end )
 * where id_titulo = 1
 * <p>
 * --atualiza o flag de estono na tabela de movimento (para não ser mais visualizado)
 * update movimentoconta
 * set st_estorno = 'S',
 * dt_movimento = current_date
 * where id_movconta = 1
 * <p>
 * MOVIMENTO DO TIPO: PAGAMENTO
 * -- atualiza o valor do saldo da conta (decrementa)
 * update conta
 * set vl_saldo = vl_saldo + (select vl_movimento from movimentoconta where id_movconta = 2 and id_conta = 2)
 * where id_conta = 2
 * <p>
 * -- atualiza o valor da parcela do título (incrementa)
 * update titulo
 * set vl_saldo = (
 * select vl_movimento from movimentoconta where id_movconta = 2 and id_titulo = 3 and id_parcela = 1
 * ),
 * dt_baixa = null
 * where id_titulo = 3
 * and id_parcela = 1
 * <p>
 * -- altera o flag para edição do título
 * update titulo
 * set st_movimento = (
 * select case
 * when (select count(*) from titulo where id_titulo = 3 and vl_saldo = 0) = 0
 * then 'N'
 * else 'S'
 * end )
 * where id_titulo = 3
 * <p>
 * --atualiza o flag de estono na tabela de movimento (nao lista
 * update movimentoconta
 * set st_estorno = 'S',
 * dt_movimento = current_date
 * where id_movconta = 2
 * <p>
 * MOVIMENTO DO TIPO: TRANSFERENCIA, SAQUE
 * <p>
 * -- atualiza o valor do saldo da conta (incrementa) (origem)
 * update conta
 * set vl_saldo = vl_saldo + (select vl_movimento from movimentoconta where id_movconta = 8)
 * where id_conta = 2
 * <p>
 * -- atualiza o valor do saldo da conta (decrementa) (destino)
 * update conta
 * set vl_saldo = vl_saldo - (select vl_movimento from movimentoconta where id_movconta = 9)
 * where id_conta = 1
 * <p>
 * --atualiza o flag de estono na tabela de movimento (origem)
 * update movimentoconta
 * set st_estorno = 'S',
 * dt_movimento = current_date
 * where id_movconta = 8
 * <p>
 * --atualiza o flag de estono na tabela de movimento (destino)
 * update movimentoconta
 * set st_estorno = 'S',
 * dt_movimento = current_date
 * where id_movconta = 9
 */

/**
 * MOVIMENTO DO TIPO: PAGAMENTO
 -- atualiza o valor do saldo da conta (decrementa)
 update conta
 set vl_saldo = vl_saldo + (select vl_movimento from movimentoconta where id_movconta = 2 and id_conta = 2)
 where id_conta = 2

 -- atualiza o valor da parcela do título (incrementa)
 update titulo
 set vl_saldo = (
 select vl_movimento from movimentoconta where id_movconta = 2 and id_titulo = 3 and id_parcela = 1
 ),
 dt_baixa = null
 where id_titulo = 3
 and id_parcela = 1

 -- altera o flag para edição do título
 update titulo
 set st_movimento = (
 select case
 when (select count(*) from titulo where id_titulo = 3 and vl_saldo = 0) = 0
 then 'N'
 else 'S'
 end )
 where id_titulo = 3

 --atualiza o flag de estono na tabela de movimento (nao lista
 update movimentoconta
 set st_estorno = 'S',
 dt_movimento = current_date
 where id_movconta = 2
 */

/**
 MOVIMENTO DO TIPO: TRANSFERENCIA, SAQUE

 -- atualiza o valor do saldo da conta (incrementa) (origem)
 update conta
 set vl_saldo = vl_saldo + (select vl_movimento from movimentoconta where id_movconta = 8)
 where id_conta = 2

 -- atualiza o valor do saldo da conta (decrementa) (destino)
 update conta
 set vl_saldo = vl_saldo - (select vl_movimento from movimentoconta where id_movconta = 9)
 where id_conta = 1

 --atualiza o flag de estono na tabela de movimento (origem)
 update movimentoconta
 set st_estorno = 'S',
 dt_movimento = current_date
 where id_movconta = 8

 --atualiza o flag de estono na tabela de movimento (destino)
 update movimentoconta
 set st_estorno = 'S',
 dt_movimento = current_date
 where id_movconta = 9
 */