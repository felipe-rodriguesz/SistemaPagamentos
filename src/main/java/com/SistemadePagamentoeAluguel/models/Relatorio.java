package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Relatorio {
    private final int id;
    private final String tipo;
    private final List<String> dados;
    private final Date dataGeracao;

    public Relatorio(int id, String tipo, List<String> dados, Date dataGeracao) {
        this.id = id;
        this.tipo = tipo;
        this.dados = Collections.unmodifiableList(dados);
        this.dataGeracao = new Date(dataGeracao.getTime());
    }

    // Getters
    public int getId() { return id; }
    public String getTipo() { return tipo; }
    public List<String> getDados() { return dados; } // Lista imutável
    public Date getDataGeracao() { return new Date(dataGeracao.getTime()); }
}