#include <bits/stdc++.h>
#include <pthread.h>
#include <unistd.h>

using namespace std;

char threadPrompt[] = "[Thread] Current data is ";
char mainPrompt[] = "[Main]   Current data is ";

/* what the new thread will do */
void* newThread(void* arg) {
    int* data = (int*)arg;

    // don't keep resources after exit
    pthread_detach(pthread_self());

    while (true) {
        string* res = new string(threadPrompt);
        res->append(to_string((*data)++));
        // construct one string as output, so it would not be interrupped
        cout << res->c_str() << endl;
        usleep(500 * 1000);
    }
}

int main(void* arg) {
    pthread_t newThreadTid;
    int shareData = 0;

    pthread_create(&newThreadTid, NULL, newThread, &shareData);
    usleep(250 * 1000);

    while (true) {
        string* res = new string(mainPrompt);
        res->append(to_string(shareData++));
        cout << res->c_str() << endl;
        usleep(500 * 1000);
    }
}
