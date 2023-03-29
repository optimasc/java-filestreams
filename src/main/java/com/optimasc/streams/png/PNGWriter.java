package com.optimasc.streams.png;

import java.util.zip.CRC32;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.AbstractDocumentWriter;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.riff.RIFFUtilities;

public class PNGWriter extends AbstractDocumentWriter
{
  public byte[] byteBuffer = new byte[64];
  
  public PNGWriter(RandomAccessContent outputStream)
      throws DocumentStreamException
  {
    super(outputStream, true,1);
    validator = new PNGUtilities();
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    try
    {
      CRC32 crc = new CRC32();
      chunkData.internalObject = crc;
      String s = validator.chunkIDToObject(chunkData.id);
      byte[] id = s.getBytes();
      // SIZE : Put a fake value currently.
      dataWriter.write32Big(0);
      // Chunk identifier 
      dataWriter.write(id, 0, 4);
      /* Calculate CRC-32 of identifier */
      crc.update(id,0,4);
      chunkData.offset = dataWriter.getPosition();
    } catch (IllegalArgumentException e)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_ID, chunkData.id.toString());
    }
  }

  protected void writeFixupChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    long pos = dataWriter.getPosition();
    // Set to position to where length value should be written (chunkData.offset - 8)
    dataWriter.setPosition(chunkData.offset-8);
    dataWriter.write32Big(chunkData.size);
    // Return back to our previous position plus any padding bytes
    dataWriter.setPosition(pos+chunkData.extraSize);
  }

  protected void writeChunkFooter(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    long crc32Value;
    
    dataWriter.setPosition(chunkData.offset+chunkData.size);
    crc32Value = ((CRC32)chunkData.internalObject).getValue();
    dataWriter.write32Big(crc32Value);
    /* On exit points to the next chunk. */
  }

  public void writeStartDocument(String publicID)
      throws DocumentStreamException
  {
    try
    {
      dataWriter.write(PNGUtilities.MAGIC_HEADER, 0, PNGUtilities.MAGIC_HEADER.length);
    } catch (IllegalArgumentException e)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_ID, publicID);
    }
  }

  public void writeEndDocument() throws DocumentStreamException
  {
    super.writeEndDocument();
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

  /** This calculates the CRC-32 value of the data also. */
  public void writeOctetString(byte[] buffer, int off, int len) throws DocumentStreamException
  {
    CRC32 crc = (CRC32) currentChunk.internalObject;
    crc.update(buffer, off, len);
    super.writeOctetString(buffer, off, len);
  }

  

}
