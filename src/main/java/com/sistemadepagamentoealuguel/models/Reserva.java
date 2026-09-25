package com.sistemadepagamentoealuguel.models;

import java.time.LocalDate;

public class Reserva {
    public enum StatusReserva { ATIVA, CANCELADA, CONVERTIDA }

    private final int id;
    private final Cliente cliente;
    private final Item item;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private StatusReserva status;

    public Reserva(int id, Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        java.util.Objects.requireNonNull(cliente, "Cliente não pode ser nulo");
        java.util.Objects.requireNonNull(item, "Item não pode ser nulo");
        java.util.Objects.requireNonNull(dataInicio, "Data de início não pode ser nula");
        java.util.Objects.requireNonNull(dataFim, "Data de fim não pode ser nula");
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data final inválida");
        }
        if (!item.isAlugado()) {
            throw new IllegalStateException("A reserva aguarda a liberação de um item alugado");
        }
        
        this.id = id;
        this.cliente = cliente;
        this.item = item;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusReserva.ATIVA;
    }

    // Getters
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Item getItem() { return item; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusReserva getStatus() { return status; }

    public boolean cancelar() {
        if (status != StatusReserva.ATIVA) return false;
        status = StatusReserva.CANCELADA;
        return true;
    }

    public boolean converterEmAluguel() {
        if (status != StatusReserva.ATIVA) return false;
        status = StatusReserva.CONVERTIDA;
        return true;
    }

    @Override
    public String toString() {
        return String.format("Reserva %d — %s — %s (%s)",
            id, item.getTitulo(), cliente.getNome(), status);
    }
}
