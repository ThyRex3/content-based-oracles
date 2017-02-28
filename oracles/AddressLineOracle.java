package oracles;

import supportMethods.Setup;
import supportObjects.NameValuePair;


public class AddressLineOracle implements Oracle, AlterDatabase  {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private String on;  //The oracle's name.
  Oracle[] o;  //A reference to the address lines one and two oracles.


  public AddressLineOracle( )  {  }  /*  Constructor  */


  /**
   *   @return The index of the first character of the token preceeding the
   *           current token's position, P.
   */
  private int nextBackward( int p, char[] a )  {
    //Skip any whitespace between the current and preceeding tokens.
    while( ( p >= 0 ) && Character.isWhitespace( a[p] ) )  {
      p--;
    }

    //Skip over the characters of the preceeding token to find the beginning
    //  character.
    while( ( p >= 0 ) && !Character.isWhitespace( a[p] ) )  {
      p--;
    }

    return( p + 1 );  //Return the position of the first character of the token.
  }  /*  private int next( boolean, int, char[] )  */


  /**
   * @return The position of the first whitespace character immediately
   *         following the leftmost token.
   */
/*
  private int endLeftToken( char[] a )  {
    int i = 0;

    while( ( i < a.length ) && Character.isWhitespace( a[i] ) )  {
      i++;
    }

    while( ( i < a.length ) && !Character.isWhitespace( a[i] ) )  {
      i++;
    }

    return( i );
  }  /*  private int endLeftToken( char[] )  */


  /**
   *   Though this is an oracle that will have its database altered, it will
   * have already been accomplished when its supporting oracles reduce their
   * respective databases.  Thus it is a requirement that the address lines one
   * and two oracles be reduced before invoking this oracle.
   */
  public void setMinDatabase( )  {  }  /*  public void setMinDatabase( )  */
  public void setMaxDatabase( )  {  }  /*  public void setMaxDatabase( )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    o = new Oracle[5];
    o[0] = og.getOracle( og.index( "street number" ) );
    o[1] = og.getOracle( og.index( "address line one" ) );
    o[2] = og.getOracle( og.index( "unit designator" ) );
    o[3] = new PostOfficeBoxOracle( );
    o[4] = og.getOracle( og.index( "address line two" ) );

    o[3].initialize( og, null );

    ml = 75;
    gid = 4;
    mp = 0.60;
    mbp = 0.15;
    on = "address line";

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
            ml = 60;
          }
        }
        else if( n.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.60;
          }
        }
        else if( n.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 0.15;
          }
        }
        else if( n.equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 4;
          }
        }
      }
    }
  }  /*  public void initialize( OracleGroup, String[] )  */


  /**
   *   !!Assumption:  address parts are separated by white space!!
   */

  public boolean isValid( char[] array )  {
    boolean ret = o[1].isValid( array ),
            falt;
    int i,
        j;

//Debug statement.
//System.out.println( "1.) AddressLineOne = " + ret + ".  |" + ( new String( array ) ) + "|" );
//End debug statement.
    //If not just an address line one, then attempt to split the string into
    //  address lines one and two.
    if( !ret )  {
      i = nextBackward( ( array.length - 1 ), array );
      j = array.length;
//Debug statement.
//System.out.println( "Calling UD.isValid( )." );
//End debug statement.
      falt = o[2].isValid( array, i, ( j - i ) );

      //Scan the string for an address line two keyword.  This keyword
      //  indicates the point where line one ends and line two begins.
      while( !falt && ( i < j ) )  {
        //Update the end and beginning indices to reflect the position of the
        //  preceeding token.
        j = i;
        i = nextBackward( ( j - 1 ), array );

//Debug statement.
//System.out.println( "Calling UD.isValid( )." );
//End debug statement.
        falt = o[2].isValid( array, i, ( j - i ) );
      }

//Debug statement.
//System.out.println( "2.) AddressLineTwo keyword = " + falt + "." );
//if( falt )  {
//System.out.println( "|" + ( new String( array, i, ( j - i ) ) ) + "|" );
//System.out.println( "" );
//}
//End debug statement.
      //If an line two keyword was found then test the parts separately,
      //  otherwise test the whole line for just an address line one.
      if( falt )  {
        ret = ( o[1].isValid( array, 0, i ) &&
                o[4].isValid( array, i, ( array.length - i ) ) );

//Debug statement.
//System.out.println( "3.) AddressLineOne and AddressLineTwo = " + ret + "." );
//End debug statement.
      }
    }

    return( ret );
  }  /*  public boolean isValid( char[] )  */
/*
  public boolean isValid( char[] array )  {
    boolean ret = o[3].isValid( array ),
            falt;  //Found address line two keyword:  unit designator or PO BOX.
    int i,
        j;

//Debug statement.
//TestAddressLine.wtf( "ENTERING isValid()." );
//End debug statement.
//Debug statement.
//System.out.println( "Current token = |" + new String( array, i, ( j - i ) ) + "|" );
//End debug statement.
    //First test if the address is a PO Box.  If not then run other tests.
    if( !ret )  {
      falt = false;

      i = 0;
      j = endLeftToken( array );

      //First filter any strings not beginning with a street number.
      if( !ret && o[0].isValid( array, 0, j ) )  {
        i = nextBackward( ( array.length - 1 ), array );
        j = array.length;

//Debug statement.
//TestAddressLine.wtf( "  Considering token at --> SP = " + i + ";  LEN = " + ( j - i ) + ".  UD = " + o[2].isValid( array, i, ( j - i ) ) + ";  PO = " + o[3].isValid( array, i, ( j - i ) ) + "." );
//End debug statement.
        //Scan the string for an address line two keyword.  This keyword
        //  indicates the point where line one ends and line two begins.
        while( ( i < j ) && !( falt = ( o[2].isValid( array, i, ( j - i ) ) ||
                                    o[3].isValid( array, i, ( j - i ) ) ) ) )  {

//Debug statement.
//System.out.println( "I = " + i + ";  J = " + j + "." );
//End debug statement.
          //Update the end and beginning indices to reflect the position of the
          //  preceeding token.
          j = i;
          i = nextBackward( ( j - 1 ), array );
//Debug statement.
//TestAddressLine.wtf( "  Considering token at --> SP = " + i + ";  LEN = " + ( j - i ) + ".  UD = " + o[2].isValid( array, i, ( j - i ) ) + ";  PO = " + o[3].isValid( array, i, ( j - i ) ) + "." );
//System.out.println( "Current token = |" + new String( array, i, ( j - i ) ) + "|" );
//End debug statement.
        }
//Debug statement.
//if( falt )  {
//TestAddressLine.wtf( "    Found an address line two keyword." );
//}
//else  {
//TestAddressLine.wtf( "    Did not find an address line two keyword." );
//}
//End debug statement.

        //If an line two keyword was found then test the parts separately,
        //  otherwise test the whole line for just an address line one.
        if( falt )  {
//Debug statement.
//System.out.println( "ARRAY LENGTH = " + array.length + "." );
//System.out.println( "Found ALT keyword = |" + new String( array, i, ( j - i ) ) + "|" );
//System.out.println( o[1].getName( ) + " = |" + ( new String( array, 0, i ) ).trim( ) + "| - " + o[1].isValid( array, 0, i ) + "." );
//System.out.println( o[4].getName( ) + " = |" + ( new String( array, i, ( array.length - i ) ) ).trim( ) + "| - " + o[4].isValid( array, i, ( array.length - i ) ) + "; BeginIndex = " + i + "; Length = " + ( array.length - i ) + "." );
//End debug statement.
          ret = ( o[1].isValid( array, 0, i ) &&
                ( o[4].isValid( array, i, ( array.length - i ) ) ) );

//Debug statement.
//if( ret )  {
//TestAddressLine.wtf( "      Two parts, valid.  Split at index " + i + "." );
//}
//else  {
//TestAddressLine.wtf( "      Two parts, NOT valid.  Split at index " + i + "." );
//}
//End debug statement.
        }
        else  {
//Debug statement.
//System.out.println( "Did not find ALT keyword." );
//End debug statement.
          ret = o[1].isValid( array );
//Debug statement.
//if( ret )  {
//TestAddressLine.wtf( "      One part, valid." );
//}
//else  {
//TestAddressLine.wtf( "      One part, NOT valid." );
//}
//End debug statement.
        }
      }
    }
//Debug statement.
//else  {
//TestAddressLine.wtf( "  There was not an initial street number." );
//}
//TestAddressLine.wtf( "EXITING isValid().  RET = " + ret + "." );
//TestAddressLine.wtf( "-----" );
//TestAddressLine.wtf( "" );
//End debug statement.

      return( ret );
  }  /*  public boolean isValid( char[] )  */


  /**
   *   !!Assumption:  address parts are separated by space characters!!
   */
  public boolean isValid( char[] array, int beginIndex, int length )  {
    char[] data = new char[length];

    System.arraycopy( array, beginIndex, data, 0, length );

    return( isValid( data ) );
  }  /*  public boolean isValid( char[], int, int )  */


  /**
   *   !!Assumption:  address parts are separated by space characters!!
   */
  public boolean isValid( String field )  {
    return( isValid( field.toCharArray( ) ) );
  }  /*  public boolean isValid( String )  */


  public int getMaxLength( )  {
    return( ml );
  }  /*  public int getMaxLength( )  */


  public int getGrouping( )  {
    return( gid );
  }  /*  public int getGrouping( )  */


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

}  /*  public class FullAddress implements Oracle, AlterDatabase  */
