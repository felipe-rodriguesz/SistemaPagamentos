package main.java.com.SistemadePagamentoeAluguel.views;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import main.java.com.SistemadePagamentoeAluguel.controllers.AluguelController;
import main.java.com.SistemadePagamentoeAluguel.controllers.PagamentoController;
import main.java.com.SistemadePagamentoeAluguel.controllers.ReservaController;
import main.java.com.SistemadePagamentoeAluguel.models.Cliente;
import main.java.com.SistemadePagamentoeAluguel.models.Item;

public class SistemaAluguel extends JFrame {
    // Controladores e dados do cliente
    private final ReservaController reservaController = new ReservaController();
    private final AluguelController aluguelController = new AluguelController();
    private final Cliente clientePadrao = new Cliente(1, "cliente_demo", "Cliente Demo");
    private final PagamentoController pagamentoController = new PagamentoController();
    private final List<Item> catalogo = new ArrayList<>(List.of(
        new Item(1, "Livro de POO", Item.TipoItem.LIVRO),
        new Item(2, "Projetor multimídia", Item.TipoItem.EQUIPAMENTO),
        new Item(3, "Mesa de estudo", Item.TipoItem.MOVEL)
    ));

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

    private JPanel criarPainelCliente() {
        JPanel panel = new JPanel(new GridLayout(1, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton btnAbrirCliente = new JButton("Abrir Painel do Cliente");
        btnAbrirCliente.addActionListener(e -> abrirPainelCliente());
        panel.add(btnAbrirCliente);
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

    private void abrirPainelCliente() {
        ClienteView clienteView = new ClienteView(
            clientePadrao, reservaController, aluguelController, catalogo);
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
