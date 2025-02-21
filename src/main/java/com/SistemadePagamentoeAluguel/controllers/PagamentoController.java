package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import main.java.com.SistemadePagamentoeAluguel.models.DateRange;
import main.java.com.SistemadePagamentoeAluguel.models.Pagamento;
import main.java.com.SistemadePagamentoeAluguel.models.Relatorio;

public class PagamentoController {
    private List<Pagamento> pagamentos;
    private int idCounter = 1;

    public PagamentoController() {
        this.pagamentos = new ArrayList<>();
    }

    public Pagamento processarPagamento(double valor, String metodo) {
        
        if (valor <= 0) {
            throw new IllegalArgumentException("Erro: O valor do pagamento deve ser positivo.");
        }

        if (!metodoValido(metodo)) {
            throw new IllegalArgumentException("Erro: Método de pagamento inválido.");
        }

        Pagamento novoPagamento = new Pagamento(valor, metodo);
        novoPagamento.processar();
        pagamentos.add(novoPagamento);
        return novoPagamento;
    }

    public boolean estornarPagamento(int pagamentoId) {
        for (Pagamento pagamento : pagamentos) {
            if (pagamento.getId() == pagamentoId) {
                try {
                    pagamento.estornar();
                    return true;
                } catch (IllegalStateException e) {
                    System.out.println("Erro: " + e.getMessage());
                    return false;
                }
            }
        }
        System.out.println("Erro: Pagamento não encontrado.");
        return false;
    }

    public String emitirRecibo(Pagamento pagamento) {
        return String.format(
            "Recibo de Pagamento\nID: %d\nValor: R$ %.2f\nMétodo: %s\nStatus: %s\nData: %s",
            pagamento.getId(), pagamento.getValor(), pagamento.getMetodo(), pagamento.getStatus(), pagamento.getData()
        );
    }

    private boolean metodoValido(String metodo) {
        return metodo.equalsIgnoreCase("Cartão de Crédito") ||
               metodo.equalsIgnoreCase("Boleto") ||
               metodo.equalsIgnoreCase("Pix") ||
               metodo.equalsIgnoreCase("Dinheiro");
    }

    public void registrarPagamento(Pagamento pagamento) {
        pagamentos.add(pagamento);
    }
    
    public void processarPagamento(Pagamento pagamento) {
        pagamento.processar();
    }

    public List<Pagamento> listarPagamentos() {
        return new ArrayList<>(pagamentos); // Retorna cópia
    }

    public Relatorio gerarRelatorio(String tipo, DateRange periodo) {
        List<Pagamento> pagamentosFiltrados = filtrarPorPeriodo(periodo);
        
        List<String> dados = new ArrayList<>();
        dados.add("=== RELATÓRIO DE PAGAMENTOS ===");
        dados.add("Período: " + periodo.getInicio() + " a " + periodo.getFim());
        dados.add("Total de Pagamentos: " + pagamentosFiltrados.size());
        
        // Estatísticas
        double totalProcessado = pagamentosFiltrados.stream()
            .filter(p -> p.getStatus() == Pagamento.StatusPagamento.PROCESSADO)
            .mapToDouble(Pagamento::getValor)
            .sum();
        
        double totalPendente = pagamentosFiltrados.stream()
            .filter(p -> p.getStatus() == Pagamento.StatusPagamento.PENDENTE)
            .mapToDouble(Pagamento::getValor)
            .sum();
        
        double totalEstornado = pagamentosFiltrados.stream()
            .filter(p -> p.getStatus() == Pagamento.StatusPagamento.ESTORNADO)
            .mapToDouble(Pagamento::getValor)
            .sum();

        dados.add("Total Processado: R$ " + String.format("%.2f", totalProcessado));
        dados.add("Total Pendente: R$ " + String.format("%.2f", totalPendente));
        dados.add("Total Estornado: R$ " + String.format("%.2f", totalEstornado));
        dados.add("\n=== DETALHES ===");

        // Detalhes
        pagamentosFiltrados.forEach(p -> 
            dados.add(String.format(
                "ID: %d | Valor: R$ %.2f | Método: %s | Status: %s | Data: %s",
                p.getId(),
                p.getValor(),
                p.getMetodo(),
                p.getStatus(),
                p.getData()
            ))
        );

        return new Relatorio(idCounter++, "Pagamentos", dados, LocalDate.now());
    }

    private List<Pagamento> filtrarPorPeriodo(DateRange periodo) {
        return pagamentos.stream()
            .filter(p -> 
                p.getData().isAfter(periodo.getInicio().minusDays(1).atStartOfDay()) &&
                p.getData().isBefore(periodo.getFim().plusDays(1).atStartOfDay())
            )
            .collect(Collectors.toList());
    }
}