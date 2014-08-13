truth_width(1).

truth_value([T1]):- T1 =< 1.0, T1 >= 0.0,true.
truth_value([t2]).
truth_value([f2]).

top([1.0]).
bot([0.0]).

ge(X,X) :- truth_value(X),!.
ge([T1], [U1]) :- T1 >= U1, true.
ge([T1],[t2]) :- T1 >= 0.6,truthvalue([T1]),!.
ge([t2],[T1]) :- T1 < 0.6,truthvalue([T1]),!.
ge([T1],[f2]) :- T1 >= 0.3,truthvalue([T1]),!.
ge([f2],[T1]) :- T1 < 0.3,truthvalue([T1]),!.
ge([t2], [f2]) :- !.

max(X,Y,X) :- X >= Y, !.
max(X,Y,Y) :- X =< Y, !.

lub([X],[X],[X]) :- !.
lub([T1], [U1], [F1]) :- max(T1,U1,F1), true.


lub([t2],[T1],[T1]) :- T1 >= 0.6,truthvalue([T1]),!.
lub([T1],[t2],[T1]) :- T1 >= 0.6,truthvalue([T1]),!.
lub([t2],[T1],[t2]) :- T1 < 0.6,truthvalue([T1]),!.
lub([T1],[t2],[t2]) :- T1 < 0.6,truthvalue([T1]),!.
lub([f2],[T1],[T1]) :- T1 >= 0.6,truthvalue([T1]),!.
lub([T1],[f2],[T1]) :- T1 >= 0.6,truthvalue([T1]),!.
lub([f2],[T1],[T1]) :- T1 >= 0.3,truthvalue([T1]),!.
lub([T1],[f2],[T1]) :- T1 >= 0.3,truthvalue([T1]),!.
lub([f2],[T1],[f2]) :- T1 < 0.3,truthvalue([T1]),!.
lub([T1],[f2],[f2]) :- T1 < 0.3,truthvalue([T1]),!.

lub([t2],[f2],[t2]) :- !.
lub([t2],[t2],[t2]) :- !.
lub([f2],[t2],[t2]) :- !.
lub([f2],[f2],[f2]) :- !.
