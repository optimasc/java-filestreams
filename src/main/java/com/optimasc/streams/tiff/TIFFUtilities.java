package com.optimasc.streams.tiff;

import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.Charset;

public class TIFFUtilities
{

  /** Data consists of 8-bit unsigned data */
  public static final int TIFF_TYPE_BYTE = 1;
  /** Data consists of ASCII data */
  public static final int TIFF_TYPE_ASCII = 2;
  /** Data consists of 16-bit unsigned data */
  public static final int TIFF_TYPE_SHORT = 3;
  /** Data consists of 32-bit unsigned data */
  public static final int TIFF_TYPE_LONG = 4;
  public static final int TIFF_TYPE_RATIONAL = 5;
  /** Data consists of 8-bit signed data */
  public static final int TIFF_TYPE_SBYTE = 6;
  public static final int TIFF_TYPE_UNDEFINED = 7;
  /** Data consists of 16-bit signed data */
  public static final int TIFF_TYPE_SSHORT = 8;
  /** Data consists of 32-bit signed data */
  public static final int TIFF_TYPE_SLONG = 9;
  public static final int TIFF_TYPE_SRATIONAL = 10;
  public static final int TIFF_TYPE_FLOAT = 11;
  public static final int TIFF_TYPE_DOUBLE = 12;

  /** Tiff tags */
  public static final int TIFF_ID_NEWSUBFILETYPE = 254;
  public static final int TIFF_ID_SUBFILETYPE = 255;
  public static final int TIFF_ID_IMAGEWIDTH = 256; // width of image in pixels
  public static final int TIFF_ID_IMAGELENGTH = 257; // height of image in pixels
  public static final int TIFF_ID_BITSPERSAMPLE = 258;
  /** Compression Compression scheme used on the image data. */
  public static final int TIFF_ID_COMPRESSION = 259;
  /** The color space of the image data. */
  public static final int TIFF_ID_PHOTOMETRICINTERPRETATION = 262;
  /**
   * For black and white TIFF files that represent shades of gray, the technique
   * used to convert from gray to black and white pixels.
   */
  public static final int TIFF_ID_THRESHHOLDING = 263;
  /**
   * The width of the dithering or halftoning matrix used to create a dithered
   * or halftoned bilevel file.
   */
  public static final int TIFF_ID_CELLWIDTH = 264;
  /**
   * The length of the dithering or halftoning matrix used to create a dithered
   * or halftoned bilevel file.
   */
  public static final int TIFF_ID_CELLLENGTH = 265;
  /** The logical order of bits within a byte. */
  public static final int TIFF_ID_FILLORDER = 266;
  /** A string that describes the subject of the image. */
  public static final int TIFF_ID_IMAGEDESCRIPTION = 270;
  /** The scanner manufacturer. */
  public static final int TIFF_ID_MAKE = 271;
  /** The scanner model name or number. */
  public static final int TIFF_ID_MODEL = 272;
  /** For each strip, the byte offset of that strip. */
  public static final int TIFF_ID_STRIPOFFSETS = 273;
  /** The orientation of the image with respect to the rows and columns. */
  public static final int TIFF_ID_ORIENTATION = 274;
  /** The number of components per pixel. */
  public static final int TIFF_ID_SAMPLESPERPIXEL = 277;
  /** The number of rows per strip. */
  public static final int TIFF_ID_ROWSPERSTRIP = 278;
  /** For each strip, the number of bytes in the strip after compression. */
  public static final int TIFF_ID_STRIPBYTECOUNTS = 279;
  /** The minimum component value used. */
  public static final int TIFF_ID_MINSAMPLEVALUE = 280;
  /** The maximum component value used. */
  public static final int TIFF_ID_MAXSAMPLEVALUE = 281;
  /** The number of pixels per ResolutionUnit in the ImageWidth direction. */
  public static final int TIFF_ID_XRESOLUTION = 282;
  /** The number of pixels per ResolutionUnit in the ImageLength direction. */
  public static final int TIFF_ID_YRESOLUTION = 283;
  /** How the components of each pixel are stored. */
  public static final int TIFF_ID_PLANARCONFIGURATION = 284;
  /**
   * For each string of contiguous unused bytes in a TIFF file, the byte offset
   * of the string.
   */
  public static final int TIFF_ID_FREEOFFSETS = 288;
  /**
   * For each string of contiguous unused bytes in a TIFF file, the number of
   * bytes in the string.
   */
  public static final int TIFF_ID_FREEBYTECOUNTS = 289;
  /** The precision of the information contained in the GrayResponseCurve. */
  public static final int TIFF_ID_GRAYRESPONSEUNIT = 290;
  /** For grayscale data, the optical density of each possible pixel value. */
  public static final int TIFF_ID_GRAYRESPONSECURVE = 291;
  /** The unit of measurement for XResolution and YResolution. */
  public static final int TIFF_ID_RESOLUTIONUNIT = 296;
  /**
   * Name and version number of the software package(s) used to create the
   * image.
   */
  public static final int TIFF_ID_SOFTWARE = 305;
  /** Date and time of image creation. */
  public static final int TIFF_ID_DATETIME = 306;
  /** Person who created the image. */
  public static final int TIFF_ID_ARTIST = 315;
  /** The computer and/or operating system in use at the time of image creation. */
  public static final int TIFF_ID_HOSTCOMPUTER = 316;
  /** A color map for palette color images. */
  public static final int TIFF_ID_COLORMAP = 320;
  /** Description of extra components. */
  public static final int TIFF_ID_EXTRASAMPLES = 338;
  /** Copyright notice. */
  public static final int TIFF_ID_COPYRIGHT = 33432;
  /** XMP DATA BLOCK */
  public static final int TIFF_ID_XMP = 700;
  /** DNG Version information */
  public static final int TIFF_ID_DNGVERSION = 50706;
  /** Document name from which this image was scanned from */
  public static final int TIFF_ID_DOCUMENTNAME = 269;
  /**
   * Internal TIFF type for the parser, represents a block containing the image
   * data.
   */
  public static final int TIFF_ID_DATA = 65535;

  /********************************************************************
   * EXIF TAGS
   ********************************************************************/
  public static final int TIFF_ID_EXIFVERSION = 36864;
  public static final int EXIF_ID_UNIQUEID = 42016;
  public static final int EXIF_ID_ORIGINALDATETIME = 36867;
  public static final int EXIF_ID_POINTER = 34665;
  public static final int EXIF_ID_ISO_SPEED_RATINGS = 34855;
  public static final int EXIF_ID_FLASH = 37385;
  public static final int EXIF_ID_FOCAL_LENGTH = 37386;
  public static final int EXIF_ID_APERTURE = 37378;
  public static final int EXIF_ID_SHUTTER_SPEED = 37377;
  public static final int EXIF_ID_EXPOSURE_TIME = 33434;
  public static final int EXIF_ID_FNUMBER = 33437;
  public static final int EXIF_ID_EXPOSURE_BIAS = 37380;
  public static final int EXIF_ID_BRIGHTNESS = 37379;

  /********************************************************************
   * GPS TAGS
   ********************************************************************/
  /** Indicates the version of GPSInfoIFD. */
  public static final int GPS_ID_VERSION_ID = 0;
  /** Indicates whether the latitude is north or south latitude. */
  public static final int GPS_ID_LATITUDE_REF = 1;
  /** Indicates the latitude */
  public static final int GPS_ID_LATITUDE = 2;
  /** Indicates whether the longitude is east or west longitude. */
  public static final int GPS_ID_LONGITUDE_REF = 3;
  /**
   * Indicates the longitude.} public static final int GPS_ID_LONGITUDE = 4; /**
   * Indicates the altitude used as the reference altitude.
   */
  public static final int GPS_ID_ALTITUDE_REF = 5;
  /** Indicates the altitude based on the reference in GPSAltitudeRef. */
  public static final int GPS_ID_ALTITUDE = 6;
  /** Indicates the time as UTC (Coordinated Universal Time). */
  public static final int GPS_ID_TIMESTAMP = 7;
  /** Indicates the GPS satellites used for measurements. */
  public static final int GPS_ID_SATELLITES = 8;
  /** Indicates the status of the GPS receiver when the image is recorded. */
  public static final int GPS_ID_STATUS = 9;
  /** Indicates the GPS measurement mode. */
  public static final int GPS_ID_MEASURE_MODE = 10;
  /** Indicates the GPS DOP (data degree of precision). */
  public static final int GPS_ID_DOP = 11;
  /** Indicates the unit used to express the GPS receiver speed of movement. */
  public static final int GPS_ID_SPEED_REF = 12;
  /**
   * Indicates the speed of GPS receiver movement.} public static final int
   * GPS_ID_SPEED = 13; /** Indicates the reference for giving the direction of
   * GPS receiver movement.} public static final int GPS_ID_TRACK_REF = 14; /**
   * Indicates the direction of GPS receiver movement.
   */
  public static final int GPS_ID_TRACK = 15;
  /**
   * Indicates the reference for giving the direction of the image when it is
   * captured.
   */
  public static final int GPS_ID_IMG_DIRECTION_REF = 16;
  /** Indicates the direction of the image when it was captured. */
  public static final int GPS_ID_IMG_DIRECTION = 17;
  /** Indicates the geodetic survey data used by the GPS receiver. */
  public static final int GPS_ID_MAP_DATUM = 18;
  /**
   * Indicates whether the latitude of the destination point is north or south
   * latitude.
   */
  public static final int GPS_ID_DEST_LATITUDE_REF = 19;
  /** Indicates the latitude of the destination point. */
  public static final int GPS_ID_DEST_LATITUDE = 20;
  /**
   * Indicates whether the longitude of the destination point is east or west
   * longitude.
   */
  public static final int GPS_ID_DEST_LONGITUDE_REF = 21;
  /** Indicates the longitude of the destination point. */
  public static final int GPS_ID_DEST_LONGITUDE = 22;
  /**
   * Indicates the reference used for giving the bearing to the destination
   * point.
   */
  public static final int GPS_ID_DEST_BEARING_REF = 23;
  /** Indicates the bearing to the destination point. */
  public static final int GPS_ID_DEST_BEARING = 24;
  /** Indicates the unit used to express the distance to the destination point. */
  public static final int GPS_ID_DEST_DISTANCE_REF = 25;
  /** Indicates the distance to the destination point. */
  public static final int GPS_ID_DEST_DISTANCE = 26;
  /**
   * A character string recording the name of the method used for location
   * finding.
   */
  public static final int GPS_ID_PROCESSING_METHOD = 27;
  /** A character string recording the name of the GPS area. */
  public static final int GPS_ID_AREA_INFORMATION = 28;
  /**
   * A character string recording date and time information relative to UTC
   * (Coordinated Universal Time).
   */
  public static final int GPS_ID_DATESTAMP = 29;
  /** Indicates whether differential correction is applied to the GPS receiver. */
  public static final int GPS_ID_DIFFERENTIAL = 30;
  public static final int GPS_ID_POINTER = 34853;

  /************************* Group types **********************/
  public static final int TIFF_GROUP = 65001;
  public static final int EXIF_GROUP = EXIF_ID_POINTER;
  public static final int GPS_GROUP = GPS_ID_POINTER;

  /********************************************************************
   * COMPRESSION TYPES
   ********************************************************************/

  public static final int TIFF_COMPRESSION_NONE = 1;
  public static final int TIFF_COMPRESSION_HUFFMAN = 2;
  public static final int TIFF_COMPRESSION_PACKBITS = 32773;
  public static final int TIFF_COMPRESSION_T4 = 3;
  public static final int TIFF_COMPRESSION_T6 = 4;
  public static final int TIFF_COMPRESSION_LZW = 5;
  public static final int TIFF_COMPRESSION_JPEG_OLD = 6;
  public static final int TIFF_COMPRESSION_JPEG_NEW = 7;
  public static final int TIFF_COMPRESSION_DEFLATE_EXP = 32946;
  public static final int TIFF_COMPRESSION_DEFLATE = 8;
  public static final int TIFF_COMPRESSION_JBIG_T85 = 9;
  public static final int TIFF_COMPRESSION_JBIG_T43 = 10;
  public static final int TIFF_COMPRESSION_NEXT_RLE = 32766;
  public static final int TIFF_COMPRESSION_THUNDERSCAN_RLE = 32809;
  public static final int TIFF_COMPRESSION_RASTERPAD = 32895;
  public static final int TIFF_COMPRESSION_RLE_LW = 32896;
  public static final int TIFF_COMPRESSION_RLE_HC = 32897;
  public static final int TIFF_COMPRESSION_RLE_BLW = 32898;
  public static final int TIFF_COMPRESSION_KODAK_DCS = 32947;
  public static final int TIFF_COMPRESSION_JBIG = 34661;
  public static final int TIFF_COMPRESSION_JPEG2000 = 34712;
  public static final int TIFF_COMPRESSION_NEF = 34713;

  /********************************************************************
   * COLOR SPACE TYPES
   ********************************************************************/
  public static final int TIFF_COLOR_BILEVEL_WZ = 0;
  public static final int TIFF_COLOR_BILEVEL_BZ = 1;
  public static final int TIFF_COLOR_RGB = 2;
  public static final int TIFF_COLOR_INDEXED = 3;
  public static final int TIFF_COLOR_TRANSPARENT = 4;
  public static final int TIFF_COLOR_CIELAB = 8;
  public static final int TIFF_COLOR_YUV = 6;
  public static final int TIFF_COLOR_CMYK = 5;

  public static final int TIFF_MAGIC_BIG_ENDIAN_SIGNATURE = 0x4D4D;
  public static final int TIFF_MAGIC_LITTLE_ENDIAN_SIGNATURE = 0x4949;
  public static final int TIFF_MAGIC_SIGNATURE = 42;

  public static final int[] validTypes =
  {
      TIFF_TYPE_BYTE,
      TIFF_TYPE_SBYTE,
      TIFF_TYPE_ASCII,
      TIFF_TYPE_SSHORT,
      TIFF_TYPE_SHORT,
      TIFF_TYPE_SLONG,
      TIFF_TYPE_LONG,
      TIFF_TYPE_RATIONAL,
      TIFF_TYPE_SRATIONAL,
      TIFF_TYPE_FLOAT,
      TIFF_TYPE_DOUBLE,
      TIFF_TYPE_UNDEFINED
  };

  /**
   * Re
   * 
   */
  public static final String TIFF_ATTRIBUTE_NAME_COUNT = "Count";
  public static final String TIFF_ATTRIBUTE_NAME_TYPE = "Type";

  /**
   * Determine if the type definition is valid or not. If it is valid, it
   * returns true, otherwise it returns false.
   */
  public boolean isValidType(int type)
  {
    for (int i = 0; i < validTypes.length; i++)
    {
      if (type == validTypes[i])
        return true;
    }
    return false;
  }

  public static int fieldTypeToSize(int type)
  {
    switch (type)
    {
      case TIFF_TYPE_BYTE:
      case TIFF_TYPE_SBYTE:
        return 1;
      case TIFF_TYPE_ASCII:
        return 1;
      case TIFF_TYPE_SHORT:
      case TIFF_TYPE_SSHORT:
        return 2;
      case TIFF_TYPE_SLONG:
      case TIFF_TYPE_LONG:
        return 4;
      case TIFF_TYPE_SRATIONAL:
      case TIFF_TYPE_RATIONAL:
        return 8;
      case TIFF_TYPE_DOUBLE:
        return 8;
      case TIFF_TYPE_UNDEFINED:
        return 1;
      case TIFF_TYPE_FLOAT:
        return 4;
    }
    return TIFF_TYPE_UNDEFINED;
  }
  
  
  /**
   * Converts a string field to a binary representation.
   *
   *
   * @param value
   *          The field value as a string. 
   * @param fieldType
   *          The field type value.
   * @param size
   * @return The binary representation of the data.
   */
  public static byte[] StringToField(String value, int fieldType, int endian)
  {
/*    switch (fieldType)
    {
      case TIFF_TYPE_ASCII:
        return value.getBytes("UTF-8");
      case TIFF_TYPE_BYTE:
        break;
      case TIFF_TYPE_SBYTE:
        break;
      case TIFF_TYPE_SHORT:
        break;
      case TIFF_TYPE_SSHORT:
        break;
      case TIFF_TYPE_SLONG:
        break;
      case TIFF_TYPE_LONG:
        break;
      case TIFF_TYPE_SRATIONAL:
        break;
      case TIFF_TYPE_RATIONAL:
        break;
      case TIFF_TYPE_DOUBLE:
        break;
      case TIFF_TYPE_FLOAT:
        break;
    }*/ /* end case */
    return null;
  }
  

  /**
   * Converts a data field to a string representation.
   * 
   * @param input
   *          The inputstream, it should point to the data.
   * @param fieldType
   *          The field type value.
   * @param fieldCount
   *          The field count value.
   * @param size
   *          The size in bytes of the data associated with this field.
   * @return The string representation of the data.
   */
  public static String fieldToString(DataInput input, int fieldType, int fieldCount, int size)
  {
    long numerator;
    long denominator;
    int count;
    StringBuffer buffer = new StringBuffer();

    /* Follow guidelines of the Metadataworking Group for reading ASCII fields. 
     * 
     * Check if all values are 0-127 then consider it as ASCII.
     * If not consider it an UTF-8 encoding
     * If illegal characters are found with both encodings, then replace with mapping
     * character.
     * 
     * */
    if (fieldType == TIFF_TYPE_ASCII)
    {
      byte[] byteBuffer = new byte[size]; 
      try
      {
        input.readFully(byteBuffer, 0, size);
        return new String(byteBuffer,"UTF-8");
      } catch (IOException e)
      {
        return null;
      }
    } else
    {
      for (count = 0; count < fieldCount; count++)
      {
        try
        {
          switch (fieldType)
          {
            case TIFF_TYPE_BYTE:
              buffer.append(Integer.toString(input.readUnsignedByte()));
              break;
            case TIFF_TYPE_SBYTE:
              buffer.append(Integer.toString(input.readByte()));
              break;
            case TIFF_TYPE_SHORT:
              buffer.append(Integer.toString(input.readUnsignedShort()));
              break;
            case TIFF_TYPE_SSHORT:
              buffer.append(Integer.toString(input.readShort()));
              break;
            case TIFF_TYPE_SLONG:
              buffer.append(Long.toString(input.readInt()));
              break;
            case TIFF_TYPE_LONG:
              buffer.append(Long.toString(input.readInt() & 0xFFFFFFFF));
              break;
            case TIFF_TYPE_SRATIONAL:
              numerator = input.readInt();
              denominator = input.readInt();
              buffer.append(Double.toString(numerator / denominator));
              break;
            case TIFF_TYPE_RATIONAL:
              numerator = input.readInt() & 0xFFFFFFFF;
              denominator = input.readInt() & 0xFFFFFFFF;
              buffer.append(Double.toString(numerator / denominator));
              break;
            case TIFF_TYPE_DOUBLE:
              buffer.append(Double.toString(input.readDouble()));
              break;
            case TIFF_TYPE_FLOAT:
              buffer.append(Float.toString(input.readFloat()));
              break;
          } /* end case */
          /* Add separator */
          if (count != fieldCount)
            buffer.append(";");
        } catch (IOException e)
        {
          throw new IllegalArgumentException("Illegal values.");
        }
      }
    }
    return buffer.toString();
  }
  
  
  /** Converts a field type to an XMLSchema representation.
   * 
   * @param type The TIFF field type (IFD Field type)
   * @return the Schema type or null if there is no datatype.
   */
  public static String fieldTypeToXMLSchema(int type)
  {
    switch (type) 
    {
        case TIFFUtilities.TIFF_TYPE_BYTE:
            return "xsd:unsignedByte";
        case TIFFUtilities.TIFF_TYPE_SBYTE:
          return "xsd:byte";
        case TIFFUtilities.TIFF_TYPE_ASCII:
            return "xsd:string";
        case TIFFUtilities.TIFF_TYPE_SHORT:
            return "xsd:unsignedShort";
        case TIFFUtilities.TIFF_TYPE_SSHORT:
          return "xsd:short";
        case TIFFUtilities.TIFF_TYPE_SLONG:
          return "xsd:int";
        case TIFFUtilities.TIFF_TYPE_LONG:
          return "xsd:unsignedInt";
        case TIFFUtilities.TIFF_TYPE_SRATIONAL:
        case TIFFUtilities.TIFF_TYPE_RATIONAL:
             return "xsd:decimal";
        case TIFFUtilities.TIFF_TYPE_DOUBLE:
             return "xsd:double";
        case TIFFUtilities.TIFF_TYPE_FLOAT:
          return "xsd:float";
    }
    return null;
  }
  
}
