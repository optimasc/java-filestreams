package com.optimasc.streams.png;

import com.optimasc.streams.internal.ChunkUtilities;

public class PNGUtilities extends ChunkUtilities
{
   public static final String MIME_TYPE = "image/png";
   
   public static final byte[] MAGIC_HEADER =  {(byte)137,80,78,71,13,10,26,10};

  public boolean isValidChunkSize(long size)
  {
    if ((size > Integer.MAX_VALUE) || (size < 0))
    {
      return false;
    }
    return true;
  }

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
   
   
       
}
