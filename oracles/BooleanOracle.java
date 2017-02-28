/**
 *   Currently this oracle only deals with fields of length one.  It might be
 * necessary in the future to extend this maximum length to account for
 * whitespace, but at the present it seems to make sense that since only one
 * character will be used the field length will also be of length one.
 */

package oracles;

import supportMethods.Setup;
import supportObjects.NameValuePair;


public class BooleanOracle implements Oracle  {
  private char[] v;  //The valid characters for a boolean value.
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private String on;


  public BooleanOracle( )  {  }  /*  Constructor  */


  /**
   * @return The lower case version of the only non-whitespace character in the
   *         sequence.  If there is more than one whitespace character then 0 is
   *         returned.
   */
  private char trim( char[] array, int sp, int len )  {
    char ret = 0;
    int i = sp,
        j = ( sp + len - 1 );

    while( ( i < ( sp + len ) ) && Character.isWhitespace( array[i] ) )  {
      i++;
    }

    while( ( j >= sp ) && Character.isWhitespace( array[j] ) )  {
      j--;
    }

    if( i == j )  {
      ret = Character.toLowerCase( array[i] );
    }

    return( ret );
  }  /*  private char[] trim( char[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i = 0;
    NameValuePair nvp;
    String n;

    //Linear search, the entries are thus ordered by perceived frequency.
//    v = new char[6];
//    v[i++] = 'n';
//    v[i++] = 'y';
//    v[i++] = '0';
//    v[i++] = '1';
//    v[i++] = 'f';
//    v[i] =   't';

    v = new char[2];  //Currently only test 'Y' and 'N'.
    v[i++] = 'n';
    v[i] =   'y';

    ml = 1;
    gid = 16;
    mp = 0.95;
    mbp = 0.10;
    on = "boolean";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        n = nvp.getName( );
        if( n.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "maximumlength" ) )  {
          try  {
            ml = Integer.parseInt( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            ml = 1;
          }
        }
        else if( n.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.95;
          }
        }
        else if( n.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 0.10;
          }
        }
        else if( n.equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 16;
          }
        }
      }
    }
  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] array )  {
    boolean ret = false;
    char ch = trim( array, 0, array.length );
    int i;

    if( ch != 0 )  {
      i = 0;

      while( !ret && ( i < v.length ) )  {
        if( ch != v[i] )  {
          i++;
        }
        else  {
          ret = true;
        }
      }
    }

    return( ret );
  }  /*  public boolean isValid( char[] )  */


  public boolean isValid( char[] array, int beginIndex, int length )  {
    boolean ret = false;
    char ch;
    int i;

    if( ( beginIndex >= 0 ) && ( length == 1 ) &&
        ( ( beginIndex + length ) <= array.length ) )  {

      ch = trim( array, beginIndex, length );
      i = 0;

      while( !ret && ( i < v.length ) )  {
        if( ch != v[i] )  {
          i++;
        }
        else  {
          ret = true;
        }
      }
    }
    
    return( ret );
  }  /*  public boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    boolean ret = false;

    if( field.length( ) == 1 )  {
      ret = isValid( field.toCharArray( ) );
    }

    return( ret );
  }  /*  public boolean isValid( String )  */


  public int getMaxLength( )  {
    return( ml );
  }  /*  public int getMaxLength( )  */


  public int getGrouping( )  {
    return( gid );
  }  /*  public int getGrouping( )  */


  /**
   *   The minimum number of records, as a percentage, that must have a valid
   * field for a potential field location to be created.
   *   For Booleans the value must be high, close to 100%, but since only 100
   * records are examined during testing and for fully delimited files 5 are
   * thrown away reduce, for now, the threshold is set to 92%.
   */
  public double getMinPercentage( )  {
    return( mp );
  }  /*  public double getMinPercentage( )  */


  public double getMaxBlankPercentage( )  {
    return( mbp );
  }  /*  public double getMaxBlankPercentage( )  */


  public double matchHeader( String label )  {
    return( 0.0 );
  }  /*  public double matchHeader( String )  */


  public String getName( )  {
    return( on );
  }  /*  public String getName( )  */

}  /*  public class Boolean implements Oracle  */
