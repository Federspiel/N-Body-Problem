#define HAVE_STRUCT_TIMESPEC
#ifndef _REENTRANT
#define _REENTRANT
#endif
#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>
#include <math.h>
#include <sys/time.h>
#define MAXBODIES 240
int size;
int timeSteps= 15;
double start_time, end_time; /* start and end times */
int numWorkers = 4;
int activeWorkers = 0;
double calculation = 0;
int mergethreshold = -1	;
double G =  (6.67*pow(10,-11));
pthread_mutex_t totlock; /* mutex lock for the total */
pthread_mutex_t pos;
pthread_mutex_t force;
struct Vector {
	double x;
	double y;
};
struct Particle{
	struct Vector positionVector;
	struct Vector velocityVector;
	struct Vector forceVector;
	double mass;
	bool valid;
};
struct Particle bodies[MAXBODIES];

void generateSpace(int i);

void moveBodies(int lowerBound,int upperBound){
	struct Vector deltav;
	struct Vector deltap;
	int DT = 1;
	int i = lowerBound;
	for(i = 0; i < upperBound;i++){
		deltav.x = (bodies[i].forceVector.x/bodies[i].mass)*DT;
		deltav.y = (bodies[i].forceVector.y/bodies[i].mass)*DT;
		deltap.x = (bodies[i].velocityVector.x+deltav.x/2) *DT;
		deltap.y = (bodies[i].velocityVector.y+deltav.y/2) *DT;
		bodies[i].velocityVector.x = bodies[i].velocityVector.x + deltav.x;
		bodies[i].velocityVector.y = bodies[i].velocityVector.y + deltav.y;
		bodies[i].positionVector.x = bodies[i].positionVector.x + deltap.x;
		bodies[i].positionVector.y = bodies[i].positionVector.y + deltap.y;
		bodies[i].forceVector.x = 0;
		bodies[i].forceVector.y = 0;
		calculation++;
	}
}


void merge(int i , int j){
	pthread_mutex_lock(&totlock);
	bodies[i].velocityVector.x = (bodies[i].velocityVector.x+bodies[j].velocityVector.x)/2;
	bodies[i].velocityVector.y = (bodies[i].velocityVector.y+bodies[j].velocityVector.y)/2;
	bodies[i].mass = (bodies[i].mass+bodies[j].mass);
	bodies[j].valid = false;
	pthread_mutex_unlock(&totlock);
}
double calculateDistance(int i, int j){
	double distance;
	distance = sqrt((double) pow((double)(bodies[i].positionVector.x - bodies[j].positionVector.x),2) + (double) pow((double)(bodies[i].positionVector.y - bodies[j].positionVector.y),2) );
	return distance;
}

bool hasMerged(int i){
	bool temp;
	temp = bodies[i].valid;
	return temp;

}

void updateForce(int i , int j, double magnitude, struct Vector direction ,double distance){
	pthread_mutex_lock(&force);
	calculation++;
	bodies[i].forceVector.x = bodies[i].forceVector.x + magnitude*direction.x/distance;
	bodies[i].forceVector.y = bodies[i].forceVector.y + magnitude*direction.y/distance;
	bodies[j].forceVector.x = bodies[j].forceVector.x + magnitude*direction.x/distance;
	bodies[j].forceVector.y = bodies[j].forceVector.y + magnitude*direction.y/distance;
	pthread_mutex_unlock(&force);
}

struct Vector calculateDirection(int i, int j){
	struct Vector direction={0,0};
	direction.x = bodies[j].positionVector.x - bodies[i].positionVector.x;
	direction.y = bodies[j].positionVector.y - bodies[i].positionVector.y;
	return direction;
}

double calculateMagnitude(int i, int j, int distance){
	double magnitude = 0;
	pthread_mutex_lock(&totlock);
	magnitude = (G*bodies[i].mass*bodies[j].mass)/pow((double)distance,2);
	pthread_mutex_unlock(&totlock);
	return magnitude;
}
void calculateForces(int lowerBound, int upperBound){
	double distance = 0;
	double magnitude = 0;
	struct Vector direction = {0,0};
	int i = lowerBound;
	int j = 0;
	for(i = lowerBound; i < upperBound-1; i++){
		if(hasMerged(i)){
		for(j = i+1; j < size; j++){
			if(hasMerged(j)){
			distance = calculateDistance(i,j);
			if(distance < mergethreshold){
				merge(i,j);
			}
			magnitude = calculateMagnitude(i,j,distance);
			direction = calculateDirection(i,j);
			updateForce(i,j,magnitude,direction,distance);
			}
			}
		}
	}

}
void *WorkerMove(void *arg){
	int temp = 0;
	int lowerBound = 0;
	int upperBound = 0;
	pthread_mutex_lock(&totlock);
	temp = size/numWorkers;
	lowerBound = temp * activeWorkers;
	activeWorkers++;
	upperBound = temp * activeWorkers;
	pthread_mutex_unlock(&totlock);
	moveBodies(lowerBound,upperBound);
	return NULL;
}

void *WorkerCalc(void *arg){
	int temp = 0;
	int lowerBound = 0;
	int upperBound = 0;
	pthread_mutex_lock(&totlock);
	temp = size/numWorkers;
	lowerBound = temp * activeWorkers;
	activeWorkers++;
	upperBound = temp * activeWorkers;
	pthread_mutex_unlock(&totlock);
	calculateForces(lowerBound,upperBound);
	return NULL;
}

double read_timer() {
	static bool initialized = false;
	static struct timeval start;
	struct timeval end;
	if (!initialized) {
		gettimeofday(&start, NULL);
		initialized = true;
	}
	gettimeofday(&end, NULL);
	return (end.tv_sec - start.tv_sec) + 1.0e-6 * (end.tv_usec - start.tv_usec);
}


int main(int argc, char *argv[]) {
	setbuf(stdout, NULL);
	int numSteps= 0;
	size = atoi(argv[1]);
	numSteps = atoi(argv[2]);
	numWorkers = atoi(argv[3]);
	/* set global thread attributes */
	pthread_attr_t attr;
	pthread_t workerid[numWorkers];
	pthread_attr_init(&attr);
	pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM);
	pthread_mutex_init(&totlock, NULL);
	pthread_mutex_init(&pos,NULL);
	pthread_mutex_init(&force,NULL);
	srand(time(NULL));   // Initialization, should only be called once.
	for(int i = 0; i < size;i++){
		generateSpace(i);
	}
	start_time = read_timer();
	int l =0;
	for(int i = 0; i < numSteps ;i++){
		for (l = 0; l < numWorkers; l++) {
			pthread_create(&workerid[l], NULL, WorkerCalc, (void *)l);
			}
		for (l = 0; l < numWorkers; l++) {
			pthread_join(workerid[l], NULL);
		}
		activeWorkers=0;
		for (l = 0; l < numWorkers; l++) {
			pthread_create(&workerid[l], NULL, WorkerMove, (void *)l);
			}
		for (l = 0; l < numWorkers; l++) {
			pthread_join(workerid[l], NULL);
		}
		activeWorkers=0;
	}
	end_time = read_timer();
	printf("Command Line Arguments size: %d numSteps: %d numOfthreads",size,numSteps,numWorkers);
	printf("The execution time is %g sec\n", end_time - start_time);
}

void generateSpace(int i){
			bodies[i].valid = true;
			bodies[i].positionVector.x = rand()%100000 +100;
			bodies[i].positionVector.y = rand()%100000 +100;
			bodies[i].velocityVector.x = rand()%100000 +100;
			bodies[i].velocityVector.y = rand()%100000 +100;
			bodies[i].forceVector.x = 0;
			bodies[i].forceVector.y = 0;
			bodies[i].mass = rand()%100000+1;
}


