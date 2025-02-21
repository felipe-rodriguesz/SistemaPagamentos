package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.util.List;
import java.util.Objects;
import main.java.com.SistemadePagamentoeAluguel.models.Administrador;
import main.java.com.SistemadePagamentoeAluguel.models.Aluguel;
import main.java.com.SistemadePagamentoeAluguel.models.Aluguel.StatusAluguel;

public class AdministradorController {
    private List<Administrador> administradores;
    private List<Aluguel> alugueis;

    public AdministradorController(List<Administrador> administradores, List<Aluguel> alugueis) {
        this.administradores = Objects.requireNonNull(administradores, "Lista de administradores não pode ser nula");
        this.alugueis = Objects.requireNonNull(alugueis, "Lista de aluguéis não pode ser nula");
    }

    public boolean autenticarAdmin(String email, String senha) {
        return administradores.stream()
                .anyMatch(admin -> admin.getEmail().equals(email) && admin.getSenha().equals(senha));
    }

    public void listarAlugueis() {
        alugueis.forEach(aluguel -> 
            System.out.println("Aluguel ID: " + aluguel.getId() + " | Status: " + aluguel.getStatus()));
    }

    public boolean editarAluguel(int aluguelId, Aluguel novosDados) {
        for (Aluguel aluguel : alugueis) {
            if (aluguel.getId() == aluguelId) {
                aluguel.renovar(novosDados.getDataFim());
                return true;
            }
        }
        return false;
    }

    public boolean forcarCancelamento(int aluguelId) {
        for (Aluguel aluguel : alugueis) {
            if (aluguel.getId() == aluguelId && aluguel.getStatus() != StatusAluguel.CANCELADO) {
                aluguel.cancelar();
                return true;
            }
        }
        return false;
    }
}