package uk.ac.bristol.hep.cbcteststand.client;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;

import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService;
import uk.ac.bristol.hep.cbcteststand.client.utilities.InitJSON;
import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService.cbcNamesResponse;
import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService.rpcMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



public class TestOccupanciesPanel implements TestStandPanel
{
	private VerticalPanel mainPanel_;
	
	private Button launchButton = new Button("Launch");
	
	private Map<String, Grid> resultsGrid = new HashMap<String, Grid>();
	
	private Map<String, Label> resultLabels;
	
	private Map<String, Map<String, Integer>> OuterTest = new HashMap<String, Map<String, Integer>>();
	private Map<String, Integer> InnerTest = new HashMap<String, Integer>();
	
	private InitJSON initGSON = new InitJSON();
	private GlibControlService service_;
	private  PopupPanel loading = new PopupPanel(false,true);
	
	public List<Map<String, Map<String, Integer>>> rpcContainer = new ArrayList<Map<String, Map<String, Integer>>>(); //List containing the RPC values
	public Map<String, Map<String, Integer>> cbcNames = new HashMap<String, Map<String, Integer>>();
	public Map<String, Integer> registerValues = new HashMap<String, Integer>();
	
	public Widget panel()
	{
		return mainPanel_;
	}
	
	public TestOccupanciesPanel()
	{
		loading.setStyleName("loadingBetween");
		Label loadingLabel = new Label("Please Wait");
		loading.add(loadingLabel);
		loading.setGlassEnabled(true); 
		loading.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		loading.center();
		
		service_ = initGSON.initGSON();	
		
		mainPanel_=new VerticalPanel();
		
		launchButton.addClickHandler(launchHandler);
		
		mainPanel_.add(launchButton);
			
		InnerTest.put("FrontEndControl", 1);
		InnerTest.put("VCth", 2);
		OuterTest.put("FE0CBC0", InnerTest);
		OuterTest.put("FE0CBC1", InnerTest);
		
		registerValues.put(null, null);
		cbcNames.put(null, null);
		rpcContainer.add(cbcNames); //sends an empty request
		
		rpcMessage myparams = new rpcMessage();
		myparams.setMethod("connectedCBCNames");
		myparams.setParams(rpcContainer); //send an empty request, returns connected names
		
		service_.connectedCBCService(myparams, new MethodCallback<cbcNamesResponse>()  {

			
			@Override
			public void onSuccess(Method method, cbcNamesResponse response) 
			{	
				
				for( String cbcName : response.getResult() ) {
					OccupancyCheckView occupancyGrid= new OccupancyCheckView(cbcName);
					Grid myGrid = occupancyGrid.getOccupancyGrid();
					Label gridTitle = new Label(cbcName);
					gridTitle.setStyleName("gridTitle");
					mainPanel_.add(gridTitle);
					mainPanel_.add(myGrid);
					loading.hide(); 
				}

			}
			
			@Override
			public void onFailure(Method method, Throwable exception)
			{
				Label gridTitle = new Label(exception.getMessage());
				gridTitle.setStyleName("gridTitle");
				mainPanel_.add(gridTitle);
				loading.hide(); 
			}


		});
		
	}
	
	private ClickHandler launchHandler = new ClickHandler() {
	@Override
	public void onClick(ClickEvent event) {
	    Widget sender = (Widget) event.getSource();

	    if (sender == launchButton) {
	    	launchButton.setEnabled(false);	     	
	    	
	    }
	    
	  }
	};

}
