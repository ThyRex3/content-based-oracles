package supportObjects;

import java.util.Iterator;
import java.util.ArrayList;


public class RecordEnumeration  {
  private int rl;  //The record length.
  private LabelMatch[] hl;  //Header label information.
  private ArrayList<FieldDescriptor> alfd;  //The fields in the record.


  public RecordEnumeration( int recordLength )  {
    rl = recordLength;
    hl = null;
    alfd = new ArrayList<FieldDescriptor>( );
  }  /*  Constructor  */


  /**
   * @param lj -- TRUE = left justified; FALSE = right justified.
   * @param fc -- Fill character.
   * @param tl -- Total length.
   * @param s -- The String to pad.
   */
  private StringBuffer pad( boolean lj, char fc, int tl, String s )  {
    int c,
        i;
    StringBuffer ret = new StringBuffer( );

    if( !lj )  {  //Left justified, append fill characters to the beginning.
      c = ( tl - s.length( ) );
      for( i = 0; i < c; i++ )  {
        ret.append( fc );
      }
    }

    if( tl < s.length( ) )  {
      ret.append( s.substring( 0, tl ) );
    }
    else  {
      ret.append( s );
    }

    if( lj )  {  //Left justified, append fill characters to the end.
      c = ( tl - s.length( ) );
      for( i = 0; i < c; i++ )  {
        ret.append( fc );
      }
    }

    return( ret );
  }  /*  private StringBuffer pad( boolean, char, int, String )  */


  public int getRecordLength( )  {
    return( rl );
  }  /*  public int getRecordLength( )  */


  public void setHeader( int index, double probability, String label )  {
    if( hl == null )  {
      hl = new LabelMatch[rl];
    }

    if( ( index >= 0 ) && ( index < hl.length ) )  {
      hl[index] = new LabelMatch( probability, label );
    }
  }  /*  public void setHeader( int, double, String )  */


  public void setEntry( FieldDescriptor newEntry )  {
    boolean b = true;
    int i = 0,
        sp,
        pos;

    //First remove all existing entries that overlap the incoming entry.
    while( i < alfd.size( ) )  {
      if( newEntry.getFieldPosition( ).overlaps(
            alfd.get( i ).getFieldPosition( ) ) )  {

        alfd.remove( i );
      }
      else  {
        i++;  //Only increment I if no entries overlapped the incoming entry.
      }
    }

    //Find the position within the existing entries to insert the new entry.
    i = 0;
    sp = newEntry.getPosition( );
    while( b && ( i < alfd.size( ) ) )  {

      pos = alfd.get( i ).getPosition( );
      if( alfd.get( i ).getLength( ) > 0 )  {
        pos += alfd.get( i ).getLength( );
      }

      if( sp >= pos )  {
        i++;
      }
      else  {
        b = false;
      }
    }

    alfd.add( i, newEntry );  //Insert the new entry into the record.
  }  /*  public void setEntry( FieldDescriptor )  */


  /**
   *   Use this method with caution!  This method assumes that the parameter
   * FIELDS contains all current FieldDescriptors to be placed in this
   * RecordEnumeration and that they are in the correct order.  This method will
   * erase any existing entries.
   */
  public void setEntries( ArrayList<FieldDescriptor> fields )  {
    alfd = fields;
  }  /*  public void setEntries( ArrayList<FieldDescriptor> )  */


  public void removeEntry( FieldDescriptor fd )  {
    boolean s = true;  //Searching.
    int i = 0;

    while( s && ( i < alfd.size( ) ) )  {
      if( alfd.get( i ).getPosition( ) == fd.getPosition( ) )  {
        if( fd.equals( alfd.get( i ) ) )  {
          alfd.remove( i );
          s = false;
        }
      }

      i++;
    }
  }  /*  public void removeEntry( FieldDescriptor )  */


  public FieldDescriptor[] getEntries( )  {
    int i = 0;
    FieldDescriptor[] ret = new FieldDescriptor[alfd.size( )];
    Iterator<FieldDescriptor> itr;

    for( itr = alfd.iterator( ); itr.hasNext( ); )  {
      ret[i++] = itr.next( );
    }

    return( ret );
  }  /*  public FieldDescriptor[] getEntries( )  */


  public ArrayList<FieldDescriptor> conflict( FieldDescriptor fd )  {
    FieldDescriptor fdh;  //FieldDescriptor holder.
    Iterator<FieldDescriptor> itr;
    ArrayList<FieldDescriptor> ret = new ArrayList<FieldDescriptor>( );

    for( itr = alfd.iterator( ); itr.hasNext( ); )  {
      fdh = itr.next( );

      if( fd.getFieldPosition( ).overlaps( fdh.getFieldPosition( ) ) )  {
        ret.add( fdh );
      }
    }

    return( ret );
  }  /*  public ArrayList<FieldDescriptor> conflict( FieldDescriptor ) */


  /**
   *   This method finds all fields contained within this RecordEnumeration that
   * are covered by the parameter FD.  Covered means that any returned fields
   * will have a range ( start position and length ) that is contained entirely
   * within the range of FD.
   *
   *   This method is different than conflict() because those fields returned by
   * conflict() may not be completely covered by the parameter FD.
   */
  public ArrayList<FieldDescriptor> fieldCovers( FieldDescriptor fd )  {
    int hsp,
        hlen,
        fdsp = fd.getPosition( ),
        fdlen = fd.getLength( );
    FieldDescriptor fdh;  //FieldDescriptor holder.
    Iterator<FieldDescriptor> itr;
    ArrayList<FieldDescriptor> ret = new ArrayList<FieldDescriptor>( );

    for( itr = alfd.iterator( ); itr.hasNext( ); )  {
      fdh = itr.next( );
      hsp = fdh.getPosition( );
      hlen = fdh.getLength( );

      if( ( hsp >= fdsp ) && ( ( hsp + hlen ) <= ( fdsp + fdlen ) ) )  {
        ret.add( fdh );
      }
    }

    return( ret );
  }  /*  public ArrayList<FieldDescriptor> fieldCovers( FieldDescriptor )  */


  /**
   * @return TRUE if parameter FD covers each entry in parameter ALFD, else
   *         FALSE.
   */
  public boolean fieldCovers( FieldDescriptor fd,
                              ArrayList<FieldDescriptor> alfd )  {

    boolean ret = true;
    FieldDescriptor fdh;  //FieldDescriptor holder.
    Iterator<FieldDescriptor> itr = alfd.iterator( );

//Debug statement.
//System.out.println( "FD = " + fd.getName( ) + " -- " + fd.getPosition( ) + ", " + fd.getLength( ) + "." );
//End debug statement.
    while( ret && itr.hasNext( ) )  {
      fdh = itr.next( );

      ret = ( ( fdh.getPosition( ) >= fd.getPosition( ) ) &&
              ( ( fdh.getPosition( ) + fdh.getLength( ) ) <=
                ( fd.getPosition( ) + fd.getLength( ) ) ) );

//Debug statement.
//System.out.println( "FDH = " + fd.getName( ) + " -- " + fd.getPosition( ) + ", " + fd.getLength( ) + ".  RET = " + ret + "." );
//End debug statement.
    }

    return( ret );
  }  /*  public boolean fieldCovers( FieldDescriptor,
                                     ArrayList<FieldDescriptor> )  */


  /**
   * @return TRUE if the parameter FD is found in this instance of a
   *         RecordEnumeration, else the method returns FALSE.
   */
  public boolean contains( FieldDescriptor fd )  {
    boolean ret = false;
    int i = 0;

    while( !ret && ( i < alfd.size( ) ) )  {
      if( !alfd.get( i ).equals( fd ) )  {
        i++;
      }
      else  {
        ret = true;
      }
    }

    return( ret );
  }  /*  public boolean contains( FieldDescriptor )  */


  /**
   *   This code is designed for hybrid and fully fixed files.
   *
   * @return An array of Position objects that specifies all of the unspecified
   *         positions in the RecordEnumeration.
   */
  public Position[] unspecified( )  {
    int sp = 0;
    Position[] ret = null;
    Iterator<FieldDescriptor> itr;
    FieldDescriptor fdh;
    ArrayList<Position> alp = new ArrayList<Position>( );

    for( itr = alfd.iterator( ); itr.hasNext( ); )  {
      fdh = itr.next( );

      if( !Position.contains( sp, fdh.getFieldPosition( ) ) )  {
        alp.add( new Position( sp, ( fdh.getPosition( ) - sp ) ) );
      }

      sp = ( fdh.getPosition( ) + fdh.getLength( ) );
    }

    if( sp < rl )  {
      alp.add( new Position( sp, ( rl - sp ) ) );
    }

    if( alp.size( ) > 0 )  {
      ret = new Position[alp.size( )];
      for( sp = 0; sp < ret.length; sp++ )  {
        ret[sp] = alp.get( sp );
      }
    }

    return( ret );
  }  /*  public Position[] unspecified( )  */


  /**
   * @return An array of all FieldDescriptor objects corresponding to the
   *         contenty type indicated by the parameter TYPE contained within this
   *         RecordEnumeration.  NULL if there is no FieldDescriptor of the
   *         specified TYPE.
   */
  public FieldDescriptor[] ofType( int type )  {
    int c = 0,
        i;
    FieldDescriptor[] ret = null;

    for( i = 0; i < alfd.size( ); i++ )  {
      if( alfd.get( i ).getType( ) == type )  {
        c++;
      }
    }

    if( c > 0 )  {
      ret = new FieldDescriptor[c];

      c = 0;  //Reuse C.
      for( i = 0; i < alfd.size( ); i++ )  {
        if( alfd.get( i ).getType( ) == type )  {
          ret[c++] = alfd.get( i );
        }
      }
    }

    return( ret );
  }  /*  public FieldDescriptor[] ofType( int )  */


  /**
   * @return A PFL indicating the type and position of the field containing the
   *         parameter POSITION.  If the parameter POSITION is outside the range
   *         of the length of the record the method returns NULL.
   */
  public FieldDescriptor type( int position )  {
    boolean b = true;
    int sp = 0;
    Iterator<FieldDescriptor> itr = alfd.iterator( );
    FieldDescriptor fdh,
                    ret = null;

    for( itr = alfd.iterator( ); itr.hasNext( ); )  {
      if( itr.next( ).getLength( ) > 0 )  {
        sp++;
      }
    }

    itr = alfd.iterator( );

    //If a majority of the FieldDescriptors have valid length values then it is
    //  probably a fixed file.
    if( ( double )( ( double )sp / ( double )alfd.size( ) ) > 0.90 )  {
      sp = 0;
      while( b && itr.hasNext( ) )  {
        fdh = itr.next( );

        //First check if POSITION is contained within the range between SP and the
        //  beginning of the next PFL.
        if( ( position >= sp ) && ( position < fdh.getPosition( ) ) )  {
          //Create a pseudo-FieldDescriptor for the UNSPECIFIED region.
          ret = new FieldDescriptor( );
          ret.setType( -1 );
          ret.setPosition( sp );
          ret.setLength( fdh.getPosition( ) - sp );
          ret.setName( "unspecified" );

          b = false;  //Set the flag to end the loop.
        }
        //Second check if the next PFL contains POSITION.
        else if( Position.contains( position, fdh.getFieldPosition( ) ) )  {
          ret = fdh;

          b = false;  //Set the flag to end the loop.
        }
        else  {
          sp = ( fdh.getPosition( ) + fdh.getLength( ) );
        }
      }

      //Lastly check if POSITION is contained within the range between the end of
      //  the final PFL and the end of the record.
      if( b && ( position >= sp ) && ( position < rl ) )  {
        //Create a pseudo-FieldDescriptor for the UNSPECIFIED region.
        ret = new FieldDescriptor( );
        ret.setType( -1 );
        ret.setPosition( sp );
        ret.setLength( rl - sp );
        ret.setName( "unspecified" );
      }
    }
    else  {  //Assume is a delimited file.
      while( b && itr.hasNext( ) )  {
        fdh = itr.next( );

        if( fdh.getPosition( ) > position )  {  //No entry at 
          b = false;  //Set the flag to end the loop.

          //Create a pseudo-FieldDescriptor for the UNSPECIFIED region.
          ret = new FieldDescriptor( );
          ret.setType( -1 );
          ret.setPosition( position );
          ret.setLength( -1 );
          ret.setName( "unspecified" );
        }
        else if( fdh.getPosition( ) == position )  {
          b = false;  //Set the flag to end the loop.
          ret = fdh;
        }
      }

      if( b && ( position < rl ) )  {  //Did not find any entries at position.
        //Create a pseudo-FieldDescriptor for the UNSPECIFIED region.
        ret = new FieldDescriptor( );
        ret.setType( -1 );
        ret.setPosition( position );
        ret.setLength( -1 );
        ret.setName( "unspecified" );
      }
    }

    return( ret );
  }  /*  public FieldDescriptor type( int )  */


  public String toXML( int level )  {
    boolean d = true;  //Assume the file is fully delimited.
    int //c = 1,
        sp = 0;
    FieldDescriptor fdh,
                    temp;
    StringBuffer ret = new StringBuffer( );
    Iterator<FieldDescriptor> itr = alfd.iterator( );

    //To test if this is a fully delimited file check each entry's length, if
    //  they are all less than zero then it is delimited.
    while( d && itr.hasNext( ) )  {
      fdh = ( FieldDescriptor )itr.next( );
      d &= ( fdh.getLength( ) < 0 );
    }

    if( !d )  {  //A fixed file - hybrid or fully fixed.
      for( itr = alfd.iterator( ); itr.hasNext( ); )  {
        fdh = itr.next( );

        if( !Position.contains( sp, fdh.getFieldPosition( ) ) )  {
          temp = new FieldDescriptor( );
          temp.setName( "* UNSPECIFIED *" );
          temp.setPosition( new Position( sp,//( sp + 1 ),
                                          ( fdh.getPosition( ) - sp ) ) );

          ret.append( temp.toXML( level ) );
          ret.append( System.getProperty( "line.separator" ) );
        }

        sp = ( fdh.getPosition( ) + fdh.getLength( ) );

        ret.append( fdh.toXML( level ) );
        ret.append( System.getProperty( "line.separator" ) );
      }

      if( sp < rl )  {
          temp = new FieldDescriptor( );
          temp.setName( "* UNSPECIFIED *" );
//          temp.setPosition( new Position( ( sp + 1 ), ( rl - sp ) ) );
          temp.setPosition( new Position( sp, ( rl - sp ) ) );

          ret.append( temp.toXML( level ) );
          ret.append( System.getProperty( "line.separator" ) );
      }
    }
    else  {
      for( itr = alfd.iterator( ); itr.hasNext( ); )  {
        fdh = itr.next( );

        while( sp < fdh.getPosition( ) )  {
          temp = new FieldDescriptor( );
          if( ( hl != null ) && ( sp <= hl.length ) )  {
            temp.setHeader( hl[sp] );
          }
          temp.setName( "* UNSPECIFIED *" );
          temp.setPosition( new Position( sp++, -1 ) );

          ret.append( temp.toXML( level ) );
          ret.append( System.getProperty( "line.separator" ) );
        }

        if( ( hl != null ) && ( sp <= hl.length ) )  {
          fdh.setHeader( hl[sp] );
        }
        ret.append( fdh.toXML( level ) );
        ret.append( System.getProperty( "line.separator" ) );

        sp++;
      }

      while( sp < rl )  {
        temp = new FieldDescriptor( );
        if( ( hl != null ) && ( sp <= hl.length ) )  {
          temp.setHeader( hl[sp] );
        }
        temp.setName( "* UNSPECIFIED *" );
        temp.setPosition( new Position( sp++, -1 ) );

        ret.append( temp.toXML( level ) );
        ret.append( System.getProperty( "line.separator" ) );
      }
    }

    return( ret.toString( ) );
  }  /*  public String toXML( int )  */


  public String toXML( )  {
    return( toXML( 0 ) );
  }  /*  public String toXML( )  */


  public String toString( )  {
    boolean d = true;  //Assume the file is fully delimited.
    int c = 1,
        sp = 0;
    FieldDescriptor fdh,
                    temp;
    StringBuffer ret = new StringBuffer( );
    Iterator<FieldDescriptor> itr = alfd.iterator( );

    //To test if this is a fully delimited file check each entry's length, if
    //  they are all less than zero then it is delimited.
    while( d && itr.hasNext( ) )  {
      fdh = ( FieldDescriptor )itr.next( );
      d &= ( fdh.getLength( ) < 0 );
    }

    //Header.
    ret.append( pad( true, ' ', 7, "" ) );
    ret.append( pad( true, ' ', 25, "CONTENT TYPE NAME" ) );
    if( !d )  {  //A fixed file - hybrid or fully fixed.
      ret.append( pad( false, ' ', 2, "J" ) );
      ret.append( pad( false, ' ', 5, "SP" ) );
      ret.append( pad( false, ' ', 5, "EP" ) );
      ret.append( pad( false, ' ', 4, "LEN" ) );
    }
    else  {
      ret.append( pad( false, ' ', 5, "SP" ) );
    }
    ret.append( pad( false, ' ', 9, "PC" ) );
    ret.append( pad( false, ' ', 5, "SC" ) );
    ret.append( pad( false, ' ', 5, "BC" ) );
    ret.append( System.getProperty( "line.separator" ) );

    if( !d )  {  //A fixed file - hybrid or fully fixed.
      for( itr = alfd.iterator( ); itr.hasNext( ); )  {
        fdh = itr.next( );

        if( !Position.contains( sp, fdh.getFieldPosition( ) ) )  {
          temp = new FieldDescriptor( );
          temp.setName( "* UNSPECIFIED *" );
          temp.setPosition( new Position( sp,//( sp + 1 ),
                                          ( fdh.getPosition( ) - sp ) ) );

          ret.append( pad( false, ' ', 4, Integer.toString( c++ ) ) );
          ret.append( ".) ".concat( temp.toString( ) ) );
          ret.append( System.getProperty( "line.separator" ) );
        }

        sp = ( fdh.getPosition( ) + fdh.getLength( ) );

        ret.append( pad( false, ' ', 4, Integer.toString( c++ ) ) );
        ret.append( ".) ".concat( fdh.toString( ) ) );
        ret.append( System.getProperty( "line.separator" ) );
      }

      if( sp < rl )  {
          temp = new FieldDescriptor( );
          temp.setName( "* UNSPECIFIED *" );
//          temp.setPosition( new Position( ( sp + 1 ), ( rl - sp ) ) );
          temp.setPosition( new Position( sp, ( rl - sp ) ) );

          ret.append( pad( false, ' ', 4, Integer.toString( c++ ) ) );
          ret.append( ".) ".concat( temp.toString( ) ) );
          ret.append( System.getProperty( "line.separator" ) );
      }
    }
    else  {
      for( itr = alfd.iterator( ); itr.hasNext( ); )  {
        fdh = itr.next( );

        while( sp < fdh.getPosition( ) )  {
          temp = new FieldDescriptor( );
          if( ( hl != null ) && ( sp <= hl.length ) )  {
            temp.setHeader( hl[sp] );
          }
          temp.setName( "* UNSPECIFIED *" );
          temp.setPosition( new Position( sp++, -1 ) );

          ret.append( pad( false, ' ', 4, Integer.toString( c++ ) ) );
          ret.append( ".) ".concat( temp.toString( ) ) );
          ret.append( System.getProperty( "line.separator" ) );
        }

        if( ( hl != null ) && ( sp <= hl.length ) )  {
          fdh.setHeader( hl[sp] );
        }

        ret.append( pad( false, ' ', 4, Integer.toString( c++ ) ) );
        ret.append( ".) ".concat( fdh.toString( ) ) );
        ret.append( System.getProperty( "line.separator" ) );

        sp++;
      }

      while( sp < rl )  {
        temp = new FieldDescriptor( );
        if( ( hl != null ) && ( sp <= hl.length ) )  {
          temp.setHeader( hl[sp] );
        }
        temp.setName( "* UNSPECIFIED *" );
        temp.setPosition( new Position( sp++, -1 ) );

        ret.append( pad( false, ' ', 4, Integer.toString( c++ ) ) );
        ret.append( ".) ".concat( temp.toString( ) ) );
        ret.append( System.getProperty( "line.separator" ) );
      }
    }

    return( ret.toString( ) );
  }  /*  public String toString( )  */

}  /*  public class RecordEnumeration  */
