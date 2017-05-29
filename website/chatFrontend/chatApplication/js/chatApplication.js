$( document ).ready(function() {  
	loadContacts();
});
var token;
var pseudonym;
var contacts;
function loadContacts(){
	var decodedCookie = decodeURIComponent(document.cookie);
    	var ca = decodedCookie.split(';');
	$.each(ca, function(index, value){
		value = value.trim();
		if(value.substring(0,6)=="token="){
			token=value.substring(6);
			alert(token);		
		}
		if(value.substring(0, "pseudonym=".length)=="pseudonym="){
			pseudonym=value.substring("pseudonym=".length);
			alert(pseudonym);
		}	
	});	
	
	var URL = "http://141.19.142.57:5002/profile/";
	var dataObject = {'token': token, 'getownprofile': pseudonym};

        alert(JSON.stringify(dataObject));

        $.ajax({
            	url: URL,
           	type: 'POST',    
            	data: JSON.stringify(dataObject),
		contentType: "application/json; charset=utf-8",
            	dataType: 'json',
            	success: function(result) {
		contacts=result.contacts;
                alert("success?");
		window.location.href = "http://141.19.142.57/chatFrontend/chatApplication/chatApplication.html";},
		error: function(xhr, a, b){
		alert(" error");}
  	});
	
}

    