package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

/**
 * Created by andrebronca on 08/10/16.
 */

public class Conta implements Serializable {
    private int idConta;
    private String dsConta;
    private double vlSaldo;
    private String cdCor;
    private String dtCadastro;
    private String dtAlterado;

    public Conta() {
    }

    public int getIdConta() {
        return idConta;
    }

    public void setIdConta(int idConta) {
        this.idConta = idConta;
    }

    public String getDsConta() {
        return dsConta;
    }

    public void setDsConta(String dsConta) {
        this.dsConta = dsConta;
    }

    public double getVlSaldo() {
        return vlSaldo;
    }

    public void setVlSaldo(double vlSaldo) {
        this.vlSaldo = vlSaldo;
    }

    public String getCdCor() {
        return cdCor;
    }

    public void setCdCor(String cdCor) {
        this.cdCor = cdCor;
    }

    public String getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(String dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getDtAlterado() {
        if (dtAlterado != null) {
            return dtAlterado;
        } else {
            return "";
        }
    }

    public void setDtAlterado(String dtAlterado) {
        this.dtAlterado = dtAlterado;
    }

    @Override
    public String toString() {
        return dsConta;
    }
}