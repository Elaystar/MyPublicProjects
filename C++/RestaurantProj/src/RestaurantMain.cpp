//============================================================================
// Name        : Restaurant.cpp
// Author      : 
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include "Restaurant.h"

using namespace std;

int main() {
	cout << "!!!Hello Restaurant!!!" << endl;

	// Objekte von Klasse Restaurant werden erstellt
	Restaurant cafe, fastfood;

		//set
		cafe.name = "Coffee Gallery";
	    cafe.bewertung = 5;
	    fastfood.bewertung = 3;
	    fastfood.name = "McDonald's";

	    //get
	    cafe.info();
	    fastfood.info();


	return 0;
}
