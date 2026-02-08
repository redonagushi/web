(() => {
    const canvas = document.getElementById("bgParticles");
    if (!canvas) return;

    const ctx = canvas.getContext("2d", { alpha: true });

    let W = 0, H = 0, DPR = 1;
    const mouse = { x: null, y: null };

    function resizeCanvas() {
        DPR = Math.min(2, window.devicePixelRatio || 1);
        W = window.innerWidth;
        H = window.innerHeight;

        canvas.style.width = W + "px";
        canvas.style.height = H + "px";
        canvas.width = Math.floor(W * DPR);
        canvas.height = Math.floor(H * DPR);

        ctx.setTransform(DPR, 0, 0, DPR, 0, 0);
    }

    window.addEventListener("mousemove", (e) => {
        mouse.x = e.clientX;
        mouse.y = e.clientY;
    });
    window.addEventListener("mouseleave", () => {
        mouse.x = null;
        mouse.y = null;
    });

    class P {
        constructor() {
            this.x = Math.random() * W;
            this.y = Math.random() * H;
            //Shpejtesine e fijeve.
            this.vx = (Math.random() - 0.5) * 1.2;
            this.vy = (Math.random() - 0.5) * 1.2;
            this.r = Math.random() * 1.6 + 0.6;
        }
        step() {
            this.x += this.vx;
            this.y += this.vy;

            if (this.x < 0) { this.x = 0; this.vx *= -1; }
            if (this.x > W) { this.x = W; this.vx *= -1; }
            if (this.y < 0) { this.y = 0; this.vy *= -1; }
            if (this.y > H) { this.y = H; this.vy *= -1; }
        }
        draw() {
            ctx.beginPath();
            ctx.arc(this.x, this.y, this.r, 0, Math.PI * 2);
            ctx.fillStyle = "rgba(230,238,255,.55)";
            ctx.fill();
        }
    }

    const dist = (ax, ay, bx, by) => {
        const dx = ax - bx, dy = ay - by;
        return Math.sqrt(dx * dx + dy * dy);
    };

    function line(x1, y1, x2, y2, alpha, color = "200,220,255") {
        ctx.strokeStyle = `rgba(${color},${alpha})`;
        ctx.lineWidth = 1;
        ctx.beginPath();
        ctx.moveTo(x1, y1);
        ctx.lineTo(x2, y2);
        ctx.stroke();
    }

    let points = [];

    function rebuildPoints() {
        // më shumë pika (auto sipas ekranit)
        const COUNT = Math.max(110, Math.min(240, Math.floor((W * H) / 9000)));
        points = Array.from({ length: COUNT }, () => new P());
    }


    // ✅ IMPORTANT: resize first, THEN create points
    resizeCanvas();
    rebuildPoints();

    // ✅ On resize: resize canvas + rebuild points (no clustering)
    window.addEventListener("resize", () => {
        resizeCanvas();
        rebuildPoints();
    });

    function animate() {
        ctx.clearRect(0, 0, W, H);

        for (const p of points) {
            p.step();
            p.draw();
        }

        const MAX = 170;
        for (let i = 0; i < points.length; i++) {
            for (let j = i + 1; j < points.length; j++) {
                const d = dist(points[i].x, points[i].y, points[j].x, points[j].y);
                if (d < MAX) {
                    const a = (1 - d / MAX) * 0.22;
                    line(points[i].x, points[i].y, points[j].x, points[j].y, a);
                }
            }
        }

        if (mouse.x != null) {
            const GRAB = 330;
            for (const p of points) {
                const d = dist(p.x, p.y, mouse.x, mouse.y);
                if (d < GRAB) {
                    const a = (1 - d / GRAB) * 0.60;
                    line(p.x, p.y, mouse.x, mouse.y, a, "120,180,255");
                }
            }
        }

        requestAnimationFrame(animate);
    }

    animate();
})();
