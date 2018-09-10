package poke;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import org.realityforge.keycloak.client.authfilter.Keycloak;
import org.realityforge.keycloak.client.authfilter.KeycloakConfig;

public class Poke
{
  private final String _keycloakServerURL;
  private final String _keycloakRealm;
  private final String _keycloakClient;
  private final String _keycloakUsername;
  private final String _keycloakPassword;
  private final String _url;

  Keycloak _keycloak;
  KeycloakConfig _keycloakConfig;

  public Poke( final String keycloakServerURL,
               final String keycloakRealm,
               final String keycloakClient,
               final String keycloakUsername,
               final String keycloakPassword,
               final String url )
  {
    _keycloakServerURL = keycloakServerURL;
    _keycloakRealm = keycloakRealm;
    _keycloakClient = keycloakClient;
    _keycloakUsername = keycloakUsername;
    _keycloakPassword = keycloakPassword;
    _url = url;
    _keycloakConfig = KeycloakConfig.createPasswordConfig( _keycloakServerURL,
                                                           _keycloakRealm,
                                                           _keycloakClient,
                                                           _keycloakUsername,
                                                           _keycloakPassword );
    _keycloak = new Keycloak( _keycloakConfig );
  }

  public static void main( final String args[] )
  {
    if ( args.length != 6 )
    {
      System.out.println( "syntax: Poke [keycloak_url] [realm] [client] [username] [password] [url]" );

      return;
    }

    new Poke( args[ 0 ], args[ 1 ], args[ 2 ], args[ 3 ], args[ 4 ], args[ 5 ] ).wget();
  }

  void wget()
  {
    try
    {
      final URL url = new URL( _url );
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod( "GET" );
      connection.setRequestProperty( "Authorization", "Bearer " + _keycloak.getAccessTokenString() );
      if ( connection.getResponseCode() != 200 && connection.getResponseCode() != 303 )
      {
        System.out.println( "Failed to connect: (" +
                            connection.getResponseCode() +
                            "): " +
                            connection.getResponseMessage() );
        return;
      }
      final BufferedReader in = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
      String inputLine;
      final StringBuilder content = new StringBuilder();
      while ( ( inputLine = in.readLine() ) != null )
      {
        content.append( inputLine ).append( "\n" );
      }
      in.close();
      connection.disconnect();
      Arrays.stream( content.toString().split( "\n" ) ).forEach( System.out::println );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }
}
