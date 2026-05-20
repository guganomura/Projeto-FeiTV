package model;

// Serie herda de Video E também implementa a interface Situacao.
// Isso significa que a própria série é a sua situação — ela sabe o próprio status.
public class Serie extends Video implements Situacao {

    private int    totalTemporadas;
    private int    totalEpisodios;
    private String status; // pode ser: "Em Andamento", "Finalizada" ou "Cancelada"

    // Construtor: chama super() para os dados comuns e preenche os específicos de série
    public Serie(int id, String titulo, String genero, int anoLancamento,
                 int curtidas, int totalTemporadas, int totalEpisodios, String status) {
        super(id, titulo, genero, anoLancamento, curtidas);
        this.totalTemporadas = totalTemporadas;
        this.totalEpisodios  = totalEpisodios;
        this.status          = status;
    }

    public int    getTotalTemporadas() { return totalTemporadas; }
    public int    getTotalEpisodios()  { return totalEpisodios; }
    public String getStatus()          { return status; }

    // Retorna a própria série como Situacao — porque Serie implementa essa interface
    // É o ponto mais importante do projeto: "this" significa o próprio objeto
    @Override
    public Situacao getSituacao() { return this; }

    // Obrigado pela interface Situacao — retorna o status como nome da situação
    @Override
    public String getNome() { return status; }

    // Obrigado pela interface Situacao — retorna uma descrição resumida
    @Override
    public String getDescricao() {
        return totalTemporadas + " temporada(s) · " + totalEpisodios + " episódio(s)";
    }

    // Identifica o tipo para o banco de dados
    @Override
    public String getTipo() { return "SERIE"; }
}
