import java.net.*;
import java.io.*;

public class ConcHTTPAsk {
    public static void main(String[] args) throws IOException {

        ServerSocket serversocket = new ServerSocket(Integer.parseInt(args[0]));

        while(true){
            Socket socket = serversocket.accept();
            Runnable runnable = new MyRunnable(socket);
            new Thread(runnable).start();
        }
    }
}
