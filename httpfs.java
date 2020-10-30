import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

public class httpfs extends server {

    public enum RequestType{
		GET,
		POST
	}
    
    private static final String DEFAULT_DIRECTORY = "/";
    private static String directory;
    private static boolean vPresent = false;
    private static boolean dPresent = false;
    private static boolean pPresent = false;


    public httpfs(int port, boolean debug,String directory) {
        super(port, debug);
        httpfs.directory = directory;
    }
    public httpfs(int port, boolean debug) {
        this(port, debug,DEFAULT_DIRECTORY);
    }
    public httpfs(int port) {
        this(port, false,DEFAULT_DIRECTORY);
    }

    public static void main(String[] args) {
        int port=DEFAULT_PORT;
        boolean validated = true;

        httpfs fileServer;

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
                    directory=DEFAULT_DIRECTORY;
                    System.out.println("The directory name is invalid, going to default directory");
                }
			} else if (args[i].equals("-p")) {
                if (pPresent)
                    validated = false;
                    pPresent = true;

                    try {
                        port = Integer.parseInt(args[i+1]);
                    } catch(Exception e) {
                        System.out.println("The port value is not valid. Switched by default to port "+DEFAULT_PORT);
                    }
                }
            if (!validated){
                System.out.println("Not validated error.");
            }
        }
        
        fileServer = new httpfs(port,vPresent,directory);

        fileServer.run();
    }

    @Override  
    public void handleGET(Socket client, String request) throws IOException{
        String returnCode = "200 OK";
        String content=null;
        File file = new File(directory);
        try{
            if(file.isDirectory()){
                String directoryContents[] = file.list();
                if(isDebug())
                    System.out.println("List of files and directories in the specified directory:");
                for(int i=0; i<directoryContents.length; i++) {
                    content+=directoryContents[i];
                    if(isDebug())
                        System.out.println(directoryContents[i]);
                }
            }
            else if (file.isFile()){
                
                content =httpLibrary.readFile(file);
                if(isDebug())
                    System.out.println("File contents: \n"+content);
            
            }
            else if (!file.canRead()){
                returnCode="403 Forbidden ";
            }
            else{
                returnCode="500 Internal Error ";
            }  
        }
            catch(FileNotFoundException fnfe){
                returnCode="404 Not Found";
            }
            catch(Exception fnfe){
                returnCode="500 Internal Error";
            }
        sendResponse(client,returnCode, content);
    }
    @Override 
    public void handlePOST(Socket client, String request) throws IOException{
        System.out.println("handlePOST");
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