package net.zcraft.chat.components;

import com.google.gson.JsonObject;
import net.zcraft.chat.Component;

import java.util.Objects;

public final class TextComponent extends Component
{
    public String text;
    public TextComponent(String text) { this.text = Objects.requireNonNull(text); }
    @Override
    public JsonObject write() {
        JsonObject o = new JsonObject();
        o.addProperty("text", text);
        return o;
    }
    @Override public String kind() { return "text"; }
}