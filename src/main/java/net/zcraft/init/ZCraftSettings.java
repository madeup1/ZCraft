package net.zcraft.init;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.lang.reflect.Field;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ZCraftSettings
{
    public static ZCraftSettings DEFAULT = ZCraftSettings.builder()
            .threads(4)
            .port(25565)
            .packetThread(true)
            .compressionThreshold(256)
            .keepAliveDelay(40)
            .build();


    // vars
    private int threads;
    private int port;
    private int compressionThreshold;
    // in ticks (btw)
    private int keepAliveDelay;
    private boolean packetThread = true;
}
