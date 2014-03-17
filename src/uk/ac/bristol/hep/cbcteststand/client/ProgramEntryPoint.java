package uk.ac.bristol.hep.cbcteststand.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtjsonrpc.common.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class ProgramEntryPoint implements EntryPoint, ClickHandler
{
	private GlibControlService service_ = GWT.create(GlibControlService.class);
	private Label echo_ = new Label();
	private VerticalPanel selectionPanel_=new VerticalPanel();
	private Map<Label,TestStandPanel> panels_=new HashMap<Label,TestStandPanel>();
	private Map<Label,java.lang.Class<?>> panelTypes_=new HashMap<Label,java.lang.Class<?>>();
	private Label currentlyActivePanelLabel_=null;
	private DockPanel pagePanel_=new DockPanel();
	
	@Override
	public void onModuleLoad()
	{
		RootPanel.get("LoadingPlaceholder").setVisible(false);
		
		echo_.setText("Initialising...");
		
		if( !GWT.isProdMode() && GWT.isClient() )
		{
			// If GWT is running in development mode then the server side components can't
			// run (they're python). The RPC service needs to point to an actual running
			// RPC service connected to the hardware. Assume whoever is testing has forwarded
			// local port 3900 to the remote machine with the hardware connected and RPC
			// running.
			((ServiceDefTarget) service_).setServiceEntryPoint("http://localhost:3900/cbcTestStand/services/GlibControlProxy.py");
		}
		else
		{
			// Otherwise this is production code, so point to the RPC service running on
			// this host.
			((ServiceDefTarget) service_).setServiceEntryPoint( GWT.getModuleBaseURL()+"services/GlibControlProxy.py" );
		}

		service_.connectedCBCNames( new String[0], new AsyncCallback<String[]>() {
			public void onSuccess( String[] result)
			{
				String message="Result is ";
				for( String cbcName : result ) message+=cbcName+", ";
				echo_.setText(message);
			}
			public void onFailure(Throwable why)
			{
				echo_.setText("Failed due to "+why.getMessage());
			}
		});

		// Create a button widget
		final Button button = new Button();
		button.setText("Click me!");
		button.addClickHandler(
			new ClickHandler()
			{
				@Override
				public void onClick(ClickEvent event)
				{
					button.setText("Hello, world! ("+GWT.getModuleBaseURL() + ")");
				}
			});
		RootPanel.get().add(button);
		RootPanel.get().add(echo_);
		Label test=addPanel( I2CRegistersPanel.class );
		setMainPanel(test);
	}
	
	private Label addPanel( java.lang.Class<?> panelClass )
	{
		Label titleLabel=new Label( "Test - panel name placeholder" );
		titleLabel.setStyleName("areaStyle");
		titleLabel.addClickHandler(this);
		selectionPanel_.add( titleLabel );
		panels_.put( titleLabel, null );
		panelTypes_.put( titleLabel, panelClass );
		return titleLabel;
	}

	@Override
	public void onClick(ClickEvent event)
	{
		// TODO Auto-generated method stub
		
	}
	
	private void getOrLoadPanel( final Label panelButton, final AsyncCallback<TestStandPanel> result )
	{
		TestStandPanel newPanel=panels_.get(panelButton);
		if( newPanel!=null ) result.onSuccess(newPanel);
		else
		{
			GWT.runAsync( new RunAsyncCallback()
			{
				public void onFailure(Throwable err)
				{
					result.onFailure(err);
				}

				public void onSuccess()
				{
					TestStandPanel instance=GWT.create( panelTypes_.get(panelButton) );
					if( instance==null ) result.onFailure( new java.lang.NullPointerException() );
					else
					{
						panels_.put( panelButton, instance );
						result.onSuccess(instance);
					}
				}
			});
		}
	}
	
	private void setMainPanel( final Label panelButton )
	{
		if( currentlyActivePanelLabel_==panelButton ) return; // Already the active panel
		
		if( currentlyActivePanelLabel_!=null )
		{
			// Take off the "selectedAreaStyle" from the old button
			currentlyActivePanelLabel_.setStyleName("areaStyle");
			// Clear the old panel off the main display
			pagePanel_.remove( panels_.get(currentlyActivePanelLabel_).panel() );
		}
		
		getOrLoadPanel( panelButton, new AsyncCallback<TestStandPanel>()
			{
				public void onFailure(Throwable err)
				{
					//throw err;
				}

				public void onSuccess( TestStandPanel newPanel )
				{
					newPanel.panel().setStyleName("selectedAreaStyle");
					pagePanel_.add( newPanel.panel(), DockPanel.CENTER );
					currentlyActivePanelLabel_=panelButton;
					currentlyActivePanelLabel_.setStyleName("selectedAreaStyle");
				}
			} );
		
	}

}