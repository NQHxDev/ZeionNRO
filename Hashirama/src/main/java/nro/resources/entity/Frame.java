/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.resources.entity;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@Getter
public class Frame {

    @SerializedName("sprite_id")
    private int spriteID;
    private int dx;
    private int dy;

}
