package com.optimasc.streams.tiff;

import java.io.InputStream;
import java.util.Vector;

import com.optimasc.streams.Attribute;
import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.DataReader;
import com.optimasc.streams.internal.AbstractDocumentReader;

/**
 * Implements a TIFF Chunk reader. Each IFD entry is represented by a chunk in
 * this parser. The ID of the chunk is returned as an {@link java.lang.Integer}
 * representing the Tag of the IFD entry. Each of these IFD entries also have
 * both the "Type" and "Count" attributes set representing respectively as a
 * string representing the type integer value, and the count of items also
 * represented as a string.
 * 
 * The image data (strip data) is represented as multiple chunks of value equal
 * to {@link com.optimasc.streams.tiff.TIFFUtilities#TIFF_ID_DATA}.
 * 
 * @author Carl Eric Codere
 * 
 */
public class TIFFReader extends AbstractDocumentReader
{

  public byte[] byteBuffer = new byte[4];
  // Main IFD Position
  protected long IFDPosition;

  TIFFUtilities tiffValidator;

  public static final Integer TAG_ID_EXIF_POINTER = new Integer(
      TIFFUtilities.EXIF_ID_POINTER);
  public static final Integer TAG_ID_GPS_POINTER = new Integer(
      TIFFUtilities.GPS_ID_POINTER);

  public static final Integer TAG_ID_STRIPOFFSETS = new Integer(
      TIFFUtilities.TIFF_ID_STRIPOFFSETS);
  public static final Integer TAG_ID_STRIPBYTECOUNTS = new Integer(
      TIFFUtilities.TIFF_ID_STRIPBYTECOUNTS);

  public static final Integer TAG_ID_DATA = new Integer(
      TIFFUtilities.TIFF_ID_DATA);

  public static final int LONGWORD_SIZE = 4;
  public static final int WORD_SIZE = 2;

  // An array containing all the images in the TIFF file.
  Vector imageCollection;

  protected int currentGroup;
  protected int currentChunkIndex;
  protected int currentImageIndex;
  protected ImageInformation currentImage;

  class TagInformation
  {
    int groupName;
    ChunkInfo info;

    public TagInformation(int groupName, ChunkInfo info)
    {
      this.groupName = groupName;
      this.info = info;
    }

  }

  /** Contains the data for one TIFF image */
  class ImageInformation
  {
    /** Position of the different IFDs for this image */
    protected Vector chunks;
    protected long[] stripOffsets;
    protected long[] stripByteCounts;

    public ImageInformation()
    {
      chunks = new Vector();
    }
  }

  public TIFFReader(InputStream inputStream, StreamFilter filter)
      throws DocumentStreamException
  {
    super(64, inputStream, filter);
    tiffValidator = new TIFFUtilities();
    currentChunkIndex = 0;
    currentImageIndex = 0;
  }

  private int processIFD(DataReader reader, ChunkInfo information)
      throws DocumentStreamException
  {
    long valueOffset;
    long count;
    int typid;
    // Read the directory entry.
    information.id = new Integer(readWord() & 0xFFFF);
    // Return the datatype 
    typid = readWord() & 0xFFFF;
    count = readLongword() & 0xFFFFFFFF;
    valueOffset = readLongword() & 0xFFFFFFFF;
    information.size = count * TIFFUtilities.fieldTypeToSize(typid);
    information.type = ChunkInfo.TYPE_CHUNK;
    // If the size is less or equal than 4 then the value
    // is equal to the ValueOffset then it points directly to
    // the data value.
    if (information.size <= LONGWORD_SIZE)
    {
      information.offset = reader.getPosition() - LONGWORD_SIZE;
    } else
    {
      information.offset = valueOffset;
    }
    information.getAttributes().addElement(
        new Attribute(null, TIFFUtilities.TIFF_ATTRIBUTE_NAME_TYPE, Integer.toString(typid)));
    information.getAttributes().addElement(
        new Attribute(null, TIFFUtilities.TIFF_ATTRIBUTE_NAME_COUNT, Long.toString(count)));
    return typid;
  }

  /**
   * Adds the specified directory entries to the director entry table. Upon
   * entry this method should point to the IFD Position.
   * 
   * It returns the totaL size of the data of this group.
   * 
   * @throws DocumentStreamException
   */
  private long addEntries(DataReader reader, Vector entries, int group)
      throws DocumentStreamException
  {
    int i;
    int dirEntries;
    long totalSize = 0;
    ChunkInfo localInfo;
    // Get the number of directory entries 
    dirEntries = readWord() & 0xFFFF;
    for (i = 0; i < dirEntries; i++)
    {
      localInfo = newChunkInfo();
      processIFD(reader, localInfo);
      totalSize = totalSize + localInfo.size;
      entries.addElement(new TagInformation(group, localInfo));
    }
    return totalSize;
  }

  protected void readChunkHeader(DataReader dataReader, ChunkInfo header)
      throws DocumentStreamException
  {
    long IFDPos;
    int maxIdx;
    long position;
    int dirEntries;
    ChunkInfo localInfo;
    int typid;
    int i, j;
    header.reset();

    // First time read through the file to identify the different TIFF groups.
    if (imageCollection == null)
    {
      imageCollection = new Vector();
      // In the first pass, get information on all strip information chunks }
      IFDPos = IFDPosition;
      do
      {
        dataReader.setPosition(IFDPos);
        currentImage = new ImageInformation();
        imageCollection.addElement(currentImage);

        // Get the number of directory entries 
        dirEntries = readWord() & 0xFFFF;
        for (i = 0; i < dirEntries; i++)
        {
          localInfo = newChunkInfo();
          typid = processIFD(dataReader, localInfo);
          position = dataReader.getPosition();
          // Call this recursively.
          if (localInfo.id.equals(TAG_ID_GPS_POINTER))
          {
            if (typid == TIFFUtilities.TIFF_TYPE_LONG)
            {
              dataReader.setPosition(localInfo.offset);
              IFDPos = readLongword() & 0xFFFFFFFF;
              dataReader.setPosition(IFDPos);
              addEntries(dataReader, currentImage.chunks,
                  TIFFUtilities.GPS_GROUP);
            } else
              errorHandler.error(new DocumentStreamException(
                  DocumentStreamException.ERR_BLOCK_INVALID_SIZE, header.id
                      .toString()));
          } else if (localInfo.id.equals(TAG_ID_EXIF_POINTER))
          {
            if (typid == TIFFUtilities.TIFF_TYPE_LONG)
            {
              dataReader.setPosition(localInfo.offset);
              IFDPos = readLongword() & 0xFFFFFFFF;
              dataReader.setPosition(IFDPos);
              addEntries(dataReader, currentImage.chunks, TIFFUtilities.EXIF_GROUP);
            } else
              errorHandler.error(new DocumentStreamException(
                  DocumentStreamException.ERR_BLOCK_INVALID_SIZE, header.id
                      .toString()));
          } else if (localInfo.id.equals(TAG_ID_STRIPOFFSETS))
          {
            long tmpPos = dataReader.getPosition();
            dataReader.setPosition(localInfo.offset);
            if (typid == TIFFUtilities.TIFF_TYPE_SHORT)
            {
              maxIdx = (int) (localInfo.size / WORD_SIZE);
              currentImage.stripOffsets = new long[maxIdx];

              for (j = 0; j < maxIdx; j++)
              {
                currentImage.stripOffsets[j] = readWord() & 0xFFFF;
              }
            }
            else
            {
              maxIdx = (int) (localInfo.size / LONGWORD_SIZE);
              currentImage.stripOffsets = new long[maxIdx];
              for (j = 0; j < maxIdx; j++)
              {
                currentImage.stripOffsets[j] = readLongword();
              }
            }
            dataReader.setPosition(tmpPos);
          }
          else
          // These two arrays give information on the data 
          if (localInfo.id.equals(TAG_ID_STRIPBYTECOUNTS))
          {
            long tmpPos = dataReader.getPosition();
            dataReader.setPosition(localInfo.offset);
            if (typid == TIFFUtilities.TIFF_TYPE_SHORT)
            {
              maxIdx = (int) (localInfo.size / WORD_SIZE);
              currentImage.stripByteCounts = new long[maxIdx];
              for (j = 0; j < maxIdx; j++)
              {
                currentImage.stripByteCounts[j] = readWord() & 0xFFFF;
              }
            }
            else
            {
              maxIdx = (int) (localInfo.size / LONGWORD_SIZE);
              currentImage.stripByteCounts = new long[maxIdx];
              for (j = 0; j < maxIdx; j++)
              {
                currentImage.stripByteCounts[j] = readLongword();
              }
            }
            dataReader.setPosition(tmpPos);
          }
          else
          {
            currentImage.chunks.addElement(new TagInformation(
                TIFFUtilities.TIFF_GROUP, localInfo));
          }
          dataReader.setPosition(position);
        } // end for
        IFDPos = readLongword() & 0xFFFFFFFF;
      } while ((dataReader.getPosition() < dataReader.getSize())
          && (IFDPos != 0));
      currentImageIndex = 0;
      currentChunkIndex = -1;
      currentGroup = TIFFUtilities.TIFF_GROUP;

      // Now create the data elements for each image based on the strip offsets
      // and byte counts - we only need to do this one.
      for (int idx = 0; idx < imageCollection.size(); idx++)
      {
        currentImage = (ImageInformation) imageCollection
            .elementAt(idx);
        /** stripOffsets is not present in JPEG EXIF */
        if (currentImage.stripOffsets != null)
        {
          for (j = 0; j < currentImage.stripOffsets.length; j++)
          {
            ChunkInfo info = newChunkInfo();
            info.offset = currentImage.stripOffsets[j];
            info.id = TAG_ID_DATA;
            info.size = currentImage.stripByteCounts[j];
            info.type = ChunkInfo.TYPE_CHUNK;
            TagInformation tag = new TagInformation(TIFFUtilities.TIFF_GROUP, info);
            currentImage.chunks.addElement(tag);
          }
        }
      }
    } // endif imageCollection == null

    // Each TIFF image: TIFF_GROUP containing GPS_GROUP and EXIF_GROUP
    while (currentImageIndex < imageCollection.size())
    {
      currentImage = (ImageInformation) imageCollection
          .elementAt(currentImageIndex);
      processChunk(header, currentImage);
      if (currentChunkIndex >= currentImage.chunks.size())
      {
        currentImageIndex++;
        currentChunkIndex = -1;
        currentGroup = TIFFUtilities.TIFF_GROUP;
      }
      return;
    }
  }

  private void processChunk(ChunkInfo header, ImageInformation imageInfo)
      throws DocumentStreamException
  {
    if (currentChunkIndex == -1)
    {
      header.type = ChunkInfo.TYPE_GROUP;
      header.extraSize = 0;
      header.id = new Integer(TIFFUtilities.TIFF_GROUP);
      header.offset = 0;
      header.size = 0;
      currentChunkIndex++;
    } else
    {
      TagInformation tagInfo = (TagInformation) imageInfo.chunks
          .elementAt(currentChunkIndex);
      if (tagInfo.groupName != currentGroup)
      {
        header.type = ChunkInfo.TYPE_GROUP;
        header.extraSize = 0;
        header.id = new Integer(tagInfo.groupName);
        header.offset = 0;
        header.size = 0;
        currentGroup = tagInfo.groupName;
        return;
      }
      // Copy the header information
      header.copy(tagInfo.info);
      reader.setPosition(tagInfo.info.offset);
      currentChunkIndex++;
    }
  }

  private short readWord() throws DocumentStreamException
  {
    if (document.getStreamType() == DocumentInfo.TYPE_BIG_ENDIAN)
    {
      return reader.read16Big();
    } else
    {
      return reader.read16Little();
    }
  }

  private long readLongword() throws DocumentStreamException
  {
    if (document.getStreamType() == DocumentInfo.TYPE_BIG_ENDIAN)
    {
      return reader.read32Big();
    } else
    {
      return reader.read32Little();
    }
  }

  protected DocumentInfo readDocumentHeader(DataReader dataReader)
  {
    int type = DocumentInfo.TYPE_UNKNOWN_ENDIAN;
    try
    {
      // Read the file identifier and determine the endian of the data
      dataReader.read(byteBuffer, 0, 2);

      int value = (int) ((byteBuffer[0] << 8) | byteBuffer[1]) & 0xFFFF;

      if (value == TIFFUtilities.TIFF_MAGIC_LITTLE_ENDIAN_SIGNATURE)
      {
        type = DocumentInfo.TYPE_LITTLE_ENDIAN;
        value = dataReader.read16Little() & 0xFFFF;
        IFDPosition = dataReader.read32Little() & 0xFFFFFFFF;
      } else if (value == TIFFUtilities.TIFF_MAGIC_BIG_ENDIAN_SIGNATURE)
      {
        type = DocumentInfo.TYPE_BIG_ENDIAN;
        value = dataReader.read16Big() & 0xFFFF;
        IFDPosition = dataReader.read32Big() & 0xFFFFFFFF;
      }
      if (value == TIFFUtilities.TIFF_MAGIC_SIGNATURE)
        return new DocumentInfo("TIFF", "image/tiff", type, dataReader.getSize());
    } catch (DocumentStreamException e)
    {
      return null;
    }

    return null;
  }

  /**
   * Checks if the group is ended or not. For TIFF files, we cannot rely on the
   * size of the chunks, we need to check the actual last chunk of the group to
   * determine if we need to close the group or not.
   * 
   * @param info
   *          The next chunk in the stack.
   * @return true if the group is ended otherwise false.
   */
  protected boolean isGroupEnd(ChunkInfo current, ChunkInfo info) throws DocumentStreamException
  {
    int lastIndex = currentImage.chunks.size() - 1;
    TagInformation tag = (TagInformation) currentImage.chunks
        .elementAt(lastIndex);
    ChunkInfo chunk = tag.info;
    if (current.equals(chunk))
      return true;
    return false;
  }

  /**
   * Determine if this is the end of the document. For TIFF files the end of
   * document is determined by being the last image in the TIFF document and by
   * having parsed all groups and data.
   * 
   * 
   */
  protected boolean isDocumentEnd(ChunkInfo current) throws DocumentStreamException
  {
    if (currentImageIndex >= imageCollection.size())
      return true;
    return false;
  }

}
