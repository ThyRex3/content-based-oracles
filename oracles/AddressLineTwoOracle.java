package oracles;

import supportMethods.Setup;
import supportObjects.NameValuePair;


public class AddressLineTwoOracle implements Oracle  {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage
  private Oracle[] oracles;
  private String on;  //The oracle's name.


  public AddressLineTwoOracle( )  {  }  /*  Constructor  */


  /**
   *   This method removes any punctuation characters from the parameter String.
   */
  private String strip( String toStrip )  {
    char[] ts = toStrip.toCharArray( ),
           ret = new char[ts.length];
    int i,
        j = 0;

    for( i = 0; i < ts.length; i++ )  {
      if( Character.isLetter( ts[i] ) || Character.isDigit( ts[i] ) ||
          Character.isSpaceChar( ts[i] ) )  {

        ret[j++] = ts[i];
      }
    }

    return( new String( ret, 0, j ) );
  }  /*  private String strip( String )  */


  /**
   * Checks whether string 's' matches an address2 type field.
   *
   * !! Assumption: components of address2 field are separated by space
   * character(s) !!
   *
   * Address2 is of the form: Unit_Designator Unit_Number [Unit_Designator
   *   Unit_Number]
   *
   *   Unit_Designator may also be replaced by a special case of the form:
   *     APO A{AEP} - (military address), FPO A{AEP} - (military address),
   *     PO Box, POBOX, or P.O. Box.
   */
  private boolean isAddress2( String s )  {
	  String[] tokens = s.split(" ");
	  String cstring;
	  boolean notdone;
	  int i = 0;		// tokens index


		// Loop twice to pickup possible 2nd secondary_unit designator & num
		for( int x = 0; x < 2; ++x)  {

	  if( i >= tokens.length )  {
			if( x == 0 )  return false;
			else return true;
		}
	  cstring = tokens[i].toLowerCase();

//Debug statement.
//System.out.println( "isAddress2() - CSTRING 1 = " + cstring + "." );
//End debug statement.
		// Look for Secondary_Unit_Designator
		if( oracles[0].isValid( strip( cstring ) ) )  ++i;
		// POBox Case
		else if ( cstring.equals( "pobox" ) || cstring.equals( "p.o.box" ) )  ++i;
		else if ( cstring.equals( "po" ) || cstring.equals( "p.o." ) )  {
			if( ++i >= tokens.length ) return false;
			if( ! tokens[i++].equals( "box" ) )  return false;
		}
		// Military Address Case
		else if ( cstring.equals( "apo" ) || cstring.equals( "fpo" ) )  {
			if( ++i >= tokens.length ) return false;
			if( tokens[i].equals( "aa" ) || tokens[i].equals( "ae" ) || tokens[i].equals( "ap" ) )  ++i;
			else return false;
		}
		else return false;


	  if( i >= tokens.length ) return false;
	  cstring = tokens[i];
//Debug statement.
//System.out.println( "isAddress2() - CSTRING 2 = " + cstring + "." );
//End debug statement.
	  notdone = true;
	  boolean foundmatch = false;
	  int j = i;

	  // Look for Secondary_Unit# (Longest possible match)
	  while( notdone )  {
		  if( i >= tokens.length )  {
				if( !foundmatch ) return false;
				else notdone = false;
			}
		  else if( oracles[1].isValid( cstring ) )  {
			  foundmatch = true;
			  j = i;
			  ++i;
			  if( i >= tokens.length )  return true;
				// append next token to check for longer match
			  cstring = cstring + " " + tokens[i];
		  }
		  else  {
			  if( !foundmatch ) return false;
			  i = ++j;
			  notdone = false;
		  }
	  }
		}


	  return true;
  }  /*  private boolean isAddress2( String s )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    oracles = new Oracle[2];

    oracles[0] = og.getOracle( og.index( "unit designator" ) );
    oracles[1] = og.getOracle( og.index( "unit number" ) );;

    ml = 50;
    gid = 4;
    mp = 0.15;
    mbp = 1.00;
    on = "address line two";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        n = nvp.getName( );
        if( n.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
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
            mp = 0.15;
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
  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] array )  {
	  return isAddress2( new String( array, 0, array.length ) );
  }  /*  boolean isValid( char[] )  */


  public boolean isValid( char[] array, int beginIndex, int length )  {
	  if( beginIndex + length > array.length ) return false;
//Debug statement.
//System.out.println( "isValid(char[],int,int) - ARRAY = " + ( new String( array, beginIndex, length ) ) + "; BeginIndex = " + beginIndex + "; Length = " + length + "." );
//End debug statement.
	  return isAddress2( new String( array, beginIndex, length ) );
  }  /*  boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
	  return isAddress2( field );
  }  /*  boolean isValid( String )  */


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

}  /*  public class AddressTwoOracle implements Oracle  */
