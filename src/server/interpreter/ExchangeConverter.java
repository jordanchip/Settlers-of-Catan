package server.interpreter;

import java.io.IOException;
import java.io.InputStream;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpExchange;

public class ExchangeConverter {

	public static JSONObject toJSON(HttpExchange e) throws IOException, ParseException {
		InputStream input = e.getRequestBody();
		int len = 0;
		
		byte[] buffer = new byte[1024];
		StringBuilder string = new StringBuilder();
		while(-1 != (len = input.read(buffer))){
			string.append(new String(buffer, 0, len));
		}

		JSONParser parser = new JSONParser();
		System.out.println(string.toString());
		JSONObject json = (JSONObject) parser.parse(string.toString());
		
		return json;
	}
}
