package com.optimasc.streams.png;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.zip.CRC32;

import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.internal.AbstractDocumentReader;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.ChunkUtilities;
import com.optimasc.streams.internal.DataReader;

public class PNGReader extends AbstractDocumentReader
{
  public byte[] byteBuffer = new byte[PNGUtilities.MAGIC_HEADER.length];
  protected ChunkUtilities chunkValidator;

  public PNGReader(InputStream inputStream, StreamFilter filter)
      throws DocumentStreamException
  {
    super(0, inputStream, filter);
    chunkValidator = new PNGUtilities();
  }

  protected void readChunkHeader(DataReader dataReader, ChunkInfo header)
      throws DocumentStreamException
  {
    long dataLength;
    long crc32;
    long calculatedcrc32;
    String chunkID = null;
    CRC32 crc = new CRC32();
    header.reset();
    dataLength = dataReader.read32Big() & 0xFFFFFFFFL;
    // Read the chunk identifier
    dataReader.read(byteBuffer, 0, 4);
    try
    {
      chunkID = new String(byteBuffer, 0, 4, "UTF-8");
    } catch (UnsupportedEncodingException e)
    {
      e.printStackTrace();
    }
    if (chunkValidator.isValidChunkSize(dataLength)==false)
    {
      errorHandler.warning(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_SIZE,chunkID));
    }
    
    try 
    {
      chunkID = chunkValidator.chunkIDToObject(chunkID);
    } catch (IllegalArgumentException e)
    {
      errorHandler.warning(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_ID,chunkID));
    }
    header.type = ChunkInfo.TYPE_CHUNK;
    header.size = dataLength;
    // 4 bytes for the CRC-32
    header.extraSize = 4;
    header.offset = dataReader.getPosition();
    header.id = chunkID;
    
    /*********************************** Integrity checking *********************************/

    /** Check the validity of the CRC-32 */
    dataReader.setPosition(dataReader.getPosition()+dataLength);
    crc32 = dataReader.read32Big() & 0xFFFFFFFFL;
    /** The chunk type is included in the CRC-32 calculation so subtract 4 bytes */
    dataReader.setPosition(header.offset-4);
    /* Temporary variable for new length used to calculate CRC-32 which includes the chunk type. */
    dataLength += 4;
    while (dataLength >= byteBuffer.length)
    {
      dataReader.read(byteBuffer,0,byteBuffer.length);
      crc.update(byteBuffer,0,byteBuffer.length);
      dataLength = dataLength - byteBuffer.length;
    }
    dataReader.read(byteBuffer,0,(int)dataLength);
    crc.update(byteBuffer,0,(int)dataLength);
    
    
    calculatedcrc32 = crc.getValue();
    if (calculatedcrc32 != crc32)
    {
      errorHandler.error(new DocumentStreamException(DocumentStreamException.ERR_CORRUPT_STREAM,chunkID));
    }
    dataReader.setPosition(header.offset);
     
  }
  
  

  protected DocumentInfo readDocumentHeader(DataReader dataReader) throws DocumentStreamException
  {
    try
    {
      dataReader.read(byteBuffer, 0, PNGUtilities.MAGIC_HEADER.length);
      if (Arrays.equals(byteBuffer, PNGUtilities.MAGIC_HEADER) == false)
      {
        return null;
      }
    } catch (DocumentStreamException e)
    {
      return null;
    }
    DocumentInfo document = new DocumentInfo("", PNGUtilities.MIME_TYPE,
        DocumentInfo.TYPE_BIG_ENDIAN,
        dataReader.getSize());
    return document;
  }

}
