//DataTable server-side: thërret /api/admin/users me paging/search, dhe bën edit/delete.

const msg = document.getElementById("msg");
const ok = document.getElementById("ok");

function showErr(t){
    msg.style.display = "block";
    ok.style.display = "none";
    msg.textContent = t;
}
function showOk(t){
    ok.style.display = "block";
    msg.style.display = "none";
    ok.textContent = t;
}

$(document).ready(function () {

    // vetëm admin lejohet
    const u = getUser();
    if (!u || u.role !== "ADMIN") {
        logout();
        return;
    }

    // reload button (vetëm nëse ekziston)
    const btnReload = document.getElementById("btnReload");
    if (btnReload) btnReload.addEventListener("click", () => {
        $('#usersTable').DataTable().ajax.reload(null, false);
    });

    $('#usersTable').DataTable({
        serverSide: true,
        processing: true,
        pageLength: 10,

        ajax: async function (data, callback) {
            try {
                const res = await apiFetch("/api/admin/users", {
                    method: "POST",
                    body: JSON.stringify(data)
                });
                callback(res);
            } catch (e) {
                showErr(e.message || "datatable error");
            }
        },

        columns: [
            { data: "id" },
            { data: "emri" },
            { data: "atesia" },
            { data: "mbiemri" },
            { data: "nrTel" },
            { data: "datelindja" },
            { data: "email" },
            {
                data: "role",
                render: function (role) {
                    if (role === "ADMIN") return `<span class="pill pill-admin">ADMIN</span>`;
                    return `<span class="pill pill-user">USER</span>`;
                }
            },
            {
                data: null,
                orderable: false,
                render: function (row) {

                    // 🔒 mos lejo veprime për ADMIN
                    if (row.role === "ADMIN") {
                        return `<div style="opacity:.5; font-style:italic;">Admin locked</div>`;
                    }

                    const esc = (s) => (s ?? "").toString().replaceAll("\\", "\\\\").replaceAll("'", "\\'").replaceAll('"', '\\"');

                    return `
            <div style="display:flex; gap:10px; flex-wrap:wrap; justify-content:flex-end;">
              <button class="btn-mini"
                onclick='editUser(${row.id}, "${esc(row.emri)}", "${esc(row.atesia)}", "${esc(row.mbiemri)}",
                                 "${esc(row.nrTel)}", "${esc(row.datelindja)}", "${esc(row.email)}", "${esc(row.role)}")'>
                Edit
              </button>
              <button class="btn-mini btn-danger" onclick='deleteUser(${row.id})'>
                Delete
              </button>
            </div>
          `;
                }
            }
        ]
    });

}); // ✅ mbylle $(document).ready

// ✅ KËTO DUHEN TË JENË JASHTË DataTable / ready()

async function deleteUser(id) {
    if (!confirm("A je i sigurt që do ta fshish?")) return;

    try {
        await apiFetch(`/api/admin/user/${id}`, { method: "DELETE" });
        showOk("U fshi ✅");
        $('#usersTable').DataTable().ajax.reload(null, false);
    } catch (e) {
        showErr(e.message || "delete failed");
    }
}

async function editUser(id, emri, atesia, mbiemri, nrTel, datelindja, email, role) {
    const newEmri = prompt("Emri:", emri); if (newEmri === null) return;
    const newAtesia = prompt("Atesia:", atesia); if (newAtesia === null) return;
    const newMbiemri = prompt("Mbiemri:", mbiemri); if (newMbiemri === null) return;
    const newNrTel = prompt("NrTel (+35569xxxxxxx):", nrTel); if (newNrTel === null) return;
    const newDatelindja = prompt("Datelindja (YYYY-MM-DD):", datelindja); if (newDatelindja === null) return;
    const newEmail = prompt("Email:", email); if (newEmail === null) return;

    const newRole = prompt("Role (USER/ADMIN):", role || "USER");
    if (newRole === null) return;

    try {
        await apiFetch(`/api/admin/user/${id}`, {
            method: "PUT",
            body: JSON.stringify({
                emri: newEmri,
                atesia: newAtesia,
                mbiemri: newMbiemri,
                nrTel: newNrTel,
                datelindja: newDatelindja,
                email: newEmail,
                role: newRole
            })
        });

        showOk("U përditësua ✅");
        $('#usersTable').DataTable().ajax.reload(null, false);
    } catch (e) {
        showErr(e.message || "update failed");
    }
}
