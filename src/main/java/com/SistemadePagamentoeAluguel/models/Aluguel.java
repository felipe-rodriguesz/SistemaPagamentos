package main.java.com.SistemadePagamentoeAluguel.models;

import java.time.LocalDate;

public class Aluguel {
    private int id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private double valorDiario;

    public Aluguel(int id, LocalDate dataInicio, LocalDate dataFim, double valorDiario) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.valorDiario = valorDiario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public double getValorDiario() {
        return valorDiario;
    }

    public void setValorDiario(double valorDiario) {
        this.valorDiario = valorDiario;
    }

    public double calcularValorTotal() {
        long dias = java.time.temporal.ChronoUnit.DAYS.between(dataInicio, dataFim);
        return dias * valorDiario;
    }
}