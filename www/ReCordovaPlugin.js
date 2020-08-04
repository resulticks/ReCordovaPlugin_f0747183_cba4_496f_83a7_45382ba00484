var exec = require('cordova/exec');

module.exports.onDeviceReady = function (arg0, success, error) {
	ReCordovaPlugin.getFieldTrackData(arg0, onEnableFieldTrack);
	ReCordovaPlugin.screenNavigation(arg0);
};

module.exports.userRegister = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'userRegister', [arg0]);
};

module.exports.customEvent = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'customEvent', [arg0]);
};

module.exports.locationUpdate = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'locationUpdate', [arg0]);
};

module.exports.screenNavigation = function (arg0, success, error) {
	ReCordovaPlugin.getFieldTrackData(arg0, onEnableFieldTrack);
	exec(success, error, 'ReCordovaPlugin', 'screenNavigation', [arg0]);
};

module.exports.getNotification = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'getNotification', [arg0]);
};

module.exports.deleteNotification = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'deleteNotification', [arg0]);
};

module.exports.updateViewsJson = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'updateViewsJson', [arg0]);
};

module.exports.updateFieldTrackData = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'updateFieldTrackData', [arg0]);
};

module.exports.getFieldTrackData = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'getFieldTrackData', [arg0]);
};

module.exports.notificationPayLoadReceiver = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'notificationPayLoadReceiver', [arg0]);
};


module.exports.deleteNotificationByNotificationId = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'deleteNotificationByNotificationId', [arg0]);
};
module.exports.deleteNotificationByCampaignId = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'deleteNotificationByCampaignId', [arg0]);
};
module.exports.getReadNotificationCount = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'getReadNotificationCount', [arg0]);
};
module.exports.getUnReadNotificationCount = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'getUnReadNotificationCount', [arg0]);
};
module.exports.readNotification = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'readNotification', [arg0]);
};

module.exports.unReadNotification = function (arg0, success, error) {
	exec(success, error, 'ReCordovaPlugin', 'unReadNotification', [arg0]);
};





module.exports.getViewJson = function (arg0, success, error) {
	getViewJsons();
};

// Capture Screen Tree
var fieldNeedToTrack = [];
var fieldTracked = [];

function getViewJsons() {
	try {
		var viewJson = [];
		$('input,button,select,textarea').each(function () {
			console.log($(this).attr('id'));
			var subdiv = new Object();
			getViewAttributes(subdiv, viewJson, $(this));
		});

		$('embed,iframe').each(function () {
			var subdiv = new Object();
			if ($(this).attr('src') != null && $(this).attr('src') != '') {
				subdiv['tagurl'] = $(this).prop('src');
				getViewAttributes(subdiv, viewJson, $(this));
			}
		});

		$('video,audio').each(function () {
			var subdiv = new Object();
			if ($(this).attr('src') != null && $(this).attr('src') != '') getViewAttributes(subdiv, viewJson, $(this));
		});

		$('object').each(function () {
			var subdiv = new Object();
			if ($(this).attr('data') != null && $(this).attr('data') != '') {
				subdiv['tagurl'] = $(this).prop('data');
				getViewAttributes(subdiv, viewJson, $(this));
			}
		});

		$('a').each(function () {
			var subdiv = new Object();
			getViewAttributes(subdiv, viewJson, $(this));
		});

		var screenViewJson = {
			views: viewJson,
			screenName: window.location.pathname,
			scrollX: window.pageXOffset,
			scrollY: window.pageYOffset
		};

		ReCordovaPlugin.updateViewsJson(screenViewJson);

		return JSON.stringify(viewJson);
	} catch (e) {
		console.log('getViewJson : ' + e);
	}
	return '';
}

function getViewAttributes(subdiv, viewJson, object) {
	try {
		var position = object.offset();
		subdiv['left'] = position.left;
		subdiv['top'] = position.top;
		subdiv['height'] = object.outerHeight();
		subdiv['width'] = object.outerWidth();
		subdiv['id'] = Math.floor(100000 + Math.random() * 900000);
		subdiv['isShow'] = true;
		subdiv['translationX'] = 0;
		subdiv['translationY'] = 0;
		subdiv['scrollX'] = 0;
		subdiv['scrollY'] = 0;
		subdiv['isWebView'] = false;
		subdiv['screenName'] = window.location.pathname;
		subdiv['category'] = object.prop('type');
		subdiv['tagtype'] = object.attr('tagtype');
		subdiv['tagname'] = object.attr('tagname');
		subdiv['href'] = object.prop('href');
		subdiv['subviews'] = '[]';

		if (object.prop('type') == 'password') {
			subdiv['isShow'] = false;
			subdiv['viewType'] = 'Value';
		} else if (object.prop('type') == 'button' || object.prop('type') == 'submit') {
			subdiv['viewType'] = 'Others';
		} else {
			subdiv['viewType'] = 'Value';
		}

		if (object.attr('id') != '' && object.attr('id') != undefined) {
			var id = object.attr('id');
			subdiv['viewId'] = 'id___' + id;
		} else if (object.attr('name') != '' && object.attr('name') != undefined) {
			var id = object.attr('name');
			subdiv['viewId'] = 'name___' + id;
		} else {
			subdiv['viewId'] = 'resulticks_id_' + Math.floor(100000 + Math.random() * 900000);
			subdiv['isShow'] = false;
		}
		viewJson.push(subdiv);
	} catch (e) {
		console.log('getViewAttributes : ' + e);
	}
}

function getIdentifier(Fields) {
	try {
		var id = Fields.identifier;
		if (id != undefined) {
			if (id.includes('id___')) {
				id = id.replace('id___', '');
				return '#' + id;
			} else if (id.includes('name___')) {
				id = id.replace('name___', '');
				return '[name="' + id + '"]';
			}
		}
	} catch (e) {
		console.log('getIdentifier : ' + e);
	}
}

function onEnableFieldTrack(data) {
	try {
		var fieldTrackingData = JSON.parse(data);
		fieldNeedToTrack = fieldTrackingData;
		if (fieldTrackingData != undefined) {
			fieldTrackingData.forEach(function (field) {
				// Value capture
				if (field.captureType == 'Value' || field.captureType == 'Length') {
					var id = getIdentifier(field);
					console.log('Change Event Name :' + id);
					if (id != undefined && id != null) {
						$(id).on('change', function (e) {
							ViewTrackingListener(field, $(this).val());
						});
					}
				} else {
					// Click Actions
					if (field.identifier != undefined && field.identifier != null) {
						if (field.identifier.includes('id___')) {
							var viewId = getIdentifier(field);
							let id = field.identifier.replace('id___', '');
							var onclickattr = document.getElementById(id).getAttribute('onclick');
							$(viewId).removeAttr('onclick');
							$(viewId).on(field.captureType.toLowerCase(), function (e) {
								ViewTrackingListener(field, 'Clicked');
								if (onclickattr != null) {
									$(viewId).attr('onClick', onclickattr);
									$(viewId).trigger('onclick');
								}
							});
						} else if (field.identifier.includes('name___')) {
							var viewName = getIdentifier(field);
							var onclickattr = $(viewName).getAttribute('onclick');
							$(viewName).removeAttr('onclick');
							$(viewName).on(field.captureType.toLowerCase(), function (e) {
								ViewTrackingListener(field, 'Clicked');
							});
							if (onclickattr != null) {
								$(viewName).attr('onClick', onclickattr);
								$(viewName).trigger('onclick');
							}
						}
					}
				}
			});
		}
	} catch (e) {
		console.log('onEnableFieldTrack : ' + e);
	}
}

function ViewTrackingListener(field, value) {
	try {
		if (field.identifier != undefined) {
			if (fieldNeedToTrack.length > 0) {
				fieldNeedToTrack.forEach(function (element) {
					if (element != undefined && field.identifier != undefined) {
						if (element.identifier == field.identifier) {
							console.log(' element viewID ' + element.viewID + 'field.viewId : ' + field.viewId);
							if (element.captureType == 'Length') element['result'] = value.length;
							else element['result'] = value;
						}
					}
				});
			}

			// Filtered tracking data
			data = [];
			if (fieldNeedToTrack.length > 0) {
				fieldNeedToTrack.forEach(function (element) {
					if (element.result != undefined) {
						data.push(element);
					}
				});
			}
			// Update to native
			var trackingData = {
				fieldTrack: data
			};

			ReCordovaPlugin.updateFieldTrackData(trackingData);
		}
	} catch (e) {
		console.log('ViewTrackingListener : ' + e);
	}
}