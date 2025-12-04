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
        string res;
        istringstream iss{path};
        while(getline(iss, s, '/')){
            if ("" == s || "." == s){
                continue;
            } else if (".." == s){
                if(!stack.empty()){
                    stack.pop_back();
                }
            } else {
                stack.push_back(s);
            }
            //cout << s << endl;
        }
        if(stack.empty()){
            res.append("/");
        } else {
            for(string& c : stack){
                res.append("/");
                res.append(c);
            }
        }
        return res;
    }
};

int main(){
    string s1 = "/dada/dada/dada/dada/../dada/da/./da//da////da";
    string s2 = "/";
    string s3 = "///////";
    string s4 = "/../";
    string s5 = "/........../.../ad";
    Solution sol;
    string res1 = sol.simplifyPath(s1);
    string res2 = sol.simplifyPath(s2);
    string res3 = sol.simplifyPath(s3);
    string res4 = sol.simplifyPath(s4);
    string res5 = sol.simplifyPath(s5);
    cout << res1 << endl;
    cout << res2 << endl;
    cout << res3 << endl;
    cout << res4 << endl;
    cout << res5 << endl;
}