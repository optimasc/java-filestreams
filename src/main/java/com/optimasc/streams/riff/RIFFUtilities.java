package com.optimasc.streams.riff;

import com.optimasc.streams.internal.ChunkUtilities;

public class RIFFUtilities extends ChunkUtilities
{
  
  private final static String[] reservedID = 
  {
   "RIFF",
   "RIFX",
   "LIST"
  };
  
  protected static final byte[] RIFF_HEADER = "RIFF".getBytes();
  protected static final byte[] RIFX_HEADER = "RIFX".getBytes();
  protected static final byte[] LIST_HEADER = "LIST".getBytes();

  public boolean isReserved(Object id)
  {
    String value = id.toString();
    for (int i=0; i < reservedID.length; i++)
    {
      if (value.equals(reservedID[i]))
      {
        return true;
      }
    }
    return false;
  }

  /** Validates and converts a chunk identifier to a canonical string
   *  representation. This validates according to the official specification
   *  the chunk identifier / leaf element node identifier and 
   *  validates that it is correct.
   * 
   * @param id The identifier to validate.
   * @return The canonical representation of the chunk if it is valid.
   * @throws IllegalArgumentException
   */
  public String chunkIDToObject(Object value) throws IllegalArgumentException
  {
    String id = value.toString();
    if (id.length() != 4)
      throw new IllegalArgumentException("Chunk identifier must be 4 characters in length.");

    for (int i = 0; i < id.length(); i++)
    {
      int c = id.charAt(i);
      if ((c < 0x20) || (c > 0x7E))
      {
        throw new IllegalArgumentException("Only ASCII characters are allowed in the chunk identifier");
      }
    }
    return id;
  }
  
  /** Validates and converts a group identifier to a canonical string
   *  representation. This validates according to the official specification
   *  the group identifier / element node identifier and 
   *  validates that it is correct.
   * 
   * @param id The identifier to validate.
   * @return The canonical representation of the chunk if it is valid.
   * @throws IllegalArgumentException
   */
  public String groupIDToObject(Object id) throws IllegalArgumentException
  {
    id =chunkIDToObject(id);
    if (isReserved(id)) 
     throw new IllegalArgumentException("Using reserved group identifier");
    return id.toString();
  }

  public boolean isValidChunkSize(long size)
  {
    if ((size < 0) || (size > ((long)Integer.MAX_VALUE*2)))
      return false;
    return true;
  }

  public boolean isValidGroupSize(long size)
  {
    if ((size < 0) || (size > ((long)Integer.MAX_VALUE*2)))
      return false;
    return true;
  }
  
  
  
  
}
