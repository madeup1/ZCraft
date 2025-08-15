package net.zcraft.chat;

import com.google.gson.*;
import net.zcraft.chat.components.ScoreComponent;
import net.zcraft.chat.components.SelectorComponent;
import net.zcraft.chat.components.TextComponent;
import net.zcraft.chat.components.TranslationComponent;
import org.tinylog.Logger;

import java.lang.reflect.Type;

public final class ChatSerializer implements JsonSerializer<Component>, JsonDeserializer<Component>
{
    public static final Gson GSON = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeHierarchyAdapter(Component.class, new ChatSerializer())
            .create();

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) return JsonNull.INSTANCE;

        JsonObject root = src.write();
        // style
        if (src.getColor() != null) root.addProperty("color", src.getColor().name());
        if (src.isBold()) root.addProperty("bold", true);
        if (src.isItalic()) root.addProperty("italic", true);
        if (src.isUnderlined()) root.addProperty("underlined", true);
        if (src.isStrikethrough()) root.addProperty("strikethrough", true);
        if (src.isObfuscated()) root.addProperty("obfuscated", true);
        if (src.getInsertion() != null) root.addProperty("insertion", src.getInsertion());

        if (src.getClickEvent() != null) {
            JsonObject ce = new JsonObject();
            ce.addProperty("action", src.getClickEvent().action().name());
            ce.addProperty("value", src.getClickEvent().value());
            root.add("clickEvent", ce);
        }
        if (src.getHoverEvent() != null) {
            JsonObject he = new JsonObject();
            he.addProperty("action", src.getHoverEvent().action.name());
            he.add("value", src.getHoverEvent().value);
            root.add("hoverEvent", he);
        }

        if (!src.extra().isEmpty()) {
            JsonArray arr = new JsonArray();
            for (Component c : src.extra()) arr.add(serialize(c, Component.class, context));
            root.add("extra", arr);
        }
        return root;
    }

    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            // Primitive string -> text component (1.8 supports this)
            return new TextComponent(json.getAsString());
        }
        if (!json.isJsonObject()) throw new JsonParseException("Invalid chat component: " + json);

        JsonObject o = json.getAsJsonObject();
        Component base;
        if (o.has("text")) {
            base = new TextComponent(o.get("text").getAsString());
        } else if (o.has("translate")) {
            TranslationComponent tc = new TranslationComponent(o.get("translate").getAsString());
            if (o.has("with")) {
                JsonArray arr = o.getAsJsonArray("with");
                for (JsonElement e : arr) tc.with.add(deserialize(e, Component.class, context));
            }
            base = tc;
        } else if (o.has("score")) {
            JsonObject sc = o.getAsJsonObject("score");
            ScoreComponent s = new ScoreComponent(sc.get("name").getAsString(), sc.get("objective").getAsString());
            if (sc.has("value")) s.value = sc.get("value").getAsString();
            base = s;
        } else if (o.has("selector")) {
            base = new SelectorComponent(o.get("selector").getAsString());
        } else {
            throw new JsonParseException("Unknown component kind: " + o);
        }

        // style
        if (o.has("color")) base.color(ChatColor.valueOf(o.get("color").getAsString()));
        if (o.has("bold")) base.bold(o.get("bold").getAsBoolean());
        if (o.has("italic")) base.italic(o.get("italic").getAsBoolean());
        if (o.has("underlined")) base.underlined(o.get("underlined").getAsBoolean());
        if (o.has("strikethrough")) base.strikethrough(o.get("strikethrough").getAsBoolean());
        if (o.has("obfuscated")) base.obfuscated(o.get("obfuscated").getAsBoolean());
        if (o.has("insertion")) base.insertion(o.get("insertion").getAsString());

        if (o.has("clickEvent")) {
            JsonObject ce = o.getAsJsonObject("clickEvent");
            base.onClick(ClickAction.valueOf(ce.get("action").getAsString()), ce.get("value").getAsString());
        }
        if (o.has("hoverEvent")) {
            JsonObject he = o.getAsJsonObject("hoverEvent");
            HoverAction act = HoverAction.valueOf(he.get("action").getAsString());
            if (act == HoverAction.show_text) {
                base.setHoverEvent(HoverEvent.showText(deserialize(he.get("value"), Component.class, context)));
            } else {
                base.setHoverEvent(HoverEvent.showItem(he.get("value").getAsString()));
            }
        }

        if (o.has("extra")) {
            JsonArray arr = o.getAsJsonArray("extra");
            for (JsonElement e : arr) base.extra().add(deserialize(e, Component.class, context));
        }
        return base;
    }
}

