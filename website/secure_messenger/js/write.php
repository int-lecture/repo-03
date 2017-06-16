<?php 
    if(isset($_POST)) {
        $message = $_POST['message'];
        $file = $_POST['file'];
        
        if(file_put_contents($file, $message)) {
            echo 'Die Nachricht "'. $message . '" wurde erfolgreich in "' . $file . '" geschrieben!';
        } else {
            echo 'Fehler beim schreiben in ' . $file . '!';
        }    
    }
?>
