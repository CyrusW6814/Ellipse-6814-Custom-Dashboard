const phases = [];
const phasesContainer = document.getElementById("phases");
let nextPhase;
const nextPhaseContainer = document.getElementById("nextPhase");

function init() {
  //how to do this without the list? I jsut want a seperate element box with updatable text
  let nextPhasebox = document.createElement("div");
  nextPhasebox.className = "nextPhaseBox";
  nextPhasebox.innerText = CONSTANTS.mainPhaseTimes[6];
  nextPhaseContainer.appendChild(nextPhasebox);
  // nextPhase[0].style.background = "grey;"
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

  init();
}

waitForConstants();

// =========================
// TARGET DRAGGING (2026)
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
// UPDATE TIMERS (2026)
// =========================
function clamp(val, min, max) {
  return Math.max(min, Math.min(max, val));
}

function updateTimers(matchTime, isAuto) {

  if (isAuto) {
    phases[0].innerText = matchTime;
    phases[1].innerText = CONSTANTS.mainPhaseTimes[1];

    // this is to set the center 4 (the 25 second phases)
    for (let i = 2; i < CONSTANTS.mainPhaseTimes.length-1; i++) {
      phases[i].innerText = CONSTANTS.mainPhaseTimes[i];
    }

    // final phase (set to final save value)
    phases[CONSTANTS.mainPhaseTimes.length-1].innerText = CONSTANTS.mainPhaseTimes[CONSTANTS.mainPhaseTimes.length-1];
  }
  else {
    // first phase
    phases[0].innerText = "0";

    for (let i = 1; i < CONSTANTS.mainPhaseTimes.length; i++) {
      let value =
        CONSTANTS.mainPhaseTimes[i] -
        (CONSTANTS.phaseTimeRemaining[i] - matchTime);

      phases[i].innerText = clamp(
        value,
        0,
        CONSTANTS.mainPhaseTimes[i]
      );
    }
  }
}
// =========================
// PHASE COLOR (FRC Rebuilt 2026)
// =========================

function updatePhases(isRed) {
  let mount = 0;
  for(let i = 0; i < phases.length; i++){
    if (i >= CONSTANTS.startCopyPhase && i <= CONSTANTS.endCopyPhase) {
      if (isRed) {
        phases[i].style.background = (i % 2 === 0) ? "blue" : "red";
      } else {
        phases[i].style.background = (i % 2 === 0) ? "red" : "blue";
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