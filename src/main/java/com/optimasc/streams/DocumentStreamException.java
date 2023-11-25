package com.optimasc.streams;

import java.io.IOException;

/**
 * The base exception for unexpected processing errors.  This Exception
 * class is used to report well-formedness errors as well as unexpected
 * processing conditions.
 * @version 1.0
 * @author Copyright (c) 2003 by BEA Systems. All Rights Reserved.
 */
public class DocumentStreamException extends Exception {

  protected Throwable nested;
  protected Location location;
  
  public static final String ERR_BLOCK_INVALID_SIZE = "Invalid block size";
  public static final String ERR_BLOCK_INVALID_ID =   "Illegal element or group identifier";
  public static final String ERR_EXTRA_DATA =   "Extra data at end of stream";
  public static final String ERR_INVALID_NESTING =   "Invalid nesting level for group/element";
  public static final String ERR_INVALID_STREAM =   "Invalid stream format for this reader";
  public static final String ERR_CORRUPT_STREAM =   "Corrupt data for this reader";
  public static final String ERR_BLOCK_INVALID_HEADER =   "Invalid, missing data or corrupt header in the chunk";
  public static final String ERR_IO =   "I/O Error while accessing stream";
  public static final String ERR_END_OF_STREAM =   "I/O Error unexpected end of stream encountered";
  public static final String ERR_BLOCK_NOT_CLOSED = "Chunk/Group is never closed for element ";
  public static final String ERR_INVALID_STATE = "Trying to write data to a non-element";
  public static final String ERR_INVALID_LINE_ENDING = "Invalid line ending in Text stream";
  public static final String ERR_INVALID_LINE_LENGTH = "Invalid line length in Text stream";
  public static final String ERR_INVALID_ATTRIBUTE_NAME = "Invalid attribute name - probably contains illegal characters";
  public static final String ERR_INVALID_ATTRIBUTE_VALUE = "Invalid attribute value - probably contains illegal characters";
  
  /**
   * Default constructor
   */
  public DocumentStreamException(){ 
    super();
  }

  /**
   * Construct an exception with the assocated message.
   *
   * @param msg the message to report
   */
  public DocumentStreamException(String msg) {
    super(msg);
  }

  /**
   * Construct an exception with the assocated message.
   *
   * @param msg the message to report
   */
  public DocumentStreamException(String msg, String parameter) {
    super(msg+" \""+parameter+"\"");
  }
  

  /**
   * Construct an exception with the assocated exception
   *
   * @param th a nested exception
   */
  public DocumentStreamException(Throwable th) {
    nested = th;
  }

  /**
   * Construct an exception with the assocated message and exception
   *
   * @param th a nested exception
   * @param msg the message to report
   */
  public DocumentStreamException(String msg, Throwable th) {
    super(msg);
    nested = th;
  }

  /**
   * Construct an exception with the assocated message, exception and location.
   *
   * @param th a nested exception
   * @param msg the message to report
   * @param location the location of the error 
   */
  public DocumentStreamException(String msg, Location location, Throwable th) {
    super("ParseError at [offset]:["+location.getOffset()+"]\n"+
          "Message: "+msg);
    nested = th;
    this.location = location;
  }

  /**
   * Construct an exception with the assocated message, exception and location.
   *
   * @param msg the message to report
   * @param location the location of the error 
   */
  public DocumentStreamException(String msg, 
                            Location location) {
    super("ParseError at [offset]:["+location.getOffset()+"]\n"+
        "Message: "+msg);
    this.location = location;
  }
  

  /**
   * Gets the nested exception.
   *
   * @return Nested exception
   */
  public Throwable getNestedException() {
    return nested;
  }

  /**
   * Gets the location of the exception
   *
   * @return the location of the exception, may be null if none is available
   */
  public Location getLocation() {
    return location;
  }

}






