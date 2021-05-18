#include <bits/stdc++.h>
#include <sys/types.h>
#include <unistd.h>

using namespace std;

int main(void) {
    pid_t pid;

    int result = -1;
    int fd0[2], fd1[2], dataLen;
    char data[100], readbuffer[100];
    int* write_fd = &fd0[1];
    int* read_fd = &fd0[0];
    string cmdline_pre = "cat /proc/", cmdline_status = "/status | head -8";
    string cmdline_mid = "/task/", cmdline_child = "/children";

    memset(data, 0, sizeof(data));
    memset(readbuffer, 0, sizeof(readbuffer));

    result = pipe(fd0);
    if (result == -1) {
        perror("Failed to create pipe 0: ");
        return -1;
    }
    result = pipe(fd1);
    if (result == -1) {
        perror("Failed to create pipe 1: ");
    }

    pid = fork();
    //pid = 111;

    //判断是否创建失败
    if (-1 == pid) {
        perror("Failed to fork: ");
        return -1;
    }

    if (0 == pid) {
        close(*read_fd);

        usleep(20000);  // wait for parent process to print its PCB info
        cout << "This is child process with PID = " << (int)getpid() << endl;
        cout << "----------- Chlid Process Control Block Info -----------"
             << endl;
        string cmdline = (cmdline_pre + to_string(getpid()) + cmdline_status);
        system(cmdline.c_str());
        cout << "--------------------------------------------------------"
             << endl;

        while (true) {
            usleep(20000);  // stop for 2ms to wait for parent process to wake
                            // up and print data it has received
            cout << "[SEND] ";
            cin >> data;
            result = write(*write_fd, data, strlen(data));
            if (!strcmp(data, "exit")) {
                cout << "[CTRL] Child process exiting..." << endl;
                break;
            }
        }
        return 0;
    } else {
        cout << "This is parent process with PID = " << (int)getpid() << endl;
        close(*write_fd);

        cout << "---------- Parent Process Control Block Info ----------"
             << endl;
        string cmdline = (cmdline_pre + to_string(getpid()) + cmdline_status);
        system(cmdline.c_str());
        cout << "-------------------------------------------------------"
             << endl;
        cout << endl;

        while (true) {
            dataLen = read(*read_fd, readbuffer, sizeof(readbuffer) - 1);
            if (!strcmp(readbuffer, "exit")) {
                cout << "[CTRL] Parent process exiting after it's child..."
                     << endl;
                return 0;
            }
            cout << "[RECV] " << readbuffer << endl;
            memset(readbuffer, 0, sizeof(readbuffer));
        }
    }
    return 0;
}