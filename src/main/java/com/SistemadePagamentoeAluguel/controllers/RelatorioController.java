package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import main.java.com.SistemadePagamentoeAluguel.models.DateRange;
import main.java.com.SistemadePagamentoeAluguel.models.Relatorio;

public class RelatorioController {
    private static final AtomicInteger idCounter = new AtomicInteger(1000);
    
    public Relatorio gerarRelatorio(String tipo, DateRange periodo) {
        validarParametros(tipo, periodo);
        
        List<String> dados = new ArrayList<>();
        switch (tipo.toLowerCase()) {
            case "alugueis":
                dados.add("=== Relatório de Aluguéis ===");
                dados.add("Período: " + periodo.getInicio() + " até " + periodo.getFim());
                dados.addAll(gerarDadosAlugueis(periodo));
                break;
                
            case "pagamentos":
                dados.add("=== Relatório de Pagamentos ===");
                dados.add("Período: " + periodo.getInicio() + " até " + periodo.getFim());
                dados.addAll(gerarDadosPagamentos(periodo));
                break;
                
            default:
                throw new IllegalArgumentException("Tipo de relatório não suportado: " + tipo);
        }
        
        return new Relatorio(idCounter.getAndIncrement(), tipo, dados, new Date());
    }

    private void validarParametros(String tipo, DateRange periodo) {
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo não pode ser nulo/vazio");
        }
        if (periodo == null) {
            throw new IllegalArgumentException("Período não pode ser nulo");
        }
    }

    // Métodos mockados (implementação real dependeria de outras classes)
    private List<String> gerarDadosAlugueis(DateRange periodo) {
        return List.of("Aluguel 1: R$ 1500", "Aluguel 2: R$ 2000");
    }

    private List<String> gerarDadosPagamentos(DateRange periodo) {
        return List.of("Pagamento 1: Confirmado", "Pagamento 2: Pendente");
    }
}

