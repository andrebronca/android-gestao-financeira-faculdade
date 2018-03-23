package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

/**
 * Created by andrebronca on 22/10/16.
 */

public class RelatorioContasPagarReceberMod implements Serializable {
    private int idtitulo;
    private int idParcela;
    private String dsTitulo;
    private String dtVencimento;
    private double vlSaldo;
    private String dsCategoria;
    private String nmPessoa;

    public RelatorioContasPagarReceberMod() {
    }

    public int getIdtitulo() {
        return idtitulo;
    }

    public void setIdtitulo(int idtitulo) {
        this.idtitulo = idtitulo;
    }

    public int getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(int idParcela) {
        this.idParcela = idParcela;
    }

    public String getDsTitulo() {
        return dsTitulo;
    }

    public void setDsTitulo(String dsTitulo) {
        this.dsTitulo = dsTitulo;
    }

    public String getDtVencimento() {
        return dtVencimento;
    }

    public String getDiaDtVencimento() {
        return "00";
    }

    public void setDtVencimento(String dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public double getVlSaldo() {
        return vlSaldo;
    }

    public void setVlSaldo(double vlSaldo) {
        this.vlSaldo = vlSaldo;
    }

    public String getDsCategoria() {
        return dsCategoria;
    }

    public void setDsCategoria(String dsCategoria) {
        this.dsCategoria = dsCategoria;
    }

    public String getNmPessoa() {
        return nmPessoa;
    }

    public void setNmPessoa(String nmPessoa) {
        this.nmPessoa = nmPessoa;
    }
}