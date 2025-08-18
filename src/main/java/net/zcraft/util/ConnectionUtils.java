package net.zcraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.EOFException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;

public class ConnectionUtils
{
    public static int getVarIntLength(int value)
    {
        int len = 1;
        while ((value & -128) != 0)
        {
            value >>= 7;
            len++;
        }

        return len;
    }

    public static int readVarInt(InputStream stream) throws Exception
    {
        int value = 0;
        int count = 0;

        while (true) {
            int b = stream.read();
            if (b == -1) return -1;

            value |= (b & 0x7F) << (7 * count);
            if ((b & 0x80) == 0) break;

            count++;
            if (count >= 5) throw new RuntimeException("Varint too long");
        }

        return value;

    }

    public static boolean isPlayerAuthenticated(String username, byte[] sharedKey, byte[] serverKeyAsn1) throws Exception {
        // Compute the serverId hash (JavaHexDigest equivalent)
        byte[] combined = concat(new byte[0], sharedKey, serverKeyAsn1);
        String serverId = javaHexDigest(combined);

        // Build the URL
        String urlStr = String.format(
                "http://session.minecraft.net/game/checkserver.jsp?user=%s&serverId=%s",
                username, serverId
        );

        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (InputStream in = conn.getInputStream()) {
            byte[] responseBytes = in.readAllBytes();
            String response = new String(responseBytes, StandardCharsets.UTF_8);

            System.out.println("Response: " + response);

            return !response.contains("NO");
        }
    }

    public static UUID getUuid(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() != 200) {
                return UUID.randomUUID();
            }

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            String rawUuid = json.get("id").getAsString(); // Mojang returns UUID without dashes
            // Insert dashes to match UUID format
            String formattedUuid = rawUuid.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                    "$1-$2-$3-$4-$5"
            );
            return UUID.fromString(formattedUuid);

        } catch (Exception e) {
            return UUID.randomUUID();
        }
    }

    // Helper: concatenate multiple byte arrays
    private static byte[] concat(byte[]... arrays) {
        int length = Arrays.stream(arrays).mapToInt(a -> a.length).sum();
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] a : arrays) {
            System.arraycopy(a, 0, result, pos, a.length);
            pos += a.length;
        }
        return result;
    }

    // Helper: convert byte array to hex string (JavaHexDigest)
    private static String javaHexDigest(byte[] data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(data);
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) {
            hex.append(String.format("%02x", b & 0xFF));
        }
        return hex.toString();
    }
}
