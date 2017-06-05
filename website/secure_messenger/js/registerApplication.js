$( document ).ready(function() {  
	
});
var ip="141.19.142.57";
function checkPw(){
	var pw1= $("#pw1").val();
	var pw2= $("#pw2").val();

	if (pw1 != pw2){
		$("#error").html("Du böser bube die Passwörter sind nicht Identisch!");
		return false;}
	
	var URL = "http://"+ip+":5002/register/";
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





