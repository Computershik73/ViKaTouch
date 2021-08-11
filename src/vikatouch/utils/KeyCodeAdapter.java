package vikatouch.utils;

import javax.microedition.lcdui.Canvas;

import ru.nnproject.vikaui.utils.DisplayUtils;
import vikatouch.VikaTouch;  

/** 
 * Class redefines codes of mobile phone to our constant values. 
 * Class can give to developers following information: 
 * <ul> 
 * <li/><i>defined platform name</i><br> 
 * In case if device vendor not defined we'll recieve <code>PLATFORM_NOT_DEFINED</code> like platform name. 
 * Same in this case keyCodes will be setted like for Nokia and SE. It's done for work on emulators, 
 * because on some of them it's impossible to define platform name. 
 * <li/><i>adopted to our constants key code value</i> 
 * <li/><i>for test returns defined real code of left softkey</i> 
 * </ul> 
 */  
public final class KeyCodeAdapter {  
  
    /** 
     * instance on this class 
     */  
    private static KeyCodeAdapter instance = new KeyCodeAdapter();  
    /** 
     * canvas used for definig codes 
     */  
    private final Canvas adaptorCanvas;  
    /** 
     * constants for platforms  names 
     */  
    public static final String PLATFORM_MOTOROLA = "motorola";  
    public static final String PLATFORM_NOKIA = "nokia";  
    public static final String PLATFORM_SONY_ERICSSON = "SE";  
    public static final String PLATFORM_SIEMENS = "siemens";  
    public static final String PLATFORM_SAMSUNG = "samsung";  
    public static final String PLATFORM_LG = "LG";  
    public static final String PLATFORM_NOT_DEFINED = "NA";  
    /** 
     * constants for keycodes 
     */  
    public static final int SOFT_KEY_LEFT = -6; 
    		//-201;  
    public static final int SOFT_KEY_RIGHT = -202;  
    public static final int SOFT_KEY_MIDDLE_INTERNET = -203;  
    /** 
     * this key is present on Nokia s60 
     */  
    public static final int PENCIL_KEY = -207;  
    public static final int DELETE_KEY = -204;  
    public static final int BACK_KEY = -7;
    		//-205;  
    //    public static final int SEND_KEY = -206; //constant will be used in future for green key start dialling  
    public static final int KEY_1 = 201;  
    public static final int KEY_2 = 202;  
    public static final int KEY_3 = 203;  
    public static final int KEY_4 = 204;  
    public static final int KEY_5 = 205;  
    public static final int KEY_6 = 206;  
    public static final int KEY_7 = 207;  
    public static final int KEY_8 = 208;  
    public static final int KEY_9 = 209;  
    public static final int KEY_0 = 200;  
    public static final int KEY__POUND = 211;  
    public static final int KEY__STAR = 212;  
    /** 
     * KEYS on JOISTICK 
     */  
    public static final int UP_KEY = -1;
    		//221;  
    public static final int DOWN_KEY = -2;
    		//222;  
    public static final int LEFT_KEY = -3;
    		//223;  
    public static final int RIGHT_KEY = -4;
    		//224;  
    public static final int CENTER_KEY = -5;
    		//225;  
  
    public static final int NOT_DEFINED_KEY = 254;  
  
    /** 
     * current platform name 
     */  
    private final String PLATFORM_NAME;  
    /** 
     * current platform codeofSoftkey 
     */  
    private final int SOFTKEY_LEFT;  
    private final int SOFTKEY_RIGHT;  
    private final int SOFTKEY_MIDDLE_INTERNET;  
    private final int SOFTKEY_DELETE;  
    private final int SOFTKEY_BACK;  
  
    /** 
     * standart values for softkeys of different platforms 
     * used only in predefining 
     */  
    private static final int SOFT_KEY_LEFT_SE = -6;  
    private static final int SOFT_KEY_RIGHT_SE = -7;  
    private static final int DELETE_KEY_SE = -8;  
    private static final int INTERNET_KEY_SE = -10;  
    private static final int BACK_KEY_SE = -11;  
    private static final int SOFT_KEY_LEFT_SAMSUNG = -6;  
    private static final int SOFT_KEY_RIGHT_SAMSUNG = -7;  
    private static final int DELETE_KEY_SAMSUNG = -8;  
    private static final int SOFT_KEY_LEFT_SIEMENS = -1;  
    private static final int SOFT_KEY_RIGHT_SIEMENS = -4;  
    private static final int SOFT_KEY_LEFT_NOKIA = -6;  
    private static final int SOFT_KEY_RIGHT_NOKIA = -7;  
    private static final int DELETE_KEY_NOKIA = -8;  
    private static final int PENCIL_KEY_NOKIA = -50;  
    private static final int SOFT_KEY_LEFT_MOTOROLA = -21;  
    private static final int SOFT_KEY_RIGHT_MOTOROLA = -22;  
    private static final int SOFT_KEY_LEFT_MOTOROLA2 = -20;  
    private static final int SOFT_KEY_LEFT_MOTOROLA1 = 21;  
    private static final int SOFT_KEY_RIGHT_MOTOROLA1 = 22;  
    private static final int SOFT_KEY_MIDLE_MOTOROLA = -23;  
    private static final int SOFT_KEY_MIDLE_NOKIA = -5;  
  
    private static final String SOFT_WORD = "SOFT";  
  
    /** 
     * constructor. 
     * here is predefining of spesial keys and platform made 
     */  
    private KeyCodeAdapter() {  
        adaptorCanvas = DisplayUtils.canvas;
        PLATFORM_NAME = getPlatform();  
        SOFTKEY_LEFT = getLeftSoftkeyCode();  
        SOFTKEY_RIGHT = getRightSoftkeyCode();  
        SOFTKEY_MIDDLE_INTERNET = getMidleORInternetSoftkeyCode();  
        SOFTKEY_DELETE = getDeleteKeyCode();  
        SOFTKEY_BACK = getBackKeyCode();  
    }  
  
    /** 
     * return platform keycode of left softkey 
     * if it's defined 
     * default value -6 
     * 
     * @return SOFTKEY_LEFT 
     */  
    public int getPlatformSoftkeyLeftCode() {  
        return SOFTKEY_LEFT;  
    }  
  
    /** 
     * Returns mobile phone platform 
     * 
     * @return name mobile phone platform 
     */  
    private String getPlatform() {  
        // detecting NOKIA or SonyEricsson  
        try {  
            final String currentPlatform = System.getProperty("microedition.platform");  
            if (currentPlatform.indexOf("Nokia") != -1) {  
                return PLATFORM_NOKIA;  
            } else if (currentPlatform.indexOf("SonyEricsson") != -1) {  
                return PLATFORM_SONY_ERICSSON;  
            }  
        } catch (Throwable ex) {  
        }  
        // detecting SAMSUNG  
        try {  
            Class.forName("com.samsung.util.Vibration");  
            return PLATFORM_SAMSUNG;  
        } catch (Throwable ex) {  
        }  
        // detecting MOTOROLA  
        try {  
            Class.forName("com.motorola.multimedia.Vibrator");  
            return PLATFORM_MOTOROLA;  
        } catch (Throwable ex) {  
            try {  
                Class.forName("com.motorola.graphics.j3d.Effect3D");  
                return PLATFORM_MOTOROLA;  
            } catch (Throwable ex2) {  
                try {  
                    Class.forName("com.motorola.multimedia.Lighting");  
                    return PLATFORM_MOTOROLA;  
                } catch (Throwable ex3) {  
                    try {  
                        Class.forName("com.motorola.multimedia.FunLight");  
                        return PLATFORM_MOTOROLA;  
                    } catch (Throwable ex4) {  
                    }  
                }  
            }  
        }  
        try {  
            if (adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA).toUpperCase().indexOf(SOFT_WORD) > -1) {  
                return PLATFORM_MOTOROLA;  
            }  
        } catch (Throwable e) {  
            try {  
                if (adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase().indexOf(SOFT_WORD) > -1) {  
                    return PLATFORM_MOTOROLA;  
                }  
            } catch (Throwable e1) {  
                try {  
                    if (adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA2).toUpperCase().indexOf(SOFT_WORD) > -1) {  
                        return PLATFORM_MOTOROLA;  
                    }  
                } catch (Throwable e2) {  
                }  
            }  
        }  
        // detecting SIEMENS  
        try {  
            Class.forName("com.siemens.mp.io.File");  
            return PLATFORM_SIEMENS;  
        } catch (Throwable ex) {  
        }  
        // detecting LG  
        try {  
            Class.forName("mmpp.media.MediaPlayer");  
            return PLATFORM_LG;  
        } catch (Throwable ex) {  
            try {  
                Class.forName("mmpp.phone.Phone");  
                return PLATFORM_LG;  
            } catch (Throwable ex1) {  
                try {  
                    Class.forName("mmpp.lang.MathFP");  
                    return PLATFORM_LG;  
                } catch (Throwable ex2) {  
                    try {  
                        Class.forName("mmpp.media.BackLight");  
                        return PLATFORM_LG;  
                    } catch (Throwable ex3) {  
                    }  
                }  
            }  
        }  
        return PLATFORM_NOT_DEFINED;  
    }  
  
    /** 
     * define real left soft key code by platform 
     * 
     * @return code 
     */  
    private int getLeftSoftkeyCode() {  
        int keyCode = 0;  
        try {  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) {  
                String softkeyLeftMoto = "";  
                try {  
                    softkeyLeftMoto = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA).toUpperCase();  
                } catch (IllegalArgumentException ilae) {  
//                    ilae.printStackTrace();  
                }  
                String softkeyLeftMoto1 = "";  
                try {  
                    softkeyLeftMoto1 = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase();  
                } catch (IllegalArgumentException ilae) {  
//                    ilae.printStackTrace();  
                }  
                String softkeyLeftMoto2 = "";  
                try {  
                    softkeyLeftMoto2 = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA2).toUpperCase();  
                } catch (IllegalArgumentException ilae) {  
//                    ilae.printStackTrace();  
                }  
                if (softkeyLeftMoto.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto.indexOf("1") >= 0) {  
                    return SOFT_KEY_LEFT_MOTOROLA;  
                } else if (softkeyLeftMoto1.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto1.indexOf("1") >= 0) {  
                    return SOFT_KEY_LEFT_MOTOROLA1;  
                } else if (softkeyLeftMoto2.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto2.indexOf("1") >= 0) {  
                    return SOFT_KEY_LEFT_MOTOROLA2;  
                } else if (softkeyLeftMoto.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto.indexOf("LEFT") >= 0) {  
                    return SOFT_KEY_LEFT_MOTOROLA;  
                } else if (softkeyLeftMoto1.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto1.indexOf("LEFT") >= 0) {  
                    return SOFT_KEY_LEFT_MOTOROLA1;  
                } else if (softkeyLeftMoto2.indexOf(SOFT_WORD) >= 0 && softkeyLeftMoto2.indexOf("LEFT") >= 0) {  
                    return SOFT_KEY_LEFT_MOTOROLA2;  
                }  
  
            } else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) {  
                return SOFT_KEY_LEFT_NOKIA;  
            } else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) {  
//                String leftkeySamsungName = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_SAMSUNG).toUpperCase();  
//                if (leftkeySamsungName.indexOf(SOFT_WORD) >= 0) {  
//                    if (leftkeySamsungName.indexOf("1") >= 0) {  
                return SOFT_KEY_LEFT_SAMSUNG;  
//                    } else if (leftkeySamsungName.indexOf("LEFT") >= 0) {  
//                        return SOFT_KEY_LEFT_SAMSUNG;  
//                    }  
//                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) {  
                String leftKeySiemensName = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_SIEMENS).toUpperCase();  
                if (leftKeySiemensName.indexOf(SOFT_WORD) >= 0) {  
                    if (leftKeySiemensName.indexOf("1") >= 0) {  
                        return SOFT_KEY_LEFT_SIEMENS;  
                    } else if (leftKeySiemensName.indexOf("LEFT") >= 0) {  
                        return SOFT_KEY_LEFT_SIEMENS;  
                    }  
                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) {  
                return SOFT_KEY_LEFT_SE;  
            } else if (PLATFORM_NAME.equals(PLATFORM_NOT_DEFINED)) {  
                //  
                for (int i = -125; i <= 125; i++) {  
                    if (i == 0) {  
                        i++;  
                    }  
//                    System.out.println(getKeyName(i).toUpperCase());  
                    final String s = adaptorCanvas.getKeyName(i).toUpperCase();  
                    if (s.indexOf(SOFT_WORD) >= 0) {  
                        if (s.indexOf("1") >= 0) {  
                            keyCode = i;  
                            break;  
                        }  
                        if (s.indexOf("LEFT") >= 0) {  
                            keyCode = i;  
                            break;  
                        }  
                    }  
                }  
            }  
            if (keyCode == 0) {  
                //#if emulator  
                return SOFT_KEY_LEFT_NOKIA;  
                //#endif  
            }  
        } catch (Throwable iaEx) {  
            //#if emulator  
            return SOFT_KEY_LEFT_NOKIA;  
            //#endif  
        }  
        return keyCode;  
    }  
  
    /** 
     * define real right soft key code for current platform 
     * 
     * @return code 
     */  
    private int getRightSoftkeyCode() {  
        int keyCode = 0;  
        try {  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) {  
  
                String rightSoftMoto1 = "";  
                try {  
                    rightSoftMoto1 = adaptorCanvas.getKeyName(SOFT_KEY_LEFT_MOTOROLA1).toUpperCase();  
                } catch (IllegalArgumentException ilae) {  
//                    ilae.printStackTrace();  
                }  
                String rightSoftMoto = "";  
                try {  
                    rightSoftMoto = adaptorCanvas.getKeyName(SOFT_KEY_RIGHT_MOTOROLA).toUpperCase();  
                } catch (IllegalArgumentException ilae) {  
//                    ilae.printStackTrace();  
                }  
                String rightSoftMoto2 = "";  
                try {  
                    rightSoftMoto2 = adaptorCanvas.getKeyName(SOFT_KEY_RIGHT_MOTOROLA1).toUpperCase();  
                } catch (IllegalArgumentException ilae) {  
//                    ilae.printStackTrace();  
                }  
                if (rightSoftMoto.indexOf(SOFT_WORD) >= 0 && rightSoftMoto.indexOf("2") >= 0) {  
                    return SOFT_KEY_RIGHT_MOTOROLA;  
                } else if (rightSoftMoto1.indexOf(SOFT_WORD) >= 0 && rightSoftMoto1.indexOf("2") >= 0) {  
                    return SOFT_KEY_RIGHT_MOTOROLA;  
                } else if (rightSoftMoto2.indexOf(SOFT_WORD) >= 0 && rightSoftMoto2.indexOf("2") >= 0) {  
                    return SOFT_KEY_RIGHT_MOTOROLA1;  
                } else if (rightSoftMoto.indexOf(SOFT_WORD) >= 0 && rightSoftMoto.indexOf("RIGHT") >= 0) {  
                    return SOFT_KEY_LEFT_MOTOROLA;  
                } else if (rightSoftMoto1.indexOf(SOFT_WORD) >= 0 && rightSoftMoto1.indexOf("RIGHT") >= 0) {  
                    return SOFT_KEY_RIGHT_MOTOROLA1;  
                } else if (rightSoftMoto2.indexOf(SOFT_WORD) >= 0 && rightSoftMoto2.indexOf("RIGHT") >= 0) {  
                    return SOFT_KEY_RIGHT_MOTOROLA;  
                }  
  
            } else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) {  
                return SOFT_KEY_RIGHT_NOKIA;  
            } else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) {  
//                String rightSoftSamsung = adaptorCanvas.getKeyName(SOFT_KEY_RIGHT_SAMSUNG).toUpperCase();  
//                if (rightSoftSamsung.indexOf(SOFT_WORD) >= 0) {  
//                    if (rightSoftSamsung.indexOf("2") >= 0) {  
                return SOFT_KEY_RIGHT_SAMSUNG;  
//                    } else if (rightSoftSamsung.indexOf("RIGHT") >= 0) {  
//                        return SOFT_KEY_RIGHT_SAMSUNG;  
//                    }  
//                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) {  
                String rightSoftSiemens = adaptorCanvas.getKeyName(SOFT_KEY_RIGHT_SIEMENS).toUpperCase();  
                if (rightSoftSiemens.indexOf(SOFT_WORD) >= 0) {  
                    if (rightSoftSiemens.indexOf("4") >= 0) {  
                        return SOFT_KEY_RIGHT_SIEMENS;  
                    } else if (rightSoftSiemens.indexOf("RIGHT") >= 0) {  
                        return SOFT_KEY_RIGHT_SIEMENS;  
                    }  
                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) {  
                return SOFT_KEY_RIGHT_SE;  
            } else if (PLATFORM_NAME.equals(PLATFORM_NOT_DEFINED)) {  
                for (int i = -125; i <= 125; i++) {  
                    if (i == 0) {  
                        i++;  
                    }  
                    String keyName = adaptorCanvas.getKeyName(i).toUpperCase();  
                    if (keyName.indexOf(SOFT_WORD) >= 0) {  
                        if (keyName.indexOf("2") >= 0) {  
                            keyCode = i;  
                            break;  
                        } else if (keyName.indexOf("4") >= 0) {  
                            keyCode = i;  
                            break;  
                        } else if (keyName.indexOf("RIGHT") >= 0) {  
                            keyCode = i;  
                            break;  
                        }  
                    }  
                }  
            }  
        } catch (Throwable iaEx) {  
            //#if emulator  
            return SOFT_KEY_RIGHT_NOKIA;  
            //#endif  
        }  
        return keyCode;  
    }  
  
    /** 
     * define real middle soft key code for current platform 
     * 
     * @return code 
     */  
    private int getMidleORInternetSoftkeyCode() {  
        try {  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) {  
                if (adaptorCanvas.getKeyName(SOFT_KEY_MIDLE_MOTOROLA).toUpperCase().indexOf("SOFT") >= 0) {  
                    return SOFT_KEY_MIDLE_MOTOROLA;  
                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) {  
                if (adaptorCanvas.getKeyName(SOFT_KEY_MIDLE_NOKIA).toUpperCase().indexOf("SOFT") >= 0) {  
                    return SOFT_KEY_MIDLE_NOKIA;  
                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) {  
            } else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) {  
            } else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) {  
                return INTERNET_KEY_SE;  
            }  
        } catch (Throwable e) {  
        }  
        return 0;  
    }  
  
    /** 
     * define real key's C or DELETE code for current platform 
     * 
     * @return code 
     */  
    private int getDeleteKeyCode() {  
        try {  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) {  
  
            } else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) {  
                if (adaptorCanvas.getKeyName(DELETE_KEY_SE).toUpperCase().indexOf("CLEAR") >= 0) {  
                    return DELETE_KEY_NOKIA;  
                } else {  
                    return DELETE_KEY_NOKIA;  
                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) {  
                if (adaptorCanvas.getKeyName(DELETE_KEY_SAMSUNG).toUpperCase().indexOf("CLEAR") >= 0) {  
                    return DELETE_KEY_SAMSUNG;  
                }  
            } else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) {  
            } else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) {  
                if (adaptorCanvas.getKeyName(DELETE_KEY_SE).toUpperCase().indexOf("CLEAR") >= 0) {  
                    return DELETE_KEY_SE;  
                } else if (adaptorCanvas.getKeyName(DELETE_KEY_SE).toUpperCase().indexOf("C") >= 0) {  
                    return DELETE_KEY_SE;  
                } else {  
                    return DELETE_KEY_SE;  
                }  
            }  
        } catch (Throwable e) {  
            return DELETE_KEY_SE;  
        }  
        return 0;  
    }  
  
  
    /** 
     * define real key's BACK code for current platform 
     * 
     * @return code 
     */  
    private int getBackKeyCode() {  
        try {  
            if (PLATFORM_NAME.equals(PLATFORM_MOTOROLA)) {  
  
            } else if (PLATFORM_NAME.equals(PLATFORM_NOKIA)) {  
  
            } else if (PLATFORM_NAME.equals(PLATFORM_SAMSUNG)) {  
            } else if (PLATFORM_NAME.equals(PLATFORM_SIEMENS)) {  
            } else if (PLATFORM_NAME.equals(PLATFORM_SONY_ERICSSON)) {  
                return BACK_KEY_SE;  
            }  
        } catch (Throwable e) {  
        }  
        return 0;  
    }  
  
    /** 
     * name of curent platform 
     * 
     * @return PLATFORM_NAME 
     */  
    public String getPlatformName() {  
        return PLATFORM_NAME;  
    }  
  
    /** 
     * Used to adopt key kode to predefined constances, which are platform independent. 
     * <p/> 
     * You can use this method in any kind of canvas, but better at first time to call 
     * <code>getInstance()</code> method at the beginning of midlet work, because initialisation takes time. 
     * <p/> 
     * Best variant for usage is calling <code>adoptKeyCode()</code> to use <code>keyPressed()</code> method in Canvas: 
     * <pre> 
     * protected void keyPressed(int keyCode) { 
     *     keyCode = KeyCodeAdapter.getInstance().adoptKeyCode(keyCode); 
     * } 
     * </pre> 
     * and then you can use it: 
     * <pre> 
     * switch (keyCode) { 
     *   case KeyCodeAdapter.UP_KEY: 
     *     break; 
     *   case KeyCodeAdapter.SOFT_KEY_LEFT: 
     *     break; 
     * }</pre> 
     * or send this code to any other clesses. 
     * 
     * @param keycode This code is sent by platform to canvas and redirected here 
     * @return this keycode is equal to one of our constants declared in this class 
     */  
    public int adoptKeyCode(int keycode) { 
    	VikaTouch.needstoRedraw=true;
    	VikaTouch.canvas.serviceRepaints();
        switch (keycode) {  
            case Canvas.KEY_NUM0:  
                return KEY_0;  
            case Canvas.KEY_NUM1:  
                return KEY_1;  
            case Canvas.KEY_NUM2:  
                return KEY_2;  
            case Canvas.KEY_NUM3:  
                return KEY_3;  
            case Canvas.KEY_NUM4:  
                return KEY_4;  
            case Canvas.KEY_NUM5:  
                return KEY_5;  
            case Canvas.KEY_NUM6:  
                return KEY_6;  
            case Canvas.KEY_NUM7:  
                return KEY_7;  
            case Canvas.KEY_NUM8:  
                return KEY_8;  
            case Canvas.KEY_NUM9:  
                return KEY_9;  
            case Canvas.KEY_STAR:  
                return KEY__STAR;  
            case Canvas.KEY_POUND:  
                return KEY__POUND; 
            case -7:
            	return -7;
            default:  
                if (keycode == SOFTKEY_LEFT) {  
                    return SOFT_KEY_LEFT;  
                } else if (keycode == SOFTKEY_RIGHT) {  
                    return SOFT_KEY_RIGHT;  
                } else if (keycode == SOFTKEY_DELETE) {  
                    return DELETE_KEY;  
                } else if (keycode == SOFTKEY_BACK) {  
                    return BACK_KEY;  
                } else if (keycode == SOFTKEY_MIDDLE_INTERNET) {  
                    return SOFT_KEY_MIDDLE_INTERNET;  
                } else if (keycode == PENCIL_KEY_NOKIA) {  
                    return PENCIL_KEY;  
                } else {  
                    try {  
                        final int gameAction;  
                        gameAction = adaptorCanvas.getGameAction(keycode);  
                        if (gameAction == Canvas.UP) {  
                            return UP_KEY;  
                        } else if (gameAction == Canvas.DOWN) {  
                            return DOWN_KEY;  
                        } else if (gameAction == Canvas.LEFT) {  
                            return LEFT_KEY;  
                        } else if (gameAction == Canvas.RIGHT) {  
                            return RIGHT_KEY;  
                        } else if (gameAction == Canvas.FIRE) {  
                            return CENTER_KEY;  
                        }  
                    } catch (IllegalArgumentException e) {  
//                    e.printStackTrace();  
                    }  
                }  
                break;  
        }  
        //#if debug  
        //# return keycode;  
        //#else  
        return NOT_DEFINED_KEY;  
        //#endif  
    }  
  
    /** 
     * return instance of class 
     * 
     * @return instance 
     */  
    public static KeyCodeAdapter getInstance() {  
        return instance;  
    }  
}  