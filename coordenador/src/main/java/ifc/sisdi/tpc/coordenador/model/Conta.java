package ifc.sisdi.tpc.coordenador.model;

public class Conta {
    private int numero;
    private double saldo;

    public Conta(int number, double balance){
        this.numero = number;
        this.saldo = balance;
    }

    public int getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double balance) {
        this.saldo = balance;
    }

    public void setNumero(int number) {
        this.numero = number;
    }
}
