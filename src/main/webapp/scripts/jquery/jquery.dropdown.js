
$j(document).ready(function(){
	$j("ul.dropdown li").hover(function(){
	$j(this).addClass("hover");
	$j('> .dir',this).addClass("open");
	$j('ul:first',this).css('visibility', 'visible');
 },function(){
	 $j(this).removeClass("hover");
	 $j('.open',this).removeClass("open");
	 $j('ul:first',this).css('visibility', 'hidden');
 });

});