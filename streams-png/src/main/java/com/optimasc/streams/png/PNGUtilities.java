package com.optimasc.streams.png;

import com.optimasc.streams.internal.ChunkUtilities;

public class PNGUtilities extends ChunkUtilities
{
   public static final String MIME_TYPE = "image/png";
   
   public static final byte[] MAGIC_HEADER =  {(byte)137,80,78,71,13,10,26,10};

  /** Validates according to the PNG specification that this is a valid 
   *  chunk identifier.
   */
  public String chunkIDToObject(Object value) throws IllegalArgumentException
  {
    String id = value.toString();
    if (id.length() != 4)
      throw new IllegalArgumentException("Chunk identifier must be 4 characters in length.");

    for (int i = 0; i < id.length(); i++)
    {
      int c = id.charAt(i);
      // Valid characters
      if (((c >= 65) && (c <= 90)) || ( (c >= 97) && (c <= 122)))
      {
      } else
      {
        throw new IllegalArgumentException("Only ASCII characters are allowed in the chunk identifier");
      }
    }
    return id;
  }

  /** According to the PNG Specification 2nd Edition, the value
   *  read should be considered unsigned, but only values until
   *  Integer.MAX_VALUE are supported.
   */
  public void validateChunkSize(long size) throws IllegalArgumentException
  {
    if ((size > Integer.MAX_VALUE) || (size < 0))
    {
      throw new IllegalArgumentException("Size of element must be between 0 and "+Integer.toString(Integer.MAX_VALUE));
    }
  }

  public void validateGroupSize(long size) throws IllegalArgumentException
  {
    validateChunkSize(size);
  }

  public void isReserved(Object id) throws IllegalArgumentException
  {
  }

  // No groups in PNG specification.
  public String groupIDToObject(Object id) throws IllegalArgumentException
  {
    return null;
  }    
   
   
       
}
