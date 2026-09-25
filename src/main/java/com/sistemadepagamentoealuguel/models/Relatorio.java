package com.sistemadepagamentoealuguel.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class Relatorio {
    private final int id;
    private final String tipo;
    private final DateRange periodo;
    private final int quantidadeRegistros;
    private final List<String> dados;
    private final LocalDateTime geradoEm;

    public Relatorio(int id, String tipo, DateRange periodo, int quantidadeRegistros, List<String> dados) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        if (quantidadeRegistros < 0) throw new IllegalArgumentException("Quantidade inválida");
        this.id = id;
        this.tipo = Objects.requireNonNull(tipo, "Tipo não pode ser nulo");
        this.periodo = Objects.requireNonNull(periodo, "Período não pode ser nulo");
        this.quantidadeRegistros = quantidadeRegistros;
        this.dados = List.copyOf(Objects.requireNonNull(dados, "Dados não podem ser nulos"));
        this.geradoEm = LocalDateTime.now();
    }

    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public DateRange getPeriodo() { return periodo; }
    public int getQuantidadeRegistros() { return quantidadeRegistros; }
    public List<String> getDados() { return dados; }
    public LocalDateTime getGeradoEm() { return geradoEm; }
}
