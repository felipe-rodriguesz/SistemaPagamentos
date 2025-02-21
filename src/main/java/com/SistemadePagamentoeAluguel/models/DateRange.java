package main.java.com.SistemadePagamentoeAluguel.models;

import java.time.LocalDate;

public class DateRange {
    private final LocalDate inicio;
    private final LocalDate fim;
    
    public DateRange(LocalDate inicio, LocalDate fim) {
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à final");
        }
        this.inicio = inicio;
        this.fim = fim;
    }
    
    public LocalDate getInicio() { return inicio; }
    public LocalDate getFim() { return fim; }
}