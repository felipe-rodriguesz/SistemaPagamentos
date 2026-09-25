package com.sistemadepagamentoealuguel.models;

import java.time.LocalDate;
import java.util.Objects;

public class Aluguel {
    private final int id;
    private final Cliente cliente;
    private final Item item;
    private final LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusAluguel status;

    // Enum para estados do aluguel
    public enum StatusAluguel {
        ATIVO, RENOVADO, CANCELADO, DEVOLVIDO
    }

    public Aluguel(int id, Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        validarParametros(cliente, item, dataInicio, dataFim);
        
        this.id = id;
        this.cliente = cliente;
        this.item = item;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusAluguel.ATIVO;
    }

    private void validarParametros(Cliente cliente, Item item, LocalDate inicio, LocalDate fim) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo");
        Objects.requireNonNull(item, "Item não pode ser nulo");
        Objects.requireNonNull(inicio, "Data de início não pode ser nula");
        Objects.requireNonNull(fim, "Data de fim não pode ser nula");
        
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("Data final deve ser posterior à data inicial");
        }
        if (item.isAlugado()) {
            throw new IllegalStateException("Item já está alugado");
        }
    }

    // Getters
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Item getItem() { return item; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusAluguel getStatus() { return status; }
    public boolean isAtivo() {
        return status == StatusAluguel.ATIVO || status == StatusAluguel.RENOVADO;
    }

    // Métodos de negócio
    public boolean renovar(LocalDate novaDataFim) {
        if (!isAtivo()) {
            throw new IllegalStateException("Aluguel não está ativo");
        }
        Objects.requireNonNull(novaDataFim, "Nova data de fim não pode ser nula");
        if (!novaDataFim.isAfter(dataFim)) {
            throw new IllegalArgumentException("Nova data de fim deve ser posterior à data atual do aluguel");
        }
        dataFim = novaDataFim;
        status = StatusAluguel.RENOVADO;
        return true;
    }

    public boolean cancelar() {
        if (!isAtivo()) return false;
        status = StatusAluguel.CANCELADO;
        return true;
    }

    public boolean devolver() {
        if (!isAtivo()) return false;
        status = StatusAluguel.DEVOLVIDO;
        return true;
    }

    @Override
    public String toString() {
        return String.format(
            "Aluguel [ID: %d | Cliente: %s | Item: %s | Período: %s a %s | Status: %s]",
            id, cliente.getNome(), item.getTitulo(), dataInicio, dataFim, status
        );
    }
}
