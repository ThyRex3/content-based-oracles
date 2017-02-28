/**
 *   OracleGroup.java is a container class holding all the oracles within this
 * project.  The standardized access specified by the Oracle.java interface
 * makes it possible to group all the oracles together in this way.  If an
 * oracle provides methods not defined in the Oracle.java interface these can be
 * accessed by acquiring a reference to the needed oracle through the
 * getOracle() methods.
 * 
 *   The core oracles, as they are referenced, are the oracles that are less
 * likely to give false positives.  They are used to define the initial record
 * structure.  The remaining oracles are then used to fill in the gaps, if
 * applicable.
 *
 *   I think a general trend is to assign higher rankings to composite oracles
 * over simple oracles.  For example, AddressTwo = 800, StreetSuffix = 750.
 *
Oracle Rankings:
250 name prefix
1000 first name
1000 last name
250 name suffix
1000 full name
50 directional
1000 street name
25 street number
750 street suffix
1000 address one
500 unit designator
25 unit number
800 address two
1000 city
500 state
500 zip code
500 email
250 phone number
75 boolean
 */

package oracles;

import java.util.Hashtable;

import supportObjects.LabelMatch;
import supportObjects.HeaderLabels;
import supportObjects.NameCountPair;
import supportObjects.FieldDescriptor;
import supportObjects.ConfigurationPart;
import supportObjects.RecordEnumeration;


public class OracleGroup  {
  private int oc,  //The number of oracles implemented in the OracleGroup.
              noi,  //The index of the next oracle in the group.
              nri,  //The index of the next oracle by rank.
              rsi,  //The index of the entry to start at in the ranking.
              otrc,  //The count of oracles to reduce.
              notr,  //The index of the next oracle to reduce.
              otpopc,  //The count of oracles to post process.
              notpop,  //The index of the next oracle to post process.
              otprpc,  //The count of oracles to pre process.
              notprp;  //The index of the next oracle to pre process.
  private int[] otr,  //The oracles to reduce, their supporting databases.
                otpop,  //The oracles that implement post processing.
                otprp,  //The oracles that implement pre processing.
                rv,  //The ranked value of the corresponding oracle.
                ri;  //An index to the next oracle in order of rank.
  HeaderLabels h;
  private NameCountPair ncp;
  private Oracle[] oracle;
  //This hashtable associates the name of a content type with an integer value. 
  private Hashtable<String, Integer> lookup;


  public OracleGroup( )  {
    int is = 5;  //Initialization size.

    oc = 0;
    noi = 0;
    nri = 0;
    rsi = -1;
    otrc = 0;
    notr = 0;
    otpopc = 0;
    notpop = 0;
    otprpc = 0;
    notprp = 0;

    otr = new int[is];
    otpop = new int[is];
    otprp = new int[is];
    rv = new int[is];
    ri = new int[is];
    h = new HeaderLabels( );
    ncp = new NameCountPair( "/data/LineCounts.dat" );
    oracle = new Oracle[is];
    lookup = new Hashtable<String, Integer>( );

    for( is = 0; is < rv.length; is++ )  {
      otr[is] = otpop[is] = otprp[is] = rv[is] = ri[is] = -1;

      oracle[is] = null;
    }
  }  /*  Constrctor  */


  public void defaultInitialization( )  {
    int i,
        j,
        m,
        n,
        d,
        ind;

    otr = new int[7];  //Oracle to reduce.
    otpop = new int[4];  //Post processing oracles.
    otprp = new int[2];  //Pre processing oracles.
    rv = new int[21];
    ri = new int[21];
    ncp = new NameCountPair( "/data/LineCounts.dat" );
    oracle = new Oracle[21];
    lookup = new Hashtable<String, Integer>( );

    otrc = 7;
    otpopc = 6;
    otprpc = 1;

    rv[oc] = 250;  //Name prefix oracle.
    otprp[0] = oc;
    oracle[oc] = new NamePrefixOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 1000;  //First name oracle.
    otr[notr++] = oc;
    oracle[oc] = new FirstNameOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 500;  //Middle name oracle.
    otpop[2] = oc;
    oracle[oc] = new MiddleNameOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 1000;  //Last name oracle.
    otr[notr++] = oc;
    oracle[oc] = new LastNameOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 250;  //Name suffix oracle.
    otprp[1] = oc;
    oracle[oc] = new NameSuffixOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 1000;  //Full name oracle.
    otr[notr++] = oc;
    oracle[oc] = new FullNameOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 30;  //Street number oracle.
    otpop[1] = oc;
    oracle[oc] = new StreetNumberOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 50;  //Directional oracle.
    oracle[oc] = new DirectionalOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 750;  //Street name oracle.
    otr[notr++] = oc;
    oracle[oc] = new StreetNameOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 750;  //Street suffix oracle.
    oracle[oc] = new StreetSuffixOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 1000;  //Address line one oracle.
    otr[notr++] = oc;
    oracle[oc] = new AddressLineOneOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 500;  //Unit designator oracle.
    oracle[oc] = new UnitDesignatorOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 20;  //Unit number ( secondary range number ) oracle.
    otpop[0] = oc;
    oracle[oc] = new SecondaryRangeNumberOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 800;  //Address line two oracle.
    oracle[oc] = new AddressLineTwoOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 1000;  //Address line oracle.
    otr[notr++] = oc;
    oracle[oc] = new AddressLineOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 1000;  //City oracle.
    otr[notr++] = oc;
    oracle[oc] = new CityNameOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 800;  //State oracle.
    oracle[oc] = new StateOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 500;  //Zip code oracle.
    otpop[3] = oc;
    oracle[oc] = new ZipcodeOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 500;  //Email oracle.
    oracle[oc] = new EmailOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 250;  //Phone number oracle.
    oracle[oc] = new PhoneNumberOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

    rv[++oc] = 75;  //Boolean oracle.
    oracle[oc] = new BooleanOracle( );
    oracle[oc].initialize( this, null );
    lookup.put( oracle[oc].getName( ), oc );

//    rv[++oc][0] = 275;  //Date oracle.
//    oracle[oc] = new DateOracle( );
//    lookup.put( oracle[oc].getName( ), oc );

    notr = 0;
    notpop = 0;
    notprp = 0;

    rsi = 0;
    for( i = 0; i < rv.length; i++ )  {
      ri[i] = -1;

      if( rv[i] > rv[rsi] )  {
        rsi = i;
      }
    }

    //N^2 procedure to order the oracles by rv.
    i = rsi;
    for( m = 0; m < rv.length; m++ )  {
      ind = -1;
      d = Integer.MAX_VALUE;
      for( n = 1; n < rv.length; n++ )  {
        j = ( ( i + n ) % rv.length );

        if( ( ri[j] < 0 ) && ( ( rv[i] - rv[j] ) >= 0 ) )  {
          if( ( rv[i] - rv[j] ) < d )  {
            ind = j;
            d = ( rv[i] - rv[j] );
          }
        }
      }

      ri[i] = ind;
      i = ind;
    }

    nri = rsi;  //Initialize the "next rv index" to the "rv start index".
  }  /*  public void defaultInitialization( )  */


  public void addOracle( ConfigurationPart cp )  {
    int i,
        j,
        k,
        ic = 5;  //Increment count.
    int[] ih;
    Oracle o;
    Oracle[] oh;

    try  {
      o = ( Oracle )Class.forName( cp.getQualifiedName( ) ).newInstance( );

      o.initialize( this, cp.getParameters( ) );

      //Create space in the array of ORACLEs.
      if( oc >= oracle.length )  {
        oh = new Oracle[( oracle.length + ic )];
        System.arraycopy( oracle, 0, oh, 0, oracle.length );
        oracle = oh;

        i = rv.length;
        ih = new int[( oracle.length + ic )];
        System.arraycopy( rv, 0, ih, 0, rv.length );
        rv = ih;

        ih = new int[( oracle.length + ic )];
        System.arraycopy( ri, 0, ih, 0, ri.length );
        ri = ih;

        for( ; i < rv.length; i++ )  {
          rv[i] = ri[i] = -1;
        }
      }

//Debug statement.
//for( i = 0; i < rv.length; i++ )  {
//System.out.print( rv[i] + ", " );
//}
//System.out.println( "" );
//End debug statement.
      //Create references to the oracle in the supporting structures to enablea
      //  accessibility.
      rv[oc] = cp.getRank( );
//Debug statement.
//System.out.println( "RV[OC] = " + rv[oc] + ";  OC = " + oc + "." );
//End debug statement.
      oracle[oc] = o;
      lookup.put( oracle[oc].getName( ), oc );

      //Update the rank ordering.
      i = 0;
      k = -1;
      j = rsi;
//Debug statement.
//System.out.println( "RV[OC] = " + rv[oc] + "." );
//End debug statement.
      while( ( i < oc ) && ( j >= 0 ) && ( rv[j] >= rv[oc] ) )  {
//Debug statement.
//System.out.println( "RV[J] = " + rv[j] + ";  J = " + j + "." );
//End debug statement.
        i++;  //Keep track of how many oracles have been examined.
        k = j;  //Remember the preceeding entry in the current ordering.
        j = ri[j];  //Move to the next highest ranked oracle.
      }

      //Place the new entry into the proper position of the rank ordering.
      if( k >= 0 )  {  //If there was a preceeding entry.
        ri[k] = oc;  //Update the preceeding rank to point to the new entry.
      }
      ri[oc] = j;
      if( i == 0 )  {  //The current rank was the largest encountered as of yet.
//Debug statement.
//System.out.println( "Updating RSI from " + rsi + " to " + oc + "." );
//End debug statement.
        rsi = oc;
      }
//Debug statement.
//System.out.println( "" );
//End debug statement.

      //If the oracle implements other interfaces make it accessible via those
      //  iterators.
      if( o instanceof AlterDatabase )  {
        if( otrc >= otr.length )  {  //Allocate new memory for this iterator.
          ih = new int[( otr.length + ic )];

          System.arraycopy( otr, 0, ih, 0, otr.length );
          otr = ih;
        }

        otr[otrc++] = oc;
      }
      if( o instanceof PostProcess )  {
        if( otpopc >= otpop.length )  {
          //Allocate new memory for this iterator.
          ih = new int[( otpop.length + ic )];

          System.arraycopy( otpop, 0, ih, 0, otpop.length );

          for( i = otpop.length; i < ih.length; i++ )  {
            ih[i] = -1;
          }

          otpop = ih;
        }

//Debug statement.
//System.out.println( "Post process..." );
//System.out.println( "Rank = " + rv[oc] + "." );
//End debug statement.
        //Order the oracles from least reliable to most reliable using rank.
        i = ( otpopc - 1 );
        while( ( i >= 0 ) && ( getRank( otpop[i] ) > rv[oc] ) )  {
          i--;
        }
//Debug statement.
//System.out.println( "Place at position " + ( i + 1 ) + "." );
//End debug statement.
        System.arraycopy( otpop, ( i + 1 ), otpop, ( i + 2 ),
                          ( otpopc - ( i + 1 ) ) );

        otpop[( i + 1 )] = oc;
//Debug statement.
//System.out.println( "OTPOP[" + ( i + 1 ) + "] = " + otpop[( i + 1 )] + " == " + oc + "." );
//End debug statement.
        otpopc++;
//Debug statement.
//for( i = 0; i < otpopc; i++ )  {
//System.out.println( "Name - " + name( otpop[i] ) + ";  Rank = " + rv[otpop[i]] + "." );
//}
//System.out.println( "-----" );
//System.out.println( "" );
//End debug statement.
      }
      if( o instanceof PreProcess )  {
        if( otprpc >= otprp.length )  {
          //Allocate new memory for this iterator.
          ih = new int[( otprp.length + ic )];

          System.arraycopy( otprp, 0, ih, 0, otprp.length );

          for( i = otprp.length; i < ih.length; i++ )  {
            ih[i] = -1;
          }

          otprp = ih;
        }

//Debug statement.
//System.out.println( "Pre process..." );
//System.out.println( "Rank = " + rv[oc] + "." );
//End debug statement.
        //Order the oracles from least reliable to most reliable using rank.
        i = ( otprpc - 1 );
        while( ( i >= 0 ) && ( getRank( otprp[i] ) > rv[oc] ) )  {
          i--;
        }
//Debug statement.
//System.out.println( "Place at position " + ( i + 1 ) + "." );
//End debug statement.
        System.arraycopy( otprp, ( i + 1 ), otprp, ( i + 2 ),
                          ( otprpc - ( i + 1 ) ) );

        otprp[( i + 1 )] = oc;
//Debug statement.
//System.out.println( "OTPRP[" + ( i + 1 ) + "] = " + otprp[( i + 1 )] + " == " + oc + "." );
//End debug statement.
        otprpc++;
//Debug statement.
//for( i = 0; i < otprpc; i++ )  {
//System.out.println( "Name - " + name( otprp[i] ) + ";  Rank = " + rv[otprp[i]] + "." );
//}
//System.out.println( "-----" );
//System.out.println( "" );
//End debug statement.
      }

      oc++;
    }catch( ClassNotFoundException e )  {
      System.out.println( "oracles - OracleGroup error 1: " +
                          "ClassNotFoundException.  " + e.getMessage( ) );

      System.exit( 1 );
    }catch( InstantiationException e )  {
      System.out.println( "oracles - OracleGroup error 2: " +
                          "InstantiationException.  " + e.getMessage( ) );

      System.exit( 1 );
    }catch( IllegalAccessException e )  {
      System.out.println( "oracles - OracleGroup error 3: " +
                          "IllegalAccessException.  " + e.getMessage( ) );

      System.exit( 1 );
    }
  }  /*  public void addOracle( ConfigurationPart )  */


  public int oracleCount( )  {
    return( oc );
  }  /*  public int oracleCount( )  */


  public int oraclesToReduceCount( )  {
    return( otrc );
  }  /*  public int oraclesToReduceCount( )  */


  public int postProcessOracleCount( )  {
    return( otpopc );
  }  /*  public int postProcessOracleCount( )  */


  public int preProcessOracleCount( )  {
    return( otprpc );
  }  /*  public int preProcessOracleCount( )  */


  /**
   * @return The index of the next oracle out of all possible oracles.
   */
  public int next( )  {
    int ret = noi++;

    if( noi >= oracle.length )  {
      noi = 0;
    }

    return( ret );
  }  /*  public int next( )  */


  public int nextByRank( )  {
    int ret = nri;

    if( ( nri = ri[nri] ) < 0 )  {
      nri = rsi;
    }

    return( ret );
  }  /*  public int nextByRank( )  */


  public int nextToReduce( )  {
    int ret = otr[notr++];

    if( notr >= otrc )  {
      notr = 0;
    }

    return( ret );
  }  /*  public int nextToReduce( )  */


  /**
   *   Oracles that implement the pre processing interface are ordered from
   * least reliable to most reliable as indicated by the associated ranking.
   * The reasoning for this ordering is that the least reliable oracles, which
   * are typically the most inclusive, may clean up their results as much as
   * possible in order to reduce ambiguity as much as possible for the more
   * reliable oracles.
   */
  public int nextToPreProcess( )  {
    int ret = otprp[notprp++];

    if( notprp >= otprpc )  {
      notprp = 0;
    }

    return( ret );
  }  /*  public int nextToPreProcess( )  */


  /**
   *   Oracles that implement the post processing interface are ordered from
   * least reliable to most reliable as indicated by the associated ranking.
   * The reasoning for this ordering is that the least reliable oracles, which
   * are typically the most inclusive, may clean up their results as much as
   * possible in order to reduce ambiguity as much as possible for the more
   * reliable oracles.
   */
  public int nextToPostProcess( )  {
    int ret = otpop[notpop++];

    if( notpop >= otpopc )  {
      notpop = 0;
    }

    return( ret );
  }  /*  public int nextToPostProcess( )  */


  /**
   * Resets the oracle index counter.
   */
  public void reset( )  {
    noi = 0;
  }  /*  public void reset( )  */


  public void rankReset( )  {
    nri = rsi;
  }  /*  public void rankReset( )  */


  public void reduceReset( )  {
    notr = 0;
  }  /*  public void reduceReset( )  */


  /**
   *   Oracles that implement the pre processing interface are ordered from
   * least reliable to most reliable as indicated by the associated ranking.
   * The reasoning for this ordering is that the least reliable oracles, which
   * are typically the most inclusive, may clean up their results as much as
   * possible in order to reduce ambiguity as much as possible for the more
   * reliable oracles.
   */
  public void preProcessReset( )  {
//Debug statement.
//for( int i = 0; i < otppc; i++ )  {
//System.out.println( name( otpp[i] ) );
//}
//End debug statement.
    notprp = 0;
  }  /*  public void preProcessReset( )  */


  /**
   *   Oracles that implement the post processing interface are ordered from
   * least reliable to most reliable as indicated by the associated ranking.
   * The reasoning for this ordering is that the least reliable oracles, which
   * are typically the most inclusive, may clean up their results as much as
   * possible in order to reduce ambiguity as much as possible for the more
   * reliable oracles.
   */
  public void postProcessReset( )  {
//Debug statement.
//for( int i = 0; i < otppc; i++ )  {
//System.out.println( name( otpp[i] ) );
//}
//End debug statement.
    notpop = 0;
  }  /*  public void postProcessReset( )  */


  /**
   * @return Returns the rank value of the corresponding oracle.  There may be
   *         duplicate values among the several oracles.
   */
  public int getRank( int oracleIndex )  {
    int ret = -1;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oc ) )  {
      ret = rv[oracleIndex];
    }

    return( ret );
  }  /*  public int getRank( int )  */


  /**
   * This method is used in conjunction with the isValid() methods.
   * 
   * @return The index of the Oracle with name ORACLENAME.  The index doubles as
   *         a specification of the content type.
   */
  public int index( String oracleName )  {
    Integer ret;

    if( ( ret = lookup.get( oracleName ) ) == null )  {
      ret = new Integer( -1 );
    }

    return( ret.intValue( ) );
  }  /*  public int index( String )  */


  /**
   * @return The name of the Oracle at position INDEX.
   */
  public String name( int index )  {
    String ret = null;

    if( ( index >= 0 ) && ( index < oracle.length ) )  {
      ret = oracle[index].getName( );
    }

    return( ret );
  }  /*  public String name( int )  */


  public boolean isValid( char[] data, int oracleIndex )  {
    boolean ret = false;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].isValid( data );
    }

    return( ret );
  }  /*  public isValid( char[], int )  */


  public boolean isValid( char[] data, int position, int length,
                          int oracleIndex )  {

    boolean ret = false;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].isValid( data, position, length );
    }

    return( ret );
  }  /*  public isValid( char[], int )  */


  public boolean isValid( String data, int oracleIndex )  {
    boolean ret = false;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].isValid( data );
    }

    return( ret );
  }  /*  public isValid( char[], int )  */


  /**
   * @return The maximum number of characters to consider for this type of
   *         oracle.
   */
  public int getMaxLength( int oracleIndex )  {
    int ret = -1;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].getMaxLength( );
    }

    return( ret );
  }  /*  public int getMaxLength( int )  */


  /**
   * @return The group of the oracle at the position specified by the parameter.
   */
  public int getGrouping( int oracleIndex )  {
    int ret = -1;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].getGrouping( );
    }

    return( ret );
  }  /*  public int getGrouping( int )  */


  public double getMinPercentage( int oracleIndex )  {
    double ret = 0.0;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].getMinPercentage( );
    }

    return( ret );
  }  /*  public double getMinPercentage( int )  */


  public double getMaxBlankPercentage( int oracleIndex )  {
    double ret = 1.0;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].getMaxBlankPercentage( );
    }

    return( ret );
  }  /*  public double getMinPercentage( int )  */


  public double matchHeader( int oracleIndex, String label )  {
    double ret = 0.0;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex].matchHeader( label );
    }

    return( ret );
  }  /*  public double getMinPercentage( int, String )  */


  /**
   *   This method should only be invoked on oracles that implement the
   * AlterDatabase.java interface.
   */
  public void setMaxDatabase( int oracleIndex )  {
    boolean found = false;
    int i = 0;

    //Ensure the index parameter references an oracle that is listed as an
    //  alterable database.
    while( !found && ( i < otr.length ) )  {
      if( oracleIndex != otr[i] )  {
        i++;
      }
      else  {
        found = true;
      }
    }

    if( found )  {
      ( ( AlterDatabase )oracle[oracleIndex] ).setMaxDatabase( );
    }
  }  /*  public void setMaxDatabase( int )  */


  /**
   *   This method should only be invoked on oracles that implement the
   * AlterDatabase.java interface.
   */
  public void setMinDatabase( int oracleIndex )  {
    boolean found = false;
    int i = 0;

    //Ensure the index parameter references an oracle that is listed as an
    //  alterable database.
    while( !found && ( i < otr.length ) )  {
      if( oracleIndex != otr[i] )  {
        i++;
      }
      else  {
        found = true;
      }
    }

    if( found )  {
      ( ( AlterDatabase )oracle[oracleIndex] ).setMinDatabase( );
    }
  }  /*  public void setMaxDatabase( int )  */


  public FieldDescriptor[] preProcess( char[][] records, int oracleIndex,
                                       FieldDescriptor[] fd )  {

    boolean found = false;
    int i = 0;

    if( ( oracleIndex >= 0 ) && ( fd != null ) )  {
      //Ensure the index parameter references an oracle that is listed as one
      //  that implements post processing.
      while( !found && ( i < otprp.length ) )  {
        if( oracleIndex != otprp[i] )  {
          i++;
        }
        else  {
//Debug statement.
//System.out.println( "Found " + oracleIndex + " at OTPRP[" + i + "] = " + otprp[i] + "." );
//End debug statement.
          found = true;
        }
      }

      if( found )  {
//Debug statement.
//System.out.println( "Calling preProcess() on type " + oracle[oracleIndex].getName( ) + "." );
//End debug statement.
        fd = ( ( PreProcess )oracle[oracleIndex] ).preProcess( records, fd );
      }
    }

    return( fd );
  }  /*  FieldDescriptor[] preProcess( char[][], int, FieldDescriptor[] )  */


  public FieldDescriptor[] preProcess( char[][][] records, int oracleIndex,
                                       FieldDescriptor[] fd )  {

    boolean found = false;
    int i = 0;

    if( ( oracleIndex >= 0 ) && ( fd != null ) )  {
      //Ensure the index parameter references an oracle that is listed as one
      //  that implements post processing.
      while( !found && ( i < otprp.length ) )  {
        if( oracleIndex != otprp[i] )  {
          i++;
        }
        else  {
          found = true;
        }
      }

      if( found )  {
        fd = ( ( PreProcess )oracle[oracleIndex] ).preProcess( records, fd );
      }
    }

    return( fd );
  }  /*  FieldDescriptor[] preProcess( char[][][], int, FieldDescriptor[] )  */


  /**
   *   This method should only be invoked on oracles that implement the
   * PostProcess.java interface.
   */
  public void postProcess( char[][] records, int oracleIndex,
                           FieldDescriptor[][] fd, RecordEnumeration re )  {

    boolean found = false;
    int i = 0;

    //Ensure the index parameter references an oracle that is listed as one
    //  that implements post processing.
    while( !found && ( i < otpop.length ) )  {
      if( oracleIndex != otpop[i] )  {
        i++;
      }
      else  {
        found = true;
      }
    }

    if( found )  {
      ( ( PostProcess )oracle[oracleIndex] ).postProcess( records, fd, re );
    }
  }  /*  public void postProcess( char[][], int, RecordEnumeration )  */


  /**
   *   This method should only be invoked on oracles that implement the
   * PostProcess.java interface.
   */
  public void postProcess( char[][][] records, int oracleIndex,
                           FieldDescriptor[][] fd, RecordEnumeration re )  {

    boolean found = false;
    int i = 0;

    //Ensure the index parameter references an oracle that is listed as one
    //  that implements post processing.
    while( !found && ( i < otpop.length ) )  {
      if( oracleIndex != otpop[i] )  {
        i++;
      }
      else  {
        found = true;
      }
    }

    if( found )  {
      ( ( PostProcess )oracle[oracleIndex] ).postProcess( records, fd, re );
    }
  }  /*  public void postProcess( char[][][], int, RecordEnumeration )  */


  /**
   * @param oracleIndex The index of the Oracle to return.
   * @return a reference to the Oracle at ORACLEINDEX.
   */
  public Oracle getOracle( int oracleIndex )  {
    Oracle ret = null;

    if( ( oracleIndex >= 0 ) && ( oracleIndex < oracle.length ) )  {
      ret = oracle[oracleIndex];
    }

    return( ret );
  }  /*  public Oracle getOracle( int )  */


  public AlterDatabase getOracleToReduce( int oracleIndex )  {
    boolean found = false;
    int i = 0;
    AlterDatabase ret = null;

    //Ensure the index parameter is an oracle with an alterable database.
    while( !found && ( i < otr.length ) )  {
      if( oracleIndex != otr[i] )  {
        i++;
      }
      else  {
        found = true;
      }
    }

    if( found )  {
      ret = ( AlterDatabase )oracle[oracleIndex];
    }

    return( ret );
  }  /*  public AlterDatabase getOracleToReduce( int )  */


  /**
   * @param oracleName The name of the Oracle to return.
   * @return a reference to the Oracle whose name is ORACLENAME.
   */
  public Oracle getOracle( String oracleName )  {
    return( getOracle( index( oracleName ) ) );
  }  /*  public Oracle getOracle( String )  */


  public LabelMatch[] searchForLabel( String label )  {
    return( h.searchForLabel( label ) );
  }  /*  public LabelMatch[] searchForLabel( String )  */


  /**
   * @param fileName The name of the file.
   * @return The number of entries in FILENAME.
   */
  public int fileLineCount( String fileName )  {
    return( ncp.getLineCount( fileName ) );
  }  /*  public int fileLineCount( String )  */

}  /*  public class OracleGroup  */
