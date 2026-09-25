package com.sistemadepagamentoealuguel.models;

import java.util.Objects;

public final class Cliente {
    private final int id;
    private final String nome;
    private final String email;

    public Cliente(int id, String nome, String email) {
        if (id <= 0) throw new IllegalArgumentException("ID inválido");
        this.id = id;
        this.nome = validarTexto(nome, "Nome");
        this.email = validarTexto(email, "E-mail");
    }

    private static String validarTexto(String valor, String nomeCampo) {
        String texto = Objects.requireNonNull(valor, nomeCampo + " não pode ser nulo").trim();
        if (texto.isEmpty()) throw new IllegalArgumentException(nomeCampo + " não pode ser vazio");
        return texto;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }

    @Override
    public String toString() {
        return nome + " (" + email + ")";
    }

    @Override
    public boolean equals(Object objeto) {
        return this == objeto || objeto instanceof Cliente outro && id == outro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
