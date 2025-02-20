package main.java.com.SistemadePagamentoeAluguel.models;

import java.time.LocalDate;
import java.util.Objects;

public class Aluguel {
    private final int id;
    private final LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusAluguel status;

    // Enum para definir os estados possíveis
    public enum StatusAluguel {
        ATIVO, CANCELADO, RENOVADO
    }

    // Construtor principal com validações
    public Aluguel(int id, LocalDate dataInicio, LocalDate dataFim, StatusAluguel status) {
        validarDatas(dataInicio, dataFim);
        
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = Objects.requireNonNull(status, "Status não pode ser nulo");
    }

    // Validação de datas
    private void validarDatas(LocalDate dataInicio, LocalDate dataFim) {
        Objects.requireNonNull(dataInicio, "Data de início não pode ser nula");
        Objects.requireNonNull(dataFim, "Data de fim não pode ser nula");
        
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de início");
        }
    }

    // Getters (sem setters para imutabilidade parcial)
    public int getId() { return id; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public StatusAluguel getStatus() { return status; }

    // Método para renovação com validação
    public void renovar(LocalDate novaDataFim) {
        if (status != StatusAluguel.ATIVO) {
            throw new IllegalStateException("Só é possível renovar aluguéis ativos");
        }
        if (novaDataFim.isBefore(dataFim)) {
            throw new IllegalArgumentException("Nova data deve ser posterior à data atual");
        }
        this.dataFim = novaDataFim;
        this.status = StatusAluguel.RENOVADO;
    }

    // Método para cancelamento
    public void cancelar() {
        if (status == StatusAluguel.CANCELADO) {
            throw new IllegalStateException("Aluguel já cancelado");
        }
        this.status = StatusAluguel.CANCELADO;
    }
}