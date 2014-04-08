package uk.ac.bristol.hep.cbcteststand.client.utilities;

import java.util.HashMap;
import java.util.Map;

import uk.ac.bristol.hep.cbcteststand.client.I2CRegistersPanel;
import uk.ac.bristol.hep.cbcteststand.client.SCurvePanel;
import uk.ac.bristol.hep.cbcteststand.client.TestOccupanciesPanel;
import uk.ac.bristol.hep.cbcteststand.client.TestStandPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtjsonrpc.common.AsyncCallback;

public class LoadPanels {
	
	private VerticalPanel selectionPanel_=new VerticalPanel();
	private Map<Label,TestStandPanel> panels_=new HashMap<Label,TestStandPanel>();
	private Map<Label,java.lang.Class<?>> panelTypes_=new HashMap<Label,java.lang.Class<?>>();
	private Label currentlyActivePanelLabel_=null;
	private DockPanel pagePanel_=new DockPanel();
	public PopupPanel loadingPopup = new PopupPanel(false, true);
	
	
	public Widget getSelectionPanel(){
		return selectionPanel_;
	}
	
	public Widget getPagePanel(){
		final HTML mainTitle=new HTML("CBC Test Stand v0.1");
		mainTitle.setStyleName("titleStyle");
		pagePanel_.add( mainTitle, DockPanel.NORTH );
		pagePanel_.add( selectionPanel_, DockPanel.WEST );
		return pagePanel_;
	}
	
	public Map<Label,TestStandPanel> getPanels(){
		return panels_;
	}
	
	public void addPanel( final String labelText, final java.lang.Class<?> panelClass )
	{
		Label titleLabel=new Label( labelText );
		titleLabel.setStyleName("areaStyle");
		titleLabel.getElement().getStyle().setCursor(Cursor.POINTER);
		
		titleLabel.addClickHandler( new ClickHandler()
			{
				public void onClick( ClickEvent event )
				{
					//loadingPopup.center(); //TODO
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
					// This line works fine in development mode but won't compile to javascript.
					// Apparently you can't have reflection in the client code
					//TestStandPanel instance=GWT.create( panelTypes_.get(panelButton) );
					// As a temporary measure I'll use this horrible hard coded version
					TestStandPanel instance;
					if( panelTypes_.get(panelButton)==I2CRegistersPanel.class ) instance=GWT.create( I2CRegistersPanel.class );
					else if( panelTypes_.get(panelButton)==TestOccupanciesPanel.class ) instance=GWT.create( TestOccupanciesPanel.class);
					else if( panelTypes_.get(panelButton)==SCurvePanel.class ) instance=GWT.create( SCurvePanel.class);
					else throw new java.lang.RuntimeException( "You need to add an 'if' clause to ProgramEntryPoint::getOrLoadPanel for class "+panelTypes_.get(panelButton).getName() );
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
		//loadingPopup.hide();
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

	
	/** @brief Removes the previously active panel from the display, and adds the new one.
	 * 
	 * @param panelButton  The Label created by addPanel() for the new panel.
	 */
	
	public void setMainPanel( final Label panelButton )
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
					//loadingPopup.center();
				}
			} );
		
	}

}
