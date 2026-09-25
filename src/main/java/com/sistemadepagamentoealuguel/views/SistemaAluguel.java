package com.sistemadepagamentoealuguel.views;

import java.awt.*;
import javax.swing.*;
import com.sistemadepagamentoealuguel.controllers.AluguelController;
import com.sistemadepagamentoealuguel.controllers.CadastroController;
import com.sistemadepagamentoealuguel.controllers.IdGenerator;
import com.sistemadepagamentoealuguel.controllers.PagamentoController;
import com.sistemadepagamentoealuguel.controllers.RelatorioController;
import com.sistemadepagamentoealuguel.controllers.ReservaController;
import com.sistemadepagamentoealuguel.models.Cliente;
import com.sistemadepagamentoealuguel.models.Item;

public class SistemaAluguel extends JFrame {
    private final IdGenerator idGenerator = new IdGenerator();
    private final CadastroController cadastroController = new CadastroController(idGenerator);
    private final ReservaController reservaController = new ReservaController(idGenerator, cadastroController);
    private final AluguelController aluguelController = new AluguelController(
        idGenerator, cadastroController, reservaController);
    private final PagamentoController pagamentoController = new PagamentoController(idGenerator, cadastroController);
    private final RelatorioController relatorioController = new RelatorioController(
        idGenerator, aluguelController, pagamentoController);
    private final Cliente clientePadrao;

    public SistemaAluguel() {
        clientePadrao = cadastroController.criarCliente("Cliente Demo", "cliente.demo@example.test");
        cadastroController.criarItem("Livro de POO", Item.TipoItem.LIVRO);
        cadastroController.criarItem("Projetor multimídia", Item.TipoItem.EQUIPAMENTO);
        cadastroController.criarItem("Mesa de estudo", Item.TipoItem.MOVEL);
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
            clientePadrao, cadastroController, reservaController, aluguelController, pagamentoController);
        clienteView.exibirPainelCliente();
    }

    private void abrirAdministradorView() {
        AdministradorView adminView = new AdministradorView(
            cadastroController, pagamentoController, aluguelController, relatorioController);
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
