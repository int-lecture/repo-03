$( document ).ready(function() {   
	var URL = "http://141.19.142.57:5001/login";
	var dataObject = { 'pseudonym': "Hallo", 'password': "1234" };
	$.ajax({	
	     	url: URL,
            	type: 'POST',
		contentType: "application/json",    
            	data: JSON.stringify(dataObject),
            	dataType: 'json',
            	success: function(result) {
                alert("success?");},
	    	error: function(xhr, ajaxOptions, thrownError){
		alert("error?");
		alert(xhr.status);}
        });

});

function checkPw(){
	
	var URL = "http://141.19.142.57:5001/login/";
	var dataObject = { 'user': $("#inputEmail").val(), 'password': $("#inputPassword").val()};

        alert(JSON.stringify(dataObject));

        $.ajax({
            	url: URL,
           	type: 'POST',    
            	data: JSON.stringify(dataObject),
            	dataType: 'json',
            	success: function(result) {
                alert("success?")}
        	}).error(function() {
    		alert( "Handler for .error() called." )
  	});;
	return true;

}