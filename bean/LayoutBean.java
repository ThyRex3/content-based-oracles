package bean;

import javax.ejb.Stateless;
import javax.jws.WebService;

import oracles.OracleGroup;
import supportObjects.GP;
import supportMethods.Setup;
import supportMethods.BasicAttributes;


@Stateless
@WebService( endpointInterface = "bean.Layout" )
public class LayoutBean implements Layout  {
  public String buildLayout( byte[] parameterData, byte[] fileSample,
                             String fileName )  {

    OracleGroup og = new OracleGroup( );

    if( parameterData != null )  {
      Setup.parseSource( parameterData, og );
    }
    else  {
      GP.defaultInitialization( );
      og.defaultInitialization( );
    }

    return( BasicAttributes.buildLayout( fileSample, og, fileName ) );
  }  /*  public String buildLayout( byte[], byte[], String )  */

}  /*  public class LayoutBean implements Layout  */
