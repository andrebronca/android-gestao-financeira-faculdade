package br.com.andrebronca.pluscontrol.modelo;

import java.io.Serializable;

import br.com.andrebronca.pluscontrol.utilitarios.Util;

/**
 * Created by andrebronca on 04/10/16.
 */

public class Titulo implements Serializable {
    private int idTitulo;
    private int idParcela;  //parcela incrementada
    private String dsTitulo;
    private String tpNatureza;
    private String dtEmissao;
    private String dtVencimento;
    private double vlTitulo;
    private double vlParcela;
    private double vlSaldo;
    private String dtBaixa;
    private int qtParcela;  //quantidade de parcela
    private String stRelatorio;
    private String stMovimento;
    private String dtCadastro;
    private String dtAlterado;
    private Categoria categoria;
    private Pessoa pessoa;
    private int idCategoria;
    private int idPessoa;

    public Titulo() {
    }

    public int getIdTitulo() {
        return idTitulo;
    }

    public void setIdTitulo(int idTitulo) {
        this.idTitulo = idTitulo;
    }

    public String getDsTitulo() {
        return dsTitulo == null ? "--" : dsTitulo;
    }

    public void setDsTitulo(String dsTitulo) {
        this.dsTitulo = dsTitulo;
    }

    public String getTpNatureza() {
        return tpNatureza;
    }

    public void setTpNatureza(String tpNatureza) {
        this.tpNatureza = tpNatureza;
    }

    public String getDtEmissao() {
        return dtEmissao;
    }

    public void setDtEmissao(String dtEmissao) {
        this.dtEmissao = dtEmissao;
    }

    public String getDtVencimento() {
        return dtVencimento;
    }

    /**
     * Utilizar o padrão: yyyy-MM-dd para gravar no banco de dados
     * @param dtVencimento
     */
    public void setDtVencimento(String dtVencimento) {
        this.dtVencimento = dtVencimento;
    }

    public double getVlTitulo() {
        return vlTitulo;
    }

    public void setVlTitulo(double vlTitulo) {
        this.vlTitulo = vlTitulo;
    }

    public double getVlParcela() {
        return vlParcela;
    }

    public void setVlParcela(double vlParcela) {
        this.vlParcela = vlParcela;
    }

    public double getVlSaldo() {
        return vlSaldo;
    }

    public void setVlSaldo(double vlSaldo) {
        this.vlSaldo = vlSaldo;
    }

    public String getDtBaixa() {
        return dtBaixa == null ? "--" : dtBaixa;
    }

    public void setDtBaixa(String dtBaixa) {
        this.dtBaixa = dtBaixa;
    }

    public int getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(int idParcela) {
        this.idParcela = idParcela;
    }

    public int getQtParcela() {
        return qtParcela;
    }

    public void setQtParcela(int qtParcela) {
        this.qtParcela = qtParcela;
    }

    public String getStRelatorio() {
        return stRelatorio;
    }

    public boolean getStRelatorioBool() {
        return stRelatorio.equals("S") ? true : false;
    }

    public void setStRelatorio(String stRelatorio) {
        this.stRelatorio = stRelatorio;
    }

    public String getStMovimento() {
        return stMovimento;
    }

    public void setStMovimento(String stMovimento) {
        this.stMovimento = stMovimento;
    }

    public String getDtCadastro() {
        return dtCadastro;
    }

    public void setDtCadastro(String dtCadastro) {
        this.dtCadastro = dtCadastro;
    }

    public String getDtAlterado() {
        return dtAlterado == null ? "--" : dtAlterado;
    }

    public void setDtAlterado(String dtAlterado) {
        this.dtAlterado = dtAlterado;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(int idPessoa) {
        this.idPessoa = idPessoa;
    }

    @Override
    public String toString() {
        return dsTitulo + ' ' + tpNatureza + ' ' +
                Util.dateToStringFormat(dtVencimento) + ' ' +
                String.format("R$%.2f", vlTitulo) + ' ' +
                String.format("R$%.2f", vlSaldo);
    }

    /**
     * Retorna a descrição do tipo de natureza do título
     * @return
     */
    public String getTpNaturezaLongo() {
        if (tpNatureza.equals("R")) {
            return "RECEITA";
        } else if (tpNatureza.equals("D")) {
            return "DESPESA";
        } else {
            return "";
        }
    }

    public String getStRelatorioLongo() {
        if (stRelatorio.equals("S")) {
            return "SIM";
        } else if (stRelatorio.equals("N")) {
            return "NÃO";
        } else {
            return "";
        }
    }

}