/* ═══════════════════════════════════════════════════════════
   LogiTrack S.A. — Lógica del panel EMPLEADO
   ═══════════════════════════════════════════════════════════ */

/* ── Guard: solo EMPLEADO (o ADMIN que venga aquí por error) ─ */
(function () {
    const u = getUsuario();
    if (!u || !getToken()) { window.location.replace("../index.html"); return; }
    // Si un admin llega aquí lo mandamos a su panel
    if (u.rol === "ADMIN") { window.location.replace("admin.html"); return; }
    document.getElementById("hdr-nombre").textContent = u.nombre;
})();

/* ── Tabs ────────────────────────────────────────────────── */
document.querySelectorAll("#main-tabs .tab-btn").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll("#main-tabs .tab-btn").forEach(b => b.classList.remove("active"));
        document.querySelectorAll(".tab-panel").forEach(p => p.classList.remove("active"));
        btn.classList.add("active");
        document.getElementById("tab-" + btn.dataset.tab).classList.add("active");
    });
});

/* ═══════════════════════════════════════════════════════════
   HELPERS
   ═══════════════════════════════════════════════════════════ */
function renderTabla(tbodyId, filas) {
    const tb = document.querySelector(`#${tbodyId} tbody`);
    if (!filas || filas.length === 0) {
        tb.innerHTML = `<tr class="empty-row"><td colspan="20">Sin resultados.</td></tr>`;
        return;
    }
    tb.innerHTML = filas.join("");
}

function fmtFecha(iso) {
    if (!iso) return "—";
    return new Date(iso).toLocaleString("es-CO", { dateStyle: "short", timeStyle: "short" });
}

function fmtMon(val) {
    if (val == null) return "—";
    return "$" + Number(val).toLocaleString("es-CO");
}

/* ═══════════════════════════════════════════════════════════
   CONSULTAS
   ═══════════════════════════════════════════════════════════ */
async function cargarBodegas() {
    const fb = document.getElementById("fb-bodegas");
    try {
        const data = await Bodegas.listar();
        renderTabla("tbl-bodegas", data.map(b => `
            <tr>
                <td>${b.id}</td>
                <td>${b.nombre}</td>
                <td>${b.ubicacion || "—"}</td>
                <td>${b.capacidad ?? "—"}</td>
                <td>${b.encargado ? b.encargado.nombre : "Sin asignar"}</td>
            </tr>`));
        mostrarFeedback(fb, `${data.length} bodega(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function listarProductos() {
    const fb = document.getElementById("fb-productos");
    try {
        const data = await Productos.listar();
        renderTablaProductos(data);
        mostrarFeedback(fb, `${data.length} producto(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function buscarProductoId() {
    const fb = document.getElementById("fb-productos");
    const id = document.getElementById("fp-id").value;
    if (!id) { mostrarFeedback(fb, "Ingresa un ID.", "err"); return; }
    try {
        const p = await Productos.buscar(Number(id));
        renderTablaProductos([p]);
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function buscarProductoCat() {
    const fb  = document.getElementById("fb-productos");
    const cat = document.getElementById("fp-cat").value.trim();
    if (!cat) { mostrarFeedback(fb, "Ingresa una categoría.", "err"); return; }
    try {
        const data = await Productos.categoria(cat);
        renderTablaProductos(data);
        mostrarFeedback(fb, `${data.length} resultado(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function renderTablaProductos(data) {
    renderTabla("tbl-productos", data.map(p => `
        <tr>
            <td>${p.id}</td>
            <td>${p.nombre}</td>
            <td>${p.categoria || "—"}</td>
            <td>${fmtMon(p.precio)}</td>
        </tr>`));
}

/* ═══════════════════════════════════════════════════════════
   MOVIMIENTOS
   ═══════════════════════════════════════════════════════════ */
function toggleBodegasEmp() {
    const tipo = document.getElementById("emp-mov-tipo").value;
    document.getElementById("emp-wrap-origen").style.display  = tipo === "ENTRADA" ? "none" : "";
    document.getElementById("emp-wrap-destino").style.display = tipo === "SALIDA"  ? "none" : "";
}
toggleBodegasEmp();

async function registrarMovimiento() {
    const fb   = document.getElementById("fb-mov-create");
    const tipo = document.getElementById("emp-mov-tipo").value;
    const orig = document.getElementById("emp-mov-origen").value;
    const dest = document.getElementById("emp-mov-destino").value;

    const payload = { tipoMovimiento: tipo, bodegaOrigenId: null, bodegaDestinoId: null };

    if (tipo !== "ENTRADA") payload.bodegaOrigenId  = Number(orig) || null;
    if (tipo !== "SALIDA")  payload.bodegaDestinoId = Number(dest) || null;

    // Validaciones de coherencia
    if (tipo === "TRANSFERENCIA") {
        if (!payload.bodegaOrigenId || !payload.bodegaDestinoId) {
            mostrarFeedback(fb, "TRANSFERENCIA requiere bodega origen y destino.", "err"); return;
        }
        if (payload.bodegaOrigenId === payload.bodegaDestinoId) {
            mostrarFeedback(fb, "La bodega origen y destino no pueden ser la misma.", "err"); return;
        }
    }
    if (tipo === "SALIDA"  && !payload.bodegaOrigenId)  { mostrarFeedback(fb, "SALIDA requiere bodega origen.", "err"); return; }
    if (tipo === "ENTRADA" && !payload.bodegaDestinoId) { mostrarFeedback(fb, "ENTRADA requiere bodega destino.", "err"); return; }

    try {
        const r = await Movimientos.registrar(payload);
        mostrarFeedback(fb, `✅ Movimiento #${r.id} (${r.tipoMovimiento}) registrado. Ahora agrega los productos.`, "ok");
        document.getElementById("emp-det-mid").value = r.id;
        await listarMovimientos();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function agregarDetalle() {
    const fb   = document.getElementById("fb-det");
    const mid  = document.getElementById("emp-det-mid").value;
    const pid  = document.getElementById("emp-det-pid").value;
    const cant = document.getElementById("emp-det-cant").value;

    if (!mid || !pid || !cant) { mostrarFeedback(fb, "Todos los campos son obligatorios.", "err"); return; }
    if (Number(cant) <= 0)     { mostrarFeedback(fb, "La cantidad debe ser mayor a 0.", "err"); return; }

    try {
        await Detalles.registrar({
            movimientoId: Number(mid),
            productoId:   Number(pid),
            cantidad:     Number(cant)
        });
        mostrarFeedback(fb, `✅ Producto #${pid} agregado (${cant} unidades) al movimiento #${mid}.`, "ok");
        document.getElementById("emp-det-pid").value  = "";
        document.getElementById("emp-det-cant").value = "";
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function listarMovimientos() {
    const fb = document.getElementById("fb-mov-get");
    try {
        const data = await Movimientos.listar();
        renderTablaMovimientos(data);
        mostrarFeedback(fb, `${data.length} movimiento(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function filtrarMovimientos() {
    const fb   = document.getElementById("fb-mov-get");
    const tipo = document.getElementById("emp-filtro-tipo").value;
    const desde= document.getElementById("emp-desde").value;
    const hasta= document.getElementById("emp-hasta").value;

    try {
        let data;
        if (tipo) {
            data = await Movimientos.porTipo(tipo);
        } else if (desde && hasta) {
            data = await Movimientos.porFechas(
                new Date(desde).toISOString().slice(0,19),
                new Date(hasta).toISOString().slice(0,19)
            );
        } else {
            data = await Movimientos.listar();
        }
        renderTablaMovimientos(data);
        mostrarFeedback(fb, `${data.length} resultado(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function renderTablaMovimientos(data) {
    const colores = { ENTRADA: "var(--green)", SALIDA: "var(--red)", TRANSFERENCIA: "var(--amber)" };
    renderTabla("tbl-movimientos", data.map(m => `
        <tr>
            <td>${m.id}</td>
            <td>${fmtFecha(m.fecha)}</td>
            <td style="color:${colores[m.tipoMovimiento]};font-weight:700">${m.tipoMovimiento}</td>
            <td>${m.usuario?.nombre || "—"}</td>
            <td>${m.bodegaOrigen?.nombre  || "—"}</td>
            <td>${m.bodegaDestino?.nombre || "—"}</td>
            <td><button class="btn btn-sm btn-outline" onclick="verDetallesMovimiento(${m.id})">Ver productos</button></td>
        </tr>`));
}

async function verDetallesMovimiento(id) {
    try {
        const data = await Detalles.porMovimiento(id);
        const info = data.map(d => `• ${d.producto.nombre} — ${d.cantidad} unidades`).join("\n")
            || "Sin productos registrados.";
        alert(`Productos del movimiento #${id}:\n\n${info}`);
    } catch (e) { alert("Error: " + e.message); }
}

/* ═══════════════════════════════════════════════════════════
   INVENTARIO
   ═══════════════════════════════════════════════════════════ */
async function verInventarioGlobal() {
    const fb = document.getElementById("fb-inv");
    try {
        const data = await Inventario.listar();
        renderTablaInventario(data);
        mostrarFeedback(fb, `${data.length} registro(s) en total.`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function verInventarioBodega() {
    const fb  = document.getElementById("fb-inv");
    const bid = document.getElementById("emp-inv-bid").value;
    if (!bid) { mostrarFeedback(fb, "Ingresa el ID de la bodega.", "err"); return; }
    try {
        const data = await Inventario.porBodega(Number(bid));
        renderTablaInventario(data);
        mostrarFeedback(fb, `${data.length} registro(s) en bodega #${bid}.`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function verStockBajo() {
    const fb = document.getElementById("fb-inv");
    try {
        const data = await Inventario.stockBajo();
        renderTablaInventario(data);
        mostrarFeedback(fb, `${data.length} producto(s) con stock crítico (< 10 unidades).`,
            data.length > 0 ? "err" : "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function renderTablaInventario(data) {
    renderTabla("tbl-inventario", data.map(i => `
        <tr>
            <td>${i.bodega.nombre}</td>
            <td>${i.producto.nombre}</td>
            <td>${i.producto.categoria || "—"}</td>
            <td>${fmtMon(i.producto.precio)}</td>
            <td style="color:${i.stock < 10 ? "var(--red)" : "var(--text)"};font-weight:${i.stock < 10 ? 700 : 400}">
                ${i.stock} ${i.stock < 10 ? "⚠" : ""}
            </td>
        </tr>`));
}
