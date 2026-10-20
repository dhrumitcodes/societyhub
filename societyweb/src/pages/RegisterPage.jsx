import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import LeafField from '../components/common/LeafField';
import Logo from '../components/common/Logo';

export default function RegisterPage() {
    const { register, user } = useAuth();
    const navigate = useNavigate();
    const [form, setForm] = useState({ fullName: '', email: '', password: '' });
    const [error, setError] = useState('');
    const [busy, setBusy] = useState(false);

    if (user) return <Navigate to="/" replace />;

    const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

    const submit = async (e) => {
        e.preventDefault();
        setError('');
        setBusy(true);
        try {
            await register(form);
            navigate('/', { replace: true });
        } catch (err) {
            if (!err.response) setError('Cannot reach the server. Is the backend running?');
            else setError(err.response.data?.message || 'Could not create the account. Please check the details.');
        } finally {
            setBusy(false);
        }
    };

    return (
        <div className="login">
            <LeafField />
            <div className="login-brand"><Logo /> Canopy</div>
            <section className="login-hero">
                <h1>Join your campus societies.</h1>
                <p>Create an account to discover events, register in a click and keep track of everything you have signed up for.</p>
            </section>
            <form className="login-card" onSubmit={submit}>
                <h2>Create account</h2>
                <p className="muted">It only takes a minute.</p>
                <label htmlFor="fn">Full name</label>
                <input id="fn" value={form.fullName} onChange={set('fullName')} required />
                <label htmlFor="em">College email</label>
                <input id="em" type="email" placeholder="name@college.edu" value={form.email} onChange={set('email')} required />
                <label htmlFor="pw">Password</label>
                <input id="pw" type="password" value={form.password} onChange={set('password')} required />
                {error && <p className="form-error" role="alert">{error}</p>}
                <button className="btn-primary" type="submit" disabled={busy}>{busy ? 'Creating account...' : 'Create account'}</button>
                <p className="login-foot muted">Already have an account? <Link className="amber-link" to="/login">Sign in</Link></p>
            </form>
        </div>
    );
}