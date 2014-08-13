% This is created by katsura
% 2014/01/12 03:55:41

truth_width(4).
truth_value(X):- top(Y), subSet(X,Y).
top(['角田山','研究室','お店','馬場']).
bot([]).

subSet([], _).
subSet([X | Xs], Y) :- my_select(X, Y, Z),subSet(Xs, Z).

ge(X,X).
ge(X,Y) :- subSet(Y,X).

lub([X], [Y], [Z]) :- append([X], [Y], Temp),sort(Temp, [Z]).

my_select(X, [X | Xs],Xs).
my_select(X,[Y | Ys],[Y | Zs]) :- my_select(X,Ys,Zs).
