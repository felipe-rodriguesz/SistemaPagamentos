package com.sistemadepagamentoealuguel.models;

import java.util.Objects;

public final class Item {
    public enum EstadoItem { DISPONIVEL, RESERVADO, ALUGADO }

    private final int id;
    private final String titulo;
    private EstadoItem estado;
    private final TipoItem tipo;

    // Enum para tipos pré-definidos
    public enum TipoItem {
        LIVRO, EQUIPAMENTO, MOVEL, OUTROS
    }

    public Item(int id, String titulo, TipoItem tipo) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        this.id = id;
        this.titulo = Objects.requireNonNull(titulo, "Título não pode ser nulo").trim();
        if (this.titulo.isEmpty()) throw new IllegalArgumentException("Título não pode ser vazio");
        this.tipo = Objects.requireNonNull(tipo, "Tipo não pode ser nulo");
        this.estado = EstadoItem.DISPONIVEL;
    }

    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isDisponivel() { return estado == EstadoItem.DISPONIVEL; }
    public boolean isReservado() { return estado == EstadoItem.RESERVADO; }
    public boolean isAlugado() { return estado == EstadoItem.ALUGADO; }
    public EstadoItem getEstado() { return estado; }
    public TipoItem getTipo() { return tipo; }

    public void marcarComoAlugado() {
        if (estado == EstadoItem.ALUGADO) {
            throw new IllegalStateException("Item já está alugado");
        }
        this.estado = EstadoItem.ALUGADO;
    }

    public void marcarComoReservado() {
        if (estado != EstadoItem.ALUGADO) {
            throw new IllegalStateException("Somente um item alugado pode passar a reservado");
        }
        this.estado = EstadoItem.RESERVADO;
    }

    public void marcarComoDevolvido() {
        if (estado == EstadoItem.DISPONIVEL) {
            throw new IllegalStateException("Item já está disponível");
        }
        this.estado = EstadoItem.DISPONIVEL;
    }

    @Override
    public String toString() {
        return String.format(
            "Item [ID: %d | Título: %s | Tipo: %s | %s]",
            id, titulo, tipo, switch (estado) {
                case DISPONIVEL -> "Disponível";
                case RESERVADO -> "Reservado";
                case ALUGADO -> "Alugado";
            }
        );
    }

    @Override
    public boolean equals(Object objeto) {
        return this == objeto || objeto instanceof Item outro && id == outro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
