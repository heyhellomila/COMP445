import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class httpfs extends server {

    public enum RequestType {
        GET,
        POST
    }

    private static final String DEFAULT_DIRECTORY = System.getProperty("user.dir");
    private static String directory;
    private static boolean vPresent = false;
    private static boolean dPresent = false;
    private static boolean pPresent = false;


    public httpfs(int port, boolean debug, String directory) {
        super(port, debug);
        httpfs.directory = directory;
    }
    public httpfs(int port, boolean debug) {
        this(port, debug, DEFAULT_DIRECTORY);
    }
    public httpfs(int port) {
        this(port, false, DEFAULT_DIRECTORY);
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        boolean validated = true;

        httpfs fileServer;
        directory = DEFAULT_DIRECTORY;

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
                } catch (Exception e) {
                    directory = DEFAULT_DIRECTORY;
                    System.out.println("The directory name is invalid, going to default directory");
                }
            } else if (args[i].equals("-p")) {
                if (pPresent)
                    validated = false;
                pPresent = true;

                try {
                    port = Integer.parseInt(args[i + 1]);
                } catch (Exception e) {
                    System.out.println("The port value is not valid. Switched by default to port " + DEFAULT_PORT);
                }
            }
            if (!validated) {
                System.out.println("Not validated error.");
            }
        }

        fileServer = new httpfs(port, vPresent, directory);

        fileServer.run();
    }

    @Override
    public void handleGET(Socket client, String request) throws IOException {
        String returnCode = httpResponse.OK_200;
        StringBuilder content = new StringBuilder();
        String baseDir = directory;
        String path = directory + request.split("\r\n")[0].split(" ")[1].replace("/", "\\");

        File file = new File(path);
        File root = new File(baseDir);

        if (!httpLibrary.isSubDirectory(root, file.getParentFile())) {
            if (isDebug())
                System.out.println(httpResponse.FORBIDDEN_ACCESS_ATTEMPT);
            sendResponse(client, httpResponse.FORBIDDEN_403, httpResponse.ACCESS_DENIED);
            return;
        }

        try {
            if (file.isDirectory()) {
                String[] directoryContents = file.list();
                if (isDebug())
                    System.out.println(httpResponse.LIST_OF_FILE);
                assert directoryContents != null;
                for (String directoryContent : directoryContents) {
                    content.append(directoryContent).append("\r\n");
                    if (isDebug())
                        System.out.println(directoryContent);
                }
            } else if (file.isFile()) {

                content = new StringBuilder(httpLibrary.readFile(file));
                if (isDebug())
                    System.out.println("File contents: \n" + content);

            } else if (!file.exists()) {
                content = new StringBuilder(httpResponse.FILE_NOT_FOUND);
                returnCode = httpResponse.NOT_FOUND_404;
            } else {
                content = new StringBuilder(httpResponse.SERVER_ERROR);
                returnCode = httpResponse.INTERNAL_SERVER_ERROR_500;
            }
        } catch (FileNotFoundException fnfe) {
            content = new StringBuilder(httpResponse.FILE_NOT_FOUND);
            returnCode = httpResponse.NOT_FOUND_404;
        } catch (Exception fnfe) {
            content = new StringBuilder(httpResponse.SERVER_ERROR);
            returnCode = httpResponse.INTERNAL_SERVER_ERROR_500;
        }
        sendResponse(client, returnCode, content.toString());
    }

    @Override
    public void handlePOST(Socket client, String request) throws IOException {
        String content = "";
        String returnCode = httpResponse.CREATED_201;
        String path = directory + request.split("\r\n")[0].split(" ")[1].replace("/", "\\");

        String baseDir = directory;
        File file = new File(path);
        File root = new File(baseDir);
        File folder = file.getParentFile();

        if (!httpLibrary.isSubDirectory(root, file)) {
            if (isDebug())
                System.out.println(httpResponse.FORBIDDEN_ACCESS_ATTEMPT);
            sendResponse(client,  httpResponse.FORBIDDEN_403, httpResponse.ACCESS_DENIED);
            return;
        }

        if (!folder.isDirectory()) {
            if (!folder.mkdirs()) {
                returnCode = httpResponse.INTERNAL_SERVER_ERROR_500;
                content = httpResponse.SERVER_ERROR;
                if (isDebug())
                    System.out.println(httpResponse.SERVER_ERROR);
            }
        } else if (!folder.canWrite()) {
            returnCode = httpResponse.FORBIDDEN_403;
            content = httpResponse.ACCESS_DENIED;
            if (isDebug())
                System.out.println(httpResponse.ACCESS_DENIED);
        }

        if (!file.exists()) {
            if (!file.createNewFile()) {
                returnCode = httpResponse.INTERNAL_SERVER_ERROR_500;
                content = httpResponse.FILE_FAILED;
                if (isDebug())
                    System.out.println(httpResponse.FILE_FAILED);
            }
        }

        if (!file.canWrite()) {
            returnCode = httpResponse.FORBIDDEN_403;
            content = httpResponse.ACCESS_DENIED;
            if (isDebug())
                System.out.println(httpResponse.ACCESS_DENIED);
        } else {
            try {
                FileWriter fw = new FileWriter(file.getAbsolutePath());
                fw.write(getRequestBody(client, request));
                fw.close();
            } catch (IOException e) {
                returnCode = httpResponse.FORBIDDEN_403;
                content = httpResponse.ACCESS_DENIED;
                if (isDebug()) {
                    System.out.println(httpResponse.ACCESS_DENIED);
                    e.printStackTrace();
                } else {
                    System.out.println(httpResponse.UNKNOWN);
                }
            }
        }


        sendResponse(client, returnCode, content);
    }

    public String getRequestBody(Socket client, String request) throws IOException {
        String body = "";
        String[] requestsLines = request.split("\r\n\r\n");
        body = requestsLines[1];
        System.out.println(body);
     
        return body;
    }
}