package com.optimasc.streams;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;


/** Interface that allows creating structured data formats. Each structured
 *  format writer should implement this interface.  
 * 
 * @author Carl Eric Codere
 *
 */
public interface DocumentStreamWriter extends DataOutput
{
  /** Starts a chunk/leaf element with the specified id and attributes
   *  which is active until {@link #writeEndElement()} is called.
   * 
   * @param id The ID of this chunk, the actual object
   *   type depends on the implementation.
   * @param attributes An array of {@link com.optimasc.utils.Attribute} containing
   *   the attributes for this element, this can be null if no attributes
   *   are supplied.
   * @throws IllegalArgumentException  If the element identifier is not well formed.
   * @throws IOException If there is an issue writing to the underlying stream.
   * @throws IllegalStateException 
   *   <ul>
   *    <li>If trying to write an element within an element. Elements are leafs of the tree.</li>
   *    <li>If trying to start an element when it is not allowed for the moment, such as when 
   *     {@link #writeStartDocument(String)} has not been called first or when this format requires a group to be started
   *     first.</li>
   *   </ul>
   */
  public void writeStartElement(Object id, Attribute[] attributes) throws IOException;

  /** Starts a group node that contains leaf elements with the specified
   *  ID and attributes.
   * 
   * @param id The ID of this chunk, the actual object
   *   type depends on the implementation.
   * @param attributes An array of {@link com.optimasc.utils.Attribute} containing
   *   the attributes for this element, this can be null if no attributes
   *   are supplied.
   * @throws IllegalArgumentException  If the group identifier is not well formed.
   * @throws IOException If there is an issue writing to the underlying stream.
   * @throws IllegalStateException 
   *   <ul>
   *    <li>If trying to write a group within an group and this is not supported
   *        by the underlying format.</li>
   *    <li>If trying to start a group when it is not allowed for the moment, such as when 
   *     {@link #writeStartDocument(String)} has not been called first or when this format requires a group to be started
   *     first.</li>
   *   </ul>
   */
  public void writeStartGroup(Object id, Attribute[] attributes) throws IOException;
  
  /** Closes the currently opened leaf chunk and updates any
   * data as required. The leaf node to close should have
   * been previously opened by {@link #writeStartElement(java.lang.Object, com.optimasc.streams.Attribute[]) }
   *
   * @throws IllegalArgumentException  If the size of the data to be written
   *   is not supported or is invalid.
   * @throws IOException If there is an issue writing to the underlying stream.
   * @throws IllegalStateException 
   *   <ul>
   *    <li>If trying to end and element that has not been start.</li>
   *    <li>If trying to end this chunk while document is not started.</li>
   *   </ul>
   *   
   */
  public void writeEndElement()  throws IOException;

/** Closes the currently opened group chunk/node and updates any
   * data as required. The group node to close should have
   * been previously opened by {@link #writeStartGroup(java.lang.Object, com.optimasc.streams.Attribute[]) }
   *
   * @throws IllegalArgumentException  If the size of the data to be written
   *   is not supported or is invalid.
   * @throws IOException If there is an issue writing to the underlying stream.
   * @throws IllegalStateException 
   *   <ul>
   *    <li>If trying to end a group that has not been started.</li>
   *    <li>If trying to end this chunk while document is not started.</li>
   *   </ul>
   *   
   */
  public void writeEndGroup()  throws IOException;
  

  /** Writes end of document information.
   * @throws IllegalArgumentException  If the size of the document 
   *   is not supported or is invalid.
   * @throws IOException If there is an issue writing to the underlying stream.
   * @throws IllegalStateException 
   *   <ul>
   *    <li>If trying to end a document that has not been started.</li>
   *    <li>If trying to end a document while the last element has not been closed.</li>
   *    <li>If trying to end a document while some of the groups have not been closed.</li>
   *   </ul>
   */
  public void writeEndDocument() throws IOException;
 
  /**
   * Close this writer and free any resources associated with the 
   * writer.  This must not close the underlying output stream.
   * @throws DocumentStreamException 
   */
  public void close() 
    throws IOException;

  /**
   * Write any cached data to the underlying output mechanism.
   * @throws DocumentStreamException 
   */
    public void flush()  throws IOException;
  
  /** Creates a new document with the specified <code>publicID</code>. This
   *  is normally the first method to call when creating a structured document.
   *
   *  <p>The usage and format of <code>publicID</code> is implementation
   *  specific and may be unused in some implementations.
   *
   * @param publicID The ID associated with this document
   * @throws DocumentStreamException
   */
  public void  writeStartDocument(String publicID) throws IOException;

  /** Write text to the output. It is assumed that the text is composed of 
   *  UCS-2 characters (BMP). The underlying encoding of the
   *  data is implementation specific and may throw {@link java.io.UnsupportedEncodingException}
   *  if the data cannot be stored in the underlying structured format without
   *  loss of precision.
   *   
   * @param text the value to write
   * @throws DocumentStreamException
   *  @throws UnsupportedEncodingException If the underlying format
   *    does not support encoding into UCS-2 format or a similar
   *    format without any loss of data.
   */
  public void writeChars(String text) 
    throws IOException;

  /**
   * Get the value of a feature/property from the underlying implementation
   * @param name The name of the property, may not be null
   * @return The value of the property
   * @throws IllegalArgumentException if the property is not supported
   * @throws NullPointerException if the name is null
   */
  public Object getProperty(java.lang.String name) throws IllegalArgumentException;

  /** Write an 8-bit octet value to the output. The low-order
   *  byte is the actual value that will be written.
   * 
   * @param b the value to write
   * @throws DocumentStreamException
   */
  public void writeByte(int b) throws IOException;
  
  /** Write an 8-bit octet value to the output. The low-order
   *  byte is the actual value that will be written.
   * 
   * @param b the value to write
   * @throws DocumentStreamException
   */
  public void write(int b) throws IOException;
  
  /** Write a 32-bit IEEE-754 floating point value (Single).
   * 
   * @param v the value to write
   * @throws IOException
   */
  public void writeFloat(float v) throws IOException;
  
  /** Write a 64-bit IEEE-754 floating point value (Single). 
   * 
   * @param v the value to write
   * @throws IOException
   */
  public void writeDouble(double v) throws IOException;

  
  /** Write 8-bit octet values to the output.
   * 
   * @param buffer The value to write
   * @param off The starting position in the array
   * @param len The number of octets to write
   * @throws IOException
   */
  public void write(byte[] buffer, int off, int len) throws IOException;
  
  
  /** Write 8-bit octet values to the output.
   * 
   * @param buffer The value to write
   * @throws IOException
   */
  public void write(byte[] buffer) throws IOException;
  
  
  /** Writes a 16-bit integer values to the output in the correct expected 
   *  format for the document.
   * 
   * @param w The value to write
   * @throws IOException
   */
  public void writeShort(int w) throws IOException;
  
  /** Writes a 32-bit integer value to the output in the correct expected format
   *  for the document.
   * 
   * @param l The value to write
   * @throws IOException
   */
  public void writeInt(int l) throws IOException;

  /** Writes a 64-bit integer value to the output in the correct expected
   *  format for the document.
   * 
   * @param v The value to write
   * @throws IOException
   */
   public void writeLong(long v) throws IOException;
   
   /** Writes a boolean value to the output in the correct expected
    *  format for the document.
    * 
    * @param v The value to write
    * @throws IOException
    */
   public void writeBoolean(boolean v) throws IOException;
   
   /** Writes a UCS-2 character to the output in the correct 
    *  expected format for the document. The underlying encoding of the
    *  data is implementation specific and may throw {@link java.io.UnsupportedEncodingException}
    *  if the data can be stored in the underlying structured format
    *  
    *  @param v The value to write
    *  @throws UnsupportedEncodingException If the underlying format
    *   does not support UCS-2 encoding.
    *  @throws IOException  
    */
   public void writeChar(int v) throws IOException;
   
   /** Writes text to the output. This assumes that the characters
    *  are only composed of ISO-8859-1 characters (Only the low
    *  order byte of each character is actually taken into account).
    *  
    *  In the case where the underlying format only supports <code>
    *  US-ASCII</code> characters and some of the low-byte values
    *  are beyond character 0x7F then {@link java.io.UnsupportedEncodingException}
    *  will be thrown, in that case {@link #write(byte[], int, int)} should
    *  be called instead.
    *   
    *  @param s [in] The value to write
    *  @throws UnsupportedEncodingException If any of the characters
    *    low-byte is beyond the value 127.
    *  @throws IOException   
    */
   public void writeBytes(String s) throws IOException;
   
   /** Writes text to the output. This assumes that the characters
    *  can be represented as an UTF-8 string. 
    *  
    *  In the case where the underlying format does not support encoding
    *  UTF-8 strings, this method will throw an <code>{@link java.io.UnsupportedEncodingException}
    *  </code> exception. 
    *   
    *  @param s [in] The value to write
    *  @throws UnsupportedEncodingException If the underlying format
    *    does not support encoding into UTF-8 format or a similar
    *    format without any loss of data.
    *  @throws IOException   
    */
   public void writeUTF(String s) throws IOException;
   
   
   public void setOutput (OutputStream os,  String encoding) throws IOException;
}
