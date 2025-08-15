package net.zcraft.util.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Getter
public class ServerStatus {

    private Version version = new Version("Custom 1.8", 47);
    private Players players = new Players(0, 0);
    private Description description = new Description("A Minecraft Server");

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public ServerStatus setVersion(String name, int protocol) {
        this.version = new Version(name, protocol);
        return this;
    }

    public ServerStatus setPlayers(int online, int max) {
        this.players = new Players(online, max);
        return this;
    }

    public ServerStatus setDescription(String text) {
        this.description = new Description(text);
        return this;
    }

    public ServerStatus addSamplePlayer(String name, String id) {
        if (players.sample == null) players.sample = new ArrayList<>();
        players.sample.add(new PlayerSample(name, id));
        return this;
    }

    /** Serialize to JSON string for the status packet */
    public String toJson() {
        return GSON.toJson(this);
    }

    /** Serialize to VarInt-prefixed byte array for sending to client */

    // ------------------ Nested Data Classes ------------------
    @Getter
    private static class Version {
        String name;
        int protocol;
        Version(String name, int protocol) { this.name = name; this.protocol = protocol; }
    }

    @Getter
    private static class Players {
        int max;
        int online;
        List<PlayerSample> sample = new ArrayList<>();
        Players(int online, int max) { this.online = online; this.max = max; }
    }

    @Getter
    private static class PlayerSample {
        String name, id;
        PlayerSample(String name, String id) { this.name = name; this.id = id; }
    }

    @Getter
    private static class Description {
        String text;
        Description(String text) { this.text = text; }
    }
}
