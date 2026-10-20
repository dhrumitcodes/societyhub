import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useOutletContext, useParams } from 'react-router-dom';
import { eventApi } from '../api/eventApi';
import { useAuth } from '../context/AuthContext';
import { formatDateTime } from '../utils/formatDate';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

export default function EventDetailPage() {
    const { eventsPath } = useOutletContext();
    const { societyId, eventId } = useParams();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [event, setEvent] = useState(null);
    const [attendees, setAttendees] = useState([]);
    const [error, setError] = useState(null);
    const [actionError, setActionError] = useState('');
    const [busy, setBusy] = useState(false);

    const membership = user.memberships.find((m) => String(m.societyId) === societyId);
    const canManage = membership ? membership.canManage : user.role === 'COLLEGE_ADMIN';

    const load = useCallback(async () => {
        try {
            const e = await eventApi.getEventById(societyId, eventId);
            setEvent(e);
            if (canManage) setAttendees(await eventApi.getAttendees(eventId));
        } catch (err) {
            setError(err);
        }
    }, [societyId, eventId, canManage]);

    useEffect(() => {
        setEvent(null);
        setError(null);
        load();
    }, [load]);

    const toggleRegistration = async () => {
        setActionError('');
        setBusy(true);
        try {
            if (event.registered) await eventApi.cancelRegistration(eventId);
            else await eventApi.registerForEvent(eventId);
            await load();
        } catch (err) {
            setActionError(err.response?.data?.message || 'Something went wrong. Please try again.');
        } finally {
            setBusy(false);
        }
    };

    const remove = async () => {
        if (!window.confirm('Delete this event? This cannot be undone.')) return;
        setBusy(true);
        try {
            await eventApi.deleteEvent(societyId, eventId);
            navigate(eventsPath, { replace: true });
        } catch (e) {
            setError(e);
            setBusy(false);
        }
    };

    if (error) return <ErrorMessage error={error} />;
    if (!event) return <Loader />;

    const isPast = new Date(event.endTime) < new Date();
    const full = event.registeredCount >= event.capacity;
    const pct = Math.min(100, (event.registeredCount / event.capacity) * 100);

    return (
        <>
            <Link className="link" to={eventsPath}>Back to events</Link>
            <div className="page-head" style={{ marginTop: 12 }}>
                <h1 className="greet">{event.title}</h1>
                {canManage && (
                    <div className="actions-row" style={{ margin: 0 }}>
                        <Link className="btn-outline-link" to={`/societies/${societyId}/events/${eventId}/edit`}>Edit</Link>
                        <button type="button" className="btn-danger" onClick={remove} disabled={busy}>Delete</button>
                    </div>
                )}
            </div>

            <section className="panel detail">
                <dl>
                    <div><dt>Society</dt><dd>{event.societyName}</dd></div>
                    <div><dt>Starts</dt><dd>{formatDateTime(event.startTime)}</dd></div>
                    <div><dt>Ends</dt><dd>{formatDateTime(event.endTime)}</dd></div>
                    <div><dt>Location</dt><dd>{event.location || 'To be announced'}</dd></div>
                </dl>

                <div className="bar-row" style={{ padding: 0 }}>
                    <span className="bar-label">Seats taken</span>
                    <div className="bar-track"><div className={full ? 'hot' : ''} style={{ width: `${pct}%` }} /></div>
                    <span className="bar-val">{event.registeredCount} of {event.capacity}</span>
                </div>

                <div className="actions-row">
                    <button
                        type="button"
                        className={event.registered ? 'btn-danger' : 'btn-solid'}
                        onClick={toggleRegistration}
                        disabled={busy || (!event.registered && (isPast || full))}
                    >
                        {event.registered ? 'Cancel my registration' : isPast ? 'Event has ended' : full ? 'Fully booked' : 'Register for this event'}
                    </button>
                    {event.registered && <span className="tone good" style={{ alignSelf: 'center' }}>You are registered</span>}
                </div>
                {actionError && <p className="form-error-light" role="alert">{actionError}</p>}

                <h2 style={{ marginTop: 28 }}>About this event</h2>
                <p className="desc">{event.description || 'No description yet.'}</p>
            </section>

            {canManage && (
                <section className="panel table-wrap" style={{ marginTop: 18 }}>
                    <h2>Attendees ({attendees.length})</h2>
                    {attendees.length === 0 ? <p className="muted empty">Nobody has registered yet.</p> : (
                        <table>
                            <thead><tr><th>Name</th><th>Email</th></tr></thead>
                            <tbody>
                            {attendees.map((a) => (
                                <tr key={a.id}><td><strong>{a.userFullName}</strong></td><td className="muted">{a.userEmail}</td></tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </section>
            )}
        </>
    );
}