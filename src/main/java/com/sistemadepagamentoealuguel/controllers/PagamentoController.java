package com.sistemadepagamentoealuguel.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.sistemadepagamentoealuguel.models.Cliente;
import com.sistemadepagamentoealuguel.models.Pagamento;
import com.sistemadepagamentoealuguel.models.Pagamento.MetodoPagamento;

public final class PagamentoController {
    private final IdGenerator idGenerator;
    private final CadastroController cadastroController;
    private final List<Pagamento> pagamentos = new ArrayList<>();

    public PagamentoController(IdGenerator idGenerator, CadastroController cadastroController) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.cadastroController = Objects.requireNonNull(cadastroController);
    }

    public Pagamento registrarPagamento(Cliente cliente, BigDecimal valor, MetodoPagamento metodo) {
        if (!cadastroController.contemCliente(cliente)) {
            throw new IllegalArgumentException("Cliente não está cadastrado neste sistema");
        }
        Pagamento pagamento = new Pagamento(idGenerator.nextId(), cliente, valor, metodo);
        pagamentos.add(pagamento);
        return pagamento;
    }

    public Optional<Pagamento> realizarPagamentoSimulado(
            Cliente cliente, BigDecimal valor, MetodoPagamento metodo) {
        Pagamento pagamento = registrarPagamento(cliente, valor, metodo);
        pagamento.processar();
        return Optional.of(pagamento);
    }

    public boolean processarPagamento(int pagamentoId) {
        Optional<Pagamento> pagamento = buscarPagamento(pagamentoId);
        if (pagamento.isEmpty()) return false;
        try {
            pagamento.get().processar();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public boolean estornarPagamento(int pagamentoId) {
        Optional<Pagamento> pagamento = buscarPagamento(pagamentoId);
        if (pagamento.isEmpty()) return false;
        try {
            pagamento.get().estornar();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public Optional<Pagamento> buscarPagamento(int id) {
        return pagamentos.stream().filter(pagamento -> pagamento.getId() == id).findFirst();
    }

    public List<Pagamento> listarPagamentos() {
        return List.copyOf(pagamentos);
    }

    public List<Pagamento> listarPagamentosDoCliente(Cliente cliente) {
        return pagamentos.stream()
            .filter(pagamento -> pagamento.getCliente().equals(cliente))
            .toList();
    }

    public String emitirRecibo(Pagamento pagamento) {
        Objects.requireNonNull(pagamento, "Pagamento não pode ser nulo");
        return "Recibo de Pagamento\nID: " + pagamento.getId()
            + "\nCliente: " + pagamento.getCliente().getNome()
            + "\nValor: R$ " + pagamento.getValor().toPlainString()
            + "\nMétodo: " + pagamento.getMetodo()
            + "\nStatus: " + pagamento.getStatus()
            + "\nData: " + pagamento.getData();
    }
}
