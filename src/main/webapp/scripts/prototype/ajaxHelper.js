 function invoke(form, event, container, asynch) {
 	if (asynch == null)
 		asynch = true;
    var params = Form.serialize(form);
    if (event != null) params = event + '&' + params;
    new Ajax.Updater(container, form.action, {method:'post', postBody:params, asynchronous:asynch});
 }
 