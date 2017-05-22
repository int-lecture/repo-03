$( document ).ready(function() {   


});

function checkPw(){
	var a= $("#pw1");
	var pw1= $("#pw1").val();
	var pw2= $("#pw2").val();

	if (pw1 != pw2){
		$("#error").html("Du böser bube die Passwörter sind nicht Identisch!");
		return false;}
	return true;

}

