import { useEffect, useState } from 'react';
import { Link, Navigate, useNavigate, useOutletContext, useParams } from 'react-router-dom';
import { eventApi } from '../api/eventApi';
import { useAuth } from '../context/AuthContext';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

const EMPTY = { title: '', description: '', location: '', startTime: '', endTime: '' };
const toInput = (v) => (v ? v.slice(0, 16) : '');

export default function EventFormPage() {
    const { societyId, membership } = useOutletContext();
    const { eventId } = useParams(); // present when editing
    const { user } = useAuth();
    const navigate = useNavigate();
    const editing = Boolean(eventId);

    const [form, setForm] = useState(EMPTY);
    const [loading, setLoading] = useState(editing);
    const [loadError, setLoadError] = useState(null);
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);

    const canManage = membership ? membership.canManage : user.role === 'COLLEGE_ADMIN';

    useEffect(() => {
        if (!editing) return;
        eventApi.getEventById(societyId, eventId)
            .then((e) => setForm({
                title: e.title ?? '', description: e.description ?? '', location: e.location ?? '',
                startTime: toInput(e.startTime), endTime: toInput(e.endTime),
            }))
            .catch(setLoadError)
            .finally(() => setLoading(false));
    }, [editing, societyId, eventId]);

    if (!canManage) return <Navigate to={`/societies/${societyId}/events`} replace />;
    if (loadError) return <ErrorMessage error={loadError} />;
    if (loading) return <Loader />;

    const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

    const submit = async (e) => {
        e.preventDefault();
        setError('');
        if (form.endTime && form.endTime <= form.startTime) {
            return setError('The end time must be after the start time.');
        }
        setBusy(true);
        try {
            const saved = editing
                ? await eventApi.updateEvent(societyId, eventId, form)
                : await eventApi.createEvent(societyId, form);
            navigate(`/societies/${societyId}/events/${saved.id}`);
        } catch (err) {
            setError(err.response?.data?.message || (err.response ? 'Could not save the event. Check the details.' : 'Cannot reach the server.'));
        } finally {
            setBusy(false);
        }
    };

    return (
        <>
            <Link className="link" to={`/societies/${societyId}/events`}>Back to events</Link>
            <h1 className="greet" style={{ marginTop: 12 }}>{editing ? 'Edit event' : 'New event'}</h1>
            <form className="panel form-card" onSubmit={submit}>
                <label htmlFor="title">Title</label>
                <input id="title" className="field full" value={form.title} onChange={set('title')} required />

                <label htmlFor="desc">Description</label>
                <textarea id="desc" className="field full" rows={5} value={form.description} onChange={set('description')} />

                <label htmlFor="loc">Location</label>
                <input id="loc" className="field full" value={form.location} onChange={set('location')} />

                <div className="form-grid">
                    <div>
                        <label htmlFor="start">Starts</label>
                        <input id="start" type="datetime-local" className="field full" value={form.startTime} onChange={set('startTime')} required />
                    </div>
                    <div>
                        <label htmlFor="end">Ends</label>
                        <input id="end" type="datetime-local" className="field full" value={form.endTime} onChange={set('endTime')} required />
                    </div>
                </div>

                {error && <p className="form-error-light" role="alert">{error}</p>}
                <div className="actions-row">
                    <button type="submit" className="btn-solid" disabled={busy}>{busy ? 'Saving...' : editing ? 'Save changes' : 'Create event'}</button>
                    <Link className="btn-outline-link" to={`/societies/${societyId}/events`}>Cancel</Link>
                </div>
            </form>
        </>
    );
}