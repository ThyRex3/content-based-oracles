package supportObjects;


public class ConfigurationPart  {
  private int r;
  private String qn;  //The qualified name of the oracle class definition.
  private String[] p;  //The parameters for the oracle.


  public ConfigurationPart( )  {
    r = -1;
    qn = "";
    p = null;
  }  /*  Constructor  */


  public ConfigurationPart( String qualifiedName )  {
    r = -1;
    qn = qualifiedName;
    p = null;
  }  /*  Constructor  */


  public void setRank( int rank )  {
    r = rank;
  }  /*  public void setRank( int )  */


  public void setQualifiedName( String qualifiedName )  {
    qn = qualifiedName;
  }  /*  public void setQualifiedName( String )  */


  public void addParameter( String parameter )  {
    String[] s;

    if( p != null )  {
      s = new String[( p.length + 1 )];

      System.arraycopy( p, 0, s, 0, p.length );
      p = s;
    }
    else  {
      p = new String[1];
    }

    p[( p.length - 1 )] = parameter;
  }  /*  public void addParameter( String )  */


  public int getRank( )  {
    return( r );
  }  /*  public int getRank( )  */


  public String getQualifiedName( )  {
    return( qn );
  }  /*  public String getQualifiedName( )  */


  public String[] getParameters( )  {
    return( p );
  }  /*  public String[] getParameters( )  */


  public String getParameter( int index )  {
    String ret = null;

    if( p != null )  {
      if( ( index >= 0 ) && ( index < p.length ) )  {
        ret = p[index];
      }
    }

    return( ret );
  }  /*  public String getParameter( int )  */


  public int parameterCount( )  {
    int ret = -1;

    if( p != null )  {
      ret = p.length;
    }

    return( ret );
  }  /*  public int parameterCount( )  */

}
