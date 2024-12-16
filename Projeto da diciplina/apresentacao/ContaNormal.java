package apresentacao;

public class ContaNormal extends Conta {

    public ContaNormal(String numero, double saldo) {
        super(numero, saldo);
    }

    @Override
    public void creditar(double valor) {
        saldo += valor;
    }

    @Override
    public void debitar(double valor) {
        if (saldo >= valor) {
            saldo -= valor;
        }
    }
}
