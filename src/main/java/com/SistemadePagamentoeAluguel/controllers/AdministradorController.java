package com.SistemadePagamentoeAluguel.controllers;

import com.SistemadePagamentoeAluguel.models.Administrador;
import com.SistemadePagamentoeAluguel.models.Aluguel;
import java.util.List;

public class AdministradorController {
    private List<Administrador> administradores;
    private List<Aluguel> alugueis;

    public AdministradorController(List<Administrador> administradores, List<Aluguel> alugueis) {
        this.administradores = administradores;
        this.alugueis = alugueis;
    }

    public boolean autenticarAdmin(String email, String senha) {
        for (Administrador admin : administradores) {
            if (admin.getEmail().equals(email) && admin.getSenha().equals(senha)) {
                return true;
            }
        }
        return false;
    }

    public void listarAlugueis() {
        for (Aluguel aluguel : alugueis) {
            System.out.println("Aluguel ID: " + aluguel.getId() + " | Status: " + aluguel.getStatus());
        }
    }

    public boolean editarAluguel(int aluguelId, Aluguel novosDados) {
        for (Aluguel aluguel : alugueis) {
            if (aluguel.getId() == aluguelId) {
                aluguel.setDataDevolucao(novosDados.getDataDevolucao());
                aluguel.setStatus(novosDados.getStatus());
                return true;
            }
        }
        return false;
    }

    public boolean forcarCancelamento(int aluguelId) {
        for (Aluguel aluguel : alugueis) {
            if (aluguel.getId() == aluguelId) {
                aluguel.cancelar();
                return true;
            }
        }
        return false;
    }
}