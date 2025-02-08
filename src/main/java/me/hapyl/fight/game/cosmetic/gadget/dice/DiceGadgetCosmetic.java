package me.hapyl.fight.game.cosmetic.gadget.dice;

import me.hapyl.eterna.module.registry.Key;
import me.hapyl.fight.game.cosmetic.Rarity;
import me.hapyl.fight.game.reward.Reward;

import javax.annotation.Nonnull;

public class DiceGadgetCosmetic extends Dice {
    public DiceGadgetCosmetic(@Nonnull Key key) {
        super(key, "Dice", Rarity.EPIC, 1_000, Reward.ofRepeatableResource("Dice Reward", 100_000));

        setSide(1, "6e22c298e7c6336af17909ac1f1ee6834b58b1a3cc99aba255ca7eaeb476173", 24);
        setSide(2, "71b7a73fc934c9de9160c0fd59df6e42efd5d0378e342b68612cfec3e894834a", 24);
        setSide(3, "abe677a1e163a9f9e0afcfcde0c95365553478f99ab11152a4d97cf85dbe66f", 24);
        setSide(4, "af2996efc2bb054f53fb0bd106ebae675936efe1fef441f653c2dce349738e", 24);
        setSide(5, "e0d2a3ce4999fed330d3a5d0a9e218e37f4f57719808657396d832239e12", 24);
        setSide(6, "41a2c088637fee9ae3a36dd496e876e657f509de55972dd17c18767eae1f3e9", 1);
    }

}
