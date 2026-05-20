package controller;

import dao.ListaReproducaoDAO;
import model.ListaReproducao;
import model.Usuario;
import model.Video;

import java.sql.SQLException;
import java.util.List;

// Responsável por intermediar todas as operações de listas de reprodução.
// Valida os dados e repassa ao DAO — a View nunca acessa o banco diretamente.
public class ListaReproducaoController {

    private final ListaReproducaoDAO dao = new ListaReproducaoDAO();

    // Retorna todas as listas do usuário para exibir na tela de listas
    public List<ListaReproducao> listar(Usuario usuario) {
        try {
            return dao.listarPorUsuario(usuario);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar listas: " + e.getMessage(), e);
        }
    }

    // Cria uma nova lista — valida que o nome não está vazio antes de chamar o banco
    public ListaReproducao criar(String nome, Usuario usuario) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome da lista não pode ser vazio.");
        }
        try {
            return dao.criar(nome.trim(), usuario);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar lista: " + e.getMessage(), e);
        }
    }

    // Renomeia uma lista — atualiza no banco E no objeto em memória
    public void editar(ListaReproducao lista, String novoNome) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException("O nome da lista não pode ser vazio.");
        }
        try {
            dao.editar(lista.getId(), novoNome.trim()); // atualiza no banco
            lista.setNome(novoNome.trim());             // atualiza o objeto em memória
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao editar lista: " + e.getMessage(), e);
        }
    }

    // Exclui a lista do banco — o CASCADE nas foreign keys remove os vídeos da lista também
    public void excluir(ListaReproducao lista) {
        try {
            dao.excluir(lista.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao excluir lista: " + e.getMessage(), e);
        }
    }

    // Adiciona um vídeo à lista — salva no banco E no objeto em memória
    public void adicionarVideo(ListaReproducao lista, Video video) {
        try {
            dao.adicionarVideo(lista.getId(), video.getId()); // salva no banco
            lista.adicionarVideo(video);                      // adiciona no objeto em memória
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao adicionar vídeo: " + e.getMessage(), e);
        }
    }

    // Remove um vídeo da lista — remove do banco E do objeto em memória
    public void removerVideo(ListaReproducao lista, Video video) {
        try {
            dao.removerVideo(lista.getId(), video.getId()); // remove do banco
            lista.removerVideo(video);                      // remove do objeto em memória
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover vídeo: " + e.getMessage(), e);
        }
    }

    // Carrega os vídeos de uma lista buscando no banco — usado ao abrir a lista
    public void carregarVideos(ListaReproducao lista) {
        try {
            dao.carregarVideos(lista);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar vídeos: " + e.getMessage(), e);
        }
    }

    // Retorna quantos vídeos tem em uma lista para exibir na interface
    public int contarVideos(int idLista) {
        try {
            return dao.contarVideos(idLista);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao contar vídeos: " + e.getMessage(), e);
        }
    }
}
