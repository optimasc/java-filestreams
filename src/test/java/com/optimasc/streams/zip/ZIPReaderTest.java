package com.optimasc.streams.zip;

import java.io.FileNotFoundException;


import com.optimasc.stream.ElementInfo;
import com.optimasc.stream.TestUtilities;
import com.optimasc.streams.DefaultStreamFilter;
import com.optimasc.streams.DocumentStreamConstants;
import com.optimasc.streams.DocumentStreamException;
import com.optimasc.streams.internal.ResourceType;
import com.optimasc.streams.riff.RIFFReader;
import com.optimasc.streams.Attribute;
import com.optimasc.zip.ZIPReaderImpl;

import junit.framework.TestCase;

public class ZIPReaderTest extends TestCase
{

  protected void setUp() throws Exception
  {
    super.setUp();
  }

  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  protected static final Attribute test00Attributes[] =
  {
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION,ResourceType.ATTRIBUTE_NAME_ENCRYPTION,"false"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ResourceType.ATTRIBUTE_NAME_COMPRESSION_TYPE,"Implode"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ResourceType.ATTRIBUTE_NAME_DATE_MODIFIED,"2017-01-09T00:06:48"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_SIZE,ResourceType.ATTRIBUTE_NAME_SIZE,"1579064"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_HASH_VALUE,ResourceType.ATTRIBUTE_NAME_HASH_VALUE,"BA26AB97")  
  };
  
  protected static final ElementInfo test00Elements[] =
  {
      new ElementInfo("LAITIERE.BMP",1243278,test00Attributes)
  };
  
  public void testLaitiere00() throws FileNotFoundException
  {

    try
    {

      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/milkmaid_000.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,test00Elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  protected static final Attribute test01Attributes[] =
  {
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION,ResourceType.ATTRIBUTE_NAME_ENCRYPTION,"false"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ResourceType.ATTRIBUTE_NAME_COMPRESSION_TYPE,"Deflate"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_CREATED,ResourceType.ATTRIBUTE_NAME_DATE_CREATED,"2017-01-09T07:06:47"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ResourceType.ATTRIBUTE_NAME_DATE_MODIFIED,"2017-01-09T07:06:48"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_ACCESSED,ResourceType.ATTRIBUTE_NAME_DATE_ACCESSED,"2017-01-09T07:00:00"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_SIZE,ResourceType.ATTRIBUTE_NAME_SIZE,"1579064"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_HASH_VALUE,ResourceType.ATTRIBUTE_NAME_HASH_VALUE,"BA26AB97"),  
  };
  
  protected static final ElementInfo test01Elements[] =
  {
      new ElementInfo("LAITI\u00C8RE.BMP",1125666,test01Attributes)
  };
  
  
  public void testLaitiere01() throws FileNotFoundException
  {

    try
    {

      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/milkmaid_001.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,test01Elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  public void testLaitiere02() throws FileNotFoundException
  {

    try
    {

      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/milkmaid_002.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,null);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  protected static final Attribute test03Attributes[] =
  {
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION,ResourceType.ATTRIBUTE_NAME_ENCRYPTION,"false"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ResourceType.ATTRIBUTE_NAME_COMPRESSION_TYPE,"Deflate"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ResourceType.ATTRIBUTE_NAME_DATE_MODIFIED,"2017-01-09T00:06:48"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_SIZE,ResourceType.ATTRIBUTE_NAME_SIZE,"1579064"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_HASH_VALUE,ResourceType.ATTRIBUTE_NAME_HASH_VALUE,"BA26AB97"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMMENT,ResourceType.ATTRIBUTE_NAME_COMMENT,"This is a comment."),  
  };
  
  protected static final ElementInfo test03Elements[] =
  {
      new ElementInfo("LAITI\u00C8RE.BMP",1122020,test03Attributes)
  };
  
  
  public void testLaitiere03() throws FileNotFoundException
  {

    try
    {

      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/milkmaid_003.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,test03Elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  
  protected static final Attribute test10Attributes[] =
  {
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION,ResourceType.ATTRIBUTE_NAME_ENCRYPTION,"false"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ResourceType.ATTRIBUTE_NAME_COMPRESSION_TYPE,"Deflate"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_SIZE,ResourceType.ATTRIBUTE_NAME_SIZE,"1579064"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_HASH_VALUE,ResourceType.ATTRIBUTE_NAME_HASH_VALUE,"BA26AB97"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_CREATED,ResourceType.ATTRIBUTE_NAME_DATE_CREATED,"2017-01-10T06:47:49"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ResourceType.ATTRIBUTE_NAME_DATE_MODIFIED,"2017-01-09T07:06:48"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_ACCESSED,ResourceType.ATTRIBUTE_NAME_DATE_ACCESSED,"2017-01-09T07:00:00"),  
  };
  
  protected static final ElementInfo test10Elements[] =
  {
      new ElementInfo("LAITI\u00C8RE.BMP",1085333,test10Attributes)
  };
  

  public void testLaitiere10() throws FileNotFoundException
  {

    try
    {

      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/milkmaid_010.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,test10Elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }

  
  protected static final Attribute test11Attributes[] =
  {
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION,ResourceType.ATTRIBUTE_NAME_ENCRYPTION,"false"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ResourceType.ATTRIBUTE_NAME_COMPRESSION_TYPE,"Deflate"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_SIZE,ResourceType.ATTRIBUTE_NAME_SIZE,"1579064"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_HASH_VALUE,ResourceType.ATTRIBUTE_NAME_HASH_VALUE,"BA26AB97"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_CREATED,ResourceType.ATTRIBUTE_NAME_DATE_CREATED,"2017-01-10T06:47:49"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ResourceType.ATTRIBUTE_NAME_DATE_MODIFIED,"2017-01-09T07:06:48"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_ACCESSED,ResourceType.ATTRIBUTE_NAME_DATE_ACCESSED,"2017-01-09T07:00:00"),  
  };
  
  protected static final ElementInfo test11Elements[] =
  {
      new ElementInfo("LAITIERE.BMP",1085333,test11Attributes)
  };
  
  public void testLaitiere11() throws FileNotFoundException
  {

    try
    {
      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/milkmaid_011.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,test11Elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }

  protected static final Attribute test12Attributes[] =
  {
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION,ResourceType.ATTRIBUTE_NAME_ENCRYPTION,"true"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION_TYPE,ResourceType.ATTRIBUTE_NAME_ENCRYPTION_TYPE,"AES"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION_KEYLENGTH,ResourceType.ATTRIBUTE_NAME_ENCRYPTION_KEYLENGTH,"128"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ResourceType.ATTRIBUTE_NAME_COMPRESSION_TYPE,"Deflate"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_SIZE,ResourceType.ATTRIBUTE_NAME_SIZE,"1579064"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_HASH_VALUE,ResourceType.ATTRIBUTE_NAME_HASH_VALUE,"BA26AB97"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_CREATED,ResourceType.ATTRIBUTE_NAME_DATE_CREATED,"2017-01-20T07:04:01"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ResourceType.ATTRIBUTE_NAME_DATE_MODIFIED,"2017-01-09T07:06:48"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_ACCESSED,ResourceType.ATTRIBUTE_NAME_DATE_ACCESSED,"2017-01-20T07:00:00"),  
  };
  
  protected static final ElementInfo test12Elements[] =
  {
      new ElementInfo("LAITI\u00C8RE.BMP",1085654,test12Attributes)
  };
  
  
  public void testLaitiere12() throws FileNotFoundException
  {

    try
    {
      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/milkmaid_012.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,test12Elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  
  protected static final Attribute testCalgary00Attributes[] =
  {
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_ENCRYPTION,ResourceType.ATTRIBUTE_NAME_ENCRYPTION,"false"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ResourceType.ATTRIBUTE_NAME_COMPRESSION_TYPE,"Deflate"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_SIZE,ResourceType.ATTRIBUTE_NAME_SIZE,"1579064"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_HASH_VALUE,ResourceType.ATTRIBUTE_NAME_HASH_VALUE,"BA26AB97"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_CREATED,ResourceType.ATTRIBUTE_NAME_DATE_CREATED,"2017-01-09T23:47:49"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ResourceType.ATTRIBUTE_NAME_DATE_MODIFIED,"2017-01-09T00:06:48"),  
     new Attribute(ResourceType.ATTRIBUTE_NAMESPACE_DATE_ACCESSED,ResourceType.ATTRIBUTE_NAME_DATE_ACCESSED,"2017-01-09T00:00:00"),  
  };
  
  protected static final ElementInfo testCalgary00Elements[] =
  {
      new ElementInfo("bib",35038,null),
      new ElementInfo("book1",313351,null),
      new ElementInfo("book2",206628,null),
      new ElementInfo("geo",68471,null),
      new ElementInfo("news",144464,null),
      new ElementInfo("obj1",10301,null),
      new ElementInfo("obj2",81572,null),
      new ElementInfo("paper1",18547,null),
      new ElementInfo("paper2",29728,null),
      new ElementInfo("paper3",18070,null),
      new ElementInfo("paper4",5511,null),
      new ElementInfo("paper5",4970,null),
      new ElementInfo("paper6",13204,null),
      new ElementInfo("pic",55924,null),
      new ElementInfo("progc",13248,null),
      new ElementInfo("progl",16249,null),
      new ElementInfo("progp",11202,null),
      new ElementInfo("trans",18936,null)
  };
  
  
  public void testCalgary00() throws FileNotFoundException
  {

    try
    {

      ZIPReaderImpl reader = new ZIPReaderImpl(getClass()
          .getResourceAsStream("/res/calgary_002.zip"), new DefaultStreamFilter());
      TestUtilities.parse(reader,testCalgary00Elements);
    } catch (DocumentStreamException e)
    {
      fail();
    }
  }
  
  
}
