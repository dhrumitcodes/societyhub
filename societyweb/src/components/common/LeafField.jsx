import { useEffect, useRef } from "react";

// Adapted from the Originkit "Falling Canopy" component: JS, responsive, reduced-motion aware.
const VERT = `
attribute vec2 aCorner; attribute vec2 aPos; attribute float aAngle; attribute float aSize; attribute float aTint;
uniform float uAspect; uniform float uPx;
varying vec2 vUv; varying float vTint; varying float vShade; varying float vAA;
void main(){
  float c=cos(aAngle), s=sin(aAngle);
  vec2 off=vec2(aCorner.x*c-aCorner.y*s, aCorner.x*s+aCorner.y*c)*aSize;
  vec2 p=aPos+off;
  gl_Position=vec4(p.x/max(uAspect,0.0001), p.y, 0.0, 1.0);
  vUv=aCorner; vTint=aTint; vAA=1.3/max(aSize*uPx,1.0);
  vShade=0.62+0.38*abs(cos(aAngle));
}`;
const FRAG = `
precision mediump float;
uniform vec3 uBase; uniform vec3 uAccent;
varying vec2 vUv; varying float vTint; varying float vShade; varying float vAA;
void main(){
  float prof=pow(max(1.0-vUv.y*vUv.y,0.0),0.75)*0.55;
  float d=abs(vUv.x)-prof;
  float a=1.0-smoothstep(-vAA,vAA,d);
  if(a<=0.003) discard;
  vec3 col=mix(uBase,uAccent,vTint);
  float rib=1.0-smoothstep(0.0,0.045,abs(vUv.x));
  rib*=smoothstep(0.95,0.55,abs(vUv.y));
  col*=vShade*(1.0-0.30*rib);
  gl_FragColor=vec4(col*a,a);
}`;

const hex = (h) => { const n = parseInt(h.slice(1), 16); return [(n >> 16) / 255, ((n >> 8) & 255) / 255, (n & 255) / 255]; };
const DRAG = 1.7, SPIN = 2.6, SWIRL = 0.55, SWAY = 2.2, TRAVEL = 1.1;
const C = [-1, -1, 1, -1, -1, 1, 1, -1, 1, 1, -1, 1];

export default function LeafField({ background = "#0E1410", base = "#C9702B", accent = "#E8B23C", direction = 104 }) {
    const host = useRef(null);
    const cvs = useRef(null);

    useEffect(() => {
        const el = host.current, canvas = cvs.current;
        const gl = canvas.getContext("webgl", { antialias: true, alpha: false });
        if (!gl) return;
        const sh = (t, s) => { const o = gl.createShader(t); gl.shaderSource(o, s); gl.compileShader(o); return o; };
        const prog = gl.createProgram();
        gl.attachShader(prog, sh(gl.VERTEX_SHADER, VERT));
        gl.attachShader(prog, sh(gl.FRAGMENT_SHADER, FRAG));
        gl.linkProgram(prog);
        gl.useProgram(prog);
        gl.enable(gl.BLEND);
        gl.blendFunc(gl.ONE, gl.ONE_MINUS_SRC_ALPHA);

        const A = (n) => gl.getAttribLocation(prog, n), U = (n) => gl.getUniformLocation(prog, n);
        const loc = { corner: A("aCorner"), pos: A("aPos"), angle: A("aAngle"), size: A("aSize"), tint: A("aTint") };
        const bg = hex(background);
        gl.uniform3fv(U("uBase"), hex(base));
        gl.uniform3fv(U("uAccent"), hex(accent));

        const reduced = window.matchMedia("(prefers-reduced-motion: reduce)").matches;
        const N = window.innerWidth < 700 ? 350 : 900;
        const dir = (direction * Math.PI) / 180, dx = Math.sin(dir), dy = -Math.cos(dir), ppx = dy, ppy = -dx;
        const L = Array.from({ length: N }, () => ({}));
        let aspect = 1, R = 2, clock = 0;

        const spawn = (l, fill) => {
            const along = fill ? (Math.random() * 2 - 1) * R : -R, across = (Math.random() * 2 - 1) * R;
            Object.assign(l, { x: dx * along + ppx * across, y: dy * along + ppy * across, vx: 0, vy: 0, a: Math.random() * 6.28, spin: (Math.random() * 2 - 1) * SPIN, s: (0.625 + 0.75 * Math.random()) * 0.032, t: Math.random(), ph: Math.random() * 6.28, rt: 0.6 + Math.random() * 0.9 });
        };

        const resize = () => {
            const dpr = Math.min(window.devicePixelRatio || 1, 2);
            canvas.width = Math.max(1, el.clientWidth * dpr);
            canvas.height = Math.max(1, el.clientHeight * dpr);
            gl.viewport(0, 0, canvas.width, canvas.height);
            aspect = canvas.width / canvas.height;
            R = Math.hypot(aspect, 1) + 0.12;
        };
        const ro = new ResizeObserver(resize); ro.observe(el); resize();
        L.forEach((l) => spawn(l, true));

        let cx = 0, cy = 0, engaged = false;
        const onMove = (e) => {
            const r = el.getBoundingClientRect(), m = Math.min(r.width, r.height);
            cx = (e.clientX - r.left - r.width / 2) / m; cy = -(e.clientY - r.top - r.height / 2) / m; engaged = true;
        };
        window.addEventListener("pointermove", onMove);

        const buf = gl.createBuffer(), data = new Float32Array(N * 42);
        let last = 0, raf = 0;
        const frame = (now) => {
            const dt = last ? Math.min(0.05, (now - last) / 1000) * 2 : 0; last = now; clock += dt;
            let w = 0;
            for (const l of L) {
                const sw = Math.sin(clock * SWAY * l.rt + l.ph) * 0.5;
                let ax = (dx * TRAVEL + ppx * sw - l.vx) * DRAG, ay = (dy * TRAVEL + ppy * sw - l.vy) * DRAG;
                if (engaged) {
                    const ddx = l.x - cx, ddy = l.y - cy, d2 = ddx * ddx + ddy * ddy;
                    if (d2 < 0.64 && d2 > 1e-8) {
                        const d = Math.sqrt(d2), f = (1 - d / 0.8) * 4.4;
                        ax += ((ddx / d) * (1 - SWIRL) - (ddy / d) * SWIRL) * f; ay += ((ddy / d) * (1 - SWIRL) + (ddx / d) * SWIRL) * f;
                    }
                }
                l.vx += ax * dt; l.vy += ay * dt; l.x += l.vx * dt; l.y += l.vy * dt;
                l.a += (l.spin * 0.98 + (l.vx * ppx + l.vy * ppy) * 1.6) * dt;
                if (l.x * dx + l.y * dy > R || Math.abs(l.x * ppx + l.y * ppy) > 2 * R) spawn(l, false);
                for (let k = 0; k < 6; k++) { data.set([C[k * 2], C[k * 2 + 1], l.x, l.y, l.a, l.s, l.t], w); w += 7; }
            }
            gl.clearColor(bg[0], bg[1], bg[2], 1); gl.clear(gl.COLOR_BUFFER_BIT);
            gl.bindBuffer(gl.ARRAY_BUFFER, buf); gl.bufferData(gl.ARRAY_BUFFER, data, gl.DYNAMIC_DRAW);
            [[loc.corner, 2, 0], [loc.pos, 2, 8], [loc.angle, 1, 16], [loc.size, 1, 20], [loc.tint, 1, 24]].forEach(([p, n, o]) => {
                gl.enableVertexAttribArray(p); gl.vertexAttribPointer(p, n, gl.FLOAT, false, 28, o);
            });
            gl.uniform1f(U("uAspect"), aspect); gl.uniform1f(U("uPx"), canvas.height / 2);
            gl.drawArrays(gl.TRIANGLES, 0, N * 6);
            if (!reduced) raf = requestAnimationFrame(frame);
        };
        raf = requestAnimationFrame(frame);
        if (reduced) { frame(16); frame(32); }

        return () => { cancelAnimationFrame(raf); ro.disconnect(); window.removeEventListener("pointermove", onMove); };
    }, [background, base, accent, direction]);

    return (
        <div ref={host} style={{ position: "absolute", inset: 0, background }} aria-hidden="true">
            <canvas ref={cvs} style={{ width: "100%", height: "100%", display: "block" }} />
        </div>
    );
}