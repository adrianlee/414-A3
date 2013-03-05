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

		// Create the socket to listen for incoming connections
		ServerSocket welcomeSocket = new ServerSocket(port);

		// Enter an infinite loop and process incoming connections
		// Use Ctrl-C to quit the application
		while (true) {
			// Listen for a new TCP connection request
			Socket connectionSocket = welcomeSocket.accept();

			// Construct an HttpRequest object to process the request message
			HttpRequest request = new HttpRequest(connectionSocket);

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

	/**
	 * Constructor takes the socket for this request
	 */
	public HttpRequest(Socket socket) throws Exception
	{
		this.socket = socket;
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

	/**
	 * This is where the action occurs
	 * @throws Exception
	 */
	private void processRequest() throws Exception
	{
		// Get a reference to the socket's input and output streams
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		// Set up input stream filters
		BufferedReader inFromClient = new BufferedReader(new InputStreamReader(is));
		DataOutputStream outToClient = new DataOutputStream(os);

		// Get the request line of the HTTP request message
		String requestLine = inFromClient.readLine();

		// Display the request line
		System.out.println();
		System.out.println(requestLine);

		// Get and display the header lines
		String headerLine = null;
		while ((headerLine = inFromClient.readLine()).length() != 0) {
			System.out.println(headerLine);
		}

		// Extract the filename from the request line
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken(); // skip over the method, which we'll assume is "GET"
		String fileName = tokens.nextToken();

		// Prepend a "." so that the file request is within the current directory
		fileName = "." + fileName;

		// Open the requested file
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}

		// Construct the response message header
		String statusLine = null;
		String contentTypeLine = null;
		String errorMessage = "<HTML><HEAD><TITLE>404 Not Found</TITLE></HEAD><BODY>404 Not Found</BODY></HTML>";
		if (fileExists) {
			statusLine = "HTTP/1.0 200 OK" + CRLF;
			contentTypeLine = "Content-type: " + contentType(fileName) + CRLF;
		} else {
			statusLine = "HTTP/1.0 404 Not Found" + CRLF;
			contentTypeLine = "Content-type: text/html" + CRLF;
		}

		// Send the status line and our header (which only contains the content-type line)
		outToClient.writeBytes(statusLine);
		outToClient.writeBytes(contentTypeLine);
		outToClient.writeBytes(CRLF);

		// Send the body of the message (the web object)
		if (fileExists) {
			sendBytes(fis, outToClient);
			fis.close();
		} else {
			outToClient.writeBytes(errorMessage);
		}

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