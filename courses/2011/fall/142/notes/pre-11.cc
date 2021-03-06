#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>

int min ( int a, int b ) {
  if ( a > b ) {
    return b;
  } else {
    return a;
  }
}

// A ListOfNumbers is either
//  - EmptyListOfNumbers
//  - OneMoreNumber
class ListOfNumbers {
public:
  // wages : ListOfNumbers -> ListOfNumbers
  virtual ListOfNumbers* wages ( ) = 0;
  // eliminateOverworkers : ListOfNumbers -> ListOfNumbers
  virtual ListOfNumbers* eliminateOverworkers () = 0;
  // capOverworkers : ListOfNumbers double -> ListOfNumbers
  virtual ListOfNumbers* capOverworkers ( double cap ) = 0;
  // update : ListOfNumbers int double -> ListOfNumbers
  virtual ListOfNumbers* update ( int n, double newOne ) = 0;
  // print : ListOfNumbers -> int
  virtual int print () = 0;
};

// A EmptyListOfNumbers is a..
//  new EmptyListOfNumbers ( )
// where
class EmptyListOfNumbers : public ListOfNumbers {
public:
    EmptyListOfNumbers ( ) {
    }

  // wages : EmptyListOfNumbers -> ListOfNumbers
  ListOfNumbers* wages ( ) {
	// ... ...
	return new EmptyListOfNumbers() ;
  }

  // toString : EmptyListOfNumbers -> String
  int print () {
    return printf("!");
  }

  // eliminateOverworkers : EmptyListOfNumbers -> ListOfNumbers
  ListOfNumbers* eliminateOverworkers () {
	// ... ...
	return new EmptyListOfNumbers() ;
  }

  // capOverworkers : EmptyListOfNumbers double -> ListOfNumbers
  ListOfNumbers* capOverworkers ( double cap ) {
	return new EmptyListOfNumbers() ;
  }

  // update : EmptyListOfNumbers int double -> ListOfNumbers
  ListOfNumbers* update ( int n, double newOne ) {
	return new EmptyListOfNumbers() ;
  }
};

// A OneMoreNumber is a...
//  new OneMoreNumber ( first, rest )
// where
//  first is a double
//  rest is a ListOfNumbers
class OneMoreNumber : public ListOfNumbers {
public:
  double first;
  ListOfNumbers* rest;

  OneMoreNumber ( double first0, ListOfNumbers* rest0 ) {
	first = first0;
	rest = rest0;
  }

  // wages : OneMoreNumber -> ListOfNumbers
  ListOfNumbers* wages ( ) {
	// this->first ... this->rest
	// this->first ... (this->rest)->wages()

	/*
	  Input: new OneMoreNumber( 10.0, new EmptyListOfNumbers() )
	  Output: new OneMoreNumber( 10.0*12.0, new EmptyListOfNumbers() )

	  Input: new OneMoreNumber( this->first, new EmptyListOfNumbers() )
	  Output: new OneMoreNumber( this->first*12.0, new EmptyListOfNumbers() )

	  Input: new OneMoreNumber( this->first, this->rest )
	  Output: new OneMoreNumber( this->first*12.0, new EmptyListOfNumbers() )

	  Input: new OneMoreNumber( this->first, this->rest )
	  Output: new OneMoreNumber( this->first*12.0, this->rest )
	*/
	// return new OneMoreNumber( this->first*12.0, this->rest ) ;

	/* 
	   Input: new OneMoreNumber( 15.0, new OneMoreNumber( 10.0, new EmptyListOfNumbers() ) )
	   Output: new OneMoreNumber( 15.0*12.0, new OneMoreNumber( 10.0*12.0, new EmptyListOfNumbers() ) )

	   Input: new OneMoreNumber( this->first, this->rest )
	   Output: new OneMoreNumber( this->first*12.0, this->rest->wages() )
	*/
	return new OneMoreNumber( this->first*12.0, this->rest->wages() );
  }

  // toString : OneMoreNumber -> String
  int print () {
	// this->first ... this->rest
    printf("%f:", this->first);
    return this->rest->print();
	//return String.format("%f:%d", this->first, this->rest->print() );
  }

  // eliminateOverworkers : OneMoreNumber -> ListOfNumbers
  ListOfNumbers* eliminateOverworkers () {
	// this->first ... this->rest
	// this->first ... (this->rest)->eliminateOverworkers()

	if ( this->first > 100.0 ) {
      /*
        Input: new OneMoreNumber( 400.0, new EmptyListOfNumbers() )
        Output: new EmptyListOfNumbers()

        Input: new OneMoreNumber( this->first, this->rest )
        Output: this->rest
      */
      //return this->rest ;

      /*
        Input: new OneMoreNumber( 800.0, new OneMoreNumber( 40.0, new OneMoreNumber( 400.0, new EmptyListOfNumbers() ) ))
        Output: new OneMoreNumber( 40.0, new EmptyListOfNumbers() )

        Input: new OneMoreNumber( this->first, this->rest )
        Output: this->rest->eliminateOverworkers()
      */
      return this->rest->eliminateOverworkers(); 
	} else {
      /*
        Input: new OneMoreNumber( 40.0, new EmptyListOfNumbers() )
        Output: new OneMoreNumber( 40.0, new EmptyListOfNumbers() )
	  
        Input: new OneMoreNumber( this->first, this->rest )
        Output: new OneMoreNumber( this->first, this->rest )
      */
      // return new OneMoreNumber( this->first, this->rest );

      /*
        Input: new OneMoreNumber( 40.0, new OneMoreNumber( 400.0, new EmptyListOfNumbers() ) ),
        Output: new OneMoreNumber( 40.0, new EmptyListOfNumbers() )
	      
        Input: new OneMoreNumber( this->first, this->rest ),
        Output: new OneMoreNumber( this->first, this->rest->eliminateOverworkers() )
      */
      return new OneMoreNumber( this->first, this->rest->eliminateOverworkers() );
	}
  }

  // capOverworkers : OneMoreNumber double -> ListOfNumbers
  ListOfNumbers* capOverworkers ( double cap ) {
	// this->first ... this->rest->capOverworkers()

	return new OneMoreNumber( min( cap, this->first ), this->rest->capOverworkers(cap) ) ;
  }

  ListOfNumbers* capOverworkersWayNumeroUno ( double cap ) {
	if ( this->first <= cap ) {
      /*
        Input: new OneMoreNumber( 40.0, mt)
        Output: new OneMoreNumber( 40.0, mt)

        Input: new OneMoreNumber( this->first, this->rest)
        Output: new OneMoreNumber( this->first, this->rest->capOverworkers() )
      */
      return new OneMoreNumber( this->first, this->rest->capOverworkers(cap) ) ;

      //	  ----
	} else {

      /*
        Input: new OneMoreNumber( 400.0, mt)
        Output: new OneMoreNumber( 100.0, mt) 

        Input: new OneMoreNumber( this->first, this->rest)
        Output: new OneMoreNumber( 100.0, this->rest->capOverworkers()) 
      */
      return new OneMoreNumber( cap, this->rest->capOverworkers(cap)) ;
	}
  }

  // update : OneNumber int double -> ListOfNumbers
  ListOfNumbers* update ( int n, double newOne ) {
	// this->first ... this->rest ... n ... newOne
	// this->first ... this->rest->update( ... , ... ) ... n ... newOne

	if ( n == 0 ) {
      /*
        Input: new OneMoreNumber( 400.0, andLightning )->update(0, 756.0)
        Output: new OneMoreNumber( 756.0, andLightning )

        Input: new OneMoreNumber( this->first, this->rest )->update(n, newOne)
        Output: new OneMoreNumber( newOne, this->rest )
      */
      return new OneMoreNumber( newOne, this->rest ) ;
	} else {
      // ---

      /*
        Input: new OneMoreNumber( 72.0, new OneMoreNumber( 400.0, andLightning ))->update(1, 756.0)
        Output: new OneMoreNumber( 72.0, new OneMoreNumber( 756.0, andLightning ) ) 


        Input: new OneMoreNumber( this->first, this->rest)->update(n, newOne)
        Output: new OneMoreNumber( this->first, this->rest->update(n-1,newOne) ) 
      */
      return new OneMoreNumber( this->first, this->rest->update(n-1,newOne) ) ;
	}
  }

};

// wage : double -> double
// Purpose: to compute the salary of a worker who worked some-many hours at $12/h
double wage ( double hours ) {
  return 12 * hours;
}

// devert : int int -> ListOfNumbers
// Examples:
//  devert(9) = new OneMoreNumber ( 9, mt )
//  devert(95) = new OneMoreNumber( 5, new OneMoreNumber ( 9, mt ) )
//  devert(95) = new OneMoreNumber( 5, devert(9) )
//  devert(951) = new OneMoreNumber( 1, new OneMoreNumber( 5, new OneMoreNumber ( 9, mt ) ) )
//  devert(951) = new OneMoreNumber( 1, devert(95) )
ListOfNumbers* devert ( int theNumber, int base ) {
  if ( theNumber < base ) {
    return new OneMoreNumber ( theNumber, new EmptyListOfNumbers () ) ;
  } else {
    // x * b + y = z
    // z / b = x
    // z % b = y
    return new OneMoreNumber( theNumber % base, devert( theNumber / base, base ) );
  }
}

int main ( ) {
  printf("The answer is %f, but should be %f\n",
         wage(10.0),
         120.0);

  ListOfNumbers* mt = new EmptyListOfNumbers();
  ListOfNumbers* andKefka = new OneMoreNumber( 10.0, mt );
  ListOfNumbers* andBlaz = new OneMoreNumber( 15.0, andKefka );
  ListOfNumbers* andLightning = new OneMoreNumber( 40.0, andBlaz );

  printf("The answer is %d, but should be %s\n",
         andKefka->print(),
         "10.0000:!" );
  printf("The answer is %d, but should be %s\n",
         (new OneMoreNumber( 10.0, mt ))->print(),
         "10.0000:!" );
  printf("The answer is %d, but should be %s\n",
         (new OneMoreNumber( 10.0, new EmptyListOfNumbers() ))->print(),
         "10.0000:!" );
  printf("The answer is %d, but should be %s\n",
         printf("%f:%d", 10.0, (new EmptyListOfNumbers())->print() ),
         "10.0000:!" );
  printf("The answer is %d, but should be %s\n",
         printf("10.000000:%d", (new EmptyListOfNumbers())->print() ),
         "10.0000:!" );
  printf("The answer is %d, but should be %s\n",
         printf("10.000000:%d", (new EmptyListOfNumbers())->print() ),
         "10.0000:!" );
  printf("The answer is %d, but should be %s\n",
         printf("10.000000:%s", "!" ),
         "10.0000:!" );
  printf("The answer is %d, but should be %s\n",
         printf("10.000000:!"),
         "10.0000:!" );
  printf("The answer is %s, but should be %s\n",
         "10.000000:!",
         "10.0000:!" );

  printf("The answer is %d, but should be %d\n",
         mt->wages()->print(),
         (new EmptyListOfNumbers())->print() );

  printf("The answer is %d, but should be %d\n",
         mt->wages()->print(),
         (new EmptyListOfNumbers())->print() );


  printf("The answer is %d, but should be %d\n",
         andKefka->wages()->print(),
         (new OneMoreNumber( 10.0*12.0,
                             new EmptyListOfNumbers() ))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 10.0, new EmptyListOfNumbers() ))->wages()->print(),
         (new OneMoreNumber( 10.0*12.0,
                             new EmptyListOfNumbers() ))->print() );

  printf("The answer is %d, but should be %d\n",
         andBlaz->wages()->print(),
         (new OneMoreNumber( 15.0*12.0,
                            new OneMoreNumber( 10.0*12.0,
                                               new EmptyListOfNumbers() ) ))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 15.0, new OneMoreNumber( 10.0, new EmptyListOfNumbers() ) ))->wages()->print(),
         (new OneMoreNumber( 15.0*12.0,
                            new OneMoreNumber( 10.0*12.0,
                                               new EmptyListOfNumbers() ) ))->print() );

  printf("The answer is %d, but should be %d\n",
         andLightning->wages()->print(),
         (new OneMoreNumber( 40.0*12.0,
                            new OneMoreNumber( 15.0*12.0,
                                               new OneMoreNumber( 10.0*12.0,
                                                                  new EmptyListOfNumbers() ) ) ))->print() );
       
  ListOfNumbers* andSquall = new OneMoreNumber( 400.0, andLightning );
  ListOfNumbers* andVivi = new OneMoreNumber( 60.0, andSquall );

  printf("The answer is %d, but should be %d\n",
         mt->eliminateOverworkers()->print(),
         (new EmptyListOfNumbers())->print() );

  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 400.0, new EmptyListOfNumbers() ))->eliminateOverworkers()->print(),
         (new EmptyListOfNumbers())->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 40.0, new EmptyListOfNumbers() ))->eliminateOverworkers()->print(),
         (new OneMoreNumber( 40.0, new EmptyListOfNumbers() ))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 40.0, new OneMoreNumber( 400.0, new EmptyListOfNumbers() ) ))->eliminateOverworkers()->print(),
         (new OneMoreNumber( 40.0, new EmptyListOfNumbers() ))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 800.0, new OneMoreNumber( 40.0, new OneMoreNumber( 400.0, new EmptyListOfNumbers() ) )))->eliminateOverworkers()->print(),
         (new OneMoreNumber( 40.0, new EmptyListOfNumbers() ))->print() );


  printf("The answer is %d, but should be %d\n",
         andSquall->eliminateOverworkers()->print(),
         andLightning->print() );
  printf("The answer is %d, but should be %d\n",
         andVivi->eliminateOverworkers()->print(),
         (new OneMoreNumber( 60.0, andLightning ))->print());

  printf("capOverworkers beloooooooow!\n");

  printf("The answer is %d, but should be %d\n",
         mt->capOverworkers(100)->print(),
         mt->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 40.0, mt))->capOverworkers(100)->print(),
         (new OneMoreNumber( 40.0, mt))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 400.0, mt))->capOverworkers(100)->print(),
         (new OneMoreNumber( 100.0, mt))->print() );

  printf("The answer is %d, but should be %d\n",
         andSquall->capOverworkers(100)->print(),
         (new OneMoreNumber( 100.0, andLightning ))->print() );
  printf("The answer is %d, but should be %d\n",
         ((new OneMoreNumber( 400.0, andLightning ))->capOverworkers(100))->print(),
          (new OneMoreNumber( 100.0, andLightning ))->print() );
  printf("The answer is %d, but should be %d\n",
         ((new OneMoreNumber( 400.0, andLightning ))->capOverworkers(300))->print(),
         (new OneMoreNumber( 300.0, andLightning ))->print() );
  printf("The answer is %d, but should be %d\n",
         ((new OneMoreNumber( 400.0, andLightning ))->capOverworkers(500))->print(),
         (new OneMoreNumber( 400.0, andLightning ))->print() );

  printf("coolFunction beloooooooow!\n");

  printf("The answer is %d, but should be %d\n",
         mt->update(923, 756.0)->print(),
         mt->print() );
  printf("The answer is %d, but should be %d\n",
         mt->update(51, 756.0)->print(),
         mt->print() );
  printf("The answer is %d, but should be %d\n",
         mt->update(0, 756.0)->print(),
         mt->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 400.0, andLightning ))->update(0, 756.0)->print(),
         (new OneMoreNumber( 756.0, andLightning ))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 72.0, new OneMoreNumber( 400.0, andLightning )))->update(1, 756.0)->print(),
         (new OneMoreNumber( 72.0, new OneMoreNumber( 756.0, andLightning ) ))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 13.0, new OneMoreNumber( 72.0, new OneMoreNumber( 400.0, andLightning ))))->update(2, 756.0)->print(),
         (new OneMoreNumber( 13.0, new OneMoreNumber( 72.0, new OneMoreNumber( 756.0, andLightning ) ) ))->print() );


  printf("The answer is %d\n",
         andSquall->print() );
  printf("The answer is %d, but should be %d\n",
         andSquall->update(0, 756.0)->print(),
         (new OneMoreNumber( 756.0, andLightning ))->print() );

  printf("The answer is %d, but should be %d\n",
         ((new OneMoreNumber( 400.0, andLightning))->update(0, 756.0))->print(),
         (new OneMoreNumber( 756.0, andLightning ))->print() );
  printf("The answer is %d, but should be %d\n",
         (new OneMoreNumber( 756.0, andLightning))->print(),
         (new OneMoreNumber( 756.0, andLightning ))->print() );

  printf("The answer is %d\n",
         andSquall->print() );
  printf("The answer is %d\n",
         (new OneMoreNumber( 400.0, andLightning))->print() );

  // Bad picture:
  // 400 -> 40 -> 15 -> 10 -> !
  // 756 -> 40 -> 15 -> 10 -> !

  // Correct picture:
  // 400 \
  //      +-- 40 -> 15 -> 10 -> !
  // 756 /

  printf("devert\n");

  printf("The answer is %d, but should be %d\n",
         devert(456, 10)->print(),
         (new OneMoreNumber( 6, new OneMoreNumber( 5, new OneMoreNumber ( 4, mt) ) ))->print());		
  printf("The answer is %d, but should be %d\n",
         devert(951, 10)->print(),
         (new OneMoreNumber( 1, new OneMoreNumber( 5, new OneMoreNumber ( 9, mt ) ) ))->print() ); 
  printf("The answer is %d\n",
         devert(951, 2)->print() ); 

}
