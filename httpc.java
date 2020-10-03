import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.MalformedURLException;


public class Httpc {
	
	public static void main(String[] args) throws MalformedURLException  {
		getExample();

		//post1Example();
		//post2Example();
		//post3Example();
		//getRedirectExample();

		/*
		System.out.println("Exercise 1:\nCommand line input:\n");

		for(int i = 0; i < args.length; i++){
			System.out.println(args[i]);
		}
		
		System.out.println("\nExercise 2:\nURL parsing:\n");

		String urlString1 = "http://www.example.com/docs/resource1.html";
		URL url1 = new URL(urlString1);

		String urlString2 = "http://www.example.com:1080/docs/resource1.html";
		URL url2 = new URL(urlString2);

		String urlString3 = "http://www.example.com/docs/resource1.html?key1=value1&key2=value2";
		URL url3 = new URL(urlString3);

		System.out.println("URL 1: " + urlString1);
		System.out.println("Host: " + url1.getHost());
		System.out.println("Path: " + url1.getPath());
		System.out.println("Port: " + url1.getPort());
		System.out.println("Default port: " + url1.getDefaultPort());
		System.out.println("File: " + url1.getFile());
		System.out.println("Protocol: " + url1.getProtocol());
		System.out.println("Query: " + url1.getQuery() + "\n");

		System.out.println("URL 2: " + urlString2);
		System.out.println("Host: " + url2.getHost());
		System.out.println("Path: " + url2.getPath());
		System.out.println("Port: " + url2.getPort());
		System.out.println("Default port: " + url2.getDefaultPort());
		System.out.println("File: " + url2.getFile());
		System.out.println("Protocol: " + url2.getProtocol());
		System.out.println("Query: " + url2.getQuery() + "\n");

		System.out.println("URL 3: " + urlString3);
		System.out.println("Host: " + url3.getHost());
		System.out.println("Path: " + url3.getPath());
		System.out.println("Port: " + url3.getPort());
		System.out.println("Default port: " + url3.getDefaultPort());
		System.out.println("File: " + url3.getFile());
		System.out.println("Protocol: " + url3.getProtocol());
		System.out.println("Query: " + url3.getQuery() + "\n");
		*/
	}

	public static void httpc(String path, String host, String query) {
		try {
			Socket socket = new Socket(host, 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			String request = "";
			if(query == null) {
				request = "GET " + path + " HTTP/1.0\r\n\r\n";
			} else {
				request = "GET " + path + "?" +query +" HTTP/1.0\r\n\r\n";
			}

			//http://postman-echo.com:100/wikypedia/article/1?key=value
			
			outputStream.write(request.getBytes());
			outputStream.flush();
			
			StringBuilder response = new StringBuilder();
			
			int data = inputStream.read();
			
			while(data != -1) {
				response.append((char) data);
				data = inputStream.read();
			}
			
			System.out.println(request);
			System.out.println(response);
			socket.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getExample() {
		try {
			Socket socket = new Socket("2cbe98bfc038.ngrok.io", 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			
			String request = "GET " +  "/get HTTP/1.0\r\nHost: 2cbe98bfc038.ngrok.io\r\n\r\n";
			
			//http://postman-echo.com:100/wikypedia/article/1?key=value

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

	public static void post1Example() {
		try {
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
		}
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
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void getRedirectExample() {
		try {
			Socket socket = new Socket("localhost", 80);
			
			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();
			

			//String request = "GET / HTTP/1.0\r\nHost: www.facebook.com\r\n\r\n";
			String request = "GET /get HTTP/1.0\r\n\r\n";
			//String request = "GET / HTTP/1.0\r\n\r\n";
			
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
}