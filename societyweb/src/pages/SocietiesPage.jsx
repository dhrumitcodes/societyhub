import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { societyApi } from '../api/societyApi';
import { joinRequestApi } from '../api/joinRequestApi';
import { useAuth } from '../context/AuthContext';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

const EMPTY = { name: '', description: '', category: '' };

export default function SocietiesPage() {
    const { user } = useAuth();
    const isAdmin = user.role === 'COLLEGE_ADMIN';
    const [societies, setSocieties] = useState(null);
    const [pending, setPending] = useState(new Set());
    const [joinError, setJoinError] = useState('');
    const [error, setError] = useState(null);
    const [form, setForm] = useState(EMPTY);
    const [formError, setFormError] = useState('');
    const [busy, setBusy] = useState(false);

    const load = useCallback(() => {
        setError(null);
        Promise.all([societyApi.list(), joinRequestApi.mine()])
            .then(([list, requests]) => {
                setSocieties(list);
                setPending(new Set(requests.filter((r) => r.status === 'PENDING').map((r) => r.societyId)));
            })
            .catch(setError);
    }, []);
    useEffect(load, [load]);

    const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

    const create = async (e) => {
        e.preventDefault();
        setFormError('');
        setBusy(true);
        try {
            await societyApi.create(form);
            setForm(EMPTY);
            load();
        } catch (err) {
            setFormError(err.response?.data?.message || 'Could not create the society.');
        } finally {
            setBusy(false);
        }
    };

    const requestJoin = async (societyId) => {
        setJoinError('');
        try {
            await joinRequestApi.create(societyId);
            setPending(new Set([...pending, societyId]));
        } catch (err) {
            setJoinError(err.response?.data?.message || 'Could not send the request.');
        }
    };

    if (error) return <ErrorMessage error={error} onRetry={load} />;
    if (!societies) return <Loader />;

    const mine = new Set(user.memberships.map((m) => m.societyId));

    return (
        <>
            <h1 className="greet">Societies</h1>
            <p className="muted greet-sub">{societies.length} {societies.length === 1 ? 'society' : 'societies'} on campus.</p>

            {isAdmin && (
                <form className="panel inline-form" onSubmit={create}>
                    <h2>Create a society</h2>
                    <div className="form-grid">
                        <div><label htmlFor="sn">Name</label><input id="sn" className="field full" value={form.name} onChange={set('name')} required /></div>
                        <div><label htmlFor="sc">Category</label><input id="sc" className="field full" placeholder="Technical, Cultural..." value={form.category} onChange={set('category')} /></div>
                    </div>
                    <label htmlFor="sd">Description</label>
                    <input id="sd" className="field full" value={form.description} onChange={set('description')} />
                    {formError && <p className="form-error-light" role="alert">{formError}</p>}
                    <div className="actions-row"><button type="submit" className="btn-solid" disabled={busy}>{busy ? 'Creating...' : 'Create society'}</button></div>
                    <p className="muted" style={{ fontSize: 13, margin: '12px 0 0' }}>After creating it, open the society and add a president from the Members page.</p>
                </form>
            )}

            {joinError && <p className="form-error-light" role="alert">{joinError}</p>}

            {societies.length === 0 ? <p className="muted empty">No societies yet.</p> : (
                <div className="card-grid">
                    {societies.map((s) => (
                        <article className="society-card" key={s.id}>
                            {s.category && <span className="tag">{s.category}</span>}
                            <h3>{s.name}</h3>
                            <p className="muted">{s.description || 'No description yet.'}</p>
                            <div className="actions-row" style={{ marginTop: 'auto' }}>
                                <Link className="btn-outline-link" to={`/discover?society=${s.id}`}>View events</Link>
                                {(isAdmin || mine.has(s.id)) && <Link className="btn-solid" to={`/societies/${s.id}/overview`}>Open dashboard</Link>}
                                {!isAdmin && !mine.has(s.id) && (pending.has(s.id)
                                    ? <span className="tone warn" style={{ alignSelf: 'center' }}>Request pending</span>
                                    : <button type="button" className="btn-solid" onClick={() => requestJoin(s.id)}>Request to join</button>)}
                            </div>
                        </article>
                    ))}
                </div>
            )}
        </>
    );
}