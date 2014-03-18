#!/usr/local/bin/python

"""
@author Mark Grimes (mark.grimes@bristol.ac.uk)
@date 13/Mar/2014 (rewritten for CORS from a file written on 17/Jan/2014)
"""
import socket, os, sys, time

# ------------------------------------
# --- Important behaviour settings ---
# ------------------------------------
INSTALLATION_PATH="/home/phmag/CMSSW_5_3_4/src/SLHCUpgradeTracker/CBCAnalysis" # Need to find a way so that this is not hardcoded 
logging=False      # Whether to dump debugging information to a log.
serverScriptListeningAddress="/tmp/CBCTestStand_rpc_server"  # The socket address that the receiving script listens on
serverScript=INSTALLATION_PATH+"/gui/serverProcess/GlibControlService.py" # The script that will answer my requests
allowCrossSiteAccess=False

def relayRequest( serverSocket, allowCORSAccess=False, logToFile="" ) :
	"""
	Passes the JSON-RPC request down the unix socket and prints the response to stdout. If the
	Apache http server has called this script then it relays stdout back to the requesting client.

	@parameter serverSocket       The socket that the server scipt is listneing on
	@parameter logToFile          If not blank, log all messages and responses to the given file
	@parameter allowCORSAccess    Whether or not to allow access from other domains. Generally this
	                              should be False, but it is often much easier to test on a local
	                              machine different to where the hardware is connected. See
	                              https://developer.mozilla.org/en/docs/HTTP/Access_control_CORS

	@author Mark Grimes (mark.grimes@bristol.ac.uk)
	@date 13/Mar/2014
	"""
	# Create a socket for the other script to pass the message back on. I need
	# some unique name so use the process ID.
	listeningAddress="/tmp/CBCTestStand_rpc_server_response-"+str(os.getpid())
	response = socket.socket( socket.AF_UNIX, socket.SOCK_DGRAM )
	response.bind( listeningAddress )

	# Apache should have set 'CONTENT_LENGTH' to the size of the message.
	# Read the whole message in.
	contLen=int(os.environ['CONTENT_LENGTH'])
	data = sys.stdin.read(contLen)
	if logToFile!="" :
		logFile=open(logToFile,'a')
		logFile.write(data+"\n")
	# For the listening script to be able to read the data, I first need to let
	# it now how long the data is. I also need to tell it where to communicate
	# the response to.
	data=listeningAddress+"\n"+str(len(data))+"\n"+data
	serverSocket.send(data)

	packetSize=1024 # The size of the chunks I receive on the pipe
	# First find out how long the response is
	datagram = response.recv( packetSize, socket.MSG_PEEK ) # Look but don't remove
	firstNewlinePosition=datagram.find('\n')
	dataLength=int(datagram[0:firstNewlinePosition])
	messageLength=dataLength+firstNewlinePosition+1
	# Make sure the packet size is large enough to read the whole message.
	while packetSize < messageLength : packetSize=packetSize*2 # keep as a power of 2
	# Now that I have the correct packet size, I can get the full message and remove
	# it from the queue.
	datagram = response.recv( packetSize )
	message=datagram[firstNewlinePosition+1:]
	if logToFile!="" :
		logFile.write(message)

	if allowCORSAccess :
		# If there is an 'Origin' header as part of CORS request (see
		# https://developer.mozilla.org/en/docs/HTTP/Access_control_CORS) then echo it back.
		# Basically allow cross-site scripting from anywhere.
		try : sys.stdout.write("Access-Control-Allow-Origin: "+os.environ['HTTP_ORIGIN']+"\n")
		except : pass # If the 'Origin' header isn't in the request I don't want it in the reply

	# Write the response to stdout. Apache passes this back to the requestor.
	sys.stdout.write(message)

	if logToFile!="" : logFile.close()

	serverSocket.close()
	response.close()
	if os.path.exists(listeningAddress) : os.remove(listeningAddress)

def openServerSocket( sendAddress, receivingScript ) :
	"""
	Tries to connect to the socket that the server script is listening on. If it can't, then
	it assumes the server script is not running and starts it.

	@parameter sendAddress       The address that the server script is listening on
	@parameter receivingScript   If the sendAddress is not already in use then the server script
	                             needs to be started. This is the filename of where the script is
	                             on the server.
	@return                      The socket, which has either been re-connected to or created.

	@author Mark Grimes (mark.grimes@bristol.ac.uk)
	@date 13/Mar/2014
	"""
	serverSocket = socket.socket( socket.AF_UNIX, socket.SOCK_DGRAM )
	try :
		#
		# See if I can connect to the sendAddress. If I can't then the listening script isn't
		# running yet. I could check os.path.exists( sendAddress ) to see if the socket is open,
		# but in some extreme circumstances the listening script dies before it has the chance
		# to remove the socket. In this case I'll try and remove it and start the listening
		# script.
		#
		serverSocket.connect( sendAddress )
	except socket.error :
		try :
			os.remove( sendAddress )
		except OSError as exception :
			if exception.strerror=="Operation not permitted" :
				# The socket has probably already been created by another user, so the file
				# permissions are set such that I can't delete it.
				# I'll raise a more meaningful error instead.
				raise RuntimeError( "The socket '"+sendAddress+"' could not be contacted, and could not be deleted. Is it in use by another user? You need to close and delete this socket.")
			if exception.strerror!="No such file or directory" :
				# If it doesn't exist that's fine, the socket.error was probably
				# because the listening script isn't running. Any other error I
				# want to pass on however.
				raise
		# I should be clear to start the listening script now.
		import subprocess
		devnull=open("/dev/null")
		subprocess.Popen( ['python2.6',receivingScript], stdout=devnull )
		time.sleep(1) # Sleep for a second to allow the new process to open the port
		# Now everything should be set up for me to try to connect again. If this
		# doesn't work I don't know what to do.
		serverSocket.connect( sendAddress )
	return serverSocket

def respondToCORSPreflight() :
	"""
	Provides the response to a CORS (https://developer.mozilla.org/en/docs/HTTP/Access_control_CORS)
	preflight request. This response allows access from ANYWHERE, so only use if you're sure you're
	on a safe network. You could perform a check on os.environ['HTTP_ORIGIN'] to allow only from
	certain sources to be more safe.

	@author Mark Grimes (mark.grimes@bristol.ac.uk)
	@date 13/Mar/2014
	"""
	message  = "Access-Control-Allow-Origin: "+os.environ['HTTP_ORIGIN']+"\n"
	message += "Access-Control-Allow-Methods: "+os.environ['HTTP_ACCESS_CONTROL_REQUEST_METHOD']+"\n"
	message += "Access-Control-Allow-Headers: "+os.environ['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']+"\n"
	message += "Access-Control-Max-Age: "+str(360)+"\n"
	message += "\n"
	sys.stdout.write(message)

#
# See what headers were in the request. Apache stores the headers in environment variables.
#
try :
	if os.environ['REQUEST_METHOD']=='OPTIONS' :
		# This is probably a CORS preflight request. If I want to allow cross-site access (usually
		# only for testing) then I need to make the appropriate response. If I don't want to allow
		# it, then ignore the request.
		if( allowCrossSiteAccess ) : respondToCORSPreflight()
	elif os.environ['REQUEST_METHOD']=='POST' :
		# This is presumably a normal json-rpc request. I first need to make sure the server script
		# is listening on the socket, then pass the message on to that.
		socket=openServerSocket( serverScriptListeningAddress, serverScript )
		if logging==True : relayRequest( socket, allowCrossSiteAccess, logToFile="/tmp/proxyCommunication.log" )
		else : relayRequest( socket, allowCrossSiteAccess )
	else : raise RuntimeError( "No code in place to handle a '"+os.environ['REQUEST_METHOD']+"' request")
except KeyError as error :
	# A required header wasn't sent with the request. I should probably think about giving a more
	# instructive message at some point in the future.
	raise
