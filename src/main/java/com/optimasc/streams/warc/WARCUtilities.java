package com.optimasc.streams.warc;

public class WARCUtilities
{
  /** Space character */
  public static final char SP = (char)0x20;
  /** Tab character */
  public static final char HTAB = (char)0x09;

  /** CR character */
  public static final char CR = (char)13;
  /** LF character */
  public static final char LF = (char)10;
  

  /** Mandatory header fields. */
  public static final String FIELD_LENGTH = "Content-Length";
  public static final String FIELD_RECORD_ID = "WARC-Record-ID";
  public static final String FIELD_RECORD_DATE = "WARC-Date";
  public static final String FIELD_RECORD_TYPE = "WARC-Type";
  
  public static final String FIELD_LENGTH_UPPER = FIELD_LENGTH.toUpperCase();
  public static final String FIELD_RECORD_ID_UPPER = FIELD_RECORD_ID.toUpperCase();
  public static final String FIELD_RECORD_DATE_UPPER = FIELD_RECORD_DATE.toUpperCase();
  public static final String FIELD_RECORD_TYPE_UPPER = FIELD_RECORD_TYPE.toUpperCase();

  public static final String MAGIC_WARC = "WARC/1.0\r\n";
  
  
  /** Returns true if whitespace otherwise returns false */
  public static boolean isWhiteSpace(char c)
  {
    if ((c == HTAB) || (c == SP))
      return false;
    return true;
  }
  
  /** Returns true if this is a digit, otherwise return false */
  public static boolean isDigit(char c)
  {
    if ((c >= 0x30) && (c <= 0x39))
    {
      return true;
    }
    return false;
  }
  
  /** Returns true if this is a visible char, otherwise return false */
  public static boolean isVisibleChar(char c)
  {
    if ((c >= 0x21) && (c <= 0x7E))
    {
      return true;
    }
    return false;
  }
}
