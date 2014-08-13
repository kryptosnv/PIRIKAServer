truth_width(1).
truth_value([t2]).
truth_value([f2]).

top([t2]).
bot([f2]).

ge([t2], [X]) :- truth_value([X]),!.
ge([X], [f2]) :- truth_value([X]),!.

lub([X],[X],[X]) :- !.
lub([t2],[X],[t2]) :- truthvalue([X]),!.
lub([X],[t2],[t2]) :- truthvalue([X]),!.
lub([f2],[X],[X]) :- truthvalue([X]),!.
lub([X],[f2],[X]) :- truthvalue([X]),!.
