package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.ArrayList;
import java.util.List;

public class Historico {
    private List<Transacao> transacoes;
    private Cliente cliente;
    
    public Historico(Cliente cliente) {
        this.cliente = cliente;
        this.transacoes = new ArrayList<>();
    }
    
    public void adicionarTransacao(Transacao transacao) {
        this.transacoes.add(transacao);
        this.cliente.addTransacao(transacao);
    }
    
    public List<Transacao> getTransacoes() {
        return new ArrayList<>(transacoes);
    }
}