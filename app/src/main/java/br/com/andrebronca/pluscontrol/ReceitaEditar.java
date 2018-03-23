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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class ReceitaEditar extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private int id_titulo, id_parcela, qt_parcela;
    private String ds_titulo, st_relatorio, dt_emissao, dt_vencimento;
    private double vl_titulo, vl_saldo;
    private int id_categoria, id_pessoa;
    private Spinner spnCategoria, spnPessoa;
    private EditText edtDescricao, edtValor, edtParcela;
    private CheckBox cbExibeRelatorio;
    private Button btnSalvar, btnExcluir, btnEmissao, btnVencimento;
    private TextView tvEmissao, tvVencimento, tvAjudaValor;
    private Titulo titulo;
    private List<Categoria> listaCategoria;
    private List<Pessoa> listaPessoa;
    private ArrayAdapter<Categoria> adapterCategoria;
    private ArrayAdapter<Pessoa> adapterPessoa;
    private final String SELECIONE = "--SELECIONE--";
    private int diaEmiss = 0, mesEmiss = 0, anoEmiss = 0, diaVenc = 0, mesVenc = 0, anoVenc = 0;
    private boolean vencSelecionado = false, emissSelecionado = false;
    private Categoria categoriaSelecionada;
    private Pessoa pessoaSelecionada;
    private boolean valorValido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receita_editar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        intent = getIntent();
        if (intent != null) {
            params = intent.getExtras();
            if (params != null) {
                id_titulo = params.getInt("id");
                id_parcela = params.getInt("id_parcela");
            }
        }

        linkToXml();
        preencherTitulo();
        preencherListaCategoria();
        preencherListaPessoa();
        setCategoriaDoTitulo();
        setPessoaDoTitulo();
        preencherFormulario();
        desativaEdicoesFormulario();
        //a
        spnCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSelecionada = (Categoria) spnCategoria.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //b
        spnPessoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa) spnPessoa.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //c
        edtDescricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(ReceitaEditar.this, edtDescricao);
            }
        });
        //d
        edtValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(ReceitaEditar.this, edtValor);
            }
        });
        //e
        btnEmissao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "E");
            }
        });
        //f
        btnVencimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "V");
            }
        });
        //g
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarDadosFormulario()) {
                    TituloDAO tituloDAO = TituloDAO.getInstance(ReceitaEditar.this);
                    tituloDAO.atualizarTituloBySql(createSqlUpdateTitulo(setTituloAtualizar()));
                    Util.exibeMensagem(getApplicationContext(), "Atualizado com sucesso!");
                    fecharActivity();
                }
            }
        });

        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ReceitaEditar.this);
                alerta
                        .setTitle("Atenção")
                        .setIcon(R.drawable.ic_delete_24dp)
                        .setCancelable(false)
                        .setMessage("Deseja excluir o Título?")
                        .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TituloDAO tituloDAO = TituloDAO.getInstance(ReceitaEditar.this);
                                tituloDAO.deleteTituloByID(id_titulo);
                                fecharActivity();
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

        tvAjudaValor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(ReceitaEditar.this);
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


    }//onCreate

    private void linkToXml() {
        spnCategoria = (Spinner) findViewById(R.id.spn_receita_edit_categoria);
        spnPessoa = (Spinner) findViewById(R.id.spn_receita_edit_pessoa);
        edtDescricao = (EditText) findViewById(R.id.edt_receita_edit_descricao);
        edtValor = (EditText) findViewById(R.id.edt_receita_edit_valor);
        edtParcela = (EditText) findViewById(R.id.edt_receita_edit_parcela);
        cbExibeRelatorio = (CheckBox) findViewById(R.id.cb_receita_edit_relatorio);
        btnSalvar = (Button) findViewById(R.id.btn_receita_edit_salvar);
        btnExcluir = (Button) findViewById(R.id.btn_receita_edit_excluir);
        btnEmissao = (Button) findViewById(R.id.btn_receita_edit_emissao);
        btnVencimento = (Button) findViewById(R.id.btn_receita_edit_vencimento);
        tvEmissao = (TextView) findViewById(R.id.lb_receita_edit_emissao);
        tvVencimento = (TextView) findViewById(R.id.lb_receita_edit_vencimento);
        tvAjudaValor = (TextView) findViewById(R.id.lb_receita_edit_valor);
    }

    //2
    private void preencherTitulo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        PessoaDAO pessoaDAO = PessoaDAO.getInstance(this, "ReceitaEditar_preencherTitulo");
        CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(this);
        titulo = tituloDAO.getTituloByID(id_titulo, id_parcela);
    }

    //3
    private void preencherListaCategoria() {
        listaCategoria = new ArrayList<>();
        CategoriaDAO categoriaDAO = CategoriaDAO.getInstance(ReceitaEditar.this);
        listaCategoria = categoriaDAO.getCategoriaByTipo("R");
        listaCategoria.add(0, getFirstCategoria());
        adapterCategoria = new ArrayAdapter<Categoria>(ReceitaEditar.this, android.R.layout.simple_spinner_dropdown_item, listaCategoria);
        spnCategoria.setAdapter(adapterCategoria);
    }

    //3.1
    private Categoria getFirstCategoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(-1);
        categoria.setDsCategoria(SELECIONE);
        return categoria;
    }

    //4
    private void preencherListaPessoa() {
        listaPessoa = new ArrayList<>();
        PessoaDAO pessoaDAO = PessoaDAO.getInstance(ReceitaEditar.this, "ReceitaEditar_preencheListaPessoa()");
        listaPessoa = pessoaDAO.getListaPessoaClienteOrAmbos();
        listaPessoa.add(0, getFirstPessoa());
        adapterPessoa = new ArrayAdapter<Pessoa>(ReceitaEditar.this, android.R.layout.simple_spinner_dropdown_item, listaPessoa);
        spnPessoa.setAdapter(adapterPessoa);
    }

    //4.1
    private Pessoa getFirstPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setIdPessoa(-1);
        pessoa.setNmPessoa(SELECIONE);
        return pessoa;
    }

    //5
    private void setCategoriaDoTitulo() {
        spnCategoria.post(new Runnable() {
            @Override
            public void run() {
                spnCategoria.setSelection(getPosicaoCategoria(titulo.getCategoria().getDsCategoria()));
            }
        });
    }

    //5.1
    private int getPosicaoCategoria(String iDesc) {
        int oPos = 0;
        for (Categoria categoria : listaCategoria) {
            if (categoria.getDsCategoria().equalsIgnoreCase(iDesc)) {
                return oPos;
            }
            oPos++;
        }
        return oPos;
    }

    //6
    private void setPessoaDoTitulo() {
        spnPessoa.post(new Runnable() {
            @Override
            public void run() {
                spnPessoa.setSelection(getPosicaoPessoa(titulo.getPessoa().getNmPessoa()));
            }
        });
    }

    //6.1
    private int getPosicaoPessoa(String iPessoa) {
        int oPos = 0;
        for (Pessoa pessoa : listaPessoa) {
            if (pessoa.getNmPessoa().equalsIgnoreCase(iPessoa)) {
                return oPos;
            }
            oPos++;
        }
        return oPos;
    }

    //7
    private void preencherFormulario() {
        edtDescricao.setText(titulo.getDsTitulo());
        edtValor.setText(String.valueOf(titulo.getVlTitulo()));
        qt_parcela = titulo.getQtParcela();
        edtParcela.setText(String.valueOf(qt_parcela));
        tvEmissao.setText(Util.dateToStringFormat(titulo.getDtEmissao()));
        tvVencimento.setText(Util.dateToStringFormat(titulo.getDtVencimento()));
        cbExibeRelatorio.setChecked(titulo.getStRelatorioBool());
        vl_saldo = titulo.getVlSaldo();
//        }
    }

    //8
    private void desativaEdicoesFormulario() {
        if (qt_parcela > 1) {
            btnVencimento.setEnabled(false);
        }
        edtParcela.setEnabled(false);
    }

    //9.1
    private void getDadosParaAtualizarTitulo() {
        ds_titulo = edtDescricao.getText().toString().trim();
        vl_titulo = getValorTitulo();
        vl_saldo = Util.getValorParcelaDuasCasas(vl_titulo, qt_parcela);
        st_relatorio = cbExibeRelatorio.isChecked() ? "S" : "N";

        dt_emissao = getDataEmissao();
        dt_vencimento = getDataVencimento();
        id_categoria = categoriaSelecionada != null ? categoriaSelecionada.getIdCategoria() : titulo.getIdCategoria();
        id_parcela = pessoaSelecionada != null ? pessoaSelecionada.getIdPessoa() : titulo.getIdPessoa();
    }

    //9.2
    private double getValorTitulo() {
        double vl = 0.0;
        String strValor = edtValor.getText().toString().trim();
        try {
            if (Util.validaValorInput(strValor)) {
                valorValido = true;
                vl = Double.parseDouble(strValor);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return Util.getValorMoedaDuasCasas(vl);
    }

    //9.3
    private String getDataEmissao() {
        if (emissSelecionado) {
            return Util.dateAtualToDateBD(diaEmiss + "/" + mesEmiss + "/" + anoEmiss);
        } else {
            return titulo.getDtEmissao();
        }
    }

    //9.4
    private String getDataVencimento() {
        if (vencSelecionado) {
            return Util.dateAtualToDateBD(diaVenc + "/" + mesVenc + "/" + anoVenc);
        } else {
            return titulo.getDtVencimento();
        }
    }

    //9
    private boolean validarDadosFormulario() {
        getDadosParaAtualizarTitulo();

        if (id_categoria == -1 || categoriaSelecionada.getDsCategoria().equals(SELECIONE)) {
            Util.exibeMensagem(this, "Selecione uma categoria!");
            return false;
        }
        if (id_pessoa == -1 || pessoaSelecionada.getNmPessoa().equals(SELECIONE)) {
            Util.exibeMensagem(this, "Selecione uma pessoa!");
            return false;
        }
        if (ds_titulo.trim().length() < 3) {
            edtDescricao.setError("Descrição deve ter mais de 3 caracteres!");
            return false;
        }
        if (vl_titulo <= 0.0) {
            edtValor.setError("Valor inválido\nDeve ser superior a 0.0!");
            return false;
        }
        if (!valorValido) {
            edtValor.setError("Revisar o valor!");
            return false;
        }
        if (emissSelecionado && vencSelecionado) {
//            long emissao = Util.convertDataParaNumero(diaEmiss, mesEmiss, anoEmiss);
//            long vencimento = Util.convertDataParaNumero(diaVenc, mesVenc, anoVenc);
            int res = Util.comparaDtVencimentoEmissao(diaVenc, mesVenc, anoVenc, diaEmiss, mesEmiss, anoEmiss);
            if (res == -1) {
                Util.exibeMensagem(this, "Data de Emissão não pode ser maior do que a de vencimento!");
                return false;
            }
        }
        return true;
    }

    //10.1
    private Titulo setTituloAtualizar() {    //se pegar os valores direto, nao precisa desse método
        Titulo obj = new Titulo();
        obj.setIdTitulo(id_titulo);
        obj.setDsTitulo(ds_titulo);
        obj.setVlTitulo(vl_titulo);
        obj.setVlParcela(vl_saldo); //é pra ser o valor valor da parcela
        obj.setVlSaldo(vl_saldo);
        obj.setStRelatorio(st_relatorio);
        obj.setDtEmissao(dt_emissao);
        obj.setDtVencimento(dt_vencimento);
        obj.setIdCategoria(id_categoria);
        obj.setIdPessoa(titulo.getIdPessoa());
        return obj;
    }

    //10
    private String createSqlUpdateTitulo(Titulo obj) {
        StringBuilder sql = new StringBuilder();
        sql.append("update " + TituloEntidade.TABLE_NAME + " ");
        sql.append(" set ds_titulo = '" + obj.getDsTitulo() + "', ");
        sql.append(" vl_titulo = " + obj.getVlTitulo() + ", ");
        sql.append(" vl_parcela = " + obj.getVlSaldo() + ", ");
        sql.append(" vl_saldo = " + obj.getVlSaldo() + ", ");
        sql.append(" st_relatorio = '" + obj.getStRelatorio() + "', ");
        sql.append(" dt_emissao = '" + obj.getDtEmissao() + "', ");
        if (qt_parcela == 1) {
            sql.append(" dt_vencimento = '" + obj.getDtVencimento() + "', ");
        }
        sql.append(" id_categoria = " + obj.getIdCategoria() + ", ");
        sql.append(" id_pessoa = " + obj.getIdPessoa() + " ");
        sql.append(" where id_titulo = " + obj.getIdTitulo());
        return sql.toString();
    }

    //11
    private void fecharActivity() {
        intent = new Intent(this, ReceitaLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

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
                String str = "Emissão: " + diaEmiss + "/" + mesEmiss + "/" + anoEmiss;
                btnEmissao.setText(str);
            }
            if (tag.equals("V")) {
                diaVenc = dayOfMonth;
                mesVenc = monthOfYear + 1;
                anoVenc = year;
                vencSelecionado = true;
                String str = "Vencimento: " + diaVenc + "/" + mesVenc + "/" + anoVenc;
                btnVencimento.setText(str);
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
            if (vencSelecionado) {
                year = anoVenc;
                month = mesVenc - 1;
                day = diaVenc;
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

    }


}
