export default function Logo({ size = 26 }) {
    return (
        <svg width={size} height={size} viewBox="-1 -1 2 2" aria-hidden="true">
            <path d="M0 -1 C0.6 -0.5 0.6 0.5 0 1 C-0.6 0.5 -0.6 -0.5 0 -1Z" fill="#E8B23C" />
        </svg>
    );
}