package oracles;

import supportObjects.FieldDescriptor;
import supportObjects.RecordEnumeration;


public interface PostProcess  {
  /**
   *   This method is used to update an existing RecordEnumeration on an oracle-
   * to-oracle basis.  This method is for hybrid and fully fixed files.
   * 
   * @return Though this method does not return anything it does have the affect
   *         of updating the RecordEnumeration parameter. 
   */
  void postProcess( char[][] records, FieldDescriptor[][] fd,
                    RecordEnumeration re );

  /**
   *   This method is used to update an existing RecordEnumeration on an oracle-
   * to-oracle basis.  This method is for fully delimited files.
   * 
   * @return Though this method does not return anything it does have the affect
   *         of updating the RecordEnumeration parameter. 
   */
  void postProcess( char[][][] records, FieldDescriptor[][] fd,
                    RecordEnumeration re );

}  /*  public interface PostProcess  */
