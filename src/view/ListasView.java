package view;

import controller.ListaReproducaoController;
import controller.VideoController;
import model.ListaReproducao;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Diálogo de gerenciamento das listas de reprodução do usuário.
 */
public class ListasView extends JDialog {

    private final ListaReproducaoController listaCtrl;
    private final VideoController           videoCtrl;
    private final Usuario                   usuario;

    private DefaultListModel<ListaReproducao> modelo;
    private JList<ListaReproducao>            listaUI;

    public ListasView(Frame owner, Usuario usuario,
                      ListaReproducaoController listaCtrl,
                      VideoController videoCtrl) {
        super(owner, "Minhas Listas", true);
        this.usuario   = usuario;
        this.listaCtrl = listaCtrl;
        this.videoCtrl = videoCtrl;
        configurarJanela();
        add(construirPainel());
        carregarListas();
        setVisible(true);
    }

    // ── Configuração ──────────────────────────────────────────────────────────

    private void configurarJanela() {
        setSize(420, 500);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(new Color(25, 25, 25));
    }

    // ── Painel ────────────────────────────────────────────────────────────────

    private JPanel construirPainel() {
        JPanel painel = new JPanel(new BorderLayout(0, 10));
        painel.setBackground(new Color(25, 25, 25));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Cabeçalho
        JLabel titulo = new JLabel("Minhas Listas de Reprodução");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(Color.WHITE);
        painel.add(titulo, BorderLayout.NORTH);

        // Lista
        modelo = new DefaultListModel<>();
        listaUI = new JList<>(modelo);
        listaUI.setBackground(new Color(35, 35, 35));
        listaUI.setForeground(Color.WHITE);
        listaUI.setFont(new Font("Arial", Font.PLAIN, 13));
        listaUI.setSelectionBackground(new Color(229, 9, 20));
        listaUI.setSelectionForeground(Color.WHITE);
        listaUI.setCellRenderer(new ListaRenderer());
        listaUI.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) abrirLista();
            }
        });

        JScrollPane scroll = new JScrollPane(listaUI);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));
        painel.add(scroll, BorderLayout.CENTER);

        // Botões
        painel.add(construirPainelBotoes(), BorderLayout.SOUTH);

        return painel;
    }

    private JPanel construirPainelBotoes() {
        JPanel p = new JPanel(new GridLayout(2, 2, 6, 6));
        p.setBackground(new Color(25, 25, 25));

        JButton btnNova     = criarBotao("+ Nova Lista",       new Color(40, 140, 60));
        JButton btnRenomear = criarBotao("✎ Renomear",         new Color(60, 90, 160));
        JButton btnExcluir  = criarBotao("✕ Excluir",          new Color(180, 0, 0));
        JButton btnAbrir    = criarBotao("▶ Ver Vídeos",       new Color(229, 9, 20));

        btnNova.addActionListener(e -> novaLista());
        btnRenomear.addActionListener(e -> renomearLista());
        btnExcluir.addActionListener(e -> excluirLista());
        btnAbrir.addActionListener(e -> abrirLista());

        p.add(btnNova);
        p.add(btnRenomear);
        p.add(btnExcluir);
        p.add(btnAbrir);
        return p;
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void carregarListas() {
        modelo.clear();
        List<ListaReproducao> listas = listaCtrl.listar(usuario);
        for (ListaReproducao l : listas) modelo.addElement(l);
    }

    private void novaLista() {
        String nome = JOptionPane.showInputDialog(this, "Nome da nova lista:");
        if (nome == null || nome.isBlank()) return;
        try {
            ListaReproducao nova = listaCtrl.criar(nome, usuario);
            modelo.addElement(nova);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renomearLista() {
        ListaReproducao sel = listaUI.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Selecione uma lista."); return; }
        String novoNome = JOptionPane.showInputDialog(this, "Novo nome:", sel.getNome());
        if (novoNome == null || novoNome.isBlank()) return;
        try {
            listaCtrl.editar(sel, novoNome);
            listaUI.repaint();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirLista() {
        ListaReproducao sel = listaUI.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Selecione uma lista."); return; }
        int conf = JOptionPane.showConfirmDialog(this,
            "Excluir a lista \"" + sel.getNome() + "\"?",
            "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf != JOptionPane.YES_OPTION) return;
        try {
            listaCtrl.excluir(sel);
            modelo.removeElement(sel);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirLista() {
        ListaReproducao sel = listaUI.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, "Selecione uma lista."); return; }
        new GerenciarListaView((Frame) getOwner(), sel, listaCtrl, videoCtrl);
        // Atualiza contagem após fechar
        listaUI.repaint();
    }

    // ── Componentes ───────────────────────────────────────────────────────────

    private JButton criarBotao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ── Renderer ──────────────────────────────────────────────────────────────

    private class ListaRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ListaReproducao l) {
                int qtd = listaCtrl.contarVideos(l.getId());
                setText("  ▶  " + l.getNome() + "   (" + qtd + " vídeo(s))");
            }
            setBackground(isSelected ? new Color(229, 9, 20) : new Color(35, 35, 35));
            setForeground(Color.WHITE);
            setFont(new Font("Arial", Font.PLAIN, 13));
            setBorder(new EmptyBorder(8, 6, 8, 6));
            return this;
        }
    }
}
