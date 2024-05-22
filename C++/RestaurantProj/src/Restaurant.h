/*
 * Restaurant.h
 *
 *  Created on: 22.05.2024
 *      Author: elaystar
 */

#ifndef RESTAURANT_H_
#define RESTAURANT_H_

#include <iostream>
#include <string>
using namespace std;


class Restaurant {
public:

	//Konstruktor Destruktor
	Restaurant();
	~Restaurant();

	//Membervariablen
	string name;
	int bewertung;


	//Methoden
	void info();
private:
	string sterneVergeben(int);

};

#endif /* RESTAURANT_H_ */
