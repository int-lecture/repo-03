$( document ).ready(function() {  
	//loadContacts();
	send();
	getMessages();
});
var token;
var pseudonym;
var contacts;
var sequenceNumbers;
var ip=141.19.142.57;
function loadContacts(){
	readCookie();	
	
	var URL = "http://"+ip+":5002/profile/";
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
		alert(contacts);
                alert("success?");},
		error: function(xhr, a, b){
		alert(" error");}
  	});
	
}
var date;
function send(){
	readCookie();
	var URL = "http://"+ip+":5000/send/";
	var dataObject = {'from': pseudonym, 'to': 'bob', 'date':'2017-03-30T17:00:00Z', 'text': 'Test', 'token': token};

        alert(JSON.stringify(dataObject));

        $.ajax({
            	url: URL,
           	type: 'PUT',    
            	data: JSON.stringify(dataObject),
		contentType: "application/json; charset=utf-8",
            	dataType: 'json',
            	success: function(result) {
		//Not Tested TODO: sequenceNumbers von dem to
		sequenceNumbers[pseudonym]=result.sequence;
		date=result.date;
		alert(sequenceNumbers[pseudonym]);
                alert("success?");},
		error: function(xhr, a, b){
		alert(" error");}
  	});
}


var messages;
function getMessages(){
	readCookie();
	//Not Tested TODO: seuqencenumber vom to anfügen
	var URL = "http://"+ip+":5000/messages/"+pseudonym+"/"+sequenceNumbers[pseudonym];
	//Not Tested TODO: Authorization Header anfügen
	$.ajax({
		headers: {
        "Authorization":token
    	}
        url: URL,
        type: 'GET',    
		contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function(result) {
		//TODO:append messages
		messages=result;
		alert(messages);
                alert("success?");},
		error: function(xhr, a, b){
		alert(" error");}
  	});

}

function readCookie(){
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
}

    