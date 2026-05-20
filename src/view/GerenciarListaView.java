package view;

import controller.ListaReproducaoController;
import controller.VideoController;
import model.ListaReproducao;
import model.Video;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

/**
 * Diálogo para gerenciar os vídeos de uma lista de reprodução.
 * Permite visualizar, remover e adicionar vídeos à lista.
 */
public class GerenciarListaView extends JDialog {

    private final ListaReproducaoController listaCtrl;
    private final VideoController           videoCtrl;
    private final ListaReproducao           lista;

    private DefaultListModel<Video> modeloNaLista;
    private DefaultListModel<Video> modeloBusca;
    private JList<Video>            listaNaLista;
    private JList<Video>            listaBusca;
    private JTextField              campoBusca;

    public GerenciarListaView(Frame owner, ListaReproducao lista,
                              ListaReproducaoController listaCtrl,
                              VideoController videoCtrl) {
        super(owner, "Gerenciar: " + lista.getNome(), true);
        this.lista     = lista;
        this.listaCtrl = listaCtrl;
        this.videoCtrl = videoCtrl;
        configurarJanela();
        listaCtrl.carregarVideos(lista);
        add(construirPainel());
        setVisible(true);
    }

    // ── Configuração ──────────────────────────────────────────────────────────

    private void configurarJanela() {
        setSize(700, 560);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        getContentPane().setBackground(new Color(25, 25, 25));
    }

    // ── Painel principal ──────────────────────────────────────────────────────

    private JPanel construirPainel() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 10, 0));
        painel.setBackground(new Color(25, 25, 25));
        painel.setBorder(new EmptyBorder(15, 15, 15, 15));
        painel.add(construirPainelEsquerdo());
        painel.add(construirPainelDireito());
        return painel;
    }

    // ── Lado esquerdo: vídeos na lista ────────────────────────────────────────

    private JPanel construirPainelEsquerdo() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(new Color(25, 25, 25));
        aplicarTitulo(p, "Vídeos na lista");

        modeloNaLista = new DefaultListModel<>();
        for (Video v : lista.getVideos()) modeloNaLista.addElement(v);

        listaNaLista = criarJList(modeloNaLista);
        p.add(new JScrollPane(listaNaLista), BorderLayout.CENTER);

        JButton btnRemover = criarBotao("Remover Selecionado", new Color(180, 0, 0));
        btnRemover.addActionListener(e -> removerVideo());
        p.add(btnRemover, BorderLayout.SOUTH);

        return p;
    }

    // ── Lado direito: buscar e adicionar vídeos ───────────────────────────────

    private JPanel construirPainelDireito() {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(new Color(25, 25, 25));
        aplicarTitulo(p, "Adicionar vídeo");

        // Campo de busca
        JPanel painelBusca = new JPanel(new BorderLayout(5, 0));
        painelBusca.setBackground(new Color(25, 25, 25));
        campoBusca = criarTextField("Buscar por título...");
        JButton btnBuscar = criarBotao("Buscar", new Color(60, 60, 60));
        btnBuscar.addActionListener(e -> buscarVideos());
        campoBusca.addActionListener(e -> buscarVideos());
        painelBusca.add(campoBusca, BorderLayout.CENTER);
        painelBusca.add(btnBuscar, BorderLayout.EAST);

        modeloBusca = new DefaultListModel<>();
        listaBusca  = criarJList(modeloBusca);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setBackground(new Color(25, 25, 25));
        centro.add(painelBusca, BorderLayout.NORTH);
        centro.add(new JScrollPane(listaBusca), BorderLayout.CENTER);
        p.add(centro, BorderLayout.CENTER);

        JButton btnAdicionar = criarBotao("Adicionar Selecionado", new Color(40, 100, 180));
        btnAdicionar.addActionListener(e -> adicionarVideo());
        p.add(btnAdicionar, BorderLayout.SOUTH);

        // Carrega todos os vídeos inicialmente
        carregarTodosVideos();

        return p;
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void removerVideo() {
        Video sel = listaNaLista.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecione um vídeo para remover.");
            return;
        }
        try {
            listaCtrl.removerVideo(lista, sel);
            modeloNaLista.removeElement(sel);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarVideos() {
        String termo = campoBusca.getText().trim();
        List<Video> resultados = videoCtrl.buscar(termo);
        modeloBusca.clear();
        for (Video v : resultados) modeloBusca.addElement(v);
        if (resultados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum vídeo encontrado.");
        }
    }

    private void carregarTodosVideos() {
        List<Video> todos = videoCtrl.listarTodos();
        modeloBusca.clear();
        for (Video v : todos) modeloBusca.addElement(v);
    }

    private void adicionarVideo() {
        Video sel = listaBusca.getSelectedValue();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Selecione um vídeo para adicionar.");
            return;
        }
        // Verifica se já está na lista
        for (int i = 0; i < modeloNaLista.size(); i++) {
            if (modeloNaLista.get(i).getId() == sel.getId()) {
                JOptionPane.showMessageDialog(this, "Este vídeo já está na lista.");
                return;
            }
        }
        try {
            listaCtrl.adicionarVideo(lista, sel);
            modeloNaLista.addElement(sel);
            JOptionPane.showMessageDialog(this,
                "\"" + sel.getTitulo() + "\" adicionado à lista.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Componentes estilizados ────────────────────────────────────────────────

    private JList<Video> criarJList(DefaultListModel<Video> modelo) {
        JList<Video> lista = new JList<>(modelo);
        lista.setBackground(new Color(35, 35, 35));
        lista.setForeground(Color.WHITE);
        lista.setFont(new Font("Arial", Font.PLAIN, 13));
        lista.setSelectionBackground(new Color(229, 9, 20));
        lista.setSelectionForeground(Color.WHITE);
        lista.setCellRenderer(new VideoListRenderer());
        return lista;
    }

    private JTextField criarTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setBackground(new Color(40, 40, 40));
        tf.setForeground(Color.LIGHT_GRAY);
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        tf.setFont(new Font("Arial", Font.PLAIN, 13));
        return tf;
    }

    private JButton criarBotao(String texto, Color cor) {
        JButton b = new JButton(texto);
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(0, 34));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void aplicarTitulo(JPanel p, String titulo) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)), titulo);
        border.setTitleColor(Color.LIGHT_GRAY);
        border.setTitleFont(new Font("Arial", Font.BOLD, 12));
        p.setBorder(BorderFactory.createCompoundBorder(border, new EmptyBorder(8, 8, 8, 8)));
    }

    // ── Renderer personalizado ────────────────────────────────────────────────

    private static class VideoListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Video v) {
                String tipo = "FILME".equals(v.getTipo()) ? "[F]" : "[S]";
                setText(tipo + " " + v.getTitulo() + " (" + v.getAnoLancamento() + ")");
            }
            setBackground(isSelected ? new Color(229, 9, 20) : new Color(35, 35, 35));
            setForeground(Color.WHITE);
            setBorder(new EmptyBorder(4, 8, 4, 8));
            return this;
        }
    }
}
