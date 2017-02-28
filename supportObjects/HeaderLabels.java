package supportObjects;

import java.util.ArrayList;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;


public class HeaderLabels  {
  private HeaderLabel[] hl;


  public HeaderLabels( )  {
    int i;
    HeaderLabel holder = null;
    String s;
    BufferedReader inf;
    ArrayList<HeaderLabel> al = new ArrayList<HeaderLabel>( );

    try  {
      inf = new BufferedReader( new FileReader( GP.hlf ) );

      //Load the knowledge base of previously encountered header labels into
      //  a local data structures for searching.
      while( ( s = inf.readLine( ) ) != null )  {
        //The content type label as recognized by this program.
        if( ( i = s.indexOf( ':' ) ) < 0 )  {
          if( holder != null )  {
            al.add( holder );
          }

          holder = new HeaderLabel( s );
        }
        else  {  //A corresponding header label with an associated count.
          try  {
            holder.addHeaderLabel( Integer.parseInt( s.substring( i + 1 ) ),
                                   s.substring( 0, i ) );
          }catch( NumberFormatException e )  {
            System.out.println( "supportObjects - HeaderLabels error 1:  " +
                                "NumberFormatException.  " + e.getMessage( ) );

          }

        }
      }

      inf.close( );
    }catch( IOException e )  {
//      System.out.println( "|" + GP.hlf + "|" );
//      System.out.println( "Bad file name.  HeaderLabels will remain empty." );
      //Currently do not report a bad file name.
//      System.out.println( "supportObjects - HeaderLabels error 2:  " +
//                          "IOException.  " + e.getMessage( ) );

    }
    catch( NullPointerException e )  {
    }

    if( al.size( ) > 0 )  {
      hl = new HeaderLabel[al.size( )];

      for( i = 0; i < hl.length; i++ )  {
        hl[i] = al.get( i );
      }
    }
    else  {
      hl = null;
    }
  }  /*  Constructor  */


  /**
   *   Insertion sort based on label probability.
   */
  private void sortLabels( LabelMatch[] arr )  {
    int i,
        j;
    LabelMatch index;

    for( i = 1; i < arr.length; i++ )  {
      index = arr[i];

      j = i;
      while( ( j > 0 ) &&
             ( arr[( j - 1 )].getProbability( ) > index.getProbability( ) ) )  {

        arr[j] = arr[( j - 1 )];
        j--;
      }

      arr[j] = index;
    }
  }  /*  private void sortLabels( LabelMatch[] )  */


  /**
   *   Returns all content type labels that have the parameter LABEL in their
   * corresponding knowledge base.  The list of labels is sorted on probability
   * from most to least probable based on the previously encountered counts
   * associated with each header label in the knowledge base.
   */
  public LabelMatch[] searchForLabel( String label )  {
    int i;
    LabelMatch holder;
    LabelMatch[] ret = null;
    ArrayList<LabelMatch> al = new ArrayList<LabelMatch>( );

    if( hl != null )  {
      for( i = 0; i < hl.length; i++ )  {
        if( ( holder = hl[i].getMatch( label ) ) != null )  {
          al.add( holder );
        }
      }

      if( al.size( ) > 0 )  {
        ret = new LabelMatch[al.size( )];
        for( i = 0; i < ret.length; i++ )  {
          ret[i] = al.get( i );
        }

        sortLabels( ret );
      }
    }

    return( ret );
  }  /*  public LabelMatch[] searchForLabel( String )  */


  public void updateSourceFile( )  {
    int i;
    PrintWriter outf;

    if( hl != null )  {
      try  {
        outf = new PrintWriter( new FileWriter( GP.hlf ) );

        for( i = 0; i < hl.length; i++ )  {
          outf.print( hl[i].toString( ) );
        }

        outf.flush( );
        outf.close( );
      }catch( IOException e )  {
        System.out.println( "supportObjects - HeaderLabels error 3:  " +
                            "IOException.  " + e.getMessage( ) );

      }
    }
  }  /*  public void updateSourceFile( )  */


  /**
   * @return A single character array of all the digit and letter characters.
   *         All other characters are removed.
   */
  private static char[] clean( String s )  {
    int i;
    char[] ret = null;
    ArrayList<Character> al = new ArrayList<Character>( );

    if( s != null )  {
      ret = s.toLowerCase( ).toCharArray( );

      for( i = 0; i < ret.length; i++ )  {
        if( Character.isLetterOrDigit( ret[i] ) )  {
          al.add( ret[i] );
        }
      }

      if( al.size( ) > 0 )  {
        ret = new char[al.size( )];
        for( i = 0; i < ret.length; i++ )  {
          ret[i] = al.get( i );
        }
      }
      else  {
        ret = null;
      }
    }

    return( ret );
  }  /*  private static char[] clean( String )  */


  /**
   *   The idea is that some labels have the same, or very similar, values
   * excluding the fact that the characters are in a different order ( mixed
   * up ).  This method allows for that by only testing for the presence of
   * characters rather than their respective positions.
   *
   *   While the approach may or may not be valid, the return value is set in a
   * very abitrary way.  This will probably need adjusted in the future.
   */
  public static boolean existential( String s, String t )  {
    boolean b,
            ret = false;
    boolean[] f;
    char[] sa = clean( s ),
           ta;
    int i,
        j,
        loc,  //Left over count.
        nfc;  //Not found count.

    if( ( sa != null ) && ( t != null ) )  {
      f = new boolean[sa.length];
      ta = t.toLowerCase( ).toCharArray( );

      for( i = 0; i < f.length; i++ )  {
        f[i] = true;
      }

      loc = 0;
      nfc = 0;
      for( i = 0; i < ta.length; i++ )  {
        if( Character.isLetterOrDigit( ta[i] ) )  {
          b = false;
          j = 0;
          //Decided to perform a linear search because the strings should be
          //  small and the ( ta.length() * sa.length()^2 ) runtime should not
          //  be much more than the time to sort the entries and perform a
          //  binary search.
          while( !b && ( j < sa.length ) )  {
            if( f[j] && ( ta[i] == sa[j] ) )  {
//Debug statement.
//System.out.println( ta[i] + " == " + sa[j] + "." );
//End debug statement.
              f[j] = false;
              b = true;  //Found a corresponding entry.
            }
            else  {
              j++;
            }
          }

          if( !b )  {  //Did not find the character in the original string.
//Debug statement.
//System.out.println( "Couldn't find " + ta[i] + "." );
//End debug statement.
            nfc++;
          }
        }
      }

      //Count how many characters that were in the original string were not in
      //  the incoming string.
      for( i = 0; i < f.length; i++ )  {
        if( f[i] )  {
//Debug statement.
//System.out.println( "Leftover character " + sa[i] + "." );
//End debug statement.
          loc++;
        }
      }
//Debug statement.
//System.out.println( "Not found count: " + nfc + " -- " + ta.length + "." );
//System.out.println( "Left over count: " + loc + " -- " + sa.length + "." );
//End debug statement.

      //The idea is that the original content type label should contain most, if
      //  not all, of the possible characters.  Thus while the original might
      //  contain many more characters ( e.g. incoming header label is an
      //  abbreviation ) there should be few, if any, unknown characters. 
      ret = ( ( nfc <= Math.ceil( ( double )ta.length / 8.0 ) ) &&
              ( loc <= ( ( 3 * sa.length ) / 4 ) ) );

    }

    return( ret );
  }  /*  public static int existential( String, String )  */


  /**
   *   Computes the Levenshtein distance of two strings.  Levenshtein distance
   * between two strings is given by the minimum number of operations needed to
   * transform one string into the other, where an operation is an insertion,
   * deletion, or substitution of a single character.  The algorithm is similar
   * to that of the LCS problem.
   * 
   * http://www.merriampark.com/ldjava.htm
   * http://en.wikipedia.org/wiki/Levenshtein_distance
   */
  public static int levenshteinDistance( String s, String t )  {
    char[] sa,
           ta;
    int i,
        j,
        n = s.length( ),  //Length of s.
        m = t.length( ),  //Length of t.
        ret = -1,
        cost;
    //To minimize space requirements two single-dimensional arrays of length
    //  ( s.length( ) + 1 ) are maintained rather than a matrix of size
    //  ( ( s.length( ) + 1 ) X ( t.length( ) + 1 ) ).  The array, D, is the
    //  current working distance array that maintains the newest distance cost
    //  counts as the method iterates through the characters of string S.  Each
    //  time the character index of T is incremented D is copied to P in order
    //  to retain the previous costs counts as required by the algorithm. Rather
    //  than spend time on a deep copy, the endpoint of the reference variables
    //  are swapped instead.
    int[] p = new int[( n + 1 )],  //"Previous" cost array, horizontally.
          d = new int[( n + 1 )],  //Cost array, horizontally.
          holder;  //Placeholder to assist in swapping p and d

    //Account for possible NULL strings to ensure the method does not crash.
    if( s == null )  {
      s = "";
    }
    if( t == null )  {
      t = "";
    }

    if( n == 0 )  {  //If S is empty, the distance is the length of T.
      ret = m;
    }
    else if( m == 0 )  {  //If T is empty, the distance is the length of S.
      ret = n;
    }
    else  {  //Otherwise compute the distance between the two strings.
      sa = s.toCharArray( );
      ta = t.toCharArray( );

      for( i = 0; i <= n; i++ )  {
        p[i] = i;
      }
    
      for( j = 1; j <= m; j++ )  {
         d[0] = j;

         for( i = 1; i <= n; i++ )  {
//           cost = s.charAt( i - 1 )==c ? 0 : 1;
           if( sa[( i - 1 )] == ta[( j - 1 )] )  {
             cost = 0;
           }
           else  {
             cost = 1;
           }

           //Minimum of cell to the ( left + 1 ), to the ( top + 1 ), diagonally
           //  left and ( up + cost ).        
           d[i] = Math.min( Math.min( ( d[( i - 1 )] + 1 ), ( p[i] + 1 ) ), 
                            ( p[( i - 1 )] + cost ) );

         }

         //Copy current distance counts to "previous" row distance counts.
         holder = p;
         p = d;
         d = holder;
      }

      ret = p[n];
    }

    //The last action in the above loop was to switch D and P, so now P actually 
    //  has the most recent cost counts.
    return( ret );
  }  /*  public static int LevenshteinDistance( String, String )  */


  /**
   *   Computes the Damerau Levenshtein distance of two strings. 
   * Damerau Levenshtein distance between two strings is given by counting the
   * minimum number of operations needed to transform one string into the other,
   * where an operation is defined as an insertion, deletion, or substitution of
   * a single character, or a transposition of two characters.
   *
   * http://en.wikipedia.org/wiki/Damerau Levenshtein distance
   *-----
   * Need to run distance on each token to try to match tokens.  (e.g. "first
   * name" and "name first".
   */
  public static int distance( String s, String t )  {
    char[] sa,
           ta;
    int i,
        j,
        ret = -1,
        cost;
    int[][] d;

    //Account for possible NULL strings to ensure the method does not crash.
    if( s == null )  {
      s = "";
    }
    if( t == null )  {
      t = "";
    }

    if( s.length( ) == 0 )  {  //If S is empty, the distance is the length of T.
      ret = t.length( );
    }
    else if( t.length( ) == 0 )  {  //If T is empty, the distance is the length of S.
      ret = s.length( );
    }
    else  {  //Otherwise compute the distance between the two strings.
      //Allocate memory.
      sa = s.toCharArray( );
      ta = t.toCharArray( );
      d = new int[( sa.length + 1 )][( ta.length + 1 )];

      //Initialize the first row and column of the matrix.
      for( i = 0; i <= sa.length; i++ )  {
        d[i][0] = i;
      }
      for( j = 1; j <= ta.length; j++ )  {
        d[0][j] = j;
      }

      for( i = 1; i <= sa.length; i++ )  {
        for( j = 1; j <= ta.length; j++ )  {
          if( sa[( i - 1 )] == ta[( j - 1 )] )  {
            cost = 0;
          }
          else  {
            cost = 1;
          }

          //Deletion, insertion, and substitution respectively.
          d[i][j] = Math.min( Math.min( ( d[( i - 1 )][j] + 1 ),
                                        ( d[i][( j - 1 )] + 1 ) ),
                              ( d[( i - 1 )][( j - 1 )] + cost ) );

          
          //This IF statement converts Levenshtein into Damerau Levenshtein
          //  distance which allows for transposition.
          if( ( i > 1 ) && ( j > 1 ) && ( sa[( i - 1 )] == ta[( j - 2 )] ) &&
              ( sa[( i - 2 )] == ta[( j - 1 )] ) )  {

            //The second parameter stands for substitution.
            d[i][j] = Math.min( d[i][j], ( d[( i - 2 )][( j - 2 )] + cost ) );
          }
        }
      }

      ret = d[sa.length][ta.length];
    }

    return( ret );
  }  /*  public static int distance( String, String )  */

}  /*  public class HeaderLabels  */
