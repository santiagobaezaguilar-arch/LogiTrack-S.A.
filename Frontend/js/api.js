/* ═══════════════════════════════════════════════════════════
   LogiTrack S.A. — Capa de comunicación con el backend
   BASE_URL apunta al servidor local. Cámbiala si el backend
   corre en otra IP (igual que poscampus usaba 192.168.x.x).
   ═══════════════════════════════════════════════════════════ */

const BASE_URL = "http://localhost:8080";

/* ── Leer / escribir sesión ──────────────────────────────── */
function getToken()   { return localStorage.getItem("lt_token"); }
function getUsuario() { return JSON.parse(localStorage.getItem("lt_usuario") || "null"); }

function guardarSesion(data) {
    localStorage.setItem("lt_token",   data.token);
    localStorage.setItem("lt_usuario", JSON.stringify({
        correo: data.correo,
        nombre: data.nombre,
        rol:    data.rol
    }));
}

function cerrarSesion() {
    localStorage.removeItem("lt_token");
    localStorage.removeItem("lt_usuario");
    window.location.replace("../index.html");
}

/* ── Fetch base con JWT ──────────────────────────────────── */
async function apiFetch(path, options = {}) {
    const token = getToken();

    const res = await fetch(BASE_URL + path, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { "Authorization": "Bearer " + token } : {}),
            ...(options.headers || {})
        }
    });

    // Sesión expirada o token inválido
    if (res.status === 401) {
        cerrarSesion();
        throw new Error("Sesión expirada. Vuelve a iniciar sesión.");
    }

    // Sin permiso (rol insuficiente)
    if (res.status === 403) {
        throw new Error("No tienes permiso para realizar esta acción.");
    }

    // 204 No Content
    if (res.status === 204) return null;

    const text = await res.text();
    const body = text ? JSON.parse(text) : null;

    if (!res.ok) {
        // Usar el mensaje del backend si viene
        const msg = body?.message || body?.errors
            ? (body.message || JSON.stringify(body.errors))
            : `Error ${res.status}`;
        throw new Error(msg);
    }

    return body;
}

/* ═══════════════════════════════════════════════════════════
   AUTH
   ═══════════════════════════════════════════════════════════ */
const Auth = {
    login:    (correo, password) =>
        apiFetch("/auth/login", { method: "POST", body: JSON.stringify({ correo, password }) }),
    register: (data) =>
        apiFetch("/auth/register", { method: "POST", body: JSON.stringify(data) })
};

/* ═══════════════════════════════════════════════════════════
   USUARIOS
   ═══════════════════════════════════════════════════════════ */
const Usuarios = {
    listar:   ()     => apiFetch("/api/usuarios"),
    buscar:   (id)   => apiFetch(`/api/usuarios/${id}`),
    crear:    (data) => apiFetch("/api/usuarios", { method: "POST", body: JSON.stringify(data) }),
    actualizar: (id, data) => apiFetch(`/api/usuarios/${id}`, { method: "PUT", body: JSON.stringify(data) }),
    eliminar: (id)   => apiFetch(`/api/usuarios/${id}`, { method: "DELETE" })
};

/* ═══════════════════════════════════════════════════════════
   BODEGAS
   ═══════════════════════════════════════════════════════════ */
const Bodegas = {
    listar:   ()     => apiFetch("/api/bodegas"),
    buscar:   (id)   => apiFetch(`/api/bodegas/${id}`),
    crear:    (data) => apiFetch("/api/bodegas", { method: "POST", body: JSON.stringify(data) }),
    actualizar: (id, data) => apiFetch(`/api/bodegas/${id}`, { method: "PUT", body: JSON.stringify(data) }),
    eliminar: (id)   => apiFetch(`/api/bodegas/${id}`, { method: "DELETE" })
};

/* ═══════════════════════════════════════════════════════════
   PRODUCTOS
   ═══════════════════════════════════════════════════════════ */
const Productos = {
    listar:   ()          => apiFetch("/api/productos"),
    buscar:   (id)        => apiFetch(`/api/productos/${id}`),
    categoria: (cat)      => apiFetch(`/api/productos/filtroCategoria?categoria=${encodeURIComponent(cat)}`),
    crear:    (data)      => apiFetch("/api/productos", { method: "POST", body: JSON.stringify(data) }),
    actualizar: (id, data) => apiFetch(`/api/productos/${id}`, { method: "PUT", body: JSON.stringify(data) }),
    eliminar: (id)        => apiFetch(`/api/productos/${id}`, { method: "DELETE" })
};

/* ═══════════════════════════════════════════════════════════
   INVENTARIO
   ═══════════════════════════════════════════════════════════ */
const Inventario = {
    listar:       ()        => apiFetch("/api/inventario"),
    porBodega:    (id)      => apiFetch(`/api/inventario/bodega/${id}`),
    stockBajo:    ()        => apiFetch("/api/inventario/stock-bajo"),
    registrar:    (data)    => apiFetch("/api/inventario", { method: "POST", body: JSON.stringify(data) }),
    actualizar:   (data)    => apiFetch("/api/inventario", { method: "PUT",  body: JSON.stringify(data) })
};

/* ═══════════════════════════════════════════════════════════
   MOVIMIENTOS
   ═══════════════════════════════════════════════════════════ */
const Movimientos = {
    listar:     ()         => apiFetch("/api/movimientos"),
    buscar:     (id)       => apiFetch(`/api/movimientos/${id}`),
    porTipo:    (tipo)     => apiFetch(`/api/movimientos/filtroTipo?tipo=${tipo}`),
    porFechas:  (d, h)     => apiFetch(`/api/movimientos/filtroFechas?desde=${d}&hasta=${h}`),
    registrar:  (data)     => apiFetch("/api/movimientos", { method: "POST", body: JSON.stringify(data) })
};

/* ═══════════════════════════════════════════════════════════
   DETALLES DE MOVIMIENTO
   ═══════════════════════════════════════════════════════════ */
const Detalles = {
    listar:        ()    => apiFetch("/api/detalles-movimiento"),
    porMovimiento: (id)  => apiFetch(`/api/detalles-movimiento/movimiento/${id}`),
    registrar: (data)    => apiFetch("/api/detalles-movimiento", { method: "POST", body: JSON.stringify(data) }),
    eliminar:  (id)      => apiFetch(`/api/detalles-movimiento/${id}`, { method: "DELETE" })
};

/* ═══════════════════════════════════════════════════════════
   AUDITORÍA
   ═══════════════════════════════════════════════════════════ */
const Auditoria = {
    listar:     ()        => apiFetch("/api/auditorias"),
    porUsuario: (u)       => apiFetch(`/api/auditorias/usuario?usuario=${encodeURIComponent(u)}`),
    porTipo:    (t)       => apiFetch(`/api/auditorias/tipo?tipoOperacion=${t}`),
    porEntidad: (e)       => apiFetch(`/api/auditorias/entidad?entidad=${encodeURIComponent(e)}`)
};

/* ── Helpers UI ──────────────────────────────────────────── */
function mostrarFeedback(el, msg, tipo = "ok") {
    el.textContent = msg;
    el.className = "feedback " + (tipo === "ok" ? "fb-ok" : "fb-err");
    if (tipo === "ok") setTimeout(() => { el.textContent = ""; el.className = "feedback"; }, 3500);
}

function jsonPre(data) {
    return data === null ? "(sin contenido)" : JSON.stringify(data, null, 2);
}
