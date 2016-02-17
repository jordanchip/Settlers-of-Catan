package shared.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import shared.definitions.*;
import shared.exceptions.InsufficientResourcesException;
import shared.exceptions.SchemaMismatchException;
import shared.exceptions.TradeException;

/** An immutable representation of an exchange of resources.
 * @author beefster
 *
 */
public class ResourceTradeList 
implements Serializable {
	
	private static final long serialVersionUID = -716474559929703075L;
	
	Map<ResourceType, Integer> offered;
	Map<ResourceType, Integer> wanted;
	
	public ResourceTradeList(JSONObject json) throws SchemaMismatchException {
		offered = new HashMap<>();
		wanted = new HashMap<>();
		try {
			JSONObject JSONOffered = (JSONObject) json.get("offered");
			JSONObject JSONWanted = (JSONObject) json.get("wanted");
			for (ResourceType type : ResourceType.values()) {
				String resource = type.toString();
				if (JSONOffered.containsKey(resource)) {
					offered.put(type, (int) (long) JSONOffered.get(resource));
				}
				if (JSONWanted.containsKey(resource)) {
					wanted.put(type, (int) (long) JSONWanted.get(resource));
				}
			}
		} catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not match the expected schema" +
					"for a ResourceTradeList:\n" + json.toJSONString());
		}
	}
	
	public ResourceTradeList() {
		// TODO Auto-generated constructor stub
	}

	public ResourceTradeList(Map<ResourceType, Integer> offered,
			Map<ResourceType, Integer> wanted) {
		this.offered = offered;
		this.wanted = wanted;
	}

	/**
	 * @return a copy of the offered resources, as a Map
	 */
	public Map<ResourceType, Integer> getOffered() {
		return new HashMap<>(offered);
	}

	/**
	 * @return a copy of the wanted resources, as a Map
	 */
	public Map<ResourceType, Integer> getWanted() {
		return new HashMap<>(wanted);
	}

	/** Makes this trade between two ResourceLists
	 * @param offerer the ResourceList to trade from
	 * @param receiver the ResourceList to trade to
	 * @pre <ul>
	 * <li>The offerer has at least what is offered</li>
	 * <li>the receiver has at least what is wanted.</li>
	 * </ul>
	 * @post The offered resources will be taken from the offerer and given to the receiver
	 * and the wanted resources will be taken from the receiver and given to the offerer.
	 * @throws TradeException if either of the preconditions are not met
	 */
	public void makeExchange(ResourceList offerer, ResourceList receiver) throws TradeException {
		// Exception check. You can't just use transferTo and re-purpose the exception because
		// trades are all-or-nothing transactions, and the transferTo could cause this method to
		// be stopped part way through the trade (obviously bad)
		for (Entry<ResourceType, Integer> resource : offered.entrySet()) {
			if (offerer.count(resource.getKey()) < resource.getValue()) throw new TradeException();
		}
		for (Entry<ResourceType, Integer> resource : wanted.entrySet()) {
			if (receiver.count(resource.getKey()) < resource.getValue()) throw new TradeException();
		}
		
		// Implementation
		for (Entry<ResourceType, Integer> resource : offered.entrySet()) {
			try {
				offerer.transfer(receiver, resource.getKey(), resource.getValue());
			} catch (InsufficientResourcesException e) {
				// This should never happen! This is a critical error.
				e.printStackTrace();
				assert false;
			}
		}
		for (Entry<ResourceType, Integer> resource : wanted.entrySet()) {
			try {
				receiver.transfer(offerer, resource.getKey(), resource.getValue());
			} catch (InsufficientResourcesException e) {
				// This should never happen! This is a critical error.
				e.printStackTrace();
				assert false;
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResourceTradeList [offered=" + offered + ", wanted=" + wanted
				+ "]";
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject tradeList = new JSONObject();
		for (ResourceType type : ResourceType.values()) {
			String resource = type.toString().toLowerCase();
			if(wanted.containsKey(type) &&
					wanted.get(type) > 0){
				tradeList.put(resource, -wanted.get(type));
			}
			else if(offered.containsKey(type) &&
					offered.get(type) > 0){
				tradeList.put(resource, offered.get(type));
			}
			else{
				tradeList.put(resource, 0);
			}
		}
		return tradeList;
	}

	public void setWanted(Map<ResourceType, Integer> wanted) {
		this.wanted = wanted;
		
	}
	public void setOffered(Map<ResourceType, Integer> offered) {
		this.offered = offered;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((offered == null) ? 0 : offered.hashCode());
		result = prime * result + ((wanted == null) ? 0 : wanted.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceTradeList other = (ResourceTradeList) obj;
		if (offered == null) {
			if (other.offered != null)
				return false;
		} else if (!offered.equals(other.offered))
			return false;
		if (wanted == null) {
			if (other.wanted != null)
				return false;
		} else if (!wanted.equals(other.wanted))
			return false;
		return true;
	}

	public boolean isEmpty() {
		if (offered == null || wanted == null) {
			return true;
		}
		for (int count : offered.values()) {
			if (count > 0) return false;
		}
		for (int count : wanted.values()) {
			if (count > 0) return false;
		}
		return true;
	}
	
}
