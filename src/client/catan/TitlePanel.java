package client.catan;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import client.utils.FontUtils;

import shared.definitions.*;

@SuppressWarnings("serial")
public class TitlePanel extends JPanel
{
	private JLabel titleLabel;
	
	public TitlePanel()
	{
		this.setLayout(new BorderLayout());
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		titleLabel = new JLabel("CS 340: Settlers");
		titleLabel.setOpaque(true);
		
		FontUtils.setFont(titleLabel, 20);
		
		this.add(titleLabel, BorderLayout.CENTER);
	}
	
	public void setLocalPlayerColor(CatanColor value)
	{
		this.setBackground(value.getJavaColor());
		titleLabel.setBackground(value.getJavaColor());
	}
	
}

