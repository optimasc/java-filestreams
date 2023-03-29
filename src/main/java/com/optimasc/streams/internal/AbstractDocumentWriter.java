package com.optimasc.streams.internal;

import java.util.Stack;
import java.util.Vector;

import org.apache.commons.vfs2.RandomAccessContent;

import com.optimasc.streams.Attribute;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamWriter;
import com.optimasc.streams.ErrorHandler;

public abstract class AbstractDocumentWriter implements DocumentStreamWriter,
    ErrorHandler
{

  protected ChunkInfo currentChunk;
  protected Stack groups;
  protected ChunkInfo currentGroup;
  protected DataWriter dataWriter;
  protected boolean bigEndian;
  protected int maxNesting;
  protected ChunkUtilities validator;
  protected byte[] w = new byte[8];

  public AbstractDocumentWriter(RandomAccessContent outputStream,
      boolean bigEndian, int maxNesting) throws DocumentStreamException
  {
    this.dataWriter = new DataWriter(outputStream, this);
    this.maxNesting = maxNesting;
    this.groups = new Stack();
    this.currentChunk = newChunkInfo();
  }
  
  /** This routine should return an instance of a Chunk, it can be overriden
   *  to return more specialised chunk types.
   * 
   * @return Allocated ChunkInfo structure
   */
  protected ChunkInfo newChunkInfo()
  {
    return new ChunkInfo();
  }  

  /**
   * This routine should write the chunk header. The position of the stream upon
   * entry into this routine is the last position where the data or last chunk footer 
   * was written.
   * 
   * Upon exit, it should position itself where the next data should be written to.
   * 
   * @param chunkData
   *          The chunk information that contains valid data
   */
  protected abstract void writeChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException;

  /**
   * This routine should write the chunk header with the correct values. The
   * position of the stream upon entry into this routine is the last position
   * where the data was written, therefore if necessary it should seek to the
   * correct chunk header position to write the data before writing it. 
   * 
   * Upon exit, it should position itself where the next chunk should be written to.
   * 
   * @param chunkData
   *          The chunk information that contains valid data
   */
  protected abstract void writeFixupChunkHeader(ChunkInfo chunkData)
      throws DocumentStreamException;

  /**
   * This routine should write any footer after the data of this
   * chunk.
   * 
   * Upon exit it should point to where the next chunk header should
   * be written.
   * 
   * @param chunkData 
   *          The chunk information that contains valid data
   */
  protected abstract void writeChunkFooter(ChunkInfo chunkData)
      throws DocumentStreamException;

  public abstract void writeStartDocument(String publicID)
      throws DocumentStreamException;

  public void writeEndDocument() throws DocumentStreamException
  {
    ChunkInfo groupInfo;
    // Verify if the current chunk has been closed.
    if (currentChunk.type != ChunkInfo.UNDEFINED_VALUE)
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_NOT_CLOSED,
          currentChunk.id.toString());
    // Verify if all groups have been closed
    while (groups.isEmpty() == false)
    {
      groupInfo = (ChunkInfo) groups.pop();
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_NOT_CLOSED, groupInfo.id.toString());
    }
  }

  // Default implementation writes the data as ISO-8859-1 characters
  public void writeCharacters(char[] text, int start, int len)
      throws DocumentStreamException
  {
    int i;
    byte[] outArray = new byte[len];
    // Convert to ISO-8859-1 character sets
    for (i = 0; i < len; i++)
    {
      if ((int)text[start+i] > (int)0xff)
      {
        outArray[i] = (byte) '?';
      } else
      {
        outArray[i] = (byte) text[start+i];
      }
    }
    writeOctetString(outArray, 0, len);
  }

  public void writeStartElement(Object id, Attribute[] attributes) throws DocumentStreamException
  {
    // Check if we currently have a valid chunk - it should not be possible
    // to have a valid chunk currently because nesting of chunks is not allowed.
    if (currentChunk.type != ChunkInfo.UNDEFINED_VALUE)
      throw new DocumentStreamException(
          DocumentStreamException.ERR_INVALID_NESTING, id.toString());
    // Verify the validity of the identifier
    try
    {
      validator.chunkIDToObject(id);
    } catch (IllegalArgumentException e)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_ID, id.toString());
    }
    currentChunk.reset();
    currentChunk.id = id;
    currentChunk.setAttributes(attributes);
    currentChunk.type = ChunkInfo.TYPE_CHUNK;
    writeChunkHeader(currentChunk);
  }

  public void writeStartGroup(Object id, Attribute[] attributes) throws DocumentStreamException
  {
    // Check if we are allowed to nest further
    if (groups.size() >= maxNesting)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_INVALID_NESTING, id.toString());
    }
    // Verify the validity of the identifier
    try
    {
      validator.groupIDToObject(id);
    } catch (IllegalArgumentException e)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_ID, id.toString());
    }
    currentGroup = new ChunkInfo();
    currentGroup.reset();
    currentGroup.id = id;
    currentGroup.type = ChunkInfo.TYPE_GROUP;
    currentChunk.setAttributes(attributes);
    groups.push(currentGroup);
    writeChunkHeader(currentGroup);
  }

  public void writeEndElement() throws DocumentStreamException
  {
    // Check if we currently have a valid chunk - if not then we have a problem.
    if (currentChunk.type == ChunkInfo.UNDEFINED_VALUE)
      throw new DocumentStreamException(
          DocumentStreamException.ERR_INVALID_NESTING);
    // Verify the validity of the size
    if (validator.isValidChunkSize(currentChunk.size) == false)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_SIZE,
          currentChunk.id.toString());
    }
    writeFixupChunkHeader(currentChunk);
    writeChunkFooter(currentChunk);
    // writeChunkHeader();
    // writeData();
    // writeChunkFooter();
    // Now there is no active chunk.
    currentChunk.type = ChunkInfo.UNDEFINED_VALUE;
  }

  public void writeEndGroup() throws DocumentStreamException
  {
    if (groups.isEmpty() == true)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_INVALID_NESTING);
    }
    currentGroup = (ChunkInfo) groups.pop();
    writeFixupChunkHeader(currentGroup);
    writeChunkFooter(currentGroup);
    // Verify the validity of the size, must be done after it is done by the specific implementation part.
    if (validator.isValidGroupSize(currentGroup.size) == false)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_SIZE,
          currentGroup.id.toString());
    }
    // Now the current Group is the one on the top of the stack
    if (groups.isEmpty() == false)
      currentGroup = (ChunkInfo) groups.peek();
    else
      currentGroup = null;
  }

  public void close() throws DocumentStreamException
  {
    dataWriter.flush();
  }

  public void flush() throws DocumentStreamException
  {
    dataWriter.flush();
  }

  public Object getProperty(String name) throws IllegalArgumentException
  {
    return null;
  }

  public void writeCharacters(String text) throws DocumentStreamException
  {
    char[] chars = text.toCharArray();
    writeCharacters(chars, 0, chars.length);
  }

  public void writeOctet(int b) throws DocumentStreamException
  {
    w[0] = (byte) (b & 0xFF);
    writeOctetString(w,0,1);
  }

  /** This should be overriden when calculating the checksum is required.
   *  This is used by all write methods in this class.
   */  
  public void writeOctetString(byte[] buffer, int off, int len)
      throws DocumentStreamException
  {
    // We are only allowed to write data values when in chunk data  
    if (currentChunk.type != ChunkInfo.TYPE_CHUNK)
    {
      throw new DocumentStreamException(
          DocumentStreamException.ERR_INVALID_STATE);
    }
    dataWriter.write(buffer, off, len);
    currentChunk.size = currentChunk.size + len;
  }

  public void writeWord(short value) throws DocumentStreamException
  {
    if (bigEndian)
    {
      w[1] = (byte)(value & 0xFF);
      w[0] = (byte)((value  >>> 8) & 0xFF);
      writeOctetString(w, 0, 2);
    }
    else
    {
      w[0] = (byte)(value & 0xFF);
      w[1] = (byte)((value  >>> 8) & 0xFF);
      writeOctetString(w, 0, 2);
    }
  }

  public void writeLongword(int value) throws DocumentStreamException
  {
    if (bigEndian)
    {
      w[0]   = (byte)(0xff & (value >> 24));
      w[1] = (byte)(0xff & (value >> 16));
      w[2] = (byte)(0xff & (value >> 8));
      w[3] = (byte)(0xff & value);
      writeOctetString(w, 0, 4);
    }
    else
    {
      w[3] = (byte)(0xff & (value >> 24));
      w[2] = (byte)(0xff & (value >> 16));
      w[1] = (byte)(0xff & (value >> 8));
      w[0] = (byte)(0xff & value);
      writeOctetString(w, 0, 4);
    }
  }

  public void writeSingle(float v) throws DocumentStreamException
  {
    int j = Float.floatToIntBits(v);
    if (bigEndian)
    {
      writeLongword(j);
    }
    else
    {
      writeLongword(j);
    }
  }

  public void writeDouble(double v) throws DocumentStreamException
  {
    long value = Double.doubleToLongBits(v);
    if (bigEndian)
    {
      w[0]   = (byte)(0xff & (value >> 56));
      w[1] = (byte)(0xff & (value >> 48));
      w[2] = (byte)(0xff & (value >> 40));
      w[3] = (byte)(0xff & (value >> 32));
      w[4] = (byte)(0xff & (value >> 24));
      w[5] = (byte)(0xff & (value >> 16));
      w[6] = (byte)(0xff & (value >> 8));
      w[7] = (byte)(0xff & value);
      writeOctetString(w,0,8);
    }
    else
    {
      w[7]   = (byte)(0xff & (value >> 56));
      w[6] = (byte)(0xff & (value >> 48));
      w[5] = (byte)(0xff & (value >> 40));
      w[4] = (byte)(0xff & (value >> 32));
      w[3] = (byte)(0xff & (value >> 24));
      w[2] = (byte)(0xff & (value >> 16));
      w[1] = (byte)(0xff & (value >> 8));
      w[0] = (byte)(0xff & value);
      writeOctetString(w,0,8);
    }
  }

}
