package supportObjects;

import java.text.DecimalFormat;


public class LabelMatch  {
  private double p;  //Probability.
  private String ctl;  //Content type label name.


  public LabelMatch( )  {
    p = 0.0;
    ctl = "";
  }  /*  Constructor  */


  public LabelMatch( double probability, String labelName )  {
    p = probability;
    ctl = labelName;
  }  /*  Constructor  */


  private String space( int level )  {
    int i;
    StringBuffer ret = new StringBuffer( );

    level *= 2;

    for( i = 0; i < level; i++ )  {
      ret.append( ' ' );
    }

    return( ret.toString( ) );
  }  /*  private String space( int )  */


  public void setProbability( double probability )  {
    p = probability;
  }  /*  public void setProbability( double )  */


  public void setLabelName( String labelName )  {
    ctl = labelName;
  }  /*  public void setLabelName( String )  */


  public double getProbability( )  {
    return( p );
  }  /*  public double getProbability( )  */


  public String getLabelName( )  {
    return( ctl );
  }  /*  public String getLabelName( )  */


  public String toXML( int level )  {
    DecimalFormat df = new DecimalFormat( "0.00" );

    return( space( level ) + "<headerlabel>" +
            System.getProperty( "line.separator" ) + space( level + 1 ) +
            "<label>" + ctl + "</label>" +
            System.getProperty( "line.separator" ) + space( level + 1 ) +
            "<probability>" + df.format( p ) + "</probability>" +
            System.getProperty( "line.separator" ) + space( level ) +
            "</headerlabel>" + System.getProperty( "line.separator" ) );

  }  /*  public String toXML( int )  */

}  /*  public class LabelMatch  */
