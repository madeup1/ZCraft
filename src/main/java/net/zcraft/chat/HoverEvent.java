package net.zcraft.chat;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

public final class HoverEvent {
    public final HoverAction action;
    // For show_text value is a Component; for show_item value is raw string (NBT-ish)
    public final JsonElement value;
    private HoverEvent(HoverAction action, JsonElement value) {
        this.action = Objects.requireNonNull(action);
        this.value = Objects.requireNonNull(value);
    }
    public static HoverEvent showText(Component c) {
        return new HoverEvent(HoverAction.show_text, ChatSerializer.GSON.toJsonTree(c, Component.class));
    }
    public static HoverEvent showItem(String itemNbtLike) {
        return new HoverEvent(HoverAction.show_item, new JsonPrimitive(itemNbtLike));
    }
}
