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
import android.widget.TextView;

import java.util.Calendar;

import br.com.andrebronca.pluscontrol.dao.RelatorioDAO;
import br.com.andrebronca.pluscontrol.entidades.MovimentoEntidade;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RelatorioFluxoCaixaFiltro extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private Button btnDataIni, btnDataFim, btnExibir;
    private TextView tvDtAntiga, tvDtNova;
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataIniSelecionada = false, dataFimSelecionada = false;
    public DialogFragment newFragment;
    private String dtAntiga, dtNova;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_fluxo_caixa_filtro);
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
                intent = new Intent(RelatorioFluxoCaixaFiltro.this, RelatorioFluxoCaixa.class);
                if (dataIniSelecionada && dataFimSelecionada) {
                    params.putString("data", getDataMoviIni() + " até " + getDataMoviFim());
                } else {
                    params.putString("data", dtAntiga + " até " + dtNova);
                }
                params.putString("sql_movimentos", getSqlPeriodoVenc());
                intent.putExtras(params);
                startActivity(intent);
            }
        });
    }

    private void linkToXml() {
        btnDataIni = (Button) findViewById(R.id.btn_fluxo_caixa_data_ini);
        btnDataFim = (Button) findViewById(R.id.btn_fluxo_caixa_data_fim);
        btnExibir = (Button) findViewById(R.id.btn_fluxo_caixa_exibir);
        tvDtAntiga = (TextView) findViewById(R.id.tv_rel_fluxo_caixa_dt_antiga);
        tvDtNova = (TextView) findViewById(R.id.tv_rel_fluxo_caixa_dt_nova);
    }

    private void setTextDatas() {
        RelatorioDAO rel = RelatorioDAO.getInstance(this);
        String strDtAntiga = getSqlTituloDataVencimentoEmissaoByTipo(true);
        String strDtNova = getSqlTituloDataVencimentoEmissaoByTipo(false);
        dtAntiga = Util.dateToStringFormat(
                rel.getSqlDataEmissVencBySql("dt_movimento", strDtAntiga));
        tvDtAntiga.setText("Lançamento desde: " + dtAntiga);
        dtNova = Util.dateToStringFormat(
                rel.getSqlDataEmissVencBySql("dt_movimento", strDtNova));
        tvDtNova.setText("Lançamento até: " + dtNova);
    }

    private String getSqlTituloDataVencimentoEmissaoByTipo(boolean dtAntiga) {
        StringBuilder sb = new StringBuilder();
        sb.append("select dt_movimento from movimentoconta ");
        sb.append(" where st_estorno = 'N' ");
        sb.append(" order by id_movconta ");
        if (dtAntiga) {
            sb.append(" limit 1");  //retorna a menor data, dataAntiga
        } else {
            sb.append(" desc limit 1"); //retorna a maior data, dataNova
        }
        return sb.toString();
    }

    private String getDataMoviIni() {
        return diaIni + "/" + mesIni + "/" + anoIni;
    }

    private String getDataMoviFim() {
        return diaFim + "/" + mesFim + "/" + anoFim;
    }

    /**
     * Utilizar esse método em duas chamadas para exibir os valores de receita e despesas
     *
     * @return
     */
    private String getSqlPeriodoVenc() {
        StringBuilder sql = new StringBuilder();
        sql.append("select dt_movimento, st_tipo, ds_historico, vl_movimento, st_direcao ");
        sql.append("  from " + MovimentoEntidade.TABLE_NAME + " ");
        sql.append(" where st_estorno = 'N' ");
        if (dataIniSelecionada && dataFimSelecionada) {
            sql.append("   and dt_movimento between '" + Util.dateAtualToDateBD(getDataMoviIni()) + "' ");
            sql.append("   and '" + Util.dateAtualToDateBD(getDataMoviFim()) + "' ");
        } else {
            sql.append(" and dt_movimento between date('now','start of month') ");
            sql.append(" and date('now','start of month','+1 month','-1 day' )");
        }
        sql.append(" order by dt_movimento ");
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