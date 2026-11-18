/* ═══════════════════════════════════════════════════════════
   LogiTrack S.A. — Lógica del panel ADMIN
   ═══════════════════════════════════════════════════════════ */

/* ── Guard: solo ADMIN ───────────────────────────────────── */
(function () {
    const u = getUsuario();
    if (!u || !getToken()) { window.location.replace("../index.html"); return; }
    if (u.rol !== "ADMIN")  { window.location.replace("empleado.html"); return; }
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

/* ── Stats rápidas al cargar ─────────────────────────────── */
async function cargarStats() {
    try {
        const [us, bo, pr, sb] = await Promise.allSettled([
            Usuarios.listar(), Bodegas.listar(), Productos.listar(), Inventario.stockBajo()
        ]);
        if (us.status === "fulfilled") document.getElementById("st-usuarios").textContent = us.value.length;
        if (bo.status === "fulfilled") document.getElementById("st-bodegas").textContent  = bo.value.length;
        if (pr.status === "fulfilled") document.getElementById("st-productos").textContent= pr.value.length;
        if (sb.status === "fulfilled") document.getElementById("st-stockbajo").textContent = sb.value.length;
    } catch (_) {}
}
cargarStats();

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
   USUARIOS
   ═══════════════════════════════════════════════════════════ */
async function crearUsuario() {
    const fb = document.getElementById("fb-cu");
    const nombre = document.getElementById("cu-nombre").value.trim();
    const correo = document.getElementById("cu-correo").value.trim();
    const telefono = document.getElementById("cu-tel").value.trim();

    if (!nombre || !correo) { mostrarFeedback(fb, "Nombre y correo son obligatorios.", "err"); return; }

    try {
        const r = await Usuarios.crear({ nombre, correo, telefono: telefono || null });
        mostrarFeedback(fb, `Usuario #${r.id} creado.`, "ok");
        document.getElementById("cu-nombre").value = "";
        document.getElementById("cu-correo").value = "";
        document.getElementById("cu-tel").value = "";
        await listarUsuarios();
        cargarStats();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function listarUsuarios() {
    const fb = document.getElementById("fb-lu");
    try {
        const data = await Usuarios.listar();
        renderTabla("tbl-usuarios", data.map(u => `
            <tr>
                <td>${u.id}</td>
                <td>${u.nombre}</td>
                <td>${u.correo}</td>
                <td>${u.telefono || "—"}</td>
                <td class="td-actions">
                    <button class="btn btn-sm btn-amber" onclick="prepararEdicionUsuario(${u.id},'${u.nombre}','${u.correo}','${u.telefono||''}')">Editar</button>
                    <button class="btn btn-sm btn-red" onclick="eliminarUsuario(${u.id})">Eliminar</button>
                </td>
            </tr>`));
        mostrarFeedback(fb, `${data.length} usuario(s) cargados.`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function prepararEdicionUsuario(id, nombre, correo, telefono) {
    document.getElementById("uu-id").value      = id;
    document.getElementById("uu-nombre").value  = nombre;
    document.getElementById("uu-correo").value  = correo;
    document.getElementById("uu-tel").value     = telefono;
    document.getElementById("uu-id-label").textContent = `#${id}`;
    document.getElementById("card-actualizar-usuario").style.display = "";
    document.getElementById("card-actualizar-usuario").scrollIntoView({ behavior: "smooth", block: "center" });
}

function cancelarEdicionUsuario() {
    document.getElementById("card-actualizar-usuario").style.display = "none";
    document.getElementById("fb-uu").textContent = "";
}

async function actualizarUsuario() {
    const fb     = document.getElementById("fb-uu");
    const id     = document.getElementById("uu-id").value;
    const nombre = document.getElementById("uu-nombre").value.trim();
    const correo = document.getElementById("uu-correo").value.trim();
    const telefono = document.getElementById("uu-tel").value.trim();

    if (!nombre || !correo) { mostrarFeedback(fb, "Nombre y correo son obligatorios.", "err"); return; }

    try {
        await Usuarios.actualizar(Number(id), { nombre, correo, telefono: telefono || null });
        mostrarFeedback(fb, `Usuario #${id} actualizado.`, "ok");
        setTimeout(() => cancelarEdicionUsuario(), 1500);
        await listarUsuarios();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function eliminarUsuario(id) {
    if (!confirm(`¿Eliminar usuario #${id}?`)) return;
    try {
        await Usuarios.eliminar(id);
        await listarUsuarios();
        cargarStats();
    } catch (e) { alert("Error: " + e.message); }
}

/* ═══════════════════════════════════════════════════════════
   BODEGAS
   ═══════════════════════════════════════════════════════════ */
async function crearBodega() {
    const fb = document.getElementById("fb-cb");
    const nombre    = document.getElementById("cb-nombre").value.trim();
    const ubicacion = document.getElementById("cb-ubicacion").value.trim();
    const capVal    = document.getElementById("cb-capacidad").value;
    const encVal    = document.getElementById("cb-encargado").value;

    if (!nombre) { mostrarFeedback(fb, "El nombre es obligatorio.", "err"); return; }

    const payload = {
        nombre,
        ubicacion: ubicacion || null,
        capacidad: capVal ? Number(capVal) : null,
        encargadoId: encVal ? Number(encVal) : null
    };

    try {
        const r = await Bodegas.crear(payload);
        mostrarFeedback(fb, `Bodega #${r.id} "${r.nombre}" creada.`, "ok");
        ["cb-nombre","cb-ubicacion","cb-capacidad","cb-encargado"].forEach(id => document.getElementById(id).value = "");
        await listarBodegas();
        cargarStats();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function listarBodegas() {
    const fb = document.getElementById("fb-lb");
    try {
        const data = await Bodegas.listar();
        renderTabla("tbl-bodegas", data.map(b => `
            <tr>
                <td>${b.id}</td>
                <td>${b.nombre}</td>
                <td>${b.ubicacion || "—"}</td>
                <td>${b.capacidad ?? "—"}</td>
                <td>${b.encargado ? b.encargado.nombre : "Sin asignar"}</td>
                <td class="td-actions">
                    <button class="btn btn-sm btn-amber" onclick="prepararEdicionBodega(${b.id},'${b.nombre}','${b.ubicacion||''}',${b.capacidad??'null'},${b.encargado?.id??'null'})">Editar</button>
                    <button class="btn btn-sm btn-red" onclick="eliminarBodega(${b.id})">Eliminar</button>
                </td>
            </tr>`));
        mostrarFeedback(fb, `${data.length} bodega(s) cargadas.`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function prepararEdicionBodega(id, nombre, ubicacion, capacidad, encargadoId) {
    document.getElementById("ub-id").value          = id;
    document.getElementById("ub-nombre").value      = nombre;
    document.getElementById("ub-ubicacion").value   = ubicacion;
    document.getElementById("ub-capacidad").value   = capacidad !== null ? capacidad : "";
    document.getElementById("ub-encargado").value   = encargadoId !== null ? encargadoId : "";
    document.getElementById("ub-id-label").textContent = `#${id}`;
    document.getElementById("card-actualizar-bodega").style.display = "";
    document.getElementById("card-actualizar-bodega").scrollIntoView({ behavior: "smooth", block: "center" });
}

function cancelarEdicionBodega() {
    document.getElementById("card-actualizar-bodega").style.display = "none";
    document.getElementById("fb-ub").textContent = "";
}

async function actualizarBodega() {
    const fb       = document.getElementById("fb-ub");
    const id       = document.getElementById("ub-id").value;
    const nombre   = document.getElementById("ub-nombre").value.trim();
    const ubicacion= document.getElementById("ub-ubicacion").value.trim();
    const capVal   = document.getElementById("ub-capacidad").value;
    const encVal   = document.getElementById("ub-encargado").value;

    if (!nombre) { mostrarFeedback(fb, "El nombre es obligatorio.", "err"); return; }

    try {
        await Bodegas.actualizar(Number(id), {
            nombre,
            ubicacion: ubicacion || null,
            capacidad: capVal ? Number(capVal) : null,
            encargadoId: encVal ? Number(encVal) : null
        });
        mostrarFeedback(fb, `Bodega #${id} actualizada.`, "ok");
        setTimeout(() => cancelarEdicionBodega(), 1500);
        await listarBodegas();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function eliminarBodega(id) {
    if (!confirm(`¿Eliminar bodega #${id}? Solo es posible si no tiene inventario ni movimientos.`)) return;
    try {
        await Bodegas.eliminar(id);
        await listarBodegas();
        cargarStats();
    } catch (e) { alert("Error: " + e.message); }
}

/* ═══════════════════════════════════════════════════════════
   PRODUCTOS
   ═══════════════════════════════════════════════════════════ */
async function crearProducto() {
    const fb = document.getElementById("fb-cp");
    const nombre   = document.getElementById("cp-nombre").value.trim();
    const categoria= document.getElementById("cp-categoria").value.trim();
    const precioV  = document.getElementById("cp-precio").value;

    if (!nombre || !precioV) { mostrarFeedback(fb, "Nombre y precio son obligatorios.", "err"); return; }
    if (Number(precioV) <= 0){ mostrarFeedback(fb, "El precio debe ser mayor a 0.", "err"); return; }

    try {
        const r = await Productos.crear({ nombre, categoria: categoria || null, precio: Number(precioV) });
        mostrarFeedback(fb, `Producto #${r.id} "${r.nombre}" creado.`, "ok");
        ["cp-nombre","cp-categoria","cp-precio"].forEach(id => document.getElementById(id).value = "");
        await listarProductos();
        cargarStats();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function listarProductos() {
    try {
        const data = await Productos.listar();
        renderTabla("tbl-productos", data.map(p => filaProducto(p)));
    } catch (e) { mostrarFeedback(document.getElementById("fb-fp"), e.message, "err"); }
}

async function buscarProductoId() {
    const id = document.getElementById("fp-id").value;
    const fb = document.getElementById("fb-fp");
    if (!id) { mostrarFeedback(fb, "Ingresa un ID.", "err"); return; }
    try {
        const p = await Productos.buscar(Number(id));
        renderTabla("tbl-productos", [filaProducto(p)]);
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function buscarProductoCat() {
    const cat = document.getElementById("fp-cat").value.trim();
    const fb  = document.getElementById("fb-fp");
    if (!cat) { mostrarFeedback(fb, "Ingresa una categoría.", "err"); return; }
    try {
        const data = await Productos.categoria(cat);
        renderTabla("tbl-productos", data.map(p => filaProducto(p)));
        mostrarFeedback(fb, `${data.length} resultado(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function filaProducto(p) {
    return `<tr>
        <td>${p.id}</td>
        <td>${p.nombre}</td>
        <td>${p.categoria || "—"}</td>
        <td>${fmtMon(p.precio)}</td>
        <td class="td-actions">
            <button class="btn btn-sm btn-amber" onclick="prepararEdicionProducto(${p.id},'${p.nombre}','${p.categoria||''}',${p.precio})">Editar</button>
            <button class="btn btn-sm btn-red" onclick="eliminarProducto(${p.id})">Eliminar</button>
        </td>
    </tr>`;
}

function prepararEdicionProducto(id, nombre, categoria, precio) {
    document.getElementById("up-id").value        = id;
    document.getElementById("up-nombre").value    = nombre;
    document.getElementById("up-categoria").value = categoria;
    document.getElementById("up-precio").value    = precio;
    document.getElementById("up-id-label").textContent = `#${id}`;
    document.getElementById("card-actualizar-producto").style.display = "";
    document.getElementById("card-actualizar-producto").scrollIntoView({ behavior: "smooth", block: "center" });
}

function cancelarEdicionProducto() {
    document.getElementById("card-actualizar-producto").style.display = "none";
    document.getElementById("fb-up").textContent = "";
}

async function actualizarProducto() {
    const fb       = document.getElementById("fb-up");
    const id       = document.getElementById("up-id").value;
    const nombre   = document.getElementById("up-nombre").value.trim();
    const categoria= document.getElementById("up-categoria").value.trim();
    const precio   = document.getElementById("up-precio").value;

    if (!nombre || !precio) { mostrarFeedback(fb, "Nombre y precio son obligatorios.", "err"); return; }
    if (Number(precio) <= 0){ mostrarFeedback(fb, "El precio debe ser mayor a 0.", "err"); return; }

    try {
        await Productos.actualizar(Number(id), { nombre, categoria: categoria || null, precio: Number(precio) });
        mostrarFeedback(fb, `Producto #${id} actualizado.`, "ok");
        setTimeout(() => cancelarEdicionProducto(), 1500);
        await listarProductos();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function eliminarProducto(id) {
    if (!confirm(`¿Eliminar producto #${id}?`)) return;
    try {
        await Productos.eliminar(id);
        await listarProductos();
        cargarStats();
    } catch (e) { alert("Error: " + e.message); }
}

/* ═══════════════════════════════════════════════════════════
   INVENTARIO
   ═══════════════════════════════════════════════════════════ */
async function registrarInventario() {
    const fb = document.getElementById("fb-inv-create");
    const bid   = document.getElementById("inv-bid").value;
    const pid   = document.getElementById("inv-pid").value;
    const stock = document.getElementById("inv-stock").value;

    if (!bid || !pid || !stock) { mostrarFeedback(fb, "Todos los campos son obligatorios.", "err"); return; }
    if (Number(stock) < 0)      { mostrarFeedback(fb, "El stock no puede ser negativo.", "err"); return; }

    try {
        await Inventario.registrar({ bodegaId: Number(bid), productoId: Number(pid), stock: Number(stock) });
        mostrarFeedback(fb, "Stock registrado correctamente.", "ok");
        ["inv-bid","inv-pid","inv-stock"].forEach(id => document.getElementById(id).value = "");
        await verInventarioGlobal();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function verInventarioGlobal() {
    const fb = document.getElementById("fb-inv-get");
    try {
        const data = await Inventario.listar();
        renderTablaInventario(data);
        mostrarFeedback(fb, `${data.length} registro(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function verInventarioBodega() {
    const fb = document.getElementById("fb-inv-get");
    const bid = document.getElementById("inv-bid-filtro").value;
    if (!bid) { mostrarFeedback(fb, "Ingresa el ID de la bodega.", "err"); return; }
    try {
        const data = await Inventario.porBodega(Number(bid));
        renderTablaInventario(data);
        mostrarFeedback(fb, `${data.length} registro(s) en bodega #${bid}.`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function verStockBajo() {
    const fb = document.getElementById("fb-inv-get");
    try {
        const data = await Inventario.stockBajo();
        renderTablaInventario(data);
        mostrarFeedback(fb, `${data.length} producto(s) con stock < 10.`, data.length > 0 ? "err" : "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function renderTablaInventario(data) {
    renderTabla("tbl-inventario", data.map(i => `
        <tr>
            <td>${i.bodega.nombre}</td>
            <td>${i.producto.nombre}</td>
            <td>${i.producto.categoria || "—"}</td>
            <td>${fmtMon(i.producto.precio)}</td>
            <td style="color:${i.stock < 10 ? "var(--red-pale)" : "var(--text)"};font-weight:${i.stock < 10 ? 700 : 400}">
                ${i.stock} ${i.stock < 10 ? "⚠" : ""}
            </td>
            <td class="td-actions">
                <button class="btn btn-sm btn-amber" onclick="prepararEdicionInventario(${i.bodega.id},'${i.bodega.nombre}',${i.producto.id},'${i.producto.nombre}',${i.stock})">Editar stock</button>
            </td>
        </tr>`));
}

function prepararEdicionInventario(bidVal, bNombre, pidVal, pNombre, stockActual) {
    document.getElementById("ui-bid").value   = bidVal;
    document.getElementById("ui-pid").value   = pidVal;
    document.getElementById("ui-stock").value = stockActual;
    document.getElementById("ui-label").textContent = `— ${pNombre} en ${bNombre}`;
    document.getElementById("card-actualizar-inv").style.display = "";
    document.getElementById("card-actualizar-inv").scrollIntoView({ behavior: "smooth", block: "center" });
}

function cancelarEdicionInventario() {
    document.getElementById("card-actualizar-inv").style.display = "none";
    document.getElementById("fb-ui").textContent = "";
}

async function actualizarInventario() {
    const fb    = document.getElementById("fb-ui");
    const bid   = document.getElementById("ui-bid").value;
    const pid   = document.getElementById("ui-pid").value;
    const stock = document.getElementById("ui-stock").value;

    if (stock === "") { mostrarFeedback(fb, "Ingresa el nuevo stock.", "err"); return; }
    if (Number(stock) < 0) { mostrarFeedback(fb, "El stock no puede ser negativo.", "err"); return; }

    try {
        await Inventario.actualizar({ bodegaId: Number(bid), productoId: Number(pid), stock: Number(stock) });
        mostrarFeedback(fb, "Stock actualizado correctamente.", "ok");
        setTimeout(() => cancelarEdicionInventario(), 1500);
        await verInventarioGlobal();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

/* ═══════════════════════════════════════════════════════════
   MOVIMIENTOS
   ═══════════════════════════════════════════════════════════ */
function toggleBodegas() {
    const tipo = document.getElementById("mov-tipo").value;
    document.getElementById("wrap-origen").style.display  = tipo === "ENTRADA" ? "none" : "";
    document.getElementById("wrap-destino").style.display = tipo === "SALIDA"  ? "none" : "";
}
toggleBodegas();

async function registrarMovimiento() {
    const fb   = document.getElementById("fb-mov-c");
    const tipo = document.getElementById("mov-tipo").value;
    const orig = document.getElementById("mov-origen").value;
    const dest = document.getElementById("mov-destino").value;

    const payload = { tipoMovimiento: tipo, bodegaOrigenId: null, bodegaDestinoId: null };

    if (tipo !== "ENTRADA")        payload.bodegaOrigenId  = Number(orig) || null;
    if (tipo !== "SALIDA")         payload.bodegaDestinoId = Number(dest) || null;

    if (tipo === "TRANSFERENCIA" && (!payload.bodegaOrigenId || !payload.bodegaDestinoId)) {
        mostrarFeedback(fb, "TRANSFERENCIA requiere bodega origen y destino.", "err"); return;
    }
    if (tipo === "SALIDA"  && !payload.bodegaOrigenId)  { mostrarFeedback(fb, "SALIDA requiere bodega origen.", "err"); return; }
    if (tipo === "ENTRADA" && !payload.bodegaDestinoId) { mostrarFeedback(fb, "ENTRADA requiere bodega destino.", "err"); return; }

    try {
        const r = await Movimientos.registrar(payload);
        mostrarFeedback(fb, `Movimiento #${r.id} (${r.tipoMovimiento}) registrado.`, "ok");
        document.getElementById("det-mid").value = r.id;
        await listarMovimientos();
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function agregarDetalle() {
    const fb   = document.getElementById("fb-det");
    const mid  = document.getElementById("det-mid").value;
    const pid  = document.getElementById("det-pid").value;
    const cant = document.getElementById("det-cant").value;

    if (!mid || !pid || !cant) { mostrarFeedback(fb, "Todos los campos son obligatorios.", "err"); return; }
    if (Number(cant) <= 0)     { mostrarFeedback(fb, "La cantidad debe ser mayor a 0.", "err"); return; }

    try {
        await Detalles.registrar({ movimientoId: Number(mid), productoId: Number(pid), cantidad: Number(cant) });
        mostrarFeedback(fb, `Detalle agregado al movimiento #${mid}.`, "ok");
        document.getElementById("det-pid").value = "";
        document.getElementById("det-cant").value = "";
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function listarMovimientos() {
    const fb = document.getElementById("fb-mov-f");
    try {
        const data = await Movimientos.listar();
        renderTablaMovimientos(data);
        mostrarFeedback(fb, `${data.length} movimiento(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function filtrarMovimientos() {
    const fb   = document.getElementById("fb-mov-f");
    const tipo = document.getElementById("filtro-tipo-mov").value;
    const desde= document.getElementById("filtro-desde").value;
    const hasta= document.getElementById("filtro-hasta").value;

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
        const info = data.map(d =>
            `• ${d.producto.nombre} — ${d.cantidad} unidades`
        ).join("\n") || "Sin productos registrados.";
        alert(`Productos del movimiento #${id}:\n\n${info}`);
    } catch (e) { alert("Error: " + e.message); }
}

/* ═══════════════════════════════════════════════════════════
   AUDITORÍA
   ═══════════════════════════════════════════════════════════ */
async function cargarAuditoria() {
    const fb = document.getElementById("fb-aud");
    try {
        const data = await Auditoria.listar();
        renderTablaAuditoria(data);
        mostrarFeedback(fb, `${data.length} registro(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

async function filtrarAuditoria() {
    const fb      = document.getElementById("fb-aud");
    const usuario = document.getElementById("aud-usuario").value.trim();
    const tipo    = document.getElementById("aud-tipo").value;
    const entidad = document.getElementById("aud-entidad").value.trim();

    try {
        let data;
        if (usuario)      data = await Auditoria.porUsuario(usuario);
        else if (tipo)    data = await Auditoria.porTipo(tipo);
        else if (entidad) data = await Auditoria.porEntidad(entidad);
        else              data = await Auditoria.listar();

        renderTablaAuditoria(data);
        mostrarFeedback(fb, `${data.length} registro(s).`, "ok");
    } catch (e) { mostrarFeedback(fb, e.message, "err"); }
}

function renderTablaAuditoria(data) {
    const colores = { INSERT: "var(--green)", UPDATE: "var(--amber)", DELETE: "var(--red)" };
    renderTabla("tbl-auditoria", data.map(a => `
        <tr>
            <td>${a.id}</td>
            <td style="color:${colores[a.tipoOperacion]};font-weight:700">${a.tipoOperacion}</td>
            <td>${a.entidadAfectada}</td>
            <td>${a.entidadId ?? "—"}</td>
            <td>${a.usuario}</td>
            <td>${fmtFecha(a.fechaHora)}</td>
            <td style="max-width:220px;white-space:normal;font-size:11px;color:var(--text-muted)">${a.detalles || "—"}</td>
        </tr>`));
}
