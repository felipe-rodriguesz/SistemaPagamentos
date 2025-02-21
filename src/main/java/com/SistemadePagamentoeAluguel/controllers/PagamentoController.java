package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.util.ArrayList;
import java.util.List;
import main.java.com.SistemadePagamentoeAluguel.models.Pagamento;

public class PagamentoController {
    private List<Pagamento> pagamentos;

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

    public List<Pagamento> listarPagamentos() {
        return pagamentos;
    }
}