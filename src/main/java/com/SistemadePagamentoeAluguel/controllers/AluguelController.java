package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import main.java.com.SistemadePagamentoeAluguel.models.Aluguel;
import main.java.com.SistemadePagamentoeAluguel.models.Aluguel.StatusAluguel;
import main.java.com.SistemadePagamentoeAluguel.models.Cliente;
import main.java.com.SistemadePagamentoeAluguel.models.Item;

public class AluguelController {
    private List<Aluguel> alugueis;
    private int idCounter = 1;

    public AluguelController() {
        this.alugueis = new ArrayList<>();
    }

    public Aluguel alugarItem(Cliente cliente, Item item, int dias) {
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo");
        Objects.requireNonNull(item, "Item não pode ser nulo");
        
        // Verifica se o item já está alugado
        for (Aluguel aluguel : alugueis) {
            if (aluguel.getStatus() == StatusAluguel.ATIVO && aluguel.getId() == item.getId()) {
                System.out.println("Item já está alugado!");
                return null;
            }
        }

        LocalDate dataInicio = LocalDate.now();
        LocalDate dataFim = dataInicio.plusDays(dias);
        Aluguel novoAluguel = new Aluguel(idCounter++, dataInicio, dataFim, StatusAluguel.ATIVO);
        alugueis.add(novoAluguel);
        return novoAluguel;
    }

    public boolean renovarAluguel(Aluguel aluguel, LocalDate novaDataFim) {
        Objects.requireNonNull(aluguel, "Aluguel não pode ser nulo");
        Objects.requireNonNull(novaDataFim, "Nova data não pode ser nula");
        
        try {
            aluguel.renovar(novaDataFim);
            return true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean cancelarAluguel(Aluguel aluguel) {
        Objects.requireNonNull(aluguel, "Aluguel não pode ser nulo");
        
        try {
            aluguel.cancelar();
            return true;
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public List<Aluguel> listarAlugueis(StatusAluguel filtro) {
        List<Aluguel> filtrados = new ArrayList<>();
        for (Aluguel aluguel : alugueis) {
            if (filtro == null || aluguel.getStatus() == filtro) {
                filtrados.add(aluguel);
            }
        }
        return filtrados;
    }

    public void registrarAluguel(Aluguel aluguel) {
        Objects.requireNonNull(aluguel, "Aluguel não pode ser nulo");
        alugueis.add(aluguel);
    }
}