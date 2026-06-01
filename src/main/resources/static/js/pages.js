/* ============================================================
   pages.js — definitiile paginilor BiteWise
   ============================================================ */

/* ===================== DASHBOARD ===================== */
PAGES.dashboard = async function () {
    if (!State.user) {
        return emptyState('🤔', 'Nu s-a putut încărca utilizatorul demo. Verifică http://localhost:8080/api/users și repornește aplicația.');
    }
    const [metrics, progress, expiring, recipes] = await Promise.all([
        API.metrics(State.userId).catch(() => null),
        API.progress(State.userId, today()).catch(() => null),
        API.expiring(State.userId, 4).catch(() => []),
        API.recipes(State.userId).catch(() => []),
    ]);
    const weights = await API.weights(State.userId).catch(() => []);

    const pct = progress && progress.targetCalories > 0
        ? Math.min(100, progress.percentage) : 0;

    const ringColor = pct > 100 ? 'var(--berry)' : 'var(--caramel)';

    let spark = '';
    if (weights.length) {
        const vals = weights.map(w => w.recordedWeight);
        const min = Math.min(...vals), max = Math.max(...vals);
        const range = (max - min) || 1;
        spark = `<div class="spark">${weights.map(w => {
            const h = 20 + ((w.recordedWeight - min) / range) * 60;
            return `<div class="b" style="height:${h}%" title="${fmt(w.recordedWeight)} kg"></div>`;
        }).join('')}</div>`;
    }

    return `
    ${pageHead('Bună,', ((State.user && State.user.name) || 'utilizator').split(' ')[0], 'Iată un rezumat al zilei tale nutriționale.')}

    <div class="grid grid-4" style="margin-bottom:18px">
        <div class="stat green">
            <div class="label">BMI</div>
            <div class="value">${metrics ? fmt(metrics.bmi) : '—'}</div>
            <div class="sub">${metrics ? esc(metrics.bmiCategory) : 'completează profilul'}</div>
        </div>
        <div class="stat">
            <div class="label">Necesar zilnic (TDEE)</div>
            <div class="value">${metrics ? fmt(metrics.tdee) : '—'}<span class="unit"> kcal</span></div>
            <div class="sub">BMR ${metrics ? fmt(metrics.bmr) : '—'} kcal</div>
        </div>
        <div class="stat gold">
            <div class="label">Consumat azi</div>
            <div class="value">${progress ? fmt(progress.consumedCalories) : '0'}<span class="unit"> kcal</span></div>
            <div class="sub">${progress && progress.targetCalories > 0 ? 'din ' + fmt(progress.targetCalories) + ' kcal țintă' : 'fără țintă setată'}</div>
        </div>
        <div class="stat berry">
            <div class="label">Expiră curând</div>
            <div class="value">${expiring.length}</div>
            <div class="sub">alimente în 4 zile</div>
        </div>
    </div>

    <div class="grid grid-2">
        <div class="card">
            <div class="section-bar"><div class="section-title">Progres caloric — azi</div></div>
            <div class="ring-wrap">
                <svg class="ring" viewBox="0 0 120 120">
                    <circle cx="60" cy="60" r="52" fill="none" stroke="var(--cream-2)" stroke-width="14"/>
                    <circle cx="60" cy="60" r="52" fill="none" stroke="${ringColor}" stroke-width="14"
                        stroke-linecap="round" stroke-dasharray="${2*Math.PI*52}"
                        stroke-dashoffset="${2*Math.PI*52 * (1 - pct/100)}"
                        transform="rotate(-90 60 60)" style="transition:stroke-dashoffset .8s ease"/>
                    <text x="60" y="56" text-anchor="middle" font-family="Fraunces" font-size="22" font-weight="600" fill="var(--sage-900)">${fmt(pct)}%</text>
                    <text x="60" y="74" text-anchor="middle" font-size="9" fill="var(--muted)">din țintă</text>
                </svg>
                <div style="flex:1">
                    ${progress ? macroBars(progress.consumed) : '<p class="page-desc">Înregistrează mese în jurnal.</p>'}
                </div>
            </div>
        </div>

        <div class="card">
            <div class="section-bar"><div class="section-title">Evoluția greutății</div></div>
            ${spark || emptyState('⚖️', 'Încă nicio înregistrare')}
            ${weights.length ? `<p class="page-desc" style="margin-top:12px">De la ${fmt(weights[0].recordedWeight)} kg la ${fmt(weights[weights.length-1].recordedWeight)} kg.</p>` : ''}
        </div>
    </div>

    <div class="card" style="margin-top:18px">
        <div class="section-bar">
            <div class="section-title">Rețetele tale</div>
            <button class="btn btn-ghost btn-sm" onclick="navigate('recipes')">Vezi toate →</button>
        </div>
        <div class="grid grid-3">
            ${recipes.slice(0,3).map(r => `
                <div class="card hover" style="cursor:pointer" onclick="navigate('recipes')">
                    <div class="eyebrow">${r.ingredients.length} ingrediente</div>
                    <div class="section-title" style="font-size:1.05rem">${esc(r.title)}</div>
                </div>`).join('') || emptyState('🍳', 'Nicio rețetă încă')}
        </div>
    </div>`;
};

function macroBars(n) {
    if (!n) return '';
    const row = (label, val, color) => `
        <div class="macro">
            <div class="top"><span>${label}</span><b>${fmt(val)} g</b></div>
            <div class="bar"><span style="width:${Math.min(100, val)}%;background:${color}"></span></div>
        </div>`;
    return `<div class="macro-row">
        ${row('Proteine', n.protein, 'linear-gradient(90deg,var(--sage-500),var(--sage-700))')}
        ${row('Carbohidrați', n.carbs, 'linear-gradient(90deg,var(--gold),var(--caramel))')}
        ${row('Grăsimi', n.fats, 'linear-gradient(90deg,var(--berry),#d4727c)')}
    </div>`;
}

/* ===================== PROFIL ===================== */
PAGES.profile = async function () {
    const u = await API.user(State.userId);
    State.user = u;
    const m = await API.metrics(State.userId).catch(() => null);

    return `
    ${pageHead('Profilul', 'tău', 'Datele tale și calculele automate de BMI, BMR și necesar caloric.')}
    <div class="grid grid-2">
        <div class="card">
            <div class="section-title" style="margin-bottom:16px">Date personale</div>
            <div class="field"><label>Nume</label><input id="p_name" value="${esc(u.name)}"></div>
            <div class="field"><label>Email</label><input id="p_email" value="${esc(u.email)}"></div>
            <div class="field-row">
                <div class="field"><label>Greutate (kg)</label><input id="p_weight" type="number" step="0.1" value="${u.weight ?? ''}"></div>
                <div class="field"><label>Înălțime (cm)</label><input id="p_height" type="number" step="0.1" value="${u.height ?? ''}"></div>
            </div>
            <div class="field-row">
                <div class="field"><label>Vârstă</label><input id="p_age" type="number" value="${u.age ?? ''}"></div>
                <div class="field"><label>Sex</label>
                    <select id="p_gender">
                        <option value="male" ${u.gender==='male'?'selected':''}>Masculin</option>
                        <option value="female" ${u.gender==='female'?'selected':''}>Feminin</option>
                    </select>
                </div>
            </div>
            <button class="btn btn-primary" onclick="saveProfile()">💾 Salvează profilul</button>
        </div>

        <div class="card" style="background:linear-gradient(160deg,var(--sage-700),var(--sage-900));color:#fff;border:none">
            <div class="eyebrow" style="color:var(--gold)">Indicatori calculați</div>
            <div class="section-title" style="color:#fff;margin-bottom:22px">Compoziție & necesar</div>
            ${m ? `
            <div style="display:flex;flex-direction:column;gap:20px">
                <div>
                    <div style="font-size:.78rem;letter-spacing:.1em;text-transform:uppercase;color:var(--sage-300)">Indice de masă corporală</div>
                    <div style="font-family:var(--font-display);font-size:2.6rem;font-weight:600;line-height:1">${fmt(m.bmi)}</div>
                    <span class="tag ${bmiTagClass(m.bmi)}">${esc(m.bmiCategory)}</span>
                </div>
                <div style="display:flex;gap:30px">
                    <div>
                        <div style="font-size:.78rem;color:var(--sage-300);text-transform:uppercase;letter-spacing:.1em">BMR</div>
                        <div style="font-family:var(--font-display);font-size:1.7rem;font-weight:600">${fmt(m.bmr)}<span style="font-size:.9rem;color:var(--sage-300)"> kcal</span></div>
                        <div style="font-size:.75rem;color:var(--sage-300)">metabolism bazal</div>
                    </div>
                    <div>
                        <div style="font-size:.78rem;color:var(--sage-300);text-transform:uppercase;letter-spacing:.1em">TDEE</div>
                        <div style="font-family:var(--font-display);font-size:1.7rem;font-weight:600">${fmt(m.tdee)}<span style="font-size:.9rem;color:var(--sage-300)"> kcal</span></div>
                        <div style="font-size:.75rem;color:var(--sage-300)">necesar zilnic</div>
                    </div>
                </div>
                <p style="font-size:.82rem;color:var(--sage-300);line-height:1.5">Calcul prin formula Mifflin-St Jeor, factor de activitate moderat (1.375).</p>
            </div>` : `<p style="color:var(--sage-300)">Completează greutatea și înălțimea pentru calcule.</p>`}
        </div>
    </div>`;
};

function bmiTagClass(bmi) {
    if (bmi < 18.5) return 'warn';
    if (bmi < 25) return 'ok';
    if (bmi < 30) return 'warn';
    return 'danger';
}

async function saveProfile() {
    try {
        const body = {
            name: $('#p_name').value.trim(),
            email: $('#p_email').value.trim(),
            weight: parseFloat($('#p_weight').value) || null,
            height: parseFloat($('#p_height').value) || null,
            age: parseInt($('#p_age').value) || null,
            gender: $('#p_gender').value,
        };
        State.user = await API.updateUser(State.userId, body);
        toast('Profil salvat ✓');
        navigate('profile');
    } catch (e) { toast(e.message, true); }
}

/* ===================== GREUTATE ===================== */
PAGES.weight = async function () {
    const weights = await API.weights(State.userId);
    const vals = weights.map(w => w.recordedWeight);
    const min = vals.length ? Math.min(...vals) : 0;
    const max = vals.length ? Math.max(...vals) : 1;
    const range = (max - min) || 1;

    return `
    ${pageHead('Istoricul', 'greutății', 'Urmărește evoluția greutății în timp.',
        `<button class="btn btn-primary" onclick="addWeightModal()">+ Înregistrare</button>`)}

    <div class="card" style="margin-bottom:18px">
        <div class="section-title" style="margin-bottom:8px">Evoluție</div>
        ${vals.length ? `
        <div class="spark" style="height:140px">
            ${weights.map(w => {
                const h = 15 + ((w.recordedWeight - min) / range) * 75;
                return `<div class="b" style="height:${h}%" title="${esc(w.date)}: ${fmt(w.recordedWeight)} kg"></div>`;
            }).join('')}
        </div>` : emptyState('⚖️', 'Nicio înregistrare încă')}
    </div>

    <div class="card">
        <div class="section-title" style="margin-bottom:14px">Toate înregistrările</div>
        <div class="list">
            ${weights.slice().reverse().map(w => `
                <div class="row-item">
                    <div class="pill-ico">⚖️</div>
                    <div class="row-main">
                        <div class="t">${fmt(w.recordedWeight)} kg</div>
                        <div class="s">${esc(w.date)}</div>
                    </div>
                    <div class="row-actions">
                        <button class="btn btn-danger btn-sm" onclick="delWeight(${w.id})">Șterge</button>
                    </div>
                </div>`).join('') || emptyState('⚖️', 'Adaugă prima înregistrare')}
        </div>
    </div>`;
};

function addWeightModal() {
    openModal('Înregistrare greutate', 'Adaugă o nouă măsurătoare.',
        `<div class="field"><label>Greutate (kg)</label><input id="w_val" type="number" step="0.1" value="${State.user.weight ?? ''}"></div>
         <div class="field"><label>Data</label><input id="w_date" type="date" value="${today()}"></div>`,
        async () => {
            const weight = parseFloat($('#w_val').value);
            if (!weight) { toast('Introdu greutatea', true); return; }
            await API.addWeight(State.userId, { recordedWeight: weight, date: $('#w_date').value });
            closeModal(); toast('Înregistrat ✓'); navigate('weight');
        }, 'Adaugă');
}
async function delWeight(id) {
    try { await API.delWeight(State.userId, id); toast('Șters ✓'); navigate('weight'); }
    catch (e) { toast(e.message, true); }
}

/* ===================== OBIECTIVE ===================== */
PAGES.goals = async function () {
    const g = await API.goal(State.userId);
    const m = await API.metrics(State.userId).catch(() => null);
    const v = (x) => (x ?? '');

    return `
    ${pageHead('Obiective', 'nutriționale', 'Stabilește țintele zilnice de calorii și macronutrienți.')}
    <div class="grid grid-2">
        <div class="card">
            <div class="section-title" style="margin-bottom:16px">Țintele tale zilnice</div>
            <div class="field"><label>Tip obiectiv</label>
                <select id="g_type">
                    <option value="lose" ${g&&g.goalType==='lose'?'selected':''}>Slăbire</option>
                    <option value="maintain" ${g&&g.goalType==='maintain'?'selected':''}>Menținere</option>
                    <option value="gain" ${g&&g.goalType==='gain'?'selected':''}>Creștere masă</option>
                </select>
            </div>
            <div class="field"><label>Calorii țintă (kcal)</label><input id="g_cal" type="number" value="${g?v(g.targetCalories):''}"></div>
            <div class="field-row">
                <div class="field"><label>Proteine (g)</label><input id="g_prot" type="number" value="${g?v(g.targetProtein):''}"></div>
                <div class="field"><label>Carbohidrați (g)</label><input id="g_carb" type="number" value="${g?v(g.targetCarbs):''}"></div>
            </div>
            <div class="field-row">
                <div class="field"><label>Grăsimi (g)</label><input id="g_fat" type="number" value="${g?v(g.targetFats):''}"></div>
                <div class="field"><label>Zahăr max (g)</label><input id="g_sugar" type="number" value="${g?v(g.targetSugar):''}"></div>
            </div>
            <button class="btn btn-primary" onclick="saveGoal()">🎯 Salvează obiectivul</button>
        </div>
        <div class="card" style="background:var(--sage-100);border-color:var(--sage-300)">
            <div class="eyebrow">Sugestie</div>
            <div class="section-title" style="margin-bottom:14px">Bazat pe profilul tău</div>
            ${m ? `
            <p class="page-desc" style="color:var(--sage-700)">Necesarul tău estimat (TDEE) este de <b>${fmt(m.tdee)} kcal/zi</b>.</p>
            <div class="list" style="margin-top:16px">
                <div class="row-item" style="background:var(--paper)"><div class="pill-ico">📉</div><div class="row-main"><div class="t">${fmt(m.tdee-400)} kcal</div><div class="s">pentru slăbire (-400)</div></div></div>
                <div class="row-item" style="background:var(--paper)"><div class="pill-ico">⚖️</div><div class="row-main"><div class="t">${fmt(m.tdee)} kcal</div><div class="s">pentru menținere</div></div></div>
                <div class="row-item" style="background:var(--paper)"><div class="pill-ico">📈</div><div class="row-main"><div class="t">${fmt(m.tdee+300)} kcal</div><div class="s">pentru creștere (+300)</div></div></div>
            </div>` : `<p class="page-desc">Completează profilul pentru sugestii.</p>`}
        </div>
    </div>`;
};

async function saveGoal() {
    try {
        const body = {
            goalType: $('#g_type').value,
            targetCalories: parseFloat($('#g_cal').value) || null,
            targetProtein: parseFloat($('#g_prot').value) || null,
            targetCarbs: parseFloat($('#g_carb').value) || null,
            targetFats: parseFloat($('#g_fat').value) || null,
            targetSugar: parseFloat($('#g_sugar').value) || null,
        };
        await API.setGoal(State.userId, body);
        toast('Obiectiv salvat ✓');
        navigate('goals');
    } catch (e) { toast(e.message, true); }
}
