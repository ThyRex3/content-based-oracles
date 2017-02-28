package oracles;

import supportObjects.FieldDescriptor;


public interface PreProcess  {
  /**
   *   This method is used to update a list of Potential Field Positions
   * associated with a single oracle, the oracle the method is invoked upon.
   * This method is for hybrid and fully fixed files.
   * 
   * @return Though this method does not return anything it does have the affect
   *         of updating the RecordEnumeration parameter. 
   */
  FieldDescriptor[] preProcess( char[][] records, FieldDescriptor[] fd );

  /**
   *   This method is used to update a list of Potential Field Positions
   * associated with a single oracle, the oracle the method is invoked upon.
   * This method is for fully delimited files.
   * 
   * @return Though this method does not return anything it does have the affect
   *         of updating the RecordEnumeration parameter. 
   */
  FieldDescriptor[] preProcess( char[][][] records, FieldDescriptor[] fd );

}  /*  public interface PreProcess  */
