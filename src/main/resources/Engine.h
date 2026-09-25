#include <iostream>
#include <thread>
#include <string>

// Tell this file that the helper functions exist over in the controller file
extern jint getLiveIntField(const char* innerClassName, const char* fieldName);
extern std::string getLiveStringField(const char* innerClassName, const char* fieldName);

namespace CoreEngine {
    void runEternalLoop() {
        while (true) {
            // Pulls the fresh live value directly from Java memory on every loop cycle
            int currentPort = getLiveIntField("Constants$GeneralConstants", "NETWORK_PORT");
            int breakLen = getLiveIntField("Constants$TaskConstants", "breakLength");

            std::cout << "[C++ Engine] Traffic port: " << currentPort << " | Break length: " << breakLen << std::endl;
            std::this_thread::sleep_for(std::chrono::milliseconds(1000));
        }
    }

    // This is the function that receives the constants from the controller
    void configure(int port, std::string encryptionAlgo) {
        std::cout << "[C++ Engine] Configured with Initial Port: " << port << " and Algo: " << encryptionAlgo << std::endl;
    }
}
