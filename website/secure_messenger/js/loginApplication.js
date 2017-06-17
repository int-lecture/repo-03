$( document ).ready(function() {  
	loadConfig();
	startConnection();
});
$(document).keypress(function(e){
    if(e.which==13){
        checkPw();
    }
})

function startConnection(){
	var URL = ipLogin+"/login/";
	$.ajax({
			url: URL,
			type: 'OPTIONS',    
			success: function(result) {
			},
			error: function(xhr, ajaxOptions, thrownError){
			}
  	});
}
function checkPw(){
	var URL = ipLogin+"/login/";
	var dataObject = {'user': $("#inputEmail").val(), 'password': $("#inputPassword").val()};

        //alert(JSON.stringify(dataObject));

        $.ajax({
			url: URL,
			type: 'POST',    
			data: JSON.stringify(dataObject),
			contentType: "application/json; charset=utf-8",
			dataType: 'json',
			success: function(result) {
				document.cookie = "token="+result.token;
				document.cookie="pseudonym="+result.pseudonym+";expires="+result["expire-date"];
				//alert("success?");
				window.location.href = "chatApplication.html";
			},
			error: function(xhr, ajaxOptions, thrownError){
				//alert(" error");
			}
  	});
	return false;

}

