package supportObjects;

import java.text.DecimalFormat;


public class LengthProperties  {
  private int rl;  //The record length.
  //The percentage of records where the field occurred at the corresponding
  //  Position.
  private double per;
  private Position pos;  //The Position properties of the field.


  public LengthProperties( )  {
    rl = -1;
    per = 0.0;
    pos = null;
  }


  public void setRecordLength( int recordLength )  {
    rl = recordLength;
  }  /*  public void setRecordLength( int )  */


  public void setPercentage( double percentage )  {
    per = percentage;
  }  /*  public void setPercentage( double )  */


  public void setPositionId( int id )  {
    if( pos == null )  {
      pos = new Position( );
    }

    pos.setId( id );
  }  /*  public void setPositionId( int )  */


  public void setPositionStart( int startPosition )  {
    if( pos == null )  {
      pos = new Position( );
    }

    pos.setPosition( startPosition );
  }  /*  public void setPositionStart( int )  */


  public void setPositionLength( int length )  {
    if( pos == null )  {
      pos = new Position( );
    }

    pos.setLength( length );
  }  /*  public void setPositionLength( int )  */


  public void setPosition( Position position )  {
    pos = position;
  }  /*  public void setPosition( Position )  */


  public int getRecordLength( )  {
    return( rl );
  }  /*  public int getRecordLength( )  */


  public double getPercentage( )  {
    return( per );
  }  /*  public double getPercentage( )  */


  public int getPositionId( )  {
    int ret = -1;

    if( pos != null )  {
      ret = pos.getId( );
    }

    return( ret );
  }  /*  public int getPositionId( )  */


  public int getPositionStart( )  {
    int ret = -1;

    if( pos != null )  {
      ret = pos.getPosition( );
    }

    return( ret );
  }  /*  public int getPositionStart( )  */


  public int getPositionLength( )  {
    int ret = -1;

    if( pos != null )  {
      ret = pos.getLength( );
    }

    return( ret );
  }  /*  public int getPositionLength( )  */


  public Position getPosition( )  {
    return( pos );
  }  /*  public Position getPosition( )  */


  public String toString( )  {
    DecimalFormat df = new DecimalFormat( "0.00" );

    return( "Record length = " + rl + ";  Percentage = " + df.format( per ) +
            ";  " + pos );
  }  /*  public String toString( )  */

}  /*  public class RecordLength  */
