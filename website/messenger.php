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
		<h2> Einloggen </h2> 
		<! Formular zum einloggen>
		<form method="post" action="messenger.php">
			<p><input  type="text" class="pflichtfeld" name="nutzer" placeholder="Nutzernamen eingeben..." required/></p>
			<p><input type="text" class="pflichtfeld" name="partner" placeholder="Partnernamen eingeben..."  required/></p>
			<p><input type="submit" class="button" value="Start" /></p> <hr/>
		</form>

<?php
		session_start();
		session_regenerate_id(true);
		echo "Session ID: " . session_id() . "<br>";
		echo "logout: " . isset($_POST['logout']) ;
		$_SESSION["nutzer"] = $_REQUEST["nutzer"];
		$_SESSION["partner"] = $_REQUEST["partner"];
		
		if($_SESSION["nutzer"]==""){
			echo "Gib die Namen ein";
		}else{
			header("location: messenger_chat.php");	
		}
		
	?>
	</div>
	
	
	
	

 </body> 
</html>