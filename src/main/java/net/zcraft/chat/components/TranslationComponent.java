package net.zcraft.chat.components;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.zcraft.chat.ChatSerializer;
import net.zcraft.chat.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class TranslationComponent extends Component
{
    public String translate;
    public final List<Component> with = new ArrayList<>();
    public TranslationComponent(String translate, Component... with) {
        this.translate = Objects.requireNonNull(translate);
        if (with != null) this.with.addAll(Arrays.asList(with));
    }
    public TranslationComponent withArgs(Component... args) { this.with.addAll(Arrays.asList(args)); return this; }
    @Override
    public JsonObject write() {
        JsonObject o = new JsonObject();
        o.addProperty("translate", translate);
        if (!with.isEmpty()) {
            JsonArray arr = new JsonArray();
            for (Component c : with) arr.add(ChatSerializer.GSON.toJsonTree(c, Component.class));
            o.add("with", arr);
        }
        return o;
    }
    @Override public String kind() { return "translate"; }
}
