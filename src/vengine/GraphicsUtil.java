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

// пусть лежит тут отдельно, проще новые самоделки перетаскивать
//а хто афтар сие тварения??
public class GraphicsUtil {
	protected static int[] lastRGB;
	
	public static Image lastPO2;
	
	
	public static void fillTransparentRect(Graphics graphics, int x, int y, int w, int h, int r, int g, int b, int a)
	{
		int c = (a << 24) | (r << 16) | (g << 8) | b;
		fillTransparentRect(graphics, x, y, w, h, c);
	}
	
	//УВОЖАЕМЫЙ,
	//ВАС БАЙТКОД ОПТИМИЗИРОВАТЬ НЕ УЧИЛИ?
	public static void fillTransparentRect(Graphics g, int x, int y, int w, int h, int color)
	{
		int l = w*h+1;
		int[] m;
		if(lastRGB != null && l>1 && lastRGB.length == l && lastRGB[0] == color)
		{
			m = lastRGB;
		}
		else
		{
			m = new int[l];
			for(int i=0; i<l; i++)
			{
				m[i] = color;
			}
		}
		g.drawRGB(m, 0, w, x, y, w, h, true);
	}
	
	public static void darkScreen(Graphics g, int w, int h, int color)
	{
		fillTransparentRect(g, 0, 0, w, h, color);
	}
	
	public static void darkScreen(Graphics graphics, int w, int h, int r, int g, int b, int a)
	{
		int c = (a << 24) | (r << 16) | (g << 8) | b;
		darkScreen(graphics, w, h, c);
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
			// ну рухнуло и рухнуло
			//а надо за исключением.
		}
		return img; 
	}
	
	/*public static ImgToPowerOf2Res powerOf2(Image img)
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
	}*/
	
	public static void DrawShadowText(Graphics g, String text, int x, int y, int a, int color1, int color2, int dx, int dy)
	{
		g.setColor(color2);
		g.drawString(text, x+dx, y+dy, a);
		g.setColor(color1);
		g.drawString(text, x, y, a);
	}
	
	public static void DrawBorderedText(Graphics g, String text, int x, int y, int a, int color1, int color2)
	{
		g.setColor(color2);
		g.drawString(text, x+1, y+1, a);
		g.drawString(text, x+1, y-1, a);
		g.drawString(text, x-1, y+1, a);
		g.drawString(text, x-1, y-1, a);
		g.setColor(color1);
		g.drawString(text, x, y, a);
	}
	
	/*
	// 3D
	
	
	
	
	*/
}
