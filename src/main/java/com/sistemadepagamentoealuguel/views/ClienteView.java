package com.sistemadepagamentoealuguel.views;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javax.swing.*;
import com.sistemadepagamentoealuguel.controllers.AluguelController;
import com.sistemadepagamentoealuguel.controllers.CadastroController;
import com.sistemadepagamentoealuguel.controllers.PagamentoController;
import com.sistemadepagamentoealuguel.controllers.ReservaController;
import com.sistemadepagamentoealuguel.models.*;

public class ClienteView extends JFrame {
    private Cliente cliente;
    private CadastroController cadastroController;
    private ReservaController reservaController;
    private AluguelController aluguelController;
    private PagamentoController pagamentoController;

    private JTextArea areaTexto;
    private JComboBox<String> comboItens;
    private JButton btnReservar, btnAlugar, btnRenovar, btnPagar, btnExibirRecibo;
    private JButton btnHistorico, btnCancelar, btnCancelarReserva, btnMeusAlugueis, btnDevolver;

    public ClienteView(Cliente cliente, CadastroController cadastroController,
            ReservaController reservaController, AluguelController aluguelController,
            PagamentoController pagamentoController) {
        this.cliente = cliente;
        this.cadastroController = cadastroController;
        this.reservaController = reservaController;
        this.aluguelController = aluguelController;
        this.pagamentoController = pagamentoController;
        configurarJanela();
        inicializarComponentes();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent evento) {
                atualizarComboItensSelecionado();
            }
        });
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

        JPanel painelControle = new JPanel(new GridLayout(0, 3, 5, 5));

        comboItens = new JComboBox<>(cadastroController.listarItens().stream()
                .map(this::formatarOpcaoItem)
                .toArray(String[]::new));

        btnReservar = new JButton("Reservar");
        btnAlugar = new JButton("Alugar");
        btnRenovar = new JButton("Renovar Aluguel");
        btnPagar = new JButton("Efetuar Pagamento");
        btnExibirRecibo = new JButton("Exibir Recibo");
        btnHistorico = new JButton("Exibir Histórico");
        btnCancelar = new JButton("Cancelar Aluguel");
        btnCancelarReserva = new JButton("Cancelar Reserva");
        btnMeusAlugueis = new JButton("Meus Aluguéis");
        btnDevolver = new JButton("Devolver Item");
        btnReservar.addActionListener(this::solicitarReserva);
        btnAlugar.addActionListener(this::solicitarAluguel);
        btnRenovar.addActionListener(this::solicitarRenovacao);
        btnPagar.addActionListener(this::efetuarPagamento);
        btnExibirRecibo.addActionListener(this::exibirRecibo);
        btnHistorico.addActionListener(this::exibirHistorico);
        btnCancelar.addActionListener(this::solicitarCancelamento);
        btnCancelarReserva.addActionListener(this::cancelarReserva);
        btnMeusAlugueis.addActionListener(this::exibirMeusAlugueis);
        btnDevolver.addActionListener(this::devolverAluguel);

        painelControle.add(new JLabel("Selecionar Item:"));
        painelControle.add(comboItens);
        painelControle.add(btnReservar);
        painelControle.add(btnAlugar);
        painelControle.add(btnRenovar);
        painelControle.add(btnPagar);
        painelControle.add(btnHistorico);
        painelControle.add(btnCancelar);
        painelControle.add(btnCancelarReserva);
        painelControle.add(btnMeusAlugueis);
        painelControle.add(btnDevolver);
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

        if (item.isDisponivel()) {
            areaTexto.setText("Reservas são para itens que estão alugados.");
            return;
        }

        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(7);
        Optional<Reserva> reservaOpt = reservaController.criarReserva(cliente, item, dataInicio, dataFim);
        areaTexto.setText(reservaOpt.isPresent()
            ? "Reserva registrada. Você poderá alugar o item quando ele for liberado."
            : "O item já possui uma reserva ativa ou não pode ser reservado.");
        if (reservaOpt.isPresent()) atualizarComboItens(item);
    }

    private void solicitarAluguel(ActionEvent e) {
        Item item = selecionarItem();
        if (item == null) return;

        if (!aluguelController.podeAlugar(cliente, item)) {
            areaTexto.setText("O item está alugado ou reservado para outro cliente.");
            return;
        }

        String diasStr = JOptionPane.showInputDialog(this, "Por quantos dias deseja alugar?");
        if (diasStr != null && !diasStr.isEmpty()) {
            try {
                int dias = Integer.parseInt(diasStr);
                if (dias <= 0) {
                    areaTexto.setText("A duração do aluguel deve ser maior que zero.");
                    return;
                }
                LocalDate dataInicio = LocalDate.now();
                LocalDate dataFim = dataInicio.plusDays(dias);
                Optional<Aluguel> aluguelOpt = aluguelController.criarAluguel(cliente, item, dataInicio, dataFim);
                areaTexto.setText(aluguelOpt.isPresent() ? "Aluguel realizado com sucesso!" : "Erro ao alugar item.");
                if (aluguelOpt.isPresent()) atualizarComboItens(item);
            } catch (NumberFormatException | java.time.DateTimeException ex) {
                areaTexto.setText("Número de dias inválido ou fora do intervalo permitido.");
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
            } catch (java.time.DateTimeException ex) {
                areaTexto.setText("Data inválida ou anterior ao fim do aluguel.");
            }
        }
    }

    private void efetuarPagamento(ActionEvent e) {
        String valorInformado = JOptionPane.showInputDialog(this, "Informe o valor do pagamento simulado:");
        if (valorInformado == null) return;

        Pagamento.MetodoPagamento metodo = (Pagamento.MetodoPagamento) JOptionPane.showInputDialog(
            this, "Selecione o método de pagamento:", "Pagamento Simulado",
            JOptionPane.QUESTION_MESSAGE, null, Pagamento.MetodoPagamento.values(),
            Pagamento.MetodoPagamento.PIX);
        if (metodo == null) return;

        try {
            BigDecimal valor = new BigDecimal(valorInformado.trim().replace(',', '.'));
            Optional<Pagamento> pagamento = pagamentoController.realizarPagamentoSimulado(cliente, valor, metodo);
            areaTexto.setText(pagamento.map(pagamentoController::emitirRecibo)
                .orElse("Não foi possível registrar o pagamento."));
        } catch (IllegalArgumentException ex) {
            areaTexto.setText("Informe um valor numérico positivo válido.");
        }
    }

    private void exibirRecibo(ActionEvent e) {
        List<Pagamento> historico = pagamentoController.listarPagamentosDoCliente(cliente);
        if (historico.isEmpty()) {
            areaTexto.setText("Nenhum recibo disponível.");
            return;
        }
        areaTexto.setText(pagamentoController.emitirRecibo(historico.get(historico.size() - 1)));
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

    private void devolverAluguel(ActionEvent e) {
        List<Aluguel> alugueis = listarAlugueisAtivosDoCliente();
        if (alugueis.isEmpty()) {
            areaTexto.setText("Você não tem aluguéis ativos para devolver.");
            return;
        }

        Aluguel aluguel = selecionarAluguel(alugueis, "Selecione o aluguel para devolver:");
        if (aluguel == null) return;

        boolean sucesso = aluguelController.devolverAluguel(aluguel.getId());
        areaTexto.setText(sucesso ? "Item devolvido." : "Não foi possível devolver o item.");
        if (sucesso) atualizarComboItens(aluguel.getItem());
    }

    private void exibirMeusAlugueis(ActionEvent e) {
        List<Aluguel> alugueis = aluguelController.listarAlugueisDoCliente(cliente);
        if (alugueis.isEmpty()) {
            areaTexto.setText("Você ainda não possui aluguéis.");
            return;
        }
        StringBuilder texto = new StringBuilder("=== Meus Aluguéis ===\n");
        alugueis.forEach(aluguel -> texto.append(aluguel).append('\n'));
        areaTexto.setText(texto.toString());
    }

    private void cancelarReserva(ActionEvent e) {
        List<Reserva> reservas = reservaController.listarReservasDoCliente(cliente).stream()
            .filter(reserva -> reserva.getStatus() == Reserva.StatusReserva.ATIVA)
            .toList();
        if (reservas.isEmpty()) {
            areaTexto.setText("Você não tem reservas ativas para cancelar.");
            return;
        }

        Object selecionada = JOptionPane.showInputDialog(
            this, "Selecione a reserva para cancelar:", "Cancelar Reserva",
            JOptionPane.QUESTION_MESSAGE, null, reservas.toArray(), reservas.get(0));
        if (!(selecionada instanceof Reserva reserva)) return;

        boolean sucesso = reservaController.cancelarReserva(reserva.getId());
        areaTexto.setText(sucesso ? "Reserva cancelada." : "Não foi possível cancelar a reserva.");
        if (sucesso) atualizarComboItens(reserva.getItem());
    }

    private List<Aluguel> listarAlugueisAtivosDoCliente() {
        return aluguelController.listarAlugueisDoCliente(cliente).stream()
            .filter(Aluguel::isAtivo)
            .toList();
    }

    private Aluguel selecionarAluguel(List<Aluguel> alugueis, String mensagem) {
        Object selecionado = JOptionPane.showInputDialog(
            this, mensagem, "Selecionar Aluguel", JOptionPane.QUESTION_MESSAGE,
            null, alugueis.toArray(), alugueis.get(0));
        return selecionado instanceof Aluguel ? (Aluguel) selecionado : null;
    }

    private void exibirHistorico(ActionEvent e) {
        List<Pagamento> historico = pagamentoController.listarPagamentosDoCliente(cliente);
        if (historico.isEmpty()) {
            areaTexto.setText("Nenhum pagamento no histórico.");
            return;
        }
        StringBuilder sb = new StringBuilder("=== Histórico de Pagamentos ===\n");
        for (Pagamento pagamento : historico) {
            sb.append("Data: ").append(pagamento.getData())
              .append(" | Método: ").append(pagamento.getMetodo())
              .append(" | Valor: R$ ").append(pagamento.getValor().toPlainString())
              .append(" | Status: ").append(pagamento.getStatus())
              .append("\n");
        }
        areaTexto.setText(sb.toString());
    }

    private Item selecionarItem() {
        String itemStr = (String) comboItens.getSelectedItem();
        if (itemStr == null) return null;

        int id = Integer.parseInt(itemStr.split(" - ")[0]);
        return cadastroController.buscarItem(id).orElse(null);
    }

    private String formatarOpcaoItem(Item item) {
        String estado = item.isDisponivel() ? "Disponível"
            : item.isReservado() ? "Reservado para cliente" : "Alugado";
        if (item.isAlugado() && reservaController.reservaAtivaParaItem(item).isPresent()) {
            estado = "Alugado (reserva aguardando)";
        }
        return item.getId() + " - " + item.getTitulo() + " (" + estado + ")";
    }

    private void atualizarComboItens(Item selecionado) {
        comboItens.removeAllItems();
        for (Item item : cadastroController.listarItens()) {
            comboItens.addItem(formatarOpcaoItem(item));
            if (item == selecionado) {
                comboItens.setSelectedIndex(comboItens.getItemCount() - 1);
            }
        }
    }

    private void atualizarComboItensSelecionado() {
        String opcaoAtual = (String) comboItens.getSelectedItem();
        Item itemAtual = null;
        if (opcaoAtual != null) {
            try {
                int id = Integer.parseInt(opcaoAtual.split(" - ")[0]);
                itemAtual = cadastroController.buscarItem(id).orElse(null);
            } catch (NumberFormatException ignorado) {
                // A lista será reconstruída a partir do catálogo compartilhado.
            }
        }
        atualizarComboItens(itemAtual);
    }

    public void exibirPainelCliente() {
        setVisible(true);
    }
}
