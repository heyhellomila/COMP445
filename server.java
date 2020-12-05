import UDP.Packet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class server {

    public static final int DEFAULT_PORT = 8090;
    public static final int MAX_PORT = 65353;
    public static final int MAX_RESERVED = 1023;

    private int port;

    private boolean debug = false;


    public server(int port, boolean debug) {
        this(port);
        this.debug = debug;
    }

    public server(int port) {
        this.port = port;
    }

    public server(boolean debug) {
        this();
        this.debug = debug;
    }

    public server() {
        this.port = DEFAULT_PORT;
    }


    public static void main(String[] args) {
        server server = new server(true);
        server.run();
    }

    public void run() {
        if (debug)
            System.out.println("Server starting...");
        if (port > MAX_PORT || port < MAX_RESERVED) {
            if (debug)
                System.out.println("Invalid port, switching to default port: " + DEFAULT_PORT);
            port = DEFAULT_PORT;
        }
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            if (debug) {
                System.out.println("Server started");
                System.out.println("Server listening on port " + port);
            }
            while (true) {
                try (DatagramChannel channel = DatagramChannel.open()) {
                    channel.bind(new InetSocketAddress(port));
                    ByteBuffer buffer = ByteBuffer
                            .allocate(Packet.MAX_LEN)
                            .order(ByteOrder.BIG_ENDIAN);
                    if (debug)
                        System.out.println("Client Connected: " + channel.getLocalAddress() + "\r\n");

                    while (true) {
                        buffer.clear();
                        SocketAddress router = channel.receive(buffer);

                        buffer.flip();
                        Packet packet = Packet.fromBuffer(buffer);
                        buffer.flip();

                        if (debug) {
                            System.out.println("Received Packet of type " + packet.getType() + " and sequence number: " + packet.getSequenceNumber());
                        }

                        //String payload = new String(packet.getPayload(), UTF_8);
                        Packet response = packet.toBuilder()
                                .setPayload(payl)

                    }
                    //String request = getRequest(client);
                    //handleClient(request.split("\r\n")[0].split(" ")[0], client, request);
                    //client.close();
                    if (debug)
                        System.out.println("Client Disconnected\r\n");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }


            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }


    }

    public String getRequest(Socket client) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
        StringBuilder requestBuilder = new StringBuilder();

        int contentLength = -1;
        String line;
        while (!(line = br.readLine()).isEmpty()) {
            if (line.contains("Content-Length"))
                contentLength = Integer.parseInt(line.split(" ")[1]);
            requestBuilder.append(line + "\r\n");

        }
        if (contentLength > 0) {
            char[] charArray = new char[contentLength];
            br.read(charArray, 0, contentLength);
            requestBuilder.append("\r\n" + new String(charArray) + "\r\n");
        }

        return requestBuilder.toString();

    }

    public void handleClient(String method, Socket client, String request) throws IOException {

        if (method.equals("GET")) {
            handleGET(client, request);
        } else if (method.equals("POST")) {
            handlePOST(client, request);
        }

    }

    public void handleGET(Socket client, String request) throws IOException {
        sendResponse(client, "200 OK", request);
    }

    public void handlePOST(Socket client, String request) throws IOException {
        sendResponse(client, "200 OK", request);
    }

    public void sendResponse(Socket client, String responseCode, String content) throws IOException {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
        OutputStream clientOutput = client.getOutputStream();
        StringBuilder clientResponse = new StringBuilder();

        clientResponse.append("HTTP/1.0 " + responseCode + "\r\n");
        clientResponse.append("Server: " + client.getInetAddress() + "\r\n");
        clientResponse.append(sdf.format(Calendar.getInstance().getTime()));
        clientResponse.append("\r\nContent-Type: text/html\r\n");
        clientResponse.append("Content-Length:" + content.getBytes().length + "\r\n");
        clientResponse.append("\r\n" + content + "\r\n\r\n");

        clientOutput.write(clientResponse.toString().getBytes());
        clientOutput.flush();
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}