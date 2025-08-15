package net.zcraft.util.status;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.zcraft.chat.ChatSerializer;
import net.zcraft.chat.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Getter
public class ServerStatus {

    private Version version = new Version("Custom 1.8", 47);
    private Players players = new Players(0, 0);
    private Component description = Component.text("A ZCraft Server");
    private String favicon = null;

    public ServerStatus setVersion(String name, int protocol) {
        this.version = new Version(name, protocol);
        return this;
    }

    public ServerStatus setPlayers(int online, int max) {
        this.players = new Players(online, max);
        return this;
    }

    public ServerStatus setDescription(String text) {
        this.description = Component.text(text);
        return this;
    }

    public ServerStatus setDescription(Component text)
    {
        this.description = text;

        return this;
    }

    public ServerStatus setFavicon(File file)
    {
        try
        {
            BufferedImage image = ImageIO.read(file);

            if (image.getWidth() != 64 || image.getHeight() != 64)
                throw new IllegalArgumentException("Favicon in wrong format");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            byte[] bytes = stream.toByteArray();

            this.favicon = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);

            return this;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public ServerStatus setDescriptionCenter(String text) {
        int width = 54;

        if (text.length() >= width) {
            this.description = Component.text(text);
        } else {
            int leftPadding = (width - text.length() / 2) / 2;
            String centered = " ".repeat(leftPadding) + text;
            this.description = Component.text(centered);
        }

        return this;
    }

    public ServerStatus addSamplePlayer(String name, String id) {
        if (players.sample == null) players.sample = new ArrayList<>();
        players.sample.add(new PlayerSample(name, id));
        return this;
    }

    /** Serialize to JSON string for the status packet */
    public String toJson() {
        return ChatSerializer.GSON.toJson(this);
    }

    /** Serialize to VarInt-prefixed byte array for sending to client */

    // ------------------ Nested Data Classes ------------------
    @Getter
    private static class Version
    {
        String name;
        int protocol;
        Version(String name, int protocol) { this.name = name; this.protocol = protocol; }
    }

    @Getter
    private static class Players
    {
        int max;
        int online;
        List<PlayerSample> sample = new ArrayList<>();
        Players(int online, int max) { this.online = online; this.max = max; }
    }

    @Getter
    private static class PlayerSample
    {
        String name, id;
        PlayerSample(String name, String id) { this.name = name; this.id = id; }
    }
}
