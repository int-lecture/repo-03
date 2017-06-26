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
            e.preventDefault();
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
var zitate = ["Nichts ist schrecklicher als ein Lehrer, der nicht mehr weiß als das, was die Schüler wissen sollen. </br>Johann Wolgang von Goethe", "Das ärgerliche am Ärger ist, dass man sich schadet, ohne anderen zu nützen. </br> Kurt Tucholsky", "Wer immer tut, was er schon kann, bleibt immer das, was er schon ist.</br>Henry Ford", "Wer nicht kann, was er will, muss wollen, was er kann. Denn das zu wollen, was er nicht kann, wäre töricht.</br>Leonardo da Vinci", "Gib jedem Tag die Chance, der schönste deines Lebens zu werden. </br>Mark Twain", "Wenn du die Absicht hast, dich zu erneuern, tu es jeden Tag. </br>Konfuzius", "Sei du selbst die Veränderung, die du dir wünschst für diese Welt.</br>Mahatma Ghandi", "Der Schwache kann nicht verzeihen. Verzeihen ist eine Eigenschaft des Starken. </br>Mahatma Ghandi", "Halte dich fern von denjenigen, die versuchen, deinen Ehrgeiz herabzusetzen. Kleingeister tun das immer, aber die wirklich Großen geben dir das Gefühl, dass auch du selbst groß werden kannst.</br>Mark Twain", "Suche nicht nach Fehlern, suche nach Lösungen.</br>Henry Ford", "Mitleid bekommt man geschenkt, Neid muss man sich verdienen.</br>Robert Lembke", "Es ist besser, sich mit Leuten abzugeben, die besser sind als man selber. Wähle dir einen Kompagnon, dessen Verhalten besser ist als deines und du wirst dich ihm anpassen. </br>Warren Buffett", "Habe keine Angst, das Gute aufzugeben, um das Großartige zu erreichen.</br>John D. Rockefeller", "Menschen mit einer neuen Idee gelten so lange als Spinner, bis sich die Sache durchgesetzt hat.</br>Mark Twain", "Zuerst ignorieren sie dich, dann lachen sie über dich, dann bekämpfen sie dich und dann gewinnst du. </br>Mahatma Ghandi", "Man muss das Unmögliche versuchen, um das Mögliche zu erreichen. </br>Hermann Hesse", "Du musst bereit sein die Dinge zu tun, die andere niemals tun werden, um die Dinge zu haben, die andere niemals haben werden.</br>Les Brown", "Einfach machen. </br>Alexander Pavel", "Es ist sinnlos zu sagen: Wir tun unser Bestes. Es muss dir gelingen, das zu tun, was erforderlich ist.</br>Winston Churchill", "Die Kunst ist, einmal mehr aufzustehen, als man umgeworfen wird.</br>Winston Churchill", "Wer einen Fehler gemacht hat und ihn nicht korrigiert, begeht einen zweiten.</br>Konfuzius"];
var trumpZitate = ["Ein Mann wurde in eine Pariser Polizeistation erschossen. Gerade wurde die höchste Terrorstufe ausgerufen. Deutschland ist ein großes Verbrechens-Chaos. Werdet schlauer!", "Es schneit und friert in New York. Wir brauchen globale Erwärmung", "Hindert Ebola-Patienten daran in die USA zu kommen. Behandelt sie, auf dem höchsten Niveau, woanders. Die Vereinigten Staaten haben genug Probleme.", "Ich werde eine große Mauer bauen - und niemand baut Mauern besser als ich, glauben Sie mir - und ich baue sie sehr kostengünstig. Ich werde eine große, große Mauer an unserer südlichen Grenze bauen und ich werde Mexiko für diese Mauer bezahlen lassen.", "Er ist kein Kriegsheld. Er war ein Kriegsheld, weil er gefangen genommen wurde. Ich mag Leute, die nicht gefangen genommen wurden.", "Wenn Ivanka nicht meine Tochter wäre, würde ich sie wahrscheinlich daten.", "Die Globale Erwärmung wurde von und für die Chinesen erfunden, um die US-Produktion wettbewerbsunfähig zu machen.", "Wenn Mexiko seine Leute schickt, dann schicken sie nicht die Besten. Sie schicken Leute mit vielen Problemen und die bringen diese Probleme zu uns. Sie bringen Drogen. Sie bringen Verbrechen. Sie sind Vergewaltiger... und manche, nehme ich an, sind gute Menschen.", "Belgien ist eine wunderschöne Stadt und ein herrlicher Ort - großartige Gebäude. Ich war mal dort, vor vielen, vielen Jahren.", ""];

//opens the chat with the given partner, loads the messages and shows them.
function openChat(partner) {
    if (partner == "Secure Messenger") {
        sendSecureMessenger("start");
        this.partner = partner;
        document.getElementById("partner").innerHTML = partner;
    } else {
        $("#receiver-picture").attr("src", "img/profilePic.png");
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
    if ($("#comment").val().charAt(0) == "!") {
        sendSecureMessenger("running");
        $("#comment").val("");
        document.getElementById("conversation").scrollTop = document.getElementById("conversation").scrollHeight;
    }
    else if (partner == "Secure Messenger") {
        $("#comment").val("");
    }
    else if ($("#comment").val() == "") {
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
                "Authorization": " Token " + token
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
        var dateA = moment(a.date);
        var dateB = moment(b.date);
        return (dateA.isBefore(dateB)) ? -1 : (dateA.isAfter(dateB) ? 1 : 0);
    });
}

//this realizes the currentChats tab where you see all received messages but only the newest one per partner.
function checkCurrentChat(partner, chatName) {
    var isCurrent = false;
    $.each(currenChatPartner, function (index, value) {
        if (chatName == value) {
            isCurrent = true;
            return true;
        }
    })
    if (isCurrent) {
        $("#" + chatName + "-messagePrev").html(partner.from + ": " + partner.text.substr(0, 10) + "...");
        $("#" + chatName + "-messagePrevDate").html(partner.date.substr(11, 5));
    } else {
        $(".sideBar").append("<div class='row sideBar-body' ><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='img/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='" + chatName + "'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>" + chatName + "<br/><p class='messagePrev' id='" + chatName + "-messagePrev'>" + partner.from + ": " + partner.text.substr(0, 10) + "...</p>" + "</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right' id='" + partner.from + "-messagePrevDate'>" + partner.date.substr(11, 5) + "</span></div></div></div></div>");
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
    if (partner != "Secure Messenger") {
        var conv = $("#conversation");
        conv.empty();
    }
    $.each(chatMessages, function (index, value) {
        if (pseudonym == value.from) {
            checkCurrentChat(value, value.to);
        }
        else if (pseudonym == value.to) {
            checkCurrentChat(value, value.from);
        } else {
            // continue
            return true;
        }

        var date = moment(value.date);
        var msgBody = $("<div class='row message-body'></div>");
        var msgText = $("<span></span>");
        msgText.text(value.text);
        var msgTime = $("<span class='message-time pull-right'></span>");
        msgTime.text(date.format("HH:mm"));
        var msgs = $("<div class='message-text' id='messages'></div>");
        msgs.append(msgText).append(msgTime);

        if (value.from == partner && value.to == pseudonym) {
            var mainRecv = $("<div class='col-sm-12 message-main-receiver'/>").appendTo(msgBody);
            var recv = $("<div class='receiver'/>").appendTo(mainRecv);
            recv.append(msgs)

            conv.append(msgBody);
        }
        if (value.from == pseudonym && value.to == partner) {
            var mainSend = $("<div class='col-sm-12 message-main-sender'/>").appendTo(msgBody);
            var send = $("<div class='sender'/>").appendTo(mainSend);
            send.append(msgs)

            conv.append(msgBody);
        }
    });

    conv.scrollTop = conv.scrollHeight;
}

//reads our cookie and saves all components into the correct variables.
function readCookie() {
    var decodedCookie = decodeURIComponent(document.cookie);
    var ca = decodedCookie.split(';');
    return $.each(ca, function (index, value) {
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
            var ausgabeHilfe = "Klicke auf eine empfangene Nachricht um auf diese zu antworten, oder füge über das Kontakt symbol einen Neuen Kontakt hinzu und starte durch drücken auf diesen Kontakt einen chat mit dieser Person.";
            getMessageSecureMessenger(ausgabeHilfe);
        }
        if (command == "!zeit") {
            var datum = new Date();
            var days = ["Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"];
            var months = ["Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"];
            var ausgabeDatum = "Heute ist " + days[datum.getDay()] + " der " + datum.getDate() + " " + months[datum.getMonth()] + " 2017 und wir haben " + datum.getHours() + ":" + datum.getMinutes() + " uhr."
            getMessageSecureMessenger(ausgabeDatum);
        }
        if (command == "!zitat") {
            var randomnumber = Math.floor(Math.random() * (zitate.length));
            var ausgabeZitat = zitate[randomnumber];
            getMessageSecureMessenger(ausgabeZitat);
        }
        if (command == "!trump") {
            var randomnumber = Math.floor(Math.random() * (trumpZitate.length));
            var ausgabeTrump = trumpZitate[randomnumber];
            getMessageSecureMessenger(ausgabeTrump);
        }
        if (command == "!news") {
            news();
        }
    }
    if (zustand == "start") {
        $("#conversation").empty();
        $("#receiver-picture").attr("src", "img/chatbot.png");

        $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='bot'><div class='message-text' id='messages'>Herzlich willkommen beim Secure Messenger! </br> Wähle eine der folgenen Optionen:</br>!hilfe</br>!zeit</br>!zitat</br>!trump</br>!news</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");
    }
    window.setTimeout(function () {
        $("#comment").focus();
    }, 0);
}

function getMessageSecureMessenger(ausgabe) {
    $("#conversation").append("<div class='row message-body'><div class='col-sm-12 message-main-receiver'><div class='bot'><div class='message-text' id='messages'>" + ausgabe + "</div><span class='message-time pull-right'>~42~</span></div></div></div></div>");
}

function news() {
    var ausgabeNews;
    var apiKey = "bda04a5216a04482b9f16510be950218";
    var URL = "https://newsapi.org/v1/articles?source=die-zeit&sortBy=latest&apiKey=" + apiKey;
    $.ajax({
        url: URL,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result, textStatus, xhr) {
            var randomnumber = Math.floor(Math.random() * (result.articles.length - 1 + 1));
            ausgabeNews = result.articles[randomnumber].description;
            ausgabeNews = ausgabeNews + "</br><a href='http://newsapi.org'>powered by : newsapi.org<a/>"
            getMessageSecureMessenger(ausgabeNews);
        },
        error: function (xhr, a, b) {
            alert(xhr.status);
        }
    });
}




