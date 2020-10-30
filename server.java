import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class server {  
    
    protected static final int DEFAULT_PORT = 8090;
    // TODO - add security by blocking non valid ports
   // private static final int MAX_PORT = 65535;
   // private static final int MIN_RESERVED_PORT=0;
   // private static final int MAX_RESERVED_PORT=1023;

    private int port;
    private boolean debug = false;

    public server(int port, boolean debug){
        this(port);
        this.debug=debug;
    }
    public server(int port){
        this.port=port;
    }
    
    public server(boolean debug){
        this();
        this.debug = debug;
    }
    public server(){
        this.port=DEFAULT_PORT;
    }

    
    public static void main(String[] args) {
        server server = new server(true);
        server.run();
    }

    public void run() {
        if(debug)
            System.out.println("Server starting...");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            if(debug){
                System.out.println("Server started");
                System.out.println("Server listening on port " + port);
            }
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    if(debug)
                        System.out.println("Client Connected\r\n");
                        
                    String request = getRequest(client);
                    handleClient(request.split("\r\n")[0].split(" ")[0],client,request);
                    
                    client.close();
                    if(debug)
                        System.out.println("Client Disconnected\r\n");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                
              
            }
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        

    }

    public String getRequest(Socket client) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            requestBuilder.append(line + "\r\n");
        }

        String request = requestBuilder.toString();
        
        return request;
       /* String[] requestsLines = request.split("\r\n");
        String method = request.split("\r\n")[0].split(" ")[0];

        List<String> headers = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }*/

    }

    public void handleClient(String method,Socket client, String request) throws IOException{
        

        if (method.equals("GET")){
            handleGET(client, request);
        }
        else if (method.equals("POST")){
            handlePOST(client, request);
        }
            
    }
    
    public void handleGET(Socket client, String content) throws IOException{
        sendResponse(client, content);
    }
    public void handlePOST(Socket client, String content) throws IOException{
        sendResponse(client, content);
    }

  
    public void sendResponse(Socket client, String content) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        OutputStream clientOutput = client.getOutputStream();
        StringBuilder clientResponse = new StringBuilder();
        
        clientResponse.append("HTTP/1.0 200 OK\r\n");
        clientResponse.append("Server: " + client.getInetAddress()+"\r\n");
        clientResponse.append(sdf.format(Calendar.getInstance().getTime()));
        clientResponse.append("\r\nContent-Type: text/html\r\n");
        clientResponse.append("Content-Length:"+ content.getBytes().length + "\r\n");
        clientResponse.append("\r\n"+content + "\r\n\r\n");

        clientOutput.write(clientResponse.toString().getBytes());
        clientOutput.flush();
    }
}
