package supportObjects;

public class Position  {
  private int id,  //No intended purpose except to identify this object.
              p,  //The ( start ) position.
              len;  //The length.


  public Position( )  {
    id = p = len = -1;
  }  /*  Constructor  */


  public Position( int position, int length )  {
    id = -1;
    p = position;
    len = length;
  }  /*  Constructor  */


  public Position( int id, int position, int length )  {
    this.id = id;
    p = position;
    len = length;
  }  /*  Constructor  */


  private String space( int level )  {
    int i;
    StringBuffer ret = new StringBuffer( );

    level *= 2;  //Two fill characters for each level.

    for( i = 0; i < level; i++ )  {
      ret.append( ' ' );
    }

    return( ret.toString( ) );
  }  /*  private String space( int )  */


  /**
   *   Returns the maximum range of the two overlapping Positions.
   */
  public static Position maxRange( Position x, Position y )  {
    Position ret = new Position( );

    if( x.getPosition( ) <= y.getPosition( ) )  {
      ret.setPosition( x.getPosition( ) );
    }
    else  {
      ret.setPosition( y.getPosition( ) );
    }

    if( ( x.getPosition( ) + x.getLength( ) ) >=
        ( y.getPosition( ) + y.getLength( ) ) )  {

      ret.setLength( ( x.getPosition( ) + x.getLength( ) ) -
                     ret.getPosition( ) );
    }
    else  {
      ret.setLength( ( y.getPosition( ) + y.getLength( ) ) -
                     ret.getPosition( ) );
    }

    ret.setId( x.getId( ) );

    return( ret );
  }  /*  public static Position maxRange( Position, Position )  */


  /**
   *   Returns the percentage of overlap between Position ONE and TWO.
   */
  public static double percentOverlap( Position one, Position two )  {
    int to = 0;  //The amount of true overlap.
    Position n,  //The Position with the minimum starting position.
             m;  //The Position with the maximum starting position.

    if( one.getPosition( ) <= two.getPosition( ) )  {
      n = one;
      m = two;
    }
    else  {
      n = two;
      m = one;
    }

    to = ( ( n.getPosition( ) + n.getLength( ) ) - m.getPosition( ) );

    return( ( double )( ( double )to / ( double )m.getLength( ) ) );
  }  /*  public static double percentOverlaps( Position, Position )  */


  /**
   * @return TRUE if POSITION is contained within P, FALSE otherwise.
   */
  public static boolean contains( int position, Position p )  {
    boolean ret = false;

    if( p.getLength( ) >= 0 )  {  //A fixed length file.
      ret = ( ( position >= p.getPosition( ) ) &&
              ( position < ( p.getPosition( ) + p.getLength( ) ) ) );

    }
    else  {
      ret = ( position == p.getPosition( ) );
    }

    return( ret );
  }


  public void setId( int id )  {
    this.id = id;
  }  /*  public void setId( int )  */


  public void setPosition( int position )  {
    p = position;
  }  /*  public void setPosition( int )  */


  public void setLength( int length )  {
    len = length;
  }  /*  public void setLength( int )  */


  public void setPosition( int position, int length )  {
    p = position;
    len = length;
  }  /*  public void setPosition( int, int )  */


  public int getId( )  {
    return( id );
  }  /*  public int getId( )  */


  public int getPosition( )  {
    return( p );
  }  /*  public int getPosition( )  */


  public int getLength( )  {
    return( len );
  }  /*  public int getLength( )  */


  public boolean valid( )  {
    return( p >= 0 );
  }  /*  public boolean valid( ) */


  /**
   *   Returns TRUE if argument POSITION overlaps the calling instance at all,
   * otherwise the method returns FALSE.
   */
  public boolean overlaps( Position position )  {
    boolean ret;
    //P = parameter, T = this.  EP = end position, SP = start position.
    int pep = ( position.getPosition( ) + position.getLength( ) - 1 ),
        tep = ( p + len - 1 ),
        psp = position.getPosition( ),
        tsp = p;


    if( ( len < 0 ) && ( position.getLength( ) < 0 ) )  {  //Delimited.
      ret = ( p == position.getPosition( ) );
    }
    else  {  //Fixed, fully or hybrid.
      //This condition tests to see if either the start or end position of
      //  the parameter falls within the range covered by the start and end
      //  position of the invoking instance.
      ret = ( ( ( psp >= tsp ) && ( psp <= tep ) ) ||
              ( ( pep >= tsp ) && ( pep <= tep ) ) );

      if( !ret )  {  //It did not fall within the range.
        //This condition tests the opposite of the preceeding condition.  It
        //  tests to see if either the start or end position of the invoking
        //  instance falls within the range covered by the start and end
        //  position of the parameter.
        ret = ( ( ( tsp >= psp ) && ( tsp <= pep ) ) ||
                ( ( tep >= psp ) && ( tep <= pep ) ) );

      }
    }

    return( ret );
  }  /*  public boolean overlaps( Position )  */


  public boolean equals( Position p )  {
    return( ( this.p == p.getPosition( ) ) && ( len == p.getLength( ) ) &&
            ( id == p.getId( ) ) );

  }  /*  public boolean equals( Position )  */


  public String toXML( int level )  {
    String ret = ( space( level ) + "<position>" +
                   System.getProperty( "line.separator" ) + space( ++level ) +
                   "<start>" + ( p + 1 ) + "</start>" +
                   System.getProperty( "line.separator" ) + space( level ) +
                   "<end>" );

    if( len > 0 )  {
      ret = ret.concat( Integer.toString( p + len ) );
    }
    ret = ret.concat( "</end>" + System.getProperty( "line.separator" ) +
                      space( level ) + "<length>" );

    if( len > 0 )  {
      ret = ret.concat( Integer.toString( len ) );
    }

    return( ret.concat( "</length>" + System.getProperty( "line.separator" ) +
                        space( level - 1 ) + "</position>" ) );

  }  /*  public String toXML( int )  */


  public String toXML( )  {
    return( toXML( 0 ) );
  }  /*  public String toXML( )  */

}  /*  public class Positions  */
