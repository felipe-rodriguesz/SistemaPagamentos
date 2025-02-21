package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import main.java.com.SistemadePagamentoeAluguel.models.Administrador;
import main.java.com.SistemadePagamentoeAluguel.models.Aluguel;

public class AdministradorController {
    private final List<Administrador> administradores = new ArrayList<>();
    private final AluguelController aluguelController;

    public AdministradorController(AluguelController aluguelController) {
        this.aluguelController = aluguelController;
        carregarAdminsPadrao();
    }

    private void carregarAdminsPadrao() {
        administradores.add(new Administrador("admin@sys.com", "admin123"));
    }

    public boolean autenticarAdmin(String email, String senha) {
        return administradores.stream()
            .anyMatch(admin -> admin.getEmail().equals(email) && admin.validarSenha(senha));
    }

    public List<Aluguel> listarAlugueisAtivos() {
        return aluguelController.listarAlugueisPorStatus(Aluguel.StatusAluguel.ATIVO); // implmentar
    }

    public Optional<Aluguel> buscarAluguelPorId(int id) {
        return aluguelController.buscarAluguel(id);
    }

    public boolean renovarAluguel(int idAluguel, LocalDate novaDataFim) {
        return aluguelController.renovarAluguel(idAluguel, novaDataFim); // implementar
    }

    public boolean cancelarAluguel(int idAluguel) {
        aluguelController.cancelarAluguel(idAluguel);
        return true;
    }
}