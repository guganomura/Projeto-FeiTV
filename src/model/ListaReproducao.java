package model;

import java.util.ArrayList;
import java.util.List;

// Representa uma lista de reprodução criada por um usuário.
// Cada lista tem um dono (Usuario) e uma coleção de vídeos.
public class ListaReproducao {

    private int         id;
    private String      nome;
    private Usuario     dono;       // quem criou essa lista
    private List<Video> videos;     // vídeos que estão dentro da lista

    public ListaReproducao(int id, String nome, Usuario dono) {
        this.id     = id;
        this.nome   = nome;
        this.dono   = dono;
        this.videos = new ArrayList<>(); // começa vazia
    }

    public int         getId()     { return id; }
    public String      getNome()   { return nome; }
    public Usuario     getDono()   { return dono; }
    public List<Video> getVideos() { return videos; }

    public void setNome(String nome) { this.nome = nome; }

    // Adiciona um vídeo à lista, mas só se ele ainda não estiver lá
    public void adicionarVideo(Video v) {
        if (!videos.contains(v)) videos.add(v);
    }

    // Remove um vídeo da lista
    public void removerVideo(Video v) { videos.remove(v); }

    // Exibe o nome da lista quando usada em componentes visuais (ex: JList)
    @Override
    public String toString() { return nome; }
}
