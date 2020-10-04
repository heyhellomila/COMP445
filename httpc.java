import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Scanner;

public class httpc {

	/*	System.out.println(method);
		if(verbose)
			System.out.println("Verbose");
		else 
			System.out.println("not Verbose");

		for(String s:headers)
			System.out.println(s);
		System.out.println(data);
		System.out.println(url);
		
		*/
	
	private final static String help_general_text="httpc is a curl-like application but supports HTTP protocol only."+"\n\n"+
	"Usage:"+"\n\t"+"httpc command [arguments]"+"\n"+"The commands are:"+"\n\t"+"get\texecutes a HTTP GET request and prints the response."+"\n\t"+"post"+"\t"+"executes a HTTP POST request and prints the response."+"\n\t"+"help"+"\t"+"prints this screen."+"\n\n"+"Use  for more information about a command.";

	private final static String help_get_usage_text = "usage: httpc get [-v] [-h key:value] URL\n\nGet executes a HTTP GET request for a given URL.\n\n\t-v\t\tPrints the detail of the response such as protocol, status, and headers.\n\t-h key:value\tAssociates headers to HTTP Request with the format 'key:value'.";

	private final static String help_post_usage_text = "usage: httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL"+"\n\n"+
	"Post executes a HTTP POST request for a given URL with inline data or from file."+"\n\n\t"+"-v"+"\t\t"+"Prints the detail of the response such as protocol, status, and headers."+"\n\t"+"-h key:value\tAssociates headers to HTTP Request with the format 'key:value'."+"\n\t"+"-d string"+"\t"+"Associates an inline data to the body HTTP POST request."+"\n\t"+"-f file\t\tAssociates the content of a file to the body HTTP POST request"+"\n\n"+"Either [-d] or [-f] can be used but not both.";
	
	private final static String error_get_format="Get Error";
	private final static String error_post_format="Post Error";
	private final static String error_argument_format="Argument Error";

	public static void main(String[] args) throws MalformedURLException  {
		
		boolean verbose=false;
		String method=null;
		ArrayList<String> headers=new ArrayList<String>();
		String inlineData=null;
		String filePath=null;
		String fileData=null;
		String url=null;
		boolean vPresent=false;
		boolean dPresent=false;
		boolean fPresent=false;
		boolean validated=true;
		

		for(int i=0; i<args.length ; i++){
			if((args[i].toLowerCase()).equals("get")){
				if(method != null){					
					validated=false;
					}
				method="get";
				}
			else if((args[i].toLowerCase()).equals("post")){
				if(method != null){					
					validated=false;
				}
				method="post";}
			else if((args[i].toLowerCase()).equals("help")){
				System.out.println(help_general_text);}
			else if(args[i].equals("-v")){
				if(vPresent){
					validated=false;
					}
				vPresent=true;
				verbose=true;
			}
			else if(args[i].equals("-h")){
				i++;
				headers.add(args[i]);
			}
			else if(args[i].equals("-d")){
				if(dPresent)
					validated=false;
				dPresent=true;
				inlineData=args[i++];
			}
			else if(args[i].equals("-f")){
				if(fPresent)
					validated=false;
				fPresent=true;
				i++;
				filePath=args[i];
			}
			else
				url=args[i];
		}
		if(!validated)
			System.out.println(error_argument_format);
		else {
			
			if(method.equals("get")){
				if(inlineData != null || filePath != null)
					System.out.println(error_get_format);
				else{
					getRequest(verbose, headers, url);}
			}
			if(method.equals("post")){
				if(inlineData != null && filePath == null)
					postRequest(verbose, headers, inlineData, url);
					
				else if(inlineData == null && filePath != null){
					fileData=readFile(filePath);
					postRequest(verbose, headers, fileData, url);
					}
				else
					System.out.println(error_get_format);
			}
			
		}
	}

	public static String readFile(String file_path){
		String data=null;
		File file = new File(file_path);
		try{
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				 data= scanner.nextLine();
				System.out.println(data);
			}
			scanner.close();
		} catch (Exception e) {
				System.out.println("We didn't find a file. Please validate the path.");
		}
		return data;
	}

	public static void getRequest(boolean verbose, ArrayList<String> headers, String url)  throws MalformedURLException  {
		URL urlObj = new URL(url);
		System.out.println("URL Host:"+urlObj.getHost());
		System.out.println("URL File:"+urlObj.getFile());
			
		try {
			Socket socket = new Socket( urlObj.getHost(), 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
						
			StringBuilder request = new StringBuilder();
		 	request.append("GET /" + urlObj.getFile() +" HTTP/1.0\n");
			request.append("Host: " + urlObj.getHost()+"\n");
			for(String header: headers)
				request.append(header+"\n");

			System.out.println(request.toString());

			outputStream.write(request.toString().getBytes());
			outputStream.flush();
			StringBuilder response = new StringBuilder();
			
			int data = inputStream.read();
			
			while(data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}

			if(verbose == true){
				for(String header: headers)
					System.out.println(header);
			}
			
			System.out.println(response);
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void postRequest(boolean verbose, ArrayList<String> headers, String fileData, String url) throws MalformedURLException{
		/*try {
			Socket socket = new Socket("httpbin.org", 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			String body = "key1=value1&key2=value2";
			
			String request = "POST /post HTTP/1.0\r\n"
							+ "Content-Type:application/x-www-form-urlencoded\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;

			outputStream.write(request.getBytes());
			outputStream.flush();
			
			StringBuilder response = new StringBuilder();
			
			int data = inputStream.read();
			
			while(data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}

			System.out.println(response);
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public static void post2Example() {
		try {
			Socket socket = new Socket("httpbin.org", 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			String body = "--limit\r\n"
							+ "Content-Disposition: form-data; name=\"key1\"\r\n"
							+ "\r\n"
							+ "value1\r\n"
							+ "--limit\r\n"
							+ "Content-Disposition: form-data; name=\"key2\"\r\n"
							+ "\r\n"
							+ "value2\r\n"
							+ "--limit--\r\n";

			String request = "POST /post HTTP/1.0\r\n"
							+ "Content-Type:multipart/form-data; boundary=\"limit\"\r\n"
							+ "Content-Length: "+ body.length() +"\r\n"
							+ "\r\n"
							+ body;
			
			outputStream.write(request.getBytes());
			outputStream.flush();
			
			StringBuilder response = new StringBuilder();
			
			int data = inputStream.read();
			
			while(data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}

			System.out.println(response);
			
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void post3Example() {
		try {
			Socket socket = new Socket("httpbin.org", 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			String body = "{"
							+ "\"key1\":value1,"
							+ "\"key2\":value2"
							+ "}";
			
			String request = "POST /post?info=info HTTP/1.0\r\n"
							+ "Content-Type:application/json\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;

			outputStream.write(request.getBytes());
			outputStream.flush();
			
			StringBuilder response = new StringBuilder();
			
			int data = inputStream.read();
			
			while(data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}
			
			System.out.println(response);
			socket.close();
			System.out.println(response);
			socket.close();
			System.out.println(response);
			socket.close();
			System.out.println(response);
			socket.close();
			System.out.println(response);
			socket.close();
			System.out.println(response);
			socket.close();
			System.out.println(response);
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}