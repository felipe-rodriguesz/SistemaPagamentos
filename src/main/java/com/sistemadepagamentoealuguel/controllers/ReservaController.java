package com.sistemadepagamentoealuguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import com.sistemadepagamentoealuguel.models.Cliente;
import com.sistemadepagamentoealuguel.models.Item;
import com.sistemadepagamentoealuguel.models.Reserva;

public class ReservaController {
    private final List<Reserva> reservas = new ArrayList<>();
    private final IdGenerator idGenerator;
    private final CadastroController cadastroController;

    public ReservaController(IdGenerator idGenerator, CadastroController cadastroController) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.cadastroController = Objects.requireNonNull(cadastroController);
    }

    public Optional<Reserva> criarReserva(Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (!cadastroController.contemCliente(cliente) || !cadastroController.contemItem(item)
                || !item.isAlugado() || reservaAtivaParaItem(item).isPresent()) {
            return Optional.empty();
        }

        try {
            Reserva reserva = new Reserva(idGenerator.nextId(), cliente, item, dataInicio, dataFim);
            reservas.add(reserva);
            return Optional.of(reserva);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public boolean cancelarReserva(int idReserva) {
        Optional<Reserva> reservaOpt = buscarReserva(idReserva);
        if (reservaOpt.isEmpty()) return false;

        Reserva reserva = reservaOpt.get();
        if (!reserva.cancelar()) return false;
        if (reserva.getItem().isReservado()) {
            reserva.getItem().marcarComoDevolvido();
        }
        return true;
    }

    public List<Reserva> listarReservasAtivas() {
        return reservas.stream()
            .filter(r -> r.getStatus() == Reserva.StatusReserva.ATIVA)
            .toList();
    }

    public List<Reserva> listarReservasDoCliente(Cliente cliente) {
        return reservas.stream()
            .filter(r -> r.getCliente().equals(cliente))
            .toList();
    }

    public Optional<Reserva> buscarReserva(int idReserva) {
        return reservas.stream()
            .filter(r -> r.getId() == idReserva)
            .findFirst();
    }

    public Optional<Reserva> reservaAtivaParaItem(Item item) {
        return reservas.stream()
            .filter(r -> r.getItem().equals(item) && r.getStatus() == Reserva.StatusReserva.ATIVA)
            .findFirst();
    }

    public boolean podeAlugar(Cliente cliente, Item item) {
        if (cliente == null || item == null || item.isAlugado()) return false;
        Optional<Reserva> reserva = reservaAtivaParaItem(item);
        if (item.isReservado()) {
            return reserva.map(r -> r.getCliente().equals(cliente)).orElse(false);
        }
        return item.isDisponivel() && reserva.isEmpty();
    }

    public boolean converterReservaParaAluguel(Cliente cliente, Item item) {
        Optional<Reserva> reserva = reservaAtivaParaItem(item);
        return reserva.isPresent()
            && reserva.get().getCliente().equals(cliente)
            && reserva.get().converterEmAluguel();
    }

}
