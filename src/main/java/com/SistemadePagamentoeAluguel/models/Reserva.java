package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.Date;
import java.util.Objects;

public class Reserva {
    private int id;
    private Date dataReserva;
    private StatusReserva status;
    private Item item;

    public enum StatusReserva {
        PENDENTE, CONFIRMADA, CANCELADA
    }

    public Reserva(int id, Date dataReserva) {
        this.id = id;
        this.dataReserva = Objects.requireNonNull(dataReserva, "Data da reserva não pode ser nula");
        this.status = StatusReserva.PENDENTE; 
    }

    public int getId() {
        return id;
    }

    public Date getDataReserva() {
        return dataReserva;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public void setDataReserva(Date novaData) {
        this.dataReserva = Objects.requireNonNull(novaData, "Nova data não pode ser nula");
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    // Método para cancelamento
    public void cancelar() {
        if (status == StatusReserva.CANCELADA) {
            throw new IllegalStateException("Reserva já está cancelada");
        }
        this.status = StatusReserva.CANCELADA;
    }

    @Override
    public String toString() {
        return String.format("Reserva [ID: %d | Data: %s | Status: %s]", id, dataReserva, status);
    }
}