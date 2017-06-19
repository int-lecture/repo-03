$(document).ready(function () {
    loadConfig();
    readCookie();
    openChat("Secure Messenger");
    getMessages();
    window.setTimeout(loadContacts, 1000);
    $(".heading-compose").click(function () {
        $(".side-two").css({
            "left": "0"
        });
        $("#newFriendsName").focus();
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
    $("#toTop").click(function () {
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
            addContact();
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
    $("#help").click(function () {
        openChat("Secure Messenger");
    })
});
var token;
var pseudonym;
var contact;
var currenChatPartner = [];
var partner;
var sequenceNumber = 0;
var messages = [];
var sentMessages = [];
var chatMessages = [];

//opens the chat with the given partner, loads the messages and shows them.
function openChat(partner) {
    if (partner == "Secure Messenger") {
        sendSecureMessenger("start");
        document.getElementById("partner").innerHTML = partner;
    } else {
        $("#receiver-picture").attr("src", "css/profilePic.png");
        this.partner = partner;
        document.getElementById("partner").innerHTML = partner;
        if (typeof messages == 'undefined') {
            getMessages();
        }
        showMessages();
    }
    $("#comment").focus();
}

//returns the current date in our string date format.
function getLegalDate() {
    var date = new Date();
    var stringDate = date.getFullYear() + "-" + ((date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1)) + "-" + ((date.getDate()) < 10 ? "0" + (date.getDate()) : (date.getDate())) + "T" + (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":" + (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) + ":" + ((date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds())) + "+0200";
    return stringDate;
}

//sends the message from the input field.
function send() {
    if (document.getElementById("partner").innerHTML == "Secure Messenger") {
        sendSecureMessenger("running");
        $("#comment").val("");
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
    $("#comment").focus();

}

//gets all messages from our service and updates them every second.
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

//sorts all sent and given messages after date.
function sortMessages() {
    chatMessages = sentMessages.concat(messages);
    chatMessages.sort(function (a, b) {
        return (a.date < b.date) ? -1 : ((a.date > b.date) ? 1 : 0);
    });
}

//this realizes the currentChats tab where you see all received messages but only the newest one per partner.
function checkCurrentChat(partner, chatName) {
    var isCurrent = false;
    $.each(currenChatPartner, function (index, value) {
        if (chatName == value) {
            isCurrent = true;
        }
    })
    if (isCurrent) {
        $("#" + chatName + "-messagePrev").html(partner.from + ": " + partner.text.substr(0, 10) + "...");
        $("#" + chatName + "-messagePrevDate").html(partner.date.substr(11, 5));
    } else {
        $(".sideBar").append("<div class='row sideBar-body' ><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='" + chatName + "'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>" + chatName + "<br/><p class='messagePrev' id='" + chatName + "-messagePrev'>" + partner.from + ": " + partner.text.substr(0, 10) + "...</p>" + "</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right' id='" + partner.from + "-messagePrevDate'>" + partner.date.substr(11, 5) + "</span></div></div></div></div>");
        $("#" + chatName).click(function () {
            openChat($(this).attr('id'));
            $(".side-two").css({
                "left": "-100%"
            });
        });
        currenChatPartner.push(chatName);
    }
}

//displays all messages in the correct field with the correct values.
function showMessages() {
    sortMessages();
    $("#conversation").empty();
    $.each(chatMessages, function (index, value) {
        if (pseudonym == value.from) {
            checkCurrentChat(value, value.to);
        }
        if (pseudonym == value.to) {
            checkCurrentChat(value, value.from);
        }
        if (value.from == partner && value.to == pseudonym) {
            $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>" + value.text + "</div><span class='message-time pull-right'>" + value.date.substr(11, 5) + "</span></div></div></div></div>");
        }
        if (value.from == pseudonym && value.to == partner) {
            $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-sender'><div class='sender'><div class='message-text' id='messages'>" + value.text + "</div><span class='message-time pull-right'>" + value.date.substr(11, 5) + "</span></div></div></div></div>");
        }
    });
    document.getElementById("conversation").scrollTop = document.getElementById("conversation").scrollHeight;
}

//reads our cookie and saves all components into the correct variables.
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

//our small chatbot, more functions are coming soon.
function sendSecureMessenger(zustand) {
    if (zustand == "running") {
        var command = $("#comment").val();
        if (command == "!hilfe") {
            $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>Klicke auf eine empfangene Nachricht um auf diese zu antworten, oder füge über das Kontakt symbol einen Neuen Kontakt hinzu und starte durch drücken auf diesen Kontakt einen chat mit dieser Person.</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");
        }
        if (command == "!zeit") {
            var datum = new Date();
            var ausgabeDatum= "Heute ist "+datum.getDay+" der "+datum.getDate+" "+datum.getMonth+" "+datum.getYear+" und wir haben "+datum.getHours+":"+datum.getMinutes+" uhr."
            $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>Datum: " + ausgabeDatum + "</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");
        }
        if(command==)
    }
    if (zustand == "start") {
        $("#conversation").empty();
        $("#receiver-picture").attr("src", "img/chatbot.png");
        $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='receiver'><div class='message-text' id='messages'>Hey willkommen beim Secure Messenger, wähle eine der folgenen Optionen:\n!hilfe\n!zeit</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");
    }
    window.setTimeout(function () {
        $("#comment").focus();
    }, 0);
}




