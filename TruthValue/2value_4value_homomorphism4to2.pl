truth_width(1).
truth_value([t2]).
truth_value([f2]).
truth_value([top]).
truth_value([t4]).
truth_value([f4]).
truth_value([bot]).

top([t2]).
bot([f2]).
top([top]).
bot([bot]).
top([t4]).
bot([f4]).

ge([t2], [X]) :- truth_value([X]),!.
ge([X], [f2]) :- truth_value([X]),!.

ge([top],[X]) :- truth_value([X]),!.
ge([X],[bot]) :- truth_value([X]),!.

ge([t4], [X]) :- truth_value([X]),!.
ge([X], [f4]) :- truth_value([X]),!.

lub([X],[X],[X]) :- !.
lub([t2],[X],[t2]) :- truthvalue([X]),!.
lub([X],[t2],[t2]) :- truthvalue([X]),!.
lub([f2],[X],[X]) :- truthvalue([X]),!.
lub([X],[f2],[X]) :- truthvalue([X]),!.

lub([top],[X],[top]) :- truth_value([X]),!.
lub([X],[top],[top]) :- truth_value([X]),!.
lub([X],[bot],[X]) :- truth_value([X]),!.
lub([bot],[X],[X]) :- truth_value([X]),!.

lub([X],[X],[X]) :- !.
lub([t4],[X],[t4]) :- truthvalue([X]),!.
lub([X],[t4],[t4]) :- truthvalue([X]),!.
lub([f4],[X],[X]) :- truthvalue([X]),!.
lub([X],[f4],[X]) :- truthvalue([X]),!.

