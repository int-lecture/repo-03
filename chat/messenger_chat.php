<!doctype html>
<html lang="de">
  <head>
    <meta charset="utf-8">
	<link rel="stylesheet" href="stylesheet.css">
    <title>Secure Messenger</title>
  </head>
  
  <body>
  
	<div class="topnav" id="myTopnav">
		<a href="index.html">Willkommen</a>
		<a href="firma.html">Firma</a>
		<a href="produkte.html">Produkte</a>
		<a href="impressum.html">Impressum</a>
		<a href="registrieren.html">Registrieren</a>
	</div>

	<div class="content" id="myContent">
		
<?php
		//Nutzer wird eingelesen.
		$strNutzer="";
		$strPartner ="";
		$datei;
		$chat;
		$dateiname;
		if(isset($_GET["nutzer"]) && isset($_GET["partner"])) {
			$strNutzer = strtolower($_GET["nutzer"]);
			$strPartner = strtolower($_GET["partner"]);
		}
		
		if($strNutzer==""|| $strPartner==""){
			echo "Gib die Namen ein";
		}else{
			if((strcmp($strNutzer, $strPartner))<=0){
				$dateiname="chats/".$strNutzer."_".$strPartner.".txt";
			}
			if((strcmp($strPartner, $strNutzer))<0){
				$dateiname="chats/".$strPartner."_".$strNutzer.".txt";
			}
			$datei = fopen($dateiname, "a+");
			chmod($dateiname, 0666);
			
			if(isset($_POST["nachricht"])){
				$strNachricht=$_POST["nachricht"];	
				if($strNachricht!=""){
		
					fwrite($datei, $strNachricht."\n");	
				}
			}
			fclose($datei);
			$chat = file_get_contents($dateiname);
			echo nl2br($chat);
			
		}
		echo "<form method='post' action='messenger_chat.php?nutzer=".$strNutzer."&partner=".$strPartner."'>
			 <p>Nachricht: <input type='text' name='nachricht' />
			 <input type='submit' value='Senden' /> "
	?>
	</div>
	
	
	

 </body> 
</html>
