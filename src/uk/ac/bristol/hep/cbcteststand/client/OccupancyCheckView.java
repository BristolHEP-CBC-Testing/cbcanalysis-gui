package uk.ac.bristol.hep.cbcteststand.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class OccupancyCheckView{ 
	
	private Grid occupancyGrid = new Grid(17,17);
	private Map<String, Label> resultLabels = new HashMap<String, Label>();
	private Map<String, Grid> resultGrids = new HashMap<String, Grid>();  
	
	private Map<String, Map<String, Integer>> OuterTest = new HashMap<String, Map<String, Integer>>();
	private Map<String, Integer> InnerTest = new HashMap<String, Integer>();

	
	public OccupancyCheckView(String titleLabel){
		
		InnerTest.put("FrontEndControl", 1);
		InnerTest.put("VCth", 2);
		OuterTest.put("FE0CBC0", InnerTest);
		OuterTest.put("FE0CBC1", InnerTest);
		
		createResultGrid(titleLabel);
		clearResults();
		
		for (String outerkey : OuterTest.keySet()){
				Map<String, Integer> test = OuterTest.get(outerkey);
				addResult(outerkey, test);	
		}
	}
	
	private void createResultGrid(String gridName){
		Label title = new Label();
		title.setText(gridName); 
		title.setStyleName("gridTitle"); //style this
		
		for (int index=1;  index < occupancyGrid.getColumnCount(); index++){
			occupancyGrid.setWidget(0, index, new HTML("Cx"+Integer.toHexString(index-1)));
		}
		
		for (int index=1;  index < occupancyGrid.getRowCount(); index++){
			occupancyGrid.setWidget(index, 0, new HTML("Cx"+Integer.toHexString(index-1)));
		}
		
		resultLabels.put(gridName, title);
		resultGrids.put(gridName, occupancyGrid);
	}
	
	private void clearResults(){
		for (String name : resultGrids.keySet()){
			Grid grid = resultGrids.get(name);
			
			for (int column=1;  column < grid.getColumnCount(); column++){
			
				for (int row=1;  row < grid.getRowCount(); row++){
					
					HTML test = new HTML("x");
					grid.setWidget(row, column, test);
					grid.setStyleName("gridStyle");
				}	
			}
		}	
	}
	
	private void addResult(String cbcName, Map<String, Integer> occupancies){ //.values().toArray()
		//need error message here
		if (occupancies == null) addError(cbcName);
		
		try{
			//occupancyGrid = resultGrids.get(cbcName);
		}
		catch (Throwable NameError){
			//createResultGrid(cbcName);
			//occupancyGrid = resultGrids.get(cbcName);
		}
			
		for (String registerValues : occupancies.keySet()){
			
			Integer ovalue = InnerTest.get(registerValues);
			
			
			int red = (int) (255.0*(1-ovalue));
			int green = (int) (255.0*(1-ovalue));
			int blue = 0;
			
			int row = 1;
			int column = 1;
			
			occupancyGrid.setWidget(row, column, new HTML(ovalue.toString()));
			occupancyGrid.getCellFormatter().setStyleName(row, column, "tableCell-green");
			//Window.alert( row.toString());
			column+= 1;
			if (column%17 == 0){
				column=1;
				row+=1;
			}
		}
		
	}
	
	private void addError (String cbcName){ //TODO need to work on this
		
		try{
			resultLabels.get(cbcName).setText("Server Error");;
		}
		catch (Throwable NameError) { //need to do more error checking here
			createResultGrid(cbcName);
			resultLabels.get(cbcName).setText(NameError.toString());;
		}
		
	}

	public Grid getOccupancyGrid() {
		
		return occupancyGrid;
	}
	
	public Map<String, Label> getGridResultLabel() {
		return resultLabels;
	}


	public Map<String, Grid> getResultGrids() {
		return resultGrids;
	}	
	
}
