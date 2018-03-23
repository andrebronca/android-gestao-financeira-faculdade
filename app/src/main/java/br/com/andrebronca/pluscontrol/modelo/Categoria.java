package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 02/10/16.
 */

public class Categoria implements Serializable {
    private int idCategoria;    //id_categoria
    private String dsCategoria;
    private String tpCategoria;
    private String stAtivo;
    private String cdCor;
    private String dtCadastro;
    private String dtAlterado;

    public Categoria() {
    }

    public Categoria(int idCategoria, String dsCategoria, String tpCategoria, String stAtivo, String cdCor, String dtCadastro, String dtAlterado) {
        this.idCategoria = idCategoria;
        this.dsCategoria = dsCategoria;
        this.tpCategoria = tpCategoria;
        this.stAtivo = stAtivo;
        this.cdCor = cdCor;
        this.dtCadastro = dtCadastro;
        this.dtAlterado = dtAlterado;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getDsCategoria() {
        return dsCategoria;
    }

    public void setDsCategoria(String dsCategoria) {
        this.dsCategoria = dsCategoria;
    }

    public String getTpCategoria() {
        return tpCategoria;
    }

    public String getTpCategoriaLong() {
        if (tpCategoria.equals("D")) {
            return "DESPESA";
        } else if (tpCategoria.equals("R")) {
            return "RECEITA";
        } else {
            return "";
        }
    }

    public void setTpCategoria(String tpCategoria) {
        this.tpCategoria = tpCategoria;
    }

    public String getStAtivo() {
        return stAtivo;
    }

    public boolean getStAtivoBool() {
        if (stAtivo.equals("S")) {
            return true;
        } else if (stAtivo.equals("N")) {
            return false;
        } else {
            return false;
        }
    }

    public String getStAtivoLong() {
        if (stAtivo.equals("S")) {
            return "SIM";
        } else if (stAtivo.equals("N")) {
            return "NÃO";
        } else {
            return "";
        }
    }

    public void setStAtivo(String stAtivo) {
        this.stAtivo = stAtivo;
    }

    public String getCdCor() {
        return cdCor;
    }

    public void setCdCor(String cdCor) {
        this.cdCor = cdCor;
    }

    public String getDtCadastro() {
        if (dtCadastro != null) {
            return dtCadastro;
        } else {
            return "--";
        }
    }

    /**
     * recebe 2010-01-15 e retorna 15/01/2010
     * @return
     */
    public String getDtCadastroFormat() {
        return Util.dateToStringFormat(getDtCadastro());
    }

    public String getDtAlteradoFormat() {
        String data = Util.dateToStringFormat(getDtAlterado());
        if (data != null) {
            return data;
        } else {
            return "--";
        }
    }

    public void setDtCadastro(String dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String setDtCadastro(Date dtCadastro) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (dtCadastro != null) {
            return sdf.format(dtCadastro);
        } else {
            return null;
        }
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
        return dsCategoria;
    }
}