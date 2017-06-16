function newContact(newChatPartner) {
    var isAlreadyFriend = false;
    $.each(contact, function (index, value) {
        if (value == newChatPartner) {
            isAlreadyFriend=true;
        }
    });
    if (isAlreadyFriend == false) {
        contact.push(newChatPartner);
    }
    loadContacts();
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
            //contact = contact.concat(result.contact);
        },
        error: function (xhr, a, b) {
            //alert("Kontakte wurden nicht erfolgreich geladen");
        }

    });
    $(".sideBar").empty();
    $.each(contact, function (index, value) {
        $(".sideBar").append("<div class='row sideBar-body' ><div class='col-sm-3 col-xs-3 sideBar-avatar'><div class='avatar-icon'><img src='css/profilePic.png'></div></div><div class='col-sm-9 col-xs-9 sideBar-main' id='" + value + "'><div class='row'><div class='col-sm-8 col-xs-8 sideBar-name'><span class='name-meta' id='contacts'>" + value + "</span></div><div class='col-sm-4 col-xs-4 pull-right sideBar-time'><span class='time-meta pull-right'>18:18</span></div></div></div></div>");
        $("#" + value).click(function () {
            openChat($(this).attr('id'));
        });
    });
}