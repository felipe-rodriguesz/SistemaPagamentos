package com.sistemadepagamentoealuguel;

import com.sistemadepagamentoealuguel.controllers.*;
import com.sistemadepagamentoealuguel.models.*;

final class DomainFixture {
    final IdGenerator ids = new IdGenerator();
    final CadastroController cadastro = new CadastroController(ids);
    final ReservaController reservas = new ReservaController(ids, cadastro);
    final AluguelController alugueis = new AluguelController(ids, cadastro, reservas);
    final PagamentoController pagamentos = new PagamentoController(ids, cadastro);
    final RelatorioController relatorios = new RelatorioController(ids, alugueis, pagamentos);
    final Cliente ana = cadastro.criarCliente("Ana", "ana@example.test");
    final Cliente bia = cadastro.criarCliente("Bia", "bia@example.test");
    final Item livro = cadastro.criarItem("Livro", Item.TipoItem.LIVRO);

    Aluguel alugar(Cliente cliente, Item item) {
        return alugueis.criarAluguel(cliente, item,
                java.time.LocalDate.of(2026, 1, 1), java.time.LocalDate.of(2026, 1, 8))
            .orElseThrow();
    }
}
