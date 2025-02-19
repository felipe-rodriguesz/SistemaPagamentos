package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.ArrayList;
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
        Pagamento pagamento = new Pagamento(this, valor, metodo);
        return PagamentoController.getInstance().processarPagamento(pagamento);
    }
    
    public boolean solicitarRenovacaoAluguel(Aluguel aluguel) {
        return aluguel.renovar(new Date());
    }
    
    public List<Transacao> consultarHistorico() {
        return new ArrayList<>(historico);
    }
    
    public void addTransacao(Transacao transacao) {
        this.historico.add(transacao);
    }
    
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}