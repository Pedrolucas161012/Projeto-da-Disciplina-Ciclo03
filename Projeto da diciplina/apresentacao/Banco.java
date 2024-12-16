package apresentacao;

import java.sql.*;
import java.util.ArrayList;

public class Banco {

    private Connection conexao;

    // Construtor do Banco
    public Banco() {
        try {
            conexao = DriverManager.getConnection("jdbc:sqlite:banco.db");
            criaTabelaSeNaoExistir();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para criar a tabela no banco de dados se ela não existir
    private void criaTabelaSeNaoExistir() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS contas (
                numero TEXT PRIMARY KEY,
                saldo REAL,
                limite REAL
            );
        """;
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(sql);
        }
    }

    // Métodos para gerenciamento de contas
    public void criaConta(Conta c) {
        String sql = "INSERT INTO contas (numero, saldo, limite) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, c.getNumero());
            stmt.setDouble(2, c.getSaldo());
            if (c instanceof ContaEspecial) {
                stmt.setDouble(3, ((ContaEspecial) c).getLimite());
            } else {
                stmt.setDouble(3, 0); // ContaNormal não tem limite
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeConta(String numero) {
        String sql = "DELETE FROM contas WHERE numero = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, numero);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void creditaConta(String numero, double valor) {
        String sql = "UPDATE contas SET saldo = saldo + ? WHERE numero = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDouble(1, valor);
            stmt.setString(2, numero);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void debitaConta(String numero, double valor) {
        String sql = "UPDATE contas SET saldo = saldo - ? WHERE numero = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDouble(1, valor);
            stmt.setString(2, numero);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void transfereConta(String origem, String destino, double valor) {
        try {
            conexao.setAutoCommit(false);
            debitaConta(origem, valor);
            creditaConta(destino, valor);
            conexao.commit();
        } catch (SQLException e) {
            try {
                conexao.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conexao.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void listaContas() {
        String sql = "SELECT numero, saldo, limite FROM contas";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String numero = rs.getString("numero");
                double saldo = rs.getDouble("saldo");
                double limite = rs.getDouble("limite");
                System.out.printf("Conta: %s | Saldo: %.2f | Limite: %.2f\n", numero, saldo, limite);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Banco banco = new Banco();

        ContaNormal c1 = new ContaNormal("1654-3", 500);
        ContaDebEspecial c2 = new ContaDebEspecial("4067-6", 2500, 1000.67);
        ContaDebEspecial c3 = new ContaDebEspecial("6578-9", 2500, 5050);

        banco.criaConta(c1);
        banco.criaConta(c2);
        banco.criaConta(c3);

        banco.listaContas();

        banco.creditaConta("6578-9", 1000);
        banco.listaContas();

        banco.debitaConta("6578-9", 500);
        banco.listaContas();

        banco.transfereConta("6578-9", "1654-3", 500);
        banco.listaContas();
    }
}
