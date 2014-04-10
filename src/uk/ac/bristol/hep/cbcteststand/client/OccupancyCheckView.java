package uk.ac.bristol.hep.cbcteststand.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class OccupancyCheckView{ 
	
	private Grid occupancyGrid;
	private Map<String, Label> resultLabels = new HashMap<String, Label>();
	private Map<String, Grid> resultGrids = new HashMap<String, Grid>();  
	
	public void createResultGrid(String gridName){
		
		occupancyGrid = new Grid(17,17);
		Label title = new Label();
		
		title.setText(gridName); 
		title.setStyleName("gridTitle"); //TODO style this
		
		for (int index=1;  index < occupancyGrid.getColumnCount(); index++){
			occupancyGrid.setWidget(0, index, new HTML("Cx"+Integer.toHexString(index-1)));
		}
		
		for (int index=1;  index < occupancyGrid.getRowCount(); index++){
			occupancyGrid.setWidget(index, 0, new HTML("C"+Integer.toHexString(index-1)+"x"));
		}
		
		resultLabels.put(gridName, title);
		resultGrids.put(gridName, occupancyGrid);
	}
	
	
	public void clearResults(){
		for (String name : resultGrids.keySet()){
			Grid clearGrid = resultGrids.get(name);
			
			for (int column=1;  column < clearGrid.getColumnCount(); column++){
			
				for (int row=1;  row < clearGrid.getRowCount(); row++){
					HTML test = new HTML("x");
					clearGrid.setWidget(row, column, test);
					clearGrid.setStyleName("gridStyle"); 
				}	
			}
		}	
	}
	
	public void addResult(Grid occupanyGrid_, List<Integer> occupancies){ 
		
		int row = 1;
		int column = 1;
			
		for (int index = 0; index<occupancies.size(); index++){
			
			occupanyGrid_.setWidget(row, column, new HTML(String.valueOf(occupancies.get(index))));
			
			if (occupancies.get(index)==0){occupanyGrid_.getCellFormatter().setStyleName(row, column, "tableCell-green");} //TODO
			
			else {occupanyGrid_.getCellFormatter().setStyleName(row, column, "tableCell-red");}
			
			column+= 1;
			if (column%17 == 0){
				column=1;
				row+=1;
			}
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
	
	public Grid getSpecificGrid(String name){
		return resultGrids.get(name);
	}
	
}
