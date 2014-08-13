%=======================================================================
% ユーザー定義

%-----------------------------------------------------------
% ユーザーによる真理値の定義
%-----------------------------------------------------------

/*
truth_value(top).
truth_value(t).
truth_value(f).
truth_value(bot).

top(top).
bot(bot).

ge(X,X) :- truth_value(X),!.
ge(top,X) :- truth_value(X),!.
ge(X,bot) :- truth_value(X),!.

lub(X,X,X) :- !.
lub(top,X,top) :- truth_value(X),!.
lub(X,top,top) :- truth_value(X),!.
lub(X,bot,X) :- truth_value(X),!.
lub(bot,X,X) :- truth_value(X),!.
lub(t,f,top) :- !.
lub(f,t,top) :- !.
*/

truth_width(2).


truth_value([T,F]) :- 
	T =< 1.0,
	T >= 0.0,
	F =< 1.0,
	F >= 0.0.

top([1.0, 1.0]).
bot([0.0, 0.0]).

ge([T1, F1], [T2, F2]) :-
	T1 >= T2,
	F1 >= F2.

max(X,Y,X) :-
	X >= Y,!.
max(X,Y,Y) :-
	X =< Y,!.


lub([T1, F1], [T2, F2], [T3, F3]) :-
	max(T1,T2,T3),
	max(F1,F2,F3).

