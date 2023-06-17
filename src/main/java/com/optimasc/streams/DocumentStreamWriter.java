package com.optimasc.streams;


/** Interface that allows creating structured data formats. Each structured
 *  format writer should implement this interface. 
 * 
 * @author Carl Eric Codere
 *
 */
public interface DocumentStreamWriter
{
  /** Starts a chunk/leaf element with the specified id and attributes
   *  which is active until {@link #writeEndElement()} is called.
   * 
   * @param id The ID of this chunk, the actual object
   *   type depends on the implementation.
   * @param attributes An array of {@link com.optimasc.utils.Attribute} containing
   *   the attributes for this element, this can be null if no attributes
   *   are supplied.
   * @throws DocumentStreamException
   */
  public void writeStartElement(Object id, Attribute[] attributes) 
    throws DocumentStreamException;

  /** Starts a group node that contains leaf elements with the specified
   *  ID and attributes.
   * 
   * @param id The ID of this chunk, the actual object
   *   type depends on the implementation.
   * @param attributes An array of {@link com.optimasc.utils.Attribute} containing
   *   the attributes for this element, this can be null if no attributes
   *   are supplied.
   * @throws DocumentStreamException
   */
  public void writeStartGroup(Object id, Attribute[] attributes) 
    throws DocumentStreamException;
  
  /** Closes the currently opened leaf chunk and updates any
   * data as required. The leaf node to close should have
   * been previously opened by {@link #writeStartElement(java.lang.Object, com.optimasc.streams.Attribute[]) }
   *
   * @throws DocumentStreamException if there is no leaf node started,
   *   or if maximum allowed nesting has been reached, or if the size
   *   of the data written to this element is not allowed.
   */
  public void writeEndElement() 
    throws DocumentStreamException;


/** Closes the currently opened group chunk/node and updates any
   * data as required. The group node to close should have
   * been previously opened by {@link #writeStartGroup(java.lang.Object, com.optimasc.streams.Attribute[]) }
   *
   * @throws DocumentStreamException if there is no group node started,
   *   or if maximum allowed nesting has been reached, or if the size
   *   of the data written to this element is not allowed.
   */
  public void writeEndGroup() 
    throws DocumentStreamException;
  

  /** Writes end of document information.
   * @throws DocumentStreamException 
   */
  public void writeEndDocument() 
    throws DocumentStreamException;
 
  /**
   * Close this writer and free any resources associated with the 
   * writer.  This must not close the underlying output stream.
   * @throws DocumentStreamException 
   */
  public void close() 
    throws DocumentStreamException;

  /**
   * Write any cached data to the underlying output mechanism.
   * @throws DocumentStreamException 
   */
  public void flush() 
    throws DocumentStreamException;
  
  /**
   * Write the XML Declaration.  Note that the encoding parameter does
   * not set the actual encoding of the underlying output.  That must 
   * be set when the instance of the XMLStreamWriter is created using the
   * XMLOutputFactory
   * @param encoding encoding of the xml declaration
   * @param version version of the xml document
   * @throws DocumentStreamException 
   */

  /** Creates a new document with the specified <code>publicID</code>. This
   *  is normally the first method to call when creating a structured document.
   *
   *  <p>The usage and format of <code>publicID</code> is implementation
   *  specific and may be unused in some implementations.
   *
   * @param publicID The ID associated with this document
   * @throws DocumentStreamException
   */
  public void  writeStartDocument(String publicID)
    throws DocumentStreamException;

  /** Write text to the output. The underlying encoding of the
   *  data is implementation specific and may throw {@link java.io.UnsupportedEncodingException}
   *  if the data can be stored in the underlying structured format. Normally
   *  <code>US-ASCII</code> should be supported by default, but other characters
   *  depends on the underlying structured format.
   * 
   * @param text the value to write
   * @throws DocumentStreamException
   * @throws UnsupportedEncodingException In the case where the underlying
   *  implementation does not permit to encode these characters.
   */
  public void writeCharacters(String text) 
    throws DocumentStreamException;

  /** Write text to the output. The underlying encoding of the
   *  data is implementation specific and may throw {@link java.io.UnsupportedEncodingException}
   *  if the data can be stored in the underlying structured format. Normally
   *  <code>US-ASCII</code> should be supported by default, but other characters
   *  depends on the underlying structured format.
   *
   * @param text the value to write
   * @param start the starting position in the array
   * @param len the number of characters to write
   * @throws DocumentStreamException
   * @throws UnsupportedEncodingException In the case where the underlying
   *  implementation does not permit to encode these characters.
   */
  public void writeCharacters(char[] text, int start, int len) 
    throws DocumentStreamException;

  /**
   * Get the value of a feature/property from the underlying implementation
   * @param name The name of the property, may not be null
   * @return The value of the property
   * @throws IllegalArgumentException if the property is not supported
   * @throws NullPointerException if the name is null
   */
  public Object getProperty(java.lang.String name) throws IllegalArgumentException;

  /** Write an 8-bit value to the output.
   * 
   * @param b the value to write
   * @throws DocumentStreamException
   */
  public void writeOctet(int b) throws DocumentStreamException;
  
  
  /** Write a 32-bit IEEE-754 floating point value (Single)
   * 
   * @param v the value to write
   * @throws DocumentStreamException
   */
  public void writeSingle(float v) throws DocumentStreamException;
  
  /** Write a 64-bit IEEE-754 floating point value (Single)
   * 
   * @param v the value to write
   * @throws DocumentStreamException
   */
  public void writeDouble(double v) throws DocumentStreamException;

  
  /** Write 8-bit values to the output.
   * 
   * @param buffer The value to write
   * @param off The starting position in the array
   * @param len The number of octets to write
   * @throws DocumentStreamException
   */
  public void writeOctetString(byte[] buffer, int off, int len) throws DocumentStreamException;
  
  /** Writes a 16-bit values to the output in the correct endian of 
   *  the document.
   * 
   * @param w The value to write
   * @throws DocumentStreamException
   */
  public void writeWord(short w) throws DocumentStreamException;
  
  /** Writes a 32-bit values to the output in the correct endian of 
   *  the document.
   * 
   * @param l The value to write
   * @throws DocumentStreamException
   */
  public void writeLongword(int l) throws DocumentStreamException;

  
}
