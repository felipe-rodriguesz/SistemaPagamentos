package main.java.com.SistemadePagamentoeAluguel.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import main.java.com.SistemadePagamentoeAluguel.controllers.AluguelController;
import main.java.com.SistemadePagamentoeAluguel.controllers.ReservaController;
import main.java.com.SistemadePagamentoeAluguel.models.*;

public class ClienteView extends JFrame {
    private Cliente cliente;
    private ReservaController reservaController;
    private AluguelController aluguelController;

    private JTextArea areaTexto;
    private final List<Item> catalogo;
    private JComboBox<String> comboItens;
    private JButton btnReservar, btnAlugar, btnRenovar, btnPagar, btnExibirRecibo, btnHistorico, btnCancelar;

    public ClienteView(Cliente cliente, ReservaController reservaController,
            AluguelController aluguelController, List<Item> catalogo) {
        this.cliente = cliente;
        this.reservaController = reservaController;
        this.aluguelController = aluguelController;
        this.catalogo = catalogo;
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

        comboItens = new JComboBox<>(catalogo.stream()
                .map(this::formatarOpcaoItem)
                .toArray(String[]::new));

        btnReservar = new JButton("Reservar");
        btnAlugar = new JButton("Alugar");
        btnRenovar = new JButton("Renovar Aluguel");
        btnPagar = new JButton("Efetuar Pagamento");
        btnExibirRecibo = new JButton("Exibir Recibo");
        btnHistorico = new JButton("Exibir Histórico");
        btnCancelar = new JButton("Cancelar Aluguel");
        btnReservar.addActionListener(this::solicitarReserva);
        btnAlugar.addActionListener(this::solicitarAluguel);
        btnRenovar.addActionListener(this::solicitarRenovacao);
        btnPagar.addActionListener(this::efetuarPagamento);
        btnExibirRecibo.addActionListener(this::exibirRecibo);
        btnHistorico.addActionListener(this::exibirHistorico);
        btnCancelar.addActionListener(this::solicitarCancelamento);

        painelControle.add(new JLabel("Selecionar Item:"));
        painelControle.add(comboItens);
        painelControle.add(btnReservar);
        painelControle.add(btnAlugar);
        painelControle.add(btnRenovar);
        painelControle.add(btnPagar);
        painelControle.add(btnHistorico);
        painelControle.add(btnCancelar);
        painelControle.add(btnExibirRecibo);

        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaTexto);

        painelPrincipal.add(painelControle, BorderLayout.NORTH);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);
        add(painelPrincipal);
    }

    private void solicitarReserva(ActionEvent e) {
        Item item = selecionarItem();
        if (item == null) return;

        if (!item.isDisponivel()) {
            areaTexto.setText("O item selecionado já está alugado ou reservado.");
            return;
        }

        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(7);
        Optional<Reserva> reservaOpt = reservaController.criarReserva(cliente, item, dataInicio, dataFim);
        areaTexto.setText(reservaOpt.isPresent() ? "Reserva realizada com sucesso!" : "Erro ao reservar item.");
        if (reservaOpt.isPresent()) atualizarComboItens(item);
    }

    private void solicitarAluguel(ActionEvent e) {
        Item item = selecionarItem();
        if (item == null) return;

        if (!item.isDisponivel()) {
            areaTexto.setText("O item selecionado já está alugado ou reservado.");
            return;
        }

        String diasStr = JOptionPane.showInputDialog(this, "Por quantos dias deseja alugar?");
        if (diasStr != null && !diasStr.isEmpty()) {
            try {
                int dias = Integer.parseInt(diasStr);
                LocalDate dataInicio = LocalDate.now();
                LocalDate dataFim = dataInicio.plusDays(dias);
                Optional<Aluguel> aluguelOpt = aluguelController.criarAluguel(cliente, item, dataInicio, dataFim);
                areaTexto.setText(aluguelOpt.isPresent() ? "Aluguel realizado com sucesso!" : "Erro ao alugar item.");
                if (aluguelOpt.isPresent()) atualizarComboItens(item);
            } catch (NumberFormatException ex) {
                areaTexto.setText("Número de dias inválido.");
            }
        }
    }

    private void solicitarRenovacao(ActionEvent e) {
        List<Aluguel> alugueis = listarAlugueisAtivosDoCliente();
        if (alugueis.isEmpty()) {
            areaTexto.setText("Você não tem aluguéis ativos para renovar.");
            return;
        }

        Aluguel aluguel = selecionarAluguel(alugueis, "Selecione o aluguel para renovar:");
        if (aluguel == null) return;

        String novaDataStr = JOptionPane.showInputDialog(this, "Digite a nova data (AAAA-MM-DD):");
        if (novaDataStr != null && !novaDataStr.isEmpty()) {
            try {
                LocalDate novaData = LocalDate.parse(novaDataStr);
                boolean sucesso = aluguelController.renovarAluguel(aluguel.getId(), novaData);
                areaTexto.setText(sucesso ? "Renovação realizada com sucesso!" : "Erro ao renovar aluguel.");
            } catch (IllegalArgumentException ex) {
                areaTexto.setText("Data inválida ou anterior ao fim do aluguel.");
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

    private void solicitarCancelamento(ActionEvent e) {
        List<Aluguel> alugueis = listarAlugueisAtivosDoCliente();
        if (alugueis.isEmpty()) {
            areaTexto.setText("Você não tem aluguéis ativos para cancelar.");
            return;
        }

        Aluguel aluguel = selecionarAluguel(alugueis, "Selecione o aluguel para cancelar:");
        if (aluguel == null) return;

        boolean sucesso = aluguelController.cancelarAluguel(aluguel.getId());
        areaTexto.setText(sucesso ? "Aluguel cancelado com sucesso!" : "Erro ao cancelar aluguel.");
        if (sucesso) atualizarComboItens(aluguel.getItem());
    }

    private List<Aluguel> listarAlugueisAtivosDoCliente() {
        return aluguelController.listarAlugueisAtivos().stream()
            .filter(aluguel -> aluguel.getCliente().equals(cliente))
            .toList();
    }

    private Aluguel selecionarAluguel(List<Aluguel> alugueis, String mensagem) {
        Object selecionado = JOptionPane.showInputDialog(
            this, mensagem, "Selecionar Aluguel", JOptionPane.QUESTION_MESSAGE,
            null, alugueis.toArray(), alugueis.get(0));
        return selecionado instanceof Aluguel ? (Aluguel) selecionado : null;
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

    private Item selecionarItem() {
        String itemStr = (String) comboItens.getSelectedItem();
        if (itemStr == null) return null;

        int id = Integer.parseInt(itemStr.split(" - ")[0]);
        return catalogo.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
    }

    private String formatarOpcaoItem(Item item) {
        String estado = item.isDisponivel() ? "Disponível" : item.isReservado() ? "Reservado" : "Alugado";
        return item.getId() + " - " + item.getTitulo() + " (" + estado + ")";
    }

    private void atualizarComboItens(Item selecionado) {
        comboItens.removeAllItems();
        for (Item item : catalogo) {
            comboItens.addItem(formatarOpcaoItem(item));
            if (item == selecionado) {
                comboItens.setSelectedIndex(comboItens.getItemCount() - 1);
            }
        }
    }

    public void exibirPainelCliente() {
        setVisible(true);
    }
}
