package com.optimasc.streams;



/** Represents an attribute value. 
 * 
 * @author Carl Eric Codere
 *
 */
public class Attribute
{
   /** The namespace of this attribute */
   protected String namespaceURI;
   /** The local name of this attribute */ 
   protected String localName;
   /** The value of this attribute */
   protected String value;
   /** The name prefix of this attribute */
   protected String prefix;
   
   public Attribute(String nameSpaceURI, String prefix, String localName, String value)
   {
     this.namespaceURI = nameSpaceURI;
     this.localName = localName;
     this.value = value;
     this.prefix = prefix;
   }
   
   public Attribute(String nameSpaceURI, String localName, String value)
   {
     this.namespaceURI = nameSpaceURI;
     this.localName = localName;
     this.value = value;
     this.prefix = null;
   }
   
   
   public Attribute()
   {
   }  
   
   public String toString()
   {
     String suffix = "";
     String value = "";
     if ((localName!=null) && (localName.length()!=0))
     {
       suffix = localName;
     }
     if ((prefix!=null) && (prefix.length()!=0))
     {
       suffix = prefix + ":" + suffix;
     }
     return suffix+"="+value;
   }

  public String getNamespaceURI()
  {
    return namespaceURI;
  }

  public void setNamespaceURI(String namespaceURI)
  {
    this.namespaceURI = namespaceURI;
  }

  public String getLocalName()
  {
    return localName;
  }

  public void setLocalName(String localName)
  {
    this.localName = localName;
  }

  public String getValue()
  {
    return value;
  }

  public void setValue(String value)
  {
    this.value = value;
  }

  public String getPrefix()
  {
    return prefix;
  }

  public void setPrefix(String prefix)
  {
    this.prefix = prefix;
  }
   
  public Object clone()
  {
    Attribute attr = new Attribute();
    attr.copy(this);
    return attr;
  }

  public boolean equals(Object obj)
  {
    if (obj==null)
      return false;
    if ((obj instanceof Attribute) == false)
    {
      return false;
    }
    Attribute other = (Attribute) obj;

    
    if ((other.namespaceURI==null) && (namespaceURI!=null))
      return false;
    if ((other.namespaceURI!=null) && (namespaceURI==null))
      return false;
    

    if ((other.namespaceURI!=null) && (other.namespaceURI.equals(namespaceURI)==false))
    {
      return false;
    }
    if (other.localName.equals(localName)==false)
    {
      return false;
    }
    if (other.value.equals(value)==false)
    {
      return false;
    }
    
    if ((other.prefix==null) && (prefix!=null))
      return false;
    if ((other.prefix!=null) && (prefix==null))
      return false;
    
    if ((other.prefix!=null) && (other.prefix.equals(prefix)==false))
    {
      return false;
    }
    return true;
  }

  /**
   * Copies the attributes from the specified Attribute into this Attribute.
   * 
   * @param f
   *          The value to copy from.
   */
  public void copy(Attribute f)
  {
    namespaceURI = f.namespaceURI;
    localName = f.localName;
    prefix = f.prefix;
    value = f.value;
  }
  
   
}
