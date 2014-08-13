truth_width(1).

truth_value([T1]):- float(T1), T1 =< 1.0, T1 >= 0.0.

top([1.0]).
bot([0.0]).

ge(top([T1]), bot([U1])) :- T1 >= U1.
ge([T1], [U1]) :- truth_value([T1]),truth_value([U1]), T1 >= U1.
ge([T1], [U1]) :- U1=T1.

max(X,Y,X) :- X >= Y, !.
max(X,Y,Y) :- X =< Y, !.

lub([T1], [U1], [F1]) :- max(T1,U1,F1).
