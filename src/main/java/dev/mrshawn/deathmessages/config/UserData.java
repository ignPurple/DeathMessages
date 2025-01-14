package dev.mrshawn.deathmessages.config;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Stack;

public class UserData {

	public final String fileName = "UserData";

	FileConfiguration config;

	File file;

	public UserData() {
	}

	private static final UserData instance = new UserData();

	public static UserData getInstance() {
		return instance;
	}

	public FileConfiguration getConfig() {
		return config;
	}

	private IOOperation ioRunning = null;
	private final Stack<IOOperation> ioOperations = new Stack<>();

	public void save() {this.save(false);}
	public void save(boolean sync) {
		if (ioRunning != null) ioOperations.push(IOOperation.SAVE);
		// We should not halt the main server thread
		if (!sync) {
			Bukkit.getScheduler().runTaskAsynchronously(DeathMessages.getInstance(), this::saveFile);
		} else {
			saveFile();
		}
	}

	private void saveFile() {
		try {
			ioRunning = IOOperation.SAVE;
			ioOperations.removeIf(op -> op == IOOperation.SAVE);
			config.save(file);
			ioRunning = null;
		} catch (IOException e) {
			File f = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".broken." + new Date().getTime());
			DeathMessages.getInstance().getLogger().severe("Could not save: " + fileName + ".yml");
			DeathMessages.getInstance().getLogger().severe("Regenerating file and renaming the current file to: " + f.getName());
			DeathMessages.getInstance().getLogger().severe("You can try fixing the file with a yaml parser online!");
			file.renameTo(f);
			initialize();
		}
	}

	public void reload() {this.reload(false);}
	public void reload(boolean sync) {
		if (ioRunning != null) ioOperations.push(IOOperation.LOAD);
		// We should not halt the main server thread
		if (!sync) {
			Bukkit.getScheduler().runTaskAsynchronously(DeathMessages.getInstance(), this::reloadFile);
		} else {
			saveFile();
		}
	}

	private void reloadFile() {
		try {
			ioRunning = IOOperation.LOAD;
			ioOperations.removeIf(op -> op == IOOperation.LOAD);
			config.load(file);
			ioRunning = null;
		} catch (Exception e) {
			File f = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".broken." + new Date().getTime());
			DeathMessages.getInstance().getLogger().severe("Could not reload: " + fileName + ".yml");
			DeathMessages.getInstance().getLogger().severe("Regenerating file and renaming the current file to: " + f.getName());
			DeathMessages.getInstance().getLogger().severe("You can try fixing the file with a yaml parser online!");
			file.renameTo(f);
			initialize();
		}
	}

	public void initialize() {

		file = new File(DeathMessages.getInstance().getDataFolder(), fileName + ".yml");

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
		save(true);
		reload(true);
	}

	private enum IOOperation {
		SAVE, LOAD
	}
}
