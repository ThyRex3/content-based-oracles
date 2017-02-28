/**
 *   This class contains several static methods used by the main() method to
 * determine some of a file's properties.
 */

package supportMethods;

import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.FileInputStream;

import supportObjects.*;
import oracles.OracleGroup;


public class BasicAttributes  {
  private static int partition( int p, int r, int[] arr )  {
    int i = ( p - 1 ),
        j,
        t,
        x = arr[r];

    for( j = p; j < r; j++ )  {
      if( arr[j] <= x )  {
        t = arr[++i];
        arr[i] = arr[j];
        arr[j] = t;
      }
    }

    t = arr[++i];
    arr[i] = arr[r];
    arr[r] = t;

    return( i );
  }  /*  private static int partition( int, int, int[] )  */


  /**
   *   This method attempts to determine if the space characters in the data are
   * filler for fixed length fields or if they are delimiters.  For now the
   * logic separates the cases of having a record delimiter or not.  This may
   * prove to be unnecessary.  The case of no record delimiter is slightly more
   * simple and may be adequate for both cases, especially since the code base
   * is so similar.
   * 
   * @return - TRUE if the spaces are considered to be filler, else FALSE.
   */
/*
  private static boolean isSpaceFiller( int startPosition, char[] data,
                                        char[] recordDelimiter )  {

    boolean frd = false,  //Found a record delimiter.
            ret = false;
    int i,
        p,
        rs = startPosition,  //The start of the record.
        gs = 0,  //Count of groups of spaces.
        ss = 0,  //Count of single spaces.
        crf = 0,  //Count of records with filler spaces.
        nrc = 25;  //The number of records to test.


    //If the percentage of groupings of spaces compared to the number of single
    //  spaces is high then assume that it is filler.
    if( recordDelimiter != null )  {
      for( i = 0; i < nrc; i++ )  {
        //Run through an entire record counting the groupings of spaces and the
        //  single spaces.
        p = 0;
        while( !frd )  {
          //Search for either a record delimiter or a space.
          while( !frd && ( ( rs + p ) < data.length ) &&
                 ( data[( rs + p )] != ( char )0x20 ) )  {

            if( data[( rs + p )] == recordDelimiter[0] )  {
              frd = checkDelimiter( p, recordDelimiter, data );
            }

            p++;
          }

          if( !frd )  {  //Did not encounter a delimiter, must be space(s).
            //Increment P by one and check for a adjacent space.
            if( ( ( rs + ++p ) < data.length ) &&
                ( data[( rs + p )] == ( char )0x20 ) )  {

              gs++;  //A group of spaces, increment the counter.

              //Move to the end of the block of spaces.
              while( ( ( rs + p ) < data.length ) &&
                     ( data[( rs + p )] == ( char )0x20 ) )  {

                p++;
              }
            }
            else  {
              ss++;  //A single space, increment the counter.
            }
          }
          else  {  //Encountered a record delimiter.
            rs += ( ( p - 1 ) + recordDelimiter.length );
          }
        }

        //If the percentage of space groupings of length greater than one
        //  compared to the number of all space groupings is greater than 35%
        //  then this record is considered to have filler spaces.
        if( ( ( double )gs / ( double )( gs + ss ) ) > 0.35 )  {
          crf++;
        }
      }

      //If the percentage of records with filler spaces is greater than 50% then
      //  assume the space is filler.
      if( ( ( double )crf / ( double )nrc ) > 0.5 )  {
        ret = true;
      }
    }
    //A little less exact, this block of code handles the condition when there
    //  are no record delimiters.  All that is done is a comparison is made
    //  between the number of space groupings of size greater than one and the
    //  total number of space groupings.  If the percentage is high enough it
    //  will be assumed that the spaces are filler.
    else  {  //A record delimiter was not found.
      p = 0;
      while( ( ( rs + p ) < data.length ) && ( ( p - rs ) < 20000 ) )  {
        //Search for either a record delimiter or a space.
        while( !frd && ( ( rs + p ) < data.length ) &&
               ( data[( rs + p )] != 0x20 ) )  {

          frd = checkDelimiter( p++, recordDelimiter, data );
        }

        if( !frd )  {  //Did not encounter a delimiter, must be space(s).
          //Increment P by one and check for a contiguous space.
          if( ( ( rs + ++p ) < data.length ) &&
              ( data[( rs + p )] == 0x20 ) )  {

            gs++;  //A group of spaces, increment the counter.

            //Move to the end of the block of spaces.
            while( ( ( rs + p ) < data.length ) &&
                   ( data[( rs + p )] == 0x20 ) )  {

              p++;
            }
          }
          else  {
            ss++;  //A single space, increment the counter.
          }
        }
      }

      //If the percentage of space groupings of length greater than one
      //  compared to the number of all space groupings is greater than 35%
      //  then the spaces are considered to be filler.
      ret = ( ( ( double )gs / ( double )( gs + ss ) ) > 0.35 );
    }

    return( ret );
  }  /*  private static boolean isSpaceFiller( int, char[], char[] )  */


  /**
   * @param LENGTHS is a sorted array.
   */
  private static int mostFrequent( int[] lengths )  {
    int i,
        j,
        k,
        ret = -1;

    j = 0;  //The index of the beginning of this group of record lengths.
    k = 1;  //The number of similar record lengths so far.
    ret = lengths[0]; //Value of the largest number of similar record lengths.
    for( i = 1; i < lengths.length; i++ )  {
      //Find the start and end position of a single record length.
      if( lengths[i] != lengths[j] )  {
        //The difference of the start and end position is the number of
        //  records with this particular length.
        if( ( i - j ) > k )  {
          k = ( i - j );
          ret = lengths[j];  //Set the return value equal to the record length.
        }

        j = i;  //Set the start position for the next sequence of lengths.
      }
    }

    return( ret );
  }  /*  private static int mostFrequent( int[] )  */


  public static int averageRecordLength( char[][][] records )  {
    int i,
        j,
        rt = 0;

    for( i = 0; i < records.length; i++ )  {
      for( j = 0; j < records[i].length; j++ )  {
        if( records[i][j] != null )  {
          rt += records[i][j].length;
        }
      }
    }

    return( ( int )( ( double )( ( double )rt / ( double )records.length ) ) );
  }  /*  public static int averageRecordLength( char[][][] )  */


  private static byte[][] readSamples( int rl, String fn )  {

    byte[] holder;
    byte[][] ret;
    int i,
        n,
        x,
        y;
    long size = Long.MIN_VALUE;
    File f;
    DataInputStream inf;
//Debug statement.
//int zzz;
//End debug statement.

//Debug statement.
//System.out.println( "Input file name |" + fn + "|." );
//End debug statement.
    //Determine the size of the file to be sampled.
    f = new File( fn );
    if( f.isFile( ) )  {
      size = f.length( );
    }
    f = null;  //Cleanup.

    //Determine the size of each partition to be read.
    x = ( ( ( GP.orc / GP.spc ) + 2 ) * rl );
//Debug statement.
//System.out.println( "File size = " + size + ";  Partition size = " + x + ";  Total sample size = " + ( x * GP.spc ) + "." );
//End debug statement.

    if( ( x * GP.spc ) < size )  {
      ret = new byte[GP.spc][];  //Allocate memory.

      try  {
        //Determine the size of each data portion between the sample partitions.
        y = ( ( ( int )size - ( x * GP.spc ) ) / ( GP.spc - 1 ) );

        holder = new byte[x];
        inf = new DataInputStream( new FileInputStream( fn ) );

        for( i = 0; i < GP.spc; i++ )  {
          n = inf.read( holder );
          inf.skipBytes( y );
//Debug statement.
//System.out.println( "Read " + n + " bytes, skipped " + zzz + " bytes." );
//End debug statement.

          if( n > 0 )  {
            ret[i] = new byte[n];
            System.arraycopy( holder, 0, ret[i], 0, n );
          }
          else  {
            ret[i] = null;
          }
        }

        inf.close( );
      }catch( IOException e )  {
        System.out.println( "readSamples - BasicAttributes error 1: " +
                            "IOException.  " + e.getMessage( ) );

        System.exit( 1 );
      }
    }
    else  {
      ret = new byte[1][];  //Allocate memory.

      ret[0] = readFilePortion( GP.readSize, fn );  //Read the whole file.
    }

    return( ret );
  }  /*  private static byte[][] readSamples( int, String )  */


  public static char[][] splitSecondSample( OracleGroup og, String fn )  {
    boolean s;
    boolean[][] nsp;
    byte[][] b;
    char[] c;
    char[][] ret = null,
             data;
    char[][][] holder;  //An array of records for each sample.
    int h,
        i,
        j,
        n = 0,
        ep = 0,
        ni = og.index( "full name" ),  //Index of the full name oracle.
        nl = og.getMaxLength( ni ),  //Maximum length to scan for full name.
        rl = GP.result.getRecordLength( ),
        sp = 0,
        fni = og.index( "first name" ),  //Index of the first name oracle.
        fnl = og.getMaxLength( fni ),  //Maximum length to scan for first name.
        offset;

    b = readSamples( rl, fn );

    //Transform the raw data into character data.
    data = new char[b.length][];  //Allocate memory.
    holder = new char[b.length][][];  //Allocate memory.
    for( i = 0; i < b.length; i++ )  {
      if( !GP.result.getIsAscii( ) )  {  //Test if the sample is ASCII.
        convertToAscii( b[i], og );  //If not, then convert.
      }

      data[i] = ( new String( b[i] ) ).toCharArray( );
    }

    holder[0] = splitRecords( data[0], rl );
    nsp = new boolean[2][data[0].length];
    //Scan the first sample once for name potential field positions.
    for( i = 0; i < data[0].length; i++ )  {  //Start positions.
      //Skip over the first few characters to save a little time.
      for( j = 5; ( ( ( i + j ) < data[0].length ) && ( j < fnl ) ); j++ )  {
        //Test for a first name.
        nsp[0][i] |= og.isValid( data[0], i, j, fni );

        //Test for a full name.
        nsp[1][i] |= og.isValid( data[0], i, j, ni );
      }

      //Full name maximum length is greater than first name.  Scan the remainder
      //  of the full name range only checking for full names.
      for( ; ( ( ( i + j ) < data[0].length ) && ( j < nl ) ); j++ )  {
        nsp[1][i] |= og.isValid( data[0], i, j, ni );
      }
    }

    //Using this information determine the PFP offset within a record.
//!!ASSUMPTION!! When using names a file sorted by city may return the position
//               of the city field instead of the name field.  This may not be
//               an actual problem though as the fields will still line up in
//               the following logic.
    offset = -1;  //Offset of name field with maximum count.
    h = Integer.MIN_VALUE;  //The maximum count currently encountered.
    for( i = 0; i < rl; i++ )  {
      sp = 0;  //The current count for first names.
      ep = 0;  //The current count for full names.
      //Divide the sample into logical records based on the known record length.
      for( j = 0; j < data[0].length; j += rl )  {
        if( nsp[0][( j + i )] )  {
          sp++;
        }
        if( nsp[1][( j + i )] )  {
          ep++;
        }
      }

      if( ( sp > h ) || ( ep > h ) )  {
        if( sp > ep )  {  //Update the maximum count.
          h = sp;
        }
        else  {
          h = ep;
        }

        offset = i;  //Update the position of the field within the record.
      }
    }

    //Since the remaining samples are taken from random points within the file
    //  it is necessary to find where the record boundaries are.  This way when
    //  a sample is divided any partial records at the beginning or end of the
    //  sample may be discarded.
    for( h = 1; h < data.length; h++ )  {  //Consider each sample.
      //First find the positioning, PFP, of fields in the current sample.
      nsp = new boolean[2][data[h].length];
      for( i = 0; i < data[h].length; i++ )  {  //Start positions.
        //Skip over the first few characters to save a little time.
        for( j = 5; ( ( ( i + j ) < data[h].length ) && ( j < fnl ) ); j++ )  {
          //Test for a first name.
          nsp[0][i] |= og.isValid( data[h], i, j, fni );

          //Test for a full name.
          nsp[1][i] |= og.isValid( data[h], i, j, ni );
        }

        //Full name maximum length is greater than first name.  Scan the
        //  remainder of the full name range only checking for full names.
        for( ; ( ( ( i + j ) < data[h].length ) && ( j < nl ) ); j++ )  {
          nsp[1][i] |= og.isValid( data[h], i, j, ni );
        }
      }

      //Examine the sample in order to determine if the the first record is only
      //  a partial record.  The difference ( RL - I ) is the supposed length of
      //  the first record.  This value is changed at each iteration of the
      //  WHILE loop until the positioning of the fields found in the previous
      //  loop, corresponding to the OFFSET determined from the first sample,
      //  line up.  Upon completion the difference ( RL - I ) should equal the
      //  length of the first record.
      s = true;
      i = rl;
      while( s && ( i >= 0 ) )  {
        n = 0;
        sp = 0;  //Reuse SP as a counter variable for first names.
        ep = 0;  //Reuse EP as a counter variable for full names.
        //Divide the sample into logical records based on known record length.
        for( j = 0; j < data[h].length; j += rl )  {
          //Test the trailing boundary condition in case the last record is a
          //  partial record.
          if( ( j + offset + ( rl - i ) ) < nsp[0].length )  {
            //Count how many records have a field at the correct offset.
            if( nsp[0][( j + offset + ( rl - i ) )] )  {  //First name.
              sp++;
            }
            if( nsp[1][( j + offset + ( rl - i ) )] )  {  //Full name.
              ep++;
            }
          }

          n++;  //The number of records in the sample.
        }

        if( ep > sp )  {  //Store the larger of the two counts into SP.
          sp = ep;
        }

        //Do the fields line up based on the acceptable threshold?
        if( ( double )( ( double )sp / ( double )n ) < GP.alup )  {
          i--;  //Iterate, test the next record length.
        }
        else  {  //They line up.
          s = false;  //Set a flag to end the WHILE loop.
        }
      }

      //Using the first record's size determine the size of the last record.
      if( !s )  {  //The field's lined up.
        if( i < rl )  {  //The first record was a partial record.
          sp = ( rl - i );
        }
        else  {  //The first record was a complete record.
          sp = 0;
        }

        ep = ( data[h].length - sp );  //Subtract out the first record's size.
        while( ep > rl )  {  //Subtract any complete records.
          ep -= rl;
        }
        //The end of the last complete record is the total length minus any
        //  characters leftover from removing the first record and any other
        //  complete records.
        ep = ( data[h].length - ep );

        c = new char[( ep - sp )];
        System.arraycopy( data[h], sp, c, 0, ( ep - sp ) );
        holder[h] = splitRecords( c, rl );
      }
    }  //END WHILE( s AND ( i >= 0 ) ).

    //Attempt to consolidate the samples into a single set of records.
    n = 0;
    for( i = 0; i < holder.length; i++ )  {  //Consider each sample.
      if( holder[i] != null )  {
        n += holder[i].length;  //Count the number of records from sample I.
      }
    }

    if( n > 0 )  {
      ret = new char[n][rl];  //Allocate memory.

      n = 0;
      for( i = 0; i < holder.length; i++ )  {
        if( holder[i] != null )  {
          for( j = 0; j < holder[i].length; j++ )  {
            ret[n++] = holder[i][j];
          }
        }
      }
//Debug statement.
//for( i = 0; i < ret.length; i++ )  {
//System.out.println( new String( ret[i], 0, 27 ) );
//}
//End debug statement.
    }
    else  {
      ret = null;
    }

    return( ret );
  }  /*  private static char[][] splitSecondSample( OracleGroup, String )  */


  public static char[][] splitSecondSample( char[] rd, OracleGroup og,
                                             String fn )  {

    boolean s;
    byte[][] b;
    char[] c;
    char[][] ret = null,
             data;
    char[][][] holder;  //An array of records for each sample.
    int i,
        j,
        ep = 0,
        sp = 0;

    //Extract samples from multiple positions within the file.
    b = readSamples( ( GP.result.getRecordLength( ) + rd.length ), fn );

    //Transform the raw data into character data.
    data = new char[b.length][];  //Allocate memory.
    holder = new char[b.length][][];  //Allocate memory.
    for( i = 0; i < b.length; i++ )  {
      if( !GP.result.getIsAscii( ) )  {  //Test if the sample is ASCII.
        convertToAscii( b[i], og );  //If not, then convert.
      }

      data[i] = ( new String( b[i] ) ).toCharArray( );
    }

    for( i = 0; i < data.length; i++ )  {  //Consider each sample.
      s = true;
      j = 0;
      while( s && ( j < data[i].length ) )  {  //Find first record delimiter.
        if( ( data[i][j] == rd[0] ) && checkDelimiter( j, rd, data[i] ) )  {
          s = false;
          sp = ( j + rd.length );
        }
        else  {
          j++;
        }
      }

      if( !s )  {
        s = true;
        j = ( data[i].length - 1 );
        while( s && ( j > 0 ) )  {  //Find last record delimiter.
          if( ( data[i][j] == rd[0] ) && checkDelimiter( j, rd, data[i] ) )  {
            s = false;
            ep = j;
          }
          else  {
            j--;
          }
        }
      }

      if( !s )  {  //If the delimiters were found.
        //Only consider the portion of the sample between the first and last
        //  available record delimiter.
        c = new char[( ep - sp )];
        System.arraycopy( data[i], sp, c, 0, ( ep - sp ) );
        holder[i] = splitRecords( rd, c );  //Split the portion into records.
      }
    }

    //Count the total number of records.
    sp = 0;  //Reuse SP as a counter variable.
    for( i = 0; i < holder.length; i++ )  {
      sp += holder[i].length;
    }

    ret = new char[sp][];  //Allocate memory.

    //Transfer the records into a two dimensional table.
    sp = 0;  //Reuse SP as an index variable.
    for( i = 0; i < holder.length; i++ )  {
      for( j = 0; j < holder[i].length; j++ )  {
        ret[sp++] = holder[i][j];
      }
    }
//Debug statement.
//for( i = 0; i < ret.length; i++ )  {
//System.out.println( new String( ret[i], 0, 27 ) );
//}
//End debug statement.

    return( ret );
  }  /*  private static char[][] splitSecondSample( char[], OracleGroup,
                                                    String )  */


  public static char[][][] splitSecondSample( int rl, OracleGroup og,
                                               String fn )  {

    boolean s,
            sh = true;  //Skip header.
    byte[][] b;
    char[] c,
           rd = GP.result.getDelimiter( ).getRecordDelimiter( );
    char[][] data;
    char[][][] ret = null;
    char[][][][] holder;  //A two dimensional table of records for each sample.
    int i,
        j,
        ep = 0,
        sp = 0;

    //Extract samples from multiple positions within the file.
    b = readSamples( rl, fn );
//Debug statement.
//for( i = 0; i < b.length; i++ )  {
//System.out.println( Boolean.toString( b[i] == null ).toUpperCase( ) );
//}
//End debug statement.

    //Transform the raw data into character data.
    data = new char[b.length][];  //Allocate memory.
    holder = new char[b.length][][][];  //Allocate memory.
    for( i = 0; i < b.length; i++ )  {
      if( !GP.result.getIsAscii( ) )  {  //Test if the sample is ASCII.
        convertToAscii( b[i], og );  //If not, then convert.
      }

      data[i] = ( new String( b[i] ) ).toCharArray( );
    }

    for( i = 0; i < data.length; i++ )  {  //Consider each sample.
      s = true;
      j = 0;
      while( s && ( j < data[i].length ) )  {  //Find first record delimiter.
        if( ( data[i][j] == rd[0] ) && checkDelimiter( j, rd, data[i] ) )  {
          s = false;
          sp = ( j + rd.length );
        }
        else  {
          j++;
        }
      }

      if( !s )  {  //If an initial record delimiter was found.
        s = true;
        j = ( data[i].length - 1 );
        while( s && ( j > 0 ) )  {  //Find last record delimiter.
          if( ( data[i][j] == rd[0] ) && checkDelimiter( j, rd, data[i] ) )  {
            s = false;
            ep = j;
          }
          else  {
            j--;
          }
        }
      }

      if( !s )  {  //If the delimiters were found.
        //Only consider the portion of the sample between the first and last
        //  available record delimiter.
        c = new char[( ep - sp )];
        System.arraycopy( data[i], sp, c, 0, ( ep - sp ) );

        //Split the portion into records and fields.
        holder[i] = splitRecords( sh, c, GP.result.getDelimiter( ) );

        if( sh )  {
          sh = false;
        }
      }
    }

    //Count the total number of records.
    sp = 0;  //Reuse SP as a counter variable.
    for( i = 0; i < holder.length; i++ )  {
      sp += holder[i].length;
    }

    ret = new char[sp][][];  //Allocate memory.

    //Transfer the records into a two dimensional table.
    sp = 0;  //Reuse SP as an index variable.
    for( i = 0; i < holder.length; i++ )  {
      for( j = 0; j < holder[i].length; j++ )  {
        ret[sp++] = holder[i][j];
      }
    }
//Debug statement.
//for( i = 0; i < ret.length; i++ )  {
//System.out.println( ret[i][0] );
//}
//End debug statement.

    return( ret );
  }  /*  private static char[][][] splitSecondSample( int, OracleGroup,
                                                      String )  */


  public static void quickSort( int p, int r, int[] arr )  {
    int q;

    if( p < r )  {
      q = partition( p, r, arr );
      quickSort( p, ( q - 1 ), arr );
      quickSort( ( q + 1 ), r, arr );
    }
  }  /*  private static quickSort( int, int, int[] )  */


  /**
   *   This method checks for DELIMITER within DATA from STARTPOSITION to
   * ( STARTPOSITION + DELIMITER.LENGTH ).
   * 
   * @return - TRUE if the delimiter was found, else the method returns FALSE if
   *           either the parameters were invalid or DELIMITER was not found as
   *           previously specified.
   */
  public static boolean checkDelimiter( int startPosition, char[] delimiter,
                                        char[] data )  {

    boolean ret = true;
    int i = 0;

    if( ( startPosition >= 0 ) && ( delimiter != null ) && ( data != null ) )  {
      while( ret && ( i < delimiter.length ) &&
             ( ( startPosition + i ) < data.length ) )  {

        if( data[( startPosition + i )] != delimiter[i++] )  {
          ret = false;
        }
      }
    }
    else  {
      ret = false;  //Any of the necessary method parameters were invalid.
    }

    return( ret );
  }  /*  public static boolean checkDelimiter( int, char[], char[] )  */


  /**
   *   Extracts the first GP.hrts records from the sample returning the
   * extraction as a three-dimensional array.
   */
  public static char[][][] extractHeader( char[] data, Delimiter d )  {
    boolean b;
    char tq;  //The text quote character found.
    char[] fd = d.getFieldDelimiter( ),
           rd = d.getRecordDelimiter( );
    char[][][] ret = null;
    int e,
        i = 0,
        j = 0,
        s = 0,
        rdc = 0;  //Record delimiter count.
    int[] rdp = new int[GP.hrts];  //Record delimiter positions.
    int[][] fdp = new int[GP.hrts][];  //Field delimiter positions.
    ArrayList<Integer> holder = new ArrayList<Integer>( );

    while( ( i < data.length ) && ( rdc < GP.hrts ) )  {
      if( ( data[i] == '"' ) || ( data[i] == '\'' ) )  {
        tq = data[i++];

        //!!ASSUMPTION!! - An opening text quote will be followed by a closing
        //  text quote.
        while( ( i < data.length ) && ( data[i++] != tq ) );
      }
      else if( ( data[i] == rd[0] ) && checkDelimiter( i, rd, data ) )  {
        fdp[rdc] = new int[holder.size( ) + 1];  //Allocate storage.

        for( j = 0; j < holder.size( ); j++ )  {
          fdp[rdc][j] = holder.get( j );
        }
        fdp[rdc][j] = i;

        rdp[rdc++] = i;

        holder.clear( );
        i += rd.length;
      }
      else if( ( data[i] == fd[0] ) && checkDelimiter( i, fd, data ) )  {
        holder.add( i );

        i += fd.length;
      }
      else  {
        i++;
      }
    }

    ret = new char[GP.hrts][][];

    for( i = 0; i < GP.hrts; i++ )  {
      ret[i] = new char[fdp[i].length][];

      for( j = 0; j < ret[i].length; j++ )  {
        //!!ASSUMPTION!! - An opening text quote will be followed by a closing
        //  text quote.
        if( ( data[s] == '"' ) || ( data[s] == '\'' ) )  {
          b = true;
          s++;
          e = ( fdp[i][j] - 1 );
        }
        else  {
          b = false;
          e = fdp[i][j];
        }

        //Allocate memory to store the data associated with the current field.
        ret[i][j] = new char[( e - s )];

        //Copy the individual record data into the two dimensional data.
        System.arraycopy( data, s, ret[i][j], 0, ret[i][j].length );

        //Skip over the field and its delimiter to the start of the next field.
        s += ( ret[i][j].length + fd.length );

        //If the field contained text quotes skip past the closing quote.
        if( b )  {
          s++;
        }
      }
    }

    return( ret );
  }  /*  public static char[][][] extractHeader( char[], Delimiter )  */


  /**
   *   Splits a one dimensional array of data into a two dimensional array of
   * records based on a record length.  Used by fully fixed files.
   * 
   * @param RL is the character sequence of the record delimiter.
   * @param ARRAY is the record data to be split.
   */
  public static char[][] splitRecords( char[] array, int rl )  {
    char[][] ret;
    int i,
        j;

    ret = new char[( array.length / rl )][rl];
    for( i = 0; i < ret.length; i++ )  {
      for( j = 0; j < rl; j++ )  {
        ret[i][j] = array[( ( i * rl ) + j )];
      }
    }

    return( ret );
  }  /*  private static char[][] splitRecords( char[], int )  */


  /**
   *   Splits a one dimensional array of data into a two dimensional array of
   * records based on a record delimiter.  Used by hybrid files, fixed files
   * with a record delimiter.
   * 
   * @param RD is the character sequence of the record delimiter.
   * @param ARRAY is the record data to be split.
   */
  public static char[][] splitRecords( char[] rd, char[] array )  {
    char[][] ret;
    int i,
        j = 0;
    ArrayList<Integer> p = new ArrayList<Integer>( );

    for( i = 0; i < array.length; i++ )  {
      //When a record delimiter is encountered store the start position for
      //  future reference.
      if( array[i] == rd[0] )  {
        if( checkDelimiter( i, rd, array ) )  {
          p.add( i );
        }
      }
    }

    ret = new char[p.size( )][];
    for( i = 0; i < ret.length; i++ )  {
      ret[i] = new char[( p.get( i ) - j )];  //Allocate memory for this record.

      //Copy the individual record data into the two dimensional array.
      System.arraycopy( array, j, ret[i], 0, ret[i].length );

      //Skip over the record and its delimiter to the start of the next record.
      j += ( ret[i].length + rd.length );
    }

    return( ret );
  }  /*  private static char[][] splitRecords( char[], char[] )  */


  /**
   *   Splits a one dimensional array of data into a three dimensional array of
   * records and fields based on the record and field delimiters respectively.
   * Used by fully delimited files.
   *   The logic for this method is slightly more complex than that required for
   * the other file types because of the possibility of text quotes surrounding
   * string literals.
   *
   * @param SKIPHEADER is a flag indicating whether or not to skip GP.HRTS
   *        initial records to avoid any potential header information.
   * @param ARRAY is the character data to be split.
   * @param D is an instance of a Delimiter object that contains the field and
   *        record delimiters.
   * @return A three dimensional array of the data divided into records ( first
   *         dimension ), fields ( second dimension ), and the corresponding
   *         character data associated with a field and record ( third
   *         dimension ).
   */
  public static char[][][] splitRecords( boolean skipHeader, char[] array,
                                         Delimiter d )  {

    boolean b = false;
    char tq;  //The text quote used to designate a string literal.
    char[] fd = d.getFieldDelimiter( ),
           rd = d.getRecordDelimiter( );
    char[][][] ret;
    int h,  //A temporary, holder variable.
        i = 0,
        j,
        k = 0,
        cp,
        sp = 0,
        end;
    ArrayList<Integer> pfd,  //Field delimiter position.
                       prd = new ArrayList<Integer>( );  //Record delimiter.

    //Skip over any possible header information.
    if( skipHeader )  {
      while( !b && ( sp < array.length ) )  {
        if( array[sp] == rd[0] )  {
          if( checkDelimiter( sp, rd, array ) )  {
            b = ( ++i == GP.hrts );
            sp += rd.length;
          }
          else  {
            sp++;
          }
        }
        else  {
          sp++;
        }
      }
    }

    for( i = sp; i < array.length; i++ )  {
      //When a record delimiter is encountered store the start position for
      //  future reference.
      if( array[i] == rd[0] )  {
        if( checkDelimiter( i, rd, array ) )  {
          prd.add( i );
        }
      }
    }

    b = false;
    k = sp;
    ret = new char[prd.size( )][][];  //Allocate memory for the records.

    for( i = 0; i < ret.length; i++ )  {
      pfd = new ArrayList<Integer>( );

      h = k;
      cp = prd.get( i );  //The position of the next record delimiter.
      //Only check for field delimiters from the start of the record up to the
      //  beginning of the record delimiter.
      while( ( h < array.length ) && ( h < cp ) )  {
        if( array[h] == fd[0] )  {
          if( checkDelimiter( h, fd, array ) )  {

            pfd.add( h );
          }
        }
        //!!ASSUMPTION!! - An opening text quote will be followed by a closing
        //  text quote.
        else if( ( array[h] == '"' ) || ( array[h] == '\'' ) )  {
          tq = array[h++];
          while( ( h < array.length ) && ( h < cp ) && ( array[h] != tq ) )  {
            h++;
          }
        }

        h++;  //Increment to the next character.
      }
      //The record delimiter indicates the end of the last field.
      pfd.add( prd.get( i ) );

      //Allocate memory for the number of fields in this particular record.
      ret[i] = new char[pfd.size( )][];

      for( j = 0; j < ret[i].length; j++ )  {
        //!!ASSUMPTION!! - An opening text quote will be followed by a closing
        //  text quote.
        if( ( array[k] == '"' ) || ( array[k] == '\'' ) )  {
          b = true;
          k++;
          end = ( pfd.get( j ) - 1 );
        }
        else  {
          b = false;
          end = pfd.get( j );
        }

        //Allocate memory to store the data associated with the current field.
        ret[i][j] = new char[( end - k )];

        //Copy the individual record data into the two dimensional array.
        System.arraycopy( array, k, ret[i][j], 0, ret[i][j].length );

        //Skip over the record and its delimiter to the start of the next record.
        k += ( ret[i][j].length + fd.length );

        //If the field contained text quotes skip past the closing quote.
        if( b )  {
          k++;
        }
      }

      //Back up one field delimiter and increment past the record delimiter.
      k += ( rd.length - fd.length );
    }

    return( ret );
  }  /*  private static char[][] splitRecords( char[], char[] )  */


  /**
   *   If there was a record delimiter skip the first RECORDSTOSKIP records to
   * ignore possible header information.  !Assumption! - header information will
   * be delimited by record delimiters.
   */
  public static int startPosition( char[] recordDelimiter, char[] fileSample,
                                   int recordsToSkip )  {

    int c = 0,
        ret = 0;

    while( ( c < recordsToSkip ) && ( ret < fileSample.length ) )  {
      if( fileSample[ret] == recordDelimiter[0] )  {
        //Found record delimiter.
        if( checkDelimiter( ret, recordDelimiter, fileSample ) )  {
          c++;  //Increment the record delimiter count.

          ret += recordDelimiter.length;
        }
      }
      else  {
        ret++;
      }
    }

    return( ret );
  }


  public static byte[] readFilePortion( int size, String fileName )  {
    byte[] holder,
           ret = null;
    int fs,  //File size.
        rc = 0;  //Read count.
    File file;
    DataInputStream inf;

    try  {
      inf = new DataInputStream( new FileInputStream( fileName ) );

      if( size < 0 )  {
        file = new File( fileName );
        fs = ( int )file.length( );
        file = null;

        holder = new byte[fs];
      }
      else  {
        holder = new byte[size];
      }

      rc = inf.read( holder );
      if( ( rc > 0 ) && ( rc < holder.length ) )  {
        ret = new byte[rc];
        System.arraycopy( holder, 0, ret, 0, rc );
      }
      else  {
        ret = holder;
      }

      //Java stores bytes as four byte integers with the first three bytes set
      //  to 0xFF.  This loop alters how the value is stored by changing the
      //  values stored in the first three bytes to 0x00.
      for( fs = 0; fs < ret.length; fs++ )  {
        ret[fs] = ( byte )( ret[fs] & 0xFF );
      }

      inf.close( );
    }
    catch( IOException e )  {
      //Currently do not report an error and exit, just return a NULL reference.
//      System.out.println( "readFilePortion - BasicAttributes error 2: " +
//                          "IOException.  " + e.getMessage( ) );

//      System.exit( 1 );
    }

//Debug statement.
//if( ret == null )  {
//System.out.println( "RET is NULL." );
//}
//else  {
//System.out.println( "RET is not NULL." );
//}
//End debug statement.
    return( ret );
  }  /*  private static byte[] readFilePortion( String )  */


  public static String extractFileName( boolean removeExtension,
                                        String filePath )  {

    int i;
    String ret;
    String[] holder = null;

    if( filePath.indexOf( '/' ) >= 0 )  {
      holder = filePath.split( "/" );
    }
    else  {
      holder = filePath.split( "\\\\" );
    }

    if( removeExtension )  {
      holder = holder[( holder.length - 1 )].split( "[.]" );
      ret = "";

      for( i = 0; i < ( holder.length - 2 ); i++ )  {
        ret = ret.concat( holder[i].concat( "." ) );
      }
      ret = ret.concat( holder[i] );
    }
    else  {
      ret = holder[( holder.length - 1 )];
    }

    return( ret );
  }  /*  public static String extractFileName( boolean, String )  */


  public static String encoding( byte[] data, OracleGroup og )  {
    boolean flag;
    boolean[] bv = new boolean[4];  //Boolean vector.
    char[] c;  //A holder for the sample.
    int i,
        j,
        k,
        cc,  //Character count.
        fc = 0,  //False count.
        tc = 0,  //True count.
        bitmask = 0x80,
        bytemask = 0xFF;
//Debug statement.
//int pc = 0;
//End debug statement.
    int[] nbl = new int[5];  //Null byte grouping length.
    String ret = "";

    for( i = 0; i < nbl.length; i++ )  {
      nbl[i] = 0;
    }

//Debug statement.
//for( pc = 0; pc < 15; pc++ )  {
//System.out.println( Integer.toBinaryString(data[pc]&bytemask) + "  --  " + Integer.toHexString(data[pc]&bytemask) + "  --  " + data[pc] );
//}
//pc = 0;
//System.out.println( Integer.toBinaryString(data[0]&bytemask) + "-" + Integer.toBinaryString(data[1]&bytemask) + "-" + Integer.toBinaryString(data[2]&bytemask) + "-" + Integer.toBinaryString(data[3]&bytemask) );
//System.out.println( Integer.toBinaryString(data[0]) + "|" + Integer.toBinaryString( 0xFF ) + "|" + Integer.toBinaryString(data[0]&0xFF) );
//System.out.println( "---" );
//System.out.println( "---" );
//End debug statement.
    //Evidence for determining UTF-32 encoding is the presences of 1, 2, or 3
    //  null bytes preceding a non-null byte.  If all null bytes fall within a
    //  grouping of one of these lengths then the sample is probably UTF-32.
    //  It is possible that the null character, indicated by '0x00 0x00 0x00
    //  0x00' in UTF-32, is a filler character.
    i = 0;
    for( i = 0; i < data.length; i++ )  {
      if( ( k = ( i + 4 ) ) < data.length )  {
        for( j = i; j < k; j++ )  {
          if( ( data[j] & bytemask ) == 0x00 )  {  //A null byte.
            bv[0] = true;

            if( bv[1] )  {  //A non-null byte was already encountered.
              bv[2] = true;  //Indicative of non UTF-32 encoding.
            }
          }
          else  {  // A non-null byte.
            bv[1] = true;
          }
        }
      }

      //If (0x00)+(!0x00)+.  If one or more null bytes were followed by at least
      //  one non-null byte with the condition that once a non-null byte is
      //  encountered no more null bytes may be seen within a word of data.
      if( bv[0] && bv[1] && !bv[2] )  {
        tc++;
      }
      else if( bv[2] )  {
        fc++;
      }
    }

    //The 'true count' must be a large amount of the sample.  This will ensure
    //  that most of the data words contain one or more null bytes as most valid
    //  characters will conform to this property.  Also the number of data words
    //  with a non-leading null byte must be very small as this is probably
    //  indicative of UTF-16.
    if( ( ( ( double )tc / ( double )data.length ) > 0.50 ) &&
        ( ( ( double )fc / ( double )data.length ) < 0.05 ) )  {

      ret = "UTF-32";
    }
    //If there exists a very high percentage of data words with a non-leading
    //  null byte, then the sample is probably UTF-16.
    else if( ( ( double )fc / ( double )data.length ) > 0.85 )  {
      ret = "UTF-16";
    }

    if( ret.equals( "" ) )  {  //Test for ASCII.
      tc = 0;

      //Count the number of bytes for which the most significant bit is not set.
      for( i = 0; i < data.length; i++ )  {
        if( ( data[i] & bitmask ) == 0 )  {
          tc++;
        }
      }

      //If a large majority of the bytes do not have the most significant bit
      //  set then the sample is probably ASCII.
      if( ( double )( ( double )tc / ( double )data.length ) > 0.95 )  {
        ret = "ASCII";
      }
    }

    if( ret.equals( "" ) )  {  //Test for UTF-8, not ASCII.
      if( ( ( data[0] & bytemask ) == 0xEF ) &&
          ( ( data[1] & bytemask ) == 0xBB ) &&
          ( ( data[2] & bytemask ) == 0xBF ) )  {  //Test for a byte order mark.

        i = 3;
      }
      else  {
        i = 0;
      }

      cc = 0;
      fc = 0;
      tc = 0;
//Debug statement.
//System.out.println( Integer.toBinaryString( bitmask ) );
//End debug statement.
      while( i < data.length )  {
        if( ( data[i] & 0x80 ) == 0x00 )  {
          tc++;
        }
        else  {
//Debug statement.
//if( pc < 10 )  {
//System.out.println( "I=" + i + ";  " + Integer.toBinaryString( data[i] & bytemask ) + " -- " + Integer.toBinaryString( data[i + 1] & bytemask ) + " -- " + Integer.toBinaryString( data[i + 2] & bytemask ) + " -- " + Integer.toBinaryString( data[i + 3] & bytemask ) );
//}
//End debug statement.
          j = 0;
          if( ( data[i] & 0xE0 ) == 0xC0 )  {  //Second byte is used.
            j = 1;
          }
          else if( ( data[i] & 0xF0 ) == 0xE0 )  {  //Third byte is used.
            j = 2;
          }
          else if( ( data[i] & 0xF8 ) == 0xF0 )  {  //Fourth byte is used.
            j = 3;
          }

          //Using the count from the previous test each following byte.
          flag = true;
          if( j > 0 )  {
            k = 0;
            while( flag && ( k < j ) && ( k < data.length ) )  {
//Debug statement.
//if( pc < 10 )  {
//System.out.print( Integer.toBinaryString( data[( i + k + 1 )] & bytemask ) + " -- " + Integer.toBinaryString( data[( i + k + 1 )] & 0xC0 ) );
//}
//End debug statement.
              flag = ( ( data[( ( i + 1 ) + k )] & 0xC0 ) == bitmask );
              k++;
            }

//Debug statement.
//if( pc < 10 )  {
//System.out.println( "" );
//}
//End debug statement.
          }

          if( flag && ( j > 0 ) )  {
//Debug statement.
//if( pc < 10 )  {
//System.out.println( "J=" + j );
//}
//End debug statement.
            tc++;
            i += j;
          }
          else  {
//Debug statement.
//if( pc < 10 )  {
//System.out.println( "I=" + i + ";  " + Integer.toBinaryString( data[i] & bytemask ) + " -- " + Integer.toBinaryString( data[i + 1] & bytemask ) + " -- " + Integer.toBinaryString( data[i + 2] & bytemask ) + " -- " + Integer.toBinaryString( data[i + 3] & bytemask ) );
//System.out.println( Integer.toBinaryString( data[i] & 0xFF ) + ";  J=" + j );
//System.out.println( "" );
//pc++;
//}
//End debug statement.
            fc++;
          }

//Debug statement.
//if( pc < 10 )  {
//System.out.println( data[i] + "|" + data[i + 1] + "|" + data[i+2] + "|" + data[i+3] );
//pc++;
//}
//End debug statement.
        }

        cc++;
        i++;
      }

//Debug statement.
//System.out.println( "DATA=" + cc + ";  TC=" + tc + ";  " + ( ( double )tc / ( double )data.length ) + ";  FC=" + fc + ";  " + ( ( double )fc / ( double )data.length ) );
//End debug statement.
      if( ( ( ( double )tc / ( double )cc ) > 0.85 ) &&
          ( ( ( double )fc / ( double )cc ) < 0.05 ) )  {

//Debug statement.
//System.out.println( "TC=" + tc + ";  FC=" + fc + ";  DATA=" + cc );
//End debug statement.
        ret = "UTF-8";
      }
    }

    if( ret.equals( "" ) )  {  //Test for EBCDIC.
      c = convertToAscii( data, og );  //Convert the sample to ASCII.

      //Count the number of valid and invalid characters.  Valid is in the range
      //  ' ' to '~' or 32 to 127.
      for( i = 0; i < c.length; i++ )  {
        if( ( c[i] >= 0x20 ) && ( c[i] <= 0x7E ) )  {
          tc++;
        }
        else  {
          fc++;
        }
      }

      if( ( ( ( double )tc / ( double )data.length ) > 0.95 ) )  {
        ret = "EBCDIC";
      }
    }

    if( ret.equals( "" ) )  {  //Otherwise assume UTF-16.
      ret = "UTF-16";
    }

//OLD UTF-32
//    i = 0;
//    while( i < data.length )  {
//      j = ( i + 1 );
//
//      if( data[i] == 0x00 )  {  //The null byte.
//        while( ( j < data.length ) && ( data[j] == 0x00 ) )  {
//          j++;
//        }
//
//        c = ( j - i );
//        if( c < 4 )  {  //Probably padding for UTF-32 encoding.
//          nbl[( c - 1 )]++;
//        }
//        //If the length of a grouping is exactly divisible by four then consider
//        //  the grouping to be a sequence of null bytes.  !!Assumes that if the
//        //  character encoding is not UTF-32 then at least some of the filler
//        //  sequences will not be divisible by four!!
//        else if( ( c % 4 ) == 0 )  {
//          nbl[3] = ( c / 4 );  //The number of null characters.
//        }
//        else  {  //Any other null grouping of length > five is probably filler.
//          nbl[( nbl.length - 1 )]++;
//        }
//      }
//
//      i = j;
//    }
//
//    //If the null character is used as filler or the number of null characters
//    //  in a grouping of length 1, 2, or 3 is statistically significant then
//    //  assume UTF-32.
//    if( ( ( ( double )nbl[( nbl.length - 1 )] /
//              ( double )data.length ) > 0.07 ) ||
//        ( ( ( double )( nbl[0] + nbl[1] + nbl[2] ) /
//              ( double )data.length ) < 0.70 ) )  {
//
//      ret = "UTF-32";
//    }

    return( ret );
  }  /*  public static String encoding( byte[], OracleGroup )  */


  public static boolean isEbcdic( byte[] data )  {
    int i,
        ac = 0,  //Potential ASCII character count.
        ec = 0;  //Potential EBCDIC character count.

    for( i = 0; i < data.length; i++ )  {
      if( ( data[i] >= 0x00 ) && ( data[i] <= 0x7F ) )  {
        ac++;  //Could be ASCII or EBCDIC, assume ASCII.
      }
      else  {  //Only EBCDIC because ASCII only uses the first seven bits.
        ec++;
      }
    }

    return( ( ( double )ec / ( double )( ec + ac ) ) > GP.ebcdic );
  }  /*  public static boolean isEBCDIC( char[] )  */


  public static char[] convertToAscii( byte[] data, OracleGroup og )  {
    byte[] ct;  //The table used for conversions.
    char[] ret = null;
    int i,
        index;
    DataInputStream inf;

    try  {
      ct = new byte[og.fileLineCount( "EBCDICtoASCII" )];

      //A file of the ASCII characters that correspond to the list of EBCDIC
      //  characters ordered by character encoding value.
      inf = new DataInputStream(
              bean.LayoutBean.class.getResourceAsStream(
                "/data/EBCDICtoASCII.dat" ) );

      i = inf.read( ct );

      inf.close( );

      if( i == ct.length )  {
        for( i = 0; i < data.length; i++ )  {
          if( data[i] < 0 )  {
            index = ( 0xff + ( data[i] + 1 ) );
          }
          else  {
            index = data[i];
          }

          data[i] = ct[index];
        }
      }

      ret = ( new String( data ) ).toCharArray( );
    }
    catch( IOException e )  {
      System.out.println( "convertToAscii - BasicAttributes error 3: " +
                          "IOException.  " + e.getMessage( ) );
      System.exit( 1 );
    }
    catch( NumberFormatException e )  {
      System.out.println( "convertToAscii - BasicAttributes error 4: " +
                          "NumberFormatException.  " + e.getMessage( ) );
      System.exit( 1 );
    }

    return( ret );
  }  /*  public static char[] convertToAscii( byte[], NameCountPair )  */


  public static Delimiter delimited( char[] data )  {
    boolean b;
    int i,
        j,
        fmi = 0;  //First max index.
    //A count for each possible field and record delimiter, respectively.
    int[] cfd,
          crd,
          ctd = null;
    Delimiter holder = new Delimiter( ),
              ret = null;

    if( GP.rd != null )  {  //First find record delimiters if they exist.
      crd = new int[GP.rd.length];
      for( i = 0; i < crd.length; i++ )  {
        crd[i] = 0;
      }
//Debug statement.
//System.out.println( "1" );
//End debug statement.
      //The logic of this loop assumes that delimiters are sorted by length,
      //  from longest to shortest.  That way when a delimiter such as
      //  { 0x0d, 0x0a } is encountered it will not be viewed as the delimiter
      //  { 0x0d }.
      i = 0;
      while( i < data.length )  {  //Sequence through the data.
        b = false;
        j = 0;
        while( !b && ( j < GP.rd.length ) )  {  //Test each delimiter.
          if( GP.rd[j][0] != data[i] )  {  //Test the first character only.
            j++;
          }
          else if( !( b = checkDelimiter( i, GP.rd[j], data ) ) )  {
            j++;
          }
        }

        if( b )  {  //A delimiter was found.
          i += GP.rd[j].length;
          crd[j]++;//  Increment the corresponding count.
        }
        else  {
          i++;
        }
      }

//Debug statement.
//System.out.println( "2" );
//End debug statement.
      //Find which record delimiter occurred most frequently.
      fmi = 0;
      for( i = 1; i < crd.length; i++ )  {
        if( crd[i] > crd[fmi] )  {
          fmi = i;
        }
      }

//Debug statement.
//System.out.println( "CRD[FMI] = " + crd[fmi] + ";  THRESHOLD = " + ( GP.readSize * 0.0001 ) + "." );
//End debug statement.
      //I picked an arbitrary error size based on the read size.  The idea is to
      //  ignore what might be error data.
      if( crd[fmi] > ( GP.sampleSize * 0.0001 ) )  {
        holder.setRecordDelimiter( GP.rd[fmi] );
      }
    }  //IF( GP.rd != NULL ).


    if( GP.fd != null )  {  //Next look for field delimiters.
      cfd = new int[GP.fd.length];
      for( i = 0; i < cfd.length; i++ )  {
        cfd[i] = 0;
      }

      if( GP.td != null )  {
        ctd = new int[GP.td.length];
        for( i = 0; i < ctd.length; i++ )  {
          ctd[i] = 0;
        }
      }

      //Skip any potential header records.
      i = 0;
      if( holder.validRecordDelimiter( ) )  {
        i = startPosition( holder.getRecordDelimiter( ), data, GP.hrts );
      }

//Debug statement.
//System.out.println( "3" );
//End debug statement.
      //The logic of this loop assumes that delimiters are sorted by length,
      //  from longest to shortest.  That way when a delimiter such as
      //  { 0x0d, 0x0a } is encountered it will not be viewed as the delimiter
      //  { 0x0d }.
      while( i < data.length )  {  //Sequence through the data.
        if( GP.td != null )  {  //First test for a text delimiter.
          b = false;
          j = 0;
          //Sequence through the list of text delimiters.
          while( !b && ( j < GP.td.length ) )  {
            if( GP.td[j][0] != data[i] )  {  //Test the first character only.
              j++;
            }
            //Check the whole delimiter.
            else if( !( b = checkDelimiter( i, GP.td[j], data ) ) )  {
              j++;
            }
          }

          if( b )  {  //Was an opening text delimiter found?
            ctd[j]++;  //Increment the corresponding count.

            //Skip over the string literal, !!assumtion!! an opening delimiter
            //  has a corresponding closing delimiter.
            b = false;
            i += GP.td[j].length;
            while( !b && ( i < data.length ) )  {
              if( GP.td[j][0] != data[i] )  {
                i++;
              }
              else if( !( b = checkDelimiter( i, GP.td[j], data ) ) )  {
                i++;
              }
            }

            if( b )  {
              i += GP.td.length;
            }
          }  //IF( a ), an opening text delimiter was found.
        }  //IF( GP.td != NULL ).

        if( i < data.length )  {
          b = false;
          j = 0;
          while( !b && ( j < GP.fd.length ) )  {  //Test each delimiter.
            if( GP.fd[j][0] != data[i] )  {  //Test the first character only.
              j++;
            }
            //Check the whole delimiter.
            else if( !( b = checkDelimiter( i, GP.fd[j], data ) ) )  {
              j++;
            }
          }

          if( b )  {  //A delimiter was found.
            i += GP.fd.length;
            cfd[j]++;  //Increment the corresponding count.
          }
          else  {
            i++;
          }
        }
      }  //WHILE( i < DATA.length ), sequence through the data sample.
//Debug statement.
//System.out.println( "4" );
//End debug statement.

      //Find which record delimiter occurred most frequently.
      fmi = 0;
      for( i = 1; i < cfd.length; i++ )  {
        if( cfd[i] > cfd[fmi] )  {
          fmi = i;
        }
      }

      //This !!arbitrary!! frequency percentage is to take into account possible
      //  errors in the data.
      if( cfd[fmi] > ( GP.sampleSize * 0.01 ) )  {
        holder.setFieldDelimiter( GP.fd[fmi] );
      }

      //Find which text delimiter occurred most frequently.
      if( cfd != null )  {
        fmi = 0;
        for( i = 1; i < ctd.length; i++ )  {
          if( ctd[i] > ctd[fmi] )  {
            fmi = i;
          }
        }

        //This !!arbitrary!! frequency percentage is to take into account
        //  possible errors in the data.
        if( cfd[fmi] > ( GP.sampleSize * 0.001 ) )  {
          holder.setTextDelimiter( GP.td[fmi] );
        }
      }
    }  //IF( GP.fd != NULL ).

    if( holder.validRecordDelimiter( ) || holder.validFieldDelimiter( ) )  {
      ret = holder;
    }
//Debug statement.
//System.out.println( "5" );
//End debug statement.

    return( ret );
  }  /*  public static Delimiter delimited( char[] )  */


  /**
   *   This method first skips the first five records to avoid any potential
   * header information.  Then it runs through the next X records counting the
   * number of fields for each record.  It returns the maximum count of fields
   * for all the examined records.
   */
/*
  public static int fieldCount( char[] fileSample, Delimiter delimiter )  {

    char[] fd = delimiter.getFieldDelimiter( ),
           rd = delimiter.getRecordDelimiter( );
    int c = 0,  //Used to count the number of records encountered so far.
        //The index of the current character in FILESAMPLE.
        i = startPosition( rd, fileSample, GP.hrts );
    int[] counts = new int[25];  //The field count for each sampled record.

    c = 0;
    while( ( c < counts.length ) && ( i < fileSample.length ) )  {
      if( fileSample[i] == rd[0] )  {
        //If a record delimiter is encountered increment the record count.
        if( checkDelimiter( i, rd, fileSample ) )  {
          //There is one fewer field delimiters than fields, so upon
          //  encountering the record delimiter increment the field count.
          //  Also, increment the record count.
          counts[c++]++;

          i += rd.length;
        }
      }
      else if( fileSample[i] == fd[0] )  {
        if( checkDelimiter( i, fd, fileSample ) )  {
          counts[c]++;  //Increment the number of fields for this record.

          i += fd.length;
        }

        //Skip over any field delimiters butted up next to each other.  In
        //  essence skip any whitespace counting the ( delimiter + whitespace )
        //  as a single delimiter.
      }
      //Encountered a text string delimited by " or '.
      else if( ( fileSample[i] == '"' ) || ( fileSample[i] == '\'' ) )  {
        //Ignore the string, skipping over any characters until the closing " or
        //  ' is encountered.
        while( ( i < fileSample.length ) &&
               !( ( fileSample[i] == '"' ) || ( fileSample[i] == '\'' ) ) )  {

          i++;
        }
        i++;  //Skip over the ending string delimiter.

        //Skip over the ending string delimiter and the next field delimiter.
      }
      else  {
        i++;  //Move to the next character.
      }
    }

    c = 0;
    for( i = 1; i < counts.length; i++ )  {
      if( counts[i] > counts[c] )  {
        c = i;
      }
    }

    return( counts[c] );
  }  /*  public static int fieldCount( char[], Delimiter )  */


  /**
   *   At this point in the code it is still unknown exactly how long a record
   * is, specifically this applies to the case of fixed length files without
   * record delimiters, fully fixed files.
   * 
   * @return The length of a record if found, else -1.
   */
  public static int recordLength( char[] fileSample, OracleGroup og )  {
    //A name occurred at a corresponding start position.
    boolean [][] nsp = new boolean[2][fileSample.length];
    int i,
        j,
        k,
        m,
        bl = 100,  //The beginning record length to start with.
        el = 1200,  //The ending record length to stop with.
        ni = og.index( "full name" ),  //Index of the full name oracle.
        nl = og.getMaxLength( ni ),  //Maximum length to scan for full name.
        rc,  //Record count for a corresponding record length.
        fni = og.index( "first name" ),  //Index of the first name oracle.
        fnl = og.getMaxLength( fni ),  //Maximum length to scan for first name.
        len = fileSample.length,
        ret = -1;
    int[][] c;
    double d;
    double[] mp = { -1.0, -1.0 };  //First and full maximum percentage.

    //Scan the complete FILESAMPLE once for name potential field locations.
    for( i = 0; i < len; i++ )  {  //Start positions.
      //Skip over the first few characters to save a little time.
      for( j = 5; ( ( ( i + j ) < len ) && ( j < fnl ) ); j++ )  {  //Lengths.
        //Test for a first name.
        nsp[0][i] |= og.isValid( fileSample, i, j, fni );

        //Test for a full name.
        nsp[1][i] |= og.isValid( fileSample, i, j, ni );
      }

      //Full name maximum length is greater than first name.  Scan the remainder
      //  of the full name range only checking for full names.
      for( ; ( ( ( i + j ) < len ) && ( j < nl ) ); j++ )  {
        nsp[1][i] |= og.isValid( fileSample, i, j, ni );
      }
    }

    i = bl;
    //I is the current record length being examined.  Loop ends as soon as an
    //  acceptable percentage is found ( the fields line up ).
    while( ( i < el ) && ( mp[0] < GP.alup ) && ( mp[1] < GP.alup ) )  {
      rc = ( int )Math.floor( len / i );

      //C will store where names occur based on the current record length being
      //  examined.  Results must be tallied for both first and full names.
      c = null;
      c = new int[2][i];

      for( m = 0; m < 2; m++ )  {  //Look at each name type.
        for( j = 0; j < i; j++ )  {  //Look at each starting position.
          for( k = 0; k < rc; k++ )  {  //Look at each record.
            if( nsp[m][( j + ( i * k ) )] )  {
              c[m][j]++;
            }
          }
        }
      }

      for( m = 0; m < 2; m++ )  {  //Look at each name type.
        for( j = 0; j < i; j++ )  {  //Look at each starting position.
          if( c[m][j] > 0 )  {  //If a name started at position J.
            //If the percentage of names that started at position J is greater
            //  than the percentages associated with any other J so far.
            if( ( d = ( c[m][j] / ( double )rc ) ) > mp[m] )  {
              mp[m] = d;  //Set the current highest percentage.
            }
          }
        }
      }

      i++;
//Debug statement.
//if( ( i > 345 ) && ( i < 360 ) )  {
//System.out.println( "First name: " + Double.toString( mp[0] ) +
//                    System.getProperty( "line.separator" ) +
//                    "Full name:  " + Double.toString( mp[1] ) );
//}
//End debug statement.
    }

    if( ( mp[0] > GP.alup ) || ( mp[1] > GP.alup ) )  {
      ret = ( i - 1 );
    }

//Debug statement.
//System.out.println( "Exiting..." );
//System.exit( 1 );
//End debug statement.
    return( ret );
  }  /*  public static int recordLength( char[], OracleGroup )  */


  /**
   *   Determines the length, in bytes, of the records within a hybrid file.
   * Currently returns the record length that occurs most often.
   */
  public static int recordLength( char[][] records )  {
    int i;
    //Ignore the last record as it may be incomplete ( cut off ).
    int[] len = new int[( records.length - 1 )];

    for( i = 0; i < len.length; i++ )  {
      len[i] = records[i].length;
    }

    quickSort( 0, ( len.length - 1 ), len );  //Sort the lengths.

    return( mostFrequent( len ) );
  }  /*  public static int recordLength( char[][] )  */


  /**
   *   Determines the length, in fields, of the records within a fully delimited
   * file.  Currently returns the record length that occurs most often.
   */
  public static int recordLength( char[][][] records )  {
    int i;
    //Ignore the last record as it may be incomplete ( cut off ).
    int[] len = new int[( records.length - 1 )];

    for( i = 0; i < len.length; i++ )  {
      len[i] = records[i].length;
    }

    quickSort( 0, ( len.length - 1 ), len );

    return( mostFrequent( len ) );
  }  /*  public static int recordLength( char[][][] )  */


  public static int maxRecordLength( char[][][] records )  {
    int i,
        ret = Integer.MIN_VALUE;

    for( i = 0; i < records.length; i++ )  {
      if( records[i].length > ret )  {
        ret = records[i].length;
      }
    }

    return( ret );
  }  /*  public static int maxRecordLength( char[][][] )  */


  public static String buildLayout( byte[] fileSample, OracleGroup og,
                                    String fileName )  {

    char[] fs;  //File sampling.
    char[][] r = null;  //The file sample split into records.
    char[][][] rf = null,  //The file sample split into records and fields.
               header = null;  //The header data.
    int rl;  //The record length.
    long st = System.currentTimeMillis( );
    RecordEnumeration re = null;
    Delimiter d;
    FieldDescriptor[][] fd = null;
    String ret = null;

    GP.result.setOracleGroup( og );
    GP.result.setFileName( extractFileName( false, fileName ) );

    //If the file is in EBCDIC format, convert to ASCII before processing.
    if( isEbcdic( fileSample ) )  {
      GP.report.addReport( "Character encoding determined to be EBCDIC." );

      fs = convertToAscii( fileSample, og );
      GP.result.setIsAscii( false );
    }
    else  {
      GP.report.addReport( "Character encoding determined to be ASCII." );

      fs = ( new String( fileSample ) ).toCharArray( );
      GP.result.setIsAscii( true );
    }
    GP.sampleSize = fs.length;
//Debug statement.
//System.out.println( "Finished encoding..." );
//End debug statement.

    //Determine what delimiters, if any, exist in the file.
    GP.result.setDelimiter( d = delimited( fs ) );
//Debug statement.
//System.out.println( "Finished delimiter..." );
//End debug statement.

    if( d != null )  {
      GP.report.addReport( "A delimiter was found." );

      if( d.validFieldDelimiter( ) )  {  //A fully delimited file.
        GP.report.addReport( "Found a field and record delimiter - " + 
                             "fully delimited." );

        header = extractHeader( fs, d );
        rf = splitRecords( true, fs, d );
        GP.result.setRecordLength( recordLength( rf ) );
        rf = splitSecondSample( averageRecordLength( rf ), og, fileName );
        GP.result.setRecordCount( rf.length );
        GP.result.setFieldDescriptors( fd = FindFields.find( rf, og ) );
//Debug statement.
//System.out.println( "Finished delimited find..." );
//End debug statement.
        FindFields.typeOverlap( rf, og, fd );
        re = FindFields.decision( og, fd );
//Debug statement.
//System.out.println( "Finished delimited decision..." );
//End debug statement.
        FindFields.postProcessing( rf, fd, og, re );
        FindFields.updateEmpty( og, re, fd );
        FindFields.postProcessing( rf, fd, og, re );
//Debug statement.
//System.out.println( "Finished delimited post..." );
//End debug statement.
        GP.result.setHeader( FindFields.determineHeader( header, og, re ) );
        GP.result.setRecordEnumeration( re );
      }
      else  {  //A hybrid, fixed length fields with record delimiters, file.
        GP.report.addReport( "Found only a record delimiter - hybrid." );

        r = splitRecords( d.getRecordDelimiter( ), fs );
        GP.result.setRecordLength( recordLength( r ) );
        r = splitSecondSample( d.getRecordDelimiter( ), og, fileName );
//Debug statement.
//System.out.println( "Finished resample..." );
//End debug statement.
        GP.result.setRecordCount( r.length );
        GP.result.setFieldDescriptors( fd = FindFields.find( r, og ) );
//Debug statement.
//System.out.println( "Finished hybrid find..." );
//End debug statement.
        FindFields.typeOverlap( r, og, fd );
        re = FindFields.decision( r, og, fd );
//Debug statement.
//System.out.println( "Finished hybrid decision..." );
//End debug statement.
        FindFields.postProcessing( r, fd, og, re );
        FindFields.updateEmpty( r, og, re, fd );
        FindFields.postProcessing( r, fd, og, re );
//Debug statement.
//System.out.println( "Finished hybrid post..." );
//End debug statement.
        GP.result.setRecordEnumeration( re );
      }
    }
    else  {  //A fully fixed file.
      GP.report.addReport( "A delimiter was not found." );

      GP.result.setRecordLength( rl = recordLength( fs, og ) );
//Debug statement.
//System.out.println( "Finished fixed record length " + rl + "..." );
//End debug statement.

      if( rl >= 0 )  {
        r = splitRecords( fs, rl );
        r = splitSecondSample( og, fileName );
//Debug statement.
//System.out.println( "Finished fixed resample..." );
//End debug statement.
        GP.result.setRecordCount( r.length );
        GP.result.setFieldDescriptors( fd = FindFields.find( r, og ) );
//Debug statement.
//System.out.println( "Finished fixed find..." );
//End debug statement.
        FindFields.typeOverlap( r, og, fd );
        re = FindFields.decision( r, og, fd );
//Debug statement.
//System.out.println( "Finished fixed decision..." );
//End debug statement.
        FindFields.postProcessing( r, fd, og, re );
        FindFields.updateEmpty( r, og, re, fd );
        FindFields.postProcessing( r, fd, og, re );
//Debug statement.
//System.out.println( "Finished fixed post..." );
//End debug statement.
        GP.result.setRecordEnumeration( re );
      }
      else  {
        GP.report.addReport( "Unable to find the record length for a fully " +
                             "fixed file -- clearing report." );

        GP.result.clear( );
      }
    }

    //Output the accumulated data.
    if( GP.result.isValid( ) )  {
      GP.result.setRunTime( System.currentTimeMillis( ) - st );
      if( GP.pto )  {
        ret = GP.result.toString( );
      }
      else  {
        ret = GP.result.toXML( );
      }
    }
    else  {
      System.out.println( GP.report );
    }

    return( ret );
  }  /*  public static String buildLayout( byte[], byte[], String )  */

}  /*  public class WorkHorse  */
