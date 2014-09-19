function CssLoader() {

	this.load = load;
	function load() {
		var bD = new BrowserDetector();
		
		if (bD.isIE()) {
			this.cssLoad("styles/ieFix.css");
		}
		
	}
	
	this.cssLoad = cssLoad;
	function cssLoad(fileName) {
		fileRef=document.createElement("link")
		fileRef.setAttribute("rel", "stylesheet");
		fileRef.setAttribute("type", "text/css");
		fileRef.setAttribute("href", fileName);
		document.getElementsByTagName("head").item(0).appendChild(fileRef);
	}

}




