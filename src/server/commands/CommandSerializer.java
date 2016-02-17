package server.commands;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.xml.bind.DatatypeConverter;

public class CommandSerializer {
	
	public static byte[] serializeBytes(ICatanCommand command)
			throws CommandSerializationException {
		SerializableCatanCommand serCommand = command.getSerializable();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		try {
			ObjectOutputStream oStream = new ObjectOutputStream(
					new GZIPOutputStream(buffer));
			oStream.writeObject(serCommand);
			oStream.close();
		} catch (IOException e) {
			throw new CommandSerializationException("Could not serialize the command.", e);
		}
		
		return buffer.toByteArray();
	}
	
	public static ICatanCommand deserializeBytes(byte[] input)
			throws CommandSerializationException {
		ByteArrayInputStream buffer = new ByteArrayInputStream(input);
		
		try {
			ObjectInputStream iStream = new ObjectInputStream(
					new GZIPInputStream(buffer));
			ICatanCommand result = ((SerializableCatanCommand) iStream.readObject()).getCommand();
			iStream.close();
			return result;
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			throw new CommandSerializationException("Could not deserialize the command.", e);
		}
	}
	
	/** Serializes a command so it can be stored or transmitted
	 * @param command The command to serialize
	 * @return A string containing a base64-formatted representation of the command
	 * @throws CommandSerializationException if something prevented the command from being serialized.
	 */
	public static String serialize(ICatanCommand command)
			throws CommandSerializationException {
		return DatatypeConverter.printBase64Binary(serializeBytes(command));
		//return new String(serializeBytes(command), StandardCharsets.ISO_8859_1);
	}
	
	/** Deserializes a command that was previously serialized
	 * @param input The base64-formatted string
	 * @return An ICatanCommand with the same data as was represented by the base64
	 * @throws CommandSerializationException if something else prevented the command
	 * from being deserialized
	 */
	public static ICatanCommand deserialize(String input)
			throws CommandSerializationException {
		return deserializeBytes(DatatypeConverter.parseBase64Binary(input));
		//return deserializeBytes(input.getBytes(StandardCharsets.ISO_8859_1));
	}

}
