function BrowserDetector() {
	

	// ******************************************
	// * Detect Operating System
	// ******************************************
	this.isWin = isWin;
	function isWin() {
		return toBool(navigator.appVersion.indexOf( 'Win' ) != -1);
	}

	this.isLinux = isLinux;
	function isLinux() {
		return toBool(navigator.userAgent.indexOf( 'Linux' ) != -1);
	}
	
	this.isMac = isMac;
	function isMac() {
		return toBool(navigator.indexOf( 'Mac' ) != -1);
	}

	this.getOS = getOS;
	function getOS() {
		if (isWin())
			return "Windows";
		else if (isLinux())
			return "Linux";
		else if (isMac())
			return "Mac";
		else
			return "unknown";
		

		//TODO: Solaris
	}





	// ******************************************
	// * Detect Browser
	// ******************************************



	this.isIE = isIE;
	function isIE() {
		return toBool(document.all && !isOpera() && navigator.userAgent.indexOf("Microsoft Internet Explorer") && navigator.appVersion.indexOf("MSIE") );
	}

	this.isIE4 = isIE4;
	function isIE4() {
		return toBool( isIE4up() && !isIE5up() );
	}
	
	this.isIE4up = isIE4up;
	function isIE4up() {
		return toBool( document.all && isIE() );
	}

	this.isIE5 = isIE5;
	function isIE5() {
		return toBool( isIE5up() && !isIE5_5up() );
	}

	this.isIE5up = isIE5up;
	function isIE5up() {
		return toBool( window.attachEvent );
	}

	this.isIE5_5 = isIE5_5;
	function isIE5_5() {
		return toBool( isIE5_5up() && !isIE6up() );
	}

	this.isIE5_5up = isIE5_5up;
	function isIE5_5up() {
		return toBool( window.createPopup );
	}

	this.isIE6 = isIE6;
	function isIE6() {
		return toBool( isIE6up() && !isIE7() );
	}

	this.isIE6up = isIE6up;
	function isIE6up() {
		return toBool( document.compatMode && document.all );
	}

	this.isIE7 = isIE7;
	function isIE7() {
		return toBool( document.documentElement && typeof document.documentElement.style.maxHeight!="undefined" );
	}

	this.isOpera = isOpera;
	function isOpera() {
		return toBool( navigator.userAgent.indexOf( 'Opera' ) != -1 && window.opera );
	}

	this.isOpera8up = isOpera8up;
	function isOpera8up() {
		return toBool(window.getComputedStyle && !isFirefox1up());
	}

	this.isSafari = isSafari;
	function isSafari() {
		return toBool( navigator.userAgent.indexOf( 'Safari' ) && navigator.vendor.indexOf( 'Apple' ) );
	}

	this.isiCab = isiCab;
	function isiCab() {
		return toBool( navigator.vendor.indexOf( 'iCab' ) );
	}

	this.isMozilla = isMozilla;
	function isMozilla() {
		return toBool( navigator.userAgent.indexOf( 'Gecko' ) != -1 && !isSafari() && !isKonqueror() );
	}

	this.isKonqueror = isKonqueror;
	function isKonqueror() {
		return toBool( navigator.userAgent.indexOf( 'Konqueror' ) && navigator.vendor.indexOf( 'KDE' ) );
	}

	this.isFirefox = isFirefox;
	function isFirefox() {
		return toBool( isFirefox1up() && navigator.userAgent.indexOf("Firefox")  );
	}

	this.isFirefox1 = isFirefox1;
	function isFirefox1() {
		return toBool( isFirefox1up() && !isFirefox1_5up() );
	}

	this.isFirefox1up = isFirefox1up;
	function isFirefox1up() {
		return toBool( window.getComputedStyle && !isOpera() );
	}

	this.isFirefox1_5 = isFirefox1_5;
	function isFirefox1_5() {
		return toBool( isFirefox1_5up() && !isFirefox2() );
	}

	this.isFirefox1_5up = isFirefox1_5up;
	function isFirefox1_5up() {
		return toBool( Array.every );
	}

	this.isFirefox2 = isFirefox2;
	function isFirefox2() {
		return toBool( window.Iterator );
	}

	this.isCamino = isCamino;
	function isCamino() {
		return toBool(navigator.vendor.indexOf("Camino"))
	}

	this.isNetscape6up = isNetscape6up;
	function isNetscape6up() {
		return toBool(navigator.userAgent.indexOf("Netscape"))
	}

	this.isOmniWeb = isOmniWeb;
	function isOmniWeb() {
		return toBool(navigator.userAgent.indexOf("OmniWeb"))
	}
	



	this.getBrowser = getBrowser;
	function getBrowser() {
		var browser = new Browser();


		if (isIE())
			browser.name = "Microsoft Internet Explorer";
		else if (isFirefox())
			browser.name = "Firefox";
		else if (isKonqueror())
			browser.name = "Konqueror";
		else if (isCamino())
			browser.name = "Camino";


		else
			browser.name = "unknown";
		
		return browser;
	}




	//***************************************
	//*     Helpers
	//***************************************

	function toBool(expression) {
		if (expression)
			return true;
		else
			return false;
	}



	function Browser() {
		var name;
		this.getName = getName;
		function getName() {
			return this.name;
		}



	}


	function OS() {
		var name;
		this.getName = getName;
		function getName() {
			return this.name;
		}



	}



}



