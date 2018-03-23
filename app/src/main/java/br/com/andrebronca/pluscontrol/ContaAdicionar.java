package br.com.andrebronca.pluscontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import br.com.andrebronca.pluscontrol.Constantes.Cores;
import br.com.andrebronca.pluscontrol.dao.BackupDAO;
import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class ContaAdicionar extends AppCompatActivity {
    private EditText edtDescricao, edtSaldo;
    private TextView tvAjudaValor;
    private Spinner spnCor;
    private Button btnSalvar, btnCancelar;
    private ContaDAO contaDAO;
    private String ds_conta, cd_cor;
    private double vl_saldo;
    private Intent intent;
    private boolean valorValido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta_adicionar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();
        preencheListaCores();

        edtDescricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(ContaAdicionar.this, edtDescricao);
                }
            }
        });

        edtSaldo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(ContaAdicionar.this, edtSaldo);
                }
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarCampos()) {
                    contaDAO = ContaDAO.getInstance(ContaAdicionar.this);
                    contaDAO.salvar(preencherConta());
                    Util.exibeMensagem(getApplicationContext(), "Adicionado com sucesso!");
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
                AlertDialog.Builder alerta = new AlertDialog.Builder(ContaAdicionar.this);
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

    private void linkToXml() {
        edtDescricao = (EditText) findViewById(R.id.edt_conta_cadastro_descricao);
        edtSaldo = (EditText) findViewById(R.id.edt_conta_cadastro_saldo);
        spnCor = (Spinner) findViewById(R.id.spn_conta_cadastro_cor);
        btnSalvar = (Button) findViewById(R.id.btn_conta_cadastro_salvar);
        btnCancelar = (Button) findViewById(R.id.btn_conta_cadastro_cancelar);
        tvAjudaValor = (TextView) findViewById(R.id.tv_conta_ajuda_saldo);
    }

    private void fecharActivity() { //ok
        intent = new Intent(ContaAdicionar.this, ContaLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void preencheListaCores() { //ok
        //Obtém o array de cores
        String[] listaCores = getResources().getStringArray(R.array.cores_array);
        //Cria o adapter
        final Cores adapterCores = new Cores(this, R.layout.spinner_cores_row, listaCores);
        //Atribui o adapter
        spnCor.setAdapter(adapterCores);
    }

    private void getValorCampos() {
        ds_conta = edtDescricao.getText().toString().trim();

        String strValor = edtSaldo.getText().toString().trim();
        try {
            if (Util.validaValorInput(strValor)) {
                valorValido = true;
                vl_saldo = Double.parseDouble(strValor);
            }
        } catch (NumberFormatException e) {
            Util.exibeMensagem(ContaAdicionar.this, "Valor do saldo é inválido!");
            e.printStackTrace();
        }

        cd_cor = String.valueOf(spnCor.getSelectedItem());
    }


    private boolean validarCampos() { //ok
        getValorCampos();

        if (ds_conta.length() < 3) {
            edtDescricao.setError("A descrição deve ter mais de 3 caracteres!");
            return false;
        } else if (!valorValido) {
            edtSaldo.setError("Informe o saldo!");
            return false;
        }

        return true;
    }

    private Conta preencherConta() {
        Conta conta = new Conta();
        conta.setDsConta(ds_conta);
        conta.setVlSaldo(vl_saldo);
        conta.setCdCor(cd_cor);
        conta.setDtCadastro(Util.dateAtualToDateBD(Util.getDataAtual()));
        return conta;
    }

}
