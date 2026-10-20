const P = {
    dash: "M3 3h7v9H3zM14 3h7v5h-7zM14 12h7v9h-7zM3 16h7v5H3z",
    cal: "M3 5h18v16H3zM3 10h18M8 3v4M16 3v4",
    users: "M9 4.5a3.5 3.5 0 100 7 3.5 3.5 0 000-7zM2.5 20c.5-3.5 3-5.5 6.5-5.5s6 2 6.5 5.5M16 4.5a3.5 3.5 0 010 7M18 14.8c2 .6 3.3 2.4 3.5 5",
    form: "M5 3h14v18H5zM9 8h6M9 12h6M9 16h3",
    wal: "M3 6h18v13H3zM3 10h18M16 14.5h2",
    bell: "M6 16v-5a6 6 0 0112 0v5l1.5 2h-15zM10 21h4",
    team: "M12 4.5a3.5 3.5 0 100 7 3.5 3.5 0 000-7zM5 20c.7-3.7 3.4-5.5 7-5.5s6.3 1.8 7 5.5",
    chk: "M4 12l5 5L20 6",
    doc: "M7 3h7l5 5v13H7zM14 3v5h5",
    set: "M12 9a3 3 0 100 6 3 3 0 000-6zM12 2v3M12 19v3M2 12h3M19 12h3M5 5l2 2M17 17l2 2M19 5l-2 2M7 17l-2 2",
    out: "M9 4H5v16h4M16 8l4 4-4 4M20 12H9",
    search: "M11 4a7 7 0 100 14 7 7 0 000-14zM20 20l-4-4",
    chev: "M6 9l6 6 6-6",
};

export default function Icon({ name, size = 20 }) {
    return (
        <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
            <path d={P[name]} />
        </svg>
    );
}