package mDimension.world;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.g3d.VertexBatch3D;
import arc.math.Mathf;
import arc.math.geom.Mat3D;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.noise.Simplex;
import mindustry.graphics.g3d.PlanetParams;
import mindustry.type.Planet;
import mindustry.type.Sector;

/**
 * 可扩展星球基类 —— 支持星环、极光、能量护盾及任意自定义绘制。
 *
 * 核心修正：
 * 1. VertexBatch3D 需要自己 new
 * 2. 必须 batch.proj(projection) 设置投影矩阵
 * 3. 薄片几何体（星环、极光）需要禁用背面剔除，否则只能看到一面
 * 4. 添加距离淡出效果（像云一样，靠近时淡出）
 *
 * 用法示例：
 * <pre>
 * Planet myPlanet = new ExtendedPlanet("my-planet", Planets.sun, 2f) {{
 *     hasAtmosphere = true;
 *
 *     // 添加星环
 *     rings.add(new PlanetRing(1.5f, 2.2f, Color.valueOf("ccaa77"), 25f));
 *
 *     // 添加极光
 *     auroras.add(new PlanetAurora(Color.valueOf("44ff88"), 0.6f));
 *
 *     // 添加完全自定义的绘制
 *     postDraw.add((planet, params, proj, trans) -> {
 *         // 你的自定义 3D 绘制代码
 *     });
 * }};
 * </pre>
 */
public class ExtendedPlanet extends Planet {

    // ==================== 开关 ====================
    public boolean drawRings = true;
    public boolean drawAuroras = true;

    // ==================== 组件列表 ====================
    /** 星环列表，按顺序绘制 */
    public Seq<PlanetRing> rings = new Seq<>();
    /** 极光列表 */
    public Seq<PlanetAurora> auroras = new Seq<>();

    // ==================== 回调接口 ====================
    /** 在星球本体绘制**之前**执行的回调 */
    public Seq<DrawCallback> preDraw = new Seq<>();
    /** 在星球本体绘制**之后**执行的回调 */
    public Seq<DrawCallback> postDraw = new Seq<>();
    /** 绑定云层的透明度*/
    public boolean bindCloud = true;

    // ==================== 3D 批次 ====================
    /**
     * 复用的 3D 顶点批次。
     * 参数：20000=最大顶点数, false=无法线, true=有颜色, 0=无纹理坐标
     */
    protected static final VertexBatch3D batch = new VertexBatch3D(20000, false, true, 0);

    // ==================== 距离淡出参数 ====================
    /** 距离淡出起始倍数（camDist > radius * fadeFar 时不淡出） */
    public float fadeFar = 6f;
    /** 距离淡出结束倍数（camDist < radius * fadeNear 时完全透明） */
    public float fadeNear = 4.2f;

    public ExtendedPlanet(String name, Planet parent, float radius) {
        super(name, parent, radius);
    }

    public ExtendedPlanet(String name, Planet parent, float radius, int sectorSize) {
        super(name, parent, radius, sectorSize);
    }


    @Override
    public void draw(PlanetParams params, Mat3D projection, Mat3D transform) {
        // 设置投影矩阵
        batch.proj(projection);

        // ---- 前置绘制 ----
        for (DrawCallback cb : preDraw) {
            cb.draw(this, params, projection, transform);
        }

        // ---- 星球本体 ----
        super.draw(params, projection, transform);

        // ---- 后置绘制 ----
        if (drawRings) {
            for (PlanetRing ring : rings) ring.draw(this, params, projection, transform);
        }
        if (drawAuroras) {
            for (PlanetAurora aurora : auroras) aurora.draw(this, params, projection, transform);
        }
        for (DrawCallback cb : postDraw) {
            cb.draw(this, params, projection, transform);
        }

        // 确保所有顶点已提交
        batch.flush(Gl.triangles);
    }

    // ==================== 便捷方法 ====================

    public ExtendedPlanet addRing(float inner, float outer, Color color) {
        rings.add(new PlanetRing(inner, outer, color));
        return this;
    }

    public ExtendedPlanet addRing(float inner, float outer, Color color, float tilt) {
        rings.add(new PlanetRing(inner, outer, color, tilt));
        return this;
    }

    public ExtendedPlanet addPostDraw(DrawCallback callback) {
        postDraw.add(callback);
        return this;
    }

    public ExtendedPlanet addPreDraw(DrawCallback callback) {
        preDraw.add(callback);
        return this;
    }

    // ==================== 回调接口 ====================

    @FunctionalInterface
    public interface DrawCallback {
        void draw(Planet planet, PlanetParams params, Mat3D projection, Mat3D transform);
    }

    // ==================== 距离淡出工具 ====================

    /**
     * 计算基于相机距离的淡出系数。
     * 返回 0~1，0=完全透明，1=完全不透明。
     * 公式：当 camDist 在 [fadeNear*R, fadeFar*R] 之间时线性插值。
     */
    public static float calcFade(Planet planet, PlanetParams params, float fadeNear, float fadeFar) {
        if(planet instanceof ExtendedPlanet ep && ep.bindCloud){
            return params.planet == planet ? 1f - params.uiAlpha : 1f;
        }
        float camDist = params.camPos.len(); // 相机到星球中心的距离
        float near = planet.radius * fadeNear;
        float far = planet.radius * fadeFar;
        return Mathf.clamp((camDist - near) / (far - near));
    }

    /**
     * 应用淡出到颜色上。
     */
    public static Color applyFade(Color base, float fade) {
        return base.cpy().a(base.a * fade);
    }

    // ==================== 星环组件 ====================

    public static class PlanetRing {
        public float innerMul = 1.4f;
        public float outerMul = 2.0f;
        public Color color = Color.white.cpy();
        public float alpha = 0.55f;
        public int segments = 70;
        public Blending blending = Blending.additive;
        public boolean enabled = true;
        public float rotateSpeed = 0f;
        public float rotateOffset = 0f;
        public float alphaIn = 1f,alphaOut = 0.5f;

        /** 是否启用距离淡出 */
        public boolean useFade = true;
        /** 此星环独立的淡出参数（覆盖星球默认值） */
        public float fadeNear = -1f; // <0 时使用星球默认值
        public float fadeFar = -1f;

        public float cosT=0,sinT=0;

        public PlanetRing() {}

        public PlanetRing(float innerMul, float outerMul, Color color) {
            this.innerMul = innerMul;
            this.outerMul = outerMul;
            this.color = color;
        }

        public PlanetRing(float innerMul, float outerMul, Color color, float tilt) {
            this(innerMul, outerMul, color);
            this. cosT = Mathf.cos(tilt * Mathf.degRad);
            this. sinT = Mathf.sin(tilt * Mathf.degRad);
        }

        public PlanetRing(float innerMul, float outerMul, Color color, float cosT,float sinT) {
            this(innerMul, outerMul, color);
            this. cosT = cosT;
            this. sinT = sinT;
        }

        public void draw(Planet planet, PlanetParams params, Mat3D projection, Mat3D transform) {
            if (!enabled) return;

            // 先提交残留顶点
            batch.flush(Gl.triangles);
            blending.apply();

            // ---- 关键修复：禁用背面剔除，让薄片双面可见 ----
            Gl.disable(Gl.cullFace);
            Gl.depthMask(false);

            float inner = planet.radius * innerMul;
            float outer = planet.radius * outerMul;

            // 自转
            float rotOffset = rotateOffset + (rotateSpeed * Time.globalTime);

            // ---- 距离淡出 ----
            float fade = 1f;
            if (useFade) {
                float fn = fadeNear > 0 ? fadeNear : ((ExtendedPlanet)planet).fadeNear;
                float ff = fadeFar > 0 ? fadeFar : ((ExtendedPlanet)planet).fadeFar;
                fade = calcFade(planet, params, fn, ff);
            }
            if (fade <= 0.01f) {
                Gl.enable(Gl.cullFace);
                Gl.cullFace(Gl.back);
                Blending.normal.apply();
                return;
            }

            Color cIn = color.cpy().a(alpha * alphaIn * fade);
            Color cOut = color.cpy().a(alpha * alphaOut * fade);

            Vec3 v1 = new Vec3(), v2 = new Vec3(), v3 = new Vec3(), v4 = new Vec3();

            for (int i = 0; i < segments; i++) {
                float a1 = ((i / (float) segments) * Mathf.PI2) + rotOffset * Mathf.degRad;
                float a2 = (((i + 1) / (float) segments) * Mathf.PI2) + rotOffset * Mathf.degRad;

                float cos1 = Mathf.cos(a1), sin1 = Mathf.sin(a1);
                float cos2 = Mathf.cos(a2), sin2 = Mathf.sin(a2);

                // 本地坐标（XZ 平面）
                v1.set(cos1 * inner, 0f, sin1 * inner);
                v2.set(cos1 * outer, 0f, sin1 * outer);
                v3.set(cos2 * outer, 0f, sin2 * outer);
                v4.set(cos2 * inner, 0f, sin2 * inner);

                // 倾斜（绕 X 轴）
                tilt(cosT, 0,sinT,v1,v2,v3,v4);

                // 本地 -> 世界坐标
                transform(v1, transform);
                transform(v2, transform);
                transform(v3, transform);
                transform(v4, transform);

                // 三角形 1: v1-v2-v3
                vertex(v1, cIn);
                vertex(v2, cOut);
                vertex(v3, cOut);

                // 三角形 2: v1-v3-v4
                vertex(v1, cIn);
                vertex(v3, cOut);
                vertex(v4, cIn);
            }

            batch.flush(Gl.triangles);

            // ---- 恢复背面剔除 ----
            Gl.enable(Gl.cullFace);
            Gl.cullFace(Gl.back);
            Gl.depthMask(true);
            Blending.normal.apply();
        }
    }

    // ==================== 极光组件 ====================

    public static class PlanetAurora {
        public Color color = Color.valueOf("62DE78");
        public float polarAngle = 17;
        public int segments = 80;
        public float animSpeed = 0.07f;
        public boolean enabled = true;
        public boolean bothPoles = true;
        public float height = 0.13f;
        public float heightScl = 0.4f;
        public float minHeight = 0.02f;
        /** 是否启用距离淡出 */
        public boolean useFade = true;
        public float fadeNear = -1f;
        public float fadeFar = -1f;
        public float scl = 0.45f;
        public float rad = 0.4f;
        public float cosT = 0,sinT = 0;
        public PlanetAurora() {}


        public PlanetAurora(Color color, float cosT, float sinT, float polarAngle) {
            this.color = color;
            this.cosT = cosT;
            this.sinT = sinT;
            this.polarAngle = polarAngle;
        }

        public void draw(Planet planet, PlanetParams params, Mat3D projection, Mat3D transform) {
            if (!enabled) return;

            batch.flush(Gl.triangles);
            Blending.additive.apply();

            // ---- 关键修复：禁用背面剔除 ----
            Gl.disable(Gl.cullFace);
            Gl.depthMask(false);
            // ---- 距离淡出 ----
            float fade = 1f;
            if (useFade) {
                float fn = fadeNear > 0 ? fadeNear : ((ExtendedPlanet)planet).fadeNear;
                float ff = fadeFar > 0 ? fadeFar : ((ExtendedPlanet)planet).fadeFar;
                fade = calcFade(planet, params, fn, ff);
            }
            if (fade <= 0.01f) {
                Gl.enable(Gl.cullFace);
                Gl.cullFace(Gl.back);
                Blending.normal.apply();
                return;
            }

            float time = Time.globalTime * animSpeed;
            float r = rad;

            if (bothPoles) {
                drawAuroraBand(planet, transform,2, 1, time, fade,1);
                drawAuroraBand(planet, transform,2, -1, time + 100f, fade,1);

                drawAuroraBand(planet, transform,2, 1, time, fade,-0.35f);
                drawAuroraBand(planet, transform,2, -1, time + 100f, fade,-0.35f);
            } else {
                drawAuroraBand(planet, transform,2, 1, time, fade,1);
                drawAuroraBand(planet, transform,2, 1, time, fade,-0.35f);
            }

            batch.flush(Gl.triangles);

            // ---- 恢复背面剔除 ----
            Gl.enable(Gl.cullFace);
            Gl.cullFace(Gl.back);
            Gl.depthMask(true);
            Blending.normal.apply();
        }

        private void drawAuroraBand(Planet planet, Mat3D transform, float noiseScale,
                                    float poleSign, float timeOffset, float fade,float heightT) {
            float R = planet.radius + this.height;
            float theta = polarAngle * Mathf.degRad;
            float dt = timeOffset * 0.03f;
            Color cOut = Color.clear;

            // 预计算每个角度点的属性（确保首尾相接）
            float[] topOffsets = new float[segments + 1];
            float[] lights = new float[segments + 1];
            float[] sinTs = new float[segments+1];
            float[] cosTs = new float[segments+1];
            for (int i = 0; i <= segments; i++) {
                float a = (i / (float) segments) * Mathf.PI2;
                float ox = Mathf.cos(a);
                float oy = Mathf.sin(a);

                float dst = (Simplex.noise2d(114, 3, 2, noiseScale*0.3f, ox + dt, oy) + 1f) / 2f;
                float h = (Simplex.noise2d(514, 2, 1, noiseScale, ox - dt, oy) + 1f) / 2f;

                // 顶边沿法线向外的总偏移
                topOffsets[i] =  (h * heightScl + minHeight) * heightT;
                lights[i] = (Simplex.noise2d(1918, 3, 3.5, noiseScale*0.2f, ox + dt, oy) + 1f) / 2f;

                sinTs[i] = Mathf.sin(theta + dst * scl);   // xz 平面半径系数
                cosTs[i] = Mathf.cos(theta + dst * scl);   // Y 高度系数
            }

            for (int i = 0; i < segments; i++) {
                float a1 = (i / (float) segments) * Mathf.PI2;
                float a2 = ((i + 1) / (float) segments) * Mathf.PI2;
                float ox1 = Mathf.cos(a1), oy1 = Mathf.sin(a1);
                float ox2 = Mathf.cos(a2), oy2 = Mathf.sin(a2);
                float sinT1 = sinTs[i],cosT1 = cosTs[i];
                float sinT2 = sinTs[(i + 1) % segments],cosT2 = cosTs[(i + 1) % segments];

                // ========== 底边：严格在球面上，极角为 theta ==========
                // 球坐标：x = R·sinθ·cosφ,  y = R·cosθ·poleSign,  z = R·sinθ·sinφ
                Vec3 v2 = new Vec3(R * sinT1 * ox1, R * cosT1 * poleSign, R * sinT1 * oy1);
                Vec3 v3 = new Vec3(R * sinT2 * ox2, R * cosT2 * poleSign, R * sinT2 * oy2);

                // ========== 顶边：从底边沿法线（径向）向外延伸 ==========
                // 法线 = 从球心指向底边点的单位向量
                Vec3 n1 = v2.cpy().nor();
                Vec3 n2 = v3.cpy().nor();

                Vec3 v1 = v2.cpy().add(n1.scl(topOffsets[i]));
                Vec3 v4 = v3.cpy().add(n2.scl(topOffsets[(i + 1) % segments]));

                // 颜色
                Color cIn = Tmp.c1.set(color).a(color.a * fade * Math.min(1f, lights[i] * lights[i] * lights[i] - 0.1f));

                tilt(this.cosT,0,this.sinT,v1,v2,v3,v4);
                transform(v1, transform);
                transform(v2, transform);
                transform(v3, transform);
                transform(v4, transform);

                vertex(v1, cOut);
                vertex(v2, cIn);
                vertex(v3, cIn);

                vertex(v1, cOut);
                vertex(v3, cIn);
                vertex(v4, cOut);
            }
        }
    }

    // ==================== 工具方法 ====================

    /**
     * 将本地坐标通过 Mat3D 变换到世界空间。
     */
    public static void transform(Vec3 v, Mat3D mat) {
        float x = v.x, y = v.y, z = v.z;
        v.x = mat.val[0] * x + mat.val[4] * y + mat.val[8]  * z + mat.val[12];
        v.y = mat.val[1] * x + mat.val[5] * y + mat.val[9]  * z + mat.val[13];
        v.z = mat.val[2] * x + mat.val[6] * y + mat.val[10] * z + mat.val[14];
    }

    public static void tilt(Vec3 v, float x, float y,float z) {
        if(!Mathf.zero(x,0.1f))rotateX(v,x);
        if(!Mathf.zero(y,0.1f))rotateY(v,y);
        if(!Mathf.zero(z,0.1f))rotateZ(v,z);
    }
    public static void tilt(float x, float y,float z,Vec3... vecs) {
        for (Vec3 vec : vecs) {
            tilt(vec, x, y ,z);
        }
    }

    /** 绕 X 轴旋转向量（倾斜，像星环那样） */
    public static void rotateX(Vec3 v, float angleDeg) {
        float rad = angleDeg * Mathf.degRad;
        float cos = Mathf.cos(rad);
        float sin = Mathf.sin(rad);
        float y = v.y * cos - v.z * sin;
        float z = v.y * sin + v.z * cos;
        v.y = y;
        v.z = z;
    }

    /** 绕 Z 轴旋转向量（像陀螺仪那样侧倾） */
    public static void rotateZ(Vec3 v, float angleDeg) {
        float rad = angleDeg * Mathf.degRad;
        float cos = Mathf.cos(rad);
        float sin = Mathf.sin(rad);
        float x = v.x * cos - v.y * sin;
        float y = v.x * sin + v.y * cos;
        v.x = x;
        v.y = y;
    }

    /** 绕 Y 轴旋转向量（水平转向） */
    public static void rotateY(Vec3 v, float angleDeg) {
        float rad = angleDeg * Mathf.degRad;
        float cos = Mathf.cos(rad);
        float sin = Mathf.sin(rad);
        float x = v.x * cos + v.z * sin;
        float z = -v.x * sin + v.z * cos;
        v.x = x;
        v.z = z;
    }
    /**
     * 安全提交顶点。
     * 如果 vertex(float, float, float, Color) 编译报错，
     * 请改为 batch.vertex(x, y, z, c.r, c.g, c.b, c.a)
     */
    public static void vertex(Vec3 v, Color c) {
        batch.vertex(v.x, v.y, v.z, c.toFloatBits());
    }

    /**
     * 在星球表面画一条 3D 线（有粗细）。
     * 已自动处理背面剔除和混合模式恢复。
     */
    public static void drawLine3D(Vec3 from, Vec3 to, Color color, float stroke, Mat3D transform) {
        batch.flush(Gl.triangles);
        Gl.disable(Gl.cullFace);

        Vec3 dir = to.cpy().sub(from).nor();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 side = dir.crs(up).nor().scl(stroke / 2f);

        if (side.len() < 0.001f) {
            up.set(1, 0, 0);
            side = dir.crs(up).nor().scl(stroke / 2f);
        }

        Vec3 a = from.cpy().add(side);
        Vec3 b = from.cpy().sub(side);
        Vec3 c = to.cpy().sub(side);
        Vec3 d = to.cpy().add(side);

        transform(a, transform);
        transform(b, transform);
        transform(c, transform);
        transform(d, transform);

        vertex(a, color);
        vertex(b, color);
        vertex(c, color);

        vertex(a, color);
        vertex(c, color);
        vertex(d, color);

        batch.flush(Gl.triangles);
        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);
    }

    /**
     * 画一个球壳（能量护盾效果）。
     * 已自动处理背面剔除。
     */
    public static void drawSphereShell(Mat3D transform, float radius, Color color,
                                       int latSegments, int lonSegments) {
        batch.flush(Gl.triangles);
        Gl.disable(Gl.cullFace);

        Color c = color.cpy();
        for (int i = 0; i < latSegments; i++) {
            for (int j = 0; j < lonSegments; j++) {
                float theta1 = Mathf.PI * i / latSegments;
                float theta2 = Mathf.PI * (i + 1) / latSegments;
                float phi1 = Mathf.PI2 * j / lonSegments;
                float phi2 = Mathf.PI2 * (j + 1) / lonSegments;

                Vec3[] v = new Vec3[4];
                v[0] = spherePoint(theta1, phi1, radius);
                v[1] = spherePoint(theta2, phi1, radius);
                v[2] = spherePoint(theta2, phi2, radius);
                v[3] = spherePoint(theta1, phi2, radius);

                for (Vec3 vec : v) transform(vec, transform);

                vertex(v[0], c);
                vertex(v[1], c);
                vertex(v[2], c);

                vertex(v[0], c);
                vertex(v[2], c);
                vertex(v[3], c);
            }
        }

        batch.flush(Gl.triangles);
        Gl.enable(Gl.cullFace);
        Gl.cullFace(Gl.back);
    }

    private static Vec3 spherePoint(float theta, float phi, float r) {
        return new Vec3(
                r * Mathf.sin(theta) * Mathf.cos(phi),
                r * Mathf.cos(theta),
                r * Mathf.sin(theta) * Mathf.sin(phi)
        );
    }
}
