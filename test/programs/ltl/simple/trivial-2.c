//#Unsafe
//@ ltl invariant positive: (<> AP(x < 0));

int x=0;
	
void foo(){
	x++;
}

void main()
{
    while(1){
		foo();
    }
}