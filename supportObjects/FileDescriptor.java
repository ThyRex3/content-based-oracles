package supportObjects;

import oracles.OracleGroup;


public class FileDescriptor  {
  private boolean ia,  //Is the original file ASCII?
                  hdr;  //Is there a header, only applies to fully delimited.
  private int rl,  //The record length, in bytes or fields.
              rc;  //The count of records contained in the file sample.
  private long rt;  //Run time for the program.
  private Delimiter d;
  private OracleGroup og;  //A reference to the supporting oracle group.
  private RecordEnumeration re;
  private FieldDescriptor[][] fd;  //An array of field description arrays.
  private String fn;  //The input file name.


  public FileDescriptor( )  {
    ia = true;
    hdr = false;
    rl = -1;
    rc = -1;
    rt = 0;
    d = null;
    og = null;
    re = null;
    fd = null;
    fn = "";
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


  public void setIsAscii( boolean isAscii )  {
    ia = isAscii;
  }  /*  public void setAscii( boolean )  */


  public void setHeader( boolean header )  {
    hdr = header;
  }  /*  public void setHeader( boolean )  */


  public void setRecordLength( int recordLength )  {
    rl = recordLength;
  }  /*  public void setRecordLength( int )  */


  public void setRecordCount( int recordCount )  {
    rc = recordCount;
  }  /*  public void setRecordCount( int )  */


  public void setRunTime( long runTime )  {
    rt = runTime;
  }  /*  public void setRunTime( long )  */


  public void setDelimiter( Delimiter delimiter )  {
    d = delimiter;
  }  /*  public void setDelimiter( Delimiter )  */


  public void setOracleGroup( OracleGroup oracleGroup )  {
    og = oracleGroup;
  }  /*  public void setDelimiter( OracleGroup )  */


  public void setRecordEnumeration( RecordEnumeration recordEnumeration )  {
    re = recordEnumeration;
  }  /*  public void setRecordEnumeration( RecordEnumeration )  */


  public void setFieldDescriptors( FieldDescriptor[][] fieldDescriptors )  {
    fd = fieldDescriptors;
  }  /*  public void setFieldDescriptors( FieldDescriptor[][] )  */


  public void addFieldDescriptor( FieldDescriptor[] fieldDescriptor )  {
    int i;
    FieldDescriptor[][] temp;

    if( fd != null )  {
      temp = new FieldDescriptor[( fd.length + 1 )][];

      for( i = 0; i < fd.length; i++ )  {
        System.arraycopy( fd[i], 0, temp[i], 0, fd[i].length );
      }

      temp[( temp.length - 1 )] = fieldDescriptor;
    }
    else  {
      temp = new FieldDescriptor[1][];
      temp[0] = fieldDescriptor;
    }

    fd = temp;
  }  /*  public void addFieldDescriptor( FieldDescriptor )  */


  public void setFileName( String fileName )  {
    fn = fileName;
  }  /*  public void setFileName( String )  */


  public boolean getIsAscii( )  {
    return( ia );
  }  /*  public boolean getAscii( )  */


  public boolean getHeader( )  {
    return( hdr );
  }  /*  public void setHeader( )  */


  public int getRecordLength( )  {
    return( rl );
  }  /*  public int getRecordLength( )  */


  public int getRecordCount( )  {
    return( rc );
  }  /*  public int getRecordCount( )  */


  public long getRunTime( )  {
    return( rt );
  }  /*  public long getRunTime( )  */


  public Delimiter getDelimiter( )  {
    return( d );
  }  /*  public Delimiter getDelimiter( )  */


  public RecordEnumeration getRecordEnumeration( )  {
    return( re );
  }  /*  public RecordEnumeration getRecordEnumeration( )  */


  public FieldDescriptor[][] getFieldDescriptors( )  {
    return( fd );
  }  /*  public FieldDescriptor[][] getFieldDescriptors( )  */


  /**
   *   Returns an array of FIELDDESCRIPTORS corresponding to the field type
   * stored at INDEX.
   */
  public FieldDescriptor[] getFieldDescriptor( int index )  {
    FieldDescriptor[] ret = null;

    if( ( index >= 0 ) && ( index < fd.length ) )  {
      ret = fd[index];
    }

    return( ret );
  }  /*  public FieldDescriptor[] getFieldDescriptor( int )  */


  public String getFileName( )  {
    return( fn );
  }  /*  public String getFileName( )  */


  /**
   *   Assumption 22 states that a file with field delimiters will necessarily
   * have record delimiters.  Thus a file with field delimiters is fully
   * delimited.
   */
  public boolean fullyDelimited( )  {
    boolean ret;

    if( ( d != null ) && d.validFieldDelimiter( ) )  {
      ret = true;
    }
    else  {
      ret = false;
    }

    return( ret );
  }  /*  public boolean fullyDelimited( )  */


  public boolean fixedWithRecordDelimiters( )  {
    boolean ret = false;

    if( d != null )  {
      if( !d.validFieldDelimiter( ) && d.validRecordDelimiter( ) )  {
        ret = true;
      }
    }

    return( ret );
  }  /*  public boolean fixedWithRecordDelimiters( )  */


  public boolean fixedWithoutRecordDelimiters( )  {
    return( ( d == null ) ||
            !( d.validFieldDelimiter( ) && d.validRecordDelimiter( ) ) );

  }  /*  public boolean fixedWithoutRecordDelimiters( )  */


  public boolean fixed( )  {
    return( fixedWithRecordDelimiters( ) || fixedWithoutRecordDelimiters( ) );
  }  /*  public boolean fixed( )  */


  public void clear( )  {
    rl = -1;
    rc = -1;
    rt = 0;
    d = null;
    re = null;
    fd = null;
    fn = null;
  }  /*  public void clear( )  */


  /**
   * @return TRUE if this FileDescriptor is valid, else FALSE.
   */
  public boolean isValid( )  {
    return( ( rc > 0 ) && ( fn != null ) );
  }  /*  public boolean isValid( )  */


  public String toXML( )  {
    int i,
        j,
        level = 0;
    String ret = ( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                   System.getProperty( "line.separator" ) + "<layout>" +
                   System.getProperty( "line.separator" ) + space( ++level ) +
                   "<filename>" + fn + "</filename>" +
                   System.getProperty( "line.separator" ) + space( level ) +
                   "<runtime>" + rt + "</runtime>" +
                   System.getProperty( "line.separator" ) + space( level ) +
                   "<encoding>" );

    if( ia )  {
      ret = ret.concat( "ASCII" );
    }
    else  {
      ret = ret.concat( "EBCDIC" );
    }
    ret = ret.concat( "</encoding>" + System.getProperty( "line.separator" ) +
                      space( level ) + "<filetype>" );

    if( fullyDelimited( ) )  {
      ret = ret.concat( "3" );
    }
    else if( fixedWithRecordDelimiters( ) )  {
      ret = ret.concat( "2" );
    }
    else if( fixedWithoutRecordDelimiters( ) )  {
      ret = ret.concat( "1" );
    }
    ret = ret.concat( "</filetype>" + System.getProperty( "line.separator" ) +
                      space( level ) + "<header>" +
                      Boolean.toString( hdr ).toUpperCase( ) + "</header>" +
                      System.getProperty( "line.separator" ) );

    if( d != null )  {
      ret = ret.concat( d.toXML( level ) );
    }
    else  {
      ret = ret.concat( space( level ) + "<delimiters>" +
              System.getProperty( "line.separator" ) + space( ++level ) +
              "<record>" + System.getProperty( "line.separator" ) +
              space( ++level ) + "<possible>" +
              GP.delimiterToString( "record" ) + "</possible>" +
              System.getProperty( "line.separator" ) + space( level ) +
              "<found></found>" + System.getProperty( "line.separator" ) +
              space( --level ) + "</record>" +
              System.getProperty( "line.separator" ) + space( level ) +
              "<field>" + System.getProperty( "line.separator" ) +
              space( ++level ) + "<possible>" +
              GP.delimiterToString( "field" ) + "</possible>" +
              System.getProperty( "line.separator" ) + space( level ) +
              "<found></found>" + System.getProperty( "line.separator" ) +
              space( --level ) + "</field>" +
              System.getProperty( "line.separator" ) + space( level ) +
              "<textquote>" + System.getProperty( "line.separator" ) +
              space( ++level ) + "<possible>" + GP.delimiterToString( "text" ) +
              "</possible>" + System.getProperty( "line.separator" ) +
              space( level ) + "<found></found>" +
              System.getProperty( "line.separator" ) + space( --level ) +
              "</textquote>" + System.getProperty( "line.separator" ) +
              space( --level ) + "</delimiters>" );

    }

    ret = ret.concat( System.getProperty( "line.separator" ) + space( level ) +
                      "<recordsexamined>" +
                      System.getProperty( "line.separator" ) +
                      space( ++level ) + "<count>" + rc + "</count>" +
                      System.getProperty( "line.separator" ) );

//    ret = ret.concat( space( level ) + "<range>" +
//                      System.getProperty( "line.separator" ) +
//                      space( ++level ) + "<startindex>" );
//
//    if( fullyDelimited( ) )  {
//      ret = ret.concat( Integer.toString( GP.hrts ) );
//    }
//    else  {
//      ret = ret.concat( "0" );
//    }
//    ret = ret.concat( "</startindex>" + System.getProperty( "line.separator" ) +
//                      space( level ) + "<endindex>" );
//
//    if( fullyDelimited( ) )  {
//      ret = ret.concat( Integer.toString( GP.hrts + rc ) );
//    }
//    else  {
//      ret = ret.concat( Integer.toString( rc ) );
//    }
//    ret = ret.concat( "</endindex>" + System.getProperty( "line.separator" ) +
//                      space( --level ) + "</range>" +
//                      System.getProperty( "line.separator" ) );

    ret = ret.concat( space( --level ) + "</recordsexamined>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<contenttypes>" +
                      System.getProperty( "line.separator" ) );

    level++;
    og.reset( );

    for( i = 0; i < og.oracleCount( ); i++ )  {
      ret = ret.concat( space( level ) + "<type>" + og.name( og.next( ) ) +
                        "</type>" + System.getProperty( "line.separator" ) );

    }
    ret = ret.concat( space( --level ) + "</contenttypes>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<record>" + System.getProperty( "line.separator" ) +
                      space( ++level ) + "<length>" + rl + "</length>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<fields>" + System.getProperty( "line.separator" ) +
                      re.toXML( ++level ) + space( --level ) + "</fields>" +
                      System.getProperty( "line.separator" ) +
                      space( --level ) + "</record>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<potentialfieldlocations>" );

    if( fd != null )  {
      ret = ret.concat( System.getProperty( "line.separator" ) );

      level++;
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          ret = ret.concat( space( level ) + "<type>" +
                            System.getProperty( "line.separator" ) );

          level++;
          for( j = 0; j < fd[i].length; j++ )  {
            ret = ret.concat( fd[i][j].toXML( level ) +
                              System.getProperty( "line.separator" ) );

          }
          ret = ret.concat( space( --level ) + "</type>" +
                            System.getProperty( "line.separator" ) );

        }
      }
    }

    return( ret.concat( space( --level ) + "</potentialfieldlocations>" +
                        System.getProperty( "line.separator" ) +
                        "</layout>" ) );

  }  /*  public String toXML( )  */


  public String toString( )  {
    int i,
    j,
    level = 0;
    StringBuffer ret = new StringBuffer( "FILE NAME: ".concat( fn ) );

    ret.append( System.getProperty( "line.separator" ) );
    ret.append( "RUN TIME: " );
    ret.append( rt );
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "CHARACTER ENCODING: " );
    if( ia )  {
      ret.append( "ASCII" );
    }
    else  {
      ret.append( "EBCDIC" );
    }
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "FILE TYPE: " );
    if( fullyDelimited( ) )  {
      ret.append( 3 );
      ret.append( " - fully delimited" );
    }
    else if( fixedWithRecordDelimiters( ) )  {
      ret.append( 2 );
      ret.append( " - hybrid" );
    }
    else if( fixedWithoutRecordDelimiters( ) )  {
      ret.append( 1 );
      ret.append( " - fully fixed" );
    }
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "HEADER: " );
    ret.append( hdr );
    ret.append( System.getProperty( "line.separator" ) );

    if( d != null )  {
      ret.append( d.toString( ) );
    }
    else  {
      
      ret.append( "RECORD DELIMITER" );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "POSSIBLE: " ) );
      ret.append( GP.delimiterToString( "record" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "FOUND:" ) );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( "FIELD DELIMITER" );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "POSSIBLE: " ) );
      ret.append( GP.delimiterToString( "field" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "FOUND:" ) );
      ret.append( System.getProperty( "line.separator" ) );

      ret.append( "TEXT DELIMITER" );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "POSSIBLE: " ) );
      ret.append( GP.delimiterToString( "text" ) );
      ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "FOUND:" ) );
      ret.append( System.getProperty( "line.separator" ) );
    }

    ret.append( "RECORDS EXAMINED: " );
    ret.append( rc );
    ret.append( System.getProperty( "line.separator" ) );

//    ret.append( space( 1 ).concat( "RANGE: " ) );
//    if( fullyDelimited( ) )  {
//      ret.append( GP.hrts );
//    }
//    else  {
//      ret.append( 0 );
//    }
//    ret.append( " - " );
//    if( fullyDelimited( ) )  {
//      ret.append( GP.hrts + rc );
//    }
//    else  {
//      ret.append( rc );
//    }
//    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "RECORD LENGTH: " );
    ret.append( rl );
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "LAYOUT ---" );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( re.toString( ) );
    ret.append( "----------" );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "POTENTIAL FIELD LOCATIONS ---" );
    ret.append( System.getProperty( "line.separator" ) );
    if( fd != null )  {
      //Header.
      ret.append( space( 5 ) );
      ret.append( pad( true, ' ', 25, "CONTENT TYPE NAME" ) );
      if( fixed( ) )  {
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

      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          for( j = 0; j < fd[i].length; j++ )  {
            ret.append( pad( false, ' ', 3, Integer.toString( i + 1 ) ) );
            ret.append( '-' );
            ret.append( pad( false, ' ', 3, Integer.toString( j + 1 ) ) );
            ret.append( ".) " );
            ret.append( fd[i][j].toString( ) );
            ret.append( System.getProperty( "line.separator" ) );
          }
        }
      }
    }
    ret.append( "-----------------------------" );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( System.getProperty( "line.separator" ) );
    
    ret.append( "CONTENT TYPES:" );
    ret.append( System.getProperty( "line.separator" ) );
    og.reset( );

    for( i = 0; i < og.oracleCount( ); i++ )  {
      ret.append( space( level ).concat( og.name( og.next( ) ) ) );
      ret.append( System.getProperty( "line.separator" ) );
    }

    return( ret.toString( ) );
  }

}  /*  public class FileDescriptor  */
