package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Historico {
    private List<Transacao> transacoes;
    private Cliente cliente;

    public Historico(Cliente cliente) {
        this.cliente = Objects.requireNonNull(cliente, "Cliente não pode ser nulo");
        this.transacoes = new ArrayList<>();
    }

    public void adicionarTransacao(Transacao transacao) {
        Objects.requireNonNull(transacao, "Transação não pode ser nula");
        transacoes.add(transacao);
        cliente.addTransacao(transacao); // Atualiza o histórico do cliente
    }

    public List<Transacao> getTransacoes() {
        return Collections.unmodifiableList(transacoes);
    }

    public void limparHistorico() {
        transacoes.clear();
    }
}