package main.java.com.SistemadePagamentoeAluguel.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.*;
import main.java.com.SistemadePagamentoeAluguel.controllers.AluguelController;
import main.java.com.SistemadePagamentoeAluguel.controllers.ReservaController;
import main.java.com.SistemadePagamentoeAluguel.models.*;

public class ClienteView extends JFrame {
    private Cliente cliente;
    private ReservaController reservaController;
    private AluguelController aluguelController;

    private JTextArea areaTexto;
    private JComboBox<String> comboItens;
    private JButton btnReservar, btnAlugar, btnRenovar, btnPagar, btnExibirRecibo, btnHistorico;

    public ClienteView(Cliente cliente, ReservaController reservaController, AluguelController aluguelController) {
        this.cliente = cliente;
        this.reservaController = reservaController;
        this.aluguelController = aluguelController;

        configurarJanela();
        inicializarComponentes();
    }

    private void configurarJanela() {
        setTitle("Painel do Cliente");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel painelControle = new JPanel(new FlowLayout());

        // Lista de itens disponíveis filtrados
        List<Item> itensDisponiveis = reservaController.listarItensDisponiveis()
                .stream().filter(Item::isDisponivel)
                .collect(Collectors.toList());

        comboItens = new JComboBox<>(itensDisponiveis.stream()
                .map(item -> item.getId() + " - " + item.getTitulo())
                .toArray(String[]::new));

        btnReservar = new JButton("Reservar");
        btnAlugar = new JButton("Alugar");
        btnRenovar = new JButton("Renovar Aluguel");
        btnPagar = new JButton("Efetuar Pagamento");
        btnExibirRecibo = new JButton("Exibir Recibo");
        btnHistorico = new JButton("Exibir Histórico");

        btnReservar.addActionListener(this::solicitarReserva);
        btnAlugar.addActionListener(this::solicitarAluguel);
        btnRenovar.addActionListener(this::solicitarRenovacao);
        btnPagar.addActionListener(this::efetuarPagamento);
        btnExibirRecibo.addActionListener(this::exibirRecibo);
        btnHistorico.addActionListener(this::exibirHistorico);

        painelControle.add(new JLabel("Selecionar Item:"));
        painelControle.add(comboItens);
        painelControle.add(btnReservar);
        painelControle.add(btnAlugar);
        painelControle.add(btnRenovar);
        painelControle.add(btnPagar);
        painelControle.add(btnExibirRecibo);
        painelControle.add(btnHistorico);

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaTexto);

        painelPrincipal.add(painelControle, BorderLayout.NORTH);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);
        add(painelPrincipal);
    }

    private void solicitarReserva(ActionEvent e) {
        Item item = obterItemSelecionado();
        if (item == null) return;

        if (!item.isDisponivel()) {
            areaTexto.setText("O item selecionado já está alugado ou reservado.");
            return;
        }

        Reserva reserva = reservaController.reservarItem(cliente, item);
        areaTexto.setText(reserva != null ? "Reserva realizada com sucesso!" : "Erro ao reservar item.");
    }

    private void solicitarAluguel(ActionEvent e) {
        Item item = obterItemSelecionado();
        if (item == null) return;

        if (!item.isDisponivel()) {
            areaTexto.setText("O item selecionado já está alugado ou reservado.");
            return;
        }

        String diasStr = JOptionPane.showInputDialog(this, "Por quantos dias deseja alugar?");
        if (diasStr != null && !diasStr.isEmpty()) {
            try {
                int dias = Integer.parseInt(diasStr);
                Aluguel aluguel = aluguelController.alugarItem(cliente, item, dias);
                areaTexto.setText(aluguel != null ? "Aluguel realizado com sucesso!" : "Erro ao alugar item.");
            } catch (NumberFormatException ex) {
                areaTexto.setText("Número de dias inválido.");
            }
        }
    }

    private void solicitarRenovacao(ActionEvent e) {
        List<Aluguel> alugueis = aluguelController.listarAlugueis(Aluguel.StatusAluguel.ATIVO);
        if (alugueis.isEmpty()) {
            areaTexto.setText("Você não tem aluguéis ativos para renovar.");
            return;
        }

        String novaDataStr = JOptionPane.showInputDialog(this, "Digite a nova data (AAAA-MM-DD):");
        if (novaDataStr != null && !novaDataStr.isEmpty()) {
            try {
                LocalDate novaData = LocalDate.parse(novaDataStr);
                boolean sucesso = aluguelController.renovarAluguel(alugueis.get(0), novaData);
                areaTexto.setText(sucesso ? "Renovação realizada com sucesso!" : "Erro ao renovar aluguel.");
            } catch (Exception ex) {
                areaTexto.setText("Formato de data inválido.");
            }
        }
    }

    private void efetuarPagamento(ActionEvent e) {
        String metodo = JOptionPane.showInputDialog(this, "Digite o método de pagamento:");
        if (metodo != null && !metodo.isEmpty()) {
            boolean sucesso = cliente.realizarPagamento(100.0, metodo);
            areaTexto.setText(sucesso ? "Pagamento realizado com sucesso!" : "Erro ao efetuar pagamento.");
        }
    }

    private void exibirRecibo(ActionEvent e) {
        List<Transacao> historico = cliente.consultarHistorico();
        if (historico.isEmpty()) {
            areaTexto.setText("Nenhum recibo disponível.");
            return;
        }
        Transacao ultimaTransacao = historico.get(historico.size() - 1);
        areaTexto.setText("Recibo: " + ultimaTransacao.getDescricao() + " - Valor: R$" + String.format("%.2f", ultimaTransacao.getValor()));
    }

    private void exibirHistorico(ActionEvent e) {
        List<Transacao> historico = cliente.consultarHistorico();
        if (historico.isEmpty()) {
            areaTexto.setText("Nenhum histórico disponível.");
            return;
        }
        StringBuilder sb = new StringBuilder("=== Histórico de Transações ===\n");
        for (Transacao t : historico) {
            sb.append("Data: ").append(t.getData())
              .append(" | ").append(t.getDescricao())
              .append(" | Valor: R$").append(String.format("%.2f", t.getValor()))
              .append("\n");
        }
        areaTexto.setText(sb.toString());
    }

    private Item obterItemSelecionado() {
        String selecao = (String) comboItens.getSelectedItem();
        if (selecao == null) return null;

        int id = Integer.parseInt(selecao.split(" - ")[0]); // Extrai o ID
        Reserva reserva = reservaController.buscarPorId(id);
        return reserva != null ? reserva.getItem() : null;
    }

    public void exibirPainelCliente() {
        setVisible(true);
    }
}