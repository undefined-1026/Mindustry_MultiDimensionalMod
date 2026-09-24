package mDimension.content;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mDimension.entity.EntityShield;
import mDimension.entity.VisibleEffect;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;

import arc.graphics.Color;
import mindustry.gen.Healthc;
import mindustry.gen.Posc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.world.Block;

import static arc.graphics.g2d.Draw.*;
import static arc.graphics.g2d.Lines.line;
import static arc.graphics.g2d.Lines.lineAngle;
import static arc.graphics.g2d.Lines.stroke;
import static arc.math.Angles.*;
import static mindustry.Vars.tilesize;

public class MD_Fx {
    public static final Rand rand = new Rand();
    public static final Vec2 v = new Vec2();
    public static final Vec2 v1 = new Vec2();
    public static final Vec2 v2 = new Vec2();

    public static final Effect
            dimension_vapor = new Effect(120f, e -> {
        color(new Color(0xffffb0ff));
        alpha(e.fout());
        randLenVectors(e.id, 5, 2.2f + e.finpow() * 12f, (x, y) -> {
            Fill.poly(e.x + x, e.y + y, 4, 0.6f + e.fin() * 7f);
        });
        Draw.reset();
    }),
            dimension_vapor_small = new Effect(90f, e -> {
                color(new Color(0xffffb0ff));
                alpha(e.fout());

                randLenVectors(e.id, 2, 1.8f + e.finpow() * 7f, (x, y) -> {
                    Fill.poly(e.x + x, e.y + y, 4, 0.6f + e.fin() * 5f);
                });
                Draw.reset();
            }),
            dimension_vapor_big =
                    new Effect(120, e -> {
                        color(new Color(0xffffb0ff));
                        alpha(e.fout());

                        randLenVectors(e.id, 3, 4f + e.finpow() * 35f, (x, y) -> {
                            Fill.poly(e.x + x, e.y + y, 4, 2f + e.fin() * 8f);
                        });
                    }),

    catFly = new Effect(500, 9999f, e -> {
        z(111);
        TextureRegion region = Core.atlas.find("cat");
        float dy = e.fin() * 8f * 30f;
        float dr = (float) Math.pow(e.fin(), 2f) * 360f * 2.5f;
        rect(region, e.x, e.y + dy, 16, 16, dr);
    }),

    plan = new Effect(120f, 80f, e -> {
        TextureRegion region = Core.atlas.find("cat");
        Draw.draw(Layer.blockBuilding, () -> {
            Shaders.blockbuild.region = region;
            Shaders.blockbuild.time = Time.time;
            Shaders.blockbuild.progress = e.fin();

            Draw.rect(region, e.x, e.y);
            Draw.flush();
        });
        Draw.reset();
    }),

    regionFlash = new Effect(70, e -> {
        if (!(e.data instanceof TextureRegion region)) return;
        alpha(e.fout());
        mixcol(Color.valueOf("ffe3bf"), e.fout());
        rect(region, e.x, e.y, e.rotation);
        Draw.reset();
    }).layer(211f),

    regionFlashColor = new Effect(70, e -> {
        if (!(e.data instanceof TextureRegion region)) return;
        alpha(e.fout());
        mixcol(e.color, e.fout());
        rect(region, e.x, e.y, e.rotation);
        Draw.reset();
    }).layer(211),

    regionFlashColorInUnit = new Effect(70, e -> {
        if (!(e.data instanceof Unit u)) return;
        alpha(e.fout());
        float scl = Mathf.lerp(1.2f,1f,e.finpow())/4;
        mixcol(e.color, e.foutpowdown());
        rect(u.type.fullIcon, u.x, u.y,scl*u.type.fullIcon.width,scl*u.type.fullIcon.height, u.rotation -90);
        Draw.reset();
    }).layer(211),

    spatter = new Effect(40f, e -> {
        Lines.stroke(4f * e.fout(Interp.pow3In));
        color(e.color);
        randLenVectors(e.id, 6, 15, 21, (x, y) -> {
            Lines.line(e.x + x * (e.fin() + 0.5f), e.y + y * (e.fin() + 0.5f), e.x + x * (e.fin() * 0.5f + 1), e.y + y * (e.fin() * 0.5f + 1));
        });
    }),

    spatterBig = new Effect(60f, e -> {
        Lines.stroke(7f * e.fout(Interp.pow3In));
        color(e.color);
        randLenVectors(e.id, 10, 30, 42, (x, y) -> {
            Lines.line(e.x + x * (e.fin() + 0.5f), e.y + y * (e.fin() + 0.5f), e.x + x * (e.fin() * 0.5f + 1), e.y + y * (e.fin() * 0.5f + 1));
        });
    }),

    polyWave = new Effect(25f, e -> {
        color(e.color);
        stroke(e.fout() * 3);
        Lines.poly(e.x, e.y, 4, e.finpow() * (16), e.rotation);
        Draw.reset();
    }),

    shieldBreak = new Effect(60f, 8f * 20, e -> {
        if (e.data instanceof EntityShield entity) {
            color(e.color);
            Lines.stroke(3f * e.fout());
            Lines.poly(e.x, e.y, entity.sides, entity.realRadius, entity.shieldRotation);
        }
    }),

    starExplosion = new Effect(25f, e -> {
        Lines.stroke(6f * e.fout());
        color(e.color);
        float rad = 40f * e.finpow();
        Lines.circle(e.x, e.y, rad);
        randLenVectors(e.id, 6, rad, 0, (x, y) -> {
            for (int i : Mathf.zeroOne) {
                Drawf.tri(e.x + x, e.y + y, 6f, 35f * e.fout(), Mathf.angle(x, y) + i * -180f);
            }
        });

    }),

    starExplosionBig = new Effect(35f, e -> {
        Lines.stroke(5f * e.fout());
        color(e.color);
        float rad = 60f * e.finpow();
        Lines.circle(e.x, e.y, rad);
        randLenVectors(e.id, 7, rad, 0, (x, y) -> {
            for (int i : Mathf.zeroOne) {
                Drawf.tri(e.x + x, e.y + y, 12f, 55f * e.fout(), Mathf.angle(x, y) + i * -180f);
            }
        });

    }),

    starExplosionSmall = new Effect(18f, e -> {
        Lines.stroke(3.5f * e.fout());
        color(e.color);
        float rad = 15f * e.finpow();
        Lines.circle(e.x, e.y, rad);
        randLenVectors(e.id, 3, rad, 0, (x, y) -> {
            for (int i : Mathf.zeroOne) {
                Drawf.tri(e.x + x, e.y + y, 4f, 13f * e.fout(), Mathf.angle(x, y) + i * -180f);
            }
        });

    }),

    shootSmokeMissileSmallColor = new Effect(80f, 200f, e -> {
        color(e.color);
        alpha(0.6f);
        rand.setSeed(e.id);
        for (int i = 0; i < 20; i++) {
            v.trns(e.rotation + 180f + rand.range(12), rand.random(e.finpow() * 50f)).add(rand.range(1.5f), rand.range(1.5f));
            e.scaled(e.lifetime * rand.random(0.2f, 1f), b -> {
                Fill.circle(e.x + v.x, e.y + v.y, b.fout() * 6f + 0.3f);
            });
        }
    }),

    leakage = new Effect(75f, e -> {
        color(e.color);
        if (e.data instanceof float[] size && size.length >= 2) {
            if (size.length >= 3) {
                if (size[2] == 1 && Vars.world.tile((int) (e.x / 8), (int) (e.y / 8)).build == null) {
                    e.time += 75f;
                    e.lifetime = 0;

                }
            }
            float len = (float) (Math.sin(e.fin() * Math.PI) * 1.9f * size[0]);
            Drawf.tri(e.x, e.y, 2f * size[1], len, Mathf.randomSeed(e.id, 360));
        } else {
            float len = (float) (Math.sin(e.fin() * Math.PI) * 10f);
            Drawf.tri(e.x, e.y, 6.5f, len, Mathf.randomSeed(e.id, 360));
        }
    }).layer(Layer.effect - 2f),

    chainLightningBig = new Effect(25f, 350f, e -> {
        if (!(e.data instanceof Position p)) return;
        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        Tmp.v1.set(p).sub(e.x, e.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float range = 12f;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(2.6f * e.fout());
        Draw.color(Color.white, e.color, e.fin() * 0.5f);
        Fill.circle(e.x, e.y, 4.5f * e.fout());
        Fill.circle(tx, ty, 4.5f * e.fout());
        Lines.beginLine();

        Lines.linePoint(e.x, e.y);

        rand.setSeed(e.id);

        for (int i = 0; i < links; i++) {
            float nx, ny;
            if (i == links - 1) {
                nx = tx;
                ny = ty;
            } else {
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(rand).scl(range * 0.45f);
                nx = e.x + normx * len + Tmp.v1.x;
                ny = e.y + normy * len + Tmp.v1.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }).followParent(false).rotWithParent(false),

    chainLightningPro = new Effect(20f,300,e->{
        if (!(e.data instanceof Position p)) return;
        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        v.set(p).sub(e.x, e.y).nor();
        rand.setSeed(e.id);
        float lenScl = 1f;
        float widScl = 1f;
        boolean nodeSpatter = false;
        if(rand.random(1f)<0.35f){
            lenScl = 3f;
            widScl = 2.5f;
            nodeSpatter = true;
        }
        float range = 13 * lenScl;
        float Mal = 20f * widScl;
        float stroke= 2.6f;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;
        float lx = e.x,ly = e.y;

        v1.set(v).rotate(90);
        v2.set(e.x,e.y);
        v.scl(spacing);
        Lines.stroke(stroke * e.fout());
        Fill.circle(e.x,e.y,stroke * e.fout());
        Fill.circle(tx,ty,stroke * e.fout());

        color(e.color,Color.white,e.fout()*0.5f);
        for(int i=0;i<links;i++){
            float X = ((float) i /links);
            float Mall = (-4*X*X+4*X)*Mal;
            float Yscl = rand.random(Mall)-Mall/2;
            v2.add(v);
            float cx = v2.x + Yscl*v1.x;
            float cy = v2.y + Yscl*v1.y;
            Lines.line(lx,ly,cx,cy,false);
            if(nodeSpatter && rand.random(1f)<0.3f){
                Fill.circle(cx, cy, stroke*0.8f* e.fout());
                e.scaled(18f,ee->{
                    Draw.alpha(0.7f);
                    Lines.stroke(stroke*0.3f*ee.foutpowdown());
                    randLenVectors((long) (ee.id+cx+cy),8,2.5f+(stroke*4f)*ee.fin(),(x, y)->{
                        Lines.lineAngle(cx+x,cy+y,Mathf.angle(x,y),ee.fslope()*stroke*1.3f);
                    });
                    Lines.circle(cx,cy,ee.finpow() * stroke * 1.8f+1f);
                    Lines.stroke(stroke* e.fout());
                });
                Draw.alpha(1f);
            }else {
                Fill.circle(cx, cy, stroke/2 * e.fout());
            }
            lx = cx;ly =cy;
        }
        Lines.line(lx,ly,tx,ty,false);
    }),
    dawnCharge = new Effect(55f, e -> {
        mixcol(Color.valueOf("F8D09E"), Color.white, e.fin());
        Lines.stroke(e.fslope() * 1.5f);
        randLenVectors(e.id, 16, 1f + 30f * e.fout(), (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 4f + 1f);
        });
        color(Color.valueOf("F8D09E"));
        Fill.circle(e.x, e.y, e.fin() * 7f);
        color(Color.white);
        Fill.circle(e.x, e.y, e.fin() * 4f);
    }),
            spikeExplosion = new Effect(40f, 100f, e -> {
                color(e.color);
                randLenVectors(e.id, 9, 16, 16, (x, y) -> {
                    float len = Mathf.len(x, y);
                    float rot = Mathf.angle(x, y);
                    float len2 = len * len * 0.17f;
                    Drawf.tri(e.x + x, e.y + y,
                            len * 0.6f * e.foutpowdown(), len2 * (e.foutpowdown() + 2) / 3, rot);
                    Drawf.tri(e.x + x, e.y + y,
                            len * 0.6f * e.foutpowdown(), len2 * (e.foutpowdown() + 2) * 0.2f / 3, rot + 180f);
                });
            }),
            spikeHit = new Effect(40f, 130f, e -> {
                color(e.color);
                randLenVectors(e.id, 11, 50, 120, (x, y) -> {
                    float rot = Mathf.angle(x, y);
                    float len = Mathf.len(x, y);

                    Drawf.tri(e.x, e.y, (len / 20 + 7) * e.foutpowdown(), len * (e.fout() + 2) / 3, rot);
                });
            }),
            spikeHitSmall = new Effect(40f, 130f, e -> {
                color(e.color);
                randLenVectors(e.id, 7, 10, 20, (x, y) -> {
                    float rot = Mathf.angle(x, y);
                    float len = Mathf.len(x, y);

                    Drawf.tri(e.x, e.y, (len / 15 + 5) * e.foutpowdown(), len * (e.fout() + 2) / 3, rot);
                });
            }),
            spikeHitRotation = new Effect(35f, 100f, e -> {
                color(e.color);
                rand.setSeed(e.id);
                for (int i = 0; i < 7; i++) {
                    v.trns(e.rotation + rand.range(25), rand.random(20) + 12);
                    float rot = Mathf.angle(v.x, v.y);
                    float len = Mathf.len(v.x, v.y);
                    float len2 = len * len * 0.15f;
                    Drawf.tri(e.x + v.x, e.y + v.y,
                            len * 0.5f * e.foutpowdown(), len2 * (e.foutpowdown() + 2) / 3, rot);
                    Drawf.tri(e.x + v.x, e.y + v.y,
                            len * 0.5f * e.foutpowdown(), len2 * (e.foutpowdown() + 2) * 0.25f / 3, rot + 180f);
                }
            }),
            spikeHitRotationSmall = new Effect(35f, 100f, e -> {
                color(e.color);
                rand.setSeed(e.id);
                for (int i = 0; i < 5; i++) {
                    v.trns(e.rotation + rand.range(30), rand.random(8) + 5);
                    float rot = Mathf.angle(v.x, v.y);
                    float len = Mathf.len(v.x, v.y);
                    float len2 = len * len * 0.17f;
                    Drawf.tri(e.x + v.x, e.y + v.y,
                            len * 0.5f * e.foutpowdown(), len2 * (e.foutpowdown() + 2) / 3, rot);
                    Drawf.tri(e.x + v.x, e.y + v.y,
                            len * 0.5f * e.foutpowdown(), len2 * (e.foutpowdown() + 2) * 0.25f / 3, rot + 180f);
                }
            }),
            crestShootFlame = new Effect(35f, e -> {

                for (int j : Mathf.zeroOne) {
                    float s = 1f - 0.6f * j;
                    color(Color.valueOf("CCDDFF"), Color.white, j);
                    for (int i : Mathf.signs) {
                        Drawf.tri(e.x, e.y, s * 16 * e.fout(), s * 100, e.rotation + 90 * i);
                        Drawf.tri(e.x, e.y, s * 12 * e.fout(), s * 70, e.rotation + 20 * i);
                        Drawf.tri(e.x, e.y, s * 7 * e.fout(), s * 50, 90 + 90 * i);
                    }
                    Drawf.tri(e.x, e.y, s * 12 * e.fout(), s * 60, e.rotation + 180);
                }

            }),
            crestShoot = new Effect(20f, e -> {

                for (int j : Mathf.zeroOne) {
                    float s = 1f - 0.6f * j;
                    color(Color.valueOf("CCDDFF"), Color.white, j);
                    for (int i = 0; i < 3; i++) {
                        Drawf.tri(e.x, e.y, s * 12 * e.fout(), s * 40, Mathf.randomSeed(e.id, 120) + 120 * i);
                    }
                    Lines.stroke(e.fout() * 1.5f);
                    Lines.circle(e.x, e.y, (1 - s * 0.8f) * 30 * e.finpow());
                }

            }),

            casingCrest = new Effect(40f, e -> {
                color(Pal.lightOrange, Pal.lightishGray, Pal.lightishGray, e.fin());
                alpha(e.fout(0.5f));
                float rot = Math.abs(e.rotation) + 105;
                int i=1;
                float len = (5 + e.finpow() * 15) * i;
                float lr = rot + Mathf.randomSeedRange(e.id + i + 6, 12 * e.fin()) * i;

                rect(Core.atlas.find(MD_blocks.modname+"casing-big"),
                        e.x + trnsx(lr, len) + Mathf.randomSeedRange(e.id + i + 7, 3f * e.fin()),
                        e.y + trnsy(lr, len) + Mathf.randomSeedRange(e.id + i + 8, 3f * e.fin()),
                        5, 15,
                        rot + e.fin() * 30 * i
                );

            }).layer(Layer.bullet),
            payloadInput = new Effect(20f, e -> {
                if (e.data instanceof Object[] data) {
                    if (data[0] instanceof UnlockableContent cont && data[1] instanceof Vec2 cv) {
                        TextureRegion t = cont.fullIcon;
                        z(Layer.blockOver);
                        color(Color.white, Pal.lighterOrange, e.fin());
                        alpha(e.fout());
                        float cx = e.x + e.fout() * cv.x, cy = e.y + e.fout() * cv.y;
                        rect(t, cx, cy);

                        z(Layer.blockOver - 0.01f);

                        if (cont instanceof Block b) {
                            Drawf.squareShadow(cx, cy, b.size * tilesize * 1.85f, e.fout());
                        }
                        reset();
                    }
                }
            }),

    triangle = new Effect(30f, e -> {
        rand.setSeed(e.id);
        color(e.color, Color.white, e.fout() * 0.8f);
        float radius = 3f;
        float angle = rand.random(120);
        v.set(1, 0).setAngle(angle + 180).setLength(radius * e.fout());
        Drawf.tri(e.x + v.x, e.y + v.y, radius * 1.732f * 2 * e.fout(), radius * 3f * e.fout(), angle);
        Draw.reset();
    }),

    mineImpactWave = new Effect(50f, e -> {
        color(e.color);

        stroke(e.fout() * 1.5f);

        randLenVectors(e.id, 12, 4f + e.finpow() * e.rotation*1.5f, (x, y) -> {
            lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 5 + 1f);
        });

        e.scaled(30f, b -> {
            Lines.stroke(5f * b.fout());
            Lines.circle(e.x, e.y, b.finpow() * e.rotation);
        });
    }),

    shoot1small = new Effect(10f,e->{
       color(e.color);
       float wid = e.fout() * 6f;
       float len = (e.fout() +1) * 7.5f;
       for(int o:Mathf.signs){
           Drawf.tri(e.x,e.y,wid,len,e.rotation +o * 12f);
       }
       Fill.circle(e.x,e.y,wid/2);
    }),

    fluffrainShoot = new Effect(20f, e->{
        color(e.color,Color.white,e.fin()*0.5f);
        rand.setSeed(e.id);
        for(int i=0;i<7;i++){
            float dr = rand.range(20f);
            v.trns(e.rotation+dr,(rand.random(6f)+3f)*e.finpow());
            v.add(v1.trns(e.rotation+180,8f));
            Fill.poly(e.x+v.x,e.y+v.y,4,e.fout()*3f);
        }
    }),

    diffusionShoot = new Effect(20f, e->{
        color(e.color,Color.white,e.fin()*0.5f);
        rand.setSeed(e.id);
        for(int i=0;i<7;i++){
            float dr = rand.range(20f);
            v.trns(e.rotation+dr,(rand.random(6f)+3f)*e.finpow());
            Fill.poly(e.x+v.x,e.y+v.y,4,e.fout()*3f);
        }
    }),

    fluffrainHit = new Effect(20f,e->{
        color(e.color,Color.white,e.fout()*0.7f);
        randLenVectors(e.id+5,6,15f*e.finpow(),(x,y)->{
            Fill.circle(e.x+x,e.y+y,e.fout()*2.5f);
        });
        Lines.stroke(e.fout());
        Lines.circle(e.x,e.y,e.finpow()*8f);
    }),


    RefractedLaser = new Effect(20f,50*8,e->{
        if(e.data instanceof Posc[] targets){
            rand.setSeed(e.id);
            v.set(e.x,e.y);
            float fout = e.fout() * (e.rotation<=0.01f?1f:e.rotation);
            float stroke;

            for(int i=0;i<targets.length;i++){
                var t = targets[i];
                v1.set(t.x(),t.y()).add(v2.trns(rand.random(360),rand.random(3f)));;
                stroke = fout*7f;

                color(e.color,0.3f);
                Fill.circle(v.x,v.y,stroke * 1.15f);
                Lines.stroke(stroke);
                Lines.line(v.x,v.y,v1.x,v1.y,false);

                stroke = fout*5.8f;

                color(e.color,Color.white,0.25f);
                Fill.circle(v.x,v.y,stroke * 1.15f);
                Lines.stroke(stroke);
                Lines.line(v.x,v.y,v1.x,v1.y,false);

                stroke = fout*3.7f;

                color(e.color,Color.white,0.9f);
                Fill.circle(v.x,v.y,stroke * 1.15f);
                Lines.stroke(stroke);
                Lines.line(v.x,v.y,v1.x,v1.y,false);
                v.set(v1);

            }
            stroke = fout * 7f;
            color(e.color,0.3f);
            Fill.circle(v.x,v.y,stroke * 1.15f);
            stroke = fout * 5.8f;
            color(e.color,Color.white,0.25f);
            Fill.circle(v.x,v.y,stroke * 1.15f);
            stroke = fout * 3.7f;
            color(e.color,Color.white,0.9f);
            Fill.circle(v.x,v.y,stroke * 1.15f);
        }
    }),

    regionTrail = new Effect(15f,e->{
        if(e.data instanceof TextureRegion region){
            color(e.color,e.fout());
            float scl = (0.6f+0.4f*e.fout())/4f;
            Draw.rect(region,e.x,e.y,region.width * scl,region.height *scl,e.rotation);
        }
    }),

    circleLineTrail = new Effect(15f,e->{
        color(e.color,e.fout());
        float r = (0.6f+0.4f*e.fout())*e.rotation;
        Lines.stroke(1.2f+0.5f*e.fout());
        Lines.circle(e.x,e.y,r);
    }).layer(99.99f),

    planter = new Effect(30f,e->{
        Draw.color(Color.white.cpy().lerp(e.color,0.5f).mul(e.fout()*0.2f+0.6f));
        randLenVectors(e.id,5,e.fin()*3f,(x,y)->{
            Fill.poly(e.x+x,e.y+y,4,e.fout()*1.2f);
        });
        Lines.stroke(e.fout());
        Lines.poly(e.x,e.y,4,e.fin()*4f);
    }).layer(Layer.blockProp+0.5f),



    none = Fx.none;


    public static Effect polyWave(int side, float radius, float rotate, float stroke, float life, Color color, float alpha) {
        return new Effect(life, e -> {
            color(new Color(color), alpha);
            stroke(e.fout() * stroke);
            Lines.poly(e.x, e.y, side, e.finpow() * (radius), rotate);
            Draw.reset();
        });
    }

    public static Effect polyFacula(int side, float radius, float rotate, float life, Color color, float alpha) {
        return new Effect(life, e -> {
            color(new Color(color), alpha);
            Fill.poly(e.x, e.y, side, e.foutpow() * (radius), rotate);
            Draw.reset();
        });
    }

    public static VisibleEffect loadLaunch(float life, String regionName, float trip, float tripXM, float tripYM, float flameSize) {
        return new VisibleEffect(life, 9999f, e -> {
            float stallRatio = 0.22f;
            z(Layer.effect + 1);
            TextureRegion loadRegion = Core.atlas.find(regionName);
            float fi = (float) Math.pow(Math.max(0, (e.fin() - stallRatio) / (1 - stallRatio)), 5);
            float rotation = 0;
            float dx = fi * (tripXM);
            float dy = fi * (tripYM);
            float cx = e.x + dx * trip;
            float cy = e.y + dy * trip;
            float size = 0.25f * (fi + 1f);
            float alpha = e.fout(Interp.pow5Out);
            float scale = (1.0F - alpha) * 1.3F + 1.0F;
            float rad = 0.2F + e.fslope();
            alpha(alpha);
            z(Layer.effect + 1f);
            rect(loadRegion, cx, cy, loadRegion.width * size, loadRegion.height * size);
            if (Time.time % (fi < 0.01f ? 10f : 5f) < Time.delta && !Vars.state.isPaused()) {
                if (fi < 0.01f) {
                    dimension_vapor(50f, 1f, 4f).at(cx, cy);
                } else {
                    dimension_vapor(100f, 1f - fi, 1.3f).at(cx, cy);
                }
            }
            z(Layer.effect + 0.01f);
            Draw.color(Pal.engine);
            alpha(alpha * 0.75f);
            for (int i = 0; i < 4; ++i) {
                Drawf.tri(cx, cy, 12F, 60f * flameSize * (rad + scale - 1.0F), (float) i * 90f + rotation);
            }
            for (int i = 0; i < 4; ++i) {
                Drawf.tri(cx, cy, 18f, 35f * flameSize * (rad + scale - 1.0F), (float) i * 90f + rotation + 45F);
            }
            Fill.light(cx, cy, 16, 35.0F * (rad + scale - 1.0F), Tmp.c2.set(Pal.engine).a(alpha), Tmp.c1.set(Pal.engine).a(0.0F));
            z(Layer.effect + 1.1f);
            Fill.light(cx, cy, 10, 20.0F, Tmp.c2.set(new Color(1f, 0.7f, 0.65f, 1f)).a(Math.max(0, fi * alpha * 2f)), Tmp.c1.set(Pal.engine).a(0.0F));
            Draw.reset();
        });
    }

    public static VisibleEffect loadLand(float life, String regionName, float trip, float tripXM, float tripYM, float flameSize, float stayTime) {
        return new VisibleEffect(life, 9999f, e -> {
            float fob = (float) Math.pow((e.fout() - stayTime / life) / (1 - stayTime / life), 5f);
            float fo = Math.max(0f, fob);
            float alpha = e.fin(Interp.pow5Out);

            float dx = fo * tripXM;
            float dy = fo * tripYM;

            float cx = e.x + dx * trip;
            float cy = e.y + dy * trip;

            float size = (fo + 1f) * 0.25f;
            float rad = 0.2F + (float) Math.sin(3.1415f * e.fin() / (1 - stayTime / life));
            float scale = (1.0F - alpha) * 1.3F + 1.0F;


            TextureRegion loadRegion = Core.atlas.find(regionName);
            z(Layer.effect + 1f);

            if (fob > 0f) {
                alpha(alpha);
                rect(loadRegion, cx, cy, loadRegion.width * size, loadRegion.height * size, 0f);
                Draw.color(Pal.engine);
                z(Layer.effect);
                alpha(alpha * 0.9f);
                for (int i = 0; i < 4; ++i) {
                    Drawf.tri(cx, cy, 10f * flameSize, 30f * flameSize * (rad + scale - 1.0F), (float) i * 90f);
                }
            } else {
                float landalpha = e.fout() / (stayTime / life);
                alpha(landalpha);
                rect(loadRegion, e.x, e.y);
            }
            Draw.reset();

            ;
        });
    }

    public static Effect dimension_vapor(float life, float alpha, float size) {
        return new Effect(life, e -> {
            color(new Color(1f, 1f, 0.647f, 1f));
            alpha(e.fout() * alpha);

            randLenVectors(e.id, 3, (3f + e.finpow() * 11f) * size, (x, y) -> {
                Fill.poly(e.x + x, e.y + y, 4, (0.6f + e.fin() * 5f) * ((size - 1) * 0.7f + 1));
            });
            Draw.reset();
        });

    }

    /**
     * precision是绘制渐变的边数
     */
    public static Effect gradientWave(float life, float rad) {
        return new Effect(life, rad * 2, e -> {

            color(e.color);
            Lines.stroke(3f * e.fout());
            float radd = rad * e.finpow();
            z(Layer.effect - 11f);

            Fill.light(e.x, e.y, (int) (radd * 1.5f + 10), radd, Color.clear, e.color.a(e.fout() * 0.8f));
            z(Layer.effect);
            alpha(0.7f);
            Lines.circle(e.x, e.y, radd);
            Draw.reset();
        });
    }

    public static Effect Mulitpleslash(float life, int number, Color color, float length, float width, float eccentricity) {
        return new Effect(life, length * 2, e -> {
            float len = length * (e.fout() * 0.2f + 0.8f);
            float wid = width * e.fout();
            color(color);
            randLenVectors(e.id, number, eccentricity, (x, y) -> {
                float rotat = Mathf.randomSeed((long) (e.id + Mathf.len(x, y)), 0f, 360f);
                for (int i = 0; i < 2; i++) {

                    Drawf.tri(e.x + x, e.y + y, wid, len / 2, i * 180 + rotat);
                }
            });
            Draw.reset();
        });
    }

    public static Effect polygonalStar(float life, int number, Color color, float length, float width, float rotation) {
        return new Effect(life, length * 2, e -> {
            float len = length * (e.fout() * 0.2f + 0.8f);
            float wid = width * e.fout();
            color(color);
            for (int i = 0; i < number; i++) {
                Drawf.tri(e.x, e.y, wid, len, i * (360f / number) + rotation + e.rotation);
            }
            Draw.reset();
        });
    }

    public static Effect polygonalStar(float life, int number, Color color, float length, float width, float rotation, float deltaRotation) {
        return new Effect(life, length * 2, e -> {
            float len = length * (e.fout() * 0.2f + 0.8f);
            float wid = width * e.fout();
            color(color);
            for (int i = 0; i < number; i++) {
                Drawf.tri(e.x, e.y, wid, len, i * deltaRotation + rotation);
            }
            Draw.reset();
        });
    }

    public static Effect waveColor(float life, float radius, float stroke) {
        return new Effect(life, e -> {
            color(e.color);
            stroke(e.fout() * stroke);
            Lines.circle(e.x, e.y, e.finpow() * (radius));
            Draw.reset();
        });
    }

    public static Effect waveColor(float life, float radius, float stroke, Interp interp) {
        return new Effect(life, e -> {
            color(e.color);
            stroke(e.fout() * stroke);
            Lines.circle(e.x, e.y, e.fin(interp) * (radius));
            Draw.reset();
        });
    }
    /** len is 0 to 1*/
    public static Effect spikeWaveColor(float life, float radius, float stroke,int amount,float len,float width) {
        return new Effect(life, e -> {
            color(e.color);
            stroke(e.fout() * stroke);
            float radd = e.finpow() * (radius);
            Lines.circle(e.x, e.y, radd);

            rand.setSeed(e.id);
            for(int i=0;i<amount;i++){
                float angle = rand.random(360f);
                float length = rand.nextFloat() * len;
                v.trns(angle,radd);
                Drawf.tri(e.x+v.x,e.y+v.y,width * e.fout(),length * radd,angle +180f);
            }
            Draw.reset();
        });
    }

    public static Effect waveHitColor(float life, float radius, float lineSize, float stroke, float alpha) {
        return new Effect(life, e -> {
            color(e.color, Color.white, e.fin());
            alpha(alpha);
            stroke(e.fout() * stroke);
            Lines.circle(e.x, e.y, e.finpow() * (radius));
            stroke(0.6f + e.fout());
            alpha(1f);
            randLenVectors(e.id, 5, lineSize * e.fin(), (x, y) -> {
                Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * lineSize * 0.5f);
            });
            Draw.reset();
        });
    }

    public static Effect craftEffect(float life, float size, Color color, int amount) {
        return craftEffect(life, size, color, amount, new float[]{0, 0});
    }

    public static Effect craftEffect(float life, float size, Color color, int amount, float[] spawn) {
        return new Effect(life, e -> {
            color(color, Pal.lightishGray, e.finpow());
            alpha(0.9f);


            rand.setSeed(e.id);
            for (int i = e.id % spawn.length, j = 0; j < amount; j++, i++) {
                v.trns(rand.random(360f), rand.random(2.5f + e.fin() * size));
                Fill.poly(e.x + v.x + spawn[(i * 2) % spawn.length], e.y + v.y + spawn[(i * 2 + 1) % spawn.length], 4, e.foutpowdown() * 2.5f);
            }
            Draw.reset();
        });
    }

    public static Effect craftEffectLight(float life,float rad, float size, Color color, int amount,float eccentricity) {
        return new Effect(life, e -> {
            color(color);
            alpha(0.9f);

            rand.setSeed(e.id);
            v1.set(eccentricity,0).rotate(rand.random(360));
            for(int i=0;i<amount;i++) {
                v.trns(rand.random(360f), rand.random(2.5f + e.fin() * size));
                v.add(v1);
                Fill.poly(e.x + v.x, e.y + v.y, 4, e.foutpowdown() * rad);
            }
            Draw.reset();
        });
    }

    public static Effect brokenWave(float life, Color color, float rad, float startRad, float stroke, int paragraph, float spread, float fractionScl) {
        return new Effect(life, e -> {
            color(color);
            rand.setSeed(e.id);
            float[] paragraphSpacing = new float[paragraph * 2];
            float count = 0;
            for (int i = 0; i < paragraph * 2; i++) {
                float rand1 = rand.range(spread) + 1;
                paragraphSpacing[i] = rand1;
                count += rand1;
            }
            float p = 360f / (paragraph * 2f);
            float scl = 360 / count;
            float paraScl = e.foutpowdown();
            float radius = rad * e.finpow() + startRad;
            Lines.stroke(e.fout() * stroke);
            for (int i = 0; i < paragraph; i++) {
                float fraction = paragraphSpacing[i * 2] * scl;
                Lines.arc(e.x, e.y, radius * (4 - fraction / p) / 3, paraScl * fraction / 360 * fractionScl, count - paraScl * fraction * fractionScl / 2);
                count += (paragraphSpacing[i * 2] + paragraphSpacing[i * 2 + 1]) * scl;
            }

            Draw.reset();
        });
    }

    public static Effect brokenWaveColor(float life, float rad, float startRad, float stroke, int paragraph, float spread, float fractionScl) {
        return new Effect(life, e -> {
            color(e.color);
            rand.setSeed(e.id);
            float[] paragraphSpacing = new float[paragraph * 2];
            float count = 0;
            for (int i = 0; i < paragraph * 2; i++) {
                float rand1 = rand.range(spread) + 1;
                paragraphSpacing[i] = rand1;
                count += rand1;
            }
            float p = 360f / (paragraph * 2f);
            float scl = 360 / count;
            float paraScl = e.foutpowdown();
            float radius = rad * e.finpow() + startRad;
            Lines.stroke(e.fout() * stroke);
            for (int i = 0; i < paragraph; i++) {
                float fraction = paragraphSpacing[i * 2] * scl;
                Lines.arc(e.x, e.y, radius * (4 - fraction / p) / 3, paraScl * fraction / 360 * fractionScl, count - paraScl * fraction * fractionScl / 2);
                count += (paragraphSpacing[i * 2] + paragraphSpacing[i * 2 + 1]) * scl;
            }

            Draw.reset();
        });
    }

    public static Effect squareWave(float life,float begin,float end,float stroke){
        return new Effect(life,e->{
            Draw.color(e.color);
            float size = begin + (end-begin)*e.finpow();
            Lines.stroke(stroke * e.fout());
            Lines.square(e.x,e.y,size,e.rotation);
        });
    }

    public static Effect Line(float life,float stroke,float beginCapSize,float endCapSize){
        return new Effect(life,e->{
            if(!(e.data instanceof Posc pos))return;
            Draw.color(e.color);

            Lines.stroke(stroke * e.fout());
            Lines.line(e.x,e.y,pos.x(),pos.y());

            Fill.circle(e.x,e.y,beginCapSize * e.fout());
            Fill.circle(pos.x(),pos.y(),endCapSize * e.fout());

        }){{followParent = false;}};
    }

    public static Effect polyStarExplosion(float life, int amount, float length, float width, float rotation, boolean hasCap) {
        return new Effect(life, length * 2, e -> {
            float len = length * e.fout() + (hasCap ? width * 0.8f : 0);
            float wid = width * (!hasCap ? e.foutpowdown() : 1);

            for (int o : Mathf.zeroOne) {
                color(e.color, Color.white, o * 0.95f);
                float scl = 1 - o * 0.4f;
                for (int i = 0; i < amount; i++) {
                    Drawf.tri(e.x, e.y, wid * scl, len * scl, i * (360f / amount) + rotation + e.rotation);
                }
            }
            Draw.reset();
        });
    }

    public static Effect hitBulletColor(float circleRad, int lines, float linesRad){
        Interp inter = circleRad>20f?Interp.pow3Out:Interp.linear;
        float da = circleRad>20f?0.5f:0f;
        float l = Math.max(0,(circleRad-10f)/4f);

        return new Effect(14+l, e -> {
            color(Color.white, e.color, e.fin());

            e.scaled(7f + l/2, s -> {
                Draw.alpha(1-s.fin()*da);
                stroke(0.1f +inter.apply(1-s.fin()));
                Lines.circle(e.x, e.y, s.fin(inter) * circleRad);
            });

            stroke(0.3f + inter.apply(1-e.fin()));

            randLenVectors(e.id, lines, e.fin(inter) * linesRad, (x, y) -> {
                float ang = Mathf.angle(x, y);
                lineAngle(e.x + x, e.y + y, ang, e.fout() * 3 * (linesRad/16f) + 0.2f);
            });

            Drawf.light(e.x, e.y, 20f, e.color, 0.6f * e.fout());
        });
    }

    public static Effect chainLightningPro(float life,float stroke,float width,float space){
        return new Effect(20f,300,e->{
            if (!(e.data instanceof Position p)) return;
            float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
            v.set(p).sub(e.x, e.y).nor();
            rand.setSeed(e.id);
            float lenScl = 1f;
            float widScl = 1f;
            boolean nodeSpatter = false;
            if(rand.random(1f)<0.35f){
                lenScl = 3f;
                widScl = 2.5f;
                nodeSpatter = true;
            }
            float range = space * lenScl;
            float Mal = width * widScl;
            int links = Mathf.ceil(dst / range);
            float spacing = dst / links;
            float lx = e.x,ly = e.y;

            v1.set(v).rotate(90);
            v2.set(e.x,e.y);
            v.scl(spacing);
            Lines.stroke(stroke * e.fout());
            Fill.circle(e.x,e.y,stroke * e.fout());
            Fill.circle(tx,ty,stroke * e.fout());

            color(e.color,Color.white,e.fout()*0.5f);
            for(int i=0;i<links;i++){
                float X = ((float) i /links);
                float Mall = (-4*X*X+4*X)*Mal;
                float Yscl = rand.random(Mall)-Mall/2;
                v2.add(v);
                float cx = v2.x + Yscl*v1.x;
                float cy = v2.y + Yscl*v1.y;
                Lines.line(lx,ly,cx,cy,false);
                if(nodeSpatter && rand.random(1f)<0.3f){
                    Fill.circle(cx, cy, stroke*0.8f* e.fout());
                    e.scaled(20f,ee->{
                        Lines.stroke(stroke*0.5f*ee.foutpowdown());
                        randLenVectors((long) (ee.id+cx+cy),8,2.5f+(stroke*4f)*ee.fin(),(x, y)->{
                            Lines.lineAngle(cx+x,cy+y,Mathf.angle(x,y),ee.fslope()*stroke*1.3f);
                        });
                        Lines.circle(cx,cy,ee.finpow() * stroke * 2.5f+2f);
                        Lines.stroke(stroke* e.fout());
                    });
                }else {
                    Fill.circle(cx, cy, stroke/2 * e.fout());
                }
                lx = cx;ly =cy;
            }
            Lines.line(lx,ly,tx,ty,false);
        });
    }

    public static Effect spark(float life,int amount,float rad,float lineLen){
        return new Effect(life,e->{
            color(e.color);
            stroke(e.fout() *2f);
            randLenVectors(e.id, amount, e.finpow() * rad, e.rotation, 360f, (x, y) -> {
                float ang = Mathf.angle(x, y);
                lineAngle(e.x + x, e.y + y, ang, e.fout() * lineLen + 1f);
            });
        });
    }



}
