package com.sistemadepagamentoealuguel.models;

import java.time.LocalDate;

public class DateRange {
    private final LocalDate inicio;
    private final LocalDate fim;
    
    public DateRange(LocalDate inicio, LocalDate fim) {
        java.util.Objects.requireNonNull(inicio, "Data inicial não pode ser nula");
        java.util.Objects.requireNonNull(fim, "Data final não pode ser nula");
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à final");
        }
        this.inicio = inicio;
        this.fim = fim;
    }
    
    public LocalDate getInicio() { return inicio; }
    public LocalDate getFim() { return fim; }
}
