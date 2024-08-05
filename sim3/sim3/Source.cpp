#include <iostream>
#include <cstdlib>
#include <time.h>
#include <random>
#include <cmath>
#include <iomanip>
using namespace std;
class Generator {
    default_random_engine generator;
    normal_distribution<double> distribution;
    double min;
    double max;
public:
    Generator(double mean, double stddev, double min, double max) :
        distribution(mean, stddev), min(min), max(max)
    {}

    double operator ()() {
        while (true) {
            double number = this->distribution(generator);
            if (number >= this->min && number <= this->max)
                return number;
        }
    }
};
float gen_IAT(float rd)
{
    return rd * (5.0);
}
int main()
{
    srand(time(NULL));
    int customers = 0;
    cout << "enter number of customers: ";
    cin >> customers;
    int waitcount = 0;
    float maxwait=0;
    float* IATarr = new float[customers];
    float* ATarr = new float[customers];
    float* WTarr = new float[customers];
    float* SSTarr = new float[customers];
    float* STarr = new float[customers];
    float* CTarr = new float[customers];
    float* TISarr = new float[customers];
    float* ATMCarr1 = new float[customers+1];
    float* ATMCarr2 = new float[customers+1];
    ATMCarr1[0] = ATMCarr2[0] = 0.0;
    float ATMcount1 = 0, ATMcount2 = 0;
    float AVGwait = 0.0;
    float AVGtime = 0.0;
    for (int i = 0;i < customers;i++)
    {
        IATarr[i] = ATarr[i] = WTarr[i] = SSTarr[i] = STarr[i] = CTarr[i]=TISarr[i] = ATMCarr1[i + 1] = ATMCarr2[i + 1] = 0;
    }
    Generator gST(2.0, 0.5, 0.25, 5.5);
    for (int i = 0; i < customers; i++)
    {
        float r = float(rand()) / RAND_MAX;
        r = (float)round(r * 100.0) / 100.0;
        IATarr[i] = gen_IAT(r);
        ATarr[i] = (i == 0) ? IATarr[i] : (ATarr[i - 1] + IATarr[i]);
        STarr[i] = gST();
        STarr[i] = (float)round(STarr[i] * 100.0) / 100.0;
        if (ATMCarr1[i] < ATarr[i])
        {
            SSTarr[i] = ATarr[i];
            WTarr[i] = 0;
            ATMCarr1[i + 1] = ATarr[i] + STarr[i];
            ATMCarr2[i + 1] = ATMCarr2[i];
            CTarr[i] = SSTarr[i] + STarr[i];
            ATMcount1 += STarr[i];
        }
        else if (ATMCarr2[i] < ATarr[i])
        {
            SSTarr[i] = ATarr[i];
            WTarr[i] = 0;
            ATMCarr2[i + 1] = ATarr[i] + STarr[i];
            ATMCarr1[i + 1] = ATMCarr1[i];
            CTarr[i] = SSTarr[i] + STarr[i];
            ATMcount2 += STarr[i];
        }
        else if (ATMCarr1[i] <= ATMCarr2[i])
        {
            WTarr[i] = ATMCarr1[i] - ATarr[i];
            SSTarr[i] = ATarr[i]+WTarr[i];
            CTarr[i] = SSTarr[i] + STarr[i];
            ATMCarr1[i + 1]=CTarr[i];
            ATMCarr2[i + 1] = ATMCarr2[i];
            ATMcount1 += STarr[i];
        }
        else
        {
            WTarr[i] = ATMCarr2[i] - ATarr[i];
            SSTarr[i] = ATarr[i] + WTarr[i];
            ATMCarr2[i + 1]= CTarr[i];
            CTarr[i] = SSTarr[i] + STarr[i];
            ATMCarr1[i + 1] = ATMCarr1[i];
            ATMcount1 += STarr[i];
        }
        TISarr[i] = CTarr[i] - ATarr[i];
        AVGwait += WTarr[i];
        AVGtime += TISarr[i];
        if (WTarr[i] > 0)
        {
            waitcount++;
        }
        if (WTarr[i] > maxwait)
        {
            maxwait = WTarr[i];
        }
    }
    cout << "-------------------------------------------------------------------------------\n";
    cout << "num\tIAT\tAT\tWT\tSST\tST\tCT\tTIS\tATM1\tATM2\n";
    cout << "-------------------------------------------------------------------------------\n";
    int customers2=0;
    if (customers > 15)
    {
        customers2 = 15;
    }
    else
    {
        customers2 = customers;
    }
    for (int i = 0;i < customers2;i++)
    {
        cout <<fixed<< setprecision(2);
        cout << i + 1 << "\t" << IATarr[i] << "\t" << ATarr[i] << "\t" << WTarr[i] << "\t" << SSTarr[i] << "\t" << STarr[i] << "\t" << CTarr[i] << "\t" << TISarr[i] << "\t" << ATMCarr1[i + 1] << "\t" << ATMCarr2[i + 1] << endl;
        cout << "-------------------------------------------------------------------------------\n";
    }
    cout <<fixed<< setprecision(3)<<"average waiting time: " << AVGwait / customers<<endl;
    cout << "number of waiting customers: " << waitcount<<endl;
    cout << "simulation time: " << CTarr[customers - 1] << endl;
    cout << fixed << setprecision(1)<< "probability of waiting: " << ((float)waitcount / customers) * 100 << "%\n";
    cout << "atm1 utilization: " << ATMcount1/CTarr[customers-1] << endl;
    cout << "atm2 utilization: " << ATMcount2 / CTarr[customers - 1] << endl;
    cout << fixed << setprecision(2) << "max number in queue: " << maxwait << endl;
    cout << fixed << setprecision(3) << "average time in system: " << AVGtime / customers << endl;
    cout << endl << endl;
    //for (float i = maxwait; i >= 0; i=i-1.0) {
    //    cout.width(2);
    //    cout << i << " | ";

    //    // Marking the values 
    //    for (int j = 0; j < customers; ++j) {
    //        if (WTarr[j] >= i) {
    //            cout << "x ";
    //        }
    //        else {
    //            cout << "  ";
    //        }
    //    }
    //    cout << endl;
    //}
    //cout << "------------------------------------------------------------------------------------------>customers"<< endl;
    //cout << "    ";
}