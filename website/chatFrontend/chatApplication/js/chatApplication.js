$(document).ready(function () {
    //loginTest("bob@web.de", "HalloIchbinBob");
    //loginTest("tom@web.de", "HalloIchbinTom");
    readCookie();
    openChat("Secure Messenger Demo");
    testContactDiv();
    //loadContacts();
    getMessages();
    $(".heading-compose").click(function () {
        $(".side-two").css({
            "left": "0"
        });
    })

    $(".newMessage-back").click(function () {
        $(".side-two").css({
            "left": "-100%"
        });
    })
    $(".sideBar-main").click(function () {
        openChat($(this).attr('id'));
    })
    $(".reply-send").click(function () {
        send();
    })

});
var token;
var pseudonym;
var contact = [];
var partner;
var sequenceNumbers = [];
//var ipLogin = ip;
//var ipRegister = ip;
//var ipChat = ip;
var messages = [];
var tokenBob;
var tokenTom;
var date;


function openChat(partner) {
    if (partner == "Secure Messenger Demo") {
        $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>Hey willkommen beim Secure Messenger neben siehst du deine bisherigen Freunde</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");

    } else {
        this.partner = partner;
        //$(".conversation").empty();
        //$(".conversation").append("<div class='row heading'><div class='col-sm-2 col-md-1 col-xs-3 heading-avatar'><div class='heading-avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-8 col-xs-7 heading-name'><a class='heading-name-meta'>"+partner+"</a><span class='heading-online'>Online</span></div><div class='col-sm-1 col-xs-1  heading-dot pull-right'><i class='fa fa-ellipsis-v fa-2x  pull-right' aria-hidden='true'></i></div></div>");
        document.getElementById("partner").innerHTML = partner;
        getMessages();
        showMessages();
    }
}
function send() {
    readCookie();
    ip = "141.19.142.57";
    var URL = "http://" + ip + ":5000/send/";
    var dataObject = { 'from': pseudonym, 'to': partner, 'date': '2017-03-30T17:00:00+0200', 'token': token, 'text': $("#comment").val() };

    $.ajax({
        url: URL,
        type: 'PUT',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            sequenceNumbers[pseudonym] = result.sequence;
            var sentMessage = {
                from: pseudonym, to: partner
                , date: '2017-03-30T17:00:00+0200', text: $("#comment").val(), sequence: 0
            };
            messages = messages.concat(sentMessage);
            $("#comment").val("");
            showMessages();
            //date = result.date;
        },
        error: function (xhr, a, b) {
            alert(text + " wurde nicht erfolgreich an " + partner + " gesendet");
        }, async: false

    });
}
function testContactDiv() {
    $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='tom'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>tom</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");
    $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='bob'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>bob</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");
    $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='peter'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>peter</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");

}

function loadContacts() {
    ip = "141.19.142.57";
    var URL = "http://" + ip + ":5002/profile/";
    var dataObject = { 'getownprofile': pseudonym, 'token': token };

    $.ajax({
        url: URL,
        type: 'POST',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            //alert(result.contact);
            contact = result.contact;
        },
        error: function (xhr, a, b) {
            alert("Kontakte wurden nicht erfolgreich geladen");
        }, async: false

    });
    $.each(contact, function (index, value) {
        $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>" + value + "</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");

    });
}

function getMessages() {
    ip = "141.19.142.57";
    //Not Tested TODO: seuqencenumber vom to anfügen
    if (typeof (sequenceNumbers[pseudonym]) == 'undefined') {
        var URL = "http://" + ip + ":5000/messages/" + pseudonym + "/";
    } else {
        var URL = "http://" + ip + ":5000/messages/" + pseudonym + "/" + sequenceNumbers[pseudonym].toString();
    }
    $.ajax({
        headers: {
            "Authorization": token
        },
        url: URL,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result, textStatus, xhr) {
            //TODO:append messages
            if (xhr.status == 200) {
                //alert("getMessages von " + pseudonym + " erfolgreich erhalten");
                messages = result;
                $.each(result, function (index, value) {
                    messages = messages.concat(value);
                });
                showMessages();
            }
        },
        error: function (xhr, a, b) {
            //alert(" error");
            alert("getMessages von " + pseudonym + " fehlgeschlagen");
        }, async: false
    });

}

function showMessages() {
    $("#conversation").empty();
    $.each(messages, function (index, value) {
        if (value.from == partner && value.to == pseudonym) {
            $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>" + value.text + "</div><span class='message-time pull-right'>" + value.date.substr(11, 5) + "</span></div></div></div></div>");
        }
        if (value.from == pseudonym && value.to == partner) {
            $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-sender'><div class='sender'><div class='message-text' id='messages'>" + value.text + "</div><span class='message-time pull-right'>" + value.date.substr(11, 5) + "</span></div></div></div></div>");
        }
    });
    document.getElementById("conversation").scrollTop = document.getElementById("conversation").scrollHeight;
}



function saveSettings() {
    document.cookie = "ipLogin=" + $("#inputIpLogin").val();
}

function readCookie() {
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    $.each(ca, function (index, value) {
        value = value.trim();
        if (value.substring(0, "token=".length) == "token=") {
            token = value.substring(6);
            //alert("token aus dem Cookie: "+token);		
        }
        if (value.substring(0, "pseudonym=".length) == "pseudonym=") {
            pseudonym = value.substring("pseudonym=".length);
            //alert(pseudonym);
        }
        if (value.substring(0, "ip=".length) == "ip=") {
            ip = value.substring("ip=".length);
            //alert(ip);
        }
        if (value.substring(0, "ipLogin=".length) == "ipLogin=") {
            ipLogin = value.substring("ipLogin=".length);
        }
        if (value.substring(0, "ipRegister=".length) == "ipRegister=") {
            ipRegister = value.substring("ipRegister=".length);
            //alert(ip);
        }
        if (value.substring(0, "ipChat=".length) == "ipChat=") {
            ipChat = value.substring("ipChat=".length);
            //alert(ip);
        }


    });
}

function loginTest(user, password) {
    ip = "141.19.142.57";
    var URL = "http://" + ip + ":5001/login/";
    var dataObject = { 'user': user, 'password': password };

    //alert(JSON.stringify(dataObject));

    $.ajax({
        url: URL,
        type: 'POST',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            //alert(result.token);
            document.cookie = "token=" + result.token;
            document.cookie = "pseudonym=" + result.pseudonym;
            //document.cookie="expires="+result["expire-date"];
        },
        error: function (xhr, a, b) {
        }, async: false
    });
}

