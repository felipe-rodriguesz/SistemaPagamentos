package main.java.com.SistemadePagamentoeAluguel.models;

import java.util.Date;
import java.util.Objects;

public class DateRange {
    private final Date inicio;
    private final Date fim;
    
    public DateRange(Date inicio, Date fim) {
        this.inicio = Objects.requireNonNull(inicio, "Data inicial não pode ser nula");
        this.fim = Objects.requireNonNull(fim, "Data final não pode ser nula");
        
        if (inicio.after(fim)) {
            throw new IllegalArgumentException("Data inicial não pode ser posterior à data final");
        }
    }
    
    public Date getInicio() { return new Date(inicio.getTime()); }
    public Date getFim() { return new Date(fim.getTime()); }
}