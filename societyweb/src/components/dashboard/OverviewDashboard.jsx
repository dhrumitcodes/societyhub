import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import axiosClient from '../../api/axiosClient';

export default function OverviewDashboard() {
    const { societyId } = useParams();
    const [events, setEvents] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        axiosClient.get(`/societies/${societyId}/events?upcoming=true&size=5`)
            .then((res) => {
                setEvents(res.data.content || res.data || []);
            })
            .catch((err) => console.error('Failed to load overview events', err))
            .finally(() => setLoading(false));
    }, [societyId]);

    return (
        <div style={{ maxWidth: '1000px', display: 'flex', flexDirection: 'column', gap: '28px' }}>

            <div>
        <span style={{ fontSize: '13px', fontWeight: '500', color: '#6b7280', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
          Monday, October 19
        </span>
                <h1 style={{ fontSize: '28px', fontWeight: '700', color: '#111827', margin: '4px 0 6px 0' }}>
                    Good evening!
                </h1>
                <p style={{ fontSize: '15px', color: '#4b5563', margin: 0 }}>
                    Here's what's happening with your club.
                </p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '16px' }}>

                <div style={cardStyle}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: '#6b7280', fontSize: '14px' }}>
                        <span>📅 Upcoming events</span>
                    </div>
                    <div style={{ fontSize: '32px', fontWeight: '700', color: '#111827', marginTop: '8px' }}>
                        {events.length}
                    </div>
                </div>

                <div style={cardStyle}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: '#6b7280', fontSize: '14px' }}>
                        <span>👥 Total members</span>
                    </div>
                    <div style={{ fontSize: '32px', fontWeight: '700', color: '#111827', marginTop: '8px' }}>
                        248
                    </div>
                </div>

                <div style={cardStyle}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: '#6b7280', fontSize: '14px' }}>
                        <span>🎟️ Event registrations</span>
                    </div>
                    <div style={{ fontSize: '32px', fontWeight: '700', color: '#111827', marginTop: '8px' }}>
                        86
                    </div>
                </div>

                {/* Card 4 */}
                <div style={cardStyle}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', color: '#6b7280', fontSize: '14px' }}>
                        <span>⏱️ Pending approvals</span>
                    </div>
                    <div style={{ fontSize: '32px', fontWeight: '700', color: '#111827', marginTop: '8px' }}>
                        12
                    </div>
                </div>

            </div>

            <div style={{ backgroundColor: '#ffffff', border: '1px solid #e5e7eb', borderRadius: '12px', padding: '24px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                    <h2 style={{ fontSize: '18px', fontWeight: '600', color: '#111827', margin: 0 }}>Upcoming events</h2>
                    <Link to={`/societies/${societyId}/events`} style={{ fontSize: '14px', color: '#2563eb', textDecoration: 'none', fontWeight: '500' }}>
                        View all &rarr;
                    </Link>
                </div>

                {loading ? (
                    <p style={{ color: '#6b7280', fontSize: '14px' }}>Loading events...</p>
                ) : events.length === 0 ? (
                    <p style={{ color: '#6b7280', fontSize: '14px' }}>No upcoming events found.</p>
                ) : (
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                        {events.map((event, idx) => (
                            <div key={event.id || idx} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '16px', backgroundColor: '#f9fafb', borderRadius: '8px', border: '1px solid #f3f4f6' }}>
                                <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
                                    <div style={{ textAlign: 'center', minWidth: '45px' }}>
                    <span style={{ display: 'block', fontSize: '11px', fontWeight: '600', color: '#6b7280', textTransform: 'uppercase' }}>
                      {new Date(event.startTime).toLocaleString('en', { month: 'short' })}
                    </span>
                                        <span style={{ display: 'block', fontSize: '18px', fontWeight: '700', color: '#111827' }}>
                      {new Date(event.startTime).getDate()}
                    </span>
                                    </div>
                                    <div>
                                        <h4 style={{ fontSize: '15px', fontWeight: '600', color: '#111827', margin: '0 0 4px 0' }}>
                                            {event.title}
                                        </h4>
                                        <span style={{ fontSize: '13px', color: '#6b7280' }}>
                      {event.location || 'Main Auditorium'} · Registration open
                    </span>
                                    </div>
                                </div>
                                <div>
                  <span style={{ padding: '4px 10px', backgroundColor: '#dcfce7', color: '#166534', borderRadius: '20px', fontSize: '12px', fontWeight: '600' }}>
                    Live
                  </span>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

        </div>
    );
}

const cardStyle = {
    backgroundColor: '#ffffff',
    border: '1px solid #e5e7eb',
    borderRadius: '12px',
    padding: '20px',
    boxShadow: '0 1px 2px 0 rgba(0, 0, 0, 0.05)'
};