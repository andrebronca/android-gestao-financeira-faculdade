package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 04/10/16.
 */

public class Pessoa implements Serializable {
    private int idPessoa; //id_pessoa
    private String nmPessoa;
    private String tpPessoa;
    private String dsEmail;
    private String stAtivo;
    private String dtCadastro;
    private String dtAlterado;

    public Pessoa() {
    }

    public int getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(int idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getNmPessoa() {
        return nmPessoa;
    }

    public void setNmPessoa(String nmPessoa) {
        this.nmPessoa = nmPessoa;
    }

    public String getTpPessoa() {
        return tpPessoa;
    }

    public void setTpPessoa(String tpPessoa) {
        this.tpPessoa = tpPessoa;
    }

    public String getDsEmail() {
        return dsEmail;
    }

    public void setDsEmail(String dsEmail) {
        this.dsEmail = dsEmail;
    }

    public String getStAtivo() {
        return stAtivo;
    }

    public void setStAtivo(String stAtivo) {
        this.stAtivo = stAtivo;
    }

    public String getDtCadastro() {
        if (dtCadastro != null) {
            return dtCadastro;
        } else {
            return "--";
        }
    }

    public void setDtCadastro(String dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getDtAlterado() {
        if (dtAlterado != null) {
            return dtAlterado;
        } else {
            return "--";
        }
    }

    public void setDtAlterado(String dtAlterado) {
        this.dtAlterado = dtAlterado;
    }

    public String toString() {
        return nmPessoa;
    }

    /**
     * Retorna a descrição conforme o tipo do cliente
     * @return CLIENTE / FORNECEDOR / AMBOS
     */
    public String getTpPessoaLongo() {
        if (tpPessoa.equals("C")) {
            return "CLIENTE";
        } else if (tpPessoa.equals("F")) {
            return "FORNECEDOR";
        } else if (tpPessoa.equals("A")) {
            return "CLIENTE & FORNECEDOR";
        } else {
            return "";
        }
    }

    /**
     * Retorna true ou false conforme o status( S / N )
     * @return
     */
    public boolean getStAtivoBool() {
        if (stAtivo.equals("S")) {
            return true;
        } else if (stAtivo.equals("N")) {
            return false;
        } else {
            return false;
        }
    }

    /**
     * Retorna a descrição SIM ou NÃO conforme o status ( S / N )
     * @return
     */
    public String getStAtivoLongo() {
        if (stAtivo.equals("S")) {
            return "SIM";
        } else if (stAtivo.equals("N")) {
            return "NÃO";
        } else {
            return "";
        }
    }

    /**
     * recebe 2010-01-15 e retorna 15/01/2010
     * @return -- caso a data for null
     */
    public String getDtCadastroFormat() {
        String data = Util.dateToStringFormat(getDtCadastro());
        if (data != null) {
            return data;
        } else {
            return "--";
        }
    }

    /**
     * recebe 2010-01-15 e retorna 15/01/2010
     * @return -- caso a data for null
     */
    public String getDtAlteradoFormat() {
        String data = Util.dateToStringFormat(getDtAlterado());
        if (data != null) {
            return data;
        } else {
            return "--";
        }
    }
}