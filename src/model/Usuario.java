package model;

// Representa um usuário cadastrado no sistema.
// Esse objeto é criado após o login e fica disponível durante toda a sessão.
public class Usuario {

    private int    id;
    private String nome;
    private String email;
    private String senha; // já vem criptografada do banco (SHA-256)

    public Usuario(int id, String nome, String email, String senha) {
        this.id    = id;
        this.nome  = nome;
        this.email = email;
        this.senha = senha;
    }

    public int    getId()    { return id; }
    public String getNome()  { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }

    // Exibe o nome do usuário quando o objeto é usado como texto
    @Override
    public String toString() { return nome; }
}
