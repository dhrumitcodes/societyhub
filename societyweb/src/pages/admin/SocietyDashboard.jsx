import { useCallback, useEffect, useState } from 'react';
import { Link, useOutletContext } from 'react-router-dom';
import { societyApi } from '../../api/societyApi';
import { useAuth } from '../../context/AuthContext';
import { greeting, roleLabel } from '../../utils/roles';
import { dayOf, formatDateTime, monthOf } from '../../utils/formatDate';
import Icon from '../../components/common/Icon';
import Loader from '../../components/common/Loader';
import ErrorMessage from '../../components/common/ErrorMessage';

export default function SocietyDashboard() {
    const { societyId } = useOutletContext();
    const { user } = useAuth();
    const [data, setData] = useState(null);
    const [error, setError] = useState(null);

    const load = useCallback(() => {
        setError(null);
        setData(null);
        societyApi.getDashboard(societyId).then(setData).catch(setError);
    }, [societyId]);

    useEffect(load, [load]);

    if (error) return <ErrorMessage error={error} onRetry={load} />;
    if (!data) return <Loader />;

    const roles = Object.entries(data.membersByRole);

    return (
        <>
            <h1 className="greet">{greeting()}, {user.fullName.split(' ')[0]}</h1>
            <p className="muted greet-sub">Here is what is happening in {data.societyName}.</p>

            <div className="stats stats-3">
                <div className="stat">
                    <span className="stat-icon"><Icon name="users" size={24} /></span>
                    <div><small>Members</small><b>{data.memberCount}</b></div>
                </div>
                <div className="stat">
                    <span className="stat-icon"><Icon name="cal" size={24} /></span>
                    <div><small>Upcoming events</small><b>{data.upcomingEventCount}</b></div>
                </div>
                <div className="stat">
                    <span className="stat-icon"><Icon name="team" size={24} /></span>
                    <div><small>Your role</small><b className="stat-text">{data.myRole ? roleLabel(data.myRole) : 'College Admin'}</b></div>
                </div>
            </div>

            <div className="two">
                <section className="panel">
                    <div className="panel-head">
                        <h2>Upcoming events</h2>
                        <Link className="link" to={`/societies/${societyId}/events`}>See all</Link>
                    </div>
                    {data.upcomingEvents.length === 0 ? (
                        <p className="muted empty">No upcoming events yet.</p>
                    ) : (
                        data.upcomingEvents.map((e) => (
                            <Link key={e.id} to={`/societies/${societyId}/events/${e.id}`} className="event-row">
                                <span className="date-tile"><b>{dayOf(e.startTime)}</b><small>{monthOf(e.startTime)}</small></span>
                                <span><strong>{e.title}</strong><small>{formatDateTime(e.startTime)}{e.location ? ` · ${e.location}` : ''}</small></span>
                            </Link>
                        ))
                    )}
                </section>

                <section className="panel">
                    <h2>Members by role</h2>
                    {roles.length === 0 ? (
                        <p className="muted empty">No members yet.</p>
                    ) : (
                        roles.map(([role, count]) => (
                            <div className="bar-row" key={role}>
                                <span className="bar-label">{roleLabel(role)}</span>
                                <div className="bar-track"><div style={{ width: `${(count / data.memberCount) * 100}%` }} /></div>
                                <span className="bar-val short">{count}</span>
                            </div>
                        ))
                    )}
                </section>
            </div>
        </>
    );
}