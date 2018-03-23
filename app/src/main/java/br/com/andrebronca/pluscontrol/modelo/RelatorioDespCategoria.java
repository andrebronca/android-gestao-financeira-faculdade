package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

/**
 * Created by andrebronca on 20/10/16.
 */

public class RelatorioDespCategoria implements Serializable {
    private String relDsCategoria;  //caso dê algum problema, sei que se trata de atributo de relatório
    private String relCdCor;
    private double relVlTotal;

    public RelatorioDespCategoria() {
    }

    public String getRelDsCategoria() {
        return relDsCategoria;
    }

    public void setRelDsCategoria(String relDsCategoria) {
        this.relDsCategoria = relDsCategoria;
    }

    public String getRelCdCor() {
        return relCdCor;
    }

    public void setRelCdCor(String relCdCor) {
        this.relCdCor = relCdCor;
    }

    public double getRelVlTotal() {
        return relVlTotal;
    }

    public void setRelVlTotal(double relVlTotal) {
        this.relVlTotal = relVlTotal;
    }
}