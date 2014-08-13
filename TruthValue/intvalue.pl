truth_width(30).
truth_value(X):- top(Y), subSet(X,Y).
top([1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31]).
bot([]).

subSet([], _).
subSet([X | Xs], Y) :- my_select(X, Y, Z),subSet(Xs, Z).

ge(X,X).
ge(X,Y) :- subSet(Y,X).

lub([X], [Y], [Z]) :- append([X], [Y], Temp),sort(Temp, [Z]).

my_select(X, [X | Xs],Xs).
my_select(X,[Y | Ys],[Y | Zs]) :- my_select(X,Ys,Zs).