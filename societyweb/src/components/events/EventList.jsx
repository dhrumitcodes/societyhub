import { useEffect, useState } from 'react';
import { eventApi } from '../../api/eventApi';
import { Link } from 'react-router-dom';

export default function EventList() {
    const [events, setEvents] = useState([]);
    const [search, setSearch] = useState('');
    const [upcoming, setUpcoming] = useState(true);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchEvents();
    }, [upcoming]);

    const fetchEvents = async () => {
        setLoading(true);
        try {
            const data = await eventApi.getEvents({ search, upcoming, size: 10 });
            setEvents(data.content || []);
        } catch (err) {
            console.error('Failed to load events', err);
        } finally {
            setLoading(false);
        }
    };

    const handleSearchSubmit = (e) => {
        e.preventDefault();
        fetchEvents();
    };

    return (
        <div style={{ maxWidth: '800px', margin: '30px auto', padding: '20px' }}>
            <h2>Society Events</h2>

            <form onSubmit={handleSearchSubmit} style={{ marginBottom: '20px', display: 'flex', gap: '10px' }}>
                <input
                    type="text"
                    placeholder="Search events..."
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    style={{ padding: '8px', flex: 1 }}
                />
                <label style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <input
                        type="checkbox"
                        checked={upcoming}
                        onChange={(e) => setUpcoming(e.target.checked)}
                    />
                    Upcoming Only
                </label>
                <button type="submit" style={{ padding: '8px 15px' }}>Search</button>
            </form>

            {loading ? (
                <p>Loading events...</p>
            ) : events.length === 0 ? (
                <p>No events found.</p>
            ) : (
                <ul style={{ listStyle: 'none', padding: 0 }}>
                    {events.map((event) => (
                        <li key={event.id} style={{ border: '1px solid #ddd', padding: '15px', marginBottom: '10px', borderRadius: '6px' }}>
                            <h3>{event.title}</h3>
                            <p><strong>Society:</strong> {event.societyName}</p>
                            <p><strong>Location:</strong> {event.location}</p>
                            <p><strong>Starts:</strong> {new Date(event.startTime).toLocaleString()}</p>
                            <Link to={`/societies/${event.societyId}/events/${event.id}`}>View Details</Link>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}