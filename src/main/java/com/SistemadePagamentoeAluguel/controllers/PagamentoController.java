package com.SistemadePagamentoeAluguel.controllers;

import com.SistemadePagamentoeAluguel.models.Pagamento;
import java.util.ArrayList;
import java.util.List;

public class PagamentoController {
    private List<Pagamento> pagamentos;
    private int idCounter = 1;

    public PagamentoController() {
        this.pagamentos = new ArrayList<>();
    }

    public Pagamento processarPagamento(double valor, String metodo) {
        if (valor <= 0) {
            System.out.println("Erro: O valor do pagamento deve ser positivo.");
            return null;
        }

        if (!metodoValido(metodo)) {
            System.out.println("Erro: Método de pagamento inválido.");
            return null;
        }

        Pagamento novoPagamento = new Pagamento(idCounter++, valor, metodo, "Processado");
        pagamentos.add(novoPagamento);
        return novoPagamento;
    }

    public boolean estornarPagamento(Pagamento pagamento) {
        if (pagamento.getStatus().equals("Processado")) {
            return pagamento.estornar();
        }
        System.out.println("Erro: Apenas pagamentos processados podem ser estornados.");
        return false;
    }

    public String emitirRecibo(Pagamento pagamento) {
        String recibo = "Recibo de Pagamento\n" +
                        "ID: " + pagamento.getId() + "\n" +
                        "Valor: R$ " + pagamento.getValor() + "\n" +
                        "Método: " + pagamento.getMetodo() + "\n" +
                        "Status: " + pagamento.getStatus();
        System.out.println(recibo);
        return recibo;
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