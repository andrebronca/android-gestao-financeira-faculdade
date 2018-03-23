package br.com.andrebronca.pluscontrol;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import java.util.Date;
import java.util.List;

import br.com.andrebronca.pluscontrol.dao.CategoriaDAO;
import br.com.andrebronca.pluscontrol.dao.PessoaDAO;
import br.com.andrebronca.pluscontrol.dao.TituloDAO;
import br.com.andrebronca.pluscontrol.modelo.Categoria;
import br.com.andrebronca.pluscontrol.modelo.Pessoa;
import br.com.andrebronca.pluscontrol.modelo.Titulo;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class DespesaAdicionar extends AppCompatActivity {
    private Spinner spnCategoria, spnFornecedor;
    private EditText edtDescricao, edtValor, edtParcela;
    private TextView tvAjudaValor, tvAjudaParcela;
    private CheckBox cbExibeRelatorio, cbxDividir;
    private Button btnSalvar, btnCancelar, btnEmissao, btnVencimento;
    private DatePickerDialog datePickerDialog;
    private List<Categoria> listaCategoria;
    private CategoriaDAO categoriaDAO;
    private ArrayAdapter<Categoria> adapterCategoria;
    private List<Pessoa> listaFornecedor;
    private PessoaDAO pessoaDAO;
    private ArrayAdapter<Pessoa> adapterFornecedor;
    private DialogFragment newFragment;
    private final String SELECIONE = "--SELECIONE--";
    private Intent intent;
    private int diaEmiss = 0, mesEmiss = 0, anoEmiss = 0, diaVenc = 0, mesVenc = 0, anoVenc = 0;
    private Categoria categoriaSelecionada;
    private Pessoa pessoaSelecionada;
    private double valor;
    private int qtdParcela;
    private boolean exibeRelatorio;
    private String descricao;
    private boolean vencSelecionado = false, emissSelecionado = false;
    private TituloDAO tituloDAO;
    private Titulo titulo;
    private final String NATUREZA_D = "D";
    private int maxIdTitulo;
    private boolean temMaxIdTitulo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesa_adicionar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        linkToXml();
        preencheListaCategoria();
        preencheListaFornecedor();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnEmissao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "E");
            }
        });

        btnVencimento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "V");
            }
        });

        edtDescricao.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(DespesaAdicionar.this, edtDescricao);
            }
        });

        edtValor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(DespesaAdicionar.this, edtValor);
            }
        });

        edtParcela.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Util.hideKeyboard(DespesaAdicionar.this, edtParcela);
            }
        });

        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validarDados()) {
                    for (Titulo titulo : getListaTituloSalvar()) {
                        tituloDAO = TituloDAO.getInstance(DespesaAdicionar.this);
                        tituloDAO.salvarTitulo(titulo);
                    }
                    Util.exibeMensagem(getApplicationContext(), "Salvo com sucesso!");
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

        spnCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoriaSelecionada = (Categoria) spnCategoria.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spnFornecedor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pessoaSelecionada = (Pessoa) spnFornecedor.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        tvAjudaValor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(DespesaAdicionar.this);
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

        tvAjudaParcela.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(DespesaAdicionar.this);
                alerta.setTitle("Ajuda sobre dividir o valor!");
                alerta.setIcon(R.drawable.ic_ajuda_24dp);
                alerta.setCancelable(false);
                StringBuilder sb = new StringBuilder();
                sb.append("Ex. 1: Não selecionar a divisão\n");
                sb.append("Valor: 1000\t\tParcela: 2\n");
                sb.append("Valor de cada parcela: 1000\n\n");
                sb.append("Ex. 2: Selecionar a divisão\n");
                sb.append("Valor: 1000\t\tParcela: 2\n");
                sb.append("Valor de cada parcela: 500\n");
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

    private String getDataEmissao() {
        return diaEmiss + "/" + mesEmiss + "/" + anoEmiss;
    }

    private String getDataVencimento() {
        return diaVenc + "/" + mesVenc + "/" + anoVenc;
    }

    private void linkToXml() {
        spnCategoria = (Spinner) findViewById(R.id.spn_despesa_adicionar_categoria);
        spnFornecedor = (Spinner) findViewById(R.id.spn_despesa_adicionar_cliente);
        edtDescricao = (EditText) findViewById(R.id.edt_despesa_editar_descricao);
        edtValor = (EditText) findViewById(R.id.edt_despesa_editar_valor);
        cbxDividir = (CheckBox) findViewById(R.id.cbx_despesa_adicionar_dividir);
        edtParcela = (EditText) findViewById(R.id.edt_despesa_editar_parcela);
        cbExibeRelatorio = (CheckBox) findViewById(R.id.cb_despesa_editar_relatorio);
        btnSalvar = (Button) findViewById(R.id.btn_despesa_editar_salvar);
        btnCancelar = (Button) findViewById(R.id.btn_despesa_adicionar_cancelar);
        btnEmissao = (Button) findViewById(R.id.btn_despesa_editar_emissao);
        btnVencimento = (Button) findViewById(R.id.btn_despesa_editar_vencimento);
        tvAjudaValor = (TextView) findViewById(R.id.lb_despesa_adicionar_valor);
        tvAjudaParcela = (TextView) findViewById(R.id.tv_despesa_adicionar_ajuda_parcela);
    }

    private void fecharActivity() { //ok
        intent = new Intent(DespesaAdicionar.this, DespesaLista.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private String getSelecionaRelatorio() {
        return exibeRelatorio ? "S" : "N";
    }

    private List<Titulo> getListaTituloSalvar() {
        List<Titulo> lista = new ArrayList<>();
        double valor_parcela;
        if (cbxDividir.isChecked()) {
            if (qtdParcela > 1) {
                valor_parcela = Util.getValorParcelaDuasCasas(valor, qtdParcela);
            } else {
                valor_parcela = valor;
            }
        } else {
            valor_parcela = valor;
        }
        for (int p = 1; p <= qtdParcela; p++) {
            Titulo titulo = getTitulo(p, valor_parcela,
                    Util.getDtVencimentoParcelas(diaVenc, mesVenc, anoVenc, p));
//                    Util.getDtVencimentoParcelas(getDataVencimento(),p));
            lista.add(titulo);
        }
        return lista;
    }

    private Titulo getTitulo(int nr_parcela, double valorParcela, String dtVencimento) {
        if (!temMaxIdTitulo) {
            selectMaxIdTitulo();
        }
        Titulo titulo = new Titulo();
        titulo.setIdTitulo(getMaxIdTitulo());
        titulo.setIdParcela(nr_parcela);
        titulo.setPessoa(pessoaSelecionada);
        titulo.setTpNatureza(NATUREZA_D);
        titulo.setDtEmissao(Util.dateAtualToDateBD(getDataEmissao()));
        titulo.setDtVencimento(dtVencimento);
        titulo.setVlTitulo(valor);
        titulo.setVlParcela(valorParcela);
        titulo.setVlSaldo(valorParcela);
        titulo.setQtParcela(qtdParcela);
        titulo.setStRelatorio(getSelecionaRelatorio());
        titulo.setStMovimento("N");
        titulo.setDsTitulo(descricao);
        titulo.setCategoria(categoriaSelecionada);
        titulo.setDtCadastro(Util.dateAtualToDateBD(Util.getDataAtual()));
        return titulo;
    }

    public int getMaxIdTitulo() {
        return maxIdTitulo;
    }

    public void setMaxIdTitulo(int maxIdTitulo) {
        this.maxIdTitulo = maxIdTitulo;
    }

    private void selectMaxIdTitulo() {
        TituloDAO tituloDAO = TituloDAO.getInstance(this);
        int id = tituloDAO.getMaxIDTitulo();
        if (id <= 0) {
            setMaxIdTitulo(1);
        } else {
            setMaxIdTitulo(id + 1);
        }
        temMaxIdTitulo = true;
    }

    private void getDadosFormulario() {
        descricao = edtDescricao.getText().toString();

        try {
            valor = Double.parseDouble(edtValor.getText().toString());
        } catch (NumberFormatException e) {
            edtValor.setError("Valor inválido!");
            e.printStackTrace();
        }

        try {
            qtdParcela = Integer.parseInt(edtParcela.getText().toString());
        } catch (NumberFormatException e) {
            edtParcela.setError("Quantidade inválida!");
            e.printStackTrace();
        }

        exibeRelatorio = cbExibeRelatorio.isChecked();
    }

    private boolean validarDados() {
        getDadosFormulario();
        Categoria categoria = getFirstCategoria();
        Pessoa pessoa = getFirstPessoa();

        if (categoriaSelecionada != null) {
            if (categoriaSelecionada.getDsCategoria().equals(categoria.getDsCategoria())) {
                Util.exibeMensagem(this, "Selecione uma categoria!");
                return false;
            }
        }
        if (pessoaSelecionada != null) {
            if (pessoaSelecionada.getNmPessoa().equals(pessoa.getNmPessoa())) {
                Util.exibeMensagem(this, "Selecione um fornecedor!");
                return false;
            }
        }
        if (descricao.trim().length() < 3) {
            edtDescricao.setError("Informe uma descrição breve com mais de 3 caracteres!");
            return false;
        }
        if (!emissSelecionado) {
            Util.exibeMensagem(this, "Selecione uma data de emissão!");
            return false;
        }
        if (!vencSelecionado) {
            Util.exibeMensagem(this, "Selecione uma data de vencimento!");
            return false;
        }
        if (valor < 0.0) {
            edtValor.setError("Valor não pode ser inferior a 0.01!");
            return false;
        }
        if (qtdParcela < 1) {
            edtParcela.setError("Quantidade mínima de parcelas deve ser: 1!");
            return false;
        }
        if (emissSelecionado && vencSelecionado) {
//            long emissao = Util.convertDataParaNumero(diaEmiss, mesEmiss, anoEmiss);
//            long vencimento = Util.convertDataParaNumero(diaVenc, mesVenc, anoVenc);
            int res = Util.comparaDtVencimentoEmissao(diaVenc, mesVenc, anoVenc, diaEmiss, mesEmiss, anoEmiss);
            if (res == -1) { //data de emissão é maior
                Util.exibeMensagem(this, "Data de Emissão não pode ser maior do que a de vencimento!");
                return false;
            }
        }
        return true;
    }

    private void preencheListaCategoria() {
        listaCategoria = new ArrayList<>();
        categoriaDAO = CategoriaDAO.getInstance(this);
        listaCategoria = categoriaDAO.getCategoriaByTipo(NATUREZA_D);
        listaCategoria.add(0, getFirstCategoria());
        adapterCategoria = new ArrayAdapter<Categoria>(this, android.R.layout.simple_spinner_dropdown_item, listaCategoria);
        spnCategoria.setAdapter(adapterCategoria);
    }

    private void preencheListaFornecedor() {
        listaFornecedor = new ArrayList<>();
        pessoaDAO = PessoaDAO.getInstance(this, "DespesaAdicionar_preencheListaFornecedor()");
        listaFornecedor = pessoaDAO.getListaPessoaFornecedorOrAmbos();
        listaFornecedor.add(0, getFirstPessoa());
        adapterFornecedor = new ArrayAdapter<Pessoa>(this, android.R.layout.simple_spinner_dropdown_item, listaFornecedor);
        spnFornecedor.setAdapter(adapterFornecedor);
    }

    /**
     * O objetivo é exibir uma descrição indicando que o usuário deve selecionar.
     *
     * @return
     */
    private Pessoa getFirstPessoa() {
        Pessoa pessoa = new Pessoa();
        pessoa.setIdPessoa(-1);
        pessoa.setNmPessoa(SELECIONE);
        return pessoa;
    }

    /**
     * O objetivo é exibir uma descrição forçando o usuário a selecionar uma categoria.
     *
     * @return
     */
    private Categoria getFirstCategoria() {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(-1);
        categoria.setDsCategoria(SELECIONE);
        return categoria;
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