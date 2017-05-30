$( document ).ready(function() {  
	//TODO: delete bob before releasing
	setTests();
	loadContacts();
	send();
	getMessages();
});
var token;
var pseudonym;
var contacts;
var sequenceNumbers=[];
var ip="141.19.142.57";

function setTests(){
	pseudonym="bob";
	sequenceNumbers["bob"]=0;
	ip="141.19.142.57";
	var URL = "http://"+ip+":5001/login/";
	var dataObject = {'user': "bob@web.de", 'password': "HalloIchbinBob"};

        //alert(JSON.stringify(dataObject));

        $.ajax({
			url: URL,
			type: 'POST',    
			data: JSON.stringify(dataObject),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success: function(result) {
				token = result.token;
				//alert("success?");
				window.location.href = "chatApplication.html";
			},
			error: function(xhr, a, b){
				//alert(" error");
			}
  	});
}


function loadContacts(){
	readCookie();	
	
	var URL = "http://"+ip+":5002/profile/";
	var dataObject = {'token': token, 'getownprofile': pseudonym};

        //alert(JSON.stringify(dataObject));

        $.ajax({
			url: URL,
			type: 'POST',    
			data: JSON.stringify(dataObject),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success: function(result) {
				contacts=result.contacts;
				alert("Kontakt wird unter Bob eingefügt");
				$("#contacts").append("<p>testContacts</p>");
				//alert(contacts);
				
			},
			error: function(xhr, a, b){
				alert("Kontakt wird unter Bob eingefügt");
				$("#contacts").append("<p>testContacts</p>");
			}
  	});
	
}
var date;
function send(){
	readCookie();
	var URL = "http://"+ip+":5000/send/";
	var dataObject = {'from': pseudonym, 'to': 'bob', 'date':'2017-03-30T17:00:00Z', 'text': 'Test', 'token': token};

        //alert(JSON.stringify(dataObject));

        $.ajax({
			url: URL,
			type: 'PUT',    
			data: JSON.stringify(dataObject),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success: function(result) {
				//Not Tested TODO: sequenceNumbers von dem to
				alert("test Nachricht wird in der Chatblase von Bob eingefügt");
				$("#send").append("<p>test</p>");
				sequenceNumbers[pseudonym]=result.sequence;
				date=result.date;
				//alert(sequenceNumbers[pseudonym]);
				//alert("success?");
			},
			error: function(xhr, a, b){
				alert("test Nachricht wird in der Chatblase von Bob eingefügt");
				//alert(" error");
				$("#send").append("<p>test</p>");
			}
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
    	},
        url: URL,
        type: 'GET',    
		contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function(result) {
			//TODO:append messages
			alert("test Nachricht wird in der Chatblase vom Partner eingefügt");
			$("#messages").append("<p>testMessages</p>");
			messages=result;
			//alert(messages);
			//alert("success?");
		},
		error: function(xhr, a, b){
			//alert(" error");
			alert("test Nachricht wird in der Chatblase vom Partner eingefügt");
			$("#messages").append("<p>testMessages</p>");
		}
  	});

}

function readCookie(){
	var decodedCookie = decodeURIComponent(document.cookie);
    	var ca = decodedCookie.split(';');
	$.each(ca, function(index, value){
		value = value.trim();
		if(value.substring(0,6)=="token="){
			token=value.substring(6);
			//alert(token);		
		}
		if(value.substring(0, "pseudonym=".length)=="pseudonym="){
			pseudonym=value.substring("pseudonym=".length);
			//alert(pseudonym);
		}
		if(value.substring(0, "ip=".length)=="ip="){
			ip=value.substring("ip=".length);
			//alert(ip);
		}

	
	});	
}

    