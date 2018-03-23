package br.com.andrebronca.pluscontrol;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import br.com.andrebronca.pluscontrol.Constantes.Cores;
import br.com.andrebronca.pluscontrol.dao.BackupDAO;
import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.dao.MovimentoContaDAO;
import br.com.andrebronca.pluscontrol.modelo.Conta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class ContaEditar extends AppCompatActivity {
    private EditText edtDescricao, edtSaldo;
    private Spinner spnCor;
    private Button btnSalvar, btnExcluir;
    private ContaDAO contaDAO;
    private Conta conta;
    private Intent intent;
    private Bundle params = new Bundle();
    private int idConta;
    private String[] listaCores;
    private Cores adapterCores;
    private String ds_conta, cd_cor;
    private double vl_saldo;
    private boolean valorValido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta_editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();
        listaCores = getResources().getStringArray(R.array.cores_array);
        adapterCores = new Cores(this, R.layout.spinner_cores_row, listaCores);
        preencheListaCores();

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                idConta = params.getInt("id");
            }
        }

        preencherFormulario();

        edtDescricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(ContaEditar.this, edtDescricao);
            }
        });

        edtSaldo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(ContaEditar.this, edtSaldo);
            }
        });

        //todo: corrigir um bug. ao salvar o valor com vírgula fica zerado, perdendo integridade das alteraçoes
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validaCampos()) {
                    contaDAO = ContaDAO.getInstance(ContaEditar.this);
                    contaDAO.atualizar(preencherConta());
                    Util.exibeMensagem(ContaEditar.this, "Editado com sucesso!");
                    fecharActivity();
                }
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!verificaVinculoComMovimento()) {
                    AlertDialog.Builder alerta = new AlertDialog.Builder(ContaEditar.this);
                    alerta.setTitle("Atenção");
                    alerta.setIcon(R.drawable.ic_delete_24dp);
                    alerta.setCancelable(false);
                    alerta.setMessage("Deseja excluir esta conta?");
                    alerta.setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ContaDAO contaDAO = ContaDAO.getInstance(ContaEditar.this);
                            contaDAO.deleteContaByID(idConta);
                            fecharActivity();
                        }
                    });
                    alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alerta.create();
                    alertDialog.show();
                } else {
                    Util.exibeMensagem(ContaEditar.this, "Esta conta está vinculada com movimento.\n" +
                            "Não pode ser excluída!");
                }
            }
        });
    }

    private void linkToXml() {
        edtDescricao = (EditText) findViewById(R.id.edt_conta_editar_descricao);
        edtSaldo = (EditText) findViewById(R.id.edt_conta_editar_saldo);
        spnCor = (Spinner) findViewById(R.id.spn_conta_editar_cor);
        btnSalvar = (Button) findViewById(R.id.btn_conta_editar_salvar);
        btnExcluir = (Button) findViewById(R.id.btn_conta_editar_excluir);
    }

    private boolean validaCampos() {
        getValorFormulario();

        if (ds_conta.length() < 3) {
            edtDescricao.setError("A descrição deve ter mais de 2 caracteres!");
            edtDescricao.requestFocus();
            return false;
        } else if (!valorValido) {
            edtSaldo.setError("Revise o saldo!");
            return false;
        }
        return true;
    }

    private boolean verificaVinculoComMovimento() {
        MovimentoContaDAO mov = MovimentoContaDAO.getInstance(this);
        int qtd = 0;
        qtd = mov.getQtdVinculoContaById(idConta);
        return qtd > 0;
    }

    private void getValorFormulario() {
        ds_conta = edtDescricao.getText().toString().trim();

        String strValor = edtSaldo.getText().toString().trim();
        try {
            if (Util.validaValorInput(strValor)) {
                valorValido = true;
                vl_saldo = Double.parseDouble(strValor);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        cd_cor = String.valueOf(spnCor.getSelectedItem());
    }

    private Conta preencherConta() {
        Conta conta = new Conta();
        conta.setIdConta(idConta);
        conta.setDsConta(ds_conta);
        conta.setVlSaldo(vl_saldo);
        conta.setCdCor(cd_cor);
        conta.setDtAlterado(Util.dateAtualToDateBD(Util.getDataAtual()));
        return conta;
    }

    private void fecharActivity() { //ok
        intent = new Intent(ContaEditar.this, ContaLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void preencheListaCores() { //ok
        //Obtém o array de cores

        //Cria o adapter
        //Atribui o adapter
        spnCor.setAdapter(adapterCores);
    }

    private void preencherFormulario() {
        Conta conta = getContaById();
        edtDescricao.setText(conta.getDsConta());
        edtSaldo.setText(String.valueOf(conta.getVlSaldo()));
//        edtSaldo.setText(String.format("%.2f", conta.getVlSaldo()));
        setCorConta(conta.getCdCor());
    }

    private Conta getContaById() {
        contaDAO = ContaDAO.getInstance(ContaEditar.this);
        conta = contaDAO.getContaById(idConta);
        return conta;
    }

    private void setCorConta(final String cor) {
        spnCor.post(new Runnable() {
            @Override
            public void run() {
                spnCor.setSelection(linkCorComAdapter(cor));
            }
        });
    }

    private int linkCorComAdapter(String cor) {
        int posicao = 0;
        for (int i = 0; i < listaCores.length; i++) {
            if (listaCores[i].equalsIgnoreCase(cor)) {
                posicao = i;
            }
        }
        return posicao;
    }

}