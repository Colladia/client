function onClickGetButton () {
	$.ajax({
		url: "http://localhost:8182",
		method: "GET"
	})
	.done(function(data) {
		console.log("GET request --> OK");
		document.getElementById("get-zone").innerHTML = data;
	})
	.fail(function() {
		console.log("GET request --> KO");
	});
}

function onClickPostButton () {
	$.ajax({
		url: "http://localhost:8182",
		method: "POST",
		data: {value : prompt()}
	})
	.done(function(data) {
		console.log("POST request --> OK");
		document.getElementById("get-zone").innerHTML = data;
	})
	.fail(function() {
		console.log("POST request --> KO");
	});
}
