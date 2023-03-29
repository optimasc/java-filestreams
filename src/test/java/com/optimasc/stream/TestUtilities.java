package com.optimasc.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Stack;
import java.util.Vector;

import junit.framework.TestCase;

import com.optimasc.streams.DocumentInfo;
import com.optimasc.streams.DocumentStreamConstants;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.DocumentStreamReader;
import com.optimasc.streams.DocumentStreamWriter;
import com.optimasc.streams.Attribute;
import com.optimasc.streams.ErrorHandler;

/**
 * Test utilities to test the data in streams.
 * 
 * @author Carl Eric Codere
 * 
 */
public class TestUtilities extends TestCase
{
  protected static byte[] buffer = new byte[1024*1024];
  
  /* Empty test to avoid errors and warnings. */
  public void testEmpty()
  {
    
  }
  
  public static class TestErrorHandler implements ErrorHandler 
  {
    /* Throw exception on warnings, contrary to default behavior */
    public void warning(DocumentStreamException exception)
        throws DocumentStreamException
    {
      throw exception;
    }

    public void error(DocumentStreamException exception)
        throws DocumentStreamException
    {
      throw exception;
    }

    public void fatalError(DocumentStreamException exception)
        throws DocumentStreamException
    {
      throw exception;
    }    
  }
  
  /** Looks for the attribute with the specified namespace and
   *  local name in an array of Attribute, and returns the index
   *  into that element, or -1 if it is not found.
   * 
   *  
   */
  protected static int findAttribute(Attribute[] attrs, String ns, String name)
  {
    int j;
    for (j = 0; j < attrs.length; j++)
    {
      if (ns.equals(attrs[j].getNamespaceURI()))
      {
        if (name.equals(attrs[j].getLocalName()))
          return j;
      }
    }
    return -1;
  }

  /** Simple generic parse routine that is used to validate valid 
   *  reader data. 
   * 
   * @param reader The reader reference.
   * @param info The information on the elements to read and verify, it
   *   tries to verify the name, as well as the data and attributes
   *   when present.
   */
  public static void parse(DocumentStreamReader reader, ElementInfo[] info) throws DocumentStreamException
  {
    int elementNr = 0;
    int chunkNr = 0;
    String s;
    Stack stack = new Stack();
    ElementInfo element;
    
    reader.setErrorHandler(new TestErrorHandler());

    try
    {
      while (reader.hasNext())
      {
        int id = reader.next();
        switch (id)
        {
        case DocumentStreamConstants.START_DOCUMENT:
          System.out.println("begin.");
          DocumentInfo docInfo = reader.getDocumentInfo();
          System.out.println("Document Type: "+docInfo.getShortTypeName());
          System.out.println("Document PublicID :"+docInfo.getPublicId());
          break;
        case DocumentStreamConstants.START_GROUP:
          System.out.println("<" + reader.getId().toString() + ">");
          if (info != null)
          {
        	  assertEquals(info[elementNr].id, reader.getId().toString());
        	  stack.push(info[elementNr]);
          }
          elementNr++;
          break;
        case DocumentStreamConstants.END_GROUP:
          if (info != null)
          {
        	  element = (ElementInfo) stack.pop();
        	  assertEquals(element.id, reader.getId().toString());
          }
          System.out.println("</" + reader.getId().toString() + ">");
          break;
        case DocumentStreamConstants.START_ELEMENT:
          if (info != null)
          {
        	  assertEquals(info[elementNr].id, reader.getId().toString());
          }
          s = "";
          if (reader.getAttributeCount() > 0)
          {
             for (int i = 0; i < reader.getAttributeCount(); i++)
             {
               s = s + " "+reader.getAttributeLocalName(i) + "=\"" + reader.getAttributeValue(i) + "\"";
             }
             // Validate the actual attribute values.
             // We only compare attributes which are present and are equal in value
             if ((info!=null) && (info[elementNr].attributes!=null))
             {
                Attribute[] attrs = info[elementNr].attributes;
                int index = -1;
                if (reader.getAttributeCount() > 0)
                {
                  for (int i = 0; i < reader.getAttributeCount(); i++)
                  {
                    index = findAttribute(attrs,reader.getAttributeNamespace(i), reader.getAttributeLocalName(i));
                    if (index != -1)
                    {
                      assertEquals(attrs[index].getValue(),reader.getAttributeValue(i));
                    }
                  }
                }
             }
          }
          System.out.print("<" + reader.getId().toString() + s + ">");
          break;
        case DocumentStreamConstants.DATA:
          if (info!=null)
          {
        	  if (info[elementNr].size != -1)
            assertEquals(info[elementNr].size, reader.getDataSize());
          // Compare the data.
          if (info[elementNr].data != null)
          {
            reader.getData(buffer, 0, (int)reader.getDataSize());
            compareData(info[elementNr].data,buffer,(int)reader.getDataSize());
          }
          }
          break;
        case DocumentStreamConstants.END_ELEMENT:
          if (info != null)
          {
        	  assertEquals(info[elementNr].id, reader.getId().toString());
          }
          elementNr++;
          System.out.println("</" + reader.getId().toString() + ">");
          break;
        case DocumentStreamConstants.END_DOCUMENT:
          System.out.println("end.");
          break;
        }
      } // end while
    } catch (com.optimasc.streams.IllegalStateException e)
    {
      fail();
    }
  }
  
  /** This method is used to test the format writer by reading a document
   *  and outputtting it. 
   *  
   * @param reader Document to read
   * @param writer Document to write
   */
  public static void copy(DocumentStreamReader reader, DocumentStreamWriter writer)
  {
	    String s;
	    Attribute[] attributes;

	    try
	    {
	      while (reader.hasNext())
	      {
	        int id = reader.next();
	        switch (id)
	        {
	        case DocumentStreamConstants.START_DOCUMENT:
	          System.out.println("begin.");
	          DocumentInfo docInfo = reader.getDocumentInfo();
	          System.out.println("Document Type: "+docInfo.getShortTypeName());
	          System.out.println("Document PublicID :"+docInfo.getPublicId());
	          writer.writeStartDocument(docInfo.getPublicId());
	          break;
	        case DocumentStreamConstants.START_GROUP:
	          System.out.println("<" + reader.getId().toString() + ">");
              
              attributes = null;
              if (reader.getAttributeCount() > 0)
              {
                 attributes = new Attribute[reader.getAttributeCount()];
                 for (int i = 0; i < reader.getAttributeCount(); i++)
                 {
                   Attribute attr = (Attribute)reader.getAttribute(i);
                   attributes[i] = attr;
                 }
              }
	          writer.writeStartGroup(reader.getId(),attributes);
	          break;
	        case DocumentStreamConstants.END_GROUP:
	          writer.writeEndGroup();
	          break;
	        case DocumentStreamConstants.START_ELEMENT:
	          attributes = null;
              if (reader.getAttributeCount() > 0)
              {
                attributes = new Attribute[reader.getAttributeCount()];
                 for (int i = 0; i < reader.getAttributeCount(); i++)
                 {
                   Attribute attr = (Attribute)reader.getAttribute(i);
                   attributes[i] = attr;
                 }
              }
	          writer.writeStartElement(reader.getId(),attributes);
	          s = "";
	          System.out.print("<" + reader.getId().toString() + s + ">");
	          break;
	        case DocumentStreamConstants.DATA:
	            reader.getData(buffer, 0, (int)reader.getDataSize());
	            writer.writeOctetString(buffer, 0, (int)reader.getDataSize());
	          break;
	        case DocumentStreamConstants.END_ELEMENT:
              writer.writeEndElement();
	          System.out.println("</" + reader.getId().toString() + ">");
	          break;
	        case DocumentStreamConstants.END_DOCUMENT:
	          writer.writeEndDocument();
	          System.out.println("end.");
	          break;
	        }
	      } // end while
	    } catch (DocumentStreamException e)
	    {
	      fail();
	    } catch (com.optimasc.streams.IllegalStateException e)
	    {
	      fail();
	    }
  }
  
  public static void compareData(byte[] data1, byte[] data2, int length)
  {
    int i;
    if (data1.length != length)
      fail("Length of data to compare is not the same!");
    for (i = 0; i < data1.length; i++)
    {
      if (data1[i] != data2[i])
        assertEquals("Data comparison failure",data1[i],data2[i]);
    }
  }
  
  
  public static boolean binaryDiff(InputStream aStream, InputStream bStream) throws IOException 
  {
    final int BLOCK_SIZE = 128;
    byte[] aBuffer = new byte[BLOCK_SIZE];
    byte[] bBuffer = new byte[BLOCK_SIZE];
    while (true) {
        int aByteCount = aStream.read(aBuffer, 0, BLOCK_SIZE);
        bStream.read(bBuffer, 0, BLOCK_SIZE);
        if (aByteCount < 0) {
            return true;
        }
        if (!Arrays.equals(aBuffer, bBuffer)) {
            return false;
        }
    }  
  }


}
