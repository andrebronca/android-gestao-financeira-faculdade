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

import br.com.andrebronca.pluscontrol.adapter.TituloAdapter;
import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RecebimentoLista extends AppCompatActivity {
    private EditText edtPesquisa;
    private Button btnLimpar, btnVencDataIni, btnVencDataFim, btnPesquisarPeriodo;
    private ListView listviewRecebimento;
    private TituloDAO tituloDAO;
    private ArrayList<Titulo> listaTitulo, listaTituloPeriodo;
    private ArrayList<Titulo> listaTituloPesquisado = new ArrayList<>();
    private Intent intent;
    private Bundle params = new Bundle();
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataVencIniSelecionado = false, dataVencFimSelecionado = false;
    public DialogFragment newFragment;
    private boolean temTitulo = false;
    private TituloAdapter tituloAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recebimento_lista);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linkToXml();
        setListTituloRecebimento();
        desativarPesquisaVazia();
        atualizarListViewTitulo();

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
//                listviewRecebimento.setAdapter(new ArrayAdapter<Titulo>(RecebimentoLista.this,
//                        android.R.layout.simple_list_item_1,
//                        listaTituloPesquisado));
                tituloAdapter = new TituloAdapter(RecebimentoLista.this, listaTituloPesquisado);
                listviewRecebimento.setAdapter(tituloAdapter);
                listviewRecebimento.setCacheColorHint(Color.TRANSPARENT);
            }
        });

        edtPesquisa.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(RecebimentoLista.this, edtPesquisa);
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
                        TituloDAO titDao = TituloDAO.getInstance(RecebimentoLista.this);
                        listaTituloPeriodo = (ArrayList<Titulo>) titDao.getTituloBySQL(getSqlPeriodoVenc(), null);
//                        listviewRecebimento.setAdapter(
//                                new ArrayAdapter<Titulo>( RecebimentoLista.this,
//                                        android.R.layout.simple_list_item_1,
//                                        listaTituloPeriodo));
                        tituloAdapter = new TituloAdapter(RecebimentoLista.this, listaTituloPeriodo);
                        listviewRecebimento.setAdapter(tituloAdapter);
                        listviewRecebimento.setCacheColorHint(Color.TRANSPARENT);
                    } else {
                        Util.exibeMensagem(RecebimentoLista.this, "Data final deve ser \nigual ou superior a data inicial!");
                    }
                } else {
                    Util.exibeMensagem(RecebimentoLista.this, "Deve ter um período selecionado!");
                }

            }
        });

        listviewRecebimento.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                intent = new Intent(RecebimentoLista.this, RecebimentoDetalhes.class);
                Titulo titulo = (Titulo) listviewRecebimento.getItemAtPosition(position);
                params.putInt("id", titulo.getIdTitulo());
                params.putInt("id_parcela", titulo.getIdParcela());
                intent.putExtras(params);
                startActivity(intent);
                return true;
            }
        });

    }

    private void linkToXml() {
        edtPesquisa = (EditText) findViewById(R.id.edt_recebimento_pesquisar_lista);
        btnLimpar = (Button) findViewById(R.id.btn_recebimento_limpar_pesquisa);
        listviewRecebimento = (ListView) findViewById(R.id.ltv_recebimento_lista);
        btnVencDataIni = (Button) findViewById(R.id.btn_recebimento_data_inicial_vencimento);
        btnVencDataFim = (Button) findViewById(R.id.btn_recebimento_data_final_vencimento);
        btnPesquisarPeriodo = (Button) findViewById(R.id.btn_recebimento_periodo);
    }

    private void setListTituloRecebimento() {
        tituloDAO = TituloDAO.getInstance(RecebimentoLista.this);
        CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(this);
        PessoaDAO pessoaDAO = PessoaDAO.getInstance(RecebimentoLista.this, "RecebimentoLista_setListTituloRecebimento()");

        String sql = "select * from " + TituloEntidade.TABLE_NAME + " where tp_natureza = 'R' and vl_saldo > 0";
        listaTitulo = (ArrayList<Titulo>) tituloDAO.getTituloBySQL(sql, null);
        tituloAdapter = new TituloAdapter(this, listaTitulo);
    }

    private void desativarPesquisaVazia() {
        if (listaTitulo.size() > 0) {
            temTitulo = true;
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
        atualizarListViewTitulo();
    }

    private void atualizarListViewTitulo() {
        if (temTitulo) {
//            listviewRecebimento.setAdapter(
//                    new ArrayAdapter<Titulo>(this,
//                            android.R.layout.simple_list_item_1, listaTitulo));
            tituloAdapter = new TituloAdapter(this, listaTitulo);
            listviewRecebimento.setAdapter(tituloAdapter);
            listviewRecebimento.setCacheColorHint(Color.TRANSPARENT);
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

    /*
    select * from titulo
where tp_natureza = 'R'
and dt_vencimento between '2016-09-15' and '2016-10-15'
and vl_saldo > 0
     */
    private String getSqlPeriodoVenc() {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from " + TituloEntidade.TABLE_NAME);
        sql.append(" where tp_natureza = 'R' ");
        sql.append("   and dt_vencimento between '" + Util.dateAtualToDateBD(getDataVencIni()) + "' ");
        sql.append("   and '" + Util.dateAtualToDateBD(getDataVencFim()) + "' ");
        sql.append("   and vl_saldo > 0 ");
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