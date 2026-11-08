package Unbound.unboundAPI.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;

public final class ConfigFile {

    private final JavaPlugin owner;
    private final String fileName;
    private final File file;
    private FileConfiguration configuration;

    public ConfigFile(JavaPlugin owner, String fileName) {
        this.owner = Objects.requireNonNull(owner, "owner");
        this.fileName = Objects.requireNonNull(fileName, "fileName");
        this.file = new File(owner.getDataFolder(), fileName);
        reload();
    }

    public FileConfiguration get() {
        return configuration;
    }

    public File file() {
        return file;
    }

    public void reload() {
        if (!owner.getDataFolder().exists() && !owner.getDataFolder().mkdirs()) {
            owner.getLogger().warning("Could not create plugin data folder.");
        }
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists() && !parent.mkdirs()) {
                    owner.getLogger().warning("Could not create directory for " + fileName + ".");
                }
                try (InputStream resource = owner.getResource(fileName)) {
                    if (resource != null) {
                        owner.saveResource(fileName, false);
                    } else if (!file.createNewFile()) {
                        owner.getLogger().warning("Could not create " + fileName + ".");
                    }
                }
            } catch (IOException | IllegalArgumentException exception) {
                owner.getLogger().log(Level.WARNING, "Could not create " + fileName + ".", exception);
            }
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException exception) {
            owner.getLogger().log(Level.SEVERE, "Could not save " + fileName + ".", exception);
        }
    }
}
