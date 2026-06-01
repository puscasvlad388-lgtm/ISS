/* ============================================================
   app.js — logica de UI pentru BiteWise (SPA simplu, vanilla JS)
   ============================================================ */

const State = {
    userId: null,
    user: null,
    categories: [],
    page: 'dashboard',
};

const UNITS = ['GRAMS', 'MILLILITERS', 'PIECES', 'SERVINGS', 'TABLESPOON', 'TEASPOON'];
const UNIT_LABEL = {
    GRAMS: 'g', MILLILITERS: 'ml', PIECES: 'buc',
    SERVINGS: 'porții', TABLESPOON: 'lingură', TEASPOON: 'linguriță'
};

const $  = (sel, root = document) => root.querySelector(sel);
const $$ = (sel, root = document) => Array.from(root.querySelectorAll(sel));
const el = (html) => { const t = document.createElement('template'); t.innerHTML = html.trim(); return t.content.firstChild; };
const fmt = (n) => (n == null ? '0' : (Math.round(n * 10) / 10).toLocaleString('ro-RO'));
const esc = (s) => (s == null ? '' : String(s).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c])));
const today = () => new Date().toISOString().slice(0, 10);

/* ---------- Toast ---------- */
let toastTimer;
function toast(msg, isErr = false) {
    const t = $('#toast');
    t.textContent = msg;
    t.className = 'toast show' + (isErr ? ' err' : '');
    clearTimeout(toastTimer);
    toastTimer = setTimeout(() => t.className = 'toast', 2600);
}

/* ---------- Modal ---------- */
function openModal(title, sub, bodyHtml, onSubmit, submitLabel = 'Salvează') {
    const box = $('#modalBox');
    box.innerHTML = `
        <h3>${esc(title)}</h3>
        ${sub ? `<p class="modal-sub">${esc(sub)}</p>` : ''}
        <div id="modalBody">${bodyHtml}</div>
        <div class="modal-foot">
            <button class="btn btn-ghost" id="modalCancel">Anulează</button>
            ${onSubmit ? `<button class="btn btn-primary" id="modalOk">${esc(submitLabel)}</button>` : ''}
        </div>`;
    $('#modalBackdrop').classList.add('open');
    $('#modalCancel').onclick = closeModal;
    if (onSubmit) {
        $('#modalOk').onclick = async () => {
            try { await onSubmit(); } catch (e) { toast(e.message, true); }
        };
    }
}
function closeModal() { $('#modalBackdrop').classList.remove('open'); }
$('#modalBackdrop').addEventListener('click', (e) => { if (e.target.id === 'modalBackdrop') closeModal(); });
document.addEventListener('keydown', (e) => { if (e.key === 'Escape') closeModal(); });

/* ---------- Helpers UI ---------- */
function pageHead(title, accent, desc, actionHtml = '') {
    return `<div class="page-head">
        <div>
            <h1 class="page-title">${esc(title)} ${accent ? `<span class="accent">${esc(accent)}</span>` : ''}</h1>
            ${desc ? `<p class="page-desc">${esc(desc)}</p>` : ''}
        </div>
        <div>${actionHtml}</div>
    </div>`;
}
function emptyState(icon, text) {
    return `<div class="empty"><div class="big">${icon}</div>${esc(text)}</div>`;
}
function unitOptions(selected) {
    return UNITS.map(u => `<option value="${u}" ${u === selected ? 'selected' : ''}>${UNIT_LABEL[u]}</option>`).join('');
}

/* ---------- Router ---------- */
const PAGES = {};   // populat de fiecare modul de pagina (vezi pages.js)

async function navigate(page) {
    State.page = page;
    $$('.nav-item').forEach(b => b.classList.toggle('active', b.dataset.page === page));
    const content = $('#content');
    content.innerHTML = `<div class="empty"><div class="big">🥗</div>Se încarcă…</div>`;
    try {
        const html = await PAGES[page]();
        content.innerHTML = html;
        content.classList.remove('fade-in'); void content.offsetWidth; content.classList.add('fade-in');
        if (PAGES['_after_' + page]) await PAGES['_after_' + page]();
    } catch (e) {
        content.innerHTML = emptyState('⚠️', 'Eroare: ' + e.message);
    }
}

$$('.nav-item').forEach(btn => btn.addEventListener('click', () => navigate(btn.dataset.page)));

/* ---------- Bootstrap ---------- */
async function boot() {
    try {
        const users = await API.users();
        if (!users.length) { $('#content').innerHTML = emptyState('🤔', 'Niciun utilizator. Reporneste aplicatia pentru date demo.'); return; }
        State.userId = users[0].id;
        State.user = users[0];
        State.categories = await API.categories();
        await navigate('dashboard');
    } catch (e) {
        $('#content').innerHTML = emptyState('⚠️', 'Nu pot contacta serverul: ' + e.message);
    }
}

document.addEventListener('DOMContentLoaded', boot);
