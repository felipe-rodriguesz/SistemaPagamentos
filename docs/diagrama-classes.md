# Diagrama de classes — versão atual

Este diagrama editável representa as classes e relações presentes no código atual. A fonte usa Mermaid e pode ser visualizada diretamente no GitHub.

```mermaid
classDiagram
direction LR

class Cliente {
  -int id
  -String nome
  -String email
}

class DateRange {
  -LocalDate inicio
  -LocalDate fim
}

class Item {
  -int id
  -String titulo
  -TipoItem tipo
  -EstadoItem estado
  +marcarComoAlugado()
  +marcarComoReservado()
  +marcarComoDevolvido()
}

class TipoItem {
  <<enumeration>>
  LIVRO
  EQUIPAMENTO
  MOVEL
  OUTROS
}

class EstadoItem {
  <<enumeration>>
  DISPONIVEL
  RESERVADO
  ALUGADO
}

class Aluguel {
  -int id
  -LocalDate dataInicio
  -LocalDate dataFim
  -StatusAluguel status
  +isAtivo() boolean
  +renovar(LocalDate) boolean
  +cancelar() boolean
  +devolver() boolean
}

class StatusAluguel {
  <<enumeration>>
  ATIVO
  RENOVADO
  CANCELADO
  DEVOLVIDO
}

class Reserva {
  -int id
  -LocalDate dataInicio
  -LocalDate dataFim
  -StatusReserva status
  +cancelar() boolean
  +converterEmAluguel() boolean
}

class StatusReserva {
  <<enumeration>>
  ATIVA
  CANCELADA
  CONVERTIDA
}

class Pagamento {
  -int id
  -BigDecimal valor
  -MetodoPagamento metodo
  -LocalDateTime data
  -StatusPagamento status
  +processar() void
  +estornar() void
}

class MetodoPagamento {
  <<enumeration>>
  CARTAO_CREDITO
  BOLETO
  PIX
  DINHEIRO
}

class StatusPagamento {
  <<enumeration>>
  PENDENTE
  PROCESSADO
  ESTORNADO
}

class Relatorio {
  -int id
  -String tipo
  -DateRange periodo
  -int quantidadeRegistros
  -List dados
  -LocalDateTime geradoEm
}

class CadastroController {
  -List clientes
  -List itens
  +criarCliente(String, String) Cliente
  +criarItem(String, TipoItem) Item
  +listarClientes() List
  +listarItens() List
}

class IdGenerator {
  -AtomicInteger nextId
  +nextId() int
}

class AluguelController {
  -List alugueis
  +criarAluguel(Cliente, Item, LocalDate, LocalDate) Optional
  +renovarAluguel(int, LocalDate) boolean
  +cancelarAluguel(int) boolean
  +devolverAluguel(int) boolean
  +listarAlugueis() List
}

class ReservaController {
  -List reservas
  +criarReserva(Cliente, Item, LocalDate, LocalDate) Optional
  +cancelarReserva(int) boolean
  +listarReservasAtivas() List
}

class PagamentoController {
  -List pagamentos
  +registrarPagamento(Cliente, BigDecimal, MetodoPagamento) Pagamento
  +realizarPagamentoSimulado(Cliente, BigDecimal, MetodoPagamento) Optional
  +processarPagamento(int) boolean
  +estornarPagamento(int) boolean
  +listarPagamentos() List
}

class RelatorioController {
  +gerarRelatorioAlugueis(DateRange) Relatorio
  +gerarRelatorioPagamentos(DateRange) Relatorio
}

class SistemaAluguel {
  -Cliente clientePadrao
  +main(String[]) void
}

class ClienteView {
  -Cliente cliente
  +exibirPainelCliente() void
}

class AdministradorView {
  +solicitarLogin() boolean
  +exibirPainelControle() void
}

class JFrame
<<Swing>> JFrame

Item --> TipoItem : tipo
Item --> EstadoItem : estado
Aluguel --> StatusAluguel : status
Reserva --> StatusReserva : status
Pagamento --> MetodoPagamento : metodo
Pagamento --> StatusPagamento : status

Cliente "1" <-- "0..*" Aluguel : cliente
Item "1" <-- "0..*" Aluguel : item
Cliente "1" <-- "0..*" Reserva : cliente
Item "1" <-- "0..*" Reserva : item
Cliente "1" <-- "0..*" Pagamento : cliente
DateRange "1" <-- "0..*" Relatorio : periodo

CadastroController "1" o-- "0..*" Cliente : clientes
CadastroController "1" o-- "0..*" Item : itens
AluguelController "1" o-- "0..*" Aluguel : alugueis
ReservaController "1" o-- "0..*" Reserva : reservas
PagamentoController "1" o-- "0..*" Pagamento : pagamentos

AluguelController --> CadastroController : consulta
AluguelController --> ReservaController : coordena reserva
ReservaController --> CadastroController : consulta
PagamentoController --> CadastroController : valida cliente
RelatorioController --> AluguelController : consulta dados
RelatorioController --> PagamentoController : consulta dados
RelatorioController ..> Relatorio : gera

CadastroController --> IdGenerator : usa
AluguelController --> IdGenerator : usa
ReservaController --> IdGenerator : usa
PagamentoController --> IdGenerator : usa
RelatorioController --> IdGenerator : usa

SistemaAluguel *-- IdGenerator
SistemaAluguel *-- CadastroController
SistemaAluguel *-- ReservaController
SistemaAluguel *-- AluguelController
SistemaAluguel *-- PagamentoController
SistemaAluguel *-- RelatorioController
SistemaAluguel --> Cliente : clientePadrao
SistemaAluguel ..> ClienteView : cria
SistemaAluguel ..> AdministradorView : cria

ClienteView --|> JFrame
AdministradorView --|> JFrame
SistemaAluguel --|> JFrame
ClienteView --> Cliente : cliente atual
ClienteView --> CadastroController
ClienteView --> ReservaController
ClienteView --> AluguelController
ClienteView --> PagamentoController
AdministradorView --> CadastroController
AdministradorView --> AluguelController
AdministradorView --> PagamentoController
AdministradorView --> RelatorioController
```

As multiplicidades indicam as coleções e referências explícitas do código: cada aluguel, reserva e pagamento guarda um cliente; aluguéis e reservas guardam um item; cada relatório guarda um intervalo. `Cliente` não contém coleções próprias desses registros. As associações tracejadas indicam criação/uso sem retenção do objeto correspondente. Os relatórios guardam linhas de texto, não referências diretas aos aluguéis ou pagamentos consultados.
