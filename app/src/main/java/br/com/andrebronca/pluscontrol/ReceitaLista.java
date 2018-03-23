package br.com.andrebronca.pluscontrol;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import br.com.andrebronca.pluscontrol.adapter.TituloAdapter;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class ReceitaLista extends AppCompatActivity {
    private EditText edtPesquisa;
    private Button btnLimparPesquisa, btnVencDataIni, btnVencDataFim, btnPesquisarPeriodo;
    private ListView listViewTitulos;
    private Intent intent;
    private Titulo titulo;
    private TituloDAO tituloDAO;
    private ArrayList<Titulo> listaTitulo, listaTituloPeriodo;
    private ArrayList<Titulo> listaTituloPesquisado = new ArrayList<>();
    private boolean temTitulo = false;
    private String sql;
    private String[] selectionArgs;
    private Bundle params = new Bundle();
    private int idTitulo = 0;
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataVencIniSelecionado = false, dataVencFimSelecionado = false;
    public DialogFragment newFragment;
    private TituloAdapter tituloAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        linkToXml();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(ReceitaLista.this, ReceitaAdicionar.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tituloDAO = TituloDAO.getInstance(this);
        listaTitulo = (ArrayList<Titulo>) tituloDAO.listaTituloReceitaDescricao();
        tituloAdapter = new TituloAdapter(this, listaTitulo);

        desativarPesquisaVazia();
        atualizarListViewTitulo();

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
//                listViewTitulos.setAdapter(
//                        new ArrayAdapter<Titulo>(ReceitaLista.this,
//                                android.R.layout.simple_list_item_1, listaTituloPesquisado));
                tituloAdapter = new TituloAdapter(ReceitaLista.this, listaTituloPesquisado);
                listViewTitulos.setAdapter(tituloAdapter);
                listViewTitulos.setCacheColorHint(Color.TRANSPARENT);
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Util.hideKeyboard(ReceitaLista.this, edtPesquisa);
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
                        TituloDAO titDao = TituloDAO.getInstance(ReceitaLista.this);
                        listaTituloPeriodo = (ArrayList<Titulo>) titDao.getTituloBySQL(getSqlPeriodoVenc(), null);
//                        listViewTitulos.setAdapter(
//                                new ArrayAdapter<Titulo>( ReceitaLista.this,
//                                        android.R.layout.simple_list_item_1,
//                                        listaTituloPeriodo));
                        tituloAdapter = new TituloAdapter(ReceitaLista.this, listaTituloPeriodo);
                        listViewTitulos.setAdapter(tituloAdapter);
                        listViewTitulos.setCacheColorHint(Color.TRANSPARENT);
                    } else {
                        Util.exibeMensagem(ReceitaLista.this, "Data final deve ser \nigual ou superior a data inicial!");
                    }
                } else {
                    Util.exibeMensagem(ReceitaLista.this, "Deve ter um período selecionado!");
                }

            }
        });

        listViewTitulos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(ReceitaLista.this, ReceitaDetalhes.class);
                Titulo titulo = new Titulo();
                titulo = (Titulo) listViewTitulos.getItemAtPosition(position);
                params.putInt("id", titulo.getIdTitulo());
                params.putInt("id_parcela", titulo.getIdParcela());
                //params.putDouble("saldo", tituloDAO.getValorSaldoTitulo( idTitulo ));
                intent.putExtras(params);
                startActivity(intent);
                return true;
            }
        });

    }

    private void linkToXml() {
        edtPesquisa = (EditText) findViewById(R.id.edt_receita_pesquisar_lista);
        btnLimparPesquisa = (Button) findViewById(R.id.btn_receita_lista_limpar);
        listViewTitulos = (ListView) findViewById(R.id.ltv_receita_lista);
        btnVencDataFim = (Button) findViewById(R.id.btn_receita_data_final_vencimento);
        btnVencDataIni = (Button) findViewById(R.id.btn_receita_data_inicial_vencimento);
        btnPesquisarPeriodo = (Button) findViewById(R.id.btn_receita_periodo);
    }

    private void limparPesquisa() {
        edtPesquisa.setText("");
        edtPesquisa.requestFocus();
        dataVencIniSelecionado = false;
        dataVencFimSelecionado = false;
        btnVencDataIni.setText("Data Inicial");
        btnVencDataFim.setText("Data Final");
        atualizarListViewTitulo();
    }

    private void desativarPesquisaVazia() {
        if (listaTitulo.size() > 0) {
            temTitulo = true;
            edtPesquisa.setEnabled(true);
            edtPesquisa.requestFocus();
        } else {
            edtPesquisa.setEnabled(false);
        }
    }

    private void atualizarListViewTitulo() {
        if (temTitulo) {
//            listViewTitulos.setAdapter(
//                    new ArrayAdapter<Titulo>(this,
//                            android.R.layout.simple_list_item_1, listaTitulo));
            tituloAdapter = new TituloAdapter(ReceitaLista.this, listaTitulo);
            listViewTitulos.setAdapter(tituloAdapter);
            listViewTitulos.setCacheColorHint(Color.TRANSPARENT);
            pesquisar();
        }
    }

    private void pesquisar() {
        if (temTitulo) {
            int tamPesq = edtPesquisa.getText().length();
            if (!(listaTituloPesquisado == null)) {
                listaTituloPesquisado.clear();
                for (Titulo titulo : listaTitulo) {
                    String pesq = edtPesquisa.getText().toString();
                    String descricao = titulo.getDsTitulo();
                    if (tamPesq <= descricao.length()) {
                        if (pesq.equals(descricao.subSequence(0, tamPesq))) {
                            listaTituloPesquisado.add(titulo);
                        }
                    }
                }
            }
        }
    }

    private boolean dataFimMaiorIgualDataIni() {
//        long dataFim = Util.convertDataParaNumero(diaFim, mesFim, anoFim);
//        long dataIni = Util.convertDataParaNumero(diaIni, mesIni, anoIni);
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
        sql.append("select *, sum(vl_saldo) as vl_saldo from " + TituloEntidade.TABLE_NAME);
        sql.append(" where tp_natureza = 'R' ");
        sql.append("   and st_movimento = 'N' ");
        sql.append("   and dt_vencimento between '" + Util.dateAtualToDateBD(getDataVencIni()) + "' ");
        sql.append("   and '" + Util.dateAtualToDateBD(getDataVencFim()) + "' ");
        sql.append(" group by id_titulo ");
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