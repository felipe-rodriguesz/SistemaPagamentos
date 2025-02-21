package main.java.com.SistemadePagamentoeAluguel.models;

import java.time.LocalDate;

public class Reserva {
    public enum StatusReserva { ATIVA, CANCELADA }

    private final int id;
    private final Cliente cliente;
    private final Item item;
    private final LocalDate dataInicio;
    private final LocalDate dataFim;
    private StatusReserva status;

    public Reserva(int id, Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data final inválida");
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
        if (status == StatusReserva.CANCELADA) return false;
        status = StatusReserva.CANCELADA;
        return true;
    }
}