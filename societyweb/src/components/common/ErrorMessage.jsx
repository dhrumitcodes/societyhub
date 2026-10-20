export default function ErrorMessage({ error, onRetry }) {
    const message = !error?.response
        ? 'Cannot reach the server.'
        : error.response.status === 403
            ? 'You do not have access to this.'
            : 'Something went wrong loading this.';
    return (
        <div className="error-box" role="alert">
            <span>{message}</span>
            {onRetry && <button type="button" className="btn-outline" onClick={onRetry}>Try again</button>}
        </div>
    );
}