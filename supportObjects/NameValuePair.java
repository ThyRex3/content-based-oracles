package supportObjects;


public class NameValuePair  {
  String n,  //Name.
         v;  //Value.


  public NameValuePair( )  {
  }  /*  Constructor  */


  public NameValuePair( String name, String value )  {
    n = name;
    v = value;
  }  /*  Constructor  */


  public void setName( String name )  {
    n = name;
  }  /*  public void setName( String )  */


  public void setValue( String value )  {
    v = value;
  }  /*  public void setValue( String )  */


  public String getName( )  {
    return( n );
  }  /*  public String getName( )  */


  public String getValue( )  {
    return( v );
  }  /*  public String getValue( )  */

}  /*  public class NameValuePair  */
