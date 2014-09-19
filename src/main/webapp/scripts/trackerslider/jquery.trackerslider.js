/*
 * Tracker Panel Slider Plugin for jQuery
 *
 * Author: Mark White
 *
 * (c) 2012, Sybase, Inc. an SAP Company
 *
 * Example HTML for default usage;
 *
 *  $("#trackerPanel").trackerPanelSlider():
 *	
 *  <div id="trackerPanel">
 *    <ul>
 *      <li> [Panel 1 Contents] </li>
 *      <li> [Panel 2 Contents] </li>
 *      <li> [Panel 3 Contents] </li>
 *      <li> [Panel 4 Contents] </li>
 *      <li> [Panel 5 Contents] </li>
 *    </ul>
 *  </div>
 */

(function($) {

	$.fn.trackerPanelSlider = function(options){
	  
		// default properties
		var defaults = {			
			panelWidth: 	1000,			// width of inner panel - note: must match css for size of inner panel
			prevBtnId: 	'prevBtn',		// css element id of previous button
			prevBtnTxt: 	'Previous',		// text to assign to tooltip for previous button
			nextBtnId: 	'nextBtn',		// css element id of next button
			nextBtnTxt: 	'Next',			// text to assign to tooltip for next button
			speed: 		400,			// ms default speed of panel slide animate
			showPanelLinks: false,			// show the direct links to panels or not
			showButtons:	true,			// show the prev/next buttons or not
			panelLinksId: 	'panelLink',		// css element id suffix for each generated panel link
			panelLinkClass: 'dot mini',		// css style class for each generated panel link
			direction:	'auto'			// page direction (values: ltr, rtl, auto - determine from html attribute <html dir="xyz"> )
		}; 
		
		var options = $.extend(defaults, options);  

		// set page direction automatically based on page setting, if necessary
		if (options.direction == "auto") {
			var htmlDirection = $("html").attr("dir");
			if (htmlDirection != "undefined") {
				options.direction = htmlDirection;
			}
		}
		
		// iterate over each match		
		this.each(function() {  
		
			// handy reference to $(this)
			var thisSlider = $(this); 				
			thisSlider.css("overflow","hidden");

			var listLength = $("li", thisSlider).length;
			var totalPanels = listLength - 1;

			var disableChange = true;
			var currentPanelNo = 0;

			$("ul", thisSlider).css('width', listLength * options.panelWidth);
			if (options.direction == 'rtl') {
				$("li", thisSlider).css('float', 'right');
			}
			else {
				$("li", thisSlider).css('float', 'left');
			}
										
			var html = "";				
			
			// generate a panel link container links
			if (options.showPanelLinks){
				// move 'dot' links into centre of panel area
				var leftMargin = Math.floor((options.panelWidth / 2) - ((totalPanels * 12) / 2));
				html += '<ol id="'+ options.panelLinksId +'" style="margin-left: ' + leftMargin + 'px;"></ol>';
			}

			// create the prev/next button, location base on styling
			if (options.showButtons) {	
				html += ' <span id="'+ options.prevBtnId +'"><a href=\"#\">'+ options.prevBtnTxt +'</a></span>';
				html += ' <span id="'+ options.nextBtnId +'"><a href=\"#\">'+ options.nextBtnTxt +'</a></span>';
			}				

			// add the generated panel links container and buttons to the document			
			$(thisSlider).after(html);
			
			// now generate a 'dot' link for each panel in the list
			if (options.showPanelLinks) {									
			
				for(var i=0; i < listLength; i++){						

					// add the panel link to the panel link container				
					$(document.createElement("li"))
						.attr("id", options.panelLinksId + (i+1))
						.attr("class", "")
						.html("<a rel=" + i + " href=\"#\"><div class=\"" + options.panelLinkClass + "\"></div></a>")
						.appendTo($("#" + options.panelLinksId))
						.click(function(){							
							changePanel($("a",$(this)).attr('rel'));
						}); 												
						
				};							
			}
			
			// add click event handlers to next/prev buttons
			if (options.showButtons) {
				$("a", "#" + options.nextBtnId).click(function(){
					if (!$(this).hasClass("disabled")) {
						changePanel("n");
					}
				});
				$("a", "#" + options.prevBtnId).click(function(){
					if (!$(this).hasClass("disabled")) {
						changePanel("p");
					}
				});
			};
			
			// set the panel link 'dot' for showing panel
			function setCurrentPanelNo(i) {
				i = parseInt(i) + 1;				
				$("li a div", "#" + options.panelLinksId).removeClass("showing");				
				$("#" + options.panelLinksId + i + " a div").addClass("showing");
			};
			
			// Function called once the jquery animation is complete
			function onComplete() {
				// reset if overflowed
				if (currentPanelNo > totalPanels) {
					currentPanelNo = 0;
				}				
				if (currentPanelNo < 0) {
					currentPanelNo = totalPanels;				
				}

				// move margin to showing panel				
				if (options.direction == "rtl" ) {
					$("ul", thisSlider).css("margin-right", (currentPanelNo * options.panelWidth * -1));
				}
				else {
					$("ul", thisSlider).css("margin-left", (currentPanelNo * options.panelWidth * -1));
				}
				
				disableChange = true;
				
				if (options.showPanelLinks) {
					setCurrentPanelNo(currentPanelNo);
				}
				
				// change class of previous next if we've got to the end - wrap around is not supported					
				if (options.showButtons && currentPanelNo == totalPanels) {
					$("a", "#" + options.nextBtnId).addClass("disabled");
				}
				else {
					$("a", "#" + options.nextBtnId).removeClass("disabled");
				};

				// change class of previous button if we've got to the end - wrap around is not supported					
				if (options.showButtons && currentPanelNo == 0) {
					$("a", "#" + options.prevBtnId).addClass("disabled");
				}
				else {
					$("a", "#" + options.prevBtnId).removeClass("disabled");
				};	
			};

			// main panel view changer - called by onClick event handlers of controls
			function changePanel(direction){

				// disable while changing panel
				if (disableChange) {
				
					disableChange = false;
					
					var lastPanelNo = currentPanelNo;

					// either a direction; n = next, p = previous or a specific page number					
					switch (direction){
						case "n":
							currentPanelNo = (lastPanelNo >= totalPanels) ? totalPanels : currentPanelNo + 1;
							break; 
						case "p":
							currentPanelNo = (currentPanelNo <= 0) ? 0 : currentPanelNo - 1;
							break; 
						default:
							// treat direction as a numeric page number
							currentPanelNo = direction;
							break; 
					};	

					// set animate speed faster if jumping > 1 page
					var diff = Math.abs(lastPanelNo - currentPanelNo);
					var speed = diff * options.speed;

					// do the sliding animation...
					var slideToMargin = (currentPanelNo * options.panelWidth * -1);
					if (options.direction == 'rtl') {
						$("ul",thisSlider).animate(
							{ marginRight: slideToMargin }, 
							{ queue: false, duration: speed, complete: onComplete }
						);				
					} 
					else {
						$("ul",thisSlider).animate(
							{ marginLeft: slideToMargin }, 
							{ queue: false, duration: speed, complete: onComplete }
						);				
					}
					
				};
				
			};	

			// init slider to panel 0 view state
			$("a", "#" + options.prevBtnId).addClass("disabled");
						
			if (options.showPanelLinks) {
				setCurrentPanelNo(0);
			}

			if (options.showButtons && currentPanelNo == totalPanels) {
				$("a", "#" + options.nextBtnId).addClass("disabled");
			}
			
		});
	  
	};

})(jQuery);



