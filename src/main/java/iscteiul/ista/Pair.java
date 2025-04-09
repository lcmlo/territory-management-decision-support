package iscteiul.ista;

/**
 * Classe genérica que representa um par de dois elementos.
 *
 * @param <F> o tipo do primeiro elemento
 * @param <S> o tipo do segundo elemento
 */
public class Pair<F, S> {

    /** O primeiro elemento do par. */
    public final F first;

    /** O segundo elemento do par. */
    public final S second;

    /**
     * Cria um novo par com os dois elementos fornecidos.
     *
     * @param first  o primeiro elemento
     * @param second o segundo elemento
     */
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
