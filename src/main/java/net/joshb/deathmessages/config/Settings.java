package net.joshb.deathmessages.config;

import net.joshb.deathmessages.assets.ConfigUpdater;
import net.joshb.deathmessages.DeathMessages;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

public class Settings {

    private String fileName = "Settings";

    FileConfiguration config;

    File file;

    public Settings(){ }
    private static Settings instance = new Settings();
    public static Settings getInstance(){
        return instance;
    }

    public FileConfiguration getConfig(){
        return config;
    }

    public void save(){
        try {
            ConfigUpdater.update(DeathMessages.plugin, fileName + ".yml", file, Arrays.asList());
        } catch (Exception e){
            File f = new File(DeathMessages.plugin.getDataFolder(), fileName + ".broken." + new Date().getTime());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not save: " + fileName + ".yml");
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Regenerating file and renaming the current file to: " + f.getName());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(f);
            initialize();
        }
    }

    public void reload(){
        try {
            config.load(file);
        } catch (Exception e){
            File f = new File(DeathMessages.plugin.getDataFolder(), fileName + ".broken." + new Date().getTime());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Could not reload: " + fileName + ".yml");
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "Regenerating file and renaming the current file to: " + f.getName());
            DeathMessages.plugin.getLogger().log(Level.SEVERE, "You can try fixing the file with a yaml parser online!");
            file.renameTo(f);
            initialize();
        }
    }

    public void initialize(){
        if (!DeathMessages.plugin.getDataFolder().exists()) {
            DeathMessages.plugin.getDataFolder().mkdir();
        }

        file = new File(DeathMessages.plugin.getDataFolder(), fileName + ".yml");

        if(!file.exists()){
            file.getParentFile().mkdirs();
            copy(DeathMessages.plugin.getResource(fileName + ".yml"), file);
        }
        config = YamlConfiguration.loadConfiguration(file);
        save();
        reload();
    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}