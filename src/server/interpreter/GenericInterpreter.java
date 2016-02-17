package server.interpreter;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.Map.Entry;

/**
 * <b>Gotcha:</b> varargs <i>cannot</i> be primitive types.
 * 
 * @author Justin Snyder
 * 
 */
public class GenericInterpreter extends SimpleInterpreter {

	private static Map<String, Method> dispatchTable = null;
	private int accessLevel;

	// Workaround for this not being able to be static
	private void initializeDispatchTable() {
		dispatchTable = new HashMap<>();

		for (Method method : getClass().getMethods()) {
			if (method.isAnnotationPresent(Command.class)) {

				String commandName = camelToDash(method.getName());
				// you can't overload commands... yet.
				assert !dispatchTable.containsKey(commandName);

				dispatchTable.put(commandName, method);

				// annotation is also used to derive help data
				Command command = null;
				for (Annotation annotation : method.getAnnotations()) {
					if (annotation instanceof Command) {
						command = (Command) annotation;
						break;
					}
				}
				// command cannot be null at this point
				String description = command.description();
				if (description.equals("?")) {
					description = command.info();
				}

			}
		}

	}

	private static String camelToDash(String str) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < str.length(); ++i) {
			char c = str.charAt(i);
			if (Character.isUpperCase(c)) {
				result.append('-');
			}
			result.append(Character.toLowerCase(c));
		}

		return result.toString();
	}

	public GenericInterpreter(OutputStream ostream) {
		super(ostream);

		// This can't be done polymorphically at the static level,
		// so we'll do it on first instantiation instead.
		if (dispatchTable == null) initializeDispatchTable();
		
		accessLevel = 0;
	}

	@Override
	final protected void handle(String command, String[] strArgs) {
		command = camelToDash(command);
		if (dispatchTable.containsKey(command)) {
			Method handler = dispatchTable.get(command);
			
			// Check access level
			Command metadata = handler.getAnnotation(Command.class);
			if (accessLevel < metadata.runLevel()) {
				// You don't have access to this command
				// Check if the command is viewable to the user
				if (accessLevel >= metadata.viewLevel()) {
					getWriter().println(accessDeniedString());
				}
				else {
					// Pretend the command doesn't even exist if it is unviewable
					getWriter().println("No such command: " + command);
				}
				return;
			}
			
			int numArgs = handler.getParameterTypes().length;

			if (!handler.isVarArgs() && strArgs.length > numArgs) {
				getWriter().println("Too many arguments.");
				helpOnCommand(command);
				return;
			}

			List<Object> args = getArgList(command, strArgs, handler, numArgs);
			if (args == null) return;

			try {
				Object result = handler.invoke(this, args.toArray());
				if (result != null) {
					getWriter().println(resultString() + result.toString());
				}
			} catch (IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException | NullPointerException
					| ClassCastException e) {
				e.printStackTrace();
				helpOnCommand(command);
			}
		} else {
			getWriter().println("No such command: " + command);
		}
	}

	protected List<Object> getArgList(String command, String[] strArgs,
			Method handler, int numArgs) {
		// Convert args to typed args
		List<Object> args = new ArrayList<>();
		int currentArg = 0;
		Class<?> varArgType = null;
		for (Class<?> type : handler.getParameterTypes()) {
			if (handler.isVarArgs() && currentArg >= numArgs - 1) {
				varArgType = type;
				break;
			}

			Object value = null;
			if (currentArg < strArgs.length) {
				try {
					value = TypeConverter.convertString(strArgs[currentArg], type);
				} catch (Exception e) {
					getWriter().println("Invalid type for argument #" + currentArg);
					getWriter().println(e.getMessage());
					helpOnCommand(command);
					return null;
				}
			}
			args.add(value);
			++currentArg;
		}

		// varargs
		if (varArgType != null && handler.isVarArgs()) {
			Class<?> type = varArgType.getComponentType();
			List<Object> varArgs = new ArrayList<>();
			for (; currentArg < strArgs.length; ++currentArg) {
				try {
					varArgs.add(TypeConverter.convertString(strArgs[currentArg], type));
				} catch (Exception e) {
					getWriter().println("Invalid type for argument #" + currentArg);
					getWriter().println(e.getMessage());
					helpOnCommand(command);
					return null;
				}
			}
			processVarArgs(args, varArgs, currentArg, type);
		}
		return args;
	}

	protected String accessDeniedString() {
		return "Access denied!";
	}

	protected String resultString() {
		return "RESULT: ";
	}

	protected String requirementDescription(int runLevel) {
		return "Requires access level: " + runLevel;
	}

	@SuppressWarnings("unchecked")
	private <T> void processVarArgs(List<Object> args, List<Object> varArgs,
			int currentArg, Class<T> type) {
		T[] arr = (T[]) Array.newInstance(type, varArgs.size());
		for (int i = 0; i < varArgs.size(); ++i) {
			arr[i] = (T) type.cast(varArgs.get(i));
		}

		// Make sure it's the correct type for varargs
		args.add((T[]) arr);
	}

	/**
	 * @param accessLevel the accessLevel to set
	 */
	final protected void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	@Command(info = "Closes this interpreter session.")
	public void quit() {
		exitInterpreter();
	}

	@Command(info = "Echoes user input.")
	public void echo(String... input) {
		PrintWriter out = getWriter();
		for (String chunk : input) {
			out.print(chunk);
			out.print(' ');
		}
		out.println();
	}

	@Command(info = "Shows a list of available commands")
	public void commands() {
		PrintWriter out = getWriter();

		List<String> commands = new ArrayList<>();
		for (Entry<String, Method> dispatchEntry : dispatchTable.entrySet()) {
			String command = dispatchEntry.getKey();
			Command info = dispatchEntry.getValue().getAnnotation(Command.class);
			if (accessLevel >= info.viewLevel()) {
				commands.add(command);
			}
		}
		
		Collections.sort(commands);
		out.print("Available Commands: ");
		Iterator<String> iter = commands.iterator();
		while (iter.hasNext()) {
			out.print(iter.next());
			if (iter.hasNext()) {
				out.print(", ");
			}
		}

		out.println();
	}

	@Command(args = { "[command]" }, info = "Gives help for all commands or a specific command.", description = "Shows a list of available commands and their descriptions. If used "
			+ "on a single command, this gives a long description of the given command.")
	public void help(String command) {
		if (command == null) {
			help();
		} else {
			helpOnCommand(command);
		}
	}
	
	private class HelpEntry implements Comparable<HelpEntry> {
		String command;
		String[] args;
		String info;
		
		HelpEntry(String command, String[] args, String info) {
			this.command = command;
			this.args = args;
			this.info = info;
		}

		@Override
		public int compareTo(HelpEntry other) {
			return command.compareTo(other.command);
		}
		
		// This includes the space at the end...
		int argsLength() {
			int length = 0;
			for (String arg : args) {
				length += arg.length() + 1;
			}
			return length;
		}
		
		String toPaddedString(int commandLength, int argsLength) {
			StringBuilder result = new StringBuilder();
			
			result.append(command);
			for (int i=command.length(); i<=commandLength; ++i) {
				result.append(' ');
			}
			
			int pos = 0;
			for (String arg : args) {
				result.append(arg);
				result.append(' ');
				pos += arg.length() + 1;
			}
			for (; pos<argsLength; ++pos) {
				result.append(' ');
			}
			
			result.append(info);
			
			return result.toString();
		}
	}

	private void help() {
		PrintWriter out = getWriter();

		out.println("Available Commands:");
		Set<HelpEntry> commandInfo = new TreeSet<>();
		int maxCommandLength = 0;
		int maxArgsLength = 0;
		for (Entry<String, Method> dispatchEntry : dispatchTable.entrySet()) {
			String command = dispatchEntry.getKey();
			Command info = dispatchEntry.getValue().getAnnotation(Command.class);
			if (accessLevel >= info.viewLevel()) {
				HelpEntry help = new HelpEntry(command, info.args(), info.info());
				commandInfo.add(help);
				if (help.command.length() > maxCommandLength) {
					maxCommandLength = help.command.length();
				}
				if (help.argsLength() > maxArgsLength) {
					maxArgsLength = help.argsLength();
				}
			}
		}

		for (HelpEntry helpEntry : commandInfo) {
			out.print("  ");
			out.println(helpEntry.toPaddedString(maxCommandLength, maxArgsLength));
		}
	}

	private void helpOnCommand(String command) {
		PrintWriter out = getWriter();

		if (!dispatchTable.containsKey(command)) {
			out.println("Unrecognized command: " + command);
			return;
		}

		Command info = dispatchTable.get(command).getAnnotation(Command.class);
		
		if (accessLevel < info.viewLevel()) {
			// You can't see the command, so pretend it doesn't exist
			out.println("Unrecognized command: " + command);
			return;
		}

		out.print(command + " ");
		for (String arg : info.args()) {
			out.print(arg + " ");
		}
		out.println();
		
		out.println(requirementDescription(info.runLevel()));

		if (!info.description().equals(""))	out.println(info.description());
		else out.println(info.info());
	}

}
