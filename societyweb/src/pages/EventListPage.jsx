import { useEffect, useState } from 'react';
import { Link, useOutletContext } from 'react-router-dom';
import { eventApi } from '../api/eventApi';
import { useAuth } from '../context/AuthContext';
import { dayOf, formatDateTime, monthOf } from '../utils/formatDate';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

export default function EventListPage() {
    const { societyId, membership } = useOutletContext();
    const { user } = useAuth();
    const canManage = membership ? membership.canManage : user.role === 'COLLEGE_ADMIN';
    const [search, setSearch] = useState('');
    const [query, setQuery] = useState('');
    const [when, setWhen] = useState('upcoming'); // upcoming | past | all
    const [page, setPage] = useState(0);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const t = setTimeout(() => { setQuery(search); setPage(0); }, 300);
        return () => clearTimeout(t);
    }, [search]);

    useEffect(() => {
        let cancelled = false;
        setError(null);
        const params = { page, size: 10, search: query || undefined, upcoming: when === 'all' ? undefined : when === 'upcoming' };
        eventApi.getSocietyEvents(societyId, params)
            .then((d) => { if (!cancelled) setData(d); })
            .catch((e) => { if (!cancelled) setError(e); });
        return () => { cancelled = true; };
    }, [societyId, query, when, page]);

    return (
        <>
            <div className="page-head">
                <h1 className="greet">Events</h1>
                {canManage && <Link className="btn-solid" to={`/societies/${societyId}/events/new`}>New event</Link>}
            </div>
            <div className="toolbar">
                <input className="field" placeholder="Search events" aria-label="Search events" value={search} onChange={(e) => setSearch(e.target.value)} />
                <select className="field" aria-label="Filter by time" value={when} onChange={(e) => { setWhen(e.target.value); setPage(0); }}>
                    <option value="upcoming">Upcoming</option>
                    <option value="past">Past</option>
                    <option value="all">All</option>
                </select>
            </div>

            {error ? <ErrorMessage error={error} /> : !data ? <Loader /> : (
                <section className="panel">
                    {data.content.length === 0 ? (
                        <p className="muted empty">No events found.</p>
                    ) : data.content.map((e) => (
                        <Link key={e.id} to={`/societies/${societyId}/events/${e.id}`} className="event-row">
                            <span className="date-tile"><b>{dayOf(e.startTime)}</b><small>{monthOf(e.startTime)}</small></span>
                            <span>
                                <strong>{e.title}</strong>
                                <small>{formatDateTime(e.startTime)}{e.location ? ` · ${e.location}` : ''}</small>
                            </span>
                        </Link>
                    ))}
                    {data.totalPages > 1 && (
                        <div className="pager">
                            <button className="btn-outline" disabled={data.number === 0} onClick={() => setPage(page - 1)}>Previous</button>
                            <span className="muted">Page {data.number + 1} of {data.totalPages}</span>
                            <button className="btn-outline" disabled={data.number + 1 >= data.totalPages} onClick={() => setPage(page + 1)}>Next</button>
                        </div>
                    )}
                </section>
            )}
        </>
    );
}