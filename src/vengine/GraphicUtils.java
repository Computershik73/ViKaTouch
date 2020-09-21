package vengine;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
/*import javax.microedition.m3g.Appearance;
import javax.microedition.m3g.Camera;
import javax.microedition.m3g.CompositingMode;
import javax.microedition.m3g.Image2D;
import javax.microedition.m3g.Material;
import javax.microedition.m3g.Texture2D;
import javax.microedition.m3g.Transform;
import javax.microedition.m3g.TriangleStripArray;
import javax.microedition.m3g.VertexArray;
import javax.microedition.m3g.VertexBuffer;*/


// НАСТОЯТЕЛЬНО рекомендую тут ничего не трогать второй раз, 
// первый уже показал что оно работает как надо только в текущем виде.
public class GraphicUtils {
	protected static int[] lastRGB;
	
	public static Image lastPO2;
	
	public static void fillTransparentRect(Graphics G, int x, int y, int w, int h, int r, int g, int b, int a)
	{
		int c = (a << 24) | (r << 16) | (g << 8) | b;
		int l = w*h+1;
		int[] m;
		if(lastRGB != null && l>1 && lastRGB.length == l && lastRGB[0] == c)
		{
			m = lastRGB;
		}
		else
		{
			m = new int[l];
			for(int i=0; i<l; i++)
			{
				m[i] = c;
			}
		}
		G.drawRGB(m, 0, w, x, y, w, h, true);
	}
	
	public static void darkScreen(Graphics G, int w, int h, int r, int g, int b, int a)
	{
		int c = (a << 24) | (r << 16) | (g << 8) | b;
		int l = w*30+1;
		int[] m;
		if(lastRGB != null && l>1 && lastRGB.length == l && lastRGB[0] == c)
		{
			m = lastRGB;
		}
		else
		{
			m = new int[l];
			for(int i=0; i<l; i++)
			{
				m[i] = c;
			}
		}
		for(int i=0; i<=h; i+=30)
		{
			G.drawRGB(m, 0, w, 0, i, w, h, true);
		}
	}
	
	public static Image roundImage(Image img)
	{
		try
		{
			int w = img.getWidth();
			int h = img.getHeight();
			int[] c = new int[w*h];
			img.getRGB(c, 0, w, 0, 0, w, h);
			for(int i = 0; i < h; i++)
			{
				/*try
				{*/
					float y = (float) (h/2-i) / (h-1);
					y = y * 2;
					float xf = (float) Math.sqrt(1 - y*y);
					int x = (int) (xf * (w-1));
					x = (w - x) / 2;
					for(int j=0; j < x; j++)
					{
						c[i*w + j] = 0x00FFFFFF;
						c[i*w + w - j - 1] = 0x00FFFFFF;
					}
				/*}
				catch(Exception e)
				{
					e.printStackTrace();
				}*/
			}
			return Image.createRGBImage(c, w, h, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return img; // ну рухнуло и рухнуло
		}
	}
	
	public static Image trim(Image img, int r, int g, int b)
	{
		try
		{
			int w = img.getWidth();
			int h = img.getHeight();
			int[] c = new int[w*h];
			img.getRGB(c, 0, w, 0, 0, w, h);
			int tc = (0x00 << 24) | (r << 16) | (g << 8) | b;
			for(int i = 0; i < h; i++)
			{
				for(int j = 0; j < w; j++)
				{
					int cc = (c[i*h+j] & 0x00ffffff);
					int m = tc ^ cc;
					boolean clear = (m == 0);
					if(clear)
					{
						c[i*h+j] = cc;
					}
				}
			}
			return Image.createRGBImage(c, w, h, true);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return img; // ну рухнуло и рухнуло
		}
	}
	
	public static ImgToPowerOf2Res powerOf2(Image img)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		int ms = Math.max(w, h);
		int ns;
		for(ns = 2; ns<=32768; ns = ns * 2)
		{
			if(h==w && ns==h) 
			{
				ImgToPowerOf2Res r = new ImgToPowerOf2Res();
				r.orig = r.res = img;
				r.texH = r.texW = 1f;
				return r;
			}
			if(ns>=ms) break;
		}
		int[] c = new int[ns*ns];
		img.getRGB(c, 0, ns, 0, 0, w, h);
		ImgToPowerOf2Res r = new ImgToPowerOf2Res();
		r.orig = img;
		r.res = lastPO2 = Image.createRGBImage(c, ns, ns, true);
		r.texW = (float) w / (float) ns;
		r.texH = (float) h / (float) ns;
		return r;
	}
	
	public static class ImgToPowerOf2Res
	{
		public Image orig;
		public Image res;
		public float texW;
		public float texH;
	}
	
	public static void DrawShadowText(Graphics G, String text, int x, int y, int a, int color1, int color2, int dx, int dy)
	{
		G.setColor(color2);
		G.drawString(text, x+dx, y+dy, a);
		G.setColor(color1);
		G.drawString(text, x, y, a);
	}
	
	public static void DrawBorderedText(Graphics G, String text, int x, int y, int a, int color1, int color2)
	{
		G.setColor(color2);
		G.drawString(text, x+1, y+1, a);
		G.drawString(text, x+1, y-1, a);
		G.drawString(text, x-1, y+1, a);
		G.drawString(text, x-1, y-1, a);
		G.setColor(color1);
		G.drawString(text, x, y, a);
	}
	
	// 3D
	/*public static Camera createCamera()
	{
		Camera c = new Camera();
		float ar = (float)Math.max(VEngine.w, VEngine.h) / Math.min(VEngine.w, VEngine.h);
		c.setParallel(360, ar, 0.1f, 1000f);
		return c;
	}
	
	public static Transform createCameraT()
	{
		Transform camTr = new Transform();
		camTr.postTranslate(0, 360/2, 100);
		return camTr;
	}
	
	public static VertexBuffer createQuad(int w, int h, int x, int y)
	{
		short[] vert = { 
				(short) (w-x), (short) (y), 0, 
				(short) (-x), (short) (y), 0,
				(short) (w-x),(short) (y-h), 0,
				(short) (-x), (short) (y-h), 0
		};
		VertexArray vertArray = new VertexArray(vert.length / 3, 3, 2);
		vertArray.set(0, vert.length/3, vert);
		
		byte[] norm = { 0, 0, 127, 0, 0, 127, 0, 0, 127, 0, 0, 127 };
		VertexArray normArray = new VertexArray(norm.length / 3, 3, 1);
		normArray.set(0, norm.length/3, norm);
		
		short tx = 1;//(short) (0xffff*texX);
		short ty = 1;//(short) (0xffff*texY);
		short[] tex = { tx, 0, 0, 0, tx, ty, 0, ty };
		VertexArray texArray = new VertexArray(tex.length / 2, 2, 2);
		texArray.set(0, tex.length/2, tex);
		
		VertexBuffer vb = new VertexBuffer();
		vb.setPositions(vertArray, 1.0f, null); // unit scale, zero bias
		vb.setNormals(normArray);
		vb.setTexCoords(0, texArray, 1.0f, null); 
		
		return vb;
	}
	
	public static TriangleStripArray getQuadStrip()
	{
		return new TriangleStripArray(0, new int[] { 4 });
	}
	
	public static Appearance createAp(Image img)
	{
		Image2D image2D = new Image2D(Image2D.RGBA, img);

		Texture2D tex = new Texture2D(image2D);
		tex.setFiltering(Texture2D.FILTER_NEAREST, Texture2D.FILTER_NEAREST);
		tex.setWrapping(Texture2D.WRAP_CLAMP, Texture2D.WRAP_CLAMP);
		tex.setBlending(Texture2D.FUNC_BLEND);
		tex.setBlendColor(0xffffffff);
		
		Material mat = new Material();
		mat.setColor(Material.DIFFUSE, 0xFFFFFFFF); // white
		mat.setColor(Material.SPECULAR, 0xFFFFFFFF); // white
		mat.setShininess(128f);
		
		CompositingMode cm = new CompositingMode();
		cm.setAlphaThreshold(0.5f);
		cm.setBlending(CompositingMode.ALPHA);
		
		Appearance ap = new Appearance();
		ap.setTexture(0, tex);
		ap.setMaterial(mat);
		ap.setCompositingMode(cm);
		
		return ap;
	}*/
}
