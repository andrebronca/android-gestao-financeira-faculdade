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
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.entidades.ContaEntidade;
import br.com.andrebronca.pluscontrol.entidades.MovimentoEntidade;
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RecebimentoEditar extends AppCompatActivity {
    private Spinner spnConta;
    private EditText edtValor, edtHistorico;
    private TextView tvAjudaValor, tvDataEmissao, tvDataVencimento;
    private Button btnDtMovimento, btnSalvar, btnCancelar;
    private Intent intent;
    private Bundle params = new Bundle();
    private int idTitulo, idConta, id_parcela;
    private Titulo objTitulo;
    private List<Conta> listaConta;
    private ArrayAdapter<Conta> adapterConta;
    private final String SELECIONE = "--SELECIONE--";
    private final String DIRECAO = "E";
    private final String TIPO = "R";    //recebimento
    private int diaMov, mesMov, anoMov;
    private boolean dtMovimentoSelecionado;
    private double vl_movimento, vl_saldo_maximo;
    private String dt_movimento, ds_historico;
    private Conta contaSelecionada;
    private DialogFragment newFragment;
    private boolean valorValido = false;
    private String dataEmissao = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recebimento_editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                idTitulo = params.getInt("id");
                id_parcela = params.getInt("id_parcela");
            }
        }

        linkToXml();
        preencherSpinnerConta();
        getTituloByIdParcela();

        spnConta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contaSelecionada = (Conta) spnConta.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnDtMovimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "E");
            }
        });


        edtHistorico.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(RecebimentoEditar.this, edtHistorico);
            }
        });

        edtValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(RecebimentoEditar.this, edtValor);
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarFormulario()) {
                    MovimentoContaDAO movimentoContaDAO = MovimentoContaDAO.getInstance(RecebimentoEditar.this);
                    String sqlMovimento = createSqlCreateMovimento(getMovimentoConta(
                            getContaComSaldoAtualizado(), getTituloComSaldoAtualizado()));
                    String sqlConta = createSqlUpdateSaldoConta(getContaComSaldoAtualizado());
                    String sqlTitulo = createSqlUpdateSaldoTitulo(getTituloComSaldoAtualizado());
                    String sqlTituloMovimento = createSqlAtualizaStatusMovimento();
                    movimentoContaDAO.salvarMovimentoEntradaTransacao(sqlMovimento, sqlConta, sqlTitulo, sqlTituloMovimento);
                    Util.exibeMensagem(RecebimentoEditar.this, "Movimento salvo com sucesso!");
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
                AlertDialog.Builder alerta = new AlertDialog.Builder(RecebimentoEditar.this);
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

    //1
    private void linkToXml() {
        spnConta = (Spinner) findViewById(R.id.spn_recebimento_editar_conta);
        edtValor = (EditText) findViewById(R.id.edt_recebimento_editar_valor);
        edtHistorico = (EditText) findViewById(R.id.edt_recebimento_editar_historico);
        btnDtMovimento = (Button) findViewById(R.id.btn_recebimento_editar_data);
        btnSalvar = (Button) findViewById(R.id.btn_recebimento_editar_salvar);
        btnCancelar = (Button) findViewById(R.id.btn_recebimento_editar_cancelar);
        tvAjudaValor = (TextView) findViewById(R.id.tv_recebimento_editar_valor);
        tvDataEmissao = (TextView) findViewById(R.id.tv_recebimento_data_emissao);
        tvDataVencimento = (TextView) findViewById(R.id.tv_recebimento_data_vencimento);
    }

    //2
    private void preencherSpinnerConta() {
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        listaConta = new ArrayList<>();
        listaConta = contaDAO.listarTudo();
        listaConta.add(0, setPrimeiraConta());
        adapterConta = new ArrayAdapter<Conta>(this, android.R.layout.simple_spinner_dropdown_item, listaConta);
        spnConta.setAdapter(adapterConta);
    }

    //2.1
    private Conta setPrimeiraConta() {
        Conta conta = new Conta();
        conta.setIdConta(-1);
        conta.setDsConta(SELECIONE);
        return conta;
    }

    //3
    //objTitulo é usado externamente
    private void getTituloByIdParcela() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        objTitulo = tituloDAO.getTituloByID(idTitulo, id_parcela);
        vl_saldo_maximo = objTitulo.getVlSaldo();
        dataEmissao = objTitulo.getDtEmissao();
        tvDataEmissao.setText("Emissão: " + Util.dateToStringFormat(objTitulo.getDtEmissao()));
        tvDataVencimento.setText("Vencimento: " + Util.dateToStringFormat(objTitulo.getDtVencimento()));
        preencheSaldoMaximo(vl_saldo_maximo);
    }

    private void preencheSaldoMaximo(double saldo) {
        edtValor.setText(String.valueOf(saldo));
    }

    //4
    private void fecharActivity() {
        intent = new Intent(RecebimentoEditar.this, RecebimentoLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    //5
    private void getDadosFormulario() {
        String strValor = edtValor.getText().toString().trim();

        try {
            if (Util.validaValorInput(strValor)) {
                valorValido = true;
                vl_movimento = Double.parseDouble(strValor);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        idConta = contaSelecionada.getIdConta();
        dt_movimento = diaMov + "/" + mesMov + "/" + anoMov;
        ds_historico = edtHistorico.getText().toString();
    }

    //6
    private boolean validarFormulario() {
        getDadosFormulario();

        if (vl_movimento <= 0.0) {
            edtValor.setError("Valor não pode ser inferior ou igual a 0!");
            return false;
        }

        if (!valorValido) {
            edtValor.setError("Revisar o valor!");
            return false;
        }

        if (vl_movimento > vl_saldo_maximo) {
            edtValor.setError("Valor não pode ser superior ao saldo máximo: " + vl_saldo_maximo);
            return false;
        }

        if (!dtMovimentoSelecionado) {
            Util.exibeMensagem(this, "Selecione a data do movimento!");
            return false;
        }

        if (contaSelecionada != null) {
            if (contaSelecionada.getDsConta().equals(SELECIONE)) {
                Util.exibeMensagem(this, "Selecione uma conta!");
                return false;
            }
        }

        if (!dataSeleciondaMaiorEmissao()) {
            Util.exibeMensagem(this, "Data da baixa deve ser maior ou igual a data de emissão: " +
                    Util.dateToStringFormat(dataEmissao));
            return false;
        }

        return true;
    }

    private boolean dataSeleciondaMaiorEmissao() {
        String diaEmis = dataEmissao.substring(8);
        String mesEmis = dataEmissao.substring(5, 7);
        String anoEmis = dataEmissao.substring(0, 4);
        int diaE = Integer.parseInt(diaEmis);
        int mesE = Integer.parseInt(mesEmis);
        int anoE = Integer.parseInt(anoEmis);
        int res = Util.comparaDtVencimentoEmissao(diaMov, mesMov, anoMov, diaE, mesE, anoE);
        return res > -1;
    }

    //7
    private Conta getContaComSaldoAtualizado() {
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        Conta contaTmp = contaDAO.getContaById(idConta);
        double saldo_original = contaTmp.getVlSaldo();
        Conta conta = new Conta();
        conta.setIdConta(idConta);
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

    private Titulo getTituloComSaldoAtualizado() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        Titulo tituloTmp = tituloDAO.getTituloByID(idTitulo, id_parcela);
        double saldo_original = tituloTmp.getVlSaldo();
        Titulo titulo = new Titulo();
        titulo.setIdTitulo(idTitulo);
        titulo.setIdParcela(id_parcela);
        titulo.setVlSaldo(saldo_original - vl_movimento);
        return titulo;
    }

    private String createSqlUpdateSaldoTitulo(Titulo titulo) {
        double novoSaldo = titulo.getVlSaldo();
        StringBuilder sql = new StringBuilder();
        sql.append("update " + TituloEntidade.TABLE_NAME + " ");
        sql.append(" set vl_saldo = " + novoSaldo + " ,");
        if (novoSaldo == 0) {
            sql.append(" dt_baixa = current_date, ");
        }
        sql.append(" dt_alterado = current_date ");
        sql.append(" where id_titulo = " + titulo.getIdTitulo());
        sql.append(" and id_parcela = " + titulo.getIdParcela());
        return sql.toString();
    }

    //flag sinaliza que o título já teve alteração e não permite edição
    private String createSqlAtualizaStatusMovimento() {
        StringBuilder sql = new StringBuilder();
        sql.append("update " + TituloEntidade.TABLE_NAME + " ");
        sql.append(" set st_movimento = 'S' ");
        sql.append(" where id_titulo = " + idTitulo);
        return sql.toString();
    }

    private MovimentoConta getMovimentoConta(Conta conta, Titulo titulo) {
        MovimentoConta movimentoConta = new MovimentoConta();
        movimentoConta.setConta(conta);
        movimentoConta.setIdConta(conta.getIdConta());
        movimentoConta.setTitulo(titulo);
        movimentoConta.setIdTitulo(titulo.getIdTitulo());
        movimentoConta.setIdParcela(titulo.getIdParcela());    //por hora utilizar assim
        movimentoConta.setVlMovimento(vl_movimento);
        movimentoConta.setDtMovimento(Util.dateAtualToDateBD(dt_movimento));    //formato para gravar no BD
        movimentoConta.setStDirecao(DIRECAO);
        movimentoConta.setDsHistorico(ds_historico);
        movimentoConta.setStTipo(TIPO);
        return movimentoConta;
    }

    private String createSqlCreateMovimento(MovimentoConta mov) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into " + MovimentoEntidade.TABLE_NAME + " ( ");
        sql.append(" id_conta, id_titulo, id_parcela, vl_movimento, dt_movimento, st_direcao, ds_historico, st_tipo ) ");
        sql.append(" values ( ");
        sql.append(mov.getIdConta() + "," + mov.getIdTitulo() + "," + mov.getIdParcela() + "," + mov.getVlMovimento() + ",'");
        sql.append(mov.getDtMovimento() + "','" + mov.getStDirecao() + "','" + mov.getDsHistorico() + "','" + mov.getStTipo() + "'");
        sql.append(" ) ");
        return sql.toString();
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String tag = getTag();
            if (tag.equals("E")) {
                diaMov = dayOfMonth;
                mesMov = monthOfYear + 1;
                anoMov = year;
                dtMovimentoSelecionado = true;
                String str = "Data movimento: " + diaMov + "/" + mesMov + "/" + anoMov;
                btnDtMovimento.setText(str);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            if (dtMovimentoSelecionado) {
                year = anoMov;
                month = mesMov - 1;
                day = diaMov;
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

    }

}