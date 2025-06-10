
package at.htl.leonding.webserver;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;


public class WebServer {

    private final static int PORT = 8080;

    public static void main( String[] args ) throws IOException {
        ServerSocket socket = ServerSocketFactory.getDefault().createServerSocket( PORT );

        Sql.init();

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

            if ( !method.equals( "GET" ) ) {
                outputStream.write( ( "HTTP/1.1 405 Method Not Allowed\n"
                        + "Content-Type: text/plain\n"
                        + "Content-Length: 23\n"
                        + "Allow: GET, POST\n\n"
                        + "Method Not Supported" ).getBytes() );
                return;
            }

            if ( requestParts.length < 3 ) {
                outputStream.write( ( "HTTP/1.1 400 Bad Request\n"
                        + "Content-Type: text/plain\n"
                        + "Content-Length: 15\n\n"
                        + "Bad Request" ).getBytes() );
                return;
            }

            String path = requestParts[ 1 ];

            // match path for /greeting/digit

            if ( path.equals( "/greeting" ) || path.equals( "/greeting/" ) ) {

                var greetings = Sql.getAll();

                // return in html table format

                StringBuilder sb = new StringBuilder();

                sb.append( "<html><body><table>" );
                sb.append( "<tr><th>ID</th><th>Greeting</th></tr>" );
                for ( var greeting : greetings ) {
                    sb.append( "<tr><td>" )
                            .append( greeting.id() )
                            .append( "</td><td>" )
                            .append( greeting.greeting() )
                            .append( "</td></tr>" );
                }
                sb.append( "</table></body></html>" );
                String responseBody = sb.toString();

                String response = "HTTP/1.1 200 OK\n"
                        + "Content-Type: text/html\n"
                        + "Content-Length: " + responseBody.length() + "\n\n"
                        + responseBody;

                outputStream.write( response.getBytes() );
                outputStream.flush();
                return;
            }

            if ( !path.matches( "/greeting/\\d+" ) ) {
                outputStream.write( ( """
                        HTTP/1.1 404 Not Found
                        Content-Type: text/plain
                        Content-Length: 13
                        
                        Not Found""" ).getBytes() );
                return;
            }

            // extract digit from path

            long id = Long.parseLong( path.substring( 10 ) );

            var greeting = Sql.getGreeting( id );

            // return greeting

            if ( greeting == null ) {
                outputStream.write( ( """
                        HTTP/1.1 404 Not Found
                        Content-Type: text/plain
                        Content-Length: 13
                        
                        Not Found""" ).getBytes() );
                return;
            }

            String responseBody = greeting.greeting();
            String response = "HTTP/1.1 200 OK\n"
                    + "Content-Type: text/plain\n"
                    + "Content-Length: " + responseBody.length() + "\n\n"
                    + responseBody;

            outputStream.write( response.getBytes() );
            outputStream.flush();

            System.out.println(
                    "Answered request for "
                            + path
                            + " with greeting: "
                            + greeting.greeting()
            );


        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
