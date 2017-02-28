package oracles;

import supportMethods.Setup;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public class SecondaryRangeNumberOracle implements Oracle, PostProcess  {
  boolean[] a;  //The list of accepting states.
  char[] c;  //The list of possible characters.
  short[] ti;  //Transition index, one for each character.
  short[][] t;  //The transition table.
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private OracleGroup og;
  private String on;  //The oracle's name.


  public SecondaryRangeNumberOracle( )  {  }  /*  Constructor  */


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
   *   Removes white space from the beginning and end of the input ARRAY.
   */
  private char[] remove( char[] array )  {
    char[] ret = null;
    int i = 0,
        j,
        k;

    if( array != null )  {
      while( ( i < array.length ) && Character.isWhitespace( array[i] ) )  {
        i++;
      }

      j = ( array.length - 1 );
      while( ( j >= i ) && Character.isWhitespace( array[j] ) )  {
        j--;
      }
      j++;

      for( k = i; k < j; k++ )  {
        array[i] = Character.toLowerCase( array[i] );
      }

      if( j > i )  {
        ret = new char[( j - i )];
        System.arraycopy( array, i, ret, 0, ( j - i ) );
      }
    }

    return( ret );
  }  /*  private char[] remove( char[] )  */


  private boolean test( char[] array )  {
    boolean c = true;  //Continue?
    int i = 0,
        cs = 0,  //The current state.
        index;

    if( array != null )  {
      while( c && ( i < array.length ) )  {
        //Is the current character valid for this FSM?
        if( c = ( ( index = search( array[i++] ) ) >= 0 ) )  {
          //Record whether or not the state transitioned to is the error state.
          c = ( ( cs = t[cs][ti[index]] ) < ( t.length - 1 ) );
        }
      }
    }

    return( a[cs] );  //Return the result of the FSM.
  }  /*  private boolean test( char[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    char ch;
    int i = 0,
        j;
    NameValuePair nvp;
    String n;

    a = new boolean[13];
    c = new char[68];
    ti = new short[68];
    t = new short[13][9];

    c[i] = ' ';  ti[i++] = 0;
    c[i] = '#';  ti[i++] = 1;
    c[i] = '-';  ti[i++] = 2;
    c[i] = '.';  ti[i++] = 3;
    c[i] = '/';  ti[i++] = 4;

    //Set the digit information.
    ch = '0';
    for( j = 0; j < 10; j++ )  {
      c[( i + j )] = ch++;
      ti[( i + j )] = 5;
    }
    i += j;

    //Set the LETTER information.
    ch = 'A';
    for( j = 0; j < 26; j++ )  {
      c[( i + j )] = ch++;
      ti[( i + j )] = 6;
    }
    i += j;

    //Set the letter information.
    ch = 'a';
    for( j = 0; j < 26; j++ )  {
      c[( i + j )] = ch++;
      ti[( i + j )] = 7;
    }

    //Set the default transition to the error state, and all states default to a
    //  non-accepting state.
    for( i = 0; i < t.length; i++ )  {
      a[i] = false;

      for( j = 0; j < t[i].length; j++ )  {
        t[i][j] = ( short )( t.length - 1 );
      }
    }

    t[0][1] = 1;  //On a # transition to state 1.
    t[0][5] = 2;  //On a D transition to state 2.
    t[0][6] = t[0][7] = 10;  //On a L or l transition to state 10.
    t[1][0] = 3;  //On a ' ' transition to state 3;
    t[1][5] = 2;  //On a D transition to state 2;
    t[2][0] = 6;  //On a ' ' transition to state 6.
    t[2][2] = 4;  //On a - transition to state 4.
    t[2][3] = 5;  //On a . transition to state 5.
    t[2][5] = t[2][6] = t[2][7] = 2;  //On a D, L, or l transition to state 2.
    t[3][0] = 3;  //On a ' ' transition to state 3;
    t[3][5] = 2;  //On a D transition to state 2;
    t[4][5] = t[4][6] = t[4][7] = 2;  //On a D, L, or l transition to state 2.
    t[5][5] = t[5][6] = t[5][7] = 2;  //On a D, L, or l transition to state 2.
    t[6][5] = 7;  //On a D transition to state 7;
    t[7][4] = 8;  //On a / transition to state 8;
    t[8][5] = 9;  //On a D transition to state 9;
    t[10][5] = 2;  //On a D transition to state 2;
    t[10][6] = t[10][7] = 11;  //On a L or l transition to state 11;
    t[11][5] = 2;  //On a D transition to state 2;

    a[2] = true;
    a[9] = true;
    a[10] = true;

    ml = 15;
    gid = 4;
    mp = 0.10;
    mbp = 1.00;
    this.og = og;
    on = "unit number";

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
            mp = 0.10;
          }
        }
        else if( n.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 1.00;
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
      ret = test( remove( data ) );
    }

    return( ret );
  }  /*  boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    return( test( field.trim( ).toCharArray( ) ) );
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


  /**
   *   At a high level, the reasoning is basically that a RangeNumber field
   * will usually be surrounded by or adjacent to other AddressLineTwo parts
   * such as UnitDesignator.  If this is not the case then it is probably not a
   * RangeNumber
   */
  public void postProcess( char[][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    boolean adj;  //Is an AddressLineOne part adjacent to this field.
    int i,
        j;
    int[] api = { og.index( "unit designator" ) };

    FieldDescriptor fdh;
    FieldDescriptor[] fda = re.ofType( og.index( on ) );

    if( fda != null )  {  //There were UnitNumbers in the RE.
      for( i = 0; i < fda.length; i++ )  {
        adj = false;

        //Acquire the FieldDescriptor of the preceeding field.
        if( ( fdh = re.type( fda[i].getPosition( ) - 1 ) ) != null )  {
          //If the immediately preceeding field is UNSPECIFIED and its length is
          //  less than ten then it might just be a fluke field, whitespace or
          //  something, and should be disregarded.
          if( ( fdh.getType( ) < 0 ) &&
              ( ( fda[i].getPosition( ) - fdh.getPosition( ) ) < 10 ) )  {

            fdh = re.type( fdh.getPosition( ) - 1 );
          }

          if( fdh != null )  {
            //Determine if the content type of the preceeding field is equal to
            //  an AddressLineTwo part.
            j = 0;
            while( !adj && ( j < api.length ) )  {
              if( fdh.getType( ) != api[j] )  {
                j++;
              }
              else  {
                adj = true;
              }
            }
          }
        }

        if( !adj )  {
          //Acquire the FieldDescriptor of the following field.
          if( ( fdh = re.type( fda[i].getPosition( ) + fda[i].getLength( ) ) )
              != null )  {

            if( ( fdh.getType( ) < 0 ) &&
                ( ( fdh.getPosition( ) -
                  ( fda[i].getPosition( ) + fda[i].getLength( ) ) ) < 10 ) )  {

              fdh = re.type( fdh.getPosition( ) + fdh.getLength( ) );
            }

            if( fdh != null )  {
              j = 0;
              while( !adj && ( j < api.length ) )  {
                if( fdh.getType( ) != api[j] )  {
                  j++;
                }
                else  {
                  adj = true;
                }
              }
            }
          }
        }

        if( !adj )  {
          re.removeEntry( fda[i] );
        }
      }
    }
  }  /*  public void postProcess( char[][] records, FieldDescriptor[][],
                                  RecordEnumeration re )  */


  public void postProcess( char[][][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    boolean adj;  //Is an AddressLineOne part adjacent to this field.
    int i,
        j;
    int[] api = { og.index( "unit designator" ) };

    FieldDescriptor fdh;
    FieldDescriptor[] fda = re.ofType( og.index( on ) );

//Debug statement.
//System.out.println( on + " = " + og.index( on ) );
//End debug statement.
    if( fda != null )  {  //There were unit numbers in the RE.
      for( i = 0; i < fda.length; i++ )  {
        adj = false;
//Debug statement.
//System.out.println( fda[i] );
//End debug statement.
        //Acquire the FieldDescriptor of the preceeding field.
        if( ( ( fdh = re.type( fda[i].getPosition( ) - 1 ) ) != null ) &&
            ( fdh.getType( ) >= 0 ) )  {

//Debug statement.
//System.out.println( "Preceding -->  " + fdh );
//End debug statement.
          j = 0;

          //Determine if the content type of the preceeding field is equal to an
          //  AddressLineTwo part.
          while( !adj && ( j < api.length ) )  {
            if( fdh.getType( ) != api[j] )  {
              j++;
            }
            else  {
              adj = true;
            }
          }
//Debug statement.
//System.out.println( "After preceding while, ADJ = " + adj + "." );
//End debug statement.
        }

        if( !adj )  {
          //Acquire the FieldDescriptor of the following field.
          if( ( ( fdh = re.type( fda[i].getPosition( ) + 1 ) ) != null ) &&
              ( fdh.getType( ) >= 0 ) )  {

//Debug statement.
//System.out.println( "Following -->  " + fdh );
//End debug statement.
            j = 0;

            //Determine if the content type of the preceeding field is equal to
            //  an AddressLineTwo part.
            while( !adj && ( j < api.length ) )  {
              if( fdh.getType( ) != api[j] )  {
                j++;
              }
              else  {
                adj = true;
              }
            }
//Debug statement.
//System.out.println( "After following while, ADJ = " + adj + "." );
//End debug statement.
          }
        }

        if( !adj )  {
//Debug statement.
//System.out.println( "Removing..." );
//End debug statement.
          re.removeEntry( fda[i] );
        }
      }
    }
//Debug statement.
//System.out.println( "-----" );
//System.out.println( "-----" );
//End debug statement.
  }  /*  public void postProcess( char[][][] records, FieldDescriptor[][],
                                  RecordEnumeration re )  */

}  /*  public class SecondaryRangeNumber implements Oracle, PostProcess  */
