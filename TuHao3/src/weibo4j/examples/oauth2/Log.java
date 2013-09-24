package weibo4j.examples.oauth2;

public class Log {
	
//	static Logger log = Logger.getLogger(Log.class.getName());
	
    public static void logDebug(String message) {
//			log.debug(message);
    		android.util.Log.d("sswilliam", message);
	}

	public static void logInfo(String message) {
		android.util.Log.i("sswilliam", message);
//			log.info(message);
	}


}
