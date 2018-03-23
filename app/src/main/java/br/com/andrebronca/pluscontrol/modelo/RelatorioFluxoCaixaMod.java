package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

/**
 * Created by andrebronca on 29/10/16.
 */

public class RelatorioFluxoCaixaMod implements Serializable {
    private String dtMovimento;
    private String stTipo;
    private String dsHistorico;
    private double vlMovimento;
    private String stDirecao;

    public RelatorioFluxoCaixaMod() {
    }

    public String getDtMovimento() {
        return dtMovimento;
    }

    public void setDtMovimento(String dtMovimento) {
        this.dtMovimento = dtMovimento;
    }

    public String getStTipo() {
        return stTipo;
    }

    public String getStTipoLongo() {
        String str = "";
        switch (stTipo) {
            case "R":
                str = "Recebimento";
                break;
            case "P":
                str = "Pagamento";
                break;
            case "T":
                str = "Transferência";
                break;
            case "S":
                str = "Saque";
                break;
        }
        return str;
    }

    public void setStTipo(String stTipo) {
        this.stTipo = stTipo;
    }

    public String getDsHistorico() {
        return dsHistorico;
    }

    public void setDsHistorico(String dsHistorico) {
        this.dsHistorico = dsHistorico;
    }

    public double getVlMovimento() {
        return vlMovimento;
    }

    public void setVlMovimento(double vlMovimento) {
        this.vlMovimento = vlMovimento;
    }

    public String getStDirecao() {
        return stDirecao;
    }

    public void setStDirecao(String stDirecao) {
        this.stDirecao = stDirecao;
    }
}