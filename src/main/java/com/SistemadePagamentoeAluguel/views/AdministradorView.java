package main.java.com.SistemadePagamentoeAluguel.views;

import main.java.com.SistemadePagamentoeAluguel.controllers.*;
import main.java.com.SistemadePagamentoeAluguel.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdministradorView extends JFrame {
    private final PagamentoController pagamentoController;
    private final AluguelController aluguelController;
    private JTabbedPane tabbedPane;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AdministradorView(PagamentoController pagamentoController, AluguelController aluguelController) {
        this.pagamentoController = pagamentoController;
        this.aluguelController = aluguelController;
        configurarJanela();
        inicializarUI();
    }

    private void configurarJanela() {
        setTitle("Painel Administrativo");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Aluguéis", criarPainelAlugueis());
        tabbedPane.addTab("Pagamentos", criarPainelPagamentos());
        tabbedPane.addTab("Relatórios", criarPainelRelatorios());
        add(tabbedPane);
    }

    // Método de Login
    public boolean solicitarLogin() {
        JPanel painelLogin = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField txtUsuario = new JTextField();
        JPasswordField txtSenha = new JPasswordField();

        painelLogin.add(new JLabel("Usuário:"));
        painelLogin.add(txtUsuario);
        painelLogin.add(new JLabel("Senha:"));
        painelLogin.add(txtSenha);

        int resultado = JOptionPane.showConfirmDialog(
            this,
            painelLogin,
            "Login Administrativo",
            JOptionPane.OK_CANCEL_OPTION
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String usuario = txtUsuario.getText();
            String senha = new String(txtSenha.getPassword());
            return validarCredenciais(usuario, senha);
        }
        return false;
    }

    private boolean validarCredenciais(String usuario, String senha) {
        // Credenciais mockadas (substituir por validação real)
        return "admin".equals(usuario) && "admin123".equals(senha);
    }

    // Painel de Aluguéis
    private JPanel criarPainelAlugueis() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabela
        String[] colunas = {"ID", "Cliente", "Item", "Início", "Fim", "Status"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        atualizarTabelaAlugueis(modelo);

        // Botões
        JButton btnNovo = new JButton("Novo Aluguel");
        JButton btnCancelar = new JButton("Cancelar");
        btnNovo.addActionListener(e -> abrirDialogoNovoAluguel(modelo));
        btnCancelar.addActionListener(e -> cancelarAluguel(tabela, modelo));

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnNovo);
        painelBotoes.add(btnCancelar);

        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        panel.add(painelBotoes, BorderLayout.SOUTH);
        return panel;
    }

    private void atualizarTabelaAlugueis(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        aluguelController.listarAlugueis().forEach(aluguel -> 
            modelo.addRow(new Object[]{
                aluguel.getId(),
                aluguel.getCliente().getNome(),
                aluguel.getItem().getTitulo(),
                aluguel.getDataInicio().format(formatter),
                aluguel.getDataFim().format(formatter),
                aluguel.getStatus()
            })
        );
    }

    private void abrirDialogoNovoAluguel(DefaultTableModel modelo) {
        JTextField campoNome = new JTextField();
        JTextField campoEmail = new JTextField();
        JTextField campoIdItem = new JTextField();
        JTextField campoTituloItem = new JTextField();
        JPanel painel = new JPanel(new GridLayout(4, 2, 5, 5));
        painel.add(new JLabel("Nome do cliente:"));
        painel.add(campoNome);
        painel.add(new JLabel("E-mail do cliente:"));
        painel.add(campoEmail);
        painel.add(new JLabel("ID do item:"));
        painel.add(campoIdItem);
        painel.add(new JLabel("Título do item:"));
        painel.add(campoTituloItem);

        if (JOptionPane.showConfirmDialog(this, painel, "Novo Aluguel",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int itemId = Integer.parseInt(campoIdItem.getText().trim());
            Cliente cliente = new Cliente(1, campoNome.getText().trim(), campoEmail.getText().trim());
            Item item = new Item(itemId, campoTituloItem.getText().trim(), Item.TipoItem.OUTROS);
            Optional<Aluguel> aluguel = aluguelController.criarAluguel(
                cliente, item, LocalDate.now(), LocalDate.now().plusDays(7));
            if (aluguel.isPresent()) {
                atualizarTabelaAlugueis(modelo);
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível criar o aluguel.");
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Informe um ID de item válido.");
        }
    }

    private void cancelarAluguel(JTable tabela, DefaultTableModel modelo) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um aluguel.");
            return;
        }
        int aluguelId = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
        if (aluguelController.cancelarAluguel(aluguelId)) {
            atualizarTabelaAlugueis(modelo);
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível cancelar o aluguel.");
        }
    }

    // Painel de Pagamentos
    private JPanel criarPainelPagamentos() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabela
        String[] colunas = {"ID", "Valor", "Método", "Status", "Data"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        JTable tabela = new JTable(modelo);
        atualizarTabelaPagamentos(modelo);

        // Botões
        JButton btnRegistrar = new JButton("Registrar Pagamento");
        JButton btnProcessar = new JButton("Processar");
        btnRegistrar.addActionListener(e -> abrirDialogoPagamento(modelo));
        btnProcessar.addActionListener(e -> processarPagamento(tabela, modelo));

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnRegistrar);
        painelBotoes.add(btnProcessar);

        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        panel.add(painelBotoes, BorderLayout.SOUTH);
        return panel;
    }

    private void atualizarTabelaPagamentos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        pagamentoController.listarPagamentos().forEach(pagamento -> 
            modelo.addRow(new Object[]{
                pagamento.getId(),
                String.format("R$ %.2f", pagamento.getValor()),
                pagamento.getMetodo(),
                pagamento.getStatus(),
                pagamento.getData().format(formatter)
            })
        );
    }

    private void abrirDialogoPagamento(DefaultTableModel modelo) {
        JTextField campoValor = new JTextField();
        JTextField campoMetodo = new JTextField();
        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 5));
        painel.add(new JLabel("Valor:"));
        painel.add(campoValor);
        painel.add(new JLabel("Método (Cartão de Crédito, Boleto, Pix ou Dinheiro):"));
        painel.add(campoMetodo);

        if (JOptionPane.showConfirmDialog(this, painel, "Registrar Pagamento",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            Pagamento pagamento = new Pagamento(
                Double.parseDouble(campoValor.getText().trim()), campoMetodo.getText().trim());
            pagamentoController.registrarPagamento(pagamento);
            atualizarTabelaPagamentos(modelo);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Informe um valor positivo válido.");
        }
    }

    private void processarPagamento(JTable tabela, DefaultTableModel modelo) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento.");
            return;
        }
        int pagamentoId = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
        pagamentoController.listarPagamentos().stream()
            .filter(pagamento -> pagamento.getId() == pagamentoId)
            .findFirst()
            .ifPresentOrElse(pagamento -> {
                try {
                    pagamentoController.processarPagamento(pagamento);
                    atualizarTabelaPagamentos(modelo);
                } catch (IllegalStateException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                }
            }, () -> JOptionPane.showMessageDialog(this, "Pagamento não encontrado."));
    }

    // Painel de Relatórios
    private JPanel criarPainelRelatorios() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea areaRelatorio = new JTextArea();
        JButton btnGerar = new JButton("Gerar Relatório");
        
        btnGerar.addActionListener(e -> {
            DateRange periodo = obterPeriodo();
            Relatorio relatorioAlugueis = aluguelController.gerarRelatorio("alugueis", periodo);
            Relatorio relatorioPagamentos = pagamentoController.gerarRelatorio("pagamentos", periodo);
            
            areaRelatorio.setText(
                formatarRelatorio(relatorioAlugueis) + 
                "\n\n" + 
                formatarRelatorio(relatorioPagamentos)
            );
        });

        panel.add(new JScrollPane(areaRelatorio), BorderLayout.CENTER);
        panel.add(btnGerar, BorderLayout.SOUTH);
        return panel;
    }

    // Métodos auxiliares
    private DateRange obterPeriodo() {
        JDialog dialog = new JDialog(this, "Selecionar Período", true);
        dialog.setLayout(new GridLayout(3, 2));
        
        JSpinner inicioSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner fimSpinner = new JSpinner(new SpinnerDateModel());
        inicioSpinner.setEditor(new JSpinner.DateEditor(inicioSpinner, "dd/MM/yyyy"));
        fimSpinner.setEditor(new JSpinner.DateEditor(fimSpinner, "dd/MM/yyyy"));
        
        dialog.add(new JLabel("Data Início:"));
        dialog.add(inicioSpinner);
        dialog.add(new JLabel("Data Fim:"));
        dialog.add(fimSpinner);
        
        JButton btnConfirmar = new JButton("Confirmar");
        btnConfirmar.addActionListener(e -> dialog.dispose());
        dialog.add(btnConfirmar);
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        LocalDate inicio = ((java.util.Date) inicioSpinner.getValue()).toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate fim = ((java.util.Date) fimSpinner.getValue()).toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        
        return new DateRange(inicio, fim);
    }

    private String formatarRelatorio(Relatorio relatorio) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(relatorio.getTipo().toUpperCase()).append(" ===\n");
        sb.append("Período: ").append(relatorio.getDataGeracao().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter)).append("\n");
        sb.append("Total de Registros: ").append(relatorio.getDados().size()).append("\n\n");
        relatorio.getDados().forEach(linha -> sb.append(linha).append("\n"));
        return sb.toString();
    }

    public void exibirPainelControle() {
        setVisible(true);
    }
}
