package com.optimasc.streams.jpeg;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.AbstractDocumentWriter;

public class JPEGWriter extends AbstractDocumentWriter
{
  public JPEGWriter(RandomAccessContent outputStream) throws DocumentStreamException
  {
    super(outputStream, true, 0);
    validator = new JPEGUtilities();
  }

  public void warning(DocumentStreamException exception)
      throws DocumentStreamException
  {
  }

  public void error(DocumentStreamException exception)
      throws DocumentStreamException
  {
  }

  public void fatalError(DocumentStreamException exception)
      throws DocumentStreamException
  {
    throw exception;
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    int id = Integer.parseInt(chunkData.id.toString());
    chunkData.offset = dataWriter.getPosition();
    if (id != JPEGUtilities.JPEG_ID_DATA)
    {
      // Write segment marker
      dataWriter.write8(0xff);
      // Write segment identifier
      dataWriter.write8(id);
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
      dataWriter.write16Big(0);
    }
  }

  protected void writeFixupChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    int offset = 0;
    int id = Integer.parseInt(chunkData.id.toString());
    long pos = dataWriter.getPosition();
    // JPEG_ID_DATA does not have a segment identifier - but all others do
    if (id != JPEGUtilities.JPEG_ID_DATA)
    {
      // Except for JPEG_ID_DATA maximum size of segments cannot be bigger 
      // than MAX_SEGMENT_DATA_SIZE
      if (chunkData.size > JPEGUtilities.JPEG_MAX_DATA_SEGMENT_SIZE)
      {
        // Groups should never be padded.
        throw new DocumentStreamException(
            DocumentStreamException.ERR_BLOCK_INVALID_SIZE, chunkData.id.toString());
      }
      // Skip segment marker and segment identifier to go to size
      offset += 2;
    }
    dataWriter.setPosition(chunkData.offset+offset);
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
      dataWriter.write16Big((int)chunkData.size+2);
    }
    }
    // Return back to our previous position plus any padding bytes
    dataWriter.setPosition(pos+chunkData.extraSize);
  }

  protected void writeChunkFooter(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    // Nothing for JPEG files
  }

  public void writeStartDocument(String publicID)
      throws DocumentStreamException
  {
//    dataWriter.write8(JPEGUtilities.JPEG_MARKER);
//    dataWriter.write8(JPEGUtilities.JPEG_ID_SOI);
  }
  
  public void writeEndDocument()
  throws DocumentStreamException
  {
    super.writeEndDocument();
//    dataWriter.write8(JPEGUtilities.JPEG_MARKER);
//    dataWriter.write8(JPEGUtilities.JPEG_ID_EOI);
  }
  

}
