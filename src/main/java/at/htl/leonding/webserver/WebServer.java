
package at.htl.leonding.webserver;

import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;


public class WebServer {

    private final static int PORT = 8080;
    
    public static void main( String[] args ) throws IOException {
        ServerSocket socket = ServerSocketFactory.getDefault().createServerSocket( PORT );

        while ( true ) {
            Socket client = socket.accept();
            new Thread( () -> handleConnection( client ) ).start();
        }
    }

    private static void handleConnection( Socket client ) {
        try ( client ) {

            var outputStream = client.getOutputStream();
            var sc = new Scanner( client.getInputStream() );


            String requestLine = sc.nextLine();
            String[] requestParts = requestLine.split( " " );
            String method = requestParts[ 0 ];
            String path = requestParts[ 1 ];

            if ( !"GET".equals( method ) || !( path.equals( "/" ) || path.equals( "/index.html" ) ) ) {
                System.out.println( "Unsupported request: " + requestLine );
                return;
            }

            System.out.println( "Done Getting REQUEST" );

            System.out.println("Sending HTTP Header");

            String responseHeader = """
                    HTTP/1.1 200 OK
                    Content-Type: text/html; charset=UTF-8
                    Server: Custom Java Server (Java 21)
                    Connection: close
                    
                    """;

            outputStream.write( responseHeader.getBytes() );

            try (InputStream is = WebServer.class.getResourceAsStream("/index.html")) {
                if (is == null) {
                    throw new RuntimeException("Could not find index.html in classpath");
                }
                System.out.println("Sending HTML");
                outputStream.write(is.readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
