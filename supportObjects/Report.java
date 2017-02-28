package supportObjects;

import java.util.ArrayList;


public class Report  {
  private ArrayList<String> al;


  public Report( )  {
    al = new ArrayList<String>( );
  }  /*  Constructor  */


  public void addReport( String report )  {
    al.add( report );
  }  /*  public addReport( String )  */


  public String toString( )  {
    int i;
    String ret = "";

    for( i = 0; i < al.size( ); i++ )  {
      ret = ret.concat( al.get( i ) + System.getProperty( "line.separator" ) );
    }

    return( ret );
  }  /*  public toString( )  */

}  /*  public class Report  */
