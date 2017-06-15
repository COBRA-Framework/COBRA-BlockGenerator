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
quicksort_str( *quicksort_strings, 681, sizeof( char[ 20 ] ) );

  _Pragma( "marker recursivecall2" )
  _Pragma( "flowrestriction 1*quicksort_vec <= 650*recursivecall2" )
  quicksort_vec(
    ( char * ) quicksort_vectors, 1000,
    sizeof( struct quicksort_3DVertexStruct ) );
}

int main(void)
{
	benchmarkBlock();
}
