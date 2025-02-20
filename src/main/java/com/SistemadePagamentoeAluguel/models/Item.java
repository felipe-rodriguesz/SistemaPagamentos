package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.Objects;

public class Item {
    private final int id;
    private String titulo;
    private boolean disponivel;
    private TipoItem tipo;

    // Enum para tipos pré-definidos
    public enum TipoItem {
        LIVRO, EQUIPAMENTO, MOVEL, OUTROS
    }

    public Item(int id, String titulo, TipoItem tipo) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        
        this.id = id;
        this.titulo = Objects.requireNonNull(titulo, "Título não pode ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "Tipo não pode ser nulo");
        this.disponivel = true;
    }

    // Getters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public boolean isDisponivel() { return disponivel; }
    public TipoItem getTipo() { return tipo; }

    // Setters com validação
    public void setTitulo(String titulo) {
        this.titulo = Objects.requireNonNull(titulo, "Título não pode ser nulo");
    }

    public void setTipo(TipoItem tipo) {
        this.tipo = Objects.requireNonNull(tipo, "Tipo não pode ser nulo");
    }

    // Métodos de negócio
    public void marcarComoAlugado() {
        if (!disponivel) {
            throw new IllegalStateException("Item já está alugado");
        }
        this.disponivel = false;
    }

    public void marcarComoDevolvido() {
        if (disponivel) {
            throw new IllegalStateException("Item já está disponível");
        }
        this.disponivel = true;
    }

    @Override
    public String toString() {
        return String.format(
            "Item [ID: %d | Título: %s | Tipo: %s | %s]",
            id, titulo, tipo, disponivel ? "Disponível" : "Alugado"
        );
    }
}