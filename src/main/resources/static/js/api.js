// Nëse frontend serviret nga Spring Boot (static), lëre bosh.
// Nëse e hap nga një server tjetër (p.sh. Vite), vendos "http://localhost:8080"
//Wrapper për thirrje API (fetch/axios), vendos token automatikisht, base URL, error handling
const API_BASE = ""; // ose "http://localhost:8080"

function getToken() {
    return localStorage.getItem("token");
}

function setToken(token) {
    localStorage.setItem("token", token);
}

function clearAuth() {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
}

function getUser() {
    const u = localStorage.getItem("user");
    return u ? JSON.parse(u) : null;
}

function setUser(user) {
    localStorage.setItem("user", JSON.stringify(user));
}

async function apiFetch(path, options = {}) {
    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {}),
    };

    // Shto token automatikisht kur ekziston
    const token = getToken();
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const res = await fetch(API_BASE + path, {
        ...options,
        headers,
    });

    // Lexo body (JSON ose text)
    let data = null;
    const contentType = res.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        data = await res.json().catch(() => null);
    } else {
        const text = await res.text().catch(() => "");
        data = text ? { message: text } : null;
    }

    // ❌ ERROR → shfaq vetëm MESAZH (JO status teknik)
    if (!res.ok) {
        const msg =
            data?.message ||
            (res.status === 401 ? "Email/NrTel ose password gabim." : null) ||
            (res.status === 409 ? "Ky email është i regjistruar. Provo Login." : null) ||
            "Ndodhi një gabim. Provo përsëri.";

        throw new Error(msg);
    }

    // ✅ OK
    return data;
}

function logout() {
    clearAuth();
    window.location.href = "/index.html";
}
