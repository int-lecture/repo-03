$( document ).ready(function() {  
	
});

function checkPw(){
	var URL = "http://141.19.142.57:5002/register/";
	var dataObject = {'user': $("#inputEmail").val(), 'password': $("#pw1").val()};

        alert(JSON.stringify(dataObject));

        $.ajax({
            	url: URL,
           	type: 'POST',    
            	data: JSON.stringify(dataObject),
		contentType: "application/json; charset=utf-8",
            	dataType: 'json',
            	success: function(result) {
                alert("success?")}
  	});
	alert("Erfolgreich registriert");
	return true;

}