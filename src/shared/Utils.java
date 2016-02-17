package shared;

public class Utils {

	public static boolean isBuiltinType(Class<?> type) {
		return type.getCanonicalName().startsWith("java");
	}

}
