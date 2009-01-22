var navMenuTimer;
var navMenuNum;

function navMenuOn(id) {
    document.getElementById(id).style.visibility = 'visible';
}

function navMenuOff(id) {
    document.getElementById(id).style.visibility = 'hidden';
}

function delayOff(id) {
    navMenuTimer = setTimeout("navMenuOff1()", 400);
}

function navMenuOff1(){
     document.getElementById("navMenu_1").style.visibility = 'hidden';    
}