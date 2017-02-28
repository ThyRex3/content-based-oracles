package dataStructures;

public class Node  {
  private char[] value;
  private int count;
  private Node next;


  public Node( )  {
    value = null;
    count = 0;
    next = null;
  }  /*  Constructor  */


  public Node( char[] value )  {
    this.value = trim( value, 0, value.length );
    count = 1;
    next = null;
  }  /*  Constructor  */


  public Node( char[] value, int sp, int len )  {
    this.value = trim( value, sp, len );
    count = 1;
    next = null;
  }  /*  Constructor  */


  private char[] trim( char[] arr, int sp, int len )  {
    boolean b;
    char[] ret = null;
    int ep = ( sp + len - 1 );

//Debug statement.
//System.out.println( "Trim() - 1... --  SP = " + sp + ";  EP = " + ep + "." );
//End debug statement.
    if( ( sp >= 0 ) && ( len > 0 ) && ( ( sp + len ) <= arr.length ) )  {
      b = true;
      while( b && ( sp < arr.length ) )  {
        if( Character.isWhitespace( arr[sp] ) )  {
          sp++;
        }
        else  {
          b = false;
        }
      }

      b = true;
      while( b && ( ep >= sp ) )  {
        if( Character.isWhitespace( arr[ep] ) )  {
          ep--;
        }
        else  {
          b = false;
        }
      }

//Debug statement.
//System.out.println( "Trim() - 2... --  SP = " + sp + ";  EP = " + ep + "." );
//End debug statement.
      if( ep >= sp )  {
        ret = new char[( ( ep + 1 ) - sp )];
        for( len = sp; len < ep; len++ )  {
          ret[( len - sp )] = Character.toLowerCase( arr[len] );
        }
      }
    }

    return( ret );
  }  /*  private char[] trim( char[] )  */


  public void setValue( char[] value )  {
    this.value = trim( value, 0, value.length );
  }  /*  public void setValue( char[] )  */


  public void setCount( int count )  {
    this.count = count;
  }  /*  public void setCount( int )  */


  public void incrementCount( )  {
    count++;
  }  /*  public void incrementCount( )  */


  public void setNext( Node next )  {
    this.next = next;
  }  /*  public void setNext( Node )  */


  public char[] getValue( )  {
    return( value );
  }  /*  public char[] getValue( )  */


  public int getCount( )  {
    return( count );
  }  /*  public int getCount( )  */


  public Node getNext( )  {
    return( next );
  }  /*  public Node getNext( )  */


  public boolean equals( char[] arr )  {
    return( equals( arr, 0, arr.length ) );
  }  /*  public boolean equals( char[] )  */


  public boolean equals( char[] arr, int sp, int len )  {
    boolean ret;
    int i = 0;

    ret = ( ( ( arr = trim( arr, sp, len ) ) != null ) &&
            ( value.length == arr.length ) );

    while( ret && ( i < value.length ) )  {
      if( value[i] == arr[i] )  {
        i++;
      }
      else  {
        ret = false;
      }
    }

    return( ret );
  }  /*  public boolean equals( char[], int, int )  */

}  /*  public class Node  */
