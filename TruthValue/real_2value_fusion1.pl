truth_width(1).

truth_value([T1]):- T1 =< 1.0, T1 >= 0.0, true.
truth_value([t2]).
truth_value([f2]).

top([1.0]).
bot([0.0]).
ge([T1], [U1]) :- T1 >= U1, true.

ge([1.0], [X]) :- truth_value([X]),!.
ge([X], [0.0]) :- truth_value([X]),!.

ge([t2], [f2]) :- !.

max(X,Y,X) :- X >= Y, !.
max(X,Y,Y) :- X =< Y, !.

lub([T1], [U1], [F1]) :- max(T1,U1,F1), true.

lub([t2],[f2],[1.0]) :- !.
