package model;

// Classe abstrata — não dá pra criar um "Video" diretamente.
// Ela existe para guardar o que Filme e Serie têm em comum.
public abstract class Video {

    // Atributos que todo vídeo tem, seja filme ou série
    protected int    id;
    protected String titulo;
    protected String genero;
    protected int    anoLancamento;
    protected int    curtidas;

    // Construtor que recebe todos os dados comuns
    public Video(int id, String titulo, String genero, int anoLancamento, int curtidas) {
        this.id            = id;
        this.titulo        = titulo;
        this.genero        = genero;
        this.anoLancamento = anoLancamento;
        this.curtidas      = curtidas;
    }

    // Getters — fornecem acesso aos atributos de fora da classe
    public int    getId()            { return id; }
    public String getTitulo()        { return titulo; }
    public String getGenero()        { return genero; }
    public int    getAnoLancamento() { return anoLancamento; }
    public int    getCurtidas()      { return curtidas; }
    public void   setCurtidas(int c) { this.curtidas = c; }

    // Incrementa a contagem de curtidas
    public void curtir()    { curtidas++; }

    // Decrementa a contagem — mas nunca passa de zero
    public void descurtir() { if (curtidas > 0) curtidas--; }

    // Método abstrato: cada subclasse decide o que retornar.
    // Filme retorna null, Serie retorna a própria série (this)
    public abstract Situacao getSituacao();

    // Retorna o tipo do vídeo como String: "FILME" ou "SERIE"
    public abstract String getTipo();

    // Quando o objeto for exibido como texto (ex: numa JList), mostra o título
    @Override
    public String toString() { return titulo; }
}
