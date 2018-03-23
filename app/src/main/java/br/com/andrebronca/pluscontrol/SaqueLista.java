package br.com.andrebronca.pluscontrol;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
//import android.app.DialogFragment;
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

public class SaqueLista extends AppCompatActivity {
    private EditText edtPesquisa;
    private Button btnLimpar, btnVencDataIni, btnVencDataFim, btnPesquisarPeriodo;
    private ListView listviewSaque;
    private ArrayList<MovimentoConta> listaSaque, listaSaquePeriodo;
    private ArrayList<MovimentoConta> listaSaquePesquisado = new ArrayList<>();
    private Intent intent;
    private Bundle params = new Bundle();
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataVencIniSelecionado = false, dataVencFimSelecionado = false;
    public DialogFragment newFragment;
    private boolean temSaque = false;
    private MovimentoAdapter movimentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saque_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linkToXml();
        setListSaque();
        desativarPesquisaVazia();
        atualizarListViewSaque();

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
//                listviewSaque.setAdapter(new ArrayAdapter<MovimentoConta>(SaqueLista.this,
//                        android.R.layout.simple_list_item_1,
//                        listaSaquePesquisado));
                movimentoAdapter = new MovimentoAdapter(SaqueLista.this, listaSaquePesquisado);
                listviewSaque.setAdapter(movimentoAdapter);
                listviewSaque.setCacheColorHint(Color.TRANSPARENT);
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(SaqueLista.this, edtPesquisa);
            }
        });

        btnLimpar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                limparPesquisa();
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
                        MovimentoContaDAO moviContaDao = MovimentoContaDAO.getInstance(SaqueLista.this);
                        listaSaquePeriodo = (ArrayList<MovimentoConta>) moviContaDao.getMovimentoContaBySQL(getSqlPeriodoVenc(), null);
//                        listviewSaque.setAdapter(
//                                new ArrayAdapter<MovimentoConta>( SaqueLista.this,
//                                        android.R.layout.simple_list_item_1,
//                                        listaSaquePeriodo));
                        movimentoAdapter = new MovimentoAdapter(SaqueLista.this, listaSaquePeriodo);
                        listviewSaque.setAdapter(movimentoAdapter);
                        listviewSaque.setCacheColorHint(Color.TRANSPARENT);
                    } else {
                        Util.exibeMensagem(SaqueLista.this, "Data final deve ser \nigual ou superior a data inicial!");
                    }
                } else {
                    Util.exibeMensagem(SaqueLista.this, "Deve ter um período selecionado!");
                }

            }
        });

        listviewSaque.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(SaqueLista.this, SaqueDetalhes.class);
                MovimentoConta movConta = (MovimentoConta) listviewSaque.getItemAtPosition(position);
                params.putInt("id_origem", movConta.getIdMovConta());
                intent.putExtras(params);
                startActivity(intent);
                return true;
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(SaqueLista.this, SaqueAdicionar.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    private void linkToXml() {
        edtPesquisa = (EditText) findViewById(R.id.edt_saque_pesquisar_lista);
        btnLimpar = (Button) findViewById(R.id.btn_saque_limpar_pesquisa);
        listviewSaque = (ListView) findViewById(R.id.ltv_saque_lista);
        btnVencDataIni = (Button) findViewById(R.id.btn_saque_data_inicial_vencimento);
        btnVencDataFim = (Button) findViewById(R.id.btn_saque_data_final_vencimento);
        btnPesquisarPeriodo = (Button) findViewById(R.id.btn_saque_periodo);
    }

    private void setListSaque() {
        MovimentoContaDAO movimentoContaDAO = MovimentoContaDAO.getInstance(this);
        ContaDAO contaDAO = ContaDAO.getInstance(this);
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        String sql = "select * from " + MovimentoEntidade.TABLE_NAME + " where st_tipo = 'S' and st_direcao = 'S' ";
        listaSaque = (ArrayList<MovimentoConta>) movimentoContaDAO.getMovimentoContaBySQL(sql, null);
        movimentoAdapter = new MovimentoAdapter(this, listaSaque);
    }

//    private void preencherListaSaque(){
//        listviewSaque.setAdapter(new ArrayAdapter<MovimentoConta>(this,
//                android.R.layout.simple_list_item_1, listaSaque));
//    }

    private void desativarPesquisaVazia() {
        if (listaSaque.size() > 0) {
            temSaque = true;
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
        atualizarListViewSaque();
        //preencherListaSaque();
    }

    private void atualizarListViewSaque() {
        if (temSaque) {
//            listviewSaque.setAdapter(
//                    new ArrayAdapter<MovimentoConta>(this,
//                            android.R.layout.simple_list_item_1, listaSaque));
            movimentoAdapter = new MovimentoAdapter(SaqueLista.this, listaSaque);
            listviewSaque.setAdapter(movimentoAdapter);
            listviewSaque.setCacheColorHint(Color.TRANSPARENT);
            pesquisar();
        }
    }

    private void pesquisar() {
        if (temSaque) {
            int tamPesq = edtPesquisa.getText().length();
            if (!(listaSaquePesquisado == null)) {
                listaSaquePesquisado.clear();
                for (MovimentoConta movConta : listaSaque) {
                    String pesq = edtPesquisa.getText().toString();
                    String descricao = movConta.getDsHistorico();
                    if (tamPesq <= descricao.length()) {
                        if (pesq.equals(descricao.subSequence(0, tamPesq))) {
                            listaSaquePesquisado.add(movConta);
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
        sql.append(" where st_tipo = 'S' ");
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