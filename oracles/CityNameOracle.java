package oracles;

import dataStructures.StringLookup;
import supportObjects.NameValuePair;
import supportMethods.BasicAttributes;
import supportMethods.Setup;


public class CityNameOracle implements Oracle, AlterDatabase  {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private StringLookup lookup;  //The data structure containing the database.
  private OracleGroup og;  //The container class this oracle is a part of.
  private String on,  //Oracle name.
                 nfn,  //The name of the file containing the minimum database.
                 xfn;  //The name of the file containing the maximum database.


  public CityNameOracle( )  {  }  /*  Constructor  */


  private String clean( String s )  {
    return( s.trim( ).toLowerCase( ) );
  }  /*  private String screen( String )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    ml = 50;
    gid = 8;
    mp = 0.60;
    mbp = 0.10;
    lookup = new StringLookup( );
    this.og = og;
    on = "city";
    nfn = "/data/Cities_Top7500.dat";
    xfn = "/data/Cities_CompleteSorted.dat";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        n = nvp.getName( );
        if( n.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "minimumsourcefile" ) )  {
          nfn = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "maximumsourcefile" ) )  {
          xfn = nvp.getValue( );
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
            mbp = 1.00;
          }
        }
        else if( n.equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 8;
          }
        }
      }
    }

    lookup.setDataSource(
      og.fileLineCount( BasicAttributes.extractFileName( true, xfn ) ), xfn );

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


  public void setMinDatabase( )  {
    lookup.setDataSource(
        og.fileLineCount( BasicAttributes.extractFileName( true, nfn ) ), nfn );
  }  /*  public void setMinDatabase( )  */


  public void setMaxDatabase( )  {
    lookup.setDataSource(
        og.fileLineCount( BasicAttributes.extractFileName( true, xfn ) ), xfn );
  }  /*  public void setMaxDatabase( )  */

}  /*  public class CityNameOracle implements Oracle, AlterDatabase  */
