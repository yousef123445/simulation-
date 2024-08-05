package sim;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.LinkedList;

class Customer {
	boolean isExpress;
	int interArrivalTime;
	int arrivalTime;
	int serviceTime;
	int serviceStartTime;
	int waitingTime;
	int completionTime;

	// generating customer information
	Customer(double typeRandom, double serviceRandom, double InterarrivalRandom, boolean r) {
		this.isExpress = isExpress(typeRandom, r);
		this.interArrivalTime = generateInterArrivalTime(InterarrivalRandom);
		if (isExpress) {
			this.serviceTime = generateExpressServiceTime(serviceRandom);
		} else {
			this.serviceTime = generateRegularServiceTime(serviceRandom);
		}
	}

	public int generateInterArrivalTime(double random) {
		if (random <= 0.16) {
			return 0;
		} else if (random <= 0.39) {
			return 1;
		} else if (random <= 0.69) {
			return 2;
		} else if (random < 0.90) {
			return 3;
		} else {
			return 4;
		}
	}

	public int generateRegularServiceTime(double random) {
		if (random <= 0.2) {
			return 3;
		} else if (random <= 0.7) {
			return 5;
		} else {
			return 7;
		}
	}

	public int generateExpressServiceTime(double random) {
		if (random <= 0.3) {
			return 1;
		} else if (random <= 0.7) {
			return 2;
		} else {
			return 3;
		}
	}

	public boolean isExpress(double random, boolean reverse) {
		if (random <= 0.4)
			return false ^ reverse;// XOR
		else {
			return true ^ reverse;// XOR
		}
	}
}

public class Problem1 {
	static int queuesize(LinkedList<Customer> queue, int arrival) {
		int count = 0;// check how many customers is in a queue in the given arrival time
		for (int i = 0; i < queue.size(); i++) {
			if (queue.get(i).completionTime > arrival) {
				count++;
			}
		}
		return count;
	}

	public static void main(String[] args) {
		LinkedList<Customer> allCustomers = new LinkedList<Customer>();
		LinkedList<Customer> regularQueue = new LinkedList<Customer>();
		LinkedList<Customer> expressQueue = new LinkedList<Customer>();
		LinkedList<Integer> regularQueueCompletion = new LinkedList<Integer>();
		LinkedList<Integer> expressQueueCompletion = new LinkedList<Integer>();
		LinkedList<Integer> idleRegularQueue = new LinkedList<Integer>();
		LinkedList<Integer> idleExpressQueue = new LinkedList<Integer>();
		ArrayList<Double> avrExpressServiceTime = new ArrayList<Double>();
		ArrayList<Double> avrRegularServiceTime = new ArrayList<Double>();
		ArrayList<Double> avrExpressWaitingTime = new ArrayList<Double>();
		ArrayList<Double> avrRegularWaitingTime = new ArrayList<Double>();
		ArrayList<Double> avrInterArrivalTime = new ArrayList<Double>();
		ArrayList<Integer> expressCount = new ArrayList<Integer>();
		ArrayList<Integer> regularCount = new ArrayList<Integer>();
		ArrayList<Integer> expressWaitCount = new ArrayList<Integer>();
		ArrayList<Integer> maxRegularQueue = new ArrayList<Integer>();
		ArrayList<Integer> maxExpressQueue = new ArrayList<Integer>();
		ArrayList<Integer> regularQueueIdleTime = new ArrayList<Integer>();
		ArrayList<Integer> expressQueueIdleTime = new ArrayList<Integer>();
		ArrayList<Integer> totalTime = new ArrayList<Integer>();
		regularQueueCompletion.add(0);
		expressQueueCompletion.add(0);
		Scanner scanner = new Scanner(System.in);
		System.out.println("enter number of simulations");
		int Simulations = scanner.nextInt();
		System.out.println("Enter number of customers: ");
		int customers = scanner.nextInt();
		System.out.println("do you want to reverse express and regular percentage(true or false): ");
		boolean reverse = scanner.nextBoolean();
		for (int j = 0; j < Simulations; j++) {
			avrInterArrivalTime.add(0d);
			avrRegularServiceTime.add(0d);
			avrRegularWaitingTime.add(0d);
			avrExpressServiceTime.add(0d);
			avrExpressWaitingTime.add(0d);
			expressWaitCount.add(0);
			regularCount.add(0);
			expressCount.add(0);
			maxRegularQueue.add(0);
			maxExpressQueue.add(0);
			regularQueueIdleTime.add(0);
			expressQueueIdleTime.add(0);
			for (int i = 0; i < customers; i++) {
				allCustomers.add(new Customer(Math.random(), Math.random(), Math.random(), reverse));
				regularQueueCompletion.add(0);
				expressQueueCompletion.add(0);
				idleExpressQueue.add(0);
				idleRegularQueue.add(0);
				if (i != 0) {//calculate arrival from interarrival
					allCustomers.get(i).arrivalTime = allCustomers.get(i - 1).arrivalTime
							+ allCustomers.get(i).interArrivalTime;
				} else {
					allCustomers.get(i).arrivalTime = allCustomers.get(i).interArrivalTime;
				}
				//calculating max queue size
				if (maxRegularQueue.get(j) < queuesize(regularQueue, allCustomers.get(i).arrivalTime)) {
					maxRegularQueue.set(j, queuesize(regularQueue, allCustomers.get(i).arrivalTime));
				}
				if (maxExpressQueue.get(j) < queuesize(expressQueue, allCustomers.get(i).arrivalTime)) {
					maxExpressQueue.set(j, queuesize(expressQueue, allCustomers.get(i).arrivalTime));
				}
				//calculating express customer information
				if (allCustomers.get(i).isExpress) {
					//checks if regular queue is 1.5 shorter than express queue
					if (queuesize(regularQueue,
							allCustomers.get(i).arrivalTime) < (int) (1.5
									* queuesize(expressQueue, allCustomers.get(i).arrivalTime))
							&& (regularQueueCompletion.get(i - 1) < expressQueueCompletion.get(i - 1))) {
						regularQueue.add(allCustomers.get(i));
						//if no one is waiting
						if (queuesize(regularQueue, allCustomers.get(i).arrivalTime) == 0) {
							allCustomers.get(i).waitingTime = 0;
							allCustomers.get(i).serviceStartTime = allCustomers.get(i).arrivalTime;
							allCustomers.get(i).completionTime = allCustomers.get(i).serviceStartTime
									+ allCustomers.get(i).serviceTime;
							regularQueueCompletion.set(i, allCustomers.get(i).completionTime);
							expressQueueCompletion.set(i, expressQueueCompletion.get(i - 1));
							idleRegularQueue.set(i,
									(allCustomers.get(i).serviceStartTime - regularQueueCompletion.get(i - 1)));
							regularQueueIdleTime.set(j, regularQueueIdleTime.get(j) + idleRegularQueue.get(i));
						
						} 
						//if someone is in the queue
						else {
							if (regularQueueCompletion.get(i - 1) - allCustomers.get(i).arrivalTime >= 0) {
								allCustomers.get(i).waitingTime = regularQueueCompletion.get(i - 1)
										- allCustomers.get(i).arrivalTime;
							}
							allCustomers.get(i).serviceStartTime = allCustomers.get(i).arrivalTime
									+ allCustomers.get(i).waitingTime;
							allCustomers.get(i).completionTime = allCustomers.get(i).serviceStartTime
									+ allCustomers.get(i).serviceTime;
							regularQueueCompletion.set(i, allCustomers.get(i).completionTime);
							expressQueueCompletion.set(i, expressQueueCompletion.get(i - 1));
							idleRegularQueue.set(i,
									(allCustomers.get(i).serviceStartTime - regularQueueCompletion.get(i - 1)));
							regularQueueIdleTime.set(j, regularQueueIdleTime.get(j) + idleRegularQueue.get(i));
						}
						//if no one waiting in express queue
					} else if (i == 0 || expressQueueCompletion.get(i - 1) < allCustomers.get(i).arrivalTime) {
						expressQueue.add(allCustomers.get(i));
						allCustomers.get(i).waitingTime = 0;
						allCustomers.get(i).serviceStartTime = allCustomers.get(i).arrivalTime
								+ allCustomers.get(i).waitingTime;
						allCustomers.get(i).completionTime = allCustomers.get(i).serviceStartTime
								+ allCustomers.get(i).serviceTime;
						expressQueueCompletion.set(i, allCustomers.get(i).completionTime);
						if (i == 0) {
							regularQueueCompletion.set(i, 0);
						} else {
							regularQueueCompletion.set(i, regularQueueCompletion.get(i - 1));
							idleExpressQueue.set(i,
									(allCustomers.get(i).serviceStartTime - expressQueueCompletion.get(i - 1)));
							expressQueueIdleTime.set(j, expressQueueIdleTime.get(j) + idleExpressQueue.get(i));
						}
						//someone is in the express queue
					} else {
						expressQueue.add(allCustomers.get(i));
						if (expressQueueCompletion.get(i - 1) - allCustomers.get(i).arrivalTime >= 0) {
							allCustomers.get(i).waitingTime = expressQueueCompletion.get(i - 1)
									- allCustomers.get(i).arrivalTime;
						}
						allCustomers.get(i).serviceStartTime = allCustomers.get(i).arrivalTime
								+ allCustomers.get(i).waitingTime;
						allCustomers.get(i).completionTime = allCustomers.get(i).serviceStartTime
								+ allCustomers.get(i).serviceTime;
						expressQueueCompletion.set(i, allCustomers.get(i).completionTime);
						regularQueueCompletion.set(i, regularQueueCompletion.get(i - 1));
						idleExpressQueue.set(i,
								(allCustomers.get(i).serviceStartTime - expressQueueCompletion.get(i - 1)));
						expressQueueIdleTime.set(j, expressQueueIdleTime.get(j) + idleExpressQueue.get(i));
					}
					//calculating regular customers information
				} else {
					regularQueue.add(allCustomers.get(i));
					//if someone in queue
					if (queuesize(regularQueue, allCustomers.get(i).arrivalTime) != 0) {
						if (regularQueueCompletion.get(i - 1) - allCustomers.get(i).arrivalTime >= 0) {
							allCustomers.get(i).waitingTime = regularQueueCompletion.get(i - 1)
									- allCustomers.get(i).arrivalTime;
						}
						allCustomers.get(i).serviceStartTime = allCustomers.get(i).arrivalTime
								+ allCustomers.get(i).waitingTime;
						allCustomers.get(i).completionTime = allCustomers.get(i).serviceStartTime
								+ allCustomers.get(i).serviceTime;
						regularQueueCompletion.set(i, allCustomers.get(i).completionTime);
						expressQueueCompletion.set(i, expressQueueCompletion.get(i - 1));
						idleRegularQueue.set(i,
								(allCustomers.get(i).serviceStartTime - regularQueueCompletion.get(i - 1)));
						regularQueueIdleTime.set(j, regularQueueIdleTime.get(j) + idleRegularQueue.get(i));
					}
					//if no one in queue
					else {
						allCustomers.get(i).waitingTime = 0;
						allCustomers.get(i).serviceStartTime = allCustomers.get(i).arrivalTime
								+ allCustomers.get(i).waitingTime;
						allCustomers.get(i).completionTime = allCustomers.get(i).serviceStartTime
								+ allCustomers.get(i).serviceTime;
						regularQueueCompletion.set(i, allCustomers.get(i).completionTime);
						if (i == 0) {
							expressQueueCompletion.set(i, 0);
						} else {
							expressQueueCompletion.set(i, expressQueueCompletion.get(i - 1));
							idleRegularQueue.set(i,
									(allCustomers.get(i).serviceStartTime - regularQueueCompletion.get(i - 1)));
							regularQueueIdleTime.set(j, regularQueueIdleTime.get(j) + idleRegularQueue.get(i));
						}
					}
				}
				if (allCustomers.get(i).isExpress) {
					avrExpressServiceTime.set(j, avrExpressServiceTime.get(j) + allCustomers.get(i).serviceTime);
					avrExpressWaitingTime.set(j, avrExpressWaitingTime.get(j) + allCustomers.get(i).waitingTime);
					if (allCustomers.get(i).waitingTime > 0) {
						expressWaitCount.set(j, expressWaitCount.get(j) + 1);
					}
					expressCount.set(j, expressCount.get(j) + 1);
				} else {
					avrRegularServiceTime.set(j, avrRegularServiceTime.get(j) + allCustomers.get(i).serviceTime);
					avrRegularWaitingTime.set(j, avrRegularWaitingTime.get(j) + allCustomers.get(i).waitingTime);
					regularCount.set(j, regularCount.get(j) + 1);
				}
				avrInterArrivalTime.set(j, avrInterArrivalTime.get(j) + allCustomers.get(i).interArrivalTime);
				if (i == customers - 1) {
					totalTime.add(allCustomers.get(i).completionTime);
				}
			}
			//clearing queues to use in next run
			allCustomers.clear();
			expressQueue.clear();
			regularQueue.clear();
			expressQueueCompletion.clear();
			regularQueueCompletion.clear();
			idleExpressQueue.clear();
			idleRegularQueue.clear();
		}
		//calculating all needed averages and simulation information
		double avrExpressServiceTimeFinal = 0;
		double avrRegularServiceTimeFinal = 0;
		double avrExpressWaitingTimeFinal = 0;
		double avrRegularWaitingTimeFinal = 0;
		double avrInterArrivalTimeFinal = 0;
		double avrExpressWaitingCount = 0;
		double avrMaxExpressCashierCountFinal = 0;
		double avrMaxRegularCashierCountFinal = 0;
		double avrRegularCashierIdleTimeFinal = 0;
		double avrExpressCashierIdleTimeFinal = 0;
		double avrExpressCount = 0;
		double avrRegularCount = 0;
		for (int i = 0; i < Simulations; i++) {
			avrInterArrivalTimeFinal += avrInterArrivalTime.get(i) / customers;
			avrExpressServiceTimeFinal += avrExpressServiceTime.get(i) / expressCount.get(i);
			avrRegularServiceTimeFinal += avrRegularServiceTime.get(i) / regularCount.get(i);
			avrExpressWaitingTimeFinal += avrExpressWaitingTime.get(i) / expressCount.get(i);
			avrRegularWaitingTimeFinal += avrRegularWaitingTime.get(i) / regularCount.get(i);
			avrExpressWaitingCount += (double) (expressWaitCount.get(i)) / expressCount.get(i);
			avrMaxRegularCashierCountFinal += maxRegularQueue.get(i);
			avrMaxExpressCashierCountFinal += maxExpressQueue.get(i);
			avrRegularCashierIdleTimeFinal += (double) (regularQueueIdleTime.get(i)) / totalTime.get(i);
			avrExpressCashierIdleTimeFinal += (double) (expressQueueIdleTime.get(i)) / totalTime.get(i);
			avrExpressCount += expressCount.get(i);
			avrRegularCount += regularCount.get(i);
		}
		System.out.println("average of inter arrival time= " + (avrInterArrivalTimeFinal / Simulations));
		System.out.println("average service time for regular customers= " + avrRegularServiceTimeFinal / Simulations);
		System.out
				.println("average service time for express customers= " + ((avrExpressServiceTimeFinal / Simulations)));
		System.out.println("average waiting time for regular cashier= " + avrRegularWaitingTimeFinal / Simulations);
		System.out.println("average waiting time for express cashier= " + avrExpressWaitingTimeFinal / Simulations);
		System.out.println(
				"probabilty of waiting in express cashier= " + avrExpressWaitingCount * 100 / Simulations + "%");
		System.out.println("maximum number in express queue= " + avrMaxExpressCashierCountFinal / Simulations);
		System.out.println("maximum number in regular queue= " + avrMaxRegularCashierCountFinal / Simulations);
		System.out.println("idle time for regular queue= " + avrRegularCashierIdleTimeFinal * 100 / Simulations + "%");
		System.out.println("idle time for express queue= " + avrExpressCashierIdleTimeFinal * 100 / Simulations + "%");
		System.out.println("number of express customers: " + avrExpressCount / Simulations);
		System.out.println("number of regular customers: " + avrRegularCount / Simulations);

	}
}