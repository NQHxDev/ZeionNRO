/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.services.func;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nro.jdbc.daos.PlayerDAO;
import nro.models.player.Player;
import nro.server.Client;
import nro.services.InventoryService;
import nro.services.Service;
import nro.utils.Util;

/**
 *
 * @author Việt Nguyễn - 0857853150
 */
public class SoMayMan implements Runnable {

    public int SoGiaiTruoc;
    public boolean baotri = false;
    public long lastTimeEnd;
    public List<Player> PlayerThamGia = new ArrayList<>();
    public List<Player> TrungGiai = new ArrayList<>();
    private static SoMayMan instance;

    public static SoMayMan gI() {
        if (instance == null) {
            instance = new SoMayMan();
        }
        return instance;
    }

    public void addPlayerSMM(Player pl) {
        if (!PlayerThamGia.contains(pl)) {
            PlayerThamGia.add(pl);
        }
    }

    public void addPlayerTrung(Player pl) {
        if (!TrungGiai.contains(pl)) {
            TrungGiai.add(pl);
        }
    }

    public boolean KetQuaWin(Player pl, int kqua) {
        for (int num : pl.soMayMan) {
            return kqua == num;
        }
        return false;
    }

    public void PhatThuong(Player pl, int num) {
        int goldC = 100_000;
        addPlayerTrung(pl);
        Service.getInstance().sendsoxo(pl, num, true);
        pl.inventory.ruby += goldC;
        Service.getInstance().sendMoney(pl);
    }

    public void ThuaCuoc(Player pl, int num) {
        Service.getInstance().sendsoxo(pl, num, false);
    }

    public void ThuongOff(Player pl) {
        int goldC = 100_000;
        addPlayerTrung(pl);
        PlayerDAO.PhatThuongOfline(pl, goldC);
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (((this.lastTimeEnd - System.currentTimeMillis()) / 1000) <= 0) {
                    TrungGiai.clear();
                    int num = Util.nextInt(0, 99);//Util.nextInt(0, 99)
                    SoGiaiTruoc = num;
                    if (!this.PlayerThamGia.isEmpty()) {
                        for (int i = 0; i < PlayerThamGia.size(); i++) {
                            Player pl = this.PlayerThamGia.get(i);
                            if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                Player pll = new Player();
                                if (Client.gI().getPlayer(pl.id) != null){
                                    pll = Client.gI().getPlayer(pl.id);
                                }
                                if (KetQuaWin(pll, num) == true) {
                                    PhatThuong(pll, num);
                                } else {
                                    ThuaCuoc(pll, num);
                                }
                                pll.soMayMan.clear();
                            } else {
                                if (KetQuaWin(pl, num) == true) {
                                    ThuongOff(pl);
                                }
                                PlayerDAO.XoaSoMayMan(pl);
                            }
                        }
                    }
                    PlayerThamGia.clear();
                    this.lastTimeEnd = System.currentTimeMillis() + 60000;
                }
                Thread.sleep(500);
            } catch (Exception e) {
            }
        }
    }
}
