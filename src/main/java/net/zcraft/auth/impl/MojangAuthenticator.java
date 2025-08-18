package net.zcraft.auth.impl;

import net.zcraft.ZCraftServer;
import net.zcraft.auth.IAuthProvider;
import net.zcraft.crypto.Encryption;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.util.AuthUtils;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MojangAuthenticator implements IAuthProvider
{
    private static final String MOJANG_AUTH_URL = "https://session.minecraft.net/game/checkserver.jsp?user=%s&serverId=%s";
    @Override
    public boolean isAuthenticated(ZCraftConnection connection)
    {
        if (!connection.has("secret") || connection.getPlayer() == null)
            return false;

        byte[] secret = connection.get("secret");
        connection.remove("secret");

        String url = String.format(MOJANG_AUTH_URL, connection.getPlayer().getName(), AuthUtils.getServerHash("", secret, Encryption.getPublicKey()));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try
        {
            HttpResponse<String> response = ZCraftServer.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            return "YES".equals(response.body().trim());
        }
        catch (IOException | InterruptedException e)
        {
            Logger.error(e);
        }

        return false;
    }
}
