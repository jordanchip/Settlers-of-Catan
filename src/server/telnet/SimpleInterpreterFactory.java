package server.telnet;

import java.io.OutputStream;

import server.interpreter.Interpreter;
import server.interpreter.SimpleInterpreter;

public class SimpleInterpreterFactory implements InterpreterFactory {
	
	public SimpleInterpreterFactory() {}

	@Override
	public Interpreter getInterpreter(OutputStream out) {
		return new SimpleInterpreter(out);
	}

	

}
