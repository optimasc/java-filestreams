package com.optimasc.zip;

import com.optimasc.streams.internal.ChunkUtilities;

public class ZIPUtilities extends ChunkUtilities
{
   /** local file header magic */
   public static final int MAGIC_LOCAL_HEADER = 0x04034b50;
   /** data descriptor magic (optional) */
   public static final int MAGIC_DATA_DESCRIPTOR = 0x08074b50;
   /** archive extra data record magic */
   public static final int MAGIC_ARCHIVE_EXTRA_DATA = 0x08064b50;
   /** central directory header record magic */
   public static final int MAGIC_CENTRAL_DIRECTORY_HEADER = 0x02014b50;   
   public static final int MAGIC_DIGITAL_SIGNATURE = 0x05054b50;
   /** zip64 end of central directory magic */
   public static final int MAGIC_ZIP64_CENTRAL_DIRECTORY_END = 0x06064b50;
   /** zip64 end of central directory locator magic */
   public static final int MAGIC_ZIP64_CENTRAL_DIRECTORY_LOCATOR = 0x07064b50;
   /** end of central directory record magic */
   public static final int MAGIC_CENTRAL_DIRECTORY_END = 0x06054b50;
   
   public static final String MIME_TYPE = "application/zip";

   /** General purpose bit flag: Indicates data descriptor information */
   public static final int DATA_DESCRIPTOR_BIT = 0x0008;
   /** General purpose bit flag: Indicates UTF-8 encoded filenames and comments */
   public static final int UTF8_ENCODING_BIT = 0x0800;
   /** General purpose bit flag: Indicates encrypted file */
   public static final int ENCRYPTED_BIT = 0x0001;
   
   
   /** Compression type: The file is stored (no compression) */
   public static final int COMPRESSION_NONE = 0;
   /** Compression type: Shrink */
   public static final int COMPRESSION_SHRINK = 1;
   /** Compression type: Reduce with compression factor 1 */
   public static final int COMPRESSION_REDUCE1 = 2;
   /** Compression type: Reduce with compression factor 2 */
   public static final int COMPRESSION_REDUCE2 = 3;
   /** Compression type: Reduce with compression factor 3 */
   public static final int COMPRESSION_REDUCE3 = 4;
   /** Compression type: Reduce with compression factor 4 */
   public static final int COMPRESSION_REDUCE4 = 5;
   /** Compression type: Implode */
   public static final int COMPRESSION_IMPLODE = 6;
   /** Compression type: Deflate */
   public static final int COMPRESSION_DEFLATE = 8;
   /** Compression type: Deflate64 */
   public static final int COMPRESSION_DEFLATE64 = 9;
   /** Compression type: bzip2 */
   public static final int COMPRESSION_BZIP2 = 12;
   /** Compression type: LZMA */
   public static final int COMPRESSION_LZMA = 14;
   /** Compression type: Wavpack */
   public static final int COMPRESSION_WAVPACK = 97;
   /** Compression type: PPMd */
   public static final int COMPRESSION_PPMD = 98;
   
   
   /* Private chunk identifiers in ExtraData field */
   public static final int CHUNK_UNICODE_PATH = 0x7075;
   public static final int CHUNK_UNICODE_COMMENT = 0x6375;
   public static final int CHUNK_NTFS_ATTRIBUTES = 0x000A;
   public static final int CHUNK_STRONG_ENCRYPTION = 0x0017;
   public static final int CHUNK_UNIX_TIMESTAMP = 0x5455;
   
   public static final int ENCRYPTION_DES       = 0x6601;
   public static final int ENCRYPTION_RC2       = 0x6602;
   public static final int ENCRYPTION_3DES168   = 0x6603;
   public static final int ENCRYPTION_3DES112   = 0x6609;
   public static final int ENCRYPTION_AES128    = 0x660E;
   public static final int ENCRYPTION_AES192    = 0x660F;
   public static final int ENCRYPTION_AES256    = 0x6610;
   public static final int ENCRYPTION_RC2_NEW   = 0x6702;
   public static final int ENCRYPTION_BLOWFISH  = 0x6720;
   public static final int ENCRYPTION_TWOFISH   = 0x6721;
   public static final int ENCRYPTION_RC4       = 0x6801;
   
   
   /* UNIX timestamp bitmasks for flag */
   
   /** mtime value is present in local header */
   public static final int UNIX_TIMESTAMP_MTIME = 0x01;
   /** atime value is present in local header */
   public static final int UNIX_TIMESTAMP_ATIME = 0x02;
   /** ctime value is present in local header */
   public static final int UNIX_TIMESTAMP_CTIME = 0x04;
   
   
   
   
   private final static int compressionValues[] =
   {
     COMPRESSION_NONE,
     COMPRESSION_SHRINK,
     COMPRESSION_REDUCE1, 
     COMPRESSION_REDUCE2, 
     COMPRESSION_REDUCE3,
     COMPRESSION_REDUCE4,
     COMPRESSION_IMPLODE,
     COMPRESSION_DEFLATE, 
     COMPRESSION_DEFLATE64, 
     COMPRESSION_BZIP2, 
     COMPRESSION_LZMA, 
     COMPRESSION_WAVPACK, 
     COMPRESSION_PPMD 
   };
   
   private final static String compressionStrings[] =
   {
     "",
     "Shrink",
     "Reduce1", 
     "Reduce2", 
     "Reduce3",
     "Redeuce4",
     "Implode",
     "Deflate", 
     "Deflate64", 
     "bzip2",
     "LZMA",
     "WavPack",
     "PPMd" 
   };
   
       
   
   /** Converts a pkzip compatible type compression value
    *  to a standard string value as definied in the metadata terms
    *  document. 
    * 
    * @param type
    * @return The algorithm represented as a string, or
    *   NULL if unknown, or empty if there is no compression.
    */
   public static String compressionToString(int type)
   {
     if (compressionValues.length != compressionStrings.length)
       throw new RuntimeException("Illegal array lengths");
     for (int i = 0; i < compressionValues.length; i++)
     {
       if (type == compressionValues[i])
         return compressionStrings[i];
     }
     return null;
   }


  public String chunkIDToObject(Object id) throws IllegalArgumentException
  {
    return id.toString();
  }

  public String groupIDToObject(Object id) throws IllegalArgumentException
  {
    return id.toString();
  }

  public void validateChunkSize(long size) throws IllegalArgumentException
  {
  }


  public void validateGroupSize(long size) throws IllegalArgumentException
  {
  }
   
   

}
