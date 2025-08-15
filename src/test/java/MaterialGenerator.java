import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class MaterialGenerator {
    private static final String BLOCKS_URL =
            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pc/1.8/blocks.json";
    private static final String ITEMS_URL =
            "https://raw.githubusercontent.com/PrismarineJS/minecraft-data/master/data/pc/1.8/items.json";

    @Test
    public void generate() throws IOException, InterruptedException
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        HttpClient client = HttpClient.newHttpClient();

        // Download blocks
        String blocksJson = download(client, BLOCKS_URL);
        String itemsJson = download(client, ITEMS_URL);

        // Parse JSON
        Type listType = new TypeToken<List<Map<String, Object>>>() {}.getType();
        List<Map<String, Object>> blocks = gson.fromJson(blocksJson, listType);
        List<Map<String, Object>> items = gson.fromJson(itemsJson, listType);

        LinkedList<String> outJava = new LinkedList<>();

        Set<Double> seenIds = new HashSet<>();
        List<Map<String, Object>> merged = new ArrayList<>();

        // Normalize blocks
        for (Map<String, Object> b : blocks) {
            Double id = ((Number) b.get("id")).doubleValue();
            if (seenIds.add(id)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", id.intValue());
                entry.put("name", b.get("name"));
                entry.put("displayName", b.getOrDefault("displayName", b.get("name")));
                entry.put("type", "block");
                entry.put("stackSize", 64);
                entry.put("hardness", b.get("hardness"));
                entry.put("diggable", b.getOrDefault("diggable", true));
                Map<String, Object> harvestTools = (Map<String, Object>) b.get("harvestTools");
                if (harvestTools != null && !harvestTools.isEmpty()) {
                    String toolId = harvestTools.keySet().iterator().next(); // pick first tool ID
                    Optional<Map<String, Object>> toolItem = items.stream()
                            .filter(i -> String.valueOf(i.get("id")).equals(toolId))
                            .findFirst();

                    toolItem.ifPresent(tool -> {
                        String toolName = (String) tool.get("name");
                        String baseTool = toolName.contains("_") ? toolName.split("_")[1] : toolName;
                        entry.put("tool", baseTool); // e.g., "pickaxe", "shovel"
                    });
                } else {
                    entry.put("tool", null);
                }

                List<Map<String, Object>> variations = (List<Map<String, Object>>) b.get("variations");

                String material = (String) b.getOrDefault("material", "");
                // entry.put("flammable", List.of("wood", "plant", "leaves").contains(material));
                entry.put("transparent", b.getOrDefault("transparent", false));
                merged.add(entry);

                String javaLine = "   @NonNull Material " + ((String) (entry.get("name"))).toUpperCase() + " = Objects.requireNonNull(REGISTRY.get(\"" + ((String) (entry.get("name"))) + "\"));";

                if (!outJava.contains(javaLine))
                    outJava.add(javaLine);

                if (variations != null)
                {
                    for (Map<String, Object> variation : variations)
                    {
                        double meta = (double) variation.get("metadata");

                        if (meta == 0)
                            continue;

                        Map<String, Object> nntry = new LinkedHashMap<>();

                        nntry.put("id", id.intValue());

                        String disp = (String) variation.get("displayName");
                        String locid = disp.toUpperCase().replace(' ', '_');
                        if (disp.contains("(") || disp.contains(")") || disp.contains(",") || disp.contains("/") || (disp.length() >= 20 && disp.chars().filter(c -> c == ' ').count() > 3))
                            locid = ((String) entry.get("name")) + ":" + (int) meta;
                        nntry.put("name", locid);
                        nntry.put("metadata", variation.get("metadata"));
                        nntry.put("displayName", variation.get("displayName"));
                        nntry.put("type", "block");
                        nntry.put("stackSize", 64);
                        nntry.put("hardness", b.get("hardness"));
                        nntry.put("diggable", b.getOrDefault("diggable", true));
                        nntry.put("transparent", b.getOrDefault("transparent", false));

                        javaLine = "   @NonNull Material " + ((String) (nntry.get("name"))).toUpperCase().replace(':', '_') + " = Objects.requireNonNull(REGISTRY.get(\"" + ((String) (nntry.get("name"))) + "\"));";

                        if (!outJava.contains(javaLine))
                            outJava.add(javaLine);

                        merged.add(nntry);
                    }
                }
            }
        }

        // Normalize items
        for (Map<String, Object> i : items) {
            Double id = ((Number) i.get("id")).doubleValue();
            if (seenIds.add(id)) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("id", id.intValue());
                entry.put("name", i.get("name"));
                entry.put("displayName", i.getOrDefault("displayName", i.get("name")));
                entry.put("type", "item");
                entry.put("stackSize", ((Number) i.getOrDefault("stackSize", 64)).intValue());
                merged.add(entry);

                String javaLine = "   @NonNull Material " + ((String) (entry.get("name"))).toUpperCase() + " = Objects.requireNonNull(REGISTRY.get(\"" + ((String) (entry.get("name"))) + "\"));";

                if (!outJava.contains(javaLine))
                    outJava.add(javaLine);
            }
        }

        // Save to file
        Path outputPath = Path.of("materials.json");
        Path outJavaCode = Path.of("jc.txt");
        Files.writeString(outputPath, gson.toJson(merged));
        Files.writeString(outJavaCode, String.join("\n", outJava));
        System.out.println("Saved to: " + outputPath.toAbsolutePath());
    }

    private String download(HttpClient client, String url) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(url)).build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        return resp.body();
    }
}
