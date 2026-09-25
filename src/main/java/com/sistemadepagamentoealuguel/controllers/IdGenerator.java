package com.sistemadepagamentoealuguel.controllers;

import java.util.concurrent.atomic.AtomicInteger;

public final class IdGenerator {
    private final AtomicInteger nextId = new AtomicInteger(1);

    public int nextId() {
        return nextId.getAndIncrement();
    }
}
