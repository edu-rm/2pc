package ifc.sisdi.tpc.replica2.models;

public class Action {
    private int id;
    private String operacao;
    private int conta;
    private double valor;

    public Action(int id, String operacao, int conta, double valor) {
        this.id = id;
        this.operacao = operacao;
        this.conta = conta;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public String getOperacao() {
        return operacao;
    }

    public int getConta() {
        return conta;
    }

    public double getValor() {
        return valor;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public void setConta(int conta) {
        this.conta = conta;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }


}