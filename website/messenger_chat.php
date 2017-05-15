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
		<a href="messenger.php">Messenger</a>
	</div>

	<div class="content" id="myContent">
		
<?php
		session_start();
		session_regenerate_id(true);
		echo "Session ID: " . session_id() . "<br>";
		echo "logout: " . isset($_POST['logout']) ;
		//Nutzer wird eingelesen.
		$strNutzer="";
		$strPartner ="";
		$datei;
		$chat;
		$dateiname;
		if(isset($_SESSION["nutzer"]) && isset($_SESSION["partner"])) {
			$strNutzer = strtolower($_SESSION["nutzer"]);
			$strPartner = strtolower($_SESSION["partner"]);
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
			
			if(isset($_REQUEST["nachricht"])){
				$strNachricht=$_REQUEST["nachricht"];	
				if($strNachricht!=""){
					date_default_timezone_set("Europe/Berlin");
					$timestamp = time();
					$datum = date("d.m.Y",$timestamp);
					$uhrzeit = date("H:i",$timestamp);
					fwrite($datei, $strNutzer."  ".$datum." - ".$uhrzeit." : ".$strNachricht."\n");	
				}
			}
			fclose($datei);
			$chat = file_get_contents($dateiname);
			
		}
		echo "<h3>".$strNutzer." du chattest jetzt mit ".$strPartner."</h2><form method='post' action='messenger_chat.php'>
			 <input type='submit' class='button' value='logout' /></form><p id='chatInhalt'>".nl2br($chat).
			 "</p><form method='post' action='messenger_chat.php'>
			 <input type='text' class='button' name='nachricht' placeholder='Nachricht eingeben...'/></br>
			 <input type='submit' id='senden' value='Senden' /> </form>
			 <form method='post' action='messenger_chat.php'>
			 <input type='submit' class='button' value='Reload' /> </form>"
	?>
	</div>
	
	
	

 </body> 
</html>
