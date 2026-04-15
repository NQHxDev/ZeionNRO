/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nro.models;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Kitak
 */
@Getter
@Setter
public class Part {

    public short id;

    byte type;

    private PartImage[] pi;

    public short getIcon(int index) {
        return pi[index].getIcon();
    }
}
