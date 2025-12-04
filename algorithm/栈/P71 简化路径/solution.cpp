#include<iostream>
#include<vector>
#include<string>
#include<sstream>

using namespace std;


class Solution {
public:
    string simplifyPath(string path) {
        
        vector<string> stack;
        string s;
        istringstream iss{path};
        
        getline(iss, s, '/');
        return "";
    }
};


int main(){
    string s = "hello world";

    cout << s << endl;

}