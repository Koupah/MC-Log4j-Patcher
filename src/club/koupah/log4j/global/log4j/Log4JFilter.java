package club.koupah.log4j.global.log4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import club.koupah.log4j.global.configuration.Config;
import club.koupah.log4j.global.configuration.ConfigEntry.ActionType;
import club.koupah.log4j.global.utils.PUtil;

/**
 * @author Koupah
 * @createdAt 5:35:47 pm on 12 Dec 2021
 */

public class Log4JFilter extends AbstractFilter {

	private static final Log4JFilter INSTANCE = new Log4JFilter(Result.DENY, Result.NEUTRAL);

	public static boolean addFilter() {
		((Logger) LogManager.getRootLogger()).addFilter(INSTANCE);
		return true;
	}

	private Log4JFilter(final Result onMatch, final Result onMismatch) {
		super(Result.DENY, Result.ACCEPT);
	}

	@Override
	public Result filter(final LogEvent event) {
		if (!PUtil.hasLog4jFormat(event.getMessage().getFormat()))
			return this.onMismatch;

		boolean shouldReplace = Config.Option.LOGGER_ACTION.get().asAction() == ActionType.REPLACE;
		boolean fixed = shouldReplace && setMessage(event.getMessage(), PUtil
				.replaceFormats(event.getMessage().getFormat(), Config.Option.LOGGER_REPLACEMENT.get().asString()));

		if (fixed) {
			return this.onMismatch;
		} else { // Fall back to filtering out the message, especially if set to replace
			if (shouldReplace)
				System.out.println("Failed to fix a log message containing a Format. Cancelling it instead.");

			return this.onMatch;
		}
	}

	private boolean setMessage(Message message, String string) {
		try {
			Class<?> clazz = message.getClass();

			boolean set = false;

			set = PUtil.setField(clazz, "message", message, string);

			if (!set)
				set = PUtil.setField(clazz, "messagePattern", message, string);

			return set;
		} catch (Exception e) {
			return false;
		}
	}

}
