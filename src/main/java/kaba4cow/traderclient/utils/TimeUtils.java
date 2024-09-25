package kaba4cow.traderclient.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public final class TimeUtils {

	private TimeUtils() {
	}

	public static Long getTimestamp(ZonedDateTime dateTime) {
		return dateTime.toInstant().toEpochMilli();
	}

	public static ZonedDateTime getDateTime(Long timestamp) {
		return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
	}

}
