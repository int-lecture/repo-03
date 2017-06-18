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
	var URL = ipLogin + "/register";
	$.ajax({
		url: URL,
		type: 'OPTIONS',
		success: function (result) {
		},
		error: function (xhr, ajaxOptions, thrownError) {
		}
	});
}

//checks if both passwords are the same then trys to register the user.
function checkPw() {
	var pw1 = $("#pw1").val();
	var pw2 = $("#pw2").val();
	if (pw1 != pw2) {
		$("#error").html("Du böser bube die Passwörter sind nicht Identisch!");
		return false;
	}
	var URL = ipRegister + "/register";
	var dataObject = { 'pseudonym': $("#inputPseudonym").val(), 'user': $("#inputEmail").val(), 'password': pw1 };
	$.ajax({
		url: URL,
		type: 'PUT',
		data: JSON.stringify(dataObject),
		contentType: "application/json; charset=utf-8",
		dataType: 'json',
		success: function (result) {
				window.location.href = "loginApplication.html";
		},
		error: function (xhr, ajaxOptions, thrownError) {
		}
	});
	return false;
}





