/**
 *   A linked list of sorts.
 */
package dataStructures;


public class IntegerLinkedList  {
  private int v;  //The value.
  private IntegerLinkedList n;  //The next entry in the list.


  public IntegerLinkedList( )  {
    v = -1;
    n = null;
  }  /*  Constructor  */


  public IntegerLinkedList( int value )  {
    v = value;
    n = null;
  }  /*  Constructor  */


  public IntegerLinkedList( int value, IntegerLinkedList next )  {
    v = value;
    n = next;
  }  /*  Constructor  */


  public void setValue( int value )  {
    v = value;
  }  /*  public void setZipcode( int )  */


  public void setNext( IntegerLinkedList next )  {
    n = next;
  }   /*  public void setNext( ZipcodeNode )  */


  public int getValue( )  {
    return( v );
  }  /*  public int getValue( )  */


  public IntegerLinkedList getNext( )  {
    return( n );
  }  /*  public ZipcodeNode getNext( )  */

}  /*  public class IntegerLinkedList  */
