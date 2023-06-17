package com.optimasc.streams;



/**
 *  Interface that allows forward, read-only access to structured
 *  file formats. It is designed to be the lowest level and most efficient way to
 *  read structured document data.
 *
 * <p> The DocumentStreamReader is designed to iterate over chunks/elements using
 * {@link #next()} and {@link #hasNext()}.  The data can be accessed using methods such as {@link #getEventType()}
 * {@link #getId()} and {@link #getData(byte[], int, int)}.
 *
 * <p> The <a href="#next()">next()</a> method causes the reader to read the next parse event.
 * The next() method returns an integer which identifies the type of event just read.
 * <p> The event type can be determined using <a href="#getEventType()">getEventType()</a>.
 * <p> Parsing events are defined as the document Declaration, 
 * start chunk, group or element, binary data and group or element.
 *
 * <p>The following table describes which methods are valid in what state.
 * If a method is called in an invalid state the method will throw a
 * java.lang.IllegalStateException.
 *
 * <table border="2" rules="all" cellpadding="4">
 *   <thead>
 *     <tr>
 *       <th align="center" colspan="2">
 *         Valid methods for each state
 *       </th>
 *     </tr>
 *   </thead>
 *   <tbody>
 *     <tr>
 *       <th>Event Type</th>
 *       <th>Valid Methods</th>
 *     </tr>
 *     <tr>
 *       <td> All States  </td>
 *       <td> close(), getDocumentInfo(), getEventType(), getLocation(), getProperty(), hasNext(),
 *            setErrorHandler(), getDocumentInfo()
 *       </td>
 *     </tr>
 *     <tr>
 *       <td> START_GROUP  </td>
 *       <td> next(), getId(), getAttributeXXX() 
 *       </td>
 *     </tr>
 *     <tr>
 *       <td> START_ELEMENT  </td>
 *       <td> next(), getId(), getAttributeXXX() 
 *       </td>
 *     </tr>
 *     <tr>
 *       <td> END_ELEMENT  </td>
 *       <td> next(), getId(), nextTag() 
 *      </td>
 *     </tr>
 *     <tr>
 *       <td> DATA  </td>
 *       <td> next(), getDataSize(), getData() </td>
 *     </tr>
 *     <tr>
 *       <td> START_DOCUMENT  </td>
 *       <td> next(), getDocumentInfo()</td>
 *     </tr>
 *     <tr>
 *       <td> END_DOCUMENT  </td>
 *       <td> close()</td>
 *     </tr>
 *   </tbody>
 *  </table>
 *  
 *  
 *
 */
public interface DocumentStreamReader
{
  /**
   * Get the value of a feature/property from the underlying implementation. Features
   * or property are configuration elements specific to implementations.
   * 
   * @param name The name of the property, may not be null
   * @return The value of the property
   * @throws IllegalArgumentException if name is null
   */
  public Object getProperty(java.lang.String name) throws java.lang.IllegalArgumentException;

  /**
   * Get next parsing event. A processor will always return all contiguous
   * binary data in a single chunk.

   * This method will throw an IllegalStateException if it is called after hasNext() returns false.
   * @return the integer code corresponding to the current parse event
   * @throws DocumentStreamException  if there is an error processing the underlying source
   */
  public int next() throws DocumentStreamException;

  
  /**
   * Returns an identifier for the current START_ELEMENT, START_GROUP, END_ELEMENT or
   * END_GROUP event. The actual type of the object is stream dependent, but
   * each identifier can be represented as a string using toString().
   * 
   * @return the Identifier for the current event
   * @throws IllegalStateException if this is not a START_ELEMENT, START_GROUP, END_GROUP
   * or END_ELEMENT
   */
  public Object getId();
  

  /**
   * Returns true if there are more parsing events and false
   * if there are no more events.  This method will return
   * false if the current state of the DocumentStreamReader is
   * END_DOCUMENT
   * @return true if there are more events, false otherwise
   * @throws DocumentStreamException if there is a fatal error detecting the next state
   */
  public boolean hasNext() throws DocumentStreamException;
  
  /**
   * Frees any resources associated with this Reader.  This method does not close the
   * underlying input source.
   * @throws DocumentStreamException if there are errors freeing associated resources
   */
  public void close() throws DocumentStreamException;

  /**
   * Returns the normalized attribute value of the
   * attribute with the namespace and localName
   * If the namespaceURI is null the namespace is not checked for equality
   * @param namespaceURI the namespace of the attribute, only valid in 
   *   certain instances.
   * @param localName the local name of the attribute, cannot be null
   * @return returns the value of the attribute , returns null if not found
   * @throws IllegalStateException if this is not a START_ELEMENT, DATA or START_GROUP
   */
  public String getAttributeValue(String namespaceURI,
                                  String localName)  throws IllegalStateException;

  
  /** Returns the namespace of the attribute at the provided index. 
   * this method is only valid on a START_ELEMENT, DATA or START_GROUP. 
   * Attribute indices are zero-based.
   * 
   * @param index - the position of the attribute 
   * @return the namespace URI (can be null) 
   */
  public String getAttributeNamespace(int index) throws IllegalStateException;
  
  /** Returns the local name of the attribute at the provided index. 
   * this method is only valid on a START_ELEMENT, DATA or START_GROUP. 
   * Attribute indices are zero-based.
   * 
   * @param index - the position of the attribute 
   * @return he localName of the attribute 
   */
  public String getAttributeLocalName(int index) throws IllegalStateException;
  

  /**
   * Returns the count of attributes on this element
   * this method is only valid on a START_ELEMENT, START_GROUP or DATA 
   * Attribute indices are zero-based.
   * @return returns the number of attributes
   * @throws IllegalStateException if this is not a START_ELEMENT, DATA or START_GROUP
   */
  public int getAttributeCount() throws IllegalStateException;

  /**
   * Returns the value of the attribute at the
   * index
   * @param index the position of the attribute
   * @return the attribute value
   * @throws IllegalStateException if this is not a START_ELEMENT, DATA or START_GROUP
   */
  public String getAttributeValue(int index) throws IllegalStateException;
  
  /** Returns the complete Attribute object at the specified index.
   *  This routine is not compatible with the XML Pull specification
   *  and is used as a convenience method.
   * 
   * @param index The index of the attribute to return
   * @return The Attribute instance object
   */
  public Attribute getAttribute(int index) throws IllegalStateException;

  /**
   * Returns an integer code that indicates the type
   * of the event the cursor is pointing to.
   */
  public int getEventType();

  /**
   * Gets the the data associated with the DATA event.  
   * Data starting at 0 is copied into "target" starting at "targetStart".  
   * Up to "length" characters are copied.  The number of data actually copied is returned.
   *
   * If the number of bytes actually copied is less than the "length", then there is no more data.  
   * Otherwise, subsequent calls need to be made until all data has been retrieved. For example:
   * 
   * DocumentStreamException may be thrown if there are any document errors in the underlying source. 
   * The "targetStart" argument must be greater than or equal to 0 and less than the length of "target",  
   * Length must be greater than 0 and "targetStart + length" must be less than or equal to length of "target".  
   *
   * @param target the destination array to receive the copied data
   * @param targetStart the start offset in the target array
   * @param length the number of characters to copy
   * @return the number of bytes actually copied
   * @throws DocumentStreamException if the underlying data source is not well-formed
   * @throws IndexOutOfBoundsException if targetStart < 0 or > than the length of target
   * @throws IndexOutOfBoundsException if length < 0 or targetStart + length > length of target
   * @throws UnsupportedOperationException if this method is not supported 
   * @throws NullPointerException is if target is null
   */
   public int getData(byte[] target, int targetStart, int length)  throws DocumentStreamException;

  /**
   * Returns the total number of bytes available for this DATA event.  
   * @throws DocumentStreamException 
   * @throws java.lang.IllegalStateException if this state is not
   * a valid DATA state.
   */
  public long getDataSize() throws DocumentStreamException;


  /**
   * Return input encoding if this is a character based stream if known or null if unknown.
   * @return the encoding of this instance or null
   */
  public String getEncoding();

  /**
   * Return the current location of the processor.
   * If the Location is unknown the processor should return
   * an implementation of Location that returns -1 for the
   * location and null for the publicId and systemId.
   * The location information is only valid until next() is
   * called.
   */
  public Location getLocation();
  

  /**
   * Allow an application to register an error event handler.
   *
   * <p>If the application does not register an error handler, all
   * error events reported by the parser will be silently
   * ignored except for a fatal error which will throw a DocumentStreamEzxception; 
   * however, normal processing may not continue.  It is
   * highly recommended that all applications implement an
   * error handler to avoid unexpected bugs.</p>
   *
   * <p>Applications may register a new or different handler in the
   * middle of a parse, and the parser must begin using the new
   * handler immediately.</p>
   *
   * @param handler The error handler.
   * @see #getErrorHandler
   */
  public void setErrorHandler (ErrorHandler handler);


  /**
   * Return the current error handler.
   *
   * @return The current error handler, or null if none
   *         has been registered.
   * @see #setErrorHandler
   */
  public ErrorHandler getErrorHandler ();

  /** Returns the document information for this document. This can be called
   *  at any time, even without calling the next method, as it can be used
   *  rapidly to identify a stream type. It should never return any formatting
   *  error exception, it should return a null value instead.
   * 
   *  @return The document information or null if this
   *    is not of the specified document type.
   * 
   */
  public DocumentInfo getDocumentInfo() throws DocumentStreamException;

}
