package main.java.com.SistemadePagamentoeAluguel.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import main.java.com.SistemadePagamentoeAluguel.controllers.AluguelController;
import main.java.com.SistemadePagamentoeAluguel.controllers.PagamentoController;
import main.java.com.SistemadePagamentoeAluguel.controllers.ReservaController;
import main.java.com.SistemadePagamentoeAluguel.models.Cliente;

public class SistemaAluguel extends JFrame {
    // Controladores e dados do cliente
    private final ReservaController reservaController = new ReservaController();
    private final AluguelController aluguelController = new AluguelController();
    private final Cliente clientePadrao = new Cliente(1, "cliente_demo", "Cliente Demo");
    private final PagamentoController pagamentoController = new PagamentoController();

    public SistemaAluguel() {
        configurarJanela();
        exibirInterface();
    }

    private void configurarJanela() {
        setTitle("Sistema de Aluguel");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void exibirInterface() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Cliente", criarPainelCliente());
        tabbedPane.addTab("Administrador", criarPainelAdministrador());
        add(tabbedPane);
    }

    // Interface do Cliente (totalmente preservada)
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

    // Painel Administrador simplificado (apenas abre a view dedicada)
    private JPanel criarPainelAdministrador() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JButton btnAbrirAdmin = new JButton("Abrir Painel Administrativo");
        btnAbrirAdmin.addActionListener(e -> abrirAdministradorView());
        
        panel.add(btnAbrirAdmin);
        return panel;
    }

    private void handleClienteAction(ActionEvent e) {
        String comando = ((JButton) e.getSource()).getText();
        abrirPainelCliente();
    }

    private void abrirPainelCliente() {
        ClienteView clienteView = new ClienteView(clientePadrao, reservaController, aluguelController);
        clienteView.exibirPainelCliente();
    }

    private void abrirAdministradorView() {
        AdministradorView adminView = new AdministradorView(pagamentoController, aluguelController);
        if (adminView.solicitarLogin()) {
            adminView.exibirPainelControle(); 
        } else {
            JOptionPane.showMessageDialog(this, "Acesso negado!");
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new SistemaAluguel().setVisible(true));
    }
}