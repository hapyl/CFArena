package me.hapyl.fight.alphabet;

import java.util.HashMap;
import java.util.Map;

public class AlphabetImpl implements Alphabet {

    private final Map<Character, Character> enToCharMap;
    private final Map<Character, Character> charToEnMap;

    private AlphabetImpl() {
        enToCharMap = new HashMap<>();
        charToEnMap = new HashMap<>();
    }

    @Override
    public char getAlphabetChar(char enChar) {
        return enToCharMap.getOrDefault(Character.toLowerCase(enChar), enChar);
    }

    @Override
    public char getEnglishChar(char alphabetChar) {
        return charToEnMap.getOrDefault(alphabetChar, alphabetChar);
    }

    static AlphabetImpl of(
            char a, char b, char c, char d, char e, char f,
            char g, char h, char i, char j, char k, char l,
            char m, char n, char o, char p, char q, char r,
            char s, char t, char u, char v, char w, char x,
            char y, char z) {

        final AlphabetImpl alphabet = new AlphabetImpl();
        alphabet.mapChar('a', a);
        alphabet.mapChar('b', b);
        alphabet.mapChar('c', c);
        alphabet.mapChar('d', d);
        alphabet.mapChar('e', e);
        alphabet.mapChar('f', f);
        alphabet.mapChar('g', g);
        alphabet.mapChar('h', h);
        alphabet.mapChar('i', i);
        alphabet.mapChar('j', j);
        alphabet.mapChar('k', k);
        alphabet.mapChar('l', l);
        alphabet.mapChar('m', m);
        alphabet.mapChar('n', n);
        alphabet.mapChar('o', o);
        alphabet.mapChar('p', p);
        alphabet.mapChar('q', q);
        alphabet.mapChar('r', r);
        alphabet.mapChar('s', s);
        alphabet.mapChar('t', t);
        alphabet.mapChar('u', u);
        alphabet.mapChar('v', v);
        alphabet.mapChar('w', w);
        alphabet.mapChar('x', x);
        alphabet.mapChar('y', y);
        alphabet.mapChar('z', z);

        return alphabet;
    }

    protected AlphabetImpl mapChar(char en, char c) {
        this.enToCharMap.put(en, c);
        this.charToEnMap.put(c, en);

        return this;
    }
}
