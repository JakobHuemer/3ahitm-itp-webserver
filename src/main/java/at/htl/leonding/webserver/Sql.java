package at.htl.leonding.webserver;

import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Objects;

public class Sql {

    private static final String INIT_SQL_PATH = "/init.sql";

    public static void init() {
        try ( var conn = Database.getConnection();
              var reader = new BufferedReader( new InputStreamReader(
                      Objects.requireNonNull( Sql.class.getResourceAsStream( INIT_SQL_PATH ) ) ) ) ) {

            ScriptRunner scriptRunner = new ScriptRunner( conn );
            scriptRunner.setLogWriter( null );

            scriptRunner.runScript( reader );

        } catch ( SQLException | IOException e ) {
            throw new RuntimeException( e );
        }
    }

    public static Greeting getGreeting( long id ) {

        String sql = """
                select id, greeting
                from app.greeting
                where id = ?
                """;

        try ( var conn = Database.getConnection();
              var stmt = conn.prepareStatement( sql ) ) {

            stmt.setLong( 1, id );

            try ( var rs = stmt.executeQuery() ) {
                if ( rs.next() ) {
                    return new Greeting( rs.getLong( "id" ), rs.getString( "greeting" ) );
                }
                return null;
            }
        } catch ( SQLException e ) {
            throw new RuntimeException( e );
        }
    }
}
