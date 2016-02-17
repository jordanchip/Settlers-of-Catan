package server.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import org.json.simple.JSONObject;
import shared.exceptions.InvalidActionException;
import shared.model.ModelFacade;

/**
 * A generic implementation of commands that uses reflection to do its business.
 * <div>This makes class makes certain assumptions:
 * <ul>
 * <li>all arguments are serializable to JSONObjects with one call to toJSONObject
 * if they are not builtin types.</li>
 * <li>all non-builtin types of the arguments must either have a static fromJSONObject
 * method or a constructor that takes a single JSONObject (the model classes all qualify
 * for this assumption)</li>
 * </div>
 * @author Justin Snyder
 * 
 */
public class CatanCommand implements ICatanCommand {

	public static void main(String[] args) throws Exception {
		ModelFacade model = new ModelFacade();
		ICatanCommand command_old = new CatanCommand("print", model,
				"If this works, then CatanCommand serialization is probably working!");
		
		String serializedString = CommandSerializer.serialize(command_old);

		System.out.println(serializedString);

		ICatanCommand command = CommandSerializer.deserialize(serializedString);

		command.execute(model);
	}

	private Method method;
	private Object[] arguments;

	/**
	 * Creates a generic command from the ModelFacade class by inferring the correct
	 * method from argument types
	 * 
	 * @param method the name of the method
	 * @param args the arguments you want to pass to the method
	 * @throws SecurityException if the method is inaccessible
	 * @throws NoSuchMethodException if the method doesn't exist
	 */
	public CatanCommand(String method, Object... args)
			throws NoSuchMethodException, SecurityException {
		setDispatch(method, args);
	}

	// Infers the correct method to use from argument types
	private void setDispatch(String method, Object... args)
			throws NoSuchMethodException, SecurityException {
		List<Class<?>> argTypes = new ArrayList<>();
		for (Object arg : args) {
			argTypes.add(arg.getClass());
		}
		//
		//I HAVE CHANGE FROM ClientModelFacade.class to ModelFacade.class
		//Might need to change this back!!
		// Nope. This should be fine. It just got caught by an automated
		// Refactor that I did earlier.
		//
		this.method = ModelFacade.class.getMethod(method,
				argTypes.toArray(new Class<?>[argTypes.size()]));
		arguments = args;
	}

	/** Creates a CatanCommand from a JSON representation
	 * @param json
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public CatanCommand(JSONObject json) throws Exception {
		String methodName = (String) json.get("method");
		List<Object> jsonArgs = (List<Object>) json.get("arguments");

		List<Object> args = new ArrayList<>();
		for (Object arg : jsonArgs) {
			if (arg instanceof JSONObject) {
				JSONObject jsonArg = (JSONObject) arg;
				Class<?> type = Class.forName((String) jsonArg.get("<class>"));
				jsonArg.remove("<class>");
				try {
					Method converter = type.getMethod("fromJSONObject", JSONObject.class);

					if (Modifier.isStatic(converter.getModifiers())
							&& converter.getReturnType().equals(type)) {
						args.add(converter.invoke(null, jsonArg));
					}
					else throw new Exception(); // a bit hackish...
				} catch (Exception e) {
					try {
						Constructor<?> ctor = type
								.getConstructor(JSONObject.class);

						args.add(ctor.newInstance(jsonArg));
					} catch (Exception ex) {
						ex.printStackTrace();
						args.add(null);
					}
				}
			} else {
				args.add(arg);
			}
		}
		
		setDispatch(methodName, args.toArray());
	}

	@Override
	public void execute(ModelFacade model) throws InvalidActionException {
		try {
			model.getCatanModel().toString();
			method.invoke(model, arguments);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new InvalidActionException(e.getCause().getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} // Let through IllegalArgumentExceptions
	}

	public CatanCommandInfo getInfo() {
		return new CatanCommandInfo(method.getName(), arguments);
	}

	@Override
	public SerializableCatanCommand getSerializable() {
		return getInfo();
	}
	
	@Override
	public String toString() {
		return getInfo().toString();
	}

}
