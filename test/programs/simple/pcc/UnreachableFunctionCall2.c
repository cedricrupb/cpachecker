// This file is part of CPAchecker,
// a tool for configurable software verification:
// https://cpachecker.sosy-lab.org
//
// SPDX-FileCopyrightText: 2007-2020 Dirk Beyer <https://www.sosy-lab.org>
//
// SPDX-License-Identifier: Apache-2.0

int f()
{
  return 2;
}

int main()
{ int x=0;
  int a;
  a = f();
  if(a!=2){
    f();
    ERROR: x=-1;
  }
  return x;
}
