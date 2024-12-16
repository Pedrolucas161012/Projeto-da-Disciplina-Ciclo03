package apresentacao;

public abstract class ContaEspecial extends Conta {
    protected double limite;

    public ContaEspecial(String numero, double saldo, double limite) {
        super(numero, saldo);
        this.limite = limite;
    }

    public double getLimite() {
        return limite;
    }

    public void setLimite(double limite) {
        this.limite = limite;
    }

    @Override
    public void creditar(double valor) {
        saldo += valor;
    }
}
