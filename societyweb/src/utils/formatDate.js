const opts = (o) => new Intl.DateTimeFormat('en-IN', o);

export const formatDate = (v) => (v ? opts({ day: 'numeric', month: 'short', year: 'numeric' }).format(new Date(v)) : '');
export const formatDateTime = (v) =>
    v ? opts({ day: 'numeric', month: 'short', hour: 'numeric', minute: '2-digit' }).format(new Date(v)) : '';
export const dayOf = (v) => new Date(v).getDate();
export const monthOf = (v) => opts({ month: 'short' }).format(new Date(v));