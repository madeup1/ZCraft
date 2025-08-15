package net.zcraft.chat.components;

import com.google.gson.JsonObject;
import net.zcraft.chat.Component;

import java.util.Objects;

public final class ScoreComponent extends Component
{
    public String name;       // entity name or selector result
    public String objective;
    public String value;      // optional cached value
    public ScoreComponent(String name, String objective) {
        this.name = Objects.requireNonNull(name);
        this.objective = Objects.requireNonNull(objective);
    }
    public ScoreComponent cachedValue(String v) { this.value = v; return this; }
    @Override
    public JsonObject write() {
        JsonObject o = new JsonObject();
        JsonObject score = new JsonObject();
        score.addProperty("name", name);
        score.addProperty("objective", objective);
        if (value != null) score.addProperty("value", value);
        o.add("score", score);
        return o;
    }
    @Override public String kind() { return "score"; }
}
