import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class server {
    public enum RequestType{GET, POST}
    private static final DEFAULT_PORT = 8080;
    private int port;
    private boolean debug = false;
    
    public server(int port, boolean debug){
        server(port);
        this.debug = debug;
    }

    public server(int port){
        this.port = port
    }

    public server(){
        this.port = DEFAULT_PORT;
    }

    public void run() throws IOException {
        if(debug)
            System.out.println("Server starting...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            if(debug)
                System.out.println("Server started");
            while (true) {
                
            if(debug)
                System.out.println("Server listening on port " + port);
                try (Socket client = serverSocket.accept()) {
                    System.out.println("Client Connected\r\n");
                    handleClient(client);
                }
            }

    }

    public void handleClient(Socket client) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            requestBuilder.append(line + "\r\n");
        }

        String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        List<String> headers = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }

        if (method.equals("GET"){
            handleGET();
        }
        else if (method.equals("POST"){
            handlePOST();
        }
            
    }
   
   

    private static void handleClient(Socket client) throws IOException {
        

        Path filePath = getFilePath(path);
        File f = new File(path);
        if (f.exist()) {
            // file exist
            String contentType = guessContentType(filePath);
            sendResponse(client, "200 OK", Files.readAllBytes(filePath));
        } else {
            // 404
            byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
            sendResponse(client, "404 Not Found", notFoundContent);
        }

    }

    private static Path getFilePath(String path) {
        if ("/".equals(path)) {
            path = "/index.html";
        }

        return Paths.get(path);
    }
    private static void sendResponse(Socket client, String status, byte[] content) throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
        clientOutput.write(("ContentType: text/html" + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(content);
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();
        client.close();
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }

}
