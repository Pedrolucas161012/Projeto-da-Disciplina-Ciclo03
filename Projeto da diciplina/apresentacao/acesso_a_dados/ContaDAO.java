package acesso_a_dados;

import apresentacao.Conta;
import apresentacao.ContaDebEspecial;
import apresentacao.ContaNormal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContaDAO {

    private Connection conexao;

    public ContaDAO() {
        try {
            conexao = DriverManager.getConnection("jdbc:sqlite:banco.db");
            criaTabelaSeNaoExistir();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public void criaConta(Conta conta) {
        String sql = "INSERT INTO contas (numero, saldo, limite) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, conta.getNumero());
            stmt.setDouble(2, conta.getSaldo());
            if (conta instanceof ContaDebEspecial) {
                stmt.setDouble(3, ((ContaDebEspecial) conta).getLimite());
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

    public void atualizaSaldo(String numero, double saldo) {
        String sql = "UPDATE contas SET saldo = ? WHERE numero = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDouble(1, saldo);
            stmt.setString(2, numero);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Conta buscaConta(String numero) {
        String sql = "SELECT numero, saldo, limite FROM contas WHERE numero = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, numero);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double saldo = rs.getDouble("saldo");
                    double limite = rs.getDouble("limite");
                    if (limite > 0) {
                        return new ContaDebEspecial(numero, saldo, limite);
                    } else {
                        return new ContaNormal(numero, saldo);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Conta> listaContas() {
        List<Conta> contas = new ArrayList<>();
        String sql = "SELECT numero, saldo, limite FROM contas";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String numero = rs.getString("numero");
                double saldo = rs.getDouble("saldo");
                double limite = rs.getDouble("limite");
                if (limite > 0) {
                    contas.add(new ContaDebEspecial(numero, saldo, limite));
                } else {
                    contas.add(new ContaNormal(numero, saldo));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return contas;
    }
}

