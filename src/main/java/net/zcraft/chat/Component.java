package net.zcraft.chat;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.zcraft.chat.components.TextComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// ---------- Component Base ----------
public abstract class Component {
    // style     // null = not set
    @Getter
    private ChatColor color;
    // accessors (if needed)
    @Getter
    private boolean bold;
    @Getter
    private boolean italic;
    @Getter
    private boolean underlined;
    @Getter
    private boolean strikethrough;
    @Getter
    private boolean obfuscated;
    @Getter
    private String insertion;           // optional
    @Getter
    @Setter
    private ClickEvent clickEvent;      // optional
    @Getter
    @Setter
    private HoverEvent hoverEvent;      // optional
    private final List<Component> extra = new ArrayList<>();

    // fluent style setters
    public Component color(ChatColor color) {this.color = color; return this;}
    public Component bold(Boolean v) { this.bold = v; return this; }
    public Component italic(Boolean v) { this.italic = v; return this; }
    public Component underlined(Boolean v) { this.underlined = v; return this; }
    public Component strikethrough(Boolean v) { this.strikethrough = v; return this; }
    public Component obfuscated(Boolean v) { this.obfuscated = v; return this; }
    public Component insertion(String s) { this.insertion = s; return this; }
    public Component onClick(ClickAction a, String value) { this.clickEvent = new ClickEvent(a, value); return this; }
    public Component onHoverShowText(Component text) { this.hoverEvent = HoverEvent.showText(text); return this; }
    public Component onHoverShowItem(String nbtLike) { this.hoverEvent = HoverEvent.showItem(nbtLike); return this; }
    public Component withExtra(Component... children) { this.extra.addAll(Arrays.asList(children)); return this; }
    public List<Component> extra() { return extra; }

    // subtype tag writing
    public abstract JsonObject write();
    public abstract String kind();

    // helpers
    public static Component fromJson(String json)
    {
        return ChatSerializer.GSON.fromJson(json, Component.class);
    }

    public static String toJson(Component c)
    {
        return ChatSerializer.GSON.toJson(c);
    }

    public static Component text(String text)
    {
        return new TextComponent(text);
    }
}
