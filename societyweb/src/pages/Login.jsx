import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LeafField from '../components/common/LeafField';
import Logo from '../components/common/Logo';

export default function Login() {
    const { login, user } = useAuth();
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);

    if (user) return <Navigate to="/" replace />;

    const handleLogin = async (e) => {
        e.preventDefault();
        setError('');
        setBusy(true);
        try {
            await login(email, password);
            navigate('/', { replace: true });
        } catch (err) {
            if (!err.response) setError('Cannot reach the server. Is the backend running?');
            else if (err.response.status === 401 || err.response.status === 403) setError('Invalid email or password');
            else setError('Something went wrong. Please try again.');
        } finally {
            setBusy(false);
        }
    };

    return (
        <div className="login">
            <LeafField />
            <div className="login-brand"><Logo /> Canopy</div>
            <section className="login-hero">
                <h1>Every society on campus, in one place.</h1>
                <p>Open registrations, run events and keep your members organised, whichever society you lead.</p>
            </section>
            <form className="login-card" onSubmit={handleLogin}>
                <h2>Sign in</h2>
                <p className="muted">For office bearers of a society.</p>
                <label htmlFor="em">College email</label>
                <input id="em" type="email" placeholder="name@college.edu" value={email} onChange={(e) => setEmail(e.target.value)} required />
                <label htmlFor="pw">Password</label>
                <input id="pw" type="password" placeholder="Enter your password" value={password} onChange={(e) => setPassword(e.target.value)} required />
                {error && <p className="form-error" role="alert">{error}</p>}
                <button className="btn-primary" type="submit" disabled={busy}>{busy ? 'Signing in...' : 'Sign in'}</button>
                <p className="login-foot muted">Student looking to join? Register from your society's page.</p>
            </form>
        </div>
    );
}