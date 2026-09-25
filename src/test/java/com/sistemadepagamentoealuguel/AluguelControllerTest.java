package com.sistemadepagamentoealuguel;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class AluguelControllerTest {
    @Test
    void alugaItemDisponivelERejeitaSegundoAluguelAtivo() {
        DomainFixture f = new DomainFixture();
        assertTrue(f.alugueis.criarAluguel(f.ana, f.livro, LocalDate.now(), LocalDate.now().plusDays(2)).isPresent());
        assertTrue(f.livro.isAlugado());
        assertTrue(f.alugueis.criarAluguel(f.bia, f.livro, LocalDate.now(), LocalDate.now().plusDays(2)).isEmpty());
    }

    @Test
    void renovacaoMantemAluguelAtivoEExigeFimPosterior() {
        DomainFixture f = new DomainFixture();
        var aluguel = f.alugar(f.ana, f.livro);
        assertTrue(f.alugueis.renovarAluguel(aluguel.getId(), LocalDate.of(2026, 1, 15)));
        assertTrue(aluguel.isAtivo());
        assertTrue(f.alugueis.listarAlugueisAtivos().contains(aluguel));
        assertFalse(f.alugueis.renovarAluguel(aluguel.getId(), LocalDate.of(2026, 1, 15)));
        assertFalse(f.alugueis.renovarAluguel(-1, LocalDate.of(2026, 1, 20)));
    }

    @Test
    void devolucaoECancelamentoLiberamOuReservamItemEIdInexistenteFalha() {
        DomainFixture f = new DomainFixture();
        var aluguel = f.alugar(f.ana, f.livro);
        assertFalse(f.alugueis.devolverAluguel(-1));
        assertTrue(f.alugueis.devolverAluguel(aluguel.getId()));
        assertTrue(f.livro.isDisponivel());
        assertFalse(f.alugueis.devolverAluguel(aluguel.getId()));

        var outro = f.cadastro.criarItem("Mesa", com.sistemadepagamentoealuguel.models.Item.TipoItem.MOVEL);
        var aluguelOutro = f.alugar(f.ana, outro);
        assertTrue(f.alugueis.cancelarAluguel(aluguelOutro.getId()));
        assertTrue(outro.isDisponivel());
        assertFalse(f.alugueis.cancelarAluguel(aluguelOutro.getId()));
    }

    @Test
    void rejeitaEntidadesQueNaoPertencemAoCadastroCompartilhado() {
        DomainFixture f = new DomainFixture();
        var externo = new com.sistemadepagamentoealuguel.models.Cliente(999, "Externo", "ext@example.test");
        assertTrue(f.alugueis.criarAluguel(externo, f.livro, LocalDate.now(), LocalDate.now().plusDays(1)).isEmpty());
    }
}
