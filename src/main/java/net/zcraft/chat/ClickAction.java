package net.zcraft.chat;

import com.google.gson.annotations.SerializedName;

public enum ClickAction {
    @SerializedName("open_url") open_url,
    @SerializedName("open_file") open_file,
    @SerializedName("run_command") run_command,
    @SerializedName("suggest_command") suggest_command
}
