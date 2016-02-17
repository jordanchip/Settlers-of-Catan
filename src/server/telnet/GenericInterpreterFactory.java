package server.telnet;

import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import server.interpreter.GenericInterpreter;
import server.interpreter.Interpreter;

public class GenericInterpreterFactory implements
		InterpreterFactory {
	
	Constructor<? extends GenericInterpreter> interpCtor;
	Object[] extraArgs;
	
	public GenericInterpreterFactory(
			Class<? extends GenericInterpreter> interpClass, Object... extraArgs)
			throws NoSuchMethodException, SecurityException {
		this.extraArgs = extraArgs;
		List<Class<?>> params = new ArrayList<>();
		params.add(OutputStream.class);
		for (Object arg : extraArgs) {
			params.add(arg.getClass());
		}
		this.interpCtor = interpClass.getConstructor(params.toArray(new Class<?>[params.size()]));
	}

	@Override
	public Interpreter getInterpreter(OutputStream out) {
		try {
			List<Object> args = new ArrayList<>();
			args.add(out);
			for (Object arg : extraArgs) args.add(arg);
			return interpCtor.newInstance(args.toArray());
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

}
