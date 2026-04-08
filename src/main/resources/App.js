const phases = [];
const phasesContainer = document.getElementById("phases");

function init() {
  for (let i = 0; i < CONSTANTS.mainPhaseTimes.length; i++) {
    let box = document.createElement("div");
    box.className = "phaseBox";
    box.innerText = CONSTANTS.mainPhaseTimes[i];
    phases.push(box);
    phasesContainer.appendChild(box);
  }
}

function waitForConstants() {
  if (!window.CONSTANTS) {
    setTimeout(waitForConstants, 50);
    return;
  }

  init(); // now it's safe
}

waitForConstants();

// =========================
// TARGET DRAGGING
// =========================
const target = document.getElementById("target");

let dragging = false;
let offsetX, offsetY;

target.addEventListener("mousedown", e => {
  dragging = true;
  offsetX = e.offsetX+30;
  offsetY = e.offsetY+5;
});

document.addEventListener("mousemove", e => {
  if (!dragging) return;

  target.style.left = (e.pageX - offsetX) + "px";
  target.style.top = (e.pageY - offsetY) + "px";
});

document.addEventListener("mouseup", () => dragging = false);

// =========================
// ROBOT UPDATE (FROM JAVA)
// =========================
function updateRobot(x, y) {
  const robot = document.getElementById("robot");

  robot.style.left = (x * 100) + "%";
  robot.style.top = (y * 100) + "%";
}

// =========================
// CONNECTION STATUS
// =========================
function updateConnection(connected) {
  const status = document.getElementById("status");

  status.innerText = connected ? "Connected" : "Disconnected";
}

// =========================
// PHASE COLOR
// =========================

function updatePhases(isRed) {
  let mount = 0;
  for(let i = 0; i < phases.length; i++){
    if (i >= 2 && i <= 5) {
      if (isRed) {
        phases[i].style.background = (i % 2 === 0) ? "red" : "blue";
      } else {
        phases[i].style.background = (i % 2 === 0) ? "blue" : "red";
      }
    }
  }
}

// =========================
// BUTTONS (JS → JAVA)
// =========================
function connectRobot() {
  window.java.connectRobot();
  document.getElementById("status").innerText = "Connecting to robot...";
}

function connectSim() {
  window.java.connectSim();
  document.getElementById("status").innerText = "Connecting to simulation...";
}