import tcpclient.TCPClient;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MyRunnable implements Runnable {

    private Socket socket;
    private StringBuilder response = new StringBuilder();
    private boolean shutdown = false;
    private String hostname = "";
    private Integer limit = null;
    private Integer timeout = null;
    private Integer port = null;
    private byte[] webdata = new byte[0];
    private final byte[] mem = new byte[1024];
    private final String Bad_Request = "HTTP/1.1 400 Bad Request\r\n";
    private final String Not_Found = "HTTP/1.1 404 Not Found\r\n";
    private String[] parameters;

    public MyRunnable(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            OutputStream output = socket.getOutputStream();
            socket.getInputStream().read(mem);
            String url = new String(mem, StandardCharsets.UTF_8);
            try {
                parameters = url.split("[?\\&\\=\\ ]"); // split method using ?, &, = and space.

                if (!parameters[0].equals("GET") || !url.contains("HTTP/1.1")) { // check if the request is valid.
                    response.append(Bad_Request);
                    throw new Exception("Bad Request");
                }
                if (parameters.length < 3) {
                    response.append(Bad_Request);
                    throw new Exception("Bad request");
                }
                if (parameters[1].equals("/ask")) {
                    for (int i = 0; i < parameters.length; i++) {

                        if (parameters[i].equals("hostname")) {
                            i++;
                            hostname = parameters[i];
                        } else if (parameters[i].equals("limit")) {
                            i++;
                            if (parameters[i].equals(""))
                                limit = null;
                            try {
                                int integer = Integer.parseInt(parameters[i]);

                            } catch (NumberFormatException e) {
                                response.append(Bad_Request);
                            }
                            limit = Integer.parseInt(parameters[i]);
                        } else if (parameters[i].equals("timeout")) {
                            i++;
                            if (parameters[i].equals(""))
                                timeout = null;

                            try {
                                int integer = Integer.parseInt(parameters[i]);

                            } catch (NumberFormatException e) {
                                response.append(Bad_Request);
                            }

                            timeout = Integer.parseInt(parameters[i]);

                        } else if (parameters[i].equals("shutdown"))
                            shutdown = true;

                        else if (parameters[i].equals("port")) {
                            i++;
                            if (parameters[i].equals(""))
                                port = null;

                            try {
                                int integer = Integer.parseInt(parameters[i]);

                            } catch (NumberFormatException e) {
                                response.append(Bad_Request);
                            }

                            port = Integer.parseInt(parameters[i]);

                        } else if (parameters[i].equals("string")) {
                            i++;
                            webdata = parameters[i].getBytes();
                        }
                    }

                    if (hostname.equals("") || port == null || port == 0) {
                        response.append(Bad_Request);
                        throw new Exception("Bad Request");
                    }

                    try {
                        response.append("HTTP/1.1 200 OK\r\n\r\n");
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        response.append(new String(tcpClient.askServer(hostname, port, webdata)));

                    } catch (Exception e) {
                        response = new StringBuilder();
                        response.append(Not_Found);
                        throw new Exception("Not Found");
                    }

                } else {
                    response.append(Not_Found);
                    throw new Exception("Not found");
                }

                output.write(response.toString().getBytes());
                socket.close();

            } catch (Exception e) {
                output.write(response.toString().getBytes());
                e.printStackTrace();
                socket.close();
            }
        } catch (IOException e) {
        }
    }
}