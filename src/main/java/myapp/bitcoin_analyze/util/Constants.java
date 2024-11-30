package myapp.bitcoin_analyze.util;

/**
 * Application constants.
 */
public final class Constants {
    public static final String FILE_NAME = "fileName";
    public static final String MEDIA_TYPE = "mediaType";
    public static final String CLOUD = "cloud";
    public static final String PROD = "PROD";
	// Regex for acceptable logins
	public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

	public static final String SYSTEM = "system";
	public static final String DEFAULT_LANGUAGE = "en";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String DATE_MSG_FORMAT = "yyyyMMdd";
	public static final String YES = "Y";
	public static final String NO = "N";

	public static final int DAYS_OF_WEEK = 7;
	public static final String DAILY ="DAILY";
	public static final String WEEKLY ="WEEKLY";
	public static final String MONTHLY ="MONTHLY";

	public static final String SCHEDULED_TASKS = "scheduledTasks";

	private Constants() {
	}

	public enum CRON_JOB_STATUS {
		COMPLETE("C", "COMPLETE"), RUNNING("R", "RUNNING"), FAILED("F", "FAILED");

		private final String key;
		private final String value;

		CRON_JOB_STATUS(String key, String value) {
			this.key = key;
			this.value = value;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}
}
