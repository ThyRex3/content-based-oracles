package oracles;

import dataStructures.LinkedList;
import dataStructures.Lookup;
import dataStructures.Node;
import supportObjects.NameValuePair;
import supportObjects.FieldDescriptor;
import supportMethods.Setup;
import supportMethods.BasicAttributes;


public class NameSuffixOracle implements Oracle, /*AlterDatabase,*/ PreProcess  {

  private int ml,  //Maximum length.
              gid;  //Grouping ID.
  private double mp,  //Minimum percentage.
                 mbp,  //Maximum blank percentage.
                 mnp;  //Maximum number percentage.
  private Lookup lookup;  //The data structure containing the oracle's database.
//  private OracleGroup og;
  private String on,  //Oracle name.
//                 nfn,  //The name of the file containing the minimum database.
                 xfn;  //The name of the file containing the maximum database.


  public NameSuffixOracle( )  {  }  /*  Constructor  */


  private String clean( String s )  {
    return( s.trim( ).toLowerCase( ) );
  }  /*  private String screen( String )  */


  private int nonBlankCount( char[] arr, int sp, int len )  {
    int i,
        ep,
        ret = 0;

    if( arr != null )  {
      ep = ( sp + len );

      for( i = sp; i < ep; i++ )  {
        if( !Character.isWhitespace( arr[i] ) )  {
          ret++;
        }
      }
    }

    return( ret );
  }  /*  private int nonBlankCount( char[], int, int )  */


  private boolean blank( char[] arr, int sp, int len )  {
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
  }  /*  private boolean blank( char[] )  */


  public void initialize( OracleGroup og, String[] args )  {
    int i;
    NameValuePair nvp;
    String n;

    ml = 25;
    gid = 2;
    mp = 0.30;
    mbp = 0.98;
    mnp = 0.95;
    lookup = new Lookup( );
//    this.og = og;
    on = "name suffix";
//    nfn = "/data/NameSuffix_Limited.dat";
//    xfn = "/data/NameSuffix_CompleteSorted.dat";
    xfn = "/data/NameSuffix_Limited.dat";

    if( args != null )  {
      for( i = 0; i < args.length; i++ )  {
        nvp = Setup.separate( args[i] );

        n = nvp.getName( );
        if( n.equalsIgnoreCase( "typename" ) )  {
          on = nvp.getValue( );
        }
//        else if( n.equalsIgnoreCase( "minimumsourcefile" ) )  {
//          nfn = nvp.getValue( );
//        }
        else if( n.equalsIgnoreCase( "maximumsourcefile" ) )  {
          xfn = nvp.getValue( );
        }
        else if( n.equalsIgnoreCase( "maximumlength" ) )  {
          try  {
            ml = Integer.parseInt( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            ml = 25;
          }
        }
        else if( n.equalsIgnoreCase( "minimumthreshold" ) )  {
          try  {
            mp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mp = 0.30;
          }
        }
        else if( n.equalsIgnoreCase( "maximumblankpercentage" ) )  {
          try  {
            mbp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mbp = 0.98;
          }
        }
        else if( n.equalsIgnoreCase( "maximumnumberpercentage" ) )  {
          try  {
            mnp = Double.parseDouble( nvp.getValue( ) );
          }catch( NumberFormatException e )  {
            mnp = 0.95;
          }
        }
        else if( n.equalsIgnoreCase( "grouping" ) )  {
          try  {
            gid = Integer.parseInt( nvp.getValue( ), 2 );
          }catch( NumberFormatException e )  {
            gid = 2;
          }
        }
      }
    }

    lookup.setDataSource(
      og.fileLineCount( BasicAttributes.extractFileName( true, xfn ) ), xfn );

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


/*
  public void setMinDatabase( )  {
    lookup.setDataSource(
        og.fileLineCount( BasicAttributes.extractFileName( true, nfn ) ), nfn );

  }  /*  public void setMinDatabase( )  */


/*
  public void setMaxDatabase( )  {
    lookup.setDataSource(
        og.fileLineCount( BasicAttributes.extractFileName( true, xfn ) ), xfn );

  }  /*  public void setMaxDatabase( )  */


  /**
   *   Case 1:  A representative PFP is of length one then it is probably not a
   * NameSuffix.  While there may be name suffix values with a length of one, it
   * is unlikely that the field will be of length one.
   *   Case 2:  Vertical analysis shows that all values corresponding to a
   * particular PFP are all numbers.  Because of name suffixes such as 1, 2, 3;
   * it is possible for a number column to be identified as a name suffix.  This
   * vertical analysis checks to ensure that a field is not entirely numbers.
   * If it is then the NameSuffix label is removed as it is probably not a
   * NameSuffix field.
   *   Case 3:  Vertical analysis shows that each entry in a field is exactly
   * the same.  Based on the assumption of normal distribution of data it is
   * improbable that every record contain the exact NameSuffix value.
   */
  public FieldDescriptor[] preProcess( char[][] records,
                                       FieldDescriptor[] fd )  {

    boolean b,
            anc;  //All non-blank entries are not characters/letters.
    int c,
        i,
        j,
        k,
        x,
        sp;
    Node n;
    LinkedList list;
    FieldDescriptor[] ret = null;

    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] != null )  {  //May not be necessary, but just in case.
//Debug statement.
//System.out.println( fd[i] );
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//System.out.println( "" );
//System.out.println( fd[i] );
//}
//End debug statement.
        b = true;

        //If the field has a length of one.  Though some name suffixes values
        //  have a length of one, it is unlikely that the field will be that
        //  size.  If this is the case then it is probable that the field is in
        //  fact something else.
        if( fd[i].getLength( ) == 1 )  {
//Debug statement.
//System.out.println( "Field length is one." );
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//System.out.println( "Setting B to false. - 1" );
//}
//System.out.println( "Setting to NULL -- " + fd[i].toString( ) );
//End debug statement.
          b = false;
          fd[i] = null;
        }

        //Now test to see if a vast majority of the valid entries are of length
        //  one.  This is different because the field length may be greater than
        //  one while the actual length of the entries is only one.
        if( b )  {
          k = 0;
          x = fd[i].getLength( );
          sp = fd[i].getPosition( );

          for( j = 0; j < records.length; j++ )  {
            if( isValid( records[j], sp, x ) &&
                ( nonBlankCount( records[j], sp, x ) < 2 ) )  {

              k++;
            }
          }

          //
          if( ( ( double )k / ( double )fd[i].getNumberValid( ) ) > 0.95 )  {
//Debug statement.
//System.out.println( "Field entries length is one." );
//End debug statement.
            b = false;
            fd[i] = null;
          }
        }

        //Next perform vertical analysis upon the field currently under
        //  consideration.  If all, most, of the fields contain only numbers
        //  then the field should be indicated as a numeric field rather than a
        //  name suffix.
        if( b )  {
          //Store the range of the NameSuffix field under consideration.
          sp = fd[i].getPosition( );
          x = ( sp + fd[i].getLength( ) );  //The end position.

          //Scan the column counting how many entries do not contain a letter. 
          c = 0;
          for( j = 0; j < records.length; j++ )  {  //For each record.
            //Look at the field in the current record testing for letters.
            anc = true;
            k = sp;
            while( anc && ( k < x ) )  {
              if( Character.isLetter( records[j][k] ) )  {
                anc = false;  //Found a letter.
              }
              else  {
                k++;
              }
            }

            if( anc )  {  //If there were no characters/letters increment C.
//Debug statement.
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//System.out.println( "|" + ( new String( records[j], sp, fd[i].getLength( ) ) ) + "| has no characters." );
//}
//End debug statement.
              c++;
            }
          }

//Debug statement.
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//System.out.println( "C = " + c + ";  Records length = " + records.length + ";  Blank count = " + fd[i].getBlankCount( ) + "  ==  " + ( ( double )( ( double )c - ( double )fd[i].getBlankCount( ) ) / ( double )( ( double )records.length - ( double )fd[i].getBlankCount( ) ) ) + "." );
//}
//End debug statement.
          //If almost all of the entries did not contain a letter then it is
          //  probably not a NameSuffix.
          if( ( ( double )( ( double )c - ( double )fd[i].getBlankCount( ) ) /
                ( double )( ( double )records.length -
                            ( double )fd[i].getBlankCount( ) ) ) >= mnp )  {

//Debug statement.
//System.out.println( "All numbers." );
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//System.out.println( "Setting B to false. - 2" );
//}
//System.out.println( "Setting to NULL -- " + fd[i].toString( ) );
//End debug statement.
            b = false;
            fd[i] = null;
          }
        }

//Debug statement.
//if( fd[i] != null )  {
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//if( !b )  {
//System.out.println( "B is false." );
//}
//}
//}
//End debug statement.
        //Next perform vertical analysis to see if every valid entry contains
        //  the same value.  If so it is probably not a name suffix.
        if( b )  {
          j = 0;
          k = 0;
          sp = fd[i].getPosition( );
          x = fd[i].getLength( );
          list = new LinkedList( );

          while( ( list.length( ) < 3 ) && ( j < records.length ) )  {
            //Only insert entries into the linked list if they are valid.
            if( isValid( records[j], sp, x ) )  {
              k++;  //Count the total number of valid entries.
              list.searchAndInsert( records[j], sp, x );
//Debug statement.
//if( fd[i] != null )  {
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//System.out.println( "|" + ( new String( records[j], sp, x ) ) + "| is valid.  List length = " + list.length( ) + "." );
//}
//}
//End debug statement.
            }

            j++;
          }

          //If there was a non-whitespace character, the number of unique, valid
          //  entries is greater than 10% of the total number of valid entries,
          //  and the frequency of the most frequently occurring entry is
          //  greater than 90% of the non-whitespace entries.  Basically this is
          //  a test to see if a single value occurred in all of the records.
          //  If so it is probably not a name suffix.
          n = list.maxCount( );
//Debug statement.
//if( fd[i] != null )  {
//if( ( fd[i].getPosition( ) > 358 ) && ( fd[i].getPosition( ) < 366 ) )  {
//if( n == null )  {
//System.out.println( "N is NULL." );
//}
//else  {
//System.out.println( "N is not NULL." );
//System.out.println( "N count = " + n.getCount( ) + ";  Records length = " + records.length + ";  Blank count = " + fd[i].getBlankCount( ) + ";  " + ( double )( ( double )n.getCount( ) / ( double )( ( double )records.length - ( double )fd[i].getBlankCount( ) ) ) );
//}
//System.out.println( "List length = " + list.length( ) + ";  Valid count = " + k + ";  " + ( double )( ( double )list.length( ) / ( double )k ) );
//}
//}
//End debug statement.
          if( ( n == null ) || ( ( n != null ) &&
                ( ( double )( ( double )list.length( ) /
                              ( double )k ) > 0.10 ) &&
                ( ( double )( ( double )n.getCount( ) /
                              ( double )( ( double )records.length -
                                ( double )fd[i].getBlankCount( ) ) ) >
                            0.90 ) ) )  {

//Debug statement.
//System.out.println( "No statistical distribution." );
//System.out.println( "Setting to NULL -- " + fd[i].toString( ) );
//End debug statement.
            fd[i] = null;
          }

          n = null;
          list.clear( );
          list = null;
        }
      }
    }

    c = 0;
    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] == null )  {
        c++;
      }
    }

    if( ( fd.length - c ) > 0 )  {
      ret = new FieldDescriptor[( fd.length - c )];

      j = 0;
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          ret[j++] = fd[i];
        }
      }

      fd = ret;
    }

    return( ret );
  }  /*  public FieldDescriptor[] postProcess( char[][], FieldDescriptor )  */


  public FieldDescriptor[] preProcess( char[][][] records,
                                       FieldDescriptor[] fd )  {

    boolean b,
            anc;  //All non-blank entries are not characters/letters.
    int c,
        i,
        j,
        k,
        bc,
        sp;
    Node n;
    LinkedList list;
    FieldDescriptor[] ret = null;

    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] != null )  {  //May not be necessary, but just in case.
        b = false;

        //Scan all the records to see if each field is of length one.
        sp = fd[i].getPosition( );
        j = 0;
        while( !b && ( j < records.length ) )  {
          if( ( records[j][sp] == null ) || ( records[j][sp].length == 1 ) )  {
            j++;
          }
          else  {
            b = true;
          }
        }

        if( !b )  {  //All fields were blank or of length one.
          fd[i] = null;
        }

        if( b )  {
          //Scan the column counting how many entries do not contain a letter. 
          c = 0;
          bc = 0;
          for( j = 0; j < records.length; j++ )  {  //For each record.
//Debug statement.
//System.out.println( "Record count = " + records.length + " -- " + j + ";  Field count = " + records[j].length + " -- " + sp + ";  Field length = " + records[j][sp].length + "." );
//End debug statement.
            if( ( records[j][sp] != null ) &&
                !blank( records[j][sp], 0, records[j][sp].length ) )  {
              //Look at the field in the current record testing for letters.
              anc = true;
              k = 0;
              while( anc && ( k < records[j][sp].length ) )  {
                if( Character.isLetter( records[j][sp][k] ) )  {
                  anc = false;  //Found a letter.
                }
                else  {
                  k++;
                }
              }

              //If there were no characters/letters increment count.
              if( anc )  {
                c++;
              }
            }
            else  {
              bc++;  //Increment the blank count.
            }

          }

          //If almost all of the entries did not contain a letter then it is
          //  probably not a NameSuffix.
          if( ( ( double )c / ( ( double )records.length - ( double )bc ) ) >=
              mnp )  {

            b = false;
            fd[i] = null;
          }
        }

        if( b )  {
          c = 0;  //The blank count.
          j = 0;
          list = new LinkedList( );

          while( ( list.length( ) < 3 ) && ( j < records.length ) )  {
            if( ( records[j][sp] == null ) ||
                blank( records[j][sp], 0, records[j][sp].length ) )  {

              c++;
            }
            //Only insert entries into the linked list if they are valid.
            else if( isValid( records[j][sp] ) )  {
              list.searchAndInsert( records[j][sp] );
            }

            j++;
          }

          //If there was a non-whitespace character, the number of unique, valid
          //  entries is less than 3, and the frequency of the most frequently
          //  occurring entry is greater than 90% of the non-whitespace entries.
          //  Basically this is a test to see if a single value occurred in all
          //  of the records.  If so it is probably not a name suffix.
          n = list.maxCount( );
          if( ( n != null ) && ( list.length( ) < 3 ) &&
              ( ( double )( ( double )n.getCount( ) /
                            ( double )list.totalCount( ) ) > 0.90 ) )  {

            fd[i] = null;
          }

          n = null;
          list = null;
        }
      }
    }

    c = 0;
    for( i = 0; i < fd.length; i++ )  {
      if( fd[i] == null )  {
        c++;
      }
    }

    if( ( fd.length - c ) > 0 )  {
      ret = new FieldDescriptor[( fd.length - c )];

      j = 0;
      for( i = 0; i < fd.length; i++ )  {
        if( fd[i] != null )  {
          ret[j++] = fd[i];
        }
      }

      fd = ret;
    }

    return( ret );
  }  /*  public void preProcess( char[][][], FieldDescriptor[] )  */

}  /*  public class NameSuffixOracle implements Oracle, AlterDatabase,
                                                PreProcess  */
