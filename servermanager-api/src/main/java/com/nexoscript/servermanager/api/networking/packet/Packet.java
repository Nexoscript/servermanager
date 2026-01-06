package com.nexoscript.servermanager.api.networking.packet;

public abstract class Packet {
    private final int id;

    protected Packet(int id) {
        this.id = id;
    }

    public int id() {
        return id;
    }
}
