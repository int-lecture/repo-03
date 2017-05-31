$(document).ready(function () {
	//TODO: delete bob before releasing
	//setTests();
    alert("Seite geladen Html wird jetzt per JQuery eingefügt")
    testContactDiv();
    testContactDiv();
    testContactDiv();
    testUserMessage();
    testPartnerMessage();
    testUserMessage();
    testPartnerMessage();
	//loadContacts();
	//send();
	//getMessages();
});
var token;
var pseudonym;
var contacts;
var sequenceNumbers = [];
var ip = "141.19.142.57";
var ipLogin = ip;
var ipRegister = ip;
var ipChat = ip;


function testContactDiv() {
    $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='http://shurl.esy.es/y'></div></div><div class='col-sm-9 col-xs-9 sideBar-main'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>Test123</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");
}

function testUserMessage(){
     $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-sender'><div class='sender'><div class='message-text' id='messages'>UserTestMessage</div><span class='message-time pull-right'>Sun</span></div></div></div></div>");
}
function testPartnerMessage(){
    $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>PartnerTestMessage</div><span class='message-time pull-right'>Sun</span></div></div></div></div>");
}


function setTests() {
	pseudonym = "bob";
	sequenceNumbers['bob'] = 0;
	ip = "141.19.142.57";
	var URL = "http://" + ip + ":5001/login/";
	var dataObject = {'user': "bob@web.de", 'password': "HalloIchbinBob"};

        //alert(JSON.stringify(dataObject));

    $.ajax({
        url: URL,
        type: 'POST',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            token = result.token;
            //alert("success?");
            window.location.href = "chatApplication.html";
        },
        error: function (xhr, a, b) {
            //alert(" error");
        }
    });
}


function loadContacts() {
	readCookie();
	
	var URL = "http://" + ip + ":5002/profile/";
	var dataObject = {'token': token, 'getownprofile': pseudonym};

        //alert(JSON.stringify(dataObject));

    $.ajax({
        url: URL,
        type: 'POST',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            contacts = result.contacts;
            alert("Kontakt wird unter Bob eingefügt");
            $("#contacts").append("<p>testContacts</p>");
            //alert(contacts);

        },
        error: function (xhr, a, b) {
            alert("Kontakt wird unter Bob eingefügt");
            $("#contacts").append("<p>testContacts</p>");
        }
    });

}
var date;
function send() {
	readCookie();
	var URL = "http://" + ip + ":5000/send/";
	var dataObject = {'from': pseudonym, 'to': 'bob', 'date': '2017-03-30T17:00:00Z', 'text': 'Test', 'token': token};

    //alert(JSON.stringify(dataObject));

    $.ajax({
        url: URL,
        type: 'PUT',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            //Not Tested TODO: sequenceNumbers von dem to
            alert("test Nachricht wird in der Chatblase von Bob eingefügt");
            $("#send").append("<p>test</p>");
            sequenceNumbers[pseudonym] = result.sequence;
            date = result.date;
            //alert(sequenceNumbers[pseudonym]);
            //alert("success?");
        },
        error: function (xhr, a, b) {
            alert("test Nachricht wird in der Chatblase von Bob eingefügt");
            //alert(" error");
            $("#send").append("<p>test</p>");
        }
    });
}


var messages;
function getMessages() {
	readCookie();
	//Not Tested TODO: seuqencenumber vom to anfügen
	var URL = "http://" + ip + ":5000/messages/" + pseudonym + "/" + sequenceNumbers[pseudonym];
	//Not Tested TODO: Authorization Header anfügen
	$.ajax({
		headers: {
            "Authorization": token
        },
        url: URL,
        type: 'GET',
		contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
			//TODO:append messages
			alert("test Nachricht wird in der Chatblase vom Partner eingefügt");
			$("#messages").append("<p>testMessages</p>");
			messages = result;
			//alert(messages);
			//alert("success?");
		},
		error: function (xhr, a, b) {
			//alert(" error");
			alert("test Nachricht wird in der Chatblase vom Partner eingefügt");
			$("#messages").append("<p>testMessages</p>");
		}
    });

}

function readCookie() {
	var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
	$.each(ca, function (index, value) {
		value = value.trim();
		if (value.substring(0, 6)=="token=") {
			token = value.substring(6);
			//alert(token);		
		}
		if(value.substring(0, "pseudonym=".length)=="pseudonym="){
			pseudonym = value.substring("pseudonym=".length);
			//alert(pseudonym);
		}
		if(value.substring(0, "ip=".length)=="ip="){
			ip = value.substring("ip=".length);
			//alert(ip);
		}
        if(value.substring(0, "ipLogin=".length)=="ipLogin="){
			ipLogin = value.substring("ipLogin=".length);
		}
        if(value.substring(0, "ipRegister=".length)=="ipRegister="){
			ipRegister = value.substring("ipRegister=".length);
			//alert(ip);
		}
        if(value.substring(0, "ipChat=".length)=="ipChat="){
			ipChat = value.substring("ipChat=".length);
			//alert(ip);
		}

	
	});	
}

    