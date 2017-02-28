package oracles;


public interface Oracle  {
  void initialize( OracleGroup og, String[] args );
  boolean isValid( char[] array );
  boolean isValid( char[] array, int beginIndex, int length );
  boolean isValid( String field );
  /**  The maximum length a field of this type will assume.  */
  int getMaxLength( );
  int getGrouping( );
  /**
   *   The minimum number of records, as a percentage, that must have a valid
   * field  of the corresponding type for a potential field location to be
   * created.
   */
  double getMinPercentage( );
  /**
   *   Often a field will contain very few valid entries with the rest blank.
   * This does not conform to the statistical assumptions required by the
   * prototype and can often lead to errors in the report, particularly with
   * respect to false positives.  This method specifies the maximum number of
   * fields that may be blank as compared to the total number of records.
   */
  double getMaxBlankPercentage( );
  double matchHeader( String label );
  /**
   *   The label associated with this oracle.  The type corresponding to the
   * domain definition specified within this oracle.
   */
  String getName( );

}  /*  public interface Oracle  */
