package com.SistemadePagamentoeAluguel.controllers;

import com.SistemadePagamentoeAluguel.models.Cliente;
import com.SistemadePagamentoeAluguel.models.Item;
import com.SistemadePagamentoeAluguel.models.Reserva;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReservaController {
    private List<Reserva> reservas;
    private int idCounter = 1;

    public ReservaController() {
        this.reservas = new ArrayList<>();
    }

    public Reserva reservarItem(Cliente cliente, Item item) {
        if (cliente == null || item == null) {
            System.out.println("Erro: Cliente ou item inválido.");
            return null;
        }

        if (isItemReservado(item)) {
            System.out.println("Erro: O item já está reservado.");
            return null;
        }

        Reserva novaReserva = new Reserva(idCounter++, cliente, new Date());
        reservas.add(novaReserva);
        System.out.println("Reserva realizada com sucesso!");
        return novaReserva;
    }

    public boolean cancelarReserva(Reserva reserva) {
        if (reserva.cancelar()) {
            System.out.println("Reserva cancelada com sucesso!");
            return true;
        } else {
            System.out.println("Erro: Não foi possível cancelar a reserva.");
            return false;
        }
    }

    public void cadastrarReserva(Reserva reserva) {
        reservas.add(reserva);
        System.out.println("Reserva cadastrada com sucesso!");
    }

    public List<Reserva> listarReservas() {
        return reservas;
    }

    public Reserva buscarPorId(int idReserva) {
        for (Reserva reserva : reservas) {
            if (reserva.getId() == idReserva) {
                return reserva;
            }
        }
        System.out.println("Erro: Reserva não encontrada.");
        return null;
    }

    private boolean isItemReservado(Item item) {
        for (Reserva reserva : reservas) {
            if (reserva.getStatus() == Reserva.StatusReserva.ATIVA) {
                return true;
            }
        }
        return false;
    }
}