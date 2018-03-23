package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

/**
 * Created by andrebronca on 08/10/16.
 */

public class MovimentoConta implements Serializable {
    private int idMovConta;
    private Conta conta;
    private int idConta;
    private Titulo titulo;
    private int idTitulo;
    private int idParcela;
    private double vlMovimento;
    private String dtMovimento;
    private String stDirecao;
    private int idTransferencia;
    private String dsHistorico;
    private String stEstorno;
    private String stTipo;

    public MovimentoConta() {
    }

    public int getIdMovConta() {
        return idMovConta;
    }

    public void setIdMovConta(int idMovConta) {
        this.idMovConta = idMovConta;
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public int getIdTitulo() {
        return idTitulo;
    }

    public void setIdTitulo(int idTitulo) {
        this.idTitulo = idTitulo;
    }

    public Titulo getTitulo() {
        return titulo;
    }

    public void setTitulo(Titulo titulo) {
        this.titulo = titulo;
    }

    public double getVlMovimento() {
        return vlMovimento;
    }

    public void setVlMovimento(double vlMovimento) {
        this.vlMovimento = vlMovimento;
    }

    public String getDtMovimento() {
        return dtMovimento;
    }

    public void setDtMovimento(String dtMovimento) {
        this.dtMovimento = dtMovimento;
    }

    public String getStDirecao() {
        return stDirecao;
    }

    public void setStDirecao(String stDirecao) {
        this.stDirecao = stDirecao;
    }

    public int getIdTransferencia() {
        return idTransferencia;
    }

    public void setIdTransferencia(int idTransferencia) {
        this.idTransferencia = idTransferencia;
    }

    public String getDsHistorico() {
        return dsHistorico;
    }

    public int getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(int idParcela) {
        this.idParcela = idParcela;
    }

    public String getStTipo() {
        return stTipo;
    }

    public void setStTipo(String stTipo) {
        this.stTipo = stTipo;
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

    public String getStDirecaoLongo() {
        String str = "";
        switch (stDirecao) {
            case "E":
                str = "Entrada";
                break;
            case "S":
                str = "Saída";
                break;
        }
        return str;
    }

    public void setDsHistorico(String dsHistorico) {
        this.dsHistorico = dsHistorico != null ? dsHistorico : "";
    }

    public String getStEstorno() {
        return stEstorno;
    }

    public void setStEstorno(String stEstorno) {
        this.stEstorno = stEstorno;
    }

    @Override
    public String toString() {
        return dsHistorico + " - " + dtMovimento;
    }
}