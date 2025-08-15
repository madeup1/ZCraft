package net.zcraft.events.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.zcraft.events.Event;

@AllArgsConstructor
public class EndTickEvent extends Event
{
    @Getter
    private long time;
}
