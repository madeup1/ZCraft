package net.zcraft.chat;

import java.util.Objects;

public record ClickEvent(ClickAction action, String value)
{
    public ClickEvent(ClickAction action, String value)
    {
        this.action = Objects.requireNonNull(action);
        this.value = Objects.requireNonNull(value);
    }
}