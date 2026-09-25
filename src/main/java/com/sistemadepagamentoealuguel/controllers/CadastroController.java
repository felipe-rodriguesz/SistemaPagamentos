package com.sistemadepagamentoealuguel.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import com.sistemadepagamentoealuguel.models.Cliente;
import com.sistemadepagamentoealuguel.models.Item;

public final class CadastroController {
    private final IdGenerator idGenerator;
    private final List<Cliente> clientes = new ArrayList<>();
    private final List<Item> itens = new ArrayList<>();

    public CadastroController(IdGenerator idGenerator) {
        this.idGenerator = Objects.requireNonNull(idGenerator);
    }

    public Cliente criarCliente(String nome, String email) {
        Cliente cliente = new Cliente(idGenerator.nextId(), nome, email);
        boolean emailJaCadastrado = clientes.stream()
            .anyMatch(cadastrado -> cadastrado.getEmail().equalsIgnoreCase(cliente.getEmail()));
        if (emailJaCadastrado) throw new IllegalArgumentException("E-mail já cadastrado");
        clientes.add(cliente);
        return cliente;
    }

    public Item criarItem(String titulo, Item.TipoItem tipo) {
        Item item = new Item(idGenerator.nextId(), titulo, tipo);
        itens.add(item);
        return item;
    }

    public List<Cliente> listarClientes() {
        return List.copyOf(clientes);
    }

    public List<Item> listarItens() {
        return List.copyOf(itens);
    }

    public List<Item> listarItensDisponiveis() {
        return itens.stream().filter(Item::isDisponivel).toList();
    }

    public Optional<Cliente> buscarCliente(int id) {
        return clientes.stream().filter(cliente -> cliente.getId() == id).findFirst();
    }

    public Optional<Item> buscarItem(int id) {
        return itens.stream().filter(item -> item.getId() == id).findFirst();
    }

    public boolean contemCliente(Cliente cliente) {
        return cliente != null && buscarCliente(cliente.getId()).orElse(null) == cliente;
    }

    public boolean contemItem(Item item) {
        return item != null && buscarItem(item.getId()).orElse(null) == item;
    }
}
