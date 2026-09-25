package com.sistemadepagamentoealuguel;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ReservaControllerTest {
    @Test
    void reservaEsperaAluguelAtivoEDevolucaoMantemItemReservadoAoCliente() {
        DomainFixture f = new DomainFixture();
        var aluguel = f.alugar(f.ana, f.livro);
        var reserva = f.reservas.criarReserva(f.bia, f.livro,
            LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 15)).orElseThrow();

        assertTrue(f.reservas.criarReserva(f.ana, f.livro, LocalDate.now(), LocalDate.now().plusDays(5)).isEmpty());
        assertFalse(f.alugueis.podeAlugar(f.bia, f.livro));
        assertTrue(f.alugueis.devolverAluguel(aluguel.getId()));
        assertTrue(f.livro.isReservado());
        assertFalse(f.alugueis.podeAlugar(f.ana, f.livro));
        assertTrue(f.alugueis.podeAlugar(f.bia, f.livro));

        var aluguelDaReserva = f.alugueis.criarAluguel(f.bia, f.livro,
            LocalDate.of(2026, 1, 8), LocalDate.of(2026, 1, 15)).orElseThrow();
        assertTrue(f.livro.isAlugado());
        assertEquals(com.sistemadepagamentoealuguel.models.Reserva.StatusReserva.CONVERTIDA, reserva.getStatus());
        assertEquals(f.bia, aluguelDaReserva.getCliente());
    }

    @Test
    void cancelarReservaAposLiberacaoDisponibilizaItemEPermiteAluguel() {
        DomainFixture f = new DomainFixture();
        var aluguel = f.alugar(f.ana, f.livro);
        var reserva = f.reservas.criarReserva(f.bia, f.livro,
            LocalDate.now(), LocalDate.now().plusDays(7)).orElseThrow();
        assertTrue(f.alugueis.cancelarAluguel(aluguel.getId()));
        assertTrue(f.livro.isReservado());
        assertTrue(f.reservas.cancelarReserva(reserva.getId()));
        assertTrue(f.livro.isDisponivel());
        assertTrue(f.alugueis.podeAlugar(f.ana, f.livro));
        assertFalse(f.reservas.cancelarReserva(reserva.getId()));
    }

    @Test
    void reservaComDataNulaERejeitadaAposValidarClienteEItemAlugado() {
        DomainFixture f = new DomainFixture();
        f.alugar(f.ana, f.livro);

        assertTrue(f.reservas.criarReserva(f.bia, f.livro, null, LocalDate.now()).isEmpty());
        assertTrue(f.reservas.listarReservasAtivas().isEmpty());
    }

    @Test
    void idDeReservaInexistenteEPeriodoInvertidoSaoRejeitados() {
        DomainFixture f = new DomainFixture();
        assertFalse(f.reservas.cancelarReserva(-1));
        assertThrows(IllegalArgumentException.class,
            () -> new com.sistemadepagamentoealuguel.models.DateRange(LocalDate.now().plusDays(1), LocalDate.now()));
    }
}
