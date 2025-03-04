package com.optimasc.zip;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;

import com.optimasc.date.DOSDate;
import com.optimasc.date.DateConverter;
import com.optimasc.date.DateTimeEpochs;
import com.optimasc.date.NTFSDateTime;
import com.optimasc.io.ByteOrder;
import com.optimasc.io.SeekableDataInputStream;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.AbstractDocumentReader;
import com.optimasc.streams.internal.ResourceChunkInfo;

/**
 * Basic ZIP File reader parser. In this parser, each chunk represents
 * a filename and its associated compressed data, the
 * <code>ID</code> of the chunk represents the filename in the zip file
 * represented as a {@link java.lang.String}, and the data is the actual data stored
 * for that file.
 *
 * <p>The standard resource attributes
 * {@link com.optimasc.streams.internal.ResourceType} are available.
 *
 * 
 * @author Carl Eric Codere
 * 
 */
public class ZIPReaderImpl extends AbstractDocumentReader
{
  protected ZIPUtilities zipValidator;
  protected byte[] filenameBuffer = new byte[65535];
  protected byte[] extraFieldBuffer = new byte[65535];
  protected byte[] commentBuffer = new byte[65535];
  protected byte[] readBuffer = new byte[64];
  protected long centralDirectoryEntryOffset;
  /** Local header entries */
  protected Hashtable entries;

  public ZIPReaderImpl()
  {
    super(64);
    zipValidator = new ZIPUtilities();
    entries = new Hashtable();
  }

  protected void readChunkHeader(SeekableDataInputStream dataReader, ChunkInfo header)
      throws DocumentStreamException, IOException
  {

    long dataLength;
    int w;
    int compression;
    long lw;
    long compressedSize;
    long crc32;
    int flags;
    int fileNameLength;
    int extraFieldLength;
    int commentLength;
    long relativeOffset;
    long uncompressedSize;
    int time, date;
    String encoding;
    String filename = null;
    String fileComment;
    byte[] intBuffer = new byte[4];

    long id;
    header.reset();
    reader.seek(centralDirectoryEntryOffset);
    // Read the chunk identifier
    id = dataReader.readUnsignedInt() & 0xFFFFFFFFL;
    if (id != ZIPUtilities.MAGIC_CENTRAL_DIRECTORY_HEADER)
    {
      errorHandler.fatalError(new DocumentStreamException(
          DocumentStreamException.ERR_BLOCK_INVALID_ID));
    }
    
        // Version made by
    w = dataReader.readUnsignedShort();
    // Version needed to extract 
    w = dataReader.readUnsignedShort();
    // General purpose bit flag 
    flags = dataReader.readUnsignedShort();
    // Compression method
    compression = dataReader.readUnsignedShort();
    // Last modification file time    
    time = dataReader.readUnsignedShort();
    // Last modification file date    
    date = dataReader.readUnsignedShort();
    // CRC-32 - read little endian and store in big endian
    crc32 = dataReader.readUnsignedInt();

    // compressed size
    compressedSize = dataReader.readUnsignedInt();
    // uncompressed size
    uncompressedSize = dataReader.readUnsignedInt();
    // filename length
    fileNameLength = dataReader.readUnsignedShort();
    // extra field length
    extraFieldLength = dataReader.readUnsignedShort();
    // file comment length
    commentLength = dataReader.readUnsignedShort();
    // disk number start
    w = dataReader.readUnsignedShort();
    // internal attributes
    w = dataReader.readUnsignedShort();
    // external attributes
    lw = dataReader.readUnsignedInt();
    // external attributes
    relativeOffset = dataReader.readUnsignedInt();

    dataReader.read(filenameBuffer, 0, fileNameLength);
    dataReader.read(extraFieldBuffer, 0, extraFieldLength);
    dataReader.read(commentBuffer, 0, commentLength);

    encoding = "ISO-8859-1";
    /* PKWare supports native UTF-8 encoding */
    if ((flags & ZIPUtilities.UTF8_ENCODING_BIT) == ZIPUtilities.UTF8_ENCODING_BIT)
    {
      encoding = "UTF-8";
    }
    try
    {
      filename = new String(filenameBuffer, 0, fileNameLength, encoding);
      fileComment = new String(commentBuffer, 0, commentLength, encoding);
    } catch (UnsupportedEncodingException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO));
    }

    /* Determine if we need to complete the local header or simply create a new complete entry. */
    ResourceChunkInfo extHeader = (ResourceChunkInfo) entries.get(filename);
    if (extHeader == null)
    {
      extHeader = new ResourceChunkInfo();
      extHeader.reset();
    }

    if ((flags & ZIPUtilities.ENCRYPTED_BIT) == ZIPUtilities.ENCRYPTED_BIT)
    {
      extHeader.setEncrypted(new Boolean(true));
    }
    extHeader.offset = getFileDataOffset(relativeOffset);
    extHeader.size = compressedSize;
    extHeader.type = ChunkInfo.TYPE_CHUNK;
    extHeader.setSize(uncompressedSize);
    extHeader.setCompression(ZIPUtilities.compressionToString(compression));
    extHeader.setDigestType("CCITT CRC-32");
    extHeader.setLastModifiedDate(DOSDate.DOSDateAndTimeToCalendar(date, time).getTime());
    // Write the value in big endian
    intBuffer[0] = (byte) ((crc32 >>> 24) & 0xFF);
    intBuffer[1] = (byte) ((crc32 >>> 16) & 0xFF);
    intBuffer[2] = (byte) ((crc32 >>> 8) & 0xFF);
    intBuffer[3] = (byte) ((crc32 >>> 0) & 0xFF);
    extHeader.setDigest(intBuffer);

    if (extraFieldLength != 0)
    {
      parseExtField(extraFieldBuffer, extraFieldLength, extHeader);
    }

    centralDirectoryEntryOffset = reader.getStreamPosition();
    reader.seek(header.offset);

    /* Convert the data to attributes */
    extHeader.addStandardAttributes();
    /* Copy the data to the actual header. */
    header.copy(extHeader);

  }

  /* From an offset to the local file header, return the absolute offset into
   * the actual file data. 
   * 
   */
  protected long getFileDataOffset(long localHeaderOffset) throws DocumentStreamException, IOException
  {
    long dataLength;
    int w;
    long lw;
    long compressedSize;
    int flags;
    int fileNameLength;
    int extraFieldLength;
    long id;
    long offset;
    long pos;

    pos = reader.getStreamPosition();
    reader.seek(localHeaderOffset);

    // Read the chunk identifier
    id = reader.readUnsignedInt();
    if (id != ZIPUtilities.MAGIC_LOCAL_HEADER)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO));
    }

    // Read the entire values to retrieve the name of the file

    // Version
    w = reader.readUnsignedShort();
    // General purpose bit flag 
    flags = reader.readUnsignedShort();
    // Compression method
    w = reader.readUnsignedShort();
    // Last modification file time    
    w = reader.readUnsignedShort();
    // Last modification file date    
    w = reader.readUnsignedShort();
    // CRC-32
    lw = reader.readUnsignedInt();
    // compressed size
    compressedSize = reader.readUnsignedInt();
    // uncompressed size
    lw = reader.readUnsignedInt();
    // filename length
    fileNameLength = reader.readUnsignedShort();
    // extra field length
    extraFieldLength = reader.readUnsignedShort();

    offset = reader.getStreamPosition() + fileNameLength + extraFieldLength;

    if ((flags & ZIPUtilities.DATA_DESCRIPTOR_BIT) == ZIPUtilities.DATA_DESCRIPTOR_BIT)
    {
      // DATA descriptor signature is optional.
      id = reader.readUnsignedInt();
      if (id != ZIPUtilities.MAGIC_DATA_DESCRIPTOR)
      {
        offset += 8;
      } else
      {
        offset += 12;
      }
    }

    reader.seek(pos);
    return offset;
  }

  /**
   * Parse the actual extra data fields, and fill up the ResourceChunkInfo
   * attributes accordingly.
   * 
   * @param extraDataBuffer
   *          The byte array containing the extra field data.
   * @param extraLength
   *          Total length of extra buffer.
   * @param chunk
   *          The chunk to populate.
   * @throws DocumentStreamException
   */
  protected void parseExtField(byte[] extraDataBuffer, int extraLength, ResourceChunkInfo chunk)
      throws DocumentStreamException
  {
    String s;

    //------------- Check if we have unicode filename
    int offset = findTag(ZIPUtilities.CHUNK_UNICODE_PATH, extraDataBuffer, 0, extraLength);
    if (offset != -1)
    {
      int size = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
      size = size - 5; // Remove version and CRC-32 value

      // Override previous value.
      try
      {
        chunk.id = new String(extraDataBuffer, offset + 2 + 5, size, "UTF-8");
      } catch (UnsupportedEncodingException e)
      {
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO));
      }
    }
    //------------- Check if we have unicode comment
    offset = findTag(ZIPUtilities.CHUNK_UNICODE_COMMENT, extraDataBuffer, 0, extraLength);
    if (offset != -1)
    {
      int size = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
      size = size - 5; // Remove version and CRC-32 value
      // Override any previous value
      try
      {
        chunk.setComment(new String(extraDataBuffer, offset + 2 + 5, size, "UTF-8"));
      } catch (UnsupportedEncodingException e)
      {
        errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO));
      }
    }
    //------------- Check if we have NTFS attributes
    offset = findTag(ZIPUtilities.CHUNK_NTFS_ATTRIBUTES, extraDataBuffer, 0, extraLength);
    if (offset != -1)
    {
      int size = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
      offset += 2;
      // Check if we have tag #0001 which contains the NTFS timestamps
      int internalOffset = findTag(0x0001, extraDataBuffer, offset, extraLength);
      if ((internalOffset <= (offset + size)) && (internalOffset != -1))
      {
        size = getShortLittle(extraDataBuffer, internalOffset) & 0xFFFF;
        internalOffset += 2;
        // 3 x 8 byte fields
        if (size == 24)
        {
          long mtime = getLongLittle(extraDataBuffer, internalOffset);
          internalOffset += 8;
          Calendar internalmodificationTime = NTFSDateTime.converter.decode(mtime).toCalendar();
          chunk.setLastModifiedDate(internalmodificationTime.getTime());
          long atime = getLongLittle(extraDataBuffer, internalOffset);
          internalOffset += 8;
          Calendar internalAccessTime = NTFSDateTime.converter.decode(atime).toCalendar();
          chunk.setLastAccessedDate(internalAccessTime.getTime());
          long ctime = getLongLittle(extraDataBuffer, internalOffset);
          internalOffset += 8;
          Calendar internalCreationTime = NTFSDateTime.converter.decode(ctime).toCalendar();
          chunk.setCreatedDate(internalCreationTime.getTime());
        }
      }

    }

    offset = findTag(ZIPUtilities.CHUNK_UNIX_TIMESTAMP, extraDataBuffer, 0, extraLength);
    if (offset != -1)
    {
      int expectedSize = 0;
      int size = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
      offset += 2;
      int flag = extraDataBuffer[offset] & 0xFF;
      if ((flag & ZIPUtilities.UNIX_TIMESTAMP_MTIME) == ZIPUtilities.UNIX_TIMESTAMP_MTIME)
      {
        expectedSize += 4;
      }
      if ((flag & ZIPUtilities.UNIX_TIMESTAMP_ATIME) == ZIPUtilities.UNIX_TIMESTAMP_ATIME)
      {
        expectedSize += 4;
      }
      if ((flag & ZIPUtilities.UNIX_TIMESTAMP_CTIME) == ZIPUtilities.UNIX_TIMESTAMP_CTIME)
      {
        expectedSize += 4;
      }
      if (size >= expectedSize)
      {
        offset++;
        long mtime = getIntLittle(extraDataBuffer, offset);
        Date d = new Date();
        d.setTime(mtime * 1000);
        chunk.setLastModifiedDate(d);
        offset += 4;
        long atime = getIntLittle(extraDataBuffer, offset);
        Date d1 = new Date();
        d1.setTime(mtime * 1000);
        chunk.setLastAccessedDate(d1);
        offset += 4;
        long ctime = getIntLittle(extraDataBuffer, offset);
        Date d2 = new Date();
        d2.setTime(mtime * 1000);
        chunk.setCreatedDate(d2);
        offset += 4;
      }
    }
    offset = findTag(ZIPUtilities.CHUNK_STRONG_ENCRYPTION, extraDataBuffer, 0, extraLength);
    if (offset != -1)
    {
      int size = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
      offset += 2;
      if (size >= 6)
      {
        /* Record version, this is version 2 */
        int value = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
        offset += 2;
        /* Algorithm */
        int algo = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
        switch (algo)
        {
          case ZIPUtilities.ENCRYPTION_DES:
            chunk.setEncryptionType("DES");
            break;
          case ZIPUtilities.ENCRYPTION_RC2:
          case ZIPUtilities.ENCRYPTION_RC2_NEW:
            chunk.setEncryptionType("RC2");
            break;
          case ZIPUtilities.ENCRYPTION_3DES168:
          case ZIPUtilities.ENCRYPTION_3DES112:
            chunk.setEncryptionType("DESede");
            break;
          case ZIPUtilities.ENCRYPTION_AES128:
          case ZIPUtilities.ENCRYPTION_AES192:
          case ZIPUtilities.ENCRYPTION_AES256:
            chunk.setEncryptionType("AES");
            break;
          case ZIPUtilities.ENCRYPTION_BLOWFISH:
            chunk.setEncryptionType("Blowfish");
            break;
          case ZIPUtilities.ENCRYPTION_TWOFISH:
            chunk.setEncryptionType("Twofish");
            break;
          case ZIPUtilities.ENCRYPTION_RC4:
            chunk.setEncryptionType("RC4");
            break;
        }
        offset += 2;
        int keyLength = getShortLittle(extraDataBuffer, offset) & 0xFFFF;
        chunk.setEncryptionKeyLength(keyLength);
      }

    }

  }

  /**
   * This reads a complete entry, leaving the file pointer to the next entry.
   * Each entry is composed of: [local file header 1] [encryption header 1]
   * [file data 1] [data descriptor 1] It also add the entry with information
   * into a hashtable, which can then be completed when the central directory is
   * read.
   * 
   * @return
   * @throws DocumentStreamException
   */
  protected boolean readEntry(SeekableDataInputStream is) throws DocumentStreamException, IOException
  {
    long dataLength;
    int w;
    long lw;
    long compressedSize;
    int flags;
    int fileNameLength;
    int extraFieldLength;
    long id;
    String encoding;
    long offset = 0;

    ResourceChunkInfo extHeader = new ResourceChunkInfo();
    extHeader.reset();

    // Read the chunk identifier
    id = reader.readUnsignedInt();
    if (id != ZIPUtilities.MAGIC_LOCAL_HEADER)
    {
      // Seek back to old position. 
      reader.seek(reader.getStreamPosition() - 4);
      return false;
    }

    // Read the entire values to retrieve the name of the file
    

    // Version
    w = reader.readUnsignedShort();
    // General purpose bit flag 
    flags = reader.readUnsignedShort();
    // Compression method
    w = reader.readUnsignedShort();
    // Last modification file time    
    w = reader.readUnsignedShort();
    // Last modification file date    
    w = reader.readUnsignedShort();
    // CRC-32
    lw = reader.readUnsignedInt();
    // compressed size
    compressedSize = reader.readUnsignedInt();
    // uncompressed size
    lw = reader.readUnsignedInt();
    // filename length
    fileNameLength = reader.readUnsignedShort();
    // extra field length
    extraFieldLength = reader.readUnsignedShort();

    reader.read(filenameBuffer, 0, fileNameLength);
    reader.read(extraFieldBuffer, 0, extraFieldLength);

    encoding = "ISO-8859-1";
    /* PKWare supports native UTF-8 encoding */
    if ((flags & ZIPUtilities.UTF8_ENCODING_BIT) == ZIPUtilities.UTF8_ENCODING_BIT)
    {
      encoding = "UTF-8";
    }
    try
    {
      extHeader.id = new String(filenameBuffer, 0, fileNameLength, encoding);
      entries.put(extHeader.id, extHeader);
    } catch (UnsupportedEncodingException e)
    {
      errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO));
    }
    if (extraFieldLength != 0)
    {
      parseExtField(extraFieldBuffer, extraFieldLength, extHeader);
    }
    if ((flags & ZIPUtilities.ENCRYPTED_BIT) == ZIPUtilities.ENCRYPTED_BIT)
    {
      extHeader.setEncrypted(new Boolean(true));
    }
    offset = reader.getStreamPosition() + compressedSize;
    reader.seek(offset);
    offset = 0;
    if ((flags & ZIPUtilities.DATA_DESCRIPTOR_BIT) == ZIPUtilities.DATA_DESCRIPTOR_BIT)
    {
      // DATA descriptor signature is optional.
      id = reader.readUnsignedInt() & 0xFFFFFFFFL;
      if (id != ZIPUtilities.MAGIC_DATA_DESCRIPTOR)
      {
        offset = 8;
      } else
      {
        offset = 12;
      }
    }
    reader.seek(reader.getStreamPosition() + offset);
    return true;
  }

  protected DocumentInfo readDocumentHeader(SeekableDataInputStream dataReader) throws DocumentStreamException, IOException
  {
    int type;
    long id;
    long pos;
    long offset;
    try
    {
      dataReader.setByteOrder(ByteOrder.LITTLE_ENDIAN);
      // Read the chunk identifier
      pos = dataReader.getStreamPosition();
      id = dataReader.readUnsignedInt();
      if (id == ZIPUtilities.MAGIC_LOCAL_HEADER)
      {
        type = DocumentInfo.TYPE_LITTLE_ENDIAN;
      } else
        return null;
      DocumentInfo document = new DocumentInfo(null, ZIPUtilities.MIME_TYPE, type,
          dataReader.length());
      dataReader.seek(pos);

      /* Read the entry */
      offset = reader.getStreamPosition();
      while (readEntry(dataReader) == true)
      {
        offset = reader.getStreamPosition();
      }
      // Now check if we need to skip this data or not
      id = dataReader.readUnsignedInt();
      if (id == ZIPUtilities.MAGIC_ARCHIVE_EXTRA_DATA)
      {
        dataReader.seek(offset + dataReader.readUnsignedInt());
      } else
      {
        // Seek back to old position. 
        reader.seek(reader.getStreamPosition() - 4);
      }
      centralDirectoryEntryOffset = reader.getStreamPosition();

      /* Now find offset to */
      return document;

    } catch (EOFException e)
    {
      return null;
    }
  }

  protected boolean isDocumentEnd(ChunkInfo current) throws DocumentStreamException, IOException
  {
    long id;
    long pos;
    // Read the chunk identifier
    pos = reader.getStreamPosition();
    reader.seek(centralDirectoryEntryOffset);
    // Read the chunk identifier
    id = reader.readUnsignedInt();
    if (id != ZIPUtilities.MAGIC_CENTRAL_DIRECTORY_HEADER)
    {
      return true;
    }
    reader.seek(pos);
    return false;
  }

  protected ChunkInfo newChunkInfo()
  {
    return new ResourceChunkInfo();
  }

  /**
   * Searches for the specified tag and returns in the buffer to the tag data
   * (just after this tag header)
   * 
   * @param tag
   *          The tag to search for
   * @param buffer
   *          The buffer to search in
   * @param off
   *          The offset in the buffer to search for
   * @param len
   *          The length of the buffer
   * @return the Offset in the buffer, or -1 if not found.
   */
  protected static int findTag(int tag, byte[] buffer, int off, int len)
  {
    int sTag = 0;
    int sLength = 0;
    if (len == 0)
      return -1;

    while (off < len)
    {
      sTag = getShortLittle(buffer, off) & 0xFFFF;
      off = off + 2;
      if (sTag == tag)
      {
        return off;
      }
      sLength = getShortLittle(buffer, off) & 0xFFFF;
      off += 2;
      off += sLength;
    }
    return -1;
  }

  public static short getShortLittle(byte[] array, int offset)
  {
    return (short) ((array[1 + offset] & 0xff) << 8 | (array[offset] & 0xff));
  }

  public static int getIntLittle(byte[] w, int offset)
  {
    return (w[3 + offset]) << 24 | (w[2 + offset] & 0xff) << 16 | (w[1 + offset] & 0xff) << 8
        | (w[offset] & 0xff);

  }

  public static long getLongLittle(byte[] array, int offset)
  {
    return ((long) (array[offset + 7] & 0xff) << 56) |
        ((long) (array[offset + 6] & 0xff) << 48) |
        ((long) (array[offset + 5] & 0xff) << 40) |
        ((long) (array[offset + 4] & 0xff) << 32) |
        ((long) (array[offset + 3] & 0xff) << 24) |
        ((long) (array[offset + 2] & 0xff) << 16) |
        ((long) (array[offset + 1] & 0xff) << 8) |
        ((long) (array[offset] & 0xff));
  }

}
