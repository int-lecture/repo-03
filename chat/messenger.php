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
		<h2> Registrierung </h2> 
		<! Formular für die Registrierung>
		<form method="get" action="messenger.php">
			<p>Nutzer: <input type="text" name="nutzer" /></p>
			<p><input type="submit" value="Start" /></p>
		</form>

<?php
		//Nutzer wird eingelesen.
		$strNutzer = $_GET["nutzer"];
		if($strNutzer==""){
			echo "Gib die Namen ein";
		}else{
			$alledateien = scandir('chats');
		
			foreach($alledateien as $datei){
				$strpos= strpos($datei, ".txt");
				$datei = substr($datei,0 , $strpos);
				$namen = explode("_", $datei);	
				$name1= $namen[0];
				$name2= $namen[1];
				if(strcasecmp($name1, $strNutzer)==0){
					echo" <a href=messenger_chat.php?nutzer=".$strNutzer."&partner=".$name2.">".$name2."</a> </br>";
				}
				if(strcasecmp($name2, $strNutzer)==0){			
					echo" <a href=messenger_chat.php?nutzer=".$strNutzer."&partner=".$name1.">".$name1."</a> </br>";
				}	
			}
		}
		
	?>
	</div>
	
	
	
	

 </body> 
</html>