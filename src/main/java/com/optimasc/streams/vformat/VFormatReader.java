package com.optimasc.streams.vformat;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.StreamFilter;
import com.optimasc.streams.internal.ChunkInfo;
import com.optimasc.streams.internal.DataReader;
import com.optimasc.streams.internal.AbstractDocumentReader;
import com.optimasc.streams.Attribute;

;

/**
 * Implements a lenient RFC 2425 Format compliant reader.
 * 
 * @author Carl Eric Codere
 * 
 */

public class VFormatReader extends AbstractDocumentReader
{

  public static final int INDEX_GROUP = 0;
  public static final int INDEX_NAME = 1;
  public static final int INDEX_PARAMS = 2;
  public static final int INDEX_VALUE = 3;

  protected VFormatUtilities validator;
  // Internal buffer used for the value of the data
  protected byte[] utf8buffer;
  protected int internalPosition;

  protected int lineNumber;
  protected int columnNumber;

  public VFormatReader(InputStream inputStream, StreamFilter filter)
      throws DocumentStreamException
  {
    super(1, inputStream, filter);
    validator = new VFormatUtilities();
  }

  /**
   * This method should read the next chunk and populate the ChunkInfo value
   * accordingly. Upon exiting the stream position should be set to the actual
   * data of the chunk or the next group if this was a group chunk.
   * 
   * @param dataReader
   *          The reader api containing the stream to read from
   * @param header
   *          The actual structure containing information on the chunk that
   *          should be filled in.
   */

  protected void readChunkHeader(DataReader dataReader, ChunkInfo header)
      throws DocumentStreamException
  {
    String s;
    header.reset();
    do
    {
      s = readLine(dataReader);
    } while (s.length() == 0);

    String values[] = parseLine(s);
    if (values == null)
    {
      errorHandler.fatalError(new DocumentStreamException(
          DocumentStreamException.ERR_INVALID_STREAM, ""));
    }

    try
    {
      String name = validator.groupIDToObject(values[INDEX_NAME]);
      // Check the group Identifier
      if (name.toLowerCase().equals("begin") == true)
      {
        header.type = ChunkInfo.TYPE_GROUP;
        header.size = 0;
        header.offset = 0;
        header.id = values[INDEX_VALUE];
        parseAttributes(header.getAttributes(), values[INDEX_PARAMS]);
      } else
      {
        try
        {
          utf8buffer = values[INDEX_VALUE].getBytes("UTF-8");
          internalPosition = 0;
        } catch (UnsupportedEncodingException e)
        {
          errorHandler.fatalError(new DocumentStreamException(DocumentStreamException.ERR_IO,
              "Unsupported encoding conversion"));
        }
        header.type = ChunkInfo.TYPE_CHUNK;
        header.size = utf8buffer.length;
        header.offset = 0;
        header.id = values[INDEX_NAME];
        parseAttributes(header.getAttributes(), values[INDEX_PARAMS]);
      }
    } catch (IllegalArgumentException e)
    {
    }
  }

  protected DocumentInfo readDocumentHeader(DataReader dataReader)
  {
    String s;
    String groupID;
    try
    {

      do
      {
        s = readLine(dataReader);
      } while (s.length() == 0);

      String values[] = parseLine(s);
      if (values == null)
        return null;

      try
      {
        groupID = validator.groupIDToObject(values[INDEX_NAME]);
        // Check the group Identifier
        if (groupID.toLowerCase().equals("begin") == false)
          return null;
      } catch (IllegalArgumentException e)
      {
        return null;
      }
      // Reset the stream
      dataReader.setPosition(0);
      DocumentInfo document = new DocumentInfo(values[INDEX_VALUE],
          "text/directory", DocumentInfo.TYPE_CHARACTER, dataReader.getSize());
      return document;

    } catch (DocumentStreamException e)
    {
      return null;
    }

  }

  /**
   * Returns true if the specific character is an ASCII whitespace, as defined
   * in the line folding and unfolding algorithm.
   * 
   * @param ch
   *          Character to check
   * @return
   */
  protected static boolean isWhiteSpace(int ch)
  {
    if ((ch == VFormatUtilities.CHAR_TAB) || (ch == VFormatUtilities.CHAR_SPACE))
      return true;
    return false;
  }

  protected int readByte(DataReader reader) throws DocumentStreamException
  {
    int v = reader.read8Raw();
    columnNumber++;
    return v;
  }

  /**
   * Reads a CRLF line, but also supports the other line endings, and takes care
   * of unfolding the line as required.
   * 
   * @param reader
   * @return The string read.
   * @throws DocumentStreamException
   */
  protected String readLine(DataReader reader) throws DocumentStreamException
  {
    lineNumber = 0;
    columnNumber = 0;
    StringBuffer buffer = new StringBuffer();
    int ch = readByte(reader);
    if (ch == -1)
      return buffer.toString();
    long pos;
    do
    {
      if (ch == VFormatUtilities.CHAR_CR)
      {
        if (columnNumber > VFormatUtilities.MAX_LINE_LENGTH + 1)
        {
          errorHandler.warning(new DocumentStreamException(
              DocumentStreamException.ERR_INVALID_LINE_LENGTH));
        }
        lineNumber++;
        columnNumber = 0;

        pos = reader.getPosition();
        ch = readByte(reader);
        // We exit the loop we have finished reading, CR+LF mode (Windows,DOS) 
        if (ch == VFormatUtilities.CHAR_LF)
        {
          pos = reader.getPosition();
          ch = readByte(reader);
          // If the next character is whitespace then we continue reading - unfolding }
          if (isWhiteSpace(ch) == false)
          {
            // We get back one character 
            reader.setPosition(pos);
            break;
          }
        } else if (isWhiteSpace(ch) == false)
        {
          // This is CR mode (MacOS), backward one character 
          reader.setPosition(pos);
        }
      } else if (ch == VFormatUtilities.CHAR_LF)
      {
        if (columnNumber > VFormatUtilities.MAX_LINE_LENGTH + 1)
        {
          errorHandler.warning(new DocumentStreamException(
              DocumentStreamException.ERR_INVALID_LINE_LENGTH));
        }
        lineNumber++;
        columnNumber = 0;

        pos = reader.getPosition();
        errorHandler.warning(new DocumentStreamException(
            DocumentStreamException.ERR_INVALID_LINE_ENDING));
        // This is LF mode (UNIX mode operating systems) 
        ch = readByte(reader);
        // If the next character is whitespace then we continue reading - unfolding 
        if (isWhiteSpace(ch) == false)
        {
          // This is LF mode, backward one character
          reader.setPosition(pos);
          break;
        }
      } else
      {
        buffer.append((char) ch);
      }
      ch = readByte(reader);
    } while (ch != -1);
    return buffer.toString();
  }

  /**
   * This actually parses the values of a parameter, it normalizes the values
   * and checks their validity. It removes all whitespace between all the values
   * themselves.
   * 
   * @param paramValues
   *          The string containing a list of parameter values which is not
   *          normalized.
   * @return The string containing a list of parameter values which is
   *         normalized.
   * @throws DocumentStreamException
   *           If the value contains illegal characters.
   */
  protected static String parseParamValues(String paramValues) throws DocumentStreamException
  {
    int index = 0;
    int i;
    String s;
    char ch;
    boolean inDQuote = false;
    Vector v = new Vector();
    StringBuffer buffer = new StringBuffer();

    while (index < paramValues.length())
    {
      ch = paramValues.charAt(index);
      if (ch == VFormatUtilities.VFORMAT_DQUOTE_CHARACTER)
      {
        if (inDQuote == false)
        {
          inDQuote = true;
        } else
        {
          inDQuote = false;
          s = buffer.toString();
          if (VFormatUtilities.isSafeQCharValues(s) == false)
          {
            throw new DocumentStreamException(DocumentStreamException.ERR_INVALID_ATTRIBUTE_VALUE);
          }
          v.addElement(s);
          buffer.setLength(0);
        }
      } else if ((ch == VFormatUtilities.VFORMAT_PARAMETER_VALUE_SEPARATOR) && (inDQuote == false))
      {
        s = buffer.toString();
        if (VFormatUtilities.isSafeCharValues(s) == false)
        {
          throw new DocumentStreamException(DocumentStreamException.ERR_INVALID_ATTRIBUTE_VALUE);
        }
        v.addElement(s);
        buffer.setLength(0);
      } else
      {
        buffer.append(ch);
      }
      index++;
    }

    // If Double quote is not terminated.
    if (inDQuote == true)
    {
      throw new DocumentStreamException(DocumentStreamException.ERR_INVALID_ATTRIBUTE_VALUE);
    }
    if (buffer.length() > 0)
    {
      s = buffer.toString();
      if (VFormatUtilities.isSafeCharValues(s) == false)
      {
        throw new DocumentStreamException(DocumentStreamException.ERR_INVALID_ATTRIBUTE_VALUE);
      }
      v.addElement(s);
    }
    s = "";
    for (i = 0; i < v.size() - 1; i++)
    {
      s = s + (String) v.elementAt(i) + VFormatUtilities.VFORMAT_PARAMETER_VALUE_SEPARATOR;
    }
    if (v.size() > 0)
      s = s + (String) v.elementAt(i);
    return s;
  }

  protected static Attribute parseParam(String param) throws DocumentStreamException
  {
    Attribute attr = new Attribute();
    int startIndex;
    int endIndex;
    String s;
    int index = param.indexOf(VFormatUtilities.VFORMAT_PARAMETER_SEPARATOR);
    if (index == -1)
    {
      s = param.trim();
      s = parseParamValues(s);
      attr.setValue(s);
    } else
    {
      s = param.substring(index + 1).trim();
      s = parseParamValues(s);
      attr.setValue(s);
      s = param.substring(0, index).trim();
      // Check if this is a valid attribute name
      if (VFormatUtilities.isValidIdentifier(s) == false)
      {
        throw new DocumentStreamException(DocumentStreamException.ERR_INVALID_ATTRIBUTE_NAME);
      }
      attr.setLocalName(s);
    }
    return attr;
  }

  /**
   * Parses the attributes of a text/directory profile, converts them as
   * necessary to uniform format (removing double quotes) and then puts them in
   * a vector of Attributes.
   * 
   * @param attrs
   *          The vector that will contain the parsed attributes
   * @param attributes
   *          The attributes as a string format
   * @return A vector containing the attributes for this chunk.
   */
  protected static void parseAttributes(Vector attrs, String attributes)
      throws DocumentStreamException
  {
    int index = 0;
    char ch;
    Attribute attr;
    String param;
    StringBuffer buffer = new StringBuffer();
    if (attributes == null)
      return;
    while (index < attributes.length())
    {
      ch = attributes.charAt(index);
      if (ch == VFormatUtilities.VFORMAT_DELIMITER)
      {
        if ((buffer.length()) != 0)
        {
          param = buffer.toString();
          // Clear the buffer
          buffer.setLength(0);
          attr = parseParam(param);
          attrs.addElement(attr);
        }
      } else
      {
        buffer.append(ch);
      }
      index++;
    }
    // Check if there is something left
    if ((buffer.length()) != 0)
    {
      param = buffer.toString();
      // Clear the buffer
      buffer.setLength(0);
      attr = parseParam(param);
      attrs.addElement(attr);
    }

  }

  /**
   * Separates the line into its different components according to the directory
   * MIME profile.
   * 
   * 
   * 
   * @param line
   * @return The string array containing the different indexes, null if the line
   *         is not recognized as a correct format.
   */
  protected static String[] parseLine(String line)
  {
    String valueString;
    String headerString;
    String[] values;
    int index2;
    char ch;
    boolean inDQuote = false;
    int index;
    // Find the value separator - but we must make sure the value is not double-quote.
    index = 0;
    while (index < line.length())
    {
      ch = line.charAt(index);
      if (ch == VFormatUtilities.VFORMAT_DQUOTE_CHARACTER)
      {
        if (inDQuote == true)
        {
          inDQuote = false;
        } else
        {
          inDQuote = true;
        }
      }
      if ((ch == VFormatUtilities.VFORMAT_VALUE_SEPARATOR) && (inDQuote == false))
      {
        break;
      }
      index++;
    }

    // No value separator this is an invalid type.
    if (index == -1)
    {
      return null;
    }
    // If we have less than 3 characters, then there is an error, since name : value is at least 3 characters.  
    if (line.length() < 3)
    {
      return null;
    }
    // Check if there is a value string
    if ((index + 1) >= line.length())
      return null;
    valueString = line.substring(index + 1);

    headerString = line.substring(0, index);

    values = new String[4];
    // Check if there is a group identifier
    index = headerString.indexOf(VFormatUtilities.VFORMAT_GROUP_DELIMITER);
    // The group identifier can only before the parameters, never after - therefore check if there are parameters 
    index2 = headerString.indexOf(VFormatUtilities.VFORMAT_DELIMITER);
    if (index == -1)
    {
      values[INDEX_GROUP] = null;
    } else if (((index2 != -1) && (index < index2)) || (index2 == -1))
    {
      values[INDEX_GROUP] = headerString.substring(0, index);
      // Check if there is a name string
      if ((index + 1) >= line.length())
        return null;
      headerString = headerString.substring(index + 1);
    }
    // Check if there are parameters
    index = headerString.indexOf(VFormatUtilities.VFORMAT_DELIMITER);
    if (index == -1)
    {
      values[INDEX_NAME] = headerString;
      values[INDEX_PARAMS] = null;
    } else
    {
      values[INDEX_NAME] = headerString.substring(0, index);
      values[INDEX_PARAMS] = headerString.substring(index);
    }
    values[INDEX_VALUE] = valueString;

    // Final validation
    if (values[INDEX_VALUE].length() == 0)
    {
      return null;
    }
    if (values[INDEX_NAME].length() == 0)
    {
      return null;
    }

    return values;
  }

  protected void skipData(long size) throws DocumentStreamException
  {
    // Do nothing - since the data is in an internal buffer.
  }

  // Read from the internal buffer instead from the actual stream.
  protected void read(byte[] target, int currentLength, int length)
      throws DocumentStreamException
  {
    System.arraycopy(utf8buffer, internalPosition, target, currentLength, length);
    internalPosition += length;
  }

  // The only way to determine if a group is finished is to actually read
  // ahead and if the end is found then the group is finished. 
  protected boolean isGroupEnd(ChunkInfo current, ChunkInfo info) throws DocumentStreamException
  {
    long currentPos = reader.getPosition();
    String s;
    do
    {
      s = readLine(reader);
    } while (s.length() == 0);

    String values[] = parseLine(s);
    if (values == null)
    {
      reader.setPosition(currentPos);
      return false;
    }

    try
    {
      String groupID = validator.groupIDToObject(values[INDEX_NAME]);
      // Check the group Identifier
      if (groupID.toLowerCase().equals("end") == true)
      {
        if (info.id.toString().equalsIgnoreCase(values[INDEX_VALUE]))
        {
          return true;
        }
      }
    } catch (IllegalArgumentException e)
    {
      reader.setPosition(currentPos);
      return false;
    }
    reader.setPosition(currentPos);
    return false;
  }

  protected boolean isDocumentEnd(ChunkInfo current) throws DocumentStreamException
  {
    // TODO Auto-generated method stub
    return super.isDocumentEnd(current);
  }

}
