/* 

	Created with COBRA-Framework Export Tool v0.1
	Developed by: Thomas Huybrechts - MOSAIC 2016

*/

/*
	Include section
*/

// Include header files here...

/*
	Forward declaration of functions
*/

void benchmarkBlock(void);
int main(void);

void benchmarkBlock(void)
{
int checksum = 0;

  checksum +=
    quicksort_strings[ 42 ][ 1 ] +
    quicksort_vectors[ 42 ].x + quicksort_vectors[ 42 ].y +
    quicksort_vectors[ 42 ].z;
}

int main(void)
{
	benchmarkBlock();
}
