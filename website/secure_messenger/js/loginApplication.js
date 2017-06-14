$( document ).ready(function() {  
    loadConfig();
});
$(document).keypress(function(e){
    if(e.which==13){
        checkPw();
    }
})
var ipLogin;
function checkPw(){
	var URL = ipLogin+"/login/";
    alert(URL);
	var dataObject = {'user': $("#inputEmail").val(), 'password': $("#inputPassword").val()};

        alert(JSON.stringify(dataObject));

        $.ajax({
			url: URL,
			type: 'POST',    
			data: JSON.stringify(dataObject),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success: function(result) {
				document.cookie = "token="+result.token;
				document.cookie="pseudonym="+result.pseudonym+";expires="+result["expire-date"];
				alert("success?");
				window.location.href = "chatApplication.html";
			},
			error: function(xhr, ajaxOptions, thrownError){
				alert(" error");
			}
  	});
	return false;

}
function loadConfig() {
      $.ajax({
        url: 'js/config.txt',
        type: 'GET',
        success: function (result) {
            var ips = result.split(";");
            ipLogin = ips[0].substring("ipLogin:".length);
        }
    });
}

