package main.java.com.SistemadePagamentoeAluguel.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cliente {
    private int id;
    private String nome;
    private String email;
    private List<Transacao> historico;

    public Cliente(int id, String nome, String email) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.historico = new ArrayList<>();
    }

    public boolean realizarPagamento(double valor, String metodo) {
        Transacao pagamento = new Transacao("Pagamento via " + metodo, valor);
        historico.add(pagamento);
        return true; // Lógica simplificada (substitua pela integração real)
    }

    public boolean solicitarRenovacaoAluguel(Aluguel aluguel) {
        try {
            Transacao renovacao = new Transacao("Renovação de Aluguel", 0.0);
            historico.add(renovacao);
            aluguel.renovar(LocalDate.now().plusDays(7)); // Nova data de renovação
            return true; // Renovação bem-sucedida
        } catch (IllegalStateException | IllegalArgumentException e) {
            // Log de erro ou tratamento adequado
            return false; // Renovação falhou
        }
    }

    public List<Transacao> consultarHistorico() {
        return Collections.unmodifiableList(historico);
    }

    public void addTransacao(Transacao transacao) {
        historico.add(transacao);
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}