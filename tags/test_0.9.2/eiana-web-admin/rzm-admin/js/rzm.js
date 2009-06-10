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
    if (e.keyCode == 13 && e.srcElement.type != 'textarea' && e.srcElement.type != 'submit') {
        e.abort = true;
        e.cancel_handlers = true;
        return false;
    }

    return true;
}

function show(ele, checkbox) {
    var nav = window.Event;
    if (nav) {
        showHideNAV(ele, checkbox);
    } else {
        showHideIE(ele, checkbox);
    }

}


function showHideNAV(id, checkbox) {
    var style = document.getElementById(id).style

    if (!checkbox.checked) {
        style.display = "none";
    } else {

        if (style.display == "table-row") {
            style.display = "none";
            checkbox.checked = false;
        } else {
            style.display = "table-row";
            checkbox.checked = true;
        }
    }
}


function showHideIE(id, checkbox) {

    var style = document.getElementById(id).style

    if (!checkbox.checked) {
        style.display = "none";
    } else {
        if (style.display == "inline") {
            style.display = "none";
            checkbox.checked = false;
        } else {
            style.display = "inline";
            checkbox.checked = true;
        }
    }


}

function showhide(id)
{
    var style = document.getElementById(id).style
    if (style.visibility == "hidden") {
        style.visibility = "visible";
    }
    else {
        style.visibility = "hidden";
    }
}

