import java.net.*;


/*Status details for later:

200 Ok if successful,
401 unauthorized access,
404 if file not found;

 */

public class server {

    private Socket socket;
    private String directory;

    public server(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
    }
}

