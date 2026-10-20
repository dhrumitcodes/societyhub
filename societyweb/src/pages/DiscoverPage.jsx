import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { eventApi } from '../api/eventApi';
import { dayOf, formatDateTime, monthOf } from '../utils/formatDate';
import Loader from '../components/common/Loader';
import ErrorMessage from '../components/common/ErrorMessage';

export default function DiscoverPage() {
    const [params] = useSearchParams();
    const societyId = params.get('society');
    const [search, setSearch] = useState('');
    const [query, setQuery] = useState('');
    const [page, setPage] = useState(0);
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const t = setTimeout(() => { setQuery(search); setPage(0); }, 300);
        return () => clearTimeout(t);
    }, [search]);

    useEffect(() => { setPage(0); }, [societyId]);

    useEffect(() => {
        let cancelled = false;
        setError(null);
        const q = { page, size: 10, search: query || undefined, upcoming: true };
        const request = societyId ? eventApi.getSocietyEvents(societyId, q) : eventApi.getEvents(q);
        request
            .then((d) => { if (!cancelled) setData(d); })
            .catch((e) => { if (!cancelled) setError(e); });
        return () => { cancelled = true; };
    }, [societyId, query, page]);

    return (
        <>
            <div className="page-head">
                <h1 className="greet">Upcoming events</h1>
                {societyId && <Link className="link" to="/discover">Show all societies</Link>}
            </div>
            <div className="toolbar">
                <input className="field" placeholder="Search events" aria-label="Search events" value={search} onChange={(e) => setSearch(e.target.value)} />
            </div>

            {error ? <ErrorMessage error={error} /> : !data ? <Loader /> : (
                <section className="panel">
                    {data.content.length === 0 ? (
                        <p className="muted empty">No upcoming events right now. Check back soon.</p>
                    ) : data.content.map((e) => (
                        <Link key={e.id} to={`/discover/societies/${e.societyId}/events/${e.id}`} className="event-row">
                            <span className="date-tile"><b>{dayOf(e.startTime)}</b><small>{monthOf(e.startTime)}</small></span>
                            <span style={{ flex: 1 }}>
                                <strong>{e.title}</strong>
                                <small>{e.societyName} · {formatDateTime(e.startTime)}{e.location ? ` · ${e.location}` : ''}</small>
                            </span>
                            <span className={`tone ${e.registered ? 'good' : e.registeredCount >= e.capacity ? 'warn' : 'muted'}`}>
                                {e.registered ? 'Registered' : e.registeredCount >= e.capacity ? 'Full' : `${e.registeredCount} of ${e.capacity} seats`}
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