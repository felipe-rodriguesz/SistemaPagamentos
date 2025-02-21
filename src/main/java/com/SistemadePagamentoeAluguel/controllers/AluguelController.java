package main.java.com.SistemadePagamentoeAluguel.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import main.java.com.SistemadePagamentoeAluguel.models.Aluguel;
import main.java.com.SistemadePagamentoeAluguel.models.Cliente;
import main.java.com.SistemadePagamentoeAluguel.models.DateRange;
import main.java.com.SistemadePagamentoeAluguel.models.Item;
import main.java.com.SistemadePagamentoeAluguel.models.Relatorio;

public class AluguelController {
    private final AtomicInteger idCounter = new AtomicInteger(1);

    public Optional<Aluguel> criarAluguel(Cliente cliente, Item item, LocalDate dataInicio, LocalDate dataFim) {
        if (cliente == null || item == null) {
            System.out.println("[ERRO] Cliente ou item inválido");
            return Optional.empty();
        }

        if (!item.isDisponivel()) {
            System.out.println("[ERRO] Item já está alugado");
            return Optional.empty();
        }

        try {
            Aluguel aluguel = new Aluguel(
                idCounter.getAndIncrement(),
                cliente,
                item,
                dataInicio,
                dataFim
            );
            
            item.marcarComoAlugado();
            alugueis.add(aluguel);
            System.out.println("[SUCESSO] Aluguel ID: " + aluguel.getId());
            return Optional.of(aluguel);
            
        } catch (IllegalArgumentException e) {
            System.out.println("[ERRO] " + e.getMessage());
            return Optional.empty();
        }
    }

    public boolean renovarAluguel(Cliente cliente, Item item, LocalDate novaData) {
        Optional<Aluguel> aluguelOpt = alugueis.stream()
            .filter(a -> a.getCliente().equals(cliente) && a.getItem().equals(item))
            .findFirst();
        
        if (aluguelOpt.isPresent()) {
            Aluguel aluguel = aluguelOpt.get();
            if (aluguel.renovar(novaData)) {
                System.out.println("[SUCESSO] Aluguel renovado ID: " + aluguel.getId());
                return true;
            }
        }
        System.out.println("[ERRO] Aluguel não encontrado");
        return false;
    }

    public List<Aluguel> listarAlugueisAtivos() {
        return alugueis.stream()
            .filter(a -> a.getStatus() == Aluguel.StatusAluguel.ATIVO)
            .toList();
    }

    public Optional<Aluguel> buscarAluguel(int idAluguel) {
        return alugueis.stream()
            .filter(a -> a.getId() == idAluguel)
            .findFirst();
    }

    public boolean renovarAluguel(int idAluguel, LocalDate novaDataFim) {
        Optional<Aluguel> aluguelOpt = buscarAluguel(idAluguel);
        
        if (aluguelOpt.isPresent()) {
            Aluguel aluguel = aluguelOpt.get();
            aluguel.renovar(novaDataFim);
            return true;
        }
        return false;
    }

    public List<Aluguel> listarAlugueisPorStatus(Aluguel.StatusAluguel status) {
        return alugueis.stream()
            .filter(a -> a.getStatus() == status)
            .toList();
    }

    private final List<Aluguel> alugueis = new ArrayList<>();

    public List<Aluguel> getAlugueis() {
        return alugueis;
    }

    public void cancelarAluguel(int idAluguel) {
        alugueis.stream()
            .filter(a -> a.getId() == idAluguel)
            .findFirst()
            .ifPresent(Aluguel::cancelar);
    }

    public List<Aluguel> listarAlugueis() {
        return new ArrayList<>(alugueis); // Retorna cópia para evitar modificações externas
    }

    public Relatorio gerarRelatorio(String tipo, DateRange periodo) {
        int idCounter = 1;
        List<Aluguel> alugueisFiltrados = filtrarPorPeriodo(periodo);
        
        List<String> dados = new ArrayList<>();
        dados.add("=== RELATÓRIO DE ALUGUÉIS ===");
        dados.add("Período: " + periodo.getInicio() + " a " + periodo.getFim());
        dados.add("Total de Aluguéis: " + alugueisFiltrados.size());
        
        // Estatísticas
        long ativos = alugueisFiltrados.stream()
            .filter(a -> a.getStatus() == Aluguel.StatusAluguel.ATIVO)
            .count();
        
        long cancelados = alugueisFiltrados.stream()
            .filter(a -> a.getStatus() == Aluguel.StatusAluguel.CANCELADO)
            .count();
        
        double receitaTotal = calcularReceita(alugueisFiltrados);

        dados.add("Ativos: " + ativos);
        dados.add("Cancelados: " + cancelados);
        dados.add("Receita Total: R$ " + String.format("%.2f", receitaTotal));
        dados.add("\n=== DETALHES ===");

        // Detalhes
        alugueisFiltrados.forEach(a -> 
            dados.add(String.format(
                "ID: %d | Cliente: %s | Item: %s | Período: %s a %s | Status: %s",
                a.getId(),
                a.getCliente().getNome(),
                a.getItem().getTitulo(),
                a.getDataInicio(),
                a.getDataFim(),
                a.getStatus()
            ))
        );

        return new Relatorio(idCounter++, "Aluguéis", dados, LocalDate.now());
    }

    private List<Aluguel> filtrarPorPeriodo(DateRange periodo) {
        return alugueis.stream()
            .filter(a -> 
                a.getDataInicio().isAfter(periodo.getInicio().minusDays(1)) &&
                a.getDataInicio().isBefore(periodo.getFim().plusDays(1))
            )
            .collect(Collectors.toList());
    }

    private double calcularReceita(List<Aluguel> alugueis) {
        return alugueis.stream()
            .filter(a -> a.getStatus() == Aluguel.StatusAluguel.ATIVO)
            .mapToDouble(this::calcularValorAluguel)
            .sum();
    }

    private double calcularValorAluguel(Aluguel aluguel) {
        // Implementação de cálculo de valor (exemplo: R$ 100 por dia)
        long dias = aluguel.getDataFim().getDayOfYear() - aluguel.getDataInicio().getDayOfYear();
        return dias * 100.0;
    }
}