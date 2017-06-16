$( document ).ready(function() {  

});
$(document).keypress(function(e){
    if(e.which==13){
        checkPw();
    }
})
var ip="141.19.142.57";
function checkPw(){
    ip="141.19.142.57";
	var URL = "http://"+ip+":5001/login/";
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

