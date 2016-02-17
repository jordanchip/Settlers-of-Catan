package server.telnet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import server.interpreter.GenericInterpreter;
import server.interpreter.Interpreter;

public class TelnetServer implements Runnable {

	public static final int DEFAULT_PORT = 2323;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TelnetServer telnet = new TelnetServer(
				new GenericInterpreterFactory(
						GenericInterpreter.class));
		System.out.println("Starting Telnet Server on port " + DEFAULT_PORT);
		telnet.run();
	}

	private ServerSocket serverSocket;
	private InterpreterFactory interpreterFactory;

	public TelnetServer(int port, InterpreterFactory interpreterFactory)
			throws IOException {
		serverSocket = new ServerSocket(port);
		this.interpreterFactory = interpreterFactory;
	}

	public TelnetServer(InterpreterFactory interpreter)
			throws IOException {
		this(DEFAULT_PORT, interpreter);
	}

	@Override
	public void run() {
		while (true) {
			try {
				final Socket socket = serverSocket.accept();
				System.out.println("Accepted Telnet connection from "
						+ socket.getInetAddress().toString() + ":"
						+ socket.getPort());

				OutputStream os = socket.getOutputStream();
				final Interpreter interpreter = interpreterFactory
						.getInterpreter(os);

				new Thread() {

					@Override
					public void run() {
						try {
							interpreter.onOpen();

							BufferedReader br = new BufferedReader(new InputStreamReader(
									socket.getInputStream()));

							while (interpreter.isActive()) {
								interpreter.prompt();
								interpreter.interpret(br.readLine());
							}

							interpreter.onClose();

						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							System.out.println("Closing Telnet connection from "
									+ socket.getInetAddress().toString() + ":"
									+ socket.getPort());
							try {
								if (!socket.isClosed()) socket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					
				}.start();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
