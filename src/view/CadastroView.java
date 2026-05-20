package view;

import controller.UsuarioController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Diálogo de cadastro de novo usuário.
 */
public class CadastroView extends JDialog {

    private final UsuarioController controller = new UsuarioController();

    private JTextField     campoNome;
    private JTextField     campoEmail;
    private JPasswordField campoSenha;
    private JPasswordField campoConfirma;

    public CadastroView(Frame owner) {
        super(owner, "FEItv — Cadastro", true);
        configurarJanela();
        add(construirPainel());
        setVisible(true);
    }

    // ── Configuração ──────────────────────────────────────────────────────────

    private void configurarJanela() {
        setSize(420, 500);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(new Color(20, 20, 20));
    }

    // ── Painel ────────────────────────────────────────────────────────────────

    private JPanel construirPainel() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(new Color(20, 20, 20));
        painel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0; c.weightx = 1;

        // Título
        JLabel titulo = new JLabel("Criar Conta", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 26));
        titulo.setForeground(Color.WHITE);
        c.gridy = 0; c.insets = new Insets(0, 0, 20, 0); painel.add(titulo, c);

        // Nome
        c.insets = new Insets(4, 0, 2, 0);
        c.gridy = 1; painel.add(criarLabel("Nome completo"), c);
        campoNome = criarTextField();
        c.gridy = 2; c.insets = new Insets(0, 0, 10, 0); painel.add(campoNome, c);

        // E-mail
        c.insets = new Insets(4, 0, 2, 0);
        c.gridy = 3; painel.add(criarLabel("E-mail"), c);
        campoEmail = criarTextField();
        c.gridy = 4; c.insets = new Insets(0, 0, 10, 0); painel.add(campoEmail, c);

        // Senha
        c.insets = new Insets(4, 0, 2, 0);
        c.gridy = 5; painel.add(criarLabel("Senha (mínimo 6 caracteres)"), c);
        campoSenha = criarPasswordField();
        c.gridy = 6; c.insets = new Insets(0, 0, 10, 0); painel.add(campoSenha, c);

        // Confirmar senha
        c.insets = new Insets(4, 0, 2, 0);
        c.gridy = 7; painel.add(criarLabel("Confirmar senha"), c);
        campoConfirma = criarPasswordField();
        c.gridy = 8; c.insets = new Insets(0, 0, 20, 0); painel.add(campoConfirma, c);

        // Botão cadastrar
        JButton btnCadastrar = criarBotaoPrimario("Cadastrar");
        btnCadastrar.addActionListener(e -> cadastrar());
        c.gridy = 9; c.insets = new Insets(0, 0, 8, 0); painel.add(btnCadastrar, c);

        // Cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setForeground(Color.LIGHT_GRAY);
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnCancelar.addActionListener(e -> dispose());
        c.gridy = 10; painel.add(btnCancelar, c);

        return painel;
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void cadastrar() {
        try {
            controller.cadastrar(
                campoNome.getText(),
                campoEmail.getText(),
                new String(campoSenha.getPassword()),
                new String(campoConfirma.getPassword())
            );
            JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso!\nFaça login para continuar.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
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
