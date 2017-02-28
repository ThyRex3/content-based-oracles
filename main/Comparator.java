package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import supportObjects.Position;
import supportObjects.Delimiter;
import supportObjects.FileDescriptor;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public class Comparator  {
  //In order not to have to pass these variables around to each method, make
  //  them global to the class.
  private static FileDescriptor actual,
                                generated;
  private static final String unsp = "* UNSPECIFIED *";
  private static String[] ct;  //Content types associated with the project.


  private static int partition( int p, int r, String[] arr )  {
    int i = ( p - 1 ),
        j;
    String s,
           x = arr[r];

    for( j = p; j < r; j++ )  {
      if( arr[j].compareTo( x ) <= 0 )  {
        s = arr[++i];
        arr[i] = arr[j];
        arr[j] = s;
      }
    }

    s = arr[++i];
    arr[i] = arr[r];
    arr[r] = s;

    return( i );
  }  /*  private static int partition( int, int, String[] )  */


  private static void quickSort( int p, int r, String[] arr )  {
    int q;

    if( p < r )  {
      q = partition( p, r, arr );
      quickSort( p, ( q - 1 ), arr );
      quickSort( ( q + 1 ), r, arr );
    }
  }  /*  private static quickSort( String[] )  */


  private static int bs( String s )  {
    boolean searching = true;
    int low = 0,
        ret = -1,
        high = ( ct.length - 1 ),
        middle;

    while( searching && ( low <= high ) )  {
      middle = ( ( low + high ) / 2 );

      if( s.equals( ct[middle] ) )  {
        ret = middle;
        searching = false;
      }
      else if( s.compareTo( ct[middle] ) < 0 )  {
        high = ( middle - 1 );
      }
      else  {
        low = ( middle + 1 );
      }
    }

    return( ret );
  }  /*  private static int bs( String )  */


/*---Methods to extract and store the incoming data.---*/
  private static Document parse( String inputFile )  {
    Document ret = null;

    try  {
      ret = DocumentBuilderFactory.newInstance( ).newDocumentBuilder( ).
              parse( new File( inputFile ) );

    }catch( SAXException e )  {
      System.out.println( "comparator - ComparatorMain error 1: SAXException " +
                          e.getMessage( ) );

      System.exit( 1 );
    }
    catch( ParserConfigurationException e )  {
      System.out.println( "comparator - ComparatorMain error 2: " +
                          "ParserConfigurationException " + e.getMessage( ) );

      System.exit( 1 );
    }
    catch( IOException e )  {
      System.out.println( "comparator - ComparatorMain error 3: IOException " +
                          e.getMessage( ) );

      System.exit( 1 );
    }

    return( ret );
  }  /*  private static Document parse( String )  */


  private static char[] extractDelimiter( Node n )  {
    boolean searching = true;
    char[] ret = null;
    int i = 0,
        j;
    String s;
    String[] arr;
    NodeList nl = n.getChildNodes( );

    while( searching && ( i < nl.getLength( ) ) )  {
      if( nl.item( i ).getNodeName( ).equals( "found" ) )  {
        if( !( s = nl.item( i ).getTextContent( ) ).equals( "" ) )  {
          arr = s.split( "," );
          ret = new char[arr.length];

          for( j = 0; j < ret.length; j++ )  {
            ret[j] = ( char )Integer.parseInt( arr[j] );
          }
        }

        searching = false;
      }
      else  {
        i++;
      }
    }

    return( ret );
  }  /*  private static char[] extractDelimiter( Node )  */


  private static Delimiter extractDelimiters( Node n )  {
    char[] c;
    int i;
    Delimiter ret = new Delimiter( );
    NodeList nl = n.getChildNodes( );

    for( i = 0; i < nl.getLength( ); i++ )  {
      if( ( c = extractDelimiter( nl.item( i ) ) ) != null )  {
        if( nl.item( i ).getNodeName( ).equals( "record" ) )  {
          ret.setRecordDelimiter( c );
        }
        else if( nl.item( i ).getNodeName( ).equals( "field" ) )  {
          ret.setFieldDelimiter( c );
        }
      }
    }

    return( ret );
  }  /*  private static Delimiter extractDelimiter( Node )  */


  private static int extractIntegerValue( String tagName, Node n )  {
    boolean searching = true;
    int i = 0,
        ret = -1;
    NodeList nl = n.getChildNodes( );

    while( searching && i < nl.getLength( ) )  {
      if( nl.item( i ).getNodeName( ).equals( tagName ) )  {
        ret = Integer.parseInt( nl.item( i ).getTextContent( ) );
        searching = false;
      }
      else  {
        i++;
      }
    }

    return( ret );
  }  /*  private static int extractIntegerValue( String, Node )  */


  private static String[] setContentTypes( Node n )  {
    int i;
    NodeList nl = n.getChildNodes( );
    ArrayList<String> holder = new ArrayList<String>( );
    String[] ret = null;

    for( i = 0; i < nl.getLength( ); i++ )  {
      if( nl.item( i ).getNodeName( ).equals( "type" ) )  {
        holder.add( nl.item( i ).getTextContent( ) );
      }
    }

    ret = new String[holder.size( )];
    for( i = 0; i < ret.length; i++ )  {
      ret[i] = holder.get( i );
    }

    quickSort( 0, ( ret.length - 1 ), ret );
//Debug statement.
//for( i = 0; i < ret.length; i++ )  {
//System.out.println( ret[i] );
//}
//End debug statement.

    return( ret );
  }  /*  private static String[] setContentTypes( Node )  */




  private static Position extractPosition( Node n )  {
    int i;
    Position ret = new Position( );
    String len;
    NodeList nl = n.getChildNodes( );

    for( i = 0; i < nl.getLength( ); i++ )  {
      if( nl.item( i ).getNodeName( ).equals( "start" ) )  {
        //Return the value to zero based.
        ret.setPosition(
              Integer.parseInt( nl.item( i ).getTextContent( ) ) - 1 );

      }
      else if( nl.item( i ).getNodeName( ).equals( "length" ) )  {
        if( !( len = nl.item( i ).getTextContent( ) ).equals( "" ) )  {
          ret.setLength( Integer.parseInt( len ) );
        }
      }
    }

    return( ret );
  }  /*  private static Position extractPosition( Node )  */




  private static FieldDescriptor extractField( Node n )  {
    int i,
        justification = -1;  //Default to LEFT.
    FieldDescriptor ret = null;
    Position p = null;
    String s,
           ct = null;  //The content type.
    NodeList nl = n.getChildNodes( );

    for( i = 0; i < nl.getLength( ); i++ )  {
      if( nl.item( i ).getNodeName( ).equals( "position" ) )  {
        p = extractPosition( nl.item( i ) );
//Debug statement.
//System.out.println( "--" );
//System.out.println( ret.getFieldPosition( ) );
//System.out.println( "---" );
//End debug statement.
      }
      else if( nl.item( i ).getNodeName( ).equals( "contenttype" ) )  {
        ct = nl.item( i ).getTextContent( );
//Debug statement.
//System.out.println( ret.getName( ) );
//End debug statement.
      }
      else if( nl.item( i ).getNodeName( ).equals( "justification" ) )  {
        if( ( s = nl.item( i ).getTextContent( ) ).equals( "RIGHT" ) )  {
          justification = 1;
        }
        else if( s.equals( "CENTER" ) )  {
          justification = 0;
        }
      }
    }

    if( ( ct != null ) && ( p != null ) )  {
      ret = new FieldDescriptor( );

      ret.setJustification( justification );
      ret.setPosition( p );
      ret.setName( ct );
    }

    return( ret );
  }  /*  private static FieldDescriptor extractField( Node )  */




  private static ArrayList<FieldDescriptor> extractFields( Node n )  {
    int i;
    FieldDescriptor fd;
    ArrayList<FieldDescriptor> ret = new ArrayList<FieldDescriptor>( );
    NodeList nl = n.getChildNodes( );
//Debug statement.
//System.out.println( "There are " + nl.getLength( ) + " fields." );
//End debug statement.

    for( i = 0; i < nl.getLength( ); i++ )  {
      if( ( fd = extractField( nl.item( i ) ) ) != null )  {
        ret.add( fd );
      }
    }

    return( ret );
  }  /*  private static ArrayList<FieldDescriptor> extractFields( Node )  */




  private static RecordEnumeration extractRecord( int recordLength, Node n )  {
    boolean searching = true;
    int i = 0;
    RecordEnumeration ret = null;
    NodeList nl = n.getChildNodes( );

//Debug statement.
//System.out.println( "extractRecord() parent node name is " + n.getNodeName( ) + "." );
//System.out.println( "extractRecord() --> There are " + nl.getLength( ) + " child nodes." );
//End debug statement.
    while( searching && ( i < nl.getLength( ) ) )  {
//Debug statement.
//System.out.println( "  extractRecord() node " + i + " name is " + nl.item( i ).getNodeName( ) + "." );
//End debug statement.
      if( nl.item( i ).getNodeName( ).equals( "fields" ) )  {
        ret = new RecordEnumeration( recordLength );
        ret.setEntries( extractFields( nl.item( i ) ) );

        searching = false;
      }
      else  {
        i++;
      }
    }

    return( ret );
  }  /*  private static RecordEnumeration extractRecord( int, Node )  */


  private static FileDescriptor store( Document doc )  {
    int i;
    FileDescriptor ret = new FileDescriptor( );
    Node n;
    NodeList nl = doc.getElementsByTagName( "layout" ).item( 0 ).
                    getChildNodes( );

    for( i = 0; i < nl.getLength( ); i++ )  {
      n = nl.item( i );

      if( n.getNodeName( ).equals( "filename" ) )  {
        ret.setFileName( n.getTextContent( ) );
      }
      else if( n.getNodeName( ).equals( "runtime" ) )  {
        ret.setRunTime( Long.parseLong( n.getTextContent( ) ) );
      }
      else if( n.getNodeName( ).equals( "encoding" ) )  {
        ret.setIsAscii( n.getTextContent( ).equals( "ASCII" ) );
      }
      else if( n.getNodeName( ).equals( "header" ) )  {
        ret.setHeader( Boolean.parseBoolean( n.getTextContent( ) ) );
      }
      else if( n.getNodeName( ).equals( "delimiters" ) )  {
        ret.setDelimiter( extractDelimiters( n ) );
      }
      else if( n.getNodeName( ).equals( "recordsexamined" ) )  {
        ret.setRecordCount( extractIntegerValue( "count", n ) );
      }
      else if( n.getNodeName( ).equals( "contenttypes" ) )  {
        ct = setContentTypes( n );
      }
      else if( n.getNodeName( ).equals( "record" ) )  {
        ret.setRecordLength( extractIntegerValue( "length", n ) );
        ret.setRecordEnumeration( extractRecord( ret.getRecordLength( ), n ) );
      }
    }

    return( ret );
  }  /*  private static FileDescriptor store( Document )  */


/*---Methods to perform comparison and print the results to the screen.---*/
  private static String fill( char c, int count )  {
    int i;
    StringBuffer ret = new StringBuffer( );

    for( i = 0; i < count; i++ )  {
      ret.append( c );
    }

    return( ret.toString( ) );
  }  /*  private static String fill( char, int )  */


  /**
   *   This method places the String, S, either LEFT or right justified, as
   * indicated by the parameter, filling the remaining ( COUNT - S.length )
   * spaces with the fill character, C.
   */
  private static String pad( boolean left, char c, int count, String s )  {
    int i;
    StringBuffer sb = new StringBuffer( ),
                 ret = new StringBuffer( );

    for( i = 0; i < ( count - s.length( ) ); i++ )  {
      sb.append( c );
    }

    if( count > s.length( ) )  {  //Fill characters must be inserted.
      if( left )  {  //Left justified.
        ret.append( s );
        ret.append( sb );
      }
      else  {
        ret = sb.append( s );
      }
    }
    else  {  //No fill characters needed.
      ret.append( s.substring( 0, count ) );
    }

    return( ret.toString( ) );
  }  /*  private static String pad( boolean, char, int, String )  */


  private static String createStartTag( String tagName )  {
    int i,
        len;
    StringBuffer ret = new StringBuffer( "<--".concat( tagName ) );

    len = ( 20 - tagName.length( ) );
    for( i = 0; i < len; i++ )  {
      ret.append( '-' );
    }
    ret.append( '>' );

    return( ret.toString( ) );
  }  /*  private static String createStartTag( String )  */


  private static String compareEncoding( )  {
    StringBuffer ret = new StringBuffer( createStartTag( "ENCODING" ) );

    ret.append( System.getProperty( "line.separator" ) );

    if( actual.getIsAscii( ) == generated.getIsAscii( ) )  {
      ret.append( "CORRECT" );
    }
    else  {
      ret.append( "ACTUAL - ".concat( fill( ' ', 3 ) ) );
      if( actual.getIsAscii( ) )  {
        ret.append( "ASCII" );
      }
      else  {
        ret.append( "EBCDIC" );
      }
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( "GENERATED - " );
      if( generated.getIsAscii( ) )  {
        ret.append( "ASCII" );
      }
      else  {
        ret.append( "EBCDIC" );
      }
    }

    ret.append( System.getProperty( "line.separator" ) );
    ret.append( System.getProperty( "line.separator" ) );

    return( ret.toString( ) );
  }  /*  private static String compareEncoding( )  */


  private static String compareFileType( )  {
    int aft,  //Actual file type.
        gft;  //Generated file type.
    StringBuffer ret = new StringBuffer( createStartTag( "FILE TYPE" ) );

    ret.append( System.getProperty( "line.separator" ) );

    if( actual.fullyDelimited( ) )  {
      aft = 3;
    }
    else if( actual.fixedWithRecordDelimiters( ) )  {
      aft = 2;
    }
    else  {
      aft = 1;
    }

    if( generated.fullyDelimited( ) )  {
      gft = 3;
    }
    else if( generated.fixedWithRecordDelimiters( ) )  {
      gft = 2;
    }
    else  {
      gft = 1;
    }

    if( aft == gft )  {
      ret.append( "CORRECT" );
    }
    else  {
      ret.append( "ACTUAL - ".concat( fill( ' ', 3 ) ) );
      ret.append( aft );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( "GENERATED - " );
      ret.append( gft );
    }

    ret.append( System.getProperty( "line.separator" ) );
    ret.append( System.getProperty( "line.separator" ) );

    return( ret.toString( ) );
  }  /*  private static String compareFileType( )  */


  private static String compareDelimiters( )  {
    StringBuffer ret = new StringBuffer( createStartTag( "DELIMITERS" ) );

    ret.append( System.getProperty( "line.separator" ) );

    ret.append( fill( ' ', 2 ).concat( "Record" ) );
    ret.append( System.getProperty( "line.separator" ) );
    if( Delimiter.equals( actual.getDelimiter( ).getRecordDelimiter( ),
                          generated.getDelimiter( ).getRecordDelimiter( ) ) )  {

      ret.append( fill( ' ', 4 ).concat( "CORRECT" ) );
    }
    else  {
      ret.append( fill( ' ', 4 ) );
      ret.append( "ACTUAL - ".concat( fill( ' ', 3 ) ) );
      ret.append( actual.getDelimiter( ).delimiterValues( true ) );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( fill( ' ', 4 ) );
      ret.append( "GENERATED - " );
      ret.append( generated.getDelimiter( ).delimiterValues( true ) );
    }

    ret.append( System.getProperty( "line.separator" ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( fill( ' ', 2 ).concat( "Field" ) );
    ret.append( System.getProperty( "line.separator" ) );
    if( Delimiter.equals( actual.getDelimiter( ).getFieldDelimiter( ),
                          generated.getDelimiter( ).getFieldDelimiter( ) ) )  {

      ret.append( fill( ' ', 4 ).concat( "CORRECT" ) );
    }
    else  {
      ret.append( fill( ' ', 4 ) );
      ret.append( "ACTUAL - ".concat( fill( ' ', 3 ) ) );
      ret.append( actual.getDelimiter( ).delimiterValues( false ) );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( fill( ' ', 4 ) );
      ret.append( "GENERATED - " );
      ret.append( generated.getDelimiter( ).delimiterValues( false ) );
    }

    ret.append( System.getProperty( "line.separator" ) );
    ret.append( System.getProperty( "line.separator" ) );

    return( ret.toString( ) );
  }  /*  private static String compareDelimiters( )  */


  private static StringBuffer compareDelimitedFile( )  {
    boolean eae = false,  //Extra actual entries?
            bsae,  //The binary search results for the current actual entry.
            bsge;  //The binary search results for the current generated entry.
    int i = 0,
        cc = 0,  //Correct count.
        uc = 0,  //Unknown ( missed ) count.
        vi,  //Index into the valid array.
        fpc = 0,  //False positive count.
        ivi,  //Index into the invalid array.
        mic = 0,  //Misidentification count.
        ncei = 0;  //Index into the corresponding entry array.
    int[] v,  //Valid entries.
          iv,  //Invalid entries.
          nce;  //No corresponding entry.
    //The entries from the actual and generated file, respectively.
    FieldDescriptor[] ae = actual.getRecordEnumeration( ).getEntries( ),
                      ge = generated.getRecordEnumeration( ).getEntries( );
    StringBuffer ret = new StringBuffer( );

    if( ae.length >= ge.length )  {
      vi = ae.length;
    }
    else  {
      vi = ge.length;
    }

    v = new int[vi];
    iv = new int[vi];
    nce = new int[vi];
    for( i = 0; i < vi; i++ )  {
      v[i] = iv[i] = nce[i] = -1;
    }
    vi = ivi = 0;  //( Re )Set the index values.

    i = 0;
    while( ( i < ae.length ) && ( i < ge.length ) )  {
      bsae = ( bs( ae[i].getName( ) ) >= 0 );
      bsge = ( bs( ge[i].getName( ) ) >= 0 );

      if( bsae || bsge )  {
        if( ae[i].getName( ).equals( ge[i].getName( ) ) )  {  //Correct.
          cc++;
          v[vi++] = i;
        }
        else if( bsge && ae[i].getName( ).equals( unsp ) )  {  //False positive.
          fpc++;
          iv[ivi++] = i;
        }
        else if( bsae )  {
          if( ge[i].getName( ).equals( unsp ) )  {  //Missed ( unknown ).
            uc++;
            iv[ivi++] = i;
          }
          else  {  //Misidentification.
            mic++;
            iv[ivi++] = i;
          }
        }
      }

      i++;
    }

    if( i < ae.length )  {
      eae = true;

      for( ; i < ae.length; i++ )  {
        nce[ncei++] = i;
      }
    }
    else if( i < ge.length )  {
      for( ; i < ge.length; i++ )  {
        nce[ncei++] = i;
      }
    }

    if( vi > 0 )  {
      ret.append( "Correct entries" );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( pad( true, ' ', 25, "Type name" ) );
      ret.append( pad( false, ' ', 5, "POS" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( fill( '-', 30 ) );
      ret.append( System.getProperty( "line.separator" ) );
    
      for( i = 0; i < vi; i++ )  {
        ret.append( pad( true, ' ', 25, ae[v[i]].getName( ) ) );
        ret.append( pad( false, ' ', 5, Integer.toString( ( v[i] + 1 ) ) ) );
        ret.append( System.getProperty( "line.separator" ) );
      }

      ret.append( System.getProperty( "line.separator" ) );
    }

    if( ivi > 0 )  {
      ret.append( "Incorrect entries" );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( pad( true, ' ', 25, "Actual - Type name" ) );
      ret.append( pad( false, ' ', 5, "POS" ) );
      ret.append( fill( ' ', 5 ) );
      ret.append( pad( true, ' ', 25, "Generated - Type name" ) );
      ret.append( pad( false, ' ', 5, "POS" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( fill( '-', 30 ) );
      ret.append( fill( ' ', 5 ) );
      ret.append( fill( '-', 30 ) );
      ret.append( System.getProperty( "line.separator" ) );
    
      for( i = 0; i < ivi; i++ )  {
        ret.append( pad( true, ' ', 25, ae[iv[i]].getName( ) ) );
        ret.append( pad( false, ' ', 5, Integer.toString( ( iv[i] + 1 ) ) ) );
        ret.append( fill( ' ', 5 ) );
        ret.append( pad( true, ' ', 25, ge[iv[i]].getName( ) ) );
        ret.append( pad( false, ' ', 5, Integer.toString( ( iv[i] + 1 ) ) ) );
        ret.append( System.getProperty( "line.separator" ) );
      }

      ret.append( System.getProperty( "line.separator" ) );
    }

    if( ncei > 0 )  {
      ret.append( "Extra entries, from " );
      if( eae )  {
        ret.append( "actual " );
      }
      else  {
        ret.append( "generated " );
      }
      ret.append( "layout".concat( System.getProperty( "line.separator" ) ) );
      ret.append( pad( true, ' ', 25, "Type name" ) );
      ret.append( pad( false, ' ', 5, "POS" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( fill( '-', 30 ) );
      ret.append( System.getProperty( "line.separator" ) );

      for( i = 0; i < ncei; i++ )  {
        if( eae )  {
          ret.append( pad( true, ' ', 25, ae[nce[i]].getName( ) ) );
        }
        else  {
          ret.append( pad( true, ' ', 25, ge[nce[i]].getName( ) ) );
        }
        ret.append( pad( false, ' ', 5, Integer.toString( ( nce[i] + 1 ) ) ) );
        ret.append( System.getProperty( "line.separator" ) );
      }

      ret.append( System.getProperty( "line.separator" ) );
    }

    ret.append( "Statistics".concat( System.getProperty( "line.separator" ) ) );
    ret.append( fill( ' ', 5 ) );
    ret.append( "Incorrect count - " );
    ret.append( Integer.toString( fpc + mic + uc + ncei ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( fill( ' ', 10 ) );
    ret.append( "False positive count - ".concat( Integer.toString( fpc ) ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( fill( ' ', 10 ) );
    ret.append( "Misidentification count - " + Integer.toString( mic ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( fill( ' ', 10 ) );
    ret.append( "Missed count - ".concat( Integer.toString( uc ) ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( fill( ' ', 10 ) );
    ret.append( "Extra count - ".concat( Integer.toString( ncei ) ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( fill( ' ', 5 ) );
    ret.append( "Correct count - ".concat( Integer.toString( cc ) ) );

    return( ret );
  }  /*  private static StringBuffer compareDelimitedFile( )  */


  private static StringBuffer compareFixedFile( )  {
    boolean sfp;  //Searching for a false positive.
    int i,
        j,
        fpi = 0,  //False positive index.
        ivi = 0;  //Invalid index.
    int[] fp,  //False positive array, points to false positive entries.
          iv;  //Invalid array, points to invalid entries.
    FieldDescriptor fdh;
    FieldDescriptor[] fdl = actual.getRecordEnumeration( ).getEntries( );
    StringBuffer ret = new StringBuffer( );
    ArrayList<FieldDescriptor> al = new ArrayList<FieldDescriptor>( );

    fp = new int[fdl.length];
    iv = new int[fdl.length];
    for( i = 0; i < iv.length; i++ )  {
      fp[i] = iv[i] = -1;
    }

    ret.append( "Correct entries" );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( pad( true, ' ', 23, "Type name" ) );
    ret.append( pad( false, ' ', 4, "SP" ) );
    ret.append( pad( false, ' ', 4, "EP" ) );
    ret.append( pad( false, ' ', 4, "LEN" ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( fill( '-', 35 ) );
    ret.append( System.getProperty( "line.separator" ) );
    
    for( i = 0; i < fdl.length; i++ )  {
      al = generated.getRecordEnumeration( ).conflict( fdl[i] );

      //Search the list of known types for the current FieldDescriptor name.
      if( bs( fdl[i].getName( ) ) >= 0 )  {
        if( ( al.size( ) == 1 ) && fdl[i].equals( al.get( 0 ) ) )  {
          ret.append( pad( true, ' ', 23, fdl[i].getName( ) ) );
          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdl[i].getPosition( ) + 1 ) ) );

          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdl[i].getPosition( ) +
                                               fdl[i].getLength( ) ) ) );

          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdl[i].getLength( ) ) ) );

          ret.append( System.getProperty( "line.separator" ) );
        }
        else  {
//Debug statement.
//if( fdl[i].getPosition( ) == 58 )  {
//System.out.println( "Conflict size = " + al.size( ) + ";  Equal = " + fdl[i].equals( al.get( 0 ) ) + "." );
//System.out.println( fdl[i].toString( ) );
//for( int k = 0; k < al.size( ); k++ )  {
//System.out.println( al.get( k ) );
//}
//}
//System.out.println( "INVALID -- " + fdl[i].getName( ) + " - " + fdl[i].getPosition( ) + ", " + fdl[i].getLength( ) );
//System.out.println( "" );
//End debug statement.
          iv[ivi++] = i;
        }
      }
      else  {  //If it is not a known type then check for a false positives.
        sfp = true;
        j = 0;

        while( sfp && ( j < al.size( ) ) )  {
          if( bs( al.get( j++ ).getName( ) ) >= 0 )  {
            sfp = false;
          }
        }

        if( !sfp )  {  //A false positive was found.
          fp[fpi++] = i;
        }
      } 
    }

    if( ivi > 0 )  {
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( "Incorrect entries - not false positives" );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( pad( true, ' ', 23, "Actual - Type name" ) );
      ret.append( pad( false, ' ', 4, "SP" ) );
      ret.append( pad( false, ' ', 4, "EP" ) );
      ret.append( pad( false, ' ', 4, "LEN" ) );
      ret.append( fill( ' ', 5 ) );
      ret.append( pad( true, ' ', 23, "Generated - Type name" ) );
      ret.append( pad( false, ' ', 4, "SP" ) );
      ret.append( pad( false, ' ', 4, "EP" ) );
      ret.append( pad( false, ' ', 4, "LEN" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( fill( '-', 35 ) );
      ret.append( fill( ' ', 5 ) );
      ret.append( fill( '-', 35 ) );
      ret.append( System.getProperty( "line.separator" ) );
    
      for( i = 0; i < ivi; i++ )  {
        al = generated.getRecordEnumeration( ).conflict( fdl[iv[i]] );
        fdh = al.get( 0 );

        ret.append( pad( true, ' ', 23, fdl[iv[i]].getName( ) ) );
        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdl[iv[i]].getPosition( ) + 1 ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdl[iv[i]].getPosition( ) +
                                             fdl[iv[i]].getLength( ) ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdl[iv[i]].getLength( ) ) ) );

        ret.append( fill( ' ', 5 ) );
        ret.append( pad( true, ' ', 23, fdh.getName( ) ) );
        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdh.getPosition( ) + 1 ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdh.getPosition( ) +
                                             fdh.getLength( ) ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdh.getLength( ) ) ) );

        ret.append( System.getProperty( "line.separator" ) );

        for( j = 1; j < al.size( ); j++ )  {
          fdh = al.get( j );

          ret.append( fill( ' ', 40 ) );
          ret.append( pad( true, ' ', 23, fdh.getName( ) ) );
          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdh.getPosition( ) + 1 ) ) );

          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdh.getPosition( ) +
                                               fdh.getLength( ) ) ) );

          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdh.getLength( ) ) ) );

          ret.append( System.getProperty( "line.separator" ) );
        }
      }

      ret.append( System.getProperty( "line.separator" ) );
    }

    if( fpi > 0 )  {
      ret.append( "Incorrect entries - false positives" );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( pad( true, ' ', 23, "Actual - Type name" ) );
      ret.append( pad( false, ' ', 4, "SP" ) );
      ret.append( pad( false, ' ', 4, "EP" ) );
      ret.append( pad( false, ' ', 4, "LEN" ) );
      ret.append( fill( ' ', 5 ) );
      ret.append( pad( true, ' ', 23, "Generated - Type name" ) );
      ret.append( pad( false, ' ', 4, "SP" ) );
      ret.append( pad( false, ' ', 4, "EP" ) );
      ret.append( pad( false, ' ', 4, "LEN" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( fill( '-', 35 ) );
      ret.append( fill( ' ', 5 ) );
      ret.append( fill( '-', 35 ) );
      ret.append( System.getProperty( "line.separator" ) );
    
      for( i = 0; i < fpi; i++ )  {
        al = generated.getRecordEnumeration( ).conflict( fdl[fp[i]] );
        fdh = al.get( 0 );

        ret.append( pad( true, ' ', 23, fdl[fp[i]].getName( ) ) );
        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdl[fp[i]].getPosition( ) + 1 ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdl[fp[i]].getPosition( ) +
                                             fdl[fp[i]].getLength( ) ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdl[fp[i]].getLength( ) ) ) );

        ret.append( fill( ' ', 5 ) );
        ret.append( pad( true, ' ', 23, fdh.getName( ) ) );
        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdh.getPosition( ) + 1 ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdh.getPosition( ) +
                                             fdh.getLength( ) ) ) );

        ret.append( pad( false, ' ', 4,
                           Integer.toString( fdh.getLength( ) ) ) );

        ret.append( System.getProperty( "line.separator" ) );

        for( j = 1; j < al.size( ); j++ )  {
          fdh = al.get( j );

          ret.append( fill( ' ', 40 ) );
          ret.append( pad( true, ' ', 23, fdh.getName( ) ) );
          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdh.getPosition( ) + 1 ) ) );

          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdh.getPosition( ) +
                                               fdh.getLength( ) ) ) );

          ret.append( pad( false, ' ', 4,
                             Integer.toString( fdh.getLength( ) ) ) );

          ret.append( System.getProperty( "line.separator" ) );
        }
      }
    }

    return( ret );
  }  /*  private static StringBuffer compareFixedFile( )  */


  private static StringBuffer compareHeaders( )  {
    StringBuffer ret = new StringBuffer( createStartTag( "HEADER RECORD" ) );

    ret.append( System.getProperty( "line.separator" ) );


    if( actual.getHeader( ) == generated.getHeader( ) )  {
      ret.append( "CORRECT" );
    }
    else  {
      if( actual.getHeader( ) )  {
        ret.append( "ACTUAL - Header exists" );
        ret.append( System.getProperty( "line.separator" ) );
        ret.append( "GENERATED - No header exists" );
      }
      else  {
        ret.append( "ACTUAL - No header exists" );
        ret.append( System.getProperty( "line.separator" ) );
        ret.append( "GENERATED - Header exists" );
      }
    }

    ret.append( System.getProperty( "line.separator" ) );
    ret.append( System.getProperty( "line.separator" ) );

    return( ret );
  }  /*  private static String compareHeaders( )  */


  private static String compareRecord( )  {
    StringBuffer ret = new StringBuffer(
                         createStartTag( "RECORD ENUMERATION" ) );

    ret.append( System.getProperty( "line.separator" ) );

    if( actual.fullyDelimited( ) )  {
      ret.append( "Fully delimited file..." );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( compareDelimitedFile( ) );
    }
    else if( actual.fixedWithRecordDelimiters( ) )  {
      ret.append( "Fixed file with record delimiters..." );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( compareFixedFile( ) );
    }
    else if( actual.fixedWithoutRecordDelimiters( ) )  {
      ret.append( "Fixed file without record delimiters..." );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( compareFixedFile( ) );
    }

    return( ret.toString( ) );
  }  /*  private static String compareRecord( )  */


  private static String compare( )  {
    StringBuffer ret = new StringBuffer( );

    ret.append( compareEncoding( ) );

    ret.append( compareFileType( ) );

    if( actual.fullyDelimited( ) && generated.fullyDelimited( ) )  {
      ret.append( compareHeaders( ) );
    }

    ret.append( compareDelimiters( ) );

//Debug statement.
//System.out.println( actual.fullyDelimited( ) );
//System.out.println( actual.fixedWithRecordDelimiters( ) );
//System.out.println( Boolean.toString( actual.fixedWithoutRecordDelimiters( ) && generated.fixedWithoutRecordDelimiters( ) ).toUpperCase( ) );
//End debug statement.
    if( ( actual.fullyDelimited( ) && generated.fullyDelimited( ) ) ||
        ( actual.fixedWithRecordDelimiters( ) &&
            generated.fixedWithRecordDelimiters( ) ) ||
        ( actual.fixedWithoutRecordDelimiters( ) &&
            generated.fixedWithoutRecordDelimiters( ) ) )  {

      ret.append( compareRecord( ) );
    }

    return( ret.toString( ) );
  }  /*  private static String compare( )  */


/*---Main method, entry point for the program.---*/
  public static void main( String[] args )  {
    Document d;

    if( args.length < 2 )  {
      System.out.println( "USAGE - $java comparator.ComparatorMain " +
                          "ActualLayoutFile GeneratedLayoutFile" );

      System.exit( 1 );
    }

    if( ( d = parse( args[0] ) ) == null )  {
      System.out.println( "Unable to parse the actual layout file." );
      System.exit( 1 );
    }

    actual = store( d );
//Debug statement.
//System.out.println( "--actual----------" );
//System.out.println( actual.getFileName( ) );
//System.out.println( actual.getRunTime( ) );
//System.out.println( actual.getDelimiter( ).toString( ) );
//System.out.println( actual.getRecordCount( ) );
//System.out.println( actual.getRecordLength( ) );
//System.out.println( actual.getRecordEnumeration( ).toString( ) );
//System.out.println( "" );
//System.out.println( "" );
//End debug statement.

    if( ( d = parse( args[1] ) ) == null )  {
      System.out.println( "Unable to parse the generated layout file." );
      System.exit( 1 );
    }

    generated = store( d );
//Debug statement.
//System.out.println( "--generated-------" );
//System.out.println( generated.getFileName( ) );
//System.out.println( generated.getRunTime( ) );
//System.out.println( generated.getDelimiter( ).toString( ) );
//System.out.println( generated.getRecordCount( ) );
//System.out.println( generated.getRecordLength( ) );
//System.out.println( generated.getRecordEnumeration( ).toString( ) );
//End debug statement.

    d = null;  //Explicitly free up memory.

    System.out.println( compare( ) );
  }  /*  public static void main( String[] )  */

}  /*  public class ComparatorMain  */
