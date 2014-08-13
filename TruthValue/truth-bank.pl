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

truth_width(1).


truth_value([X]) :-
	X >= 1.0,
    X =< 9.0.

top([9.0]).
bot([1.0]).

ge(X, Y) :-
	sublist(Y,X).


lub(X, Y, Z) :-
	append(X, Y, Temp),
	sort(Temp, Z).

