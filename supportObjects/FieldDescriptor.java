/**
 *   Also referred to as a potential field position (PFP) before the decision( )
 * method is invoked in supportMethods.FindFields.
 */

package supportObjects;


public class FieldDescriptor  {
  private boolean u;  //Uncertain field.
  private int j,  //The justification: <0=left, 0=center, >0=right.
              t,  //The type as indicated by an OracleGroup.
              bc,  //The count of blank fields contained within the sample.
              nv,  //The number of valid entries from the file sample
                   //  corresponding to this descriptor.
              rc,  //Record count.
              gid,  //Grouping ID of the corresponding oracle type.
              snv;  //Secondary number of valid entries.
  private LabelMatch h;  //The header label information.
  private Position pos;
  private String n;  //The name of the oracle as indicated by an OracleGroup.


  public FieldDescriptor( )  {
    u = false;
    j = -1;  //Assume left.
    t = -1;
    gid = -1;
    bc = 0;
    nv = 0;
    snv = -1;
    h = new LabelMatch( 0.0, "" );
    pos = null;
    n = "";
  }  /*  Constructor  */


  private String space( int level )  {
    int i;
    StringBuffer ret = new StringBuffer( );

    level *= 2;

    for( i = 0; i < level; i++ )  {
      ret.append( ' ' );
    }

    return( ret.toString( ) );
  }  /*  private String space( int )  */


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


  /**
   *   Certain fields are uncertain.  These include, but are not limited to,
   * sparsely populated fields, resized fields, and fields that may not conform
   * to normally assumed statistical distributions.  While included in the
   * output report these fields will be marked.
   */
  public void setUncertain( boolean uncertain )  {
    u = uncertain;
  }  /*  public void setUncertain( boolean )  */


  /**
   *   The justification of the field.  Less than zero for left justified, equal
   * to zero for center justified, and greater than zero for right justified.
   */
  public void setJustification( int justification )  {
    j = justification;
  }  /*  public void setJustification( int )  */


  /**
   *   The field type as indicated by an OracleGroup.
   */
  public void setType( int type )  {
    t = type;
  }  /*  public void setType( int )  */


  public void setGrouping( int group )  {
    gid = group;
  }  /*  public void setGrouping( int )  */


  /**
   *   The blank count is the number of records within the sample that contained
   * no information, all whitespace, within the field's range as specified by
   * the corresponding Position information.
   */
  public void setBlankCount( int blankCount )  {
    bc = blankCount;
  }  /*  public void setblankCount( int )  */


  /**
   *   The number of valid entries from the file sample corresponding to the
   * descriptor for this field type.
   */
  public void setNumberValid( int numberValid )  {
    nv = numberValid;
  }  /*  public void setNumberValid( int )  */


  public void setRecordCount( int recordCount )  {
    rc = recordCount;
  }  /*  public void setRecordCount( int )  */


  public void setSecondaryValid( int secondaryValid )  {
    snv = secondaryValid;
  }  /*  public void setSecondaryValid( int )  */


  public void setHeader( LabelMatch header )  {
    h = header;
  }  /*  public void setHeader( LabelMatch )  */


  public void setHeader( double probability, String label )  {
    h = new LabelMatch( probability, label );
  }  /*  public void setHeader( double, String )  */


  /**
   *   Intentionally ambiguous, POSITION can hold the start position of a field
   * for fixed length files or the field's position relative to other fields for
   * a delimited file.
   */
  public void setPosition( int position )  {
    if( pos == null )  {
      pos = new Position( );
    }
    pos.setPosition( position );
  }  /*  public void setPosition( int )  */


  public void setName( String name )  {
    n = name;
  }  /*  public void setName( String )  */


  public void setPosition( Position position )  {
    pos = position;
  }  /*  public void setPosition( Position )  */


  /**
   *   Should be less than zero for delimited files and greater than zero for
   * fixed files.
   */
  public void setLength( int length )  {
    if( pos == null )  {
      pos = new Position( );
    }
    pos.setLength( length );
  }  /*  public void setLength( int )  */


  public boolean getUncertain( )  {
    return( u );
  }  /*  public boolean getUncertain( )  */


  /**
   *   The justification of the field.  Less than zero for left justified, equal
   * to zero for center justified, and greater than zero for right justified.
   */
  public int getJustification( )  {
    return( j );
  }  /*  public int getJustification( )  */


  /**
   *   The field type as indicated by an OracleGroup.
   */
  public int getType( )  {
    return( t );
  }  /*  public int getType( )  */


  public int getGrouping( )  {
    return( gid );
  }  /*  public int getGrouping( )  */


  /**
   *   The blank count is the number of records within the sample that contained
   * no information, all whitespace, within the field's range as specified by
   * the corresponding Position information.
   */
  public int getBlankCount( )  {
    return( bc );
  }  /*  public int getBlankCount( )  */


  /**
   *   The number of valid entries from the file sample corresponding to the
   * descriptor for this field type.
   */
  public int getNumberValid( )  {
    return( nv );
  }  /*  public int getNumberValid( )  */


  public int getRecordCount( )  {
    return( rc );
  }  /*  public int getRecordCount( )  */


  public int getSecondaryValid( )  {
    return( snv );
  }  /*  public int getSecondaryValid( )  */


  public LabelMatch getHeader( )  {
    return( h );
  }  /*  public LabelMatch getHeader( )  */


  /**
   *   This method is different from getNumberValid() and getSecondaryValid().
   *
   * @return The secondary number of valid entries, which will correspond to
   *         the results of reducing a database in an oracle, if the field is
   *         set; otherwise the primary number of valid entries.
   */
  public int getValidCount( )  {
    int ret;

    if( snv > 0 )  {
      ret = snv;
    }
    else  {
      ret = nv;
    }

    return( ret );
  }  /*  public int getValidCount( )  */


  public double maxDifference( FieldDescriptor fd )  {
    int obc = fd.getBlankCount( ),  //Other blank count.
        onv = fd.getNumberValid( ),  //Other number valid.
        osnv = fd.getSecondaryValid( );
    double d,
           ret;

    if( ( snv >= 0 ) && ( osnv >= 0 ) )  {
      ret = ( ( double )( ( double )nv / ( ( double )rc - ( double )bc ) ) ) -
            ( ( double )( ( double )onv / ( ( double )rc - ( double )obc ) ) );

      d = ( ( double )( ( double )snv / ( ( double )rc - ( double )bc ) ) ) -
          ( ( double )( ( double )osnv / ( ( double )rc - ( double )obc ) ) );

      if( Math.abs( d ) > Math.abs( ret ) )  {
        ret = d;
      }
    }
    else if( snv >= 0 )  {
      ret = ( ( double )( ( double )snv / ( ( double )rc - ( double )bc ) ) ) -
            ( ( double )( ( double )onv / ( ( double )rc - ( double )obc ) ) );

    }
    else if( osnv >= 0 )  {
      ret = ( ( double )( ( double )nv / ( ( double )rc - ( double )bc ) ) ) -
            ( ( double )( ( double )osnv / ( ( double )rc - ( double )obc ) ) );

    }
    else  {
      ret = ( ( double )( ( double )nv / ( ( double )rc - ( double )bc ) ) ) -
            ( ( double )( ( double )onv / ( ( double )rc - ( double )obc ) ) );

    }

    return( ret );
  }  /*  public int maxDifference( FieldDescriptor )  */


  /**
   *   In order to better make decisions the content types are collected into
   * groupings.  The purpose of these groupings is to list all content types
   * that when compared to each other should use the primary count instead of
   * also considering, when available, the secondary count.  ( e.g., When
   * comparing an AddressLineOne and an AddressLine PFP it is not helpful to
   * use the secondary count as they are of a common type / grouping. )  This is
   * a means to explicitly indicate which content types have overlapping domains
   * and which do not.
   *
   * @param group - the ID associated with the group corresponding to the
   *                oracle, content type, that this FieldDescriptor is being
   *                compared against.  -1 if no comparison is being made.
   * @return The percentage of valid entries as indicated by the oracle of the
   *         corresponding type for the associated range of characters.
   */
  public double getPercentValid( int group )  {
    double ret = 0.0;

    if( ( rc - bc ) > 0 )  {
      //When comparing content types of the same group only consider the primary
      //  count, the count obtained before reducing any applicable knowledge
      //  bases.
      if( ( group >= 0 ) && ( ( group & gid ) > 0 ) )  {
        ret = ( double )( ( double )nv / ( ( double )rc - ( double )bc ) );
      }
      else  {  //Otherwise return the secondary count if appropriate.
        if( snv >= 0 )  {
          ret = ( double )( ( double )snv / ( ( double )rc - ( double )bc ) );
        }
        else  {
          ret = ( double )( ( double )nv / ( ( double )rc - ( double )bc ) );
        }
      }
    }

    return( ret );
  }  /*  public double getPercentValid( )  */


  /**
   *   Intentionally ambiguous, POSITION can hold the start position of a field
   * for fixed length files or the field's position relative to other fields for
   * a delimited file.
   */
  public int getPosition( )  {
    int ret = -1;

    if( pos != null )  {
      ret = pos.getPosition( );
    }

    return( ret );
  }  /*  public int getPosition( )  */


  public Position getFieldPosition( )  {
    return( pos );
  }  /*  public Position getFieldPosition( )  */


  public String getName( )  {
    return( n );
  }  /*  public String getName( )  */


  /**
   *   Should be less than zero for delimited files and greater than zero for
   * fixed files.
   */
  public int getLength( )  {
    int ret = -1;

    if( pos != null )  {
      ret = pos.getLength( );
    }

    return( ret );
  }  /*  public int getLength( )  */


  /**
   *   Currently does not test justification.
   */
  public boolean equals( FieldDescriptor fd )  {
//( j == fd.getJustification( ) ) && 
    return( ( t == fd.getType( ) ) &&
            ( nv == fd.getNumberValid( ) ) &&
            pos.equals( fd.getFieldPosition( ) ) && n.equals( fd.getName( ) ) );

  }  /*  public boolean equals( FieldDescriptor )  */


  /**
   *   The <fillcharacter> tag needs to be updated.
   */
  public String toXML( int level )  {
    StringBuffer ret = new StringBuffer( );

    ret.append( space( level ) + "<field>" +
                System.getProperty( "line.separator" ) +
                pos.toXML( ++level ) + System.getProperty( "line.separator" ) +
                h.toXML( level ) + space( level ) + "<contenttype>" + n +
                "</contenttype>" + System.getProperty( "line.separator" ) +
                space( level ) + "<justification>" );

    if( j < 0 )  {
      ret.append( "LEFT" );
    }
    else if( j > 0 )  {
      ret.append( "RIGHT");
    }
    else  {
      ret.append( "CENTER");
    }

    ret.append( "</justification>" + System.getProperty( "line.separator" ) +
                space( level ) + "<fillcharacter></fillcharacter>" +
                System.getProperty( "line.separator" ) + space( level ) +
                "<validcount>" + System.getProperty( "line.separator" ) +
                space( level + 1 ) + "<primary>" + Integer.toString( nv ) +
                "</primary>" + System.getProperty( "line.separator" ) +
                space( level + 1 ) + "<secondary>" );

    if( snv >= 0 )  {
      ret.append( snv );
    }

    ret.append( "</secondary>" + System.getProperty( "line.separator" ) +
                space( level + 1 ) + "<blankcount>" );

    if( bc >= 0 )  {
      ret.append( bc );
    }

    ret.append( "</blankcount>" + System.getProperty( "line.separator" ) +
                space( level ) + "</validcount>" +
                System.getProperty( "line.separator" ) + space( level ) +
                "<certain>" +
                Boolean.toString( u ).toUpperCase( ) + "</certain>" +
                System.getProperty( "line.separator" ) + space( level - 1 ) +
                "</field>" );

    return( ret.toString( ) );
  }  /*  public String toXML( int )  */


  public String toXML( )  {
    return( toXML( 0 ) );
  }  /*  public String toXML( )  */


  /**
   *   Does not print header information or fill character.
   */
  public String toString( )  {
    StringBuffer ret = new StringBuffer( );

    if( u )  {  //An uncertain field.
      ret.append( "~ " );
    }
    else  {  //A certain field.
      ret.append( "  " );
    }

    ret.append( pad( true, ' ', 23, n ) );  //Content type name.

    if( pos.getLength( ) > 0 )  {  //A fixed file.
      if( j < 0 )  {  //Justification.
        ret.append( pad( false, ' ', 2, "L" ) );
      }
      else if( j > 0 )  {
        ret.append( pad( false, ' ', 2, "R" ) );
      }
      else  {
        ret.append( pad( false, ' ', 2, "C" ) );
      }

      //Start position.
      ret.append( pad( false, ' ', 5,
                       Integer.toString( pos.getPosition( ) + 1 ) ) );

      //End position.
      ret.append( pad( false, ' ', 5,
                  Integer.toString( pos.getPosition( ) + pos.getLength( ) ) ) );

      //Length
      ret.append( pad( false, ' ', 4, Integer.toString( pos.getLength( ) ) ) );
    }
    else  {
      //Start position.
      ret.append( pad( false, ' ', 5,
                       Integer.toString( pos.getPosition( ) + 1 ) ) );

    }

    ret.append( " -- " );  //Counts.
    ret.append( pad( false, ' ', 5, Integer.toString( nv ) ) );
    ret.append( pad( false, ' ', 5, Integer.toString( snv ) ) );
    ret.append( pad( false, ' ', 5, Integer.toString( bc ) ) );

    return( ret.toString( ) );
  }  /*  public String toString( )  */

}  /*  public class FieldDescriptor  */
