package oracles;

import java.util.ArrayList;

import supportMethods.Setup;
import supportObjects.Position;
import supportObjects.NameValuePair;


public class FullNameOracle implements Oracle, AlterDatabase  {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private String on;  //The oracle's name.
  private Oracle[] oracles;


  public FullNameOracle( )  {  }  /*  Constructor  */


  /**
   *   Though this is an oracle that will have its database altered, it will
   * have already been accomplished when the first and last name oracles reduce
   * their databases.  Thus a requirement is that the first and last name
   * oracles must be reduced before invoking this oracle.
   */
  public void setMinDatabase( )  {  }  /*  public void setMinDatabase( )  */
  public void setMaxDatabase( )  {  }  /*  public void setMaxDatabase( )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    oracles = new Oracle[4];

    oracles[0] = og.getOracle( og.index( "name prefix" ) );
    oracles[1] = og.getOracle( og.index( "first name" ) );
    oracles[2] = og.getOracle( og.index( "last name" ) );
    oracles[3] = og.getOracle( og.index( "name suffix" ) );

    ml = 70;
    gid = 3;
    mp = 0.60;
    mbp = 0.15;
    on = "full name";

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
            ml = 70;
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
            gid = 3;
          }
        }
      }
    }
  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] array )  {
    return( isValid( array, 0, array.length ) );
  }  /*  public boolean isValid( char[] )  */


  public boolean isValid( char[] array, int beginIndex, int length )  {
    boolean x,
            y,
            z,
            fne = false,  //First name exists.
            lne = false,  //Last name exists.
            ret = true;
    int i,
        j;
    Position p = null;
    ArrayList<Position> pal = new ArrayList<Position>( );

    //Ensure the range is valid.
    if( beginIndex < 0 )  {
      beginIndex = 0;
    }
    if( ( beginIndex + length ) > array.length )  {
      length = ( array.length - beginIndex );
    }

    //Test the array for invalid characters.  The only valid characters for a
    //  full name include letters, whitespace, commas, and periods.  Commas are
    //  often used to separate the last name from the first name when the last
    //  name is given first.  Periods are often used to indicate an abbreviated
    //  name.
    //!ASSUMPTION! - The number of occurrences of a digit in the name prefix or
    //  suffix is statistically insignificant enough to allow for screening
    //  strings with digits.
    i = beginIndex;
    while( ret && ( i < ( beginIndex + length ) ) )  {
      ret = ( ret && ( Character.isLetter( array[i] ) ||
                       Character.isWhitespace( array[i] ) ||
                       ( array[i] == ',' )  || ( array[i] == '.' ) ) );

      i++;
    }

    //Determine where the character groupings are in the array of characters.
    i = beginIndex;
//!!Since the logic has been changed RET is insignificant at this point!!
    while( ret && ( i < ( beginIndex + length ) ) )  {
      //First skip any non-character data in the array.
      while( ( i < ( beginIndex + length ) ) &&
             !Character.isLetter( array[i] ) )  {

        i++;
      }

      //Count the number of grouped characters.
      j = ( i + 1 );
      while( ( j < ( beginIndex + length ) ) &&
             Character.isLetter( array[j] ) )  {

        j++;
      }

      if( i < ( beginIndex + length ) )  {
        pal.add( new Position( i, ( j - i ) ) );
      }

      i = j;
    }

//Debug statement.
//for( int h = 0; h < pal.size( ); h++ )  {
//System.out.print( "|" + ( new String( array, pal.get( h ).getPosition( ), pal.get( h ).getLength( ) ) ) + "|, " );
//}
//System.out.println( "" );
//End debug statement.
    i = 0;
    while( ret && ( i < pal.size( ) ) )  {
      p = pal.get( i );

      x = false;
      y = false;
      z = false;
      //Each sequence of characters should be either a name or an initial.
      if( oracles[1].isValid( array, p.getPosition( ), p.getLength( ) ) )  {
        x = true;
      }
      if( oracles[2].isValid( array, p.getPosition( ), p.getLength( ) ) )  {
        y = true;
      }
      //If it is not a name then test to see if it is either a name prefix, a
      //  name suffix, an initial.
      if( !( x || y ) )  {
        if( oracles[0].isValid( array, p.getPosition( ), p.getLength( ) ) ||
            oracles[3].isValid( array, p.getPosition( ), p.getLength( ) ) )  {

          z = true;
        }
        //Initials must be near other parts of the name, otherwise it might just
        //  be the beginning or end of another field.  'Near' is currently
        //  defined as two characters of separation or less.
        else if( p.getLength( ) == 1 )  {
//Debug statement.
//System.out.print( "|" + ( new String( array, p.getPosition( ), 1 ) ) + "| :: " );
//End debug statement.
          if( i > 0 )  {  //First test the token before the current one.
//Debug statement.
//System.out.print( "a.)  " + p.getPosition( ) + " - " + ( pal.get( i - 1 ).getPosition( ) + pal.get( i - 1 ).getLength( ) ) );
//End debug statement.
            z = ( ( p.getPosition( ) - ( pal.get( i - 1 ).getPosition( ) +
                                        pal.get( i - 1 ).getLength( ) ) ) < 3 );

          }

          if( !z && ( i < ( pal.size( ) - 1 ) ) )  {  //The following token.
//Debug statement.
//System.out.print( "b.)  " + pal.get( i + 1 ).getPosition( ) + " - " + ( p.getPosition( ) + 1 ) );
//End debug statement.
            z = ( ( pal.get( i + 1 ).getPosition( ) -
                    ( p.getPosition( ) + 1 ) ) < 3 );

          }
//Debug statement.
//System.out.println( "" );
//End debug statement.
        }
      }

      //Each sequence of characters should be a name, a name prefix or suffix,
      //  or an initial.
      if( !( x || y || z ) )  {
        ret = false;
      }
      else  {
        if( !fne && x )  {
          fne = true;
        }
        else if( !lne && y )  {
          lne = true;
        }
      }

      i++;
    }

    //All character sequences were either a name or an initial and there exists
    //  at least one name.
    return( ret && fne && lne );
  }  /*  public boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    return( isValid( field.toCharArray( ), 0, field.length( ) ) );
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

}  /*  public class CompleteName implements Oracle  */
