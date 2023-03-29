package com.optimasc.streams.vformat;

import com.optimasc.streams.internal.ChunkUtilities;

public class VFormatUtilities extends ChunkUtilities
{
  
  protected final static int CHAR_LF = 10;
  protected final static int CHAR_TAB = 9;
  protected final static int CHAR_SPACE = 32;
  protected final static int CHAR_CR = 13;
  
  /** Maximum number of bytes allowed per line */
  protected final static int MAX_LINE_LENGTH = 75;
  
  public static final char VFORMAT_DELIMITER = ';';
  public static final char VFORMAT_GROUP_DELIMITER = '.';
  public static final char VFORMAT_VALUE_SEPARATOR = ':';
  public static final char VFORMAT_PARAMETER_SEPARATOR = '=';
  public static final char VFORMAT_PARAMETER_VALUE_SEPARATOR = ',';
  public static final char VFORMAT_DQUOTE_CHARACTER = '"';
  
  
  /** Verifies that this is a valid identifier as specified
   *  in IETF RFC 2425.
   * 
   * @return
   */
  protected static boolean isValidIdentifier(String s)
  {
    int i;
    for (i = 0; i < s.length(); i++)
    {
      char ch = s.charAt(i);
      // A-Z
      if ((ch >= 0x41) && (ch <= 0x5A))
        continue;
      // a-z
      if ((ch >= 0x61) && (ch <= 0x7A))
        continue;
      // 0-9
      if ((ch >= 0x30) && (ch <= 0x39))
        continue;
      if (ch == '-')
        continue;
      return false;
    }
    return true;
  }
  
  /** Verifies that this is a valid quoted safe-character 
   *  as specified in IETF RFC 2425.
   * 
   * @return true if the characters are valid, otherwise
   *   returns false.
   */
  protected static boolean isSafeQCharValues(String s)
  {
    int i;
    for (i = 0; i < s.length(); i++)
    {
      char ch = s.charAt(i);
      if ((ch >= 0x23) && (ch <= 0x7E))
        continue;
      if ((ch >= 0x80) && (ch <= 0xFF))
        continue;
      if ((ch == CHAR_TAB) || (ch == CHAR_SPACE))
        continue;
      if (ch == 0x21)
        continue;
      return false;
    }
      return true;
  }
  
  /** Verifies that this is a valid safe-character 
   *  as specified in IETF RFC 2425.
   * 
   * @return true if the characters are valid, otherwise
   *   returns false.
   */
  protected static boolean isSafeCharValues(String s)
  {
    int i;
    
    for (i = 0; i < s.length(); i++)
    {
      char ch = s.charAt(i);
      if ((ch == CHAR_TAB) || (ch == CHAR_SPACE))
        continue;
      if ((ch >= 0x23) && (ch <= 0x2B))
        continue;
      if ((ch >= 0x2D) && (ch <= 0x39))
        continue;
      if ((ch >= 0x3C) && (ch <= 0x7E))
        continue;
      if ((ch >= 0x80) && (ch <= 0xFF))
        continue;
      if (ch == 0x21)
        continue;
      return false;
    }
    return true;
  }
  

  public String chunkIDToObject(Object id) throws IllegalArgumentException
  {
     if (isValidIdentifier(id.toString())==false)
       throw new IllegalArgumentException("Only characters in the range [A-Za-z0-9-] are allowed in the chunk identifier");
     return id.toString();
  }

  public String groupIDToObject(Object id) throws IllegalArgumentException
  {
    if (isValidIdentifier(id.toString())==false)
      throw new IllegalArgumentException("Only characters in the range [A-Za-z0-9-] are allowed in the group identifier");
    return id.toString();
  }

}

