package main;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

import oracles.OracleGroup;
import supportObjects.GP;
import supportMethods.Setup;
import supportMethods.BasicAttributes;


public class Main  {
  public static void main( String[] args )  {
    byte[] fileData,
           parameterData;
    OracleGroup og = new OracleGroup( );
    String fe,  //Output file extension.
           layout;
    PrintWriter outf = null;

    if( args.length < 2 )  {
      System.out.println( "USAGE - $java Main configFile inputFile" );
      System.exit( 1 );
    }

    try  {
      if( ( parameterData = BasicAttributes.
                              readFilePortion( -1, args[0] ) ) != null )  {

        Setup.parseSource( parameterData, og );
      }
      else  {
        GP.defaultInitialization( );
        og.defaultInitialization( );
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

        if( ( layout = BasicAttributes.buildLayout( fileData, og, args[1] ) )
            != null )  {

          outf.print( layout );
          outf.flush( );
        }
      }

      outf.close( );
    }  catch( IOException e )  {
        System.out.println( "main - Main error 1:  IOException.  " +
                            e.getMessage( ) );

    }
  }  /*  public static void main( String[] )  */

}  /*  public class Main  */
