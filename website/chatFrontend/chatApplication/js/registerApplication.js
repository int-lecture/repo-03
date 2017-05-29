$( document ).ready(function() {   
	var URL = "http://141.19.142.57:5002/register";
	var dataObject = { 'pseudonym': "Hallo", 'user': "hallo@web.de", 'password': "1234" };
	$.ajax({	
	     	url: URL,
            	type: 'PUT',
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
	var a= $("#pw1");
	var pw1= $("#pw1").val();
	var pw2= $("#pw2").val();

	if (pw1 != pw2){
		$("#error").html("Du böser bube die Passwörter sind nicht Identisch!");
		return false;}
	
	var URL = "http://141.19.142.57:5002/register/";
	var dataObject = { 'pseudonym': $("#inputPseudonym").val(), 'user': $("#inputEmail").val(), 'password': pw1 };

        alert(JSON.stringify(dataObject));

        $.ajax({
            	url: URL,
           	type: 'PUT',    
            	data: JSON.stringify(dataObject),
            	dataType: 'json',
            	success: function(result) {
                alert("success?")}
        	}).error(function() {
    		alert( "Handler for .error() called." )
  	});;
	return true;

}



