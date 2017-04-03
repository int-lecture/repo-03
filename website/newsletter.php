	<?php
		$strVorname = $_POST["vorname"];
		$strNachname = $_POST["nachname"];
		$strEmail = $_POST["email"];
		
		if(vorname == "" || nachname == "" || email == "" || !ja) {
			echo "Bitte alle Daten eingeben und der Datenspeicherung zustimmen!";
		}
		else {
			$handle = fopen ('newsletterAbos.txt', 'w'	);
			fwrite($handle, $strVorname);
			fwrite($handle, $strNachname);
			fwrite($handle, $strEmail);
			fclose($handle);
			echo "Die Speicherung war erfolgreich!";
		}
			
	?>