#include <bits/stdc++.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>

using namespace std;

sem_t isBusStopped, isConductorFinished;

/* Delay for cnt of 10ms with printing one dot per each 10ms*/
inline void delay10MsWithPrompt(int cnt) {
    for (int i = 0; i < cnt; i++) {
        usleep(10 * 10000);
        cout << ". " << flush;
    }
    cout << endl;
}

/* Simulates the driver */
void* driver(void*) {
    while (true) {
        cout << "------------------------------------" << endl;
        cout << "[Driver] Driver stopped the bus" << flush;
        delay10MsWithPrompt(5);

        sem_post(&isBusStopped);         // release isBusStopped signal,
                                         // showing thebus has stopped
        sem_wait(&isConductorFinished);  // wait for the conductor

        cout << "[Driver] Heading for next bus stop" << flush;
        delay10MsWithPrompt(5);
        cout << "------------------------------------" << endl << endl;

        usleep(50 * 10000);  // delay 500ms from one stop to another
    }
}

/* Simulates the conductor */
void* conductor(void*) {
    while (true) {
        sem_wait(&isBusStopped);  // waiting for stop

        cout << "[Conductor] Conductor is opening the door" << flush;
        delay10MsWithPrompt(3);

        cout << "[Passenger] Passengers are getting on&off the bus" << flush;
        delay10MsWithPrompt(5);

        cout << "[Conductor] Conductor is closing the door" << flush;
        delay10MsWithPrompt(3);

        cout << "[Conductor] Conductor is selling tickets" << flush;
        delay10MsWithPrompt(5);

        sem_post(
            &isConductorFinished);  // releasing the isConductorFinished signal,
                                    // telling the driver prep is finished
    }
}

int main() {
    sem_init(&isBusStopped, 0, 0);
    sem_init(&isConductorFinished, 0, 0);

    /* Using two threads to simulate the driver and the conductor */
    pthread_t driverThread, conductorThread;

    pthread_create(&driverThread, 0, driver, 0);
    pthread_create(&conductorThread, 0, conductor, 0);

    pthread_join(driverThread, NULL);
    pthread_join(conductorThread, NULL);

    return 0;
}
