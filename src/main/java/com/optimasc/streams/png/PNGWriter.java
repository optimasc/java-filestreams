package com.optimasc.streams.png;

import java.io.IOException;
import java.util.zip.CRC32;

import com.optimasc.streams.internal.AbstractDocumentWriter;
import com.optimasc.streams.internal.ChunkInfo;

public class PNGWriter extends AbstractDocumentWriter
{
  public byte[] byteBuffer = new byte[64];
  
  public PNGWriter()
  {
    super(true,1);
    validator = new PNGUtilities();
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws IOException
  {
      CRC32 crc = new CRC32();
      chunkData.internalObject = crc;
      String s = validator.chunkIDToObject(chunkData.id);
      byte[] id = s.getBytes();
      // SIZE : Put a fake value currently.
      dataWriter.writeInt(0);
      // Chunk identifier 
      dataWriter.write(id, 0, 4);
      /* Calculate CRC-32 of identifier */
      crc.update(id,0,4);
      chunkData.offset = dataWriter.getStreamPosition();
  }

  protected void writeFixupChunkHeader(ChunkInfo chunkData)
      throws IOException
  {
    long pos = dataWriter.getStreamPosition();
    // Set to position to where length value should be written (chunkData.offset - 8)
    dataWriter.seek(chunkData.offset-8);
    dataWriter.writeInt((int)chunkData.size);
    // Return back to our previous position plus any padding bytes
    dataWriter.seek(pos+chunkData.extraSize);
  }

  protected void writeChunkFooter(ChunkInfo chunkData)
      throws IOException
  {
    long crc32Value;
    
    dataWriter.seek(chunkData.offset+chunkData.size);
    crc32Value = ((CRC32)chunkData.internalObject).getValue();
    dataWriter.writeInt((int)crc32Value);
    /* On exit points to the next chunk. */
  }

  public void writeStartDocument(String publicID)
      throws IOException
  {
    dataWriter.write(PNGUtilities.MAGIC_HEADER, 0, PNGUtilities.MAGIC_HEADER.length);
  }

  public void writeEndDocument() throws IOException
  {
    super.writeEndDocument();
  }

  
  /** This calculates the CRC-32 value of the data also. */
  public void write(byte[] buffer, int off, int len) throws IOException
  {
    CRC32 crc = (CRC32) currentChunk.internalObject;
    crc.update(buffer, off, len);
    super.write(buffer, off, len);
  }

  /** This calculates the CRC-32 value of the data also. */
  public void write(int b) throws IOException
  {
    CRC32 crc = (CRC32) currentChunk.internalObject;
    w[0] = (byte) b;
    crc.update(w, 0, 1);
    super.write(b);
  }

  /** This calculates the CRC-32 value of the data also. */
  public void write(byte[] buffer) throws IOException
  {
    CRC32 crc = (CRC32) currentChunk.internalObject;
    crc.update(buffer, 0, buffer.length);
    super.write(buffer);
  }


}
