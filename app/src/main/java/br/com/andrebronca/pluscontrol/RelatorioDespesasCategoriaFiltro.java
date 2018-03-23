package br.com.andrebronca.pluscontrol;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Calendar;

import br.com.andrebronca.pluscontrol.dao.RelatorioDAO;
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RelatorioDespesasCategoriaFiltro extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private Button btnDataIni, btnDataFim, btnExibir;
    private RadioButton rbReceita, rbDespesa;
    private TextView tvDtAntiga, tvDtNova;
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataIniSelecionada = false, dataFimSelecionada = false;
    public DialogFragment newFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_despesas_categoria_filtro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linkToXml();
        setTextDatas();

        btnDataIni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "I");
            }
        });

        btnDataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "F");
            }
        });

        btnExibir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(RelatorioDespesasCategoriaFiltro.this, RelatorioDespesasCategoria.class);
                if (dataIniSelecionada && dataFimSelecionada) {
                    params.putString("data", getDataVencIni() + " até " + getDataVencFim());
                }
                if (rbReceita.isChecked()) {
                    params.putString("sql_grafico", getSqlPeriodoVenc("R"));
                    params.putString("rel_tipo", "R");
//                    params.putString("sql_receita", getSqlPeriodoVenc("R"));
                } else {
                    params.putString("sql_grafico", getSqlPeriodoVenc("D"));
                    params.putString("rel_tipo", "D");
//                    params.putString("sql_despesa", getSqlPeriodoVenc("D"));
                }
                intent.putExtras(params);
                startActivity(intent);
            }
        });
    }

    private void linkToXml() {
        btnDataIni = (Button) findViewById(R.id.btn_contas_pagar_data_ini);
        btnDataFim = (Button) findViewById(R.id.btn_desp_categ_data_fim);
        btnExibir = (Button) findViewById(R.id.btn_contas_pagar_exibir);
        tvDtAntiga = (TextView) findViewById(R.id.tv_rel_contas_pagar_dt_antiga);
        tvDtNova = (TextView) findViewById(R.id.tv_rel_desp_cat_dt_nova);
        rbReceita = (RadioButton) findViewById(R.id.rb_rel_categoria_receita);
        rbDespesa = (RadioButton) findViewById(R.id.rb_rel_categoria_despesa);
    }

    private void setTextDatas() {
        RelatorioDAO rel = RelatorioDAO.getInstance(this);
        String dtAntiga = getSqlTituloDataVencimentoEmissaoByTipo(true);
        String dtNova = getSqlTituloDataVencimentoEmissaoByTipo(false);
        tvDtAntiga.setText("Emissão desde: " + Util.dateToStringFormat(
                rel.getSqlDataEmissVencBySql("dt_emissao", dtAntiga)));
        tvDtNova.setText("Emissão até: " + Util.dateToStringFormat(
                rel.getSqlDataEmissVencBySql("dt_emissao", dtNova)));
    }

    private String getSqlTituloDataVencimentoEmissaoByTipo(boolean dtAntiga) {
        StringBuilder sb = new StringBuilder();
        sb.append("select dt_emissao from titulo ");
        sb.append(" where id_parcela = 1 ");
        sb.append(" order by dt_emissao ");
        if (dtAntiga) {
            sb.append(" limit 1");  //retorna a menor data, dataAntiga
        } else {
            sb.append(" desc limit 1"); //retorna a maior data, dataNova
        }
        return sb.toString();
    }

    private String getDataVencIni() {
        return diaIni + "/" + mesIni + "/" + anoIni;
    }

    private String getDataVencFim() {
        return diaFim + "/" + mesFim + "/" + anoFim;
    }

    /**
     * Utilizar esse método em duas chamadas para exibir os valores de receita e despesas
     *
     * @param tipo = R ou D
     * @return
     */
    private String getSqlPeriodoVenc(String tipo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select c.ds_categoria, c.cd_cor, sum(vl_titulo) as vl_total ");
        sql.append("  from " + TituloEntidade.TABLE_NAME + " as t ");
        sql.append(" inner join categoria as c on (t.id_categoria = c.id_categoria) ");
        sql.append(" where t.tp_natureza = '" + tipo + "' ");
        sql.append("   and t.st_relatorio = 'S'");
        sql.append("   and t.id_parcela = 1 ");
        if (dataIniSelecionada && dataFimSelecionada) {
            sql.append("   and t.dt_emissao between '" + Util.dateAtualToDateBD(getDataVencIni()) + "' ");
            sql.append("   and '" + Util.dateAtualToDateBD(getDataVencFim()) + "' ");
        } else {
            sql.append(" and t.dt_vencimento between date('now','start of month') ");
            sql.append(" and date('now','start of month','+1 month','-1 day' )");
        }
        sql.append(" group by t.id_categoria ");
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
                dataIniSelecionada = true;
                String str = diaIni + "/" + mesIni + "/" + anoIni;
                btnDataIni.setText(str);
            }
            if (tag.equals("F")) {
                diaFim = dayOfMonth;
                mesFim = monthOfYear + 1;
                anoFim = year;
                dataFimSelecionada = true;
                String str = diaFim + "/" + mesFim + "/" + anoFim;
                btnDataFim.setText(str);
            }
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            if (dataIniSelecionada) {
                year = anoIni;
                month = mesIni - 1;
                day = diaIni;
            }
            if (dataFimSelecionada) {
                year = anoFim;
                month = mesFim - 1;
                day = diaFim;
            }

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

    }

}