$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	$("#sendModal").modal("hide");

	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
	    CONTEXT_PATH + "/SkylineForum/letter/sendLetter",
	    {"toName":toName,"content":content},
	    function(data) {
	        data = $.parseJSON(data);
	        if(data.code === 1) {
	            $("#hintBody").text(data.msg);
				$("#hintModal").modal("show");
				setTimeout(function(){
					$("#hintModal").modal("hide");
				}, 2000);
	        }
			setTimeout(function (){location.reload();},2000);
	    }
	);
}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}