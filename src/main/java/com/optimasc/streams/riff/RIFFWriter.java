package com.optimasc.streams.riff;

import java.io.IOException;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.AbstractDocumentWriter;

public class RIFFWriter extends AbstractDocumentWriter
{
  protected long startDocumentPosition;
  
  public RIFFWriter()
  {
    super(false,255);
    validator = new RIFFUtilities();
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws IOException
  {
      chunkData.offset = dataWriter.getStreamPosition();
      String s = chunkData.id.toString();
      byte[] id = s.getBytes();
      // If a group the group identifier
      if (chunkData.type == ChunkInfo.TYPE_GROUP)
      {
          // Group header
          dataWriter.write(RIFFUtilities.LIST_HEADER, 0, 4);
          // SIZE : Put a fake value currently.
          dataWriter.writeInt(0);
          // ID
          dataWriter.write(id, 0, 4);
      } else
      {
        // Chunk identifier
        dataWriter.write(id, 0, 4);
        // SIZE : Put a fake value currently.
        dataWriter.writeInt(0);
      }
  }

  protected void writeFixupChunkHeader(ChunkInfo chunkData)
      throws IOException
  {
    long pos = dataWriter.getStreamPosition();
    if (chunkData.type == ChunkInfo.TYPE_CHUNK)
    {
      // We must add a padding byte if the value is odd
      if ((chunkData.size & 0x01) == 0x01)
      {
        dataWriter.write((byte) 0);
        chunkData.extraSize = 1;
      }
    } else
    {
      // We must add a padding byte if the value is odd
      if ((chunkData.size & 0x01) == 0x01)
      {
      // Groups should never be padded.
      throw new IllegalStateException(
          DocumentStreamException.ERR_BLOCK_INVALID_SIZE+" '" +chunkData.id.toString()+"'");
      }
      // Add the group identifier
      chunkData.size += 4;
    }
    // Set to position of chunk past the ID and go directly to size
    dataWriter.seek(chunkData.offset + 4);
    validator.validateChunkSize(chunkData.size);
    dataWriter.writeInt((int)chunkData.size);

    // Add the size to the current group if any exists.
    if (groups.isEmpty()==false)
    {
      ChunkInfo groupInfo = (ChunkInfo)groups.peek();
      // Add to the current group size the padding the data size + ckID + ckSize
      groupInfo.size += chunkData.size + chunkData.extraSize + 4 + 4;
    }
    
    // Return back to our previous position plus any padding bytes
    dataWriter.seek(pos+chunkData.extraSize);
  }

  // RIFF has no chunk footer
  protected void writeChunkFooter(ChunkInfo chunkData)
  {
    // Do nothing for RIFF
  }

  public void writeStartDocument(String publicID)
      throws IOException
  {
    startDocumentPosition = dataWriter.getStreamPosition();
    String s = validator.groupIDToObject(publicID);
    byte[] id = s.getBytes();

    // RIFF/RIFX
    dataWriter.write(RIFFUtilities.RIFF_HEADER, 0, 4);
    // SIZE : Put a fake value currently.
    dataWriter.writeInt(0);
    // ID
    dataWriter.write(id, 0, 4);
  }

  public void writeEndDocument() throws IOException
  {
    // Do some validation first.
    super.writeEndDocument();
    
    long size = dataWriter.length();
    // Set position to size of RIFF GROUP
    dataWriter.seek(startDocumentPosition + 4);
    // SIZE : Put the real value
    validator.validateGroupSize(size);
    dataWriter.writeInt((int)(size-8));
    // ID - skipped
  }

}
