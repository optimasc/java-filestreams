package com.optimasc.streams.vformat;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import com.optimasc.stream.ElementInfo;
import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamConstants;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.tiff.TIFFReader;
import com.optimasc.streams.Attribute;

import junit.framework.TestCase;

public class VFormatReaderImplTest extends TestCase
{
  
  public static final ElementInfo[] elements =
  {
    new ElementInfo("VCARD",-1,null),
      new ElementInfo("source",-1,null,null),
      new ElementInfo("name",-1,null,"Bjorn Jensen".getBytes()),
      new ElementInfo("fn",-1,null,"Bj=F8rn Jensen".getBytes()),
      new ElementInfo("n",-1,null,"Jensen;Bj=F8rn".getBytes()),
      new ElementInfo("note",-1,null,"This is a long description that exists on a long very line.".getBytes()),
      new ElementInfo("email",-1,null,"bjorn@umich.edu".getBytes()),
      new ElementInfo("tel",-1,null,"+1 313 747-4454".getBytes()),
      new ElementInfo("key",-1,null,"dGhpcyBjb3VsZCBiZSAKbXkgY2VydGlmaWNhdGUK".getBytes()),
  };
  
  /**
   * Test standard usage cases with a valid resource.
   * 
   * @throws FileNotFoundException
   * @throws UnsupportedEncodingException
   */
  public void testOne() throws FileNotFoundException
  {
    try
    {
      InputStream is = getClass().getResourceAsStream("/res/sample.vcf");
      VFormatReader reader = new VFormatReader();
      reader.setInput(is, null);
      TestUtilities.parse(reader, elements);
    } catch (Exception e)
    {
      fail();
    }
  }

  public void testParseLine()
  {
    // Format of a content line:    
    //  contentline  = [group "."] name *(";" param) ":" value CRLF

    String[] values;
    // Invalid format - nothing
    values = VFormatReader.parseLine("");
    assertEquals(null, values);
    // Invalid line
    values = VFormatReader.parseLine("groupName.");
    assertEquals(null, values);
    // Invalid format - only separator : 
    values = VFormatReader.parseLine(":");
    assertEquals(null, values);

    // Invalid format - only separator and name  - no actual value 
    values = VFormatReader.parseLine("name:");
    assertEquals(null, values);

    // Invalid format - only separator and value  - no actual name 
    values = VFormatReader.parseLine(":value");
    assertEquals(null, values);

    // Valid format - simple 
    values = VFormatReader.parseLine("name:value");
    assertEquals(values[VFormatReader.INDEX_GROUP], null);
    assertEquals(values[VFormatReader.INDEX_NAME], "name");
    assertEquals(values[VFormatReader.INDEX_PARAMS], null);
    assertEquals(values[VFormatReader.INDEX_VALUE], "value");

    // Valid format - with parameters 
    values = VFormatReader.parseLine("name;param1;param2;param3:value");
    assertEquals(values[VFormatReader.INDEX_GROUP], null);
    assertEquals(values[VFormatReader.INDEX_NAME], "name");
    assertEquals(values[VFormatReader.INDEX_PARAMS],
        ";param1;param2;param3");
    assertEquals(values[VFormatReader.INDEX_VALUE], "value");

    values = VFormatReader
        .parseLine("group.name;param1;type=param2;param3:value");
    assertEquals(values[VFormatReader.INDEX_GROUP], "group");
    assertEquals(values[VFormatReader.INDEX_NAME], "name");
    assertEquals(values[VFormatReader.INDEX_PARAMS],
        ";param1;type=param2;param3");
    assertEquals(values[VFormatReader.INDEX_VALUE], "value");

    values = VFormatReader
        .parseLine("name;param1;type=\"email:info@optimasc.com\";type=param3,param4:value");
    assertEquals(values[VFormatReader.INDEX_GROUP], null);
    assertEquals(values[VFormatReader.INDEX_NAME], "name");
    assertEquals(values[VFormatReader.INDEX_PARAMS],
        ";param1;type=\"email:info@optimasc.com\";type=param3,param4");
    assertEquals(values[VFormatReader.INDEX_VALUE], "value");

    values = VFormatReader
        .parseLine("group.name;param1;type=\"http://param2;\";type=param3,param4:value");
    assertEquals(values[VFormatReader.INDEX_GROUP], "group");
    assertEquals(values[VFormatReader.INDEX_NAME], "name");
    assertEquals(values[VFormatReader.INDEX_PARAMS],
        ";param1;type=\"http://param2;\";type=param3,param4");
    assertEquals(values[VFormatReader.INDEX_VALUE], "value");

    values = VFormatReader
        .parseLine("group.name;param1;type=\"email:info@optimasc.com\";type=param3,param4:value");
    assertEquals(values[VFormatReader.INDEX_GROUP], "group");
    assertEquals(values[VFormatReader.INDEX_NAME], "name");
    assertEquals(values[VFormatReader.INDEX_PARAMS],
        ";param1;type=\"email:info@optimasc.com\";type=param3,param4");
    assertEquals(values[VFormatReader.INDEX_VALUE], "value");

  }

  private void checkAtts(Attribute attr,
      String namespaceURI, String localName, String value)
  {
    assertEquals(namespaceURI, attr.getNamespaceURI());
    assertEquals(localName, attr.getLocalName());
    assertEquals(value, attr.getValue());
  }

  public void testParseAttributes()
  {
    Vector v = new Vector();
    Attribute attr;

    /******************************************** Invalid test cases ****************************************/
    // Test 01
    try
    {
      v.clear();
      VFormatReader.parseAttributes(v, "global:=");
      fail("Illegal characters in parameter name");
    } catch (DocumentStreamException e)
    {
    }

    // Test 02
    try
    {
      v.clear();
      VFormatReader.parseAttributes(v, "global\"");
      fail("Illegal characters in parameter name");
    } catch (DocumentStreamException e)
    {
    }

    /******************************************** Valid test cases ****************************************/

    // Test 01
    try
    {
      VFormatReader.parseAttributes(v, "");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    assertEquals(v.size(), 0);
    v.clear();

    // Test 02
    try
    {
      VFormatReader.parseAttributes(v, ";param1;param2;param3");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    assertEquals(v.size(), 3);
    attr = (Attribute) v.elementAt(0);
    checkAtts(attr, null, null, "param1");
    attr = (Attribute) v.elementAt(1);
    checkAtts(attr, null, null, "param2");
    attr = (Attribute) v.elementAt(2);
    checkAtts(attr, null, null, "param3");

    // Test 03
    v.clear();
    try
    {
      VFormatReader.parseAttributes(v, ";type=param1;param2;param3");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    assertEquals(v.size(), 3);
    attr = (Attribute) v.elementAt(0);
    checkAtts(attr, null, "type", "param1");
    attr = (Attribute) v.elementAt(1);
    checkAtts(attr, null, null, "param2");
    attr = (Attribute) v.elementAt(2);
    checkAtts(attr, null, null, "param3");

    // Test 03
    v.clear();
    try
    {
      VFormatReader.parseAttributes(v, ";param1;type=param2;p3=param3");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    assertEquals(v.size(), 3);
    attr = (Attribute) v.elementAt(0);
    checkAtts(attr, null, null, "param1");
    attr = (Attribute) v.elementAt(1);
    checkAtts(attr, null, "type", "param2");
    attr = (Attribute) v.elementAt(2);
    checkAtts(attr, null, "p3", "param3");

    // Test 04
    v.clear();
    try
    {
      VFormatReader.parseAttributes(v, "type=");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    attr = (Attribute) v.elementAt(0);
    checkAtts(attr, null, "type", "");
    v.clear();

    // Test 05
    v.clear();
    try
    {
      VFormatReader.parseAttributes(v, "type= param1");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    attr = (Attribute) v.elementAt(0);
    checkAtts(attr, null, "type", "param1");
    v.clear();

    // Test 06
    v.clear();
    try
    {
      VFormatReader.parseAttributes(v, "type== param1");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    attr = (Attribute) v.elementAt(0);
    checkAtts(attr, null, "type", "= param1");
    v.clear();

    // Test 07
    v.clear();
    try
    {
      VFormatReader.parseAttributes(v,
          ";param1;type=\"email:info@optimasc.com\";type=param3,param4");
    } catch (DocumentStreamException e)
    {
      fail();
    }
    attr = (Attribute) v.elementAt(0);
    checkAtts(attr, null, null, "param1");
    attr = (Attribute) v.elementAt(1);
    checkAtts(attr, null, "type", "email:info@optimasc.com");
    attr = (Attribute) v.elementAt(2);
    checkAtts(attr, null, "type", "param3,param4");
    v.clear();

  }

  public void testParseParamValues()
  {

    String s;
    //------------------------------------ Invalid test cases ------------------------------------
    try
    {
      s = VFormatReader.parseParamValues("param1,p:ff");
      fail();
    } catch (DocumentStreamException e)
    {
    }
    

    //------------------------------------ Valid test cases ------------------------------------
    
    // Test 01
    try
    {
      s = VFormatReader.parseParamValues("param3");
      assertEquals("param3", s);
    } catch (DocumentStreamException e)
    {
      fail("Valid parameter values!");
    }
    
    // Test 02
    try
    {
      s = VFormatReader.parseParamValues(" param3  ");
      assertEquals(" param3  ", s);
    } catch (DocumentStreamException e)
    {
      fail("Valid parameter values!");
    }

    // Test 03
    try
    {
      s = VFormatReader.parseParamValues(" param3  ,\"email:info@optimasc.com\"");
      assertEquals(" param3  ,email:info@optimasc.com", s);
    } catch (DocumentStreamException e)
    {
      fail("Valid parameter values!");
    }

    // Test 03
    try
    {
      s = VFormatReader.parseParamValues("param3  ,\" spacing \"");
      assertEquals("param3  , spacing ", s);
    } catch (DocumentStreamException e)
    {
      fail("Valid parameter values!");
    }
    
  }

}
