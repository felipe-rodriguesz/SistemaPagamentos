package com.sistemadepagamentoealuguel;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import com.sistemadepagamentoealuguel.models.DateRange;
import com.sistemadepagamentoealuguel.models.Pagamento.MetodoPagamento;

class RelatorioControllerTest {
    @Test
    void filtroInclusivoPorDataDeInicioFuncionaEmPeriodoQueCruzaAno() {
        DomainFixture f = new DomainFixture();
        var inicio = f.alugueis.criarAluguel(f.ana, f.livro,
            LocalDate.of(2025, 12, 31), LocalDate.of(2026, 1, 5)).orElseThrow();
        var fimItem = f.cadastro.criarItem("Projetor", com.sistemadepagamentoealuguel.models.Item.TipoItem.EQUIPAMENTO);
        var fim = f.alugueis.criarAluguel(f.bia, fimItem,
            LocalDate.of(2026, 1, 2), LocalDate.of(2026, 1, 5)).orElseThrow();
        var iniciadoAntes = f.cadastro.criarItem("Mesa", com.sistemadepagamentoealuguel.models.Item.TipoItem.MOVEL);
        var aluguelIniciadoAntes = f.alugueis.criarAluguel(f.ana, iniciadoAntes,
            LocalDate.of(2025, 12, 30), LocalDate.of(2026, 1, 1)).orElseThrow();

        var periodo = new DateRange(LocalDate.of(2025, 12, 31), LocalDate.of(2026, 1, 2));
        var relatorioAlugueis = f.relatorios.gerarRelatorioAlugueis(periodo);

        assertEquals(2, relatorioAlugueis.getQuantidadeRegistros());
        assertTrue(contemRegistro(relatorioAlugueis, inicio.getId()));
        assertTrue(contemRegistro(relatorioAlugueis, fim.getId()));
        assertFalse(contemRegistro(relatorioAlugueis, aluguelIniciadoAntes.getId()));
    }

    @Test
    void estimativaUsaPrazoIntegralEDiariaAtualDeCemReais() {
        DomainFixture f = new DomainFixture();
        f.alugueis.criarAluguel(f.ana, f.livro,
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 4)).orElseThrow();

        var relatorio = f.relatorios.gerarRelatorioAlugueis(
            new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 1)));

        assertTrue(relatorio.getDados().stream()
            .anyMatch(linha -> linha.startsWith("Valor estimado dos aluguéis ativos:") && linha.contains("300,00")));
    }

    @Test
    void relatorioDePagamentosConsultaRegistrosReaisNoPeriodo() {
        DomainFixture f = new DomainFixture();
        f.pagamentos.realizarPagamentoSimulado(f.ana, new BigDecimal("12.50"), MetodoPagamento.PIX);
        var periodo = new DateRange(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        var relatorio = f.relatorios.gerarRelatorioPagamentos(periodo);

        assertEquals(1, relatorio.getQuantidadeRegistros());
        assertTrue(relatorio.getDados().stream().anyMatch(linha -> linha.contains("12,50")));
    }

    @Test
    void relatorioDeAlugueisExpõeDadosReaisComoColecaoImutavel() {
        DomainFixture f = new DomainFixture();
        f.alugueis.criarAluguel(f.ana, f.livro,
            LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 4)).orElseThrow();
        var relatorio = f.relatorios.gerarRelatorioAlugueis(
            new DateRange(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 4)));

        assertEquals(1, relatorio.getQuantidadeRegistros());
        assertThrows(UnsupportedOperationException.class, () -> relatorio.getDados().add("alteração"));
    }

    private boolean contemRegistro(com.sistemadepagamentoealuguel.models.Relatorio relatorio, int id) {
        return relatorio.getDados().stream().anyMatch(linha -> linha.startsWith("ID " + id + " |"));
    }

    @Test
    void colecoesDeControladoresNaoPermitemAlterarDadosInternos() {
        DomainFixture f = new DomainFixture();
        assertThrows(UnsupportedOperationException.class, () -> f.cadastro.listarItens().clear());
        assertThrows(UnsupportedOperationException.class, () -> f.alugueis.listarAlugueis().clear());
        assertThrows(UnsupportedOperationException.class, () -> f.pagamentos.listarPagamentos().clear());
    }
}
