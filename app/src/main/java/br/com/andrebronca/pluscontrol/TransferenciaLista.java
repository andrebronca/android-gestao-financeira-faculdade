package br.com.andrebronca.pluscontrol;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.andrebronca.pluscontrol.adapter.MovimentoAdapter;
import br.com.andrebronca.pluscontrol.dao.ContaDAO;
import br.com.andrebronca.pluscontrol.dao.MovimentoContaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.entidades.MovimentoEntidade;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class TransferenciaLista extends AppCompatActivity {
    private EditText edtPesquisa;
    private Button btnLimpar, btnVencDataIni, btnVencDataFim, btnPesquisarPeriodo;
    private ListView listviewTransferencia;
    //    private MovimentoContaDAO movimentoContaDAO;
//    private MovimentoConta movimentoContaTransferencia;
    private ArrayList<MovimentoConta> listaTransferencia, listaTransfPeriodo;
    private ArrayList<MovimentoConta> listaTransferenciaPesquisada = new ArrayList<>();
    private Intent intent;
    private Bundle params = new Bundle();
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataVencIniSelecionado = false, dataVencFimSelecionado = false;
    public DialogFragment newFragment;
    private boolean temTransferencia = false;
    private MovimentoAdapter movimentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();
        setListTituloRecebimento();
        desativarPesquisaVazia();
        preencherListaTransferencia();

        edtPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pesquisar();
//                listviewTransferencia.setAdapter(new ArrayAdapter<MovimentoConta>(TransferenciaLista.this,
//                        android.R.layout.simple_list_item_1,
//                        listaTransferenciaPesquisada));
                movimentoAdapter = new MovimentoAdapter(TransferenciaLista.this, listaTransferenciaPesquisada);
                listviewTransferencia.setAdapter(movimentoAdapter);
                listviewTransferencia.setCacheColorHint(Color.TRANSPARENT);
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(TransferenciaLista.this, edtPesquisa);
            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparPesquisa();
            }
        });

        listviewTransferencia.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(TransferenciaLista.this, TransferenciaDetalhes.class);
                MovimentoConta movConta = (MovimentoConta) listviewTransferencia.getItemAtPosition(position);
                params.putInt("id_origem", movConta.getIdMovConta());
//                params.putInt("id_destino", (movConta.getIdMovConta() + 1));    //sempre é sequencial, se bem que poderia ser pego no detalhe
                intent.putExtras(params);
                startActivity(intent);
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(TransferenciaLista.this, TransferenciaAdicionar.class);
                startActivity(intent);
            }
        });

        btnVencDataIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "I");
            }
        });

        btnVencDataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "F");
            }
        });

        btnPesquisarPeriodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dataVencIniSelecionado || dataVencFimSelecionado) {
                    if (dataFimMaiorIgualDataIni()) {
                        MovimentoContaDAO moviContaDao = MovimentoContaDAO.getInstance(TransferenciaLista.this);
                        listaTransfPeriodo = (ArrayList<MovimentoConta>) moviContaDao.getMovimentoContaBySQL(getSqlPeriodoVenc(), null);
//                        listviewTransferencia.setAdapter(
//                                new ArrayAdapter<MovimentoConta>( TransferenciaLista.this,
//                                        android.R.layout.simple_list_item_1,
//                                        listaTransfPeriodo));
                        movimentoAdapter = new MovimentoAdapter(TransferenciaLista.this, listaTransfPeriodo);
                        listviewTransferencia.setAdapter(movimentoAdapter);
                        listviewTransferencia.setCacheColorHint(Color.TRANSPARENT);
                    } else {
                        Util.exibeMensagem(TransferenciaLista.this, "Data final deve ser \nigual ou superior a data inicial!");
                    }
                } else {
                    Util.exibeMensagem(TransferenciaLista.this, "Deve ter um período selecionado!");
                }
            }
        });

    }

    private void linkToXml() {
        edtPesquisa = (EditText) findViewById(R.id.edt_transferencia_pesquisar_lista);
        btnLimpar = (Button) findViewById(R.id.btn_transferencia_limpar_pesquisa);
        listviewTransferencia = (ListView) findViewById(R.id.ltv_transferencia_lista);
        btnVencDataIni = (Button) findViewById(R.id.btn_transferencia_data_inicial_vencimento);
        btnVencDataFim = (Button) findViewById(R.id.btn_transferencia_data_final_vencimento);
        btnPesquisarPeriodo = (Button) findViewById(R.id.btn_transferencia_periodo);
    }

    private void setListTituloRecebimento() {
        MovimentoContaDAO movimentoContaDAO = MovimentoContaDAO.getInstance(this);
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        String sql = "select * from " + MovimentoEntidade.TABLE_NAME + " where st_tipo = 'T' and st_direcao = 'S'";
        listaTransferencia = (ArrayList<MovimentoConta>) movimentoContaDAO.getMovimentoContaBySQL(sql, null);
        movimentoAdapter = new MovimentoAdapter(this, listaTransferencia);
    }

    private void preencherListaTransferencia() {
//        listviewTransferencia.setAdapter(new ArrayAdapter<MovimentoConta>(this,
//                android.R.layout.simple_list_item_1, listaTransferencia));
        movimentoAdapter = new MovimentoAdapter(TransferenciaLista.this, listaTransferencia);
        listviewTransferencia.setAdapter(movimentoAdapter);
        listviewTransferencia.setCacheColorHint(Color.TRANSPARENT);
    }

    private void desativarPesquisaVazia() {
        if (listaTransferencia.size() > 0) {
            temTransferencia = true;
            edtPesquisa.setEnabled(true);
            edtPesquisa.requestFocus();
        } else {
            edtPesquisa.setEnabled(false);
            btnVencDataIni.setEnabled(false);
            btnVencDataFim.setEnabled(false);
            btnPesquisarPeriodo.setEnabled(false);
            btnLimpar.setEnabled(false);
        }
    }

    private void limparPesquisa() {
        edtPesquisa.setText("");
        edtPesquisa.requestFocus();
        dataVencIniSelecionado = false;
        dataVencFimSelecionado = false;
        btnVencDataIni.setText("Data Inicial");
        btnVencDataFim.setText("Data Final");
        atualizarListViewTransferencia();
    }

    private void atualizarListViewTransferencia() {
        if (temTransferencia) {
//            listviewTransferencia.setAdapter(
//                    new ArrayAdapter<MovimentoConta>(this,
//                            android.R.layout.simple_list_item_1, listaTransferencia));
            movimentoAdapter = new MovimentoAdapter(TransferenciaLista.this, listaTransferencia);
            listviewTransferencia.setAdapter(movimentoAdapter);
            listviewTransferencia.setCacheColorHint(Color.TRANSPARENT);
            pesquisar();
        }
    }

    private void pesquisar() {
        if (temTransferencia) {
            int tamPesq = edtPesquisa.getText().length();
            if (!(listaTransferenciaPesquisada == null)) {
                listaTransferenciaPesquisada.clear();
                for (MovimentoConta movConta : listaTransferencia) {
                    String pesq = edtPesquisa.getText().toString();
                    String descricao = movConta.getDsHistorico();
                    if (tamPesq <= descricao.length()) {
                        if (pesq.equals(descricao.subSequence(0, tamPesq))) {
                            listaTransferenciaPesquisada.add(movConta);
                        }
                    }
                }
            }
        }
    }

    private boolean dataFimMaiorIgualDataIni() {
//        long dataFim = Util.convertDataParaNumero(diaFim, mesFim, anoFim);
//        long dataIni = Util.convertDataParaNumero(diaIni,mesIni, anoIni);
        int res = Util.comparaDtVencimentoEmissao(diaFim, mesFim, anoFim, diaIni, mesIni, anoIni);
        return res > -1;
    }

    private String getDataVencIni() {
        return diaIni + "/" + mesIni + "/" + anoIni;
    }

    private String getDataVencFim() {
        return diaFim + "/" + mesFim + "/" + anoFim;
    }

    private String getSqlPeriodoVenc() {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from " + MovimentoEntidade.TABLE_NAME);
        sql.append(" where st_tipo = 'T' ");
        sql.append("   and dt_movimento between '" + Util.dateAtualToDateBD(getDataVencIni()) + "' ");
        sql.append("   and '" + Util.dateAtualToDateBD(getDataVencFim()) + "' ");
        sql.append("   and st_direcao = 'S' ");
        return sql.toString();
    }

    @SuppressLint("ValidFragment")
    public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String tag = getTag();
            if (tag.equals("I")) {
                diaIni = dayOfMonth;
                mesIni = monthOfYear + 1;
                anoIni = year;
                dataVencIniSelecionado = true;
                String str = diaIni + "/" + mesIni + "/" + anoIni;
                btnVencDataIni.setText(str);
            }
            if (tag.equals("F")) {
                diaFim = dayOfMonth;
                mesFim = monthOfYear + 1;
                anoFim = year;
                dataVencFimSelecionado = true;
                String str = diaFim + "/" + mesFim + "/" + anoFim;
                btnVencDataFim.setText(str);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            if (dataVencIniSelecionado) {
                year = anoIni;
                month = mesIni - 1;
                day = diaIni;
            }
            if (dataVencFimSelecionado) {
                year = anoFim;
                month = mesFim - 1;
                day = diaFim;
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

    }
}