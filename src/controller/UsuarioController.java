package controller;

import dao.UsuarioDAO;
import model.Usuario;

import java.sql.SQLException;

// O controller fica entre a View (tela) e o DAO (banco).
// Aqui ficam as validações antes de chamar o banco — a View não precisa saber dessas regras.
public class UsuarioController {

    private final UsuarioDAO dao = new UsuarioDAO();

    // Valida os dados do formulário de cadastro antes de mandar pro banco.
    // Se algo estiver errado, lança uma exceção com a mensagem de erro para a View exibir.
    public void cadastrar(String nome, String email, String senha, String confirmaSenha) {
        // Verifica se os campos obrigatórios foram preenchidos
        if (nome.isBlank())   throw new IllegalArgumentException("Nome não pode ser vazio.");
        if (email.isBlank())  throw new IllegalArgumentException("E-mail não pode ser vazio.");
        if (senha.isBlank())  throw new IllegalArgumentException("Senha não pode ser vazia.");

        // Regra mínima de segurança para a senha
        if (senha.length() < 6) throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres.");

        // Garante que o usuário digitou a senha igual nas duas vezes
        if (!senha.equals(confirmaSenha)) throw new IllegalArgumentException("As senhas não coincidem.");

        try {
            // trim() remove espaços extras, toLowerCase() padroniza o email
            dao.cadastrar(nome.trim(), email.trim().toLowerCase(), senha);
        } catch (SQLException e) {
            // O banco lança erro de "unique" quando o email já está cadastrado
            if (e.getMessage() != null && e.getMessage().contains("unique")) {
                throw new IllegalArgumentException("Este e-mail já está cadastrado.");
            }
            throw new RuntimeException("Erro ao cadastrar usuário: " + e.getMessage(), e);
        }
    }

    // Valida o login e retorna o objeto Usuario se as credenciais estiverem corretas.
    // A View usa esse objeto para saber quem está logado.
    public Usuario login(String email, String senha) {
        // Campos não podem estar em branco
        if (email.isBlank() || senha.isBlank()) {
            throw new IllegalArgumentException("Preencha e-mail e senha.");
        }
        try {
            Usuario u = dao.login(email.trim().toLowerCase(), senha);

            // Se o DAO retornou null, as credenciais estão erradas
            if (u == null) throw new IllegalArgumentException("E-mail ou senha incorretos.");
            return u;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao fazer login: " + e.getMessage(), e);
        }
    }
}
