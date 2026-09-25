package com.sistemadepagamentoealuguel.views;

import com.sistemadepagamentoealuguel.controllers.*;
import com.sistemadepagamentoealuguel.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdministradorView extends JFrame {
    private final CadastroController cadastroController;
    private final PagamentoController pagamentoController;
    private final AluguelController aluguelController;
    private final RelatorioController relatorioController;
    private JTabbedPane tabbedPane;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AdministradorView(CadastroController cadastroController,
            PagamentoController pagamentoController, AluguelController aluguelController,
            RelatorioController relatorioController) {
        this.cadastroController = cadastroController;
        this.pagamentoController = pagamentoController;
        this.aluguelController = aluguelController;
        this.relatorioController = relatorioController;
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

    public boolean solicitarLogin() {
        int resultado = JOptionPane.showConfirmDialog(
            this,
            "Este protótipo acadêmico local não possui autenticação configurada. Deseja abrir o painel administrativo?",
            "Acesso ao Protótipo",
            JOptionPane.OK_CANCEL_OPTION
        );
        return resultado == JOptionPane.OK_OPTION;
    }

    // Painel de Aluguéis
    private JPanel criarPainelAlugueis() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabela
        String[] colunas = {"ID", "Cliente", "Item", "Início", "Fim", "Status"};
        DefaultTableModel modelo = criarModeloSomenteLeitura(colunas);
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
        List<Cliente> clientes = cadastroController.listarClientes();
        List<Item> itens = cadastroController.listarItens();
        if (clientes.isEmpty() || itens.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre clientes e itens antes de criar um aluguel.");
            return;
        }

        JComboBox<Cliente> comboClientes = new JComboBox<>(clientes.toArray(Cliente[]::new));
        JComboBox<Item> comboItens = new JComboBox<>(itens.toArray(Item[]::new));
        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 5));
        painel.add(new JLabel("Cliente:"));
        painel.add(comboClientes);
        painel.add(new JLabel("Item:"));
        painel.add(comboItens);

        if (JOptionPane.showConfirmDialog(this, painel, "Novo Aluguel",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        Cliente cliente = (Cliente) comboClientes.getSelectedItem();
        Item item = (Item) comboItens.getSelectedItem();
        Optional<Aluguel> aluguel = aluguelController.criarAluguel(
            cliente, item, LocalDate.now(), LocalDate.now().plusDays(7));
        if (aluguel.isPresent()) {
            atualizarTabelaAlugueis(modelo);
        } else {
            JOptionPane.showMessageDialog(this,
                "O item não está disponível ou está reservado para outro cliente.");
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
        String[] colunas = {"ID", "Cliente", "Valor", "Método", "Status", "Data"};
        DefaultTableModel modelo = criarModeloSomenteLeitura(colunas);
        JTable tabela = new JTable(modelo);
        atualizarTabelaPagamentos(modelo);

        // Botões
        JButton btnRegistrar = new JButton("Registrar Pagamento");
        JButton btnProcessar = new JButton("Processar");
        JButton btnEstornar = new JButton("Estornar");
        btnRegistrar.addActionListener(e -> abrirDialogoPagamento(modelo));
        btnProcessar.addActionListener(e -> processarPagamento(tabela, modelo));
        btnEstornar.addActionListener(e -> estornarPagamento(tabela, modelo));

        JPanel painelBotoes = new JPanel();
        painelBotoes.add(btnRegistrar);
        painelBotoes.add(btnProcessar);
        painelBotoes.add(btnEstornar);

        panel.add(new JScrollPane(tabela), BorderLayout.CENTER);
        panel.add(painelBotoes, BorderLayout.SOUTH);
        return panel;
    }

    private void atualizarTabelaPagamentos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        pagamentoController.listarPagamentos().forEach(pagamento -> 
            modelo.addRow(new Object[]{
                pagamento.getId(),
                pagamento.getCliente().getNome(),
                "R$ " + pagamento.getValor().toPlainString(),
                pagamento.getMetodo(),
                pagamento.getStatus(),
                pagamento.getData().format(formatter)
            })
        );
    }

    private DefaultTableModel criarModeloSomenteLeitura(String[] colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void abrirDialogoPagamento(DefaultTableModel modelo) {
        List<Cliente> clientes = cadastroController.listarClientes();
        if (clientes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cadastre um cliente antes de registrar pagamentos.");
            return;
        }
        JTextField campoValor = new JTextField();
        JComboBox<Cliente> comboClientes = new JComboBox<>(clientes.toArray(Cliente[]::new));
        JComboBox<Pagamento.MetodoPagamento> comboMetodos =
            new JComboBox<>(Pagamento.MetodoPagamento.values());
        JPanel painel = new JPanel(new GridLayout(3, 2, 5, 5));
        painel.add(new JLabel("Cliente:"));
        painel.add(comboClientes);
        painel.add(new JLabel("Valor:"));
        painel.add(campoValor);
        painel.add(new JLabel("Método:"));
        painel.add(comboMetodos);

        if (JOptionPane.showConfirmDialog(this, painel, "Registrar Pagamento",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            BigDecimal valor = new BigDecimal(campoValor.getText().trim().replace(',', '.'));
            pagamentoController.registrarPagamento(
                (Cliente) comboClientes.getSelectedItem(), valor,
                (Pagamento.MetodoPagamento) comboMetodos.getSelectedItem());
            atualizarTabelaPagamentos(modelo);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Informe um valor numérico positivo válido.");
        }
    }

    private void processarPagamento(JTable tabela, DefaultTableModel modelo) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento.");
            return;
        }
        int pagamentoId = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
        if (pagamentoController.processarPagamento(pagamentoId)) {
            atualizarTabelaPagamentos(modelo);
        } else {
            JOptionPane.showMessageDialog(this, "Pagamento não encontrado ou não está pendente.");
        }
    }

    private void estornarPagamento(JTable tabela, DefaultTableModel modelo) {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento.");
            return;
        }
        int pagamentoId = (int) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0);
        if (pagamentoController.estornarPagamento(pagamentoId)) {
            atualizarTabelaPagamentos(modelo);
        } else {
            JOptionPane.showMessageDialog(this, "Pagamento não encontrado ou não pode ser estornado.");
        }
    }

    // Painel de Relatórios
    private JPanel criarPainelRelatorios() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea areaRelatorio = new JTextArea();
        JButton btnGerar = new JButton("Gerar Relatório");
        
        btnGerar.addActionListener(e -> {
            DateRange periodo = obterPeriodo();
            if (periodo == null) return;
            Relatorio relatorioAlugueis = relatorioController.gerarRelatorioAlugueis(periodo);
            Relatorio relatorioPagamentos = relatorioController.gerarRelatorioPagamentos(periodo);
            
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
        JSpinner inicioSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner fimSpinner = new JSpinner(new SpinnerDateModel());
        inicioSpinner.setEditor(new JSpinner.DateEditor(inicioSpinner, "dd/MM/yyyy"));
        fimSpinner.setEditor(new JSpinner.DateEditor(fimSpinner, "dd/MM/yyyy"));

        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 5));
        painel.add(new JLabel("Data inicial:"));
        painel.add(inicioSpinner);
        painel.add(new JLabel("Data final:"));
        painel.add(fimSpinner);
        if (JOptionPane.showConfirmDialog(this, painel, "Selecionar Período",
                JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION) {
            return null;
        }
        
        LocalDate inicio = ((java.util.Date) inicioSpinner.getValue()).toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        LocalDate fim = ((java.util.Date) fimSpinner.getValue()).toInstant()
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        
        try {
            return new DateRange(inicio, fim);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "A data inicial deve ser anterior ou igual à data final.");
            return null;
        }
    }

    private String formatarRelatorio(Relatorio relatorio) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== ").append(relatorio.getTipo().toUpperCase()).append(" ===\n");
        sb.append("Período: ").append(relatorio.getPeriodo().getInicio().format(formatter))
            .append(" a ").append(relatorio.getPeriodo().getFim().format(formatter)).append("\n");
        sb.append("Total de Registros: ").append(relatorio.getQuantidadeRegistros()).append("\n\n");
        relatorio.getDados().forEach(linha -> sb.append(linha).append("\n"));
        return sb.toString();
    }

    public void exibirPainelControle() {
        setVisible(true);
    }
}
