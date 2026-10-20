import { useEffect, useState } from 'react';
import { Link, useNavigate, useOutletContext, useParams } from 'react-router-dom';
import { eventApi } from '../api/eventApi';
import { useAuth } from '../context/AuthContext';
import { formatDateTime } from '../utils/formatDate';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

export default function EventDetailPage() {
    const { societyId, membership } = useOutletContext();
    const { eventId } = useParams();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [event, setEvent] = useState(null);
    const [error, setError] = useState(null);
    const [busy, setBusy] = useState(false);

    const canManage = membership ? membership.canManage : user.role === 'COLLEGE_ADMIN';

    useEffect(() => {
        let cancelled = false;
        setEvent(null);
        setError(null);
        eventApi.getEventById(societyId, eventId)
            .then((e) => { if (!cancelled) setEvent(e); })
            .catch((e) => { if (!cancelled) setError(e); });
        return () => { cancelled = true; };
    }, [societyId, eventId]);

    const remove = async () => {
        if (!window.confirm('Delete this event? This cannot be undone.')) return;
        setBusy(true);
        try {
            await eventApi.deleteEvent(societyId, eventId);
            navigate(`/societies/${societyId}/events`, { replace: true });
        } catch (e) {
            setError(e);
            setBusy(false);
        }
    };

    if (error) return <ErrorMessage error={error} />;
    if (!event) return <Loader />;

    return (
        <>
            <Link className="link" to={`/societies/${societyId}/events`}>Back to events</Link>
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
                <h2>About this event</h2>
                <p className="desc">{event.description || 'No description yet.'}</p>
            </section>
        </>
    );
}