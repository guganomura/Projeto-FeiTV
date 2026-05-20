package dao;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Responsável por buscar vídeos no banco e gerenciar as curtidas.
public class VideoDAO {

    // Busca vídeos pelo título — funciona mesmo se o usuário digitar em letras minúsculas.
    // O % antes e depois do termo faz busca por qualquer trecho do título (LIKE %termo%).
    // Faz JOIN com filmes, series e curtidas para trazer tudo de uma vez só.
    public List<Video> buscarPorNome(String termo) throws SQLException {
        String sql =
            "SELECT v.id, v.titulo, v.genero, v.ano_lancamento, v.tipo," +
            "       COUNT(c.id_usuario) AS num_curtidas," +  // conta as curtidas de cada vídeo
            "       f.duracao, f.diretor," +                 // dados do filme (null se for série)
            "       s.total_temporadas, s.total_episodios, s.status" + // dados da série (null se for filme)
            " FROM videos v" +
            " LEFT JOIN curtidas c ON v.id = c.id_video" +  // LEFT JOIN: traz mesmo sem curtidas
            " LEFT JOIN filmes   f ON v.id = f.id" +        // LEFT JOIN: traz mesmo se for série
            " LEFT JOIN series   s ON v.id = s.id" +        // LEFT JOIN: traz mesmo se for filme
            " WHERE LOWER(v.titulo) LIKE LOWER(?)" +        // busca case-insensitive
            " GROUP BY v.id, f.duracao, f.diretor, s.total_temporadas, s.total_episodios, s.status" +
            " ORDER BY v.titulo";

        List<Video> lista = new ArrayList<>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + termo + "%"); // envolve o termo com % para busca parcial
            ResultSet rs = ps.executeQuery();
            while (rs.next()) lista.add(mapear(rs)); // converte cada linha em objeto Video
        }
        return lista;
    }

    // Retorna todos os vídeos — usado na tela de gerenciar lista para mostrar opções
    public List<Video> listarTodos() throws SQLException {
        return buscarPorNome(""); // termo vazio = traz tudo
    }

    // Busca um vídeo específico pelo id — usado para atualizar os detalhes na tela
    public Video buscarPorId(int id) throws SQLException {
        String sql =
            "SELECT v.id, v.titulo, v.genero, v.ano_lancamento, v.tipo," +
            "       COUNT(c.id_usuario) AS num_curtidas," +
            "       f.duracao, f.diretor," +
            "       s.total_temporadas, s.total_episodios, s.status" +
            " FROM videos v" +
            " LEFT JOIN curtidas c ON v.id = c.id_video" +
            " LEFT JOIN filmes   f ON v.id = f.id" +
            " LEFT JOIN series   s ON v.id = s.id" +
            " WHERE v.id = ?" +
            " GROUP BY v.id, f.duracao, f.diretor, s.total_temporadas, s.total_episodios, s.status";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        }
    }

    // Verifica se o usuário já curtiu esse vídeo antes de tentar curtir de novo
    public boolean usuarioCurtiu(int idUsuario, int idVideo) throws SQLException {
        String sql = "SELECT 1 FROM curtidas WHERE id_usuario = ? AND id_video = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idVideo);
            return ps.executeQuery().next(); // se retornou linha, significa que já curtiu
        }
    }

    // Insere uma curtida — ON CONFLICT DO NOTHING garante que não dá erro se já existir
    public void curtir(int idUsuario, int idVideo) throws SQLException {
        String sql = "INSERT INTO curtidas (id_usuario, id_video) VALUES (?,?) ON CONFLICT DO NOTHING";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idVideo);
            ps.executeUpdate();
        }
    }

    // Remove a curtida do usuário para esse vídeo
    public void descurtir(int idUsuario, int idVideo) throws SQLException {
        String sql = "DELETE FROM curtidas WHERE id_usuario = ? AND id_video = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idVideo);
            ps.executeUpdate();
        }
    }

    // Conta quantas curtidas um vídeo tem no total (de todos os usuários)
    public int contarCurtidas(int idVideo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM curtidas WHERE id_video = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVideo);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Converte uma linha do ResultSet em um objeto Filme ou Serie,
    // dependendo do campo "tipo" que vem do banco.
    private Video mapear(ResultSet rs) throws SQLException {
        int    id       = rs.getInt("id");
        String titulo   = rs.getString("titulo");
        String genero   = rs.getString("genero");
        int    ano      = rs.getInt("ano_lancamento");
        int    curtidas = rs.getInt("num_curtidas");
        String tipo     = rs.getString("tipo"); // "FILME" ou "SERIE"

        if ("FILME".equals(tipo)) {
            // Cria um Filme com os dados específicos de filme
            return new Filme(id, titulo, genero, ano, curtidas,
                             rs.getInt("duracao"),
                             rs.getString("diretor"));
        } else {
            // Cria uma Serie com os dados específicos de série
            return new Serie(id, titulo, genero, ano, curtidas,
                             rs.getInt("total_temporadas"),
                             rs.getInt("total_episodios"),
                             rs.getString("status"));
        }
    }
}
