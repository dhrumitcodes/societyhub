import { useCallback, useEffect, useState } from 'react';
import { useOutletContext } from 'react-router-dom';
import { announcementApi } from '../api/announcementApi';
import { useAuth } from '../context/AuthContext';
import { formatDateTime } from '../utils/formatDate';
import { PRIORITY_OPTIONS, roleLabel } from '../utils/roles';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

const EMPTY = { title: '', content: '', priority: PRIORITY_OPTIONS[1] ?? PRIORITY_OPTIONS[0] };

export default function AnnouncementsPage() {
    const { societyId, membership } = useOutletContext();
    const { user } = useAuth();
    const canManage = membership ? membership.canManage : user.role === 'COLLEGE_ADMIN';

    const [items, setItems] = useState(null);
    const [error, setError] = useState(null);
    const [form, setForm] = useState(EMPTY);
    const [formError, setFormError] = useState('');
    const [busy, setBusy] = useState(false);

    const load = useCallback(() => {
        setError(null);
        announcementApi.list(societyId).then(setItems).catch(setError);
    }, [societyId]);

    useEffect(() => { setItems(null); load(); }, [load]);

    const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

    const post = async (e) => {
        e.preventDefault();
        setFormError('');
        setBusy(true);
        try {
            await announcementApi.create(societyId, form);
            setForm(EMPTY);
            load();
        } catch (err) {
            setFormError(err.response?.data?.message || 'Could not post the announcement.');
        } finally {
            setBusy(false);
        }
    };

    const remove = async (id) => {
        if (!window.confirm('Delete this announcement?')) return;
        try {
            await announcementApi.remove(societyId, id);
            load();
        } catch (err) {
            setFormError(err.response?.data?.message || 'Could not delete the announcement.');
        }
    };

    if (error) return <ErrorMessage error={error} onRetry={load} />;
    if (!items) return <Loader />;

    return (
        <>
            <h1 className="greet">Announcements</h1>
            <p className="muted greet-sub">News and notices for members of this society.</p>

            {canManage && (
                <form className="panel inline-form" onSubmit={post}>
                    <h2>Post an announcement</h2>
                    <label htmlFor="at">Title</label>
                    <input id="at" className="field full" value={form.title} onChange={set('title')} required />
                    <label htmlFor="ac">Message</label>
                    <textarea id="ac" className="field full" rows={4} value={form.content} onChange={set('content')} required />
                    <label htmlFor="ap">Priority</label>
                    <select id="ap" className="field" value={form.priority} onChange={set('priority')}>
                        {PRIORITY_OPTIONS.map((p) => <option key={p} value={p}>{roleLabel(p)}</option>)}
                    </select>
                    {formError && <p className="form-error-light" role="alert">{formError}</p>}
                    <div className="actions-row"><button type="submit" className="btn-solid" disabled={busy}>{busy ? 'Posting...' : 'Post announcement'}</button></div>
                </form>
            )}
            {!canManage && formError && <p className="form-error-light" role="alert">{formError}</p>}

            <section className="panel">
                {items.length === 0 ? <p className="muted empty">No announcements yet.</p> : items.map((a) => (
                    <article className="notice" key={a.id}>
                        <div className="notice-head">
                            <h3>{a.title}</h3>
                            <span className="tag">{roleLabel(a.priority)}</span>
                        </div>
                        <small>{formatDateTime(a.createdAt)}</small>
                        <p>{a.content}</p>
                        {canManage && <button type="button" className="btn-danger" onClick={() => remove(a.id)}>Delete</button>}
                    </article>
                ))}
            </section>
        </>
    );
}