package br.com.andrebronca.pluscontrol.utilitarios;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.andrebronca.pluscontrol.R;

/**
 * Created by andrebronca on 01/10/16.
 */

public class Util {
    public static void exibeLogI(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void exibeMensagem(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static String getDataAtual() {
        long date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    /**
     * Data atual dd-MM-yyyy será gravada no BD no formato: yyyy-MM-dd
     *
     * @return
     */
    public static String getDataAtualToBD() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(getDataAtual());
    }

    public static String getPrimLetra(String s) {
        return String.valueOf(s.charAt(0));
    }

    public static String dateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(date);
    }

    public static boolean isEmailValido(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean validaCorInput(String cor) {
        String hexChar = "#0123456789ABCDEF";
        int tot = 0;
        for (int i = 0; i < cor.length(); i++) {
            if (hexChar.contains(String.valueOf(cor.charAt(i)))) {
                ++tot;
            }
        }
        return tot == cor.length();
    }

    public static boolean validaValorInput(String vl) {
        String avaliar = null;
        if (vl.contains(",")) {
            avaliar = vl.replace(",", ".");
        } else {
            avaliar = vl;
        }
        String[] aNum = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        String[] aPont = {".", "-"};
        int temNum = 0, temSinais = 0;
        for (String strN : aNum) {
            if (avaliar.contains(strN)) {
                temNum++;
            }
        }
        for (String strP : aPont) {
            if (avaliar.contains(strP)) {
                temSinais++;
            }
        }

        if (temNum == 0 && temSinais == 0) {   //não tem nada
            return false;
        } else if (temNum == 0 && temSinais == 1) {   //só tem . ou só -
            return false;
        } else if (temSinais == 2 && temNum == 0) {   //só tem - e .
            return false;
        } else if (temNum >= temSinais) { //tem pelo menos um número
            return true;
        } else if (temNum > 0 && temSinais == 2) {  // -.1
            return true;
        } else {
            return false;
        }
    }

    /**
     * Dessa forma também funciona, melhor do que extrar i milissegundos
     *
     * @param dia 10
     * @param mes 10
     * @param ano 2016
     * @return 20161010
     */ //todo: desativar
    public static long convertDataParaNumero(String s, int dia, int mes, int ano) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(ano, mes, dia);
        return calendar.getTimeInMillis();
    }

    /**
     * Utilizado onde tem data de emissao e vencimento
     *
     * @return 0 se forem iguais, -1 data emissao for maior, 1 data emissao for menor
     */
    public static int comparaDtVencimentoEmissao(int diaV, int mesV, int anoV, int diaE, int mesE, int anoE) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.set(anoE, mesE, diaE);
        calendar2.set(anoV, mesV, diaV);
        return calendar2.compareTo(calendar1);
    }

    /**
     * recebe data yyyy-MM-dd e retorna string de data no formato: dd/MM/yyyy
     *
     * @param dt
     * @return "--" para indicar que não tem data cadastrada
     */
    public static String dateToStringFormat(String dt) {
        String data = "--";
        boolean data_valida = dt != null;
        if (data_valida) {
            if (!dt.equals("") || !dt.equals("--")) {
                SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd");
                Date dt_bd = null;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    dt_bd = sdf_bd.parse(dt);
                    return sdf.format(dt_bd);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return data;
    }

    /**
     * recebe data no formato string dd/MM/yyyy e retorna yyyy-MM-dd
     *
     * @return
     */
    public static String dateAtualToDateBD(String ddMMyyyy) {
        String dataString = null;
        if (ddMMyyyy != null || ddMMyyyy != "") {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data = null;
            SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd");
            try {
                data = sdf.parse(ddMMyyyy);
                dataString = sdf_bd.format(data);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dataString;
    }

    public static String getDataHoraAtual() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
        return df.format(calendar.getTime());
    }

    /**
     * Utilizado nos detalhes
     *
     * @param cor
     * @param view
     */
    public static void setCorCategoria(String cor, View view) {
        switch (cor) {
            case "#696969":
                view.setBackgroundResource(R.drawable.bg_cor_01);
                break;
            case "#808080":
                view.setBackgroundResource(R.drawable.bg_cor_02);
                break;
            case "#A9A9A9":
                view.setBackgroundResource(R.drawable.bg_cor_03);
                break;
            case "#C0C0C0":
                view.setBackgroundResource(R.drawable.bg_cor_04);
                break;
            case "#000000":
                view.setBackgroundResource(R.drawable.bg_cor_05);
                break;
            case "#B0C4DE":
                view.setBackgroundResource(R.drawable.bg_cor_06);
                break;
            case "#00BFFF":
                view.setBackgroundResource(R.drawable.bg_cor_07);
                break;
            case "#1E90FF":
                view.setBackgroundResource(R.drawable.bg_cor_08);
                break;
            case "#4682B4":
                view.setBackgroundResource(R.drawable.bg_cor_09);
                break;
            case "#4169E1":
                view.setBackgroundResource(R.drawable.bg_cor_10);   //ok
                break;
            case "#6A5ACD":
                view.setBackgroundResource(R.drawable.bg_cor_11);
                break;
            case "#483D8B":
                view.setBackgroundResource(R.drawable.bg_cor_12);
                break;
            case "#0000FF":
                view.setBackgroundResource(R.drawable.bg_cor_13);
                break;
            case "#000080":
                view.setBackgroundResource(R.drawable.bg_cor_14);
                break;
            case "#66CDAA":
                view.setBackgroundResource(R.drawable.bg_cor_15);
                break;
            case "#3CB371":
                view.setBackgroundResource(R.drawable.bg_cor_16);
                break;
            case "#32CD32":
                view.setBackgroundResource(R.drawable.bg_cor_17);
                break;
            case "#9ACD32":
                view.setBackgroundResource(R.drawable.bg_cor_18);
                break;
            case "#6B8E23":
                view.setBackgroundResource(R.drawable.bg_cor_19);
                break;
            case "#228B22":
                view.setBackgroundResource(R.drawable.bg_cor_20);   //ok
                break;
            case "#008B8B":
                view.setBackgroundResource(R.drawable.bg_cor_21);
                break;
            case "#556B2F":
                view.setBackgroundResource(R.drawable.bg_cor_22);
                break;
            case "#808000":
                view.setBackgroundResource(R.drawable.bg_cor_23);
                break;
            case "#006400":
                view.setBackgroundResource(R.drawable.bg_cor_24);
                break;
            case "#BC8F8F":
                view.setBackgroundResource(R.drawable.bg_cor_25);
                break;
            case "#F4A460":
                view.setBackgroundResource(R.drawable.bg_cor_26);
                break;
            case "#CD853F":
                view.setBackgroundResource(R.drawable.bg_cor_27);
                break;
            case "#D2691E":
                view.setBackgroundResource(R.drawable.bg_cor_28);
                break;
            case "#8B4513":
                view.setBackgroundResource(R.drawable.bg_cor_29);
                break;
            case "#FF00FF":
                view.setBackgroundResource(R.drawable.bg_cor_30);   //ok
                break;
            case "#DA70D6":
                view.setBackgroundResource(R.drawable.bg_cor_31);
                break;
            case "#9400D3":
                view.setBackgroundResource(R.drawable.bg_cor_32);
                break;
            case "#8B008B":
                view.setBackgroundResource(R.drawable.bg_cor_33);
                break;
            case "#4B0082":
                view.setBackgroundResource(R.drawable.bg_cor_34);
                break;
            case "#FFC0CB":
                view.setBackgroundResource(R.drawable.bg_cor_35);
                break;
            case "#F08080":
                view.setBackgroundResource(R.drawable.bg_cor_36);
                break;
            case "#FF69B4":
                view.setBackgroundResource(R.drawable.bg_cor_37);
                break;
            case "#FF1493":
                view.setBackgroundResource(R.drawable.bg_cor_38);
                break;
            case "#FA8072":
                view.setBackgroundResource(R.drawable.bg_cor_39);
                break;
            case "#FF7F50":
                view.setBackgroundResource(R.drawable.bg_cor_40);   //ok
                break;
            case "#FF6347":
                view.setBackgroundResource(R.drawable.bg_cor_41);
                break;
            case "#FF0000":
                view.setBackgroundResource(R.drawable.bg_cor_42);
                break;
            case "#A52A2A":
                view.setBackgroundResource(R.drawable.bg_cor_43);
                break;
            case "#FFA500":
                view.setBackgroundResource(R.drawable.bg_cor_44);
                break;
            case "#FF8C00":
                view.setBackgroundResource(R.drawable.bg_cor_45);
                break;
            case "#FF4500":
                view.setBackgroundResource(R.drawable.bg_cor_46);
                break;
            case "#F0E68C":
                view.setBackgroundResource(R.drawable.bg_cor_47);
                break;
            case "#FFFF00":
                view.setBackgroundResource(R.drawable.bg_cor_48);
                break;
            case "#FFD700":
                view.setBackgroundResource(R.drawable.bg_cor_49);
                break;
            case "#FFFFFF":
                view.setBackgroundResource(R.drawable.bg_cor_50);
                break;
        }
    }

    /**
     * Utilizado no relatório de gráfico setorial
     *
     * @param cor
     * @return
     */
    public static int getCorIntRGB(String cor) {
        int rgb = 0;
        switch (cor) {
            case "#696969":
                rgb = Color.rgb(105, 105, 105);
                break;
            case "#808080":
                rgb = Color.rgb(128, 128, 128);
                break;
            case "#A9A9A9":
                rgb = Color.rgb(169, 169, 169);
                break;
            case "#C0C0C0":
                rgb = Color.rgb(192, 192, 192);
                break;
            case "#000000":
                rgb = Color.rgb(0, 0, 0);
                break;
            case "#B0C4DE":
                rgb = Color.rgb(176, 196, 22);
                break;
            case "#00BFFF":
                rgb = Color.rgb(0, 191, 255);
                break;
            case "#1E90FF":
                rgb = Color.rgb(30, 144, 255);
                break;
            case "#4682B4":
                rgb = Color.rgb(70, 130, 180);
                break;
            case "#4169E1":
                rgb = Color.rgb(65, 105, 225);
                break;
            case "#6A5ACD":
                rgb = Color.rgb(106, 90, 205);
                break;
            case "#483D8B":
                rgb = Color.rgb(72, 61, 139);
                break;
            case "#0000FF":
                rgb = Color.rgb(0, 0, 255);
                break;
            case "#000080":
                rgb = Color.rgb(0, 0, 128);
                break;
            case "#66CDAA":
                rgb = Color.rgb(102, 205, 170);
                break;
            case "#3CB371":
                rgb = Color.rgb(60, 179, 113);
                break;
            case "#32CD32":
                rgb = Color.rgb(50, 205, 50);
                break;
            case "#9ACD32":
                rgb = Color.rgb(154, 205, 50);
                break;
            case "#6B8E23":
                rgb = Color.rgb(107, 142, 35);
                break;
            case "#228B22":
                rgb = Color.rgb(34, 139, 34);
                break;
            case "#008B8B":
                rgb = Color.rgb(0, 139, 139);
                break;
            case "#556B2F":
                rgb = Color.rgb(85, 107, 47);
                break;
            case "#808000":
                rgb = Color.rgb(128, 128, 0);
                break;
            case "#006400":
                rgb = Color.rgb(0, 100, 0);
                break;
            case "#BC8F8F":
                rgb = Color.rgb(188, 143, 143);
                break;
            case "#F4A460":
                rgb = Color.rgb(244, 164, 96);
                break;
            case "#CD853F":
                rgb = Color.rgb(205, 133, 63);
                break;
            case "#D2691E":
                rgb = Color.rgb(210, 105, 30);
                break;
            case "#8B4513":
                rgb = Color.rgb(139, 69, 19);
                break;
            case "#FF00FF":
                rgb = Color.rgb(255, 0, 255);
                break;
            case "#DA70D6":
                rgb = Color.rgb(218, 112, 214);
                break;
            case "#9400D3":
                rgb = Color.rgb(148, 0, 211);
                break;
            case "#8B008B":
                rgb = Color.rgb(139, 0, 139);
                break;
            case "#4B0082":
                rgb = Color.rgb(75, 0, 130);
                break;
            case "#FFC0CB":
                rgb = Color.rgb(255, 192, 203);
                break;
            case "#F08080":
                rgb = Color.rgb(240, 128, 128);
                break;
            case "#FF69B4":
                rgb = Color.rgb(255, 105, 180);
                break;
            case "#FF1493":
                rgb = Color.rgb(255, 20, 147);
                break;
            case "#FA8072":
                rgb = Color.rgb(250, 128, 114);
                break;
            case "#FF7F50":
                rgb = Color.rgb(255, 127, 80);
                break;
            case "#FF6347":
                rgb = Color.rgb(255, 99, 71);
                break;
            case "#FF0000":
                rgb = Color.rgb(255, 0, 0);
                break;
            case "#A52A2A":
                rgb = Color.rgb(165, 42, 42);
                break;
            case "#FFA500":
                rgb = Color.rgb(255, 165, 0);
                break;
            case "#FF8C00":
                rgb = Color.rgb(255, 140, 0);
                break;
            case "#FF4500":
                rgb = Color.rgb(255, 69, 0);
                break;
            case "#F0E68C":
                rgb = Color.rgb(240, 230, 140);
                break;
            case "#FFFF00":
                rgb = Color.rgb(255, 255, 0);
                break;
            case "#FFD700":
                rgb = Color.rgb(255, 215, 0);
                break;
            case "#FFFFFF":
                rgb = Color.rgb(255, 255, 255);
                break;
        }
        return rgb;
    }

    /**
     * Utilizado no listview personalizado
     *
     * @param cor
     * @return
     */
    public static int getCirculoCor(String cor) {
        int icon = 0;
        switch (cor) {
            case "#696969":
                icon = R.drawable.ic_circulo_01;
                break;
            case "#808080":
                icon = R.drawable.ic_circulo_02;
                break;
            case "#A9A9A9":
                icon = R.drawable.ic_circulo_03;
                break;
            case "#C0C0C0":
                icon = R.drawable.ic_circulo_04;
                break;
            case "#000000":
                icon = R.drawable.ic_circulo_05;
                break;
            case "#B0C4DE":
                icon = R.drawable.ic_circulo_06;
                break;
            case "#00BFFF":
                icon = R.drawable.ic_circulo_07;
                break;
            case "#1E90FF":
                icon = R.drawable.ic_circulo_08;
                break;
            case "#4682B4":
                icon = R.drawable.ic_circulo_09;
                break;
            case "#4169E1":
                icon = R.drawable.ic_circulo_10;
                break;
            case "#6A5ACD":
                icon = R.drawable.ic_circulo_11;
                break;
            case "#483D8B":
                icon = R.drawable.ic_circulo_12;
                break;
            case "#0000FF":
                icon = R.drawable.ic_circulo_13;
                break;
            case "#000080":
                icon = R.drawable.ic_circulo_14;
                break;
            case "#66CDAA":
                icon = R.drawable.ic_circulo_15;
                break;
            case "#3CB371":
                icon = R.drawable.ic_circulo_16;
                break;
            case "#32CD32":
                icon = R.drawable.ic_circulo_17;
                break;
            case "#9ACD32":
                icon = R.drawable.ic_circulo_18;
                break;
            case "#6B8E23":
                icon = R.drawable.ic_circulo_19;
                break;
            case "#228B22":
                icon = R.drawable.ic_circulo_20;
                break;
            case "#008B8B":
                icon = R.drawable.ic_circulo_21;
                break;
            case "#556B2F":
                icon = R.drawable.ic_circulo_22;
                break;
            case "#808000":
                icon = R.drawable.ic_circulo_23;
                break;
            case "#006400":
                icon = R.drawable.ic_circulo_24;
                break;
            case "#BC8F8F":
                icon = R.drawable.ic_circulo_25;
                break;
            case "#F4A460":
                icon = R.drawable.ic_circulo_26;
                break;
            case "#CD853F":
                icon = R.drawable.ic_circulo_27;
                break;
            case "#D2691E":
                icon = R.drawable.ic_circulo_28;
                break;
            case "#8B4513":
                icon = R.drawable.ic_circulo_29;
                break;
            case "#FF00FF":
                icon = R.drawable.ic_circulo_30;
                break;
            case "#DA70D6":
                icon = R.drawable.ic_circulo_31;
                break;
            case "#9400D3":
                icon = R.drawable.ic_circulo_32;
                break;
            case "#8B008B":
                icon = R.drawable.ic_circulo_33;
                break;
            case "#4B0082":
                icon = R.drawable.ic_circulo_34;
                break;
            case "#FFC0CB":
                icon = R.drawable.ic_circulo_35;
                break;
            case "#F08080":
                icon = R.drawable.ic_circulo_36;
                break;
            case "#FF69B4":
                icon = R.drawable.ic_circulo_37;
                break;
            case "#FF1493":
                icon = R.drawable.ic_circulo_38;
                break;
            case "#FA8072":
                icon = R.drawable.ic_circulo_39;
                break;
            case "#FF7F50":
                icon = R.drawable.ic_circulo_40;
                break;
            case "#FF6347":
                icon = R.drawable.ic_circulo_41;
                break;
            case "#FF0000":
                icon = R.drawable.ic_circulo_42;
                break;
            case "#A52A2A":
                icon = R.drawable.ic_circulo_43;
                break;
            case "#FFA500":
                icon = R.drawable.ic_circulo_44;
                break;
            case "#FF8C00":
                icon = R.drawable.ic_circulo_45;
                break;
            case "#FF4500":
                icon = R.drawable.ic_circulo_46;
                break;
            case "#F0E68C":
                icon = R.drawable.ic_circulo_47;
                break;
            case "#FFFF00":
                icon = R.drawable.ic_circulo_48;
                break;
            case "#FFD700":
                icon = R.drawable.ic_circulo_49;
                break;
            case "#FFFFFF":
                icon = R.drawable.ic_circulo_50;
                break;
        }
        return icon;
    }

    /**
     * Oculta o teclado após o campo perder o foco.
     *
     * @param context
     * @param editText
     */
    public static void hideKeyboard(Context context, View editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * verificar se a data for null, ocorreu problema na conversao
     *
     * @param dt
     * @return
     */
    public static Date converteStringToDate(String dt) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = sdf.parse(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    public static double getValorParcelaDuasCasas(double valor, int qtdParcela) {
        double valor_parcela = valor / qtdParcela;
        BigDecimal valorDuasCasas = new BigDecimal(valor_parcela).setScale(2, RoundingMode.HALF_DOWN);
        return valorDuasCasas.doubleValue();
    }

    public static double getValorMoedaDuasCasas(double valor) {
        BigDecimal valorDuasCasas = new BigDecimal(valor).setScale(2, RoundingMode.HALF_DOWN);
        return valorDuasCasas.doubleValue();
    }

    /**
     * Retorna a data do próximo vencimento, o valor da parcela precisa ser fornecido
     * Ideal é utilizar esse método dentro de um for e passar i
     *
     * @param parcela
     * @return
     */
    public static String getDtVencimentoParcelas(String ddMMyyyy, int parcela) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(Util.converteStringToDate(ddMMyyyy));

        if (parcela == 1) {
            return Util.dateAtualToDateBD(ddMMyyyy);
        } else {
            int dias = 30 * (parcela - 1);
            calendar.add(calendar.DAY_OF_MONTH, dias);
            return Util.dateAtualToDateBD(Util.dateToString(calendar.getTime()));
        }
    }

    public static String getDtVencimentoParcelas(int dia, int mes, int ano, int parcela) {
        Calendar calendar = Calendar.getInstance();
        String dtVencimento = dia + "/" + mes + "/" + ano;
        calendar.setTime(Util.converteStringToDate(dtVencimento));

        if (parcela == 1) {
            return Util.dateAtualToDateBD(Util.dateToString(calendar.getTime()));
        } else {
            calendar.add(Calendar.MONTH, parcela - 1);
            return Util.dateAtualToDateBD(Util.dateToString(calendar.getTime()));
        }

    }

    public static String criptografar(String s) {
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
    }

    public static String descriptografar(String s) {
        return new String(Base64.decode(s, Base64.DEFAULT));
    }
}