package ifc.sisdi.tpc.replica2.models;

public class Account {
    private int number;
    private double balance;

    public Account(int number, double balance){
        this.number = number;
        this.balance = balance;
    }

    public int getNumber() {
        return number;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
