package supportObjects;


public class Delimiter  {
  private char[] fd,  //The field delimiter.
                 rd,  //The record delimiter.
                 td;  //The text delimiter ( string literals ).


  public Delimiter( )  {
    fd = null;
    rd = null;
    td = null;
  }  /*  Constructor  */


  public Delimiter( char[] fieldDelimiter, char[] recordDelimiter )  {
    fd = fieldDelimiter;
    rd = recordDelimiter;
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


  public void setFieldDelimiter( char[] fieldDelimiter )  {
    fd = fieldDelimiter;
  }  /*  public void setFieldDelimiter( char[] )  */


  public void addFieldDelimiter( char fieldDelimiter )  {
    char[] temp;

    if( fd == null )  {
      fd = new char[1];
      fd[0] = fieldDelimiter;
    }
    else  {
      temp = new char[( fd.length + 1 )];
      System.arraycopy( fd, 0, temp, 0, fd.length );
      temp[( temp.length - 1 )] = fieldDelimiter;
      fd = temp;
    }
  }  /*  public void addFieldDelimiter( char )  */


  public void setRecordDelimiter( char[] recordDelimiter )  {
    rd = recordDelimiter;
  }  /*  public void setRecordDelimiter( char[] )  */


  public void addRecordDelimiter( char recordDelimiter )  {
    char[] temp;

    if( rd == null )  {
      rd = new char[1];
      rd[0] = recordDelimiter;
    }
    else  {
      temp = new char[( rd.length + 1 )];
      System.arraycopy( rd, 0, temp, 0, rd.length );
      temp[( temp.length - 1 )] = recordDelimiter;
      rd = temp;
    }
  }  /*  public void addRecordDelimiter( char )  */


  public void setTextDelimiter( char[] textDelimiter )  {
    td = textDelimiter;
  }  /*  public void setTextDelimiter( char[] )  */


  public boolean validFieldDelimiter( )  {
    return( fd != null );
  }  /*  public boolean validFieldDelimiter( )  */


  public char[] getFieldDelimiter( )  {
    return( fd );
  }  /*  public char[] getFieldDelimiter( )  */


  public int fieldDelimiterLength( )  {
    return( fd.length );
  }  /*  public int fieldDelimiterLength( )  */


  public boolean validRecordDelimiter( )  {
    return( rd != null );
  }  /*  public boolean validRecordDelimiter( )  */


  public char[] getRecordDelimiter( )  {
    return( rd );
  }  /*  public char[] getRecordDelimiter( )  */


  public int recordDelimiterLength( )  {
    return( rd.length );
  }  /*  public int recordDelimiterLength( )  */


  public boolean validTextDelimiter( )  {
    return( td != null );
  }  /*  public boolean validTextDelimiter( )  */


  public char[] getTextDelimiter( )  {
    return( td );
  }  /*  public char[] getTextDelimiter( )  */


  public int textDelimiterLength( )  {
    return( td.length );
  }  /*  public int textDelimiterLength( )  */


  public String delimiterValues( boolean recordDelimiter )  {
    char[] c;
    int i;
    StringBuffer ret = new StringBuffer( );

    if( recordDelimiter )  {
      c = rd;
    }
    else  {
      c = fd;
    }

    if( c != null )  {
      for( i = 0; i < ( c.length - 1 ); i++ )  {
        ret.append( Integer.toString( ( int )c[i] ) );
        ret.append( "," );
      }
      ret.append( Integer.toString( ( int )c[i] ) );
    }

    return( ret.toString( ) );
  }  /*  public String recordDelimiterValues( )  */


  public static boolean equals( char[] d1, char[] d2 )  {
    boolean same = false;
    int i = 0;

    if( ( d1 != null ) && ( d2 != null ) )  {
      if( same = ( d1.length == d2.length ) )  {
        while( same && ( i < d1.length ) )  {
          if( d1[i] == d2[i] )  {
            i++;
          }
          else  {
            same = false;
          }
        }
      }
    }
    else if( ( d1 == null ) && ( d2 == null ) )  {
      same = true;
    }

    return( same );
  }  /*  public static boolean equals( char[], char[] )  */


  /**
   *   The possible entries for record, field, and textquote delimiters are
   * currently static.  This should change somehow to a more dynamic approach.
   * This will need to affect BasicAttributes.delimited( char[] ) as well.
   */
  public String toXML( int level )  {
    int i;
    String ret = ( space( level ) + "<delimiters>" +
                   System.getProperty( "line.separator" ) + space( ++level ) +
                   "<record>" + System.getProperty( "line.separator" ) +
                   space( ++level ) + "<possible>" +
                   GP.delimiterToString( "record" ) + "</possible>" +
                   System.getProperty( "line.separator" ) + space( level ) +
                   "<found>" );

    if( rd != null )  {
      for( i = 0; i < ( rd.length - 1 ); i++ )  {
        ret = ret.concat( Integer.toString( ( int )rd[i] ) + "," );
      }
      ret = ret.concat( Integer.toString( ( int )rd[i] ) );
    }
    ret = ret.concat( "</found>" + System.getProperty( "line.separator" ) +
                      space( --level ) + "</record>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<field>" + System.getProperty( "line.separator" ) +
                      space( ++level ) + "<possible>" +
                      GP.delimiterToString( "field" ) + "</possible>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<found>" );

    if( fd != null )  {
      for( i = 0; i < ( fd.length - 1 ); i++ )  {
        ret = ret.concat( Integer.toString( ( int )fd[i] ) + "," );
      }
      ret = ret.concat( Integer.toString( ( int )fd[i] ) );
    }
    ret = ret.concat( "</found>" + System.getProperty( "line.separator" ) +
                      space( --level ) + "</field>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<textquote>" + System.getProperty( "line.separator" ) +
                      space( ++level ) + "<possible>" +
                      GP.delimiterToString( "text" ) + "</possible>" +
                      System.getProperty( "line.separator" ) + space( level ) +
                      "<found>" );

    if( td != null )  {
      for( i = 0; i < ( td.length - 1 ); i++ )  {
        ret = ret.concat( Integer.toString( ( int )td[i] ) + "," );
      }
      ret = ret.concat( Integer.toString( ( int )td[i] ) );
    }
    ret = ret.concat( "</found>" + System.getProperty( "line.separator" ) +
                      space( --level ) + "</textquote>" + 
                      System.getProperty( "line.separator" ) +
                      space( --level ) + "</delimiters>" );

    return( ret );
  }  /*  public String toXML( int )  */


  public String toXML( )  {
    return( toXML( 0 ) );
  }  /*  public String toXML( )  */


  public String toString( )  {
    int i;
    StringBuffer ret = new StringBuffer( "RECORD DELIMITER" );

    ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "POSSIBLE: " ) );
      ret.append( GP.delimiterToString( "record" ) );
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( space( 1 ).concat( "FOUND: " ) );
    if( rd != null )  {
      for( i = 0; i < ( rd.length - 1 ); i++ )  {
        ret.append( ( int )rd[i] );
        ret.append( "," );
      }
      ret.append( ( int )rd[i] );
    }
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "FIELD DELIMITER" );
    ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "POSSIBLE: " ) );
      ret.append( GP.delimiterToString( "field" ) );
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( space( 1 ).concat( "FOUND: " ) );
    if( fd != null )  {
      for( i = 0; i < ( fd.length - 1 ); i++ )  {
        ret.append( ( int )fd[i] );
        ret.append( "," );
      }
      ret.append( ( int )fd[i] );
    }
    ret.append( System.getProperty( "line.separator" ) );

    ret.append( "TEXT DELIMITER ( string literals )" );
    ret.append( System.getProperty( "line.separator" ) );
      ret.append( space( 1 ).concat( "POSSIBLE: " ) );
      ret.append( GP.delimiterToString( "text" ) );
    ret.append( System.getProperty( "line.separator" ) );
    ret.append( space( 1 ).concat( "FOUND: " ) );
    if( td != null )  {
      for( i = 0; i < ( td.length - 1 ); i++ )  {
        ret.append( ( int )td[i] );
        ret.append( "," );
      }
      ret.append( ( int )td[i] );
    }
    ret.append( System.getProperty( "line.separator" ) );

    return( ret.toString( ) );
  }  /*  public String toString( )  */
    
}  /*  public class Delimiter  */
