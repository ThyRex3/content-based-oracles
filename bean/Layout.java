package bean;

import java.rmi.Remote;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


@WebService
@SOAPBinding( style = Style.RPC )
public interface Layout extends Remote  {
  @WebMethod String buildLayout( byte[] parameterData, byte[] fileSample,
                                 String fileName );

}  /*  public interface Layout extends Remote  */
