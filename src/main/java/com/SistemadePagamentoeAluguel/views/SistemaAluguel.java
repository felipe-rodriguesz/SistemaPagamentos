package main.java.com.SistemadePagamentoeAluguel.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

public class SistemaAluguel extends JFrame {
    private AdministradorView adminView;

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
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        String[] botoes = {"Fazer Reserva", "Alugar Item", "Renovar Aluguel", "Cancelar Aluguel", "Consultar Histórico"};
        
        for (String titulo : botoes) {
            JButton btn = new JButton(titulo);
            btn.addActionListener(this::handleClienteAction);
            panel.add(btn);
        }
        return panel;
    }

    private JPanel criarPainelAdministrador() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
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
            abrirPainelCliente();
            break;
        default:
            JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento: " + comando);
    }
}

private void abrirPainelCliente() {
    ClienteView clienteView = new ClienteView(null, null, null);
    clienteView.exibirPainelCliente();
}

    private void handleAdminAction(ActionEvent e) {
        String comando = ((JButton) e.getSource()).getText();
        
        switch (comando) {
            case "Gerar Relatórios":
                abrirPainelAdministrativo();
                break;
            case "Gerenciar Aluguéis":
            case "Gerenciar Pagamentos":
                JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento: " + comando);
                break;
        }
    }

    private void abrirPainelAdministrativo() {
        if (adminView == null) {
            adminView = new AdministradorView();
        }
        
        if (adminView.solicitarLogin()) {
            adminView.exibirPainelControle();
            adminView.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Acesso negado!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new SistemaAluguel().setVisible(true));
    }
}