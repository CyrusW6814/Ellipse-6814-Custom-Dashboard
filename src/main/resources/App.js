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

window.addEventListener("load", init);

// =========================
// TARGET DRAGGING
// =========================
const target = document.getElementById("target");

let dragging = false;
let offsetX, offsetY;

target.addEventListener("mousedown", e => {
  dragging = true;
  offsetX = e.offsetX;
  offsetY = e.offsetY;
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