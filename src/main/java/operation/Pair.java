package operation;

import java.util.Objects;

public class Pair<F,S>{
    public F first;
    public S second;
    public static<F, S> Pair<F,S> make_pair(F _first, S _second)
    {

        // Pair<F,S> rpair = new Pair(_first, _second);
        Pair<F,S> rpair = new Pair<>();
        rpair.first = _first;
        rpair.second = _second;
        return rpair;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair)) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    public Pair()
    {
        first = null;
        second = null;
    }
    public Pair(F _first, S _second)
    {
        this.first = _first;
        this.second = _second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
