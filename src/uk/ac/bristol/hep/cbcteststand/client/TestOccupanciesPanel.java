package uk.ac.bristol.hep.cbcteststand.client;

//A class that displays the Occupancy Grids.
//Currently polls the server TODO server push

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;

import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService;
import uk.ac.bristol.hep.cbcteststand.client.utilities.InitJSON;
import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService.cbcNamesResponse;
import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService.rpcMessage;
import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService.rpcResponse;
import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService.getOccupanciesResponse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
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
	private dataRunManager dataRun = new dataRunManager();
	
	private Button launchButton = new Button("Launch");
	private Button update = new Button("Update");
	
	private Map<String, Grid> resultsGrid = new HashMap<String, Grid>();
	
	
	private InitJSON initGSON = new InitJSON();
	
	private GlibControlService service_;
	private PopupPanel loading = new PopupPanel(false,true);
	
	public List<Map<String, Map<String, Integer>>> rpcContainer = new ArrayList<Map<String, Map<String, Integer>>>(); //List containing the RPC values
	public Map<String, Map<String, Integer>> cbcNames = new HashMap<String, Map<String, Integer>>();
	public Map<String, Integer> registerValues = new HashMap<String, Integer>();
	
	public Label echo = new Label("Initiating...");
	
	private OccupancyCheckView occupancyCheckView = new OccupancyCheckView();
	
	private GlibControlService dataRunManagerService_ = GWT.create(GlibControlService.class); //separate instance for the dataRun
	
	private int REFRESH_INTERVAL = 5000;
	private boolean pollFlag;
	
	public Widget panel()
	{
		return mainPanel_;
	}
	
	public TestOccupanciesPanel()
	{
		//This part is for the separate instance of the data taking service
		if( !GWT.isProdMode() && GWT.isClient() )
		{
			Resource resource = new Resource("http://localhost:3900/cbcTestStand/services/GlibControlProxy.py");
			((RestServiceProxy)dataRunManagerService_).setResource(resource);
		}
		else
		{
			Resource resource = new Resource(GWT.getModuleBaseURL()+"services/GlibControlProxy.py");
			((RestServiceProxy)dataRunManagerService_).setResource(resource);
		}
		
		loading.setStyleName("loadingBetween");
		Label loadingLabel = new Label("Please Wait");
		loading.add(loadingLabel);
		loading.setGlassEnabled(true); 
		loading.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
		loading.center();
		
		service_ = initGSON.initGSON();	
		
		mainPanel_ = new VerticalPanel();
		
		launchButton.addClickHandler(launchHandler);
		update.addClickHandler(launchHandler);
		mainPanel_.add(launchButton);
		mainPanel_.add(update);
		mainPanel_.add(echo);
		
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
				
				loading.hide();
				
				for( String cbcName : response.getResult() ) {

					Label gridTitle = new Label(cbcName);
					gridTitle.setStyleName("gridTitle");
					mainPanel_.add(gridTitle);
					
					occupancyCheckView.createResultGrid(cbcName);
					occupancyCheckView.clearResults();
					resultsGrid.put(cbcName, occupancyCheckView.getOccupancyGrid());
					mainPanel_.add(resultsGrid.get(cbcName));
					echo.setText("No Data");
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
	
	public class dataRunManager{
		
		public void startOccupancyCheck(){
			
			//occupancyGrid.clearResults();
			rpcContainer.clear();
			cbcNames.clear();
			registerValues.clear();
			registerValues.put(null, null); //sends an empty request
			rpcContainer.add(cbcNames);
			
			rpcMessage myparams = new rpcMessage();
			myparams.setMethod("startOccupancyCheck"); 
			myparams.setParams(rpcContainer); 
			dataRunManagerService_.rpcService(myparams, new MethodCallback<rpcResponse>(){
				
				@Override
				public void onFailure(Method method, Throwable exception) {
					echo.setText("Error: " + exception.getMessage());
				}

				@Override
				public void onSuccess(Method method, rpcResponse response) {
					//TODO clear results
					echo.setText("Request Sent");
					launchButton.setEnabled(false);
					
					if (response.getError()!=null){
					echo.setText("Currently Taking Data");
					}
					
				}
				
			});
		}
			
			public void getOccupancies(){
				
				cbcNames.clear();
				registerValues.clear();
				rpcContainer.clear();
				registerValues.put(null, null); //sends an empty request
				rpcContainer.add(cbcNames);
				
				rpcMessage myparams = new rpcMessage();
				myparams.setMethod("getOccupancies"); 
				myparams.setParams(rpcContainer); 
				service_.getOccupanciesService(myparams, new MethodCallback<getOccupanciesResponse>(){

					@Override
					public void onFailure(Method method, Throwable exception) {
						echo.setText("error " + exception.getMessage());
						
					}

					@Override
					public void onSuccess(Method method, getOccupanciesResponse response) {

							if (response.getResult().containsValue(null)){
								launchButton.setEnabled(false);
								echo.setText("Taking data...");
							
								}
							
								else{
									
									for (String cbcName : response.getResult().keySet()){
										pollFlag=false;
										update.setEnabled(true);
										occupancyCheckView.addResult(resultsGrid.get(cbcName), response.getResult().get(cbcName));
										launchButton.setEnabled(true);
										echo.setText("Results refreshed");
									}
									
								}
					}
					
				});
				
			}			
		}
	
	private ClickHandler launchHandler = new ClickHandler() {
	@Override
	public void onClick(ClickEvent event) {
	    Widget sender = (Widget) event.getSource();
	    

	    if (sender == launchButton) {
	    	echo.setText("Starting data run");
	    	
	    	dataRun.startOccupancyCheck();
	    	launchButton.setEnabled(false);
	    	
	    }
	    else if(sender == update ){
	    	pollFlag=true;
	    	update.setEnabled(false);
	    	dataRun.getOccupancies();
	    	serverPoll();
	    }
	    
	  }
	};
	
	public void serverPoll(){
		Timer refreshTimer = new Timer() {
    		public void run()
    		{
    			dataRun.getOccupancies();
    			
    			if (pollFlag==false){
    				cancel();
    			}
    		}
    	};
    	
    	refreshTimer.scheduleRepeating(REFRESH_INTERVAL);
	}

}
