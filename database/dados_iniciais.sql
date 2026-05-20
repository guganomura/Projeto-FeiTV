-- FEItv - Dados iniciais de exemplo


-- Filmes
INSERT INTO videos (titulo, genero, ano_lancamento, tipo) VALUES
  ('The Dark Knight',    'Ação',              2008, 'FILME'),
  ('Inception',          'Ficção Científica', 2010, 'FILME'),
  ('Interstellar',       'Ficção Científica', 2014, 'FILME'),
  ('Pulp Fiction',       'Crime',             1994, 'FILME'),
  ('The Godfather',      'Crime',             1972, 'FILME'),
  ('Forrest Gump',       'Drama',             1994, 'FILME'),
  ('The Matrix',         'Ficção Científica', 1999, 'FILME'),
  ('Gladiator',          'Ação',              2000, 'FILME'),
  ('Parasite',           'Thriller',          2019, 'FILME'),
  ('Schindler''s List',  'Drama',             1993, 'FILME');

INSERT INTO filmes (id, duracao, diretor)
SELECT v.id, f.duracao, f.diretor
FROM videos v
JOIN (VALUES
  ('The Dark Knight',   152, 'Christopher Nolan'),
  ('Inception',         148, 'Christopher Nolan'),
  ('Interstellar',      169, 'Christopher Nolan'),
  ('Pulp Fiction',      154, 'Quentin Tarantino'),
  ('The Godfather',     175, 'Francis Ford Coppola'),
  ('Forrest Gump',      142, 'Robert Zemeckis'),
  ('The Matrix',        136, 'The Wachowskis'),
  ('Gladiator',         155, 'Ridley Scott'),
  ('Parasite',          132, 'Bong Joon-ho'),
  ('Schindler''s List', 195, 'Steven Spielberg')
) AS f(titulo, duracao, diretor) ON v.titulo = f.titulo;

-- Séries
INSERT INTO videos (titulo, genero, ano_lancamento, tipo) VALUES
  ('Breaking Bad',    'Crime',             2008, 'SERIE'),
  ('Game of Thrones', 'Fantasia',          2011, 'SERIE'),
  ('Stranger Things', 'Ficção Científica', 2016, 'SERIE'),
  ('The Office',      'Comédia',           2005, 'SERIE'),
  ('Black Mirror',    'Ficção Científica', 2011, 'SERIE'),
  ('Friends',         'Comédia',           1994, 'SERIE'),
  ('The Witcher',     'Fantasia',          2019, 'SERIE'),
  ('Narcos',          'Crime',             2015, 'SERIE'),
  ('Dark',            'Mistério',          2017, 'SERIE'),
  ('Squid Game',      'Thriller',          2021, 'SERIE');

INSERT INTO series (id, total_temporadas, total_episodios, status)
SELECT v.id, s.total_temporadas, s.total_episodios, s.status
FROM videos v
JOIN (VALUES
  ('Breaking Bad',    5,  62,  'Finalizada'),
  ('Game of Thrones', 8,  73,  'Finalizada'),
  ('Stranger Things', 4,  34,  'Em Andamento'),
  ('The Office',      9,  201, 'Finalizada'),
  ('Black Mirror',    6,  27,  'Em Andamento'),
  ('Friends',         10, 236, 'Finalizada'),
  ('The Witcher',     3,  24,  'Em Andamento'),
  ('Narcos',          3,  30,  'Finalizada'),
  ('Dark',            3,  26,  'Finalizada'),
  ('Squid Game',      2,  17,  'Em Andamento')
) AS s(titulo, total_temporadas, total_episodios, status) ON v.titulo = s.titulo;
