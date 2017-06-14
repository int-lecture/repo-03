$( document ).ready(function() {  
	loadConfig();
});
$(document).keypress(function(e){
    if(e.which==13){
        checkPw();
    }
})
var ipRegister;
function checkPw(){
	var pw1= $("#pw1").val();
	var pw2= $("#pw2").val();

	if (pw1 != pw2){
		$("#error").html("Du böser bube die Passwörter sind nicht Identisch!");
		return false;}
	
	var URL = ipRegister+/register/";
	var dataObject = {'pseudonym': $("#inputPseudonym").val(), 'user': $("#inputEmail").val(), 'password': pw1 };

        alert(JSON.stringify(dataObject));

        $.ajax({
			url: URL,
			type: 'PUT',    
			data: JSON.stringify(dataObject),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success: function(result) {
				alert("success?");
				window.location.href = "loginApplication.html";
			},
			error: function(xhr, ajaxOptions, thrownError){
			alert("Error!§§!");
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
            ipRegister = ips[2].substring("ipRegister:".length + 1);
        }
    });
}





