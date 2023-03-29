package com.optimasc.streams.riff;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.DataReader;
import com.optimasc.streams.internal.AbstractDocumentReader;

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

  public RIFFReader(InputStream inputStream, StreamFilter filter) throws DocumentStreamException
  {
    super(64, inputStream, filter);
    riffValidator = new RIFFUtilities();
    bigEndian = false;
  }

  protected void readChunkHeader(DataReader dataReader, ChunkInfo header) throws DocumentStreamException
  {

    long dataLength;
    header.reset();
    // Read the chunk identifier
    dataReader.read(byteBuffer, 0, 4);
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
    if (bigEndian)
    {
      dataLength = dataReader.read32Big() & 0xFFFFFFFFL;
    } else
    {
      dataLength = dataReader.read32Little() & 0xFFFFFFFFL;
    }
    // Check reserved Chunk Types - never skip them
    if (riffValidator.isReserved(chunkID))
    {
      header.type = ChunkInfo.TYPE_GROUP;
      dataReader.read(byteBuffer, 0, 4);
      try
      {
        chunkID = new String(byteBuffer, "UTF-8");
      } catch (UnsupportedEncodingException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // Group elements should never be odd
      if ((dataLength & 0x01)==0x01)
      {
        errorHandler.error(new DocumentStreamException(DocumentStreamException.ERR_BLOCK_INVALID_SIZE,chunkID));
        // Add one padding byte
        header.extraSize = 1;
      }
      header.size = dataLength-4;
      header.offset = dataReader.getPosition();
      header.id = chunkID;
      //          header.realSize = ;
    } else
    {
      header.type = ChunkInfo.TYPE_CHUNK;
      header.size = dataLength;
      // Add one padding byte as expected.
      if ((dataLength & 0x01)==0x01)
      {
        header.extraSize = 1;
      }
      header.offset = dataReader.getPosition();
      header.id = chunkID;
    }
  }

  protected DocumentInfo readDocumentHeader(DataReader dataReader)
  {
    int type;
    long dataLength;
    // Read the chunk identifier
    try
    {
    dataReader.read(byteBuffer, 0, 4);
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
    } else 
    if (chunkID.equals("RIFX"))
    {
      type = DocumentInfo.TYPE_BIG_ENDIAN;
    }  else
      return null;
    
    // Read the data length
    if (bigEndian)
    {
      dataLength = dataReader.read32Big() & 0xFFFFFFFFL;
    } else
    {
      dataLength = dataReader.read32Little() & 0xFFFFFFFFL;
    }
    // Read the chunk identifier
    dataReader.read(byteBuffer, 0, 4);
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
    DocumentInfo document = new DocumentInfo(chunkID,MIME_TYPE,type,dataLength);
    return document;
    } catch (DocumentStreamException e)
    {
      return null;
    }
  }

}
