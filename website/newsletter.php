	<?php
		<! Variablen auslesen und setzen>
		$strVorname = $_POST["vorname"];
		$strNachname = $_POST["nachname"];
		$strEmail = $_POST["email"];
		<! Überprüfen ob alle Felder ausgefüllt und Datenspeicherung akzeptiert wurde>
		if($strVorname == "" || $strNachname == "" || $strEmail == "" || !(isset( $_POST[datenspeicherung]))) {
			header("Location: registrierenFail.html");
			exit;
			
		}
		else {
			<! Schreibe die Daten der Person in die Textdatei "newsletterAbos.txt>
			$handle = fopen ('newsletterAbos.txt', 'a+'	);
			fwrite($handle, $strVorname . ",");
			fwrite($handle, $strNachname. ",");
			fwrite($handle, $strEmail. ", \n");
			fclose($handle);
			header("Location: registrierenSuccess.html");
			exit;
			
		}
			
	?>