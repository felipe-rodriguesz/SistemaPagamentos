package main.java.com.SistemadePagamentoeAluguel.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Transacao {
    private final LocalDateTime data;
    private final String descricao;
    private final double valor;

    public Transacao(String descricao, double valor) {
        this.data = LocalDateTime.now();
        this.descricao = Objects.requireNonNull(descricao, "Descrição não pode ser nula");
        this.valor = valor;
    }

    // Getters
    public LocalDateTime getData() { return data; }
    public String getDescricao() { return descricao; }
    public double getValor() { return valor; }

    @Override
    public String toString() {
        return String.format(
            "[%s] %s - Valor: R$ %.2f",
            data.toString(),
            descricao,
            valor
        );
    }
}