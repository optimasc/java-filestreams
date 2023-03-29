package com.optimasc.streams.internal;

public abstract class ChunkUtilities
{

  /** Returns if this chunk identifier is a reserved chunk 
   *  identifier. Reserved chunk identifiers are usually 
   *  only allowed for groups.
   * 
   * @param value The chunk identifier to validate.
   * @return True if this value is reserved or false if it can be used.
   */
  public boolean isReserved(Object id) throws IllegalArgumentException
  {
    return false;
  }
  
  
  /** Validates and converts to a string representation the specified
   *  chunk identifier.
   * 
   * @param id
   * @return
   * @throws IllegalArgumentException If the identifier is not valid.
   */
  public String chunkIDToObject(Object id) throws IllegalArgumentException
  {
    return id.toString();
  }
  
  /** Validates and converts to a string representation the specified
   *  group identifier.
   * 
   * @param id
   * @return
   * @throws IllegalArgumentException If the identifier is not valid.
   */
  public String groupIDToObject(Object id) throws IllegalArgumentException
  {
    return id.toString();
  }
  
  public boolean isValidChunkSize(long size)
  {
    return true;
  }
  
  public boolean isValidGroupSize(long size)
  {
    return true;
  }
  
}
