package apresentacao;

public abstract class Conta {
    protected String numero;
    protected double saldo;

    public Conta(String numero, double saldo) {
        this.numero = numero;
        this.saldo = saldo;
    }

    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public abstract void creditar(double valor);

    public abstract void debitar(double valor);
}
