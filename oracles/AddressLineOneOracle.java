package oracles;

import java.util.ArrayList;

import supportMethods.Setup;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public class AddressLineOneOracle implements Oracle, AlterDatabase,
                                             PostProcess  {

  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp;
  private OracleGroup og;
  private String on;  //The oracle's name.
  private Oracle[] o;


  public AddressLineOneOracle( )  {  }  /*  Constructor  */


  /**
   *   Builds a "super" token out of the tokens, T, from indices ST to
   * ( ET - 1 ).
   *
   * @param ST The index of the token to start with.
   * @param ET The index of the token to end before.
   * @return A string containing the "super" token.
   */
  private String buildStreetName( int st, int et, String[] t )  {
    int i;
    String ret = "";

    if( st < et )  {
      ret = t[st];

      for( i = ( st + 1 ); i < et; i++ )  {
        ret = ret.concat( " ".concat( t[i] ) );
      }
    }

    return( ret );
  }  /*  private String buildStreetName( int, int, String[] )  */


  /**
   * Checks whether string 's' matches an address1 type field.
   *
   * Address1 is of the form:
   *   StreetNumber [PreDirectional] StreetName [StreetSuffix]
   *   [PostDirectional].
   *
   * !!Assumption!!  Components of address1 field are separated by white space.
   */
  private boolean test( String s )  {
    boolean ret = o[4].isValid( s );  //Test for a PO Box address.
    int st = 0,  //Start token.
        et = 0;  //End token.
    String h = "";
    String[] t = null;  //Tokens.

    if( !ret )  {
      t = s.split( " " );  //Separate the tokens based on white space.

      //At a minimum there must be two tokens:  a StreetNumber and a StreetName.
      if( t.length > 1 )  {
        st = 0;
        et = t.length;

        if( ret = o[0].isValid( t[st] ) )  {
          do  {
            if( ++st < t.length )  {
              h = h.concat( " ".concat( t[st] ) );
            }
          }while( ( st < t.length ) && ( o[0].isValid( h ) ) );

//Debug statement.
//System.out.println( "The first token after the street number is |" + t[st] + "." );
//End debug statement.
          //This cannot be an AddressLineOne field as all of the tokens are
          //  StreetNumbers.  Otherwise ST will be pointing at the first token
          //  of the rest of the field.
          ret = ( st < t.length );
        }

        if( ret )  {


          //Append all of the tokens that are not StreetNumbers into one token.
          //  Then test it against the StreetName oracle, if it fails first try
          //  to remove Directionals, pre and post, from the remaining tokens.
          if( !( ret = o[2].isValid( buildStreetName( st, et, t ) ) ) )  {
            //Test to see if the first token is a Directional.
            if( ( st < et ) && o[1].isValid( t[st] ) )  {
//Debug statement.
//System.out.println( "Removing a pre-directional |" + t[st] + "." );
//End debug statement.
              st++;  //If it is remove it from the list of possible tokens.
            }
            //Test to see if the last token is a Directional.
            if( ( et > 0 ) && o[1].isValid( t[( et - 1 )] ) )  {
//Debug statement.
//System.out.println( "Removing a post-directional |" + t[et] + "." );
//End debug statement.
              et--;  //If it is remove it from the list of possible tokens.
            }

            //Append all of the tokens that are not StreetNumbers or
            //  Directionals into one token.  Then test it against the
            //  StreetName oracle, if it fails try to remove StreetSuffix from
            //  the remaining tokens.
            if( !( ret = o[2].isValid( buildStreetName( st, et, t ) ) ) )  {
              //Test to see if the last token is a StreetSuffix.
              if( ( et > 0 ) && o[3].isValid( t[( et - 1 )] ) )  {
//Debug statement.
//System.out.println( "Removing a street suffix |" + t[( et - 1 )] + "." );
//End debug statement.
                et--;  //If it is remove it from the list of possible tokens.
              }

              //Append all of the tokens that are not StreetNumbers,
              //  Directionals, or a StreetSuffix into one token.  Then test it
              //  against the StreetName oracle, if it fails then this field
              //  does not conform to the AddressLineOne content type.
              ret = o[2].isValid( buildStreetName( st, et, t ) );
//Debug statement.
//System.out.println( "RET = " + ret + ";  SN = |" + buildStreetName( st, et, t ) + "|" );
//End debug statement.
            }
          }
        }
      }
    }

    return( ret );
  }  /*  private boolean test( String s )  */


  private void insertionSort( int[] arr )  {
    int i,
        j,
        x;

    for( i = 1; i < arr.length; i++ )  {
      x = arr[i];

      j = i;
      while( ( j > 0 ) && ( arr[( j - 1 )] > x ) )  {
        arr[j] = arr[( j - 1 )];
        j--;
      }

      arr[j] = x;
    }
  }  /*  private void insertionSort( int[] )  */


  private void place( FieldDescriptor fd, ArrayList<FieldDescriptor> al )  {
    boolean b = true;
    int i = 0;
    ArrayList<Integer> ind = new ArrayList<Integer>( );
    ArrayList<FieldDescriptor> c = new ArrayList<FieldDescriptor>( );

//Debug statement.
//System.out.println( fd );
//End debug statement.
    for( i = 0; i < al.size( ); i++ )  {
      if( fd.getFieldPosition( ).
             overlaps( al.get( i ).getFieldPosition( ) ) )  {

        ind.add( i );
        c.add( al.get( i ) );
      }
    }

    i = 0;
    while( b && ( i < c.size( ) ) )  {
      if( ( og.getRank( fd.getType( ) ) <
            og.getRank( c.get( i ).getType( ) ) ) ||
          ( ( og.getRank( fd.getType( ) ) ==
              og.getRank( c.get( i ).getType( ) ) ) &&
            ( fd.getPercentValid( c.get( i ).getGrouping( ) ) <=
              c.get( i ).getPercentValid( fd.getGrouping( ) ) ) ) )  {

        b = false;
      }
      else  {
        i++;
      }
    }

    if( b )  {
      for( i = 0; i < ind.size( ); i++ )  {
//Debug statement.
//System.out.println( "Removing --> " + al.get( ind.get( i ) ) + "." );
//End debug statement.
        al.set( ind.get( i ), null );
      }

      al.add( fd );

      i = 0;
      while( i < al.size( ) )  {
        if( al.get( i ) == null )  {
          al.remove( i );
        }
        else  {
          i++;
        }
      }
//Debug statement.
//System.out.println( "" );
//for( i = 0; i < al.size( ); i++ )  {
//System.out.println( al.get( i ) );
//}
//System.out.println( "-----" );
//End debug statement.
    }
  }  /*  private void place( FieldDescriptor, ArrayList<FieldDescriptor> )  */


  /**
   *   Though this is an oracle that will have its database altered, it will
   * have already been accomplished when the street name oracle reduces its
   * database.  Thus a requirement is that the street name oracle must be
   * reduced before invoking this oracle.
   */
  public void setMinDatabase( )  {  }  /*  public void setMinDatabase( )  */
  public void setMaxDatabase( )  {  }  /*  public void setMaxDatabase( )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    o = new Oracle[5];

    o[0] = og.getOracle( og.index( "street number" ) );
    o[1] = og.getOracle( og.index( "directional" ) );
    o[2] = og.getOracle( og.index( "street name" ) );
    o[3] = og.getOracle( og.index( "street suffix" ) );
    o[4] = new PostOfficeBoxOracle( );

    o[4].initialize( og, null );

    ml = 60;
    gid = 4;
    mp = 0.60;
    mbp = 0.15;
    this.og = og;
    on = "address line one";

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
            ml = 60;
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
            mbp = 0.15;
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
    return( test( ( new String( array, 0, array.length ) ).trim( ) ) );
  }  /*  boolean isValid( char[] )  */


  public boolean isValid( char[] array, int beginIndex, int length )  {
    boolean ret = ( ( beginIndex >= 0 ) &&
                    ( ( beginIndex + length ) <= array.length ) );

    if( ret )  {
      ret = test( ( new String( array, beginIndex, length ) ).trim( ) );
    }

    return( ret );
  }  /*  boolean isValid( char[], int, int )  */


  public boolean isValid( String field )  {
    return( test( field.trim( ) ) );
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


  /**
   *   AddressLineOne is unique to the composite oracles.  In other composite
   * oracles the associated ranking is equal to the ranking of its comprising
   * simple oracles, or at least several of them.  AddressLineOne, because it is
   * comprised of so many simple oracles that are generally unreliable by
   * themselves, is necessarily ranked higher than its comprising oracles.  This
   * creates a problem during conflict resolution as AddressLineOne will always
   * be chosed over the individual parts due to its greater reliability.
   * 
   *   To address this issue, any AddressLineOne fields placed in the
   * RecordEnumeration during conflict resolution will check all conflicting
   * component entries to see if they are all left or right justified.  If this
   * is TRUE, then it can be assumed that the parts should be listed
   * individually.  At a minimum there should be a StreetNumber and a
   * StreetName field.
   * 
   *   This functionality only applies to hybrid and fully fixed files as the
   * field delimiter prevents this issue within fully delimited files.
   */
  public void postProcess( char[][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    boolean b;
    int i,
        j,
        k,
        pob,
        snum,
        sname;
    int[] cpi = new int[( o.length - 1 )];
    FieldDescriptor[] fda = re.ofType( og.index( on ) );
    String n;
    ArrayList<FieldDescriptor> alfd;

    if( fda != null )  {
      //Determine the indexes of the individual address line one components
      //  within FD.
      for( i = 0; i < cpi.length; i++ )  {
        cpi[i] = og.index( o[i].getName( ) );
//Debug statement.
//System.out.println( "CPI[" + i + "] = " + cpi[i] + ";  Name = " + o[i].getName( ) + "." );
//End debug statement.
      }
      insertionSort( cpi );

      for( i = 0; i < fda.length; i++ )  {
//Debug statement.
//System.out.println( fda[i] );
//End debug statement.
        alfd = new ArrayList<FieldDescriptor>( );

        //Scan through all applicable FD determining which overlap with the
        //  current AddressLineOne PF.
        for( j = 0; j < cpi.length; j++ )  {
          for( k = 0; k < fd[cpi[j]].length; k++ )  {
//!!This may be a problem.  Only testing overlap rather than contains.
            if( fda[i].getFieldPosition( ).
                  overlaps( fd[cpi[j]][k].getFieldPosition( ) ) )  {

//Debug statement.
//System.out.println( "Calling place()..." );
//End debug statement.
              place( fd[cpi[j]][k], alfd );
            }
          }
        }
//Debug statement.
//System.out.println( "StreetNumber == " + o[0].getName( ) + ", " + og.index( o[0].getName( ) ) + ";  StreetName == " + o[2].getName( ) + ", " + og.index( o[2].getName( ) ) + ";  PO Box == " + o[4].getName( ) + ", " + og.index( o[4].getName( ) ) + "." );
//End debug statement.

        b = true;
        j = 0;
        pob = 0;  //PO Box count.
        snum = 0;  //Street number count.
        sname = 0;  //Street name count.

        while( b && ( j < alfd.size( ) ) )  {
          //Is this field left or right justified?
          b = ( b && ( alfd.get( j ).getJustification( ) != 0 ) );

          if( b )  {
            n = alfd.get( j++ ).getName( );

            if( n.equals( o[0].getName( ) ) )  {
              snum++;  //Count the number of overlapping StreetNumber fields.
            }
            else if( n.equals( o[2].getName( ) ) )  {
              sname++;  //Count the number of overlapping StreetName fields.
            }
            else if( n.equals( o[4].getName( ) ) )  {
              pob++;  //Count the number of overlapping PO Box fields.
            }
          }
        }

//Debug statement.
//System.out.println( "B = " + b + ";  Street number count = " + snum + ";  Street name count = " + sname + ";  PO Box count = " + pob + "." );
//End debug statement.
        //If the individual components were all left or right justified and
        //  there exists either a { StreetNumber, StreetName } pair or a PO Box,
        //  then remove the AddressLineOne entry from the RecordEnumeration and
        //  update it with the individual components.
        if( b && ( ( ( snum > 0 ) && ( sname == 1 ) ) || ( pob == 1 ) ) )  {
//Debug statement.
//System.out.println( "---" );
//System.out.println( re );
//System.out.println( "---" );
//End debug statement.
          for( j = 0; j < alfd.size( ); j++ )  {
            re.setEntry( alfd.get( j ) );
          }
//          re.removeEntry( fda[i] );  //Remove AddressLineOne PF.
//          re.setEntries( alfd );  //Add individual components.
//Debug statement.
//System.out.println( "---" );
//System.out.println( re );
//System.out.println( "---" );
//End debug statement.
        }
//Debug statement.
//System.out.println( "----------" );
//System.out.println( "" );
//End debug statement.
      }
    }
  }  /*  public void postProcess( char[][], FieldDescriptor[][],
                                  RecordEnumeration)  */


  public void postProcess( char[][][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    
  }  /*  public void postProcess( char[][][], FieldDescriptor[][],
                                  RecordEnumeration )  */

}  /*  public class AddressOneOracle implements Oracle, AlterDatabase,
                                                PostProcess  */
