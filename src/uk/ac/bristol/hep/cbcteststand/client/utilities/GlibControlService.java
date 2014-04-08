//Uses RestyGWT to make the call since GSON can't parse a map of maps during an async callback

package uk.ac.bristol.hep.cbcteststand.client.utilities;

import java.util.Map;
import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.RestService;

public interface GlibControlService extends RestService {
	
	globalId globalId = new globalId();
	
	 	
	 	
	 	@Produces("application/json")
		@POST
	 	public void rpcService(rpcMessage parameters, MethodCallback<rpcResponse> callback ); //generic class handles all callbacks which have a container
	 	
	 	@Produces("application/json")
		@POST
	    public void connectedCBCService(rpcMessage parameters, MethodCallback<cbcNamesResponse> methodCallback ); //the only method that doesn't receive data in a container from the server
	 	
	 	public class rpcMessage {
	 		  String jsonrpc = "2.0";
	 		  String method;
	 		  List<Map<String, Map<String, Integer>>> params; //[Container{ CBCName{ RegValue{Value} ...
	 		  int id = globalId.getId();
	 		  
	 		  public void setId(int id){
	 			  this.id = id;
	 		  }

	 		  public void setMethod(String method){
	 			  this.method = method;
	 		  }
	 		  
	 		  public void setParams(List<Map<String, Map<String, Integer>>> params){
	 			  this.params = params;
	 		  }
	 		  
	 		}
	 	
	 	//responses from server are parsed as a class
	 	
	 	public class rpcResponse {

	 	    private int id;
	 	    private Map<String, Map<String, Integer>> result;
	 	    private String error;

	 	    public int getId() {
	 	        return id;
	 	    }

	 	    public void setId(int id) {
	 	        this.id = id;
	 	    }

	 	    public Map<String, Map<String, Integer>> getResult() {
	 	        return result;
	 	    }

	 	    public void setResult(Map<String, Map<String, Integer>> result) {
	 	        this.result = result;
	 	    }

	 	    public String getError() {
	 	        return error;
	 	    }

	 	    public void setError(String error) {
	 	        this.error = error;
	 	    }

	 	}
	 	public class cbcNamesResponse {

	 	    private int id;
	 	    List<String> result;
	 	    private String error;

	 	    public int getId() {
	 	        return id;
	 	    }

	 	    public void setId(int id) {
	 	        this.id = id;
	 	    }

	 	    public List<String> getResult() {
	 	        return result;
	 	    }

	 	    public void setResult(List<String> result) {
	 	        this.result = result;
	 	    }

	 	    public String getError() {
	 	        return error;
	 	    }

	 	    public void setError(String error) {
	 	        this.error = error;
	 	    }

	 	}
	 	
	 	//sets unique ID for JSON-RPC request
	 	public class globalId{
	 		public int getId(){
	 			return (int) System.currentTimeMillis(); 
	 		}
	 		
	 	}
}

