package view;

import controller.UsuarioController;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Tela de login do FEItv.
 */
public class LoginView extends JFrame {

    private final UsuarioController controller = new UsuarioController();

    private JTextField  campoEmail;
    private JPasswordField campoSenha;

    public LoginView() {
        configurarJanela();
        add(construirPainel());
        setVisible(true);
    }

    // ── Configuração da janela ─────────────────────────────────────────────────

    private void configurarJanela() {
        setTitle("FEItv — Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 420);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(new Color(20, 20, 20));
    }

    // ── Construção do painel ───────────────────────────────────────────────────

    private JPanel construirPainel() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(20, 20, 20));
        painel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(6, 0, 6, 0);
        c.gridx = 0; c.weightx = 1;

        // Título
        JLabel titulo = new JLabel("FEItv", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 38));
        titulo.setForeground(new Color(229, 9, 20));
        c.gridy = 0; painel.add(titulo, c);

        JLabel sub = new JLabel("Faça login para continuar", SwingConstants.CENTER);
        sub.setFont(new Font("Arial", Font.PLAIN, 13));
        sub.setForeground(Color.LIGHT_GRAY);
        c.gridy = 1; painel.add(sub, c);

        c.insets = new Insets(10, 0, 4, 0);

        // E-mail
        JLabel lblEmail = criarLabel("E-mail");
        c.gridy = 2; painel.add(lblEmail, c);
        campoEmail = criarTextField();
        c.insets = new Insets(0, 0, 8, 0);
        c.gridy = 3; painel.add(campoEmail, c);

        // Senha
        c.insets = new Insets(4, 0, 4, 0);
        JLabel lblSenha = criarLabel("Senha");
        c.gridy = 4; painel.add(lblSenha, c);
        campoSenha = criarPasswordField();
        c.insets = new Insets(0, 0, 16, 0);
        c.gridy = 5; painel.add(campoSenha, c);

        // Botão entrar
        JButton btnEntrar = criarBotaoPrimario("Entrar");
        btnEntrar.addActionListener(e -> fazerLogin());
        // Enter na senha também faz login
        campoSenha.addActionListener(e -> fazerLogin());
        c.insets = new Insets(4, 0, 8, 0);
        c.gridy = 6; painel.add(btnEntrar, c);

        // Separador
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 60));
        c.gridy = 7; painel.add(sep, c);

        // Link cadastro
        JButton btnCadastro = new JButton("Não tem conta? Cadastre-se");
        btnCadastro.setContentAreaFilled(false);
        btnCadastro.setBorderPainted(false);
        btnCadastro.setForeground(new Color(229, 9, 20));
        btnCadastro.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCadastro.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCadastro.addActionListener(e -> abrirCadastro());
        c.gridy = 8; painel.add(btnCadastro, c);

        return painel;
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void fazerLogin() {
        try {
            Usuario u = controller.login(
                campoEmail.getText(),
                new String(campoSenha.getPassword())
            );
            dispose();
            new MainView(u);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Login", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro de conexão: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirCadastro() {
        new CadastroView(this);
    }

    // ── Componentes estilizados ────────────────────────────────────────────────

    private JLabel criarLabel(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Arial", Font.PLAIN, 12));
        return l;
    }

    private JTextField criarTextField() {
        JTextField tf = new JTextField();
        estilizarCampo(tf);
        return tf;
    }

    private JPasswordField criarPasswordField() {
        JPasswordField pf = new JPasswordField();
        estilizarCampo(pf);
        return pf;
    }

    private void estilizarCampo(JTextField tf) {
        tf.setBackground(new Color(40, 40, 40));
        tf.setForeground(Color.WHITE);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        tf.setPreferredSize(new Dimension(0, 36));
    }

    private JButton criarBotaoPrimario(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(new Color(229, 9, 20));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setPreferredSize(new Dimension(0, 40));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
