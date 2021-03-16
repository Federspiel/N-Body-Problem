#include <stdlib.h>
#include <stdio.h>
#include <stdbool.h>
#include <time.h>
#include <math.h>
#define MAXBODIES 240
int size;
int mergethreshold = 2;
double G =  (6.67*pow(10,-11));
double calculation = 0;

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

void merge(int i , int j){
	bodies[i].velocityVector.x = (bodies[i].velocityVector.x+bodies[j].velocityVector.x)/2;
	bodies[i].velocityVector.y = (bodies[i].velocityVector.y+bodies[j].velocityVector.y)/2;
	bodies[i].mass = (bodies[i].mass+bodies[j].mass);
	bodies[j].valid = false;
}

void moveBodies(){
	int DT = 1;
	struct Vector deltav;
	struct Vector deltap;
	int i = 0;
	for(i = 0; i < size;i++){
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

void calculateForces(){
	double distance = 0;
	double magnitude = 0;
	struct Vector direction = {0,0};
	int i = 0;
	int j = 0;
	for(i = 0; i < size-1; i++){
		if(bodies[i].valid){
		for(j = i+1; j < size; j++){
			if(bodies[j].valid){
			distance = sqrt((double) pow((double)(bodies[i].positionVector.x - bodies[j].positionVector.x),2) + (double) pow((double)(bodies[i].positionVector.y - bodies[j].positionVector.y),2) );
			if(distance < mergethreshold){
				merge(i,j);
			}
			magnitude = (G*bodies[i].mass*bodies[j].mass)/pow((double)distance,2);
			direction.x = (bodies[j].positionVector.x - bodies[i].positionVector.x);
			direction.y = (bodies[j].positionVector.y - bodies[i].positionVector.y);
			bodies[i].forceVector.x = bodies[i].forceVector.x + magnitude*direction.x/distance;
			bodies[i].forceVector.y = bodies[i].forceVector.y + magnitude*direction.y/distance;
			bodies[j].forceVector.x = bodies[j].forceVector.x + magnitude*direction.x/distance;
			bodies[j].forceVector.y = bodies[j].forceVector.y + magnitude*direction.y/distance;
			calculation++;
			}
			}
		}
	}

}

int main(int argc, char *argv[]) {
	setbuf(stdout, NULL);
	int numSteps;
	size = (argc > 1) ? atoi(argv[1]) : MAXBODIES;
	numSteps = (argc > 2) ? atoi(argv[2]) : 15;
	srand(time(NULL));
	for(int i = 0; i < size;i++){
		generateSpace(i);
	}
	clock_t start = clock();
	for(int i = 0; i < numSteps ;i++){
		calculateForces();
		moveBodies();
	}
	clock_t end = clock();
	float seconds = (float)(end - start) / CLOCKS_PER_SEC;
	printf("Command Line Arguments size: %d numSteps %d",size,numSteps);
	printf("Time it took %f\n",seconds);
}

void generateSpace(int i){
			bodies[i].valid = true;
			bodies[i].positionVector.x = rand()%100000 +1;
			bodies[i].positionVector.y = rand()%100000 +1;
			bodies[i].velocityVector.x = rand()%100000 +1;
			bodies[i].velocityVector.y = rand()%100000 +1;
			bodies[i].forceVector.x = 0;
			bodies[i].forceVector.y = 0;
			bodies[i].mass = rand()%100000+1;
}


