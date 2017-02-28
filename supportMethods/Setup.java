package supportMethods;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.ParserConfigurationException;

import oracles.OracleGroup;
import supportObjects.GP;
import supportObjects.ReadInput;
import supportObjects.NameValuePair;
import supportObjects.ConfigurationPart;


public class Setup  {
  public static NameValuePair separate( String parameter )  {
    int i;
    NameValuePair ret = null;

    if( ( parameter != null ) && ( ( i = parameter.indexOf( ':' ) ) > 0 ) )  {
      ret = new NameValuePair( parameter.substring( 0, i ),
                               parameter.substring( i + 1 ) );

    }

    return( ret );
  }  /*  public static NameValuePair separate( String )  */


  public static void setGlobalParameters( ConfigurationPart cp )  {
    int i,
        length = cp.parameterCount( );
    NameValuePair nvp;
    String n,
           v;

//Debug statement.
//System.out.println( "Parameter count = " + length + "." );
//End debug statement.
    for( i = 0; i < length; i++ )  {
      nvp = separate( cp.getParameter( i ) );
      n = nvp.getName( );
      v = nvp.getValue( );

      if( n.equalsIgnoreCase( "printtextoutput" ) )  {
//Debug statement.
//System.out.println( "PrintTextOutput." );
//End debug statement.
        GP.pto = Boolean.parseBoolean( v );
      }
      else if( n.equalsIgnoreCase( "recordstotest" ) )  {
        GP.rtt = Integer.parseInt( v );
//Debug statement.
//System.out.println( "RTT = " + GP.rtt + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "acceptablerecordcount" ) )  {
        GP.arc = Integer.parseInt( v );
//Debug statement.
//System.out.println( "ARC = " + GP.arc + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "optimalrecordcount" ) )  {
        GP.orc = Integer.parseInt( v );
//Debug statement.
//System.out.println( "ORC = " + GP.orc + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "samplepartitioncount" ) )  {
        GP.spc = Integer.parseInt( v );
//Debug statement.
//System.out.println( "SPC = " + GP.spc + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "headerrecordstoskip" ) )  {
        GP.hrts = Integer.parseInt( v );
//Debug statement.
//System.out.println( "HRTS = " + GP.hrts + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "samplesize" ) )  {
        GP.readSize = Integer.parseInt( v );
//Debug statement.
//System.out.println( "READ_SIZE = " + GP.readSize + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "ebcdicpercentage" ) )  {
        GP.ebcdic = Double.parseDouble( v );
//Debug statement.
//System.out.println( "EBCDIC = " + GP.ebcdic + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "lineuppercentage" ) )  {
        GP.alup = Double.parseDouble( v );
//Debug statement.
//System.out.println( "ALUP = " + GP.alup + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "headerlabelsourcefile" ) )  {
        GP.hlf = v;
//Debug statement.
//System.out.println( "HLF = " + GP.hlf + "." );
//End debug statement.
      }
      else if( n.equalsIgnoreCase( "recorddelimiter" ) ||
               n.equalsIgnoreCase( "fielddelimiter" ) ||
               n.equalsIgnoreCase( "textdelimiter" ) )  {

//Debug statement.
//System.out.println( "Name = " + n + ".  I = " + i + "." );
//End debug statement.
        GP.setDelimiter( n, v );
      }
    }
  }  /*  public static void setGlobalParameters( ConfigurationPart )  */


  public static void parseSource( byte[] sourceData, OracleGroup og )  {
    SAXParser sp;

    try  {
      sp = SAXParserFactory.newInstance( ).newSAXParser( );
      sp.parse( new ByteArrayInputStream( sourceData ), new ReadInput( og ) );
    }catch( SAXException e )  {
      System.out.println( "supportMethods - Setup error 1: " +
                          "SAXException.  " + e.getMessage( ) );

      System.exit( 1 );
    }catch( ParserConfigurationException e )  {
      System.out.println( "supportMethods - Setup error 2: " +
                          "ParserConfigurationException.  " + e.getMessage( ) );

      System.exit( 1 );
    }catch( IOException e )  {
      System.out.println( "supportMethods - Setup error 3: " +
                          "IOException.  " + e.getMessage( ) );

      System.exit( 1 );
    }
  }  /*  public static void parseSource( byte[], OracleGroup )  */

}  /*  public class Setup  */
