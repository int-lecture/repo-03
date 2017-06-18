$(document).ready(function () {
	loadConfig();
	startConnection();
});
$(document).keypress(function (e) {
	if (e.which == 13) {
		checkPw();
	}
})

//starts the connection to our server, cause otherwise the user needs to wait after pressing login.
function startConnection() {
	var URL = ipLogin + "/login";
	$.ajax({
		url: URL,
		type: 'OPTIONS',
		success: function (result) {
		},
		error: function (xhr, ajaxOptions, thrownError) {
		}
	});
}

//checks if the password is correct then logs the user in.
function checkPw() {
	var URL = ipLogin + "/login";
	var dataObject = { 'user': $("#inputEmail").val(), 'password': $("#inputPassword").val() };
	$.ajax({
		url: URL,
		type: 'POST',
		data: JSON.stringify(dataObject),
		contentType: "application/json; charset=utf-8",
		dataType: 'json',
		success: function (result) {
			document.cookie = "token=" + result.token;
			document.cookie = "pseudonym=" + result.pseudonym + ";expires=" + result["expire-date"];
			window.location.href = "chatApplication.html";
		},
		error: function (xhr, ajaxOptions, thrownError) {
			if(xhr.status==401){
				$("#error").html("Das Passwort ist nicht korrekt");
			}
		}
	});
	return false;

}

