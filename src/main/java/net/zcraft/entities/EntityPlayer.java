package net.zcraft.entities;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.zcraft.ZCraftServer;
import net.zcraft.instance.Instance;
import net.zcraft.network.ZCraftConnection;
import net.zcraft.position.Vec3;
import net.zcraft.protocol.IServerPacket;
import net.zcraft.protocol.server.play.ServerPlayerPosLook;
import net.zcraft.util.Gamemode;

import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class EntityPlayer extends Entity
{
    @Setter private String name;
    @Setter private ZCraftConnection connection;
    @Setter private UUID uuid;
    @Setter private Gamemode gamemode;
    @Setter private int viewDistance;


    @Setter private Vec3 position;
    @Setter private float yaw;
    @Setter private float pitch;
    private boolean loaded;


    public EntityPlayer(String name, ZCraftConnection connection)
    {
        super();
        this.name = name;
        this.connection = connection;

        this.loaded = false;

        this.gamemode = this.getInstance().defaultGamemode();
        this.position = this.getInstance().spawnPosition().asVec();
        this.yaw = 0;
        this.pitch = 0;
    }

    /*
        FIX THIS
     */
    public boolean isAuthenticated()
    {
        if (connection.has("authed"))
            return connection.get("authed");

        return ZCraftServer.getAuthManager().isAuthenticated(connection);
    }

    public void sendPacket(IServerPacket packet)
    {
        this.connection.sendPacket(packet);
    }

    public <T> T get(@NonNull String key)
    {
        return this.connection.get(key);
    }

    public <T> void set(@NonNull String key, @NonNull T value)
    {
        this.connection.set(key, value);
    }

    public void remove(@NonNull String key)
    {
        this.connection.remove(key);
    }

    public boolean has(String key)
    {
        return this.connection.has(key);
    }

    public <T> void ifPresent(@NonNull String key, @NonNull Consumer<T> consumer)
    {
        if (this.has(key))
            consumer.accept(this.get(key));
    }

    @Override
    public void init()
    {
        if (!loaded)
        {
            // reload reusable data, else be sigma

            loaded = true;

            this.getInstance().addPlayer(this);
        }

        this.sendPacket(new ServerPlayerPosLook(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), (byte) 0));
    }

    @Override
    public void tick()
    {

    }

    public double getX()
    {
        return this.position.x();
    }

    public double getY()
    {
        return this.position.y();
    }

    public double getZ()
    {
        return this.position.z();
    }

    public void setPosition(double x, double y, double z)
    {
        this.position = new Vec3(x, y, z);
    }
}
