package oracles;

import dataStructures.Node;
import dataStructures.LinkedList;
import supportMethods.Setup;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public class DirectionalOracle implements Oracle, PreProcess, PostProcess  {
  private int ml, //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private String on;  //The oracle's name.
  private OracleGroup og;


  public DirectionalOracle()  {  }  /*  Constructor  */


  /**
  * Strip legal non-digit characters from string v
  */
  private String strip( String v )  {
    String ret = new String();
    char c;

    for( int i = 0; i < v.length(); ++i )  {
      c = v.charAt( i );
      // strip away all non-digits/non-characters
      if( Character.isDigit( c ) || Character.isLetter( c ) ) ret += c;
    }

    return ret;
  }


  /**
   * Checks whether string 's' matches a Directional string
   */
  private boolean isDirectional( String s )  {
	  String sLower = strip( s.trim().toLowerCase() );
	  System.out.println(sLower);
	  if( sLower.equals( "n" )
			  || sLower.equals( "e" )
			  || sLower.equals( "s" )
			  || sLower.equals( "w" )
			  || sLower.equals( "ne" )
			  || sLower.equals( "nw" )
			  || sLower.equals( "se" )
			  || sLower.equals( "sw" )
			  || sLower.equals( "north" )
			  || sLower.equals( "east" )
			  || sLower.equals( "south" )
			  || sLower.equals( "west" )
			  || sLower.equals( "northeast" )
			  || sLower.equals( "northwest" )
			  || sLower.equals( "southeast" )
			  || sLower.equals( "southwest" )
			  )  {
		  return true;
			  }
	  return false;
  }  /*  private boolean isDirectional( String s )  */


/*
  private boolean isBlank( char[] arr, int sp, int len )  {
    boolean ret = true;
    int ep = ( sp + len );

    if( arr != null )  {
      if( ( sp >= 0 ) && ( len > 0 ) && ( ep <= arr.length ) )  {
        while( ret && ( sp < ep ) )  {
          if( Character.isWhitespace( arr[sp] ) )  {
            sp++;
          }
          else  {
            ret = false;
          }
        }
      }
    }

    return( ret );
  }  /*  private boolean isBlank( char[], int, int )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    ml = 20;
    gid = 4;
    mp = 0.60;
    mbp = 1.00;
    on = "directional";
    this.og = og;

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
            ml = 20;
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
            gid = 4;
          }
        }
      }
    }
  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] array )  {
	  return isDirectional( new String( array, 0, array.length ) );
  }  /*  boolean isValid( char[] )  */


  public boolean isValid( char[] array, int beginIndex, int length )  {
	  if( beginIndex + length >= array.length ) return false;
	  return isDirectional( new String( array, beginIndex, length ) );
  }  /*  boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
	  return isDirectional( field );
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


  public FieldDescriptor[] preProcess( char[][] records,
                                       FieldDescriptor[] fd )  {

    int c = 0,
        i,
        j,
        sp,
        len;
    Node n;
    LinkedList list;
    FieldDescriptor[] ret = null;

    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] != null )  {
        sp = fd[i].getPosition( );
        len = fd[i].getLength( );
        list = new LinkedList( );

        for( j = 0; j < records.length; j++ )  {
          if( isValid( records[j], sp, len ) )  {
            list.searchAndInsert( records[j], sp, len );
          }
        }

        //If all or predominately most of the entries are a single, common value
        //  then remove this FieldDescriptor as it does not conform to assumed
        //  statistical distribution.
        n = list.maxCount( );
        if( ( n != null ) && ( list.length( ) < 3 ) &&
            ( ( double )( ( double )n.getCount( ) /
                          ( double )list.totalCount( ) ) > 0.95 ) )  {

          c++;  //Increment the NULL count.
          fd[i] = null;
        }

        list.clear( );
        list = null;
      }
      else  {  //A NULL entry in FD.
        c++;
      }
    }

    j = 0;
    ret = new FieldDescriptor[( fd.length - c )];
    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] != null )  {
        ret[j++] = fd[i];
      }
    }

    return( ret );
  }  /*  public FieldDescriptor[] preProcess( char[][], FieldDescriptor[] )  */


  public FieldDescriptor[] preProcess( char[][][] records,
                                       FieldDescriptor[] fd )  {

    int c = 0,
        i,
        j,
        p;
    Node n;
    LinkedList list;
    FieldDescriptor[] ret = null;

    if( fd != null )  {
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {  //A precautionary test.
          p = fd[i].getPosition( );
          list = new LinkedList( );

          for( j = 0; j < records.length; j++ )  {
//Debug statement.
//if( i == 0 && j > 0 )  {
//System.out.println( "J = 0;  Records[j].length = " + records[0].length + "." );
//if( records[j-1].length != records[j].length )  {
//System.out.println( "J = " + j + ";  Records[j].length = " + records[j].length + "." );
//}
//}
//End debug statement.
            if( ( p < records[j].length ) && isValid( records[j][p] ) )  {
              list.searchAndInsert( records[j][p] );
            }
          }

          //If all or predominately most of the entries are a single, common
          //  value then remove this FieldDescriptor as it does not conform to
          //  assumed statistical distribution.
          n = list.maxCount( );
//Debug statement.
//if( p > 60 && p < 70 )  {
//if( n != null )  {
//System.out.println( "P = " + p + ";  list len == " + list.length( ) + ";  List total count = " + list.totalCount( ) + ";  N count = " + n.getCount( ) );
//} 
//else  {
//System.out.println( "N == NULL" );
//}
//}
//End debug statement.
          if( ( n != null ) && ( list.length( ) < 3 ) &&
              ( ( double )( ( double )n.getCount( ) /
                            ( double )list.totalCount( ) ) > 0.90 ) )  {

            c++;  //Increment the NULL count.
            fd[i] = null;
          }

          list.clear( );
          list = null;
        }
        else  {  //A NULL entry in FD.
          c++;
        }
      }

      j = 0;
      ret = new FieldDescriptor[( fd.length - c )];
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          ret[j++] = fd[i];
        }
      }
    }

    return( ret );
  }  /*  public FieldDescriptor[] preProcess( char[][][],
                                              FieldDescriptor[] )  */


  /**
   *   At a high level, the reasoning is basically that a Directional field
   * will usually be surrounded by or adjacent to other AddressLine parts
   * such as StreetName, StreetSuffix, and UnitDesignator.  If this is not the
   * case then it might not be a Directional.
   */
  public void postProcess( char[][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    boolean adj;  //Is an AddressLineOne part adjacent to this field.
    int i,
        j;
    int[] api = { og.index( "street name" ),  //Adjacent part index.
                  og.index( "street suffix" ),
                  og.index( "unit designator" ) };

    FieldDescriptor fdh;
    FieldDescriptor[] fda = re.ofType( og.index( on ) );

    if( fda != null )  {  //There were StreetNumbers in the RE.
//Debug statment.
//System.out.println( "Found " + fda.length + " StreetNumber fields." );
//End debug statement.
      for( i = 0; i < fda.length; i++ )  {
        adj = false;  //Reset the flag for this iteration.

//Debug statment.
//System.out.println( "Entry " + ( i + 1 ) + " - " + ( fda[i].getPosition( ) + 1 ) + ", " + ( fda[i].getPosition( ) + fda[i].getLength( ) ) + ", " + fda[i].getLength( ) + "." );
//End debug statement.
        //Acquire the FieldDescriptor of the preceeding field.
        if( ( fdh = re.type( fda[i].getPosition( ) - 1 ) ) != null )  {
          //If the immediately preceeding field is UNSPECIFIED and its length is
          //  less than ten then it might just be a fluke field, whitespace or
          //  something, and should be disregarded.
          if( ( fdh.getType( ) < 0 ) &&
              ( ( fda[i].getPosition( ) - fdh.getPosition( ) ) < 10 ) )  {

            fdh = re.type( fdh.getPosition( ) - 1 );
          }

          if( fdh != null )  {
            //Determine if the content type of the preceeding field is equal to
            //  an AddressLineOne part.
            j = 0;
            while( !adj && ( j < api.length ) )  {
              if( fdh.getType( ) != api[j] )  {
                j++;
              }
              else  {
//Debug statment.
//System.out.println( "Found an adjacent field before (" + og.name( api[j] ) + ").  " + ( fda[i].getPosition( ) + 1 ) );
//End debug statement.
                adj = true;
              }
            }
          }
        }

        if( !adj )  {
          //Acquire the FieldDescriptor of the following field.
          if( ( fdh = re.type( fda[i].getPosition( ) + fda[i].getLength( ) ) )
              != null )  {

            if( ( fdh.getType( ) < 0 ) &&
                ( ( fdh.getPosition( ) -
                  ( fda[i].getPosition( ) + fda[i].getLength( ) ) ) < 10 ) )  {

              fdh = re.type( fdh.getPosition( ) + fdh.getLength( ) );
            }

            if( fdh != null )  {
              j = 0;
              while( !adj && ( j < api.length ) )  {
                if( fdh.getType( ) != api[j] )  {
                  j++;
                }
                else  {
//Debug statment.
//System.out.println( "Found an adjacent field after(" + og.name( api[j] ) + ").  " + ( fda[i].getPosition( ) + fda[i].getLength( ) ) );
//End debug statement.
                  adj = true;
                }
              }
            }
          }
        }

        if( !adj )  {
//Debug statment.
//System.out.println( "Removing entry." );
//End debug statement.
          re.removeEntry( fda[i] );
        }
//Debug statment.
//System.out.println( "" );
//End debug statement.
      }
    }
  }  /*  public void postProcess( char[][] records, FieldDescriptor[][],
                                  RecordEnumeration re )  */


  public void postProcess( char[][][] records, FieldDescriptor[][] fd, 
                           RecordEnumeration re )  {
    boolean adj;  //Is an AddressLineOne part adjacent to this field.
    int i,
        j,
        k;
    int[] api = { og.index( "street name" ),  //Adjacent part index.
                  og.index( "street suffix" ),
                  og.index( "unit designator" ) };

    FieldDescriptor fdh;
    FieldDescriptor[] fda = re.ofType( og.index( on ) );

    if( fda != null )  {  //There were Directionals in the RE.
      for( i = 0; i < fda.length; i++ )  {
        adj = false;

        //Acquire the FieldDescriptor of the preceeding field.
        j = ( fda[i].getPosition( ) - 1 );
        while( !adj && ( j >= fda[i].getPosition( ) - 2 ) )  {
          if( ( ( fdh = re.type( j ) ) != null ) && ( fdh.getType( ) >= 0 ) )  {
            k = 0;

            //Determine if the content type of the preceeding field is equal to
            //  an AddressLineOne part.
            while( !adj && ( k < api.length ) )  {
              if( fdh.getType( ) != api[k] )  {
                k++;
              }
              else  {
                adj = true;
              }
            }
          }

          j++;
        }

        if( !adj )  {
          j = ( fda[i].getPosition( ) + 1 );
          while( !adj && ( j <= fda[i].getPosition( ) + 2 ) )  {
            //Acquire the FieldDescriptor of the following field.
            if( ( ( fdh = re.type( j ) ) != null ) &&
                ( fdh.getType( ) >= 0 ) )  {

              k = 0;

              //Determine if the content type of the preceeding field is equal
              //  to an AddressLineOne part.
              while( !adj && ( k < api.length ) )  {
                if( fdh.getType( ) != api[k] )  {
                  k++;
                }
                else  {
                  adj = true;
                }
              }
            }

            j++;
          }
        }

        if( !adj )  {
          re.removeEntry( fda[i] );
        }
      }
    }
  }  /*  public void postProcess( char[][][] records, FieldDescriptor[][],
                                  RecordEnumeration re )  */

}  /*  public class Directional  */
