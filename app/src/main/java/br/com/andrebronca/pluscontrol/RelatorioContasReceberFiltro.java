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
import br.com.andrebronca.pluscontrol.entidades.TituloEntidade;
import br.com.andrebronca.pluscontrol.utilitarios.Util;

public class RelatorioContasReceberFiltro extends AppCompatActivity {
    private Intent intent;
    private Bundle params = new Bundle();
    private Button btnDataIni, btnDataFim, btnExibir;
    private TextView tvDtAntiga, tvDtNova;
    private int diaIni = 0, mesIni = 0, anoIni = 0, diaFim = 0, mesFim = 0, anoFim = 0;
    private boolean dataIniSelecionada = false, dataFimSelecionada = false;
    private DialogFragment newFragment;
    private String dataDe, dataAte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relatorio_contas_receber_filtro);
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
                if (periodoValido()) {
                    intent = new Intent(RelatorioContasReceberFiltro.this, RelatorioContasReceber.class);
                    if (dataIniSelecionada && dataFimSelecionada) {
                        params.putString("data", getDataVencIni() + " até " + getDataVencFim());
                    } else {
                        params.putString("data", dataDe + " até " + dataAte);
                    }
                    params.putString("sql_contas", getSqlContasReceber());
                    intent.putExtras(params);
                    startActivity(intent);
                } else {
                    Util.exibeMensagem(RelatorioContasReceberFiltro.this, "Data inicial deve ser menor ou igual\n a data final!");
                }
            }
        });
    }


    private void linkToXml() {
        btnDataIni = (Button) findViewById(R.id.btn_contas_receber_data_ini);
        btnDataFim = (Button) findViewById(R.id.btn_contas_receber_data_fim);
        btnExibir = (Button) findViewById(R.id.btn_contas_receber_exibir);
        tvDtAntiga = (TextView) findViewById(R.id.tv_rel_contas_receber_dt_antiga);
        tvDtNova = (TextView) findViewById(R.id.tv_rel_contas_receber_dt_nova);
    }

    private void setTextDatas() {
        RelatorioDAO rel = RelatorioDAO.getInstance(this);
        String strDataDe = getSqlTituloDataVencimentoEmissaoByTipo(true);
        String strDataAte = getSqlTituloDataVencimentoEmissaoByTipo(false);
        dataDe = Util.dateToStringFormat(
                rel.getSqlDataEmissVencBySql("dt_vencimento", strDataDe));
        dataAte = Util.dateToStringFormat(
                rel.getSqlDataEmissVencBySql("dt_vencimento", strDataAte));
        tvDtAntiga.setText("Vencimento desde: " + dataDe);
        tvDtNova.setText("Vencimento até: " + dataAte);
    }

    private String getSqlTituloDataVencimentoEmissaoByTipo(boolean dtAntiga) {
        StringBuilder sb = new StringBuilder();
        sb.append("select dt_vencimento from titulo ");
        sb.append(" where tp_natureza = 'R' and dt_baixa = '--' ");
        sb.append(" order by dt_vencimento ");
        if (dtAntiga) {
            sb.append(" limit 1");  //retorna a menor data, dataAntiga
        } else {
            sb.append(" desc limit 1"); //retorna a maior data, dataNova
        }
        return sb.toString();
    }

    private boolean periodoValido() {
//        long dtIni = Util.convertDataParaNumero(diaIni, mesIni, anoIni);
//        long dtFim = Util.convertDataParaNumero(diaFim, mesFim, anoFim);
        int res = Util.comparaDtVencimentoEmissao(diaFim, mesFim, anoFim, diaIni, mesIni, anoIni);
        return res > -1;
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
     * @return
     */
    private String getSqlContasReceber() {
        StringBuilder sql = new StringBuilder();
        sql.append("select t.id_titulo, t.id_parcela, t.ds_titulo, t.dt_vencimento, ");
        sql.append(" t.vl_saldo, c.ds_categoria, p.nm_pessoa ");
        sql.append("  from " + TituloEntidade.TABLE_NAME + " as t ");
        sql.append(" inner join categoria as c using (id_categoria) ");
        sql.append(" inner join pessoa as p using (id_pessoa) ");
        sql.append(" where t.dt_baixa = '--' ");
        sql.append("   and t.tp_natureza = 'R' ");
        if (dataIniSelecionada && dataFimSelecionada) {
            sql.append("   and t.dt_vencimento between '" + Util.dateAtualToDateBD(getDataVencIni()) + "' ");
            sql.append("   and '" + Util.dateAtualToDateBD(getDataVencFim()) + "' ");
        } else {
            sql.append(" and t.dt_vencimento between date('now','start of month') ");
            sql.append(" and date('now','start of month','+1 month','-1 day' )");
        }
        sql.append(" order by t.dt_vencimento ");
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