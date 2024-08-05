package sim;

import java.util.ArrayList;
import java.util.Scanner;

class room {
	int maxCapacity;
	int currunt;

	public room(int capacity, int currunt) {
		this.maxCapacity = capacity;
		this.currunt = currunt;
	}
	//filling the room with cars and returns the number of remaining cars in the order
	int fill(int numberofCars) {
		if (currunt + numberofCars > maxCapacity) {
			numberofCars -= maxCapacity - currunt;
			currunt = maxCapacity;
			return numberofCars;
		} else {
			currunt += numberofCars;
			return 0;
		}

	}

}

public class Problem2 {
	static int generateDemand(double random) {
		if (random <= 0.2) {
			return 0;
		} else if (random <= 0.54) {
			return 1;
		} else if (random <= 0.9) {
			return 2;
		} else {
			return 3;
		}
	}

	static int generateLeadTime(double random) {
		if (random <= 0.4) {
			return 1;
		} else if (random <= 0.75) {
			return 2;
		} else {
			return 3;
		}
	}
	//return the index of arrival of order
	static int order(int curruntDay) {
		return curruntDay + generateLeadTime(Math.random());
	}

	public static void main(String[] args) {
		room inventory = new room(10, 3);
		room showroom = new room(5, 4);
		int carProfit = 10000;
		int storeCost = 1000;
		int orderCost = 20000;
		ArrayList<Integer> inventoryEndUnitsCount = new ArrayList<Integer>();
		ArrayList<Integer>showroomEndUnitsCount = new ArrayList<Integer>();
		ArrayList<Integer>shortageCount = new ArrayList<Integer>();
		ArrayList<Double> averageDemand = new ArrayList<Double>();
		ArrayList<Double> averageLeadTime = new ArrayList<Double>();
		ArrayList<Integer>orderCount = new ArrayList<Integer>();
		ArrayList<Integer> Profit = new ArrayList<Integer>();
		ArrayList<Integer>averageProfit = new ArrayList<Integer>();
		Scanner scanner = new Scanner(System.in);
		System.out.println("enter number of simulations");
		int Simulations = scanner.nextInt();
		System.out.println("enter number of days to simulate: ");
		int days = scanner.nextInt();
		int recieveIndex = 2;//order to arrive in 2 days
		int carsOrdered = 5;//to recive 5 cars
		int leadtime = 0;
		System.out.println("enter the review period: ");
		int reviewPeriod = scanner.nextInt();
		for (int j = 0; j < Simulations; j++) {
			averageProfit.add(0);
			inventoryEndUnitsCount.add(0);
			showroomEndUnitsCount.add(0);
			shortageCount.add(0);
			orderCount.add(0);
			averageDemand.add(0d);
			averageLeadTime.add(0d);
			for (int i = 0; i < days; i++) {
				if (i == recieveIndex) {//if day to recieve order is today
					int remaining = showroom.fill(carsOrdered);//fill showroom 
					inventory.fill(remaining);//fill inventory with remaining cars 
				}
				int demand = generateDemand(Math.random());
				averageDemand.set(j,averageDemand.get(j)+demand);
				if (inventory.currunt >= demand) {//if we have enough cars in inventory
					inventory.currunt -= demand;
					Profit.add(demand * carProfit);
				} else if (inventory.currunt + showroom.currunt >= demand) {//if we have enough in inventory and showroom
					showroom.currunt -= demand - inventory.currunt;
					inventory.currunt = 0;
					Profit.add(demand * carProfit);
				} else {
					//if we dont have enough cars
					Profit.add(carProfit * (inventory.currunt + showroom.currunt));
					showroom.currunt = 0;
					inventory.currunt = 0;
					shortageCount.set(j, shortageCount.get(j)+1);//lost oppurtunity
				}
				Profit.set(i, Profit.get(i) - storeCost * (inventory.currunt + showroom.currunt));//add storage cost
				//if review day is today
				if (i % reviewPeriod == 0 && i != 0) {
					if (inventory.currunt <= 1)//check if we need to order cars
					{//this number can be changed
						recieveIndex = order(i);
						leadtime = recieveIndex - i;
						averageLeadTime.set(j,averageLeadTime.get(j)+leadtime);
						orderCount.set(j, orderCount.get(j)+1);
						carsOrdered = 15;
						Profit.set(i, Profit.get(i) - orderCost);
					}
				} else {
					leadtime = 0;
				}
				//end units at the end of the day
				inventoryEndUnitsCount.set(j, inventoryEndUnitsCount.get(j)+inventory.currunt);
				showroomEndUnitsCount.set(j,showroomEndUnitsCount.get(j)+showroom.currunt);
			}
			for (int i = 0; i < Profit.size(); i++) {
				averageProfit.set(j, averageProfit.get(j)+Profit.get(i));
			}
			Profit.clear();//clearing profit array to use next run
		}
		//calculting simulation average and other information
		double averageProfitfinal=0;
		double averageDemandFinal=0;
		double averageLeadTimeFinal=0;
		double averageShortageCondition=0;
		double inventoryEndUnitAverage=0;
		double showroomEndUnitAverage=0;
		for (int i = 0; i < Simulations; i++) {
			averageProfitfinal+= averageProfit.get(i)/days;
			averageDemandFinal+= averageDemand.get(i)/days;
			averageLeadTimeFinal+=averageLeadTime.get(i)/orderCount.get(i);
			averageShortageCondition+=shortageCount.get(i);
			inventoryEndUnitAverage+=inventoryEndUnitsCount.get(i);
			showroomEndUnitAverage+=showroomEndUnitsCount.get(i);
		}
		System.out.println("average net profit= " + (averageProfitfinal / Simulations));
		System.out.println("average demand= "+ averageDemandFinal/Simulations);
		System.out.println("average lead time= "+ averageLeadTimeFinal/Simulations);
		System.out.println("average shortage condition count="+averageShortageCondition/Simulations);
		System.out.println("average end units in inventory per day= "+inventoryEndUnitAverage/(Simulations*days));
		System.out.println("average end units in showroom per day= "+showroomEndUnitAverage/(Simulations*days));		
		
		
	}
}