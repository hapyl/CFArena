package me.hapyl.fight.terminology;

import com.google.common.collect.Lists;
import me.hapyl.fight.game.attribute.AttributeType;
import me.hapyl.fight.terminology.terms.TermPiercingDamage;

import javax.annotation.Nonnull;
import java.util.List;

public enum EnumTerm {

    BREACH_DAMAGE(Term.builder()
                      .setName("Breach Damage")
                      .setShortDescription("""
                            Ignores defense.
                            """)
                      .setDescription("""
                            Ignores victim's %s, while still being affected by damager's %s.
                            
                            &4Cannot crit.
                            """.formatted(AttributeType.DEFENSE, AttributeType.ATTACK))
                      .build()),

    TRUE_DAMAGE(
            Term.builder()
                .setName("True Damage")
                .setShortDescription("""
                        Ignores any external modifications.
                        """)
                .setDescription("""
                        Ignores any attributes of either the victim or the damager.
                        
                        What you see is what you get.
                        
                        &4Cannot crit.
                        """)
                .build()
    ),

    PIERCING_DAMAGE(new TermPiercingDamage()),

    BASE_CHANCE(Term.builder()
                    .setName("Base Chance")
                    .setShortDescription("""
                            Base chance can be increased in internal or external way.
                            """)
                    .setDescription("""
                            Unlike fixed chance, base chance can be increased in a variety of ways.
                            """)
                    .build()),

    BLOOD_DEBT(
            Term.builder()
                .setName("Blood Debt")
                .setShortDescription("""
                        A bloody debt that must be repaid by healing.
                        """)
                .setDescription("""
                        A debuff that must be healed before health can be healed.
                        """)
                .build()
    ),

    ;

    public final Term term;

    EnumTerm(Term term) {
        this.term = term;
    }

    @Nonnull
    @Override
    public String toString() {
        // Since most of the terms are used in lore,
        // lower case makes much more sense than Capitalized Every Word
        return term.toString().toLowerCase();
    }

    @Nonnull
    public static List<Term> byContext(String query) {
        List<Term> possibleTerms = Lists.newArrayList();

        query = query.toLowerCase();

        for (EnumTerm enumTerm : values()) {
            if (enumTerm.term.isMatching(query)) {
                possibleTerms.add(enumTerm.term);
            }
        }

        return possibleTerms;
    }

    @Nonnull
    public static List<Term> listTerms() {
        List<Term> terms = Lists.newArrayList();

        for (EnumTerm term : values()) {
            terms.add(term.term);
        }

        return terms;
    }
}
