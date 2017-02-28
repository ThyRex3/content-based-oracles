package supportObjects;


public class StateNode  {
  int cai;  //City array index.
  String sn;  //State name.
  CityNode[] ca;  //Array of cities.


  public StateNode( )  {
    cai = 0;
    sn = null;
    ca = null;
  }  /*  Constructor  */


  public StateNode( String stateName )  {
    cai = 0;
    sn = stateName;
    ca = null;
  }  /*  Constructor  */


  public StateNode( int cityCount, String stateName )  {
    cai = 0;
    sn = stateName;
    ca = new CityNode[cityCount];
  }  /*  Constructor  */


  public void setStateName( String stateName )  {
    sn = stateName;
  }  /*  public void setStateName( String )  */


  public void setCities( CityNode[] cities )  {
    ca = cities;
  }  /*  public void setCities( CityNode[] )  */


  public void setCityCount( int cityCount )  {
    cai = 0;
    ca = new CityNode[cityCount];
  }  /*  public void setCityCount( int )  */


  /**
   *   Should only be called after the city count has been set, otherwise the
   * incoming data will be thrown away.
   */
  public void addCity( CityNode city )  {
    if( ( ca != null ) && ( cai < ca.length ) )  {
      ca[cai++] = city;
    }
  }  /*  public void addCity( CityNode )  */


  public String getStateName( )  {
    return( sn );
  }  /*  public String getStateName( )  */


  /**
   * @param ZIPCODE - will be greater than or equal to zero if that field is
   *        to be searched for, else less than zero.
   * @param CITY - the name of the city to be searched for.
   * @return TRUE if the {CITY,ZIPCODE} pair or the CITY name is found
   *         ( depending on how the input parameters are set ), else FALSE.
   */
  public boolean search( int zipcode, String city )  {
    boolean ret = false;  //Were the parameters found?
    int beginning = 0,
        middle = 0,
        end = ( ca.length - 1 );
    String cmp;

    city = city.trim( ).toLowerCase( );

    //Do a binary search for the CITY name.
    while( !ret && ( beginning <= end ) )  {
      middle = ( ( beginning + end ) / 2 );

      if( city.equals( ( cmp = ca[middle].getCityName( ) ) ) )  {
        ret = true;
      }
      else if( city.compareTo( cmp ) < 0 )  {
        end = ( middle - 1 );
      }
      else  {
        beginning = ( middle + 1 );
      }
    }

    //Search for ZIPCODE if the CITY name was found and ZIPCODE is to be
    //  searched for.
    if( ret && ( zipcode >= 0 ) )  {
      ret &= ca[middle].searchForZipcode( zipcode );
    }

    return( ret );
  }  /*  public boolean searchCities( String )  */

}  /*  public class StateNode  */
