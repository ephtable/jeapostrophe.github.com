// A Person is a
//   new Person ( firstName, lastName, cuteness, eyeColor, height, weight, isAlive )
// where
//  - firstName is a String
//  - lastName is a String
//  - cuteness is a double [0,100]
//  - eyeColor is a String
//  - height is a double (inchers)
//  - weight is a double (pounders)
//  - isAlive is a boolean
class Person {
    public String firstName;
    public String lastName;
    public double cuteness;
    public String eyeColor;
    public double height;
    public double weight;
    public boolean isAlive;

    public Person ( String firstName0, String lastName0, double cuteness0, String eyeColor0,
		    double height0, double weight0, boolean isAlive0 ) {
	firstName = firstName0;
	lastName = lastName0;
	cuteness = cuteness0;
	eyeColor = eyeColor0;
	height = height0;
	weight = weight0;
	isAlive = isAlive0;
    }

    // willMarry : Person -> boolean
    // Computes whether the person should be married (to you!)
    // Examples:
    //  willMarry(jay) = true;
    //  willMarry(tom) = false;

    // Steps to go from a "function" to a "method"
    // 1. Change static to public
    // 2. Change argument name to "this"
    // 3. Remove the argument
    public boolean willMarry (  ) {
	// Take stock!
	/*
	return ... this.firstName ... this.lastName ...
	    this.cuteness ... this.weight ... this.height ...
	    this.eyeColor ... this.isAlive ...;
	*/
	return (this.cuteness >= 80.35) ;
    }
}

// A Posn is a 
//   new Posn ( x, y )
// where
//  - x is a double
//  - y is a double
class Posn {
    public double x;
    public double y;

    public Posn ( double x0, double y0 ) {
	x = x0;
	y = y0;
    }

    // moveTheGuy : Posn double double -> Posn
    // Computes the new position of the guy after moving dx in the x direction and dy in the y dir.
    // Examples:
    //  moveTheGuy( new Posn( 2, 2 ), 1, -1 ) = new Posn (3, 1)
    public Posn moveTheGuy ( double dx, double dy ) {
	// return .... ;
	// Take stock
	// return ... p ... dx ... dy ; // <- We know this from Posn p
	// return new Posn ( ... p.x ... p.y ... dx ... dy ); // <- We know this from Posn moveTheGuy
	// return new Posn ( ... p.x ... p.y ... dx ... dy, ... p.x ... p.y ... dx ... dy );	
	return new Posn ( this.x + dx, this.y + dy );	
    }

    // distanceToOrigin : Posn -> double
    // Compute the distance of the posn from the origin (0,0)
    // Examples:
    //  distanceToOrigin ( new Posn ( 0, 3 ) ) = 3
    //  distanceToOrigin ( new Posn ( 2, 0 ) ) = 2
    //  distanceToOrigin ( new Posn ( 3, 4 ) ) = 5
    public double distanceToOrigin ( ) {
	// return ... p.x ... p.y ... ;
	return Math.sqrt(Math.pow(this.x,2) + Math.pow(this.y,2)) ;
    }

    // physicsForPosns : Posn double -> Posn
    // Purpose: Computes a new Posn after an x movment of some units
    // Examples:
    //  physicsForPosns ( new Posn ( 0, 0 ), 2.3 ) = new Posn ( 2.3 , 0 )
    //  physicsForPosns ( new Posn ( 1, 0 ), 2.3 ) = new Posn ( 3.3 , 0 )
    public Posn physicsForPosns ( double xmovement ) {
	/*
	  return ... p.x ... p.y ... xmovement ;
	  return new Posn ( ... p.x ... p.y ... xmovement,
	 		    ... p.x ... p.y ... xmovement );
	*/
	return new Posn ( this.x + xmovement,
			  this.y );
    }


}

// A Ball is a
//   new Ball ( pos, xvelocity )
// where
//   - pos is a Posn
//   - xvelocity is double [measured in Planck length / Planck time]
class Ball {
    public Posn pos;
    public double xvelocity;

    public Ball ( Posn pos0, double xvelocity0 ) {
	pos = pos0;
	xvelocity = xvelocity0;
    }

    // physics : Ball -> Ball
    // Purpose: Computes the position of 'this' after one step of time (Planck time units)
    // Example:
    //  physics ( new Ball ( new Posn ( 0, 0 ), 2.3 ) ) =
    //            new Ball ( new Posn ( 2.3, 0 ), 2.3 )
    public Ball physics ( ) {
	// return ... physicsForPosns( ... aBall.pos ... ) ... aBall.xvelocity ... ;
	/*
	return new Ball ( ... physicsForPosns( ... aBall.pos ... ) ... aBall.xvelocity ...,
			  ... physicsForPosns( ... aBall.pos ... ) ... aBall.xvelocity ... );
	*/
	return new Ball ( (this.pos).physicsForPosns( this.xvelocity ),
			  this.xvelocity );

    }

}

// A PhoneNumber is a...
//   new PhoneNumber ( areaCode, prefix, line )
// where
//   - areaCode is an int
//   - prefix is an int
//   - line is an int
class PhoneNumber {
    public int areaCode;
    public int prefix;
    public int line;

    public PhoneNumber ( int areaCode0, int prefix0, int line0 ) {
	areaCode = areaCode0;
	prefix = prefix0;
	line = line0;
    }

    // inAreaHuh : PhoneNumber int -> boolean
    // Examples:
    // inAreaHuh ( new PhoneNumber ( 978, 555, 1723 ), 978 ) = true
    // inAreaHuh ( new PhoneNumber ( 978, 555, 1723 ), 801 ) = false
    public boolean inAreaHuh ( int someArea ) {
	// return ... pn.areaCode ... pn.prefix ... pn.line ... someArea ;
	return this.areaCode == someArea ;
    }

}

// An AddressBookEntry is a...
//   new AddressBookEntry ( home, office, cell )
// where
//   - home is a PhoneNumber
//   - office is a PhoneNumber
//   - cell is a PhoneNumber
class AddressBookEntry {
    /*
    public int homeAreaCode;
    public int homePrefix;
    public int homeLine;
    */
    public PhoneNumber home;
    public PhoneNumber office;
    public PhoneNumber cell;

    public AddressBookEntry ( PhoneNumber home0, PhoneNumber office0, PhoneNumber cell0 ) {
	home = home0;
	office = office0;
	cell = cell0;
    }

    // allInAreaHuh : AddressBookEntry int -> boolean
    // Purpose: determine if every phone number is in the same specific area
    // Example:
    //   allInAreaHuh ( new AddressBookEntry ( new PhoneNumber ( 978, 555, 1723 ),
    //                                         new PhoneNumber ( 978, 555, 9211 ),
    //                                         new PhoneNumber ( 978, 565, 9211 ) ),
    //                  978 )
    //   = true
    public boolean allInAreaHuh ( int someArea ) {
	/*
	return ... ae.home ... ae.office ... ae.cell ... someArea ;
	return ... inAreaHuh( ... ae.home ...) ... inAreaHuh( ... ae.office ...) ... inAreaHuh( ... ae.cell ...) ... someArea ;
	return ... inAreaHuh( ae.home, someArea ) ... inAreaHuh( ae.office, someArea ) ... inAreaHuh( ae.cell, someArea ) ... someArea ;
	*/
	return this.home.inAreaHuh( someArea ) 
	    && this.office.inAreaHuh( someArea ) 
	    && this.cell.inAreaHuh( someArea );	
    }

}

/*
Write a data definition for three digit numbers, where there are three integers.
*/

// A TDN is a...
//   new TDN ( first, second, third )
// where
//   - first is a integer
//   - second is a integer
//   - third is an integer
class TDN {
    public int first;
    public int second;
    public int third;

    public TDN ( int first0, int second0, int third0 ) {
	first = first0;
	second = second0;
	third = third0;
    }

    /*
      Write a function called reveal that takes three arguments: a 'chosen' three digit number, a 'current' three digit number, and 'guess' integer. The function produces a new 'current' number where the digits are the same as before, unless the 'guess' is the same as one of the digits, in which case it is "revealed". For example,

      reveal( new TDN(1, 2, 3), new TDN(0, 0, 0), 1) = new TDN( 1, 0, 0)
      reveal( new TDN(1, 2, 3), new TDN(1, 0, 0), 3) = new TDN( 1, 0, 3)
      reveal( new TDN(1, 2, 3), new TDN(1, 0, 3), 6) = new TDN( 1, 0, 3)
      reveal( new TDN(1, 2, 3), new TDN(1, 0, 3), 2) = new TDN( 1, 2, 3)
      reveal( new TDN(1, 2, 2), new TDN(1, 0, 3), 2) = new TDN( 1, 2, 2)
    */
    // Contract: reveal : TDN TDN int -> TDN
    // Purpose: see above
    public TDN reveal ( TDN chosen, int guess ) {
	return revealThird( chosen,
			    revealSecond ( chosen,
					   this.revealFirst ( chosen, guess ),
					   guess ),
			    guess );
    }

    // Contract: revealFirst : TDN TDN int -> TDN
    // Purpose: like reveal, but only reveals the first digit
    // Examples:
    // revealFirst( new TDN(1, 2, 3), new TDN(0, 0, 0), 1) = new TDN( 1, 0, 0)
    // revealFirst( new TDN(1, 2, 3), new TDN(1, 0, 0), 3) = new TDN( 1, 0, 0)
    public TDN revealFirst ( TDN chosen, int guess ) {
	// return ... ;
	// return ... chosen.first ... chosen.second ... chosen.third ... current.first ... current.second ... current.third .. guess ;
	/*
	return new TDN ( ... chosen.first ... chosen.second ... chosen.third ... current.first ... current.second ... current.third .. guess,
			 ... chosen.first ... chosen.second ... chosen.third ... current.first ... current.second ... current.third .. guess,
			 ... chosen.first ... chosen.second ... chosen.third ... current.first ... current.second ... current.third .. guess ) ;
	*/
	if ( this.first == guess ) {
	    return new TDN ( chosen.first,
			     this.second,
			     this.third) ;
	} else {
	    return this;
	    /*
	      return new TDN ( current.first,
	                       current.second,
			       current.third) ;
	    */
	}
    }

    static TDN revealSecond ( TDN chosen, TDN current, int guess ) {
	if ( chosen.second == guess ) {
	    return new TDN ( current.first,
			     chosen.second,
			     current.third) ;
	} else {
	    return current;
	}
    }

    static TDN revealThird ( TDN chosen, TDN current, int guess ) {
	if ( chosen.third == guess ) {
	    return new TDN ( current.first,
			     current.second,
			     chosen.third) ;
	} else {
	    return current;
	}
    }

}

// java scratch
// =>
// call scratch.main(...)
class scratch {

    static int f () {
	return 5;
    }

    static Posn shifted ( int x ) {
	return new Posn ( x , 0 );
    }


    public static void main (String[] args) {	
	Posn p0 = new Posn ( 2.54, 7.1 ) ;
	Posn p1 = new Posn ( 2.0, Math.sqrt(2) ) ;
	Posn pMario = new Posn ( 2.0, 2.0 ) ;

	System.out.format("The answer is: %s%n", "Amaaaaaazing");
	// System.out.format("The answer is: %Posn%n", p0);
	System.out.format("The answer is: (%f,%f)%n", p0.x, p0.y);
	System.out.format("The answer is: (%f,%f)%n", p1.x, p1.y);
	System.out.format("The answer is: (%f,%f)%n", pMario.x, pMario.y);
	
	System.out.format("The answer is: (%f,%f) but should be (%f,%f)%n", 
			  new Posn( 2, 2 ).moveTheGuy( 1, -1 ).x,
			  // Posn.moveTheGuy( new Posn( 2, 2 ), 1, -1 ).y,
			  new Posn( 2, 2 ).moveTheGuy( 1, -1 ).y,
			  new Posn (3, 1).x,
			  new Posn (3, 1).y);

	System.out.format("The answer is: (%f,%f) but should be (%f,%f)%n", 
			  pMario.moveTheGuy( 1, -1 ).x,
			  pMario.moveTheGuy( 1, -1 ).y,
			  new Posn (3, 1).x,
			  new Posn (3, 1).y);

	Posn pMarioKillinAGoomba = new Posn (3, 1);
	System.out.format("The answer is: (%f,%f) but should be (%f,%f)%n", 
			  pMario.moveTheGuy( 1, -1 ).x,
			  pMario.moveTheGuy( 1, -1 ).y,
			  pMarioKillinAGoomba.x,
			  pMarioKillinAGoomba.y);

	Posn pMarioAfterMovin = pMario.moveTheGuy( 1, -1 );
	System.out.format("The answer is: (%f,%f) but should be (%f,%f)%n", 
			  pMarioAfterMovin.x,
			  pMarioAfterMovin.y,
			  pMarioKillinAGoomba.x,
			  pMarioKillinAGoomba.y);

	System.out.format("The answer is: (%f,%f)%n", pMario.x, pMario.y);


	System.out.format("The answer is: %f but should be %f%n", 
			  new Posn ( 0, 3 ).distanceToOrigin ( ),
			  3.0);
	System.out.format("The answer is: %f but should be %f%n", 
			  new Posn ( 2, 0 ).distanceToOrigin ( ),
			  2.0);
	System.out.format("The answer is: %f but should be %f%n", 
			  new Posn ( 3, 4 ).distanceToOrigin (  ),
			  5.0);

	Person jay = new Person ( "Jay", "McCarthy", 100.00, "Bleu", 71.0, 140.0, true );
	Person tom = new Person ( "Tom", "Thumb", 75.0, "Brown", 3.0, 1.0, false );
	// Uncomment and see what these doooze!
	// System.out.format("The answer is: %f%n", jay.cutness);
	//System.out.format("The answer is: %f%n", jay.moveTheGuy(4.0, 5.0));
	System.out.format("The answer is: %f%n", jay.cuteness);
	System.out.format("The answer is: %f%n", tom.cuteness);

	System.out.format("The answer is: %b but should be %b%n",
			  //Person.willMarry(jay),
			  //jay.willMarry(jay),
			  jay.willMarry(),
			  true);
	System.out.format("The answer is: %b but should be %b%n",
			  tom.willMarry(), false);

	/*
	System.out.format("The answer is %d%n",
			  if ( 4 > 3 ) {
			      return 4;
			  } else { 
			      return 5;
			  });
	*/
	System.out.format("The answer is %d%n",
			  (4 > 3) ? 4 : 5);
	System.out.format("The answer is %d%n",
			  ((4 > 3) ? 4 : 5)+90);
	// sign(x) == 0

	System.out.format("The answer is: (%f,%f)%n", pMario.x, pMario.y);

	System.out.format("The answer is: (%f,%f) but should be (%f,%f)%n", 
			  pMarioAfterMovin.x,
			  pMarioAfterMovin.y,
			  pMarioKillinAGoomba.x,
			  pMarioKillinAGoomba.y);

	System.out.format("The answer is: (%f,%f)%n", pMario.x, pMario.y);


	double fixedCost = 180;
	TDN harry = new TDN(1, 2, 3);
	TDN hermione = new TDN(0, 0, 0);
	TDN snape = new TDN( 1, 0, 0);

	System.out.format("Harry is %d%d%d%n",
			  harry.first,
			  harry.second,
			  harry.third);

	TDN ex1after = new TDN(0, 0, 0).revealFirst( new TDN(1, 2, 3), 1);
	System.out.format("Ex1 after is %d%d%d but should be 100%n",
			  ex1after.first, ex1after.second, ex1after.third);
	TDN ex2after = new TDN(1, 0, 0).revealFirst( new TDN(1, 2, 3), 3);
	System.out.format("Ex2 after is %d%d%d but should be 100%n",
			  ex2after.first, ex2after.second, ex2after.third);


	TDN ex3after = new TDN(0, 0, 0).reveal( new TDN(1, 1, 1), 1);
	System.out.format("Ex3 after is %d%d%d but should be 111%n",
			  ex3after.first, ex3after.second, ex3after.third);

	TDN ex4afterafter = ((new TDN(0, 0, 0)).reveal( new TDN(1, 2, 1), 1)).reveal( new TDN(1, 2, 1), 2);
	/*
	TDN ex4afterafter = TDN.reveal( new TDN(1, 2, 1), 
					TDN.reveal( new TDN(1, 2, 1), new TDN(0, 0, 0), 1),
					2);
	*/
	System.out.format("Ex4 after is %d%d%d but should be 121%n",
			  ex4afterafter.first, ex4afterafter.second, ex4afterafter.third);

	Ball steve = new Ball ( new Posn ( 0, 0 ), 2.3 );
	System.out.format("Steve is at (%f,%f) and moving in the x-direction at %fu/t%n",
			  (steve.pos).x, (steve.pos).y, steve.xvelocity);
	System.out.format("Steve is at (%f,%f) and moving in the x-direction at %fu/t%n",
			  steve.pos.x, steve.pos.y, steve.xvelocity);
	Posn stevesposn = steve.pos;
	System.out.format("Steve is at (%f,%f) and moving in the x-direction at %fu/t%n",
			  stevesposn.x, stevesposn.y, steve.xvelocity);

	Ball steveAfterPhysics = steve.physics();
	System.out.format("Steve after physics is at (%f,%f) and moving in the x-direction at %fu/t%n",
			  (steveAfterPhysics.pos).x, (steveAfterPhysics.pos).y, steveAfterPhysics.xvelocity);
	System.out.format("But he should be at (2.3,0) and moving at 2.3u/t%n");

	System.out.format("The answer is %b, but should be %b%n",
			  new AddressBookEntry 
			  (new PhoneNumber ( 978, 555, 1723 ),
			   new PhoneNumber ( 978, 555, 9211 ),
			   new PhoneNumber ( 978, 565, 9211 ) ).allInAreaHuh ( 978 ),
			  true );
	System.out.format("The answer is %b, but should be %b%n",
			  new AddressBookEntry ( new PhoneNumber ( 978, 555, 1723 ),
								new PhoneNumber ( 978, 555, 9211 ),
								new PhoneNumber ( 978, 565, 9211 ) ).allInAreaHuh ( 801 ),
			  false );
	System.out.format("The answer is %b, but should be %b%n",
			  new AddressBookEntry ( new PhoneNumber ( 978, 555, 1723 ),
								new PhoneNumber ( 647, 555, 9211 ),
								new PhoneNumber ( 978, 565, 9211 ) ).allInAreaHuh ( 978 ),
			  false );


	System.out.format("%b%n", 1 == 1);
	Posn p5 = new Posn( 4, 5);
	System.out.format("%b%n", p5 == p5);
	int x = 1;
	System.out.format("%b%n", x == x);
	System.out.format("%b%n", 1 == x);
	System.out.format("%b%n", x == 1);

	System.out.format("%b%n", p5 == p5);
	System.out.format("%b%n", new Posn( 4, 5) == p5);
	System.out.format("%b%n", p5 == new Posn( 4, 5));
	System.out.format("%b%n", new Posn( 4, 5) == new Posn( 4, 5));
	// We could write:
	//System.out.format("%b%n", new Posn( 4, 5).equals(new Posn( 4, 5)));
	
	System.out.format("%b%n", "Jay" == "Jay");
	System.out.format("%b%n", "Jay".equals("Jay"));
    }
}