package dao;

import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Responsável por todas as operações de banco relacionadas às listas de reprodução.
public class ListaReproducaoDAO {

    // Retorna todas as listas do usuário ordenadas por nome.
    // Não carrega os vídeos de cada lista aqui — isso é feito separadamente no carregarVideos().
    public List<ListaReproducao> listarPorUsuario(Usuario usuario) throws SQLException {
        String sql = "SELECT id, nome FROM listas_reproducao WHERE id_usuario = ? ORDER BY nome";
        List<ListaReproducao> listas = new ArrayList<>();
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuario.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listas.add(new ListaReproducao(rs.getInt("id"), rs.getString("nome"), usuario));
            }
        }
        return listas;
    }

    // Cria uma nova lista no banco e retorna o objeto com o id gerado automaticamente.
    // RETURNING id faz o PostgreSQL devolver o id criado pelo SERIAL.
    public ListaReproducao criar(String nome, Usuario usuario) throws SQLException {
        String sql = "INSERT INTO listas_reproducao (nome, id_usuario) VALUES (?, ?) RETURNING id";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setInt(2, usuario.getId());
            ResultSet rs = ps.executeQuery();
            rs.next();
            return new ListaReproducao(rs.getInt(1), nome, usuario); // usa o id gerado
        }
    }

    // Atualiza o nome de uma lista no banco
    public void editar(int idLista, String novoNome) throws SQLException {
        String sql = "UPDATE listas_reproducao SET nome = ? WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, novoNome);
            ps.setInt(2, idLista);
            ps.executeUpdate();
        }
    }

    // Exclui a lista — o ON DELETE CASCADE nas foreign keys remove os vínculos com vídeos automaticamente
    public void excluir(int idLista) throws SQLException {
        String sql = "DELETE FROM listas_reproducao WHERE id = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            ps.executeUpdate();
        }
    }

    // Adiciona um vídeo à lista na tabela de junção lista_videos.
    // ON CONFLICT DO NOTHING evita erro se o vídeo já estiver na lista.
    public void adicionarVideo(int idLista, int idVideo) throws SQLException {
        String sql = "INSERT INTO lista_videos (id_lista, id_video) VALUES (?,?) ON CONFLICT DO NOTHING";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            ps.setInt(2, idVideo);
            ps.executeUpdate();
        }
    }

    // Remove o vínculo entre o vídeo e a lista na tabela lista_videos
    public void removerVideo(int idLista, int idVideo) throws SQLException {
        String sql = "DELETE FROM lista_videos WHERE id_lista = ? AND id_video = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            ps.setInt(2, idVideo);
            ps.executeUpdate();
        }
    }

    // Busca os vídeos que estão nessa lista e os adiciona ao objeto ListaReproducao.
    // Usa JOIN com lista_videos para trazer só os vídeos daquela lista específica.
    public void carregarVideos(ListaReproducao lista) throws SQLException {
        String sql =
            "SELECT v.id, v.titulo, v.genero, v.ano_lancamento, v.tipo," +
            "       COUNT(c.id_usuario) AS num_curtidas," +
            "       f.duracao, f.diretor," +
            "       s.total_temporadas, s.total_episodios, s.status" +
            " FROM lista_videos lv" +
            " JOIN videos v ON lv.id_video = v.id" +       // só vídeos que estão nessa lista
            " LEFT JOIN curtidas c ON v.id = c.id_video" +
            " LEFT JOIN filmes   f ON v.id = f.id" +
            " LEFT JOIN series   s ON v.id = s.id" +
            " WHERE lv.id_lista = ?" +
            " GROUP BY v.id, f.duracao, f.diretor, s.total_temporadas, s.total_episodios, s.status" +
            " ORDER BY v.titulo";

        lista.getVideos().clear(); // limpa a lista antes de recarregar do banco
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lista.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.adicionarVideo(mapearVideo(rs));
            }
        }
    }

    // Conta quantos vídeos estão em uma lista — usado para exibir na interface
    public int contarVideos(int idLista) throws SQLException {
        String sql = "SELECT COUNT(*) FROM lista_videos WHERE id_lista = ?";
        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idLista);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // Converte uma linha do ResultSet em Filme ou Serie — igual ao VideoDAO
    private Video mapearVideo(ResultSet rs) throws SQLException {
        int    id       = rs.getInt("id");
        String titulo   = rs.getString("titulo");
        String genero   = rs.getString("genero");
        int    ano      = rs.getInt("ano_lancamento");
        int    curtidas = rs.getInt("num_curtidas");
        String tipo     = rs.getString("tipo");

        if ("FILME".equals(tipo)) {
            return new Filme(id, titulo, genero, ano, curtidas,
                             rs.getInt("duracao"), rs.getString("diretor"));
        } else {
            return new Serie(id, titulo, genero, ano, curtidas,
                             rs.getInt("total_temporadas"),
                             rs.getInt("total_episodios"),
                             rs.getString("status"));
        }
    }
}
