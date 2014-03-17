package uk.ac.bristol.hep.cbcteststand.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class I2CRegistersPanel implements TestStandPanel
{
	private HorizontalPanel mainPanel_;
	
	public I2CRegistersPanel()
	{
		mainPanel_=new HorizontalPanel();
	}

	@Override
	public Widget panel()
	{
		return mainPanel_;
	}

	@Override
	public String name()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public static String staticName()
	{
		return "I2C Registers Panel";
	}
	
}
