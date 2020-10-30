import java.io.IOException;
import java.net.Socket;

public class httpfs extends server {

    public enum RequestType{
		GET,
		POST
	}

    public static void main(String[] args) {
		int port = 8090;
		String directory = "";
		boolean vPresent = false;
        boolean dPresent = false;
        boolean pPresent = false;
		boolean validated = true;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-v")) {
				if (vPresent) {
				    validated = false;
				}
				vPresent = true;
			} else if (args[i].equals("-d")) {
				if (dPresent)
				    validated = false;
				    dPresent = true;
				try {
                    directory = args[i + 1];
                } catch(Exception e){
                    System.out.println("The directory name is invalid");
                }
			} else if (args[i].equals("-p")) {
                if (pPresent)
                    validated = false;
                    pPresent = true;

                    try {
                        port = Integer.parseInt(args[i+1]);
                    } catch(Exception e) {
                        port = 8080;
                        System.out.println("The port value is not valid. Switched by default to port 8080");
                    }
                }
            if (!validated){
                System.out.println("Not validated error.");
            }
        }

        server server = new server(port,true);
        server.run();
    }
    @Override 
    public void handleClient(String method,Socket client, String request) throws IOException{}
    
    @Override  
    public void handleGET(Socket client, String content) throws IOException{
        //sendResponse(client, content);
    }
    @Override 
    public void handlePOST(Socket client, String content) throws IOException{
        //sendResponse(client, content);
    }
}
     /*   String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
                System.out.println(request);
                System.out.println(requestsLines.toString());
                System.out.println(requestLine.toString());
                System.out.println(method);
                System.out.println(path);
                System.out.println(version);
                System.out.println(host);
                */
        

/*
        Path filePath = getFilePath(path);
        if (Files.exists(filePath)) {
            // file exist
            String contentType = guessContentType(filePath);
            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            // 404
            byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
            sendResponse(client, "404 Not Found", "text/html", notFoundContent);
        }

    }

    private static void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
        clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(content);
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        client.close();
    }

    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/index.html";
        }

        return Paths.get("/tmp/www", path);
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }


}
*/