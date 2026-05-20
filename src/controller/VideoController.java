package controller;

import dao.VideoDAO;
import model.Video;

import java.sql.SQLException;
import java.util.List;

// Responsável por intermediar as operações de vídeo entre a tela e o banco.
// A View chama esse controller — nunca chama o DAO diretamente.
public class VideoController {

    private final VideoDAO dao = new VideoDAO();

    // Busca vídeos pelo nome e retorna a lista para a View exibir na JTable.
    // Se o termo for null, trata como vazio para evitar NullPointerException.
    public List<Video> buscar(String termo) {
        try {
            return dao.buscarPorNome(termo == null ? "" : termo.trim());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar vídeos: " + e.getMessage(), e);
        }
    }

    // Retorna todos os vídeos — usado na tela de gerenciar lista
    public List<Video> listarTodos() {
        try {
            return dao.listarTodos();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vídeos: " + e.getMessage(), e);
        }
    }

    // Alterna a curtida: se já curtiu, descurte; se não curtiu, curte.
    // Atualiza tanto o banco quanto o objeto em memória.
    // Retorna true se curtiu, false se descurtiu — para a View atualizar o botão.
    public boolean alternarCurtida(int idUsuario, Video video) {
        try {
            boolean jaCurtiu = dao.usuarioCurtiu(idUsuario, video.getId());

            if (jaCurtiu) {
                dao.descurtir(idUsuario, video.getId()); // remove do banco
                video.descurtir();                       // atualiza o objeto em memória
                return false;
            } else {
                dao.curtir(idUsuario, video.getId());    // insere no banco
                video.curtir();                          // atualiza o objeto em memória
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao curtir/descurtir: " + e.getMessage(), e);
        }
    }

    // Verifica se o usuário já curtiu — usado para definir o texto do botão na tela
    public boolean usuarioCurtiu(int idUsuario, int idVideo) {
        try {
            return dao.usuarioCurtiu(idUsuario, idVideo);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar curtida: " + e.getMessage(), e);
        }
    }

    // Retorna a contagem atualizada de curtidas para exibir na tela
    public int contarCurtidas(int idVideo) {
        try {
            return dao.contarCurtidas(idVideo);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar curtidas: " + e.getMessage(), e);
        }
    }
}
