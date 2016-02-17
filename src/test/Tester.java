package test;

import server.communication.ServerCommunicator;
import client.main.Catan;

public class Tester {

	public static void main(String args[]) {
		String[] args2 = new String[0];
		ServerCommunicator.main(args2);
		Catan.main(args2);
		Catan.main(args2);
		Catan.main(args2);
		Catan.main(args2);
	}
}
