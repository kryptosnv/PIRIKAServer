truth_width(1).
truth_value([top]).
truth_value([t4]).
truth_value([f4]).
truth_value([bot]).
truth_value([t2]).
truth_value([f2]).

top([top]).
bot([bot]).
top([t2]).
bot([f2]).

ge([X],[X]) :- truth_value([X]),!.
ge([top],[X]) :- truth_value([X]),!.
ge([X],[bot]) :- truth_value([X]),!.
ge([t2], [X]) :- truth_value([X]),!.
ge([X], [f2]) :- truth_value([X]),!.

lub([X],[X],[X]) :- !.
lub([top],[X],[top]) :- truth_value([X]),!.
lub([X],[top],[top]) :- truth_value([X]),!.
lub([X],[bot],[X]) :- truth_value([X]),!.
lub([bot],[X],[X]) :- truth_value([X]),!.
lub([t4],[f4],[top]) :- !.
lub([f4],[t4],[top]) :- !.

lub([t2],[X],[t2]) :- truthvalue([X]),!.
lub([X],[t2],[t2]) :- truthvalue([X]),!.
lub([f2],[X],[X]) :- truthvalue([X]),!.
lub([X],[f2],[X]) :- truthvalue([X]),!.
lub([t4],[f4],[t2]) :- !.
lub([f4],[t4],[t2]) :- !.
