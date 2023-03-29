package com.optimasc.streams.riff;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.AbstractDocumentWriter;

public class RIFFWriter extends AbstractDocumentWriter
{
  protected long startDocumentPosition;
  

  public RIFFWriter(RandomAccessContent outputStream, boolean bigEndian)
      throws DocumentStreamException
  {
    super(outputStream, bigEndian,255);
    validator = new RIFFUtilities();
  }

  protected void writeChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException
  {
    try
    {
      chunkData.offset = dataWriter.getPosition();
      String s = chunkData.id.toString();
      byte[] id = s.getBytes();
      // If a group the group identifier
      if (chunkData.type == ChunkInfo.TYPE_GROUP)
      {
          // Group header
          dataWriter.write(RIFFUtilities.LIST_HEADER, 0, 4);
          // SIZE : Put a fake value currently.
          dataWriter.write32Big(0);
          // ID
          dataWriter.write(id, 0, 4);
      } else
      {
        // Chunk identifier
        dataWriter.write(id, 0, 4);
        // SIZE : Put a fake value currently.
        dataWriter.write32Big(0);
      }
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
    if (chunkData.type == ChunkInfo.TYPE_CHUNK)
    {
      // We must add a padding byte if the value is odd
      if ((chunkData.size & 0x01) == 0x01)
      {
        dataWriter.write8((byte) 0);
        chunkData.extraSize = 1;
      }
    } else
    {
      // We must add a padding byte if the value is odd
      if ((chunkData.size & 0x01) == 0x01)
      {
      // Groups should never be padded.
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_SIZE, chunkData.id.toString());
      }
      // Add the group identifier
      chunkData.size += 4;
    }
    // Set to position of chunk past the ID and go directly to size
    dataWriter.setPosition(chunkData.offset + 4);
    if (bigEndian)
      dataWriter.write32Big(chunkData.size);
     else
       dataWriter.write32Little(chunkData.size);

    // Add the size to the current group if any exists.
    if (groups.isEmpty()==false)
    {
      ChunkInfo groupInfo = (ChunkInfo)groups.peek();
      // Add to the current group size the padding the data size + ckID + ckSize
      groupInfo.size += chunkData.size + chunkData.extraSize + 4 + 4;
    }
    
    // Return back to our previous position plus any padding bytes
    dataWriter.setPosition(pos+chunkData.extraSize);
  }

  // RIFF has no chunk footer
  protected void writeChunkFooter(ChunkInfo chunkData)
  {
    // Do nothing for RIFF
  }

  public void writeStartDocument(String publicID)
      throws DocumentStreamException
  {
    try
    {
      startDocumentPosition = dataWriter.getPosition();
      String s = validator.groupIDToObject(publicID);
      byte[] id = s.getBytes();

      // RIFF/RIFX
      if (bigEndian)
        dataWriter.write(RIFFUtilities.RIFX_HEADER, 0, 4);
      else
        dataWriter.write(RIFFUtilities.RIFF_HEADER, 0, 4);
      // SIZE : Put a fake value currently.
      dataWriter.write32Big(0);
      // ID
      dataWriter.write(id, 0, 4);

    } catch (IllegalArgumentException e)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_ID, publicID);
    }
  }

  public void writeEndDocument() throws DocumentStreamException
  {
    // Do some validation first.
    super.writeEndDocument();
    
    long size = dataWriter.getSize();
    // Set position to size of RIFF GROUP
    dataWriter.setPosition(startDocumentPosition + 4);
    // SIZE : Put the real value
    if (validator.isValidGroupSize(size)==false)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_SIZE);
    }
    if (bigEndian)
     dataWriter.write32Big(size-8);
    else
      dataWriter.write32Little(size-8);
    // ID - skipped
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


  

}
