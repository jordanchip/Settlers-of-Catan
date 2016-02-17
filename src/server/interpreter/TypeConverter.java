package server.interpreter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TypeConverter {

	public static Object convertString(String string, Class<?> type)
			throws UnsupportedTypeException {
		if (type.equals(String.class)) {
			return string;
		} else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
			return Integer.parseInt(string);
		} else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
			return Long.parseLong(string);
		} else if (type.equals(Short.TYPE) || type.equals(Short.class)) {
			return Short.parseShort(string);
		} else if (type.equals(Byte.TYPE) || type.equals(Byte.class)) {
			return Byte.parseByte(string);
		} else if (type.equals(Boolean.TYPE) || type.equals(Boolean.class)) {
			return Boolean.parseBoolean(string);
		} else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
			return Double.parseDouble(string);
		} else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
			return Float.parseFloat(string);
		} else {
			if (type.isEnum()) {
				try {
					Method converter = type.getMethod("fromString",
							String.class);
	
					if (Modifier.isStatic(converter.getModifiers())
							&& converter.getReturnType().equals(type)) {
						return converter.invoke(null, string);
					} else {
						throw new UnsupportedTypeException(String.class, type);
					}
				} catch (NoSuchMethodException | SecurityException
						| IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new UnsupportedTypeException(String.class, type);
				}
	
			} else {
				try {
					return type.getConstructor(String.class).newInstance(string);
				} catch (NoSuchMethodException | SecurityException
						| InstantiationException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					throw new UnsupportedTypeException(String.class, type);
				}
			}
		}
	}

}
