package supportMethods;

//import java.util.Iterator;
import java.util.ArrayList;

import oracles.OracleGroup;
import supportObjects.GP;
import supportObjects.Position;
import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;

public class FindFields  {
//Debug statement.
//private static boolean tr = false;
//End debug statement.

  /**
   * @return TRUE if the characters contained within REC from SP to
   *         ( SP + LEN - 1 ) are all white space.
   */
  private static boolean isBlank( char[] rec, int sp, int len )  {
    boolean ret = true;
    int i = sp,
        ep = ( sp + len );

    while( ret && ( i < ep ) )  {
      if( Character.isWhitespace( rec[i] ) )  {
        i++;
      }
      else  {
        ret = false;
      }
    }

    return( ret );
  }  /*  private static boolean isBlank( char[], int, int )  */


  private static FieldDescriptor[] topResults( char[][] records,
                                               int oracleIndex,
                                               int[][] blanks,
                                               int[][] counts,
                                               OracleGroup og )  {

    int i,
        j,
        k,
        m = -1,
        mep;  //The maximum end position.
    //The index of the maximum valid count associated with each start position.
    int[] mvci;
    Position just;
    FieldDescriptor fd;
    FieldDescriptor[] ret = null;
    ArrayList<FieldDescriptor> al = new ArrayList<FieldDescriptor>( );

    //Record the maximum valid count for each start position.
    mvci = new int[counts.length];
    for( i = 0; i < counts.length; i++ )  {
      mvci[i] = 0;
      for( j = 1; j < counts[i].length; j++ )  {
//Debug statement.
//if( tr )  {
//if( ( ( i > 44 ) && ( i < 50 ) ) && ( counts[i][j] > 3 ) )  {
//System.out.println( i + "," + j + " - " + counts[i][j] );
//}
//}
//End debug statement.
        if( counts[i][j] > counts[i][mvci[i]] )  {
          mvci[i] = j;
        }
      }
//Debug statement.
//if( tr )  {
//if( ( ( i > 44 ) && ( i < 50 ) ) )  {
//System.out.println( "MVCI[I]] = " + mvci[i] + ";  Count = " + counts[i][mvci[i]] + "." );
//}
//}
//End debug statement.
    }

    //Due to the combinatoric approach in find() it is possible that multiple,
    //  grouped start positions will contain the same maximum valid count.
    i = 0;
    while( i < mvci.length )  {
      fd = new FieldDescriptor( );
      fd.setRecordCount( records.length );
      fd.setNumberValid( counts[i][mvci[i]] );
      fd.setBlankCount( blanks[i][mvci[i]] );

      //Determine the range of the grouping, if there is one.  There is a
      //  grouping if multiple, adjacent start positions have the same valid
      //  count.
      j = ( i + 1 );
      while( ( j < mvci.length ) &&
             ( counts[j][mvci[j]] == counts[i][mvci[i]] ) )  {

        j++;
      }
//Debug statement.
//if( tr )  {
//if( ( ( i > 44 ) && ( i < 50 ) ) )  {
//System.out.println( "PV = " + fd.getPercentValid( -1 ) + ";  MPT = " + og.getMinPercentage( oracleIndex ) + ";  record count = " + fd.getRecordCount( ) + ";  number valid = " + fd.getNumberValid( ) + ";  blank count = " + fd.getBlankCount( ) + ";  max blank percentage = " + og.getMaxBlankPercentage( oracleIndex ) + "." );
//System.out.println( "COUNTS[" + i + "][" + mvci[i] + "] = " + counts[i][mvci[i]] + "." );
//}
//}
//End debug statement.
      if( fd.getPercentValid( -1 ) > og.getMinPercentage( oracleIndex ) )  {
//      if( ( fd.getPercentValid( -1 ) > og.getMinPercentage( oracleIndex ) ) &&
//          ( ( double )( ( double )fd.getBlankCount( ) /
//                        ( double )fd.getRecordCount( ) ) <
//            og.getMaxBlankPercentage( oracleIndex ) ) )  {

//Debug statement.
//if( tr )  {
//System.out.println( "COUNTS[" + i + "][" + mvci[i] + "] = " + counts[i][mvci[i]] + "." );
//}
//if( tr )  {
//if( ( ( i > 44 ) && ( i < 50 ) ) )  {
//System.out.println( "SP - 1 = " + i + "." );
//}
//}
//End debug statement.
        //Find the maximum end position associated with this grouping of start
        //  positions.  The MEP is the position furthest away from the leftmost
        //  start position that contains the same valid count.
        mep = Integer.MIN_VALUE;
        for( k = i; k < j; k++ )  {
          //Consider each length associated with the start position, K.
          for( m = 0; m < counts[k].length; m++ )  {
            //If the valid count recorded at start position, K, and length, M,
            //  is equal to the maximum valid count for the grouping of start
            //  positions and the end position is greater than any considered so
            //  far then update MEP.
//!!Made a change at this line.  In order to determine a plateau the previous
//  logic only considered { start position, length } pairs with a count equal to
//  that of the largest count.  "( counts[k][m] == counts[i][mvci[i]] )" but now
//  the logic has been changed to allow for small drops in the plateau.
            if( counts[k][m] > 0 )  {
              if( ( ( counts[i][mvci[i]] - counts[k][m] ) < 3 ) &&
                  ( ( k + m ) > mep ) )  {

                mep = ( k + m );
              }
            }
          }
        }

//Debug statement.
//if( tr )  {
//if( ( ( i > 44 ) && ( i < 50 ) ) )  {
//System.out.println( "EP + 1 = " + mep + "." );
//}
//}
//End debug statement.
        //At this point the start position, I, and the end position, MEP,
        //  reflect the maximum range associated with this field for the maximum
        //  valid count associated with this grouping.  Using the field's
        //  justification this range may be able to be reduced to more
        //  accurately reflect the field's positioning.

        if( ( mep - i ) > 0 )  {  //The length of the field is greater than 0.
          just = justification( records, i, mep );

          if( just.getId( ) < 0 )  {
            i = just.getPosition( );
          }
          else if( just.getId( ) > 0 )  {
            mep = just.getPosition( );
          }

//Debug statement.
//if( tr )  {
//if( ( ( i > 44 ) && ( i < 50 ) ) )  {
//System.out.println( "SP = " + i + ";  EP = " + mep + ";  Justification = " + just.getId( ) + "." );
//}
//}
//End debug statement.
          //Only add a new entry to AL if the field length was greater than 0.
//          fd.setUncertain( ( double )( ( double )fd.getBlankCount( ) /
//                                       ( double )fd.getRecordCount( ) ) >=
//                           og.getMaxBlankPercentage( oracleIndex ) );

          fd.setJustification( just.getId( ) );
          fd.setLength( mep - i );
          fd.setPosition( i );
          al.add( fd );  //Add the newly created FieldDescriptor to the results.
        }
      }

      i = j;  //Set I equal to the index of the next start position to consider.
    }

    //Place the results into a structure more conducive to processing later on.
    if( al.size( ) > 0 )  {
      ret = new FieldDescriptor[al.size( )];
//Debug statement.
//if( tr )  {
//System.out.println( "Before cleanOverlap()..." );
//}
//End debug statement.
      for( i = 0; i < ret.length; i++ )  {
        ret[i] = al.get( i );
//Debug statement.
//if( tr )  {
//if( ( ret[i] != null ) && ( ret[i].getPosition( ) > 44 ) && ( ret[i].getPosition( ) < 50 ) )  {
//System.out.println( ret[i] );
//}
//}
//End debug statement.
      }
//Debug statement.
//if( tr )  {
//System.out.println( "" );
//}
//End debug statement.

      ret = cleanOverlap( ret );
    }

//Debug statement.
//if( tr )  {
//System.out.println( "After cleanOverlap()..." );
//for( i = 0; i < ret.length; i++ )  {
//if( ( ret[i] != null ) && ( ret[i].getPosition( ) > 44 ) && ( ret[i].getPosition( ) < 50 ) )  {
//System.out.println( ret[i] );
//}
//}
//System.out.println( "Exiting topResults()..." );
//}
//End debug statement.
    return( ret );
  }  /*  private static FieldDescriptor[] topResults( char[][], int[][],
                                                      int[][], double )  */


  /**
   *   Determines the justification of a field.
   *
   *   @param SP is the start position for the field.
   *   @param EP is the end position for the field.
   *
   *   @return <0 for left, 0 for center, and >0 for right justified.
   */
  private static Position justification( char[][] records, int sp, int ep )  {
    int i,
        j,
        k,
        fc = 0;  //The field count.  Some fields may be empty.
    int[] left = new int[( ep - sp )],
          right = new int[( ep - sp )];
    Position ret = new Position( 0, sp, ( ep - sp ) );
//Debug statement.
//int z = 0;
//End debug statement.

    for( i = 0; i < records.length; i++ )  {  //For each record
      //Skip any leading space characters.
      j = sp;
      while( ( j < ep ) && Character.isWhitespace( records[i][j] ) )  {
        j++;
      }
      if( j < ep )  {
        //Increment the count corresponding to this start position.
        left[( j - sp )]++;
        fc++;
//Debug statement.
//if( tr )  {
//if( ( z < 10 ) && ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.print( ( new String( records[i], sp, ( ep - sp ) ) ) + " --- left[" + ( j - sp ) + "] = " + left[( j - sp )] );
//}
//}
//End debug statement.
      }

      //Skip any trailing space characters.
      j = ( ep - 1 );
      while( ( j >= sp ) && Character.isWhitespace( records[i][j] ) )  {
        j--;
      }
      if( j >= sp )  {
        //Increment the count corresponding to this end position.
        right[( j - sp )]++;
//Debug statement.
//if( tr )  {
//if( ( z++ < 10 ) && ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( ";  right[" + ( j - sp ) + "] = " + right[( j - sp )] );
//}
//}
//End debug statement.
      }
    }

    i = 0;
    for( k = 1; k < left.length; k++ )  {
      if( left[k] > left[i] )  {
        i = k;
      }
    }

    j = 0;
    for( k = 1; k < right.length; k++ )  {
      if( right[k] > right[j] )  {
        j = k;
      }
    }

//Debug statement.
//if( tr )  {
//if( ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( "TopResult() -- SP = " + sp + ";  EP = " + ep + "." );
//System.out.println( "left[" + i + "] = " + left[i] + ";  right[" + j + "] = " + right[j] + "." );
//}
//}
//End debug statement.
//Debug statement.
//if( tr )  {
//if( ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( "FC = " + fc + "." );
//}
//}
//End debug statement.
    if( left[i] >= right[j] )  {
      if( j < i )  {
        fc -= right[j];
      }

//Debug statement.
//if( tr )  {
//if( ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( "FC = " + fc + "." );
//}
//}
//End debug statement.
      if( ( ( double )left[i] / ( double )fc ) > .90 )  {
        ret.setId( -1 );  //Left justified at position I.
        ret.setPosition( ( sp + i ), -1 );
//Debug statement.
//if( tr )  {
//if( ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( "Setting left justified..." );
//}
//}
//End debug statement.
      }
    }
    else if( right[j] > left[i] )  {
//Debug statement.
//if( tr )  {
//if( ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( "FC = " + fc + "." );
//}
//}
//End debug statement.
      if( i > j )  {
        fc -= left[i];
      }

//Debug statement.
//if( tr )  {
//if( ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( "FC = " + fc + "." );
//}
//}
//End debug statement.
      if( ( ( double )right[j] / ( double )fc ) > .90 )  {
        ret.setId( 1 );  //Right justified at position J.
        ret.setPosition( ( sp + j ), -1 );
//Debug statement.
//if( tr )  {
//if( ( sp > 348 ) && ( sp < 358 ) )  {
//System.out.println( "Setting right justified..." );
//}
//}
//End debug statement.
      }
    }

    return( ret );
  }  /*  private static Position justification( char[][], int, int )  */


  private static FieldDescriptor[] cleanOverlap( FieldDescriptor[] fd )  {
    int c = fd.length,
        i,
        j;
    FieldDescriptor[] ret = null;

    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] != null )  {
        for( j = ( i + 1 ); j < fd.length; j++ )  {
          if( fd[j] != null )  {
            //Given a potential field position, I, the valid count for a nearby
            //  potential field position, J, may essentially be the same because
            //  the two fields overlap by a significant amount.  In this case
            //  remove the potential field position with the lowest valid count.
            if( Position.percentOverlap( fd[i].getFieldPosition( ),
                                         fd[j].getFieldPosition( ) ) > 0.85 )  {

              if( fd[i].getNumberValid( ) >= fd[j].getNumberValid( ) )  {
                fd[j] = null;
              }
              else  {
                fd[i] = null;
                i = j;
              }

              c--;
            }
          }
        }
      }
    }

    if( c > 0 )  {
      ret = new FieldDescriptor[c];

      j = 0;
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          ret[j++] = fd[i];
        }
      }
    }

    return( ret );
  }  /*  private static FieldDescriptor[] cleanOverlap( FieldDescriptor[] )  */


  private static void place( FieldDescriptor fd, ArrayList<FieldDescriptor> al,
                             OracleGroup og )  {

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
//Debug statement.
//if( fd.getName( ).equals( "last name" ) )  {
//System.out.println( "last name conflicts with --> " + al.get( i ) );
//}
//End debug statement.
      }
    }

    i = 0;
    while( b && ( i < c.size( ) ) )  {
      if( ( og.getRank( fd.getType( ) ) <
            og.getRank( c.get( i ).getType( ) ) ) ||
          ( ( og.getRank( fd.getType( ) ) ==
              og.getRank( c.get( i ).getType( ) ) ) &&
            ( fd.getPercentValid( c.get( i ).getGrouping( ) ) <
              c.get( i ).getPercentValid( fd.getGrouping( ) ) ) ) )  {

        b = false;
      }
      else  {
        i++;
      }
    }

    if( b )  {
//Debug statement.
//if( fd.getName( ).equals( "last name" ) )  {
//System.out.println( "Last name wins." );
//}
//End debug statement.
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
//Debug statement.
//if( fd.getName( ).equals( "last name" ) )  {
//System.out.println( "---" );
//}
//End debug statement.
  }  /*  private static void place( FieldDescriptor, ArrayList<FieldDescriptor>,
                                    OracleGroup )  */


  /**
   *   This method only applies to fixed length files, hybrid and fully fixed.
   * It is possible, due entirely to blank fields that two fields could overlap
   * significantly based on the information ascertained from the combinatoric
   * approach.  When this happens one of the fields will not be placed in the
   * RecordEnumeration though both should exist.  This method corrects the
   * mistake by attempting to update the Position of the FieldDescriptor not
   * assigned to the RecordEnumeration.
   * 
   * @return Though this method does not return anything it has the side effect
   *         of updating the RecordEnumeration parameter, RE.
   */
  public static void updateEmpty( char[][] records, OracleGroup og,
                                  RecordEnumeration re,
                                  FieldDescriptor[][] fd )  {

    int c,
        i,
        j,
        k,
        x,
        y,
        bc,  //New blank count.
        pos,
        len,
        index;
    Position[] up;  //The unspecified Positions from the RecordEnumeration.
    FieldDescriptor fdh;
    ArrayList<FieldDescriptor> alfd;
    ArrayList<FieldDescriptor> nfd;

    //First test if there are any UNSPECIFIED ranges in the RecordEnumeration.
    if( ( up = re.unspecified( ) ) != null )  {
      for( i = 0; i < up.length; i++ )  {  //Consider each UNSPECIFIED region.
//Debug statement.
//if( up[i].getPosition( ) > 900 && up[i].getPosition( ) < 950 )  {
//System.out.println( "unspecified -- P = " + up[i].getPosition( ) + ", LEN = " + up[i].getLength( ) + "." );
//System.out.println( "" );
//}
//End debug statement.

        //Reset ALFD.  This structure will contain all the FieldDescriptors that
        //  overlap the Position at index H.
        alfd = new ArrayList<FieldDescriptor>( );

        for( j = 0; j < fd.length; j++ )  {  //Look at each content type.
          if( fd[j] != null )  {
            for( k = 0; k < fd[j].length; k++ )  {  //Each FieldDescriptor.
              if( fd[j][k].getFieldPosition( ).overlaps( up[i] ) )  {
//Debug statement.
//if( up[i].getPosition( ) > 800 && up[i].getPosition( ) < 900 )  {
//System.out.println( fd[j][k] );
//}
//End debug statement.
                alfd.add( fd[j][k] );
              }
            }
          }
        }

        //Once a list of overlapping FieldDescriptors has been generated remove
        //  any that are already in the RecordEnumeration.
        j = 0;
        while( j < alfd.size( ) )  {
          if( !re.contains( alfd.get( j ) ) )  {
            j++;
          }
          else  {
            alfd.remove( j );
          }
        }
//Debug statement.
//if( up[i].getPosition( ) > 800 && up[i].getPosition( ) < 900 )  {
//for( j = 0; j < alfd.size( ); j++ )  {
//System.out.println( alfd.get( j ) );
//}
//System.out.println( "---" );
//}
//End debug statement.

        pos = up[i].getPosition( );
        len = up[i].getLength( );
        //Consider each of the overlapping FieldDescriptors in turn.  The
        //  purpose is to see if the count for the unspecified Position is
        //  greater than or equal to the FieldDescriptor's current valid count.
        //  If it is then update the FieldDescriptor's Position to correspond to
        //  the unspecified Position.
        for( j = 0; j < alfd.size( ); j++ )  {
          fdh = alfd.get( j );
//Debug statement.
//if( up[i].getPosition( ) > 900 && up[i].getPosition( ) < 950 )  {
//System.out.println( "Original --> " + fdh );
//}
//End debug statement.
          //The existing PFL's position and length may not contain all of UP[I],
          //  instead they may include only a subsection.  If this is the case
          //  then only check the appropriate portion of UP[I].
          if( pos >= fdh.getPosition( ) )  {
            x = pos;
          }
          else  {
            x = fdh.getPosition( );
          }
          if( ( pos + len ) <= ( fdh.getPosition( ) + fdh.getLength( ) ) )  {
            y = ( ( pos + len ) - x );
          }
          else  {
            y = ( ( fdh.getPosition( ) + fdh.getLength( ) ) - x );
          }

          c = 0;
          bc = 0;
          index = fdh.getType( );

          //Only recount and update the PF if it has been resized.
          if( ( x != fdh.getPosition( ) ) ||
              ( ( x + y ) != ( fdh.getPosition( ) + fdh.getLength( ) ) ) )  {

            //Determine the valid count for the new Position.
            for( k = 0; k < records.length; k++ )  {
              if( og.isValid( records[k], x, y, index ) )  {
                c++;
              }
              else if( isBlank( records[k], x, y ) )  {
                bc++;
              }
            }

//Debug statement.
//if( up[i].getPosition( ) > 900 && up[i].getPosition( ) < 950 )  {
//System.out.println( "Resize --> X = " + ( x + 1 ) + ";  Y = " + y + ";  C = " + c + ";  BC = " + bc + ";  MIN% = " + og.getMinPercentage( index ) + ";  MB% = " + og.getMaxBlankPercentage( index ) + "." );
//}
//End debug statement.
            //Test if the new valid count passes the minimum threshold.
            if( ( bc >= records.length ) || 
                ( double )( ( double )c / ( double )( ( double )records.length -
                                                      ( double )bc ) ) <
                og.getMinPercentage( index ) )  {

              alfd.set( j, null );  //If not remove it from candidacy.
            }
            else  {  //Update the FieldDescriptor.
              //If the blank threshold is surpassed mark the updated field as
              //  uncertain.
//              if( ( ( double )( ( double )bc / ( double )records.length ) ) >
//                    og.getMaxBlankPercentage( index ) )  {
//
//                fdh.setUncertain( true );
//              }

              fdh.setNumberValid( c );
              fdh.setBlankCount( bc );
              fdh.setPosition( x );
              fdh.setLength( y );
//Debug statement.
//if( up[i].getPosition( ) > 800 && up[i].getPosition( ) < 900 )  {
//System.out.println( "Alter --> X = " + ( x + 1 ) + ";  Y = " + y + ";  C = " + c + ";  BC = " + bc + ";  MIN% = " + og.getMinPercentage( index ) + ";  MB% = " + og.getMaxBlankPercentage( index ) + "." );
//}
//End debug statement.
            }

          //Test the new valid count against the old.  If it is at least the
          //  same then update the FieldDescriptor to fit into the unspecified
          //  region.
//          if( c >= fdh.getNumberValid( ) )  {
          //Test to see if the new valid and blank count pass their respective
          //  thresholds.  If so update the PF information and test the RE to
          //  see if this PF should be reported.
//          if( ( ( double )( ( double )c / ( double )( ( double )records.length -
//                                                      ( double )bc ) ) >
//                og.getMinPercentage( index ) ) &&
//              ( ( ( double )( ( double )bc / ( double )records.length ) ) <
//                  og.getMaxBlankPercentage( index ) ) )  {
//
//            fdh.setNumberValid( c );  //Update the FieldDescriptor.
//            fdh.setBlankCount( bc );
//            fdh.setPosition( x );
//            fdh.setLength( y );
////Debug statement.
//if( up[i].getPosition( ) > 800 && up[i].getPosition( ) < 900 )  {
//System.out.println( "Reset --> " + fdh + "." );
//}
////End debug statement.
//
//            //Determine the index, CM, of the most appropriate field for the RE.
//            if( cm >= 0 )  {
//              if( ( ( og.getRank( index ) ==
//                      og.getRank( alfd.get( cm ).getType( ) ) ) &&
//                    ( c > alfd.get( cm ).getNumberValid( ) ) ) ||
//                  ( og.getRank( index ) >
//                    og.getRank( alfd.get( cm ).getType( ) ) ) )  {
//
//                cm = j;
//              }
//            }
//            else  {
//              cm = j;
//            }
//          }
          }
        }

        //Remove the NULL entries from the last stage.
        j = 0;
        while( j < alfd.size( ) )  {
          if( alfd.get( j ) == null )  {
            alfd.remove( j );
          }
          else  {
            j++;
          }
        }

        //Create a local RE corresponding to the current UNSPECIFIED region in
        //  the ArrayList NFD.
        if( !alfd.isEmpty( ) )  {
          nfd = new ArrayList<FieldDescriptor>( );
          for( j = 0; j < alfd.size( ); j++ )  {
            place( alfd.get( j ), nfd, og );
          }

          //Update the unspecified region of the RE to the new FD.
          for( j = 0; j < nfd.size( ); j++ )  {
//Debug statement.
//if( up[i].getPosition( ) > 900 && up[i].getPosition( ) < 950 )  {
//System.out.println( "Update RE --> " + nfd.get( j ) + "." );
//}
//End debug statement.
            re.setEntry( nfd.get( j ) );
          }
        }
      }
    }
  }  /*    public static void updateEmpty( char[][], OracleGroup,
                                           RecordEnumeration,
                                           FieldDescriptor[][] )  */


  /**
   *   Any UNSPECIFIED positions are tested to determine if an uncertain PF
   * could be assigned to the position.
   */
  public static void updateEmpty( OracleGroup og, RecordEnumeration re,
                                  FieldDescriptor[][] fd )  {

    int i,
        j,
        k;
    FieldDescriptor fdh;
    ArrayList<FieldDescriptor> alfd;

    for( i = 0; i < re.getRecordLength( ); i++ )  {
      fdh = re.type( i );

      if( fdh.getName( ).equals( "unspecified" ) )  {
        alfd = new ArrayList<FieldDescriptor>( );

        //Find any uncertain PF at the current position.
        for( j = 0; j < fd.length; j++ )  {
          if( fd[j] != null )  {
            for( k = 0; k < fd[j].length; k++ )  {
              //Only uncertain fields were not considered during decision().
              if( fd[j][k].getUncertain( ) &&
                  ( fd[j][k].getPosition( ) == i ) )  {

                alfd.add( fd[j][k] );
              }
            }
          }
        }

        if( !alfd.isEmpty( ) )  {  //Any PF corresponding to this position?
          //First find the index of a PF with the highest confidence ranking of
          //  all the conflicting PF. 
          fdh = alfd.get( 0 );
          k = og.getRank( fdh.getType( ) );
          for( j = 1; j < alfd.size( ); j++ )  {
            if( og.getRank( alfd.get( j ).getType( ) ) > k )  {
              fdh = alfd.get( j );
              k = og.getRank( fdh.getType( ) );  //Update the highest rank.
            }
          }

          //For all PF with the same rank find the one with the greatest valid
          //  percentage.  
          fdh = null;
          for( j = 0; j < alfd.size( ); j++ )  {
            if( og.getRank( alfd.get( j ).getType( ) ) == k )  {
              if( fdh != null )  {
                //Test the valid percentage of both equally ranked PF.
                if( alfd.get( j ).getPercentValid( fdh.getGrouping( ) ) >
                    fdh.getPercentValid( alfd.get( j ).getGrouping( ) ) )  {

                  fdh = alfd.get( j );
                }
              }
              else  {
                fdh = alfd.get( j );  //The first PF with rank == k.
              }
            }
          }

          if( fdh != null )  {
            re.setEntry( fdh );  //Update the RE.
          }
        }
      }
    }
  }  /*    public static void updateEmpty( OracleGroup, RecordEnumeration,
                                           FieldDescriptor[][] )  */


  /**
   *   This method is to be run on fixed files.
   *   Several content types have overlapping domains.  After the potential
   * field locations have been found, reduce the databases of these particular
   * oracles and count how many of the entries remain valid.  By reducing the
   * databases the overlap will be reduced also.
   *   Which oracles are reduced is specified in the OracleGroup.java container
   * class.
   * 
   * @return This method does not return anything but it does have the side
   *         effect of setting the SecondaryNumberValid field in FD.  This field
   *         will affect the FD.getValidCount() method used in fixedDecision().
   */
  public static void typeOverlap( char[][] rec, OracleGroup og,
                                       FieldDescriptor[][] fd )  {

    int i,
        j,
        k,
        oc = og.oraclesToReduceCount( ),  //Oracle count.
        sp,  //Start position of a particular potential field location.
        len,  //Length of a particular potential field location.
        count,
        index;  //The current oracle being examined.

    //Ensure the ordering is that specified by the OracleGroup.
    og.reduceReset( );
//Debug statement.
//System.out.println( "OC = " + oc + "." );
//End debug statement.

    for( i = 0; i < oc; i++ )  {
      //Grab the next oracle whose database can be reduced as specified by the
      //  OracleGroup.
      index = og.nextToReduce( );
//Debug statement.
//System.out.println( "Looking at reduced oracle - " + og.name( index ) + "." );
//End debug statement.

      //Only consider oracles that have potential field locations.
      if( fd[index] != null )  {
        //Reduce this oracle's database.
        og.setMinDatabase( index );

        for( j = 0; j < fd[index].length; j++ )  {
          count = 0;  //Reset the count for the new field.
          sp = fd[index][j].getPosition( );  //Get the field start position.
          len = fd[index][j].getLength( );  //Get the field length.

          //Scan the records again, only for the associated start position and
          //  length, and count the number valid corresponding to the reduced
          //  oracle database.
          for( k = 0; k < rec.length; k++ )  {
            if( og.isValid( rec[k], sp, len, index ) )  {
              count++;
            }
          }

          fd[index][j].setSecondaryValid( count );  //Store the new count.
        }
      }
    }
  }  /*    public static void typeOverlap( char[][], OracleGroup,
                                                FieldDescriptor[][] )  */


  /**
   *   This method is to be run on fully delimited files.
   *   Several content types have overlapping domains.  After the potential
   * field locations have been found, reduce the databases of these particular
   * oracles and count how many of the entries remain valid.  By reducing the
   * databases the overlap will be reduced also.
   *   Which oracles are reduced is specified in the OracleGroup.java container
   * class.
   * 
   * @return This method does not return anything but it does have the side
   *         effect of setting the SecondaryNumberValid field in FD.  This field
   *         will affect the FD.getValidCount() method used in fixedDecision().
   */
  public static void typeOverlap( char[][][] rec, OracleGroup og,
                                           FieldDescriptor[][] fd )  {

    int i,
        j,
        k,
        oc = og.oraclesToReduceCount( ),  //Oracle count.
        pos,  //Position of a particular potential field location.
        count,
        index;  //The current oracle being examined.

    //Ensure the ordering is that specified by the OracleGroup.
    og.reduceReset( );

    for( i = 0; i < oc; i++ )  {
      //Grab the next oracle whose database can be reduced as specified by the
      //  OracleGroup.
      index = og.nextToReduce( );

      //Only consider oracles that have potential field locations.
      if( fd[index] != null )  {
        //Reduce this oracle's database.
        og.setMinDatabase( index );

        for( j = 0; j < fd[index].length; j++ )  {
          count = 0;  //Reset the count for the new field.
          pos = fd[index][j].getPosition( );  //Get the field position.

          //Scan the records again, only for the associated start position and
          //  length, and count the number valid corresponding to the reduced
          //  oracle database.
          for( k = 0; k < rec.length; k++ )  {
            if( pos < rec[k].length )  {
              if( og.isValid( rec[k][pos], index ) )  {
                count++;
              }
            }
          }

          fd[index][j].setSecondaryValid( count );  //Store the new count.
        }
      }
    }
  }  /*    public static void typeOverlap( char[][][], OracleGroup,
                                                    FieldDescriptor[][] )  */


  /**
   *   This decision method has been adapted from the original to allow for the
   * notion of enclaves / groupings.
   *   The varying content types have been subdivided into enclaves or
   * groupings.  This division should allow the decision methodology to make a
   * better choice among the potential fields (PF) acquired in the
   * combinatoric stage.  ( i.e., decision( ) will use the primary count when
   * choosing between similar content types such as AddressLineOne and
   * AddressLine but will use the secondary count, the count tabulated after
   * reducing the appropriate oracles' knowledge bases, when choosing between
   * dissimilar content types such as FirstName and AddressLineOne. )  This
   * should provide a better approach to using the evidence required by content
   * type domain overlap.  Though it is necessary to use secondary counts to
   * discriminate between dissimilar content types with overlapping domains,
   * this count should not be considered as reliable as the primary count when
   * considering similar content types for which the domain overlap problem is
   * not applicable.
   *
   * @param RL the record length as determined by find().
   * @param FD the list of potential field positions along with other pertinent
   *           information.
   * @return An instance of a RecordEnumeration object that details the
   *         positions of fields within the record.
   */
  public static RecordEnumeration decision( char[][] rec, OracleGroup og,
                                            FieldDescriptor[][] fd )  {

    boolean b;
    int h,
        i,
        j,
        k,
        oc = og.oracleCount( ),
        index;
    RecordEnumeration ret;
    ArrayList<FieldDescriptor> c = new ArrayList<FieldDescriptor>( );

    ret = new RecordEnumeration( GP.result.getRecordLength( ) );

    og.rankReset( );
    for( i = 0; i < oc; i++ )  {  //For each of the oracle types.
      index = og.nextByRank( );
//Debug statement.
//System.out.println( "Looking at type - " + og.name( index ) + "." );
//System.out.println( "Type - " + og.name( index ) + ";  Grouping - " + og.getGrouping( index ) + "." );
//System.out.println( "" );
//End debug statement.

      //Are there any potential field positions (PFP) for the current oracle?
      if( fd[index] != null )  {
        for( j = 0; j < fd[index].length; j++ )  {  //Look at each PFP.
          //During the decision ( i.e., conflict resolution ) stage only
          //  consider PF that are considered to be certain.  Immediately
          //  afterwards in updateEmpty() any UNSPECIFIED regions will be
          //  updated to include fields marked as uncertain.
          if( !fd[index][j].getUncertain( ) )  {
          //Originally 'certain' only meant the field was not sparse.
//          if( ( double )( ( double )fd[index][j].getBlankCount( ) /
//                          ( double )fd[index][j].getRecordCount( ) ) <
//              og.getMaxBlankPercentage( index ) )  {

//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "J = " + j + ";  Looking at entry 1 --- " + fd[index][j] );
//}
//End debug statement.
            //Determine if any fields already stored within RET within the range
            //  of FD[INDEX][K].
            c = ret.conflict( fd[index][j] );
//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "J = " + j + ";  Looking at entry 2 --- " + fd[index][j] );
//System.out.println( "Conflict count = " + c.size( ) + "." );
//for( h = 0; h < c.size( ); h++ )  {
//System.out.println( "Type = " + c.get( h ).getName( ) + ".  SP = " + c.get( h ).getPosition( ) + ";  LEN = " + c.get( h ).getLength( ) + ";  PC = " + c.get( h ).getNumberValid( ) + ";  SC = " + c.get( h ).getSecondaryValid( ) + "." );
//}
//}
//End debug statement.

            h = 0;
            k = 1;
            b = ( !c.isEmpty( ) && ( og.getRank( c.get( h ).getType( ) ) <=
                                     og.getRank( index ) ) );

//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "J = " + j + ";  Looking at entry 3 --- " + fd[index][j] );
//System.out.println( "Before while{}, B = " + b + ";  K = " + k + ";  C size = " + c.size( ) + "." );
//}
//End debug statement.
            //Look at each conflicting entry in turn.
            while( b && ( k < c.size( ) ) )  {
              //For all the conflicting entries with equal rank test the valid
              //  count to determine if the incoming entry is a better fit.
              if( og.getRank( c.get( k ).getType( ) ) ==
                  og.getRank( index ) )  {

                if( c.get( k ).getPercentValid( fd[index][j].getGrouping( ) ) >
                    c.get( h ).getPercentValid(
                        fd[index][j].getGrouping( ) ) )  {

//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "J = " + j + ";  Looking at entry 4 --- " + fd[index][j] );
//System.out.println( "Updating index from " + h + " with " + c.get( h ).getPercentValid( fd[index][j].getGrouping( ) ) + " to " + k + " with " + c.get( k ).getPercentValid( fd[index][j].getGrouping( ) ) + "." );
//}
//End debug statement.
                  h = k;
                }
              }

              //If there is a conflicting entry with a higher rank then do not
              //  even consider the incoming entry.
              else if( og.getRank( c.get( k ).getType( ) ) >
                       og.getRank( index ) )  {

                b = false;
              }

              k++;  //Increment to the next conflicting field.
//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "J = " + j + ";  Looking at entry 5 --- " + fd[index][j] );
//System.out.println( "End of while{}, incremented K to " + k + ";  B = " + b + "." );
//}
//End debug statement.
            }

//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "J = " + j + ";  Looking at entry 6 --- " + fd[index][j] );
//if( b )  {
//System.out.println( "FD = " + fd[index][j].getPercentValid( c.get( h ).getGrouping( ) ) + ";  c[h] = " + c.get( h ).getPercentValid( fd[index][j].getGrouping( ) ) + "." );
//}
//}
//End debug statement.
            //If the incoming entry has a rank greater than or equal to all
            //  conflicting entries and if its valid count is greater than all
            //  conflicting entries with an equal rank then update the
            //  RecordEnumeration.
            if( c.isEmpty( ) || ( b &&
                ( fd[index][j].getPercentValid( c.get( h ).getGrouping( ) ) >
                  c.get( h ).getPercentValid(
                      fd[index][j].getGrouping( ) ) ) ) )  {

              ret.setEntry( fd[index][j] );
//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "J = " + j + ";  Looking at entry 7 --- " + fd[index][j] );
//System.out.println( "Updating RecordEnumeration with PFP --- " + fd[index][j] + "." );
//}
//End debug statement.
            }
          }
//Debug statement.
//if( ( index == og.index( "name suffix" ) ) && ( fd[index][j].getPosition( ) > 155 ) && ( fd[index][j].getPosition( ) < 160 ) )  {
//System.out.println( "" );
//}
//End debug statement.
        }
      }
//Debug statement.
//System.out.println( "----------" );
//System.out.println( "" );
//End debug statement.
    }

    return( ret );
  }  /*  private static RecordEnumeration decision( char[][], OracleGroup,
                                                    FieldDescriptor[][] )  */


  /**
   *   This method is for fully delimited files.
   */
  public static RecordEnumeration decision( OracleGroup og,
                                            FieldDescriptor[][] fd )  {

    int h,
        i,
        j,
        k,
        m,
        rl = GP.result.getRecordLength( );  //The record length
    RecordEnumeration ret = new RecordEnumeration( rl );
    FieldDescriptor fdh;
    ArrayList<FieldDescriptor> alfd;

    for( h = 0; h < rl; h++ )  {  //Consider each field position in turn.
      alfd = new ArrayList<FieldDescriptor>( );
      fdh = null;

      //Find all potential field positions that contain position I.
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          for( j = 0; j < fd[i].length; j++ )  {
            if( !fd[i][j].getUncertain( ) &&
                Position.contains( h, fd[i][j].getFieldPosition( ) ) )  {

              alfd.add( fd[i][j] );
            }
          }
        }
      }

      if( alfd.size( ) > 0 )  {
        //First determine the the highest ranking from the list of conflicting
        //  FieldDescriptors.
        j = 0;
        k = og.getRank( alfd.get( 0 ).getType( ) );
        for( i = 1; i < alfd.size( ); i++ )  {
          if( ( m = og.getRank( alfd.get( i ).getType( ) ) ) > k )  {
            j = i;
            k = m;
          }
        }

        //Then for all the FieldDescriptors with the same rank find the one with
        //  the highest valid count.
        for( i = ( j + 1 ); i < alfd.size( ); i++ )  {
          fdh = alfd.get( i );

          if( og.getRank( fdh.getType( ) ) == k )  {
            if( fdh.getPercentValid( alfd.get( j ).getGrouping( ) ) >
                alfd.get( j ).getPercentValid( fdh.getGrouping( ) ) )  {

              j = i;
            }
          }
        }

        ret.setEntry( alfd.get( j ) );
      }
    }

    return( ret );
  }  /*  private static FieldDescriptor decision( int,
                                                  FieldDescriptor[][] )  {  */


  /**
   *   This method is for fixed length files, hybrid and fully fixed.
   */
  public static FieldDescriptor[][] find( char[][] rec, OracleGroup og )  {
//Debug statement.
//boolean b;
//End debug statement.
    int h,
        i,
        j,
        k,
        m,
        oc = og.oracleCount( ),
        inc,  //The increment amount for grabbing random records.
        len,  //The number of characters from the current position to check.
        nbc,  //The non-blank count.
        index;  //The current oracle being used.
//Report statement.
//int sampleTwo, sampleComplete;
//End report statement.
    int[][] bc;  //The count of blank fields for a start position and length.
    int[][] count;  //Counts valid entries at a given start position and length.
    FieldDescriptor[][] fd = new FieldDescriptor[og.oracleCount( )][];

    bc = new int[rec[0].length][rec[0].length];
    count = new int[rec[0].length][rec[0].length];
    og.reset( );

    for( h = 0; h < oc; h++ )  {  //For each oracle.
      index = og.next( );
      len = og.getMaxLength( index );

//Report statement.
//sampleTwo = 0;
//sampleComplete = 0;
//End report statement.
//Debug statement.
//System.out.println( og.name( index ) );
//End debug statement.
      //Clear COUNT.
      for( i = 0; i < count.length; i++ )  {
        for( j = 0; j < count[0].length; j++ )  {
          count[i][j] = 0;
        }
      }

      //Start positions.
      for( i = 0; i < rec[0].length; i++ )  {
//Debug statement.
//if( index == og.index( "name suffix" ) )  {
//if( i == 296 )  {
//for( j = 0; j < 50; j++ )  {
//System.out.print( count[296][j] + ", " );
//}
//System.out.print( "" );
//System.out.print( "" );
//}
//}
//End debug statement.
        //End positions.
        for( j = ( i + 1 ); ( ( j <= rec[0].length ) && ( j <= ( i + len ) ) );
             j++ )  {

          if( rec.length > ( 2 * GP.rtt ) )  {
//Report statement.
//sampleTwo++;
//End report statement.
            //As a heuristic only test a limited number of records for the
            //  current start position, length pair.  ( Take a sampling. )
            inc = ( int )Math.floor( ( double )rec.length / ( double )GP.rtt );

            m = 0;
            nbc = 0;

            //For the given start position and length attempt to find GP.RTT
            //  non-blank sequences of characters to test.
            while( ( nbc < GP.rtt ) && ( m < rec.length ) )  {
              for( k = m++; k < rec.length; k += inc )  {  //Scan the records.
                if( !isBlank( rec[k], i, ( j - i ) ) )  {
                  nbc++;

                  if( og.isValid( rec[k], i, ( j - i ), index ) )  {
                    count[i][( j - i )]++;
                  }
                }
              }

              m++;
            }
          }

          //If an acceptable count from the sampling evaluate to TRUE then test
          //  all the records.  There is a limited number ( GP.rtt ) of
          //  redundant computations if COUNT >= GP.arc.
          if( ( count[i][( j - i )] >= GP.arc )// )  {
              || ( rec.length <= ( 2 * GP.rtt ) ) )  {

//Report statement.
//sampleComplete++;
//End report statement.
            count[i][( j - i )] = 0;  //Reset count.
            bc[i][( j - i )] = 0;  //Reset blank field count.

//Debug statement.
//b = ( ( index == og.index( "name suffix" ) ) && ( i == 296 ) && ( ( j - i ) == 2 ) );
//End debug statement.
            //Records.
            for( k = 0; k < rec.length; k++ )  {
//Debug statement.
//if( b )  {
//System.out.println( ( new String( rec[k], i, ( j - i ) ) ) + "| - " + og.isValid( rec[k], i, ( j - i ), index ) + "." );
//}
//End debug statement.
              if( isBlank( rec[k], i, ( j - i ) ) )  {
                bc[i][( j - i )]++;
              }
              else if( og.isValid( rec[k], i, ( j - i ), index ) )  {
                //COUNT[a][b] is the number of fields found that start at
                //  position 'a' with length 'b'.
                count[i][( j - i )]++;
              }
//              if( og.isValid( rec[k], i, ( j - i ), index ) )  {
//                //COUNT[a][b] is the number of fields found at position 'a' with
//                //  length 'b'.
//                count[i][( j - i )]++;
//              }
//              else if( isBlank( rec[k], i, ( j - i ) ) )  {
//                bc[i][( j - i )]++;
//              }
            }

          }
        }
      }
//Debug statement.
//if( index == og.index( "name suffix" ) )  {
//for( i = 100; i < 109; i++ )  {
//System.out.print( i + " --- " );
//for( j = 0; j < 15; j++ )  {
//System.out.print( j + "-" + count[i][j] + ", " );
//}
//System.out.println( "" );
//}
//for( i = 0; i < 50; i++ )  {
//System.out.print( count[296][i] + ", " );
//}
//System.out.print( "" );
//for( i = 0; i < rec.length; i++ )  {
//System.out.println( ( new String( rec[i], 296, 34 ) ) + "| - " + og.isValid( rec[i], 296, 34, index ) + "." );
//}

//tr = true;
//}
//else  {
//tr = false;
//}
//System.out.println( "Before top results..." );
//End debug statement.

      fd[index] = topResults( rec, index, bc, count, og );
//Debug statement.
//if( tr )  {
//System.out.println( "After top results..." );
//}
//End debug statement.
      if( fd[index] != null )  {
        for( i = 0; i < fd[index].length; i++ )  {
          fd[index][i].setType( index );
          fd[index][i].setGrouping( og.getGrouping( index ) );
          fd[index][i].setName( og.name( index ) );
//Debug statement.
//if( tr )  {
//System.out.println( fd[index][i] );
//}
//End debug statement.
        }

        //After determining the PFP from horizontal analysis perform vertical
        //  analysis on the applicable PFP to ensure they are truely a PFP.
        fd[index] = og.preProcess( rec, index, fd[index] );
//Debug statement.
//System.out.println( "After pre..." );
//if( index == og.index( "address line one" ) )  {
//System.out.println( "" );
//System.out.println( "" );
//}
//End debug statement.
      }
//Report statement.
//System.out.println( og.name( index ) + " --  sample^2 = " + sampleTwo + ";  complete sample = " + sampleComplete + ";  max field length = " + og.getMaxLength( index ) + "." );
//End report statement.
    }  //END FOR each oracle.

    return( fd );
  }  /*  public static FieldDescriptor[][] find( char[][], OracleGroup )  */


  /**
   *   This method finds field positions in fully delimited files.
   */
  public static FieldDescriptor[][] find( char[][][] rec, OracleGroup og )  {
    int i,
        j,
        k,
        oc = og.oracleCount( );
    //The rows correspond to the oracles as specified by the OracleGroup.  The
    //  columns correspond to the field position.  So count[0][2] is how many
    //  occurences of oracle zero occur in the third field.  Add to the column
    //  count to allow for error within the sample.
    int[][] count = new int[oc][BasicAttributes.maxRecordLength( rec )];
    int[] bc = new int[count[0].length];  //Blank field count.
    double mpt;  //Minimum percentage for a given oracle.
    FieldDescriptor[][] fd = new FieldDescriptor[oc][];  //The returned PFPs.
    ArrayList<Integer> al;

    //Initialize the blank count variable.
    for( i = 0; i < bc.length; i++ )  {
      bc[i] = 0;
    }

    for( i = 0; i < rec.length; i++ )  {  //Records.
      for( j = 0; j < rec[i].length; j++ )  {  //Fields.
        //First test to see if the current field is blank.  The first equality
        //  test is a simple hueristic to try and avoid a function call.
        if( ( rec[i][j].length == 0 ) ||
            isBlank( rec[i][j], 0, rec[i][j].length ) )  {

          bc[j]++;
        }
        else  {  //Otherwise test the field against all the oracles.
          og.reset( );
          for( k = 0; k < oc; k++ )  {  //Oracles.
            if( og.isValid( rec[i][j], og.next( ) ) )  {
              count[k][j]++;
            }
          }
        }
      }
    }
//    for( i = 0; i < rec.length; i++ )  {  //Records.
//      for( j = 0; j < rec[i].length; j++ )  {  //Fields.
//        og.reset( );
//        for( k = 0; k < oc; k++ )  {  //Oracles.
//          if( og.isValid( rec[i][j], og.next( ) ) )  {
//            count[k][j]++;
//          }
//          else if( ( rec[i][j].length == 0 ) ||
//                   isBlank( rec[i][j], 0, rec[i][j].length ) )  {
//
//            bc[k][j]++;
//          }
//        }
//      }
//    }

    //Stores the indices of the fields that will become a PF.
    al = new ArrayList<Integer>( );

    og.reset( );
    for( i = 0; i < oc; i++ )  {  //Oracles
      al.clear( );

      k = og.next( );
      mpt = og.getMinPercentage( k );

      for( j = 0; j < count[i].length; j++ )  {  //Fields
        if( ( rec.length - bc[j] ) > 0 )  {  //Any non-blank fields?
          //Test to see if the current oracle's count passes the minimum
          //  threshold associated with the oracle.
          if( ( double )( ( double )count[i][j] /
                ( ( double )rec.length - ( double )bc[j] ) ) > mpt )  {

            al.add( j );  //Store the index.
          }
        }
      }

      if( al.size( ) > 0 )  {  //Any indexed fields.
        //Create a PF for each indexed field with the appropriate information.
        fd[i] = new FieldDescriptor[al.size( )];
        for( j = 0; j < al.size( ); j++ )  {
          fd[i][j] = new FieldDescriptor( );

          fd[i][j].setUncertain( ( double )( ( double )bc[al.get( j )] /
                                             ( double )rec.length ) >=
                                 og.getMaxBlankPercentage( k ) );

          fd[i][j].setType( k );
          fd[i][j].setGrouping( og.getGrouping( k ) );
          fd[i][j].setName( og.name( k ) );
          fd[i][j].setPosition( al.get( j ) );
          fd[i][j].setRecordCount( rec.length );
          fd[i][j].setNumberValid( count[i][al.get( j )] );
          fd[i][j].setBlankCount( bc[al.get( j )] );
        }
      }

      fd[i] = og.preProcess( rec, i, fd[i] );
    }

    return( fd );
  }  /*    public static void find( char[][][], Delimiter, OracleGroup )  */


  /**
   *   This method pertains to fixed file types.  ( Notice the first parameter
   * is a two dimensional character array. )
   *
   * @return Though this method does not return anything it has the side effect
   *         of updating the RecordEnumeration parameter, RE.
   */
  public static void postProcessing( char[][] records, FieldDescriptor[][] fd,
                                     OracleGroup og, RecordEnumeration re )  {

    int i,
        oc = og.postProcessOracleCount( );

    //Ensure the ordering is that specified by the OracleGroup.
    og.postProcessReset( );

    for( i = 0; i < oc; i++ )  {
      //Perform post processing on the next oracle that implements the
      //  PostProcess.java interface.
      og.postProcess( records, og.nextToPostProcess( ), fd, re );
    }
  }  /*  public static void postProcessing( char[][], FieldDescriptor[][],
                                            OracleGroup, RecordEnumeration )  */


  /**
   *   This method pertains to delimited file types.  ( Notice the first
   * parameter is a three dimensional character array. )
   *
   * @return Though this method does not return anything it has the side effect
   *         of updating the RecordEnumeration parameter, RE.
   */
  public static void postProcessing( char[][][] records, FieldDescriptor[][] fd,
                                     OracleGroup og, RecordEnumeration re )  {

    int i,
        oc = og.postProcessOracleCount( );

    //Ensure the ordering is that specified by the OracleGroup.
    og.postProcessReset( );

    for( i = 0; i < oc; i++ )  {
      //Perform post processing on the next oracle that implements the
      //  PostProcess.java interface.
      og.postProcess( records, og.nextToPostProcess( ), fd, re );
    }
  }  /*  public static void postProcessing( char[][][], FieldDescriptor[][],
                                            OracleGroup, RecordEnumeration )  */


  /**
   *   This method is currently only appropriate for fully delimited files.
   *
   *   The current logic is to take the recognized fields determined to be part
   * of a record and to test the corresponding fields in the first record to see
   * if any are valid as determined by the appropriate oracles.  If the vast
   * majority of fields are not valid then assume the first record is comprised
   * of header information. 
   */
  public static boolean determineHeader( char[][][] header, OracleGroup og,
                                         RecordEnumeration re )  {

    boolean ret;
    int i,
        vc = 0;  //Valid count.
    FieldDescriptor fdh;
    FieldDescriptor[] fd = re.getEntries( );
    String hl;

    //Count how many of the fields in the first record of the file correspond to
    //  the content type as determined by the program.
    for( i = 0; i < fd.length; i++ )  {
      if( og.isValid( header[0][fd[i].getPosition( )], fd[i].getType( ) ) )  {
        vc++;
      }
    }

    //If a large percentage failed then it is probable that the first record is
    //  a header record.
    if( ret = ( ( ( double )( ( double )fd.length - ( double )vc ) /
                ( double )fd.length ) > 0.90 ) )  {

      //If there was a header record, map header labels to the corresponding
      //  fields in the RecordEnumeration.
      vc = re.getRecordLength( );  //Reuse VC.
      for( i = 0; i < vc; i++ )  {  //Examine each field position.
        fdh = re.type( i );  //Acquire the type of the field at position, I.
//Debug statement.
//System.out.print( "Position " + i + " is of type
//End debug statement.
        hl = ( new String( header[0][i] ) );  //Grab the corresponding header.

        //Match the header label to a field in the RecordEnumeration object.
        if( fdh.getType( ) >= 0 )  {
          re.setHeader( i, og.matchHeader( fdh.getType( ), hl ), hl );
        }
        else  {
          re.setHeader( i, 1.0, hl );
        }
      }
    }

    return( ret );
  }  /*  public static boolean determineHeader( char[][][], OracleGroup,
                                                RecordEnumeration )  */

}  /*  public class FindFields  */
