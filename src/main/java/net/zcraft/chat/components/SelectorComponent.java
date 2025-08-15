package net.zcraft.chat.components;

import com.google.gson.JsonObject;
import net.zcraft.chat.Component;

import java.util.Objects;

public final class SelectorComponent extends Component
{
    public String selector;
    public SelectorComponent(String selector) { this.selector = Objects.requireNonNull(selector); }
    @Override
    public JsonObject write() {
        JsonObject o = new JsonObject();
        o.addProperty("selector", selector);
        return o;
    }
    @Override
    public String kind() { return "selector"; }
}
