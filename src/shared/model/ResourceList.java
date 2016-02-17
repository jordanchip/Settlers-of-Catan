package shared.model;

import java.io.Serializable;
import java.util.Map;

import shared.definitions.ResourceType;
import shared.exceptions.InsufficientResourcesException;
import shared.exceptions.SchemaMismatchException;

import java.util.*;

import org.json.simple.JSONObject;

/** A ResourceList where all counts must be non-negative
 * and all mutations are <i>zero-sum</i> between two instances.
 * (i.e. cards should never spontaneously appear or disappear.
 * Rather, they can only be transfered from one ResourceList
 * to another, not created nor destroyed.)
 * @author beefster
 *
 */
public class ResourceList 
implements Serializable {
	private static final long serialVersionUID = 327842772934319600L;
	
	private Map<ResourceType, Integer> resources;
	
	/** Creates an empty ResourceList- e.g. for a player
	 * 
	 */
	public ResourceList() {
		
	}
	
	/** Creates a ResourceList with a specific number of each resource- e.g. 
	 * for the bank at the beginning of a game.
	 * @param count the amount to have of each resource
	 * @throws IllegalArgumentException if count is negative.
	 */
	public ResourceList(int count) throws IllegalArgumentException {
		resources = new HashMap<>();
		if (count < 0) {
			throw new IllegalArgumentException("ResourceLists may not have negative counts.");
		}
		for (ResourceType type : ResourceType.values()) {
			resources.put(type, count);
		}
	}
	
	public static ResourceList fromJSONObject(JSONObject json) throws SchemaMismatchException {
		if (json.containsKey("resources")) {
			json = (JSONObject) json.get("resources");
		}
		ResourceList self = new ResourceList();
		self.resources = new HashMap<>();
		try {
			for (ResourceType type : ResourceType.values()) {
				String key = type.toString();
				if (json.containsKey(key)) {
					self.resources.put(type, (int) (long) json.get(key));
				}
				else if (json.containsKey(key.toLowerCase())) {
					self.resources.put(type, (int) (long) json.get(key.toLowerCase()));
				}
				else {
					throw new SchemaMismatchException("A resource count is missing from the " +
							"given JSONObject:\n" + json.toJSONString());
				}
			}
		} catch (ClassCastException | IllegalArgumentException e) {
			e.printStackTrace();
			throw new SchemaMismatchException("The JSON does not match the expected schema" +
					"for a ResourceList:\n" + json.toJSONString());
		}
		return self;
	}
	
	/** Creates a ResourceList with the given resource amounts. Values will be copied.
	 * @param counts a map representing the counts for each resource
	 * @throws IllegalArgumentException if any of the counts are negative.
	 */
	public ResourceList(Map<ResourceType, Integer> counts) throws IllegalArgumentException {
		for (int count : counts.values()) {
			if (count < 0) throw new IllegalArgumentException("ResourceLists may not have " +
					"negative counts.");
		}
		resources = new HashMap<>(counts);
	}

	/** Counts the total number of cards in this ResourceList
	 * @return The number of cards of all types
	 * @pre none
	 * @post none
	 */
	public int count() {
		int total = 0;
		for (int count : resources.values()) {
			total += count;
		}
		return total;
	}
	
	/** Counts the total number of cards of the given type in this ResourceList
	 * @param type
	 * @return The number of cards of the given type
	 * @pre none
	 * @post none
	 */
	public int count(ResourceType type) {
		if (resources.containsKey(type)) {
			return resources.get(type);
		}
		else {
			return 0;
		}
	}

	/** Transfers cards from one ResourceList to another
	 * @param destination the ResourceList to transfer to
	 * @param type the Type of resource to transfer
	 * @param amount the number of cards to transfer
	 * @pre This ResourceList has at least amount cards of the given type in it
	 * @post This list's number of cards will decrease by amount and the destination
	 * list will increase by amount
	 * @throws InsufficientResourcesException if the precondition is not met.
	 */
	public void transfer(ResourceList destination, ResourceType type, int amount)
			throws InsufficientResourcesException {
		if (count(type) < amount) throw new InsufficientResourcesException();
		this.resources.put(type, count(type) - amount);
		destination.resources.put(type, destination.count(type) + amount);
	}

	/** Transfers cards from one ResourceList to another, but not more than is possible.
	 * @param destination the ResourceList to transfer to
	 * @param type the Type of resource to transfer
	 * @param amount the maximum number of cards to transfer
	 * @post This list's number of cards will decrease by amount and the destination
	 * list will increase by amount
	 */
	public void transferAtMost(ResourceList destination, ResourceType type, int amount) {
		if (count(type) < amount) {
			amount = count(type);
		}
		this.resources.put(type, count(type) - amount);
		destination.resources.put(type, destination.count(type) + amount);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ResourceList [resources=" + resources + "]";
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject o = new JSONObject();
		int brick = 0;
		int ore = 0;
		int wood = 0;
		int wheat = 0;
		int sheep = 0;
		
		if(resources.containsKey(ResourceType.BRICK.toString().toLowerCase())){
			brick = resources.get(ResourceType.BRICK.toString().toLowerCase());
		}
		if(resources.containsKey(ResourceType.ORE.toString().toLowerCase())){
			ore = resources.get(ResourceType.ORE.toString().toLowerCase());
		}
		if(resources.containsKey(ResourceType.WOOD.toString().toLowerCase())){
			wood = resources.get(ResourceType.WOOD.toString().toLowerCase());
		}
		if(resources.containsKey(ResourceType.WHEAT.toString().toLowerCase())){
			wheat = resources.get(ResourceType.WHEAT.toString().toLowerCase());
		}
		if(resources.containsKey(ResourceType.SHEEP.toString().toLowerCase())){
			sheep = resources.get(ResourceType.SHEEP.toString().toLowerCase());
		}

		o.put("brick", brick);
		o.put("ore", ore);
		o.put("wood", wood);
		o.put("wheat", wheat);
		o.put("sheep", sheep);
		return o;
	}

	public Map<ResourceType, Integer> getResources() {
		return resources;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resources == null) ? 0 : resources.hashCode());
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
		ResourceList other = (ResourceList) obj;
		if (resources == null) {
			if (other.resources != null)
				return false;
		} else if (!resources.equals(other.resources))
			return false;
		return true;
	}

	public void transferRandomCard(ResourceList destination) {
		if (destination.count() == 0) return; // Nothing to steal.
		List<ResourceType> choices = new ArrayList<>();
		for (Map.Entry<ResourceType, Integer> cards : resources.entrySet()) {
			for (int i=0; i<cards.getValue(); ++i) {
				choices.add(cards.getKey());
			}
		}
		ResourceType card = choices.get(new Random().nextInt(choices.size()));
		try {
			transfer(destination, card, 1);
		} catch (InsufficientResourcesException e) {
			assert false;
			// This should NEVER happen.
		}
	}
}
