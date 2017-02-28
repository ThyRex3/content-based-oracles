/**
 *   For the special cases included in this oracle's FSM see Appendix D of
 * http://pe.usps.gov/text/pub28/welcome.htm.
 */

package oracles;

import java.util.ArrayList;

import supportMethods.Setup;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public class StreetNumberOracle implements Oracle, PostProcess  {
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


  public StreetNumberOracle( )  {  }  /*  Constructor  */


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
   *   Sorts the PFL entries based on the associated start position.
   *
   *   Use insertion sort because the number of entries in ARR should be small
   * and possibly could be in near order already.
   */
  private void insertionSort( FieldDescriptor[] arr )  {
    int i,
        j;
    FieldDescriptor index;

    for( i = 1; i < arr.length; i++ )  {
      index = arr[i];

      j = i;
      while( ( j > 0 ) &&
             ( arr[( j - 1 )].getPosition( ) > index.getPosition( ) ) )  {

        arr[j] = arr[( j - 1 )];
        j--;
      }

      arr[j] = index;
    }
  }  /*  private void insertionSort( FieldDescriptor[] )  */


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

    //StreetNumber length should be less than six.   && ( array.length < 7 )
    if( ( array != null ) )  {
      while( c && ( i < array.length ) )  {
        //Is the current character valid for this FSM?
        if( c = ( ( index = search( array[i++] ) ) >= 0 ) )  {
          //Record if the state transitioned to is the error state.
          c = ( ( cs = t[cs][ti[index]] ) < ( t.length - 1 ) );
        }
      }
    }

    return( a[cs] );  //Return the result of the FSM.
  }  /*  private boolean test( char[] )  */


  private FieldDescriptor[] buildAddressList( OracleGroup og,
                                              RecordEnumeration re )  {

    int i,
        j,
        type;
    FieldDescriptor[] ret = null;
    String[] nfn = { "street number",
                     "directional",
                     "street name",
                     "street suffix" };
    ArrayList<FieldDescriptor> fdal = new ArrayList<FieldDescriptor>( );

    //Find all PFLs associated with a name content type existing in the RE.
    for( i = 0; i < nfn.length; i++ )  {  //Each name type.
      if( ( type = og.index( nfn[i] ) ) >= 0 )  {
        if( ( ret = re.ofType( type ) ) != null )  {
          for( j = 0; j < ret.length; j++ )  {  //Each PFL for this type.
            fdal.add( ret[j] );  //Insert the entries into a dynamic list.
          }
        }
      }
    }

    //Once a complete list is acquired place it in a structure more conducive to
    //  processing.
    if( fdal.size( ) > 0 )  {
      ret = new FieldDescriptor[fdal.size( )];
      for( i = 0; i < ret.length; i++ )  {
        ret[i] = fdal.get( i );
      }

      insertionSort( ret );  //Sort the PFLs based on start position.
    }

    return( ret );
  }  /*  private FieldDescriptor[] buildAddressList( OracleGroup,
                                                     RecordEnumeration )  */


  public void initialize( OracleGroup og, String[] args )  {
    char ch;
    int i = 0,
        j;
    NameValuePair nvp;
    String n;

    a = new boolean[11];
//  c = new char[66];
//  ti = new short[66];
    c = new char[22];
    ti = new short[22];
    t = new short[11][8];

    c[i] = ' ';  ti[i++] = 0;
    c[i] = '-';  ti[i++] = 1;
    c[i] = '.';  ti[i++] = 2;
    c[i] = '/';  ti[i++] = 3;

    //Set the digit information.
    ch = '0';
    for( j = 0; j < 10; j++ )  {
      c[( i + j )] = ch++;
      ti[( i + j )] = 4;
    }
    i += j;

    //Set the LETTER information.
//  ch = 'A';
//  for( j = 0; j < 26; j++ )  {
//    c[( i + j )] = ch++;
//    ti[( i + j )] = 5;
//  }
//  i += j;
    c[i] = 'E';  ti[i++] = 5;
    c[i] = 'N';  ti[i++] = 5;
    c[i] = 'S';  ti[i++] = 5;
    c[i] = 'W';  ti[i++] = 5;

    //Set the letter information.
//  ch = 'a';
//  for( j = 0; j < 26; j++ )  {
//    c[( i + j )] = ch++;
//    ti[( i + j )] = 6;
//  }
    c[i] = 'e';  ti[i++] = 5;
    c[i] = 'n';  ti[i++] = 5;
    c[i] = 's';  ti[i++] = 5;
    c[i] = 'w';  ti[i++] = 5;

    //Set the default transition to the error state, and all states default to a
    //  non-accepting state.
    for( i = 0; i < t.length; i++ )  {
      a[i] = false;

      for( j = 0; j < t[i].length; j++ )  {
        t[i][j] = ( short )( t.length - 1 );
      }
    }

    t[0][4] = 1;  //On a D transition to state 1.
    t[0][5] = t[0][6] = 2;  //On a L or l transition to state 2.
    t[1][0] = 3;  //On a ' ' transition to state 3;
    t[1][1] = t[1][2] = 7;  //On a '-' or '.' transition to state 7.
    t[1][4] = 1;  //On a D transition to state 1;
    t[1][5] = t[1][6] = 9;  //On a L or l transition to state 9.
    t[2][4] = 9;  //On a D transition to state 9.
    t[2][5] = t[2][6] = 2;  //On a L or l transition to state 2.
    t[3][4] = 4;  //On a D transition to state 4;
    t[4][3] = 5;  //On a '/' transition to state 5;
    t[5][4] = 6;  //On a D transition to state 6;
    t[7][4] = 8;  //On a D transition to state 8;
    t[8][4] = 8;  //On a D transition to state 8;
    t[9][4] = 9;  //On a D transition to state 9.
    t[9][5] = t[9][6] = 2;  //On a L, or l transition back to state 2.

    a[1] = true;
    a[6] = true;
    a[8] = true;
    a[9] = true;

    ml = 15;
    gid = 4;
    mp = 0.50;
    mbp = 0.25;
    this.og = og;
    on = "street number";

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
            mp = 0.50;
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
   *   At a high level, the reasoning is basically that a StreetNumber field
   * will usually be surrounded by or adjacent to other AddressLineOne parts
   * such as StreetName, Directional, and StreetSuffix.  If this is not the case
   * then probably it is not a StreetNumber
   */
  public void postProcess( char[][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    boolean b;
    int i,
        j,
        k;
    FieldDescriptor[] fdh = buildAddressList( og, re );

//Debug statement.
//System.out.println( "Beginning StreetNumber postProcess()..." );
//End debug statement.
    if( fdh != null )  {
      i = 0;
      while( i < fdh.length )  {
        //Use proximity of fields to determine groupings of address components.
        k = i;
        j = ( i + 1 );
        while( ( j < fdh.length ) && ( fdh[j].getPosition( ) <
                   ( fdh[k].getPosition( ) + fdh[k].getLength( ) + 10 ) ) )  {

          k = j++;
        }

        //In the current grouping determine if any of the PFP are a StreetName
        //  or a StreetSuffix.  These two fields represent the most reliable,
        //  exclusive domains, content types associated with AddressLineOne.
        b = false;
        k = i;
        while( !b && ( k < j ) )  {
          b = ( fdh[k].getName( ).equalsIgnoreCase( "street name" ) ||
                fdh[k].getName( ).equalsIgnoreCase( "street suffix" ) );

          if( !b )  {
            k++;
          }
        }

        //No reliable AddressLineOne component fields were found therefore
        //  remove any StreetNumbers in the grouping.
        if( !b )  {
          for( k = i; k < j; k++ )  {
            if( fdh[k].getName( ).equalsIgnoreCase( on ) )  {
              re.removeEntry( fdh[k] );
            }
          }
        }

        i = j;  //Transition to the next grouping.
      }
    }
//Debug statement.
//System.out.println( "Ending StreetNumber postProcess()..." );
//End debug statement.
  }  /*  public void postProcess( char[][], FieldDescriptor[][],
                                  RecordEnumeration )  {  */
/*
    boolean adj;  //Is an AddressLineOne part adjacent to this field.
    int i,
        j;
    int[] api = { og.index( "directional" ),  //Adjacent part index.
                  og.index( "street name" ),
                  og.index( "street suffix" ) };

    FieldDescriptor fdh;
    FieldDescriptor[] fda = re.ofType( og.index( on ) );

    if( fda != null )  {  //There were StreetNumbers in the RE.
//Debug statment.
//System.out.println( "Found " + fda.length + " StreetNumber fields." );
//End debug statement.
      for( i = 0; i < fda.length; i++ )  {
        adj = false;  //Reset the flag for this iteration.

//Debug statment.
//System.out.println( "Entry " + ( i + 1 ) + " - " + ( fda[i].getPosition( ) + 1 ) + ", " + ( fda[i].getPosition( ) + fda[i].getLength( ) ) + ", " + fda[i].getLength( ) + "." );
//End debug statement.
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
            //  an AddressLineOne part.
            j = 0;
            while( !adj && ( j < api.length ) )  {
              if( fdh.getType( ) != api[j] )  {
                j++;
              }
              else  {
//Debug statment.
//System.out.println( "Found an adjacent field before (" + og.name( api[j] ) + ").  " + ( fda[i].getPosition( ) + 1 ) );
//End debug statement.
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
//Debug statment.
//System.out.println( "Found an adjacent field after(" + og.name( api[j] ) + ").  " + ( fda[i].getPosition( ) + fda[i].getLength( ) ) );
//End debug statement.
                  adj = true;
                }
              }
            }
          }
        }

        if( !adj )  {
//Debug statment.
//System.out.println( "Removing entry." );
//End debug statement.
          re.removeEntry( fda[i] );
        }
//Debug statment.
//System.out.println( "" );
//End debug statement.
      }
    }
  }  /*  public void postProcess( char[][] records, RecordEnumeration re )  */


  public void postProcess( char[][][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    boolean adj;  //Is an AddressLineOne part adjacent to this field.
    int i,
        j;
    int[] api = { og.index( "directional" ),
                  og.index( "street name" ),
                  og.index( "street suffix" ) };

    FieldDescriptor fdh;
    FieldDescriptor[] fda = re.ofType( og.index( on ) );

    if( fda != null )  {  //There were StreetNumbers in the RE.
      for( i = 0; i < fda.length; i++ )  {
        adj = false;

        //Acquire the FieldDescriptor of the preceeding field.
        if( ( ( fdh = re.type( fda[i].getPosition( ) - 1 ) ) != null ) &&
            ( fdh.getType( ) >= 0 ) )  {

          j = 0;

          //Determine if the content type of the preceeding field is equal to an
          //  AddressLineOne part.
          while( !adj && ( j < api.length ) )  {
            if( fdh.getType( ) != api[j] )  {
              j++;
            }
            else  {
              adj = true;
            }
          }
        }

        if( !adj )  {
          //Acquire the FieldDescriptor of the following field.
          if( ( ( fdh = re.type( fda[i].getPosition( ) + 1 ) ) != null ) &&
              ( fdh.getType( ) >= 0 ) )  {

            j = 0;

            //Determine if the content type of the preceeding field is equal to
            //  an AddressLineOne part.
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
          re.removeEntry( fda[i] );
        }
      }
    }
  }  /*  public void postProcess( char[][][] records, FieldDescriptor[][],
                                  RecordEnumeration re )  */

}  /*  public class StreetNum implements Oracle, PostProcess  */
