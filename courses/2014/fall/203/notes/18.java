interface Sequenced {
    public Sequence seq();
}
// This is really Iterable
interface MSequenced {
    public MSequence mseq();
}

interface Sequence {
    public int here();
    public boolean notEmpty();
    public Sequence next();
    // If notEmpty returns false, then next() can return anything and
    // here() can return anything
}

// This is really Iterator
interface MSequence {
    public int mhere();
    public boolean mnotEmpty();
    public void mnext();
    // If notEmpty returns false, then next() can return anything and
    // here() can return anything
}

interface Indexed {
    public int size();
    public int read( int idx );
}

class RingBuffer implements Indexed, Sequenced, MSequenced {
    int size;
    int[] smg;
    int end;

    // returns a rb of the size
    RingBuffer(int size) {
        this.size = size;
        this.smg = new int[size];
        this.end = 0;
    }

    public int size() {
        return this.size;
    }

    public RingBuffer insert( int elem ) {
        this.smg[ this.end ] = elem;
        this.end = (this.end + 1) % this.size;
        return this;
    }

    public int read ( int idx ) {
        return
            this.smg[ (this.end - idx + this.size - 1)
                      % this.size  ];
    }

    public Sequence seq() {
        return new AS_Indexed( this );
    }
    class MSeq_for_RB implements MSequence {
        RingBuffer rb;
        int where_am_i;
        MSeq_for_RB( RingBuffer rb ) { 
            this.rb = rb;
            this.where_am_i = 0;
        }
        public boolean mnotEmpty() {
            return this.where_am_i < this.rb.size;
        }
        public void mnext() {
            this.where_am_i = this.where_am_i + 1;
        }
        public int mhere() {
            return this.rb.read( this.where_am_i );
        }
    }
    public MSequence mseq() {
        return new MSeq_for_RB( this );
    }
}

class Evens implements Indexed {
    public Evens () { }
    public int read ( int idx ) {
        return idx * 2;
    }
    public int size () {
        return 100;
    }
}

class AS_Indexed implements Sequence {
    Indexed o;
    int where_am_i;

    public AS_Indexed ( Indexed o ) {
        this.o = o;
        this.where_am_i = 0;
    }
    private AS_Indexed ( Indexed o, int where_am_i ) {
        this.o = o;
        this.where_am_i = where_am_i;
    }

    public int here() {
        return this.o.read( this.where_am_i );
    }
    public boolean notEmpty() {
        return this.where_am_i < this.o.size();
    }
    public Sequence next() {
        return new AS_Indexed( o, where_am_i + 1 );
    }
}

interface List extends Sequenced, Sequence, MSequenced {
    public int length();
    public List remove( int elt );
}

class Empty implements List, MSequence {
    Empty() { }

    public int length() {
        return 0;
    }

    public List remove( int elt ) {
        return this;
    }

    public Sequence seq() { return this; }
    public boolean notEmpty() { return false; }
    public int here() { return -1; }
    public Sequence next() { return this; }

    public MSequence mseq() { return this; }
    public boolean mnotEmpty() { return false; }
    public int mhere() { return -1; }
    public void mnext() { }
}

class Cons implements List {
    int first;
    List rest;

    Cons( int first, List rest ) {
        this.first = first;
        this.rest = rest;
    }

    public int length() {
        return 1 + rest.length();
    }

    public List remove( int elt ) {
        if ( this.first != elt ) {
            return new Cons( this.first, this.rest.remove(elt) );
        } else {
            return this.rest;
        }
    }

    public Sequence seq() { return this; }
    public boolean notEmpty() { return true; }
    public int here() { return this.first; }
    public Sequence next() { return this.rest; }

    class MSeq_for_Cons implements MSequence {
        Cons c;
        boolean used;
        MSequence more;
        public MSeq_for_Cons ( Cons c ) { 
            this.used = false;
            this.c = c; 
            this.more = null;
        }

        public boolean mnotEmpty() {
            if ( this.used == false ) {
                return true; 
            } else {
                return more.mnotEmpty();
            }
        }
        public int mhere() { 
            if ( this.used == false ) {
                return this.c.first; 
            } else {
                return more.mhere();
            }
        }
        public void mnext() {
            if ( this.used == false ) {
                this.used = true;
                this.more = this.c.rest.mseq();
            } else {
                this.more.mnext();
            }
        }

    }
    public MSequence mseq() { return new MSeq_for_Cons( this ); }
}



interface BST extends Sequenced {
    boolean isIn ( int there );
}

class BST_MT implements BST, Sequence {
    BST_MT() { }

    public boolean isIn ( int there ) {
        return false; }

    public Sequence seq() { return this; }
    public boolean notEmpty() { return false; }
    public int here() { return -1; }
    public Sequence next() { return this; }
}

class BST_BR implements BST, Sequence {
    int here;
    BST left;
    BST right;
    BST_BR(BST left, int here, BST right) {
        this.left = left;
        this.here = here;
        this.right = right; }

    public boolean isIn ( int there ) {
        if ( here == there ) {
            return true; }
        else {
            if ( there < here ) {
                return left.isIn(there); }
            else {
                return right.isIn(there); } } }

    public Sequence seq() { return this; }
    public boolean notEmpty() { return true; }
    public int here() {  return this.here; }
    public Sequence next() {
        return new AS_Cat( this.left.seq(), this.right.seq() );
    }
}

class AS_Cat implements Sequence {
    Sequence left;
    Sequence right;

    AS_Cat(Sequence l, Sequence r) {
        System.out.println("Alloc");
        this.left = l;
        this.right = r;
    }

    public boolean notEmpty() {
        return this.left.notEmpty() || this.right.notEmpty();
    }
    public int here() {
        if ( this.left.notEmpty() ) {
            return this.left.here();
        } else {
            return this.right.here();
        }
    }
    public Sequence next() {
        if ( this.left.notEmpty() ) {
            // Potential optimizaiton if right is empty
            return new AS_Cat( this.left.next(), this.right );
        } else {
            return this.right.next();
        }
    }
}

class Naturals implements Sequence {
    int starting_from;
    public Naturals() { this.starting_from = 0; }
    private Naturals(int sf) { this.starting_from = sf; }
    public int here() { return starting_from; }
    public boolean notEmpty() { return true; }
    public Sequence next() { return new Naturals( starting_from + 1 ); }
}

interface Action {
    public int apply( int h );
}

interface TwoAction {
    public int apply( int x, int y );
}

class AS_FEveryone implements Sequence {
    Action F;
    Sequence seq;
    public AS_FEveryone ( Action F, Sequence seq ) {
        this.F = F;
        this.seq = seq;
    }
    public int here() {
        return this.F.apply(this.seq.here());
    }
    public boolean notEmpty() { return this.seq.notEmpty(); }
    public Sequence next() {
        return new AS_FEveryone( F, this.seq.next() );
    }
}

class AS_FEveryone2 implements Sequence {
    TwoAction F;
    boolean cached;
    int cache;
    Sequence xseq;
    Sequence yseq;
    public AS_FEveryone2 ( TwoAction F, Sequence xseq, Sequence yseq ) {
        this.F = F;
        this.xseq = xseq;
        this.yseq = yseq;
        this.cached = false;
    }
    public int here() {
        if ( this.cached == false ) {
            this.cache = this.F.apply(this.xseq.here(), this.yseq.here());
            this.cached = true;
        }
        return this.cache;
    }
    public boolean notEmpty() { return this.xseq.notEmpty() && this.yseq.notEmpty(); }
    Sequence theNext;
    public Sequence next() {
        if (theNext == null) {
            theNext = new AS_FEveryone2( F, this.xseq.next(), this.yseq.next() );
        }
        return theNext;
    }
}

class A_Mult2 implements Action {
    public A_Mult2() { }
    public int apply( int h ) {
        return 2 * h;
    }
}

class A2_Plus implements TwoAction {
    public A2_Plus() { }
    public int apply( int x, int y ) {
        int z = x + y;
        System.out.println("Adding " + x + " to " + y + " and getting " + z);
        return z;
    }
}

class AS_Limited implements Sequence {
    int limit;
    Sequence seq;
    public AS_Limited( int limit, Sequence seq ) {
        this.limit = limit;
        this.seq = seq;
    }
    public int here() { return this.seq.here(); }
    public boolean notEmpty() {
        if (limit <= 0) {
            return false;
        } else {
            return this.seq.notEmpty();
        }
    }
    public Sequence next() { return new AS_Limited( limit - 1, this.seq.next() ); }
}

class SequenceBox {
    Sequence a_seq;
    public SequenceBox() { }
    public void setBox( Sequence s ) {
        this.a_seq = s;
    }
    public Sequence getBox() {
        return this.a_seq;
    }
}

class AS_Fibs implements Sequence {
    SequenceBox other_stuffbox;
    Sequence theRest;
    public AS_Fibs() {
        this.other_stuffbox = new SequenceBox();
    }
    public void setRest( Sequence other_stuff ) {
        this.other_stuffbox.setBox( other_stuff );
    }
    public int here() {
        return 1;
    }
    public boolean notEmpty() { return true; }
    public Sequence next() {
        if ( this.theRest == null ) {
            this.theRest = new AS_FibsRest(other_stuffbox);
        }
        return this.theRest;
    }
}

class AS_FibsRest implements Sequence {
    SequenceBox other_stuffbox;
    public AS_FibsRest(SequenceBox other_stuffbox) {
        this.other_stuffbox = other_stuffbox;
    }
    public int here() {
        return 1;
    }
    public boolean notEmpty() { return true; }
    public Sequence next() {
        return this.other_stuffbox.getBox();
    }
}

interface Filter {
    public boolean apply( int h );
}

class AS_Filtered implements Sequence {
    Filter F;
    Sequence seq;
    public AS_Filtered ( Filter F, Sequence seq ) {
        this.F = F;
        this.seq = seq;
        // Search for the first thing that F returns true to.
        while ( this.seq.notEmpty() ) {
            int v = this.seq.here();
            if ( ! this.F.apply( v ) ) {
                this.seq = this.seq.next();
            } else {
                break;
            }
        }
    }
    public int here() { return this.seq.here(); }
    public boolean notEmpty() { return this.seq.notEmpty(); }
    Sequence next;
    public Sequence next() {
        if ( this.next == null ) {
            this.next = new AS_Filtered( F, this.seq.next() );
        }
        return this.next;
    }
}

class AS_LimitWhile implements Sequence {
    Filter F;
    Sequence seq;
    boolean notEmpty;
    public AS_LimitWhile( Filter F, Sequence seq ) {
        this.F = F;
        this.seq = seq;
        if ( ! this.seq.notEmpty() || ! this.F.apply( this.seq.here() ) ) {
            this.notEmpty = false;
        } else {
            this.notEmpty = true;
        }
    }
    public int here() { return this.seq.here(); }
    public boolean notEmpty() { return this.notEmpty; }
    public Sequence next() { return new AS_LimitWhile( F, this.seq.next() ); }
}

class F_LessThanEq implements Filter {
    int m;
    public F_LessThanEq( int m ) { this.m = m; }
    public boolean apply ( int n ) { return n <= m; }
}

class F_PrimeHuh implements Filter {
    Sequence primes;
    public F_PrimeHuh() {}
    public boolean apply ( int n ) {
        if ( n < 2 ) { return false; }
        else if ( n == 2 ) { return true; }
        else {
            Sequence seq = new AS_LimitWhile( new F_LessThanEq( (int) Math.floor( Math.sqrt( n ) ) ), this.primes );
            // Is n NOT divisible by everything in m
            boolean ret = true;
            while ( ret && seq.notEmpty() ) {
                int m = seq.here();
                System.out.println("Checking " + n + " against " + m);
                ret = ret && (! ((n % m) == 0));
                seq = seq.next();
            }
            return ret;
        }
    }
    public void setPrimes ( Sequence primes ) {
        this.primes = primes;
    }
}

class C18 {
    public static int sumIt ( Sequenced o ) {
        return sumItS( o.seq() );
    }
    public static int sumItS ( Sequence as ) {
        int sum = 0;
        while ( as.notEmpty() ) {
            sum = sum + as.here();
            as = as.next();
        }
        return sum;
    }
    public static int sumItMS ( MSequence as ) {
        int sum = 0;
        while ( as.mnotEmpty() ) {
            int v = as.mhere();
            System.out.println("Got a " + v + " while sum was " + sum );
            sum = sum + v;
            as.mnext();
        }
        return sum;
    }
    public static boolean areYouThere ( Sequenced o, int you ) {
        return areYouThereS( o.seq(), you );
    }
    public static boolean areYouThereS ( Sequence as, int you ) {
        while ( as.notEmpty() ) {
            if ( as.here() == you ) {
                return true;
            }
            as = as.next();
        }
        return false;
    }

    public static void main(String[] args) {
        RingBuffer rb = (new RingBuffer(2));
        rb.insert(1);
        rb.insert(2);
        rb.insert(3);
        rb.insert(4);
        System.out.println(rb.read(0) + " should be " + 4 );
        System.out.println(rb.read(1) + " should be " + 3 );

        List mt = (new Empty());
        List l5 = (new Cons(5, mt));
        List l25 = (new Cons(2, l5));
        List l325 = (new Cons(3, l25));
        System.out.println(l325.length() + " should be " + 3);
        System.out.println(l325.remove(2).length() + " should be " + 2);
        System.out.println(l325.remove(8).length() + " should be " + 3);

        BST b_mt = new BST_MT();
        BST b_5 = new BST_BR( b_mt, 5, b_mt );
        BST b_7 = new BST_BR( b_mt, 7, b_mt );
        BST b_6 = new BST_BR( b_5, 6, b_7 );
        System.out.println( b_6.isIn( 5 ) + " should be " + true );

        System.out.println("\nSequenced\n");
        System.out.println( sumIt(rb) + " should be " + (4 + 3) );
        System.out.println( sumIt(l325) + " should be " + (3 + 2 + 5) );
        System.out.println( areYouThere(l325, 5) + " should be " + true );
        System.out.println( sumIt(b_6) + " should be " + (6 + 5 + 7) );
        System.out.println( areYouThere(b_6, 5) + " should be " + true );

        System.out.println( sumItS(new AS_Cat(l325.seq(), b_6.seq())) + " should be " +
                            (3 + 2 + 5 + 6 + 5 + 7) );

        System.out.println( sumItS(new AS_Indexed(new Evens())) + " should be " +
                            (9900) );

        System.out.println( areYouThereS(new Naturals(), 25) + " should be " +
                            true );
        // System.out.println( areYouThereS(new Naturals(), -1) + " should be " +
        //                    false );

        System.out.println( sumItS(new AS_FEveryone(new A_Mult2(),
                                                    (new AS_Cat(l325.seq(), b_6.seq()))))
                            + " should be " +
                            (2 * (3 + 2 + 5 + 6 + 5 + 7)) );

        System.out.println( areYouThereS(new AS_FEveryone(new A_Mult2(), new Naturals()), 26)
                            + " should be " +
                            true );

        System.out.println( sumItS(new AS_Limited(100, new Naturals())) + " should be " +
                            ((100 * 99)/2) );

        AS_Fibs fibs = new AS_Fibs();
        Sequence fibsAfterSecond = new AS_FEveryone2( new A2_Plus(), fibs, fibs.next() );
        fibs.setRest( fibsAfterSecond );

        // fibsAfterSecond = F( fibs.next() )
        // fibs.next().next() = fibsAfterSecond;

        printSome( new AS_Limited( 10, fibs ) );

        F_PrimeHuh primeHuh = new F_PrimeHuh();
        Sequence primes = new AS_Filtered( primeHuh, new Naturals() );
        primeHuh.setPrimes( primes );
        printSome( new AS_Limited( 30, primes ) );


        GList<String> gmt = new GEmpty<String>();
        GList<String> lJay = new GCons<String>("Jay", gmt);
        GList<String> lTom = new GCons<String>("Tom", lJay);

        GprintSome( lTom.seq() );

        GSequence<Integer> ElTres =
            new GAS_FEveryone<String,Integer>( new GA_StringToNum(), lTom.seq() );

        GprintSome( ElTres );

        GList<GList<String>> Lgmt = new GEmpty<GList<String>>();
        GList<GList<String>> LlJay = new GCons<GList<String>>(lJay, Lgmt);
        GList<GList<String>> LlTom = new GCons<GList<String>>(lTom, LlJay);

        GList<Sequence> Lsa = new GEmpty<Sequence>();
        GList<Sequence> Lsb = new GCons<Sequence>(rb.seq(), Lsa);
        GList<Sequence> Lsc = new GCons<Sequence>(b_6.seq(), Lsb);
        GList<Sequence> Lsd = new GCons<Sequence>(l325.seq(), Lsc);

        GPrintSomes( Lsd );

        System.out.println("MSeq");

        System.out.println( sumItMS(l325.mseq()) + " should be " + (3 + 2 + 5) );
        System.out.println( sumItMS(l325.mseq()) + " should be " + (3 + 2 + 5) );
        System.out.println( sumItMS(rb.mseq()) + " should be " + (3 + 4) );
    }

    static void printSome ( Sequence s ) {
        while ( s.notEmpty() ) {
            System.out.println(s.here() + " ");
            s = s.next();
        }
        System.out.println("\n");
    }

    static void GprintSome ( GSequence s ) {
        while ( s.notEmpty() ) {
            System.out.println(s.here() + " ");
            s = s.next();
        }
        System.out.println("\n");
    }

    static void GPrintSomes ( GSequence<Sequence> s ) {
        while ( s.notEmpty() ) {
            printSome( s.here() );
            s = s.next();
        }
        System.out.println("\n");
    }
}

// Primes (just show)
// Type-genericity
// MutableAS
// Fold

interface GSequenced<X> {
    public GSequence<X> seq();
}

interface GSequence<X> {
    public X here();
    public boolean notEmpty();
    public GSequence<X> next();
    // If notEmpty returns false, then next() can return anything and
    // here() can return anything
}

interface GList<X> extends GSequenced<X>, GSequence<X> {
    public int length();
    public GList<X> remove( X elt );
}

class GEmpty<X> implements GList<X> {
    GEmpty() { }

    public int length() {
        return 0;
    }

    public GList<X> remove( X elt ) {
        return this;
    }

    public GSequence<X> seq() { return this; }
    public boolean notEmpty() { return false; }
    public X here() { throw new RuntimeException("No X here"); }
    public GSequence<X> next() { return this; }
}

class GCons<X> implements GList<X> {
    X first;
    GList<X> rest;

    GCons( X first, GList<X> rest ) {
        this.first = first;
        this.rest = rest;
    }

    public int length() {
        return 1 + rest.length();
    }

    public GList<X> remove( X elt ) {
        if ( ! this.first.equals(elt) ) {
            return new GCons<X>( this.first, this.rest.remove(elt) );
        } else {
            return this.rest;
        }
    }

    public GSequence<X> seq() { return this; }
    public boolean notEmpty() { return true; }
    public X here() { return this.first; }
    // public X here() { return this.rest.here(); }
    public GSequence<X> next() { return this.rest; }
}

interface GAction<X,Y> {
    public Y apply( X h );
}

class GAS_FEveryone<X,Y> implements GSequence<Y> {
    GAction<X,Y> F;
    GSequence<X> seq;
    public GAS_FEveryone ( GAction<X,Y> F, GSequence<X> seq ) {
        this.F = F;
        this.seq = seq;
    }
    public Y here() {
        return this.F.apply(this.seq.here());
    }
    public boolean notEmpty() { return this.seq.notEmpty(); }
    public GSequence<Y> next() {
        return new GAS_FEveryone<X,Y>( F, this.seq.next() );
    }
}

class GA_StringToNum implements GAction<String,Integer> {
    public GA_StringToNum() {}
    public Integer apply (String h) {
        return h.length();
    }
}

interface Comparable {
    public boolean lessThanEq ( /* Comparable this, */ Comparable y );
}

// F-bounded polymorphism
interface FiniteSet<X extends Comparable> {
}
