package oracles;

import supportMethods.Setup;
import supportMethods.BasicAttributes;
import dataStructures.LinkedList;
import dataStructures.StringLookup;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;


public class UnitDesignatorOracle implements Oracle, PreProcess  {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private StringLookup lookup;  //The data structure containing the database.
  private String on,  //Oracle name.
                 dbfn;  //The database file name.


  public UnitDesignatorOracle( )  {  }  /*  Constructor  */


  /**
  *   Strip legal non-digit characters from string S.
  */
  private String clean( String s )  {
    return( s.trim( ).toLowerCase( ) );
  }  /*  private String clean( String s )  */
/*
  private String clean( String s )  {
    StringBuffer ret = new StringBuffer( );
    char c;

    for( int i = 0; i < s.length(); ++i )  {
      c = s.charAt( i );

      ///Strip away all non-digits/non-characters.
      if( !Character.isWhitespace( c ) ) {
        ret.append( Character.toLowerCase( c ) );
      }
    }

    return( ret.toString( ) );
  }  /*  private String clean( String s )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    ml = 50;
    gid = 4;
    mp = 0.10;
    mbp = 1.00;
    lookup = new StringLookup( );
    on = "unit designator";
    dbfn = "/data/SecondaryUnitDesignator_CompleteSorted.dat";

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
            mp = 0.10;
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
//Debug statement.
//System.out.println( "|" + ( new String( array, sp, len ) ) + "|" );
//End debug statement.
      ret = ( lookup.search( clean( new String( array, sp, len ) ) ) >= 0 );
//Debug statement.
//System.out.println( "|" + clean( new String( array, sp, len ) ) + "|" );
//End debug statement.
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


  public FieldDescriptor[] preProcess( char[][] records,
                                       FieldDescriptor[] fd )  {

    int c,
        i,
        j,
        sp,
        len;
    LinkedList list;
    FieldDescriptor[] ret = null;

    if( fd != null )  {
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          c = 0;
          sp = fd[i].getPosition( );
          len = fd[i].getLength( );
          list = new LinkedList( );

          for( j = 0; j < records.length; j++ )  {
            if( isValid( records[j], sp, len ) )  {
              c++;
              list.searchAndInsert( records[j], sp, len );
            }
          }

          if( ( double )( ( double )list.length( ) / ( double )c ) <= 0.15 )  {
            fd[i] = null;
          }
        }
      }
    }

    //Count all of the NULL entries in FD.
    c = 0;
    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] == null )  {
        c++;
      }
    }

    //Remove all of the NULL entries in FD.
    if( ( fd.length - c ) > 0 )  {
      ret = new FieldDescriptor[( fd.length - c )];

      j = 0;
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          ret[j++] = fd[i];
        }
      }

      fd = ret;
    }

    return( ret );
  }  /*  FieldDescriptor[] preProcess( char[][], FieldDescriptor[] )  */


  public FieldDescriptor[] preProcess( char[][][] records,
                                       FieldDescriptor[] fd )  {

    int c,
        i,
        j,
        p;
    LinkedList list;
    FieldDescriptor[] ret = null;

    if( fd != null )  {
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          c = 0;
          p = fd[i].getPosition( );
          list = new LinkedList( );

          for( j = 0; j < records.length; j++ )  {
            if( isValid( records[j][p] ) )  {
              c++;
              list.searchAndInsert( records[j][p] );
            }
          }

          if( ( double )( ( double )list.length( ) / ( double )c ) <= 0.15 )  {
            fd[i] = null;
          }
        }
      }

      //Count all of the NULL entries in FD.
      c = 0;
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] == null )  {
          c++;
        }
      }

      //Remove all of the NULL entries in FD.
      if( ( fd.length - c ) > 0 )  {
        ret = new FieldDescriptor[( fd.length - c )];

        j = 0;
        for( i = 0; i < fd.length; i++ )  {
          if( fd[i] != null )  {
            ret[j++] = fd[i];
          }
        }

        fd = ret;
      }
    }

    return( ret );
  }  /*  FieldDescriptor[] preProcess( char[][][], FieldDescriptor[] )  */

}  /*  public class UnitDesignator implements Oracle  */
