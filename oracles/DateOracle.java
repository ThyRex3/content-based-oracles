/**
 *   This oracle is still being tested and as such may not work entirely as
 * expected.  Before using be aware of this fact.
 */

package oracles;


public class DateOracle implements Oracle  {
  private char[] v;  //The valid characters for a boolean value.
  private String on;


  public DateOracle( )  {  }  /*  Constructor  */


  /**
   * @param LTET - the upper bound on the largest value.
   * @return The largest value stored in the parameter ARRAY that is less than
   *         or equal to the parameter LTET.  If no values are less than
   *         LESSTHAN then -1 is returned.
   */
  public int largestLessThan( int ltet, int[] array )  {
    int c = 0,
        i = 0,
        ret = -1;

    for( i = 0; i < array.length; i++ )  {
      if( array[i] == ltet )  {
        c++;
      }
    }

    if( c < 2 )  {
      for( i = 0; i < array.length; i++ )  {
        if( ( array[i] > ret ) && ( array[i] < ltet ) )  {
          ret = array[i];
        }
      }
    }
    else  {
      ret = ltet;
    }

    return( ret );
  }  /*  public int largestLessThan( int, int[] )  */


  public boolean match( int day, int month )  {
    boolean ret;

    switch( month )  {
      case 1:  //January
      case 3:  //March
      case 5:  //May
      case 7:  //July
      case 8:  //August
      case 10:  //October
      case 12:  //December
        ret = ( day < 31 );
        break;

      case 4:  //April
      case 6:  //June
      case 9:  //September
      case 11:  //November
        ret = ( day < 30 );
        break;

      case 2:  //February, to keep it simple allow for day 29.
        ret = ( day < 29 );
        break;

      default:
        ret = false;
    }

    return( ret );
  }  /*  public boolean match( int, int )  */


  public int convert( char[] array, int sp, int len )  {
    int i,
        mul = 1,
        ret = 0;

    for( i = ( sp + len - 1 ); i >= sp; i-- )  {
      ret += ( ( array[i] - 48 ) * mul );

      mul *= 10;
    }

    return( ret );
  }  /*  public int convert( char[], int, int )  */


  /**
   *   Has the side effect of trimming leading and trailing characters.
   */
  private char[] screen( char[] array, int sp, int len )  {
    boolean b = true;
    char[] holder = new char[len],
        ret = null;
    int i = sp,
        j,
        ep = ( sp + len - 1 ),
        ind = 0;

    while( ( i < ( sp + len ) ) && Character.isWhitespace( array[i] ) )  {
      i++;
    }
    while( ( ep >= sp ) && Character.isWhitespace( array[ep] ) )  {
      ep--;
    }

    while( b && ( i <= ep ) )  {
      if( !( b = Character.isDigit( array[i] ) ) )  {
        j = 0;

        while( !b && ( j < v.length ) )  {
          b = ( array[i] == v[j++] );
        }
      }

      if( b )  {
        holder[ind++] = array[i];
      }

      i++;
    }

    if( ind > 0 )  {
      ret = new char[ind];

      System.arraycopy( holder, 0, ret, 0, ind );
    }

    return( ret );
  }  /*  private boolean screen( char[], int, int )  */


  public boolean test( int[] dateParts )  {
    boolean ret = false;
    int d,
        m;

    if( largestLessThan( 3000, dateParts ) > 999 )  {
      d = largestLessThan( 31, dateParts );
      m = largestLessThan( d, dateParts );

//Debug statement.
//System.out.println( "  In test(), Y = " + largestLessThan( 3000, dateParts ) + ";  D = " + d + ";  M = " + m + "." );
//End debug statement.
      if( !( ret = match( d, m ) ) )  {
        if( d <= 12 )  {
          ret = match( m, d );
        }
      }
    }

    return( ret );
  }  /*  public boolean test( int[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i = 0;

    v = new char[2];
    v[i++] = '-';
    v[i] =   '/';

    on = "date";
  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] array )  {
    return( isValid( array, 0, array.length ) );
  }  /*  public boolean isValid( char[] )  */


  public boolean isValid( char[] array, int sp, int len )  {
    boolean s,  //Searching.
            ret = false,
            svc;  //Searching for a valid character.
    char ch = 'a';  //A default value, should not be contained within V[].
    int i,
        j;
    int[] p = new int[3];  //Hold the parts of the date if separated.

    if( ( sp >= 0 ) && ( ( sp + len ) <= array.length ) )  {
      if( ( array = screen( array, sp, len ) ) != null )  {
//Debug statement.
//System.out.println( "  Past screen() with value |" + ( new String( array ) ) + "|." );
//End debug statement.
        s = true;
        i = 0;

        //Search for valid control characters such as '-' or '/' within ARRAY.
        while( s && ( i < array.length ) )  {
          svc = true;
          j = 0;

          //Test the current character against all possible valid characters.
          while( svc && ( j < v.length ) )  {
            if( !( array[i] == v[j] ) )  {
              j++;
            }
            else  {
              svc = false;
            }
          }

          if( svc )  {  //Not a valid, non-digit, character; increment count.
            i++;
          }
          else  {
            ch = v[j];
            s = false;
          }
        }

        if( !s )  {  //A valid, non-digit, character was found.
//Debug statement.
//System.out.println( "  Found a control character |" + ch + "|." );
//End debug statement.
          p[0] = convert( array, 0, i++ );

          j = i;
          while( ( i < array.length ) && ( array[i++] != ch ) );

          if( i < array.length )  {
            p[1] = convert( array, j, ( ( i - 1 ) - j ) );
            p[2] = convert( array, i, ( array.length - i ) );

//Debug statement.
//System.out.println( "  With control character |" + ch + "| the values are..." );
//for( int z = 0; z < p.length; z++ )  {
//System.out.println( "    " + p[z] );
//}
//End debug statement.
            ret = test( p );
          }
        }
        else  {  //No control characters such as '-' or '/'.
//Debug statement.
//System.out.println( "  Did not find a control character." );
//End debug statement.
          if( array.length == 8 )  {
            p[0] = convert( array, 0, 2 );  //DDMMYYYY or MMDDYYYY
            p[1] = convert( array, 2, 2 );
            p[2] = convert( array, 4, 4 );

//Debug statement.
//System.out.println( "  Without a control character the values are..." );
//for( int z = 0; z < p.length; z++ )  {
//System.out.println( "    " + p[z] );
//}
//End debug statement.
            if( !( ret = test( p ) ) )  {
              p[0] = convert( array, 0, 4 );  //YYYYDDMM or YYYYMMDD
              p[1] = convert( array, 4, 2 );
              p[2] = convert( array, 6, 2 );

//Debug statement.
//System.out.println( "  (2nd try)  Without a control character the values are..." );
//for( int z = 0; z < p.length; z++ )  {
//System.out.println( "    " + p[z] );
//}
//End debug statement.
              ret = test( p );
            }
          }
        }
      }
    }

    return( ret );
  }  /*  public boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    return( isValid( field.toCharArray( ), 0, field.length( ) ) );
  }  /*  public boolean isValid( String )  */


  public int getMaxLength( )  {
    return( 15 );
  }  /*  public int getMaxLength( )  */


  public int getGrouping( )  {
    return( 7 );  //Might as well throw it in with the Boolean oracle's group.
  }  /*  public int getGrouping( )  */


  /**
   *   The minimum number of records, as a percentage, that must have a valid
   * field for a potential field location to be created.
   */
  public double getMinPercentage( )  {
    return( 0.70 );
  }  /*  public double getMinPercentage( )  */


  public double getMaxBlankPercentage( )  {
    return( 0.75 );
  }  /*  public double getMaxBlankPercentage( )  */


  public double matchHeader( String label )  {
    return( 0.0 );
  }  /*  public double matchHeader( String )  */


  public String getName( )  {
    return( on );
  }  /*  public String getName( )  */

}  /*  public class DateOracle implements Oracle  */
