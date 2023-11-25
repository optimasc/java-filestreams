package com.optimasc.streams.internal;

import java.util.Calendar;
import java.util.Date;

import com.optimasc.archive.ArchiveEntry;
import com.optimasc.text.BaseISO8601Date;
import com.optimasc.streams.Attribute;

public class ResourceChunkInfo extends ChunkInfo implements ResourceType
{
  Boolean encrypted;
  Date    lastModified;
  Date    created;
  Date    lastAccessed;
  byte[]  digest;
  String  digestType;
  String  compression;
  String  encryptionType;
  int     encryptionKeyLength;
  
  protected long    uncompressedSize;
  String  comment;
  
  public Boolean isEncrypted()
  {
    return encrypted;
  }

  public Date getLastModifiedDate()
  {
    return lastModified;
  }

  public Date getLastAccessedDate()
  {
    return lastAccessed;
  }

  public Date getCreatedDate()
  {
    return created;
  }

  public byte[] getHash()
  {
    return digest;
  }

  public String getHashType()
  {
    return digestType;
  }

  public String getCompression()
  {
    return compression;
  }

  public void reset()
  {
    super.reset();
    encrypted = null;
    lastModified = null;
    created = null;
    lastAccessed = null;
    digest = null;
    digestType = null;
    compression = null;
    uncompressedSize = -1;
    comment = null;
    encryptionType = null;
    encryptionKeyLength = -1;
  }

  public void copy(ChunkInfo f)
  {
    super.copy(f);
    if (f instanceof ResourceChunkInfo)
    {
      ResourceChunkInfo f1 = (ResourceChunkInfo)f;
      encrypted = f1.encrypted;
      lastModified = f1.lastModified;
      created = f1.created;
      lastAccessed = f1.lastAccessed;
      digest = f1.digest;
      digestType = f1.digestType;
      compression = f1.compression;
      uncompressedSize = f1.uncompressedSize;
      comment = f1.comment;
      encryptionType = f1.encryptionType;
      encryptionKeyLength = f1.encryptionKeyLength;
    }
  }

  public Object clone()
  {
    ChunkInfo chunk = new ResourceChunkInfo();
    chunk.copy(this);
    return chunk;
  }

  public boolean equals(Object obj)
  {
    int i;
    if (super.equals(obj)==false)
      return false;
    if ((obj instanceof ResourceChunkInfo) == false)
    {
      return false;
    }
    ResourceChunkInfo other = (ResourceChunkInfo) obj;
    
    if (encrypted.equals(other.encrypted)==false)
      return false;
    if (lastModified.equals(other.lastModified)==false)
      return false;
    if (created.equals(other.created)==false)
      return false;
    if (lastAccessed.equals(other.lastAccessed)==false)
      return false;
    if (digestType.equals(other.digestType)==false)
      return false;
    if (compression.equals(other.compression)==false)
      return false;
    if (comment.equals(other.comment)==false)
      return false;
    if (digest.length != other.digest.length)
      return false;
    if (uncompressedSize != other.uncompressedSize)
      return false;
    if (encryptionType.equals(other.encryptionType)==false)
      return false;
    if (encryptionKeyLength != other.encryptionKeyLength)
      return false;
    for (i = 0; i < digest.length; i++)
    {
      if (digest[i]!=other.digest[i])
        return false;
    }
    return true;
  }

  public long getSize()
  {
    return uncompressedSize;
  }

  public String getComment()
  {
    return comment;
  }
  
  public String getEncryptionType()
  {
    return encryptionType;
  }
  
  public int getEncryptionKeyLength()
  {
    return encryptionKeyLength;
  }
  

  public void setEncrypted(Boolean encrypted)
  {
    this.encrypted = encrypted;
  }
  
  public void setEncryptionType(String encryptionType)
  {
    this.encryptionType = encryptionType;
  }
  
  public void setEncryptionKeyLength(int encryptionKeyLength)
  {
    this.encryptionKeyLength = encryptionKeyLength;
  }

  public void setDigest(byte[] digest)
  {
    this.digest = digest;
  }

  public void setDigestType(String digestType)
  {
    this.digestType = digestType;
  }

  public void setCompression(String compression)
  {
    this.compression = compression;
  }

  public void setSize(long realSize)
  {
    this.uncompressedSize = realSize;
  }

  public void setComment(String comment)
  {
    this.comment = comment;
  }

  public void setLastModifiedDate(Date lastModified)
  {
    this.lastModified = lastModified;
  }

  public void setCreatedDate(Date created)
  {
    this.created = created;
  }

  public void setLastAccessedDate(Date lastAccessed)
  {
    this.lastAccessed = lastAccessed;
  }
  
  
  
  /** Sets the attributes for this resource. */
  public void addStandardAttributes()
  {
    Calendar cal1 = Calendar.getInstance();
    Calendar cal2 = Calendar.getInstance();
    Calendar cal3 = Calendar.getInstance();
    if (encrypted != null)
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_ENCRYPTION,ATTRIBUTE_NAME_ENCRYPTION,encrypted.toString()));
    if (encryptionType != null)
    {
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_ENCRYPTION_TYPE,ATTRIBUTE_NAME_ENCRYPTION_TYPE,encryptionType));
    }
    if (encryptionKeyLength != -1)
    {
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_ENCRYPTION_KEYLENGTH,ATTRIBUTE_NAME_ENCRYPTION_KEYLENGTH,Integer.toString(encryptionKeyLength)));
    }
    if (compression != null)
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_COMPRESSION_TYPE,ATTRIBUTE_NAME_COMPRESSION_TYPE,compression));
    if (comment != null)
    {
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_COMMENT,ATTRIBUTE_NAME_COMMENT,comment));
    }
    if (created != null)
    {
      cal1.setTime(created);
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_DATE_CREATED,ATTRIBUTE_NAME_DATE_CREATED,BaseISO8601Date.toString(cal1, true, false)));
    }
    if (lastModified != null)
    {
     cal2.setTime(lastModified);
     attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_DATE_MODIFIED,ATTRIBUTE_NAME_DATE_MODIFIED,BaseISO8601Date.toString(cal2, true, false)));
    }
    if (lastAccessed != null)
    {
     cal3.setTime(lastAccessed);
     attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_DATE_ACCESSED,ATTRIBUTE_NAME_DATE_ACCESSED,BaseISO8601Date.toString(cal3, true, false)));
    }
    attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_SIZE,ATTRIBUTE_NAME_SIZE,new Long(uncompressedSize).toString()));
    if (digestType != null)
    {
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_HASH_TYPE,ATTRIBUTE_NAME_HASH_TYPE,digestType));
    }
    if (digest != null)
    {
      StringBuffer s = new StringBuffer();
      for (int i =0; i < digest.length; i++)
      {
        s.append(hexByte(digest[i]));
      }
      attributes.addElement(new Attribute(ATTRIBUTE_NAMESPACE_HASH_VALUE,ATTRIBUTE_NAME_HASH_VALUE,s.toString()));
    }
  }
  
  /** Converts a byte to a hexadecimal value with necessary leading zeros. */
  protected String hexByte(byte b)
  {
    return Integer.toHexString(0x100 | (b & 0xFF)).substring(1).toUpperCase();
  }

  public String getName()
  {
    return id.toString();
  }

  public boolean isDirectory()
  {
    return false;
  }
  

  
}
