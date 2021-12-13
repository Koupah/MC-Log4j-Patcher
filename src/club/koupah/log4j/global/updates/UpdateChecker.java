package club.koupah.log4j.global.updates;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import club.koupah.log4j.Patcher;
import club.koupah.log4j.global.configuration.Config;
import club.koupah.log4j.global.utils.Loggable;

/**
 * @author Koupah
 * @createdAt 2:23:19 am on 14 Dec 2021
 */

public class UpdateChecker implements Loggable {

	// Sick variable name!
	private static final String REPO_RELEASES_API_URL = "https://api.github.com/repos/Koupah/MC-Log4J-Exploit-Checker/releases/latest";

	boolean checking;
	UpdateThread thread;

	String latestVersion;

	public UpdateChecker() {
		this.checking = false;
		this.thread = new UpdateThread(this);
		this.latestVersion = null;
	}

	public boolean isRunning() {
		return checking;
	}

	public boolean hasUpdate() {
		return latestVersion != null && !latestVersion.equals(Patcher.version);
	}

	public void checkForUpdate() {
		if (checking) {
			log("Cannot check for update as we're already checking!");
			return;
		}

		checking = true;

		if (Config.Option.ASYNC_UPDATE_CHECK.get().asBoolean()) {
			log("Checking for update in the background.");
			thread.start();
		} else {
			log("Checking for update.");
			thread.run();
			checking = false;
		}
	}

	private static class UpdateThread extends Thread {
		UpdateChecker updater;

		public UpdateThread(UpdateChecker updateChecker) {
			this.updater = updateChecker;
		}

		public void run() {
			try {
				URL url = new URL(REPO_RELEASES_API_URL);

				/*
				 * I remember for years when I was still learning Java I would always google how
				 * to do this. Nowadays I know how to do this off by heart, but when I was
				 * younger I remember almost always going to the same StackerOverflow post.
				 * 
				 * Search query almost every time: "java read from website"
				 * 
				 * God bless this StackOverflow post
				 * https://stackoverflow.com/questions/5867975/reading-websites-contents-into-
				 * string
				 */

				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

				// The content read should simply be one large JSON Object
				String line = br.readLine();

				// If the line isn't null and it does indeed contain atleast 1 tag_name
				if (line != null && line.contains("tag_name")) {
					String split = line.split("\"tag_name\":")[1];

					/*
					 * In all honesty, I don't normally do this LMAO
					 * 
					 * Normally I would just do something like:
					 * line.split("\"tag_name\":")[1].split(",")[0];
					 */
					StringBuilder sb = new StringBuilder();
					int index = 0;
					while (true) {
						char next = split.charAt(index);
						if (next == ',')
							break;
						sb.append(next);
						index++;
					}

					// Remove the surrounding quotation marks
					sb.setLength(sb.length() - 1);

					updater.latestVersion = sb.substring(1);
					updater.log("Latest version is " + updater.latestVersion);

					if (updater.hasUpdate()) {
						updater.log("---------------------------------");
						updater.log("There may be an update available!");
						updater.log("You're on: " + Patcher.version);
						updater.log("Latest: " + updater.latestVersion);
						updater.log("---------------------------------");
					}
				} else {
					System.out.println("Failed to find a release! Assuming current version is newest.");
				}
			} catch (Exception e) {
				/*
				 * Catch all exceptions.
				 * 
				 * I know this is HORRIBLE HORRIBLE HORRIBLE (horrible) practice, but I honestly
				 * only do it for the cleanliness. Seriously, I don't mind handling all the
				 * different exceptions differently based on what they are, but I just find it
				 * so gosh ugly.
				 */

				updater.log(
						"Exception whilst checking latest version! Assuming current version is newest. [Exception Message: "
								+ e.getMessage() + "]");
			}

			updater.checking = false;
		}
	}

}
