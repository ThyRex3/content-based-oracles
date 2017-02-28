package oracles;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.Iterator;
import java.util.ArrayList;

import dataStructures.IntegerLookup;

import supportObjects.CityNode;
import supportObjects.StateNode;
import supportObjects.NameValuePair;
import supportObjects.RecordEnumeration;
import supportObjects.FieldDescriptor;
import supportMethods.BasicAttributes;
import supportMethods.Setup;


public class ZipcodeOracle implements Oracle, PostProcess  {
  private class TripletCount  {  //Used by cross-reference analysis.
//    private boolean bc;
    private int ci,  //City index.
                si,  //State index.
                zi;  //Zipcode index.
    private double pv;  //Percent valid.


    private TripletCount( )  {
//      bc = false;
      ci = -1;
      si = -1;
      zi = -1;
      pv = 0.0;
    }  /*  Constructor  */


/*
    public void setBestCandidate( boolean bestCandidate )  {
      bc = bestCandidate;
    }  /*  public void setBestCandidate( boolean )  */


    public void setCityIndex( int cityIndex )  {
      ci = cityIndex;
    }  /*  public void setCityIndex( int )  */


    public void setStateIndex( int stateIndex )  {
      si = stateIndex;
    }  /*  public void setStateIndex( int )  */


    public void setZipcodeIndex( int zipcodeIndex )  {
      zi = zipcodeIndex;
    }  /*  public void setZipcodeIndex( int )  */


    public void setIndex( int cityIndex, int stateIndex, int zipcodeIndex )  {
      ci = cityIndex;
      si = stateIndex;
      zi = zipcodeIndex;
    }  /*  public void setIndex( int, int, int )  */


    public void setPercentValid( double percentValid )  {
      pv = percentValid;
    }  /*  public void setPercentValid( double )  */


/*
    public boolean getBestCandidate( )  {
      return( bc );
    }  /*  public boolean getBestCandidate( )  */


    public int getCityIndex( )  {
      return( ci );
    }  /*  public int getCityIndex( )  */


    public int getStateIndex( )  {
      return( si );
    }  /*  public int getStateIndex( )  */


    public int getZipcodeIndex( )  {
      return( zi );
    }  /*  public int getZipcodeIndex( )  */


    public double getPercentValid( )  {
      return( pv );
    }  /*  public int getPercentValid( )  */


    public boolean valid( )  {
      return( ( si >= 0 ) && ( ci >= 0 ) && ( zi >= 0 ) );
    }  /*  public boolean valid( )  */

  }  /*  private class TripletCount  */


  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 crt,  //Cross-reference threshold.
                 mbp;  //Maximum blank percentage.
  private IntegerLookup lookup;  //The data structure containing the database.
  private StateNode[] sa;  //The array of states used for cross referencing.
  private OracleGroup og;
  private String on,  //Oracle name.
                 crfn,  //The crossreference file name.
                 dbfn;  //The database file name.


  public ZipcodeOracle( )  {  }  /*  Constructor  */


  private String clean( String s )  {
    return( s.trim( ).toLowerCase( ) );
  }  /*  private String screen( String )  */


/*
  private boolean scan( int value, int[] array )  {
    boolean ret = false;
    int i = 0;

    while( !ret && ( i < array.length ) )  {
      if( array[i] != value )  {
        i++;
      }
      else  {
        ret = true;
      }
      
    }

    return( ret );
  }  /*  private boolean scan( int, int[] )  */


  private boolean equal( char[] arr, int sp1, int len1, int sp2, int len2 )  {
    boolean ret = false;
    int i = 0,
        ep1 = ( sp1 + len1 - 1 ),
        ep2 = ( sp2 + len2 - 1 );

    //Skip over any leading whitespace.
    while( ( sp1 < arr.length ) && Character.isWhitespace( arr[sp1] ) )  {
      sp1++;
    }
    while( ( sp2 < arr.length ) && Character.isWhitespace( arr[sp2] ) )  {
      sp2++;
    }

    //Skip over any trailing whitespace.
    while( ( ep1 >= sp1 ) && Character.isWhitespace( arr[ep1] ) )  {
      ep1--;
    }
    while( ( ep2 >= sp2 ) && Character.isWhitespace( arr[ep2] ) )  {
      ep2--;
    }

    //If the trimmed field boundaries are valid and the lengths the same then
    //  test each character.
    if( ( sp1 < arr.length ) && ( sp2 < arr.length ) && ( ep1 >= sp1 ) &&
        ( ep2 >= sp2 ) && ( ( ep1 - sp1 ) == ( ep2 - sp2 ) ) )  {

      ret = true;

      //Perform a case insensitive comparison of the field values.
      while( ret && ( i < ( ep1 - sp1 ) ) )  {
        ret = ( Character.toLowerCase( arr[( sp1 + i )] ) ==
                Character.toLowerCase( arr[( sp2 + i )] ) );

        i++;  //Increment to the next character.
      }
    }

    return( ret );
  }  /*  private boolean equal( char[], int, int, int, int )  */


  /**
   *   Performs a binary search on SA.
   */
  private int search( String value )  {
    int beginning = 0,
        middle,
        end = ( sa.length - 1 );
    String s;

    value = value.trim( ).toLowerCase( );

    while( beginning <= end )  {
      middle = ( ( beginning + end ) / 2 );

      if( value.equals( ( s = sa[middle].getStateName( ) ) ) )  {
        return( middle );
      }
      else if( value.compareTo( s ) < 0 )  {
        end = ( middle - 1 );
      }
      else  {
        beginning = ( middle + 1 );
      }
    }

    return( -1 );
  }  /*  private int search( String )  */


  /**
   *   Takes part of a character array, ASCII encoding, and converts the string
   * to an integer.
   */
  private int convertToInteger( char[] record, int start, int length )  {
    int i = ( start + length - 1 ),
        j = 1,
        ret = 0;

    while( ( i >= start ) && Character.isWhitespace( record[i] ) )  {
      i--;
    }

    while( ( i >= start ) && Character.isDigit( record[i] ) )  {
      ret += ( j * ( ( int )record[i--] - 48 ) );
      j *= 10;
    }

    return( ret );
  }  /*  private int convertToInteger( char[], int, int )  */


  /**
   * @param REC - The character array to test for whitespace.
   * @param POS - The positional ranges of the three fields to test:  state,
   *              city, and zipcode.  POS[0] and POS[1] are the start postion
   *              and length of the state field respectively.  City is 2 and 3,
   *              and zicode is 4 and 5.  Six total entries are expected.
   * @return TRUE if all the characters contained within the specified ranges of
   *         REC are white space, else FALSE.
   */
  private boolean isBlank( char[] rec, int[] pos )  {
    boolean ret = true;
    int i = 0,
        p,
        ep;

    if( pos.length > 5 )  {
      while( ret && ( i < pos.length ) )  {
        p = pos[i];
        ep = ( pos[i] + pos[( i + 1 )] );

        while( ret && ( p < ep ) )  {
          if( Character.isWhitespace( rec[p] ) )  {
            p++;
          }
          else  {
            ret = false;
          }
        }

        i += 2;
      }
    }

    return( ret );
  }  /*  private boolean isBlank( char[], int[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    int c,
        i = 0,
        j,
        k;
    int[] zc = null;
    NameValuePair nvp;
    CityNode ncn = null;
    StateNode nsn = null;
    String s;
    String[] state = null,
             city = null,
             holder = null;
    Iterator<CityNode> ci;
    Iterator<StateNode> si;
    BufferedReader inf;
    //Temporary city array.
    ArrayList<CityNode> tca = new ArrayList<CityNode>( 200 );
    //Temporary state array.
    ArrayList<StateNode> tsa = new ArrayList<StateNode>( 60 );

    //Define parameters associated with the ZipcodeOracle.
    ml = 15;
    gid = 8;
    mp = 0.60;
    crt = 0.70;
    mbp = 0.20;
    lookup = new IntegerLookup( );
    this.og = og;
    on = "zipcode";
    crfn = "/data/StateCityZip_SortedByState.dat";
    dbfn = "/data/ZipCodes_CompleteSorted.dat";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        s = nvp.getName( );
        if( s.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
        else if( s.equalsIgnoreCase( "crossreferencesourcefile" ) )  {
          crfn = nvp.getValue( );
        }
        else if( s.equalsIgnoreCase( "maximumsourcefile" ) )  {
          dbfn = nvp.getValue( );
        }
        else if( s.equalsIgnoreCase( "maximumlength" ) )  {
          try  {
            ml = Integer.parseInt( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            ml = 15;
          }
        }
        else if( s.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.75;
          }
        }
        else if( s.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 0.20;
          }
        }
        else if( s.equalsIgnoreCase( "crossreferencethreshold" ) )  {
          try  {
            crt = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            crt = 0.70;
          }
        }
        else if( s.equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 8;
          }
        }
      }
    }

    lookup.setDataSource(
      og.fileLineCount( BasicAttributes.extractFileName( true, dbfn ) ), dbfn );

    //Prepare the data structures used for cross referencing.
    c = og.fileLineCount( BasicAttributes.extractFileName( true, crfn ) );

    i = 0;
    zc = new int[c];
    state = new String[c];
    city = new String[c];

    //States are stored in a sorted, lookup table.  Each state entry contains a
    //  link to a sorted, lookup table of cities.  Each city entry contains a
    //  link to a linked list of zipcodes.
    try  {
      inf = new BufferedReader( new InputStreamReader(
                      bean.LayoutBean.class.getResourceAsStream( crfn ) ) );

      try  {
        
        while( ( i < c ) && ( ( s = inf.readLine( ) ) != null ) )  {
          holder = s.toLowerCase( ).split( "[|]" );

          state[i] = holder[0];
          city[i] = holder[1];
          zc[i++] = Integer.parseInt( holder[2] );
        }
      }catch( NumberFormatException e )  {
        System.out.println( "oracles - ZipcodeOracle error 1:  " +
                            "NumberFormatException.  " + e.getMessage( ) );

        System.exit( 1 );
      }

      inf.close( );
    }catch( IOException e )  {
      System.out.println( "oracles - ZipcodeOracle error 2:  IOException.  " +
                          e.getMessage( ) );

      System.exit( 1 );
    }

    c = i;  //Reset COUNT to the number of file entries that were read.
    i = 0;
    while( i < c )  {  //Increment through the states in the file.
      //Increment through the cities corresponding to the current state.
      j = i;
      while( ( j < c ) && ( state[j].equals( state[i] ) ) )  {
        //Create a CityNode entry for each city in the state.
        ncn = new CityNode( city[j] );

        //Increment throught the zipcodes corresponding to the current city.
        k = j;
        while( ( k < c ) && ( city[k].equals( city[j] ) ) )  {
          ncn.insertZipcode( zc[k] );
          k++;
        }

        j = k;  //Move the index to the next city.
        tca.add( ncn );  //Add the new CityNode to the dynamic, temporary array.
      }

      //Allocate space in the new StateNode for the cities and store them.
      nsn = new StateNode( tca.size( ), state[i] );
      for( ci = tca.iterator( ); ci.hasNext( ); )  {
        nsn.addCity( ci.next( ) );
      }
      tca.clear( );
      tsa.add( nsn );

      i = j;  //Move the index to the next state.
    }

    //Finally move the StateNode objects into the local, static array.
    i = 0;
    sa = new StateNode[tsa.size( )];
    for( si = tsa.iterator( ); si.hasNext( ); )  {
      sa[i++] = si.next( );
    }
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


  /**
   *   Has the side effect of updating BC when BC[I] and BC[J] contain a
   * conflicting entry.  If BC[I] has the greater count then BC[J] is modified,
   * else BC[I] is changed.
   *
   * @return TRUE if the BC[I] is reset else FALSE.
   */
/*
  private boolean testDuplicate( int i, int j, int sc, int[] bc,
                                 TripletCount[] tc )  {

    boolean ret = false;
    int g,
        k,
        x,
        y,
        index;

    //Test if two triplets share a city or zipcode field.  They will not
    //  share a state field because there is only one best candidate per
    //  state.
    if( ( tc[bc[i]].getCityIndex( ) == tc[bc[j]].getCityIndex( ) ) ||
        ( tc[bc[i]].getZipcodeIndex( ) == tc[bc[j]].getZipcodeIndex( ) ) )  {

      //BC[I] has the better percentage.
      if( tc[bc[i]].getPercentValid( ) >= tc[bc[j]].getPercentValid( ) )  {
        ret = true;
        x = i;
        y = j;
      }
      else  {  //BC[J] has the better percentage.
        x = j;
        y = i;
      }

      //The candidate with the lower percentage, indicated by BC[Y],
      //  must choose the next best candidate from the possible
      //  combinations.  Since a choice has been made between
      //  conflicting triplets the losing entry will never be valid and
      //  thus to avoid potential confusion later on the valid
      //  percentage is set to 0.0.
      tc[bc[y]].setPercentValid( 0.0 );  //Triplet will never be valid.
      g = ( y * sc );  //The index of the first viable triplet.
      index = -1;  //The index of the next best candidate.

      for( k = 0; k < sc; k++ )  {
        //Do not allow duplicate city or zipcode fields when choosing
        //  the next best candidate.
        if( ( tc[( g + k )].getCityIndex( ) != tc[bc[x]].getCityIndex( ) ) &&
            ( tc[( g + k )].getZipcodeIndex( ) !=
              tc[bc[x]].getZipcodeIndex( ) ) )  {

          //Is this the best percentage yet found.
          if( ( ( index >= 0 ) && ( tc[index].getPercentValid( ) <
            tc[( g + k )].getPercentValid( ) ) ) || ( index < 0 ) )  {

            index = ( g + k );  //Update the current best candidate.
          }
        }
      }

      //The next best candidate has been chosen therefore update the
      //  best candidate index.
      if( ( index >= 0 ) && ( tc[index].getPercentValid( ) >= crt ) )  {
//Debug statement.
//System.out.println( "2 -- BC[" + x + "] = " + index + " - {" + tc[index].getStateIndex( ) + ", " + tc[index].getCityIndex( ) + ", " + tc[index].getZipcodeIndex( ) + "}." );
//End debug statement.
        bc[x] = index;
      }
      else  {
        bc[x] = -1;
      }
    }

    return( ret );
  }  /*  private boolean testDuplicate( int, int, int, int[],
                                        TripletCount[] )  */


  private void cross( char[][] records, FieldDescriptor[] c,
                      FieldDescriptor[] s, FieldDescriptor[] zc,
                      RecordEnumeration re )  {

    boolean b;
    int g = 0,
        h,
        i,
        j,
        k,
        x,
        y,
        count,
        index,
        blanks;  //Blank count.
    int[] pos = new int[6],
          bc = new int[s.length];  //The index of the best count for a state.
    TripletCount[] tc = new TripletCount[( s.length * c.length * zc.length )];
    FieldDescriptor[] fd;

//Debug statement.
//System.out.println( "Beginning cross()..." );
//End debug statement.
    //Consider all possible permutations of state, city, and zipcode.  The loops
    //  are ordered from most reliable to least reliable ( i.e., state then city
    //  then zipcode ).
    for( i = 0; i < s.length; i++ )  {  //Consider each possible state.
      pos[0] = s[i].getPosition( );  //Store the state field position.
      pos[1] = s[i].getLength( );

      for( j = 0; j < c.length; j++ )  {  //Consider each possible city.
        pos[2] = c[j].getPosition( );  //Store the city field position.
        pos[3] = c[j].getLength( );

        for( k = 0; k < zc.length; k++ )  {  //Consider each possible zipcode.
          pos[4] = zc[k].getPosition( );  //Store the zipcode field position.
          pos[5] = zc[k].getLength( );

          //COUNT is used to store how many valid { state, city, zipcode }
          //  triplets occurred in the record sample.
          blanks = 0;
          count = 0;
          for( h = 0; h < records.length; h++ )  {  //Examine each record.
            if( isBlank( records[h], pos ) )  {
              blanks++;
            }
            else  {
              index = search( new String( records[h], pos[0], pos[1] ) );

              //Was there a valid state?  If so determine if the current city
              //  and zipcode information match the state data.
              if( ( index >= 0 ) && sa[index].search(
                    convertToInteger( records[h], pos[4], pos[5] ),
                    ( new String( records[h], pos[2], pos[3] ) ) ) )  {

                count++;  //Increment the count.
              }
            }
          }
          tc[g] = new TripletCount( );  //Store the results.
          tc[g].setIndex( j, i, k );
          tc[g++].setPercentValid( ( double )( ( double )count /
                                          ( double )( ( double )records.length -
                                                      ( double )blanks ) ) );

//Debug statement.
//System.out.println( "State {" + pos[0] + ", " + pos[1] + "}, City {" + pos[2] + ", " + pos[3] + "}, Zipcode {" + pos[4] + ", " + pos[5] + "} -- " + count + ";  " + tc[( g - 1 )].getPercentValid( ) + "." );
//End debug statement.
        }
      }
    }
//Debug statement.
//System.out.println( "" );
//System.out.println( "After enumeration..." );

//System.out.println( "State length = " + s.length + ";  City length = " + c.length + ";  Zipcode length = " + zc.length + ";  TC length = " + tc.length + "." );
//for( i = 0; i < tc.length; i++ )  {
//if( tc[i] == null )  {
//System.out.println( "TC[" + i + "] is NULL." );
//}
//}
//End debug statement.

    //Find the most likely { state, city, zipcode } triplet by looking at each
    //  state ( the field in the outermost loop ) and returning the result with
    //  the largest count.

    //Set G to the total number of combinations possible for each state.
    g = ( c.length * zc.length );
    h = 0;

    for( i = 0; i < tc.length; i += g )  {  //Consider each state.
      index = i;  //The first entry is the best candidate so far.

      for( j = 1; j < g; j++ )  {  //Consider the remaining candidates.
        //Is this candidate better?
        if( tc[( i + j )].getPercentValid( ) > tc[index].getPercentValid( ) )  {
          index = ( i + j );  //If so, update INDEX.
        }
      }

      //Just because there is a state PFP and even though the state oracle is
      //  generally very reliable does not mean that there must be a triplet for
      //  each state.  If the valid percentage is too low do not indicate a 
      //  found triplet.
      if( tc[index].getPercentValid( ) >= crt )  {
//Debug statement.
//System.out.println( "1 -- BC[" + h + "] = " + index + " - {" + tc[index].getStateIndex( ) + ", " + tc[index].getCityIndex( ) + ", " + tc[index].getZipcodeIndex( ) + "}." );
//End debug statement.
        bc[h] = index;
      }
      else  {
//Debug statement.
//System.out.println( "1 -- BC[" + h + "] = -1." );
//End debug statement.
        bc[h] = -1;
      }


      h++;
    }

    //At this point it is possible that repetition of fields may occur in
    //  distinct triplets ( e.g., The third City field occurs in two separate
    //  triplets ).  It is necessary to determine which triplet the duplicated
    //  entry should be assigned to.  The other triplet must also choose a
    //  different best candidate.

    //Consider each triplet best candidate.
    for( i = 0; i < bc.length; i++ )  {
      //Test if any triplets associated with the state under consideration
      //  passed the minimum, validity threshold.
      if( bc[i] >= 0 )  {
        //Look at the other best candidate triplets to see if any duplicate
        //  entries exist.
        for( j = ( i + 1 ); j < bc.length; j++ )  {
          if( ( bc[i] >= 0 ) && ( bc[j] >= 0 ) )  {
            //Test if two triplets share a city or zipcode field.  They will not
            //  share a state field because there is only one best candidate per
            //  state.
//Debug statement.
//System.out.println( "BC[" + i + "]-I = " + bc[i] + ";  BC[" + j + "]-J = " + bc[j] + "." );
//End debug statement.
            if( ( tc[bc[i]].getCityIndex( ) == tc[bc[j]].getCityIndex( ) ) ||
                ( tc[bc[i]].getZipcodeIndex( ) ==
                  tc[bc[j]].getZipcodeIndex( ) ) )  {
//Debug statement.
//System.out.println( "TC[" + bc[i] + "] and TC[" + bc[j] + "] share a conflicting entry.  City -- i = " + tc[bc[i]].getCityIndex( ) + ";  j = " + tc[bc[j]].getCityIndex( ) + ".  Zipcode -- i = " + tc[bc[i]].getZipcodeIndex( ) + ";  j = " + tc[bc[j]].getZipcodeIndex( ) + "." );
//End debug statement.

              //BC[I] has the better percentage.
              if( tc[bc[i]].getPercentValid( ) >=
                  tc[bc[j]].getPercentValid( ) )  {
//Debug statement.
//System.out.println( "( I -- TC[" + bc[i] + "] == " + tc[bc[i]].getPercentValid( ) + " ) >= ( J -- TC[" + bc[j] + "] == " + tc[bc[j]].getPercentValid( ) + " )." );
//End debug statement.

                x = i;
                y = j;
              }
              else  {  //BC[J] has the better percentage.
//Debug statement.
//System.out.println( "( J -- TC[" + bc[j] + "] == " + tc[bc[j]].getPercentValid( ) + " ) > ( I -- TC[" + bc[i] + "] == " + tc[bc[i]].getPercentValid( ) + " )." );
//End debug statement.
                x = j;
                y = i;
              }

              //The candidate with the lower percentage, indicated by BC[Y],
              //  must choose the next best candidate from the possible
              //  combinations.  Since a choice has been made between
              //  conflicting triplets the losing entry will never be valid and
              //  thus to avoid potential confusion later on the valid
              //  percentage is set to 0.0.
              tc[bc[y]].setPercentValid( 0.0 );  //Triplet will never be valid.
              g = ( y * c.length * zc.length );  //The index of the first viable triplet.
              index = -1;  //The index of the next best candidate.

              for( k = 0; k < ( c.length * zc.length ); k++ )  {
                //Do not allow duplicate city or zipcode fields when choosing
                //  the next best candidate.  Must check against all preceding,
                //  assigned entries.
                for( h = 0; h < j; h++ )  {
                  if( ( h != x ) && ( bc[h] >= 0 ) )  {
                    if( ( tc[( g + k )].getCityIndex( ) !=
                            tc[bc[h]].getCityIndex( ) ) &&
                        ( tc[( g + k )].getZipcodeIndex( ) !=
                            tc[bc[h]].getZipcodeIndex( ) ) )  {

                      //Is this the best percentage yet found.
                      if( ( ( index >= 0 ) && ( tc[index].getPercentValid( ) <
                            tc[( g + k )].getPercentValid( ) ) ) ||
                            ( index < 0 ) )  {

                        index = ( g + k );  //Update the current best candidate.
                      }
                    }
                  }
                }
              }

              //The next best candidate has been chosen therefore update the
              //  best candidate index.
              if( ( index >= 0 ) && ( tc[index].getPercentValid( ) >= crt ) )  {
//Debug statement.
//System.out.println( "2 -- BC[" + y + "] = " + index + " - {" + tc[index].getStateIndex( ) + ", " + tc[index].getCityIndex( ) + ", " + tc[index].getZipcodeIndex( ) + "}." );
//End debug statement.
                bc[y] = index;
              }
              else  {
//Debug statement.
//System.out.println( "2 -- BC[" + y + "] = -1." );
//End debug statement.
                bc[y] = -1;
              }
            }
          }
        }
      }
    }

    //Remove false positives.  A false positive is a state, city, or zipcode
    //  field that has not been assigned to a triplet and is not a duplicate of
    //  an entry in a triplet.

    //In order to prevent essentially duplicated code the PFP of each content
    //  type is referenced by a holder array and processed generically.  There
    //  are two switch{ } statements that indicate the only non-generic code.
    for( h = 0; h < 3; h++ )  {  //Consider each content type in turn.
      switch( h )  {  //Which content type is being considered.
        case 0:
//Debug statement.
//System.out.println( "FD = STATE, length = " + s.length + "." );
//End debug statement.
          fd = s;  //State PFP.
          break;
        case 1:
//Debug statement.
//System.out.println( "FD = CITY, length = " + c.length + "." );
//End debug statement.
          fd = c;  //City PFP.
          break;
        default:
//Debug statement.
//System.out.println( "FD = ZIPCODE, length = " + zc.length + "." );
//End debug statement.
          fd = zc;  //Zipcode PFP.
          break;
      }

      for( i = 0; i < fd.length; i++ )  {
//Debug statement.
//System.out.println( "" );
//System.out.println( fd[i] );
//End debug statement.

        b = true;
        j = 0;
        while( b && ( j < bc.length ) )  {
          if( bc[j] >= 0 )  {  // && ( i == tc[bc[j]].getStateIndex( ) ) )  {
            switch( h )  {
              case 0:  //State.
                if( i == tc[bc[j]].getStateIndex( ) )  {
//Debug statement.
//System.out.println( "State " + i + " assigned to a triplet." );
//End debug statement.
                  b = false;
                }
                break;
              case 1:  //City.
                if( i == tc[bc[j]].getCityIndex( ) )  {
//Debug statement.
//System.out.println( "City " + i + " assigned to a triplet." );
//End debug statement.
                  b = false;
                }
                break;
              default:  //Zipcode.
                if( i == tc[bc[j]].getZipcodeIndex( ) )  {
//Debug statement.
//System.out.println( "Zipcode " + i + " assigned to a triplet." );
//End debug statement.
                  b = false;
                }
                break;
            }

            if( b )  {
              j++;
            }

            //The field under consideration has been assigned to a triplet.
//            b = false;
          }
          else  {
            j++;
          }
        }

        if( b )  {  //If not assigned to a triplet check for duplicate fields.
          pos[0] = fd[i].getPosition( );  //Reuse the variable array POS.
          pos[1] = fd[i].getLength( );

          for( j = 0; j < bc.length; j++ )  {
            if( bc[j] >= 0 )  {
              switch( h )  {  //Which content type is being considered.
                case 0:
                  x = tc[bc[j]].getStateIndex( );
                  break;
                case 1:
                  x = tc[bc[j]].getCityIndex( );
                  break;
                default:
                  x = tc[bc[j]].getZipcodeIndex( );
                  break;
              }

//Debug statement.
//if( h == 1 )  {
//System.out.println( "X = " + x + "." );
//}
//End debug statement.
              pos[2] = fd[x].getPosition( );
              pos[3] = fd[x].getLength( );
              count = 0;

//Debug statement.
//System.out.println( "Current = {" + pos[0] + ", " + pos[1] + "};  Testing = {" + pos[2] + ", " + pos[3] + "}." );
//End debug statement.
              //Scan the records counting duplicate field values.
              for( k = 0; k < records.length; k++ )  {
                if( equal( records[k], pos[0], pos[1], pos[2], pos[3] ) )  {
//Debug statement.
//if( count <= 0 )  {
//System.out.println( ( new String( records[k], pos[0], pos[1] ) ) + " == " + ( new String( records[k], pos[2], pos[3] ) ) );
//}
//End debug statement.
                  count++;
                }
              }

              //If not a duplicate entry then remove it as a false positive.
              if( ( double )( ( double )count / ( double )records.length ) <
                  0.95 )  {

//Debug statement.
//System.out.println( "Removing...  " + fd[i] );
//End debug statement.
                re.removeEntry( fd[i] );
              }
//Debug statement.
else  {
//System.out.println( "Duplicate... " + fd[i] );
}
//End debug statement.
            }
          }
        }
      }
    }
//Debug statement.
//System.out.println( "Exiting cross()..." );
//End debug statement.
  }  /*  private void cross( char[][], FieldDescriptor[], FieldDescriptor[],
                             FieldDescriptor[], RecordEnumeration )  */


  /**
   *   The ZipcodeOracle's post processing procedures perform a cross reference
   * between state, city, and zipcode.  Currently the assumption is that the
   * reported state field will be correct.  Using this assumption the procedure
   * then finds the most correct city field, based on statistical analysis, and
   * then finally correlates this pair to a zipcode field.  Leftover zipcode
   * fields are then removed from the RecordEnumeration.
   *
   * FOR each state
   *   Find best matching city;
   *   Create { city, state } pair;
   * FOR each pair
   *   Find best matching zipcode;
   *   Create { city, state, zipcode } triplet;
   * Remove extraneous entries ( probably false positives )
   *   Remove state field not corresponding to a city field;
   *   Remove city fields not assigned to a { city, state } pair;
   *   Remove zipcode fields not assigned to a { city, state, zipcode } triplet;
   *   
   *   Considering each state, city, and zipcode field, all possible
   * combinations of the three types are evaluated.  Based on this evaluation
   * triplets are created from the combinations where the several types show
   * correlation to each other.  No two triplets may contain an identical
   * field.  Any fields not assigned to a triplet are then evaluated in order to
   * determine if they are a duplicate of a field assigned to a triplet.  If
   * neither assigned to a triplet nor a duplicate field the field is considered
   * a false positive and removed from the result.
   */
  public void postProcess( char[][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    FieldDescriptor[] c,
                      s,
                      zc;

    //Determine all the positions corresponding to a city, state, or zipcode.
    c = re.ofType( og.index( "city" ) );
    s = re.ofType( og.index( "state" ) );
    zc = re.ofType( og.index( "zipcode" ) );

    //Only perform a cross reference among field types if all are present.
    if( ( s != null ) && ( c != null ) && ( zc != null ) )  {
      cross( records, c, s, zc, re );
    }
  }  /*  public void postProcess( char[][], FieldDescriptor[][],
                                  RecordEnumeration )  */


  public void postProcess( char[][][] records, FieldDescriptor[][] fd,
                           RecordEnumeration re )  {

    
  }  /*  public void postProcess( char[][][], FieldDescriptor[][],
                                  RecordEnumeration )  */

}  /*  public class ZipcodeOracle implements Oracle, PostProcess  */
