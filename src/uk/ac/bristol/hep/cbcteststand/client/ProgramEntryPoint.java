package uk.ac.bristol.hep.cbcteststand.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwtjsonrpc.common.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class ProgramEntryPoint implements EntryPoint
{
	private GlibControlService service_ = GWT.create(GlibControlService.class);
	private VerticalPanel selectionPanel_=new VerticalPanel();
	private Map<Label,TestStandPanel> panels_=new HashMap<Label,TestStandPanel>();
	private Map<Label,java.lang.Class<?>> panelTypes_=new HashMap<Label,java.lang.Class<?>>();
	private Label currentlyActivePanelLabel_=null;
	private DockPanel pagePanel_=new DockPanel();
	
	/** @brief The main entry point for the program.
	 */
	@Override
	public void onModuleLoad()
	{
		final HTML mainTitle=new HTML("CBC Test Stand");
		mainTitle.setStyleName("titleStyle");
		pagePanel_.add( new HTML("CBC Test Stand"), DockPanel.NORTH );
		
		addPanel( "I2C Registers", I2CRegistersPanel.class );
		pagePanel_.add( selectionPanel_, DockPanel.WEST );
		
		setMainPanel( panels_.keySet().iterator().next() );
		RootPanel.get("LoadingPlaceholder").setVisible(false);
		RootPanel.get().add(pagePanel_);
	}
	
	/** @brief Adds a new panel of the given class to the list of available panels.
	 * 
	 * Adds a button (actually just text but it's clickable) to the left hand column, with
	 * the given text. When this is clicked a panel of the given class is displayed in the
	 * central area.
	 *
	 * @param labelText   The text that will be displayed in the list of panels
	 * @param panelClass  The class of the TestStandPanel concrete class to create
	 */
	private void addPanel( String labelText, java.lang.Class<?> panelClass )
	{
		Label titleLabel=new Label( labelText );
		titleLabel.setStyleName("areaStyle");
		titleLabel.addClickHandler( new ClickHandler()
			{
				public void onClick( ClickEvent event )
				{
					Label sender=(Label) event.getSource();
					setMainPanel(sender);
				}
			});
		selectionPanel_.add( titleLabel );
		panels_.put( titleLabel, null );
		panelTypes_.put( titleLabel, panelClass );
	}

	/** @brief Asynchronously returns the TestStandPanel instance attached to the given Label.
	 * 
	 * Code splitting has been used so that the code for the panels is only loaded when they're
	 * first needed. This should reduce start up time. If the panel has not been used yet then
	 * a call to get the code from the server needs to be made, hence why an AsyncCallback is
	 * required.
	 *
	 * If the panel has already been loaded then the existing code is used and the return value
	 * is given almost immediately, but still using the callback.
	 *
	 * See http://www.gwtproject.org/doc/latest/DevGuideCodeSplitting.html for information on
	 * code splitting.
	 *
	 * @param panelButton   The Label that was created by the addPanel method for this panel.
	 * @param result        The callback used to return the result.
	 */
	private void getOrLoadPanel( final Label panelButton, final AsyncCallback<TestStandPanel> result )
	{
		TestStandPanel newPanel=panels_.get(panelButton);
		// If the code for the panel has already been loaded reuse that
		if( newPanel!=null ) result.onSuccess(newPanel);
		else
		{
			// Code has not been loaded yet create a request to get the code from the
			// server.
			GWT.runAsync( new RunAsyncCallback()
			{
				public void onFailure(Throwable err)
				{
					result.onFailure(err);
				}

				public void onSuccess()
				{
					TestStandPanel instance=GWT.create( panelTypes_.get(panelButton) );
					if( instance==null ) result.onFailure( new java.lang.NullPointerException( "Couldn't get the code for the panel "+panelButton.getText() ) );
					else
					{
						// Store the result so that I can reuse it for future calls
						panels_.put( panelButton, instance );
						// And return the result to the caller
						result.onSuccess(instance);
					}
				}
			});
		}
	}
	
	/** @brief Removes the previously active panel from the display, and adds the new one.
	 * 
	 * @param panelButton  The Label created by addPanel() for the new panel.
	 */
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
		
		// The object instance for the panel might not have been created yet, so use this method
		// to check and take the necassary steps.
		getOrLoadPanel( panelButton, new AsyncCallback<TestStandPanel>()
			{
				public void onFailure(Throwable err)
				{
					// TODO - add some kind of error handling
					pagePanel_.add( new Label("An error occured while loading the panel: "+err.getMessage()), DockPanel.CENTER );
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