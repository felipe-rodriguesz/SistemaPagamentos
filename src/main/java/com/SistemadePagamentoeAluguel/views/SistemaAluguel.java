package main.java.com.SistemadePagamentoeAluguel.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import main.java.com.SistemadePagamentoeAluguel.controllers.AluguelController;
import main.java.com.SistemadePagamentoeAluguel.controllers.ReservaController;
import main.java.com.SistemadePagamentoeAluguel.models.Cliente;
import main.java.com.SistemadePagamentoeAluguel.models.DateRange;
import main.java.com.SistemadePagamentoeAluguel.models.Pagamento;
import main.java.com.SistemadePagamentoeAluguel.models.Relatorio;

public class SistemaAluguel extends JFrame {
    private AdministradorView adminView;
    private JDialog gerenciarDialog;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<Pagamento> pagamentos = new ArrayList<>(); // Lista temporária para demonstração
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Controladores necessários para ClienteView
    private ReservaController reservaController = new ReservaController();
    private AluguelController aluguelController = new AluguelController();
    
    // Cliente padrão (para demonstração)
    private final Cliente clientePadrao = new Cliente(1, "cliente_demo", "Cliente Demo");

    public SistemaAluguel() {
        setTitle("Sistema de Aluguel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Cliente", criarPainelCliente());
        tabbedPane.addTab("Administrador", criarPainelAdministrador());
        add(tabbedPane);
    }

    private JPanel criarPainelCliente() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] botoes = {
            "Fazer Reserva", 
            "Alugar Item", 
            "Renovar Aluguel", 
            "Consultar Histórico",
            "Efetuar Pagamento",
            "Cancelar Aluguel"
        };
        
        for (String titulo : botoes) {
            JButton btn = new JButton(titulo);
            btn.addActionListener(this::handleClienteAction);
            panel.add(btn);
        }
        return panel;
    }

    private JPanel criarPainelAdministrador() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        String[] botoes = {"Gerenciar Aluguéis", "Gerenciar Pagamentos", "Gerar Relatórios"};
        
        for (String titulo : botoes) {
            JButton btn = new JButton(titulo);
            btn.addActionListener(this::handleAdminAction);
            panel.add(btn);
        }
        return panel;
    }

    private void handleClienteAction(ActionEvent e) {
        String comando = ((JButton) e.getSource()).getText();
        
        switch (comando) {
            case "Fazer Reserva":
            case "Alugar Item":
            case "Renovar Aluguel":
            case "Cancelar Aluguel":
            case "Consultar Histórico":
            case "Efetuar Pagamento":
                abrirPainelCliente();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento: " + comando);
        }
    }

    private void abrirPainelCliente() {
        // Criando instância de ClienteView com o cliente padrão e os controladores
        ClienteView clienteView = new ClienteView(clientePadrao, reservaController, aluguelController);
        clienteView.exibirPainelCliente();
    }

    private void handleAdminAction(ActionEvent e) {
        String comando = ((JButton) e.getSource()).getText();
        
        switch (comando) {
            case "Gerar Relatórios":
                abrirPainelAdministrativo();
                break;
            case "Gerenciar Aluguéis":
                if (verificarLogin()) {
                    abrirGerenciamentoAlugueis();
                }
                break;
            case "Gerenciar Pagamentos":
                if (verificarLogin()) {
                    abrirGerenciamentoPagamentos();
                }
                break;
        }
    }

    private boolean verificarLogin() {
        if (adminView == null) {
            adminView = new AdministradorView();
        }
        return adminView.solicitarLogin();
    }

    private void abrirPainelAdministrativo() {
        if (adminView == null) {
            adminView = new AdministradorView() {
                @Override
                protected Relatorio gerarRelatorioPersonalizado(String tipo, DateRange periodo) {
                    if ("pagamentos".equals(tipo.toLowerCase())) {
                        return gerarRelatorioPagamentos(periodo);
                    }
                    return super.gerarRelatorioPersonalizado(tipo, periodo);
                }
            };
        }
        
        if (adminView.solicitarLogin()) {
            adminView.exibirPainelControle();
            adminView.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso negado!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Relatorio gerarRelatorioPagamentos(DateRange periodo) {
        // Filtrar pagamentos pelo período
        List<String> dadosRelatorio = new ArrayList<>();
        
        double totalProcessado = 0;
        double totalPendente = 0;
        double totalEstornado = 0;
        
        for (Pagamento pagamento : pagamentos) {
            // Adicionar detalhes de cada pagamento
            dadosRelatorio.add(String.format("ID: %d | Valor: R$ %.2f | Método: %s | Status: %s | Data: %s",
                pagamento.getId(),
                pagamento.getValor(),
                pagamento.getMetodo(),
                pagamento.getStatus(),
                pagamento.getData().format(formatter)));
            
            // Calcular totais por status
            switch (pagamento.getStatus()) {
                case PROCESSADO:
                    totalProcessado += pagamento.getValor();
                    break;
                case PENDENTE:
                    totalPendente += pagamento.getValor();
                    break;
                case ESTORNADO:
                    totalEstornado += pagamento.getValor();
                    break;
            }
        }
        
        // Adicionar resumo no início do relatório
        dadosRelatorio.add(0, "=== RESUMO ===");
        dadosRelatorio.add(1, String.format("Total Processado: R$ %.2f", totalProcessado));
        dadosRelatorio.add(2, String.format("Total Pendente: R$ %.2f", totalPendente));
        dadosRelatorio.add(3, String.format("Total Estornado: R$ %.2f", totalEstornado));
        dadosRelatorio.add(4, "");
        dadosRelatorio.add(5, "=== DETALHES DOS PAGAMENTOS ===");
        
        Relatorio relatorio = new Relatorio(0, "Pagamentos", dadosRelatorio, null);
        return relatorio;
    }

    private void abrirGerenciamentoPagamentos() {
        gerenciarDialog = new JDialog(this, "Gerenciar Pagamentos", true);
        gerenciarDialog.setSize(800, 500);
        gerenciarDialog.setLocationRelativeTo(this);

        // Configurar tabela
        String[] colunas = {"ID", "Valor", "Método", "Status", "Data"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabela);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnRegistrar = new JButton("Registrar Pagamento");
        JButton btnProcessar = new JButton("Processar");
        JButton btnEstornar = new JButton("Estornar");
        JButton btnAtualizar = new JButton("Atualizar");

        btnRegistrar.addActionListener(e -> registrarPagamento());
        btnProcessar.addActionListener(e -> processarPagamento());
        btnEstornar.addActionListener(e -> estornarPagamento());
        btnAtualizar.addActionListener(e -> carregarPagamentos());

        painelBotoes.add(btnRegistrar);
        painelBotoes.add(btnProcessar);
        painelBotoes.add(btnEstornar);
        painelBotoes.add(btnAtualizar);

        // Layout
        gerenciarDialog.setLayout(new BorderLayout());
        gerenciarDialog.add(painelBotoes, BorderLayout.NORTH);
        gerenciarDialog.add(scrollPane, BorderLayout.CENTER);

        carregarPagamentos();
        gerenciarDialog.setVisible(true);
    }

    private void carregarPagamentos() {
        modeloTabela.setRowCount(0);
        for (Pagamento pagamento : pagamentos) {
            modeloTabela.addRow(new Object[]{
                pagamento.getId(),
                String.format("R$ %.2f", pagamento.getValor()),
                pagamento.getMetodo(),
                pagamento.getStatus(),
                pagamento.getData().format(formatter)
            });
        }
    }

    private void registrarPagamento() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField valorField = new JTextField();
        JComboBox<String> metodoCombo = new JComboBox<>(new String[]{"PIX", "Cartão", "Boleto"});

        panel.add(new JLabel("Valor:"));
        panel.add(valorField);
        panel.add(new JLabel("Método:"));
        panel.add(metodoCombo);

        int result = JOptionPane.showConfirmDialog(
            gerenciarDialog, panel, "Registrar Pagamento",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double valor = Double.parseDouble(valorField.getText().replace(",", "."));
                String metodo = (String) metodoCombo.getSelectedItem();
                
                Pagamento novoPagamento = new Pagamento(valor, metodo);
                pagamentos.add(novoPagamento);
                carregarPagamentos();
                
                JOptionPane.showMessageDialog(gerenciarDialog,
                    "Pagamento registrado com sucesso!\nID: " + novoPagamento.getId());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(gerenciarDialog,
                    "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(gerenciarDialog,
                    e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void processarPagamento() {
        int selectedRow = tabela.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(gerenciarDialog, "Selecione um pagamento para processar");
            return;
        }

        Pagamento pagamento = pagamentos.get(selectedRow);
        try {
            pagamento.processar();
            carregarPagamentos();
            JOptionPane.showMessageDialog(gerenciarDialog, "Pagamento processado com sucesso!");
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(gerenciarDialog, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void estornarPagamento() {
        int selectedRow = tabela.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(gerenciarDialog, "Selecione um pagamento para estornar");
            return;
        }

        Pagamento pagamento = pagamentos.get(selectedRow);
        try {
            pagamento.estornar();
            carregarPagamentos();
            JOptionPane.showMessageDialog(gerenciarDialog, "Pagamento estornado com sucesso!");
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(gerenciarDialog, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirGerenciamentoAlugueis() {
        // Por enquanto mantemos a implementação básica
        JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento: Gerenciar Aluguéis");
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new SistemaAluguel().setVisible(true));
    }
}