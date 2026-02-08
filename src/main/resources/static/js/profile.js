//Merr profilin, update profilin, ndryshon foto (nëse e ke).
const nameRe = /^[A-Za-z]{1,20}$/;
const phoneRe = /^\+35569\d{7}$/;

const msgEl = document.getElementById("msg");
const okEl  = document.getElementById("ok");
const roleBadge = document.getElementById("roleBadge");

function showErr(t){
    okEl.style.display = "none";
    msgEl.style.display = "block";
    msgEl.textContent = t;
}
function showOk(t){
    msgEl.style.display = "none";
    okEl.style.display = "block";
    okEl.textContent = t;
}

function validateProfile() {
    const emri = document.getElementById("emri").value.trim();
    const atesia = document.getElementById("atesia").value.trim();
    const mbiemri = document.getElementById("mbiemri").value.trim();
    const nrTel = document.getElementById("nrTel").value.trim();
    const datelindja = document.getElementById("datelindja").value;

    if (!nameRe.test(emri)) return "Emri: vetëm shkronja, max 20";
    if (!nameRe.test(atesia)) return "Atesia: vetëm shkronja, max 20";
    if (!nameRe.test(mbiemri)) return "Mbiemri: vetëm shkronja, max 20";
    if (!phoneRe.test(nrTel)) return "NrTel duhet: +35569xxxxxxx";
    if (!datelindja) return "Vendos datëlindjen";
    return null;
}

async function loadProfile() {
    const user = await apiFetch("/api/user/profile", { method: "GET" });

    document.getElementById("emri").value = user.emri || "";
    document.getElementById("atesia").value = user.atesia || "";
    document.getElementById("mbiemri").value = user.mbiemri || "";
    document.getElementById("nrTel").value = user.nrTel || "";
    document.getElementById("datelindja").value = user.datelindja || "";

    // badge
    roleBadge.textContent = user.role || "USER";

    const photo = document.getElementById("photo");
    photo.src = user.photoUrl ? user.photoUrl : "https://via.placeholder.com/140";
}

loadProfile().catch(e => showErr(e.message || "Profile load failed"));

document.getElementById("btnSave").addEventListener("click", async () => {
    msgEl.style.display = "none";
    okEl.style.display = "none";

    const err = validateProfile();
    if (err) { showErr(err); return; }

    try {
        await apiFetch("/api/user/profile", {
            method: "PUT",
            body: JSON.stringify({
                emri: document.getElementById("emri").value.trim(),
                atesia: document.getElementById("atesia").value.trim(),
                mbiemri: document.getElementById("mbiemri").value.trim(),
                nrTel: document.getElementById("nrTel").value.trim(),
                datelindja: document.getElementById("datelindja").value
            })
        });

        showOk("U ruajt profili ✅");
        await loadProfile();
    } catch (e) {
        showErr(e.message || "Save failed");
    }
});

document.getElementById("btnUpload").addEventListener("click", async () => {
    msgEl.style.display = "none";
    okEl.style.display = "none";

    const f = document.getElementById("file").files[0];
    if (!f) { showErr("Zgjidh një foto"); return; }

    const form = new FormData();
    form.append("file", f);

    try {
        const token = getToken();
        const res = await fetch("/api/user/upload-photo", {
            method: "POST",
            headers: { "Authorization": "Bearer " + token },
            body: form
        });

        if (!res.ok) throw new Error(await res.text());

        showOk("Foto u ndryshua ✅");
        await loadProfile();
    } catch (e) {
        showErr(e.message || "Upload failed");
    }
});
