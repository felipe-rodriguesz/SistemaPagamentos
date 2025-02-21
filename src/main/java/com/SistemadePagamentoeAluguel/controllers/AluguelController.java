package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import main.java.com.SistemadePagamentoeAluguel.models.*;

public class AluguelController {
    private final List<Aluguel> alugueis = new ArrayList<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Optional<Aluguel> criarAluguel(Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (cliente == null || item == null) {
            System.out.println("[ERRO] Cliente ou item inválido");
            return Optional.empty();
        }

        if (!item.isDisponivel()) {
            System.out.println("[ERRO] Item já está alugado");
            return Optional.empty();
        }

        try {
            Aluguel aluguel = new Aluguel(
                idCounter.getAndIncrement(),
                cliente,
                item,
                dataInicio,
                dataFim
            );
            
            item.marcarComoAlugado();
            alugueis.add(aluguel);
            System.out.println("[SUCESSO] Aluguel ID: " + aluguel.getId());
            return Optional.of(aluguel);
            
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO] " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean cancelarAluguel(int idAluguel) {
        Optional<Aluguel> aluguelOpt = buscarAluguel(idAluguel);
        
        if (aluguelOpt.isPresent()) {
            Aluguel aluguel = aluguelOpt.get();
            if (aluguel.cancelar()) {
                aluguel.getItem().marcarComoDevolvido();
                return true;
            }
        }
        return false;
    }

    public List<Aluguel> listarAlugueisAtivos() {
        return alugueis.stream()
            .filter(a -> a.getStatus() == Aluguel.StatusAluguel.ATIVO)
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
            aluguel.renovar(novaDataFim);
            return true;
        }
        return false;
    }

    public List<Aluguel> listarAlugueisPorStatus(Aluguel.StatusAluguel status) {
        return alugueis.stream()
            .filter(a -> a.getStatus() == status)
            .toList();
    }
}