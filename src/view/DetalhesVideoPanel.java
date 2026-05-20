package view;

import controller.ListaReproducaoController;
import controller.VideoController;
import model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Painel lateral que exibe os detalhes do vídeo selecionado.
 */
public class DetalhesVideoPanel extends JPanel {

    private final VideoController          videoCtrl;
    private final ListaReproducaoController listaCtrl;
    private final Usuario                  usuario;

    private Video videoAtual;

    // Componentes
    private JLabel lblTitulo;
    private JLabel lblTipo;
    private JLabel lblGenero;
    private JLabel lblAno;
    private JLabel lblExtra1;   // Diretor / Temporadas
    private JLabel lblExtra2;   // Duração  / Episódios
    private JLabel lblSituacao; // Apenas séries
    private JLabel lblCurtidas;
    private JButton btnCurtir;
    private JButton btnAddLista;
    private JPanel painelInfo;

    public DetalhesVideoPanel(Usuario usuario, VideoController videoCtrl,
                              ListaReproducaoController listaCtrl) {
        this.usuario   = usuario;
        this.videoCtrl = videoCtrl;
        this.listaCtrl = listaCtrl;
        construir();
    }

    // ── Construção ────────────────────────────────────────────────────────────

    private void construir() {
        setLayout(new BorderLayout());
        setBackground(new Color(25, 25, 25));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Estado inicial: nenhum vídeo selecionado
        JLabel vazio = new JLabel("<html><center>Selecione um vídeo<br>para ver os detalhes</center></html>",
                SwingConstants.CENTER);
        vazio.setForeground(new Color(120, 120, 120));
        vazio.setFont(new Font("Arial", Font.ITALIC, 14));
        vazio.setName("vazio");
        add(vazio, BorderLayout.CENTER);
    }

    /** Exibe os detalhes do vídeo selecionado. */
    public void exibir(Video video) {
        this.videoAtual = video;
        removeAll();

        painelInfo = new JPanel();
        painelInfo.setLayout(new BoxLayout(painelInfo, BoxLayout.Y_AXIS));
        painelInfo.setBackground(new Color(25, 25, 25));

        // Tipo chip
        JPanel chipPainel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chipPainel.setBackground(new Color(25, 25, 25));
        String tipoTexto = "FILME".equals(video.getTipo()) ? "  FILME  " : "  SÉRIE  ";
        Color  tipoCor   = "FILME".equals(video.getTipo()) ? new Color(229, 9, 20) : new Color(20, 130, 229);
        JLabel chip = new JLabel(tipoTexto);
        chip.setFont(new Font("Arial", Font.BOLD, 11));
        chip.setForeground(Color.WHITE);
        chip.setBackground(tipoCor);
        chip.setOpaque(true);
        chip.setBorder(new EmptyBorder(3, 6, 3, 6));
        chipPainel.add(chip);
        painelInfo.add(chipPainel);

        painelInfo.add(Box.createVerticalStrut(10));

        // Título
        lblTitulo = new JLabel("<html><b>" + video.getTitulo() + "</b></html>");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        painelInfo.add(lblTitulo);

        painelInfo.add(Box.createVerticalStrut(14));

        // Informações gerais
        painelInfo.add(criarLinha("Gênero", video.getGenero()));
        painelInfo.add(Box.createVerticalStrut(6));
        painelInfo.add(criarLinha("Ano", String.valueOf(video.getAnoLancamento())));
        painelInfo.add(Box.createVerticalStrut(6));

        // Informações específicas
        if (video instanceof Filme f) {
            painelInfo.add(criarLinha("Diretor", f.getDiretor()));
            painelInfo.add(Box.createVerticalStrut(6));
            painelInfo.add(criarLinha("Duração", f.getDuracao() + " min"));
        } else if (video instanceof Serie s) {
            painelInfo.add(criarLinha("Temporadas", String.valueOf(s.getTotalTemporadas())));
            painelInfo.add(Box.createVerticalStrut(6));
            painelInfo.add(criarLinha("Episódios", String.valueOf(s.getTotalEpisodios())));
            painelInfo.add(Box.createVerticalStrut(6));

            Situacao sit = s.getSituacao();
            if (sit != null) {
                Color corStatus = corDoStatus(sit.getNome());
                JPanel linha = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                linha.setBackground(new Color(25, 25, 25));
                JLabel key = criarLabelChave("Status");
                JLabel val = new JLabel(sit.getNome());
                val.setFont(new Font("Arial", Font.BOLD, 13));
                val.setForeground(corStatus);
                linha.add(key);
                linha.add(val);
                painelInfo.add(linha);
            }
        }

        painelInfo.add(Box.createVerticalStrut(14));

        // Curtidas
        int curtidas = videoCtrl.contarCurtidas(video.getId());
        video.setCurtidas(curtidas);
        boolean jaCurtiu = videoCtrl.usuarioCurtiu(usuario.getId(), video.getId());

        JPanel painelCurtida = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        painelCurtida.setBackground(new Color(25, 25, 25));
        lblCurtidas = new JLabel("♥ " + curtidas + " curtida(s)");
        lblCurtidas.setForeground(new Color(229, 9, 20));
        lblCurtidas.setFont(new Font("Arial", Font.BOLD, 13));
        painelCurtida.add(lblCurtidas);
        painelInfo.add(painelCurtida);

        painelInfo.add(Box.createVerticalStrut(16));

        // Botões
        btnCurtir = new JButton(jaCurtiu ? "♥ Descurtir" : "♡ Curtir");
        estilizarBotao(btnCurtir, jaCurtiu ? new Color(180, 0, 0) : new Color(229, 9, 20));
        btnCurtir.addActionListener(e -> alternarCurtida());
        painelInfo.add(btnCurtir);

        painelInfo.add(Box.createVerticalStrut(8));

        btnAddLista = new JButton("+ Adicionar à Lista");
        estilizarBotao(btnAddLista, new Color(40, 100, 180));
        btnAddLista.addActionListener(e -> adicionarALista());
        painelInfo.add(btnAddLista);

        add(painelInfo, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    // ── Ações ─────────────────────────────────────────────────────────────────

    private void alternarCurtida() {
        if (videoAtual == null) return;
        boolean curtiu = videoCtrl.alternarCurtida(usuario.getId(), videoAtual);
        int novasCurtidas = videoCtrl.contarCurtidas(videoAtual.getId());
        lblCurtidas.setText("♥ " + novasCurtidas + " curtida(s)");
        btnCurtir.setText(curtiu ? "♥ Descurtir" : "♡ Curtir");
        btnCurtir.setBackground(curtiu ? new Color(180, 0, 0) : new Color(229, 9, 20));
    }

    private void adicionarALista() {
        if (videoAtual == null) return;

        List<ListaReproducao> listas = listaCtrl.listar(usuario);
        if (listas.isEmpty()) {
            int resp = JOptionPane.showConfirmDialog(this,
                "Você não possui listas. Deseja criar uma agora?",
                "Nenhuma Lista", JOptionPane.YES_NO_OPTION);
            if (resp != JOptionPane.YES_OPTION) return;
            String nome = JOptionPane.showInputDialog(this, "Nome da nova lista:");
            if (nome == null || nome.isBlank()) return;
            try {
                ListaReproducao nova = listaCtrl.criar(nome, usuario);
                listaCtrl.adicionarVideo(nova, videoAtual);
                JOptionPane.showMessageDialog(this, "Vídeo adicionado à lista \"" + nome + "\".");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
            return;
        }

        // Mostra seleção de lista
        String[] nomes = listas.stream().map(ListaReproducao::getNome).toArray(String[]::new);
        String escolha = (String) JOptionPane.showInputDialog(
            this, "Selecione a lista:", "Adicionar à Lista",
            JOptionPane.PLAIN_MESSAGE, null, nomes, nomes[0]);
        if (escolha == null) return;

        ListaReproducao listaSel = listas.stream()
            .filter(l -> l.getNome().equals(escolha)).findFirst().orElse(null);
        if (listaSel == null) return;

        try {
            listaCtrl.adicionarVideo(listaSel, videoAtual);
            JOptionPane.showMessageDialog(this,
                "\"" + videoAtual.getTitulo() + "\" adicionado à lista \"" + escolha + "\".");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ── Auxiliares ────────────────────────────────────────────────────────────

    private JPanel criarLinha(String chave, String valor) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setBackground(new Color(25, 25, 25));
        p.add(criarLabelChave(chave));
        JLabel v = new JLabel(valor != null ? valor : "—");
        v.setFont(new Font("Arial", Font.PLAIN, 13));
        v.setForeground(Color.WHITE);
        p.add(v);
        return p;
    }

    private JLabel criarLabelChave(String chave) {
        JLabel l = new JLabel(chave + ":  ");
        l.setFont(new Font("Arial", Font.BOLD, 13));
        l.setForeground(new Color(160, 160, 160));
        return l;
    }

    private Color corDoStatus(String status) {
        if (status == null) return Color.GRAY;
        return switch (status) {
            case "Em Andamento" -> new Color(50, 200, 100);
            case "Finalizada"   -> new Color(150, 150, 150);
            case "Cancelada"    -> new Color(220, 80, 80);
            default             -> Color.WHITE;
        };
    }

    private void estilizarBotao(JButton b, Color cor) {
        b.setBackground(cor);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        b.setAlignmentX(LEFT_ALIGNMENT);
    }
}
