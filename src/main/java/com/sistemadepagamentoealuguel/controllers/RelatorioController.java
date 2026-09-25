package com.sistemadepagamentoealuguel.controllers;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import com.sistemadepagamentoealuguel.models.Aluguel;
import com.sistemadepagamentoealuguel.models.DateRange;
import com.sistemadepagamentoealuguel.models.Pagamento;
import com.sistemadepagamentoealuguel.models.Relatorio;

public final class RelatorioController {
    private static final BigDecimal VALOR_DIARIA = new BigDecimal("100.00");

    private final IdGenerator idGenerator;
    private final AluguelController aluguelController;
    private final PagamentoController pagamentoController;

    public RelatorioController(IdGenerator idGenerator, AluguelController aluguelController,
            PagamentoController pagamentoController) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
        this.aluguelController = Objects.requireNonNull(aluguelController);
        this.pagamentoController = Objects.requireNonNull(pagamentoController);
    }

    public Relatorio gerarRelatorioAlugueis(DateRange periodo) {
        Objects.requireNonNull(periodo, "Período não pode ser nulo");
        List<Aluguel> registros = aluguelController.listarAlugueis().stream()
            .filter(aluguel -> dentroDoPeriodo(aluguel.getDataInicio(), periodo))
            .toList();

        long ativos = registros.stream().filter(Aluguel::isAtivo).count();
        long cancelados = registros.stream()
            .filter(aluguel -> aluguel.getStatus() == Aluguel.StatusAluguel.CANCELADO).count();
        long devolvidos = registros.stream()
            .filter(aluguel -> aluguel.getStatus() == Aluguel.StatusAluguel.DEVOLVIDO).count();
        BigDecimal receitaEstimada = registros.stream()
            .filter(Aluguel::isAtivo)
            .map(this::calcularValorEstimado)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> dados = new java.util.ArrayList<>();
        dados.add("Período: " + periodo.getInicio() + " a " + periodo.getFim());
        dados.add("Aluguéis ativos: " + ativos);
        dados.add("Aluguéis cancelados: " + cancelados);
        dados.add("Aluguéis devolvidos: " + devolvidos);
        dados.add("Valor estimado dos aluguéis ativos: " + formatarMoeda(receitaEstimada));
        registros.forEach(aluguel -> dados.add(String.format(
            "ID %d | %s | %s | %s a %s | %s",
            aluguel.getId(), aluguel.getCliente().getNome(), aluguel.getItem().getTitulo(),
            aluguel.getDataInicio(), aluguel.getDataFim(), aluguel.getStatus())));

        return new Relatorio(idGenerator.nextId(), "Aluguéis", periodo, registros.size(), dados);
    }

    public Relatorio gerarRelatorioPagamentos(DateRange periodo) {
        Objects.requireNonNull(periodo, "Período não pode ser nulo");
        List<Pagamento> registros = pagamentoController.listarPagamentos().stream()
            .filter(pagamento -> dentroDoPeriodo(pagamento.getData().toLocalDate(), periodo))
            .toList();

        BigDecimal processados = somar(registros, Pagamento.StatusPagamento.PROCESSADO);
        BigDecimal pendentes = somar(registros, Pagamento.StatusPagamento.PENDENTE);
        BigDecimal estornados = somar(registros, Pagamento.StatusPagamento.ESTORNADO);
        List<String> dados = new java.util.ArrayList<>();
        dados.add("Período: " + periodo.getInicio() + " a " + periodo.getFim());
        dados.add("Total processado: " + formatarMoeda(processados));
        dados.add("Total pendente: " + formatarMoeda(pendentes));
        dados.add("Total estornado: " + formatarMoeda(estornados));
        registros.forEach(pagamento -> dados.add(String.format(
            "ID %d | %s | %s | %s | %s",
            pagamento.getId(), pagamento.getCliente().getNome(), formatarMoeda(pagamento.getValor()),
            pagamento.getMetodo(), pagamento.getStatus())));

        return new Relatorio(idGenerator.nextId(), "Pagamentos", periodo, registros.size(), dados);
    }

    private BigDecimal somar(List<Pagamento> pagamentos, Pagamento.StatusPagamento status) {
        return pagamentos.stream()
            .filter(pagamento -> pagamento.getStatus() == status)
            .map(Pagamento::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calcularValorEstimado(Aluguel aluguel) {
        long dias = ChronoUnit.DAYS.between(aluguel.getDataInicio(), aluguel.getDataFim());
        return VALOR_DIARIA.multiply(BigDecimal.valueOf(dias));
    }

    private boolean dentroDoPeriodo(LocalDate data, DateRange periodo) {
        return !data.isBefore(periodo.getInicio()) && !data.isAfter(periodo.getFim());
    }

    private String formatarMoeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }
}
