package supportObjects;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import oracles.OracleGroup;
import supportMethods.Setup;


public class ReadInput extends DefaultHandler  {
  private boolean read;
  private int state;
  private ConfigurationPart cp;
  private OracleGroup og;
  StringBuffer rs;  //The running string.


  public ReadInput( OracleGroup og )  {
    read = false;
    state = -1;
    cp = null;
    this.og = og;
  }


  public void startElement( String uRI, String localName, String qName,
                            Attributes attributes ) throws SAXException  {

    if( qName.equalsIgnoreCase( "global_parameters" ) )  {
      state = 0;
      cp = new ConfigurationPart( "global_parameters" );
    }
    else if( qName.equalsIgnoreCase( "oracle" ) )  {
      state = 1;

      cp = new ConfigurationPart( );
    }
    else if( qName.equalsIgnoreCase( "qualified_name" ) )  {
      read = true;
      state = 2;
      rs = new StringBuffer( );
    }
    else if( qName.equalsIgnoreCase( "rank" ) )  {
      read = true;
      state = 3;
      rs = new StringBuffer( );
    }
    else if( qName.equalsIgnoreCase( "parameter" ) )  {
      read = true;
      state = 4;
      rs = new StringBuffer( );
    }
  }  /*  public void startElement( String, String, String, Attributes )
                                   throws SAXException  */


  public void endElement( String uri, String localName, String qName )
                throws SAXException  {

    if( qName.equalsIgnoreCase( "global_parameters" ) )  {
      state = -1;

//Debug statement.
//System.out.println( "Calling Setup.setGlobalParameters()..." );
//End debug statement.
      Setup.setGlobalParameters( cp );
    }
    if( qName.equalsIgnoreCase( "oracle" ) )  {
      state = -1;

//Debug statement.
//System.out.println( "Calling OG.addOracle() on oracle " + cp.getQualifiedName( ) + "." );
//End debug statement.
      if( og != null )  {
        og.addOracle( cp );
      }
    }
    else if( qName.equalsIgnoreCase( "qualified_name" ) )  {
      read = false;
      state = -1;
      cp.setQualifiedName( rs.toString( ) );
    }
    else if( qName.equalsIgnoreCase( "rank" ) )  {
      read = false;
      state = -1;
      cp.setRank( Integer.parseInt( rs.toString( ) ) );
    }
    else if( qName.equalsIgnoreCase( "parameter" ) )  {
      read = false;
      state = -1;
      cp.addParameter( rs.toString( ) );
    }
  }  /*  public void endElement( String ) throws SAXException  */


  public void characters( char[] ch, int start, int len )
                          throws SAXException  {

    if( read )  {
      switch( state )  {
        case 2:  //qualified_name
        case 3:  //rank
        case 4:  //parameter
//Debug statement.
//System.out.println( "QN:  |" + ( new String( ch, start, len ) ) + "|" );
//End debug statement.
          rs.append( ch, start, len );
//          cp.setQualifiedName( new String( ch, start, len ) );
//          break;
//Debug statement.
//System.out.println( "RANK:  |" + ( new String( ch, start, len ) ) + "|" );
//End debug statement.
//          rs.append( ch, start, len );
//          cp.setRank( Integer.parseInt( new String( ch, start, len ) ) );
//          break;
//Debug statement.
//System.out.println( "PARAMETER:  |" + ( new String( ch, start, len ) ) + "|" );
//End debug statement.
//          rs.append( ch, start, len );
//          cp.addParameter( new String( ch, start, len ) );
          break;
      }
    }
  }  /*  public void characters( char, int, int ) throws SAXException  */
  
}  /*  public class ReadInput extends DefaultHandler  */
