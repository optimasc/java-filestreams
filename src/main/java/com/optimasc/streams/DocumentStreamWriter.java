package com.optimasc.streams;

import java.util.Vector;


/** Base implementation for writing structured file formats. Each structured
 *  format writer should implement this interface. 
 * 
 * @author Carl Eric Codere
 *
 */
public interface DocumentStreamWriter
{
  /** Starts a chunk element with the specified id and attributes
   *  which is active until {@link #writeEndElement()} is called.
   * 
   * @param id The ID of this chunk, the actual object
   *   type depends on the implementation.
   * @param attributes An array of {@link #com.optimasc.utils.Attribute} containing
   *   the attributes for this element, this can be null if no attributes
   *   are supplied.
   * @throws DocumentStreamException
   */
  public void writeStartElement(Object id, Attribute[] attributes) 
    throws DocumentStreamException;

  /** 
   * 
   * @param id The ID of this chunk, the actual object
   *   type depends on the implementation.
   * @param attributes An array of {@link #com.optimasc.utils.Attribute} containing
   *   the attributes for this element, this can be null if no attributes
   *   are supplied.
   * @throws DocumentStreamException
   */
  public void writeStartGroup(Object id, Attribute[] attributes) 
    throws DocumentStreamException;
  
  /**
   * Writes an end tag to the output relying on the internal 
   * state of the writer to determine the prefix and local name
   * of the event.
   * @throws DocumentStreamException 
   */
  public void writeEndElement() 
    throws DocumentStreamException;
  
  /**
   * Writes an end tag to the output relying on the internal 
   * state of the writer to determine the prefix and local name
   * of the event.
   * @throws DocumentStreamException 
   */
  public void writeEndGroup() 
    throws DocumentStreamException;
  

  /**
   * Closes any start tags and writes corresponding end tags.
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
  public void  writeStartDocument(String publicID)
    throws DocumentStreamException;

  /**
   * Write text to the output
   * @param text the value to write
   * @throws DocumentStreamException 
   */
  public void writeCharacters(String text) 
    throws DocumentStreamException;

  /**
   * Write text to the output
   * @param text the value to write
   * @param start the starting position in the array
   * @param len the number of characters to write
   * @throws DocumentStreamException 
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
