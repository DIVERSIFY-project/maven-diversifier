package fr.inria.diversify.utils

/**
 * Created by nicolas on 25/08/2015.
 */
class Looper {
    private Closure code

    static Looper loop( Closure code ) {
        new Looper(code:code)
    }

    void until( Closure test ) {
        code()
        while (!test()) {
            code()
        }
    }
}
