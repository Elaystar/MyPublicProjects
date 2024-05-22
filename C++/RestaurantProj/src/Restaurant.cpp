/*
 * Restaurant.cpp
 *
 *  Created on: 22.05.2024
 *      Author: elaystar
 */

#include "Restaurant.h"



string Restaurant::sterneVergeben(int pkte){
	string sterne;
		if (pkte == 0){
			sterne = "mit 0 Sternen bewertet";
		}
		else if (pkte == 1){
			sterne = "*";
		}
		else if (pkte == 2){
			sterne = "**";
				}
		else if (pkte == 3){
			sterne = "***";
						}
		else if (pkte == 4){
			sterne = "****";
						}
		else if (pkte == 5){
			sterne = "*****";
						}

		return sterne;
	}

void Restaurant::info() {
		cout<<name + " (" + sterneVergeben(bewertung) + ")"<<endl;
		}

Restaurant::Restaurant() {
	// TODO Auto-generated constructor stub

}

Restaurant::~Restaurant() {
	// TODO Auto-generated destructor stub
}
