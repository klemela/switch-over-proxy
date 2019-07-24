package fi.csc.chipster.proxy.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

import fi.csc.chipster.proxy.ProxyServer;

/**
 * ProxyServer example
 * 
 * Start the proxy server and setup a minimal admin API (without any authentication)
 * for managing it. Print some examples commands to see how it works.
 * 
 * @author klemela
 *
 */
public class ExampleProxyServer {
	
	private final Logger logger = LogManager.getLogger();
	
    private ProxyServer proxy;

	private HttpServer adminServer;
    
    public static void main(String[] args) throws IOException {
    	@SuppressWarnings("unused")
		ExampleProxyServer server = new ExampleProxyServer();

//    	server.close();
    }

	public ExampleProxyServer() throws IOException {

    	try {
    		
    		int proxyPort = 8000;
    		int adminPort = 8001;
    		    		
	    	this.proxy = new ProxyServer(URI.create("http://0.0.0.0:" + proxyPort));

	    	proxy.startServer();
	    	logger.info("proxy listening in port " + proxyPort);
	    		    	
							
	    	ResourceConfig rc = new ResourceConfig()
	    			.register(JacksonJaxbJsonProvider.class)
	            	.register(new ExampleProxyAdminResource(proxy));

	        adminServer = GrizzlyHttpServerFactory.createHttpServer(URI.create("http://0.0.0.0:" + adminPort), rc);
	        adminServer.start();
	        logger.info("admin API listening in port " + adminPort);
	        
	        
	        proxy.addRoute("tools", "http://www.nic.funet.fi/pub/sci/molbio/chipster/dist/tools_extras");
	        
	        
	        logger.info("Examples");
	        logger.info("- Make a request through the proxy");
	        logger.info("    $ curl localhost:8000/tools/");
	        logger.info("    <!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">..."); 
	        
	        logger.info("- Create some example traffic");
	        logger.info("    $ curl -s localhost:8000/tools/samtools-0.1.18.tar.gz --limit-rate 4k > /dev/null &");
	        
	        logger.info("- List current routes");
	        logger.info("    $ curl localhost:8001/admin/routes");
	        logger.info("    [{\"proxyPath\":\"tools\",\"proxyTo\":\"http://www.nic.funet.fi/pub/sci/molbio/chipster/dist/tools_extras\",\"requestCount\":2,\"openConnectionCount\":1,\"requestsPerSecond\":0}]");	        	       
	        
	        logger.info("- List connections");
	        logger.info("    $ curl localhost:8001/admin/connections");
	        logger.info("    [{\"route\":{\"proxyPath\":\"tools\",\"proxyTo\":\"http://www.nic.funet.fi/pub/sci/molbio/chipster/dist/tools_extras\"},\"sourceAddress\":\"0:0:0:0:0:0:0:1\",\"requestURI\":\"http://localhost:8000/tools/samtools-0.1.18.tar.gz\",\"openTime\":{\"epochSecond\":1563961845,\"nano\":723000000},\"closeTime\":null,\"method\":\"GET\"}]");
	        
	        logger.info("- Add a new route");
	        logger.info("    $ curl localhost:8001/admin/routes -X POST  -H \"Content-Type: application/json\" -d '{\"proxyPath\":\"vm\",\"proxyTo\":\"http://www.nic.funet.fi/pub/sci/molbio/chipster/dist/virtual_machines\"}'");
	        
	        logger.info("- Delete a route ");
	        logger.info("    $ curl localhost:8001/admin/routes/tools -X DELETE");
	        
	        logger.info("- Add a new WebSocket route");
	        logger.info("    $ curl localhost:8001/admin/routes -X POST  -H \"Content-Type: application/json\" -d '{\"proxyPath\":\"events\",\"proxyTo\":\"ws://web-socket-server\"}'");	        		    		
		
    	} catch (URISyntaxException e) {
    		logger.error("proxy configuration error", e);
    	}
    }
	
	public void close() {
		adminServer.shutdown();
		proxy.close();
	}
}
