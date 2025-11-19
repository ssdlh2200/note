#include <semaphore.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct {
    sem_t sem2;
    sem_t sem3;
} Foo;

void printFirst();
void printSecond();
void printThird();

Foo* fooCreate() {
    Foo* obj = malloc(sizeof(Foo));
    sem_init(&obj->sem2, 0, 0);
    sem_init(&obj->sem3, 0, 0);
    return obj;
}
void first(Foo* obj) {
    printFirst();
    sem_post(&obj->sem2);
}

void second(Foo* obj) {
    sem_wait(&obj->sem2);
    printSecond();
    sem_post(&obj->sem3);
}

void third(Foo* obj) {
    sem_wait(&obj->sem3);
    printThird();
}

void fooFree(Foo* obj) {
    sem_destroy(&obj->sem2);
    sem_destroy(&obj->sem3);
    free(obj);
}
int main()
{
    printf("hello world");

    return 0;
}