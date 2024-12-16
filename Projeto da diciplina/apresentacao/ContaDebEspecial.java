package apresentacao;

public class ContaDebEspecial extends ContaEspecial {

    public ContaDebEspecial(String numero, double saldo, double limite) {
        super(numero, saldo, limite);
    }

    @Override
    public void debitar(double valor) {
        if (saldo + limite >= valor) {
            saldo -= valor;
        }
    }
}
