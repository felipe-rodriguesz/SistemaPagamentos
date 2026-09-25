# SistemaPagamentos

Protótipo desktop em Java para demonstrar o gerenciamento local de um catálogo de itens, com operações de aluguel, reserva e acompanhamento de pagamentos simulados. O projeto foi desenvolvido como trabalho acadêmico na disciplina de **Programação Orientada a Objetos (POO)**.

## Proposta

O sistema reúne, em uma aplicação Swing, operações básicas para consultar itens de exemplo, registrar aluguéis e reservas, renovar ou cancelar aluguéis e visualizar relatórios. Ele demonstra como conceitos de orientação a objetos podem ser usados para representar entidades e regras de um domínio pequeno.

Os dados existem somente durante a execução. Pagamentos são simulados localmente: a aplicação não se conecta a instituições financeiras, não processa transações reais e não é um gateway de pagamentos.

## Funcionalidades

- Catálogo de itens de exemplo mantido em memória.
- Criação de aluguéis, renovação e cancelamento.
- Criação de reservas e controle de disponibilidade dos itens.
- Registro local de transações de demonstração e exibição de histórico/recibo.
- Registro e processamento local de pagamentos simulados no painel administrativo.
- Geração e visualização de relatórios de aluguéis e pagamentos.
- Interface desktop Java Swing com painéis de cliente e administrativo.

## Tecnologias e conceitos

- **Java 21** e APIs padrão do Java.
- **Java Swing** para a interface gráfica.
- Orientação a objetos, com classes de domínio e encapsulamento de estado.
- Organização de responsabilidades em `models`, `controllers` e `views`.
- Enums para categorias e estados, coleções para dados em memória e `Optional` em operações que podem não encontrar resultado.
- Streams para filtrar e transformar coleções.
- `LocalDate` e `LocalDateTime` para representar datas.
- Validações e transições simples de regras de negócio nos modelos e controladores.

O projeto não usa bibliotecas externas nem ferramenta de build. A separação em modelos, controladores e views é uma organização por responsabilidades; as telas chamam controladores e, em alguns fluxos, modelos diretamente, portanto não se apresenta como uma implementação formal de MVC.

## Organização do código

As classes estão em `src/main/java/com/SistemadePagamentoeAluguel/`, agrupadas por responsabilidade:

- `models/`: entidades e estados do domínio, como `Cliente`, `Item`, `Aluguel`, `Reserva`, `Pagamento` e `Relatorio`.
- `controllers/`: operações em memória para aluguéis, reservas, pagamentos, relatórios e administração.
- `views/`: janelas Swing; `SistemaAluguel` é a entrada principal e abre os painéis de cliente e administração.

Os packages declarados nas classes usam o prefixo `main.java.com.SistemadePagamentoeAluguel`, que deve ser considerado ao executar a classe principal.

## Compilar e executar

É necessário ter o **JDK 21** instalado. Como a interface usa Java Swing, a execução requer um ambiente desktop com suporte gráfico.

A partir da raiz do repositório, compile todas as fontes:

```bash
mkdir -p out
javac -d out $(find src/main/java -name '*.java')
```

Inicie a aplicação:

```bash
java -cp out main.java.com.SistemadePagamentoeAluguel.views.SistemaAluguel
```

As classes compiladas ficam em `out/`, diretório ignorado pelo Git.

## Validação

O repositório não possui testes automatizados nem framework de testes configurado. A validação disponível consiste em:

1. Compilar as fontes com o comando acima.
2. Executar a aplicação em um ambiente gráfico.
3. Verificar manualmente o catálogo, operações de aluguel e reserva, renovação e cancelamento, transações simuladas e geração de relatórios.

## Diagrama preliminar

Diagrama de classes produzido durante o desenvolvimento inicial do projeto. Por se tratar de uma versão preliminar, algumas classes e assinaturas podem diferir da implementação atual.

![Diagrama de classes UML preliminar](docs/diagrama-classes-preliminar.jpg)

## Limitações e escopo

- Os dados de catálogo, clientes, aluguéis, reservas, pagamentos e relatórios são mantidos em memória e se perdem ao encerrar a aplicação; não há persistência.
- Pagamentos são demonstrações locais e não têm integração externa.
- A aplicação é local e não oferece API ou integração com serviços externos.
- O painel administrativo não possui autenticação real ou persistente. O acesso atual é uma confirmação para demonstração local das funções administrativas.
- A classe de modelo `Administrador` ainda contém comparação de senha baseada em `hashCode()`, que não é adequada para proteger credenciais; ela não representa autenticação segura da aplicação.
- Este é um protótipo acadêmico, não um sistema financeiro ou serviço pronto para produção.
