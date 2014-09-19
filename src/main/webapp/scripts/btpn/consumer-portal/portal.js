/*
 * This the js file for bank portal application. All the javascript code can be placed here.
 * 
 * @author Vikram Gunda
 */
var headerTimer = null;
var resetSessionTimer = null;
var sessionTimer = null;

/*
 * Sets up the current timer display for the logged in user
 * 
 */
function setupTimerDisplay() {
	var timeDisplay = document.getElementById("timestamp");
	if (!timeDisplay) {
		return;
	}

	if (!headerTimer) {
		displayTimer();
		headerTimer = window.setInterval(displayTimer, 1000);
	}
}

/*
 * Timer logic for the logged in user.
 * 
 */
function displayTimer() {
	var timeDisplay = document.getElementById("timestamp");

	if (!timeDisplay) {
		return;
	}

	var months = new Array("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
			"Aug", "Sep", "Oct", "Nov", "Dec");
	var curts = new Date();

	var normalTime = curts.toString();
	normalTime = normalTime.replace(new RegExp(/.*\(/), "");
	normalTime = normalTime.replace(new RegExp(/\).*/), "");

	var hr = curts.getHours();
	if (hr < 10) {
		hr = "0" + hr;
	}

	var min = curts.getMinutes();
	if (min < 10) {
		min = "0" + min;
	}

	var sec = curts.getSeconds();
	if (sec < 10) {
		sec = "0" + sec;
	}
	var timeString = curts.getDate() + " " + months[curts.getMonth()] + " "
			+ curts.getFullYear() + " - " + hr + ":" + min + ":" + sec;

	timeDisplay.innerHTML = timeString;
}

/*
 * Prints and sets the session timeout
 * 
 */
function sessionTimoutPrint() {
	var min = '' + parseInt(sessionTimoutSeconds / 60);
	var sec = '' + sessionTimoutSeconds % 60;
	if (sec.length == 1)
		sec = 0 + '' + sec;
	if (sessionTimoutSeconds > 0)
		document.getElementById("session.timeout.counter").innerHTML = document
				.getElementById("session.timeout.counter.label").innerHTML
				+ +min
				+ ":"
				+ sec
				+ document
						.getElementById("session.timeout.counter.minutes.label").innerHTML;
	else
		document.getElementById("session.timeout.counter").innerHTML = document
				.getElementById("session.timeout.label").innerHTML;
}

/*
 * Displays the session timeout
 * 
 */
function sessionTimoutDisplay() {
	if (sessionTimoutSeconds > 0) {
		sessionTimoutSeconds -= 1;
	}
	sessionTimoutPrint();
	if (resetSessionTimer) {
		clearTimeout(sessionTimer);
		sessionTimoutSeconds = document
				.getElementById("session.timeout.seconds").innerHTML;
		resetSessionTimer = null;
	}
	sessionTimer = setTimeout("sessionTimoutDisplay()", 1000);
}

/*
 * Fetches the selected menu name to display
 * 
 */
function getSelectedMenuName() {
	var ul = document.getElementById("leftMenuList");
	var liArray = ul.getElementsByTagName("li");
	for ( var i = 0; i < liArray.length; i++) {
		if (liArray[i].getAttribute("class") == "selected") {
			var anchor = liArray[i].getElementsByTagName("a");
			document.getElementById("selectedMenuName").innerHTML = anchor[0].innerHTML;
			break;
		}
	}
}