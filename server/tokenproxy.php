<?php
header('Access-Control-Allow-Origin: *');
$third = $_SERVER['QUERY_STRING']; 
$third = urldecode($third)
$result = get_web_page($third); 
if ($result['errno'] != 0 || $result['http_code'] != 200)
{
	echo $result['errmsg'];
}
else
{
    $page = $result['content'];
	$stt=$page; 
	/*
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

	$st = str_replace($cyr, $lat, $stt); 
	*/
	if (strlen($st)==0)
	{
	  echo 'error';
	} 
	else 
	{
		echo $st;
	}
}
function get_web_page($url)
{
  $uagent = "KateMobileAndroid/51.1 lite-442 (Android 4.2.2; SDK 17; x86; LENOVO Lenovo S898t+; ru)";
  $ch = curl_init($url);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
  curl_setopt($ch, CURLOPT_HEADER, 0);
  curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1);
  curl_setopt($ch, CURLOPT_ENCODING, "");
  curl_setopt($ch, CURLOPT_USERAGENT, $uagent);
  curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 120);
  curl_setopt($ch, CURLOPT_TIMEOUT, 120);
  curl_setopt($ch, CURLOPT_MAXREDIRS, 100);
  $content = curl_exec( $ch );
  $err = curl_errno( $ch );
  $errmsg = curl_error( $ch ); $header = curl_getinfo( $ch );
  curl_close( $ch );
  $header['errno'] = $err;
  $header['errmsg'] = $errmsg;
  $header['content'] = $content;
  return $header;
}
?>
