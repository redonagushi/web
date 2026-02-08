// auth.js - Login/Register logic + validim minimal + ruan token/user + redirect

function showMsg(text, type = "error") {
    const msg = document.getElementById("msg");
    if (!msg) return;
    msg.textContent = text || "";
    msg.className = type; // opsionale: css .error .success
}

function isValidEmail(email) {
    return /^\S+@\S+\.\S+$/.test(email);
}

function isValidPhone(phone) {
    // pranon numra me + ose pa, 7-15 shifra (e thjeshtë)
    return /^\+?\d{7,15}$/.test(phone);
}

function redirectByRole(user) {
    if (user?.role === "ADMIN") window.location.href = "/admin.html";
    else window.location.href = "/profile.html";
}

// ================= LOGIN (vetëm një herë) =================
async function handleLogin(e) {
    e?.preventDefault?.();

    const emailOrPhoneEl = document.getElementById("emailOrPhone");
    const passwordEl = document.getElementById("password");

    const emailOrPhone = (emailOrPhoneEl?.value || "").trim();
    const password = passwordEl?.value || "";

    showMsg("");

    // validim minimal
    if (!emailOrPhone || !password) {
        showMsg("Plotëso email/telefon dhe password.");
        return;
    }

    try {
        // përdor apiFetch (që menaxhon base url + token kur duhet)
        const res = await apiFetch("/api/auth/login", {
            method: "POST",
            body: JSON.stringify({ emailOrPhone, password }),
        });

        if (!res?.token || !res?.user) {
            showMsg("Login failed: përgjigje e pavlefshme nga serveri.");
            return;
        }

        setToken(res.token);
        setUser(res.user);

        redirectByRole(res.user);
    } catch (err) {
        showMsg(err?.message || "Login failed");
    }
}

// lidhe login me form dhe/ose button (pa duplikim)
const loginForm = document.getElementById("loginForm");
if (loginForm) loginForm.addEventListener("submit", handleLogin);

const btnLogin = document.getElementById("btnLogin");
if (btnLogin) btnLogin.addEventListener("click", handleLogin);

// ================= REGISTER =================
const registerForm = document.getElementById("registerForm");
if (registerForm) {
    registerForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        showMsg("");

        const payload = {
            emri: (document.getElementById("emri")?.value || "").trim(),
            atesia: (document.getElementById("atesia")?.value || "").trim(),
            mbiemri: (document.getElementById("mbiemri")?.value || "").trim(),
            nrTel: (document.getElementById("nrTel")?.value || "").trim(),
            datelindja: document.getElementById("datelindja")?.value || "",
            email: (document.getElementById("email")?.value || "").trim(),
            password: document.getElementById("regPassword")?.value || document.getElementById("password")?.value || "",
            confirmPassword: document.getElementById("confirmPassword")?.value || "",
        };

        // ---- validime minimale ----
        if (!payload.emri || !payload.mbiemri || !payload.email || !payload.password || !payload.confirmPassword) {
            showMsg("Plotëso fushat kryesore: Emri, Mbiemri, Email, Password, Confirm Password.");
            return;
        }

        if (!isValidEmail(payload.email)) {
            showMsg("Email nuk është në format të saktë.");
            return;
        }

        if (payload.nrTel && !isValidPhone(payload.nrTel)) {
            showMsg("Nr. Tel duhet të jetë me shifra (mundësisht me +) dhe 7-15 shifra.");
            return;
        }

        if (payload.password.length < 6) {
            showMsg("Password duhet të ketë të paktën 6 karaktere.");
            return;
        }

        if (payload.password !== payload.confirmPassword) {
            showMsg("Password dhe Confirm Password nuk përputhen.");
            return;
        }

        try {
            // përdor apiFetch edhe këtu që të kesh trajtim të njëjtë të errors
            const res = await apiFetch("/api/auth/register", {
                method: "POST",
                body: JSON.stringify(payload),
            });

            // nëse backend kthen tekst, apiFetch mund ta kthejë si objekt; prandaj thjesht shfaq sukses
            showMsg("Regjistrimi u krye me sukses. Tani bëj login!", "success");
            window.location.href = "/index.html";
        } catch (err) {
            // p.sh. 409 -> email ekziston
            showMsg(err?.message || "Register failed");
        }
    });
}
