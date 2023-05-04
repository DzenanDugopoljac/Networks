package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {

    private boolean shutdown;
    private Integer timeout;
    private Integer limit;

    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        this.shutdown = shutdown;
        this.timeout = timeout;
        this.limit = limit;
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

        byte[] temp_buffer = new byte[1024];                         // pre allocating a small buffer
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();  // a buffer that can grow dynamically
        int webdata = 0;

        Socket clientSocket = new Socket(hostname, port);   // open connection to server "hostname" at int port.
        clientSocket.getOutputStream().write(toServerBytes);
        InputStream input = clientSocket.getInputStream();  // returns an inputstream from the socket and store it in "input"


        if(shutdown)
            clientSocket.shutdownOutput();                  // if shutdown == true, then the TCP client shutdown.

        try {
            if (timeout != null)
                clientSocket.setSoTimeout(timeout);        // if the timeout is not 0 then the timeout is set i.e for how
            // long it will wait for the data.
            if (limit != null) {
                byte[] limit_buffer;
                limit_buffer = new byte[limit];
                int count = 0;

                while (true) {
                    webdata = input.read(limit_buffer);
                    count++;
                    if (webdata == -1 || count >= limit_buffer.length)
                        break;
                    else
                        buffer.write(limit_buffer, 0, webdata);
                    count += limit - count;
                }
            } else if (limit == null) {
                while (true) {
                    webdata = input.read(temp_buffer);
                    if (webdata == -1)
                        break;
                    else
                        buffer.write(temp_buffer, 0, webdata);
                }
            }
        }
        catch (SocketTimeoutException e){}

        clientSocket.close();
        return buffer.toByteArray();
    }
}