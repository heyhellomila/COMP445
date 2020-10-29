import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class httpc {

	/*
	 * System.out.println(method);
	 * 
	 * if(verbose) System.out.println("Verbose"); else
	 * System.out.println("not Verbose");
	 * 
	 * for(String s:headers) System.out.println(s); System.out.println(data);
	 * System.out.println(url);
	 * 
	 */

	private final static String help_general_text = "httpc is a curl-like application but supports HTTP protocol only."
			+ "\n\n" + "Usage:" + "\n\t" + "httpc command [arguments]" + "\n" + "The commands are:" + "\n\t"
			+ "get\texecutes a HTTP GET request and prints the response." + "\n\t" + "post" + "\t"
			+ "executes a HTTP POST request and prints the response." + "\n\t" + "help" + "\t" + "prints this screen."
			+ "\n\n" + "Use  for more information about a command.";

	private final static String help_get_usage_text = "usage: httpc get [-v] [-h key:value] URL\n\nGet executes a HTTP GET request for a given URL.\n\n\t-v\t\tPrints the detail of the response such as protocol, status, and headers.\n\t-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.";

	private final static String help_post_usage_text = "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL"
			+ "\n\n" + "Post executes a HTTP POST request for a given URL with inline data or from file." + "\n\n\t"
			+ "-v" + "\t\t" + "Prints the detail of the response such as protocol, status, and headers." + "\n\t"
			+ "-h key:value\tAssociates headers to HTTP Request with the format 'key:value'." + "\n\t" + "-d string"
			+ "\t" + "Associates an inline data to the body HTTP POST request." + "\n\t"
			+ "-f file\t\tAssociates the content of a file to the body HTTP POST request" + "\n\n"
			+ "Either [-d] or [-f] can be used but not both.";

	private final static String error_get_format = "Get Error";
	private final static String error_post_format = "Post Error";
	private final static String error_argument_format = "Argument Error";

	public enum RequestType{
		GET,
		POST
	}

	public static void main(String[] args) throws MalformedURLException {

		boolean verbose = false;
		String method = null;
		ArrayList<String> headers = new ArrayList<String>();
		String inlineData = null;
		String filePath = null;
		String fileData = null;
		String url = null;
		boolean vPresent = false;
		boolean dPresent = false;
		boolean fPresent = false;
		boolean validated = true;

		for (int i = 0; i < args.length; i++) {
			if ((args[i].toLowerCase()).equals("get")) {
				if (method != null) {
					validated = false;
				}
				method = "get";
			} else if ((args[i].toLowerCase()).equals("post")) {
				if (method != null) {
					validated = false;
				}
				method = "post";
			} else if ((args[i].toLowerCase()).equals("help")) {
				i++;
				validated=false;
				if(args[i]==null)
					System.out.println(help_general_text);
				else if(args[i].toLowerCase().equals("get")){
					System.out.println(help_get_usage_text);
				}
				else if(args[i].toLowerCase().equals("post")){
					System.out.println(help_post_usage_text);
				}

			} else if (args[i].equals("-v")) {
				if (vPresent) {
					validated = false;
				}
				vPresent = true;
				verbose = true;
			} else if (args[i].equals("-h")) {
				i++;
				headers.add(args[i]);
			} else if (args[i].equals("-d")) {
				if (dPresent)
					validated = false;
				dPresent = true;
				i++;
				inlineData = args[i];
			} else if (args[i].equals("-f")) {
				if (fPresent)
					validated = false;
				fPresent = true;
				i++;
				filePath = args[i];
			} else
				url = args[i];
		}
		if (!validated || method == null)
			System.out.println(error_argument_format);
		else {

			if (method.equals("get")) {
				if (inlineData != null || filePath != null)
					System.out.println(error_get_format);
				else {
					request(RequestType.GET, url,verbose,headers,null);
				}
			}
			if (method.equals("post")) {
				if (inlineData != null && filePath == null)
					request(RequestType.POST,url, verbose, headers, new ArrayList<String>(Arrays.asList("0",inlineData)));

				else if (inlineData == null && filePath != null) {
					try{
					fileData = readFile(filePath);
					request(RequestType.POST,url, verbose, headers, new ArrayList<String>(Arrays.asList("1",fileData)));
				}
				catch (FileNotFoundException e){
					System.out.println("We didn't find a file. Please validate the path");
				}
				catch (Exception e){
					System.out.println("Something went wrong! Validate the file for errors");
				}
				} 
				else
					System.out.println(error_post_format);
			}

		}
	}
	
	public static String readFile(String file_path) throws FileNotFoundException{
		StringBuilder data = new StringBuilder();
		File file = new File(file_path);

			Scanner scanner = new Scanner(file);
			while (scanner.hasNextLine()) {
				data.append(scanner.nextLine());				
			}
			scanner.close();
		
		return data.toString();
	}

	// if Post, args must contain in the first position a 1/0 as boolean for if the fhe body comes from a file 
	// on the second position it should contain the body itself or "" if empty body
	// if Get, Args is empty
	public static void request(RequestType rt, String url, boolean verbose, ArrayList<String> headers, ArrayList<String> args) throws MalformedURLException{
		URL urlObj = new URL(url);
		String contentType=null;
		String body=null;

		if(rt.equals(RequestType.POST)){
			Boolean fileBody = args.get(0).equals("1") ? true : false;
			body = args.get(1);
			contentType = "application/x-www-form-urlencoded";
		if (fileBody)
			contentType = "multipart/form-data; boundary=\"limit\"";
		else if (body.length() > 0 && (body.charAt(0) == '{'))
			contentType = "application/json";
		}
		contentType="text/html; charset=UTF-8";
		
		try {
			Socket socket = new Socket(urlObj.getHost(), 80);

			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			StringBuilder request = new StringBuilder();
			String requestString = null;
			if(rt.equals(RequestType.GET)){
				requestString = "GET";
			}
			else if (rt.equals(RequestType.POST)){
				requestString = "POST";
			}
			request.append(requestString+" /" + urlObj.getFile() + " HTTP/1.0\n");
			
			request.append("Host: " + urlObj.getHost() + "\n");
			
			for (String header : headers)
				request.append(header + "\n");
			
			if (rt.equals(RequestType.POST)){
				request.append("Content-Type: " + contentType + "\n");
				request.append("Content-Length: " + body.length() + "\n" + "\n" + body);
			}

			request.append("\n");
			System.out.println(request.toString());
			outputStream.write(request.toString().getBytes());
			outputStream.flush();
			StringBuilder response = new StringBuilder();

			int data = inputStream.read();

			while (data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}

			if (!verbose) 
			System.out.println(response.toString().substring(response.toString().indexOf("\r\n\r\n")));
				else
			System.out.println(response);

			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}