truth_width(1).
truth_value(upper_top).
truth_value(upper_t).
truth_value(upper_f).
truth_value(upper_bot).
truth_value(lower_top).
truth_value(lower_t).
truth_value(lower_f).
truth_value(lower_bot).
top(upper_top).
bot(lower_bot).
ge(X,X) :- truth_value(X),!.
ge(upper_top,X) :- truth_value(X),!.
ge(upper_t,upper_bot) :- !.
ge(upper_t,lower_t) :- !.
ge(upper_f,upper_bot) :- !.
ge(upper_f,lower_f) :- !.
ge(lower_top,lower_t) :- !.
ge(lower_top,lower_f) :- !.
ge(X,lower_bot) :- truth_value(X),!.
lub(X,X,X) :- !.
lub(upper_top,X,upper_top) :- truth_value(X),!.
lub(X,upper_top,upper_top) :- truth_value(X),!.
lub(upper_t,upper_bot,upper_t) :- !.
lub(upper_t,lower_t,upper_t) :- !.
lub(upper_f,upper_bot,upper_f) :- !.
lub(upper_f,lower_f,upper_f) :- !.
lub(lower_top,lower_t,lower_top) :- !.
lub(lower_top,lower_f,lower_top) :- !.
lub(X,lower_bot,X) :- truth_value(X),!.
lub(lower_bot,X,X) :- truth_value(X),!.
lub(upper_t,upper_f,upper_top) :- !.
lub(upper_f,upper_t,upper_top) :- !.
lub(upper_t,lower_top,upper_top) :- !.
lub(lower_top,upper_t,upper_top) :- !.
lub(lower_top,upper_f,upper_top) :- !.
lub(upper_f,lower_top,upper_top) :- !.
lub(lower_t,lower_f,lower_top) :- !.
lub(lower_f,lower_t,lower_top) :- !.
lub(lower_t,upper_bot,upper_t) :- !.
lub(upper_bot,lower_t,upper_t) :- !.
lub(upper_bot,lower_f,upper_f) :- !.
lub(lower_f,upper_bot,upper_f) :- !.
