package Foundation;

public class Pair<F,S>{
    public F first;
    public S second;
    public static<F, S> Pair<F,S> make_pair(F _first, S _second)
    {

        // Pair<F,S> rpair = new Pair(_first, _second);
        Pair<F,S> rpair = new Pair();
        rpair.first = _first;
        rpair.second = _second;
        return rpair;
    }
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        if(obj instanceof Pair)
        {
            Pair<F,S> pr = (Pair<F,S>)obj;
            if(pr.first.equals(this.first) && pr.second.equals(this.second)) return true;
            else return false;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
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
