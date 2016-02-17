package shared.definitions;

import java.awt.Color;

public enum CatanColor
{
	RED, ORANGE, YELLOW, BLUE, GREEN, PURPLE, PUCE, WHITE, BROWN;
	
	private Color color;
	
	static
	{
		RED.color = new Color(227, 66, 52);
		ORANGE.color = new Color(255, 165, 0);
		YELLOW.color = new Color(253, 224, 105);
		BLUE.color = new Color(111, 183, 246);
		GREEN.color = new Color(109, 192, 102);
		PURPLE.color = new Color(157, 140, 212);
		PUCE.color = new Color(204, 136, 153);
		WHITE.color = new Color(223, 223, 223);
		BROWN.color = new Color(161, 143, 112);
	}
	
	public Color getJavaColor()
	{
		return color;
	}
	
	static public CatanColor getColorFromString(String input) throws IllegalArgumentException{
		String lowerInput = input.toLowerCase();
		if (lowerInput.equals("red")) return RED;
		else if (lowerInput.equals("orange")) return ORANGE;
		else if (lowerInput.equals("yellow")) return YELLOW;
		else if (lowerInput.equals("green")) return GREEN;
		else if (lowerInput.equals("blue")) return BLUE;
		else if (lowerInput.equals("purple")) return PURPLE;
		else if (lowerInput.equals("puce")) return PUCE;
		else if (lowerInput.equals("white")) return WHITE;
		else if (lowerInput.equals("brown")) return BROWN;
		else throw new IllegalArgumentException();
	}
}

