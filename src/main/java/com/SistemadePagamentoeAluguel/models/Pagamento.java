package main.java.com.SistemadePagamentoeAluguel.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Pagamento {
    private static final AtomicInteger idCounter = new AtomicInteger(1000);
    
    private final int id;
    private final double valor;
    private final String metodo;
    private StatusPagamento status;
    private final LocalDateTime data;

    // Enum para estados do pagamento
    public enum StatusPagamento {
        PENDENTE, PROCESSADO, ESTORNADO, FALHA
    }

    public Pagamento(double valor, String metodo) {
        if (valor <= 0) throw new IllegalArgumentException("Valor deve ser positivo");
        
        this.id = idCounter.getAndIncrement();
        this.valor = valor;
        this.metodo = Objects.requireNonNull(metodo, "Método não pode ser nulo");
        this.status = StatusPagamento.PENDENTE;
        this.data = LocalDateTime.now();
    }

    // Getters
    public int getId() { return id; }
    public double getValor() { return valor; }
    public String getMetodo() { return metodo; }
    public StatusPagamento getStatus() { return status; }
    public LocalDateTime getData() { return data; }

    // Ações de negócio
    public void processar() {
        if (status != StatusPagamento.PENDENTE) {
            throw new IllegalStateException("Pagamento só pode ser processado se estiver pendente");
        }
        this.status = StatusPagamento.PROCESSADO;
    }

    public void estornar() {
        if (status != StatusPagamento.PROCESSADO) {
            throw new IllegalStateException("Só é possível estornar pagamentos processados");
        }
        this.status = StatusPagamento.ESTORNADO;
    }

    @Override
    public String toString() {
        return String.format(
            "Pagamento [ID: %d | Valor: R$ %.2f | Método: %s | Status: %s | Data: %s]",
            id, valor, metodo, status, data
        );
    }
}