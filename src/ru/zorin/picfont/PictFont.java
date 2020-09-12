package ru.zorin.picfont;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Reader;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;

public class PictFont
{
  public static final short SYM_UNKNOWN = 0;
  public static final short SYM_SPACE = 1;
  public static final short SYM_FIRST = 2;
  private int style;
  private int size;
  private int face;
  private int height;
  private int baseline;
  private int midline;
  private int leading;
  private int letterSpacing;
  private int wordSpacing;
  private int backStep;
  private Image image;
  private int imageWidth;
  private int imageHeight;
  private int lineNumber;
  private int lineHeight;
  private int[] lineStart;
  private int setNumber;
  private int setCurrent;
  private int symbolNumber;
  private int totalSymbolNumber;
  private int[] ranges = null;
  private int rangesLen;
  private short[] shifts = null;
  private short[] widths = null;
  private boolean unknownShown;
  private short unknownAsCode;
  private char unknownAsChar;
  private int mainColor;
  private boolean loaded;
  
  public PictFont()
  {
    initMembers();
  }
  
  private void initMembers()
  {
    this.style = 0;
    this.size = 0;
    this.face = 64;
    this.height = Integer.MIN_VALUE;
    this.baseline = Integer.MIN_VALUE;
    this.midline = Integer.MIN_VALUE;
    this.leading = Integer.MIN_VALUE;
    this.letterSpacing = 1;
    this.wordSpacing = 5;
    this.backStep = 0;
    
    this.image = null;
    this.imageWidth = Integer.MIN_VALUE;
    this.imageHeight = Integer.MIN_VALUE;
    this.lineNumber = 1;
    this.lineHeight = Integer.MIN_VALUE;
    this.lineStart = null;
    this.setNumber = 1;
    this.setCurrent = 0;
    
    this.symbolNumber = Integer.MIN_VALUE;
    this.totalSymbolNumber = Integer.MIN_VALUE;
    this.ranges = null;
    this.rangesLen = 0;
    this.shifts = null;
    this.widths = null;
    
    this.unknownShown = false;
    this.unknownAsCode = 1;
    this.unknownAsChar = ' ';
    
    this.mainColor = 0;
    
    this.loaded = false;
  }
  
  private static String rsListName = "pf_list";
  private static String rsInfoName = "pf_info";
  private static String rsImageName = "pf_image";
  
  public int loadFromResource(String info_name, String image_name)
  {
    int ret = 0;
    
    initMembers();
    
    Class theclass = getClass();
    InputStream is = theclass.getResourceAsStream(info_name);
    if (is != null)
    {
      ret = readInfo(is);
      try
      {
        is.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
    else
    {
      ret = -2;
    }
    if (ret == 0) {
      try
      {
        this.image = Image.createImage(image_name);
        this.imageWidth = this.image.getWidth();
        this.imageHeight = this.image.getHeight();
        this.loaded = true;
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
    return ret;
  }
  
  private int readInfo(InputStream is)
  {
    int ret = 0;
    try
    {
      Reader reader = new InputStreamReader(is, "UTF-8");
      Parser parser = new Parser(reader);
      
      int retval = 0;
      
      int majorVersion = Integer.MIN_VALUE;
      int minorVersion = Integer.MIN_VALUE;
      
      StringBuffer key = new StringBuffer(40);
      StringBuffer value = new StringBuffer(80);
      
      String rangesStr = null;
      while ((retval >= 0) && ((retval = parser.readPair(key, value)) >= -1))
      {
        String keyStr = key.toString().toLowerCase();
        String valueStr = value.toString();
        try
        {
          if (keyStr.equals("version_major"))
          {
            majorVersion = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("version_minor"))
          {
            minorVersion = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("style"))
          {
            this.style = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("size"))
          {
            this.size = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("face"))
          {
            this.face = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("height"))
          {
            this.height = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("baseline"))
          {
            this.baseline = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("midline"))
          {
            this.midline = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("leading"))
          {
            this.leading = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("letterspace"))
          {
            this.letterSpacing = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("wordspace"))
          {
            this.wordSpacing = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("backstep"))
          {
            this.backStep = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("lineheight"))
          {
            this.lineHeight = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("lines"))
          {
            this.lineNumber = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("sets"))
          {
            this.setNumber = Integer.parseInt(valueStr);
            continue;
          }
          if (keyStr.equals("maincolor"))
          {
            this.mainColor = Integer.parseInt(valueStr, 16);
            continue;
          }
        }
        catch (NumberFormatException ex) {}
        if (keyStr.equals("unknown_show"))
        {
          this.unknownShown = false;
          if (valueStr.equals("1")) {
            this.unknownShown = true;
          }
        }
        else if (keyStr.equals("unknown_as"))
        {
          Parser.trimQuote(value);
          this.unknownAsChar = value.charAt(0);
        }
        else if (keyStr.equals("ranges"))
        {
          Parser.trimQuote(value);
          
          this.rangesLen = value.length();
          if ((this.rangesLen & 0x1) != 0)
          {
            this.rangesLen -= 1;
            value.setLength(this.rangesLen);
          }
          this.ranges = new int[this.rangesLen];
          
          this.symbolNumber = 0;
          int i = 0;
          while (i < this.rangesLen)
          {
            this.ranges[i] = value.charAt(i);
            this.ranges[(i + 1)] = value.charAt(i + 1);
            if (this.ranges[i] > this.ranges[(i + 1)])
            {
              this.ranges[i] = value.charAt(i + 1);
              this.ranges[(i + 1)] = value.charAt(i);
            }
            this.symbolNumber += this.ranges[(i + 1)] - this.ranges[i] + 1;
            i += 2;
          }
          this.totalSymbolNumber = (this.symbolNumber + 2);
        }
        else
        {
          if (keyStr.equals("shifts_begin"))
          {
            if (this.symbolNumber < 1) {
              this.symbolNumber = 10;
            }
            StringBuffer[] buff = new StringBuffer[3];
            for (int j = 0; j < 3; j++) {
              buff[j] = new StringBuffer(16);
            }
            this.totalSymbolNumber = (this.symbolNumber + 2);
            
            this.shifts = new short[this.totalSymbolNumber];
            this.widths = new short[this.totalSymbolNumber];
            if (this.lineNumber <= 0) {
              this.lineNumber = 10;
            }
            this.lineStart = new int[this.lineNumber];
            int currentLine = 0;
            this.lineStart[(currentLine++)] = 0;
            
            int i = 0;
            while (i < 2)
            {
              this.shifts[i] = 0;
              this.widths[i] = 0;
              i++;
            }
            while ((retval >= 0) && ((retval = parser.readFields(buff)) >= -1))
            {
              valueStr = buff[0].toString();
              if (valueStr.equals("shifts_end")) {
                break;
              }
              if (i < this.totalSymbolNumber)
              {
                try
                {
                  this.shifts[i] = Short.parseShort(valueStr);
                }
                catch (NumberFormatException ex)
                {
                  this.shifts[i] = ((short)(this.shifts[(i - 1)] + 1));
                }
                if ((this.shifts[i] < this.shifts[(i - 1)]) && (currentLine < this.lineNumber))
                {
                  this.lineStart[(currentLine++)] = i;
                  this.widths[i] = this.shifts[i];
                }
                else
                {
                  this.widths[i] = ((short)(this.shifts[i] - this.shifts[(i - 1)]));
                }
                i++;
              }
            }
            while (i < this.totalSymbolNumber)
            {
              this.shifts[i] = this.shifts[(i - 1)];
              this.widths[i] = 0;
              i++;
            }
          }
          if (retval == -1) {
            break;
          }
        }
      }
    }
    catch (IOException ex)
    {
      ret = -2;
      ex.printStackTrace();
    }
    if ((this.height == Integer.MIN_VALUE) || (this.baseline == Integer.MIN_VALUE) || (this.midline == Integer.MIN_VALUE) || (this.leading == Integer.MIN_VALUE) || (this.lineHeight == Integer.MIN_VALUE) || (this.lineStart == null) || (this.symbolNumber == Integer.MIN_VALUE) || (this.totalSymbolNumber == Integer.MIN_VALUE) || (this.ranges == null) || (this.rangesLen == 0) || (this.shifts == null) || (this.widths == null))
    {
      System.err.println("Font loading error");
      ret = -3;
    }
    else
    {
      this.unknownAsCode = getCode(this.unknownAsChar);
      if (this.unknownAsCode == 0)
      {
        this.unknownAsChar = ' ';
        this.unknownAsCode = 1;
      }
      this.mainColor |= 0xFF000000;
    }
    return ret;
  }
  
  private int writeBinaryInfo(OutputStream os)
  {
    int ret = 0;
    
    DataOutputStream dos = null;
    
    dos = new DataOutputStream(os);
    try
    {
      dos.writeInt(2845);
      dos.writeShort(0);
      dos.writeShort(1);
      
      dos.writeInt(this.style);
      dos.writeInt(this.size);
      dos.writeInt(this.face);
      
      dos.writeInt(this.lineHeight);
      dos.writeInt(this.baseline);
      dos.writeInt(this.midline);
      dos.writeInt(this.leading);
      
      dos.writeInt(this.letterSpacing);
      dos.writeInt(this.wordSpacing);
      dos.writeInt(this.backStep);
      dos.writeInt(this.lineNumber);
      dos.writeInt(this.setNumber);
      dos.writeInt(this.mainColor);
      
      dos.writeBoolean(this.unknownShown);
      dos.writeChar(this.unknownAsChar);
      
      StringBuffer rangesString = new StringBuffer();
      for (int i = 0; i < this.rangesLen; i++) {
        rangesString.append((char)this.ranges[i]);
      }
      dos.writeUTF(rangesString.toString());
      
      dos.writeInt(this.symbolNumber);
      for (int i = 2; i < this.totalSymbolNumber; i++) {
        dos.writeShort(this.shifts[i]);
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      ret = -1;
    }
    return ret;
  }
  
  public int saveToStore(String name)
  {
    int ret = 0;
    if (!this.loaded) {
      return -1;
    }
    if (this.image == null) {
      return -1;
    }
    try
    {
      RecordStore rs = null;
      int infoID = 0;
      int imageID = 0;
      ByteArrayOutputStream baos = null;
      DataOutputStream dos = null;
      byte[] b = null;
      int maxBuffSize = 16384;
      int maxImageSize = 4096;
      
      System.gc();
      try
      {
        rs = RecordStore.openRecordStore(rsInfoName, true);
        baos = new ByteArrayOutputStream();
        if (writeBinaryInfo(baos) == 0)
        {
          b = baos.toByteArray();
          infoID = rs.addRecord(b, 0, b.length);
        }
        else
        {
          ret = -1;
        }
        baos.close();
      }
      catch (RecordStoreFullException ex)
      {
        ret = -2;
        ex.printStackTrace();
      }
      catch (RecordStoreException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      catch (IOException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      if (rs != null) {
        try
        {
          rs.closeRecordStore();
          rs = null;
        }
        catch (RecordStoreNotOpenException ex)
        {
          ex.printStackTrace();
        }
        catch (RecordStoreException ex)
        {
          ret = -1;
          ex.printStackTrace();
        }
      }
      b = null;
      baos = null;
      dos = null;
      System.gc();
      if (ret == 0)
      {
        try
        {
          rs = RecordStore.openRecordStore(rsImageName, true);
          
          baos = new ByteArrayOutputStream(16 + this.imageWidth * this.imageHeight * 4);
          dos = new DataOutputStream(baos);
          
          dos.writeInt(this.imageWidth);
          dos.writeInt(this.imageHeight);
          int[] argb = new int[this.imageWidth * this.imageHeight];
          
          this.image.getRGB(argb, 0, this.imageWidth, 0, 0, this.imageWidth, this.imageHeight);
          for (int i = 0; i < argb.length; i++) {
            dos.writeInt(argb[i]);
          }
          b = baos.toByteArray();
          imageID = rs.addRecord(b, 0, b.length);
          
          baos.close();
        }
        catch (RecordStoreFullException ex)
        {
          ret = -2;
          ex.printStackTrace();
        }
        catch (RecordStoreException ex)
        {
          ret = -1;
          ex.printStackTrace();
        }
        catch (IOException ex)
        {
          ret = -1;
          ex.printStackTrace();
        }
        if (rs != null) {
          try
          {
            rs.closeRecordStore();
            rs = null;
          }
          catch (RecordStoreNotOpenException ex)
          {
            ex.printStackTrace();
          }
          catch (RecordStoreException ex)
          {
            ret = -1;
            ex.printStackTrace();
          }
        }
        b = null;
        baos = null;
        dos = null;
        System.gc();
      }
      if (ret == 0)
      {
        try
        {
          rs = RecordStore.openRecordStore(rsListName, true);
          baos = new ByteArrayOutputStream();
          dos = new DataOutputStream(baos);
          
          dos.writeUTF(name);
          dos.writeInt(infoID);
          dos.writeInt(imageID);
          
          b = baos.toByteArray();
          rs.addRecord(b, 0, b.length);
          
          baos.close();
        }
        catch (RecordStoreFullException ex)
        {
          ret = -2;
          ex.printStackTrace();
        }
        catch (RecordStoreException ex)
        {
          ret = -1;
          ex.printStackTrace();
        }
        catch (IOException ex)
        {
          ret = -1;
          ex.printStackTrace();
        }
        if (rs != null) {
          try
          {
            rs.closeRecordStore();
            rs = null;
          }
          catch (RecordStoreNotOpenException ex)
          {
            ex.printStackTrace();
          }
          catch (RecordStoreException ex)
          {
            ret = -1;
            ex.printStackTrace();
          }
        }
        b = null;
        baos = null;
        dos = null;
        System.gc();
      }
    }
    catch (OutOfMemoryError err)
    {
      ret = -3;
      err.printStackTrace();
    }
    return ret;
  }
  
  public class RSFilter
    implements RecordFilter
  {
    private byte[] bytePattern = null;
    
    public RSFilter() {}
    
    public RSFilter(String pattern)
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      try
      {
        dos.writeUTF(pattern);
        this.bytePattern = baos.toByteArray();
        baos.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
    
    public boolean matches(byte[] candidate)
    {
      if (this.bytePattern == null) {
        return false;
      }
      try
      {
        for (int i = 0; i < this.bytePattern.length; i++) {
          if (this.bytePattern[i] != candidate[i]) {
            return false;
          }
        }
      }
      catch (IndexOutOfBoundsException ex)
      {
        return false;
      }
      return true;
    }
  }
  
  public int loadFromStore(String name)
  {
    int ret = 0;
    RecordStore rs = null;
    byte[] b = null;
    ByteArrayInputStream bais = null;
    DataInputStream dis = null;
    int infoID = 0;
    int imageID = 0;
    
    initMembers();
    
    System.gc();
    if (ret == 0)
    {
      try
      {
        rs = RecordStore.openRecordStore(rsListName, false);
        RecordEnumeration renum = rs.enumerateRecords(new RSFilter(name), null, false);
        try
        {
          b = renum.nextRecord();
          bais = new ByteArrayInputStream(b);
          dis = new DataInputStream(bais);
          
          String storedName = dis.readUTF();
          infoID = dis.readInt();
          imageID = dis.readInt();
          
          bais.close();
        }
        catch (InvalidRecordIDException ex) {}catch (IOException ex)
        {
          ret = -1;
          ex.printStackTrace();
        }
        if ((infoID == 0) || (imageID == 0)) {
          ret = -1;
        }
      }
      catch (RecordStoreException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      try
      {
        if (rs != null)
        {
          rs.closeRecordStore();
          rs = null;
        }
      }
      catch (RecordStoreException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
    }
    b = null;
    bais = null;
    dis = null;
    System.gc();
    if (ret == 0)
    {
      try
      {
        rs = RecordStore.openRecordStore(rsInfoName, false);
        b = rs.getRecord(infoID);
        bais = new ByteArrayInputStream(b);
        if (readBinaryInfo(bais) != 0) {
          ret = -1;
        }
        bais.close();
      }
      catch (RecordStoreException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
      try
      {
        if (rs != null)
        {
          rs.closeRecordStore();
          rs = null;
        }
      }
      catch (RecordStoreException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      b = null;
      bais = null;
      dis = null;
      System.gc();
    }
    if (ret == 0)
    {
      try
      {
        rs = RecordStore.openRecordStore(rsImageName, false);
        b = rs.getRecord(imageID);
        bais = new ByteArrayInputStream(b);
        dis = new DataInputStream(bais);
        
        this.imageWidth = dis.readInt();
        this.imageHeight = dis.readInt();
        
        int[] argb = new int[this.imageWidth * this.imageHeight];
        for (int i = 0; i < argb.length; i++) {
          argb[i] = dis.readInt();
        }
        this.image = Image.createRGBImage(argb, this.imageWidth, this.imageHeight, true);
        bais.close();
      }
      catch (RecordStoreException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      catch (IOException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      try
      {
        if (rs != null)
        {
          rs.closeRecordStore();
          rs = null;
        }
      }
      catch (RecordStoreException ex)
      {
        ret = -1;
        ex.printStackTrace();
      }
      b = null;
      bais = null;
      dis = null;
      System.gc();
    }
    if (ret == 0) {
      this.loaded = true;
    }
    return ret;
  }
  
  public int loadFromBinaryResource(String info_name, String image_name)
  {
    int ret = 0;
    
    initMembers();
    
    Class theclass = getClass();
    InputStream is = theclass.getResourceAsStream(info_name);
    if (is != null)
    {
      ret = readBinaryInfo(is);
      try
      {
        is.close();
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
    else
    {
      ret = -2;
    }
    if (ret == 0) {
      try
      {
        this.image = Image.createImage(image_name);
        this.imageWidth = this.image.getWidth();
        this.imageHeight = this.image.getHeight();
        this.loaded = true;
      }
      catch (IOException ex)
      {
        ex.printStackTrace();
      }
    }
    return ret;
  }
  
  private int readBinaryInfo(InputStream is)
  {
    int ret = 0;
    DataInputStream dis = null;
    try
    {
      dis = new DataInputStream(is);
      
      int magicWord = dis.readInt();
      short majorVersion = dis.readShort();
      short minorVersion = dis.readShort();
      if ((magicWord != 2845) || (majorVersion != 0) || (minorVersion != 1))
      {
        ret = -1;
      }
      else
      {
        this.style = dis.readInt();
        this.size = dis.readInt();
        this.face = dis.readInt();
        
        this.lineHeight = dis.readInt();
        this.baseline = dis.readInt();
        this.midline = dis.readInt();
        this.leading = dis.readInt();
        
        this.height = (this.lineHeight + this.leading);
        
        this.letterSpacing = dis.readInt();
        this.wordSpacing = dis.readInt();
        this.backStep = dis.readInt();
        this.lineNumber = dis.readInt();
        this.setNumber = dis.readInt();
        this.mainColor = dis.readInt();
        this.unknownShown = dis.readBoolean();
        this.unknownAsChar = dis.readChar();
        
        StringBuffer rangesString = new StringBuffer(dis.readUTF());
        
        this.rangesLen = rangesString.length();
        if ((this.rangesLen & 0x1) != 0)
        {
          this.rangesLen -= 1;
          rangesString.setLength(this.rangesLen);
        }
        this.ranges = new int[this.rangesLen];
        
        this.symbolNumber = 0;
        int i = 0;
        while (i < this.rangesLen)
        {
          this.ranges[i] = rangesString.charAt(i);
          this.ranges[(i + 1)] = rangesString.charAt(i + 1);
          if (this.ranges[i] > this.ranges[(i + 1)])
          {
            this.ranges[i] = rangesString.charAt(i + 1);
            this.ranges[(i + 1)] = rangesString.charAt(i);
          }
          this.symbolNumber += this.ranges[(i + 1)] - this.ranges[i] + 1;
          i += 2;
        }
        this.totalSymbolNumber = (this.symbolNumber + 2);
        
        int shiftsNumber = dis.readInt();
        if (shiftsNumber != this.symbolNumber)
        {
          ret = -1;
        }
        else
        {
          this.shifts = new short[this.totalSymbolNumber];
          this.widths = new short[this.totalSymbolNumber];
          for (i = 0; i < 2; i++)
          {
            this.shifts[i] = 0;
            this.widths[i] = 0;
          }
          if (this.lineNumber <= 0) {
            this.lineNumber = 10;
          }
          this.lineStart = new int[this.lineNumber];
          
          int currentLine = 0;
          this.lineStart[(currentLine++)] = 0;
          for (i = 2; i < this.totalSymbolNumber; i++)
          {
            this.shifts[i] = dis.readShort();
            this.widths[i] = ((short)(this.shifts[i] - this.shifts[(i - 1)]));
            if (this.widths[i] < 0)
            {
              this.lineStart[(currentLine++)] = i;
              this.widths[i] = this.shifts[i];
            }
          }
        }
      }
    }
    catch (EOFException ex)
    {
      ret = -2;
      ex.printStackTrace();
    }
    catch (IOException ex)
    {
      ret = -2;
      ex.printStackTrace();
    }
    if ((this.height == Integer.MIN_VALUE) || (this.baseline == Integer.MIN_VALUE) || (this.midline == Integer.MIN_VALUE) || (this.leading == Integer.MIN_VALUE) || (this.lineHeight == Integer.MIN_VALUE) || (this.lineStart == null) || (this.symbolNumber == Integer.MIN_VALUE) || (this.totalSymbolNumber == Integer.MIN_VALUE) || (this.ranges == null) || (this.rangesLen == 0) || (this.shifts == null) || (this.widths == null))
    {
      System.err.println("Font loading error");
      ret = -3;
    }
    else
    {
      this.unknownAsCode = getCode(this.unknownAsChar);
      if (this.unknownAsCode == 0)
      {
        this.unknownAsChar = ' ';
        this.unknownAsCode = 1;
      }
      this.mainColor |= 0xFF000000;
    }
    return ret;
  }
  
  public int getStyle()
  {
    return this.style;
  }
  
  public int getSize()
  {
    return this.size;
  }
  
  public int getFace()
  {
    return this.face;
  }
  
  public boolean isPlain()
  {
    return this.style == 0;
  }
  
  public boolean isBold()
  {
    return (this.style & 0x1) != 0;
  }
  
  public boolean isItalic()
  {
    return (this.style & 0x2) != 0;
  }
  
  public boolean isUnderlined()
  {
    return (this.style & 0x4) != 0;
  }
  
  public int getHeight()
  {
    return this.height;
  }
  
  public int getBaselinePosition()
  {
    return this.baseline;
  }
  
  public int getMidline()
  {
    return this.midline;
  }
  
  public int getLeading()
  {
    return this.leading;
  }
  
  public int getLetterSpacing()
  {
    return this.letterSpacing;
  }
  
  public int recolor(int color)
  {
    int ret = 0;
    color |= 0xFF000000;
    try
    {
      int[] argb = new int[this.imageWidth * this.imageHeight];
      this.image.getRGB(argb, 0, this.imageWidth, 0, 0, this.imageWidth, this.imageHeight);
      for (int i = argb.length - 1; i >= 0; i--) {
        if (argb[i] == this.mainColor) {
          argb[i] = color;
        }
      }
      this.image = Image.createRGBImage(argb, this.imageWidth, this.imageHeight, true);
      this.mainColor = color;
      System.gc();
    }
    catch (OutOfMemoryError err)
    {
      ret = -3;
      err.printStackTrace();
    }
    return ret;
  }
  
  public int charWidth(char ch)
  {
    short code = getCode(ch);
    
    return getWidth(code) + this.letterSpacing;
  }
  
  public int charsWidth(char[] ch, int offset, int length)
  {
    short[] codes = new short[length];
    for (int i = 0; i < length; i++) {
      codes[i] = getCode(ch[(offset + i)]);
    }
    return getWidth(codes) + this.letterSpacing;
  }
  
  public int stringWidth(String str)
  {
    short[] codes = getCodes(str);
    
    return getWidth(codes) + this.letterSpacing;
  }
  
  public int substringWidth(String str, int offset, int len)
  {
    return stringWidth(str.substring(offset, offset + len));
  }
  
  public void drawChar(Graphics g, char character, int x, int y, int anchor)
  {
    short code = getCode(character);
    drawCode(g, code, x, y, anchor);
  }
  
  public void drawChars(Graphics g, char[] data, int offset, int length, int x, int y, int anchor)
  {
    if (data == null) {
      return;
    }
    short[] codes = new short[length];
    for (int i = 0; i < length; i++) {
      codes[i] = getCode(data[(offset + i)]);
    }
    drawCodes(g, codes, x, y, anchor);
  }
  
  public void drawString(Graphics g, String str, int x, int y, int anchor)
  {
    short[] codes = getCodes(str);
    drawCodes(g, codes, x, y, anchor);
  }
  
  public void drawSubstring(Graphics g, String str, int offset, int len, int x, int y, int anchor)
  {
    String sub = str.substring(offset, offset + len);
    drawString(g, sub, x, y, anchor);
  }
  
  public int getSet()
  {
    return this.setCurrent;
  }
  
  public int setSet(int set)
  {
    int oldSet = this.setCurrent;
    this.setCurrent = (set % this.setNumber);
    
    return oldSet;
  }
  
  public short getCode(char ch)
  {
    short ret;
    if (ch == ' ')
    {
      ret = 1;
    }
    else
    {
      int ich = ch;
      
      boolean found = false;
      
      ret = 2;
      for (int i = 0; i < this.rangesLen; i += 2)
      {
        if ((this.ranges[i] <= ich) && (ich <= this.ranges[(i + 1)]))
        {
          ret = (short)(ret + (ich - this.ranges[i]));
          found = true;
          
          break;
        }
        ret = (short)(ret + (this.ranges[(i + 1)] - this.ranges[i] + 1));
      }
      if (!found) {
        ret = 0;
      }
    }
    return ret;
  }
  
  public short[] getCodes(String str)
  {
    if (str == null) {
      return null;
    }
    int len = str.length();
    
    short[] codes = new short[len];
    
    char[] chars = new char[len];
    
    str.getChars(0, len, chars, 0);
    for (int i = 0; i < len; i++) {
      codes[i] = getCode(chars[i]);
    }
    return codes;
  }
  
  public int getWidth(short code)
  {
    int width = 0;
    if ((code < 0) || (code >= this.totalSymbolNumber)) {
      code = 0;
    }
    if ((code == 0) && (this.unknownShown)) {
      code = this.unknownAsCode;
    }
    switch (code)
    {
    case 0: 
      break;
    case 1: 
      width = this.wordSpacing;
      
      break;
    default: 
      width = this.widths[code];
    }
    return width;
  }
  
  public int getWidth(short[] codes)
  {
    int width = 0;
    
    int cw = 0;
    if (codes == null) {
      return 0;
    }
    int len = codes.length;
    
    boolean wasSpace = true;
    for (int i = 0; i < len; i++)
    {
      short code = codes[i];
      cw = getWidth(code);
      if (cw > 0) {
        if (code == 1)
        {
          width += this.wordSpacing;
          if (!wasSpace) {
            width -= this.backStep;
          }
          wasSpace = true;
        }
        else
        {
          width += cw;
          if (!wasSpace) {
            width += this.letterSpacing - this.backStep;
          }
          wasSpace = false;
        }
      }
    }
    return width;
  }
  
  public int drawCode(Graphics g, short code, int x, int y)
  {
    if (code == 1) {
      return this.wordSpacing;
    }
    if ((code < 0) || (code >= this.totalSymbolNumber)) {
      code = 0;
    }
    if (code == 0) {
      if (this.unknownShown) {
        code = this.unknownAsCode;
      } else {
        return 0;
      }
    }
    int width = getWidth(code);
    if (width > 0)
    {
      int clipX = g.getClipX();
      
      int clipY = g.getClipY();
      
      int clipWidth = g.getClipWidth();
      
      int clipHeight = g.getClipHeight();
      
      int line = 0;
      
      int nextline = 1;
      while ((nextline < this.lineNumber) && (code >= this.lineStart[nextline])) {
        nextline++;
      }
      line = nextline - 1;
      if ((x > clipX + clipWidth) || (x + width < clipX) || (y > clipY + clipHeight) || (y + this.lineHeight < clipY)) {
        return width;
      }
      int shiftX = 0;
      
      int shiftY = this.lineHeight * (this.setCurrent * this.lineNumber + line);
      if (code > this.lineStart[line]) {
        shiftX = this.shifts[(code - 1)];
      }
      int newClipX = x;
      
      int newClipY = y;
      
      int newClipWidth = width;
      
      int newClipHeight = this.lineHeight;
      if (newClipX < clipX)
      {
        newClipWidth -= clipX - newClipX;
        newClipX = clipX;
      }
      if (newClipX + newClipWidth > clipX + clipWidth) {
        newClipWidth = clipX + clipWidth - newClipX;
      }
      if (newClipY < clipY)
      {
        newClipHeight -= clipY - newClipY;
        newClipY = clipY;
      }
      if (newClipY + newClipHeight > clipY + clipHeight) {
        newClipHeight = clipY + clipHeight - newClipY;
      }
      g.setClip(newClipX, newClipY, newClipWidth, newClipHeight);
      g.drawImage(this.image, x - shiftX, y - shiftY, 20);
      g.setClip(clipX, clipY, clipWidth, clipHeight);
    }
    return width;
  }
  
  public int drawCode(Graphics g, short code, int x, int y, int anchor)
  {
    int x1 = x;
    
    int y1 = y;
    
    int width = getWidth(code);
    if ((anchor & 0x1) != 0) {
      x1 -= (width >> 1);
    } else if ((anchor & 0x8) != 0) {
      x1 -= width;
    }
    if ((anchor & 0x2) != 0) {
      y1 -= (this.height >> 1);
    } else if ((anchor & 0x40) != 0) {
      y1 -= this.baseline;
    } else if ((anchor & 0x20) != 0) {
      y1 -= this.height;
    }
    return drawCode(g, code, x1, y1);
  }
  
  public int drawCodes(Graphics g, short[] codes, int x, int y)
  {
    if (codes == null) {
      return 0;
    }
    int width = 0;
    
    int cw = 0;
    
    int len = codes.length;
    
    boolean wasSpace = true;
    for (int i = 0; i < len; i++)
    {
      short code = codes[i];
      if (code == 1)
      {
        width += this.wordSpacing;
        if (!wasSpace) {
          width -= this.backStep;
        }
        wasSpace = true;
      }
      else
      {
        if (wasSpace)
        {
          cw = drawCode(g, code, x + width, y);
          width += cw;
        }
        else
        {
          width += this.letterSpacing - this.backStep;
          cw = drawCode(g, code, x + width, y);
          width += cw;
        }
        if (cw > 0) {
          wasSpace = false;
        }
      }
    }
    return width;
  }
  
  public int drawCodes(Graphics g, short[] codes, int x, int y, int anchor)
  {
    int x1 = x;
    
    int y1 = y;
    
    int width = getWidth(codes);
    if ((anchor & 0x1) != 0) {
      x1 -= (width >> 1);
    } else if ((anchor & 0x8) != 0) {
      x1 -= width;
    }
    if ((anchor & 0x2) != 0) {
      y1 -= (this.height >> 1);
    } else if ((anchor & 0x40) != 0) {
      y1 -= this.baseline;
    } else if ((anchor & 0x20) != 0) {
      y1 -= this.height;
    }
    return drawCodes(g, codes, x1, y1);
  }
}
