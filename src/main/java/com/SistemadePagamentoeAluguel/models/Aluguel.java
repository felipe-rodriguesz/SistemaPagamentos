package main.java.com.SistemadePagamentoeAluguel.models;

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
        ATIVO, CANCELADO, RENOVADO
    }

    public Aluguel(int id, Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
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
        
        if (fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Data final anterior à data inicial");
        }
        if (!item.isDisponivel()) {
            throw new IllegalStateException("Item já está alugado/reservado");
        }
    }

    // Getters
    public int getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Item getItem() { return item; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusAluguel getStatus() { return status; }

    // Métodos de negócio
    public void renovar(LocalDate novaDataFim) {
        if (status != StatusAluguel.ATIVO) {
            throw new IllegalStateException("Aluguel deve estar ativo para renovação");
        }
        if (novaDataFim.isBefore(dataFim)) {
            throw new IllegalArgumentException("Nova data deve ser posterior ao término atual");
        }
        
        dataFim = novaDataFim;
        status = StatusAluguel.RENOVADO;
    }

    public boolean  cancelar() {
        if (status == StatusAluguel.CANCELADO) {
            throw new IllegalStateException("Aluguel já está cancelado");
        }
        status = StatusAluguel.CANCELADO;
        item.marcarComoDevolvido(); // Libera o item
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