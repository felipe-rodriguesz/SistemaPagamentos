package com.sistemadepagamentoealuguel;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import com.sistemadepagamentoealuguel.models.Pagamento.MetodoPagamento;
import com.sistemadepagamentoealuguel.models.Pagamento.StatusPagamento;

class PagamentoControllerTest {
    @Test
    void pagamentoClienteApareceNaColecaoAdministrativaCompartilhada() {
        DomainFixture f = new DomainFixture();
        var pagamento = f.pagamentos.realizarPagamentoSimulado(f.ana, new BigDecimal("10.005"), MetodoPagamento.PIX)
            .orElseThrow();
        assertEquals(new BigDecimal("10.01"), pagamento.getValor());
        assertEquals(StatusPagamento.PROCESSADO, pagamento.getStatus());
        assertSame(pagamento, f.pagamentos.listarPagamentos().getFirst());
        assertEquals(1, f.pagamentos.listarPagamentosDoCliente(f.ana).size());
        assertTrue(f.pagamentos.listarPagamentosDoCliente(f.bia).isEmpty());
    }

    @Test
    void processarEstornarEValoresInvalidosRespeitamEstado() {
        DomainFixture f = new DomainFixture();
        var pagamento = f.pagamentos.registrarPagamento(f.ana, new BigDecimal("25"), MetodoPagamento.BOLETO);
        assertTrue(f.pagamentos.processarPagamento(pagamento.getId()));
        assertFalse(f.pagamentos.processarPagamento(pagamento.getId()));
        assertTrue(f.pagamentos.estornarPagamento(pagamento.getId()));
        assertEquals(StatusPagamento.ESTORNADO, pagamento.getStatus());
        assertFalse(f.pagamentos.estornarPagamento(-1));
        assertThrows(IllegalArgumentException.class,
            () -> f.pagamentos.registrarPagamento(f.ana, BigDecimal.ZERO, MetodoPagamento.PIX));
        assertThrows(IllegalArgumentException.class,
            () -> f.pagamentos.registrarPagamento(f.ana, new BigDecimal("-1"), MetodoPagamento.PIX));
        assertThrows(IllegalArgumentException.class,
            () -> f.pagamentos.registrarPagamento(f.ana, new BigDecimal("NaN"), MetodoPagamento.PIX));
    }
}
