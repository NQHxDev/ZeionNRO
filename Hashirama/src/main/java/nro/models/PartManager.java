/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class PartManager {

    public static class PartPot {

        public int id;

        public int type;

        public List<PartDetail> partDetails;

        public PartPot() {
            this.partDetails = new ArrayList();
        }
    }

    public static class PartDetail {

        public short iconId;

        public byte dx;

        public byte dy;

        public PartDetail(short iconId, byte dx, byte dy) {
            this.iconId = iconId;
            this.dx = dx;
            this.dy = dy;
        }
    }

}
