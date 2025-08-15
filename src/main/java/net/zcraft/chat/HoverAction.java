package net.zcraft.chat;

import com.google.gson.annotations.SerializedName;

public enum HoverAction {
    // In 1.8: show_text, show_item (show_entity appears later)
    @SerializedName("show_text") show_text,
    @SerializedName("show_item") show_item
}
