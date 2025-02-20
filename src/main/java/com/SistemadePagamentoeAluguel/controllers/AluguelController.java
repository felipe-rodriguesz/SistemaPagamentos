package com.SistemadePagamentoeAluguel.controllers;

import com.SistemadePagamentoeAluguel.models.Aluguel;
import com.SistemadePagamentoeAluguel.models.Cliente;
import com.SistemadePagamentoeAluguel.models.Item;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AluguelController {
    private List<Aluguel> alugueis;
    private int idCounter = 1;

    public AluguelController() {
        this.alugueis = new ArrayList<>();
    }

    public Aluguel alugarItem(Cliente cliente, Item item, int dias) {
        // Verifica se o item já está alugado
        for (Aluguel aluguel : alugueis) {
            if (aluguel.getStatus().equals("Ativo") && aluguel.getId() == item.getId()) {
                System.out.println("Item já está alugado!");
                return null;
            }
        }

        Date dataInicio = new Date();
        Date dataDevolucao = new Date(dataInicio.getTime() + (dias * 86400000L)); // Adiciona "dias" em milissegundos
        Aluguel novoAluguel = new Aluguel(idCounter++, dataInicio, dataDevolucao, "Ativo");
        alugueis.add(novoAluguel);
        return novoAluguel;
    }

    public boolean renovarAluguel(Aluguel aluguel, Date novaData) {
        if (novaData.after(aluguel.getDataDevolucao())) {
            return aluguel.renovar(novaData);
        }
        System.out.println("Nova data deve ser posterior à data de devolução atual.");
        return false;
    }

    public boolean cancelarAluguel(Aluguel aluguel) {
        if (new Date().before(aluguel.getDataDevolucao())) {
            aluguel.setStatus("Cancelado");
            return true;
        }
        System.out.println("Não é possível cancelar um aluguel já expirado.");
        return false;
    }

    public List<Aluguel> listarAlugueis(String filtro) {
        List<Aluguel> filtrados = new ArrayList<>();
        for (Aluguel aluguel : alugueis) {
            if (aluguel.getStatus().equalsIgnoreCase(filtro) || filtro.isEmpty()) {
                filtrados.add(aluguel);
            }
        }
        return filtrados;
    }

    public void registrarAluguel(Aluguel aluguel) {
        alugueis.add(aluguel);
    }
}