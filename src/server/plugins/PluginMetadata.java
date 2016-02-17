package server.plugins;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

public class PluginMetadata
	implements Serializable {
	private static final long serialVersionUID = 100000;
	
	private String name;
	private String jarFileLocation;
	private String className;
	
	// Dependencies?
	
	/**
	 * @param name the name to associate with the plugin
	 * @param jarFileLocation the location of the jar file relative to the plugins folder
	 * @param className the class to load as the plugin
	 */
	PluginMetadata(String name, String jarFileLocation, String className) {
		super();
		this.name = name;
		this.jarFileLocation = jarFileLocation;
		this.className = className;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the jarFileLocation
	 */
	public String getJarFileLocation() {
		return jarFileLocation;
	}
	
	public URL getJarURL() throws MalformedURLException {
		return new File("plugins/" + jarFileLocation).toURI().toURL();
	}
	
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PluginMetadata [name=" + name + ", jarFileLocation="
				+ jarFileLocation + ", className=" + className + "]";
	}
	
}
