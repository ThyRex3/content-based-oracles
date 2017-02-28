package oracles;

import dataStructures.StringLookup;
import supportObjects.NameValuePair;
import supportMethods.BasicAttributes;
import supportMethods.Setup;


public class StreetSuffixOracle implements Oracle {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private StringLookup lookup;  //The data structure containing the database.
  private String on,  //Oracle name.
                 dbfn;  //The database file name.


  public StreetSuffixOracle( )  {  }  /*  Constructor  */


  private String clean( String s )  {
    return( s.trim( ).toLowerCase( ) );
  }  /*  private String screen( String )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    ml = 50;
    gid = 4;
    mp = 0.50;
    mbp = 0.80;
    lookup = new StringLookup( );
    on = "street suffix";
    dbfn = "/data/StreetSuffixes_CompleteSorted.dat";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        n = nvp.getName( );
        if( n.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "maximumsourcefile" ) )  {
          dbfn = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "maximumlength" ) )  {
          try  {
            ml = Integer.parseInt( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            ml = 50;
          }
        }
        else if( n.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.60;
          }
        }
        else if( n.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 0.80;
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

    lookup.setDataSource(
      og.fileLineCount( BasicAttributes.extractFileName( true, dbfn ) ), dbfn );

  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] array )  {
    return( ( lookup.search( clean( new String( array ) ) ) >= 0 ) );
  }  /*  public boolean isValid( char[] )  */


  public boolean isValid( char[] array, int sp, int len )  {
    boolean ret = ( ( sp >= 0 ) && ( len > 0 ) &&
                    ( ( sp + len ) <= array.length ) );

    if( ret )  {
      ret = ( lookup.search( clean( new String( array, sp, len ) ) ) >= 0 );
    }

    return( ret );
  }  /*  public boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    return( lookup.search( clean( field ) ) >= 0 );
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

}
