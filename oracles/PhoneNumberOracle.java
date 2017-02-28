package oracles;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import supportMethods.Setup;
import supportMethods.BasicAttributes;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public class PhoneNumberOracle implements Oracle, PostProcess  {
  class NXX  {  //A nested, inner class
    private short nxx;  //The NXX part of the phone number.
    private short[] zc;  //A list of zipcodes corresponding to this NXX.


    public NXX( )  {
      nxx = -1;
      zc = null;
    }  /*  Constructor  */


    public NXX( short nxx, short[] zipcodes )  {
      this.nxx = nxx;
      zc = zipcodes;
    }  /*  Constructor  */


    public void setNxx( short nxx )  {
      this.nxx = nxx;
    }  /*  public void setNxx( short )  */


    public void setZipcodes( short[] zipcodes )  {
      zc = zipcodes;
    }  /*  public void setNxx( short[] )  */


    public short getNxx( )  {
      return( nxx );
    }  /* public short getNxx( )  */


    public short[] getZipcodes( )  {
      return( zc );
    }  /* public short[] getZipcodes( )  */


    /**
     *   Performs a linear search because the number of zipcodes corresponding
     * to a single NXX value should be small.
     */
    public boolean validZipcode( short zipcode )  {
      boolean ret = false;
      int i = 0;

      while( !ret && ( i < zc.length ) )  {
        if( zc[i] != zipcode )  {
          i++;
        }
        else  {
          ret = true;
        }
      }

      return( ret );
    }  /*  public boolean validZipcode( short )  */

  }  /*  class NXX  */


  class NPA  {  //A nested, inner class
    private char[] state;  //The state corresponding to this NPA.
    private short npa;  //The NPA, area code, part of the phone number.
    private NXX[] nxx;  //The list of NXX entries corresponding to this NPA.


    public NPA( )  {
      state = new char[2];
      npa = -1;
      nxx = null;
    }  /*  Constructor  */


    public NPA( char[] state, short npa )  {
      this.state = state;
      this.npa = npa;
      nxx = null;
    }  /*  Constructor  */


    private int binarySearch( short value )  {
      boolean b = true;  //Searching.
      int beginning = 0,
          middle = -1,
          end = ( nxx.length - 1 );

      while( b && ( beginning <= end ) )  {
        middle = ( ( beginning + end ) / 2 );

        if( value == nxx[middle].getNxx( ) )  {
          b = false;
        }
        else if( value < nxx[middle].getNxx( ) )  {
          end = ( middle - 1 );
        }
        else  {
          beginning = ( middle + 1 );
        }
      }

      if( b )  {
        middle = -1;
      }

      return( middle );
    }  /*  private int binarySearch( short value )  */


    public void setNpa( short npa )  {
      this.npa = npa;
    }  /*  public void setNpa( short )  */


    public void setState( char[] state )  {
      this.state = state;
    }  /*  public void setState( char[] )  */


    public void setNxx( NXX[] nxx )  {
      this.nxx = nxx;
    }  /*  public void setNxx( NXX[] )  */


    public void setNxx( ArrayList<NXX> nxxal )  {
      int i = 0;
      Iterator<NXX> itr;

      nxx = new NXX[nxxal.size( )];
      for( itr = nxxal.iterator( ); itr.hasNext( ); )  {
        nxx[i++] = itr.next( );
      }
    }  /*  public void setNxx( ArrayList<NXX> )  */


    public void setNxxCount( int count )  {
      nxx = new NXX[count];
    }  /*  public void setNxxCount( int )  */


    public short getNpa( )  {
      return( npa );
    }  /*  public short getNpa( )  */


    public char[] getState( )  {
      return( state );
    }  /*  public char[] getState( )  */


    public NXX[] getNxx( )  {
      return( nxx );
    }  /*  public NXX[] getNxx( )  */


    public boolean validNxx( short nxx )  {
      return( binarySearch( nxx ) >= 0 );
    }  /*  public boolean validNxx( short )  */


    public boolean validZipcode( short nxx, short zipcode )  {
      boolean ret = false;
      int i = binarySearch( nxx );

      if( i >= 0 )  {
        ret = this.nxx[i].validZipcode( zipcode );
      }

      return( ret );
    }  /*  public boolean validZipcode( short, short )  */

  }  /*  class NPA  */


  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private NPA[] pnl;  //The list of phone numbers and associated information.
  private String on;
  private OracleGroup og;


  /**
   *   This method expects a file of the format NPA, 1-3; NXX, 4-6; state, 7-8;
   * {zipcode, 9-13}.
   */
  public PhoneNumberOracle( )  {  }  /*  Constructor  */


  private int binarySearch( short value )  {
    boolean b = true;  //Searching.
    int beginning = 0,
        middle = -1,
        end = ( pnl.length - 1 );

    while( b && ( beginning <= end ) )  {
      middle = ( ( beginning + end ) / 2 );

      if( value == pnl[middle].getNpa( ) )  {
        b = false;
      }
      else if( value < pnl[middle].getNpa( ) )  {
        end = ( middle - 1 );
      }
      else  {
        beginning = ( middle + 1 );
      }
    }

    if( b )  {
      middle = -1;
    }

    return( middle );
  }  /*  private int binarySearch( short value )  */


  /**
   * @return The original array minus any whitespace or control characters such
   *         as '(', ')', '-', or '.'.  If letters exist in the input then the
   *         method will return NULL. 
   */
  private char[] strip( char[] array, int sp, int len )  {
    char[] ret = null;
    int i,
        dc = 0,  //Digit count.
        lc = 0;  //Letter count.

    for( i = sp; i < ( sp + len ); i++ )  {
      if( Character.isDigit( array[i] ) )  {
        dc++;
      }
      else if( Character.isLetter( array[i] ) )  {
        lc++;
      }
    }

    if( lc <= 0 )  {
      ret = new char[dc];
      lc = 0;
      for( i = sp; i < ( sp + len ); i++ )  {
        if( Character.isDigit( array[i] ) )  {
          ret[lc++] = array[i];
        }
      }
    }

    return( ret );
  }  /*  private char[] strip( char[], int, int )  */


  private short extractPart( boolean npa, char[] array )  {
    short ret = -1;
    int i,
        x,
        sp,
        ep;

    if( npa )  {  //Extract the first three characters, the NPA or area code.
      sp = 2;
      ep = -1;
    }
    else  {  //Extract the second three characters, the NXX.
      sp = 5;
      ep = 2;
    }

    ret = 0;
    x = 1;
    for( i = sp; i > ep; i-- )  {
      ret += ( ( ( ( short )array[i] ) - 48 ) * x );
      x *= 10;
    }

    return( ret );
  }  /*  private short extractPart( boolean, char[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    char[] sh = new char[2];  //A holder variable for the state information.
    char[][] cin = null;  //A character array to store the input. 
    short npav,
          npavh,
          nxxv,
          nxxvh,
          zcv;
    short[] zc;  //A variable to store zipcodes.
    int c = 0,
        i = 0,
        j,
        k,
        m,
        n,
        x,
        zcc;  //Zipcode count.
//Debug statement.
//int a = 0,
//    b;
//End debug statement.
    NameValuePair nvp;
    NPA npah;
    String s,
           fn = "/data/NpaNxxStateZip.dat";  //The file name.
    BufferedReader inf;
    Iterator<NPA> itr;
    ArrayList<NXX> nxxal;
    ArrayList<NPA> npaal = new ArrayList<NPA>( );

    ml = 20;
    gid = 16;
    mp = 0.50;
    mbp = 0.90;
    this.og = og;
    on = "phone number";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        s = nvp.getName( );
        if( s.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
        else if( s.equalsIgnoreCase( "maximumsourcefile" ) )  {
          fn = nvp.getValue( );
        }
        else if( s.equalsIgnoreCase( "maximumlength" ) )  {
          try  {
            ml = Integer.parseInt( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            ml = 20;
          }
        }
        else if( s.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.25;
          }
        }
        else if( s.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 0.90;
          }
        }
        else if( s.equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 16;
          }
        }
      }
    }

    try  {
      inf = new BufferedReader( new InputStreamReader(
                            bean.LayoutBean.class.getResourceAsStream( fn ) ) );

      cin = new char[og.fileLineCount(
                       BasicAttributes.extractFileName( true, fn ) )][];

      while( ( c < cin.length ) && ( ( s = inf.readLine( ) ) != null ) )  {
        cin[c++] = s.toCharArray( );
      }

      inf.close( );
    }catch( IOException e )  {
      System.out.println( "oracles - PhoneNumberOracle error 1: " +
                          "IOException.  " + e.getMessage( ) );

      System.exit( 1 );
    }

    //Determine the value of the first NPA entry, a priming read.
    x = 1;
    npav = 0;
    for( k = 2; k >= 0; k-- )  {  //Extract the NPA.
      npav += ( ( ( ( short )cin[i][k] ) - 48 ) * x );
      x *= 10;
    }

    while( i < c )  {
      npavh = npav;
      j = i;

      //Determine how many fields correspond to the current NPA.
      while( ( npavh == npav ) && ( ++j < c ) )  {
        x = 1;
        npavh = 0;
        for( k = 2; k >= 0; k-- )  {  //Extract the NPA.
          npavh += ( ( ( ( short )cin[j][k] ) - 48 ) * x );
          x *= 10;
        }
      }

//try  {
      //Extract the state information from the first record in this grouping.
      System.arraycopy( cin[i], 6, sh, 0, 2 );
//}catch( ArrayIndexOutOfBoundsException e )  {
//System.out.println( new String( cin[i] ) + "| " + cin[i].length + "." );
//}

//Debug statemnt.
//if( a < 2 )  {
//a++;
//System.out.println( "Storing NPA = " + npav + ";  STATE = " + ( new String( sh ) ) + "." );
//}
//End debug statemnt.
      npah = new NPA( sh, npav );  //Create an entry for the current NPA, state.
      npav = npavh;

        //Count the number of NXX entries in this area code.
//        nxxc = 0;
//        v = -1;
//        for( m = i; m < j; m++ )  {
//          x = 1;
//          vh = 0;
//          for( k = 5; k > 2; k-- )  {  //Extract the NXX.
//            vh += ( ( ( ( short )cin[m][k] ) - 48 ) * x );
//            x *= 10;
//          }
//
//          if( vh != v )  {
//            nxxc++;
//            v = vh;
//          }
//        }

      nxxal = new ArrayList<NXX>( j - i );

      nxxv = 0;
      x = 1;
      for( k = 5; k > 2; k-- )  {  //Extract the NXX, a priming read.
        nxxv += ( ( ( ( short )cin[i][k] ) - 48 ) * x );
        x *= 10;
      }

      while( i < j )  {  //Retrieve and store the NXX information.
        nxxvh = nxxv;
        m = i;

        while( ( nxxvh == nxxv ) && ( ++m < j ) )  {
          nxxvh = 0;
          x = 1;
          for( k = 5; k > 2; k-- )  {  //Extract the NXX.
            nxxvh += ( ( ( ( short )cin[m][k] ) - 48 ) * x );
            x *= 10;
          }
        }

        zcc = 0;
        for( n = i; n < m; n++ )  {  //Count the number of zipcodes.
          if( cin[n].length > 8 )  {  //Not every entry contains a zipcode.
            zcc++;
          }
        }

        //Retrieve and store the zipcodes corresponding to the current NXX.
        zc = new short[zcc];
        zcc = 0;

        for( n = i; n < m; n++ )  {
          if( cin[n].length > 8 )  {  //Not every entry contains a zipcode.
            zcv = 0;
            x = 1;

            for( k = ( cin[n].length - 1 ); k > 7; k-- )  {
              zcv += ( ( ( ( short )cin[n][k] ) - 48 ) * x );
              x *= 10;
            }

            zc[zcc++] = zcv;  //Store the new zipcode.
          }
        }

        i = m;  //Move the index to point at the beginning of the next group.
        nxxal.add( new NXX( nxxv, zc ) );  //Create an entry for the NXX group.
        nxxv = nxxvh;
//Debug statemnt.
//if( a < 2 )  {
//System.out.print( "NXX = " + nxxv + ";  ZC list = " );
//for( b = 0; b < zc.length; b++ )  {
//System.out.print( zc[b] + ", " );
//}
//System.out.println( "." );
//}
//End debug statemnt.
      }

      //Store the NXX information corresponding to the current NPA.
      npah.setNxx( nxxal );
      npaal.add( npah );
    }

    i = 0;
    pnl = new NPA[npaal.size( )];
    for( itr = npaal.iterator( ); itr.hasNext( ); )  {
      pnl[i++] = itr.next( );
    }
//Debug statement.
//for( i = 1; i < pnl.length; i++ )  {
//if( pnl[i].getNpa( ) <= pnl[( i - 1 )].getNpa( ) )  {
//System.out.println( "I = " + i + ";  PNL[I-1] = " + pnl[( i - 1 )].getNpa( ) + ";  PNL[I] = " + pnl[i].getNpa( ) + "." );
//}
//}
//End debug statement.
  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] array )  {
    return( isValid( array, 0, array.length ) );
  }  /*  public boolean isValid( char[] )  */


  public boolean isValid( char[] array, int sp, int len )  {
    boolean ret = false;
    int i;

//Debug statement.
//System.out.println( "NPA = " + extractPart( true, array ) + ";  NXX = " + extractPart( false, array ) + "." );
//End debug statement.
    if( ( ( array = strip( array, sp, len ) ) != null ) &&
        ( array.length == 10 ) )  {

      if( ( i = binarySearch( extractPart( true, array ) ) ) >= 0 )  {
//Debug statement.
//System.out.println( "Found NPA." );
//End debug statement.
        ret = pnl[i].validNxx( extractPart( false, array ) );
//Debug statement.
//if( ret )  {
//System.out.println( "Found NXX." );
//}
//else  {
//System.out.println( "Did not find NXX." );
//}
//End debug statement.
      }
    }

    return( ret );
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


  /*  This needs to be a hash for the fields that are phone numbers.  */
  public void postProcess( char[][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    int i,
        j,
//        mc,  //Field value maximum count.
        sp,  //Current field start position.
//        vc,  //Valid count.
        len;  //Current field length.
    Integer count;
    FieldDescriptor[] fdh = re.ofType( og.index( on ) );
    String s;
    Hashtable<String, Integer> hash;

//Debug statement.
//System.out.println( "Beginning PhoneNumber postProcess()..." );
//End debug statement.
    if( fdh != null )  {
//      mc = Integer.MIN_VALUE;

      for( i = 0; i < fdh.length; i++ )  {
//Debug statement.
//System.out.println( fd[i] );
//End debug statement.
        hash = new Hashtable<String, Integer>( records.length );
//        vc = 0;
        sp = fdh[i].getPosition( );
        len = fdh[i].getLength( );

        for( j = 0; j < records.length; j++ )  {
          s = new String( records[j], sp, len );

          if( ( count = hash.get( s ) ) != null )  {
            count++;
          }
          else  {
            count = new Integer( 1 );
          }
          hash.put( s, count );

//          if( count.intValue( ) > mc )  {
//            mc = count.intValue( );
//          }

//          if( isValid( records[j], sp, len ) )  {
//            vc++;
//          }
        }

//Debug statement.
//System.out.println( "Hash size = " + hash.size( ) + ";  FD[" + i + "] blank count = " + fd[i].getBlankCount( ) + ";  Result = " + ( double )( ( double )hash.size( ) / ( ( double )records.length - ( double )fd[i].getBlankCount( ) ) ) + "." );
//End debug statement.

//Debug statement.
//System.out.println( "" );
//End debug statement.
        //As these are personal records the number of distinct, non-blank field
        //  values should be close to the total number of non-blank values.
        //  Work phone numbers present a challenge because potentially many
        //  people may work at the same number.
        if( ( double )( ( double )hash.size( ) /
                        ( ( double )records.length -
                          ( double )fdh[i].getBlankCount( ) ) ) < 0.70 )  {

//Debug statement.
//System.out.println( "Removing..." );
//End debug statement.
            re.removeEntry( fdh[i] );
        }

        hash = null;  //Explicit cleanup.
      }
    }

    fdh = null;  //Explicit cleanup.
//Debug statement.
//System.out.println( "Ending PhoneNumber postProcess()..." );
//End debug statement.
  }  /*  public void postProcess( char[][], FieldDescriptor[][],
                                  RecordEnumeration )  */


  public void postProcess( char[][][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {
    
  }  /*  public void postProcess( char[][][], FieldDescriptor[][],
                                  RecordEnumeration )  */

}  /*  public class PhoneNumberOracle2 implements Oracle, PostProcess  */
