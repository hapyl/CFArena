package test;

public class Test {

    static class Ladder extends Upper {

    }

    static class Upper {
    }

    static class Upper_Named extends Upper {
    }

    static class Ladder_Named extends Ladder {

    }

    public static void main(String[] args) {
        final Class<? extends Upper_Named> namedUpper = new Upper_Named().getClass();
        final Class<? extends Ladder_Named> namedLadder = new Ladder_Named().getClass();

        System.out.println(namedUpper.getCanonicalName());
        System.out.println(namedLadder.getCanonicalName());
    }

}
