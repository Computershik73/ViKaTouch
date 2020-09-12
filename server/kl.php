<?php
        function get_web_page( $url ) {
  $uagent = "KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)";
  $ch = curl_init( $url );
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); // ⽵罵⽵�ཱུ�ཱུ彵� ⽵彵ή-���ཱུ�轵��
  curl_setopt($ch, CURLOPT_HEADER, 0); // �張 ⼵缵⼵�༵�༵張� 缵㼵뼵⼵꼵輵
  curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1); // Ｕ張�張�伵輵� Ｕ �張伵輵�張꼵�༵켵
  curl_setopt($ch, CURLOPT_ENCODING, ""); // ἵ�༵ἵ༵��⼵張� ⼵�張 꼵伵輵�⼵꼵輵
  curl_setopt($ch, CURLOPT_USERAGENT, $uagent); // useragent
  curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 120); // �༵鼵켵�� �張伵輵�張�輵�
  curl_setopt($ch, CURLOPT_TIMEOUT, 120); // �༵鼵켵�� �⼵張�༵
  curl_setopt($ch, CURLOPT_MAXREDIRS, 100); // ��༵�༵⼵뼵輵⼵���� Ｕ�뼵張 10-㼵 �張伵輵�張꼵�༵
//curl_setopt($ch, CURLOPT_HTTPHEADER, array("VKAndroidApp/5.5-1891 (Android 48.1; SDK 431; 7.0; 24
//armeabi-v7a; V$
  $content = curl_exec( $ch );
  $err = curl_errno( $ch );
  $errmsg = curl_error( $ch ); $header = curl_getinfo( $ch );
  curl_close( $ch );
  $header['errno'] = $err;
  $header['errmsg'] = $errmsg;
  $header['content'] = $content;
  return $header;
}
function Hex2String($hex){
    $string='';
    for ($i=0; $i < strlen($hex)-1; $i+=2){
        $string .= chr(hexdec($hex[$i].$hex[$i+1]));
    }
    return $string;
}
ini_set('display_errors', 1);
header('Access-Control-Allow-Origin: *');
$third = substr($_SERVER['REQUEST_URI'], 18); 
//echo $third;
$third = Hex2String($third);
//echo $third;
$result = get_web_page($third); 
if (($result['errno'] != 0)||($result['http_code'] != 200))
    {
        echo $result['errmsg'];
        }
else
        {
        $page = $result['content']; //echo $_SERVER[$third]; //echo $page; 
//$stt = iconv('cp1251', 'utf-8', $page); 
$stt=$page; 
//$textcyr="Тествам с кирилица";
  // $textlat="I pone dotuk raboti!";
        $cyr = [
            'а','б','в','г','д','е','ё','ж','з','и','й','к','л','м','н','о','п',
            'р','с','т','у','ф','х','ц','ч','ш','щ','ъ','ы','ь','э','ю','я',
            'А','Б','В','Г','Д','Е','Ё','Ж','З','И','Й','К','Л','М','Н','О','П',
           'Р','С','Т','У','Ф','Х','Ц','Ч','Ш','Щ','Ъ','Ы','Ь','Э','Ю','Я', 'ツ', '😊', '😂', '😄', '😁', '😃', '😆', '😭'
];
 $lat = [
            '%D0%B0','%D0%B1','%D0%B2','%D0%B3','%D0%B4','%D0%B5','%D1%91','%D0%B6','%D0%B7','%D0%B8','%D0%B9','%D0%Ba','%D0%Bb','%D0%Bc','%D0%Bd','%D0%Be','%D0%Bf',
            '%D1%80','%D1%81','%D1%82','%D1%83','%D1%84','%D1%85','%D1%86','%D1%87','%D1%88','%D1%89','%D1%8a','%D1%8b','%D1%8c','%D1%8d','%D1%8e','%D1%8f',
            '%D0%90','%D0%91','%D0%92','%D0%93','%D0%94','%D0%95','%D0%81','%D0%96','%D0%97','%D0%98','%D0%99','%D0%9a','%D0%9b','%D0%9c','%D0%9d','%D0%9e','%D0%9f',
            '%D0%a0','%D0%a1','%D0%a2','%D0%a3','%D0%a4','%D0%a5','%D0%a6','%D0%a7','%D0%a8','%D0%a9','%D0%aa','%D0%ab','%D0%ac','%D0%ad','%D0%ae','%D0%af', '%E3%83%84', '%F0%9F%98%8A', '%F0%9F%98%82', '%F0%9F%98%84', '%F0%9F%98%81', '%F0%9F%98%83', '%F0%9F%98%86', '%F0%9F%98%AD'
        ];

      /*  $lat = [
   '^a*','^b*','^v*','^g*','^d*','^e*','^je*','^zh*','^z*','^i*','^y*','^k*','^l*','^m*','^n*','^o*','^p*',
            '^r*','^s*','^t*','^u*','^f*','^kh*','^c*','^ch*','^sh*','^jsh*','^hh*','^ih*','^jh*','^eh*','^ju*','^ja*',
            '^A*','^B*','^V*','^G*','^D*','^E*','^JE*','^ZH*','^Z*','^I*','^Y*','^K*','^L*','^M*','^N*','^O*','^P*',
            '^R*','^S*','^T*','^U*','^F*','^KH*','^C*','^CH*','^SH*','^JSH*','^HH*','^IH*','^JH*','^EH*','^JU*','^JA*'
        ];*/
/*$buf="";
$buf+=$stt{0};
$i=1;
while ($i<(strlen($stt)-1)) {
	
	if ((!in_array($stt{$i-1}, $cyr)) && (in_array($stt{$i}, $cyr)))  {
		$buf+="|^";
	}
	$buf+=$stt{$i};
	 if ((!in_array($stt{$i}, $cyr)) && (in_array($stt{$i+1}, $cyr)))  {
                $buf+="^|";
        }
	$i++;
}*/
$st = str_replace($cyr, $lat, $stt); 
if (strlen($st)==0) {
  echo 'error';
} else {
$user_agent = $_SERVER["HTTP_USER_AGENT"];
//if ((strpos($user_agent, "Chrome")<1) && (strpos($user_agent, "Firefox")<1)) {
echo $st;
//} else {
//echo('Status: 503 Service Temporarily Unavailable');
//header('Status: 503 Service Temporarily Unavailable');
//}  
}
        }

        

?>
