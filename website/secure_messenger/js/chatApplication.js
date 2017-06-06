$(document).ready(function () {
    loadConfig();
    //loginTest("bob@web.de", "HalloIchbinBob");
    //loginTest("tom@web.de", "HalloIchbinTom");
    readCookie();
    openChat("Secure Messenger");
    testContactDiv();
    //loadContacts();
    getMessages();
    $(".heading-compose").click(function () {
        $(".side-two").css({
            "left": "0"
        });
    })

    $(".heading-dot").click(function () {
        $("#conversation").empty();
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
    $("#startChatWithFriend").click(function () {
        openChat($("#newFriendsName"));
    })


});
var token;
var pseudonym;
var contact = [];
var partner;
var sequenceNumbers = [];
var sequenceNumber;
var ipLogin;
var ipRegister;
var ipChat;
var messages = [];
var sentMessages = [];
var tokenBob;
var tokenTom;
var date;
var chatMessages = [];

function openThisChat(id) {
    openChat(id);
}
function openNewChat() {
    var newFriendsName = $("#newFriendsName").val();
    openChat(newFriendsName);
    //newContact(newFriendsName);
    $(".side-two").css({
        "left": "-100%"
    });
}

function loadConfig() {
      $.ajax({
        url: 'js/config.txt',
        type: 'GET',
        success: function (result) {
            var ips = xhr.responseText.split(";");
            ipLogin = ips[0].substring("ipLogin:".length + 1);
            ipChat = ips[1].substring("ipChat:".length + 1);
            ipRegister = ips[2].substring("ipRegister:".length + 1);
        }
    });
}

function openChat(partner) {
    if (partner == "Secure Messenger") {
        $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>Hey willkommen beim Secure Messenger neben siehst du deine bisherigen Freunde</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");
    } else {
        this.partner = partner;
        document.getElementById("partner").innerHTML = partner;
        if (typeof messages == 'undefined') {
            getMessages();
        }
        showMessages();
    }
}

function getLegalDate() {
    var date = new Date();
    var stringDate = date.getFullYear() + "-" + (date.getMonth() < 10 ? "0" + date.getMonth() : date.getMonth()) + "-" + (date.getDay() < 10 ? "0" + date.getDay() : date.getDay()) + "T" + (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) + ":" + ((date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds())) + "+0200";
    return stringDate;
}

function send() {
    if (document.getElementById("partner").innerHTML == "Secure Messenger") {
        $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>Du kannst nicht mit uns schreiben, wähle bitte einen deiner Kontakte aus deiner Kontaktliste aus oder suche über die Sprechblase nach neuen Freunden</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");
        getMessages();
    } else if ($("#comment").val() == "") {

    } else {
        readCookie();
        var URL = ipChat + "/send/";
        var dataObject = { 'from': pseudonym, 'to': partner, 'date': getLegalDate(), 'token': token, 'text': $("#comment").val() };
        $.ajax({
            url: URL,
            type: 'PUT',
            data: JSON.stringify(dataObject),
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            success: function (result) {
                sequenceNumber = result.sequence;
                var sentMessage = {
                    from: pseudonym, to: partner
                    , date: getLegalDate(), text: $("#comment").val(), sequence: result.sequence
                };
                sentMessages.push(sentMessage);
                $("#comment").val("");
                showMessages();
            },
            error: function (xhr, a, b) {
                alert("Die Nachricht wurde nicht erfolgreich an " + partner + " gesendet");
            }

        });
    }
}
function newContact(newChatPartner) {
    $(".sideBar").append("<div class='row sideBar-body' ><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='" + newChatPartner + "'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>" + newChatPartner + "</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");

}

function testContactDiv() {
    $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='tom'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>tom</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");
    $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='bob'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>bob</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");
    $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='peter'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>peter</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");

}

function loadContacts() {
    var URL = ipRegister + "/profile/";
    var dataObject = { 'getownprofile': pseudonym, 'token': token };

    $.ajax({
        url: URL,
        type: 'POST',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            contact = result.contact;
        },
        error: function (xhr, a, b) {
            alert("Kontakte wurden nicht erfolgreich geladen");
        }

    });
    $.each(contact, function (index, value) {
        $(".sideBar").append("<div class='row sideBar-body'><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>" + value + "</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");

    });
}

function getMessages() {
    function update() {
        if (typeof sequenceNumber != 'undefined') {
            var URL = ipChat + "/messages/" + pseudonym + "/0"
            //+ sequenceNumber.toString();
        } else {
            var URL = ipChat + "/messages/" + pseudonym + "/0";
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
                if (xhr.status == 200) {
                    if (typeof sequenceNumber == 'undefined') {
                        messages = result;
                        showMessages();
                    }
                    else {
                        messages = result;
                        sequenceNumber = sequenceNumber + result.length;
                        showMessages();
                    }
                } else if (xhr.status == 204) {

                }
            },
            error: function (xhr, a, b) {
                window.location.href = "loginApplication.html";
                //alert("getMessages von " + pseudonym + " fehlgeschlagen");
            }

        });

    }
    setInterval(update, 10000);
}

function sortMessages() {
    chatMessages = sentMessages.concat(messages);
    chatMessages.sort(function (a, b) {
        return (a.date < b.date) ? -1 : ((a.date > b.date) ? 1 : 0);
    });
}
function showMessages() {
    sortMessages();
    $("#conversation").empty();
    $.each(chatMessages, function (index, value) {
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
        }
        if (value.substring(0, "pseudonym=".length) == "pseudonym=") {
            pseudonym = value.substring("pseudonym=".length);
        }
    });
}

function loginTest(user, password) {
    var URL = ipLogin + "/login/";
    var dataObject = { 'user': user, 'password': password };
    $.ajax({
        url: URL,
        type: 'POST',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            document.cookie = "token=" + result.token;
            document.cookie = "pseudonym=" + result.pseudonym;
        },
        error: function (xhr, a, b) {
        }, async: false
    });
}



