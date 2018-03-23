package br.com.andrebronca.pluscontrol;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.dao.MovimentoContaDAO;
import br.com.andrebronca.pluscontrol.entidades.ContaEntidade;
import br.com.andrebronca.pluscontrol.entidades.MovimentoEntidade;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class TransferenciaAdicionar extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private Spinner spnContaOrigem, spnContaDestino;
    private EditText edtValor, edtHistorico;
    private TextView tvAjudaValor, tvSaldoOrigem, tvSaldoDestino;
    private Button btnDtTransferencia, btnSalvar, btnCancelar;
    private int id_movconta, id_contaOrigem, id_contaDestino, id_transferencia;
    private double vl_movimento;
    private String dt_movimento;
    private final String DIRECAO_ORIGEM = "S", DIRECAO_DESTINO = "E", TIPO = "T", SELECIONE = "--SELECIONE--";
    private String ds_historico;
    private List<Conta> listaContaOrigem;
    private ArrayAdapter<Conta> adapterContaOrigem;
    private Conta contaOrigemSelecionada;
    private List<Conta> listaContaDestino;
    private int posicaoContaOrigem, posicaoContaDestino;    //saber a posição para verificar se são as mesmas contas.
    private ArrayAdapter<Conta> adapterContaDestino;
    private Conta contaDestinoSelecionada;
    private int diaEmiss = 0, mesEmiss = 0, anoEmiss = 0;
    private boolean emissSelecionado = false, temIdTransferenciaOrigem = false;
    private boolean valorValido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_adicionar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToXml();
        preencherListaContaOrigem();
        preencherListaContaDestino();

        edtValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(TransferenciaAdicionar.this, edtValor);
            }
        });

        edtHistorico.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(TransferenciaAdicionar.this, edtHistorico);
            }
        });

        btnDtTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "E");
            }
        });

        spnContaOrigem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contaOrigemSelecionada = (Conta) spnContaOrigem.getItemAtPosition(position);
                id_contaOrigem = contaOrigemSelecionada.getIdConta();
                tvSaldoOrigem.setText( String.format("R$ %.2f", contaOrigemSelecionada.getVlSaldo()));
                posicaoContaOrigem = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnContaDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contaDestinoSelecionada = (Conta) spnContaDestino.getItemAtPosition(position);
                id_contaDestino = contaDestinoSelecionada.getIdConta();
                tvSaldoDestino.setText(String.format("R$ %.2f", contaDestinoSelecionada.getVlSaldo()));
                posicaoContaDestino = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarDados()) {
                    MovimentoContaDAO movimentoContaDAO = MovimentoContaDAO.getInstance(TransferenciaAdicionar.this);
                    String sqlMovimentoOrigem = createSqlCreateMovimentoOrigem(getMovimentoConta(id_contaOrigem, DIRECAO_ORIGEM));
                    id_transferencia = movimentoContaDAO.getMaxIDMovimentoConta() + 1;
                    temIdTransferenciaOrigem = true;
                    String sqlSaldoOrigem = createSqlUpdateSaldoConta(getSaldoContaOrigem());
                    String sqlSaldoDestino = createSqlUpdateSaldoConta(getSaldoContaDestino());
                    String sqlMovimentoDestino = createSqlCreateMovimentoDestino(getMovimentoConta(id_contaDestino, DIRECAO_DESTINO));
                    movimentoContaDAO.salvarMovimentoTransferenciaTransacao(sqlMovimentoOrigem, sqlSaldoOrigem, sqlSaldoDestino, sqlMovimentoDestino);
                    Util.exibeMensagem(TransferenciaAdicionar.this, "Transferência executada com sucesso!");
                    fecharActivity();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fecharActivity();
            }
        });

        tvAjudaValor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(TransferenciaAdicionar.this);
                alerta.setTitle("Ajuda sobre valores válidos!");
                alerta.setIcon(R.drawable.ic_ajuda_24dp);
                alerta.setCancelable(false);
                StringBuilder sb = new StringBuilder();
                sb.append("Obs. Inserir somente (.) para separação decimal.\n");
                sb.append("Total de 8 dígitos com 2 casas decimais.\n");
                sb.append("Formatos aceitos:\n");
                sb.append("123456.78 ou -123456.78\n");
                sb.append(".01 ou -.01");
                alerta.setMessage(sb.toString());
                alerta.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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

    private MovimentoConta getMovimentoConta(int idConta, String direcao) {
        MovimentoConta movimentoConta = new MovimentoConta();
        movimentoConta.setIdConta(idConta);
        movimentoConta.setVlMovimento(vl_movimento);
        movimentoConta.setDtMovimento(Util.dateAtualToDateBD(dt_movimento));    //formato para gravar no BD
        movimentoConta.setStDirecao(direcao);
        if (temIdTransferenciaOrigem) {
            movimentoConta.setIdTransferencia(id_transferencia);
        }
        movimentoConta.setDsHistorico(ds_historico);
        movimentoConta.setStTipo(TIPO);
        return movimentoConta;
    }

    private String createSqlCreateMovimentoOrigem(MovimentoConta mov) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into " + MovimentoEntidade.TABLE_NAME + " ( ");
        sql.append(" id_conta, vl_movimento, dt_movimento, st_direcao, ds_historico, st_tipo ) ");
        sql.append(" values ( ");
        sql.append(mov.getIdConta() + "," + mov.getVlMovimento() + ",'" + mov.getDtMovimento() + "','" + mov.getStDirecao() + "'");
        sql.append(",'" + mov.getDsHistorico() + "','" + mov.getStTipo() + "'");
        sql.append(" ) ");
        return sql.toString();
    }

    private String createSqlCreateMovimentoDestino(MovimentoConta mov) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into " + MovimentoEntidade.TABLE_NAME + " ( ");
        sql.append(" id_conta, vl_movimento, dt_movimento, st_direcao, ds_historico, st_tipo, id_transferencia ) ");
        sql.append(" values ( ");
        sql.append(mov.getIdConta() + "," + mov.getVlMovimento() + ",'" + mov.getDtMovimento() + "','" + mov.getStDirecao() + "'");
        sql.append(",'" + mov.getDsHistorico() + "','" + mov.getStTipo() + "'," + mov.getIdTransferencia());
        sql.append(" ) ");
        return sql.toString();
    }

    private Conta getSaldoContaOrigem() {
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        Conta contaTmp = contaDAO.getContaById(id_contaOrigem);
        double saldo_original = contaTmp.getVlSaldo();
        Conta conta = new Conta();
        conta.setIdConta(id_contaOrigem);
        conta.setVlSaldo(saldo_original - vl_movimento);
        return conta;
    }

    private Conta getSaldoContaDestino() {
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        Conta contaTmp = contaDAO.getContaById(id_contaDestino);
        double saldo_original = contaTmp.getVlSaldo();
        Conta conta = new Conta();
        conta.setIdConta(id_contaDestino);
        conta.setVlSaldo(saldo_original + vl_movimento);
        return conta;
    }

    private String createSqlUpdateSaldoConta(Conta conta) {
        StringBuilder sql = new StringBuilder();
        sql.append("update " + ContaEntidade.TABLE_NAME + " ");
        sql.append(" set vl_saldo = " + conta.getVlSaldo() + " ,");
        sql.append(" dt_alterado = current_date ");
        sql.append(" where id_conta = " + conta.getIdConta());
        return sql.toString();
    }

    private void linkToXml() {
        spnContaOrigem = (Spinner) findViewById(R.id.spn_transferencia_origem);
        spnContaDestino = (Spinner) findViewById(R.id.spn_transferencia_destino);
        edtValor = (EditText) findViewById(R.id.edt_transferencia_valor);
        edtHistorico = (EditText) findViewById(R.id.edt_transferencia_historico);
        btnDtTransferencia = (Button) findViewById(R.id.btn_transferencia_data);
        btnSalvar = (Button) findViewById(R.id.btn_transferencia_salvar);
        btnCancelar = (Button) findViewById(R.id.btn_transferencia_cancelar);
        tvAjudaValor = (TextView) findViewById(R.id.tv_transferencia_valor);
        tvSaldoOrigem = (TextView) findViewById(R.id.tv_transferencia_saldo_origem);
        tvSaldoDestino = (TextView) findViewById(R.id.tv_transferencia_saldo_destino);
    }

    private void preencherListaContaOrigem() {
        listaContaOrigem = new ArrayList<>();
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        listaContaOrigem = contaDAO.listaContaDescricao();
        listaContaOrigem.add(0, getFirstConta());
        adapterContaOrigem = new ArrayAdapter<Conta>(this, android.R.layout.simple_spinner_dropdown_item, listaContaOrigem);
        spnContaOrigem.setAdapter(adapterContaOrigem);
    }

    private void preencherListaContaDestino() {
        listaContaDestino = new ArrayList<>();
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        listaContaDestino = contaDAO.listaContaDescricao();
        listaContaDestino.add(0, getFirstConta());
        adapterContaDestino = new ArrayAdapter<Conta>(this, android.R.layout.simple_spinner_dropdown_item, listaContaDestino);
        spnContaDestino.setAdapter(adapterContaDestino);
    }

    private Conta getFirstConta() {
        Conta conta = new Conta();
        conta.setIdConta(-1);
        conta.setDsConta(SELECIONE);
        return conta;
    }

    private void getDadosFormulario() {
        String strValor = edtValor.getText().toString().trim();

        try {
            if (Util.validaValorInput(strValor)) {
                valorValido = true;
                vl_movimento = Double.parseDouble(strValor);
            }
        } catch (NumberFormatException e) {
            edtValor.setError("Valor inválido!");
            e.printStackTrace();
        }

        ds_historico = edtHistorico.getText().toString();
    }

    private boolean validarDados() {
        getDadosFormulario();
        Conta conta = getFirstConta();

        if (posicaoContaOrigem == posicaoContaDestino) {
            Util.exibeMensagem(this, "As contas de origem e destino não podem ser iguais");
            return false;
        }
        if (contaOrigemSelecionada != null) {
            if (contaOrigemSelecionada.getDsConta().equals(conta.getDsConta())) {
                Util.exibeMensagem(this, "Selecione uma conta de Origem!");
                return false;
            }
        }
        if (contaDestinoSelecionada != null) {
            if (contaDestinoSelecionada.getDsConta().equals(conta.getDsConta())) {
                Util.exibeMensagem(this, "Selecione uma conta de Destino!");
                return false;
            }
        }
        if (vl_movimento < 0.0) {
            edtValor.setError("Valor não pode ser inferior a 0.0!");
            return false;
        }
        if (!valorValido) {
            edtValor.setError("Revisar o valor!");
            return false;
        }
        if (!emissSelecionado) {
            Util.exibeMensagem(this, "Selecione uma data para o movimento!");
            return false;
        }
        return true;
    }

    private void fecharActivity() { //ok
        intent = new Intent(TransferenciaAdicionar.this, TransferenciaLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void setDtMovimento() {
        dt_movimento = diaEmiss + "/" + mesEmiss + "/" + anoEmiss;
    }

    //dia, mes e ano separados para o caso de precisar utilizá-los
    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String tag = getTag();
            if (tag.equals("E")) {
                diaEmiss = dayOfMonth;
                mesEmiss = monthOfYear + 1;
                anoEmiss = year;
                emissSelecionado = true;
                String str = "Transferido em: " + diaEmiss + "/" + mesEmiss + "/" + anoEmiss;
                btnDtTransferencia.setText(str);
                setDtMovimento();
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            if (emissSelecionado) {
                year = anoEmiss;
                month = mesEmiss - 1;
                day = diaEmiss;
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

    }

}
