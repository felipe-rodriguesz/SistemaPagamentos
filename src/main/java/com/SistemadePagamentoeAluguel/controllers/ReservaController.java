package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import main.java.com.SistemadePagamentoeAluguel.models.*;

public class ReservaController {
    private final List<Reserva> reservas = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Optional<Reserva> criarReserva(Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (cliente == null || item == null) {
            System.out.println("[ERRO] Cliente ou item inválido");
            return Optional.empty();
        }

        if (!item.isDisponivel() || isItemReservado(item)) {
            System.out.println("[ERRO] Item já reservado/alugado");
            return Optional.empty();
        }

        try {
            Reserva reserva = new Reserva(
                idCounter.getAndIncrement(),
                cliente,
                item,
                dataInicio,
                dataFim
            );
            
            item.marcarComoAlugado();
            reservas.add(reserva);
            System.out.println("[SUCESSO] Reserva ID: " + reserva.getId());
            return Optional.of(reserva);
            
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO] " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean cancelarReserva(int idReserva) {
        Optional<Reserva> reservaOpt = buscarReserva(idReserva);
        
        if (reservaOpt.isPresent()) {
            Reserva reserva = reservaOpt.get();
            if (reserva.cancelar()) {
                reserva.getItem().marcarComoDevolvido();
                return true;
            }
        }
        return false;
    }

    public List<Reserva> listarReservasAtivas() {
        return reservas.stream()
            .filter(r -> r.getStatus() == Reserva.StatusReserva.ATIVA)
            .toList();
    }

    public Optional<Reserva> buscarReserva(int idReserva) {
        return reservas.stream()
            .filter(r -> r.getId() == idReserva)
            .findFirst();
    }

    private boolean isItemReservado(Item item) {
        return reservas.stream()
            .anyMatch(r -> r.getItem().equals(item) && r.getStatus() == Reserva.StatusReserva.ATIVA);
    }

    public List<Item> listarItensDisponiveis(List<Item> todosItens) {
        List<Item> disponiveis = new ArrayList<>(todosItens);
        disponiveis.removeIf(item -> !item.isDisponivel() || isItemReservado(item));
        return Collections.unmodifiableList(disponiveis);
    }
}