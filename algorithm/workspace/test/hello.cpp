#include <iostream>
#include <unordered_map>


using namespace std;

#define PRINT_UNMAP

int main(){
    unordered_map<int, int> map;

    map[0] = 1;
    map[1] = 2;
    map[0] = 3;

    cout << map[0] << endl;
    cout << map[0] << endl;

}