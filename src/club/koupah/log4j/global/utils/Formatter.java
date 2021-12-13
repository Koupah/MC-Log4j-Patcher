package club.koupah.log4j.global.utils;

import java.util.Arrays;
import java.util.LinkedList;

public class Formatter {

	public LinkedList<Object> parts;
	Formatter parent;

	public Formatter(String content) {
		this(null, content);
	}

	public Formatter(Formatter parent, String content) {
		this.parts = !hasFormat(content) ? new LinkedList<Object>(Arrays.asList(content))
				: getParts(this, content, true);
		this.parent = parent;
	}

	private static final String[] empty = new String[0];

	private static final char[] start = new char[] { '$', '{' };
	private static final char end = '}';

	public String getFormats() {
		StringBuilder sb = new StringBuilder();
		getFormats(sb);
		return sb.toString();
	}

	public void getFormats(StringBuilder sb) {
		final int size = parts.size();
		for (int i = 0; i < size; i++) {
			Object part = parts.get(i);
			if (part instanceof String) {
				Object closer;
				if (((String) part).equals("${") && i + 2 < size && (closer = parts.get(i + 2)) instanceof String
						&& ((String) closer).equals("}")) {
					sb.append("${" + ((Formatter) parts.get(i + 1)).getContent() + "}");
					i += 2;
				}
			} else {
				((Formatter) part).getFormats(sb);
			}
		}
	}

	public Formatter replaceFormats(String string) {
		final int size = parts.size();
		for (int i = 0; i < size; i++) {
			Object part = parts.get(i);
			if (part instanceof String) {
				Object closer;
				if (((String) part).equals("${") && i + 2 < size && (closer = parts.get(i + 2)) instanceof String
						&& ((String) closer).equals("}")) {
					parts.set(i + 1, string);
					i += 2;
				}
			} else {
				((Formatter) part).replaceFormats(string);
			}
		}
		return this;
	}

	public String getContent() {
		StringBuilder sb = new StringBuilder();
		getContent(sb);
		return sb.toString();
	}

	public void getContent(StringBuilder sb) {
		for (Object part : parts) {
			if (part instanceof String) {
				sb.append(part);
			} else {
				((Formatter) part).getContent(sb);
			}
		}
	}

	public static boolean hasFormat(String content) {
		return getFormatIndex(content, true, true).length != 0;
	}

	public static LinkedList<Object> getParts(Formatter parent, String content) {
		return getParts(parent, content, false);
	}

	public static LinkedList<Object> getParts(Formatter parent, String content, boolean deep) {
		LinkedList<Object> parts = new LinkedList<Object>();
		int[] locations = getFormatIndex(content, false, false);

		String[] results = locations.length == 0 ? empty : new String[locations.length];

		if (results.length != 0) {
			for (int i = 0; i < results.length; i += 2) {
				final int lastEnded = i == 0 ? 0 : locations[i - 1];
				final int currentStart = locations[i];

				if (currentStart > lastEnded) {
					final String normal = content.substring(lastEnded, currentStart);
					parts.add(normal);
				}

				final String format = content.substring(locations[i] + start.length, locations[i + 1] - 1);
				parts.add(String.valueOf(start));
				parts.add(new Formatter(parent, format));
				parts.add(String.valueOf(end));
			}

			if (locations[locations.length - 1] < content.length()) {
				parts.add(content.substring(locations[locations.length - 1]));
			}

		} else if (content.length() > 0) {
			parts.add(content);
		}

		return parts;
	}

	public static int[] getFormatIndex(String string, boolean deep, boolean stopAtFirst) {
		final char[] chars = string.toCharArray();
		final int length = chars.length;

		final int[] entries = new int[(length + 3) / 3];
		final int[] found = new int[length];

		boolean colon = false;
		boolean invalid = false;

		int inside = 0;
		int complete = 0;

		for (int i = 0; i < length; i++) {
			if (chars[i] == start[0] && length > i + 1 && chars[i + 1] == start[1] && !invalid) {
				if (deep || inside == 0)
					entries[inside] = i;

				inside++;
				i++;
				continue;
			} else if (chars[i] == end && inside != 0) {
				if (/*(deep || inside == 1) &&*/ !invalid) {
					found[complete * 2] = entries[inside - 1];
					found[(complete * 2) + 1] = i + 1;
					complete++;

					if (stopAtFirst)
						break;
				}
				inside--;

				colon = false;
				invalid = false;
			} else if (!deep && inside != 0 && !colon && !invalid) {
				if (chars[i] == ':') {
					colon = true;
				} else if (chars[i] == ' ') {
					invalid = true;
				}
			}

		}

		return Arrays.copyOf(found, complete * 2);
	}

}