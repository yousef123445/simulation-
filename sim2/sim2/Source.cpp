#include <iostream>
#include <random>
#include <time.h>
using namespace std;

int get_x(float r) {

    if (r < 0.2) {
        return 0;
    }
    else if (r < 0.6) {
        return 1;
    }
    else if (r < 0.8) {
        return 2;
    }
    else if (r < 0.9) {
        return 3;
    }
    else {
        return 4;
    }
}

int main() {
    srand(time(NULL));
    int n = 0;
    int ordperweek;
    cout << "enter number of runs: ";
    cin >> n;
    cout << "enter number of orders per week: ";
    cin >> ordperweek;
    double* profitlist=new double[n];
    int* x = new int[n];
    double avrprofit = 0;

    for (int i = 0; i < n; i++) {
        float rd = (float)rand()/RAND_MAX;
        x[i]=get_x(rd);
    }
    int available=0;
    for (int i = 0; i < n; i++)
    {
        available += ordperweek;
        profitlist[i] = 0;
        if (available > x[i])
        {
            profitlist[i] += 450 * x[i];
            profitlist[i] -= 50 * (available - x[i]);
            available -= x[i];
        }
        else if (available < x[i])
        {
            profitlist[i] += 450 * available;
            profitlist[i] -= 100 * (x[i] - available);
            available = 0;
        }
        else
        {
            profitlist[i] += 450 * x[i];
            available = 0;
        }
        avrprofit += profitlist[i];
    }
    avrprofit /= n;
    cout << "average profit after simulation is " << avrprofit;
    return 0;
}