/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.models.mob;

import nro.consts.Cmd;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.MobService;
import nro.services.Service;
import nro.utils.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class Hirudegarn extends BigBoss {

    private byte type;
    private long lastTimeMove;

    public Hirudegarn(Mob mob) {
        super(mob);
        this.point.dame = 100000;
    }

    @Override
    public int getSys() {
        return type;
    }

    public List<Player> getListPlayerCanAttack(int range) {
        List<Player> list = new ArrayList<>();
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isMiniPet) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= range) {
                        list.add(pl);
                    }
                }
            }
        } catch (Exception e) {

        }
        return list;
    }

    private void jump() {
        if (!isDie() && !effectSkill.isHaveEffectSkill()) {
            List<Player> players = getListPlayerCanAttack(500);
            long[][] array = new long[players.size()][2];
            int i = 0;
            for (Player pl : players) {
                double damage = MobService.gI().mobAttackPlayer(this, pl);
                array[i][0] = pl.id;
                array[i][1] = (long)damage;
                i++;
            }
            send(array, (byte) 2);
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    public void send(long[][] array, byte type) {
        try {
            Message ms = new Message(Cmd.BIG_BOSS);
            DataOutputStream ds = ms.writer();
            ds.writeByte(type);
            ds.writeByte(array.length);
            for (long[] arr : array) {
                ds.writeLong(arr[0]);
                ds.writeLong(arr[1]);
            }
            ds.flush();
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
            ms.cleanup();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void attackPlayer() {
        if (Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            int rd = Util.nextInt(5);
            if (rd == 0) {
                jump();
            } else if (rd == 1) {
                flyTo(Util.nextInt(30, zone.map.mapWidth - 30), 360);
            } else {
                super.attackPlayer();
            }
        }
        if (Util.canDoWithTime(lastTimeMove, 2000)) {
            int rd = Util.nextInt(3);
            if (rd == 0) {
                move(Util.nextInt(30, zone.map.mapWidth - 30), 360);
            }
            lastTimeMove = System.currentTimeMillis();
        }
    }

    @Override
    public void attack(Player target) {
        byte action = (byte) ((byte) Util.nextInt(4) == 3 ? 1 : 0);
        double damage = MobService.gI().mobAttackPlayer(this, target);
        send(target, damage, action);
    }

    public void send(Player cAttack, double damage, byte type) {
        try {
            Message ms = new Message(Cmd.BIG_BOSS);
            DataOutputStream ds = ms.writer();
            ds.writeByte(type);
            ds.writeByte(1);
            ds.writeInt((int) cAttack.id);
            ds.writeDouble(damage);
            ds.flush();
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
            ms.cleanup();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void transform() {
        try {
            this.type++;
            if (this.type <= 2) {
                MobService.gI().hoiSinhMob(this);
            }
            Message ms = new Message(Cmd.BIG_BOSS);
            DataOutputStream ds = ms.writer();
            if (type == 1) {
                ds.writeByte(6);
                ds.writeShort(location.x);
                ds.writeShort(location.y);
            } else if (type == 2) {
                ds.writeByte(5);
            } else {
                super.move(-1000, -1000);
                ds.writeByte(9);
            }
            ds.flush();
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Hirudegarn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void flyTo(int x, int y) {
        try {
            super.move(x, y);
            Message ms = new Message(Cmd.BIG_BOSS);
            DataOutputStream ds = ms.writer();
            ds.writeByte(3);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.flush();
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Hirudegarn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void move(int x, int y) {
        super.move(x, y);
        try {
            Message ms = new Message(Cmd.BIG_BOSS);
            DataOutputStream ds = ms.writer();
            ds.writeByte(8);
            ds.writeShort(x);
            ds.writeShort(y);
            ds.flush();
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
            ms.cleanup();
        } catch (IOException ex) {
            Logger.getLogger(Hirudegarn.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setDie() {
        super.setDie();
        Util.setTimeout(() -> {
            transform();
        }, 3000);
    }

    @Override
    public synchronized void injured(Player plAtt, double damage, boolean dieWhenHpFull) {
        damage /= 2;
        double max = this.point.hp / (double)((this.type + 1) * 20.0);
        if (max <= 0) {
            max = 1;
        }
        if (damage > max) {
            damage = max;
        }
        if (this.point.hp <= 200000.0) {
            damage = 1000.0;
        }
        super.injured(plAtt, damage, dieWhenHpFull);
    }

}
