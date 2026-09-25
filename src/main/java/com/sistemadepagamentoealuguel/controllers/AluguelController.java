package com.sistemadepagamentoealuguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Objects;
import com.sistemadepagamentoealuguel.models.Aluguel;
import com.sistemadepagamentoealuguel.models.Cliente;
import com.sistemadepagamentoealuguel.models.Item;

public class AluguelController {
    private final ReservaController reservaController;
    private final CadastroController cadastroController;
    private final IdGenerator idGenerator;

    public AluguelController(IdGenerator idGenerator, CadastroController cadastroController,
            ReservaController reservaController) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.cadastroController = Objects.requireNonNull(cadastroController);
        this.reservaController = Objects.requireNonNull(reservaController);
    }

    public boolean podeAlugar(Cliente cliente, Item item) {
        return cliente != null && item != null && reservaController.podeAlugar(cliente, item);
    }

    public Optional<Aluguel> criarAluguel(Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (!cadastroController.contemCliente(cliente) || !cadastroController.contemItem(item)) {
            return Optional.empty();
        }

        if (!podeAlugar(cliente, item)) {
            return Optional.empty();
        }

        try {
            Aluguel aluguel = new Aluguel(
                idGenerator.nextId(),
                cliente,
                item,
                dataInicio,
                dataFim
            );

            item.marcarComoAlugado();
            alugueis.add(aluguel);
            reservaController.converterReservaParaAluguel(cliente, item);
            return Optional.of(aluguel);

        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public boolean renovarAluguel(Cliente cliente, Item item, LocalDate novaData) {
        Optional<Aluguel> aluguelOpt = alugueis.stream()
            .filter(a -> a.getCliente().equals(cliente) && a.getItem().equals(item) && a.isAtivo())
            .findFirst();

        if (aluguelOpt.isPresent()) {
            Aluguel aluguel = aluguelOpt.get();
            return renovarAluguel(aluguel.getId(), novaData);
        }
        return false;
    }

    public List<Aluguel> listarAlugueisAtivos() {
        return alugueis.stream()
            .filter(Aluguel::isAtivo)
            .toList();
    }

    public Optional<Aluguel> buscarAluguel(int idAluguel) {
        return alugueis.stream()
            .filter(a -> a.getId() == idAluguel)
            .findFirst();
    }

    public boolean renovarAluguel(int idAluguel, LocalDate novaDataFim) {
        Optional<Aluguel> aluguelOpt = buscarAluguel(idAluguel);

        if (aluguelOpt.isPresent()) {
            Aluguel aluguel = aluguelOpt.get();
            try {
                return aluguel.renovar(novaDataFim);
            } catch (IllegalArgumentException | IllegalStateException | NullPointerException e) {
                return false;
            }
        }
        return false;
    }

    public List<Aluguel> listarAlugueisPorStatus(Aluguel.StatusAluguel status) {
        return alugueis.stream()
            .filter(a -> a.getStatus() == status)
            .toList();
    }

    private final List<Aluguel> alugueis = new ArrayList<>();

    public boolean cancelarAluguel(int idAluguel) {
        Optional<Aluguel> aluguelOpt = alugueis.stream()
            .filter(a -> a.getId() == idAluguel)
            .findFirst();
        if (aluguelOpt.isEmpty()) {
            return false;
        }
        Aluguel aluguel = aluguelOpt.get();
        if (!aluguel.cancelar()) return false;
        liberarItem(aluguel.getItem());
        return true;
    }

    public boolean devolverAluguel(int idAluguel) {
        Optional<Aluguel> aluguelOpt = buscarAluguel(idAluguel);
        if (aluguelOpt.isEmpty() || !aluguelOpt.get().devolver()) return false;
        liberarItem(aluguelOpt.get().getItem());
        return true;
    }

    private void liberarItem(Item item) {
        if (reservaController.reservaAtivaParaItem(item).isPresent()) {
            item.marcarComoReservado();
        } else {
            item.marcarComoDevolvido();
        }
    }

    public List<Aluguel> listarAlugueis() {
        return List.copyOf(alugueis);
    }

    public List<Aluguel> listarAlugueisDoCliente(Cliente cliente) {
        return alugueis.stream()
            .filter(aluguel -> aluguel.getCliente().equals(cliente))
            .toList();
    }

}
