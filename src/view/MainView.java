package view;

import controller.ListaReproducaoController;
import controller.VideoController;
import model.Usuario;
import model.Video;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Tela principal do FEItv. Exibe busca de vídeos, resultados e detalhes.
 */
public class MainView extends JFrame {

    private final VideoController           videoCtrl = new VideoController();
    private final ListaReproducaoController listaCtrl = new ListaReproducaoController();
    private final Usuario                   usuario;

    private JTextField       campoBusca;
    private DefaultTableModel modeloTabela;
    private JTable           tabela;
    private DetalhesVideoPanel painelDetalhes;
    private List<Video>      resultadoAtual;

    public MainView(Usuario usuario) {
        this.usuario = usuario;
        configurarJanela();
        add(construirHeader(), BorderLayout.NORTH);
        add(construirPainelPrincipal(), BorderLayout.CENTER);
        setVisible(true);
    }

    // ── Configuração da janela ─────────────────────────────────────────────────

    private void configurarJanela() {
        setTitle("FEItv");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(18, 18, 18));
    }

    // ── Header ────────────────────────────────────────────────────────────────

    private JPanel construirHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(18, 18, 18));
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(50, 50, 50)),
            new EmptyBorder(8, 14, 8, 14)
        ));

        // Logo
        JLabel logo = new JLabel("FEItv");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(new Color(229, 9, 20));
        header.add(logo, BorderLayout.WEST);

        // Lado direito: usuário + botões
        JPanel direito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        direito.setBackground(new Color(18, 18, 18));

        JLabel lblUser = new JLabel("Olá, " + usuario.getNome());
        lblUser.setForeground(Color.LIGHT_GRAY);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 13));
        direito.add(lblUser);

        JButton btnListas = criarBotaoMenu("▶ Minhas Listas");
        btnListas.addActionListener(e ->
            new ListasView(this, usuario, listaCtrl, videoCtrl));
        direito.add(btnListas);

        JButton btnSair = criarBotaoMenu("Sair");
        btnSair.setBackground(new Color(60, 60, 60));
        btnSair.addActionListener(e -> {
            dispose();
            new LoginView();
        });
        direito.add(btnSair);

        header.add(direito, BorderLayout.EAST);
        return header;
    }

    // ── Painel principal ──────────────────────────────────────────────────────

    private JSplitPane construirPainelPrincipal() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                construirPainelEsquerdo(), construirPainelDireito());
        split.setDividerLocation(580);
        split.setDividerSize(3);
        split.setBorder(null);
        split.setBackground(new Color(18, 18, 18));
        return split;
    }

    // ── Painel esquerdo: busca + tabela de resultados ─────────────────────────

    private JPanel construirPainelEsquerdo() {
        JPanel painel = new JPanel(new BorderLayout(0, 0));
        painel.setBackground(new Color(18, 18, 18));

        // Barra de busca
        painel.add(construirBarraBusca(), BorderLayout.NORTH);

        // Tabela de resultados
        String[] colunas = {"Título", "Tipo", "Ano", "Gênero", "♥ Curtidas"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(modeloTabela);
        estilizarTabela();
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) exibirDetalhesSelecionado();
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(50, 50, 50)));
        scroll.getViewport().setBackground(new Color(25, 25, 25));
        painel.add(scroll, BorderLayout.CENTER);

        // Rodapé
        JLabel hint = new JLabel("  Dica: dê duplo clique em uma linha para ver detalhes completos");
        hint.setForeground(new Color(100, 100, 100));
        hint.setFont(new Font("Arial", Font.ITALIC, 11));
        hint.setBorder(new EmptyBorder(4, 4, 4, 4));
        painel.add(hint, BorderLayout.SOUTH);

        tabela.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) exibirDetalhesSelecionado();
            }
        });

        return painel;
    }

    private JPanel construirBarraBusca() {
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setBackground(new Color(18, 18, 18));
        barra.setBorder(new EmptyBorder(12, 12, 10, 12));

        campoBusca = new JTextField();
        campoBusca.setBackground(new Color(38, 38, 38));
        campoBusca.setForeground(Color.WHITE);
        campoBusca.setCaretColor(Color.WHITE);
        campoBusca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 70, 70)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        campoBusca.setFont(new Font("Arial", Font.PLAIN, 14));
        campoBusca.addActionListener(e -> buscar());

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setBackground(new Color(229, 9, 20));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 13));
        btnBuscar.setPreferredSize(new Dimension(90, 36));
        btnBuscar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscar());

        barra.add(campoBusca, BorderLayout.CENTER);
        barra.add(btnBuscar, BorderLayout.EAST);

        return barra;
    }

    // ── Painel direito: detalhes do vídeo ─────────────────────────────────────

    private JPanel construirPainelDireito() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(25, 25, 25));
        wrapper.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(50, 50, 50)));

        painelDetalhes = new DetalhesVideoPanel(usuario, videoCtrl, listaCtrl);
        wrapper.add(painelDetalhes, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void buscar() {
        String termo = campoBusca.getText().trim();
        try {
            resultadoAtual = videoCtrl.buscar(termo);
            atualizarTabela(resultadoAtual);
            if (resultadoAtual.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    termo.isEmpty()
                        ? "Nenhum vídeo cadastrado no sistema."
                        : "Nenhum vídeo encontrado para: \"" + termo + "\".",
                    "Sem resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarTabela(List<Video> videos) {
        modeloTabela.setRowCount(0);
        for (Video v : videos) {
            String tipo = "FILME".equals(v.getTipo()) ? "Filme" : "Série";
            modeloTabela.addRow(new Object[]{
                v.getTitulo(), tipo, v.getAnoLancamento(), v.getGenero(), v.getCurtidas()
            });
        }
    }

    private void exibirDetalhesSelecionado() {
        int row = tabela.getSelectedRow();
        if (row < 0 || resultadoAtual == null || row >= resultadoAtual.size()) return;
        Video v = resultadoAtual.get(row);
        painelDetalhes.exibir(v);
    }

    // ── Componentes ───────────────────────────────────────────────────────────

    private void estilizarTabela() {
        tabela.setBackground(new Color(25, 25, 25));
        tabela.setForeground(Color.WHITE);
        tabela.setGridColor(new Color(45, 45, 45));
        tabela.setFont(new Font("Arial", Font.PLAIN, 13));
        tabela.setRowHeight(28);
        tabela.setShowVerticalLines(false);
        tabela.setSelectionBackground(new Color(229, 9, 20, 180));
        tabela.setSelectionForeground(Color.WHITE);
        tabela.setFillsViewportHeight(true);

        // Cabeçalho
        tabela.getTableHeader().setBackground(new Color(35, 35, 35));
        tabela.getTableHeader().setForeground(Color.LIGHT_GRAY);
        tabela.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabela.getTableHeader().setBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)));

        // Larguras das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(240); // Título
        tabela.getColumnModel().getColumn(1).setPreferredWidth(60);  // Tipo
        tabela.getColumnModel().getColumn(2).setPreferredWidth(55);  // Ano
        tabela.getColumnModel().getColumn(3).setPreferredWidth(120); // Gênero
        tabela.getColumnModel().getColumn(4).setPreferredWidth(80);  // Curtidas

        // Renderer para coluna Tipo com cor
        tabela.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? new Color(229, 9, 20, 180) : new Color(25, 25, 25));
                setForeground("Filme".equals(v) ? new Color(255, 80, 80) : new Color(80, 160, 255));
                setFont(new Font("Arial", Font.BOLD, 11));
                return this;
            }
        });

        // Renderer para Curtidas centralizado
        tabela.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                setHorizontalAlignment(CENTER);
                setBackground(sel ? new Color(229, 9, 20, 180) : new Color(25, 25, 25));
                setForeground(new Color(229, 9, 20));
                return this;
            }
        });

        // Renderer padrão para demais colunas (fundo alternado)
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (sel) {
                    setBackground(new Color(229, 9, 20, 180));
                } else {
                    setBackground(row % 2 == 0 ? new Color(25, 25, 25) : new Color(30, 30, 30));
                }
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return this;
            }
        };
        tabela.getColumnModel().getColumn(0).setCellRenderer(renderer);
        tabela.getColumnModel().getColumn(2).setCellRenderer(renderer);
        tabela.getColumnModel().getColumn(3).setCellRenderer(renderer);
    }

    private JButton criarBotaoMenu(String texto) {
        JButton b = new JButton(texto);
        b.setBackground(new Color(229, 9, 20));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(140, 28));
        return b;
    }
}
