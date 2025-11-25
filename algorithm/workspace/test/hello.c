#include <stdio.h>


int foo1(int a, int b){
    return a+b;
}



int foo2(int a, int b){
    return a+b;
}


int main(){
    int mark = 0xcafebebe;
    int r1 = foo1(1, 2);
    int r2 = foo2(3, 4);
    printf("%d, %d", r1, r2);
    getchar();
    return 0;
}