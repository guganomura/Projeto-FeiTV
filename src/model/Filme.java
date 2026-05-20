package model;

// Filme herda de Video — já tem titulo, genero, curtidas, etc.
// Aqui só adicionamos o que é exclusivo de um filme: duração e diretor.
public class Filme extends Video {

    private int    duracao; // duração em minutos
    private String diretor;

    // Construtor chama o super() para preencher os dados comuns de Video,
    // e depois preenche os dados específicos do Filme
    public Filme(int id, String titulo, String genero, int anoLancamento,
                 int curtidas, int duracao, String diretor) {
        super(id, titulo, genero, anoLancamento, curtidas);
        this.duracao = duracao;
        this.diretor = diretor;
    }

    public int    getDuracao() { return duracao; }
    public String getDiretor() { return diretor; }

    // Filme não tem situação/status, por isso retorna null
    @Override
    public Situacao getSituacao() { return null; }

    // Identifica o tipo para quando formos salvar ou buscar no banco
    @Override
    public String getTipo() { return "FILME"; }
}
