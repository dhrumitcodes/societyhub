import { useState } from 'react';
import { authApi } from '../api/authApi';
import { useAuth } from '../context/AuthContext';

const EMPTY = { currentPassword: '', newPassword: '', confirm: '' };

export default function ChangePasswordPage() {
    const { user } = useAuth();
    const [form, setForm] = useState(EMPTY);
    const [error, setError] = useState('');
    const [done, setDone] = useState(false);
    const [busy, setBusy] = useState(false);

    const set = (k) => (e) => { setForm({ ...form, [k]: e.target.value }); setDone(false); };

    const submit = async (e) => {
        e.preventDefault();
        setError('');
        setDone(false);
        if (form.newPassword.length < 8) return setError('Your new password must be at least 8 characters.');
        if (form.newPassword !== form.confirm) return setError('The new passwords do not match.');
        setBusy(true);
        try {
            await authApi.changePassword({ currentPassword: form.currentPassword, newPassword: form.newPassword });
            setForm(EMPTY);
            setDone(true);
        } catch (err) {
            setError(err.response?.data?.message || 'Could not change the password. Please try again.');
        } finally {
            setBusy(false);
        }
    };

    return (
        <>
            <h1 className="greet">Account</h1>
            <p className="muted greet-sub">{user.fullName} · {user.email}</p>

            <form className="panel form-card" onSubmit={submit}>
                <h2>Change password</h2>
                <label htmlFor="cp">Current password</label>
                <input id="cp" type="password" autoComplete="current-password" className="field full" value={form.currentPassword} onChange={set('currentPassword')} required />
                <label htmlFor="np">New password</label>
                <input id="np" type="password" autoComplete="new-password" className="field full" value={form.newPassword} onChange={set('newPassword')} required />
                <label htmlFor="cf">Confirm new password</label>
                <input id="cf" type="password" autoComplete="new-password" className="field full" value={form.confirm} onChange={set('confirm')} required />
                {error && <p className="form-error-light" role="alert">{error}</p>}
                {done && <p className="tone good" role="status" style={{ marginTop: 16 }}>Your password has been changed.</p>}
                <div className="actions-row">
                    <button type="submit" className="btn-solid" disabled={busy}>{busy ? 'Saving...' : 'Change password'}</button>
                </div>
            </form>
        </>
    );
}