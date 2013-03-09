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

	// private Object getData(Node node, String[] routes, int r) throws FileNotFoundException{
 //    // Print root node
 //    System.out.print(node.getNodeName());
 //    System.out.print("(");
 //    System.out.print(r + "/" + routes.length);
 //    System.out.println(")");

 //    // Make node list of children
 //    NodeList nodeList = node.getChildNodes();

 //    // Check if single node.
	// 	if (nodeList.getLength() == 1) {
	// 		// System.out.println("OUTPUT = " + nodeList.item(0).getTextContent());
	// 		return nodeList.item(0);

	// 	}	else {

	// 		Node firstNode = nodeList.item(1);

	//     for (int x = 0; x<nodeList.getLength(); x++) {
	//     	try{
	//     		// System.out.println("Node Name: " + nodeList.item(x).getNodeName());
	//     	} catch (Exception e) {
	//     		//We have reached text
	//     		// System.out.println("Output = " + nodeList.item(x-1).getNodeName());
	//     	}
	//     }

	//     // Check if first node has attributes
	//     if (firstNode.hasAttributes()) {
	//     	// System.out.println(node.getNodeName() + "'s CHILD HAS ATTRIBUTES");

	//     	if ((r+1) == routes.length) {
	//     		System.out.println("NodeList returned");
	//     		return nodeList;
	//     	}

	//     	// Get attribute to look for
	//     	String attribute = routes[r+1];
	//     	System.out.println("Searching for attribute " + attribute);

	//     	// Find child which has matching attribute
	//     	for (int i = 1; i < nodeList.getLength(); i=i+2) {
	//         Node currentNode = nodeList.item(i);

	// 				if (currentNode.getAttributes().getNamedItem("id").getNodeValue().equals(attribute)){
	// 					return getData(currentNode, routes, r+2);
	//     		}
	//    		 }

	//     } else { //if no attribute
	//     	// System.out.println(node.getNodeName() + "' CHILD HAS NO ATTRIBUTES");

	//     	String tag = routes[r];
	//     	// System.out.println("TAG = "+ tag);

	//     	Node currentNode;
	//     	for (int i = 0; i<nodeList.getLength(); i++){
	//     		currentNode = nodeList.item(i);
	//     		if (currentNode.getNodeName().equals(tag)){
	//     			return getData(currentNode, routes, ++r);
	//     		}

	//     	}
	//     	throw new FileNotFoundException();

	//     }
 //    }
 //    return null;
	// }

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

		URL url = new URL("http://local.dev" + requestPath);

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
		try{
			String[] stringRoutes = new String[routes.size()];
			stringRoutes = routes.toArray(stringRoutes);

			obj = resourceManager.getData(xmlDOM.getDocumentElement(), stringRoutes, 2);
		} catch (Exception e) {
			// handle xml not found!
			System.out.println("Something went wrong with getData()");
			System.out.println(e);
		}

		System.out.println();


		System.out.println("getData() returned: ");

		if (resourceManager.isNode(obj)) {
			System.out.println("object is a node");
			System.out.println(((MyNode)obj).getNode().getTextContent());

			Node node = ((MyNode)obj).getNode();
			for (int i = 0; i < node.getChildNodes().getLength(); i=i+2) {
				if (node.getChildNodes().item(i).getNodeValue().trim() != "") {
					System.out.println(node.getChildNodes().item(i).getNodeValue().trim());
				}
			}
		} else {
			System.out.println("object is a list");
			NodeList list = ((MyNode)obj).getList();
			System.out.println(list.getLength());
			for (int i = 1; i < list.getLength(); i=i+2) {
				System.out.println(list.item(i).getAttributes().getNamedItem("id").getNodeValue());
			}
		}

		if (obj != null) {
			// if (obj instanceof Node) {
			// 	System.out.println("Node");
			// 	System.out.println(((Node)obj).getTextContent());
			// }

			// ((Node)obj).setNodeValue("asdf");


			// System.out.println((resourceManager.getDom("customers")).getDocumentElement().getChildNodes().item(1).getChildNodes().item(1).getChildNodes().item(9).getChildNodes().item(3).getTextContent());

			// if ( > 1) {
			// System.out.println(((NodeList)obj).item(0));
			// }

			// if (obj instanceof NodeList) {
			// 	System.out.println("NodeList");
			// 	System.out.println(((NodeList)obj).item(0).getAttributes().getNamedItem("id").getNodeValue());
			// }

		} else {
			System.out.println("null");
		}

		System.out.println();



		// Construct the response message header
		String statusLine = null;
		String contentTypeLine = null;

		// statusLine = "HTTP/1.0 200 OK" + CRLF;
		// contentTypeLine = "Content-type: " + contentType(requestPath) + CRLF;

		statusLine = "HTTP/1.0 404 Not Found" + CRLF;
		contentTypeLine = "Content-type: text/html" + CRLF;

		switch (requestMethod) {
			// GET
			case "GET":
				System.out.println("get");
				if (obj != null) {
					if (resourceManager.isNode(obj)) {
						outToClient.writeBytes("Node \n");
						outToClient.writeBytes(((MyNode)obj).getNode().getTextContent());
					} else  {
						outToClient.writeBytes("NodeList \n");
						outToClient.writeBytes(((MyNode)obj).getList().item(0).getAttributes().getNamedItem("id").getNodeValue());
					}

					} else {
						System.out.println("null");
					}

				break;

			// CREATE BUT NOT UPDATE AN EXISTING ONE
			case "POST":
				System.out.println("post");
				break;

			// CREATE AND UPDATE
			case "PUT":
				System.out.println("put");
				break;

			// DELETE
			case "DELETE":
				System.out.println("delete");
				break;

			// DEFAULT
			default:
				System.out.println("unrecognized method");
				// return 501 Not Implemented
				break;
		}

		// statusLine = "HTTP/1.0 200 OK" + CRLF;
		// statusLine = "HTTP/1.0 201 Created" + CRLF;	// creation
		// statusLine = "HTTP/1.0 202 Accepted" + CRLF;	// update
		// statusLine = "HTTP/1.0 400 Bad Request" + CRLF;	// bad formed request
		// statusLine = "HTTP/1.0 404 Not Found" + CRLF;
		// statusLine = "HTTP/1.0 405 Method Not Allowed" + CRLF;
		// statusLine = "HTTP/1.0 500 Server Error" + CRLF;
		// statusLine = "HTTP/1.0 501 Not Implemented" + CRLF;

		// Send the status line and our header (which only contains the content-type line)
		//outToClient.writeBytes(statusLine);
		//outToClient.writeBytes(contentTypeLine);
		//outToClient.writeBytes(CRLF);

		// Send the body of the message (the web object)
		// sendBytes(fis, outToClient);
		outToClient.writeBytes("hello");

		// Close the streams and sockets
		os.close();
		inFromClient.close();
		socket.close();
	}

	/**
	 * Private method that returns the appropriate MIME-type string based on the suffix of the appended file
	 * @param fileName
	 * @return
	 */
	private static String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if (fileName.endsWith(".gif")) {
			return "image/gif";
		}
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		return "application/octet-stream";
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