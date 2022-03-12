package com.github.jenya705.repsystem.storage;

import com.github.jenya705.repsystem.RepSystem;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Jenya705
 */
public class RepStorage implements Listener {

    private static final Gson gson = new Gson();

    private final RepSystem plugin;
    private final Map<UUID, Integer> reputations = new ConcurrentHashMap<>();
    private final Map<UUID, Map<UUID, Boolean>> gives = new HashMap<>();
    private final Map<String, UUID> nicknames = new HashMap<>();

    public RepStorage(RepSystem plugin) throws IOException {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        try (Reader reader = new FileReader(saveFile())) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            if (json == null) return;
            JsonObject jsonReputations = json.getAsJsonObject("reputations");
            if (jsonReputations != null) {
                jsonReputations.entrySet().forEach(stringJsonElementEntry -> reputations.put(
                        UUID.fromString(stringJsonElementEntry.getKey()),
                        stringJsonElementEntry.getValue().getAsInt()
                ));
            }
            JsonObject givesJson = json.getAsJsonObject("gives");
            if (givesJson != null) {
                givesJson.entrySet().forEach(stringJsonElementEntry -> {
                    Map<UUID, Boolean> playerReputationGave = new HashMap<>();
                    stringJsonElementEntry.getValue().getAsJsonObject().entrySet().forEach(it ->
                            playerReputationGave.put(
                                    UUID.fromString(it.getKey()), it.getValue().getAsBoolean()
                            )
                    );
                    gives.put(
                            UUID.fromString(stringJsonElementEntry.getKey()),
                            playerReputationGave
                    );
                });
            }
            JsonObject nicknamesJson = json.getAsJsonObject("nicknames");
            if (nicknamesJson != null) {
                nicknamesJson.entrySet().forEach(stringJsonElementEntry -> nicknames.put(
                        stringJsonElementEntry.getKey(),
                        UUID.fromString(stringJsonElementEntry.getValue().getAsString())
                ));
            }
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

    public void giveReputation(UUID to, UUID who, boolean plus) {
        if (gives.containsKey(to) && gives.get(to).containsKey(who)) {
            boolean wasPlus = gives.get(to).get(who);
            if (wasPlus == plus) return;
            int change = wasPlus ? -2 : 2;
            setReputation(to, reputation(to) + change);
        }
        else {
            gives.put(to, new HashMap<>());
            setReputation(to, reputation(to) + (plus ? 1 : -1));
        }
        gives.get(to).put(who, plus);
    }

    public UUID getUUID(String nickname) {
        return nicknames.get(nickname.toLowerCase(Locale.ROOT));
    }

    public void save() throws IOException {
        try (Writer writer = new FileWriter(saveFile())) {
            JsonObject json = new JsonObject();
            JsonObject reputationsJson = new JsonObject();
            reputations.forEach((uuid, integer) -> reputationsJson.addProperty(uuid.toString(), integer));
            json.add("reputations", reputationsJson);
            JsonObject givesJson = new JsonObject();
            gives.forEach((uuid, uuidLongMap) -> {
                JsonObject playerTimes = new JsonObject();
                uuidLongMap.forEach((uuid1, aLong) -> playerTimes.addProperty(uuid1.toString(), aLong));
                givesJson.add(uuid.toString(), playerTimes);
            });
            json.add("gives", givesJson);
            JsonObject nicknamesJson = new JsonObject();
            nicknames.forEach((s, uuid) -> nicknamesJson.addProperty(s, uuid.toString()));
            json.add("nicknames", nicknamesJson);
            gson.toJson(json, writer);
        }
    }

    private File saveFile() throws IOException {
        File file = new File(plugin.getDataFolder(), "save.json");
        if (!file.exists()) file.createNewFile();
        return file;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        nicknames.put(
                event.getPlayer().getName().toLowerCase(Locale.ROOT),
                event.getPlayer().getUniqueId()
        );
    }


}
