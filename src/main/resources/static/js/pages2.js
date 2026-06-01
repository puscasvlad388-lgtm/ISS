/* ============================================================
   pages2.js — jurnal, cămară, rețete, ingrediente
   ============================================================ */

/* ===================== JURNAL ALIMENTAR ===================== */
PAGES.journal = async function () {
    const date = State._journalDate || today();
    const [meals, progress] = await Promise.all([
        API.meals(State.userId, date),
        API.progress(State.userId, date),
    ]);

    const pct = progress.targetCalories > 0 ? Math.min(100, progress.percentage) : 0;

    return `
    ${pageHead('Jurnal', 'alimentar', 'Înregistrează ce ai consumat. Cantitatea se scade automat din cămară.',
        `<button class="btn btn-primary" onclick="logMealModal()">+ Adaugă masă</button>`)}

    <div class="grid grid-2" style="margin-bottom:18px">
        <div class="card">
            <div class="section-bar">
                <div class="section-title">Progresul zilei</div>
                <input type="date" value="${date}" onchange="changeJournalDate(this.value)"
                    style="padding:7px 11px;border:1.5px solid var(--line);border-radius:9px;font-family:var(--font-body)">
            </div>
            <div style="display:flex;align-items:baseline;gap:10px;margin-bottom:6px">
                <span style="font-family:var(--font-display);font-size:2.4rem;font-weight:600;color:var(--sage-900)">${fmt(progress.consumedCalories)}</span>
                <span style="color:var(--muted)">/ ${progress.targetCalories>0?fmt(progress.targetCalories):'—'} kcal</span>
            </div>
            <div class="bar"><span style="width:${pct}%"></span></div>
            <p class="page-desc" style="margin-top:10px">
                ${progress.targetCalories>0
                    ? (progress.remaining>=0 ? `Îți mai rămân <b>${fmt(progress.remaining)} kcal</b> din țintă.` : `Ai depășit ținta cu <b>${fmt(-progress.remaining)} kcal</b>.`)
                    : 'Setează un obiectiv pentru a urmări progresul.'}
            </p>
        </div>
        <div class="card">
            <div class="section-title" style="margin-bottom:14px">Macronutrienți consumați</div>
            ${macroBars(progress.consumed)}
        </div>
    </div>

    <div class="card">
        <div class="section-title" style="margin-bottom:14px">Mesele zilei</div>
        <div class="list">
            ${meals.map(meal => `
                <div class="row-item">
                    <div class="pill-ico">🍽️</div>
                    <div class="row-main">
                        <div class="t">${esc(meal.ingredient.name)}</div>
                        <div class="s">${fmt(meal.amountConsumed)} ${UNIT_LABEL[meal.unit]} · ${new Date(meal.consumedAt).toLocaleTimeString('ro-RO',{hour:'2-digit',minute:'2-digit'})}</div>
                    </div>
                    <span class="tag">${fmt(meal.ingredient.calories * meal.amountConsumed/100)} kcal</span>
                    <div class="row-actions"><button class="btn btn-danger btn-sm" onclick="delMeal(${meal.id})">Șterge</button></div>
                </div>`).join('') || emptyState('📓', 'Nicio masă înregistrată în această zi')}
        </div>
    </div>`;
};

function changeJournalDate(d) { State._journalDate = d; navigate('journal'); }

async function logMealModal() {
    const ings = await API.ingredients();
    openModal('Înregistrează masă', 'Cantitatea consumată se va scădea din cămară (dacă există stoc).',
        `<div class="field"><label>Ingredient</label>
            <select id="ml_ing">${ings.map(i => `<option value="${i.id}">${esc(i.name)} (${fmt(i.calories)} kcal/100g)</option>`).join('')}</select>
         </div>
         <div class="field-row">
            <div class="field"><label>Cantitate</label><input id="ml_amt" type="number" step="1" value="100"></div>
            <div class="field"><label>Unitate</label><select id="ml_unit">${unitOptions('GRAMS')}</select></div>
         </div>
         <div class="field"><label>Data și ora</label><input id="ml_when" type="datetime-local" value="${new Date().toISOString().slice(0,16)}"></div>`,
        async () => {
            const body = {
                ingredientId: parseInt($('#ml_ing').value),
                amountConsumed: parseFloat($('#ml_amt').value),
                unit: $('#ml_unit').value,
                consumedAt: $('#ml_when').value ? $('#ml_when').value + ':00' : null,
            };
            if (!body.amountConsumed) { toast('Introdu cantitatea', true); return; }
            await API.logMeal(State.userId, body);
            closeModal(); toast('Masă înregistrată ✓ Stoc actualizat'); navigate('journal');
        }, 'Înregistrează');
}
async function delMeal(id) {
    try { await API.delMeal(State.userId, id); toast('Șters ✓'); navigate('journal'); }
    catch (e) { toast(e.message, true); }
}

/* ===================== CĂMARĂ ===================== */
PAGES.pantry = async function () {
    const items = await API.pantry(State.userId);
    const now = new Date(); now.setHours(0,0,0,0);

    const status = (d) => {
        if (!d) return { cls: '', txt: 'fără dată' };
        const days = Math.ceil((new Date(d) - now) / 864e5);
        if (days < 0) return { cls: 'danger', txt: 'expirat' };
        if (days <= 3) return { cls: 'danger', txt: days + ' zile' };
        if (days <= 7) return { cls: 'warn', txt: days + ' zile' };
        return { cls: 'ok', txt: days + ' zile' };
    };

    const expiring = items.filter(i => i.expirationDate && Math.ceil((new Date(i.expirationDate)-now)/864e5) <= 4);

    return `
    ${pageHead('Cămara', 'ta', 'Gestionează stocul de alimente și urmărește datele de expirare.',
        `<button class="btn btn-primary" onclick="addPantryModal()">+ Adaugă aliment</button>`)}

    ${expiring.length ? `
    <div class="card" style="margin-bottom:18px;background:#fbf0e6;border-color:#f0d3b3">
        <div style="display:flex;align-items:center;gap:10px">
            <span style="font-size:1.6rem">⏰</span>
            <div>
                <div style="font-weight:600;color:var(--caramel-dark)">Atenție: ${expiring.length} alimente expiră în curând</div>
                <div class="s" style="color:var(--muted);font-size:.85rem">${expiring.map(i=>esc(i.ingredient.name)).join(', ')}</div>
            </div>
        </div>
    </div>` : ''}

    <div class="card">
        <div class="section-title" style="margin-bottom:14px">Toate alimentele (${items.length})</div>
        <div class="list">
            ${items.map(it => {
                const st = status(it.expirationDate);
                return `<div class="row-item">
                    <div class="pill-ico">🧺</div>
                    <div class="row-main">
                        <div class="t">${esc(it.ingredient.name)}</div>
                        <div class="s">${fmt(it.quantity)} unități · expiră: ${it.expirationDate ? esc(it.expirationDate) : '—'}</div>
                    </div>
                    <span class="tag ${st.cls}">${st.txt}</span>
                    <div class="row-actions">
                        <button class="btn btn-ghost btn-sm" onclick='editPantryModal(${it.id}, ${it.ingredient.id}, ${it.quantity}, ${JSON.stringify(it.expirationDate)})'>Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="delPantry(${it.id})">Șterge</button>
                    </div>
                </div>`;
            }).join('') || emptyState('🧺', 'Cămara e goală')}
        </div>
    </div>`;
};

async function addPantryModal() {
    const ings = await API.ingredients();
    openModal('Adaugă în cămară', 'Adaugă un aliment în stoc.',
        `<div class="field"><label>Ingredient</label>
            <select id="pt_ing">${ings.map(i => `<option value="${i.id}">${esc(i.name)}</option>`).join('')}</select></div>
         <div class="field"><label>Cantitate</label><input id="pt_qty" type="number" step="1" value="100"></div>
         <div class="field"><label>Data expirării</label><input id="pt_exp" type="date" value="${today()}"></div>`,
        async () => {
            const body = { ingredientId: parseInt($('#pt_ing').value), quantity: parseFloat($('#pt_qty').value), expirationDate: $('#pt_exp').value || null };
            if (!body.quantity) { toast('Introdu cantitatea', true); return; }
            await API.addPantry(State.userId, body);
            closeModal(); toast('Adăugat în cămară ✓'); navigate('pantry');
        }, 'Adaugă');
}
async function editPantryModal(id, ingId, qty, exp) {
    const ings = await API.ingredients();
    openModal('Editează aliment', '',
        `<div class="field"><label>Ingredient</label>
            <select id="pt_ing">${ings.map(i => `<option value="${i.id}" ${i.id===ingId?'selected':''}>${esc(i.name)}</option>`).join('')}</select></div>
         <div class="field"><label>Cantitate</label><input id="pt_qty" type="number" step="1" value="${qty}"></div>
         <div class="field"><label>Data expirării</label><input id="pt_exp" type="date" value="${exp||''}"></div>`,
        async () => {
            const body = { ingredientId: parseInt($('#pt_ing').value), quantity: parseFloat($('#pt_qty').value), expirationDate: $('#pt_exp').value || null };
            await API.updPantry(State.userId, id, body);
            closeModal(); toast('Actualizat ✓'); navigate('pantry');
        }, 'Salvează');
}
async function delPantry(id) {
    try { await API.delPantry(State.userId, id); toast('Șters ✓'); navigate('pantry'); }
    catch (e) { toast(e.message, true); }
}

/* ===================== REȚETE ===================== */
PAGES.recipes = async function () {
    const recipes = await API.recipes(State.userId);
    return `
    ${pageHead('Rețetele', 'tale', 'Creează rețete, calculează valorile nutriționale și generează liste de cumpărături.',
        `<button class="btn btn-primary" onclick="recipeModal()">+ Rețetă nouă</button>`)}
    <div class="grid grid-2" id="recipeGrid">
        ${recipes.map(r => `
            <div class="card hover">
                <div class="section-bar">
                    <div>
                        <div class="eyebrow">${r.ingredients.length} ingrediente</div>
                        <div class="section-title">${esc(r.title)}</div>
                    </div>
                </div>
                <p class="page-desc" style="font-size:.88rem;margin:0 0 14px">${esc((r.instructions||'').slice(0,120))}${(r.instructions||'').length>120?'…':''}</p>
                <div class="list" style="margin-bottom:14px">
                    ${r.ingredients.slice(0,4).map(ri => `<div style="font-size:.85rem;color:var(--sage-700)">• ${fmt(ri.quantity)} ${UNIT_LABEL[ri.unit]} ${esc(ri.ingredient.name)}</div>`).join('')}
                    ${r.ingredients.length>4?`<div style="font-size:.82rem;color:var(--muted)">+ încă ${r.ingredients.length-4}</div>`:''}
                </div>
                <div class="row-actions" style="flex-wrap:wrap;gap:6px">
                    <button class="btn btn-ghost btn-sm" onclick="showNutrition(${r.id}, '${esc(r.title).replace(/'/g,"\\'")}')">🔥 Calorii</button>
                    <button class="btn btn-ghost btn-sm" onclick="showShopping(${r.id}, '${esc(r.title).replace(/'/g,"\\'")}')">🛒 Listă cumpărături</button>
                    <button class="btn btn-ghost btn-sm" onclick="editRecipe(${r.id})">✏️</button>
                    <button class="btn btn-danger btn-sm" onclick="delRecipe(${r.id})">🗑</button>
                </div>
            </div>`).join('') || emptyState('🍳', 'Nicio rețetă. Creează prima!')}
    </div>`;
};

let _recipeLines = [];
async function recipeModal(existing) {
    const ings = await API.ingredients();
    State._ingCache = ings;
    _recipeLines = existing ? existing.ingredients.map(ri => ({ ingredientId: ri.ingredient.id, quantity: ri.quantity, unit: ri.unit })) : [{ ingredientId: ings[0].id, quantity: 100, unit: 'GRAMS' }];

    openModal(existing ? 'Editează rețeta' : 'Rețetă nouă', 'Adaugă titlu, pași și ingrediente.',
        `<div class="field"><label>Titlu</label><input id="r_title" value="${existing?esc(existing.title):''}"></div>
         <div class="field"><label>Instrucțiuni</label><textarea id="r_instr">${existing?esc(existing.instructions):''}</textarea></div>
         <div class="field"><label>Ingrediente</label><div id="r_lines"></div>
            <span class="chip-add" onclick="addRecipeLine()">+ adaugă ingredient</span></div>`,
        async () => {
            const body = {
                title: $('#r_title').value.trim(),
                instructions: $('#r_instr').value.trim(),
                ingredients: readRecipeLines(),
            };
            if (!body.title) { toast('Introdu titlul', true); return; }
            if (existing) await API.updateRecipe(existing.id, body);
            else await API.createRecipe(State.userId, body);
            closeModal(); toast('Rețetă salvată ✓'); navigate('recipes');
        }, 'Salvează');
    renderRecipeLines();
}
function renderRecipeLines() {
    const ings = State._ingCache;
    $('#r_lines').innerHTML = _recipeLines.map((l, i) => `
        <div class="ing-line">
            <select onchange="_recipeLines[${i}].ingredientId=parseInt(this.value)">
                ${ings.map(g => `<option value="${g.id}" ${g.id===l.ingredientId?'selected':''}>${esc(g.name)}</option>`).join('')}
            </select>
            <input type="number" value="${l.quantity}" onchange="_recipeLines[${i}].quantity=parseFloat(this.value)">
            <select onchange="_recipeLines[${i}].unit=this.value">${unitOptions(l.unit)}</select>
            <button class="btn btn-danger btn-sm" onclick="removeRecipeLine(${i})">✕</button>
        </div>`).join('');
}
function addRecipeLine() { _recipeLines.push({ ingredientId: State._ingCache[0].id, quantity: 100, unit: 'GRAMS' }); renderRecipeLines(); }
function removeRecipeLine(i) { _recipeLines.splice(i,1); renderRecipeLines(); }
function readRecipeLines() { return _recipeLines.filter(l => l.ingredientId && l.quantity); }

async function editRecipe(id) { const r = await API.recipe(id); recipeModal(r); }
async function delRecipe(id) {
    try { await API.delRecipe(id); toast('Șters ✓'); navigate('recipes'); }
    catch (e) { toast(e.message, true); }
}

async function showNutrition(id, title) {
    const n = await API.recipeNutrition(id);
    openModal('🔥 ' + title, 'Valori nutriționale totale pentru rețetă.',
        `<div class="grid grid-2" style="gap:12px">
            <div class="stat gold"><div class="label">Calorii</div><div class="value">${fmt(n.calories)}<span class="unit"> kcal</span></div></div>
            <div class="stat green"><div class="label">Proteine</div><div class="value">${fmt(n.protein)}<span class="unit"> g</span></div></div>
            <div class="stat"><div class="label">Carbohidrați</div><div class="value">${fmt(n.carbs)}<span class="unit"> g</span></div></div>
            <div class="stat berry"><div class="label">Grăsimi</div><div class="value">${fmt(n.fats)}<span class="unit"> g</span></div></div>
         </div>
         <p class="page-desc" style="margin-top:14px">Din care zaharuri: <b>${fmt(n.sugars)} g</b>.</p>`,
        null);
}

async function showShopping(id, title) {
    const list = await API.shoppingList(id, State.userId);
    openModal('🛒 Listă cumpărături', title,
        `<div class="list">
            ${list.map(item => `
                <div class="row-item">
                    <div class="pill-ico">${item.toBuy>0?'🛒':'✓'}</div>
                    <div class="row-main">
                        <div class="t">${esc(item.ingredientName)}</div>
                        <div class="s">Necesar ${fmt(item.neededQuantity)} ${UNIT_LABEL[item.unit]} · în cămară ${fmt(item.inPantry)}</div>
                    </div>
                    ${item.toBuy>0 ? `<span class="tag warn">cumpără ${fmt(item.toBuy)}</span>` : `<span class="tag ok">ai destul</span>`}
                </div>`).join('') || emptyState('🛒', 'Rețeta nu are ingrediente')}
         </div>`,
        null);
}

/* ===================== INGREDIENTE ===================== */
PAGES.ingredients = async function () {
    const [ings, cats] = await Promise.all([API.ingredients(), API.categories()]);
    State.categories = cats;
    return `
    ${pageHead('Ingrediente', '& categorii', 'Baza de date de alimente cu valori nutriționale per 100g.',
        `<button class="btn btn-primary" onclick="ingredientModal()">+ Ingredient</button>`)}
    <div class="card" style="margin-bottom:16px">
        <input type="text" placeholder="🔍 Caută ingredient…" oninput="filterIngredients(this.value)"
            style="width:100%;padding:11px 15px;border:1.5px solid var(--line);border-radius:11px;font-family:var(--font-body);font-size:.95rem">
    </div>
    <div class="card">
        <div class="list" id="ingList">
            ${ings.map(ingredientRow).join('')}
        </div>
    </div>`;
};

function ingredientRow(i) {
    return `<div class="row-item" data-name="${esc((i.name||'').toLowerCase())}">
        <div class="pill-ico">🥕</div>
        <div class="row-main">
            <div class="t">${esc(i.name)} ${i.category?`<span class="tag" style="margin-left:6px">${esc(i.category.name)}</span>`:''}</div>
            <div class="s">${fmt(i.calories)} kcal · P ${fmt(i.protein)}g · C ${fmt(i.carbs)}g · G ${fmt(i.fats)}g (per 100g)</div>
        </div>
        <div class="row-actions">
            <button class="btn btn-ghost btn-sm" onclick='ingredientModal(${JSON.stringify(i).replace(/'/g,"&#39;")})'>Edit</button>
            <button class="btn btn-danger btn-sm" onclick="delIngredient(${i.id})">Șterge</button>
        </div>
    </div>`;
}
function filterIngredients(q) {
    q = q.toLowerCase();
    $$('#ingList .row-item').forEach(r => r.style.display = r.dataset.name.includes(q) ? '' : 'none');
}

function ingredientModal(existing) {
    const cats = State.categories;
    openModal(existing ? 'Editează ingredient' : 'Ingredient nou', 'Valori nutriționale per 100g.',
        `<div class="field"><label>Nume</label><input id="i_name" value="${existing?esc(existing.name):''}"></div>
         <div class="field"><label>Categorie</label>
            <select id="i_cat"><option value="">— fără —</option>
            ${cats.map(c => `<option value="${c.id}" ${existing&&existing.category&&existing.category.id===c.id?'selected':''}>${esc(c.name)}</option>`).join('')}</select></div>
         <div class="field-row">
            <div class="field"><label>Calorii</label><input id="i_cal" type="number" step="0.1" value="${existing?existing.calories:0}"></div>
            <div class="field"><label>Proteine</label><input id="i_prot" type="number" step="0.1" value="${existing?existing.protein:0}"></div>
         </div>
         <div class="field-row">
            <div class="field"><label>Carbohidrați</label><input id="i_carb" type="number" step="0.1" value="${existing?existing.carbs:0}"></div>
            <div class="field"><label>Zaharuri</label><input id="i_sug" type="number" step="0.1" value="${existing?existing.sugars:0}"></div>
            <div class="field"><label>Grăsimi</label><input id="i_fat" type="number" step="0.1" value="${existing?existing.fats:0}"></div>
         </div>`,
        async () => {
            const body = {
                name: $('#i_name').value.trim(),
                calories: parseFloat($('#i_cal').value)||0,
                protein: parseFloat($('#i_prot').value)||0,
                carbs: parseFloat($('#i_carb').value)||0,
                sugars: parseFloat($('#i_sug').value)||0,
                fats: parseFloat($('#i_fat').value)||0,
            };
            if (!body.name) { toast('Introdu numele', true); return; }
            const catId = $('#i_cat').value || null;
            if (existing) await API.updateIngredient(existing.id, body, catId);
            else await API.createIngredient(body, catId);
            closeModal(); toast('Salvat ✓'); navigate('ingredients');
        }, 'Salvează');
}
async function delIngredient(id) {
    try { await API.delIngredient(id); toast('Șters ✓'); navigate('ingredients'); }
    catch (e) { toast(e.message, true); }
}
