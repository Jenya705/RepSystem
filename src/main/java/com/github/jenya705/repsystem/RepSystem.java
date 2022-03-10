package com.github.jenya705.repsystem;

import com.github.jenya705.repsystem.command.RootRepCommand;
import com.github.jenya705.repsystem.storage.RepStorage;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;

@Getter
public final class RepSystem extends JavaPlugin {

    private final RepMessage message = new RepMessage(this);

    private RepStorage storage;
    private Duration delay;

    @Override
    @SneakyThrows
    public void onEnable() {
        saveDefaultConfig();
        delay = getDuration("delay");
        storage = new RepStorage(this);
        getCommand("rep").setExecutor(new RootRepCommand(this));
    }

    @Override
    @SneakyThrows
    public void onDisable() {
        if (storage != null) {
            storage.save();
        }
    }

    public Duration getDuration(String key) {
        String duration = getConfig().getString(key);
        if (duration == null) return null;
        int number = Integer.parseInt(duration.substring(0, duration.length() - 1));
        if (duration.endsWith("d")) {
            return Duration.ofDays(number);
        }
        else if (duration.endsWith("h")) {
            return Duration.ofHours(number);
        }
        else if (duration.endsWith("m")) {
            return Duration.ofMinutes(number);
        }
        else if (duration.endsWith("s")) {
            return Duration.ofSeconds(number);
        }
        return null;
    }
}
