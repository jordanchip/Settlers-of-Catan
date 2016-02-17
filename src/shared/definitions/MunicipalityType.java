package shared.definitions;

public enum MunicipalityType {
	SETTLEMENT, CITY;

	private int pointValue;
	private int income;
	
	static {
		SETTLEMENT.pointValue = 1;
		SETTLEMENT.income = 1;
		CITY.pointValue = 2;
		CITY.income = 2;
	}
	public static MunicipalityType getMunicipalityTypeFromString(String arg) {
		if (arg.equals("SETTLEMENT"))
			return SETTLEMENT;
		else if (arg.equals("CITY"))
			return CITY;
		return null;
	}
	
	public int getPointValue() {
		return pointValue;
	}

	public int getIncome() {
		return income;
	}

}
