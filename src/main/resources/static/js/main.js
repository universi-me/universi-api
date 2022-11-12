
function goto_url(pathName) {
	var url = window.location.href;
	if(!url.endsWith("/")) {
	url = url + "/";
	}
	url = url + pathName;
	window.location.href = url;
}

function RemoveLastDirectoryPartOf(the_url)
{
	var the_arr = the_url.split('/');
	the_arr.pop();
	return( the_arr.join('/') );
}

function openSubUrl()
{
	window.location.href = RemoveLastDirectoryPartOf(window.location.href);
}
