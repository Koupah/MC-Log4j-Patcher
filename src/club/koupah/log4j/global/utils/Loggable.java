package club.koupah.log4j.global.utils;

/**
 * Loggable interface
 * 
 * @author Koupah
 * @createdAt 2:28:27 am on 14 Dec 2021
 */
public interface Loggable {

	/*
	 * I've never made anything like this before, and in honesty I never even use
	 * interfaces. I somewhat understand their purpose, but I've never used them
	 * before. No idea if I'm even doing this correctly. I normally just use
	 * abstract classes! Is there benefits of using one over the other? Other than
	 * the fact I can't extend 50 classes lol.
	 * 
	 * TODO: Research the benefits of interfaces over abstract classes
	 * 
	 * Being a developer is mentally straining when you know others will see your
	 * code. God bless anyone who isn't impacted by the judgment of others.
	 */

	public static enum LogType {
		INFO, SUCCESS, ERROR,
	}

	public default void log(Object log) {
		System.out.println(processLog(getLoggerPrefix() + log, LogType.INFO));
	}
	
	public default void error(Object log) {
		System.out.println(processLog(getLoggerPrefix() + log, LogType.ERROR));
	}

	/*
	 * This will probably be used in the future for allowing some patchers to apply
	 * colors
	 */
	public default String processLog(String log, LogType type) {
		return log;
	}

	public static final String prefixFormat = "[%s] ";

	public default String getLoggerPrefix() {
		return String.format(prefixFormat, getClass().getSimpleName());
	}

}
