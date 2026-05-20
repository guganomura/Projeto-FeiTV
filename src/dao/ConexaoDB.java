package dao;

import java.sql.*;

// Responsável por gerenciar a conexão com o banco de dados PostgreSQL.
// Também cria as tabelas e insere os dados iniciais na primeira vez que o app roda.
public class ConexaoDB {

    // Dados de conexão com o banco — altere aqui se necessário
    private static final String HOST    = "localhost";
    private static final String PORTA   = "5432";
    private static final String BANCO   = "feitv";
    private static final String USUARIO = "postgres";
    private static final String SENHA   = "210405";

    // Monta a URL de conexão com o formato que o JDBC espera
    private static final String URL =
            "jdbc:postgresql://" + HOST + ":" + PORTA + "/" + BANCO;

    // Abre e retorna uma conexão com o banco.
    // Cada DAO chama esse método antes de executar qualquer SQL.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    // Esse método é chamado uma única vez quando o app inicia (em Main.java).
    // Ele cria as tabelas SE ainda não existirem (IF NOT EXISTS), então é seguro chamar sempre.
    // Se a tabela de vídeos estiver vazia, insere os 20 vídeos padrão.
    public static void inicializarBanco() throws SQLException {
        try (Connection conn = getConnection();
             Statement  stmt = conn.createStatement()) {

            // Tabela de usuários cadastrados
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "  id    SERIAL PRIMARY KEY," +
                "  nome  VARCHAR(100) NOT NULL," +
                "  email VARCHAR(100) UNIQUE NOT NULL," +  // email não pode repetir
                "  senha VARCHAR(255) NOT NULL" +           // senha salva como hash SHA-256
                ")"
            );

            // Tabela principal de vídeos — guarda o que filmes e séries têm em comum
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS videos (" +
                "  id             SERIAL PRIMARY KEY," +
                "  titulo         VARCHAR(200) NOT NULL," +
                "  genero         VARCHAR(50)  NOT NULL," +
                "  ano_lancamento INT          NOT NULL," +
                "  tipo           VARCHAR(10)  NOT NULL" +  // "FILME" ou "SERIE"
                ")"
            );

            // Dados específicos de filmes — referencia a tabela videos pelo id
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS filmes (" +
                "  id      INT PRIMARY KEY REFERENCES videos(id) ON DELETE CASCADE," +
                "  duracao INT," +
                "  diretor VARCHAR(100)" +
                ")"
            );

            // Dados específicos de séries — referencia a tabela videos pelo id
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS series (" +
                "  id               INT PRIMARY KEY REFERENCES videos(id) ON DELETE CASCADE," +
                "  total_temporadas INT," +
                "  total_episodios  INT," +
                "  status           VARCHAR(50)" +
                ")"
            );

            // Tabela de curtidas — relaciona usuário com vídeo (N para N)
            // A chave primária composta garante que o mesmo usuário não curta duas vezes
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS curtidas (" +
                "  id_usuario INT REFERENCES usuarios(id) ON DELETE CASCADE," +
                "  id_video   INT REFERENCES videos(id)   ON DELETE CASCADE," +
                "  PRIMARY KEY (id_usuario, id_video)" +
                ")"
            );

            // Listas de reprodução criadas pelos usuários
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS listas_reproducao (" +
                "  id         SERIAL PRIMARY KEY," +
                "  nome       VARCHAR(100) NOT NULL," +
                "  id_usuario INT REFERENCES usuarios(id) ON DELETE CASCADE" +
                ")"
            );

            // Relaciona vídeos com listas (N para N)
            // PRIMARY KEY composta impede o mesmo vídeo entrar duas vezes na mesma lista
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS lista_videos (" +
                "  id_lista INT REFERENCES listas_reproducao(id) ON DELETE CASCADE," +
                "  id_video INT REFERENCES videos(id)             ON DELETE CASCADE," +
                "  PRIMARY KEY (id_lista, id_video)" +
                ")"
            );

            // Verifica se já existem vídeos no banco
            // Se não existir nenhum, insere os 20 vídeos padrão (seed)
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM videos");
            rs.next();
            if (rs.getInt(1) == 0) {
                inserirDadosIniciais(conn);
            }
        }
    }

    // Insere 10 filmes e 10 séries para o sistema já ter conteúdo na primeira execução
    private static void inserirDadosIniciais(Connection conn) throws SQLException {
        String sqlVideo = "INSERT INTO videos (titulo, genero, ano_lancamento, tipo) VALUES (?,?,?,?) RETURNING id";
        String sqlFilme = "INSERT INTO filmes (id, duracao, diretor) VALUES (?,?,?)";
        String sqlSerie = "INSERT INTO series (id, total_temporadas, total_episodios, status) VALUES (?,?,?,?)";

        // Cada linha: { titulo, genero, ano, duracao, diretor }
        Object[][] filmes = {
            {"The Dark Knight",   "Ação",              2008, 152, "Christopher Nolan"},
            {"Inception",         "Ficção Científica", 2010, 148, "Christopher Nolan"},
            {"Interstellar",      "Ficção Científica", 2014, 169, "Christopher Nolan"},
            {"Pulp Fiction",      "Crime",             1994, 154, "Quentin Tarantino"},
            {"The Godfather",     "Crime",             1972, 175, "Francis Ford Coppola"},
            {"Forrest Gump",      "Drama",             1994, 142, "Robert Zemeckis"},
            {"The Matrix",        "Ficção Científica", 1999, 136, "The Wachowskis"},
            {"Gladiator",         "Ação",              2000, 155, "Ridley Scott"},
            {"Parasite",          "Thriller",          2019, 132, "Bong Joon-ho"},
            {"Schindler's List",  "Drama",             1993, 195, "Steven Spielberg"},
        };

        for (Object[] f : filmes) {
            try (PreparedStatement ps = conn.prepareStatement(sqlVideo)) {
                ps.setString(1, (String) f[0]);
                ps.setString(2, (String) f[1]);
                ps.setInt(3,    (int)    f[2]);
                ps.setString(4, "FILME");
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = rs.getInt(1); // pega o id gerado automaticamente
                try (PreparedStatement ps2 = conn.prepareStatement(sqlFilme)) {
                    ps2.setInt(1,    id);
                    ps2.setInt(2,    (int)    f[3]);
                    ps2.setString(3, (String) f[4]);
                    ps2.executeUpdate();
                }
            }
        }

        // Cada linha: { titulo, genero, ano, temporadas, episodios, status }
        Object[][] series = {
            {"Breaking Bad",    "Crime",             2008, 5,  62,  "Finalizada"},
            {"Game of Thrones", "Fantasia",          2011, 8,  73,  "Finalizada"},
            {"Stranger Things", "Ficção Científica", 2016, 4,  34,  "Em Andamento"},
            {"The Office",      "Comédia",           2005, 9,  201, "Finalizada"},
            {"Black Mirror",    "Ficção Científica", 2011, 6,  27,  "Em Andamento"},
            {"Friends",         "Comédia",           1994, 10, 236, "Finalizada"},
            {"The Witcher",     "Fantasia",          2019, 3,  24,  "Em Andamento"},
            {"Narcos",          "Crime",             2015, 3,  30,  "Finalizada"},
            {"Dark",            "Mistério",          2017, 3,  26,  "Finalizada"},
            {"Squid Game",      "Thriller",          2021, 2,  17,  "Em Andamento"},
        };

        for (Object[] s : series) {
            try (PreparedStatement ps = conn.prepareStatement(sqlVideo)) {
                ps.setString(1, (String) s[0]);
                ps.setString(2, (String) s[1]);
                ps.setInt(3,    (int)    s[2]);
                ps.setString(4, "SERIE");
                ResultSet rs = ps.executeQuery();
                rs.next();
                int id = rs.getInt(1);
                try (PreparedStatement ps2 = conn.prepareStatement(sqlSerie)) {
                    ps2.setInt(1,    id);
                    ps2.setInt(2,    (int)    s[3]);
                    ps2.setInt(3,    (int)    s[4]);
                    ps2.setString(4, (String) s[5]);
                    ps2.executeUpdate();
                }
            }
        }
    }
}
