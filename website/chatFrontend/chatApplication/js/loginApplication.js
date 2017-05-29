$( document ).ready(function() {  
	
});

function checkPw(){
	var URL = "http://141.19.142.57:5001/login/";
	var dataObject = {'user': $("#inputEmail").val(), 'password': $("#inputPassword").val()};

        alert(JSON.stringify(dataObject));

        $.ajax({
            	url: URL,
           	type: 'POST',    
            	data: JSON.stringify(dataObject),
		contentType: "application/json; charset=utf-8",
            	dataType: 'json',
            	success: function(result) {
		document.cookie = "token="+result.token+";pseudonym="+result.pseudonym+";expires="+result["expire-date"];
                alert("success?");
		window.location.href = "http://141.19.142.57/chatFrontend/chatApplication/chatApplication.html";},
		error: function(xhr, a, b){
		alert(" error");}
  	});
	alert("Erfolgreich eingelogt");
	return false;

}