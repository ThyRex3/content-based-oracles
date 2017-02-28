package main;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceFactory;
import javax.xml.rpc.ServiceException;

import bean.Layout;
import supportMethods.BasicAttributes;
//import supportObjects.GP;
import supportMethods.Setup;
import supportObjects.GP;


public class Layout_EJB_Client  {
  public static void main( String[] args )  {
    byte[] parameterData = null,
           fileData;
    String fe,  //Output file extension.
           layout;
    PrintWriter outf;

    if( args.length < 2 )  {
      System.out.println( "USAGE - $java main.Layout_EJB_Client configFile " +
                          "inputFile" );

      System.exit( 1 );
    }

    try  {
      Layout proxy = ( Layout )ServiceFactory.newInstance( ). createService( 
                      new URL( "http://127.0.0.1:2626/layout/LayoutBean?wsdl" ),
                      new QName( "http://bean/", "LayoutBeanService" ) ).
                     getPort( Layout.class );

      //Read in any parameters allowing for errors in the file.
      if( ( parameterData = BasicAttributes.
                              readFilePortion( -1, args[0] ) ) != null )  {

        Setup.parseSource( parameterData, null );
      }
      else  {
        GP.defaultInitialization( );  //The default scenario.
      }

      if( GP.pto )  {
        fe = ".out";
      }
      else  {
        fe = ".xml";
      }

      outf = new PrintWriter( new FileWriter(
                      BasicAttributes.extractFileName( true, args[1] ) + "_" +
                      Long.toString( System.currentTimeMillis( ) ) + fe ) );

      //Extract a sample from the input file.
      if( ( fileData = BasicAttributes.
                         readFilePortion( GP.readSize, args[1] ) ) != null )  {

        //Determine the layout from the sample.
        if( ( layout = proxy.buildLayout( parameterData, fileData, args[1] ) )
            != null )  {

          outf.print( layout );
          outf.flush( );
        }

        outf.close( );
      }
    }catch( MalformedURLException e )  {
      System.out.println( "main - Layout_EJB_Client error 1:  " +
                          "MalformedURLException.  " + e.getMessage( ) );

    }
    catch( ServiceException e )  {
      System.out.println( "main - Layout_EJB_Client error 2:  " +
                          "ServiceException.  " + e.getMessage( ) );

    }
    catch( IOException e )  {
      System.out.println( "main - Layout_EJB_Client error 3:  " +
                          "IOException.  " + e.getMessage( ) );

    }
  }  /*  public static void main( String[] )  */

}  /*  public class Client  */
