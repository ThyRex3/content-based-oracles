package supportObjects;

import dataStructures.IntegerLinkedList;


public class CityNode  {
  String cn;  //The city name.
  IntegerLinkedList hn;  //The head node of a list of zip codes.


  public CityNode( )  {
    cn = null;
    hn = null;
  }  /*  Constructor  */


  public CityNode( String cityName )  {
    cn = cityName;
    hn = null;
  }  /*  Constructor  */


  public void setCityName( String cityName )  {
    cn = cityName;
  }  /*  public void setCityName( String )  */


  public void insertZipcode( int zipcode )  {
    IntegerLinkedList zn = new IntegerLinkedList( zipcode, hn );

    hn = zn;
  }  /*  public void insertZipcode( int )  */


  public String getCityName( )  {
    return( cn );
  }  /*  public String getCityName( )  */


  /**
   * @return TRUE if the input parameter ZIPCODE was found in the list of
   *         ZipcodeNode objects, else FALSE.
   */
  public boolean searchForZipcode( int zipcode )  {
    boolean ret = false;
    IntegerLinkedList zn = hn;

    while( !ret && ( zn != null ) )  {
      if( zn.getValue( ) != zipcode )  {
        zn = zn.getNext( );
      }
      else  {
        ret = true;
      }
    }

    return( ret );
  }  /*  public boolean searchForZipcode( int )  */

}  /*  public class CityNode  */
