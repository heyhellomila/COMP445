import java.net.*;

public class server {

    private Socket socket;
    private String directory;

    public server(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
    }
}

