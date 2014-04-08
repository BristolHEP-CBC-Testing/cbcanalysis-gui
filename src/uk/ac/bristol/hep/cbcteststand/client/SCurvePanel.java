package uk.ac.bristol.hep.cbcteststand.client;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class SCurvePanel implements TestStandPanel
{
	private HorizontalPanel mainPanel_;
	
	public SCurvePanel()
	{
		mainPanel_=new HorizontalPanel();
		mainPanel_.add(new Label("Coming soon..."));
		
	}
	
	public Widget panel()
	{
		return mainPanel_;
	}

	public String name()
	{
		return "S-Curve Run";
	}

}
