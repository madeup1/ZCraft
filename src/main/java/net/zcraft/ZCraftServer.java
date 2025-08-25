package net.zcraft;

import lombok.Getter;
import lombok.Setter;
import net.zcraft.auth.AuthManager;
import net.zcraft.auth.impl.MojangAuthenticator;
import net.zcraft.chat.ChatColor;
import net.zcraft.chat.ChatPosition;
import net.zcraft.chat.Component;
import net.zcraft.commands.CommandManager;
import net.zcraft.crypto.Encryption;
import net.zcraft.events.EventBus;
import net.zcraft.events.impl.ChatEvent;
import net.zcraft.events.impl.EndTickEvent;
import net.zcraft.events.impl.StartTickEvent;
import net.zcraft.init.ZCraftSettings;
import net.zcraft.instance.InstanceManager;
import net.zcraft.instance.impl.BasicInstance;
import net.zcraft.loop.LoopManager;
import net.zcraft.materials.Material;
import net.zcraft.materials.MaterialImpl;
import net.zcraft.materials.Materials;
import net.zcraft.network.ConnectionManager;
import net.zcraft.network.NetworkListener;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.network.buffers.ReadBuffer;
import net.zcraft.network.buffers.Types;
import net.zcraft.protocol.PacketManager;
import net.zcraft.protocol.server.play.ServerChatMessage;
import net.zcraft.protocol.server.play.ServerKeepAlive;
import net.zcraft.protocol.server.play.ServerSetSlot;
import net.zcraft.util.*;
import net.zcraft.util.status.ServerStatus;
import net.zcraft.util.threading.MultiExecutor;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.logging.LogManager;

public class ZCraftServer
{
    @Getter
    private static ZCraftSettings settings = ZCraftSettings.DEFAULT;
    // managers
    @Getter
    private static ConnectionManager connections;
    @Getter
    private static InstanceManager instanceManager;
    @Getter
    private static PacketManager packetManager;
    @Getter
    private static CommandManager commandManager;
    @Getter
    private static AuthManager authManager;
    @Getter
    private static HttpClient httpClient;
    @Getter
    private static NetworkListener listener;
    @Getter
    private static LoopManager loopManager;
    @Getter
    private static MultiExecutor executor;
    @Getter
    private static EventBus eventBus;
    @Getter
    private static Random random;
    @Getter
    private static long ticks;

    // variables

    /*
        THIS IS IN A VIRTUAL THREAD, YOU CAN USE CONNECTIONS AND DELAY IT.
     */

    // ---------------------------------------------
    // 45 characters wide.

    @Getter @Setter
    private static Function<ZCraftConnection, ServerStatus> motdHandler = (conn) -> {
        ServerStatus status = new ServerStatus();

        Component component = Component.text("               Welcome To ZCraft").color(ChatColor.yellow).bold(true).withExtra(Component.text("\n           WE ARE IN DEVELOPMENT").color(ChatColor.red));

        status.setDescription(component)
                .setVersion("ZCraft Beta", 47)
                .setPlayers(941, -1);

        File favicon = new File("favicon.png");

        if (favicon.exists())
            status.setFavicon(favicon);

        return status;
    };

    @SuppressWarnings("")
    public static void init(@Nullable ZCraftSettings _settings)
    {
        LogManager.getLogManager().reset();
        NanoTimer timer = new NanoTimer();
        settings = _settings == null ? ZCraftSettings.DEFAULT : _settings;

        connections = new ConnectionManager();
        loopManager = new LoopManager();
        packetManager = new PacketManager();
        executor = new MultiExecutor(settings.getThreads());
        listener = new NetworkListener(settings.getPort());
        instanceManager = new InstanceManager();
        authManager = new AuthManager();
        commandManager = new CommandManager();

        instanceManager.setDefaultInstance(new BasicInstance(Gamemode.Creative, Difficulty.Easy, Dimension.End, LevelType.Flat));

        authManager.setProvider(new MojangAuthenticator());

        httpClient = HttpClient.newHttpClient();
        random = new Random(System.currentTimeMillis());

        Encryption.init();

        eventBus = new EventBus();

        eventBus.register(ChatEvent.class, (c) -> {
            Logger.debug("{}: {}", c.getPlayer().getName(), c.getMessage());

            if (c.getMessage().startsWith("/"))
            {
                commandManager.execute(c.getMessage(), c.getPlayer());

                return;
            }

            Component component = Component.text(c.getPlayer().getName()).color(ChatColor.gray)
                            .withExtra(Component.text(": " + c.getMessage()).color(ChatColor.white));

            c.getPlayer().getInstance()
                    .broadcast(new ServerChatMessage(component, ChatPosition.Chat));
        });

        long lastTick = 0L;
        long interval = 50_000_000L; // 50 ms in nanoseconds

        MaterialImpl.getRegistry();
        Material material = Materials.STONE_SWORD;

        Logger.debug("Id: {}", material.getNamespace());

        ZCraftServer.getExecutor()
                .runVirtual(() -> {
                    listener.start();
                });

        Logger.info("Load took {}ms", timer.elapsedMs());

        while (true)
        {
            long nano = System.nanoTime();

            if ((nano - lastTick) >= interval)
            {
                lastTick = nano;

                tick();
            }

            loopManager.run();
        }
    }

    private static void tick()
    {
        eventBus.post(new StartTickEvent(System.currentTimeMillis()));

        // queue neccesary packets to be processed.
        packetManager.process();

        // keep alive
        if (ticks % settings.getKeepAliveDelay() == 0)
            connections.getConnections().forEach(c -> {
                if (c.getPlayer() == null || !c.getPlayer().isLoaded()) return;
                c.sendPacketVirtual(new ServerKeepAlive(random.nextInt(0, 127)));
            });

        eventBus.post(new EndTickEvent(System.currentTimeMillis()));

        ticks++;
    }

}
