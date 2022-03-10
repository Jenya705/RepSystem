package com.github.jenya705.repsystem.storage;

import com.github.jenya705.repsystem.RepSystem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Jenya705
 */
public class RepStorage {

    private static final Gson gson = new Gson();

    private final RepSystem plugin;
    private final Map<UUID, Integer> reputations;
    private final Map<UUID, Map<UUID, Long>> reputationTimes;

    public RepStorage(RepSystem plugin) throws IOException {
        this.plugin = plugin;
        try (Reader reader = new FileReader(saveFile())) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            reputations = new HashMap<>();
            reputationTimes = new HashMap<>();
            if (json == null) return;
            json.getAsJsonObject("reputations").entrySet().forEach(stringJsonElementEntry -> reputations.put(
                    UUID.fromString(stringJsonElementEntry.getKey()),
                    stringJsonElementEntry.getValue().getAsInt()
            ));
            json.getAsJsonObject("times").entrySet().forEach(stringJsonElementEntry -> {
                Map<UUID, Long> playerTimings = new HashMap<>();
                stringJsonElementEntry.getValue().getAsJsonObject().entrySet().forEach(it -> playerTimings.put(
                        UUID.fromString(it.getKey()), it.getValue().getAsLong()
                ));
                reputationTimes.put(
                        UUID.fromString(stringJsonElementEntry.getKey()),
                        playerTimings
                );
            });
        }
    }

    public int reputation(UUID uuid) {
        if (!reputations.containsKey(uuid)) {
            setReputation(uuid, plugin.getConfig().getInt("default-reputation"));
        }
        return reputations.get(uuid);
    }

    public void setReputation(UUID uuid, int reputation) {
        reputations.put(uuid, reputation);
    }

    public void setDelay(UUID reputationHolder, UUID whoSet) {
        reputationTimes.putIfAbsent(reputationHolder, new HashMap<>());
        reputationTimes.get(reputationHolder).put(whoSet, System.currentTimeMillis());
    }

    public boolean checkDelay(UUID reputationHolder, UUID whoSet) {
        return !(reputationTimes.containsKey(reputationHolder) &&
                reputationTimes.get(reputationHolder).containsKey(whoSet) &&
                reputationTimes.get(reputationHolder).get(whoSet) + plugin.getDelay().toMillis() > System.currentTimeMillis()
        );
    }

    public void save() throws IOException {
        try (Writer writer = new FileWriter(saveFile())) {
            JsonObject json = new JsonObject();
            JsonObject reps = new JsonObject();
            reputations.forEach((uuid, integer) -> reps.addProperty(uuid.toString(), integer));
            json.add("reputations", reps);
            JsonObject times = new JsonObject();
            reputationTimes.forEach((uuid, uuidLongMap) -> {
                JsonObject playerTimes = new JsonObject();
                uuidLongMap.forEach((uuid1, aLong) -> playerTimes.addProperty(uuid1.toString(), aLong));
                times.add(uuid.toString(), playerTimes);
            });
            json.add("times", times);
            gson.toJson(json, writer);
        }
    }

    private File saveFile() throws IOException {
        File file = new File(plugin.getDataFolder(), "save.json");
        if (!file.exists()) file.createNewFile();
        return file;
    }


}
