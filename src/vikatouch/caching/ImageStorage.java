 package vikatouch.caching;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

import vikatouch.VikaTouch;
import vikatouch.settings.Settings;

public class ImageStorage
{
    //private static final int MAX_AREA_OF_IMAGE = 640 * 480;

    
  //name of storage in RMS
    //private static final String RMS_IMAGES = "vikaImagesCache";
    //HashMap<String, Image>
    private static Hashtable images;
 
    /**
     * This color will be used to indicate transparent color, 
     * you can change it, but be sure that
     * your color is processed correctly by application.
     * It is setted only once (you can make it final)
     */
   // protected static int COLOR_TO_BE_TRANSPARENT = 0xFFFFFF;
 
   // private static boolean isLoaded = false; //flag indicates that images have been loaded from RMS


	//private static int lastSize;


	//private static RecordStore recordStore;
 
    
    public static void init()
    {
    	images = new Hashtable();
    	//try
    	//{
		//	loadImages();
		//}
    	//catch (Exception e)
    	//{
			
		//}
    }
 
    
    public static Image get(String s)
    {
    	Image i = null;
		try
		{
			if(Runtime.getRuntime().freeMemory() < Settings.memoryClearCache)
			{
				images.clear();
				VikaTouch.inst.freeMemoryLow();
			}
	    	i = (Image) images.get(s);
			if(images.containsKey(s) && i != null)
			{
	    		return i;
			}
			/*
	    	if(!images.contains(s) || !isLoaded)
	    	{
				//loadImages();
	        	if(images.contains(s))
	        	{
	        		return i = (Image) images.get(s);
	        	}
	    	}
	    	else
	    	{
	    		return i;
	    	}
	    	*/
		} 
	    catch (Exception e)
		{
	    	e.printStackTrace();
		}
		return i;
    }
    
    public static void save(String s, Image i)
    {
		images.put(s, i);
		//if(images.size() > lastSize)
		//	storeImagesInRMS();
		//lastSize = images.size();
    }
    
    public static boolean has(String s)
    {
    	return images.containsKey(s);
    }
    /**
     * Gets table of colors of image curImage into int array rgbInts
     *
     * @param rgbInts  - result array with table of colors of image
     * @param curImage - image object from which we take table of colors
     * @param w        - width of taken image (we can take not whole region of image)
     * @param h        - height of taken image (we can take not whole region of image)
     * @param s        - area of image (should be w*h)
     */
    /*
    protected static void getRGB(int[] rgbInts, Image curImage, int w, int h, int s)
    {
        curImage.getRGB(rgbInts, 0, w, 0, 0, w, h);
        for (int i = 0; i < s; i++)
            if ((rgbInts[i] & 0x00FFFFFF) == COLOR_TO_BE_TRANSPARENT)
                rgbInts[i] = (rgbInts[i] & 0x00FFFFFF);
    }
    */
 
    /**
     * Checks if we need to update cash
     * You can use data that is stored in RMS to find is it neccessary to update
     *
     * @param recordStore - RMS store with additional data
     * @return false if data in RMS is up-to-date
     *         otherwise return true
     * @throws RecordStoreException
     * @throws IOException
     */
 
    /**
     * Restores array of images from RMS to array of cashed images
     *
     * @param images2 - array of images to be restored
     */
    protected static void restoreImagesFromRMS()
    {
    	/*
        int[] intArrayOfRGBforImage = null;
        int w = 0;//width of image
        int h = 0;//height of image
        int l = 0;//area of image
        String s = null;
 
        try
        {
            recordStore = RecordStore.openRecordStore(RMS_IMAGES, true);
            RecordEnumeration re = recordStore.enumerateRecords(null, null, true);
 
            // Here you can place code for taking additional info for re-cashing into RMS (you should simply skip it)
             * because it is already processed) 
            //...
 
            try
            {
                while (re.hasNextElement())
                {
                    int id = re.nextRecordId();
                    ByteArrayInputStream bais = new ByteArrayInputStream(recordStore.getRecord(id));
                    DataInputStream inputStream = new DataInputStream(bais);
 
                    try
                    {
                        s = inputStream.readUTF();
                        l = inputStream.readInt();
                        w = inputStream.readInt();
                        h = inputStream.readInt();
                        intArrayOfRGBforImage = new int[l];
                        for (int j = 0; j < l; j++)
                            intArrayOfRGBforImage[j] = inputStream.readInt();
                    }
                    catch (EOFException ioe)
                    {
                        ioe.printStackTrace();
                    }
 
                    images.put(s, Image.createRGBImage(intArrayOfRGBforImage, w, h, true));
                    System.gc();
                }
            }
            catch (IOException ioe)
            {
                ioe.printStackTrace();
            }
            recordStore.closeRecordStore();
        } 
        catch (Exception rse)
        {
            rse.printStackTrace();
        }
        */
    }
 
    /**
     * Stores array of images in RMS from array of cashed images
     *
     * @param images2 - array of images to be stored
     */
    public static void storeImagesInRMS()
    {
    	/*
        int w, h, l;
        int[] rgbImage = new int[MAX_AREA_OF_IMAGE];
        try
        {
        	//загрузить все перед очищением
            try
            {
            	restoreImagesFromRMS();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            try
            {
            	// clear record store
                RecordStore.deleteRecordStore(RMS_IMAGES);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            RecordStore recordStore = RecordStore.openRecordStore(RMS_IMAGES, true);
 
            //save additional info for checking necessity of re-cashing into RMS
            //...
            Image curImage;
            Enumeration e = images.keys();
            String x = null;
            for (curImage = null; e.hasMoreElements(); curImage = (Image) images.get(x = (String) e.nextElement()))
            {
            	if(curImage != null)
	            {
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                DataOutputStream outputStream = new DataOutputStream(baos);
	 
	                w = curImage.getWidth();
	                h = curImage.getHeight();
	                l = w * h;
	                if (l > MAX_AREA_OF_IMAGE)
	                    rgbImage = new int[l];
	 
	                getRGB(rgbImage, curImage, w, h, l);
	                try {
	                    outputStream.writeUTF(x);
	                    outputStream.writeInt(l);
	                    outputStream.writeInt(w);
	                    outputStream.writeInt(h);
	                    for (int j = 0; j < l; j++)
	                        outputStream.writeInt(rgbImage[j]);
	                    System.gc();
	                }
	                catch (IOException ioe)
	                {
	                    ioe.printStackTrace();
	                }
	 
	                byte[] b = baos.toByteArray();
	                int id = recordStore.addRecord(b, 0, b.length);
            	}
            }
            recordStore.closeRecordStore();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        */
    }
 
    /**
     * Checks if data in RMS (that contains cashed images) is up-to-date and if it is then
     * loads cashed images from RMS. If it is not, then the application cashes images by using
     * proper method drawImage, saves them into RMS, and then loads cashed images from RMS.
     *
     * @throws Exception
     */
    /*
    public static void loadImages()
    		throws Exception
    {
    	//RecordStore recordStore;
    	//try 
    	//{
    		//recordStore = RecordStore.openRecordStore(RMS_IMAGES, true);
    		//if (recordStore.getNumRecords() == 0 && images.size() > 0)
            //{
            //    storeImagesInRMS();
            //}
          //  restoreImagesFromRMS();
          //  isLoaded = true;
            //recordStore.closeRecordStore();
    	//} 
    	//catch (RecordStoreNotFoundException e)
    	//{ 
    	//	e.printStackTrace();
    	//}
        
    }
    */
 /*
    public static void drawImage(Graphics g, String s, int x, int y, int anchor)
    {
        if (ImageStorage.isLoaded)
        {
            try
        	{
                g.drawImage((Image) images.get(s), x, y, anchor);
            }
        	catch (Exception e)
        	{
                isLoaded = false;
                e.printStackTrace();
            }
        }
    }
    */
}