# FEItv

Plataforma de informações sobre filmes e séries desenvolvida como projeto da disciplina **CCM310 — Programação Orientada a Objetos** da FEI.

**Aluno:** Gustavo Nomura — RA: 721250504
**Professor:** André Levi Zanon

---

## Objetivo

O FEItv é uma aplicação desktop inspirada em plataformas de streaming como Netflix e YouTube. Permite que usuários cadastrados busquem filmes e séries, curtam conteúdos e organizem seus favoritos em listas de reprodução personalizadas.

---

## Tecnologias Utilizadas

| Tecnologia | Uso |
|------------|-----|
| Java 17+ | Linguagem principal |
| Swing | Interface gráfica (GUI) |
| JDBC | Comunicação com banco de dados |
| PostgreSQL | Persistência de dados |
| MVC | Padrão arquitetural |

---

## Funcionalidades

- **Cadastro e Login** de usuário com senha criptografada (SHA-256)
- **Busca de vídeos** por título (case-insensitive)
- **Listagem de informações** — Filmes: diretor e duração | Séries: temporadas, episódios e status
- **Curtir e descurtir** vídeos (persistido por usuário)
- **Gerenciar listas de reprodução:**
  - Criar, renomear e excluir listas
  - Adicionar e remover vídeos das listas

---

## Diagrama de Classes

```
<<interface>>          <<abstract>>
Situacao    ←────   Video
+ getNome()          - id, titulo, genero
+ getDescricao()     - anoLancamento, curtidas
                     + curtir(), descurtir()
                     + getSituacao()
                          ▲
              ┌───────────┴───────────┐
           Filme                   Serie (implements Situacao)
         - duracao               - totalTemporadas
         - diretor               - totalEpisodios
                                 - status

Usuario  ◆────────  ListaReproducao  ◆──── Video
- id, nome               - id, nome
- email, senha           - dono (Usuario)
```

**Relações:**
- `Filme` e `Serie` herdam de `Video` (herança)
- `Serie` implementa `Situacao` (interface) — `getSituacao()` retorna `this`
- `Filme.getSituacao()` retorna `null`
- `Usuario` → `ListaReproducao`: agregação (1 para muitos)
- `ListaReproducao` → `Video`: associação (muitos para muitos)
- `Usuario` → `Video` (curtidas): associação (muitos para muitos)

---

## Estrutura do Projeto

```
src/
├── Main.java
├── model/
│   ├── Situacao.java         (interface)
│   ├── Video.java            (classe abstrata)
│   ├── Filme.java
│   ├── Serie.java
│   ├── Usuario.java
│   └── ListaReproducao.java
├── dao/
│   ├── ConexaoDB.java
│   ├── UsuarioDAO.java
│   ├── VideoDAO.java
│   └── ListaReproducaoDAO.java
├── controller/
│   ├── UsuarioController.java
│   ├── VideoController.java
│   └── ListaReproducaoController.java
└── view/
    ├── LoginView.java
    ├── CadastroView.java
    ├── MainView.java
    ├── DetalhesVideoPanel.java
    ├── ListasView.java
    └── GerenciarListaView.java
database/
├── schema.sql
└── dados_iniciais.sql
lib/
└── postgresql-*.jar
```

---

## Banco de Dados

### Tabelas

| Tabela | Descrição |
|--------|-----------|
| `usuarios` | Dados dos usuários cadastrados |
| `videos` | Informações gerais de filmes e séries |
| `filmes` | Dados específicos de filmes (diretor, duração) |
| `series` | Dados específicos de séries (temporadas, episódios, status) |
| `curtidas` | Relação usuário ↔ vídeo curtido |
| `listas_reproducao` | Listas de reprodução dos usuários |
| `lista_videos` | Relação lista ↔ vídeo |

O banco é **inicializado automaticamente** ao abrir o app — as tabelas são criadas e 20 vídeos (10 filmes + 10 séries) são inseridos na primeira execução.

---

## Como Executar

### Pré-requisitos
- Java 17+
- PostgreSQL instalado e rodando
- Driver JDBC do PostgreSQL em `lib/` → [Download](https://jdbc.postgresql.org/download/)

### Passo a passo

**1. Criar o banco de dados:**
```bash
psql -U postgres -c "CREATE DATABASE feitv;"
```

**2. Compilar:**
```bash
./compile.sh
```

**3. Executar:**
```bash
./run.sh
```

> As credenciais do banco podem ser alteradas em `src/dao/ConexaoDB.java` (HOST, PORTA, BANCO, USUARIO, SENHA).

---

## Dados de Teste

O sistema já vem com 20 vídeos pré-cadastrados:

**Filmes:** The Dark Knight, Inception, Interstellar, Pulp Fiction, The Godfather, Forrest Gump, The Matrix, Gladiator, Parasite, Schindler's List

**Séries:** Breaking Bad, Game of Thrones, Stranger Things, The Office, Black Mirror, Friends, The Witcher, Narcos, Dark, Squid Game

---

## Repositório GitHub

https://github.com/guganomura/projeto-FeiTV
