#include <bits/stdc++.h>
#include <semaphore.h>
#include <stdio.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <sys/types.h>
#include <unistd.h>

using namespace std;

struct shareData {
    sem_t isBusStopped;
    sem_t isConductorFinished;
};

/* Delay for cnt of 100ms with printing one dot per each 10ms*/
inline void delay100MsWithPrompt(int cnt) {
    for (int i = 0; i < cnt; i++) {
        usleep(10 * 10000); //100ms
        cout << ". " << flush;
    }
    cout << endl;
}

int main() {
    int pid;

    // Create shared memory
    int shmid = shmget((key_t)522, sizeof(struct shareData), 0666 | IPC_CREAT);
    // Get pointer to shared mem
    struct shareData* sharedData = (struct shareData*)shmat(shmid, 0, 0);

    // Initialize sem
    sem_init(&(sharedData->isBusStopped), 1, 0);
    sem_init(&(sharedData->isConductorFinished), 1, 0);

    pid = fork();
    // Check if failed to fork
    if (pid < 0) {
        cout << "[Fatal Error] Failed to fork" << endl;
        return -1;
    }

    if (pid > 0) {
        // Main process, act as Driver
        while (true) {
            cout << "------------------------------------" << endl;
            cout << "[Driver] Driver stopped the bus" << flush;
            delay100MsWithPrompt(5);

            // release isBusStopped signal, showing thebus has stopped
            sem_post(&(sharedData->isBusStopped));

            // wait for the conductor
            sem_wait(&(sharedData->isConductorFinished));  

            cout << "[Driver] Heading for next bus stop" << flush;
            delay100MsWithPrompt(5);
            cout << "------------------------------------" << endl << endl;

            usleep(50 * 10000);  // delay 500ms from one stop to another
        }
    } else if (pid == 0) {
        // Forked process, act as Conductor
        while (true) {
            sem_wait(&(sharedData->isBusStopped));  // waiting for stop

            cout << "[Conductor] Conductor is opening the door" << flush;
            delay100MsWithPrompt(3);

            cout << "[Passenger] Passengers are getting on&off the bus"
                 << flush;
            delay100MsWithPrompt(5);

            cout << "[Conductor] Conductor is closing the door" << flush;
            delay100MsWithPrompt(3);

            cout << "[Conductor] Conductor is selling tickets" << flush;
            delay100MsWithPrompt(5);

            // releasing the sConductorFinished signal, telling the
            // driver prep is finished
            sem_post(&(sharedData->isConductorFinished));
            
        }
    }

    //Free sharedData
    sem_destroy(&(sharedData->isBusStopped));
    sem_destroy(&(sharedData->isConductorFinished));

    //Free shared mem
    shmctl(shmid, IPC_RMID, 0); 

    return 0;
}
