/* ============================================================
   api.js — strat subtire peste fetch pentru API-ul BiteWise
   ============================================================ */

const API = {
    async request(method, url, body) {
        const opts = {
            method,
            headers: { 'Content-Type': 'application/json' }
        };
        if (body !== undefined) opts.body = JSON.stringify(body);

        const res = await fetch(url, opts);
        if (res.status === 204) return null;

        const text = await res.text();
        const data = text ? JSON.parse(text) : null;

        if (!res.ok) {
            const msg = (data && data.message) ? data.message : ('Eroare ' + res.status);
            throw new Error(msg);
        }
        return data;
    },

    get(url)        { return this.request('GET', url); },
    post(url, body) { return this.request('POST', url, body); },
    put(url, body)  { return this.request('PUT', url, body); },
    del(url)        { return this.request('DELETE', url); },

    // --- Endpoint-uri specifice ---
    users:        ()          => API.get('/api/users'),
    user:         (id)        => API.get('/api/users/' + id),
    createUser:   (u)         => API.post('/api/users', u),
    updateUser:   (id, u)     => API.put('/api/users/' + id, u),
    metrics:      (id)        => API.get('/api/users/' + id + '/metrics'),

    weights:      (uid)       => API.get(`/api/users/${uid}/weights`),
    addWeight:    (uid, b)    => API.post(`/api/users/${uid}/weights`, b),
    delWeight:    (uid, id)   => API.del(`/api/users/${uid}/weights/${id}`),

    goal:         (uid)       => API.get(`/api/users/${uid}/goal`).catch(() => null),
    setGoal:      (uid, g)    => API.put(`/api/users/${uid}/goal`, g),

    meals:        (uid, date) => API.get(`/api/users/${uid}/meals` + (date ? `?date=${date}` : '')),
    logMeal:      (uid, b)    => API.post(`/api/users/${uid}/meals`, b),
    delMeal:      (uid, id)   => API.del(`/api/users/${uid}/meals/${id}`),
    progress:     (uid, date) => API.get(`/api/users/${uid}/meals/progress` + (date ? `?date=${date}` : '')),

    pantry:       (uid)       => API.get(`/api/users/${uid}/pantry`),
    expiring:     (uid, d)    => API.get(`/api/users/${uid}/pantry/expiring?days=${d}`),
    addPantry:    (uid, b)    => API.post(`/api/users/${uid}/pantry`, b),
    updPantry:    (uid, id,b) => API.put(`/api/users/${uid}/pantry/${id}`, b),
    delPantry:    (uid, id)   => API.del(`/api/users/${uid}/pantry/${id}`),

    recipes:      (uid)       => API.get(`/api/users/${uid}/recipes`),
    recipe:       (id)        => API.get('/api/recipes/' + id),
    createRecipe: (uid, b)    => API.post(`/api/users/${uid}/recipes`, b),
    updateRecipe: (id, b)     => API.put('/api/recipes/' + id, b),
    delRecipe:    (id)        => API.del('/api/recipes/' + id),
    recipeNutrition: (id)     => API.get(`/api/recipes/${id}/nutrition`),
    shoppingList: (id, uid)   => API.get(`/api/recipes/${id}/shopping-list?userId=${uid}`),

    ingredients:  (q)         => API.get('/api/ingredients' + (q ? `?q=${encodeURIComponent(q)}` : '')),
    createIngredient: (b, c)  => API.post('/api/ingredients' + (c ? `?categoryId=${c}` : ''), b),
    updateIngredient: (id,b,c)=> API.put('/api/ingredients/' + id + (c ? `?categoryId=${c}` : ''), b),
    delIngredient:(id)        => API.del('/api/ingredients/' + id),

    categories:   ()          => API.get('/api/categories'),
    createCategory: (b)       => API.post('/api/categories', b),
    delCategory:  (id)        => API.del('/api/categories/' + id),
};
