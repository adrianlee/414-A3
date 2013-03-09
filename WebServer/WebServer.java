/**
 * This is the solution file for Assignment 1 Problem 4 for ECSE 414 Fall 2012.
 *
 * This class implements a multi-threaded HTTP 1.0-compliant web server. The root directory from which files are
 * served is the same directory from which this application is executed. When the server encounters an error, it
 * sends a response message with the appropriate HTML code so that the error information is displayed.
 *
 * @author michaelrabbat
 *
 */
import java.io.*;
import java.net.*;
import java.util.*;
import org.w3c.dom.*;
import javax.xml.*;
import javax.xml.parsers.*;
import java.net.*;
import java.io.FileNotFoundException;

import static org.w3c.dom.Node.ATTRIBUTE_NODE;
import static org.w3c.dom.Node.DOCUMENT_TYPE_NODE;
import static org.w3c.dom.Node.ELEMENT_NODE;
import static org.w3c.dom.Node.TEXT_NODE;

/**
 * This is the main class which runs the loop that listens for incoming requests
 * and spawns new threads to handle each request.
 *
 * @author michaelrabbat
 *
 */


public final class WebServer {
	public static void main(String argx[]) throws Exception {
		// Set the port number (may not work with 80)
		int port = 6789;
		// Document[] doms = buildDOMs(new File("."));

		// Hashtable<String,Document> doms = buildTable(new File("."));

		ResourceManager resourceManager = new ResourceManager(new File("."));

		// System.out.println("Root element: " + doms[0].getDocumentElement().getNodeName());
		// System.out.println("Root element: " + doms[1].getDocumentElement().getNodeName());

		// Create the socket to listen for incoming connections
		ServerSocket welcomeSocket = new ServerSocket(port);

		// Enter an infinite loop and process incoming connections
		// Use Ctrl-C to quit the application
		while (true) {
			// Listen for a new TCP connection request
			Socket connectionSocket = welcomeSocket.accept();

			// Construct an HttpRequest object to process the request message
			HttpRequest request = new HttpRequest(connectionSocket, resourceManager);

			// Create a new thread to process the request
			Thread thread = new Thread(request);

			// Start the thread
			thread.start();
		}
	}
}

/**
 * This is the helper class that
 *
 * @author michaelrabbat
 *
 */
final class HttpRequest implements Runnable {
	final static String CRLF = "\r\n";
	Socket socket;
	ResourceManager resourceManager;
	/**
	 * Constructor takes the socket for this request
	 */
	public HttpRequest(Socket socket, ResourceManager resourceManager) throws Exception
	{
		this.socket = socket;
		this.resourceManager = resourceManager;
	}

	/**
	 * Implement the run() method of the Runnable interface.
	 */
	@Override
	public void run()
	{
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}



	public String getFirstLevelTextContent(Node node) {
    NodeList list = node.getChildNodes();
    StringBuilder textContent = new StringBuilder();
    for (int i = 0; i < list.getLength(); ++i) {
        Node child = list.item(i);
        if (child.getNodeType() == Node.TEXT_NODE)
            textContent.append(child.getTextContent().trim());
    }
    return textContent.toString();
	}


	public String getEnumeratedList(NodeList list) {
		String output = "";
		for (int i = 1; i < list.getLength(); i=i+2) {
			output += list.item(i).getAttributes().getNamedItem("id").getNodeValue() + CRLF;
		}
		return output;
	}

	/**
	 * This is where the action occurs
	 * @throws Exception
	 */
	private void processRequest() throws Exception
	{
		// Get a reference to the socket's input and output streams
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		// System.out.println("Root element: " + resourceManager.getDom("library").getDocumentElement().getNodeName());
		// System.out.println("Root element: " + resourceManager.getDom("customers").getDocumentElement().getNodeName());

		// Set up input stream filters
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(is));
		DataOutputStream outToClient = new DataOutputStream(os);

		int responseCode = 500;
		String responseBody = "";

		// Get the request line of the HTTP request message
		String requestLine = inFromClient.readLine();

		// Display the request line
		System.out.println();
		// System.out.println(requestLine);

		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		String requestMethod = tokens.nextToken();
		String requestPath = tokens.nextToken();
		String requestQuery = null;

		URI url = new URI("http://local.dev" + requestPath);

		requestPath = url.getPath();
		requestQuery = url.getQuery();

		// might want to decode after tokenizing path.
		requestPath = URLDecoder.decode(requestPath, "UTF-8");

		tokens = new StringTokenizer(requestPath, "/");

		List<String> routes = new ArrayList<String>();
		String next = null;
		while (true) {
			try {
				next = tokens.nextToken();
				if (next != null) {
					routes.add(next);
				}
			} catch (Exception e) {
				break;
			}
		}

		// Print the Request Method and Path
		System.out.println("METHOD: " + requestMethod);
		System.out.println("PATH: " + requestPath);
		System.out.println("QUERY: " + requestQuery);
		System.out.println();

		Document xmlDOM = null;
		Object obj = null;

		// Retrieve document specified by route(0)
		try{
			xmlDOM = resourceManager.getDom(routes.get(0));
		} catch (Exception e) {
			// FileNotFoundException
			System.out.println(e);
		}

		// Get outpu
		System.out.println("getData() output: ");
		String lastRoute = routes.get(routes.size() - 1);
		

		String[] stringRoutes = new String[routes.size()];
		String[] routesWOchild = new String[stringRoutes.length-1];
		stringRoutes = routes.toArray(stringRoutes);
		routes.remove(routes.size() - 1 );
		routesWOchild = routes.toArray(routesWOchild);

		try{
			if(requestMethod.equals("PUT") || requestMethod.equals("POST") || requestMethod.equals("DELETE") ){
				obj = resourceManager.getData(xmlDOM.getDocumentElement(), routesWOchild, 2);
			}else{
				obj = resourceManager.getData(xmlDOM.getDocumentElement(), stringRoutes, 2);
			}
		} catch (Exception e) {
			// handle xml not found!
			System.out.println("Something went wrong with getData()");
			System.out.println(e);
		}

		System.out.println();

		System.out.println("getData() returned: ");

		Node node = null;
		NodeList list = null;


		if(obj!=null){
			if (resourceManager.isNode(obj)) {
				System.out.println("object is a node");
				// System.out.println(((MyNode)obj).getNode().getTextContent());

				node = ((MyNode)obj).getNode();
				System.out.println(getFirstLevelTextContent(node));
			} else {
				System.out.println("object is a list");
				list = ((MyNode)obj).getList();
			}
		}else{
			System.out.println("Obj is null");
		}
		System.out.println();

		Object lastNodeInRoute;

		switch (requestMethod) {
			// GET
			case "GET":
				System.out.println("get");
				if (obj != null) {
					if (resourceManager.isNode(obj)) {
						// responseBody += "Node" + CRLF;
						responseBody += getFirstLevelTextContent(node);
					} else {
						// responseBody += "NodeList" + CRLF;
						responseBody += getEnumeratedList(list);
					}
					responseCode = 200;
					// OK
				} else {
					System.out.println("null");
					// Not found
					responseCode = 404;
				}

				break;

			// CREATE BUT NOT UPDATE AN EXISTING ONE
			case "POST":
				System.out.println("post");

				//check if tag already exists
				try{
					System.out.println("testing if " + lastRoute + " is already in");
					Object dummy = resourceManager.getData(xmlDOM.getDocumentElement(), stringRoutes, 2);
				}catch(Exception e){
					System.out.println(lastRoute + " not here, creating");
					Element tagCreate = xmlDOM.createElement(lastRoute);
					tagCreate.appendChild(xmlDOM.createTextNode(requestQuery));
					((MyNode)obj).getNode().appendChild(tagCreate);
					break;
				}
				//if this is reached, then the tag existed and thats not permitted, so send error
				System.out.println(lastRoute + " is already there, cannot update with POST");
				break;

			// CREATE AND UPDATE
			case "PUT": //A PUT request is used to CREATE and UPDATE a resource
				
				try{
					System.out.println("testing if " + lastRoute + " is already in");
					lastNodeInRoute = resourceManager.getData(xmlDOM.getDocumentElement(), stringRoutes, 2);
				}catch(Exception e){
					System.out.println(lastRoute + " not here, creating");
					Element tagCreate = xmlDOM.createElement(lastRoute);
					tagCreate.appendChild(xmlDOM.createTextNode(requestQuery));
					((MyNode)obj).getNode().appendChild(tagCreate);
					break;
				}
				//if this is reached, then the tag existed and thats not permitted, so send error
				System.out.println(lastRoute + " is already there so updating it");
				Element tagCreate = xmlDOM.createElement(lastRoute);
				tagCreate.appendChild(xmlDOM.createTextNode(requestQuery));
				((MyNode)obj).getNode().removeChild(((MyNode)lastNodeInRoute).getNode());
				((MyNode)obj).getNode().appendChild(tagCreate);
				break;

			// DELETE
			case "DELETE":
				System.out.println("delete");
				
				try{
					System.out.println("testing if " + lastRoute + " is already in");
					lastNodeInRoute = resourceManager.getData(xmlDOM.getDocumentElement(), stringRoutes, 2);
				}catch(Exception e){
					System.out.println(lastRoute + " not here, nothing to delete: ERROR");
					
					break;
				}
				//if this is reached, then the tag existed and thats not permitted, so send error
				System.out.println(lastRoute + " is already there so DELETING it");
				
				((MyNode)obj).getNode().removeChild(((MyNode)lastNodeInRoute).getNode());
				
				
				break;

			// DEFAULT
			default:
				System.out.println("Not Implemented");
				// Not Implemented
				responseCode = 501;
				break;
		}

		// Construct the response message header
		outToClient.writeBytes(setStatusLine(responseCode));
		outToClient.writeBytes(setContentType());
		outToClient.writeBytes(CRLF);

		// Send the body of the message (the web object)
		outToClient.writeBytes(responseBody);

		// Close the streams and sockets
		os.close();
		inFromClient.close();
		socket.close();
	}


	private String setStatusLine(int code) {
		String output;
		switch (code) {
			case 200:
				output = "HTTP/1.0 200 OK" + CRLF;
				break;
			case 201:
				output = "HTTP/1.0 201 Created" + CRLF;	// creation
				break;
			case 202:
				output = "HTTP/1.0 202 Accepted" + CRLF;	// update
				break;
			case 400:
				output = "HTTP/1.0 400 Bad Request" + CRLF;	// bad formed request
				break;
			case 404:
				output = "HTTP/1.0 404 Not Found" + CRLF;
				break;
			case 405:
				output = "HTTP/1.0 405 Method Not Allowed" + CRLF;
				break;
			case 500:
				output = "HTTP/1.0 500 Server Error" + CRLF;
				break;
			case 501:
				output = "HTTP/1.0 501 Not Implemented" + CRLF;
				break;
			default:
				output = "HTTP/1.0 500 Server Error" + CRLF;
				// 500
		}
		return output;
	}

	private String setContentType() {
		return "Content-type: text/html" + CRLF;
	}

	/**
	 * Private helper method to read the file and send it to the socket
	 * @param fis
	 * @param os
	 * @throws Exception
	 */
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		// Allocate a 1k buffer to hold bytes on their way to the socket
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket's output stream
		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}
}