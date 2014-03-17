package uk.ac.bristol.hep.cbcteststand.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class I2CRegistersPanel implements TestStandPanel
{
	private HorizontalPanel mainPanel_=new HorizontalPanel();
	
	public I2CRegistersPanel()
	{
	}

	@Override
	public Widget panel()
	{
		return mainPanel_;
	}
}
