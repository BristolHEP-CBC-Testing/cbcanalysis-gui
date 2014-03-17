package uk.ac.bristol.hep.cbcteststand.client;

import com.google.gwt.user.client.ui.Widget;

/** @brief Interface for all the test stand panels.
 * 
 * Anything that implements this interface will appear in the list of
 * available panels on the left, and will be displayed when the name
 * is clicked on.
 * 
 * @author Mark Grimes (mark.grimes@bristol.ac.uk)
 * @date 19/Jan/2013
 */
public interface TestStandPanel
{
	/** @brief Returns the main panel, which already encompasses all of the UI elements. */
	public Widget panel();
	
	/** @brief The name of the panel, as it will appear in the list on the main page. */
	public String name();
}
