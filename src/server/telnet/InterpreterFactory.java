package server.telnet;

import java.io.OutputStream;

import server.interpreter.Interpreter;

public interface InterpreterFactory {
	
	Interpreter getInterpreter(OutputStream out);

}
