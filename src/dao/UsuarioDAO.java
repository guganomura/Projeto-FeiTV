package dao;

import model.Usuario;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

// Responsável por todas as operações de banco relacionadas ao usuário.
// Nunca salva a senha em texto puro — sempre passa pelo método hash() antes.
public class UsuarioDAO {

    // Insere um novo usuário no banco.
    // A senha é criptografada antes de salvar — nunca é guardada como texto puro.
    public void cadastrar(String nome, String email, String senha) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, email, senha) VALUES (?, ?, ?)";

        // PreparedStatement evita SQL Injection — os valores são passados separadamente
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, email);
            ps.setString(3, hash(senha)); // criptografa a senha antes de salvar
            ps.executeUpdate();
        }
    }

    // Verifica as credenciais do usuário no banco.
    // Faz o hash da senha digitada e compara com o que está salvo.
    // Retorna o objeto Usuario se tudo certo, ou null se errar email/senha.
    public Usuario login(String email, String senha) throws SQLException {
        String sql = "SELECT id, nome, email, senha FROM usuarios WHERE email = ? AND senha = ?";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, hash(senha)); // compara o hash digitado com o hash no banco
            ResultSet rs = ps.executeQuery();

            // Se encontrou linha, monta e retorna o objeto Usuario
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("senha")
                );
            }

            // Nenhuma linha encontrada — credenciais inválidas
            return null;
        }
    }

    // Converte qualquer texto em SHA-256 usando o MessageDigest do próprio Java.
    // O resultado é uma string de 64 caracteres hexadecimais.
    private String hash(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(texto.getBytes());

            // Converte cada byte para dois dígitos hexadecimais
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 não disponível", e);
        }
    }
}
