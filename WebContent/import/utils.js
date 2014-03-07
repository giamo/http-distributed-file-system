function menu_goto(actionform) {
    var baseurl = window.location.protocol + "//" + window.location.host + "/Dfilesystem/server/servlet/";
    var selecteditem = actionform.newurl.selectedIndex;
    var label = actionform.newurl.options[selecteditem].label;
	var newurl = actionform.newurl.options[selecteditem].value;
	
    if (label=="select" || newurl.length == 0)
		return;
	else if (label == "download") {
		//alert(mostraAttesa('caricamento'));
		location.href = baseurl + newurl;
	}
	else if (label == "delete") {
		if (confirm('Are you sure you want to delete this file?'))
			location.href = baseurl + newurl;
	}
	else if (label == "rename") {
		var nome = prompt('Insert the new name');
		if (!(nome == null))
			location.href = baseurl + newurl + encodeURIComponent(nome);
	}
	else if (label == "property") {
		//window.open('server/servlet/' + newurl, 'Properties', 'width=700, height=300');
		location.href = baseurl + newurl;
	}
}

function open_property() {
	var hover = document.getElementById("hover");
	var box = document.getElementById("property-box");
	
	hover.style.display = "block";
	box.style.display = "block";
}

function close_property() {
	var hover = document.getElementById("hover");
	var box = document.getElementById("property-box");
	
	hover.style.display = "none";
	box.style.display = "none";
}
