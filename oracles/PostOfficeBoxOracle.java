package oracles;

import java.util.ArrayList;

import supportMethods.Setup;
import supportObjects.NameValuePair;


public class PostOfficeBoxOracle implements Oracle  {
  boolean[] a;  //The list of accepting states.
  char[] c;  //The list of possible characters.
  short[] ti;  //Transition index, one for each character.
  short[][] t;  //The transition table.
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private String on;  //The oracle's name.


  /**
   *   Initialize the FSM to accept PO Box and Military Box addresses.
   */
  public PostOfficeBoxOracle( )  {  }  /*  Constructor  */


  private int search( char ch )  {
    boolean s = true;  //Still searching?
    int b = 0,
        m = -1,
        e = ( c.length - 1 );

    while( s && ( b <= e ) )  {
      m = ( ( b + e ) / 2 );

      if( ch == c[m] )  {
        s = false;
      }
      else if( ch < c[m] )  {
        e = ( m - 1 );
      }
      else  {
        b = ( m + 1 );
      }
    }

    if( s )  {  //Did not find an matching entry;
      m = -1;
    }

    return( m );
  }  /*  private int search( char )  */


  /**
   *   Removes everything except letters and digits from ARRAY.
   */
  private char[] remove( char[] array )  {
    char[] ret = null;
    int i;
    ArrayList<Character> al = new ArrayList<Character>( array.length );

    if( array != null )  {
      for( i = 0; i < array.length; i++ )  {
        array[i] = Character.toLowerCase( array[i] );

        if( Character.isDigit( array[i] ) || Character.isLetter( array[i] ) )  {
          al.add( array[i] );
        }
      }

      if( al.size( ) > 0 )  {
        ret = new char[al.size( )];

        for( i = 0; i < ret.length; i++ )  {
          ret[i] = al.get( i );
        }
      }
    }

    return( ret );
  }  /*  private char[] remove( char[] )  */


  private boolean test( char[] array )  {
    boolean b = true;  //Continue?
    int i = 0,
        cs = 0,  //The current state.
        index;

    if( array != null )  {
      while( b && ( i < array.length ) )  {
        //Is the current character valid for this FSM?
        b = ( ( index = search( array[i++] ) ) >= 0 );

        if( b )  {  //If a valid character, then transition to the next state.
          //Record whether or not the state transitioned to is the error state.
          b = ( ( cs = t[cs][ti[index]] ) < ( t.length - 1 ) );
        }
      }
    }

    return( a[cs] );  //Return the result of the FSM.
  }  /*  private boolean test( char[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    char ch = '0';
    int i,
        j;
    NameValuePair nvp;
    String n;

    a = new boolean[13];
//Debug statement.
//System.out.println( "Allocating memory for C[]." );
//End debug statement.
    c = new char[17];
    ti = new short[17];
    t = new short[13][8];

    //Set the Digit information.
    for( i = 0; i < 10; i++ )  {
      c[i] = ch++;
      ti[i] = 0;
    }
    c[i] = 'a';  ti[i++] = 1;
    c[i] = 'b';  ti[i++] = 2;
    c[i] = 'e';  ti[i++] = 3;
    c[i] = 'f';  ti[i++] = 4;
    c[i] = 'o';  ti[i++] = 5;
    c[i] = 'p';  ti[i++] = 6;
    c[i] = 'x';  ti[i++] = 7;

    //Set the default transition to the error state, and all states default to a
    //  non-accepting state.
    for( i = 0; i < t.length; i++ )  {
      a[i] = false;

      for( j = 0; j < t[i].length; j++ )  {
        t[i][j] = ( short )( t.length - 1 );
      }
    }

    t[0][1] = t[0][4] = 1;  //On 'a' and 'f' transition to state 1.
    t[0][6] = 6;  //On 'p' transition to state 6.
    t[1][6] = 2;  //On 'p' transition to state 2.
    t[2][5] = 3;  //On 'o' transition to state 3.
    t[3][1] = 4;  //On 'a' transition to state 4.
    //On 'a', 'e', and 'p' transition to state 5.
    t[4][1] = t[4][3] = t[4][6] = 5;
    t[5][0] = 11;  //On 'D' transition to state 2.
    
    t[6][5] = 7;  //On 'o' transition to state 7.
    t[7][2] = 8;  //On 'b' transition to state 8.
    t[8][5] = 9;  //On 'o' transition to state 9.
    t[9][7] = 10;  //On 'x' transition to state 10.
    t[10][0] = 11;  //On 'p' transition to state 2.
    t[11][0] = 11;  //On 'D' transition to state 11.

    a[11] = true;  //The only accepting state.

    ml = 15;
    gid = 3;
    mp = 0.40;
    mbp = 0.85;
    on = "post office box";

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
            ml = 15;
          }
        }
        else if( n.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.40;
          }
        }
        else if( n.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 0.85;
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
    return( test( remove( array ) ) );
  }  /*  boolean isValid( char[] )  */


  public boolean isValid( char[] array, int beginIndex, int length )  {
    boolean ret = ( ( beginIndex >= 0 ) && ( length > 0 ) &&
                    ( ( beginIndex + length ) <= array.length ) );

    char[] data;

    if( ret )  {
      data = new char[length];
      System.arraycopy( array, beginIndex, data, 0, length );
      ret = test( remove( array ) );
    }

    return( ret );
  }  /*  boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    return( test( remove( field.toCharArray( ) ) ) );
  }  /*  boolean isValid( String )  */


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

}  /*  public class PostOfficeBox implements Oracle  */
