package net.zcraft.entities;

import lombok.Getter;
import lombok.Setter;
import net.zcraft.ZCraftServer;
import net.zcraft.instance.Instance;

public abstract class Entity
{
    @Getter private final int entityId;
    @Getter @Setter private Instance instance;

    public Entity()
    {
        this.entityId = ZCraftServer.getInstanceManager().getEntityId();

        this.instance = ZCraftServer.getInstanceManager().getDefaultInstance();
    }
}
