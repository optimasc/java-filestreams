package com.optimasc.streams.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Stack;
import java.util.Vector;

import com.optimasc.io.AbstractDataOutputStream;
import com.optimasc.io.ByteOrder;
import com.optimasc.io.SeekableDataOutputStream;
import com.optimasc.io.SeekableDataStream;
import com.optimasc.streams.Attribute;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamWriter;
import com.optimasc.streams.ErrorHandler;

public abstract class AbstractDocumentWriter implements DocumentStreamWriter
{
  protected ChunkInfo currentChunk;
  protected Stack groups;
  protected ChunkInfo currentGroup;
  protected boolean bigEndian;
  protected int maxNesting;
  protected SeekableDataOutputStream dataWriter;
  protected ChunkUtilities validator;
  protected byte[] w = new byte[8];

  public AbstractDocumentWriter(boolean bigEndian, int maxNesting)
  {
    this.maxNesting = maxNesting;
    this.groups = new Stack();
    this.bigEndian = bigEndian;
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
      throws IOException;

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
      throws IOException;

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
      throws IOException;

  public abstract void writeStartDocument(String publicID)
      throws IOException;

  public void writeEndDocument() throws IOException
  {
    ChunkInfo groupInfo;
    // Verify if the current chunk has been closed.
    if (currentChunk.type != ChunkInfo.UNDEFINED_VALUE)
      throw new IllegalStateException(
          DocumentStreamException.ERR_BLOCK_NOT_CLOSED+
          " '"+currentChunk.id.toString()+"'");
    // Verify if all groups have been closed
    while (groups.isEmpty() == false)
    {
      groupInfo = (ChunkInfo) groups.pop();
      throw new IllegalStateException(
          DocumentStreamException.ERR_BLOCK_NOT_CLOSED+" '"+groupInfo.id.toString()+"'");
    }
  }


  public void writeStartElement(Object id, Attribute[] attributes) throws IOException
  {
    // Check if we currently have a valid chunk - it should not be possible
    // to have a valid chunk currently because nesting of chunks is not allowed.
    if (currentChunk.type != ChunkInfo.UNDEFINED_VALUE)
      throw new IllegalStateException(DocumentStreamException.ERR_INVALID_NESTING+id.toString());
    // Verify the validity of the identifier
    validator.chunkIDToObject(id);
    currentChunk.reset();
    currentChunk.id = id;
    currentChunk.setAttributes(attributes);
    currentChunk.type = ChunkInfo.TYPE_CHUNK;
    writeChunkHeader(currentChunk);
  }

  public void writeStartGroup(Object id, Attribute[] attributes) throws IOException
  {
    // Check if we are allowed to nest further
    if (groups.size() >= maxNesting)
    {
      throw new IllegalStateException(DocumentStreamException.ERR_INVALID_NESTING+id.toString());
    }
    // Verify the validity of the identifier
    validator.groupIDToObject(id);
    currentGroup = new ChunkInfo();
    currentGroup.reset();
    currentGroup.id = id;
    currentGroup.type = ChunkInfo.TYPE_GROUP;
    currentChunk.setAttributes(attributes);
    groups.push(currentGroup);
    writeChunkHeader(currentGroup);
  }

  public void writeEndElement() throws IOException
  {
    // Check if we currently have a valid chunk - if not then we have a problem.
    if (currentChunk.type == ChunkInfo.UNDEFINED_VALUE)
      throw new IllegalStateException(DocumentStreamException.ERR_INVALID_NESTING);
    // Verify the validity of the size
    validator.validateChunkSize(currentChunk.size);
    writeFixupChunkHeader(currentChunk);
    writeChunkFooter(currentChunk);
    // writeChunkHeader();
    // writeData();
    // writeChunkFooter();
    // Now there is no active chunk.
    currentChunk.type = ChunkInfo.UNDEFINED_VALUE;
  }

  public void writeEndGroup() throws IOException
  {
    if (groups.isEmpty() == true)
    {
      throw new IllegalStateException(
          DocumentStreamException.ERR_INVALID_NESTING);
    }
    currentGroup = (ChunkInfo) groups.pop();
    writeFixupChunkHeader(currentGroup);
    writeChunkFooter(currentGroup);
    // Verify the validity of the size, must be done after it is done by the specific implementation part.
    validator.validateGroupSize(currentGroup.size);
    // Now the current Group is the one on the top of the stack
    if (groups.isEmpty() == false)
      currentGroup = (ChunkInfo) groups.peek();
    else
      currentGroup = null;
  }

  public void close() throws IOException
  {
    dataWriter.close();
  }

  public Object getProperty(String name) throws IllegalArgumentException
  {
    return null;
  }

  public void writeChars(String text) throws IOException
  {
    dataWriter.writeChars(text);
  }

  public void writeByte(int b) throws IOException
  {
    dataWriter.write(b);
  }

  /** This should be overriden when calculating the checksum is required.
   *  This is used by all write methods in this class.
   */  
  public void write(byte[] buffer, int off, int len)
      throws IOException
  {
    // We are only allowed to write data values when in chunk data  
    if (currentChunk.type != ChunkInfo.TYPE_CHUNK)
    {
      throw new IllegalStateException(
          DocumentStreamException.ERR_INVALID_STATE);
    }
    dataWriter.write(buffer, off, len);
    currentChunk.size = currentChunk.size + len;
  }

  public void writeShort(int value) throws IOException
  {
    if (bigEndian)
    {
      w[1] = (byte)(value & 0xFF);
      w[0] = (byte)((value  >>> 8) & 0xFF);
      dataWriter.write(w, 0, 2);
    }
    else
    {
      w[0] = (byte)(value & 0xFF);
      w[1] = (byte)((value  >>> 8) & 0xFF);
      dataWriter.write(w, 0, 2);
    }
  }

  public void writeInt(int value) throws IOException
  {
    if (bigEndian)
    {
      w[0]   = (byte)(0xff & (value >> 24));
      w[1] = (byte)(0xff & (value >> 16));
      w[2] = (byte)(0xff & (value >> 8));
      w[3] = (byte)(0xff & value);
      dataWriter.write(w, 0, 4);
    }
    else
    {
      w[3] = (byte)(0xff & (value >> 24));
      w[2] = (byte)(0xff & (value >> 16));
      w[1] = (byte)(0xff & (value >> 8));
      w[0] = (byte)(0xff & value);
      dataWriter.write(w, 0, 4);
    }
  }

  public void writeFloat(float v) throws IOException
  {
    int j = Float.floatToIntBits(v);
    if (bigEndian)
    {
      writeInt(j);
    }
    else
    {
      writeInt(j);
    }
  }

  public void writeDouble(double v) throws IOException
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
      dataWriter.write(w,0,8);
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
      dataWriter.write(w,0,8);
    }
  }

  @Override
  public void write(int b) throws IOException
  {
    dataWriter.write(b);
  }

  @Override
  public void write(byte[] buffer) throws IOException
  {
    dataWriter.write(buffer);
  }

  @Override
  public void writeLong(long value) throws IOException
  {
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
      dataWriter.write(w,0,8);
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
      dataWriter.write(w,0,8);
    }
  }

  @Override
  public void writeBoolean(boolean v) throws IOException
  {
    if (v==true)
    {
      dataWriter.write(1);  
    } else
    {
      dataWriter.write(0);
    }
  }

  @Override
  public void writeChar(int v) throws IOException
  {
  }

  /** Overriden default implementation that supports writing
   *  ISO-8859-1 characters as raw array of octets. 
   * 
   */
  public void writeBytes(String s) throws IOException
  {
    int i;
    int c;
    for (i = 0; i < s.length(); i++)
    {
      c = s.charAt(i);
      if (c > 0xFF)
      {
        throw new UnsupportedEncodingException("Character at position "+Integer.toString(i)+" is over the value 255.");
      }
      dataWriter.write(c);
    }
  }

  public void writeUTF(String s) throws IOException
  {
  }

  public void setOutput(OutputStream os, String encoding) throws IOException
  {
    if ((os instanceof SeekableDataOutputStream)==false)
    {
      throw new IllegalArgumentException("'os' should be of instance of "+SeekableDataOutputStream.class.getName());
    }
    dataWriter = (SeekableDataOutputStream)os;
    if (bigEndian)
    {
      dataWriter.setByteOrder(ByteOrder.BIG_ENDIAN);
    }  else
    {
      dataWriter.setByteOrder(ByteOrder.LITTLE_ENDIAN);
    }
  }

  public void flush() throws IOException
  {
    dataWriter.flush();
  }
  
  


}
