package com.optimasc.streams.jpeg;

import java.io.IOException;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.AbstractDocumentWriter;

public class JPEGWriter extends AbstractDocumentWriter
{
  public JPEGWriter()
  {
    super(true, 0);
    validator = new JPEGUtilities();
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws IOException
  {
    int id = Integer.parseInt(chunkData.id.toString());
    chunkData.offset = dataWriter.getStreamPosition();
    if (id != JPEGUtilities.JPEG_ID_DATA)
    {
      // Write segment marker
      dataWriter.writeByte(0xff);
      // Write segment identifier
      dataWriter.writeByte(id);
    }
    // Now check if we need to write the size element
    switch (id)
    {
    // THESE DO NOT HAVE ANY LENGTH BYTES! 
    case JPEGUtilities.JPEG_ID_TEM:
    case JPEGUtilities.JPEG_ID_RST0:
    case JPEGUtilities.JPEG_ID_rst1:
    case JPEGUtilities.JPEG_ID_rst2:
    case JPEGUtilities.JPEG_ID_rst3:
    case JPEGUtilities.JPEG_ID_rst4:
    case JPEGUtilities.JPEG_ID_rst5:
    case JPEGUtilities.JPEG_ID_rst6:
    case JPEGUtilities.JPEG_ID_rst7:
    case JPEGUtilities.JPEG_ID_SOI:
    case JPEGUtilities.JPEG_ID_EOI:
    case JPEGUtilities.JPEG_ID_DATA:
      break;
    // These segments have the length marker  
    default:
      dataWriter.writeShort(0);
    }
  }

  protected void writeFixupChunkHeader(ChunkInfo chunkData)
      throws IOException 
  {
    int offset = 0;
    int id = Integer.parseInt(chunkData.id.toString());
    long pos = dataWriter.getStreamPosition();
    // JPEG_ID_DATA does not have a segment identifier - but all others do
    if (id != JPEGUtilities.JPEG_ID_DATA)
    {
      // Except for JPEG_ID_DATA maximum size of segments cannot be bigger 
      // than MAX_SEGMENT_DATA_SIZE
      if (chunkData.size > JPEGUtilities.JPEG_MAX_DATA_SEGMENT_SIZE)
      {
        // Groups should never be padded.
        throw new IllegalArgumentException(
            DocumentStreamException.ERR_BLOCK_INVALID_SIZE+" '"+chunkData.id.toString()+"'");
      }
      // Skip segment marker and segment identifier to go to size
      offset += 2;
    }
    dataWriter.seek(chunkData.offset+offset);
    // Now check if we need to write the size element
    switch (id)
    {
    // THESE DO NOT HAVE ANY LENGTH BYTES! 
    case JPEGUtilities.JPEG_ID_TEM:
    case JPEGUtilities.JPEG_ID_RST0:
    case JPEGUtilities.JPEG_ID_rst1:
    case JPEGUtilities.JPEG_ID_rst2:
    case JPEGUtilities.JPEG_ID_rst3:
    case JPEGUtilities.JPEG_ID_rst4:
    case JPEGUtilities.JPEG_ID_rst5:
    case JPEGUtilities.JPEG_ID_rst6:
    case JPEGUtilities.JPEG_ID_rst7:
    case JPEGUtilities.JPEG_ID_SOI:
    case JPEGUtilities.JPEG_ID_EOI:
    case JPEGUtilities.JPEG_ID_DATA:
      break;
    // These segments have the length marker, add 2 to the size, as the size includes itself!  
    default:
    {
      dataWriter.writeShort((int)chunkData.size+2);
    }
    }
    // Return back to our previous position plus any padding bytes
    dataWriter.seek(pos+chunkData.extraSize);
  }

  protected void writeChunkFooter(ChunkInfo chunkData)
      throws IOException
  {
    // Nothing for JPEG files
  }

  public void writeStartDocument(String publicID)
      throws IOException
  {
//    dataWriter.writeByte(JPEGUtilities.JPEG_MARKER);
//    dataWriter.writeByte(JPEGUtilities.JPEG_ID_SOI);
  }
  
  public void writeEndDocument()
  throws IOException
  {
    super.writeEndDocument();
//    dataWriter.writeByte(JPEGUtilities.JPEG_MARKER);
//    dataWriter.writeByte(JPEGUtilities.JPEG_ID_EOI);
  }


}
