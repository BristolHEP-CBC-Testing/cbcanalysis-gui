package uk.ac.bristol.hep.cbcteststand.client;

import com.google.gwtjsonrpc.common.AsyncCallback;
import com.google.gwtjsonrpc.common.RemoteJsonService;
import com.google.gwtjsonrpc.common.RpcImpl;

@RpcImpl(version=RpcImpl.Version.V2_0,transport=RpcImpl.Transport.HTTP_POST)
public interface GlibControlService extends RemoteJsonService
{
	public void connectedCBCNames( String [] cbcNames, AsyncCallback<String[]> callback );
}
