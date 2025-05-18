package iscteiul.ista;

import java.util.Objects;

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

    /**
     * Devolve o primeiro valor do par.
     *
     * @return o valor {@code F} armazenado em first
     */
    public F getFirst() {
        return first;
    }
    /**
     * Devolve o segundo valor do par.
     *
     * @return o valor {@code S} armazenado em second
     */
    public S getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> other = (Pair<?, ?>) o;
        return Objects.equals(first, other.first) && Objects.equals(second, other.second);
    }
}
