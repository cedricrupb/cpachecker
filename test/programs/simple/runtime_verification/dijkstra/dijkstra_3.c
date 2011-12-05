#define bool int
#define true 1
#define false 0

extern bool crit1;
extern bool crit2;
bool crit3 = false;
int turn = 1;
int flag1 = 0;
int flag2 = 0;
int flag3 = 0;

int program_counter_1 = 1;
int program_counter_2 = 1;
int program_counter_3 = 1;

void thread1() {
	switch(program_counter_1) {
		case 1: goto L_1_1;
		case 2: goto L_1_2;
		case 3: goto L_1_3;
		case 4: goto L_1_4;
		case 5: goto L_1_5;
		case 6: goto L_1_6;
		case 7: goto L_1_7;
		case 8: goto L_1_8;
		case 9: goto L_1_9;
		case 10: goto L_1_10;
		case 11: goto L_1_11;
		case 12: goto L_1_12;
		case 13: goto L_1_13;
		case 14: goto L_1_14;
		case 15: goto L_1_15;
		case 16: goto L_1_16;
		case 17: goto L_1_17;
		default: return; 
	}

L_1_1:	flag1 = 1;
	program_counter_1 = 2;
	return;

L_1_2:	while(!(turn == 1)) {
		program_counter_1 = 3;
		return;

L_1_3:		if(turn == 1) {
			program_counter_1 = 4;
			return;

L_1_4:			if(flag1 == 0) {
				program_counter_1 = 5;
				return;

L_1_5:				turn = 1;
				program_counter_1 = 2;
				return;

			}
			program_counter_1 = 2;
			return;

		}
		program_counter_1 = 6;
		return;

L_1_6:		/*else*/ if(turn == 2) {
			program_counter_1 = 7;
			return;

L_1_7:			if(flag2 == 0) {
				program_counter_1 = 8;
				return;

L_1_8:				turn = 1;
				program_counter_1 = 2;
				return;

			}
			program_counter_1 = 2;
			return;

		}
		program_counter_1 = 9;
		return;

L_1_9:		/*else*/ if(turn == 3) {
			program_counter_1 = 10;
			return;

L_1_10:			if(flag3 == 0) {
				program_counter_1 = 11;
				return;

L_1_11:				turn = 1;
				program_counter_1 = 2;
				return;

			}
			program_counter_1 = 2;
			return;

		}
		program_counter_1 = 2;
		return;

	}
	program_counter_1 = 12;
	return;

L_1_12:	flag1 = 2;
	program_counter_1 = 13;
	return;


L_1_13:	if(flag2 == 2) {
		program_counter_1 = 1;
		return;

	}
	program_counter_1 = 14;
	return;

L_1_14:	if(flag3 == 2) {
		program_counter_1 = 1;
		return;

	}
	program_counter_1 = 15;
	return;


L_1_15:	crit1 = true;
	program_counter_1 = 16;
	return;

L_1_16:	crit1 = false;
	program_counter_1 = 17;
	return;

	
L_1_17:	flag1 = 0;
	program_counter_1 = 18;
	return;

}

void thread2() {
	switch(program_counter_2) {
		case 1: goto L_2_1;
		case 2: goto L_2_2;
		case 3: goto L_2_3;
		case 4: goto L_2_4;
		case 5: goto L_2_5;
		case 6: goto L_2_6;
		case 7: goto L_2_7;
		case 8: goto L_2_8;
		case 9: goto L_2_9;
		case 10: goto L_2_10;
		case 11: goto L_2_11;
		case 12: goto L_2_12;
		case 13: goto L_2_13;
		case 14: goto L_2_14;
		case 15: goto L_2_15;
		case 16: goto L_2_16;
		case 17: goto L_2_17;
		default: return; 
	}

L_2_1:	flag2 = 1;
	program_counter_2 = 2;
	return;

L_2_2:	while(!(turn == 2)) {
		program_counter_2 = 3;
		return;

L_2_3:		if(turn == 1) {
			program_counter_2 = 4;
			return;

L_2_4:			if(flag1 == 0) {
				program_counter_2 = 5;
				return;

L_2_5:				turn = 2;
				program_counter_2 = 2;
				return;

			}
			program_counter_2 = 2;
			return;

		}
		program_counter_2 = 6;
		return;

L_2_6:		/*else*/ if(turn == 2) {
			program_counter_2 = 7;
			return;

L_2_7:			if(flag2 == 0) {
				program_counter_2 = 8;
				return;

L_2_8:				turn = 2;
				program_counter_2 = 2;
				return;

			}
			program_counter_2 = 2;
			return;

		}
		program_counter_2 = 9;
		return;

L_2_9:		/*else*/ if(turn == 3) {
			program_counter_2 = 10;
			return;

L_2_10:			if(flag3 == 0) {
				program_counter_2 = 11;
				return;

L_2_11:				turn = 2;
				program_counter_2 = 2;
				return;

			}
			program_counter_2 = 2;
			return;

		}
		program_counter_2 = 2;
		return;

	}
	program_counter_2 = 12;
	return;

L_2_12:	flag2 = 2;
	program_counter_2 = 13;
	return;


L_2_13:	if(flag1 == 2) {
		program_counter_2 = 1;
		return;

	}
	program_counter_2 = 14;
	return;

L_2_14:	if(flag3 == 2) {
		program_counter_2 = 1;
		return;

	}
	program_counter_2 = 15;
	return;


L_2_15:	crit2 = true;
	program_counter_2 = 16;
	return;

L_2_16:	crit2 = false;
	program_counter_2 = 17;
	return;

	
L_2_17:	flag2 = 0;
	program_counter_2 = 18;
	return;

}

void thread3() {
	switch(program_counter_3) {
		case 1: goto L_3_1;
		case 2: goto L_3_2;
		case 3: goto L_3_3;
		case 4: goto L_3_4;
		case 5: goto L_3_5;
		case 6: goto L_3_6;
		case 7: goto L_3_7;
		case 8: goto L_3_8;
		case 9: goto L_3_9;
		case 10: goto L_3_10;
		case 11: goto L_3_11;
		case 12: goto L_3_12;
		case 13: goto L_3_13;
		case 14: goto L_3_14;
		case 15: goto L_3_15;
		case 16: goto L_3_16;
		case 17: goto L_3_17;
		default: return; 
	}

L_3_1:	flag3 = 1;
	program_counter_3 = 2;
	return;

L_3_2:	while(!(turn == 3)) {
		program_counter_3 = 3;
		return;

L_3_3:		if(turn == 1) {
			program_counter_3 = 4;
			return;

L_3_4:			if(flag1 == 0) {
				program_counter_3 = 5;
				return;

L_3_5:				turn = 3;
				program_counter_3 = 2;
				return;

			}
			program_counter_3 = 2;
			return;

		}
		program_counter_3 = 6;
		return;

L_3_6:		/*else*/ if(turn == 2) {
			program_counter_3 = 7;
			return;

L_3_7:			if(flag2 == 0) {
				program_counter_3 = 8;
				return;

L_3_8:				turn = 3;
				program_counter_3 = 2;
				return;

			}
			program_counter_3 = 2;
			return;

		}
		program_counter_3 = 9;
		return;

L_3_9:		/*else*/ if(turn == 3) {
			program_counter_3 = 10;
			return;

L_3_10:			if(flag3 == 0) {
				program_counter_3 = 11;
				return;

L_3_11:				turn = 3;
				program_counter_3 = 2;
				return;

			}
			program_counter_3 = 2;
			return;

		}
		program_counter_3 = 2;
		return;

	}
	program_counter_3 = 12;
	return;

L_3_12:	flag3 = 2;
	program_counter_3 = 13;
	return;


L_3_13:	if(flag1 == 2) {
		program_counter_3 = 1;
		return;

	}
	program_counter_3 = 14;
	return;

L_3_14:	if(flag2 == 2) {
		program_counter_3 = 1;
		return;

	}
	program_counter_3 = 15;
	return;


L_3_15:	crit3 = true;
	program_counter_3 = 16;
	return;

L_3_16:	crit3 = false;
	program_counter_3 = 17;
	return;

	
L_3_17:	flag3 = 0;
	program_counter_3 = 18;
	return;

}

int entry() {
	crit1 = false;
	crit2 = false;

	while(true) {
		switch(nondet_int()) {
			case 0: thread1(); break;
			case 1: thread2(); break;
			case 2: thread3(); break;
		}
		performed_operation();		
	}
	return 1;
}



