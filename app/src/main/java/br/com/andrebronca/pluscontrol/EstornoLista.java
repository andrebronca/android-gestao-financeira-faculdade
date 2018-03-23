package br.com.andrebronca.pluscontrol;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
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
import br.com.andrebronca.pluscontrol.dao.MovimentoContaDAO;
import br.com.andrebronca.pluscontrol.entidades.MovimentoEntidade;
import br.com.andrebronca.pluscontrol.modelo.MovimentoConta;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class EstornoLista extends AppCompatActivity {
    private EditText edtPesquisa;
    private Button btnLimparPesquisa, btnVencDataIni, btnVencDataFim, btnPesquisarPeriodo;
    private ListView listViewMovimentos;
    private ArrayList<MovimentoConta> listaMovimentos, listaMovimentoPeriodo;
    private ArrayList<MovimentoConta> listaMovimentoPesquisado = new ArrayList<>();
    private boolean temMovimento = false;
    private Intent intent;
    private Bundle params = new Bundle();
    private MovimentoContaDAO movimentoContaDAO;
    public DialogFragment newFragment;
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataVencIniSelecionado = false, dataVencFimSelecionado = false;
    private MovimentoAdapter movimentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estorno_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToXml();
        preencherListView();
        desativarPesquisaVazia();
        atualizarListViewMovimento();

        btnLimparPesquisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparPesquisa();
            }
        });

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
//                listViewMovimentos.setAdapter(
//                        new ArrayAdapter<MovimentoConta>(EstornoLista.this,
//                                android.R.layout.simple_list_item_1, listaMovimentoPesquisado));
                movimentoAdapter = new MovimentoAdapter(EstornoLista.this, listaMovimentoPesquisado);
                listViewMovimentos.setAdapter(movimentoAdapter);
                listViewMovimentos.setCacheColorHint(Color.TRANSPARENT);
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(EstornoLista.this, edtPesquisa);
                }
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
                        MovimentoContaDAO movContaDao = MovimentoContaDAO.getInstance(EstornoLista.this);
                        listaMovimentoPeriodo = (ArrayList<MovimentoConta>)
                                movContaDao.getMovimentoContaBySQL(getSqlPeriodoMovimento(), null);
//                        listViewMovimentos.setAdapter(
//                                new ArrayAdapter<MovimentoConta>( EstornoLista.this,
//                                        android.R.layout.simple_list_item_1,
//                                        listaMovimentoPeriodo));
                        movimentoAdapter = new MovimentoAdapter(EstornoLista.this, listaMovimentoPeriodo);
                        listViewMovimentos.setAdapter(movimentoAdapter);
                        listViewMovimentos.setCacheColorHint(Color.TRANSPARENT);
                    } else {
                        Util.exibeMensagem(EstornoLista.this, "Data final deve ser \nigual ou superior a data inicial!");
                    }
                } else {
                    Util.exibeMensagem(EstornoLista.this, "Deve ter um período selecionado!");
                }

            }
        });

        listViewMovimentos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(EstornoLista.this, EstornoDetalhesConfirmar.class);
                MovimentoConta movConta = new MovimentoConta();
                movConta = (MovimentoConta) listViewMovimentos.getItemAtPosition(position);
                params.putInt("id_movconta", movConta.getIdMovConta());
                intent.putExtras(params);
                startActivity(intent);
                return true;
            }
        });

    }

    private void linkToXml() {
        edtPesquisa = (EditText) findViewById(R.id.edt_estorno_pesquisar_lista);
        btnLimparPesquisa = (Button) findViewById(R.id.btn_estorno_lista_limpar);
        listViewMovimentos = (ListView) findViewById(R.id.ltv_estorno_lista);
        btnVencDataFim = (Button) findViewById(R.id.btn_estorno_data_final_vencimento);
        btnVencDataIni = (Button) findViewById(R.id.btn_estorno_data_inicial_vencimento);
        btnPesquisarPeriodo = (Button) findViewById(R.id.btn_estorno_periodo);
    }

    private void preencherListView() {
        movimentoContaDAO = MovimentoContaDAO.getInstance(this);
        String sql = "select * from " + MovimentoEntidade.TABLE_NAME + " where st_estorno = 'N' and id_transferencia is null ";
        listaMovimentos = (ArrayList<MovimentoConta>) movimentoContaDAO.getMovimentoContaBySQL(sql, null);
        movimentoAdapter = new MovimentoAdapter(this, listaMovimentos);
    }

    private void desativarPesquisaVazia() {
        if (listaMovimentos.size() > 0) {
            temMovimento = true;
            edtPesquisa.setEnabled(true);
            //edtPesquisa.requestFocus();
        } else {
            edtPesquisa.setEnabled(false);
        }
    }

    private void atualizarListViewMovimento() {
        if (temMovimento) {
//            listViewMovimentos.setAdapter(
//                    new ArrayAdapter<MovimentoConta>(this,
//                            android.R.layout.simple_list_item_1, listaMovimentos));
            movimentoAdapter = new MovimentoAdapter(EstornoLista.this, listaMovimentos);
            listViewMovimentos.setAdapter(movimentoAdapter);
            listViewMovimentos.setCacheColorHint(Color.TRANSPARENT);
            pesquisar();
        }
    }

    private void pesquisar() {
        if (temMovimento) {
            int tamPesq = edtPesquisa.getText().length();
            if (!(listaMovimentoPesquisado == null)) {
                listaMovimentoPesquisado.clear();
                for (MovimentoConta movConta : listaMovimentos) {
                    String pesq = edtPesquisa.getText().toString();
                    String descricao = movConta.getDsHistorico();
                    if (tamPesq <= descricao.length()) {
                        if (pesq.equals(descricao.subSequence(0, tamPesq))) {
                            listaMovimentoPesquisado.add(movConta);
                        }
                    }
                }
            }
        }
    }

    private void limparPesquisa() {
        edtPesquisa.setText("");
        //edtPesquisa.requestFocus();
        dataVencIniSelecionado = false;
        dataVencFimSelecionado = false;
        btnVencDataIni.setText("Data Inicial");
        btnVencDataFim.setText("Data Final");
        atualizarListViewMovimento();
    }

    private boolean dataFimMaiorIgualDataIni() {
        int res = Util.comparaDtVencimentoEmissao(diaFim, mesFim, anoFim, diaIni, mesIni, anoIni);
        return res > -1;
    }

    private String getDataVencIni() {
        return diaIni + "/" + mesIni + "/" + anoIni;
    }

    private String getDataVencFim() {
        return diaFim + "/" + mesFim + "/" + anoFim;
    }

    private String getSqlPeriodoMovimento() {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from " + MovimentoEntidade.TABLE_NAME);
        sql.append(" where st_estorno = 'N' ");
        sql.append("   and dt_movimento between '" + Util.dateAtualToDateBD(getDataVencIni()) + "' ");
        sql.append("   and '" + Util.dateAtualToDateBD(getDataVencFim()) + "' ");
        sql.append(" and id_transferencia is null ");
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