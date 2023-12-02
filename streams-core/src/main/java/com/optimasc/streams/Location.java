package com.optimasc.streams;

/**
 * Provides information on the location of an event.
 *
 * All the information provided by a Location is optional.  For example
 * an application may only report line numbers.
 *
 */
public interface Location {
  /**
   * Return the line number where the current event ends,
   * returns -1 if none is available.
   * @return the current line number
   */
  int getLineNumber();

  /**
   * Return the column number where the current event ends,
   * returns -1 if none is available.
   * @return the current column number
   */
  int getColumnNumber();
  
  /**
   * Return the byte or character offset into the input source this location
   * is pointing to. If the input source is a file or a byte stream then 
   * this is the byte offset into that stream, but if the input source is 
   * a character media then the offset is the character offset. 
   * Returns -1 if there is no offset available.
   * @return the current offset
   */
  long getOffset();

  /**
   * Returns the public ID or type of the document
   * @return the public ID, or null if not available
   */
  public String getPublicId();

  /**
   * Returns the system ID of the document
   * @return the system ID, or null if not available
   */
  public String getSystemId();
}



