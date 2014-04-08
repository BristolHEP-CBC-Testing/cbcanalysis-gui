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
import uk.ac.bristol.hep.cbcteststand.client.utilities.GlibControlService.rpcResponse;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class I2CRegistersPanel implements TestStandPanel
{
	private HorizontalPanel mainPanel_;
	private ListBox cbcList_;
	private DisclosurePanel mainSettings_;
	private DisclosurePanel channelMasks_;
	private DisclosurePanel channelTrims_;
	private GlibControlService service_;
	private InitJSON initGSON = new InitJSON();
	
	private Label echo_ = new Label();
	
	private Map<String, TextBox> i2cValueEntries = new HashMap<String, TextBox>();
	
	public List<Map<String, Map<String, Integer>>> rpcContainer = new ArrayList<Map<String, Map<String, Integer>>>(); //List containing the RPC values
	public Map<String, Map<String, Integer>> cbcNames = new HashMap<String, Map<String, Integer>>();
	public Map<String, Integer> registerValues = new HashMap<String, Integer>();
	
	
	public Widget panel()
	{
		return mainPanel_;
	}
	
	
	public I2CRegistersPanel()
	{
				
		service_ = initGSON.initGSON();	

		mainPanel_=new HorizontalPanel();
		
		cbcList_=new ListBox(true);//TODO enables multiple select
		
		cbcList_.addItem( "Waiting..." );
		cbcList_.setEnabled( false );
		cbcList_.addChangeHandler(new ChangeHandler() {
		      public void onChange(ChangeEvent event) {
		    	  getI2CValues();
		      }
		    });
		
		cbcList_.setVisibleItemCount(0);
		mainPanel_.add( cbcList_ );
		mainPanel_.add(echo_);
		
		registerValues.put(null, null);
		cbcNames.put(null, null);
		rpcContainer.add(cbcNames); //sends an empty request
		
		rpcMessage myparams = new rpcMessage();
		myparams.setMethod("connectedCBCNames");
		myparams.setParams(rpcContainer); //send an empty request, returns connected names
		
		service_.connectedCBCService(myparams, new MethodCallback<cbcNamesResponse>(){

			 @Override
			public void onFailure(Method method, Throwable exception) {
				// TODO Auto-generated method stub
				Window.alert(exception.getMessage().toString());
				
			}

			@Override
			public void onSuccess(Method method, cbcNamesResponse response) {
				
				cbcList_.removeItem(0);
				for( final String cbcName : response.getResult() ) {
					cbcList_.addItem(cbcName);
				}
				cbcList_.setEnabled( true );
				cbcList_.setVisibleItemCount(response.getResult().size());
				getI2CValues();

			}
		});
		
		mainSettings_ = new DisclosurePanel("Main control registers");
		channelMasks_ = new DisclosurePanel("Channel masks");
		channelTrims_ = new DisclosurePanel("Channel trims");
		HorizontalPanel verticalPanelForI2C = new HorizontalPanel();
		verticalPanelForI2C.add(mainSettings_);
		verticalPanelForI2C.add(channelMasks_);
		verticalPanelForI2C.add(channelTrims_);
		mainPanel_.add( verticalPanelForI2C );

		mainSettings_.add( addRegisters( new String[]{"FrontEndControl","TriggerLatency","HitDetectSLVS","Ipre1","Ipre2","Ipsf","Ipa","Ipaos","Vpafb","Icomp","Vpc","Vplus","VCth","TestPulsePot","SelTestPulseDel&ChanGroup","MiscTestPulseCtrl&AnalogMux","TestPulseChargePumpCurrent","TestPulseChargeMirrCascodeVolt","CwdWindow&Coincid","MiscStubLogic"} ) );
		channelMasks_.add( addRegisters( new String[]{"MaskChannelFrom008downto001","MaskChannelFrom016downto009","MaskChannelFrom024downto017","MaskChannelFrom032downto025","MaskChannelFrom040downto033","MaskChannelFrom048downto041","MaskChannelFrom056downto049","MaskChannelFrom064downto057","MaskChannelFrom072downto065","MaskChannelFrom080downto073","MaskChannelFrom088downto081","MaskChannelFrom096downto089","MaskChannelFrom104downto097","MaskChannelFrom112downto105","MaskChannelFrom120downto113","MaskChannelFrom128downto121","MaskChannelFrom136downto129","MaskChannelFrom144downto137","MaskChannelFrom152downto145","MaskChannelFrom160downto153","MaskChannelFrom168downto161","MaskChannelFrom176downto169","MaskChannelFrom184downto177","MaskChannelFrom192downto185","MaskChannelFrom200downto193","MaskChannelFrom208downto201","MaskChannelFrom216downto209","MaskChannelFrom224downto217","MaskChannelFrom232downto225","MaskChannelFrom240downto233","MaskChannelFrom248downto241","MaskChannelFrom254downto249"} ) );
		channelTrims_.add( addRegisters( new String[]{"Channel001","Channel002","Channel003","Channel004","Channel005","Channel006","Channel007","Channel008","Channel009","Channel010","Channel011","Channel012","Channel013","Channel014","Channel015","Channel016","Channel017","Channel018","Channel019","Channel020","Channel021","Channel022","Channel023","Channel024","Channel025","Channel026","Channel027","Channel028","Channel029","Channel030","Channel031","Channel032","Channel033","Channel034","Channel035","Channel036","Channel037","Channel038","Channel039","Channel040","Channel041","Channel042","Channel043","Channel044","Channel045","Channel046","Channel047","Channel048","Channel049","Channel050","Channel051","Channel052","Channel053","Channel054","Channel055","Channel056","Channel057","Channel058","Channel059","Channel060","Channel061","Channel062","Channel063","Channel064","Channel065","Channel066","Channel067","Channel068","Channel069","Channel070","Channel071","Channel072","Channel073","Channel074","Channel075","Channel076","Channel077","Channel078","Channel079","Channel080","Channel081","Channel082","Channel083","Channel084","Channel085","Channel086","Channel087","Channel088","Channel089","Channel090","Channel091","Channel092","Channel093","Channel094","Channel095","Channel096","Channel097","Channel098","Channel099","Channel100","Channel101","Channel102","Channel103","Channel104","Channel105","Channel106","Channel107","Channel108","Channel109","Channel110","Channel111","Channel112","Channel113","Channel114","Channel115","Channel116","Channel117","Channel118","Channel119","Channel120","Channel121","Channel122","Channel123","Channel124","Channel125","Channel126","Channel127","Channel128","Channel129","Channel130","Channel131","Channel132","Channel133","Channel134","Channel135","Channel136","Channel137","Channel138","Channel139","Channel140","Channel141","Channel142","Channel143","Channel144","Channel145","Channel146","Channel147","Channel148","Channel149","Channel150","Channel151","Channel152","Channel153","Channel154","Channel155","Channel156","Channel157","Channel158","Channel159","Channel160","Channel161","Channel162","Channel163","Channel164","Channel165","Channel166","Channel167","Channel168","Channel169","Channel170","Channel171","Channel172","Channel173","Channel174","Channel175","Channel176","Channel177","Channel178","Channel179","Channel180","Channel181","Channel182","Channel183","Channel184","Channel185","Channel186","Channel187","Channel188","Channel189","Channel190","Channel191","Channel192","Channel193","Channel194","Channel195","Channel196","Channel197","Channel198","Channel199","Channel200","Channel201","Channel202","Channel203","Channel204","Channel205","Channel206","Channel207","Channel208","Channel209","Channel210","Channel211","Channel212","Channel213","Channel214","Channel215","Channel216","Channel217","Channel218","Channel219","Channel220","Channel221","Channel222","Channel223","Channel224","Channel225","Channel226","Channel227","Channel228","Channel229","Channel230","Channel231","Channel232","Channel233","Channel234","Channel235","Channel236","Channel237","Channel238","Channel239","Channel240","Channel241","Channel242","Channel243","Channel244","Channel245","Channel246","Channel247","Channel248","Channel249","Channel250","Channel251","Channel252","Channel253","Channel254","ChannelDummy"} ) );
	}
	
	private Widget addRegisters( String[] registerNames )
	{
		FlowPanel flowPanel=new FlowPanel();
		Label[] labels=new Label[registerNames.length]; // Keep a note so that I can set their lengths later
		for( int index=0; index<registerNames.length; ++index )
		{
			String buttonName=registerNames[index];
			HorizontalPanel newPanel=new HorizontalPanel();
			labels[index]=new Label(buttonName);
			newPanel.add( labels[index] );
			final TextBox newTextBox=new TextBox();
			newTextBox.setWidth("30");
			newTextBox.setText("Select chip...");
			newTextBox.setEnabled(false);
			newTextBox.setTitle(buttonName);
			newTextBox.addChangeHandler(new ChangeHandler() {
			      public void onChange(ChangeEvent event) {
			    	  String registerName = new String(newTextBox.getTitle());
			    	  String registerValue = new String(newTextBox.getText());
			    	  
			    	  int _registerValue;
			    	  
			    	  if (registerValue.startsWith("0x")){ //removes 0x if it's there
			    		  registerValue = registerValue.substring(2);
			    	  }
			    	  
			    	  _registerValue = Integer.parseInt(registerValue, 16);
			    	  
			    	  if (_registerValue>256){
			    		 _registerValue = Integer.parseInt("ff", 16);
			    	  }
			    	  Map<String, Integer> activeRegister = new HashMap<String, Integer>();
			    	  activeRegister.put(registerName, _registerValue);
			    	  
			    	  setI2CRegisters(activeRegister);
			      }
			    });
			newPanel.add(newTextBox);
			flowPanel.add(newPanel);
			
			i2cValueEntries.put(buttonName, newTextBox);
		}
		
		// Resize them so that they're all the same size
		int maxSize=0;
		for( int index=0; index<labels.length; ++index )
		{
			if( labels[index].getText().length()*8 > maxSize ) maxSize=labels[index].getText().length()*8;
		}
		for( int index=0; index<labels.length; ++index )
		{
			labels[index].setWidth(Integer.toString(maxSize)+"px");
		}
		
		return flowPanel;
	}
	
	private List<String> getActiveCBCs(){
		
		List<String> _cbcNames = new ArrayList<String>();
		
		for (int i=0 ;  i < cbcList_.getItemCount(); i++){
			if (cbcList_.isItemSelected(i)){
				_cbcNames.add(cbcList_.getItemText(i));
			}
		}
		return _cbcNames;
	}
	
	public void setI2CRegisters(Map<String, Integer> activeRegister){
		
		rpcContainer.clear();
		cbcNames.clear();
		registerValues.clear();
		
		registerValues = activeRegister;
		
		for (String _cbcName : getActiveCBCs()){
			
			cbcNames.put(_cbcName, registerValues);
			
		}
		rpcContainer.add(cbcNames);
		
		rpcMessage myparams = new rpcMessage();
		myparams.setMethod("setI2CRegisterValues");
		myparams.setParams(rpcContainer); 
		service_.rpcService(myparams, new MethodCallback<rpcResponse>(){

			@Override
			public void onFailure(Method method, Throwable exception) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Method method, rpcResponse response) {
				getI2CValues();	
			}
			
		});
		
		
	}
	
	public void getI2CValues(){
		
		rpcContainer.clear();
		registerValues.clear();
		cbcNames.clear();
		
		registerValues.put(null, null);
		
		for (String _cbcName : getActiveCBCs()){
			
			cbcNames.put(_cbcName, registerValues);
			
		}
		
		rpcContainer.add(cbcNames);
		rpcMessage myparams = new rpcMessage();
		
		myparams.setMethod("I2CRegisterValues");
		myparams.setParams(rpcContainer); 
		
		service_.rpcService(myparams, new MethodCallback<rpcResponse>(){

			@Override
			public void onFailure(Method method, Throwable exception) {
				// TODO Auto-generated method stub
				Window.alert(exception.getMessage().toString());			
			}

			@Override
			public void onSuccess(Method method, rpcResponse response) {
				Map<String, Integer> regValues = new HashMap<String, Integer>();
				
				regValues = response.getResult().get(cbcList_.getItemText(cbcList_.getSelectedIndex())); //edits values from selected item on list
				for (String regValueNames : regValues.keySet()){
				TextBox test = i2cValueEntries.get(regValueNames);
				test.setEnabled(true);
				String hexValue = Integer.toHexString(regValues.get(regValueNames));
				if (hexValue.length()==1){
					test.setText("0x0" + hexValue);
					}
				else{
					test.setText("0x" + hexValue);
					}
				}
				
			}
		});
		
	}
		
}
	
	


