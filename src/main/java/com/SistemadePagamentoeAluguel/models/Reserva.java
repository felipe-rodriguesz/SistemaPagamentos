package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.Date;

public class Reserva {
    private int id;
    private Cliente cliente;
    private Item item;
    private Date dataReserva;
    private String status;
    private boolean cancelada;
    
    public Reserva(int id, Cliente cliente, Item item) {
        this.id = id;
        this.cliente = cliente;
        this.item = item;
        this.dataReserva = new Date();
        this.status = "Pendente";
        this.cancelada = false;
    } 
    
    public boolean isCancelada() { return cancelada; }
    public void cancelar() { this.cancelada = true; this.status = "Cancelada"; }
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Item getItem() { return item; }
    public Date getDataReserva() { return dataReserva; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}