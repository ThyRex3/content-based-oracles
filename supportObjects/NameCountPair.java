package supportObjects;

import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class NameCountPair  {
  private int[] c;  //The count fields of the {name, count} pair.
  private String[] n;  //The name fields of the {name, count} pair.


  public NameCountPair( String fileName )  {
    int i;
    String s;
    String[] line;
    BufferedReader inf;
    ArrayList<Integer> lc = new ArrayList<Integer>( );
    ArrayList<String> fn = new ArrayList<String>( );

    try  {
      inf = new BufferedReader( new InputStreamReader(
                      bean.LayoutBean.class.getResourceAsStream( fileName ) ) );

      while( ( s = inf.readLine( ) ) != null )  {
        line = s.split( ":" );
        fn.add( line[0] );
        lc.add( Integer.parseInt( line[1] ) );
      }

      inf.close( );

      c = new int[lc.size( )];
      n = new String[fn.size( )];
      for( i = 0; i < c.length; i++ )  {
        c[i] = lc.get( i );
        n[i] = fn.get( i );
      }
    }
    catch( IOException e )  {
      System.out.println( "supportObjects - NameCountPair error 1: " +
                          "IOException.  " + e.getMessage( ) );

      System.exit( 1 );
    }
    catch( NumberFormatException e )  {
      System.out.println( "supportObjects - NameCountPair error 2: " +
                          "IOException.  " + e.getMessage( ) );

      System.exit( 1 );
    }
  }  /*  Constructor  */


  public int getLineCount( String fileName )  {
    boolean found = false;
    int beginning = 0,
        middle = 0,
        end = ( c.length - 1 ),
        ret = -1;

    while( !found && ( beginning <= end ) )  {
      middle = ( ( beginning + end ) / 2 );

      if( fileName.equals( n[middle] ) )  {
        found = true;
      }
      else if( fileName.compareTo( n[middle] ) < 0 )  {
        end = ( middle - 1 );
      }
      else  {
        beginning = ( middle + 1 );
      }
    }

    if( found )  {
      ret = c[middle];
    }

    return( ret );
  }  /*  public static int getLineCount( String )  */


  public String toString( )  {
    int i;
    String ret = "Line counts:";

    for( i = 0; i < ( n.length - 1 ); i++ )  {
      ret = ret.concat( n[i] + ":" + Integer.toString( c[i] ) +
                        System.getProperty( "line.separator" ) );
    }

    return( ret.concat( n[i] + ":" + Integer.toString( c[i] ) ) );
  }  /*  public String toString( )  */

}  /*  public class NameCountPair  */
