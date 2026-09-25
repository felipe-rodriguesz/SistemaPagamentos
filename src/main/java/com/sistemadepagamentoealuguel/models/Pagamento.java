package com.sistemadepagamentoealuguel.models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

public class Pagamento {
    public enum StatusPagamento { PENDENTE, PROCESSADO, ESTORNADO }

    public enum MetodoPagamento {
        CARTAO_CREDITO("Cartão de crédito"), BOLETO("Boleto"), PIX("Pix"), DINHEIRO("Dinheiro");

        private final String descricao;

        MetodoPagamento(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    private final int id;
    private final Cliente cliente;
    private final BigDecimal valor;
    private final MetodoPagamento metodo;
    private final LocalDateTime data;
    private StatusPagamento status;

    public Pagamento(int id, Cliente cliente, BigDecimal valor, MetodoPagamento metodo) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        this.id = id;
        this.cliente = Objects.requireNonNull(cliente, "Cliente não pode ser nulo");
        this.metodo = Objects.requireNonNull(metodo, "Método não pode ser nulo");
        BigDecimal valorArredondado = Objects.requireNonNull(valor, "Valor não pode ser nulo")
            .setScale(2, RoundingMode.HALF_UP);
        if (valorArredondado.signum() <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        this.valor = valorArredondado;
        this.data = LocalDateTime.now();
        this.status = StatusPagamento.PENDENTE;
    }

    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public BigDecimal getValor() { return valor; }
    public MetodoPagamento getMetodo() { return metodo; }
    public StatusPagamento getStatus() { return status; }
    public LocalDateTime getData() { return data; }

    public void processar() {
        if (status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Somente pagamentos pendentes podem ser processados");
        }
        status = StatusPagamento.PROCESSADO;
    }

    public void estornar() {
        if (status != StatusPagamento.PROCESSADO) {
            throw new IllegalStateException("Somente pagamentos processados podem ser estornados");
        }
        status = StatusPagamento.ESTORNADO;
    }

    @Override
    public String toString() {
        return String.format("Pagamento %d — %s — R$ %s (%s)",
            id, cliente.getNome(), valor.toPlainString(), status);
    }
}
