package club.koupah.log4j;

/**
 * @author Koupah
 * @createdAt 7:19:57 pm on 12 Dec 2021
 */

public enum PatcherPlatform {
	BUKKIT, BUNGEE, SPONGE, VELOCITY, FORGE, JAVAAGENT, UNKNOWN;

	final String name = name().substring(0, 1) + name().toLowerCase().substring(1);

	public String getName() {
		return name;
	}
}
