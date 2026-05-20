import dao.ConexaoDB;
import view.LoginView;

import javax.swing.*;




public class Main {

    public static void main(String[] args) {
        // Look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        // Inicializa o banco de dados (cria tabelas + seed)
        try {
            ConexaoDB.inicializarBanco();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Não foi possível conectar ao banco de dados.\n\n" +
                "Verifique:\n" +
                "  • O PostgreSQL está rodando?\n" +
                "  • O banco 'feitv' existe? (CREATE DATABASE feitv;)\n" +
                "  • As credenciais em dao/ConexaoDB.java estão corretas?\n\n" +
                "Erro: " + e.getMessage(),
                "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Inicia a interface gráfica na Event Dispatch Thread
        SwingUtilities.invokeLater(LoginView::new);
    }
}
