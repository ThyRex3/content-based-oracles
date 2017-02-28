package dataStructures;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class Lookup  {
  private int max,  //The maximum length of the input entries.
              min;  //The minimum length of the input entries.
  private String[] n;


  public Lookup( )  {  }  /*  Constructor  */


  private boolean screen( String s )  {
    boolean ret = true;

    if( ( s.length( ) < min ) || ( s.length( ) > max ) )  {
      ret = false;
    }

    return( ret );
  }  /*  private boolean screen( String )  */


  public int minLength( )  {
    return( min );
  }  /*  public int minLength( )  */


  public int maxLength( )  {
    return( max );
  }  /*  public int maxLength( )  */


  public String getEntry( int index )  {
    String ret = null;

    if( ( n != null ) && ( index >= 0 ) && ( index < n.length ) )  {
      ret = n[index];
    }

    return( ret );
  }  /*  public String getEntry( int )  */


  /**
   *   Reinitializes the instance of this object with a new set of data.  This
   * will allow for different database sizes.
   */
  public void setDataSource( int count, String fileName )  {
    int i = 0,
        len;
    String s;
    BufferedReader inf;

    max = Integer.MIN_VALUE;
    min = Integer.MAX_VALUE;
    n = new String[count];

    try  {
      inf = new BufferedReader( new InputStreamReader(
                      bean.LayoutBean.class.getResourceAsStream( fileName ) ) );

      while( ( i < count ) && ( ( s = inf.readLine( ) ) != null ) )  {
        n[i++] = s.toLowerCase( );

        if( ( len = s.length( ) ) < min )  {
          min = len;
        }
        if( len > max )  {
          max = len;
        }
      }

    }
    catch( IOException e )  {
      System.out.println( "oracles - Lookup error 2: IOException.  " +
                          e.getMessage( ) );

      System.exit( 1 );
    }
  }  /*  public void setDataSource( int, String )  */


  /**
   *   A binary search on the lookup table.
   */
  public int search( String value )  {
    boolean s = true;  //Searching.
    int beginning = 0,
        middle = -1,
        end;

    //Only perform the search if there is a chance of the entry occurring in the
    //  database.
    if( ( n != null ) && screen( value ) )  {
      end = ( n.length - 1 );

      while( s && ( beginning <= end ) )  {
        middle = ( ( beginning + end ) / 2 );

        if( value.equals( n[middle] ) )  {
          s = false;
        }
        else if( value.compareTo( n[middle] ) < 0 )  {
          end = ( middle - 1 );
        }
        else  {
          beginning = ( middle + 1 );
        }
      }
    }

    if( s )  {  //Did not find VALUE.
      middle = -1;
    }

    return( middle );
  }  /*  private int search( int )  */

}  /*  public class Lookup1  */
