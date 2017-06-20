//adds a new contact using the inputfield.
function addContact() {
    newContact($("#newFriendsName").val());
    $("#newFriendsName").val("");
}

//adds a new contact to the contactlist.
function newContact(newChatPartner) {
    var URL = ipRegister + "/addcontact";
    var dataObject = { 'pseudonym': pseudonym, 'token': token, 'newContact': newChatPartner };
    $.ajax({
        url: URL,
        type: 'PUT',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            loadContacts();
        },
        error: function (xhr, a, b) {
            if (xhr.status == 200) {
                loadContacts();
            }
        }
    });
}

//gets all contacts from our service.
function loadContacts() {
    var URL = ipRegister + "/profile";
    var dataObject = { 'getownprofile': pseudonym, 'token': token };
    $.ajax({
        url: URL,
        type: 'POST',
        data: JSON.stringify(dataObject),
        contentType: "application/json; charset=utf-8",
        dataType: 'json',
        success: function (result) {
            contact = result.contact;
            showContacts();
        },
        error: function (xhr, a, b) {
            alert("Kontakte nicht geladen fehler: " + xhr.status);
        }
    });
}

//displays all contacts with the right values.
function showContacts() {
    $(".compose-sideBar").empty();
    contact[0].sort(function (a, b) {
        return (a < b) ? -1 : ((a > b) ? 1 : 0);
    });
    $.each(contact[0], function (index, value) {
        $(".compose-sideBar").append("<div class='row sideBar-body' ><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='img/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='" + value + "'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>" + value + "</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'></span></div></div></div></div>");
        $("#" + value).click(function () {
            openChat($(this).attr('id'));
            $(".side-two").css({
                "left": "-100%"
            });
        });
    });
}