// 👉 After deploying to Railway, replace this with your backend's public URL (no trailing slash)
const API_URL = window.location.hostname === "localhost" || window.location.hostname === "127.0.0.1"
  ? "http://localhost:8080"
  : "https://backend-production-33031abf.up.railway.app";

const form = document.getElementById("workout-form");
const formTitle = document.getElementById("form-title");
const submitBtn = document.getElementById("submit-btn");
const cancelBtn = document.getElementById("cancel-edit");
const formError = document.getElementById("form-error");
const exerciseList = document.getElementById("exercise-list");
const rowTemplate = document.getElementById("exercise-row-template");
const workoutList = document.getElementById("workout-list");
const prsEl = document.getElementById("prs");
const searchInput = document.getElementById("search");

// ---------- API helpers ----------
async function api(path, options = {}) {
  const res = await fetch(`${API_URL}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (res.status === 204) return null;
  const data = await res.json().catch(() => null);
  if (!res.ok) {
    const details = data?.details ? ": " + data.details.join(", ") : "";
    throw new Error((data?.error || `Request failed (${res.status})`) + details);
  }
  return data;
}

const getWorkouts = (search = "") =>
  api(`/api/workouts${search ? `?search=${encodeURIComponent(search)}` : ""}`);
const createWorkout = (w) => api("/api/workouts", { method: "POST", body: JSON.stringify(w) });
const updateWorkout = (id, w) => api(`/api/workouts/${id}`, { method: "PUT", body: JSON.stringify(w) });
const deleteWorkout = (id) => api(`/api/workouts/${id}`, { method: "DELETE" });
const getPRs = () => api("/api/stats/prs");

// ---------- Exercise rows ----------
function addExerciseRow(ex = {}) {
  const row = rowTemplate.content.firstElementChild.cloneNode(true);
  row.querySelector(".ex-name").value = ex.name ?? "";
  row.querySelector(".ex-sets").value = ex.sets ?? 3;
  row.querySelector(".ex-reps").value = ex.reps ?? 10;
  row.querySelector(".ex-weight").value = ex.weight ?? 0;
  row.querySelector(".remove-ex").addEventListener("click", () => row.remove());
  exerciseList.appendChild(row);
}

function readExercises() {
  return [...exerciseList.querySelectorAll(".exercise-row")].map((row) => ({
    name: row.querySelector(".ex-name").value.trim(),
    sets: Number(row.querySelector(".ex-sets").value),
    reps: Number(row.querySelector(".ex-reps").value),
    weight: Number(row.querySelector(".ex-weight").value),
  }));
}

// ---------- Form ----------
function resetForm() {
  form.reset();
  document.getElementById("workout-id").value = "";
  document.getElementById("date").value = new Date().toLocaleDateString("en-CA"); // YYYY-MM-DD
  exerciseList.innerHTML = "";
  addExerciseRow();
  formTitle.textContent = "Log a workout";
  submitBtn.textContent = "Save workout";
  cancelBtn.classList.add("hidden");
  formError.textContent = "";
}

function fillForm(w) {
  document.getElementById("workout-id").value = w.id;
  document.getElementById("name").value = w.name;
  document.getElementById("date").value = w.date;
  document.getElementById("duration").value = w.durationMinutes;
  document.getElementById("notes").value = w.notes ?? "";
  exerciseList.innerHTML = "";
  (w.exercises.length ? w.exercises : [{}]).forEach(addExerciseRow);
  formTitle.textContent = "Edit workout";
  submitBtn.textContent = "Update workout";
  cancelBtn.classList.remove("hidden");
  formError.textContent = "";
  window.scrollTo({ top: 0, behavior: "smooth" });
}

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = document.getElementById("workout-id").value;
  const workout = {
    name: document.getElementById("name").value.trim(),
    date: document.getElementById("date").value,
    durationMinutes: Number(document.getElementById("duration").value) || 0,
    notes: document.getElementById("notes").value.trim(),
    exercises: readExercises(),
  };

  submitBtn.disabled = true;
  try {
    if (id) await updateWorkout(id, workout);
    else await createWorkout(workout);
    resetForm();
    await refresh();
  } catch (err) {
    formError.textContent = err.message;
  } finally {
    submitBtn.disabled = false;
  }
});

document.getElementById("add-exercise").addEventListener("click", () => addExerciseRow());
cancelBtn.addEventListener("click", resetForm);

// ---------- Rendering ----------
function escapeHtml(str = "") {
  return String(str).replace(/[&<>"']/g, (c) =>
    ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" }[c]));
}

function formatDate(iso) {
  // Parse YYYY-MM-DD as a local date (avoids off-by-one timezone bugs)
  const [y, m, d] = iso.split("-").map(Number);
  return new Date(y, m - 1, d).toLocaleDateString(undefined, {
    weekday: "short", month: "short", day: "numeric", year: "numeric",
  });
}

function renderWorkouts(workouts) {
  if (!workouts.length) {
    workoutList.innerHTML = `<p class="muted">No workouts yet. Log your first one above!</p>`;
    return;
  }
  workoutList.innerHTML = "";
  for (const w of workouts) {
    const el = document.createElement("div");
    el.className = "workout";
    el.innerHTML = `
      <div class="workout-top">
        <div>
          <h4>${escapeHtml(w.name)}</h4>
          <div class="meta">${formatDate(w.date)} · ${w.durationMinutes} min · ${w.exercises.length} exercise(s)</div>
        </div>
        <div class="workout-actions">
          <button class="secondary edit">Edit</button>
          <button class="danger delete">Delete</button>
        </div>
      </div>
      <ul>
        ${w.exercises.map((ex) =>
          `<li>${escapeHtml(ex.name)} — ${ex.sets}×${ex.reps} @ ${ex.weight}</li>`).join("")}
      </ul>
      ${w.notes ? `<p class="notes">${escapeHtml(w.notes)}</p>` : ""}
    `;
    el.querySelector(".edit").addEventListener("click", () => fillForm(w));
    el.querySelector(".delete").addEventListener("click", async () => {
      if (!confirm(`Delete "${w.name}"?`)) return;
      try {
        await deleteWorkout(w.id);
        await refresh();
      } catch (err) {
        alert(err.message);
      }
    });
    workoutList.appendChild(el);
  }
}

function renderPRs(prs) {
  if (!prs.length) {
    prsEl.innerHTML = `<p class="muted">No records yet.</p>`;
    return;
  }
  prsEl.innerHTML = `<div class="pr-grid">${prs.map((p) => `
    <div class="pr">
      <div class="ex">${escapeHtml(p.exercise)}</div>
      <div class="weight">${p.weight}</div>
      <div class="detail">${p.reps} reps · ${formatDate(p.date)}</div>
    </div>`).join("")}</div>`;
}

async function refresh() {
  try {
    const [workouts, prs] = await Promise.all([getWorkouts(searchInput.value.trim()), getPRs()]);
    renderWorkouts(workouts);
    renderPRs(prs);
  } catch (err) {
    workoutList.innerHTML = `<p class="error">Couldn't reach the API (${escapeHtml(err.message)}). Is the backend running and is API_URL correct?</p>`;
  }
}

let searchTimer;
searchInput.addEventListener("input", () => {
  clearTimeout(searchTimer);
  searchTimer = setTimeout(refresh, 300);
});

// ---------- Start ----------
resetForm();
refresh();
