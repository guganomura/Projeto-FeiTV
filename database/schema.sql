-- FEItv - Schema do Banco de Dados

CREATE TABLE IF NOT EXISTS usuarios (
    id    SERIAL PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS videos (
    id             SERIAL PRIMARY KEY,
    titulo         VARCHAR(200) NOT NULL,
    genero         VARCHAR(50)  NOT NULL,
    ano_lancamento INT          NOT NULL,
    tipo           VARCHAR(10)  NOT NULL  -- 'FILME' ou 'SERIE'
);

CREATE TABLE IF NOT EXISTS filmes (
    id      INT PRIMARY KEY REFERENCES videos(id) ON DELETE CASCADE,
    duracao INT,          -- em minutos
    diretor VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS series (
    id               INT PRIMARY KEY REFERENCES videos(id) ON DELETE CASCADE,
    total_temporadas INT,
    total_episodios  INT,
    status           VARCHAR(50)   -- 'Em Andamento', 'Finalizada', 'Cancelada'
);

CREATE TABLE IF NOT EXISTS curtidas (
    id_usuario INT REFERENCES usuarios(id) ON DELETE CASCADE,
    id_video   INT REFERENCES videos(id)   ON DELETE CASCADE,
    PRIMARY KEY (id_usuario, id_video)
);

CREATE TABLE IF NOT EXISTS listas_reproducao (
    id         SERIAL PRIMARY KEY,
    nome       VARCHAR(100) NOT NULL,
    id_usuario INT REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS lista_videos (
    id_lista INT REFERENCES listas_reproducao(id) ON DELETE CASCADE,
    id_video INT REFERENCES videos(id)             ON DELETE CASCADE,
    PRIMARY KEY (id_lista, id_video)
);
