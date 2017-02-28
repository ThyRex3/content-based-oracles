package dataStructures;



public class LinkedList  {
  private Node leaf,
               root;


  public LinkedList( )  {
    leaf = null;
    root = null;
  }  /*  Constructor  */


  public void clear( )  {
    Node n = root,
         m;

    while( n != null )  {
      m = n.getNext( );
      n = null;
      n = m;
    }
  }  /*  public void clear( )  */


  public void insert( char[] value )  {
    insert( value, 0, value.length );
  }  /*  public void insert( char[] )  */


  public void insert( char[] value, int sp, int len )  {
    Node n = new Node( value, sp, len );
//Debug statement.
//System.out.println( new String( value, sp, len ) );
//if( n.getValue( ) == null )  {
//System.out.println( "N value is NULL." );
//}
//else  {
//System.out.println( "N value is not NULL." );
//}
//End debug statement.

    if( n.getValue( ) != null )  {
//Debug statement.
//System.out.println( "Insert()..." );
//End debug statement.
      if( leaf != null )  {
        leaf.setNext( n );
        leaf = n;
      }
      else  {
        root = n;
        leaf = n;
      }
    }
  }  /*  public void insert( char, int, int )  */


  public boolean searchAndInsert( char[] value )  {
    return( searchAndInsert( value, 0, value.length ) );
  }  /*  public boolean searchAndInsert( char[] )  */


  public boolean searchAndInsert( char[] value, int sp, int len )  {
    boolean ret = false;
    Node n = root;

//Debug statement.
//System.out.println( "SP = " + sp + ";  LEN = " + len + ";  ( SP + LEN ) = " + ( sp + len ) + " <= " + value.length + "." );
//End debug statement.
    if( ( sp >= 0 ) && ( len > 0 ) && ( ( sp + len ) <= value.length ) )  {
      while( !ret && ( n != null ) )  {
        if( n.equals( value, sp, len ) )  {
//Debug statement.
//System.out.println( "Found pre-existing entry, updating count." );
//End debug statement.
          ret = true;
          n.incrementCount( );
        }
        else  {
//Debug statement.
//System.out.println( "Moving to next." );
//End debug statement.
          n = n.getNext( );
        }
      }

      if( !ret )  {
//Debug statement.
//System.out.println( "Did not find, inserting." );
//End debug statement.
        insert( value, sp, len );
      }
    }

    return( ret );
  }  /*  public boolean searchAndInsert( char, int, int )  */


  public int length( )  {
    int ret = 0;
    Node n;

    for( n = root; n != null; n = n.getNext( ) )  {
      ret++;
    }

    return( ret );
  }  /*  public int count( )  */


  public Node maxCount( )  {
    Node n,
         ret;

    if( root != null )  {
      ret = root;
      for( n = root.getNext( ); n != null; n = n.getNext( ) )  {
        if( n.getCount( ) > ret.getCount( ) )  {
          ret = n;
        }
      }
    }
    else  {
      ret = null;
    }

    return( ret );
  }  /*  public Node maxCount( )  */


  public int totalCount( )  {
    int ret = 0;
    Node n;

    for( n = root; n != null; n = n.getNext( ) )  {
      ret += n.getCount( );
    }

    return( ret );
  }  /*  public int totalCount( )  */

}  /*  public class LinkedList  */
