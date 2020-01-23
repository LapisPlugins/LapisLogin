package net.lapismc.lapislogin;

import java.util.UUID;

public class LapisLoginPlayer {

    private UUID uuid;
    private boolean registered;
    private boolean loggedIn;

    public LapisLoginPlayer(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean isRegistered() {
        return registered;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public UUID getUUID() {
        return uuid;
    }

}
