package com.optimasc.streams.internal;


/** Abstract class that implements conversion and
 *  verification routines for chunks. 
 * 
 * @author Carl Eric Codere
 *
 */
public abstract class ChunkUtilities
{
  /** Returns if this chunk identifier is a reserved chunk 
   *  identifier. Reserved chunk identifiers are usually 
   *  only allowed for groups.
   * 
   * @param value The chunk identifier to validate.
   * @return True if this value is reserved or false if it can be used.
   */
//  public abstract void isReserved(Object id) throws IllegalArgumentException;
  
  /** Validates and converts to a string representation the specified
   *  chunk identifier.
   * 
   * @param id
   * @return
   * @throws IllegalArgumentException If the identifier is not valid.
   */
  public abstract String chunkIDToObject(Object id) throws IllegalArgumentException;
  
  /** Validates and converts to a string representation the specified
   *  group identifier.
   * 
   * @param id
   * @return
   * @throws IllegalArgumentException If the identifier is not valid.
   */
  public abstract String groupIDToObject(Object id) throws IllegalArgumentException;
  
  /** Verifies the validity of the chunk size, and
   *  throws an exception if it is not valid. 
   * 
   * @param size [in] The size of the chunk to write
   * @throws IllegalArgumentException Thrown when the 
   *   size is invalid for the underlying format.
   */
  public abstract void validateChunkSize(long size) throws IllegalArgumentException;
  
  
  /** Verifies the validity of the group size, and
   *  throws an exception if it is not valid. 
   * 
   * @param size [in] The size of the chunk to write
   * @throws IllegalArgumentException Thrown when the 
   *   size is invalid for the underlying format.
   */
  public abstract void validateGroupSize(long size) throws IllegalArgumentException;
  
}
