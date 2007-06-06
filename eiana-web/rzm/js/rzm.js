function notEmpty(fieldName, value) {
    if (value == null || value == "") {
        alert("Please specify value for " + fieldName);
        return false;
    }

    return true;
}

function submitOnEnter(e) {
    var nav = window.Event ? true : false;
    var result = false;
    if (nav) {
        result = NetscapeEventHandler_KeyDown(e);
    } else {
        result = MicrosoftEventHandler_KeyDown(e);
    }

    return result;
}

function NetscapeEventHandler_KeyDown(e) {
    if (e.which == 13 && e.target.type != 'textarea' && e.target.type != 'submit') {
        e.abort = true;
        e.cancel_handlers = true;
        return false;
    }
    return true;
}

function MicrosoftEventHandler_KeyDown(e) {
    if (e.keyCode == 13 && e.srcElement.type != 'textarea' && e.srcElement.type != 'submit'){
        e.abort = true;
        e.cancel_handlers = true;
        return false;
    }

    return true;
}

function show(ele) {
    var srcElement = document.getElementById(ele);
    if(srcElement != null) {
        if(srcElement.className == "hidden") {
            srcElement.className = '';
        }
        else {
            srcElement.className ='hidden';
        }
        return false;
    }
}
