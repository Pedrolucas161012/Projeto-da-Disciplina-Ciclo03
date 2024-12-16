package negocio;

import acesso_a_dados.ContaDAO;
import apresentacao.Conta;

import java.util.List;

public class BancoNegocio {

    private final ContaDAO contaDAO;

    public BancoNegocio() {
        this.contaDAO = new ContaDAO();
    }

    public void criaConta(Conta conta) {
        if (contaDAO.buscaConta(conta.getNumero()) != null) {
            throw new IllegalArgumentException("Conta já existe!");
        }
        contaDAO.criaConta(conta);
    }

    public void removeConta(String numero) {
        if (contaDAO.buscaConta(numero) == null) {
            throw new IllegalArgumentException("Conta não encontrada!");
        }
        contaDAO.removeConta(numero);
    }

    public void creditaConta(String numero, double valor) {
        Conta conta = contaDAO.buscaConta(numero);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada!");
        }
        conta.creditar(valor);
        contaDAO.atualizaSaldo(numero, conta.getSaldo());
    }

    public void debitaConta(String numero, double valor) {
        Conta conta = contaDAO.buscaConta(numero);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada!");
        }
        conta.debitar(valor);
        contaDAO.atualizaSaldo(numero, conta.getSaldo());
    }

    public void transfereConta(String origem, String destino, double valor) {
        Conta contaOrigem = contaDAO.buscaConta(origem);
        Conta contaDestino = contaDAO.buscaConta(destino);

        if (contaOrigem == null || contaDestino == null) {
            throw new IllegalArgumentException("Conta de origem ou destino não encontrada!");
        }

        contaOrigem.debitar(valor);
        contaDestino.creditar(valor);

        contaDAO.atualizaSaldo(origem, contaOrigem.getSaldo());
        contaDAO.atualizaSaldo(destino, contaDestino.getSaldo());
    }

    public List<Conta> listaContas() {
        return contaDAO.listaContas();
    }
}
