# 线程(pthread.h)
## phthread_mutex(互斥锁)
互斥锁：在同一时刻，只允许一个线程访问某个共享资源。  当一个线程获得锁后，其他线程必须等待它释放锁后才能访问

```c
pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER; //初始化互斥锁
int i = 0;  
  
void* foo(void* args)  
{  
    pthread_mutex_lock(&lock);  
    for (int j = 0; j < 100000; j++)  
    {  
        i++;  
    }  
    pthread_mutex_unlock(&lock);  
    return NULL;  
}  
  
int main()  
{  
    pthread_t t1, t2;  
    pthread_create(&t1, NULL, foo, NULL);  
    pthread_create(&t2, NULL, foo, NULL);  
  
    pthread_join(t1, NULL);  
    pthread_join(t2, NULL);  
  
    printf("%d\n",i);  
  
    return 0;  
}

```