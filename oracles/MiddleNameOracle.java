/**
 *   The middle name oracle implemented here is an oracle whose results depend
 * entirely on other oracles, specifically the name oracles.  As a consequence
 * its isValid() methods always return false and the getMaxLength() always
 * returns zero.  The postProcess() methods are used to assign this type to the
 * RecordEnumeration object.
 */

package oracles;

import java.util.ArrayList;

import supportMethods.Setup;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public class MiddleNameOracle implements Oracle, PostProcess  {
  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;  //Maximum blank percentage.
  private OracleGroup og;
  private String on;  //The oracle's name.


  public MiddleNameOracle( )  {  }  /*  Constructor  */


  private boolean blank( char[] arr, int sp, int len )  {
    boolean ret = true;
    int i = sp,
        ep = ( sp + len );

    while( ret && ( i < ep ) )  {
      if( Character.isWhitespace( arr[i] ) )  {
        i++;
      }
      else  {
        ret = false;
      }
    }

    return( ret );
  }  /*  private void blank( char[], int, int )  */


  private boolean blank( char[] arr )  {
    return( blank( arr, 0, arr.length ) );
  }  /*  private boolean blank( char[] )  */


  /**
   *   Sorts the PFL entries based on the associated start position.
   *
   *   Use insertion sort because the number of entries in ARR should be small
   * and possibly could be in near order already.
   */
  private void insertionSort( FieldDescriptor[] arr )  {
    int i,
        j;
    FieldDescriptor index;

    for( i = 1; i < arr.length; i++ )  {
      index = arr[i];

      j = i;
      while( ( j > 0 ) &&
             ( arr[( j - 1 )].getPosition( ) > index.getPosition( ) ) )  {

        arr[j] = arr[( j - 1 )];
        j--;
      }

      arr[j] = index;
    }
  }  /*  private void insertionSort( FieldDescriptor[] )  */


  private FieldDescriptor[] buildNameList( OracleGroup og,
                                           RecordEnumeration re )  {

    int i,
        j,
        type;
    FieldDescriptor[] ret = null;
    String[] nfn = { "first name", "last name" };
    ArrayList<FieldDescriptor> fdal = new ArrayList<FieldDescriptor>( );

    //Find all PFLs associated with a name content type existing in the RE.
    for( i = 0; i < nfn.length; i++ )  {  //Each name type.
      if( ( type = og.index( nfn[i] ) ) >= 0 )  {
        if( ( ret = re.ofType( type ) ) != null )  {
          for( j = 0; j < ret.length; j++ )  {  //Each PFL for this type.
            fdal.add( ret[j] );  //Insert the entries into a dynamic list.
          }
        }
      }
    }

    //Once a complete list is acquired place it in a structure more conducive to
    //  processing.
    if( fdal.size( ) > 0 )  {
      ret = new FieldDescriptor[fdal.size( )];
      for( i = 0; i < ret.length; i++ )  {
        ret[i] = fdal.get( i );
      }

//Debug statement.
//for( i = 0; i < ret.length; i++ )  {
//System.out.println( ret[i] );
//}
//System.out.println( "Sorting..." );
//End debug statement.
      insertionSort( ret );  //Sort the PFLs based on start position.
//Debug statement.
//for( i = 0; i < ret.length; i++ )  {
//System.out.println( ret[i] );
//}
//System.out.println( "" );
//End debug statement.
    }

    return( ret );
  }  /*  private FieldDescriptor[] buildNameList( OracleGroup,
                                                  RecordEnumeration )  */


  /**
   * @return The number of letter, upper and lower case, characters in ARR.  If
   *         digits occur then -1.
   */
  private int letterCount( char[] arr, int sp, int len )  {
    int i,
        ep = ( sp + len ),
        ret = 0;

    if( ( sp >= 0 ) && ( len > 0 ) && ( ep <= arr.length ) )  {
      i = sp;
      while( ( i < ep ) && ( ret >= 0 ) )  {
        if( Character.isLetter( arr[i] ) )  {
          ret++;
        }
        else if( !Character.isWhitespace( arr[i] ) )  {
          ret = -1;
        }

        i++;
      }
    }

    return( ret );
  }  /*  private int letterCount( char[] arr )  */


  private int letterCount( char[] arr )  {
    return( letterCount( arr, 0, arr.length ) );
  }  /*  private int letterCount( char[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;

    ml = 0;
    gid = 1;
    mp = 0.90;
    mbp = 1.00;
    this.og = og;
    on = "middle name";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        if( nvp.getName( ).equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
        else if( nvp.getName( ).equalsIgnoreCase( "maximumlength" ) )  {
          try  {
            ml = Integer.parseInt( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            ml = 0;
          }
        }
        else if( nvp.getName( ).equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.90;
          }
        }
        else if( nvp.getName( ).
                   equalsIgnoreCase( "maximumblankpercentage" ) )  {

          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 1.00;
          }
        }
        else if( nvp.getName( ).equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 1;
          }
        }
      }
    }
  }  /*  public void initialize( OracleGroup, String[] )  */


  public boolean isValid( char[] field )  {
    return( false );
  }  /*  public boolean isValid( char[] )  */


  public boolean isValid( char[] field, int beginIndex, int length )  {
    return( false );
  }  /*  public boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    return( false );
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


  /**
   *   There are two instances that indicate a middle name.  First, a single
   * character, initial, between name fields and second, multiple FirstName
   * fields.
   */
  public void postProcess( char[][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    int c,
        g,
        h,
        i,
        j,
        k,
        n,
        bc,
        fnc;  //First name count.
    FieldDescriptor mn = null,  //UNSPECIFIED region that could be a MiddleName.
                    fdh;
    FieldDescriptor[] fda = buildNameList( og, re );

    if( fda != null )  {
      i = 0;
      while( i < fda.length )  {
        //Find a grouping of names.  A grouping is a collection of name fields
        //  that are at most the length of a single name field apart.
        k = i;
        j = ( i + 1 );
        while( ( j < fda.length ) && ( fda[j].getPosition( ) <=
                  ( fda[k].getPosition( ) + ( fda[k].getLength( ) * 2 ) ) ) )  {

          k = j++;
        }
//Debug statement.
//System.out.println( "Current grouping..." );
//for( k = i; k < j; k++ )  {
//System.out.println( fda[k] );
//}
//System.out.println( "" );
//End debug statement.

        h = -1;
        n = 0;
        fnc = 0;
        for( k = i; k < j; k++ )  {
          if( fda[k].getName( ).equalsIgnoreCase( "first name" ) )  {
            fnc++;  //Increment the count of first names.

            if( h >= 0 )  {
              //Count the number of initials for this field in the sample.
              c = 0;
              for( g = 0; g < records.length; g++ )  {
                if( letterCount( records[g], fda[k].getPosition( ),
                                 fda[k].getLength( ) ) == 1 )  {

                  c++;
                }
              }

              //If the current field's valid count is less than that of the
              //  current minimum with respect to the FirstName oracle and the
              //  number of blanks and initials is greater than the current
              //  number then update the current field to this one. 
              //  !!Assumption - there is only one MiddleName per grouping!!
              if( ( fda[k].getValidCount( ) < fda[h].getValidCount( ) ) &&
                  ( ( fda[k].getBlankCount( ) + c ) >
                    ( fda[h].getBlankCount( ) + n ) ) )  {

                h = k;
                n = c;
              }
            }
            else  {
              h = k;
            }
          }
        }

        //There are exactly two first names in the current grouping.  Only
        //  update groupings with two first names as more than two indicates
        //  several first names together that should not be relabeled.
        if( fnc == 2 )  {
//Debug statement.
//System.out.println( "Renaming - " + fda[h] );
//End debug statement.
          //Update the field from a FirstName to a MiddleName.
          fda[h].setType( og.index( on ) );
          fda[h].setName( on );

          re.setEntry( fda[h] );  //Update the RecordEnumeration.
        }
        //One or none first names, check UNSPECIFIED regions for MiddleNames.
        else  {
//Debug statement.
//System.out.println( "Test UNSPECIFIED regions..." );
//if( ( ( fdh = re.type( fda[i].getPosition( ) - 1 ) ) != null ) )  {
//System.out.println( fdh );  
//}
//End debug statement.
          //Test the position preceeding the grouping.
          if( ( ( fdh = re.type( fda[i].getPosition( ) - 1 ) ) != null ) &&
              ( fdh.getName( ).equalsIgnoreCase( "unspecified" )/* ||
                ( og.getRank( fdh.getType( ) ) <
                    og.getRank( og.index( on ) ) )*/ ) )  {

            c = 0;
            bc = 0;
            k = fdh.getPosition( );  //Re-use variables to avoid function calls.
            n = fdh.getLength( );
            fnc = og.index( "first name" );
            for( h = 0; h < records.length; h++ )  {
              if( blank( records[h], k, n ) )  {
                bc++;
              }
              else if( ( letterCount( records[h], k, n ) == 1 ) ||
                       og.isValid( records[h], k, n, fnc ) )  {

                c++;
              }
            }
//Debug statement.
//System.out.println( "Preceding " + fda[i].getPosition( ) + " -- C = " + c + ";  Records length = " + records.length + ";  BC = " + bc + "  ==  " + ( double )( ( double )c / ( double )( records.length - bc ) ) );
//End debug statement.

            if( ( records.length - bc ) > 0 )  {
              if( ( double )( ( double )c / ( double )( records.length -
                     bc ) ) > mp )  {

//Debug statement.
//System.out.println( "Preceding -- updating MN" );
//End debug statement.
                mn = fdh;
                mn.setNumberValid( c );
                mn.setBlankCount( bc );
              }
            }
          }

          for( g = i; g < j; g++ )  {
            //Test the position following this grouping.
            fdh = re.type( fda[g].getPosition( ) + fda[g].getLength( ) );
//Debug statement.
//System.out.println( "Following..." );
//if( fdh != null )  {
//System.out.println( fdh );  
//}
//End debug statement.
            if( ( fdh != null ) &&
                ( fdh.getName( ).equalsIgnoreCase( "unspecified" )/* ||
                  ( og.getRank( fdh.getType( ) ) <
                      og.getRank( og.index( on ) ) )*/ ) )  {

              c = 0;
              bc = 0;
              k = fdh.getPosition( );  //Re-use variables.
              n = fdh.getLength( );
              fnc = og.index( "first name" );
              //Count the number of entries that might be a MiddleName.
              for( h = 0; h < records.length; h++ )  {
                if( blank( records[h], k, n ) )  {
                  bc++;
                }
                else if( ( letterCount( records[h], k, n ) == 1 ) ||
                         og.isValid( records[h], k, n, fnc ) )  {

                  c++;
                }
              }

//Debug statement.
//System.out.println( "Following " + ( fda[g].getPosition( ) + fda[g].getLength( ) ) + " -- C = " + c + ";  Records length = " + records.length + ";  BC = " + bc + "  ==  " + ( double )( ( double )c / ( double )( records.length - bc ) ) );
//End debug statement.
              if( ( records.length - bc ) > 0 )  {
                if( ( double )( ( double )c / ( double )( records.length -
                       bc ) ) > mp )  {

                  if( mn != null )  {
//Debug statement.
//System.out.println( "Following -- MN is not NULL." );
//End debug statement.
                    //Is the current field a 'better' fit?
                    if( c > mn.getValidCount( ) )  {
//Debug statement.
//System.out.println( "Following -- updating MN" );
//End debug statement.
                      mn = fdh;
                      mn.setNumberValid( c );
                      mn.setBlankCount( bc );
                    }
                  }
                  else  {
//Debug statement.
//System.out.println( "Following -- MN, copying reference" );
//End debug statement.
                    mn = fdh;
                  }
                }
              }
            }
          }

          //If an UNSPECIFIED region was found that could be a middle name.
          if( mn != null )  {
            mn.setName( on );
            mn.setType( og.index( on ) );

//Debug statement.
//System.out.println( "setEntry() -- " + mn );
//End debug statement.
            re.setEntry( mn );
          }
        }

        //At this point J is an index to the entry after the grouping currently
        //  under consideration.
        i = j;
      }
    }
  }  /*  public void postProcess( char[][], RecordEnumeration )  */


  /**
   *   There are two instances that indicate a middle name.  First, a single
   * character, initial, between name fields and second, multiple FirstName
   * fields.
   */
  public void postProcess( char[][][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    int c,
        g,
        h,
        i,
        j,
        k,
        n,
        bc,
        fnc,
        pos;
    FieldDescriptor mn = null,  //UNSPECIFIED region that could be a MiddleName.
                    fdh;
    FieldDescriptor[] fda = buildNameList( og, re );

    if( fda != null )  {
      i = 0;
      while( i < fda.length )  {
        //Find a grouping of names.  A grouping is a collection of name fields
        //  where the individual elements are separated by at most one field.
        k = i;
        j = ( i + 1 );
        while( ( j < fda.length ) && ( fda[j].getPosition( ) <=
                                        ( fda[k].getPosition( ) + 2 ) ) )  {

          k = j++;
        }
//Debug statement.
//System.out.println( "Grouping from " + i + " to " + ( j - 1 ) + "." );
//End debug statement.

        h = -1;
        n = 0;
        fnc = 0;
        for( k = i; k < j; k++ )  {
          if( fda[k].getName( ).equalsIgnoreCase( "first name" ) )  {
            fnc++;  //Increment the count of first names.
            pos = fda[k].getPosition( );

            if( h >= 0 )  {
              //Count the number of initials for this field in the sample.
              c = 0;
              for( g = 0; g < records.length; g++ )  {
                if( letterCount( records[g][pos] ) == 1 )  {
                  c++;
                }
              }

              //If the current field's valid count is less than that of the
              //  current minimum with respect to the FirstName oracle and the
              //  number of blanks and initials is greater than the current
              //  number then update the current field to this one. 
              //  !!Assumption - there is only one MiddleName per grouping!!
              if( ( fda[k].getValidCount( ) < fda[h].getValidCount( ) ) &&
                  ( ( fda[k].getBlankCount( ) + c ) >
                    ( fda[h].getBlankCount( ) + n ) ) )  {

                h = k;
                n = c;
              }
            }
            else  {
              h = k;
            }
          }
        }

//Debug statement.
//System.out.println( "FNC = " + fnc + "." );
//End debug statement.
        if( fnc > 1 )  {  //More than one first name in the current grouping.
          //Update the field from a FirstName to a MiddleName.
          fda[h].setType( og.index( on ) );
          fda[h].setName( on );

          re.setEntry( fda[h] );  //Update the RecordEnumeration.
        }
        //One or none first names, check UNSPECIFIED regions for MiddleNames.
        else  {
          //Test the position preceeding the grouping.
          if( ( ( fdh = re.type( fda[i].getPosition( ) - 1 ) ) != null ) &&
              fdh.getName( ).equalsIgnoreCase( "unspecified" ) )  {
//Debug statement.
//System.out.println( "Preceeding entry is UNSP." );
//End debug statement.

            c = 0;
            bc = 0;
            pos = fdh.getPosition( );
            fnc = og.index( "first name" );
            for( h = 0; h < records.length; h++ )  {
              if( blank( records[h][pos] ) )  {
                bc++;
              }
              else if( ( letterCount( records[h][pos] ) == 1 ) ||
                       og.isValid( records[h][pos], fnc ) )  {

                c++;
              }
            }

            if( ( double )( ( double )c / ( double )( records.length -
                   bc ) ) > mp )  {

              mn = fdh;
              mn.setNumberValid( c );
              mn.setBlankCount( bc );
            }
          }

          for( g = i; g < j; g++ )  {
            //Test the position following this grouping.
            fdh = re.type( fda[g].getPosition( ) + 1 );
            if( ( fdh != null ) &&
                fdh.getName( ).equalsIgnoreCase( "unspecified" ) )  {

              c = 0;
              bc = 0;
              pos = fdh.getPosition( );
//Debug statement.
//System.out.println( "Entry at " + ( fda[g].getPosition( ) + 1 ) + " is UNSP;  POS = " + pos + "." );
//End debug statement.
              fnc = og.index( "first name" );
              //Count the number of entries that might be a MiddleName.
              for( h = 0; h < records.length; h++ )  {
//Debug statement.
//if( ( h % 10 ) == 0 )  {
//System.out.println( "|" + ( new String( records[h][pos] ) ) + "|" );
//}
//End debug statement.
                if( blank( records[h][pos] ) )  {
                  bc++;
                }
                else if( ( letterCount( records[h][pos] ) == 1 ) ||
                         og.isValid( records[h][pos], fnc ) )  {

                  c++;
                }
              }
//Debug statement.
//System.out.println( "RL = " + records.length + ";  BC = " + bc + ";  C = " + c + "." );
//End debug statement.

              if( ( double )( ( double )c / ( double )( records.length -
                     bc ) ) > mp )  {

                if( mn != null )  {
                  //Is the current field a 'better' fit?
                  if( c > mn.getValidCount( ) )  {
                    mn = fdh;
                    mn.setNumberValid( c );
                    mn.setBlankCount( bc );
                  }
                }
                else  {
                  mn = fdh;
                }
              }
            }
          }

          //If an UNSPECIFIED region was found that could be a middle name.
          if( mn != null )  {
//Debug statement.
//System.out.println( "Resetting field " + mn.getPosition( ) + " to a MiddleName." );
//End debug statement.
            mn.setName( on );
            mn.setType( og.index( on ) );

            re.setEntry( mn );
          }
        }

        //At this point J is an index to the entry after the grouping currently
        //  under consideration.
        i = j;
      }
    }
  }  /* public void postProcess( char[][][], FieldDescriptor[][],
                                 RecordEnumeration ) */

}  /*  public class MiddleName implements Oracle  */
