import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { eventApi } from '../../api/eventApi';

export default function EventDetail() {
    const { societyId, eventId } = useParams();
    const [event, setEvent] = useState(null);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        eventApi.getEventById(societyId, eventId)
            .then(setEvent)
            .catch((err) => console.error('Failed to load event details', err));
    }, [societyId, eventId]);

    const handleRegister = async () => {
        try {
            await eventApi.registerForEvent(eventId);
            setMessage('Successfully registered for the event!');
        } catch (err) {
            setMessage(err.response?.data?.error || 'Failed to register');
        }
    };

    if (!event) return <p style={{ textAlign: 'center', marginTop: '50px' }}>Loading event details...</p>;

    return (
        <div style={{ maxWidth: '600px', margin: '40px auto', padding: '20px', border: '1px solid #ddd', borderRadius: '8px' }}>
            <button onClick={() => navigate(-1)} style={{ marginBottom: '15px' }}>&larr; Back</button>
            <h2>{event.title}</h2>
            <p><strong>Hosted by:</strong> {event.societyName}</p>
            <p><strong>Location:</strong> {event.location}</p>
            <p><strong>Description:</strong> {event.description}</p>
            <p><strong>Start Time:</strong> {new Date(event.startTime).toLocaleString()}</p>
            <p><strong>End Time:</strong> {new Date(event.endTime).toLocaleString()}</p>
            <p><strong>Capacity:</strong> {event.capacity}</p>

            {message && <p style={{ color: message.includes('Success') ? 'green' : 'red' }}>{message}</p>}

            <button
                onClick={handleRegister}
                style={{ marginTop: '15px', padding: '10px 20px', backgroundColor: '#007BFF', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}
            >
                Register for Event
            </button>
        </div>
    );
}