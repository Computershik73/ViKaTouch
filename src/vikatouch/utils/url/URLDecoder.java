package vikatouch.utils.url;

import java.io.UnsupportedEncodingException;

/**
 * @author Shinovon
 * 
 */
public final class URLDecoder {

	public static String encode(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if ((65 <= ch) && (ch <= 90)) {
				sbuf.append((char) ch);
			} else if ((97 <= ch) && (ch <= 122)) {
				sbuf.append((char) ch);
			} else if ((48 <= ch) && (ch <= 57)) {
				sbuf.append((char) ch);
			} else if (ch == 32) {
				// sbuf.append("%20");
				sbuf.append("%20");
			} else if ((ch == 45) || (ch == 95) || (ch == 46) || (ch == 33) || (ch == 126) || (ch == 42) || (ch == 39)
					|| (ch == 40) || (ch == 41) || (ch == 58) || (ch == 47)) {
				sbuf.append((char) ch);
			} else if (ch <= 127) {
				sbuf.append(hex(ch));
			} else if (ch <= 2047) {
				sbuf.append(hex(0xC0 | ch >> 6));
				sbuf.append(hex(0x80 | ch & 0x3F));
			} else {
				sbuf.append(hex(0xE0 | ch >> 12));
				sbuf.append(hex(0x80 | ch >> 6 & 0x3F));
				sbuf.append(hex(0x80 | ch & 0x3F));
			}
		}
		return sbuf.toString();
	}
	
	private static String hex(int ch) {
		String x = Integer.toHexString(ch);
		return "%" + (x.length() == 1 ? "0" : "") + x;
	}

	public static String decode(String s) throws UnsupportedEncodingException {
		String enc = "UTF-8";
		int i = 0;
		int j = s.length();
		StringBuffer sb = new StringBuffer(j > 500 ? j / 2 : j);
		int k = 0;
		if (enc.length() == 0) {
			throw new UnsupportedEncodingException("URLDecoder: empty string enc parameter");
		}
		byte[] arrayOfByte = null;
		while (k < j) {
			char c = s.charAt(k);
			switch (c) {
			case '+':
				sb.append(' ');
				k++;
				i = 1;
				break;
			case '%':
				try {
					if (arrayOfByte == null) {
						arrayOfByte = new byte[(j - k) / 3];
					}
					int m = 0;
					while ((k + 2 < j) && (c == '%')) {
						arrayOfByte[(m++)] = ((byte) Integer.parseInt(s.substring(k + 1, k + 3), 16));
						k += 3;
						if (k < j) {
							c = s.charAt(k);
						}
					}
					if ((k < j) && (c == '%')) {
						throw new IllegalArgumentException("URLDecoder: Incomplete trailing escape (%) pattern");
					}
					sb.append(new String(arrayOfByte, 0, m, enc));
				} catch (NumberFormatException localNumberFormatException) {
					throw new IllegalArgumentException("URLDecoder: Illegal hex characters in escape (%) pattern - "
							+ localNumberFormatException.getMessage());
				}
				i = 1;
				break;
			default:
				sb.append(c);
				k++;
			}
		}
		return i != 0 ? sb.toString() : s;
	}
}