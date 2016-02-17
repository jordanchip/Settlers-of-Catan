package server.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import server.Factories.FileDAOFactory;
import server.Factories.IDAOFactory;
import server.Factories.MockDAOFactory;

public class PluginRegistry {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		PluginRegistry registry = getSingleton();
		
		if (args.length == 0) {
			System.out.println(registry.plugins);
			return;
		}
		
		switch (args[0]) {
		case "register":
			registry.registerPlugin(args[1], args[2], args[3]);
		default:
			System.out.println("Command not recognized " + args[0]);
		}
	}
	
	private static PluginRegistry singleton = null;
	
	public static PluginRegistry getSingleton() {
		if (singleton == null) {
			singleton = new PluginRegistry();
		}
		return singleton;
	}
	
	private Map<String, PluginMetadata> plugins;
	private File registryFile;
	private Map<String, Class<? extends IDAOFactory>> pluginCache;
	
	private PluginRegistry() {
		registryFile = new File("plugins.registry");
		if (registryFile.exists() && !registryFile.isDirectory()) {
			loadRegistryFile();
		}
		else {
			plugins = new HashMap<>();
		}
		pluginCache = new HashMap<>();
	}
	
	public PluginMetadata getMetadata(String pluginName) {
		return plugins.get(pluginName);
	}
	
	@SuppressWarnings("unchecked")
	public IDAOFactory getDAOFactory(String pluginName)
			throws InstantiationException {
		if (!plugins.containsKey(pluginName)) {
//			return new FileDAOFactory();
			return new MockDAOFactory();
//			throw new IllegalArgumentException();
		}
		
		if (pluginCache.containsKey(pluginName)) {
			Class<? extends IDAOFactory> daoClass = pluginCache.get(pluginName);
			try {
				return daoClass.newInstance();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			try {
				PluginMetadata pluginMeta = plugins.get(pluginName);
				URLClassLoader classLoader = new URLClassLoader(
						new URL[] {pluginMeta.getJarURL()});
				Class<? extends IDAOFactory> daoClass = 
						(Class<? extends IDAOFactory>) 
						Class.forName(pluginMeta.getClassName(), true, classLoader);
				return daoClass.newInstance();
			} catch (MalformedURLException | ClassNotFoundException |
					ClassCastException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void registerPlugin(String pluginName, String jarFileLocation,
			String className) {
		PluginMetadata meta = new PluginMetadata(pluginName, jarFileLocation, className);
		
		plugins.put(pluginName, meta);
		
		saveRegistryFile();
	}
	
	@SuppressWarnings("unchecked")
	private void loadRegistryFile() {
		try {
			ObjectInputStream registry = new ObjectInputStream(
					new FileInputStream(registryFile));
			plugins = (Map<String, PluginMetadata>) registry.readObject();
			registry.close();
		} catch (IOException | ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}
	}
	
	private void saveRegistryFile() {
		try {
			ObjectOutputStream registry = new ObjectOutputStream(
					new FileOutputStream(registryFile));
			registry.writeObject(plugins);
			registry.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
