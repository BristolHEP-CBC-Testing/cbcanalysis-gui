package uk.ac.bristol.hep.cbcteststand.client.utilities;



import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import org.fusesource.restygwt.client.Resource;
import org.fusesource.restygwt.client.RestServiceProxy;


public class InitJSON {
	private GlibControlService service_;
	
	public GlibControlService initGSON(){
		service_ = GWT.create(GlibControlService.class);

		if( !GWT.isProdMode() && GWT.isClient() )
		{
			// If GWT is running in development mode then the server side components can't
			// run (they're python). The RPC service needs to point to an actual running
			// RPC service connected to the hardware. Assume whoever is testing has forwarded
			// local port 3900 to the remote machine with the hardware connected and RPC
			// running.
			Resource resource = new Resource("http://localhost:3900/cbcTestStand/services/GlibControlProxy.py");
			((RestServiceProxy)service_).setResource(resource);
		}
		else
		{
			// Otherwise this is production code, so point to the RPC service running on
			// this host.
			Resource resource = new Resource(GWT.getModuleBaseURL()+"services/GlibControlProxy.py");
			((RestServiceProxy)service_).setResource(resource);
		}
		
		return service_;
		
	}


}
