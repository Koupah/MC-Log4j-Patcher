package club.koupah.log4j.global.configuration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.koupah.log4j.Patcher;
import club.koupah.log4j.global.configuration.ConfigEntry.ActionType;
import club.koupah.log4j.global.utils.Loggable;

/**
 * Configuration Manager I quickly whipped up! <br>
 * Note: I've never made one before, first ever attempt!
 * 
 * @author Koupah
 * @createdAt 8:18:28 pm on 12 Dec 2021
 */

public class Config implements Loggable {

	/*
	 * I've never put final on a class declaration before, I should research what it
	 * does.
	 * 
	 * TODO: Research what final does on class declaration
	 */
	public static class Option {

		public static final List<Option> defaultOptions = new ArrayList<Option>();

		public static final Option CHECK_FOR_UPDATES = new Option(new ConfigEntry<Boolean>("check_for_updates", true,
				"Whether " + Patcher.name + " should check if there are any updates available. [true/false]")
						.setCategory("Updates"));

		public static final Option ASYNC_UPDATE_CHECK = new Option(new ConfigEntry<Boolean>("check_update_async", true,
				"Should " + Patcher.name + " run the update check in the background if enabled? [true/false]")
						.setCategory("Updates"));

		public static final Option LOGGER_CHECK = new Option(new ConfigEntry<Boolean>("logger_check", true,
				"This prevents Log4j formats (jndi and more) from being logged, preventing the exploit from affecting the current platform. Highly recommended to leave this enabled! [true/false]"));

		public static final Option LOGGER_ACTION = new Option(new ConfigEntry<ActionType>("logger_action",
				ActionType.REPLACE,
				"The action that should be taken on any Log4j formats before they're logged. [REPLACE (replaces formats with value of logger_format_replacement), STOP (stops the log complete)]"));

		public static final Option LOGGER_REPLACEMENT = new Option(new ConfigEntry<String>("logger_format_replacement",
				"L4J Format",
				"This replaces the contents of any formats when logger_action is REPLACE. Examples: ${jndi:ldap://abc.xyz/a} -> ${L4J Format}, ${date:yyyy} -> ${L4J Format}. [Any String]"));

		public static final Option ADDITIONAL_FIXES = new Option(new ConfigEntry<Boolean>("additional_fixes", true,
				"Should we run additional fixes to try and completely break the jndi exploit? Highly recommended to leave this enabled! [true/false]"));

		ConfigEntry<?> entry;

		public Option(ConfigEntry<?> configEntry) {
			this.entry = configEntry;
			defaultOptions.add(this);
		}

		public ConfigEntry<?> get() {
			return entry;
		}
	}

	private static String defaultFolderName = "L4JPatcher";
	private static String defaultFileName = "config.txt";

	private static Map<String, ConfigEntry<?>> settings = new HashMap<String, ConfigEntry<?>>();

	File folder;
	File file;

	public Config(String folderName, String fileName) {
		this.folder = new File(folderName);
		this.folder.mkdirs();
		this.folder.mkdir();

		for (Option option : Option.defaultOptions) {
			settings.put(option.entry.getKey(), option.entry);
		}

		this.file = new File(folder, fileName);
		if (!this.file.exists()) {
			try {
				this.file.createNewFile();
				saveFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			loadFromFile();
		}
	}

	public Config() {
		this(defaultFolderName, defaultFileName);
	}

	public void saveFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			List<ConfigEntry<?>> sorted = new ArrayList<ConfigEntry<?>>(settings.values());
			Collections.sort(sorted, new Comparator<ConfigEntry<?>>() {
				public int compare(ConfigEntry<?> o1, ConfigEntry<?> o2) {
					return o2.category.compareTo(o1.category);
				}
			});

			String lastCategory = null;

			for (ConfigEntry<?> setting : sorted) {

				if (lastCategory == null || !lastCategory.equals(setting.category)) {
					bw.write("#");
					bw.newLine();
					bw.write("# " + setting.category);
					bw.newLine();
					bw.write("#");
					bw.newLine();
					bw.newLine();
					lastCategory = setting.category;
				}

				if (setting.hasComment()) {
					bw.write("# " + setting.getComment());
					bw.newLine();
				}

				bw.write(setting.getKey() + "=" + setting.getValue());
				bw.newLine();
				bw.newLine();

			}
			bw.close();
			log("Successfully saved Log4JPatcher Configuration file.");
		} catch (IOException e) {
			error("Exception whilst saving configuration to file!");
			e.printStackTrace();
		}

	}

	public void loadFromFile() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

			List<ConfigEntry<?>> loaded = new ArrayList<ConfigEntry<?>>();

			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#") || !line.contains("="))
					continue;

				final String[] parts = line.trim().split("=");

				ConfigEntry<?> setting = settings.get(parts[0]);
				if (setting != null) {
					setting.loadValue(parts[1]);
					loaded.add(setting);
				}
			}

			br.close();

			ArrayList<ConfigEntry<?>> missing = new ArrayList<ConfigEntry<?>>(settings.values());
			missing.removeAll(loaded);

			if (!missing.isEmpty()) {
				log(missing.size() + " Config settings were missing from the file for this platform!");
				log("Saving all Config settings so that the missing ones show.");
				saveFile();
			}

			log("Successfully loaded Log4JPatcher Configuration file.");
		} catch (IOException e) {
			error("Exception whilst loading configuration from file!");
			e.printStackTrace();
		}
	}

	public static Config create() {
		return new Config();
	}

	public static void addConfigSettings(String category, List<ConfigEntry<?>> configEntries) {
		if (configEntries == null)
			return;

		for (ConfigEntry<?> entry : configEntries) {
			entry.setCategory(category);
			settings.put(entry.key, entry);
		}
	}

}
