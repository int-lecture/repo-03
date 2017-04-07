	<?php
		
		$strVorname = $_POST["vorname"];
		$strNachname = $_POST["nachname"];
		$strEmail = $_POST["email"];
		
		if($strVorname == "" || $strNachname == "" || $strEmail == "" || !(isset( $_POST[datenspeicherung]))) {
			header("Location: registrierenFail.html");
			exit;
			
		}
		else {
			
			$handle = fopen ('newsletterAbos.txt', 'a+'	);
			fwrite($handle, $strVorname . ",");
			fwrite($handle, $strNachname. ",");
			fwrite($handle, $strEmail. ", \n");
			fclose($handle);
			header("Location: registrierenSuccess.html");
			exit;
			
		}
			
	?>