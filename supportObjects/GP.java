package supportObjects;


public class GP  {  //Global parameters.
  public static boolean pto;  //Print text output.
  public static char[][] rd;  //Record delimiters to check for.
  public static char[][] fd;  //Field delimiters to check for.
  public static char[][] td;  //Text delimiters to check for.
  //Since find() with record delimiters is taking so long this variable will
  //  allow a simple heuristic, records to test.  Basically for each start
  //  position, length pair only RTT records are considered.  If an acceptable
  //  percentage are valid, as determined by the oracle, then the remaining
  //  records will be examined; otherwise the method will move to the next start
  //  position, length pair.
  public static int rtt;
  //After considering RTT records the number of valid hits must be greater than
  //  or equal to ARC, acceptable record count.
  public static int arc;
  //When reading a sample from a file attempt to acquire at least this number of
  //  records, optimal record count.
  public static int orc;
  //This parameter specifies the number of partitions to read from the original
  //  file from which to build the sample, the sample partition count.
  public static int spc;
  //The number of records to skip in order to avoid any header information.
  public static int hrts;
  //The number of bytes to read from the file to use as a sampling.
  public static int readSize;
  //The actual number of bytes read from the file and used as a sampling.
  public static int sampleSize;
  //The percentage of characters from the sampling that must be EBCDIC in order
  //  to assume a file is EBCDIC.
  public static double ebcdic;
  //The acceptable line up percentage.  Used in determining the length of
  //  records for fixed files without a record delimiter.  If the percentage a
  //  field occurs at a certain position is greater than or equal to this value
  //  it will be considered the correct record length.
  public static double alup;
  public static Report report = new Report( );
  //The final result is a file descriptor.
  public static final FileDescriptor result = new FileDescriptor( );
  //The access path of the header labels files that contains the knowledge base
  //  off previously encountered labels.
  public static String hlf;


  public static void defaultInitialization( )  {
    pto = false;
    rtt = 20;
    arc = 2;
    orc = 150;
    spc = 5;
    hrts = 5;
    readSize = 100000;
    sampleSize = 0;
    ebcdic = 0.10;
    alup = 0.70;
    hlf = null;

    rd = new char[3][];
    rd[0] = new char[2];
    rd[0][0] = 0x0d;
    rd[0][1] = 0x0a;
    rd[1] = new char[1];
    rd[1][0] = 0x0a;
    rd[2] = new char[1];
    rd[2][0] = 0x0d;

    fd = new char[3][1];
    fd[0][0] = 0x09;
    fd[1][0] = 0x2c;
    fd[2][0] = 0x7c;

    td = new char[2][1];
    td[0][0] = 0x22;
    td[1][0] = 0x27;
  }  /*  public static void defaultInitialization( )  */


  public static void setDelimiter( String name, String value )  {
    char[] t;
    char[][] c;
    int h,
        i = 0,
        j,
        k,
        n = 0;

//Debug statement.
//System.out.println( "VALUE = " + value + "." );
//System.out.println( "1" );
//End debug statement.
    while( ( i = value.indexOf( ';', ( i + 1 ) ) ) >= 0 )  {
      n++;
    }
//Debug statement.
//System.out.println( "2" );
//End debug statement.

    c = new char[( n + 1 )][];
//Debug statement.
//System.out.println( "C has length " + c.length + "." );
//End debug statement.

    i = 0;
    k = 0;
    while( i < value.length( ) )  {
      j = i;
      if( ( i = value.indexOf( ';', ( i + 1 ) ) ) < 0 )  {
        i = value.length( );
      }
//Debug statement.
//System.out.println( "J = " + j + ";  I = " + i + "." );
//End debug statement.

      n = 0;
      while( ( ( j = value.indexOf( ',', ( j + 1 ) ) ) >= 0 ) && ( j < i ) )  {
        n++;
      }

      c[k++] = new char[( n + 1 )];
//Debug statement.
//System.out.println( "C[" + ( k - 1 ) + "] has length " + c[( k - 1 )].length + "." );
//End debug statement.
    }
//Debug statement.
//System.out.println( "3" );
//End debug statement.

    h = 0;
    k = 0;
    for( i = 0; i < c.length; i++ )  {
      for( j = 0; j < ( c[i].length - 1 ); j++ )  {
        k = value.indexOf( ',', ( h + 1 ) );
//Debug statement.
//System.out.println( "H = " + h + ";  K = " + k + "." );
//End debug statement.
        c[i][j] = ( char )Integer.parseInt( value.substring( h, k ) );
        h = ( k + 1 );
      }

      if( ( k = value.indexOf( ';', ( h + 1 ) ) ) < 0 )  {
        k = value.length( );
      }

      c[i][j] = ( char )Integer.parseInt( value.substring( h, k ) );
      h = ( k + 1 );
    }

    for( i = 0; i < ( c.length - 1 ); i++ )  {
      k = i;
      for( j = ( i + 1 ); j < c.length; j++ )  {
        if( c[j].length > c[k].length )  {
          k = j;
        }
      }

      t = c[i];
      c[i] = c[k];
      c[k] = t;
    }

//Debug statement.
//for( i = 0; i < c.length; i++ )  {
//for( j = 0; j < ( c[i].length - 1 ); j++ )  {
//System.out.print( ( int )c[i][j] + "," );
//}
//System.out.println( ( int )c[i][j] );
//}
//System.out.println( "" );
//End debug statement.

    if( name.equalsIgnoreCase( "recorddelimiter" ) )  {
//Debug statement.
//System.out.println( "4" );
//End debug statement.
      rd = c;
    }
    else if( name.equalsIgnoreCase( "fielddelimiter" ) )  {
//Debug statement.
//System.out.println( "5" );
//End debug statement.
      fd = c;
    }
    else if( name.equalsIgnoreCase( "textdelimiter" ) )  {
//Debug statement.
//System.out.println( "6" );
//End debug statement.
      td = c;
    }
  }  /*  public static void setDelimiter( String, String )  */


  public static String delimiterToString( String type )  {
    char[][] c = null;
    int i,
        j;
    StringBuffer ret = new StringBuffer( );

    if( type.equalsIgnoreCase( "record" ) )  {
      c = rd;
    }
    else if( type.equalsIgnoreCase( "field" ) )  {
      c = fd;
    }
    else if( type.equalsIgnoreCase( "text" ) )  {
      c = td;
    }

    if( c != null )  {
      for( i = 0; i < ( c.length - 1 ); i++ )  {
        for( j = 0; j < ( c[i].length - 1 ); j++ )  {
          ret.append( ( int )c[i][j] );
          ret.append( ',' );
        }
        ret.append( ( int )c[i][j] );
        ret.append( ';' );
      }

      for( j = 0; j < ( c[i].length - 1 ); j++ )  {
        ret.append( ( int )c[i][j] );
        ret.append( ',' );
      }
      ret.append( ( int )c[i][j] );
    }

    return( ret.toString( ) );
  }  /*  public static String delimiterToString( String )  */

}  /*  public class GP  */
