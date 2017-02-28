package supportObjects;

import java.util.Enumeration;
import java.util.Hashtable;


public class HeaderLabel  {
  private int tc;  //Total count.
  private String ctl;  //The content type label.
  private Hashtable<String, Integer> ht;  //Maps a header label to a count.


  public HeaderLabel( )  {
    tc = 0;
    ctl = "";
    ht = new Hashtable<String, Integer>( );
  }  /*  Constructor  */


  public HeaderLabel( String label )  {
    tc = 0;
    ctl = label;
    ht = new Hashtable<String, Integer>( );
  }  /*  Constructor  */


  public void setCount( int count )  {
    tc = count;
  }  /*  public void setCount( int )  */


  public void setLabel( String label )  {
    ctl = label;
  }  /*  public void setLabel( String )  */


  public void addHeaderLabel( int count, String label )  {
    //Increment the total count by the count associated with the current header.
    tc += count;

//Debug statement.
//System.out.println( "Inserting label - " + label + ":" + count + "." );
//End debug statement.
    ht.put( label.toLowerCase( ), count );
  }  /*  public void addHeaderLabel( int, String )  */


  public int getCount( )  {
    return( tc );
  }  /*  public int getCount( )  */


  public String getLabel( )  {
    return( ctl );
  }  /*  public String getLabel( )  */


  public void incrementCount( String label )  {
    Integer cnt = ht.get( label );

    if( cnt != null )  {
      cnt++;  //Increment the count.

      ht.put( label, cnt );  //Update the table.
    }
  }  /*  public void incrementCount( String )  */


  public int searchForLabel( String label )  {
    Integer ret = ht.get( label.toLowerCase( ) );

    if( ret == null )  {
      ret = -1;
    }

    return( ret.intValue( ) );
  }  /*  public int searchForLabel( String )  */


  public LabelMatch getMatch( String label )  {
    LabelMatch ret = null;
    Integer i = ht.get( label.toLowerCase( ) );

    if( i != null )  {
//Debug statement.
//System.out.println( "Label = " + label.toLowerCase( ) + ";  " + "Count = " + i + ";  TC = " + tc + "." );
//End debug statement.
      ret = new LabelMatch( ( double )( ( double )i / ( double )tc ), ctl );
    }

    return( ret );
  }  /*  public LabelMatch searchForLabel( String )  */


  public String toString( )  {
    Enumeration<Integer> v = ht.elements( );
    Enumeration<String> k = ht.keys( );
    StringBuffer ret = new StringBuffer( ctl );

    ret.append( System.getProperty( "line.separator" ) );
    for( ; v.hasMoreElements( ); )  {
      ret.append( k.nextElement( ).concat( ":" ) );
      ret.append( v.nextElement( ).intValue( ) );
      ret.append( System.getProperty( "line.separator" ) );
    }

    return( ret.toString( ) );
  }  /*  public String toString( )  */

}  /*  public class HeaderLabel  */
