package net.zcraft.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.zcraft.entities.EntityPlayer;
import net.zcraft.events.CancellableEvent;

@AllArgsConstructor
@Getter
@Setter
public class ChatEvent extends CancellableEvent
{
    private String message;
    private EntityPlayer player;
}
