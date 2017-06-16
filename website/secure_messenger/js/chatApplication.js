$(document).ready(function () {
    loadConfig();
    readCookie();
    openChat("Secure Messenger");
    getMessages();
    window.setTimeout(loadContacts, 2000);
    $(".heading-compose").click(function () {
        $(".side-two").css({
            "left": "0"
        });
    })

    $("#config").click(function () {
        $(".side-three").css({
            "left": "0",
            "top": "0"
        });
    })
    $(".config-back").click(function () {
        $(".side-three").css({
            "left": "-100%"
        });
    })
    $("#toTop").click(function(){
      $("#searchText").focus();
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
    $("#newFriendsName").on('keypress', function (e) {
        if (e.which == 13) {
            openNewChat();
        }
    })
    $("#comment").on('keypress', function (e) {
        if (e.which == 13) {

            $(this).attr("disabled", "disabled");
            send();
            $(this).removeAttr("disabled");
        }

    })
    $("#logout").click(function () {
        var decodedCookie = decodeURIComponent(document.cookie);
        document.cookie = "expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
        window.location.href = "loginApplication.html";

    })


});
var token;
var pseudonym;
var contact = [];
var partner;
var sequenceNumber = 0;

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
    newContact(newFriendsName);
    $("#newFriendsName").val("");
    $(".side-two").css({
        "left": "-100%"
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
        $("#comment").focus();
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
                var sentMessage = {
                    from: pseudonym, to: partner
                    , date: getLegalDate(), text: $("#comment").val(), sequence: result.sequence
                };
                sentMessages.push(sentMessage);
                $("#comment").val("");
                $("#comment").focus();
                showMessages();
            },
            error: function (xhr, a, b) {
                alert("Die Nachricht wurde nicht erfolgreich an " + partner + " gesendet");
            }

        });
    }
}


function getMessages() {
    function update() {
        var URL = ipChat + "/messages/" + pseudonym + "/" + sequenceNumber;
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
                    messages = messages.concat(result);
                    sequenceNumber = result[result.length - 1].sequence;
                    showMessages();
                } else if (xhr.status == 204) {

                }
            },
            error: function (xhr, a, b) {
                if (xhr.status == 401) {
                    alert("Leider ist da etwas schief gelaufen :(\nBeim abrufen Ihrer Nachricht gab es einen Fehler : " + xhr.status + ".\n Loggen Sie sich erneut ein.");
                    window.location.href = "loginApplication.html";
                }
            }

        });

    }
    setInterval(update, 1000);
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
    alert($("#inputIpLogin").val());
}

function loadConfig() {
    $.ajax({
        url: 'js/config.txt',
        type: 'GET',
        success: function (result) {
            var ips = result.split(";");
            ipLogin = ips[0].substring("ipLogin:".length + 1);
            ipChat = ips[1].substring("ipChat:".length + 1);
            ipRegister = ips[2].substring("ipRegister:".length + 1);
        }
    });
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




