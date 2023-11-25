package com.optimasc.streams.riff;

import java.io.EOFException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.optimasc.io.ByteBufferIO;
import com.optimasc.io.ByteOrder;
import com.optimasc.io.SeekableDataInputStream;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.AbstractDocumentReader;
import com.optimasc.streams.internal.ChunkInfo;

/**
 * Implements a RIFF Chunk reader.
 * 
 * @author ccodere
 * 
 */
public class RIFFReader extends AbstractDocumentReader
{
  public static final String MIME_TYPE = "application/x-riff";

  public byte[] byteBuffer = new byte[4];
  protected boolean bigEndian;
  RIFFUtilities riffValidator;

  public RIFFReader()
  {
    super(64);
    riffValidator = new RIFFUtilities();
    bigEndian = false;
  }

  protected void readChunkHeader(SeekableDataInputStream dataReader, ChunkInfo header)
      throws DocumentStreamException, IOException
  {

    long dataLength;
    header.reset();
    // Read the chunk identifier
    dataReader.readFully(byteBuffer, 0, 4);
    String chunkID = null;
    try
    {
      chunkID = new String(byteBuffer, "UTF-8");
    } catch (UnsupportedEncodingException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // Read the data length
    dataLength = dataReader.readUnsignedInt() & 0xFFFFFFFFL;
    // Check reserved Chunk Types - never skip them
    if (riffValidator.isReserved(chunkID))
    {
      header.type = ChunkInfo.TYPE_GROUP;
      dataReader.readFully(byteBuffer, 0, 4);
      try
      {
        chunkID = new String(byteBuffer, "UTF-8");
      } catch (UnsupportedEncodingException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // Group elements should never be odd
      if ((dataLength & 0x01) == 0x01)
      {
        errorHandler.error(new DocumentStreamException(
            DocumentStreamException.ERR_BLOCK_INVALID_SIZE, chunkID));
        // Add one padding byte
        header.extraSize = 1;
      }
      header.size = dataLength - 4;
      header.offset = dataReader.getStreamPosition();
      header.id = chunkID;
      //          header.realSize = ;
    }
    else
    {
      header.type = ChunkInfo.TYPE_CHUNK;
      header.size = dataLength;
      // Add one padding byte as expected.
      if ((dataLength & 0x01) == 0x01)
      {
        header.extraSize = 1;
      }
      header.offset = dataReader.getStreamPosition();
      header.id = chunkID;
    }
  }

  protected DocumentInfo readDocumentHeader(SeekableDataInputStream dataReader) throws IOException, DocumentStreamException
  {
    int type;
    long dataLength;
    // Read the chunk identifier
    try
    {
      dataReader.readFully(byteBuffer, 0, 4);
      String chunkID = null;
      try
      {
        chunkID = new String(byteBuffer, "UTF-8");
      } catch (UnsupportedEncodingException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      
      if (chunkID.equals("RIFF"))
      {
        type = DocumentInfo.TYPE_LITTLE_ENDIAN;
        reader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      }
      else if (chunkID.equals("RIFX"))
      {
        type = DocumentInfo.TYPE_BIG_ENDIAN;
        reader.setByteOrder(ByteOrder.BIG_ENDIAN);
      }
      else
        return null;

      // Read the data length
      dataReader.readFully(byteBuffer, 0, 4);
      if (type == DocumentInfo.TYPE_LITTLE_ENDIAN)
      {
       dataLength = ByteBufferIO.getIntLittle(byteBuffer, 0) & 0xFFFFFFFFL;
      } else
      {
        dataLength = ByteBufferIO.getIntBig(byteBuffer, 0) & 0xFFFFFFFFL;
      }
      // Read the chunk identifier
      dataReader.readFully(byteBuffer, 0, 4);
      try
      {
        chunkID = new String(byteBuffer, "UTF-8");
      } catch (UnsupportedEncodingException e1)
      {
        // TODO Auto-generated catch block
        e1.printStackTrace();
      }
      try
      {
        riffValidator.chunkIDToObject(chunkID);
      } catch (IllegalArgumentException e)
      {
        return null;
      }
      DocumentInfo document = new DocumentInfo(chunkID, MIME_TYPE, type, dataLength);
      return document;
    } catch (EOFException e)
    {
      return null;
    }
  }

}
