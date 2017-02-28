package dataStructures;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class IntegerLookup  {
  private int max,  //The maximum length of the input entries.
              min;  //The minimum length of the input entries.
  private int[] n;  //The lookup table ( database ).


  public IntegerLookup( )  {  }  /*  Constructor  */


  private boolean screen( String s )  {
    return( !( ( s.length( ) < min ) || ( s.length( ) > max ) ) );
  }  /*  private boolean screen( String )  */


  public int minLength( )  {
    return( min );
  }  /*  public int minLength( )  */


  public int maxLength( )  {
    return( max );
  }  /*  public int maxLength( )  */


  public Integer getEntry( int index )  {
    Integer ret = null;

    if( ( n != null ) && ( index >= 0 ) && ( index < n.length ) )  {
      ret = n[index];
    }

    return( ret );
  }  /*  public Integer getEntry( int )  */


  public int search( String s )  {
    boolean b = true;  //Searching.
    int beginning = 0,
        middle = -1,
        end,
        value;

    try  {
      if( ( n != null ) && screen( s ) )  {
        value = Integer.parseInt( s );
        end = ( n.length - 1 );

        while( b && ( beginning <= end ) )  {
          middle = ( ( beginning + end ) / 2 );

          if( value == n[middle] )  {
            b = false;
          }
          else if( value < n[middle] )  {
            end = ( middle - 1 );
          }
          else  {
            beginning = ( middle + 1 );
          }
        }
      }

      if( b )  {
        middle = -1;
      }
    }catch( NumberFormatException e )  {  }

    return( middle );
  }  /*  private int search( int )  */


  public void setDataSource( int count, String fileName )  {
    int i = 0,
        len;
    String s;
    BufferedReader inf;

    max = Integer.MIN_VALUE;
    min = Integer.MAX_VALUE;

    try  {
      n = new int[count];
      inf = new BufferedReader( new InputStreamReader(
                      bean.LayoutBean.class.getResourceAsStream( fileName ) ) );

      while( ( s = inf.readLine( ) ) != null )  {
        n[i++] = Integer.parseInt( s );

        if( ( len = s.length( ) ) < min )  {
          min = len;
        }
        if( len > max )  {
          max = len;
        }
      }

      inf.close( );
    }
    catch( IOException e )  {
      System.out.println( "oracles - IntegerLookup error 1: IOException.  " +
                          e.getMessage( ) );
      System.exit( 1 );
    }
    catch( NumberFormatException e )  {
      System.out.println( "oracles - IntegerLookup error 2: " + 
                          "NumberFormatException.  " + e.getMessage( ) );
      System.exit( 1 );
    }
  }  /*  public void reinitialize( int, String )  */

}  /*  public class IntegerLookup  */
