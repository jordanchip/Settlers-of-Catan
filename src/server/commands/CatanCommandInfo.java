package server.commands;

import java.util.Arrays;

public class CatanCommandInfo implements SerializableCatanCommand {
	private static final long serialVersionUID = 6090393259487372910L;
	
	private String methodName;
	private Object[] args;
	
	/**
	 * @param methodName
	 * @param args
	 */
	public CatanCommandInfo(String methodName, Object... args) {
		super();
		this.methodName = methodName;
		this.args = args;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @return the args
	 */
	public Object[] getArgs() {
		return args;
	}
	
	@Override
	public ICatanCommand getCommand() {
		try {
			return new CatanCommand(methodName, args);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CatanCommand [methodName=" + methodName + ", args="
				+ Arrays.toString(args) + "]";
	}
	
}
