package net.zcraft;

import lombok.Getter;
import lombok.Setter;
import net.zcraft.events.EventBus;
import net.zcraft.events.impl.EndTickEvent;
import net.zcraft.events.impl.StartTickEvent;
import net.zcraft.init.ZCraftSettings;
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
import net.zcraft.util.NanoTimer;
import net.zcraft.util.status.ServerStatus;
import net.zcraft.util.threading.MultiExecutor;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;

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
    private static PacketManager packetManager;
    @Getter
    private static NetworkListener listener;
    @Getter
    private static LoopManager loopManager;
    @Getter
    private static MultiExecutor executor;
    @Getter
    private static EventBus eventBus;

    // variables

    @Getter @Setter
    private static Function<ZCraftConnection, ServerStatus> motdHandler = (conn) -> {
        ServerStatus status = new ServerStatus();

        status.setDescription("ZCraft (0.1)")
                .setVersion("ZCraft Beta", 47)
                .setPlayers(941, 20_000);

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

        eventBus = new EventBus();

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

        eventBus.post(new EndTickEvent(System.currentTimeMillis()));
    }

}
