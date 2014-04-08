package uk.ac.bristol.hep.cbcteststand.client;

import java.util.HashMap;
import java.util.Map;

import uk.ac.bristol.hep.cbcteststand.client.utilities.LoadPanels;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class ProgramEntryPoint implements EntryPoint
{
	//private VerticalPanel selectionPanel_=new VerticalPanel();
	private Map<Label,TestStandPanel> panels_=new HashMap<Label,TestStandPanel>();
	private DockPanel pagePanel_=new DockPanel();
	public PopupPanel loadingPopup = new PopupPanel(false, true);
	LoadPanels loadPanels = new LoadPanels();
	//private VerticalPanel selectionPanel_;
	
	/** @brief The main entry point for the program.
	 */
	@Override
	public void onModuleLoad()
	{
		
		
		loadPanels.addPanel( "I2C Registers", I2CRegistersPanel.class );
		loadPanels.addPanel( "Test Occupancies", TestOccupanciesPanel.class);
		loadPanels.addPanel( "SCurve Panel", SCurvePanel.class );
		
		pagePanel_ = (DockPanel) loadPanels.getPagePanel();
		
		
		panels_ = (Map<Label, TestStandPanel>) loadPanels.getPanels();
		
		loadPanels.setMainPanel( panels_.keySet().iterator().next() );
		RootPanel.get("Loading").setVisible(false);
		
		
        RootPanel.get().add(pagePanel_);	

	}
	
	


}